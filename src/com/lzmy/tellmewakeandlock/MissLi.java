package com.lzmy.tellmewakeandlock;

import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MissLi {
	private long delay = 0;
	private long startTime = 0;
	private int weight = 0;
	private StringBuilder builder = null;
	private Timer mTimer = null;
	private Context mAppContext = null;
	private boolean flag = false; 
	private final int notify_id = 120; 
	private boolean isStart = false;
	
	public MissLi(Context mContext){
		this(60000, mContext);
	}
	
	public MissLi(long delay, Context mContext){
		this.delay = delay;
		this.mAppContext = mContext;
	}
	
	public void start(long startTime){
		this.startTime = startTime;
		mTimer = new Timer();
		builder = new StringBuilder();
		mTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				weight = (int)((System.currentTimeMillis() - MissLi.this.startTime)/(30*60000));
				switch(weight){
				    case 0:
				    	builder.append("你已经使用了超过30分钟！李小姐温馨提示！");
						flag = true;
						break;
					case 1:
						builder.append("你已经使用了超过30分钟！李小姐温馨提示！");
						flag = true;
						break;
					case 2:
						builder.append("你已经使用了超过一个小时！李小姐提醒您爱护眼睛！");
						flag = true;
						break;
					case 4:
						builder.append("你已经使用了超过两个小时！李小姐提醒您注意休息！");
						flag = true;
						break;
					case 6:
						builder.append("你已经使用了超过三个小时！李小姐提醒您注意身体！");
						flag = true;
						break;
					case 8:
						builder.append("你已经使用了超过四个小时！李小姐提醒注意手机电量！");
						flag = true;
						break;
					default:
						if(weight >= 5){
							builder.append("Legendary！你已经使用了超过五个小时！李小姐已经去睡觉了！");
							flag = true;
						}
						break;
				}
				if (flag) {
					notification();
					MissLi.this.cancel();
				}
			}
		}, delay);
		isStart = true;
	}
	
	private void notification(){
		if(mAppContext == null){
			return ;
		}
		NotificationManager notificationManager = (NotificationManager)mAppContext
							.getSystemService(Context.NOTIFICATION_SERVICE);
		
//		Intent intent = new Intent(mAppContext, ShareRenrenActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//指定要启动的activity
		NotificationCompat.Builder nbuilder = new NotificationCompat.Builder(mAppContext);
		Intent intent = new Intent(mAppContext, ShareRenrenActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pIntent = PendingIntent.getActivity(mAppContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		//初始化notification.builder
		nbuilder.addAction(R.drawable.ic_launcher, builder.toString(), pIntent);
		notificationManager.notify(notify_id, nbuilder.build());
		Log.d("MissLi", "send notification");
		
//		showNotification("haha", builder.toString(), notificationManager, "", intent, notify_id, R.drawable.ic_launcher);
	}
	
//	private void showNotification(String tickerText, String contentTitle, 
//			NotificationManager	notificationManager, String contentText,
//			Intent intent,int id, int resId){
//		Notification notification = notification =
//				new Notification(resId, tickerText, System.currentTimeMillis());
//		PendingIntent contentIntent = PendingIntent.getActivity(mAppContext, 0, intent, 0);
//		notification.setLatestEventInfo(mAppContext, contentTitle, contentText, contentIntent);
//		//  notificationManager是在类中定义的NotificationManager变量。在 onCreate方法中已经创建
//		notificationManager.notify(id, notification);
//		}
	 
	
	public boolean running(){
		return isStart;
	}
	
	public void cancel(){
		mTimer.cancel();
		isStart = false;
		this.startTime = 0;
	}
	
}
