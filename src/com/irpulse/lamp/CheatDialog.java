package com.irpulse.lamp;

import java.util.Random;
import java.util.zip.Inflater;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.irpulse.Utilities.SizeConverter;
import com.irpulse.Utilities.SizeManager;
import com.irpulse.Utilities.Utils;
import com.qwerjk.better_text.MagicTextView;
import com.irpulse.lamp.R;
import com.irpulse.lamp.MyApplication.TrackerName;

import android.R.anim;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix.ScaleToFit;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class CheatDialog extends DialogFragment {

	public static final int CHEAT_ITEM_SKIP = 0;
	public static final int CHEAT_ITEM_SHOW = 2;
	public static final int CHEAT_ITEM_REMOVE = 4;

	public interface OnCheatItemClicked {
		public void clicked(int item);
	}

	public CheatDialog(Boolean isFree) {
		this.isFree = isFree;
	}

	OnCheatItemClicked onCheatItemClicked;

	boolean isFree = false;
	boolean isGoingToWOW = false;
	boolean isToggled = false;
	Bitmap[] onItems;
	Bitmap[] offItems;
	ImageView[] cheatHoldrs = new ImageView[3];
	ImageView[] notMovingItems = new ImageView[3];
	SizeConverter cheatConverter;
	MagicTextView coinText;
	RelativeLayout coinBox;
	IAPDialog mDialog;

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		try {
			Tracker t = ((MyApplication) getActivity().getApplication())
					.getTracker(TrackerName.APP_TRACKER);

			// Set screen name.
			t.setScreenName("CheatDialog "

			+ ((LevelActivity) getActivity()).level.answer);

			// Send a screen view.
			t.send(new HitBuilders.ScreenViewBuilder().build());
		} catch (Exception e) {

		}

		onItems = new Bitmap[3];
		offItems = new Bitmap[3];

		mDialog = new IAPDialog(false);

		cheatConverter = SizeConverter.SizeConvertorFormHeight(
				SizeManager.getScreenHeight() * (0.17), 817, 344);

		int[] drawablesOn = { R.drawable.cheat1, R.drawable.cheat2,
				R.drawable.cheat3 };
		int[] drawablesOnFree = { R.drawable.cheat1free, R.drawable.cheat2free,
				R.drawable.cheat3free };

		int[] drawablesOff = { R.drawable.cheat10, R.drawable.cheat20,
				R.drawable.cheat30 };

		int[] drawablesOffFree = { R.drawable.cheat10free,
				R.drawable.cheat20free, R.drawable.cheat30free };

		int[] finalDrawablesOff = (isFree) ? drawablesOffFree : drawablesOff;
		int[] finalDrawablesOn = (isFree) ? drawablesOnFree : drawablesOn;

		for (int i = 0; i < 3; i++) {
			onItems[i] = ImageManager.scaledBitmapFromResource(getResources(),
					finalDrawablesOn[i], cheatConverter.mHeight,
					cheatConverter.mWidth, ImageManager.CACHE_IMPORTANT);
			offItems[i] = ImageManager.scaledBitmapFromResource(getResources(),
					finalDrawablesOff[i], cheatConverter.mHeight,
					cheatConverter.mWidth, ImageManager.CACHE_IMPORTANT);

		}

		Dialog mDialog = new Dialog(getActivity(), R.style.Theme_Dialog);
		mDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (isGoingToWOW)
						return true;
					for (int i = 0; i < 3; i++) {
						cheatHoldrs[i].setVisibility(View.VISIBLE);
						notMovingItems[i].setVisibility(View.GONE);
					}
					isGoingToWOW = true;
					myDismiss();
					return true;
				}

				return false;
			}
		});
		mDialog.setTitle(null);
		// mDialog.setCanceledOnTouchOutside(true);

		View v = getActivity().getLayoutInflater().inflate(
				R.layout.dialog_cheat, null, false);
		int top = cheatConverter.mHeight / 2;

		cheatHoldrs[0] = (ImageView) v.findViewById(R.id.cheat_dialog_item1);
		cheatHoldrs[1] = (ImageView) v.findViewById(R.id.cheat_dialog_item2);
		cheatHoldrs[2] = (ImageView) v.findViewById(R.id.cheat_dialog_item3);

		notMovingItems[0] = (ImageView) v
				.findViewById(R.id.cheat_dialog_item1_still);
		notMovingItems[1] = (ImageView) v
				.findViewById(R.id.cheat_dialog_item2_still);
		notMovingItems[2] = (ImageView) v
				.findViewById(R.id.cheat_dialog_item3_still);

		for (int i = 0; i < 3; i++) {
			cheatHoldrs[i].setScaleType(ScaleType.MATRIX);
			cheatHoldrs[i].setImageBitmap(offItems[i]);

			notMovingItems[i].setScaleType(ScaleType.MATRIX);
			notMovingItems[i].setImageBitmap(offItems[i]);
		}

		cheatHoldrs[1].setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (onCheatItemClicked != null)
					onCheatItemClicked.clicked(CHEAT_ITEM_REMOVE);
			}
		});

		notMovingItems[1].setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (onCheatItemClicked != null)
					onCheatItemClicked.clicked(CHEAT_ITEM_REMOVE);
			}
		});

		cheatHoldrs[0].setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (onCheatItemClicked != null)
					onCheatItemClicked.clicked(CHEAT_ITEM_SHOW);
			}
		});

		notMovingItems[0].setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (onCheatItemClicked != null)
					onCheatItemClicked.clicked(CHEAT_ITEM_SHOW);
			}
		});

		cheatHoldrs[2].setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (onCheatItemClicked != null)
					onCheatItemClicked.clicked(CHEAT_ITEM_SKIP);
			}
		});

		notMovingItems[2].setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (onCheatItemClicked != null)
					onCheatItemClicked.clicked(CHEAT_ITEM_SKIP);
			}
		});

		// View cheatCointainer =
		// v.findViewById(R.id.cheat_dialog_cheat_container);
		// FrameLayout.LayoutParams params =
		// (android.widget.FrameLayout.LayoutParams)
		// cheatCointainer.getLayoutParams();
		// params.topMargin = cheatConverter.mHeight/2;
		// cheatCointainer.setLayoutParams(params);
		//

		SizeConverter mConverter = SizeConverter.SizeConvertorFormHeight(
				SizeManager.getScreenHeight(), 1200, 1920);

		LayoutParams rightParams = new LayoutParams(cheatConverter.mWidth,
				cheatConverter.mHeight);

		int mWidth = (SizeManager.getScreenWidth() > mConverter.mWidth) ? mConverter.mWidth
				+ mConverter.getOffset() / 2
				: SizeManager.getScreenWidth();

		rightParams.rightMargin = mWidth + cheatConverter.mWidth;
		rightParams.leftMargin = mWidth;

		cheatHoldrs[0].setLayoutParams(rightParams);

		rightParams = new LayoutParams(cheatConverter.mWidth,
				cheatConverter.mHeight);

		rightParams.leftMargin = mWidth - cheatConverter.mWidth;

		notMovingItems[0].setLayoutParams(rightParams);

		rightParams = new LayoutParams(cheatConverter.mWidth,
				cheatConverter.mHeight);

		int leftMargin = ((mConverter.getOffset() < 0) ? 0 : mConverter
				.getOffset()) / 2 - cheatConverter.mWidth;
		int rightMargin = ((mConverter.getOffset() < 0) ? 0 : mConverter
				.getOffset()) / 2;
		rightParams.leftMargin = leftMargin;
		rightParams.rightMargin = rightMargin;

		cheatHoldrs[1].setLayoutParams(rightParams);

		rightParams = new LayoutParams(cheatConverter.mWidth,
				cheatConverter.mHeight);

		rightParams.leftMargin = rightMargin;
		rightParams.rightMargin = rightMargin + cheatConverter.mWidth;

		notMovingItems[1].setLayoutParams(rightParams);

		rightParams = new LayoutParams(cheatConverter.mWidth,
				cheatConverter.mHeight);

		rightParams.leftMargin = rightMargin;
		rightParams.rightMargin = rightMargin + cheatConverter.mWidth;

		notMovingItems[2].setLayoutParams(rightParams);

		rightParams = new LayoutParams(cheatConverter.mWidth,
				cheatConverter.mHeight);

		rightParams.leftMargin = leftMargin;
		rightParams.rightMargin = rightMargin;

		cheatHoldrs[2].setLayoutParams(rightParams);
		if (Build.VERSION.SDK_INT < 11)
			animationBelowAPI(false);
		else
			animation(false);

		final Random random = new Random();

		final Handler handler = new Handler();
		if (!isToggled)
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					isToggled = true;
					if (isDetached())
						return;

					for (int j = 0; j < 3; j++)
						notMovingItems[j]
								.setImageBitmap((random.nextInt(3) % 3 == 1) ? onItems[j]
										: offItems[j]);
					handler.postDelayed(this, 1000);
				}
			}, 1100);

		ImageView background = (ImageView) v
				.findViewById(R.id.cheat_dialog_background);

		background.setScaleType(ScaleType.MATRIX);

		background.setImageBitmap(ImageManager.scaledBitmapFromResource(
				getResources(), R.drawable.cheatpage, mConverter.mHeight,
				mConverter.mWidth, ImageManager.CACHE_IMPORTANT));
		background.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myDismiss();
			}
		});

		android.widget.RelativeLayout.LayoutParams backgroundParams = new android.widget.RelativeLayout.LayoutParams(
				android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,
				android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);

		backgroundParams.leftMargin = mConverter.getOffset() / 2;

		background.setLayoutParams(backgroundParams);

		RelativeLayout mainView = new RelativeLayout(getActivity());

		setUpCoinsLayout(getActivity(), mainView);

		RelativeLayout container = (RelativeLayout) v
				.findViewById(R.id.dialog_cheat_coin_cointainer);
		container.addView(mainView);

		mDialog.setContentView(v);

		return mDialog;

	}

	public void animationBelowAPI(boolean forDismiss) {

		int forLeft = cheatConverter.mWidth;
		int forRight = -1 * forLeft;
		if (forDismiss) {
			int temp = 0;
			temp = forLeft;
			forLeft = forRight;
			forRight = temp;
		}

		com.nineoldandroids.animation.ObjectAnimator transitionForFirst = com.nineoldandroids.animation.ObjectAnimator
				.ofFloat(cheatHoldrs[0], "translationX", forRight);
		transitionForFirst.setDuration(1000);

		com.nineoldandroids.animation.ObjectAnimator transitionForSecound = com.nineoldandroids.animation.ObjectAnimator
				.ofFloat(cheatHoldrs[1], "translationX", forLeft);
		transitionForSecound.setDuration(1000);

		com.nineoldandroids.animation.ObjectAnimator transitionForThird = com.nineoldandroids.animation.ObjectAnimator
				.ofFloat(cheatHoldrs[2], "translationX", forLeft);
		transitionForThird.setDuration(1000);
		lastTime = System.currentTimeMillis();
		if (!forDismiss)
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (isGoingToWOW)
						return;
					if (System.currentTimeMillis() - lastTime < 800)
						return;

					for (int i = 0; i < 3; i++) {
						cheatHoldrs[i].setVisibility(View.GONE);
						notMovingItems[i].setVisibility(View.VISIBLE);
					}
				}
			}, 1600);

		transitionForFirst.start();
		transitionForSecound.start();
		transitionForThird.start();

	}

	long lastTime;

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		if (getDialog() != null)
			dismiss();
		super.onPause();
	}

	public void animation(boolean forDismiss) {
		int forLeft = cheatConverter.mWidth;
		int forRight = -1 * forLeft;
		if (forDismiss) {
			int temp = 0;
			temp = forLeft;
			forLeft = forRight;
			forRight = temp;
		}
		ObjectAnimator transitionForFirst = ObjectAnimator.ofFloat(
				cheatHoldrs[0], "translationX", forRight);
		transitionForFirst.setDuration(1000);

		ObjectAnimator transitionForSecound = ObjectAnimator.ofFloat(
				cheatHoldrs[1], "translationX", forLeft);
		transitionForSecound.setDuration(1000);

		ObjectAnimator transitionForThird = ObjectAnimator.ofFloat(
				cheatHoldrs[2], "translationX", forLeft);
		transitionForThird.setDuration(1000);
		lastTime = System.currentTimeMillis();
		if (!forDismiss)
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (isGoingToWOW)
						return;
					if (System.currentTimeMillis() - lastTime < 800)
						return;

					for (int i = 0; i < 3; i++) {
						cheatHoldrs[i].setVisibility(View.GONE);
						notMovingItems[i].setVisibility(View.VISIBLE);
					}
				}
			}, 1600);

		transitionForFirst.start();
		transitionForSecound.start();
		transitionForThird.start();

	}

	public void myDismiss() {
		if (Build.VERSION.SDK_INT < 11)
			animationBelowAPI(true);
		else
			animation(true);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				dismiss();
				isGoingToWOW = false;
			}
		}, 800);

	}

	void setUpCoinsLayout(Context context, RelativeLayout mainView) {

		android.widget.RelativeLayout.LayoutParams mainViewLayoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mainViewLayoutParams.topMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 10, getResources()
						.getDisplayMetrics());
		boolean forList = false;

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
				(forList) ? R.drawable.jacoinlist : R.drawable.jacoin,
				mConverter.mHeight, mConverter.mWidth,
				ImageManager.CACHE_IMPORTANT));
		v.setLayoutParams(params);
		coinBox.addView(v);
		coinBox.addView(coinText, textParams);

		coinBox.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mDialog.isVisible()) {
					return;
				}

				mDialog.show(getFragmentManager(), null);
				// mDialog.updateText(getActivity());
				// mDialog.updateTexts();
			}
		});

		ScaleAnimation animation = new ScaleAnimation(0.8f, 1f, 0.8f, 1f,
				Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setFillAfter(true);
		animation.setDuration(300);

		coinBox.startAnimation(animation);

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		coinText.setText(CoinManager.getCoin() + "");

	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		if (getActivity() != null)
			if (((LevelActivity) getActivity()).lifesFragment != null)
				((LevelActivity) getActivity()).lifesFragment.setVisible();

		super.onDismiss(dialog);

	}

}
