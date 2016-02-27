package com.irpulse.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class UpdateApp extends AsyncTask<String, Void, Void> {
	private Context context;
	public int lenghtOfFile;

	public void setContext(Context contextf) {
		context = contextf;
	}

	@Override
	protected Void doInBackground(String... arg0) {

		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected())

			try {
				URL url = new URL(arg0[0]);
				HttpURLConnection c = (HttpURLConnection) url.openConnection();
				c.setRequestMethod("GET");
				c.setDoOutput(true);
				c.connect();

				lenghtOfFile = c.getContentLength();
				String PATH = Environment.getExternalStorageDirectory()
						+ "/download/";
				File file = new File(PATH);
				if (!file.exists())
					file.mkdirs();
				File outputFile = new File(file, "lamp.apk");
				if (outputFile.exists()) {
					outputFile.delete();
				}
				FileOutputStream fos = new FileOutputStream(outputFile);

				InputStream is = c.getInputStream();

				byte[] buffer = new byte[1024];
				int len1 = 0;
				int sum = 0;
				while ((len1 = is.read(buffer)) > -1) {
					fos.write(buffer, 0, len1);
					sum += len1;
				}

				fos.close();
				is.close();

				Intent intent = new Intent(Intent.ACTION_VIEW);

				intent.setDataAndType(Uri.fromFile(new File(Environment
						.getExternalStorageDirectory()
						+ "/download/"
						+ "lamp.apk")),
						"application/vnd.android.package-archive");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this
																// flag
																// android
																// returned
																// a intent
																// error!
				context.startActivity(intent);

			} catch (Exception e) {
				Log.e("UpdateAPP", "Update error! " + e.getMessage());
			}
		return null;
	}
}