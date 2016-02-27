package com.irpulse.lamp;

import java.util.Random;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.irpulse.Utilities.CheckingUtility;
import com.irpulse.Utilities.KeyGen;
import com.irpulse.Utilities.SizeConverter;
import com.irpulse.Utilities.SizeManager;
import com.irpulse.Utilities.Utils;
import com.qwerjk.better_text.MagicTextView;

import com.irpulse.lamp.R;
import com.irpulse.lamp.MyApplication.TrackerName;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint.Join;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class IAPDialog extends DialogFragment {

	static final String SKU_VERY_SMALL_COIN = "very_small_coin";
	static final String SKU_SMALL_COIN = "small_coin";
	static final String SKU_MEDIUM_COIN = "medium_coin";
	static final String SKU_BIG_COIN = "big_coin";

	Bitmap[] pictures = new Bitmap[3];
	ImageView viewHolder;
	MagicTextView[] iapTexts;
	boolean forList;
	boolean isDissmisd = false;
	boolean isVisable = false;
	String forLevel = "";
	String[] textes;

	public IAPDialog(boolean forList) {
		super();
		this.forList = forList;

	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		if (!forList)
			if (getActivity() != null)
				if (((LevelActivity) getActivity()).lifesFragment != null)
					if (((LevelActivity) getActivity()).dialog.getDialog() == null)
						((LevelActivity) getActivity()).lifesFragment
								.setVisible();
		if (getActivity() != null)
			if (((BaseActivity) getActivity()).loadingImageView != null)
				((BaseActivity) getActivity()).loadingImageView
						.setVisibility(View.GONE);
		super.onDismiss(dialog);
		isDissmisd = true;
		isVisable = false;
	}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		try {
			Tracker t = ((MyApplication) getActivity().getApplication())
					.getTracker(TrackerName.APP_TRACKER);

			// Set screen name.
			t.setScreenName("IAPDialog "
					+ ((forList) ? "in list"
							: ((LevelActivity) getActivity()).level.answer));

			// Send a screen view.
			t.send(new HitBuilders.ScreenViewBuilder().build());
		} catch (Exception e) {

		}
		isDissmisd = false;

		Dialog mDialog = new Dialog(getActivity(), R.style.Theme_Dialog);
		mDialog.setTitle(null);
		mDialog.setCanceledOnTouchOutside(true);

		mDialog.setContentView(R.layout.dialog_iap);
		Context context = getActivity();

		int mWidth, mHeight;

		SizeConverter mConverter = SizeConverter.SizeConvertorFormHeight(
				SizeManager.getScreenHeight(), 1200, 1920);
		SizeConverter imageConverter;// = SizeConverter.SizeConvertorFormHeight(
		// SizeManager.getScreenHeight(), 480, 853);

		int leftOffset = 0;
		int topOffset = 0;// = imageConverter.getOffset() / 2, topOffset = 0;
		if (!forList) {
			leftOffset = (mConverter.mWidth < SizeManager.getScreenWidth()) ? mConverter
					.getOffset() / 2 : 0; // : imageConverter.getOffset() / 2;
			mWidth = (mConverter.mWidth < SizeManager.getScreenWidth()) ? mConverter.mWidth
					: SizeManager.getScreenWidth();

		} else
			mWidth = SizeManager.getScreenWidth();
		// if (imageConverter.mWidth < SizeManager.getScreenWidth()) {
		// imageConverter = SizeConverter.SizeConvertorFromWidth(
		// SizeManager.getScreenWidth(), 480, 853);
		// topOffset = imageConverter.getOffset() / 2;
		// leftOffset = 0;
		// }

		imageConverter = SizeConverter.SizeConverterFromLessOffset(mWidth,
				SizeManager.getScreenHeight(), 480, 853);
		leftOffset = imageConverter.getLeftOffset() / 2;
		topOffset = imageConverter.getTopOffset();
		mWidth = imageConverter.mWidth;

		mHeight = imageConverter.mHeight;

		FrameLayout v = (FrameLayout) mDialog
				.findViewById(R.id.dialog_iap_main_layout);
		v.setLayoutParams(new FrameLayout.LayoutParams(SizeManager
				.getScreenWidth(), SizeManager.getScreenHeight()));
		RelativeLayout container = new RelativeLayout(context);
		container.setLayoutParams(v.getLayoutParams());

		v.addView(container);

		RelativeLayout mainView = new RelativeLayout(context);

		setUpCoinsLayout(context, mainView);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				mWidth, mHeight);
		params.leftMargin = leftOffset;
		// params.rightMargin = SizeManager.getScreenWidth() ;
		params.topMargin = topOffset;

		viewHolder = new ImageView(context);
		viewHolder.setScaleType(ScaleType.FIT_XY);

		pictures[0] = ImageManager.scaledBitmapFromResource(getResources(),
				R.drawable.iap1, mHeight, mWidth, ImageManager.CACHE_IMPORTANT);
		pictures[1] = ImageManager.scaledBitmapFromResource(getResources(),
				R.drawable.iap2, mHeight, mWidth, ImageManager.CACHE_IMPORTANT);
		pictures[2] = ImageManager.scaledBitmapFromResource(getResources(),
				R.drawable.iap3, mHeight, mWidth, ImageManager.CACHE_IMPORTANT);
		Random random = new Random();
		viewHolder.setImageBitmap(pictures[random.nextInt(3)]);

		container.addView(viewHolder, params);

		new Handler().post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (getDialog() == null || isDissmisd)
					return;
				Random random = new Random();
				viewHolder.setImageBitmap(pictures[random.nextInt(3)]);
				new Handler().postDelayed(this, 1300);
			}

		});

		textes = new String[] { "1000      950 تومان", "3000      1950 تومان",
				"9000      4950 تومان", "20000     9950 تومان",
				"200     نظر در بازار", "200     فالو اینستا" };

		SharedPreferences sharedPreferences = Utils.sharedPreferences;

		if (sharedPreferences.getBoolean("Liked_Bazar", false)) {
			textes[4] = " فعلا موجود نیست";
		}

		if (sharedPreferences.getBoolean("Liked_Insta", false)) {
			textes[5] = " فعلا موجود نیست";
		}

		iapTexts = new MagicTextView[6];

		for (int i = 0; i < 6; i++) {
			iapTexts[i] = new MagicTextView(context);// (MagicTextView)
														// mDialog.findViewById(resIDs[i]);
			iapTexts[i].setTextColor(Color.WHITE);
			iapTexts[i].setTextSize(TypedValue.COMPLEX_UNIT_PX,
					11.f * (mConverter.mWidth / 210.f));
			iapTexts[i].setTypeface(Utils.getIAPFont());
			// iapTexts[i].setStroke(0.7f * (mConverter.mWidth / 210.f),
			// Color.parseColor("#728e5b") , Join.MITER , 10);

			Animation animation = new AlphaAnimation(1.f, 0.75f);
			animation.setFillAfter(true);
			animation.setDuration(1);

			iapTexts[i].setSingleLine(true);
			iapTexts[i].setGravity(Gravity.CENTER);
			iapTexts[i].setOnClickListener(new IAPClickListener(i));

			iapTexts[i].startAnimation(animation);
		}

		// coinText.setTextColor(Color.WHITE);
		// coinText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
		// 48f * (mConverter.mWidth / 210.f));
		// coinText.setTypeface(Utils.getCoinFont());
		// coinText.setStroke(1.f * (mConverter.mWidth / 210.f),
		// Color.parseColor("#0d5256"));
		// coinText.setSingleLine(true);
		//
		// coinText.setGravity(Gravity.CENTER);

		int textWidth = imageConverter.convertWidth(173);
		int textHeight = imageConverter.convertHeight(50);
		LayoutParams textViewParams = new LayoutParams(textWidth, textHeight);

		textViewParams.leftMargin = imageConverter.convertWidth(141)
				+ leftOffset;
		textViewParams.topMargin = imageConverter.convertHeight(274)
				+ topOffset;// - imageConverter.convertHeight(5);

		iapTexts[0].setLayoutParams(textViewParams);

		textViewParams = new LayoutParams(textWidth, textHeight);

		textViewParams.leftMargin = imageConverter.convertWidth(127)
				+ leftOffset;
		textViewParams.topMargin = imageConverter.convertHeight(330)
				+ topOffset;// - 2 * imageConverter.convertHeight(5);

		iapTexts[1].setLayoutParams(textViewParams);

		textViewParams = new LayoutParams(textWidth, textHeight);

		textViewParams.leftMargin = imageConverter.convertWidth(156)
				+ leftOffset;
		textViewParams.topMargin = imageConverter.convertHeight(384)
				+ topOffset;// - imageConverter.convertHeight(5);

		iapTexts[2].setLayoutParams(textViewParams);
		textViewParams = new LayoutParams(textWidth, textHeight);

		textViewParams.leftMargin = imageConverter.convertWidth(119)
				+ leftOffset;
		textViewParams.topMargin = imageConverter.convertHeight(439)
				+ topOffset;// - imageConverter.convertHeight(5);

		iapTexts[3].setLayoutParams(textViewParams);

		textViewParams = new LayoutParams(textWidth, textHeight);
		textViewParams.leftMargin = imageConverter.convertWidth(136)
				+ leftOffset;
		textViewParams.topMargin = imageConverter.convertHeight(496)
				+ topOffset;// - imageConverter.convertHeight(5);

		iapTexts[4].setLayoutParams(textViewParams);

		textViewParams = new LayoutParams(textWidth, textHeight);
		textViewParams.leftMargin = imageConverter.convertWidth(127)
				+ leftOffset;
		textViewParams.topMargin = imageConverter.convertHeight(553)
				+ topOffset;// - imageConverter.convertHeight(5);

		iapTexts[5].setLayoutParams(textViewParams);

		for (int i = 0; i < 6; i++) {
			final int index = i;
			iapTexts[index].setText(getIAPText(index));
			container.addView(iapTexts[i]);
		}

		container.addView(mainView);

		return mDialog;
	}

	public class IAPClickListener implements View.OnClickListener {

		int i;

		public IAPClickListener(int i) {
			this.i = i;
		}

		@Override
		public void onClick(View v) {

			switch (i) {
			case 0:

				dismiss();
				buySKU(SKU_VERY_SMALL_COIN);
				break;
			case 1:

				dismiss();
				buySKU(SKU_SMALL_COIN);
				break;
			case 2:

				dismiss();
				buySKU(SKU_MEDIUM_COIN);
				break;
			case 3:

				dismiss();
				buySKU(SKU_BIG_COIN);
				break;

			case 4:

				SharedPreferences sharedPreferences = Utils.sharedPreferences;

				if (sharedPreferences.getBoolean("Liked_Bazar", false))
					return;
				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setData(Uri
						.parse("bazaar://details?id=com.irpulse.lamp"));

				try {
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {

							Toast.makeText(getActivity(),
									"لطفا بازار را نصب کنید",
									Toast.LENGTH_SHORT).show();
						}
					});
					return;
				}

				dismiss();

				sharedPreferences.edit().putBoolean("Liked_Bazar", true)
						.commit();

				CoinManager.earnCoin(200);

				Utils.setBackup();
				break;

			case 5:
				sharedPreferences = Utils.sharedPreferences;

				if (sharedPreferences.getBoolean("Liked_Insta", false)) {

					CoinManager.earnCoin(2000);
					return;

				}
				dismiss();
				Uri uri = Uri.parse("http://instagram.com/_u/irpulse");
				Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

				likeIng.setPackage("com.instagram.android");

				try {
					startActivity(likeIng);
				} catch (ActivityNotFoundException e) {
					startActivity(new Intent(Intent.ACTION_VIEW,
							Uri.parse("http://instagram.com/irpulse")));
				}
				sharedPreferences.edit().putBoolean("Liked_Insta", true)
						.commit();

				CoinManager.earnCoin(100);

				Utils.setBackup();
				break;

			default:
				break;
			}

		}
	}

	public void buySKU(String sku) {

		((BaseActivity) getActivity()).buySomeSKU(sku);

	}

	// @Override
	public void show(FragmentManager manager, String tag, BaseActivity activity) {
		// TODO Auto-generated method stub

		super.show(manager, tag);

		// (activity).loadingImageView.setVisibility(View.GONE);

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		isVisable = true;
		((BaseActivity) getActivity()).loadingImageView
				.setVisibility(View.GONE);

	}

	private String sharedPrefsKey = KeyGen.base + ".Versions";

	public String getIAPText(int index) {

		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences(KeyGen.sharedPrefsKey,
						Context.MODE_PRIVATE);

		boolean isDefualt = sharedPreferences.getBoolean("PriceStateIsDefualt",
				true);
		if (isDefualt)
			return textes[index];

		return sharedPreferences.getString(index + "price", textes[index]);

	}

	public interface OnDoneClickListener {
		void onDoneClicked();
	}

	public OnDoneClickListener mCallback;

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		if (getDialog() != null)
			dismiss();
		super.onPause();
	}

	public void onResume() {

		super.onResume();
		coinText.setText(CoinManager.getCoin() + "");

	};

	RelativeLayout coinBox;
	MagicTextView coinText;

	void setUpCoinsLayout(Context context, RelativeLayout mainView) {

		android.widget.RelativeLayout.LayoutParams mainViewLayoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mainViewLayoutParams.topMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 10, getResources()
						.getDisplayMetrics());

		mainView.setLayoutParams(mainViewLayoutParams);
		SizeConverter wholeConverter = SizeConverter.SizeConvertorFormHeight(
				SizeManager.getScreenHeight(), 1200, 1920);

		int mWidth, leftTemp = 0;
		if (wholeConverter.getWidth() < SizeManager.getScreenWidth()) {
			mWidth = wholeConverter.mWidth;
			leftTemp = wholeConverter.getOffset() / 2;
		}

		else
			mWidth = SizeManager.getScreenWidth();

		coinText = new MagicTextView(context);
		SizeConverter mConverter = SizeConverter.SizeConvertorFromWidth(
				mWidth * (0.25f), 210, 98);

		android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				mConverter.mWidth, mConverter.mHeight);

		android.widget.RelativeLayout.LayoutParams paramsMain = new RelativeLayout.LayoutParams(
				mConverter.mWidth, mConverter.mHeight);
		paramsMain.leftMargin = mWidth + leftTemp - mConverter.mWidth;
		paramsMain.rightMargin = mWidth;

		coinBox = new RelativeLayout(context);
		coinBox.setLayoutParams(paramsMain);

		mainView.addView(coinBox);

		android.widget.RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
				mConverter.convertWidth(136), mConverter.mHeight);

		textParams.leftMargin = +mConverter.convertWidth(210 - 136);
		// textParams.rightMargin = mWidth + leftTemp;
		textParams.topMargin = (int) (-mConverter.mHeight / 15.);
		coinText.setTextColor(Color.WHITE);
		coinText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				48f * (mConverter.mWidth / 210.f));
		coinText.setTypeface(Utils.getCoinFont());
		coinText.setStroke(1.f * (mConverter.mWidth / 210.f),
				Color.parseColor("#0d5256"));
		coinText.setSingleLine(true);

		coinText.setGravity(Gravity.CENTER);
		// coinText.set

		ImageView v = new ImageView(context);
		v.setImageBitmap(ImageManager.scaledBitmapFromResource(getResources(),
				R.drawable.jacoin, mConverter.mHeight, mConverter.mWidth,
				ImageManager.CACHE_IMPORTANT));
		v.setLayoutParams(params);
		coinBox.addView(v);
		coinBox.addView(coinText, textParams);
		if (!forList)
			if (((LevelActivity) getActivity()).dialog.getDialog() == null) {
				ScaleAnimation animation = new ScaleAnimation(0.8f, 1f, 0.8f,
						1f, Animation.RELATIVE_TO_SELF, 1f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				animation.setFillAfter(true);
				animation.setDuration(300);

				coinBox.startAnimation(animation);
			}

	}

}
