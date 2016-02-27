package com.irpulse.lamp;

import com.irpulse.utils.IabHelper;
import com.irpulse.utils.IabResult;
import com.irpulse.utils.Inventory;
import com.irpulse.utils.Purchase;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class BaseActivity extends FragmentActivity {

	static final String SKU_VERY_SMALL_COIN = "very_small_coin";
	static final String SKU_SMALL_COIN = "small_coin";
	static final String SKU_MEDIUM_COIN = "medium_coin";
	static final String SKU_BIG_COIN = "big_coin";
	boolean buyingReady = false;
	LifesFragment lifesFragment;

	static final int RC_REQUEST = 10001;

	IabHelper mHelper;
	ImageView loadingImageView;

	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		@Override
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			// Log.d(TAG, "Purchase finished: " + result + ", purchase: " +
			// purchase);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			if (result.isFailure()) {
				// Log.e(TAG, "Error purchasing: " + result);
				// findViewById(R.id.loadingPanel).setVisibility(View.GONE);
				return;
			}

			// Log.d(TAG, "Purchase successful.");

			if (purchase.getSku().equals(SKU_SMALL_COIN)
					|| purchase.getSku().equals(SKU_VERY_SMALL_COIN)
					|| purchase.getSku().equals(SKU_MEDIUM_COIN)
					|| purchase.getSku().equals(SKU_BIG_COIN)) {
				try {
					mHelper.consumeAsync(purchase, mConsumeFinishedListener);
				} catch (IllegalStateException e) {
					Toast.makeText(
							BaseActivity.this,
							"لطفا با یک اتصال اینترنت خوب دوباره اجرا کنید تا سکه‌ی خریداری شده را دریافت کنید.",
							Toast.LENGTH_LONG).show();
				}
				return;
			}

			// findViewById(R.id.loadingPanel).setVisibility(View.GONE);
		}
	};

	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		@Override
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			// Log.d(TAG, "Consumption finished. Purchase: " + purchase +
			// ", result: " + result);
			if (mHelper == null)
				return;
			if (result.isSuccess()) {
				if (purchase.getSku().equals(SKU_VERY_SMALL_COIN))
					CoinManager.earnCoin(1000);
				if (purchase.getSku().equals(SKU_SMALL_COIN))
					CoinManager.earnCoin(3000);
				if (purchase.getSku().equals(SKU_MEDIUM_COIN))
					CoinManager.earnCoin(9000);
				if (purchase.getSku().equals(SKU_BIG_COIN))
					CoinManager.earnCoin(20000);

				lifesFragment.setText(CoinManager.getCoin() + "");

				Toast.makeText(getApplicationContext(), "!مرسی", Toast.LENGTH_SHORT).show();
				
				// MediaPlayer.create(BaseActivity.this, R.raw.sound_purchase)
				// .start();

				// if (findViewById(R.id.coinsCount) != null)
				// CoinManager.updateCoinView(
				// (TextView) findViewById(R.id.coinsCount),
				// preferences);
				// if (findViewById(R.id.loadingPanel) != null)
				// findViewById(R.id.loadingPanel).setVisibility(
				// View.INVISIBLE);
			}
		}
	};

	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		@Override
		public void onQueryInventoryFinished(IabResult result,
				Inventory inventory) {
			buyingReady = true;
			// Log.d(TAG, "ASD inventory finished;");

			if (mHelper == null)
				return;
			// Is it a failure?
			if (result.isFailure()) {
				// complain("Failed to query inventory: " + result);
				return;
			}
			// Log.d(TAG, "Query inventory was successful.");
			try {
				Purchase verySmallCoinPurchase = inventory
						.getPurchase(SKU_VERY_SMALL_COIN);
				if (verySmallCoinPurchase != null)
					mHelper.consumeAsync(verySmallCoinPurchase,
							mConsumeFinishedListener);

				Purchase smallCoinPurchase = inventory
						.getPurchase(SKU_SMALL_COIN);
				if (smallCoinPurchase != null)
					mHelper.consumeAsync(smallCoinPurchase,
							mConsumeFinishedListener);

				Purchase mediumCoinPurchase = inventory
						.getPurchase(SKU_MEDIUM_COIN);
				if (mediumCoinPurchase != null)
					mHelper.consumeAsync(mediumCoinPurchase,
							mConsumeFinishedListener);

				Purchase bigCoinPurchase = inventory.getPurchase(SKU_BIG_COIN);
				if (bigCoinPurchase != null)
					mHelper.consumeAsync(bigCoinPurchase,
							mConsumeFinishedListener);
			} catch (IllegalStateException e) {
				Toast.makeText(
						BaseActivity.this,
						"لطفا با یک اتصال اینترنت خوب دوباره اجرا کنید تا سکه‌ی خریداری شده را دریافت کنید.",
						Toast.LENGTH_LONG).show();
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Log.d("GOLVAZHE ", "onActivityResult(" + requestCode + "," +
		// resultCode
		// + "," + data);
		if (mHelper == null)
			return;
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			// Log.d(TAG, "onActivityResult handled by IABUtil.");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// TODO base64Key
		String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDGHRO7cq+i52gYcc8xNuMXIMTLAZj9bzCIq/V5GcDg2harv2xdAMIbL0H415B1RlRhSc8eFk8jWKbpd6v6yywyfvBCHol93iuL6egbABD4e5VvWROTpcfAEZMFenZtxj7CNWbcrO2KWGf28AA7EzGwJOBDnNY/ebAjbKigAMorzG1KXkl1tBjsl+JGp9EAVqnDdFGO/NHM1tffEPToMhf0rsUXlRDUlywODA7GW00CAwEAAQ==";
		
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		mHelper.enableDebugLogging(false);

		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(IabResult result) {
				// Log.d(TAG, "Setup finished.");

				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					// complain("Problem setting up in-app billing: " + result);
					return;
				}

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null)
					return;

				// IAB is fully set up. Now, let's get an inventory of stuff we
				// own.
				// Log.d(TAG, "Setup successful. Querying inventory.");
				try {
					mHelper.queryInventoryAsync(mGotInventoryListener);
				} catch (IllegalStateException e) {
					// Nothing to do here...
				}
			}
		});
	}

	void buySomeSKU(String sku) {
		try {
			mHelper.launchPurchaseFlow(BaseActivity.this, sku, RC_REQUEST,
					mPurchaseFinishedListener, "");
			// findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
			// findViewById(R.id.iabLayout).setVisibility(View.GONE);
		} catch (IllegalStateException e) {
			final IllegalStateException b = e;
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.d("Lamp" , "IllegalStateException e " + b.getMessage() + " " + b.getLocalizedMessage());
					Toast.makeText(BaseActivity.this, "لطفا بازار را نصب کنید",
							Toast.LENGTH_LONG).show();

				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Log.d(TAG, "Destroying helper.");
		if (mHelper != null) {
			mHelper.dispose();
			mHelper = null;
		}
	}

}
