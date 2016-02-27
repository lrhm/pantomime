package com.irpulse.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class DownloadFilesTask extends AsyncTask<String, Void, Void> {
	public int lenghtOfFile;

	public interface DownloadProgressListener {
		
		public void onDownloadProgressChanged(int amount);
		public void onDownloadCompleted(String path , String fileName);
		public void onErrorOccured(Exception e);
	}

	DownloadProgressListener downloadProgressListener;

	public void setDownloadProgressListener(DownloadProgressListener listener) {
		this.downloadProgressListener = listener;
	}

	public DownloadFilesTask(DownloadProgressListener listener) {
		setDownloadProgressListener(listener);
	}

	@Override
	protected Void doInBackground(String... arg0) {
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
			File outputFile = new File(file, getFileName(arg0[0]));
			if (outputFile.exists()) {
				outputFile.delete();
			}
			Log.d("updateTag", lenghtOfFile + " size");
			FileOutputStream fos = new FileOutputStream(outputFile);

			InputStream is = c.getInputStream();

			byte[] buffer = new byte[1024];
			int len1 = 0;
			int sum = 0;
			while ((len1 = is.read(buffer)) > -1) {
				fos.write(buffer, 0, len1);
				sum += len1;

				downloadProgressListener
						.onDownloadProgressChanged((int) (100 * ((double) sum) / lenghtOfFile));
			}

			Log.d("updateTag", sum + " size of downloaded in bytes");
			fos.close();
			is.close();

			downloadProgressListener.onDownloadCompleted(PATH , getFileName(arg0[0]));
			// Log.d("updateTag", Environment.getExternalStorageDirectory()+"");
			// intent.setDataAndType(Uri.fromFile(new File(
			// Environment.getExternalStorageDirectory() + "/download/" +
			// "app.apk")), "application/vnd.android.package-archive");
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this
			// flag android returned a intent error!
			// context.startActivity(intent);

		} catch (Exception e) {
			Log.e("UpdateAPP", "Update error! " + e.getMessage());
			downloadProgressListener.onErrorOccured(e);
		}
		return null;
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
}