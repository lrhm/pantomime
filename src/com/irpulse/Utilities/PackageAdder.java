package com.irpulse.Utilities;

import java.io.File;

import com.irpulse.level.LevelListManager;
import com.irpulse.level.LevelManager;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

public class PackageAdder {

	String url;

	public PackageAdder(String url, final Context context) {
		this.url = url;

		final NotificationMaker updateNotif = new NotificationMaker(context,
				"updating....", "Updating Package", 1001);

		DownloadFilesTask dl = new DownloadFilesTask(
				new DownloadFilesTask.DownloadProgressListener() {

					@Override
					public void onErrorOccured(Exception e) {
						// TODO Auto-generated method stub
						updateNotif.setContentTextAfterProgress("error :(");
					}

					@Override
					public void onDownloadProgressChanged(int amount) {
						// TODO Auto-generated method stub
						updateNotif.setProgress(amount);
					}

					@Override
					public void onDownloadCompleted(final String path,
							final String fileName) {
						// TODO Auto-generated method stub
						updateNotif
								.setContentTextAfterProgress("downloded sucessfully");
						String extractFolder = context.getFilesDir().toString()
								+ "/LevelListData/";
						UnZipUtil unZipUtil = new UnZipUtil(path + fileName,
								extractFolder, "",
								new UnZipUtil.OnZipListener() {

									@Override
									public void onZipProgress(int progress) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onExtractEnded() {
										// TODO Auto-generated method stub

										LevelListManager.loadFromFile();
										LevelManager.loadFromFile();

										File f = new File(path + fileName);
										f.delete();
										updateNotif
												.setContentTextAfterProgress("levels added");
									}

									@Override
									public void onErrorOccured(Exception e) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onBeforeExtracting() {
										// TODO Auto-generated method stub

										updateNotif
												.setContentTextAfterProgress("decompresing");
									}
								});

					}
				});

		dl.execute(url);
	}
}
