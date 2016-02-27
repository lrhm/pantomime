package com.irpulse.lamp;

import com.irpulse.lamp.R;

import com.irpulse.Utilities.SizeConverter;
import com.irpulse.Utilities.SizeManager;
import com.irpulse.Utilities.Utils;
import com.qwerjk.better_text.MagicTextView;

import android.R.anim;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ImageView;

public class LifesFragment extends Fragment {

	boolean forList;
	MagicTextView coinText;
	RelativeLayout coinBox;
	SizeConverter lampConverter;
	SizeConverter keyConverter;
	ImageView[] lamps;
	ImageView key;
	boolean keyState;
	int leftTemp = 0;
	int mWidth;
	long lastTimeVisible;
	IAPDialog mDialog;

	public LifesFragment(boolean forList) {
		this.forList = forList;
		keyState = forList;
	}

	public LifesFragment() {
		forList = true;
	}

	public void setInvisble() {
		this.coinBox.setVisibility(View.GONE);
	}

	public void setVisible() {
		this.coinBox.setVisibility(View.VISIBLE);
		ScaleAnimation animation = new ScaleAnimation(1, 0.8f, 1, 0.8f,
				Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setFillAfter(true);
		animation.setDuration(300);

		coinBox.startAnimation(animation);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		SizeConverter wholeConverter = SizeConverter.SizeConvertorFormHeight(
				SizeManager.getScreenHeight(), 1200, 1920);

		mDialog = new IAPDialog(forList);
		if (forList)
			mWidth = SizeManager.getScreenWidth();
		else if (wholeConverter.getWidth() < SizeManager.getScreenWidth()) {
			mWidth = wholeConverter.mWidth;
			leftTemp = wholeConverter.getOffset() / 2;
		}

		else
			mWidth = SizeManager.getScreenWidth();

		lampConverter = SizeConverter.SizeConvertorFromWidth(mWidth * (0.09f),
				123, 150);

		keyConverter = SizeConverter.SizeConvertorFromWidth(mWidth * (0.11f),
				132, 136);

		RelativeLayout mainView = (RelativeLayout) inflater.inflate(
				R.layout.coins_layout, container, false);

		// LayoutParams param = new LayoutParams(SizeManager.getScreenWidth(),
		// LayoutParams.WRAP_CONTENT);
		// mainView.setLayoutParams(params);
		//
		setUpCoinsLayout(getActivity(), mainView);
		// setUpLamp(mainView, getActivity());
		// setUpKey(mainView, getActivity());

		return mainView;
	}

	public void setUpLamp(RelativeLayout mainView, Context context) {
		lamps = new ImageView[5];
		int leftMargin = keyConverter.mWidth + keyConverter.mWidth / 2
				+ leftTemp;
		for (int i = 0; i < 5; i++) {
			lamps[i] = new ImageView(context);
			int margin = leftMargin + i * lampConverter.mWidth;
			LayoutParams params = new LayoutParams(lampConverter.mWidth,
					lampConverter.mHeight);
			params.leftMargin = margin;
			params.topMargin = 0;
			mainView.addView(lamps[i], params);
		}
		checkLifes();
		// if (!forList)
		animatedMovingLamps();
	}

	public void setUpKey(RelativeLayout mainView, Context context) {
		key = new ImageView(context);
		LayoutParams params = new LayoutParams(keyConverter.mWidth,
				keyConverter.mHeight);
		params.leftMargin = keyConverter.mWidth / 5 + leftTemp;

		key.setImageBitmap(ImageManager.scaledBitmapFromResource(
				getResources(), R.drawable.buttonon, keyConverter.mHeight,
				keyConverter.mWidth, ImageManager.CACHE_IMPORTANT));
		mainView.addView(key, params);

		key.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				keyState = !keyState;
				animatedMovingLamps();
				key.setImageBitmap(ImageManager.scaledBitmapFromResource(
						getResources(), (keyState) ? R.drawable.buttonon
								: R.drawable.buttonof, keyConverter.mHeight,
						keyConverter.mWidth, ImageManager.CACHE_IMPORTANT));
				if (!forList)
					if (keyState) {
						lastTimeVisible = System.currentTimeMillis();
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (!LifesFragment.this.isVisible())
									return;
								if (System.currentTimeMillis()
										- lastTimeVisible >= 5000) {
									if (keyState) {
										keyState = false;

										animatedMovingLamps();
										key.setImageBitmap(ImageManager
												.scaledBitmapFromResource(
														getResources(),
														(keyState) ? R.drawable.buttonon
																: R.drawable.buttonof,
														keyConverter.mHeight,
														keyConverter.mWidth,
														ImageManager.CACHE_IMPORTANT));
									}
								}
							}
						}, 5000);
					}
			}
		});
	}

	public void triggerForLongerTime() {
		if (!keyState) {
			keyState = true;
			key.setImageBitmap(ImageManager.scaledBitmapFromResource(
					getResources(), (keyState) ? R.drawable.buttonon
							: R.drawable.buttonof, keyConverter.mHeight,
					keyConverter.mWidth, ImageManager.CACHE_IMPORTANT));
			animatedMovingLamps();
			if (!forList)
				if (keyState) {
					lastTimeVisible = System.currentTimeMillis();
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (!LifesFragment.this.isVisible())
								return;
							if (System.currentTimeMillis() - lastTimeVisible >= 5000) {
								if (keyState) {
									keyState = false;

									animatedMovingLamps();
									key.setImageBitmap(ImageManager
											.scaledBitmapFromResource(
													getResources(),
													(keyState) ? R.drawable.buttonon
															: R.drawable.buttonof,
													keyConverter.mHeight,
													keyConverter.mWidth,
													ImageManager.CACHE_IMPORTANT));
								}
							}
						}
					}, 5000);
				}

		} else {
			lastTimeVisible = System.currentTimeMillis();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (!LifesFragment.this.isVisible())
						return;
					if (System.currentTimeMillis() - lastTimeVisible >= 5000) {
						if (keyState) {
							keyState = false;

							animatedMovingLamps();
							key.setImageBitmap(ImageManager
									.scaledBitmapFromResource(getResources(),
											(keyState) ? R.drawable.buttonon
													: R.drawable.buttonof,
											keyConverter.mHeight,
											keyConverter.mWidth,
											ImageManager.CACHE_IMPORTANT));
						}
					}
				}
			}, 5000);
		}
	}

	public void animatedMovingLamps() {
		if (Build.VERSION.SDK_INT < 11) {
			animationAPIBelow();
		} else {
			animation();

		}
	}

	public void animationAPIBelow() {
		int amount = lampConverter.mHeight;
		if (!keyState) {
			amount *= -1;
		} else
			amount = 0;
		for (ImageView lamp : lamps) {
			com.nineoldandroids.animation.ObjectAnimator transitionForFirst = com.nineoldandroids.animation.ObjectAnimator
					.ofFloat(lamp, "translationY", amount);
			transitionForFirst.setDuration(300);
			transitionForFirst.start();

		}
	}

	public void animation() {
		int amount = lampConverter.mHeight;
		if (!keyState) {
			amount *= -1;
		} else
			amount = 0;
		for (ImageView lamp : lamps) {
			ObjectAnimator transitionForFirst = ObjectAnimator.ofFloat(lamp,
					"translationY", amount);
			transitionForFirst.setDuration(300);
			transitionForFirst.start();

		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		// if (!forList) {
		// checkLifes();
		// keyState = false;
		// animatedMovingLamps();
		//
		// key.setImageBitmap(ImageManager.scaledBitmapFromResource(
		// getResources(), (keyState) ? R.drawable.buttonon
		// : R.drawable.buttonof, keyConverter.mHeight,
		// keyConverter.mWidth , ImageManager.CACHE_IMPORTANT));
		// }
		super.onResume();

		setText(CoinManager.getCoin() + "");

	}

	public void checkLifes() {

		// if (isDetached())
		// return;
		// // LifeSystem.loadFromSharedPrefs();
		//
		// for (int i = 0; i < 5; i++) {
		// lamps[i].setImageBitmap(ImageManager.scaledBitmapFromResource(
		// getResources(),
		// (LifeSystem.getLife(i).isAlive) ? R.drawable.lifelamp
		// : R.drawable.lifelampoff, lampConverter.mHeight,
		// lampConverter.mWidth , ImageManager.CACHE_IMPORTANT));
		//
		// }
	}

	void setText(String text) {
		coinText.setText(text);
	}

	void setUpCoinsLayout(Context context, View mainView) {

		coinText = new MagicTextView(context);
		SizeConverter mConverter = SizeConverter.SizeConvertorFromWidth(
				mWidth * (0.25f), 210, 98);

		android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				mConverter.mWidth, mConverter.mHeight);

		android.widget.RelativeLayout.LayoutParams paramsMain = new RelativeLayout.LayoutParams(
				mConverter.mWidth, mConverter.mHeight);
		paramsMain.leftMargin = mWidth + leftTemp - mConverter.mWidth;
		paramsMain.rightMargin = mWidth;

		coinBox = (RelativeLayout) mainView.findViewById(R.id.coins_box);
		coinBox.setLayoutParams(paramsMain);

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

		ImageView v = ((ImageView) mainView.findViewById(R.id.coins_image));
		v.setImageBitmap(ImageManager.scaledBitmapFromResource(getResources(),
				(forList) ? R.drawable.jacoinlist : R.drawable.jacoin,
				mConverter.mHeight, mConverter.mWidth,
				ImageManager.CACHE_IMPORTANT));
		v.setLayoutParams(params);
		coinBox.addView(coinText, textParams);

		coinBox.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mDialog.getDialog() != null) {
					return;
				}
				mDialog = new IAPDialog(forList);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						if (mDialog.isVisable == false)
							((BaseActivity) getActivity()).loadingImageView
									.setVisibility(View.VISIBLE);

					}
				}, 50);
				mDialog.show(getFragmentManager(), null);
				if (!forList)
					setInvisble();
				// mDialog.updateText(getActivity());
				// mDialog.updateTexts();
			}
		});

		if (!forList) {
			ScaleAnimation animation = new ScaleAnimation(1, 0.8f, 1, 0.8f,
					Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF,
					0.5f);
			animation.setFillAfter(true);
			animation.setDuration(300);

			coinBox.startAnimation(animation);
		}
	}
}
