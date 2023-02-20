package com.app.awsconnect.manager;

import com.app.awsconnect.MainActivity;
import com.app.awsconnect.utils.ConstantNumber;


public class ActivityManager implements ConstantNumber {
	private static MainActivity mainActivity;

	public static MainActivity getMainActivity(){
		return mainActivity;
	}

	public static void setMainActivity(MainActivity mainActivity){
		ActivityManager.mainActivity = mainActivity;
	}

}
