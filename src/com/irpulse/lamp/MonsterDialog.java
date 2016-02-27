package com.irpulse.lamp;

import com.irpulse.Utilities.KeyGen;
import com.irpulse.Utilities.SizeConverter;
import com.irpulse.Utilities.SizeManager;
import com.irpulse.Utilities.Utils;
import com.irpulse.level.LevelManager;
import com.qwerjk.better_text.MagicTextView;

import com.irpulse.lamp.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

public class MonsterDialog extends DialogFragment {

	String answerText;
	String rewardText;

	int id;
	MagicTextView answerTextView;
	MagicTextView rewardTextView;

	public MonsterDialog(String answerText, String rewardText, int id) {
		super();
		this.answerText = answerText;
		this.rewardText = rewardText;
		this.id = id;
	}

	MediaPlayer mp;

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog mDialog = new Dialog(getActivity(), R.style.Theme_Dialog);
		mDialog.setTitle(null);
		mDialog.setCanceledOnTouchOutside(true);

		Context context = getActivity();

		mp = MediaPlayer.create(context, R.raw.win);
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mp.start();
		FrameLayout mainLayout = new FrameLayout(context);
		RelativeLayout container = new RelativeLayout(context);
		RelativeLayout containerOfContainer = new RelativeLayout(context);

		SizeConverter mConverter = SizeConverter.SizeConvertorFormHeight(
				SizeManager.getScreenHeight(), 1200, 1920);
		// SizeConverter imageConverter = SizeConverter.SizeConvertorFormHeight(
		// SizeManager.getScreenHeight(), 540, 764);
		//
		int leftOffset = 0;
		int topOffset = 0;

		SizeConverter imageConverter = SizeConverter
				.SizeConverterFromLessOffset(SizeManager.getScreenWidth(),
						SizeManager.getScreenHeight(), 540, 768);

		leftOffset += imageConverter.getLeftOffset() / 2;
		topOffset += imageConverter.getTopOffset() / 2;

		// leftOffset = (mConverter.mWidth < SizeManager.getScreenWidth()) ?
		// mConverter
		// .getOffset() / 2 + imageConverter.getOffset() / 2
		// : imageConverter.getOffset() / 2;
		//
		// if (imageConverter.mWidth < SizeManager.getScreenWidth()) {
		// imageConverter = SizeConverter.SizeConvertorFromWidth(
		// SizeManager.getScreenWidth(), 540, 764);
		// topOffset = imageConverter.getOffset() / 2;
		// leftOffset = 0;
		// }
		int mWidth = imageConverter.mWidth;

		int mHeight = imageConverter.mHeight;

		mainLayout.setLayoutParams(new LayoutParams(SizeManager
				.getScreenWidth(), SizeManager.getScreenHeight()));

		LayoutParams containerParams = new LayoutParams(mWidth, mHeight);

		// containerParams.leftMargin = leftOffset;
		// containerParams.topMargin = topOffset;

		mainLayout.addView(container, containerParams);

		RelativeLayout.LayoutParams ccontainerParams = new RelativeLayout.LayoutParams(
				mWidth, mHeight);

		ccontainerParams.leftMargin = leftOffset;
		ccontainerParams.topMargin = topOffset;

		container.addView(containerOfContainer, ccontainerParams);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				mWidth, mHeight);

		ImageView monsterImage = new ImageView(context);

		monsterImage.setImageBitmap(ImageManager.scaledBitmapFromResource(
				getResources(), R.drawable.dialog, mHeight, mWidth,
				ImageManager.CACHE_IMPORTANT));

		containerOfContainer.addView(monsterImage, params);

		answerTextView = new MagicTextView(context);

		RelativeLayout.LayoutParams answerParams = new RelativeLayout.LayoutParams(
				imageConverter.convertWidth(140),
				imageConverter.convertHeight(30));
		answerParams.leftMargin = imageConverter.convertWidth(204);
		answerParams.topMargin = imageConverter.convertHeight(370);

		containerOfContainer.addView(answerTextView, answerParams);

		rewardTextView = new MagicTextView(context);

		rewardTextView.setTextColor(Color.WHITE);
		// rewardTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
		// 10f * (mConverter.mWidth / 210.f));
		rewardTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		rewardTextView.setTypeface(Utils.getCoinFont());
		// rewardTextView.setStroke(0.1f * (mConverter.mWidth / 210.f),
		// Color.parseColor("#728e5b"));
		rewardTextView.setSingleLine(true);
		rewardTextView.setGravity(Gravity.CENTER);

		answerTextView.setTextColor(Color.WHITE);
		answerTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				8f * (mConverter.mWidth / 210.f));
		answerTextView.setTypeface(Utils.getFont());
		// answerTextView.setStroke(0.5f * (mConverter.mWidth / 210.f),
		// Color.parseColor("#728e5b"));
		answerTextView.setSingleLine(true);
		answerTextView.setGravity(Gravity.CENTER);

		RelativeLayout.LayoutParams rewardParams = new RelativeLayout.LayoutParams(
				imageConverter.convertWidth(56),
				imageConverter.convertHeight(40));
		rewardParams.leftMargin = imageConverter.convertWidth(253);
		rewardParams.topMargin = imageConverter.convertHeight(433);

		containerOfContainer.addView(rewardTextView, rewardParams);

		Button nextButton = new Button(context);

		RelativeLayout.LayoutParams nextButtonParams = new RelativeLayout.LayoutParams(
				imageConverter.convertWidth(71),
				imageConverter.convertHeight(71));
		nextButtonParams.leftMargin = imageConverter.convertWidth(370);
		nextButtonParams.topMargin = imageConverter.convertHeight(351);

		containerOfContainer.addView(nextButton, nextButtonParams);

		Button backButton = new Button(context);

		RelativeLayout.LayoutParams backButtonParams = new RelativeLayout.LayoutParams(
				imageConverter.convertWidth(71),
				imageConverter.convertHeight(71));
		backButtonParams.leftMargin = imageConverter.convertWidth(105);
		backButtonParams.topMargin = imageConverter.convertHeight(349);

		containerOfContainer.addView(backButton, backButtonParams);

		// if (((id + 2) == LevelManager.getList().size())) {
		// Toast.makeText(getActivity(), "هنوز تموم نشده ها :دی",
		// Toast.LENGTH_LONG).show();
		// }

		nextButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ImageManager.loadLevelImages(LevelManager.getLevel(id + 1),
				// getResources());

				LevelActivity activity = (LevelActivity) getActivity();

				if (((id + 1) == LevelManager.getList().size())) {
					Toast.makeText(activity, "تموم شد دیگه :دی",
							Toast.LENGTH_LONG).show();
					dismiss();
					return;
				}

				activity.loadingImageView.setVisibility(View.VISIBLE);

				Intent intent = new Intent(getActivity(), LevelActivity.class);
				intent.putExtra(KeyGen.levelIdStartActivityKey, id + 1);
				startActivity(intent);
				activity.finish();
			}
		});

		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dismiss();
				getActivity().finish();
			}
		});

		backButton.setBackgroundColor(Color.TRANSPARENT);
		nextButton.setBackgroundColor(Color.TRANSPARENT);

		rewardTextView.setText(rewardText);
		answerTextView.setText(answerText);
		mDialog.setContentView(mainLayout);

		return mDialog;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		if (onDoneClickListener != null)
			onDoneClickListener.onDoneClicked();
		super.onAttach(activity);

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public interface OnDoneClickListener {
		void onDoneClicked();
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		mp.release();
	}

	public OnDoneClickListener onDoneClickListener;

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		if (getDialog() != null)
			dismiss();
		super.onPause();
	}
}
