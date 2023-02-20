package com.app.awsconnect.aws;


import static com.app.awsconnect.utils.Consts.DEBUG_TAG;
import static com.app.awsconnect.utils.Consts.KEY_STORE_NAME_CLAIM;
import static com.app.awsconnect.utils.Consts.KEY_STORE_NAME_PROVISIONED;
import static com.app.awsconnect.utils.Consts.KEY_STORE_PASSWORD_CLAIM;
import static com.app.awsconnect.utils.Consts.KEY_STORE_PASSWORD_PROVISIONED;

import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.app.awsconnect.manager.ActivityManager;
import com.app.awsconnect.utils.AwsConfig;
import com.app.awsconnect.utils.ConstantNumber;
import com.app.awsconnect.utils.PreferencesKey;
import com.app.awsconnect.utils.RepositoryCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyStore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fleet Provisioning
 */
public class ProvisioningRepository implements ConstantNumber {

    private AwsConfig awsConfig;
    private String certStr;
    private String priKeyStr;
    private String mDeviceId;

    private AWSIotMqttManager mqttManager;
    private AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus status;
    private SharedPreferences preferences;

    public ProvisioningRepository(AwsConfig awsConfig, SharedPreferences preferences) {
        this.awsConfig = awsConfig;
        this.preferences = preferences;
//        this.preferences.edit().remove(PreferencesKey.Device.DEVICE_ID).apply();
//        int a = 100;

    }

    public void registerDevice(String keyStorePath,
                               String deviceId,
                               final RepositoryCallback<Void> callback) {
        AtomicInteger countReconnectTimes = new AtomicInteger();
        mDeviceId = deviceId;
        certStr = awsConfig.getCertStr();
        priKeyStr = awsConfig.getPriKeyStr();

        boolean isPresent = AWSIotKeystoreHelper.isKeystorePresent(
                keyStorePath,
                KEY_STORE_NAME_CLAIM);
        //重複する証明書がkeystoreに存在した場合削除
        if (isPresent) {
            AWSIotKeystoreHelper.deleteKeystoreAlias(
                    awsConfig.certificateId,
                    keyStorePath,
                    KEY_STORE_NAME_CLAIM, KEY_STORE_PASSWORD_CLAIM);
        }
        AWSIotKeystoreHelper.saveCertificateAndPrivateKey(
                awsConfig.certificateId,
                certStr,
                priKeyStr,
                keyStorePath,
                KEY_STORE_NAME_CLAIM, KEY_STORE_PASSWORD_CLAIM);

        KeyStore keyStore = AWSIotKeystoreHelper.getIotKeystore(
                awsConfig.certificateId,
                keyStorePath,
                KEY_STORE_NAME_CLAIM, KEY_STORE_PASSWORD_CLAIM);

        mqttManager = new AWSIotMqttManager(deviceId, awsConfig.mqttEndpoint);
        mqttManager.connect(keyStore, (status, throwable) -> {
            Log.d(TAG, String.format("AWSIotMqttClientStatus changed. %s", status));
            if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected
                    && AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected != this.status) {
                this.status = status;
                createCert(keyStorePath, callback);
            }
            else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Reconnecting) {
                countReconnectTimes.incrementAndGet();
            }
            else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost) {
                ActivityManager.getMainActivity().removeLoadingDialog();
                countReconnectTimes.set(0);
            }

