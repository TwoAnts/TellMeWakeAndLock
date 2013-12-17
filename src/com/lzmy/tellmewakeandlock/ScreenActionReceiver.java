package com.lzmy.tellmewakeandlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class ScreenActionReceiver extends BroadcastReceiver {
	
	public static final String SCREEN_ON_TIME = "com.lzmy.tellmewakeandlock.action.SCREEN_ON_TIME";
	public static final String SCREEN_OFF_TIME = "com.lzmy.tellmewakeandlock.action.SCREEN_OFF_TIME";
	public static final String USER_PRESENT_TIME = "com.lzmy.tellmewakeandlock.action.USER_PRESENT_TIME";

	private String TAG = "ScreenActionReceiver";
	private boolean isRegisterReceiver = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_SCREEN_ON)) {
			Log.d(TAG, "屏幕解锁广播...");
			Intent mIntent = new Intent();
			mIntent.setClass(context, WakeLockTimeSerivce.class);
			mIntent.setAction(SCREEN_ON_TIME);
			mIntent.putExtra("on_time", System.currentTimeMillis());
			context.startService(mIntent);
		}else if(action.equals(Intent.ACTION_USER_PRESENT)){
			Log.d(TAG, "用户操作广播...");
			Intent mIntent = new Intent();
			mIntent.setClass(context, WakeLockTimeSerivce.class);
			mIntent.setAction(USER_PRESENT_TIME);
			mIntent.putExtra("on_time", System.currentTimeMillis());
			context.startService(mIntent);
		} else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
			Log.d(TAG, "屏幕加锁广播...");
			Intent mIntent = new Intent();
			mIntent.setClass(context, WakeLockTimeSerivce.class);
			mIntent.setAction(SCREEN_OFF_TIME);
			mIntent.putExtra("off_time", System.currentTimeMillis());
			context.startService(mIntent);
		}
	}

	
	
	public void registerScreenActionReceiver(Context mContext) {
		if (!isRegisterReceiver) {
			isRegisterReceiver = true;

			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			filter.addAction(Intent.ACTION_SCREEN_ON);
			filter.addAction(Intent.ACTION_USER_PRESENT);
			Log.d(TAG, "注册屏幕解锁、加锁广播接收者...");
			mContext.registerReceiver(ScreenActionReceiver.this, filter);
		}
	}

	public void unRegisterScreenActionReceiver(Context mContext) {
		if (isRegisterReceiver) {
			isRegisterReceiver = false;
			Log.d(TAG, "注销屏幕解锁、加锁广播接收者...");
			mContext.unregisterReceiver(ScreenActionReceiver.this);
		}
	}

}
