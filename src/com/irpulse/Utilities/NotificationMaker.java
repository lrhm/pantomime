package com.irpulse.Utilities;

import java.util.Random;

import org.json.JSONObject;

import com.irpulse.lamp.ImageManager;
import com.irpulse.lamp.LoadingActivity;

import com.irpulse.lamp.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class NotificationMaker {

	NotificationCompat.Builder mBuilder;
	NotificationManager mNotificationManager;
	int notifID;

	public NotificationMaker(Context context, JSONObject json) {

		mBuilder = new NotificationCompat.Builder(context);

		mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		makeNotification(context, json);

	}

	public NotificationMaker(Context context, String contentText, String title,
			int id) {
		mBuilder = new NotificationCompat.Builder(context);
		mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		normalNotification(context, contentText, title, id);

	}

	public void makeNotification(Context context, JSONObject json) {
		try {
			notifID = (json.has("id")) ? json.getInt("id") : new Random()
					.nextInt();
			normalNotification(context, json.getString("content"),
					json.getString("title"), notifID);

		} catch (Exception e) {
			Log.d("notif", e.toString());

		}
	}

	public void setProgress(int progress) {
		mBuilder.setProgress(100, progress, false);
		mNotificationManager.notify(notifID, mBuilder.build());
	}

	public void setContentTextAfterProgress(String text) {
		mBuilder.setContentText(text).setProgress(0, 0, false);
		mNotificationManager.notify(notifID, mBuilder.build());
	}

	public void normalNotification(Context context, String contentText,
			String title, int id) {
		Intent resultIntent = new Intent(context, LoadingActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		this.notifID = id;
		mBuilder.setLargeIcon(
				ImageManager.loadImageFromResource(context.getResources(),
						R.drawable.icon)).setSmallIcon(R.drawable.notif)
				.setContentText(contentText).setContentTitle(title)
				.setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true)
				.setContentIntent(pendingIntent);

//		Uri alarmSound = RingtoneManager
//				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//		mBuilder.setSound(alarmSound);
		Notification result = mBuilder.build();
		Log.d("notif", result.toString());
		mNotificationManager.notify(id, result);
	}
}
