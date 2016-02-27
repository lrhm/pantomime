package com.irpulse.lamp;

import com.irpulse.Utilities.KeyGen;
import com.irpulse.Utilities.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class LifeKillingReciver extends BroadcastReceiver {

	
	static String lifeKillingAction = KeyGen.base + ".ACTION_LIFE_KILLING";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction() != lifeKillingAction)return;
		SharedPreferences sharedPreferences = context.getSharedPreferences(LifeSystem.sharedPrefsKey, context.MODE_PRIVATE);
		Bundle bundle = intent.getExtras();
		int lifeId = bundle.getInt("lifeID");
		Life life = LifeSystem.getLife(lifeId);
		life.setActive();
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(LifeSystem.deActive+lifeId, -1);
		editor.commit();
		
		Utils.backupLifes();
		
	}

}
