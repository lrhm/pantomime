package com.irpulse.lamp;

import com.irpulse.lamp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.irpulse.Utilities.SizeConverter;
import com.irpulse.Utilities.SizeManager;
import com.irpulse.level.LevelData;
import com.irpulse.level.LevelListData;
import com.irpulse.level.LevelListManager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Debug;
import android.support.v4.util.LruCache;
import android.util.Log;

public class ImageManager {

	public static final int CACHE_LIST = 0;
	public static final int CACHE_LEVELS = 2;
	public static final int CACHE_IMPORTANT = 4;
	public static final int CACHE_NO = 8;
	private static int max;
	private static int memoryClass;

	static mCache cache;
	static mCache cacheImportant;
	static mCache cacheLevel;

	private static int inSampleSizeList = 1;

	public static ArrayList<ImageKey> listKeys = new ArrayList<ImageKey>();
	public static ArrayList<ImageKey> levelKeys = new ArrayList<ImageKey>();
	public static ArrayList<ImageKey> importantKeys = new ArrayList<ImageKey>();

	public static void initCache(Context context) {

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		memoryClass = am.getMemoryClass();

		max = (int) ((memoryClass * 1024 * 1024) * ((memoryClass > 100) ? 0.90
				: (0.80))); // more than 50%
		// causes out of
		// memory in
		// BitmapFactory.decodeResource
		// or
		// Bitmap.createBitmap
		// can fix this by
		// adding a map of
		// ImageKey and
		// Boolean to know
		// if its showing
		// if not then
		// recycle it
		cache = new mCache((int) max, CACHE_LIST);
		cacheImportant = new mCache((int) max, CACHE_LEVELS);
		cacheLevel = new mCache((int) max, CACHE_LEVELS);

	}

	// return rows that need to trigger loading
	public static int[] loadListImages(Resources resources) {


		int[] places;
		int listCount = LevelListManager.ListCount;
		int maxListSize = getListMaxSize(listCount, resources);


		int maxMemory = max;

		if (maxListSize > maxMemory * (0.9))
			inSampleSizeList = 2;

		if (maxListSize < maxMemory
				&& maxListSize > maxMemory - cacheLevel.size()
						- cacheImportant.size()) {

			int size = (cacheLevel.size() < maxMemory - maxListSize - 1024
					* 1024) ? 0 : maxMemory - maxListSize - 1024 * 1024;
			cacheLevel.remoteToSize(size);
			System.gc();

		}
		if (maxListSize < maxMemory
				&& maxListSize > maxMemory - cacheImportant.size()) {

			int size = (cacheImportant.size() < maxMemory - maxListSize - 1024
					* 1024) ? 0 : maxMemory - maxListSize - 1024 * 1024;

			cacheImportant.remoteToSize(size);
			System.gc();

		}

		if ((maxListSize + getIAPSize()) < maxMemory) {

			places = new int[1];
			places[0] = 0;


			return places;
		}

		else if (maxListSize / 2 + getIAPSize() < maxMemory) {

			long scale = maxListSize / maxMemory;
			if (scale <= 2)
				scale = 2;
			scale++;

			places = new int[(int) scale];
			int j = 0;
			for (int i = 0; i < listCount && j < scale; i += (listCount / (scale))) {
				places[j++] = i;
			}


			return places;

		}


		places = new int[1];
		places[0] = 0;
		return places;

	}

	public static void loadLevelImages(LevelData level, Resources resources) {
		SizeConverter yaroConvertor = SizeConverter.SizeConvertorFormHeight(
				SizeManager.getScreenHeight() * (0.75), 1200, 1447);

		int mHeight = yaroConvertor.getHeight();
		int mWidth = yaroConvertor.getWidth();

		int bytes = (memoryClass >= 150) ? 4 : 2 * mWidth * mHeight;

		int picBytes = bytes * (level.imagePaths.length + 2);

		cacheLevel.remoteToSize(0);

		if (cache.size() + picBytes + getCheatSize() + getIAPSize()
				+ cacheImportant.size() >= max) {
			cache.remoteToSize(cache.size() - picBytes - getCheatSize()
					- getIAPSize() - cacheImportant.size());
		}

		System.gc();
		loadCheat(resources);

		loadIAP(resources);

		if (level.resIds != null) {
			for (int i = 0; i < level.resIds.length; i++)
				ImageManager.scaledBitmapFromResource(resources,
						level.resIds[i], mHeight, mWidth,
						ImageManager.CACHE_LEVELS);
		} else {
			for (int i = 0; i < level.imagePaths.length; i++) {
				ImageManager.scaledBitmapFromFile(level.imagePaths[i], mHeight,
						mWidth, CACHE_LEVELS, Config.RGB_565);
			}
		}

	}

