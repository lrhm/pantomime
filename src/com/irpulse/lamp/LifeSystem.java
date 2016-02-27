package com.irpulse.lamp;


import java.util.Calendar;

import com.irpulse.Utilities.KeyGen;
import com.irpulse.Utilities.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

/*
 * be sure to call LifeSystem.setSharedPrefrenses
 * then call loadFromSharedPrefs in LoadingActivity
 * 
 * 
 */

public class LifeSystem {
	public static int lifeCount = 5;
	public static Life[] lifes;

	public static SharedPreferences sharedPreferences;
	public static String sharedPrefsKey = KeyGen.base
			+ ".LifeSystemSharedPrefs";
	public static String deActive = "ReActiveLastTime";
	static Context context;

	public static void setSharedPrefrenses(Context context) {
		sharedPreferences = context.getSharedPreferences(sharedPrefsKey,
				Context.MODE_PRIVATE);
		LifeSystem.context = context;
	}

	public static void loadFromSharedPrefs() {
		if (lifes == null)
			lifes = new Life[lifeCount];
		for (int i = 0; i < 5; i++) {
			if(lifes[i] == null)
				lifes[i] = new Life(i);
		}

		long curTime = System.currentTimeMillis();

		for (int i = 0; i < lifeCount; i++) {
			long willActiveAtTime = sharedPreferences.getLong(deActive + i, -1);
			Life life = getLife(i);
			life.willActiveAtTime = willActiveAtTime;
	
			if (willActiveAtTime < 0) {
				life.setActive();
			} else {
				if (willActiveAtTime - System.currentTimeMillis() < 10000) {
					life.setActive();
				} else {
					setLifeDeActiveAtIndex(i, false);
				}
			}

		}
	}

	public static boolean setALifeDeActivated() {
		boolean isOneActive = false;
		int i;
		for (i = 0; i < lifeCount; i++) {
			if (lifes[i].isAlive) {
				isOneActive = true;
				break;
			}
		}
		if (!isOneActive)
			return false;

		lifes[i].setDeActive();
		setLifeDeActiveAtIndex(i, true);

		return true;

	}

	public static long getReborningETASecounds(int index) {
		if (lifes[index].isAlive)
			return -1;
		return (lifes[index].willActiveAtTime - System.currentTimeMillis()) / 1000;
	}

	public static int getDeadLifeCounts() {
		int i = 0;
		for (Life life : lifes) {
			if (!life.isAlive)
				i++;
		}
		return i;
	}

	private static void setLifeDeActiveAtIndex(int i, boolean fromUser) {
		lifes[i].isAlive = false;

		// set willActiveAtTime to sharedPrefs
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(deActive + i, lifes[i].willActiveAtTime);
		editor.commit();
		if (fromUser)
			Utils.backupLifes();

		// add life id to intent ( For LifeKillingReciver to know which life has
		// been transformed
		Intent intent = new Intent(context, LifeKillingReciver.class);
		Bundle bundle = new Bundle();
		bundle.putInt("lifeID", i);
		intent.putExtras(bundle);
		intent.setAction(LifeKillingReciver.lifeKillingAction);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i,
				intent, PendingIntent.FLAG_NO_CREATE);
		if (pendingIntent != null)
			return;
		pendingIntent = PendingIntent.getBroadcast(context, i, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarm.set(AlarmManager.RTC_WAKEUP, lifes[i].willActiveAtTime,
				pendingIntent);

	}

	public static Life getLife(int index) {
		if (lifes == null)
			loadFromSharedPrefs();
		return lifes[index];

	}

	public static LifeListener lifeListener;

	public static void setLifeListener(LifeListener lifeListener) {
		LifeSystem.lifeListener = lifeListener;
	}

	public interface LifeListener {
		void onLifeDeactived(int i);

		void onLifeReborend(int i);
	}

}
