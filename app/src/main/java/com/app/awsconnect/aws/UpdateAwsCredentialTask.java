//package com.app.awsconnect.aws;
//
//import static jp.co.lecip.aws.Consts.CONFIG_ROOT_PATH;
//import static jp.co.lecip.aws.Consts.KEY_STORE_NAME_PROVISIONED;
//import static jp.co.lecip.aws.Consts.KEY_STORE_PASSWORD_PROVISIONED;
//
//import android.app.Activity;
//import android.content.SharedPreferences;
//import android.content.res.Resources;
//import android.preference.PreferenceManager;
//import android.util.Log;
//
//import com.app.awsconnect.utils.AwsConfig;
//import com.app.awsconnect.utils.ConstantNumber;
//import com.google.gson.Gson;
//import com.google.gson.JsonSyntaxException;
//
//import org.json.JSONException;
//
//import java.io.BufferedInputStream;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.security.KeyManagementException;
//import java.security.KeyStore;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.UnrecoverableKeyException;
//import java.security.cert.Certificate;
//import java.security.cert.CertificateException;
//import java.security.cert.CertificateFactory;
//import java.util.Date;
//import java.util.TimerTask;
//
//import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.KeyManagerFactory;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManagerFactory;
//
//import jp.co.lecip.aws.network.model.AWSCredentials;
//import jp.co.lecip.utility.AwsUtility;
//
//public class UpdateAwsCredentialTask extends TimerTask implements ConstantNumber {
//
//	private static Activity activity;
//	private AwsConfig _awsConfig;
//
//	public UpdateAwsCredentialTask(Activity activity){
//		UpdateAwsCredentialTask.activity = activity;
//		_awsConfig = null;
//		try {
//			_awsConfig = new AwsConfig(activity.getApplicationContext());
//		} catch (IOException e) {
//			Log.e(TAG, "awsConfig　取得失敗", e);
//		} catch (JSONException e) {
//			Log.e(TAG, "awsConfig Parse失敗", e);
//		}
//	}
//
//	@Override
//	public void run(){
//		Certificate ca = null;
//		InputStream caInput = null;
//
//		try {
//			CertificateFactory cf = CertificateFactory.getInstance("X.509");
//			Resources res = activity.getApplicationContext().getResources();
//			InputStream isRoot = res.getAssets().open(CONFIG_ROOT_PATH + "AmazonRootCA1.pem");
//			caInput = new BufferedInputStream(isRoot);
//			ca = cf.generateCertificate(caInput);
//
//		} catch (CertificateException e) {
//			Log.w(TAG, "Certificate CertificateException", e);
//		} catch (IOException e) {
//			Log.w(TAG, "Certificate IOException", e);
//		} finally {
//			try {
//				caInput.close();
//			} catch (IOException e) {
//				Log.w(TAG, "Certificate IOException", e);
//			}
//		}
//
//		TrustManagerFactory tmf = null;
//		KeyManagerFactory kmf = null;
//
//		try {
//			// Create a KeyStore containing our trusted CAs
//			String keyStoreType = KeyStore.getDefaultType();
//			KeyStore keyStoreRootCA = KeyStore.getInstance(keyStoreType);
//			keyStoreRootCA.load(null, null);
//			keyStoreRootCA.setCertificateEntry("ca", ca);
//
//			// Create a TrustManager that trusts the CAs in our KeyStore
//			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//
//			tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//			tmf.init(keyStoreRootCA);
//
//			String keyStorePath = activity.getApplicationContext().getFilesDir().getAbsolutePath();
//
//			KeyStore keyStoreIoT = KeyStore.getInstance(keyStoreType);
//			keyStoreIoT.load(new FileInputStream(keyStorePath + "/" + KEY_STORE_NAME_PROVISIONED), KEY_STORE_PASSWORD_PROVISIONED.toCharArray());
//
//			String kmfAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
//			kmf = KeyManagerFactory.getInstance(kmfAlgorithm);
//			kmf.init(keyStoreIoT, KEY_STORE_PASSWORD_PROVISIONED.toCharArray());
//
//		} catch (IOException e) {
//			Log.w(TAG, "ManagerFactory IOException", e);
//		} catch (CertificateException e) {
//			Log.w(TAG, "ManagerFactory CertificateException", e);
//		} catch (NoSuchAlgorithmException e) {
//			Log.w(TAG, "ManagerFactory NoSuchAlgorithmException", e);
//		} catch (UnrecoverableKeyException e) {
//			Log.w(TAG, "ManagerFactory UnrecoverableKeyException", e);
//		} catch (KeyStoreException e) {
//			Log.w(TAG, "ManagerFactory KeyStoreException", e);
//		}
//
//		String result = null;
//		HttpsURLConnection urlConnection = null;
//		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
//		try {
//
//			SSLContext sslContext = SSLContext.getInstance("TLS");
//			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
//
//			String thingName = preferences.getString(PreferencesKey.Device.THING_NAME, "");
//			Log.d(TAG, "ThingName: "+ thingName);
//			String urlStr = "https://" + _awsConfig.credentialProviderEndpoint + "/role-aliases/" + _awsConfig.roleAlias + "/credentials";
//			Log.d(TAG, "URL: " + urlStr);
//			URL url = new URL(urlStr);
//			urlConnection = (HttpsURLConnection) url.openConnection();
////			urlConnection.setRequestProperty("User-agent","Mozilla/5.0");
//			urlConnection.setConnectTimeout(1000);
//			urlConnection.setReadTimeout(1000);
//			urlConnection.setRequestMethod("GET");
//			urlConnection.setRequestProperty("x-amzn-iot-thingname", thingName);
//			urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
//
//			urlConnection.connect();
//
//			int code = urlConnection.getResponseCode();
//			Log.d(TAG, "ResponseCode: " + code + "\r\n Message :" +urlConnection.getResponseMessage());
//
//			InputStream in = urlConnection.getInputStream();
//			result = AwsUtility.is2String(in);
//			Log.i(TAG, "result: " + result);
//
//		} catch (NoSuchAlgorithmException e) {
//			Log.w(TAG, "Get NoSuchAlgorithmException", e);
//		} catch (KeyManagementException e) {
//			Log.w(TAG, "Get KeyManagementException", e);
//		} catch (MalformedURLException e) {
//			Log.w(TAG, "Get MalformedURLException", e);
//		} catch (IOException e) {
//			Log.w(TAG, "Get IOException", e);
//			InputStream in = urlConnection.getErrorStream();
//			try {
//				if( in != null) {
//					String res = AwsUtility.is2String(in);
//					Log.i(TAG, "ErrorMessage: " + res);
//				}
//			} catch (IOException ex) {
//				ex.printStackTrace();
//			}
//
//		} finally {
//			if (urlConnection != null) {
//				urlConnection.disconnect();
//			}
//		}
//
//		try {
//			Gson gson = new Gson();
//			AWSCredentials awsCredential = gson.fromJson(result, AWSCredentials.class);
//			Log.i(TAG, "Success AWS Credentials Update" + new Date());
//			saveAwsCredential(awsCredential.credential);
//		} catch (JsonSyntaxException e) {
//			Log.w(TAG, "JSONException", e);
//			Log.i(TAG, "Failed AWS Credentials Update" + new Date());
//		}catch( NullPointerException e){
//			Log.e(TAG, "NullPointerException: ", e);
//		}
//
//	}
//
//	private void saveAwsCredential(AWSCredentials.Credential credential) {
//		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
//		preferences.edit().putString(PreferencesKey.LecipAwsCredential.EXPIRATION, credential.expiration).apply();
//		preferences.edit().putString(PreferencesKey.LecipAwsCredential.ACCESS_KEY_ID, credential.accessKeyId).apply();
//		preferences.edit().putString(PreferencesKey.LecipAwsCredential.SECRET_ACCESS_KEY, credential.secretAccessKey).apply();
//		preferences.edit().putString(PreferencesKey.LecipAwsCredential.SESSION_TOKEN, credential.sessionToken).apply();
//	}
//}
