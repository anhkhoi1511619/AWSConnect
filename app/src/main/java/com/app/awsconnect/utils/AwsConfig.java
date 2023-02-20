package com.app.awsconnect.utils;

import static com.app.awsconnect.utils.AwsUtility.is2String;
import static com.app.awsconnect.utils.Consts.CONFIG_ROOT_PATH;

import android.content.Context;
import android.content.res.Resources;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class AwsConfig {

    public final String mqttEndpoint;
    public final String provisioningTemlete;
    public final String certificateId;
    public final String certFileName;
    public final String publicKeyFileName;
    public final String roleAlias;
    public final String credentialProviderEndpoint;
    public final String tenantCode;
    public final String env;
    public final String region;
    public final String receiveFileTmpBucket;
    public final String apiVer;
    public final String productNo;
    public final String apiEndpoint;
    public final String certStr;
    public final String publicKey;

    public AwsConfig(Context context) throws IOException, JSONException {
        Resources res = context.getResources();
        InputStream is;


        is = res.getAssets().open(CONFIG_ROOT_PATH + "ldmsconfig.json");
        String configStr = is2String(is);
        JSONObject rootJSON = new JSONObject(configStr);
        mqttEndpoint = rootJSON.getString("mqtt_endpoint");
        provisioningTemlete = rootJSON.getString("provisioning_template_name");
        certificateId = rootJSON.getString("certificate_id");
        certFileName = rootJSON.getString("cert_file_name");
        publicKeyFileName = rootJSON.getString("publicKey_file_name");
        roleAlias = rootJSON.getString("role_alias");
        credentialProviderEndpoint = rootJSON.getString("credential_provider_endpoint");
        tenantCode = rootJSON.getString("tenant_code");
        env = rootJSON.getString("env");
        region = rootJSON.getString("region");
        receiveFileTmpBucket = rootJSON.getString("receive_file_temp_bucket");
        apiVer = rootJSON.getString("api_ver");
        productNo = rootJSON.getString("product_no");
        apiEndpoint = rootJSON.getString("api_endpoint");

        is.close();


        is = res.getAssets().open(CONFIG_ROOT_PATH + publicKeyFileName);
        publicKey = is2String(is);
        is.close();

        is = res.getAssets().open(CONFIG_ROOT_PATH + certFileName);
        certStr = is2String(is);
        is.close();
    }

    public String getCertStr() {
        return certStr;
    }

    public String getCertFileName(){
        return certFileName;
    }
    public String getPriKeyStr() {
        return publicKey;
    }

}
