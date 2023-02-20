package com.app.awsconnect.manager;

import android.os.Handler;

import com.app.awsconnect.MainActivity;
import com.app.awsconnect.utils.ConstantNumber;
import com.app.awsconnect.view.LoadingDialogFragment;



public class ActivityManager implements ConstantNumber {
	private static MainActivity mainActivity;
	private static LoadingDialogFragment dialogFragment;
	private static Handler mHandler;
	private static Handler changeInfoTextHandler;

	public static MainActivity getMainActivity(){
		return mainActivity;
	}

	public static void setMainActivity(MainActivity mainActivity){
		ActivityManager.mainActivity = mainActivity;
	}

	public static LoadingDialogFragment getDialogFragment() {
		return dialogFragment;
	}

	public static void setDialogFragment(LoadingDialogFragment dialogFragment) {
		ActivityManager.dialogFragment = dialogFragment;
	}

	public static Handler getHandler() {
		return mHandler;
	}

	public static void setHandler(Handler mHandler) {
		ActivityManager.mHandler = mHandler;
	}

	public static Handler getChangeInfoTextHandler() {
		return changeInfoTextHandler;
	}

	public static void setChangeInfoTextHandler(Handler changeInfoTextHandler) {
		ActivityManager.changeInfoTextHandler = changeInfoTextHandler;
	}
}
