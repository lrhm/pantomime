package com.irpulse.level;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import com.irpulse.Utilities.SizeManager;

import android.util.Log;

public class LevelListData {


	public class Size {
		public float x, y, w, h;

		public Size(float x, float y, float w, float h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
	}
	public static void initDeviceSizes() {

		LevelListData.mWidth = SizeManager.getScreenWidth();
		LevelListData.mHeight = (int) ( (mWidth/1080. ) * 480.);
	}


	public float convertWidth(double amount) {
		// Log.d("t", Width + " " + Height);
		return (float) ((amount / 1080) * (mWidth));
	}

	public float convertHeight(double amount) {
		return (float) (((amount / 480) * mHeight));
	}
	
	public float convertWidth(int amount) {
		// Log.d("t", Width + " " + Height);
		return (float) ((amount / 1080.) * (mWidth));
	}

	public float convertHeight(int amount) {
		return (float) (((amount / 480.) * mHeight));
	}
	public int ID;
	public String  path = null;
	public String picPath = null;
	public static int mHeight, mWidth;
	public String imageName;
	public int imageDrawable;
	public ArrayList<Size> sizes = new ArrayList<LevelListData.Size>();
	public ArrayList<Integer> levelIDs = new ArrayList<Integer>();

	public LevelListData(int imageDrawable) {
		this.imageDrawable = imageDrawable;
		this.ID = LevelListManager.ListCount;
		LevelListManager.ListCount++;
		
		
	}
	
	public LevelListData(String path){
		this.path =  path;
		this.ID = LevelListManager.ListCount;
		LevelListManager.ListCount++;
		picPath = path +"bg_"+ (ID+1) +".jpg";
		
		initFromFile();
	}

	public void addButton(double x, double y, double w, double h) {
		sizes.add(new Size(convertWidth(x), convertHeight(y), convertWidth(w),
				convertHeight(h)));
//		levelIDs.add(level -1);
	}

	public void addButton(int x, int y, int w, int h , int level) {
		sizes.add(new Size(convertWidth(x), convertHeight(y), convertWidth(w),
				convertHeight(h)));
		levelIDs.add(level -1);
	}

	public void initFromFile(){
		try {
			File f =new File( path+"init_"+ (this.ID+1) +".txt");
			Scanner scanner = new Scanner(f);
			while(scanner.hasNext()){
				addButton(scanner.nextInt()	, scanner.nextInt(), scanner.nextInt() , scanner.nextInt() , scanner.nextInt());
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


}
