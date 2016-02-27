package com.irpulse.lamp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Random;

import com.irpulse.Utilities.CheckingUtility;
import com.irpulse.Utilities.KeyGen;
import com.irpulse.Utilities.NotificationMaker;
import com.irpulse.Utilities.SizeConverter;
import com.irpulse.Utilities.SizeManager;
import com.irpulse.Utilities.UnZipUtil;
import com.irpulse.Utilities.Utils;
import com.irpulse.level.LevelData;
import com.irpulse.level.LevelListData;
import com.irpulse.level.LevelListManager;
import com.irpulse.level.LevelManager;

import com.irpulse.lamp.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;

/*
 * Loading Activity , should load images , Lifes etc in here
 * check json for notif and stuff
 * 
 */

public class LoadingActivity extends FragmentActivity {

	ImageView imageView;
	SizeConverter imageConverter;
	LoadingDialog dialog;
	Handler dialogHandler;
	boolean isFirstLoading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		ImageManager.initCache(this);

		initSizes();

		imageView = (ImageView) findViewById(R.id.loading_activity_img);
		imageConverter = SizeConverter.SizeConverterFromLessOffset(
				SizeManager.getScreenWidth(), SizeManager.getScreenHeight(),
				1080, 1920);

		imageView.setImageBitmap(ImageManager.scaledBitmapFromResource(
				getResources(), R.drawable.splash, imageConverter.mHeight,
				imageConverter.mWidth, ImageManager.CACHE_NO));

