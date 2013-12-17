package com.lzmy.tellmewakeandlock;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class WakeLockTimeSerivce extends Service {
	/**
	 * It can start when device starts.
	 * It can save one_day screen_on and screen_off time.
	 * Also,it can save your use time everyday;
	 * @author hzy
	 * @since 1.0
	 */
	
	
	public static final String TAG = "WakeLockTimeService";
	public static final String ACTION_UPDATE = "com.lzmy.tellmewakeandlock.action.update";
	public static final String ACTION_SEND_HISTORY = "com.lzmy.tellmewakeandlock.action.send_history";
	public static final String ACTION_SEND_LIST = "com.lzmy.tellmewakeandlock.action.send_list";
	public static final String ACTION_INIT_FINISHED = "com.lzmy.tellmewakeandlock.action.init_finished";
	
	public static final String FILENAME = "todayList.dat";
	private String mapName = null;
	
	private MissLi LiTongXue = null;
	
	private boolean screenopen = false;
	private TimeToken myTime = null;
	
	private ArrayList<TimeToken> list = null;
	private Context mAppContext = null;
	private ScreenActionReceiver mScreenActionReceiver = null;
	
	
	SharedPreferences sharedPreferences = null;
	SharedPreferences.Editor editor = null;
	
	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		
		return new myBinder();
	}
	
	public class myBinder extends Binder{
		public ArrayList<TimeToken> getList(){
			Log.d(TAG, "send list");
			return WakeLockTimeSerivce.this.getList();
		}
		public HashMap<String, Long> getMap(){
			Log.d(TAG, "send map");
			return WakeLockTimeSerivce.this.getMap();
		}
	}
	
	private ArrayList<TimeToken> getList(){
		long todayTime = System.currentTimeMillis()/86400000*86400000-(23-Calendar.ZONE_OFFSET)*3600000;
		TimeToken tempTime = null;
		if(list == null){
			return null;
		}
		while(list.size() > 0 && list.get(0).startTime < todayTime){
			tempTime = list.get(0);
			if(tempTime.endTime > todayTime){
				tempTime.startTime = todayTime;
				break;
			}else{
				list.remove(0);
			}
		}
		ReadAndWrite.writeTTlist(mAppContext, list, FILENAME);
		tempTime = new TimeToken();
		tempTime.startTime = myTime.startTime;
		tempTime.endTime = System.currentTimeMillis();
		ArrayList<TimeToken> mList = (ArrayList<TimeToken>) list.clone();
		mList.add(tempTime); 
		return mList;
	}
	
	private HashMap<String, Long> getMap(){
		HashMap<String, Long> map = ReadAndWrite.readMap(mAppContext, mapName);
		TimeToken tempTime = new TimeToken();
		tempTime.startTime = myTime.startTime;
		tempTime.endTime = System.currentTimeMillis();
		saveTokenToMap(tempTime, map);
		return map;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mAppContext = getApplicationContext();
		//history的文件名为Y年份.dat
		mapName = "Y"+Calendar.getInstance().get(Calendar.YEAR)+".dat";
		//初始化开始时间
		screenopen = true;
		myTime = new TimeToken();
		myTime.startTime = System.currentTimeMillis();
		//获取当天0时0分0秒的毫秒数
		long todayTime = myTime.startTime/86400000*86400000-(23-Calendar.ZONE_OFFSET)*3600000;
		//读取今天的list
		list = ReadAndWrite.readTTlist(mAppContext, FILENAME);
		if(list == null){
			list = new ArrayList<TimeToken>();
			Log.d(TAG, "read list is null!");
		}
		System.out.println("list.size() -- "+list.size());
		Log.d(TAG, "list.size() -- "+list.size());
		//去掉list中不是今天的部分
		int i = 0;
		while(list.size() > 0 && list.get(0).startTime < todayTime){
				if(list.get(0).endTime > todayTime){
					list.get(0).startTime = todayTime;
				}else{
					list.remove(0);
				}
			Log.d(TAG, "remove list item "+(i++));
		}
		
		//排除可能出现的异常数据，例如endTime = 0
		for(i = 0;i < list.size() - 1; i++){
			if(list.get(i).endTime == 0){
				list.get(i).endTime = list.get(i+1).startTime;
				i--;
			}
		}
		Log.d(TAG, "readlist cost "+(System.currentTimeMillis() - myTime.startTime)+"ms");
		
		Intent intent = new Intent(WakeLockTimeSerivce.this, MainActivity.class);
		intent.setAction(ACTION_INIT_FINISHED);
		sendBroadcast(intent);
		
		mScreenActionReceiver = new ScreenActionReceiver();
		mScreenActionReceiver.registerScreenActionReceiver(mAppContext);
		
		LiTongXue = new MissLi(this);
	}
	


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		DateFormat mFormat = DateFormat.getInstance();
		if(intent == null){
			Log.d(TAG, "intent is null!");
			return super.onStartCommand(intent, flags, startId);
		}
		if(intent.getAction() == null){
			return super.onStartCommand(intent, flags, startId);
		}
		//接受开屏时间
		if((intent.getAction().equals(ScreenActionReceiver.SCREEN_ON_TIME)
				|| intent.getAction().equals(ScreenActionReceiver.USER_PRESENT_TIME))
				&& screenopen == false){
			myTime = new TimeToken();
			myTime.startTime = System.currentTimeMillis();
			Log.d(TAG, "startTime--new---"+mFormat.format(myTime.startTime));
			screenopen = true;
			//启动MissLi
			if(LiTongXue.running()){
				LiTongXue.cancel();
			}
			LiTongXue.start(myTime.startTime);
			
			//为了避免没有接受到SCREEN_OFF_TIME的奇葩问题，在此假如一判断
//			if(list.get(list.size() - 1).endTime != 0){
//				list.add(mwtTime);
//				Log.d(TAG, "startTime--new---"+mFormat.format(mwtTime.startTime));
//			}
				//接受锁屏时间
		}else if(intent.getAction().equals(ScreenActionReceiver.SCREEN_OFF_TIME)
					&& screenopen == true){
			screenopen = false;
			myTime.endTime = System.currentTimeMillis();
			list.add(myTime);
			Log.d(TAG, "endTime--new---"+mFormat.format(myTime.endTime));
			
			//更新list和map文件
			SaveTimeToken(myTime);
			ReadAndWrite.writeTTlist(mAppContext, list, FILENAME);
			Log.d(TAG, "save the list");
		
//			TimeToken oldTime = list.get(list.size() - 1);
//			if(oldTime.endTime == 0){
//				oldTime.endTime = intent.getLongExtra("off_time", 0);
//				Log.d(TAG, "endTime--new---"+mFormat.format(oldTime.endTime));
//				SaveTimeToken(oldTime);
//				ReadAndWrite.writeTTlist(mAppContext, list, FILENAME);
//				Log.d(TAG, "save the list");
//			}
			//接受MainActivity的获取History请求
		}else if (intent.getAction().equals(MainActivity.ACTION_GET_HISTORY)){
			Intent mIntent = new Intent();
			mIntent.setAction(ACTION_SEND_HISTORY);
			//注释代码部分为sharedpreferences数据存储方式
//			mIntent.putExtra("current_history", HistoryToMap(sharedPreferences));
			mIntent.putExtra("current_history", getMap());
			sendBroadcast(mIntent);
			Log.d(TAG, "send current history");
			//接受MainActivity的获取list请求
		}else if(intent.getAction().equals(MainActivity.ACTION_GET_LIST)){
			Intent mIntent = new Intent();
			mIntent.setAction(ACTION_SEND_LIST);
 			mIntent.putExtra("current_list", getList()); 
			sendBroadcast(mIntent);
			Log.d(TAG, "send current list");
		}
		return super.onStartCommand(intent, flags, startId);
	}



	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		//若service关闭，将最后一个开屏时间与当前时间放一起，算出时间差，储存到文件里
		if(myTime != null && myTime.startTime != 0 && screenopen == true){
			screenopen = false;
			myTime.endTime = System.currentTimeMillis();
			SaveTimeToken(myTime);
			Log.d(TAG, "save the last TimeToken");
			list.add(myTime);
		}
		ReadAndWrite.writeTTlist(mAppContext, list, FILENAME);
		Log.d(TAG, "save the list");
		
		mScreenActionReceiver.unRegisterScreenActionReceiver(getApplicationContext());
		super.onDestroy();
	}
	
	private void SaveTimeToken(TimeToken mTime){
		HashMap<String, Long> map = ReadAndWrite.readMap(mAppContext, mapName);
		saveTokenToMap(mTime, map);
		ReadAndWrite.writeMap(mAppContext, map, mapName);
	}
	
	private void saveTokenToMap(TimeToken mTime, HashMap<String, Long> mMap){
		DateFormat dayFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
		//获取时间所在日期的0时0分0秒的毫秒数
		long startTime = mTime.startTime/86400000*86400000-(23-Calendar.ZONE_OFFSET)*3600000;
		long endTime = mTime.endTime/86400000*86400000-(23-Calendar.ZONE_OFFSET)*3600000;
		long totalTime = 0;
		
		//判断startTime和endTime是否在同一天
		//以天为单位，为时间分段，记录每一天的所用时间
		if(startTime == endTime){
			if(mMap.containsKey(dayFormat.format(startTime))){
				totalTime = (long)mMap.get(dayFormat.format(startTime));
			}
			
			totalTime += mTime.endTime - mTime.startTime;
			mMap.put(dayFormat.format(startTime), totalTime);
			Log.d(TAG, dayFormat.format(startTime)+" "+(int)(totalTime/60000)+"min");
		}else if(endTime > startTime){
			if(mMap.containsKey(dayFormat.format(startTime))){
				totalTime = (long)mMap.get(dayFormat.format(startTime));
			}
			totalTime += endTime - mTime.startTime;
			mMap.put(dayFormat.format(startTime), totalTime);
			Log.d(TAG, dayFormat.format(startTime)+" "+(int)(totalTime/60000)+"min");
			if(mMap.containsKey(dayFormat.format(endTime))){
				totalTime = (long)mMap.get(dayFormat.format(endTime));
			}else{
				totalTime = 0;
			}
			totalTime += mTime.endTime - endTime;
			mMap.put(dayFormat.format(endTime), totalTime);
			Log.d(TAG, dayFormat.format(endTime)+" "+(int)(totalTime/60000)+"min");
		}
		
	}
	
	
	//sharedpreferences数据存储方式，访问history的函数
//	private HashMap<String, Long> HistoryToMap(SharedPreferences msharedPreferences){
//		DateFormat dayFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
//		long StartTime = msharedPreferences.getLong("StartTime", 0);
//		Log.d(TAG, "StartTime "+dayFormat.format(StartTime));
//		if(StartTime == 0){
//			return null;
//		}
//		HashMap<String, Long> map = new HashMap<String, Long>();
//		String dayString = null;
//		long totalTime = 0;
//		for(long time = StartTime;time < System.currentTimeMillis(); time += 86400000){
//			dayString = dayFormat.format(time);
//			totalTime = msharedPreferences.getLong(dayFormat.format(time), 0);
//			if(totalTime == 0){
//				continue;
//			}else{
//				map.put(dayString, totalTime);
//				Log.d(TAG, "History "+dayString+" "+totalTime+"ms即"+totalTime/60000+"min");
//			}
//		}
//		
//		return map;
//	}
	
	
}
