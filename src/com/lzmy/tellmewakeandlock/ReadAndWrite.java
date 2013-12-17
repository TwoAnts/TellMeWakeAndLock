package com.lzmy.tellmewakeandlock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

public class ReadAndWrite {
	
	/**
	 * this class only has two public static methods.
	 * @author hzy
	 */
	
	public static final String TAG = "ReadAndWrite"; 
	
	public static void writeTTlist(Context mContext, ArrayList<TimeToken> TTlist, String filename) {
		try {
			FileOutputStream fileOut = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(TTlist);
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		Log.d(TAG, "write list successful!");
	}
	

	@SuppressWarnings("unchecked" )
	public static ArrayList<TimeToken> readTTlist(Context mContext, String filename){
		ArrayList<TimeToken> TTlist = new ArrayList<TimeToken>();
		FileInputStream filein = null;
		File file = new File(mContext.getFilesDir()+File.separator+filename);
		if(file.exists() == false){
			return TTlist;
		}
		try{
			filein = mContext.openFileInput(filename);
			ObjectInputStream in = new ObjectInputStream(filein);
			TTlist = (ArrayList<TimeToken>)in.readObject();
			filein.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG, "read list successful!");
		return TTlist;
		
	}
	
	public static void writeMap(Context mContext, HashMap<String, Long> map, String filename ){
		try {
			FileOutputStream fileOut = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(map);
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		Log.d(TAG, "write map successful!");
	}
	
	@SuppressWarnings("unchecked" )
	public static HashMap<String, Long> readMap(Context mContext, String filename){
		HashMap<String, Long> map = new HashMap<String, Long>();
		FileInputStream filein = null;
		File file = new File(mContext.getFilesDir()+File.separator+filename);
		if(file.exists() == false){
			return map;
		}
		try{
			filein = mContext.openFileInput(filename);
			ObjectInputStream in = new ObjectInputStream(filein);
			map = (HashMap<String, Long>)in.readObject();
			filein.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG, "read map successful!");
		return map;
		
	}
	
}
 