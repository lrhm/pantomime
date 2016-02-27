package com.irpulse.lamp;

import java.io.File;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.irpulse.Utilities.KeyGen;
import com.irpulse.Utilities.SizeConverter;
import com.irpulse.Utilities.SizeManager;
import com.irpulse.Utilities.UnZipUtil;
import com.irpulse.Utilities.Utils;
import com.irpulse.level.LevelListData;
import com.irpulse.level.LevelListManager;
import com.irpulse.level.LevelManager;
import com.qwerjk.better_text.MagicTextView;

import com.irpulse.lamp.MyApplication.TrackerName;
import com.irpulse.lamp.R;

import android.R.anim;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class ListActivity extends BaseActivity {

	LevelListFragment levelListFragment;
	Boolean isInIABState = false;
	Boolean isBackgroundTransparent = true;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.activity_list);

		FragmentManager fm = getSupportFragmentManager();
		levelListFragment = (LevelListFragment) fm
				.findFragmentById(R.id.list_fragment_container);

		lifesFragment = (LifesFragment) fm
				.findFragmentById(R.id.list_fragment_coin_cointainer);

		if (lifesFragment == null) {
			lifesFragment = new LifesFragment(true);
			fm.beginTransaction()
					.add(R.id.list_fragment_coin_cointainer, lifesFragment)
					.commit();
		}

		if (levelListFragment == null) {
			levelListFragment = new LevelListFragment();
			fm.beginTransaction()
					.add(R.id.list_fragment_container, levelListFragment)
					.commit();
		}
		loadingImageView = (ImageView) findViewById(R.id.activity_list_loading_level_view);
		initLoadingView();

		try {
			Tracker t = ((MyApplication) getApplication())
					.getTracker(TrackerName.APP_TRACKER);
			
			// Set screen name.
			t.setScreenName("List");

			// Send a screen view.
			t.send(new HitBuilders.ScreenViewBuilder().build());

		} catch (Exception e) {
		}
	}

	public void initLoadingView() {

		SizeConverter mConverter = SizeConverter.SizeConverterFromLessOffset(
				SizeManager.getScreenWidth(), SizeManager.getScreenHeight(),
				1080, 1920);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				mConverter.mWidth, mConverter.mHeight);
		params.leftMargin = mConverter.getLeftOffset() / 2;
		params.topMargin = mConverter.getTopOffset() / 2;

		Bitmap bitmap = ImageManager
				.scaledBitmapFromResource(getResources(),
						R.drawable.loadingonesecound, mConverter.mHeight,
						mConverter.mWidth, ImageManager.CACHE_IMPORTANT,
						Config.RGB_565);

		loadingImageView.setScaleType(ScaleType.MATRIX);

		loadingImageView.setImageBitmap(bitmap);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub

		// UserStimulator.setRunning(this);
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub

		super.onPostResume();
	}

	@Override
	protected void onResumeFragments() {
		// TODO Auto-generated method stub
		super.onResumeFragments();

	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}

	public boolean checkResources() {
		return (ImageManager.cache == null || ImageManager.cacheLevel == null
				|| ImageManager.cacheImportant == null
				|| SizeManager.getScreenHeight() == 0
				|| SizeManager.getScreenWidth() == 0
				|| LevelListManager.list == null || LevelManager.getList() == null);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (checkResources()) {
			Intent intent = new Intent(this, LoadingActivity.class);
			startActivity(intent);
			finish();
		}

		loadingImageView.setVisibility(View.GONE);
		super.onResume();

	}

	public void setY(float y, View v) {

		if (Build.VERSION.SDK_INT >= 11) {
			v.setY(y);
			return;
		}
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v
				.getLayoutParams();
		if (params == null)
			params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		params.topMargin = (int) y;
		v.setLayoutParams(params);
	}

	public void setX(float x, View v) {

		if (Build.VERSION.SDK_INT >= 11) {
			v.setX(x);
			return;
		}

		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v
				.getLayoutParams();
		if (params == null)
			params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		params.leftMargin = (int) x;
		v.setLayoutParams(params);
	}

}