            if (ActivityManager.getChangeInfoTextHandler() != null) {
                Message msg = Message.obtain();
                msg.what = 1234;
                msg.obj = status;
                msg.arg1 = countReconnectTimes.get();
                ActivityManager.getChangeInfoTextHandler().sendMessage(msg);
            }
        });

    }

    private void createCert(final String keyStorePath,
                            final RepositoryCallback<Void> callback) {
        String topicCreateCert = "$aws/certificates/create/json";

        mqttManager.publishString("{}", topicCreateCert, AWSIotMqttQos.QOS0);

        mqttManager.subscribeToTopic(
                topicCreateCert + "/accepted",
                AWSIotMqttQos.QOS0,
                (topic, data) -> onCertMsgReceived(keyStorePath, topicCreateCert, data, callback)
        );

    }

    private void onCertMsgReceived(
            final String keyStorePath,
            final String topicCreateCert,
            byte[] data,
            final RepositoryCallback<Void> callback) {
        try {
            mqttManager.unsubscribeTopic(topicCreateCert + "/accepted");

            JSONObject result = new JSONObject(new String(data));
            String provisionedCertificatedId = result.getString("certificateId");
            String provisionedCertificatePem = result.getString("certificatePem");
            String provisionedPrivateKey = result.getString("privateKey");
            String certificateOwnershipToken = result.getString("certificateOwnershipToken");
            Log.i(DEBUG_TAG, "result : " + new String(data));
            Log.d(TAG, "keyStorePath: " + keyStorePath);
            Log.d(TAG, "certID: " + provisionedCertificatedId);

            //重複する証明書がkeystoreに存在した場合削除, 新しいものを保存
            boolean isPresent = AWSIotKeystoreHelper.isKeystorePresent(
                    keyStorePath,
                    KEY_STORE_NAME_PROVISIONED);
            if (isPresent) {
                AWSIotKeystoreHelper.deleteKeystoreAlias(provisionedCertificatedId,
                        keyStorePath,
                        KEY_STORE_NAME_PROVISIONED,
                        KEY_STORE_PASSWORD_PROVISIONED);
            }

            AWSIotKeystoreHelper.saveCertificateAndPrivateKey(
                    provisionedCertificatedId,
                    provisionedCertificatePem,
                    provisionedPrivateKey,
                    keyStorePath,
                    KEY_STORE_NAME_PROVISIONED,
                    KEY_STORE_PASSWORD_PROVISIONED);

            registerThing(certificateOwnershipToken, provisionedCertificatedId, callback);

        } catch (JSONException ex) {
            Log.i(DEBUG_TAG, "JSONException", ex);
        }
    }

    private void registerThing(String certificateOwnershipToken,
                               String provisionedCertificatedId,
                               final RepositoryCallback<Void> callback) {
        String topicRegisterThing = "$aws/provisioning-templates/" + awsConfig.provisioningTemlete + "/provision/json";
//        String templateParameters = "{\"SerialNumber\": \"" + mDeviceId + "\", \"AWS::IoT::Certificate::Id\": \"" + awsConfig.certificateId + "\"}";
        String templateParameters = "{\"product_no\":\"" + awsConfig.productNo + "\",\"serial_no\":\"" + mDeviceId + "\"}";
        String requestParam = "{\"certificateOwnershipToken\": \"" + certificateOwnershipToken + "\", \"parameters\": " + templateParameters + "}";

        //端末を登録
        mqttManager.publishString(requestParam, topicRegisterThing, AWSIotMqttQos.QOS0);

        //端末の登録に成功
        mqttManager.subscribeToTopic(
                topicRegisterThing + "/accepted",
                AWSIotMqttQos.QOS0,
                (topic, data) -> {
                    try {
                        JSONObject result = new JSONObject(new String(data));
                        String thingName = result.getString("thingName");
                        saveDeviceInfo(thingName, provisionedCertificatedId);
                        if (callback != null) {
                            callback.onStatusChanged(RepositoryCallback.Status.Success, null, null);
                        }
                        mqttManager.disconnect();

                    } catch (JSONException ex) {
                        Log.i(DEBUG_TAG, "subscribeToTopic JSONException", ex);
                        callback.onStatusChanged(RepositoryCallback.Status.Error, null, ex);
                    }

                });

        //端末の登録に失敗
        mqttManager.subscribeToTopic(
                topicRegisterThing + "/rejected",
                AWSIotMqttQos.QOS0,
                (topic, data) -> {
                    try {
                        JSONObject result = new JSONObject(new String(data));

                        Log.e(DEBUG_TAG, "statusCode: " + result.getString("statusCode"));
                        Log.e(DEBUG_TAG, "errorCode: " + result.getString("errorCode"));
                        Log.e(DEBUG_TAG, "errorMessage: " + result.getString("errorMessage"));

                        mqttManager.disconnect();
                    } catch (JSONException ex) {
                        Log.e(DEBUG_TAG, "subscribeToTopic JSONException", ex);
                    }
                });
    }

    private void saveDeviceInfo(String thingName, String provisionedCertificatedId) {
        preferences.edit().putString(PreferencesKey.Device.BUS_ID, awsConfig.env).apply();
        preferences.edit().putString(PreferencesKey.Device.DEVICE_ID, mDeviceId).apply();
        preferences.edit().putString(
                PreferencesKey.Device.CERTIFICATED_ID, provisionedCertificatedId).apply();
        preferences.edit().putString(PreferencesKey.Device.THING_NAME, thingName).apply();
    }

    public void isDeviceRegistered(final RepositoryCallback<Void> callback) {
        // verify certification exist
        RepositoryCallback.Status status = preferences.getString(PreferencesKey.Device.CERTIFICATED_ID, null) != null ?
                RepositoryCallback.Status.Registered : RepositoryCallback.Status.Unregister;
        callback.onStatusChanged(status, null, null);
    }
}