		imageView.setScaleType(ScaleType.MATRIX);
		android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) imageView
				.getLayoutParams();
		params.leftMargin = imageConverter.getLeftOffset() / 2;
		params.topMargin = imageConverter.getTopOffset() / 2;
		imageView.setLayoutParams(params);
		dialog = new LoadingDialog(LoadingDialog.STYLE_PROGRESS, this);
		SharedPreferences sharedPreferences = this.getSharedPreferences(
				KeyGen.sharedPrefsKey, Context.MODE_PRIVATE);

		isFirstLoading = !sharedPreferences.getBoolean("unziped", false);

		dialogHandler = new Handler();
		dialogHandler.postDelayed(showDialog, 2000);

		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {

				Looper.prepare();
				initUtils();
				new Handler().post(new CheckingUtility(LoadingActivity.this));
				
				unZipForFirstTime();

				Intent i = new Intent(LoadingActivity.this, ListActivity.class);
				startActivity(i);

				if (dialog != null)
					if (dialog.getDialog() != null)
						dialog.dismiss();
				finish();

				Looper.loop();

			}
		});
		th.start();

		// new Handler().post(new Runnable() {
		//
		// @Override
		// public void run() {
		// initUtils();
		// unZipForFirstTime();
		// Intent i = new Intent(LoadingActivity.this, ListActivity.class);
		// startActivity(i);
		// finish();
		//
		// }
		// } );
	}

	Runnable showDialog = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// if (dialog == null)
			// dialog = new LoadingDialog(LoadingDialog.STYLE_PROGRESS,
			// getApplicationContext());

			if (dialog.getDialog() != null)
				dialog = new LoadingDialog(LoadingDialog.STYLE_PROGRESS,
						getApplicationContext(), isFirstLoading);

			dialog.show(getSupportFragmentManager(), "");
			dialog.updateProgress(1);

		}
	};

	protected void onStop() {
		super.onStop();
		dialogHandler.removeCallbacks(showDialog);

	};

	public void copyFromAsstesList() {
		dialog.updateProgress(1);
		try {
			InputStream stream = getAssets().open("listfinal.zip");
			File f = new File(getFilesDir() + "/data/");
			if (!f.exists())
				f.mkdir();
			OutputStream output = new BufferedOutputStream(
					new FileOutputStream(getFilesDir() + "/data/listfinal.zip"));

			byte data[] = new byte[1024];
			int count;

			while ((count = stream.read(data)) != -1) {
				output.write(data, 0, count);
			}

			output.flush();
			output.close();
			stream.close();
			dialog.updateProgress(5);
			output = null;
			stream = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void copyFromAsstesLevel() {
		try {
			InputStream stream = getAssets().open("level_package_1.zip");
			File f = new File(getFilesDir() + "/data/");
			if (!f.exists())
				f.mkdir();
			OutputStream output = new BufferedOutputStream(
					new FileOutputStream(getFilesDir()
							+ "/data/level_package_1.zip"));

			byte data[] = new byte[1024];
			int count;

			while ((count = stream.read(data)) != -1) {
				output.write(data, 0, count);
			}

			dialog.updateProgress(3);
			output.flush();
			output.close();
			stream.close();
			output = null;
			stream = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void unZipForFirstTime() {
		final SharedPreferences sharedPreferences = this.getSharedPreferences(
				KeyGen.sharedPrefsKey, Context.MODE_PRIVATE);
		if (sharedPreferences.getBoolean("unziped", false)) {

			dialog.updateProgress(1);

			loadIAP();

			dialog.updateProgress(1);
			LevelManager.loadLevelManager();

			dialog.updateProgress(1);
			// LevelManager.randomizeOrder();
			// setUserStimulator();

			LevelListManager.loadLevelListData();

			dialog.updateProgress(1);
			int places[] = ImageManager.loadListImages(getResources());
			int last = (places.length == 1) ? LevelListManager.ListCount
					: places[1];

			ImageManager.loadListFromTo(0, last, getResources(),
					new ImageManager.LoadingProgressListener() {

						@Override
						public void onProgress(int i) {
							// TODO Auto-generated method stub
							dialog.updateProgress(i);
						}
					});

			return;

		}

		isFirstLoading = true;
		dialog = new LoadingDialog(LoadingDialog.STYLE_PROGRESS, this, true);
		dialog.updateProgress(1);

		copyFromAsstesLevel();
		copyFromAsstesList();

		dialog.updateProgress(1);
		UnZipUtil unZipLevel = new UnZipUtil(getFilesDir()
				+ "/data/level_package_1.zip", getFilesDir() + "/data/level/",
				"", new UnZipUtil.OnZipListener() {

					@Override
					public void onZipProgress(int progress) {
						// TODO Auto-generated method stub

						dialog.updateProgress((int) (1 + progress * 0.59));
					}

					@Override
					public void onExtractEnded() {
						// TODO Auto-generated method stub
						LevelManager.loadLevelManager();
						// setUserStimulator();

						// LevelManager.randomizeOrder();
						File f = new File(getFilesDir()
								+ "/data/level_package_1.zip");
						f.delete();

						// dialog.updateProgress(36);
					}

					@Override
					public void onErrorOccured(Exception e) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onBeforeExtracting() {
						// TODO Auto-generated method stub

					}
				});

		UnZipUtil unZipUtil = new UnZipUtil(getFilesDir()
				+ "/data/listfinal.zip", getFilesDir() + "/data/list/", "",
				new UnZipUtil.OnZipListener() {

					@Override
					public void onZipProgress(int progress) {
						// TODO Auto-generated method stub

						dialog.updateProgress((int) (60 + progress * (0.2)));
					}

					@Override
					public void onExtractEnded() {
						// TODO Auto-generated method stub
						addShortcut();
						sharedPreferences.edit().putBoolean("unziped", true)
								.commit();

						loadIAP();

						LevelListManager.loadLevelListData();

						File f = new File(getFilesDir() + "/data/listfinal.zip");
						f.delete();

						int places[] = ImageManager
								.loadListImages(getResources());
						int last = (places.length == 1) ? LevelListManager.ListCount
								: places[1];
						ImageManager.loadListFromTo(0, last, getResources(),
								new ImageManager.LoadingProgressListener() {

									@Override
									public void onProgress(int i) {
										// TODO Auto-generated method stub
										dialog.updateProgress((int) (80 + (0.2) * i));
									}
								});

					}

					@Override
					public void onErrorOccured(Exception e) {

					}

					@Override
					public void onBeforeExtracting() {

					}
				});

	}

	public void initUtils() {

		dialog.updateProgress(1);
		Utils.setContext(getApplicationContext());
		LevelListData.initDeviceSizes();
		LevelListManager.baseDir = getFilesDir() + "/data/list/";
		LevelManager.baseDir = getFilesDir() + "/data/level/";
		Utils.setContext(this);

		SharedPreferences sharedPreferences = getSharedPreferences(
				KeyGen.sharedPrefsKey, MODE_PRIVATE);

		LevelData.setSharedPrefs(sharedPreferences);
		CoinManager.setSharedPrefs(sharedPreferences);
		Utils.sharedPreferences = sharedPreferences;

		Utils.checkFromBackup();

		// LevelManager.loadLevelManager();
		// LevelManager.randomizeOrder();

		// UserStimulator.setRunning(this);

		dialog.updateProgress(1);

	}

	public void initSizes() {
		int screenWidth = 0;
		int screenHeight = 0;
		if (Build.VERSION.SDK_INT >= 11) {
			Point size = new Point();
			try {
				// this.getWindowManager().getDefaultDisplay().getRealSize(size);

				this.getWindowManager().getDefaultDisplay().getSize(size);
				screenWidth = size.x;
				screenHeight = size.y;
			} catch (NoSuchMethodError e) {

				DisplayMetrics metrics = new DisplayMetrics();
				this.getWindowManager().getDefaultDisplay()
						.getRealMetrics(metrics);
				screenWidth = metrics.widthPixels;
				screenHeight = metrics.heightPixels;

			}

		} else {
			DisplayMetrics metrics = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			screenWidth = metrics.widthPixels;
			screenHeight = metrics.heightPixels;
		}
		SizeManager.setScreenHeight(screenHeight);
		SizeManager.setScreenWidth(screenWidth);

	}

	public void setUserStimulator() {
		Intent intent = new Intent(this, UserStimulator.class);
		intent.setAction(UserStimulator.ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, PendingIntent.FLAG_NO_CREATE);
		if (pendingIntent == null) {
			pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
			AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			alarmManager.setRepeating(AlarmManager.RTC,
					System.currentTimeMillis(), 1000 * 60 * 20, pendingIntent);

		}

	}

	public void loadIAP() {

		// ImageManager.scaledBitmapFromResource(getResources(),
		// R.drawable.levelindicator, 384, 372,
		// ImageManager.CACHE_IMPORTANT);

		SizeConverter creditConverter = SizeConverter.SizeConvertorFromWidth(
				SizeManager.getScreenWidth(), 1080, 1500);

		ImageManager
				.scaledBitmapFromResource(getResources(), R.drawable.credits,
						creditConverter.mHeight, creditConverter.mWidth,
						ImageManager.CACHE_LIST, Config.RGB_565);

		SizeConverter mConverter = SizeConverter.SizeConvertorFormHeight(
				SizeManager.getScreenHeight(), 1200, 1920);

		int mWidth = (SizeManager.getScreenWidth() > mConverter.mWidth) ? mConverter.mWidth
				: SizeManager.getScreenWidth();

		SizeConverter imageConverter = SizeConverter.SizeConvertorFromWidth(
				mWidth, 600, 1061);

		ImageManager.scaledBitmapFromResource(getResources(), R.drawable.iap1,
				imageConverter.mHeight, mWidth, ImageManager.CACHE_IMPORTANT);
		ImageManager.scaledBitmapFromResource(getResources(), R.drawable.iap2,
				imageConverter.mHeight, mWidth, ImageManager.CACHE_IMPORTANT);
		ImageManager.scaledBitmapFromResource(getResources(), R.drawable.iap3,
				imageConverter.mHeight, mWidth, ImageManager.CACHE_IMPORTANT);
	}

	public void loadCheat() {
		SizeConverter cheatConverter = SizeConverter.SizeConvertorFormHeight(
				SizeManager.getScreenHeight() * (0.17), 817, 344);

		ImageManager.scaledBitmapFromResource(getResources(),
				R.drawable.cheat1, cheatConverter.mHeight,
				cheatConverter.mWidth, ImageManager.CACHE_IMPORTANT);
		ImageManager.scaledBitmapFromResource(getResources(),
				R.drawable.cheat2, cheatConverter.mHeight,
				cheatConverter.mWidth, ImageManager.CACHE_IMPORTANT);
		ImageManager.scaledBitmapFromResource(getResources(),
				R.drawable.cheat3, cheatConverter.mHeight,
				cheatConverter.mWidth, ImageManager.CACHE_IMPORTANT);

		ImageManager.scaledBitmapFromResource(getResources(),
				R.drawable.cheat10, cheatConverter.mHeight,
				cheatConverter.mWidth, ImageManager.CACHE_IMPORTANT);
		ImageManager.scaledBitmapFromResource(getResources(),
				R.drawable.cheat20, cheatConverter.mHeight,
				cheatConverter.mWidth, ImageManager.CACHE_IMPORTANT);
		ImageManager.scaledBitmapFromResource(getResources(),
				R.drawable.cheat30, cheatConverter.mHeight,
				cheatConverter.mWidth, ImageManager.CACHE_IMPORTANT);

	}

	private void addShortcut() {
		// Adding shortcut for MainActivity
		// on Home screen
		Intent shortcutIntent = new Intent(getApplicationContext(),
				LoadingActivity.class);

		shortcutIntent.setAction(Intent.ACTION_MAIN);

		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "لامپ");
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(
						getApplicationContext(), R.drawable.icon));

		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		getApplicationContext().sendBroadcast(addIntent);
	}

}