	public static void loadListFromTo(int fromRow, int toRow,
			Resources resources , LoadingProgressListener listener) {

		loadIAP(resources);
		for (int i = fromRow; i < LevelListManager.ListCount && i < toRow; i++) {
			if(listener != null )listener.onProgress( (int) (((float) (i+1) / toRow)*100) );
			LevelListData l = LevelListManager.list.get(i);
			ImageManager.scaledBitmapFromFileFromWidth(l.picPath,
					SizeManager.getScreenWidth(), CACHE_LIST);
		}

	}

	// return whole size of list in bytes
	public static int getListMaxSize(int maxRow, Resources resources) {

		// int oneSize = 0;
		//
		// Bitmap bitmap = ImageManager.scaledBitmapFromFileFromWidth(
		// LevelListManager.list.get(0).picPath,
		// SizeManager.getScreenWidth(), CACHE_LIST);
		// if (Build.VERSION.SDK_INT >= 12) {
		// oneSize = bitmap.getByteCount();
		//
		// } else {
		// oneSize = bitmap.getRowBytes() * bitmap.getHeight();
		// }

		SizeConverter mConverter = SizeConverter.SizeConvertorFromWidth(
				SizeManager.getScreenWidth(), 1080, 34000);
		return mConverter.mWidth * mConverter.mHeight;
		// return 71 * oneSize;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int outHeight, int outWidth) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > outHeight || width > outWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			while ((halfHeight / inSampleSize) > outHeight
					&& (halfWidth / inSampleSize) > outWidth)
				inSampleSize *= 2;
		}
		return inSampleSize;
	}

	public static Bitmap scaledBitmapFromResource(Resources resource,
			int resId, int height, int width, int cacheMode) {

		return scaledBitmapFromResource(resource, resId, height, width,
				cacheMode, Config.ARGB_8888);
	}

	public static Bitmap scaledBitmapFromFile(String pathFile, int height,
			int width, int cacheMode) {

		return scaledBitmapFromFile(pathFile, height, width, cacheMode,
				Config.ARGB_8888);
	}

	public static Bitmap scaledBitmapFromResource(Resources resource,
			int resId, int height, int width, int cacheMode, Config config) {
		ImageKey key = new ImageKey(resId, height, width);
		Bitmap result = null;
		if (cacheMode == CACHE_IMPORTANT) {
			result = cacheImportant.get(key);
		}

		if (cacheMode == CACHE_LEVELS) {
			result = cacheLevel.get(key);
		}
		if (cacheMode == CACHE_LIST) {

			result = cache.get(key);
		}

		if (result != null) {
			return result;
		}
		//
		// width /=2;
		// height /=2;

		if (memoryClass >= 150)
			config = Config.ARGB_8888;

		result = loadImageFromResource(resource, resId, height, width, config);

		if (!(result.getHeight() == height && result.getWidth() == width)) {

			Bitmap temp = Bitmap.createBitmap(width, height, config); // ARGB_8888
																		// causes
																		// out
																		// of
																		// memory
																		// sometimes

			float ratioX = width / (float) result.getWidth();
			float ratioY = height / (float) result.getHeight();

			Matrix scaleMatrix = new Matrix();
			scaleMatrix.setScale(ratioX, ratioY, 0, 0);

			Canvas canvas = new Canvas(temp);
			canvas.setMatrix(scaleMatrix);
			canvas.drawBitmap(result, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));
			result.recycle();

			result = null;
			if (cacheMode == CACHE_IMPORTANT) {
				cacheImportant.put(key, temp);
				importantKeys.add(key);
			}

			if (cacheMode == CACHE_LEVELS) {
				levelKeys.add(key);

				cacheLevel.put(key, temp);
			}
			if (cacheMode == CACHE_LIST) {

				cache.put(key, temp);
				listKeys.add(key);
			}

			return temp;
		}
		if (cacheMode == CACHE_IMPORTANT) {
			cacheImportant.put(key, result);
			importantKeys.add(key);

		}

		if (cacheMode == CACHE_LEVELS) {
			levelKeys.add(key);

			cacheLevel.put(key, result);
		}
		if (cacheMode == CACHE_LIST) {

			cache.put(key, result);
			listKeys.add(key);
		}

		return result;
	}

	public static Bitmap scaledBitmapFromFile(String pathFile, int height,
			int width, int cacheMode, Config config) {
		Bitmap result = null;

		ImageKey key = new ImageKey(pathFile, width, height);
		if (cacheMode == CACHE_IMPORTANT) {
			result = cacheImportant.get(key);
		}

		if (cacheMode == CACHE_LEVELS) {
			result = cacheLevel.get(key);
		}
		if (cacheMode == CACHE_LIST) {

			result = cache.get(key);
		}
		// Bitmap result = cache.get(key);
		if (result != null) {
			return result;
		}
		if (memoryClass >= 150)
			config = Config.ARGB_8888;

		result = loadImageFromFile(pathFile, height, width, config);

		if (!(result.getHeight() == height && result.getWidth() == width)) {

			Bitmap temp = Bitmap.createBitmap(width, height, config); // ARGB_8888
																		// causes
																		// out
																		// of
																		// memory
																		// sometimes

			float ratioX = width / (float) result.getWidth();
			float ratioY = height / (float) result.getHeight();

			Matrix scaleMatrix = new Matrix();
			scaleMatrix.setScale(ratioX, ratioY, 0, 0);

			Canvas canvas = new Canvas(temp);
			canvas.setMatrix(scaleMatrix);
			canvas.drawBitmap(result, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));
			result.recycle();

			result = null;
			if (cacheMode == CACHE_IMPORTANT) {
				cacheImportant.put(key, temp);
				importantKeys.add(key);
			}

			if (cacheMode == CACHE_LEVELS) {
				levelKeys.add(key);

				cacheLevel.put(key, temp);
			}
			if (cacheMode == CACHE_LIST) {

				cache.put(key, temp);
				listKeys.add(key);
			}

			return temp;
		}
		if (cacheMode == CACHE_IMPORTANT) {
			cacheImportant.put(key, result);
			importantKeys.add(key);

		}

		if (cacheMode == CACHE_LEVELS) {
			levelKeys.add(key);

			cacheLevel.put(key, result);
		}
		if (cacheMode == CACHE_LIST) {

			cache.put(key, result);
			listKeys.add(key);
		}

		return result;

	}

	// keeps the aspect ratio
	public static Bitmap scaledBitmapFromFileFromWidth(String pathFile,
			int width, int cacheMode) {
		Bitmap result = null;

		ImageKey key = new ImageKey(pathFile, -1, -1);
		if (cacheMode == CACHE_IMPORTANT) {
			result = cacheImportant.get(key);
		}

		if (cacheMode == CACHE_LEVELS) {
			result = cacheLevel.get(key);
		}
		if (cacheMode == CACHE_LIST) {

			result = cache.get(key);
		}
		if (result != null) {
			return result;
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPreferredConfig = Config.ARGB_8888;
		BitmapFactory.decodeFile(pathFile, options);

		SizeConverter mConverter = SizeConverter.SizeConvertorFromWidth(width,
				options.outWidth, options.outHeight);

		width = mConverter.mWidth;
		int height = mConverter.mHeight;
		width /= 2;
		height /= 2;
		options.inSampleSize = inSampleSizeList
				* calculateInSampleSize(options, height, width);

		options.inPreferredConfig = Config.ARGB_8888;
		options.inJustDecodeBounds = false;

		result = BitmapFactory.decodeFile(pathFile, options);
		// int height = mConverter.mHeight;

		if (!(result.getHeight() == height && result.getWidth() == width)) {

			Bitmap temp;

			temp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			float ratioX = width / (float) result.getWidth();
			float ratioY = height / (float) result.getHeight();

			Matrix scaleMatrix = new Matrix();
			scaleMatrix.setScale(ratioX, ratioY, 0, 0);

			Canvas canvas = new Canvas(temp);
			canvas.setMatrix(scaleMatrix);
			canvas.drawBitmap(result, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));
			result.recycle();
			result = null;
			if (cacheMode == CACHE_IMPORTANT) {
				cacheImportant.put(key, temp);
				importantKeys.add(key);
			}

			if (cacheMode == CACHE_LEVELS) {
				levelKeys.add(key);

				cacheLevel.put(key, temp);
			}
			if (cacheMode == CACHE_LIST) {

				cache.put(key, temp);
				listKeys.add(key);
			}
			return temp;

		}
		if (cacheMode == CACHE_IMPORTANT) {
			cacheImportant.put(key, result);
			importantKeys.add(key);
		}
		if (cacheMode == CACHE_LEVELS) {
			levelKeys.add(key);

			cacheLevel.put(key, result);
		}
		if (cacheMode == CACHE_LIST) {

			cache.put(key, result);
			listKeys.add(key);
		}

		return result;

	}

	public static Bitmap loadImageFromFile(String pathFile, int height,
			int width, Config config) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPreferredConfig = config;
		BitmapFactory.decodeFile(pathFile, options);

		options.inSampleSize = calculateInSampleSize(options, height, width);
		options.inJustDecodeBounds = false;

		options.inPreferredConfig = config;
		Bitmap temp = BitmapFactory.decodeFile(pathFile, options);
		return temp;

	}

	public static Bitmap loadImageFromResource(Resources resource, int resId,
			int outHeight, int outWidth) {
		return loadImageFromResource(resource, resId, outHeight, outWidth,
				Config.ARGB_8888);
	}

	public static Bitmap loadImageFromResource(Resources resource, int resId,
			int outHeight, int outWidth, Config config) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPreferredConfig = config;
		BitmapFactory.decodeResource(resource, resId, options);
		options.inSampleSize = calculateInSampleSize(options, outHeight,
				outWidth);

		options.inPreferredConfig = config;
		options.inJustDecodeBounds = false;

		Bitmap temp = BitmapFactory.decodeResource(resource, resId, options);
		return temp;
	}
	

	public static Bitmap loadImageFromResource(Resources resource, int resId) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(resource, resId, options);

		options.inJustDecodeBounds = false;

		Bitmap temp = BitmapFactory.decodeResource(resource, resId, options);
		return temp;
	}

	public static void logHeap() {
		Double allocated = new Double(Debug.getNativeHeapAllocatedSize())
				/ new Double((1048576));
		Double available = new Double(Debug.getNativeHeapSize()) / 1048576.0;
		Double free = new Double(Debug.getNativeHeapFreeSize()) / 1048576.0;
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);

		Log.d("memory", "debug. =================================");
		Log.d("memory", "debug.heap native: allocated " + df.format(allocated)
				+ "MB of " + df.format(available) + "MB (" + df.format(free)
				+ "MB free)");
		Log.d("memory",
				"debug.memory: allocated: "
						+ df.format(new Double(Runtime.getRuntime()
								.totalMemory() / 1048576))
						+ "MB of "
						+ df.format(new Double(
								Runtime.getRuntime().maxMemory() / 1048576))
						+ "MB ("
						+ df.format(new Double(Runtime.getRuntime()
								.freeMemory() / 1048576)) + "MB free)");
	}

	public static class mCache extends LruCache<ImageKey, Bitmap> {

		int mode;

		public mCache(int maxSize, int mode) {
			super(maxSize);
			this.mode = mode;
		}

		@Override
		protected int sizeOf(ImageKey key, Bitmap value) {
			if (Build.VERSION.SDK_INT >= 12) {
				return value.getByteCount();
			} else {

				return value.getRowBytes() * value.getHeight();
			}
		}

		public void remoteToSize(int size) {

			int arg0 = this.size() - size;
			int j = 0;
			while (j < arg0) {

				ImageKey key = null;

				switch (mode) {
				case CACHE_IMPORTANT:

					if (importantKeys.isEmpty())
						break;
					key = importantKeys.remove(0);

					break;
				case CACHE_LEVELS:
					if (levelKeys.isEmpty())
						break;

					key = levelKeys.remove(0);
					break;

				case CACHE_LIST:
					if (listKeys.isEmpty())
						break;

					key = listKeys.remove(0);

				default:
					break;
				}
				if (key == null)
					break;
				
				Bitmap value = get(key);
				remove(key);
				
				if(value == null)
					continue;
				if (Build.VERSION.SDK_INT >= 12) {
					j += value.getByteCount();
				} else {
					j += value.getRowBytes() * value.getHeight();
				}
				value.recycle();
				value = null;

			}

		}

//		@Override
//		public void trimToSize(int arg0) {
//			int j = 0;
//
//			Log.d("imageManager", "trim to size of " + arg0);
//
//			super.trimToSize(arg0 - j);
//		}

		@Override
		protected void entryRemoved(boolean evicted, ImageKey key,
				Bitmap oldValue, Bitmap newValue) {
			// TODO Auto-generated method stub
			super.entryRemoved(evicted, key, oldValue, newValue);
			if (newValue == null) {
				switch (mode) {
				case CACHE_IMPORTANT:
					levelKeys.remove(key);
					break;
				case CACHE_LEVELS:
					levelKeys.remove(key);

					break;
				case CACHE_LIST:
					levelKeys.remove(key);
				default:
					break;
				}
			}
			// oldValue.recycle();
			// oldValue = null;

		}

	}

	public static class ImageKey {
		String data;

		public ImageKey(int resourceId, int width, int height) {
			data = "_" + resourceId + "," + width + "," + height;
		}

		public ImageKey(String relativePath, int width, int height) {
			data = "@" + relativePath + "," + width + "," + height;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			ImageKey imageKey = (ImageKey) o;

			if (data != null ? !data.equals(imageKey.data)
					: imageKey.data != null)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			return data != null ? data.hashCode() : 0;
		}

		@Override
		public String toString() {
			return data;
		}
	}

	public static int getIAPSize() {
		SizeConverter mConverter = SizeConverter.SizeConvertorFormHeight(
				SizeManager.getScreenHeight(), 1200, 1920);

		int mWidth = (SizeManager.getScreenWidth() > mConverter.mWidth) ? mConverter.mWidth
				: SizeManager.getScreenWidth();

		SizeConverter imageConverter = SizeConverter.SizeConvertorFromWidth(
				mWidth, 600, 1061);

		return 3 * 4 * mWidth * imageConverter.mHeight;
	}

	public static int getCheatSize() {
		SizeConverter cheatConverter = SizeConverter.SizeConvertorFormHeight(
				SizeManager.getScreenHeight() * (0.17), 817, 344);

		return 4 * 6 * cheatConverter.mHeight * cheatConverter.mWidth;
	}

	public static void loadIAP(Resources resources) {

		SizeConverter mConverter = SizeConverter.SizeConvertorFormHeight(
				SizeManager.getScreenHeight(), 1200, 1920);

		int mWidth = (SizeManager.getScreenWidth() > mConverter.mWidth) ? mConverter.mWidth
				: SizeManager.getScreenWidth();

		SizeConverter imageConverter = SizeConverter.SizeConvertorFromWidth(
				mWidth, 600, 1061);

		ImageManager.scaledBitmapFromResource(resources, R.drawable.iap1,
				imageConverter.mHeight, mWidth, ImageManager.CACHE_IMPORTANT);
		ImageManager.scaledBitmapFromResource(resources, R.drawable.iap2,
				imageConverter.mHeight, mWidth, ImageManager.CACHE_IMPORTANT);
		ImageManager.scaledBitmapFromResource(resources, R.drawable.iap3,
				imageConverter.mHeight, mWidth, ImageManager.CACHE_IMPORTANT);
	}

	public static void loadCheat(Resources resources) {
		SizeConverter cheatConverter = SizeConverter.SizeConvertorFormHeight(
				SizeManager.getScreenHeight() * (0.17), 817, 344);

		ImageManager.scaledBitmapFromResource(resources, R.drawable.cheat1,
				cheatConverter.mHeight, cheatConverter.mWidth,
				ImageManager.CACHE_IMPORTANT);
		ImageManager.scaledBitmapFromResource(resources, R.drawable.cheat2,
				cheatConverter.mHeight, cheatConverter.mWidth,
				ImageManager.CACHE_IMPORTANT);
		ImageManager.scaledBitmapFromResource(resources, R.drawable.cheat3,
				cheatConverter.mHeight, cheatConverter.mWidth,
				ImageManager.CACHE_IMPORTANT);

		ImageManager.scaledBitmapFromResource(resources, R.drawable.cheat10,
				cheatConverter.mHeight, cheatConverter.mWidth,
				ImageManager.CACHE_IMPORTANT);
		ImageManager.scaledBitmapFromResource(resources, R.drawable.cheat20,
				cheatConverter.mHeight, cheatConverter.mWidth,
				ImageManager.CACHE_IMPORTANT);
		ImageManager.scaledBitmapFromResource(resources, R.drawable.cheat30,
				cheatConverter.mHeight, cheatConverter.mWidth,
				ImageManager.CACHE_IMPORTANT);

	}

	public interface LoadingProgressListener{
		public void onProgress(int i);
	}
}