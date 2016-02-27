package com.irpulse.lamp;

import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import com.irpulse.Utilities.KeyGen;
import com.irpulse.Utilities.SizeConverter;
import com.irpulse.Utilities.SizeManager;
import com.irpulse.Utilities.UnZipUtil;
import com.irpulse.level.LevelData;
import com.irpulse.level.LevelListData;
import com.irpulse.level.LevelListManager;
import com.irpulse.level.LevelManager;
import com.irpulse.level.LevelListData.Size;

import com.irpulse.lamp.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import android.widget.ListView;
import android.widget.RelativeLayout;

public class LevelListFragment extends Fragment {

	mListAdapter listAdapter;
	mListView listView;
	ScaleGestureDetector scaleGestureDetector;
	View tempView;
	int[] loadingPlaces;
	ImageButton loadingView;
	int lastAcceptablePosition = 0;
	int firstAcceptablePosition = 0;

	LoadingDialog loadingDialog;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_list, container, false);
		loadingView = (ImageButton) v
				.findViewById(R.id.fragment_list_loadingView);

		// listView = new mListView(getActivity());
		((LinearLayout) v.findViewById(R.id.fragment_list_list_view))
				.addView(listView);

		return v;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		loadingPlaces = ImageManager.loadListImages(getResources());

		lastAcceptablePosition = (loadingPlaces.length == 1) ? LevelListManager.ListCount
				: loadingPlaces[1];
		firstAcceptablePosition = loadingPlaces[0];

		// loadingPlaces = ImageManager.loadListImages(getResources());
		// int last = (loadingPlaces.length == 1) ? LevelListManager.ListCount
		// : loadingPlaces[1];
		//
		// ImageManager.loadListFromTo(0, last, getResources());
		//

		listAdapter = new mListAdapter(LevelListManager.list);
		listView = new mListView(getActivity());

	}

	@Override
	public void onResume() {


		super.onResume();


		loadingPlaces = ImageManager.loadListImages(getResources());

		lastAcceptablePosition = (loadingPlaces.length == 1) ? LevelListManager.ListCount
				: loadingPlaces[1];
		firstAcceptablePosition = loadingPlaces[0];

		listAdapter.notifyDataSetChanged();
		loadingPlaces = ImageManager.loadListImages(getResources());

		int last = (loadingPlaces.length == 1) ? LevelListManager.ListCount
				: loadingPlaces[1];
		ImageManager.loadListFromTo(0, last, getResources(), null);

	}

	public mListAdapter getListAdapter() {
		return listAdapter;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Disable the divider
		listView.setDividerHeight(0);
		listView.setSelector(android.R.color.transparent);

		// listView.setSelection(LevelListManager.ListCount -
		// LevelListManager.getLastAnsweredPlace());
		// listView.setSelection(LevelListManager.ListCount - 1);

		int place = LevelListManager.ListCount
				- LevelListManager.getLastAnsweredPlace() - 1;
		if (place < 0)
			place = 0;
		listView.setSelection(place);

		listView.setScrollBarStyle(0);
		listView.setVerticalScrollBarEnabled(false);

		if (Build.VERSION.SDK_INT >= 11) {

			listView.setVerticalFadingEdgeEnabled(false);
			listView.setFadingEdgeLength(0);
			listView.setOverscrollFooter(new Drawable() {

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
			listView.setOverscrollHeader(new Drawable() {

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
		}
	}

	class mListAdapter extends ArrayAdapter<LevelListData> {

		public mListAdapter(ArrayList<LevelListData> list) {
			super(getActivity(), R.layout.item_list, list);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return super.getCount();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			int realPosition = position;

			convertView = getViewByPosition(realPosition);

			return convertView;

		}

		int lastPosition = 0;

		public View getViewByPosition(int position) {
			final Context context = getActivity();
			RelativeLayout v = new RelativeLayout(context);
			v.setLayoutParams(new android.widget.AbsListView.LayoutParams(
					android.widget.AbsListView.LayoutParams.MATCH_PARENT,
					android.widget.AbsListView.LayoutParams.WRAP_CONTENT));

			ImageView imageView = new ImageView(context);

			imageView.setScaleType(ScaleType.FIT_XY);

			LevelListData level = getItem(getCount() - 1 - position);
			Bitmap bitmap = null;
			position = getCount() - position;

			int height = 0, width = 0;
			if (position <= lastAcceptablePosition
					&& position >= firstAcceptablePosition) {

				bitmap = ImageManager.scaledBitmapFromFileFromWidth(
						level.picPath, SizeManager.getScreenWidth(),
						ImageManager.CACHE_LIST);

				height = bitmap.getHeight();
				width = bitmap.getWidth();
				imageView.setImageBitmap(bitmap);
			}

			android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(
					width * 2, height * 2);
			v.addView(imageView, params);

			for (int i = 0; i < level.levelIDs.size(); i++) {
				Button B = new Button(context);
				ImageView levelIndicator = new ImageView(context);
				initButton(v, B, levelIndicator, level.sizes.get(i),
						level.levelIDs.get(i));
				// v.addView(B);

			}
			return v;
		}

		public void initButton(RelativeLayout v, Button button,
				ImageView levelInicator, Size size, int levelID) {
			SizeConverter indiConverter = SizeConverter.SizeConvertorFromWidth(
					SizeManager.getScreenWidth() * (0.07f), 105, 144);

			button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			levelInicator.setLayoutParams(new LayoutParams(
					(int) indiConverter.mWidth, (int) indiConverter.mHeight));

			button.setWidth((int) size.w);
			button.setHeight((int) size.h);
			setX(size.x, levelInicator);
			setX(size.x, button);
			setY(size.y, button);
			setY(size.y, levelInicator);

			button.setOnClickListener(new LevelClickListener(levelID));
			button.setBackgroundColor(Color.TRANSPARENT);

			v.addView(button);

			if (levelID > LevelManager.getSolvedCount()) {

				levelInicator.setImageBitmap(ImageManager
						.scaledBitmapFromResource(getResources(),
								R.drawable.ghofl, indiConverter.mHeight,
								indiConverter.mWidth,
								ImageManager.CACHE_IMPORTANT));
				levelInicator.setScaleType(ScaleType.MATRIX);

				Animation alphaAnimation = new AlphaAnimation(1, 0.9f);
				alphaAnimation.setFillAfter(true);
				//
				// levelInicator.setImageBitmap(ImageManager
				// .scaledBitmapFromResource(getResources(),
				// R.drawable.levelindicator, (int) size.h,
				// (int) size.w, ImageManager.CACHE_IMPORTANT));
				v.addView(levelInicator);
				levelInicator.startAnimation(alphaAnimation);

			}

		}

		int lastLevelClicked = 0;

		long lastTimeGhofltClickd = System.currentTimeMillis();
		public class LevelClickListener implements OnClickListener {

			int levelId;
			boolean flag = false;

			public LevelClickListener(int id) {
				// TODO Auto-generated constructor stub
				levelId = id;
			}

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (LevelManager.getLevel(levelId).isLocked()) {

					if (System.currentTimeMillis() - lastTimeGhofltClickd >= 2500) {
						Toast.makeText(getActivity(), "قفله",
								Toast.LENGTH_SHORT).show();
						lastTimeGhofltClickd = System.currentTimeMillis();

						lastLevelClicked = levelId;
					}

				} else {
					new Handler().post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

							// ImageManager.loadLevelImages(
							// LevelManager.getLevel(levelId),
							// getResources());

							// loadingDialog = new LoadingDialog(
							// LoadingDialog.STYLE_NORMAL, getActivity());
							// loadingDialog.show(getFragmentManager(), null);

							// loadingLevelView.setVisibility(View.VISIBLE);
							((ListActivity) getActivity()).loadingImageView
									.setVisibility(View.VISIBLE);
							Intent intent = new Intent(getContext(),
									LevelActivity.class);
							intent.putExtra(KeyGen.levelIdStartActivityKey,
									levelId);
							startActivity(intent);

						}
					});

				}
			}
		}

	}

	class mListView extends ListView {

		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			// TODO Auto-generated method stub
			if (isScrollEnabled)
				return super.dispatchTouchEvent(ev);
			else
				return true;
		}

		boolean isScrollEnabled = true;

		public void setScrollDisabled() {
			isScrollEnabled = false;
		}

		public void setScrollEnabled() {
			isScrollEnabled = true;
		}

		public mListView(Context context) {
			super(context);

			SizeConverter creditConverter = SizeConverter
					.SizeConvertorFromWidth(SizeManager.getScreenWidth(), 1080,
							1500);

			footerView = new FrameLayout(context);

			footerView.setLayoutParams(new LayoutParams(creditConverter.mWidth,
					creditConverter.mHeight));

			ImageView footerImageView = new ImageView(context);
			footerImageView.setScaleType(ScaleType.FIT_XY);
			footerImageView.setImageBitmap(ImageManager
					.scaledBitmapFromResource(getResources(),
							R.drawable.credits, creditConverter.mHeight,
							creditConverter.mWidth, ImageManager.CACHE_LIST,
							Config.RGB_565));
			footerView.addView(footerImageView);

			addFooterView(footerView);
			setAdapter(listAdapter);
			removeFooterView(footerView);
			this.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
					// TODO Auto-generated method stub

					final int temp = Math.abs(getChildAt(getChildCount() - 1)
							.getTop()
							- getChildAt(getChildCount() - 1).getBottom());
					boolean isFooter = temp != LevelListData.mHeight
							&& temp != LevelListData.mHeight * 2;
					final int offset = SizeManager.getScreenHeight()
							- getChildAt(getChildCount() - 1).getTop();

					switch (scrollState) {
					case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
						// isBeforeStateOverscrol = isOverScrolled;
						// isOverScrolled = false;
					case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
						if (offset != temp) {
							// isBeforeStateOverscrol = offset == temp;
							isOverScrolledTemp = isOverScrolled = false;
						}
						break;
					case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
						isBeforeStateOverscrol = isInTheEnd;
						isOverScrolledTemp = isOverScrolled = false;
					default:
						break;
					}

					if (!isOverScrolled) {
						if (getFooterViewsCount() != 0
								&& offset > temp / 50 + 1 && isFooter) {
							// listView.setSelection(LevelListManager.ListCount
							// - 1);
							isInTheEnd = offset == temp;

							if (!isInTheEnd) {
								listView.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										int offsetTemp;
										if (isBeforeStateOverscrol) {
											if (offset < temp * (90. / 100))
												offsetTemp = -offset;
											else
												offsetTemp = temp - offset;
										} else
											offsetTemp = (offset < temp
													* (65 / 100.)) ? -1
													* offset : (temp - offset);
										// if(offsetTemp > 0){
										// isOverScrolledTemp =
										// isBeforeStateOverscrol = true;
										// }
										listView.smoothScrollBy(offsetTemp,
												1000);
									}

								});
								isOverScrolled = false;
							}
						}
					}
				}

				int mLastFirstVisibleItem = 0;

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {

					if (firstVisibleItem == mFirstVisableItem - 1)
						removeFooterView(footerView);

					if (loadingPlaces.length == 1)
						return;
					final int offset = loadingPlaces[1] - loadingPlaces[0];

					final int currentFirstVisibleItem = firstVisibleItem;
					if (currentFirstVisibleItem > mLastFirstVisibleItem) {
						// Scrolling down

						for (final int place : loadingPlaces) {
							if (firstAcceptablePosition >= totalItemCount
									- getLastVisiblePosition()
									&& (place != 1 && place != 0)) {
								lastAcceptablePosition = place
										+ visibleItemCount + 1;
								firstAcceptablePosition = (place - offset);

								smoothScrollBy(0, 100);
								setScrollDisabled();
								loadingView.setVisibility(View.VISIBLE);

								new Handler().post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										ImageManager.loadListFromTo(
												firstAcceptablePosition,
												lastAcceptablePosition,
												getResources(), null);

										loadingView.setVisibility(View.GONE);
										setScrollEnabled();
									}
								});

							}
						}

					} else if (currentFirstVisibleItem < mLastFirstVisibleItem) {

						// Log.d("place", "scrolling up");
						for (final int place : loadingPlaces) {
							if (lastAcceptablePosition != totalItemCount - 1
									&& lastAcceptablePosition <= totalItemCount
											- firstVisibleItem) {
								lastAcceptablePosition = (place + offset);
								firstAcceptablePosition = place - 1;

								smoothScrollBy(0, 100);

								setScrollDisabled();
								loadingView.setVisibility(View.VISIBLE);

								new Handler().post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										ImageManager.loadListFromTo(
												firstAcceptablePosition,
												lastAcceptablePosition,
												getResources(), null);
										setScrollEnabled();

										loadingView.setVisibility(View.GONE);
									}
								});

							}
						}
					}

					mLastFirstVisibleItem = currentFirstVisibleItem;

				}
			});

		}

		FrameLayout footerView;
		int count = 0;
		int mFirstVisableItem = 0;
		boolean isOverScrolled = false;
		boolean isOverScrolledTemp = false;
		boolean isInTheEnd = false;
		boolean isBeforeStateOverscrol = false;

		@Override
		protected void onOverScrolled(int scrollX, int scrollY,
				boolean clampedX, boolean clampedY) {

			// super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
			if (this.getLastVisiblePosition() == getCount() - 1) {
				if (getFooterViewsCount() != 0) {
					isOverScrolled = true;
					isBeforeStateOverscrol = true;
					isOverScrolledTemp = true;
					return;

				}
				count++;
				mFirstVisableItem = getFirstVisiblePosition();
				if (count == 10) {
					addFooterView(footerView);
					count = 0;
				}
			}

		}

	}

	public void setY(float y, View v) {
		// TODO Auto-generated method stub

		if (Build.VERSION.SDK_INT >= 11) {
			v.setY(y);
			return;
		}
		RelativeLayout.LayoutParams params = (LayoutParams) v.getLayoutParams();
		if (params == null)
			params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		params.topMargin = (int) y;
		v.setLayoutParams(params);
	}

	public void setX(float x, View v) {
		// TODO Auto-generated method stub

		if (Build.VERSION.SDK_INT >= 11) {
			v.setX(x);
			return;
		}

		RelativeLayout.LayoutParams params = (LayoutParams) v.getLayoutParams();

		if (params == null)
			params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		params.leftMargin = (int) x;
		v.setLayoutParams(params);
	}

}
