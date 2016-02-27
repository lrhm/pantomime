package com.irpulse.lamp;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.irpulse.Utilities.KeyGen;
import com.irpulse.Utilities.SizeConverter;
import com.irpulse.Utilities.SizeManager;
import com.irpulse.lamp.LifeSystem.LifeListener;
import com.irpulse.lamp.MyApplication.TrackerName;
import com.irpulse.level.LevelData;
import com.irpulse.level.LevelListManager;
import com.irpulse.level.LevelManager;

import com.irpulse.lamp.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class LevelActivity extends BaseActivity {

	long lastTime1 = 0;
	int lastProgres1 = 0;
	long lastTime2 = 110;
	int curState = 0;
	ImageView character;
	int lastProgres2 = 0;
	SeekBar seekBar;
	LevelData level;
	SizeConverter yaroConvertor;
	CheatDialog dialog;
	Bitmap[] pics;
	boolean isGoingRight;
	boolean isUserTouchedBeforeAutomaticMove = false;
	boolean isFirstTime = true;
	MonsterDialog monsterDialog;
	// ImageView loadingImageView;
	KeyboardView keyboardView;
	boolean isSkiped = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_level);
		// loadingDialog.show(getSupportFragmentManager(), null);
		loadingImageView = new ImageView(getApplicationContext());

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
		loadingImageView.setVisibility(View.GONE);
		loadingImageView.setImageBitmap(bitmap);
		initViews();

		try {
			Tracker t = ((MyApplication) getApplication())
					.getTracker(TrackerName.APP_TRACKER);

			// Set screen name.
			t.setScreenName("Level " + level.answer);

			// Send a screen view.
			t.send(new HitBuilders.ScreenViewBuilder().build());

			Log.d("Tracker", "Sent");

		} catch (Exception e) {
		}
	}

	public void initViews() {
		FragmentManager fm = getSupportFragmentManager();

		lifesFragment = (LifesFragment) fm
				.findFragmentById(R.id.activity_level_fragment_coin_cointainer);

		if (lifesFragment == null) {
			lifesFragment = new LifesFragment(false);
			fm.beginTransaction()
					.add(R.id.activity_level_fragment_coin_cointainer,
							lifesFragment).commit();
		}

		RelativeLayout frameLayout = (RelativeLayout) findViewById(R.id.activity_level_main_layout);

		ImageView background = (ImageView) findViewById(R.id.activity_level_tahi_img);

		level = LevelManager.getLevel(getIntent().getIntExtra(
				KeyGen.levelIdStartActivityKey, -1));

		ImageManager.loadLevelImages(level, getResources());

		dialog = new CheatDialog(
				level.randomID != LevelManager.getSolvedCount());

		if (level.randomID == 0) {
			new HelpDialog().show(getSupportFragmentManager(), null);
		}
		dialog.onCheatItemClicked = new CheatDialog.OnCheatItemClicked() {

			@Override
			public void clicked(int item) {
				// TODO Auto-generated method stub
				switch (item) {
				case CheatDialog.CHEAT_ITEM_REMOVE:

					removeItem();

					break;

				case CheatDialog.CHEAT_ITEM_SHOW:

					showOneChar();
					break;

				case CheatDialog.CHEAT_ITEM_SKIP:
					skipLevel();

					break;
				default:
					break;
				}
			}
		};
		keyboardView = new KeyboardView(this, level);
		keyboardView.onKeyboardEvent = new KeyboardView.OnKeyboardEvent() {

			@Override
			public void onHintClicked() {
				// TODO Auto-generated method stub
				if (level.randomID == 0) {
					new HelpDialog().show(getSupportFragmentManager(), null);
					return;
				}

				if (dialog.getDialog() == null)
					dialog.show(getSupportFragmentManager(), null);

				lifesFragment.setInvisble();

			}

			@Override
			public void onAllAnswered(String guess) {

				if ((guess.replace("آ", "ا")).equals((level.answer.replace("/",
						"")).replace("آ", "ا")) && !isSkiped) {

					monsterDialog = new MonsterDialog(
							level.answer.replace("/", " "),
							(level.randomID == LevelManager.getSolvedCount()) ? "+30"
									: "0", level.randomID);

					CoinManager.earnCoin(level.randomID == LevelManager
							.getSolvedCount() ? 30 : 0);
					// lifesFragment.coinText.setText(CoinManager.getCoin());
					if (level.randomID == LevelManager.getSolvedCount())
						LevelManager.unLockNewLevel();
					monsterDialog.show(getSupportFragmentManager(), null);

				}

			}
		};

		// LifeSystem.setLifeListener(new LifeListener() {
		//
		// @Override
		// public void onLifeReborend(int i) {
		// // TODO Auto-generated method stub
		// lifesFragment.checkLifes();
		// lifesFragment.triggerForLongerTime();
		// }
		//
		// @Override
		// public void onLifeDeactived(int i) {
		// // TODO Auto-generated method stub
		// lifesFragment.checkLifes();
		// lifesFragment.triggerForLongerTime();
		// }
		// });

		SizeConverter mConverter = SizeConverter.SizeConvertorFormHeight(
				SizeManager.getScreenHeight(), 1200, 1920);

		SizeConverter backgroundConvetor = SizeConverter
				.SizeConvertorFormHeight(
						SizeManager.getScreenHeight() * (0.25), 1200, 474);
		yaroConvertor = SizeConverter.SizeConvertorFormHeight(
				SizeManager.getScreenHeight() * (0.75), 1200, 1447);

		yaroConvertor.mWidth = backgroundConvetor.mWidth = mConverter.mWidth;

		frameLayout.addView(keyboardView);

		Bitmap bmp = ImageManager.scaledBitmapFromResource(getResources(),
				R.drawable.edameback, backgroundConvetor.getHeight(),
				backgroundConvetor.getWidth(), ImageManager.CACHE_IMPORTANT);

		background.setImageBitmap(bmp);
		background.setScaleType(ImageView.ScaleType.MATRIX);
		LinearLayout.LayoutParams backgroundParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, backgroundConvetor.getHeight());
		backgroundParams.leftMargin = backgroundConvetor.getOffset() / 2;
		background.setLayoutParams(backgroundParams);

		int mHeight = yaroConvertor.getHeight();
		int mWidth = yaroConvertor.getWidth();

		character = (ImageView) findViewById(R.id.activity_level_yaroo);

		// LinearLayout.LayoutParams characterParamms = new
		// LinearLayout.LayoutParams(
		// LayoutParams.WRAP_CONTENT, mHeight);
		//

		LinearLayout.LayoutParams characterParamms = new LinearLayout.LayoutParams(
				mWidth, mHeight);
		characterParamms.leftMargin = yaroConvertor.getOffset() / 2;

		character.setLayoutParams(characterParamms);
		character.setScaleType(ImageView.ScaleType.FIT_XY);

		if (level.resIds != null) {
			pics = new Bitmap[level.resIds.length];
			for (int i = 0; i < level.resIds.length; i++)
				pics[i] = ImageManager.scaledBitmapFromResource(getResources(),
						level.resIds[i], mHeight, mWidth,
						ImageManager.CACHE_LEVELS, Config.RGB_565);
		} else {
			pics = new Bitmap[level.imagePaths.length];
			for (int i = 0; i < level.imagePaths.length; i++) {
				pics[i] = ImageManager.scaledBitmapFromFile(
						level.imagePaths[i], mHeight, mWidth,
						ImageManager.CACHE_LEVELS, Config.RGB_565);
			}
		}

		Button button = new Button(this);
		android.widget.RelativeLayout.LayoutParams buttonParams = new android.widget.RelativeLayout.LayoutParams(
				yaroConvertor.convertWidth(770),
				yaroConvertor.convertHeight(970));
		buttonParams.topMargin = yaroConvertor.convertHeight(110);
		buttonParams.leftMargin = yaroConvertor.convertWidth(220)
				+ yaroConvertor.getOffset() / 2;

		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isUserTouchedBeforeAutomaticMove = false;
				isFirstTime = false;
				if (isInThePlaying) {
					stopPlaying = true;
					return;
				}
				if (seekBar.getProgress() >= 92)
					seekBar.setProgress(1);
				else
					seekBar.setProgress(seekBar.getProgress() + 1);

			}
		});

		button.setBackgroundColor(Color.TRANSPARENT);

		frameLayout.addView(button, buttonParams);

		character.setImageBitmap(pics[0]);

		// init seekBar
		initSeekBar();
		RelativeLayout.LayoutParams seekBarParams = new RelativeLayout.LayoutParams(
				yaroConvertor.convertWidth(880),
				yaroConvertor.convertHeight(98));
		seekBarParams.leftMargin = yaroConvertor.convertWidth(174)
				+ yaroConvertor.getOffset() / 2;
		seekBarParams.topMargin = yaroConvertor.convertHeight(1154);

		frameLayout.addView(seekBar, seekBarParams);
		frameLayout.addView(loadingImageView);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub

		// UserStimulator.setRunning(this);
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		monsterDialog = null;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		monsterDialog = null;
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

		super.onResume();
		loadingImageView.setVisibility(View.GONE);
		lifesFragment.setText(CoinManager.getCoin() + "");
		// LifeSystem.loadFromSharedPrefs();
		// lifesFragment.checkLifes();
		// lifesFragment.triggerForLongerTime();

	}

	boolean stopPlaying = false;
	boolean isInThePlaying = false;

	// @Override
	// public void onBackPressed() {
	// // TODO Auto-generated method stub
	//
	// int places[] = ImageManager.loadListImages(getResources());
	// int last = (places.length == 1) ? LevelListManager.ListCount
	// : places[1];
	//
	// ImageManager.loadListFromTo(0, last, getResources());
	//
	// Intent i = new Intent(LevelActivity.this, ListActivity.class);
	// startActivity(i);
	//
	// super.onBackPressed();
	// }

	private void initSeekBar() {
		// toDo realSizes
		seekBar = new SeekBar(this);

		seekBar.setMax(100);

		seekBar.setProgressDrawable(new Drawable() {

			@Override
			public void setColorFilter(ColorFilter cf) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setAlpha(int alpha) {
				// TODO Auto-generated method stub

			}

			@Override
			public int getOpacity() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void draw(Canvas canvas) {
				// TODO Auto-generated method stub

			}
		});
		SizeConverter amuConverter = SizeConverter.SizeConvertorFormHeight(
				yaroConvertor.convertHeight(98), 86, 96);
		Bitmap amu = ImageManager.scaledBitmapFromResource(getResources(),
				R.drawable.amu, amuConverter.mHeight, amuConverter.mWidth,
				ImageManager.CACHE_IMPORTANT);

		// uncomment below for hidden seekBar

		seekBar.setThumb(new BitmapDrawable(getResources(), amu));
		seekBar.setThumbOffset(amuConverter.mWidth / 2);

		seekBar.setPadding(amuConverter.mWidth / 2, 0, amuConverter.mWidth / 2,
				0);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar sekBar) {
				// TODO Auto-generated method stub
				if (Math.abs(lastProgres1 - sekBar.getProgress()) < 9
						&& lastTime2 - lastTime1 > 100) {
					if (sekBar.getProgress() >= 92) {
						sekBar.setProgress(0);
					}
					final Handler handler = new Handler();
					Runnable r = new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

							isInThePlaying = true;
							if (stopPlaying) {
								isInThePlaying = false;
								stopPlaying = false;
								return;
							}
							int progres = seekBar.getProgress();
							if (progres + 1 > 92) {
								isInThePlaying = false;
								return;
							}
							seekBar.setProgress(progres + 1);
							handler.postDelayed(this,
									level.imagePaths.length * 10);
						}
					};
					handler.post(r);

				}

				// final int p = seekBar.getProgress();
				// long curTime = System.currentTimeMillis();
				// if (curTime - lastTime2 > 100)
				// Log.d("t", "not moving");
				// else {
				// Log.d("t", "moving , " + (lastTime2 - lastTime1));
				// auto movable seekbar

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBarr) {
				// TODO Auto-generated method stub

				isUserTouchedBeforeAutomaticMove = true;
				if (isInThePlaying)
					stopPlaying = true;

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if (fromUser)
					isUserTouchedBeforeAutomaticMove = true;

				final SeekBar sekBar = seekBar;
				if (!isUserTouchedBeforeAutomaticMove) {
					if (progress == 0)
						return;

					final Handler handler = new Handler();
					Runnable r = new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

							isUserTouchedBeforeAutomaticMove = true;
							if (sekBar.getProgress() == 0) {
								isInThePlaying = false;
								return;
							}
							isInThePlaying = true;
							if (stopPlaying) {
								isInThePlaying = false;
								stopPlaying = false;
								return;
							}
							int progres = sekBar.getProgress();
							if (progres + 1 > 96) {
								sekBar.setProgress((isFirstTime) ? 0 : 96);
								isInThePlaying = false;
								return;
							}
							sekBar.setProgress(progres + 1);
							handler.postDelayed(this,
									level.imagePaths.length * 10);
						}
					};
					handler.postDelayed(r, (isFirstTime) ? 1500 : 5);
					if (isFirstTime)
						isFirstTime = false;
					return;

				}

				lastTime1 = lastTime2;
				lastProgres1 = lastProgres2;
				lastProgres2 = progress;
				int state = (int) (progress / 100. * level.imagePaths.length);

				if (curState != state) {
					curState = state;
					if (pics[0] == null)
						return;
					if (pics[0].isRecycled())
						return;
					if (isFinishing())
						return;

					if (state >= 0 && state < pics.length) {
						character.setImageBitmap(pics[state]);

					}
				}
				lastTime2 = System.currentTimeMillis();
				// lastProgres2 = progress;
				// Log.d("t", System.currentTimeMillis() + " " + progress);
			}
		});
		seekBar.setProgress(2);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		Log.d("Tracker" , "Stoping report level");
		GoogleAnalytics.getInstance(this).reportActivityStop(LevelActivity.this);
	}

	public View getLifesView() {

		return null;
	}

	public void setY(float y, View v) {
		// TODO Auto-generated method stub

		if (Build.VERSION.SDK_INT >= 11) {
			v.setY(y);
			return;
		}
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v
				.getLayoutParams();
		if (params == null)
			params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		params.topMargin = (int) y;
		v.setLayoutParams(params);
	}

	public void setXY(int x, int y, View v) {
		if (Build.VERSION.SDK_INT >= 11) {
			v.setX(x);
			return;
		}

		RelativeLayout.LayoutParams params = null;
		try {
			params = (RelativeLayout.LayoutParams) v.getLayoutParams();
		} catch (ClassCastException e) {

		}
		if (params == null)
			params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		params.leftMargin = (int) x;
		params.topMargin = y;
		v.setLayoutParams(params);

	}

	public void setX(float x, View v) {
		// TODO Auto-generated method stub

		if (Build.VERSION.SDK_INT >= 11) {
			v.setX(x);
			return;
		}

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v
				.getLayoutParams();

		if (params == null)
			params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		params.leftMargin = (int) x;
		v.setLayoutParams(params);
	}

	public boolean removeItem() {

		if ((level.randomID < LevelManager.getSolvedCount())) {

			dialog.dismiss();
			keyboardView.removeSome();
			return true;
		}

		if (CoinManager.spendCoin(40) == false) {
			Toast.makeText(this, "پول کافی نیست", Toast.LENGTH_SHORT).show();

			return false;
		}
		if (keyboardView.removeSome() == false) {
			Toast.makeText(this, "نمیشه دیگه", Toast.LENGTH_SHORT).show();

			CoinManager.earnCoin(40);
			return false;
		}
		dialog.dismiss();
		lifesFragment.setText(CoinManager.getCoin() + "");

		return true;
	}

	public boolean showOneChar() {

		if ((level.randomID < LevelManager.getSolvedCount())) {

			dialog.dismiss();
			keyboardView.showOne();
			return true;
		}

		if (CoinManager.spendCoin(60) == false) {
			Toast.makeText(this, "پول کافی نیست", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (keyboardView.showOne() == false) {
			Toast.makeText(this, "جایی خالی نیست", Toast.LENGTH_SHORT).show();
			CoinManager.earnCoin(60);

			return false;
		}

		lifesFragment.setText(CoinManager.getCoin() + "");

		dialog.dismiss();
		return true;

	}

	protected void onPause() {

		super.onPause();

		if (skipLevelHandler != null)
			skipLevelHandler.removeCallbacks(skipLevelRunnable);

	};

	Handler skipLevelHandler;
	Runnable skipLevelRunnable = new Runnable() {

		@Override
		public void run() {
			if (monsterDialog != null)
				if (monsterDialog.getDialog() == null)
					monsterDialog.show(getSupportFragmentManager(), null);

		}
	};

	public boolean skipLevel() {

		monsterDialog = new MonsterDialog(level.answer.replace("/", " "), "0",
				level.randomID);
		if ((level.randomID < LevelManager.getSolvedCount())) {

			dialog.dismiss();

			int len = level.answer.replace("/", "").replace(" ", "").length();

			if (!isSkiped) {
				skipLevelHandler = new Handler();
				skipLevelHandler.postDelayed(skipLevelRunnable, 2000);
			}
			isSkiped = true;
			for (int i = 0; i < len; i++)
				keyboardView.showOne();

			return true;
		}

		if (CoinManager.spendCoin(140) == false) {
			Toast.makeText(this, "پول کافی نیست", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (this.level.randomID <= LevelManager.getSolvedCount()) {

			// lifesFragment.setText(CoinManager.getCoin() + "");

			dialog.dismiss();
			int len = level.answer.replace("/", "").replace(" ", "").length();

			if (!isSkiped) {
				skipLevelHandler = new Handler();
				skipLevelHandler.postDelayed(skipLevelRunnable, 2000);
			}
			isSkiped = true;
			for (int i = 0; i < len; i++)
				keyboardView.showOne();

			if (level.randomID == LevelManager.getSolvedCount())
				LevelManager.unLockNewLevel();
			else
				CoinManager.earnCoin(140);

			lifesFragment.setText(CoinManager.getCoin() + "");

			// monsterDialog.rewardText = ("0");
			// monsterDialog.answerText =
			// (level.answer.replace("/", " "));

			return true;
		}

		return true;

	}

}
