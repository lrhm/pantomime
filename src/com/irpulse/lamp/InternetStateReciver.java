package com.irpulse.lamp;

import com.irpulse.Utilities.CheckingUtility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

public class InternetStateReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if(isOnline(context)){
			new Handler().post(new CheckingUtility(context));
		}
	}
	
	public boolean isOnline(Context context) {

	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    //should check null because in air plan mode it will be null
	    return (netInfo != null && netInfo.isConnected());

	}

}
