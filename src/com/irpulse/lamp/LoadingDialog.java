package com.irpulse.lamp;

import com.irpulse.Utilities.SizeConverter;
import com.irpulse.Utilities.SizeManager;
import com.irpulse.Utilities.Utils;
import com.qwerjk.better_text.MagicTextView;

import com.irpulse.lamp.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class LoadingDialog extends DialogFragment {

	public static final int STYLE_PROGRESS = 1;
	public static final int STYLE_NORMAL = 2;
	final MagicTextView textView;
	final MagicTextView firstLoading;

	int progress;
	int style;

	public LoadingDialog(int style, Context context) {
		this.style = style;
		textView = new MagicTextView(context);
		firstLoading = null;
	}

	public LoadingDialog(int style, Context context, boolean first) {
		this.style = style;
		textView = new MagicTextView(context);
		if (first)
			firstLoading = new MagicTextView(context);
		else
			firstLoading = null;
	}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Dialog mDialog = new Dialog(getActivity(), R.style.Theme_Dialog);
		mDialog.setTitle(null);

		Context context = getActivity();

		setCancelable(false);
		FrameLayout mainLayout = new FrameLayout(context);

		mainLayout.setLayoutParams(new FrameLayout.LayoutParams(SizeManager
				.getScreenWidth(), SizeManager.getScreenHeight()));

		RelativeLayout container = new RelativeLayout(context);

		container.setLayoutParams(mainLayout.getLayoutParams());

		mainLayout.addView(container);

		SizeConverter mConverter = SizeConverter.SizeConverterFromLessOffset(
				SizeManager.getScreenWidth(), SizeManager.getScreenHeight(),
				1080, 1920);

		int resId = (style == STYLE_NORMAL) ? R.drawable.loadingonesecound
				: R.drawable.loading;
		Bitmap bitmap = ImageManager.scaledBitmapFromResource(getResources(),
				resId, mConverter.mHeight, mConverter.mWidth,
				ImageManager.CACHE_NO, Config.RGB_565);

		ImageView imageView = new ImageView(context);

		LayoutParams params = new LayoutParams(mConverter.mWidth,
				mConverter.mHeight);
		params.leftMargin = mConverter.getLeftOffset() / 2;
		params.topMargin = mConverter.getTopOffset() / 2;

		imageView.setScaleType(ScaleType.MATRIX);

		imageView.setImageBitmap(bitmap);

		container.addView(imageView, params);

		if (firstLoading != null) {

			firstLoading.setTextColor(Color.WHITE);
			firstLoading.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					20 * (mConverter.mWidth / 210.f));
			firstLoading.setTypeface(Utils.getCoinFont());
			// firstLoading.setStroke(1.f * (mConverter.mWidth / 210.f),
			// Color.parseColor("#0d5256"));
			firstLoading.setSingleLine(true);
			firstLoading.setGravity(Gravity.CENTER);

			LayoutParams textViewParams = new LayoutParams(
					mConverter.convertWidth(500), mConverter.convertHeight(432));
			textViewParams.leftMargin = mConverter.getLeftOffset() / 2
					+ mConverter.convertWidth(300);
			textViewParams.topMargin = mConverter.getTopOffset() / 2
					+ mConverter.convertHeight(1161);

			// updateProgress(0);
			textView.setText("1");
			firstLoading.setText("بارگذاری اولیه");

			container.addView(firstLoading, textViewParams);

		}

		textView.setTextColor(Color.WHITE);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				28 * (mConverter.mWidth / 210.f));
		textView.setTypeface(Utils.getCoinFont());
		textView.setStroke(1.f * (mConverter.mWidth / 210.f),
				Color.parseColor("#0d5256"));
		textView.setSingleLine(true);
		textView.setGravity(Gravity.CENTER);

		LayoutParams textViewParams = new LayoutParams(
				mConverter.convertWidth(334), mConverter.convertHeight(432));
		textViewParams.leftMargin = mConverter.getLeftOffset() / 2
				+ mConverter.convertWidth(373);
		textViewParams.topMargin = mConverter.getTopOffset() / 2
				+ mConverter.convertHeight(725);

		// updateProgress(0);
		// textView.setText("0");

		container.addView(textView, textViewParams);

		mDialog.setContentView(mainLayout);

		return mDialog;

	}

	public void updateProgress(final int progress) {
		if (getActivity() == null)
			return;
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				textView.setText(progress + "");
			}
		});
	}
}
