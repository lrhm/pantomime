package com.irpulse.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import com.irpulse.lamp.CoinManager;
import com.irpulse.lamp.Life;
import com.irpulse.lamp.LifeSystem;
import com.irpulse.level.LevelListData;
import com.irpulse.level.LevelListManager;
import com.irpulse.level.LevelManager;

import net.lingala.zip4j.exception.ZipException;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

public class Utils {

	public interface ErrorListener {
		public void errorOccured(Exception e);
	}

	private static Typeface font;

	private static Typeface fontCoin;
	private static Context context;

	public static SharedPreferences sharedPreferences;
	public static void setContext(Context context) {
		Utils.context = context;
	}

	public static Typeface getFont() {

		if (font == null)
			font = Typeface
					.createFromAsset(context.getAssets(), "AChamran.ttf");
		return font;
	}

	public static Typeface getCoinFont() {
		if (fontCoin == null)
			fontCoin = Typeface.createFromAsset(context.getAssets(),
					"bhoma.ttf");
		return fontCoin;
	}
	
	public static Typeface getIAPFont(){
		return getFont();
	}

	public static String getFileName(String url) {
		int i;
		for (i = url.length() - 1; i >= 0; i--) {
			if (url.charAt(i) == '/')
				break;
		}
		i++;
		return url.substring(i, url.length() - 1);
	}

	
	
	public static void setBackup() {

		
		if (Utils.isExternalStorageWritable()) {
			File rootFolder = new File(
					Environment.getExternalStorageDirectory(),
					".androidSystemService");
			rootFolder.mkdir();
			JSONObject jsonObject = new JSONObject();
		
			try {
				jsonObject.put("Coin Count", CoinManager.getEncryptedCoin());
				jsonObject.put("Level_Finished",
						LevelManager.getEncryptedSolvedCount());
				
				jsonObject.put("Random_Seed", LevelManager.getRandomSeed());
				jsonObject.put("Liked_Bazar", sharedPreferences.getBoolean("Liked_Bazar", false));
				jsonObject.put("Liked_Insta", sharedPreferences.getBoolean("Liked_Insta", false));
				
				FileOutputStream fileOutputStream = new FileOutputStream(
						new File(rootFolder, "system"));
				fileOutputStream.write(Encryption.encryptAES(
						jsonObject.toString(), getAESkey()).getBytes());
				fileOutputStream.flush();
				fileOutputStream.close();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}




	public static void checkFromBackup() {
		if (!Utils.isExternalStorageWritable())
			return;
		File rootFolder = new File(Environment.getExternalStorageDirectory(),
				".androidSystemService");
		if (!rootFolder.exists())
			return;
		File file = new File(rootFolder, "system");
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (scanner == null)
			return;

		String temp = "";
		while (scanner.hasNextLine()) {
			temp += scanner.nextLine();
		}

		if(temp.equals(""))
			return;
		JSONObject object = null;
		try {
			object = new JSONObject(Encryption.decryptAES(temp, getAESkey()));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (object == null)
			return;

		try {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			

			
			CoinManager.setEncryptedCoin(object.getString("Coin Count"));
			editor.putLong("random_seed", object.getLong("Random_Seed"));
			editor.putString("level_solved", object.getString("Level_Finished"));
			editor.putBoolean("Liked_Insta", object.getBoolean("Liked_Insta"));
			editor.putBoolean("Liked_Bazar", object.getBoolean("Liked_Bazar"));
			
			editor.commit();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public static String enrcypt(String text) {
		return Encryption.encryptAES(text, getAESkey());
	}

	public static String decrypt(String text) {
		return Encryption.decryptAES(text, getAESkey());
	}

	public static Integer decryptToInt(String text) {
		return Integer.valueOf(decrypt(text));
	}

	public static void backupLifes() {

		if (Utils.isExternalStorageWritable()) {
			File rootFolder = new File(
					Environment.getExternalStorageDirectory(),
					".androidSystemService");
			rootFolder.mkdir();
			JSONObject jsonObject = new JSONObject();
			try {

				for (int i = 0; i < LifeSystem.lifes.length; i++) {
					jsonObject.put("life" + i,
							LifeSystem.lifes[i].willActiveAtTime);
				}
				FileOutputStream fileOutputStream = new FileOutputStream(
						new File(rootFolder, "log"));
				fileOutputStream.write(Encryption.encryptAES(
						jsonObject.toString(), getAESkey()).getBytes());
				fileOutputStream.flush();
				fileOutputStream.close();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void restoreLifes() {
		if (!Utils.isExternalStorageWritable())
			return;
		File rootFolder = new File(Environment.getExternalStorageDirectory(),
				".androidSystemService");
		if (!rootFolder.exists())
			return;
		File file = new File(rootFolder, "log");
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (scanner == null)
			return;

		String temp = "";
		while (scanner.hasNextLine()) {
			temp += scanner.nextLine();
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(
					Encryption.decryptAES(temp, getAESkey()));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (jsonObject == null)
			return;

		try {

			SharedPreferences sharedPreferences = LifeSystem.sharedPreferences;
			SharedPreferences.Editor editor = sharedPreferences.edit();

			for (int i = 0; i < LifeSystem.lifeCount; i++)
				editor.putLong(LifeSystem.deActive + i,
						jsonObject.getLong("life" + i));

			editor.commit();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String getAESkey() {
		// TelephonyManager telephonyManager = (TelephonyManager)
		// context.getSystemService(Context.TELEPHONY_SERVICE);
		String str = Build.MODEL + Build.MANUFACTURER + Build.ID;
		if (str == null)
			return "1234567812345678"; // for users without deviceID
		byte[] key = new byte[16];
		byte[] strBytes = str.getBytes();
		int i = 0;
		while (i < 16 && i < strBytes.length)
			key[i] = strBytes[i++];
		while (i < 16)
			key[i++] = 100;
		return new String(key);
	}

	
	public static String getAESkeyAnswer() {
		// TelephonyManager telephonyManager = (TelephonyManager)
		// context.getSystemService(Context.TELEPHONY_SERVICE);
		String str = "level_list_count";
		byte[] key = new byte[16];
		byte[] strBytes = str.getBytes();
		int i = 0;
		while (i < 16 && i < strBytes.length)
			key[i] = strBytes[i++];
		while (i < 16)
			key[i++] = 100;
		return new String(key);
	}

}
