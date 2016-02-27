package com.irpulse.lamp;

import com.irpulse.Utilities.KeyGen;
import com.irpulse.Utilities.Utils;

import android.content.SharedPreferences;

public class CoinManager {

	public interface CoinStateListener {
		public void onCoinValueChanged(int newValue);
	}

	static CoinStateListener coinStateListener;

	public static void setCointStatleListener(
			CoinStateListener coinStateListener) {
		CoinManager.coinStateListener = coinStateListener;
	}

	private static String COIN_KEY = KeyGen.base + "coin";
	private static SharedPreferences sharedPreferences;

	public static void setSharedPrefs(SharedPreferences sharedPrefs) {
		sharedPreferences = sharedPrefs;
	}

	public static int getCoin() {
		return Utils.decryptToInt(sharedPreferences.getString(COIN_KEY,
				Utils.enrcypt("200")));
	}
	
	public static String getEncryptedCoin(){
		return sharedPreferences.getString(COIN_KEY,
				Utils.enrcypt("200"));
	}

	public static void setEncryptedCoin(String coin){
		sharedPreferences.edit()
		.putString(COIN_KEY, coin).commit();	
	}
	
	public static void earnCoin(int amount) {
		int coin = (getCoin() + amount);
		setCoint(getCoin() + amount);
		if (coinStateListener != null)
			coinStateListener.onCoinValueChanged(coin);


		
	}

	public static void setCoint(int amount) {

		setEncryptedCoin(Utils.enrcypt(amount + ""));
		if (coinStateListener != null)
			coinStateListener.onCoinValueChanged(amount);

		Utils.setBackup();
		
	}

	// return false if there isnt enough coin
	public static boolean spendCoin(int amount) {
		int temp = getCoin() - amount;
		if (temp >= 0){
			setCoint(temp)
		;
			return true;
		}
		if (coinStateListener != null)
			coinStateListener.onCoinValueChanged(temp);
		
		return false;
	}
}
