package com.irpulse.Utilities;

import java.io.InputStream;

import android.os.Handler;
import android.util.Log;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class UnZipUtil {

	String zipFilePath;
	private String extractFolder, Password;

	public interface OnZipListener {
		
		public void onZipProgress(int progress);
		
		public void onBeforeExtracting();

		public void onExtractEnded();

		public void onErrorOccured(Exception e);
	}

	public UnZipUtil(String zipFilePath, String extractFolder,
			String passworld, OnZipListener onZipListener) {
		this.zipFilePath = zipFilePath;
		this.extractFolder = extractFolder;
		this.Password = passworld;
		this.onZipListener = onZipListener;
		run();
	}

	public void run() {
		if (onZipListener != null)
			onZipListener.onBeforeExtracting();
		try {

			zipExtractAll(zipFilePath, extractFolder, Password);
			// zipExt

			if (onZipListener != null)
				onZipListener.onExtractEnded();
		} catch (ZipException e) {
			// TODO Auto-generated catch block

			if (onZipListener != null)
				onZipListener.onErrorOccured(e);
			e.printStackTrace();
		}
	}

	OnZipListener onZipListener;

	// should be run in a thread
	public  void zipExtractAll(String zipFilePath, String extractFolder,
			String Password) throws ZipException {

		final ZipFile zipFile = new ZipFile(zipFilePath);
		if (Password.length() != 0)
			zipFile.setPassword(Password);


		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				Log.d("zipfile", zipFile.getProgressMonitor().getPercentDone()
						+ " % is done");

				if (onZipListener != null)
					onZipListener.onZipProgress(zipFile.getProgressMonitor().getPercentDone());
				if(zipFile.getProgressMonitor().getPercentDone() == 100)
					return;
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				new Thread(this).start();
				
			}
		}).start();

		
		zipFile.extractAll(extractFolder);
		

	}

}
