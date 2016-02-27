package com.irpulse.level;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import android.content.SharedPreferences;
import android.util.Log;

public class LevelData {

	public int randomID;
	public final int levelId;
	public float priority;

	public int[] resIds = null;

	public String[] imagePaths = null;

	public final String answer;

	private boolean isLocked;

	public static SharedPreferences sharedPreferences;

	public static void setSharedPrefs(SharedPreferences sharedPrefs) {
		sharedPreferences = sharedPrefs;
	}

	static int levelCount = 0;

	public LevelData(int[] resId, String answer) {

		levelId = levelCount++;
		resIds = resId;
		this.answer = answer;
	}

	public LevelData(String levelDir, String answer) {

		levelId = levelCount++;

		String[] temp = answer.split(" ");
		String temp2 = "";
		for (int i = 0; i < temp.length - 1; i++) {
			if (i != 0)
				temp2 += " ";
			temp2 += temp[i];
		}
		priority = Float.parseFloat(temp[temp.length - 1]) * 2;

		this.answer = temp2;
		File picDir = new File(levelDir + "/");
		int len = picDir.list().length;
		imagePaths = new String[len];
		for (int i = 0; i < len; i++) {
			imagePaths[i] = levelDir + "/" + (i + 1) + ".jpg";
		}

	}

	public void checkIsLocked() {
		isLocked = (this.randomID > LevelManager.getSolvedCount());

	}

	public boolean isLocked() {
		checkIsLocked();
		return isLocked;
	}

}
