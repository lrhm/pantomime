package com.irpulse.lamp;

import java.util.Set;

import com.irpulse.Utilities.KeyGen;
import com.irpulse.Utilities.NotificationMaker;
import com.irpulse.level.LevelData;
import com.irpulse.level.LevelManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class UserStimulator extends BroadcastReceiver {

	static public String ACTION = KeyGen.base + ".ACTION_STIMULATE";

	public static void setLastTime(long lastTime, Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				KeyGen.sharedPrefsKey, Context.MODE_PRIVATE);
		sharedPreferences.edit().putLong("userStimulator", lastTime).commit();

	}

	public static Long getLastTime(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				KeyGen.sharedPrefsKey, Context.MODE_PRIVATE);
		return sharedPreferences.getLong("userStimulator",
				System.currentTimeMillis());
	}

	public static long lastTime = 0;

	private static void setRunning(Context context) {
		lastTime = System.currentTimeMillis();
		setLastTime(lastTime, context);
	}

	public int getAllPurchaseHistory(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				KeyGen.base + "phistory", Context.MODE_PRIVATE);
		Set<String> set = sharedPreferences.getStringSet("historyOfMankind",
				null);
		int res = 0;
		String[] strings = null;
		strings = set.toArray(strings);
		if (strings == null)
			return 0;
		for (String string : strings) {
			res += Integer.getInteger(string);
		}
		return res;
	}

	int id = 2048;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
//		if (intent.getAction().equals(ACTION)) {

//			Log.d("onReceive", System.currentTimeMillis() - lastTime
//					+ " on off");
//			LevelData level = LevelManager.getLastLevel(context);
//			if(level == null)
//				return;
//
//			long interval = (System.currentTimeMillis() - lastTime) / 1000;
//
//			if (interval > 60  * 20) {
//				new NotificationMaker(context, "googooli", "maagoooli "
//						+ interval / 60  + " min shode " + level.answer.charAt(0), id);
//			}

			// if (calculateDaysOff() * 12 * 60 >= 14) {
			// NotificationMaker notif = new NotificationMaker(context,
			// "ab nabat", "miss you", 8234);
			// // TODO earn coin
			// }
			//
			// if (calculateDaysOff() * 12 * 60 >= 7) {
			// NotificationMaker notif = new NotificationMaker(context,
			// "ab nabat", "miss you 1", 8234);
			// }
//
//		}

	}

	public long calculateDaysOff() {
		long interval = System.currentTimeMillis() - lastTime;
		interval /= 1000;
		interval /= 3600;
		interval /= 12;
		return interval;
	}

}
