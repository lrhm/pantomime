package com.irpulse.lamp;

import java.util.HashMap;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.irpulse.lamp.R;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.sender.HttpSender;

import android.app.Application;
import android.util.Log;

@ReportsCrashes(formUri = "https://pulseco.cloudant.com/acra-lamp/_design/acra-storage/_update/report", reportType = HttpSender.Type.JSON, socketTimeout = 30000, httpMethod = HttpSender.Method.POST, formUriBasicAuthLogin = "fspeadengetresingentimis", formUriBasicAuthPassword = "onAkymXsRmXke1bci2Ptf7C2", formKey = "", // This
// is
// required
// for
// backward
// compatibility
// but
// not
// used
customReportContent = { ReportField.APP_VERSION_CODE,
		ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION,
		ReportField.PACKAGE_NAME, ReportField.REPORT_ID, ReportField.BUILD,
		ReportField.STACK_TRACE }, mode = ReportingInteractionMode.TOAST, resToastText = R.string.toast_crash)
public class MyApplication extends Application {

	/**
	 * Enum used to identify the tracker that needs to be used for tracking.
	 * 
	 * A single tracker is usually enough for most purposes. In case you do need
	 * multiple trackers, storing them all in Application object helps ensure
	 * that they are created only once per application instance.
	 */
	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg:
						// roll-up tracking.
		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a
							// company.
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	synchronized Tracker getTracker(TrackerName trackerId) {
		
		Log.d("Lamp" , "Get Tracker " + trackerId.toString());
		if (!mTrackers.containsKey(trackerId)) {
			String PROPERTY_ID = "UA-61508305-1";

			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics
					.newTracker(PROPERTY_ID)
					: (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics
							.newTracker(R.xml.global_tracker) : null;
			if (t != null)
				mTrackers.put(trackerId, t);

		}
		return mTrackers.get(trackerId);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

		super.onCreate();
		ACRA.init(this);
	}

	// @Override
	// public void onTrimMemory(int level) {
	// Log.d("trim" , "on trim");
	// super.onTrimMemory(level);
	//
	// }

}
