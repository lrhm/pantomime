package com.irpulse.Utilities;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CheckingUtility implements Runnable {

	// TODO fix this
	static String baseURL = "http://solidrain.ir/untitled.php?token=";
	static String jsonURL = baseURL + "check";
	public static int packageVersion, notifIDVersion;
	public static String appVersion;
	public static boolean prices;
	private static Context context;
	private static SharedPreferences sharedPreferences;
	private String sharedPrefsKey = KeyGen.base + ".Versions";
	OnPricesChangedListener onPricesChangedListener;

	public CheckingUtility(Context context) {
		this.context = context;
		sharedPreferences = context.getSharedPreferences(sharedPrefsKey,
				Context.MODE_PRIVATE);
		packageVersion = sharedPreferences.getInt("Package_Version", 0);
		notifIDVersion = sharedPreferences.getInt("Notif_ID", 0);
		String versionName = "1.0.4";
		try {
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		appVersion = versionName;
		prices = sharedPreferences.getBoolean("PriceStateIsDefualt", true);
	}

	public CheckingUtility(Context context,
			OnPricesChangedListener onPricesChangedListener) {
		this.context = context;
		sharedPreferences = context.getSharedPreferences(sharedPrefsKey,
				Context.MODE_PRIVATE);
		packageVersion = sharedPreferences.getInt("Package_Version", 0);
		notifIDVersion = sharedPreferences.getInt("Notif_ID", 0);
		String versionName = "1.0.4";
		try {
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		appVersion = versionName;
		prices = sharedPreferences.getBoolean("PriceStateIsDefualt", true);

		this.onPricesChangedListener = onPricesChangedListener;

	}

	public void setSharedPrefs() {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("Package_Version", packageVersion);
		editor.putInt("Notif_ID", notifIDVersion);
		editor.putBoolean("PriceStateIsDefualt", prices);
		editor.commit();
	}

	public void setPrices(JSONArray json) {
		SharedPreferences.Editor editor = sharedPreferences.edit();

		for (int i = 0; i < 6; i++) {
			try {
				editor.putString(i + "price", json.getString(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		editor.commit();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		readAndPareData(fetchJSON(jsonURL));

	}

	public void addPackages(JSONObject json) {

	}

	public void readAndPareData(JSONObject reader) {

		if (reader == null)
			return;
		try {
			// JSONObject reader = new JSONObject(in);
			int LastPackageVersion = reader.getInt("Package_LastVersion");
			int lastNotifId = reader.getInt("Notif_ID_LastVersion");
			boolean getPricesState = reader.getBoolean("Price_State");
			String lastAppVersion = reader.getString("App_Version");
			if (LastPackageVersion > packageVersion) {
				addPackages(reader);
			}

			if (lastNotifId > notifIDVersion) {
				NotificationMaker notif = new NotificationMaker(context,
						fetchJSON(baseURL + "notif" + lastNotifId));
				notifIDVersion = lastNotifId;
				setSharedPrefs();
			}
			boolean flag = false;
			flag = getPricesState != prices;
			if (getPricesState == false) {
				// if (prices == true) {
				// new NotificationMaker(context, "قیمت ها تغیر کردن ! :دی",
				// "لامپ کارت داره", 1024);
				// }

				prices = false;
				setPrices(fetchJSONArray(baseURL + "prices"));
				setSharedPrefs();
			} else {

				prices = true;
				setSharedPrefs();
			}
			if (flag)
				if (onPricesChangedListener != null)
					onPricesChangedListener.onChanged();
			if (!flag)
				if (onPricesChangedListener != null)
					onPricesChangedListener.onNotChanged();

			if (!lastAppVersion.equals(appVersion)) {
				UpdateApp updater = new UpdateApp();
				if (reader.has("App_Version_URL"))
					updater.execute(reader.getString("App_Version_URL"));
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	String res = "";

	public JSONObject fetchJSON(final String urlString) {

		res = "";
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					URL url = new URL(urlString);
					int timeoutMillis = 10000;
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setReadTimeout(timeoutMillis);
					connection.setConnectTimeout(timeoutMillis);
					connection.setRequestMethod("GET");
					connection.setDoInput(true);
					connection.connect();

					// InputSource inputSource = connection.getInputStream();

					Scanner scanner = new Scanner(connection.getInputStream());
					String data = scanner.nextLine();
					while (scanner.hasNextLine())
						data += scanner.nextLine();
					res = data;

					// readAndPareData(data);
					scanner.close();

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block

					Log.d("CheckingUtility", "cachted malfurmed url ");
					if (onPricesChangedListener != null)
						onPricesChangedListener.onNotChanged();

					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block

					Log.d("CheckingUtility", "cachted io  ");
					if (onPricesChangedListener != null)
						onPricesChangedListener.onNotChanged();

					e.printStackTrace();
				}

			}
		});
		thread.start();
		try {
			thread.join();

			JSONObject reader = null;
			try {
				reader = new JSONObject(res);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return reader;

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Log.d("CheckingUtility", "cachted intrupted  ");
			if (onPricesChangedListener != null)
				onPricesChangedListener.onNotChanged();

			e.printStackTrace();
		}

		return null;

	}

	public JSONArray fetchJSONArray(final String urlString) {

		res = "";
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					URL url = new URL(urlString);
					int timeoutMillis = 3000;
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setReadTimeout(timeoutMillis);
					connection.setConnectTimeout(timeoutMillis);
					connection.setRequestMethod("GET");
					connection.setDoInput(true);
					connection.connect();

					// InputSource inputSource = connection.getInputStream();

					Scanner scanner = new Scanner(connection.getInputStream());
					String data = scanner.nextLine();
					while (scanner.hasNextLine())
						data += scanner.nextLine();
					res = data;

					// readAndPareData(data);
					scanner.close();

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		thread.start();
		try {
			thread.join();

			JSONArray reader = null;
			try {
				reader = new JSONArray(res);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return reader;

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public interface OnPricesChangedListener {
		public void onChanged();

		public void onNotChanged();
	}

}
