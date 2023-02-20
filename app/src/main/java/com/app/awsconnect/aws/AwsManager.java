package com.app.awsconnect.aws;

import static com.app.awsconnect.utils.RepositoryCallback.Status.Registered;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.app.awsconnect.utils.AwsConfig;
import com.app.awsconnect.utils.ConstantNumber;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.Timer;

public class AwsManager implements ConstantNumber {

	private static final String configFile = "ldmsconfig.json";
	private static final String crtFile = "20220419-103130_certificate.pem.crt";
	private static final String SEPARATOR = File.separator;

	private static final String DBUG_DEVICE_ID = "900103";

	private ProvisioningRepository mRepository;

	private MutableLiveData<Event> mEvent = new MutableLiveData<>();

	private static Handler mHandler;
	private Timer timer;

	public AwsManager(){}

	public AwsManager(Context context){
		this.mRepository = new ProvisioningRepository(getAwsConfig(context),
		PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()));

		mHandler = new Handler(Looper.getMainLooper())
		{
			@Override
			public void handleMessage(Message msg) {
				Message message = new Message();
				message.copyFrom(msg);
				eventHandler(message);
	}

		};

		// 登録確認
		isDeviceRegistered(context);
	}

	public void isDeviceRegistered(Context context) {
		mRepository.isDeviceRegistered((status, data, throwable) -> {
			Message msg = new Message();
			if( status == Registered  )
			{
				msg.what = AWS_REGISTED;
		}
			else
			{
				msg.what = AWS_NOT_REGIST;
				msg.obj = context;
			}
			mHandler.sendMessage(msg);
		});


	}

	private void eventHandler(Message msg){
		switch(msg.what)
		{
			case AWS_REGISTED:
				Log.d(TAG, "Device Registed!!!!!!!!!!!!");

			case AWS_REGIST_SUCCESS:
				timerTaskStart();
				Log.d(TAG, "Device Regist Success!");
				break;
			case AWS_NOT_REGIST:
				registerDevice((Context) msg.obj, DBUG_DEVICE_ID);
				Log.d(TAG, "Device Not Regist");
				break;
			case AWS_REGIST_FAILED:
				Log.e(TAG, "eventHandler: RegistFailed..................");
			default:
				break;
		}
	}

	private AwsConfig getAwsConfig(Context context) {
		try {
			return new AwsConfig(context);
		} catch (IOException | JSONException e) {
			return null;
		}
	}
//	public void initAwsSetting(Context context){
//		AssetManager am = context.getAssets();
//		String internalStoragePath = context.getFilesDir().getAbsolutePath();
//
//		// Config File
//		try{
//			InputStream is = am.open(configFile);
//			FileOutputStream os = new FileOutputStream(new File(internalStoragePath,configFile));
//			byte[] buf = new byte[4096];
//			int length = 0;
//			while(( length = is.read(buf)) >= 0){
//				os.write(buf,0,length);
//	}
//
//			os.close();
//			is.close();
//
//		}catch (Exception e){
//			Log.e(TAG, "initAwsSetting: ",e );
//			}
//
//		// 証明書ファイル
//		try{
//			InputStream is = am.open(crtFile);
//			FileOutputStream os = new FileOutputStream(new File(internalStoragePath,crtFile));
//			byte[] buf = new byte[4096];
//			int length = 0;
//			while(( length = is.read(buf)) >= 0){
//				os.write(buf,0,length);
//	}
//
//			os.close();
//			is.close();
//
//		}catch (Exception e){
//			Log.e(TAG, "initAwsSetting: ",e );
//	}
//
//	}

	public void registerDevice( Context context, String deviceId )
	{
		String keyStorePath = context.getFilesDir().getAbsolutePath();
		Log.d(TAG, "keyStorePath: " + keyStorePath);
		// fetch provision of device
		mRepository.registerDevice(keyStorePath,
				deviceId,
				((status, data, throwable) -> {
					// notify result to subscriber
					Message message = new Message();
					switch (status) {
						case Success:
							Log.d(TAG, "registerDevice: RegistSuccess");
							message.what = AWS_REGIST_SUCCESS;
							break;
						case Error: {
							Log.d(TAG, "registerDevice: RegistFailure");

							message.what = AWS_REGIST_FAILED;
							break;
						}
					}
					mHandler.sendMessage(message);
				}));
	}

	public enum Event {
		REGISTERED,
		UNREGISTER,
		DEVICE_REGISTER_SUCCEEDED,
		DEVICE_REGISTER_FAILED,
	}

	public void timerTaskStart(){
//		UpdateAwsCredentialTask updateAwsCredentialTask = new UpdateAwsCredentialTask(LecipActivityManager.getMainActivity());
//		timer = new Timer();
//		timer.scheduleAtFixedRate(updateAwsCredentialTask,0,3000000);
	}

//	/***
//	 * AWSへ送信
//	 * @param messageDataToAws :AWSへ送るメッセージデータクラス
//	 */
//	public void sendMessageToAWS(AwsMessageData messageDataToAws){
//
//		new Thread(()->{
//			//Todo:@debugCode
//			String response = "";
//			switch ( messageDataToAws.getMethod()){
//				case SEISAN:
//					response = sendAwsSeisanDebug(messageDataToAws.getBody());
//					break;
//
//				default:
//					response = messageDataToAws.getBody();
//					break;
//			}
//			messageDataToAws.setResponse(response);
//			if( messageDataToAws.getHandler() != null ){
//				Message message = new Message();
//				message.what = messageDataToAws.getMethod();
//				message.obj = messageDataToAws;
//				messageDataToAws.getHandler().sendMessage(message);
//			}
//		},"AWS API Access").start();
//
//
//	}
//
//	/**
//	 * AWSでの運賃算出（LDMSができるまでの確認用）*
//	 * @param sendData
//	 * @return
//	 */
//	public String sendAwsSeisanDebug(String sendData){
//
//		String responseData = "";
//
//		String accessURL = ConstantString.tmpApiUrl;
//		Log.d(TAG, "URL: " + accessURL);
//		URL url = null;
//		HttpsURLConnection con = null;
//		try {
//
//			byte[] sendBytes = sendData.getBytes();
//			Log.d(TAG, "Send Data : " + sendData);
//			Log.d(TAG, "Send Bytes : " + DataTypeConverter.convertBytesToString(sendBytes));
//
//			url = new URL(accessURL);
//			con = (HttpsURLConnection) url.openConnection();
//			con.setRequestMethod("POST");
//			con.addRequestProperty("x-api-key",ConstantString.tmpApiKey);
//
//			//TODO:LIVUがネットワーク接続されていないので、ここで例外が発生する
//			OutputStream os = con.getOutputStream();
//			os.write(sendBytes);
//			os.close();
//
//			InputStream is = con.getInputStream();
//			responseData = AwsUtility.is2String(is);
//			Log.d(TAG, "AWS Connect Result : " + responseData);
//			is.close();
//
//		} catch (Exception e) {
//			if( con != null ) {
//				InputStream eIs = con.getErrorStream();
//				try {
//					responseData =  AwsUtility.is2String(eIs);
//					Log.d(TAG, "StatusCode: " +  con.getResponseCode());
//					Log.d(TAG, "Error Status : " + responseData);
//					eIs.close();
//				} catch (IOException ex) {
//					ex.printStackTrace();
//				}
//
//			}
//			e.printStackTrace();
//			Log.e(TAG, "AWS Connect Error: ",e);
//		}
//
//		return responseData;
//
//	}
}
