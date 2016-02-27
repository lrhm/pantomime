package com.irpulse.level;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

import com.irpulse.Utilities.Encryption;
import com.irpulse.Utilities.KeyGen;
import com.irpulse.Utilities.Utils;


public class LevelManager {

	private static ArrayList<LevelData> arrayList;
	static Boolean isLoaded = false;
	public static String baseDir = null;
	static boolean isRandomized = false;

	public static ArrayList<LevelData> getList() {
		return arrayList;
	}

	public static int getSize() {
		return arrayList.size();
	}

	public static int getSolvedCount() {
		return Utils.decryptToInt(LevelData.sharedPreferences.getString(
				"level_solved", Utils.enrcypt("0")));

	}

	public static String getEncryptedSolvedCount() {
		return LevelData.sharedPreferences.getString("level_solved",
				Utils.enrcypt("0"));
	}

	public static void setLevelSolvedCount(int count) {
		LevelData.sharedPreferences.edit()
				.putString("level_solved", Utils.enrcypt(count + "")).commit();
	}

	public static long getRandomSeed() {
		SharedPreferences sharedPreferences = LevelData.sharedPreferences;
		long seed = sharedPreferences.getLong("random_seed", 0);
		while (seed == 0) {
			seed = System.currentTimeMillis();
			if (seed != 0) {
				sharedPreferences.edit().putLong("random_seed", seed).commit();
			}
		}
		return seed;
	}

	public static void unLockNewLevel() {
		setLevelSolvedCount(getSolvedCount() + 1);
		Utils.setBackup();
	}

	private static void randomizeOrder() {

		if (isRandomized)
			return;
		// loadLevelManager();

		ArrayList<LevelData> temp = new ArrayList<LevelData>();
		Random random = new Random(getRandomSeed());

		Collections.sort(arrayList, new Comparator<LevelData>() {

			@Override
			public int compare(LevelData lhs, LevelData rhs) {
				// TODO Auto-generated method stub
				return (int) (lhs.priority - rhs.priority);
			}
		});
		int[] prioritysLastPlaces = new int[21];

		for (int i = 0; i < 21; i++)
			prioritysLastPlaces[i] = 0;

		int i = 0;
		for (LevelData level : arrayList) {


			prioritysLastPlaces[(int) level.priority]++;
		}

		prioritysLastPlaces[2]--;
		temp.add(arrayList.remove(1));
		for (i = 0; i < 21; i++) {

			int len = prioritysLastPlaces[i];
			while (len != 0) {
				temp.add(arrayList.remove(random.nextInt(len--)));
			}
		}

		// int k = 0;
		// while (!arrayList.isEmpty()) {
		// int len = (prioritysLastPlaces[k] - ((k == 0) ? 0
		// : prioritysLastPlaces[k - 1]));
		// k++;
		// Log.d("LevelManager", "len is " + len);
		// while (len != 0)
		// temp.add(arrayList.remove(random.nextInt(len--)));
		// }

		arrayList = temp;

		i = 0;
		for (LevelData level : temp) {
			level.randomID = i++;
			level.checkIsLocked();
		}
		Utils.setBackup();

		isRandomized = true;
	}

	public static synchronized void loadLevelManager() {

		if (isLoaded)
			return;
		arrayList = new ArrayList<LevelData>();

		loadFromFile();
		randomizeOrder();
		isLoaded = true;
	}

	public static LevelData getLevel(int id) {
		if (arrayList == null)
			loadLevelManager();

		return arrayList.get(id);
	}

	public static LevelData getLastLevel(Context context) {

		if (LevelData.sharedPreferences == null) {
			SharedPreferences sharedPreferences = context.getSharedPreferences(
					KeyGen.sharedPrefsKey, context.MODE_PRIVATE);

			LevelData.setSharedPrefs(sharedPreferences);
		}
		getLevel(0);

		if (getSolvedCount() == arrayList.size())
			return null;

		return getLevel(getSolvedCount());
	}

	public static void loadFromFile() {

		File baseFile = new File(baseDir);
		int max = 0;
		for (String l : baseFile.list()) {
			if (l.contains(".txt"))
				continue;
			Integer intTemp = Integer.parseInt(l.substring(3, l.length() - 1));
			if (intTemp > max)
				max = intTemp;
		}

		int last = max;
		int i = arrayList.size();

		Scanner scanner = null;
		// ArrayList<String> answers = new ArrayList<String>();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(baseDir + "/init.txt"), "UTF16"));

			scanner = new Scanner(in);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// while(scanner.hasNext()){
		// answers.add(Encryption.decryptAES(scanner.next(),
		// Utils.getAESkeyAnswer()));
		// }
		String answer = "گل گاو زبون";
		for (; i < last ; i++) {
			String path = baseDir + "a (" + (i + 1) + ")";
			if (i + 1 == 84)
				continue;
			
			answer = Encryption.decryptAES(scanner.nextLine(),
					Utils.getAESkeyAnswer());
			LevelData temp = new LevelData(path, answer);
			arrayList.add(temp);

		}

	}
}
