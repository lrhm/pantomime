package com.irpulse.lamp;

import java.util.ArrayList;
import java.util.Random;

import com.irpulse.Utilities.SizeConverter;
import com.irpulse.Utilities.SizeManager;
import com.irpulse.Utilities.Utils;
import com.irpulse.level.LevelData;

import com.irpulse.lamp.R;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class KeyboardView extends RelativeLayout {
	public OnKeyboardEvent onKeyboardEvent;
	SizeConverter answerConverter;
	public KeyView[] buttons;
	public KeyView[] answers;
	int leftX, rightX;
	int heightSwipe;
	String allStrings = "ضثقفغعهخحشسیبلاتنمکگچجظطژزرذدپو";
	LevelData level;
	SwipeView swipeView;
	public String showedData = "";
	public String levelAnswer;

	public void getAnswersInALine(String answer, Context context,
			boolean isTwoLine, int lineNumber, int otherAnswerCount) {
		int length = answer.length();
		int middle = SizeManager.getScreenWidth() / 2;
		int offSet = answerConverter.mWidth / 30;
		if (length == 1) {
			offSet += answerConverter.mWidth / 2;
		}

		KeyView[] temp = new KeyView[answer.length()];
		RelativeLayout answerCointainer = new RelativeLayout(context);
		LayoutParams answerParams = new LayoutParams(getLayoutParams());
		int topMargin = 0;
		if (isTwoLine) {
			if (lineNumber == 1) {
				topMargin = (int) (SizeManager.getScreenHeight() * (0.677)
						- answerConverter.mHeight / 2 - 1);
			}
			if (lineNumber == 2) {
				topMargin = (int) (SizeManager.getScreenHeight() * (0.677)
						+ answerConverter.mHeight / 2 + 1);
			}
		} else {
			topMargin = (int) (SizeManager.getScreenHeight() * (0.677));
		}
		int indexer = (lineNumber == 1) ? 0 : otherAnswerCount;
		answerParams.topMargin = topMargin;
		int top = answerParams.topMargin;
		int leftOffset = 0;
		int rightOffset = 0;

		for (int i = 0; i < length / 2; i++) {
			middle = SizeManager.getScreenWidth() / 2;
			offSet = answerConverter.mWidth / 30;
			if (length % 2 == 1) {
				offSet += (answer.charAt(length / 2) == ' ') ? answerConverter.mWidth / 4
						: answerConverter.mWidth / 2;
			}

			RelativeLayout.LayoutParams paramsRight = new RelativeLayout.LayoutParams(
					getLayoutParams());
			RelativeLayout.LayoutParams paramsLeft = new RelativeLayout.LayoutParams(
					getLayoutParams());
			paramsLeft.leftMargin = middle - (i + 1) * answerConverter.mWidth
					- offSet + leftOffset;
			paramsRight.leftMargin = middle + offSet + i
					* answerConverter.mWidth + rightOffset;

			boolean state = answer.charAt(length / 2 - (i + 1)) == ' ';

			if (state) {
				rightOffset -= answerConverter.mWidth / 2;
				paramsRight.leftMargin += rightOffset;

			}
			KeyView rightKey = new KeyView(context, KeyView.TYPE_RIGHT,
					KeyView.MODE_ANSWER, state);

			state = answer.charAt(length / 2
					+ (i + ((length % 2 == 1) ? 1 : 0))) == ' ';

			if (state) {
				leftOffset += answerConverter.mWidth / 2;
				paramsLeft.leftMargin += leftOffset;
			}
			KeyView leftKey = new KeyView(context, KeyView.TYPE_LEFT,
					KeyView.MODE_ANSWER, state);

			rightKey.setXY(paramsRight.leftMargin, top);
			leftKey.setXY(paramsLeft.leftMargin, top);
			rightKey.index = length / 2 - (i + 1) + indexer;
			leftKey.index = length / 2 + (i + ((length % 2 == 1) ? 1 : 0))
					+ indexer;
			temp[length / 2 + (i + ((length % 2 == 1) ? 1 : 0))] = leftKey;
			temp[length / 2 - (i + 1)] = rightKey;

			// answers[level.answer.length() / 2 + i + 1] = leftKey;
			//
			// answers[level.answer.length() / 2 + i + 1].index = level.answer
			// .length() / 2 + i + 1;
			//
			// answers[level.answer.length() / 2 - i - 1] = rightKey;
			// answers[level.answer.length() / 2 - i - 1].index = level.answer
			// .length() / 2 - i - 1;

			answerCointainer.addView(leftKey, paramsLeft);
			answerCointainer.addView(rightKey, paramsRight);

		}

		if (length % 2 == 1) {
			int mmiddle = SizeManager.getScreenWidth() / 2;
			int moffSet = answerConverter.mWidth / 2;
			RelativeLayout.LayoutParams paramsLamp = new RelativeLayout.LayoutParams(
					getLayoutParams());

			boolean state = answer.charAt(length / 2) == ' ';

			KeyView center = new KeyView(context, KeyView.TYPE_CENTER,
					KeyView.MODE_ANSWER, state);
			paramsLamp.leftMargin = mmiddle - moffSet;
			center.setXY(paramsLamp.leftMargin, top);
			answerCointainer.addView(center, paramsLamp);
			temp[length / 2] = center;
			temp[length / 2].index = length / 2 + indexer;
			// answers[level.answer.length() / 2] = center;
			// answers[level.answer.length() / 2].index = level.answer.length()
			// / 2;

		}

		for (int i = 0; i < temp.length; i++) {
			answers[i + indexer] = temp[i];
		}
		addView(answerCointainer, answerParams);

	}

	public KeyboardView(Context context, LevelData level) {
		super(context);
		// TODO Auto-generated constructor stub

		this.level = level;
		levelAnswer = level.answer.replace(" ", "").replace("/", "");

		Random random = new Random(level.levelId);
		String string = level.answer.replace(" ", "").replace("/", "");

		ArrayList<Character> unshuffledavailableGuesses = new ArrayList<Character>();

		for (Character charr : string.toCharArray())
			unshuffledavailableGuesses.add(charr);
		while (unshuffledavailableGuesses.size() != 20) {
			unshuffledavailableGuesses.add(allStrings.charAt(random
					.nextInt(allStrings.length())));
		}
		ArrayList<Character> availableGuesses = new ArrayList<Character>();
		while (unshuffledavailableGuesses.size() != 0) {
			availableGuesses.add(unshuffledavailableGuesses.remove(random
					.nextInt(unshuffledavailableGuesses.size())));
		}

		buttons = new KeyView[20];
		answers = new KeyView[level.answer.replace("/", "").length()];

		setLayoutParams(new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		SizeConverter yaroConvertor = SizeConverter.SizeConvertorFormHeight(
				SizeManager.getScreenHeight(), 1200, 1920);

		int baseWidth = (yaroConvertor.mWidth > SizeManager.getScreenWidth()) ? SizeManager
				.getScreenWidth() : yaroConvertor.mWidth;

		buttonConvertor = SizeConverter.SizeConvertorFromWidth(baseWidth
				* (10.6f / 100), 120, 132);
		answerConverter = SizeConverter.SizeConvertorFromWidth(baseWidth
				* (6.f / 100), 77, 77);

		swipeView = new SwipeView(context);

		heightSwipe = (int) (yaroConvertor.mHeight * (0.18));
		LayoutParams swipeParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				(int) (yaroConvertor.mHeight * (0.18)));
		swipeParams.topMargin = (int) (yaroConvertor.mHeight * (0.57));
		addView(swipeView, swipeParams);

		if (level.answer.contains("/")) {
			getAnswersInALine(level.answer.split("/")[0], context, true, 1, 0);
			getAnswersInALine(level.answer.split("/")[1], context, true, 2,
					level.answer.split("/")[0].length());

		} else {
			getAnswersInALine(level.answer, context, false, 1, 0);
		}

		leftX = answers[level.answer.replace("/", "").length() - 1].x
				+ answerConverter.mWidth;
		rightX = answers[0].x - answerConverter.mWidth;

		RelativeLayout buttonContainer = new RelativeLayout(context);
		LayoutParams buttonContainerParams = new LayoutParams(getLayoutParams());

		int middle, top, offSet;
		buttonContainerParams.topMargin = (int) (SizeManager.getScreenHeight() * (0.75));
		top = buttonContainerParams.topMargin;
		int j = 0;
		int k = 0;
		for (int i = 0; i < 4; i++) {
			middle = SizeManager.getScreenWidth() / 2;
			offSet = buttonConvertor.mWidth / 30;
			RelativeLayout.LayoutParams paramsRight = new RelativeLayout.LayoutParams(
					getLayoutParams());
			RelativeLayout.LayoutParams paramsLeft = new RelativeLayout.LayoutParams(
					getLayoutParams());
			paramsLeft.leftMargin = middle - offSet - (i + 1)
					* buttonConvertor.mWidth;
			paramsRight.leftMargin = middle + offSet + i
					* buttonConvertor.mWidth;
			KeyView rightKey = new KeyView(context, KeyView.TYPE_RIGHT,
					KeyView.MODE_BUTTON, availableGuesses.get(k++));

			KeyView leftKey = new KeyView(context, KeyView.TYPE_LEFT,
					KeyView.MODE_BUTTON, availableGuesses.get(k++));

			rightKey.setXY(paramsRight.leftMargin, top);
			leftKey.setXY(paramsLeft.leftMargin, top);

			buttonContainer.addView(leftKey, paramsLeft);
			buttonContainer.addView(rightKey, paramsRight);
			buttons[j++] = rightKey;
			buttons[j - 1].index = j - 1;
			buttons[j++] = leftKey;
			buttons[j - 1].index = j - 1;
		}
		for (int i = 0; i < 3; i++) {

			middle = SizeManager.getScreenWidth() / 2;
			offSet = buttonConvertor.mWidth / 2;
			RelativeLayout.LayoutParams paramsRight = new RelativeLayout.LayoutParams(
					getLayoutParams());
			RelativeLayout.LayoutParams paramsLeft = new RelativeLayout.LayoutParams(
					getLayoutParams());
			paramsLeft.topMargin = paramsRight.topMargin = buttonConvertor
					.getHeight() + buttonConvertor.getHeight() / 10;
			paramsLeft.leftMargin = middle - offSet - (i + 1)
					* buttonConvertor.mWidth;
			paramsRight.leftMargin = middle + offSet + i
					* buttonConvertor.mWidth;
			top = paramsLeft.topMargin + buttonContainerParams.topMargin;

			KeyView rightKey = new KeyView(context, KeyView.TYPE_RIGHT,
					KeyView.MODE_BUTTON, availableGuesses.get(k++));

			KeyView leftKey = new KeyView(context, KeyView.TYPE_LEFT,
					KeyView.MODE_BUTTON, availableGuesses.get(k++));

			rightKey.setXY(paramsRight.leftMargin, top);
			leftKey.setXY(paramsLeft.leftMargin, top);

			buttonContainer.addView(leftKey, paramsLeft);
			buttonContainer.addView(rightKey, paramsRight);
			buttons[j++] = rightKey;
			buttons[j - 1].index = j - 1;
			buttons[j++] = leftKey;
			buttons[j - 1].index = j - 1;
		}
		int mmiddle = SizeManager.getScreenWidth() / 2;
		int moffSet = buttonConvertor.mWidth / 2;
		RelativeLayout.LayoutParams paramsLamp = new RelativeLayout.LayoutParams(
				getLayoutParams());
		paramsLamp.topMargin = buttonConvertor.getHeight()
				+ buttonConvertor.getHeight() / 10;
		KeyView lamp = new KeyView(context, KeyView.TYPE_LEFT,
				KeyView.MODE_LAMP);
		paramsLamp.leftMargin = mmiddle - moffSet;
		buttonContainer.addView(lamp, paramsLamp);

		for (int i = 0; i < 3; i++) {
			middle = SizeManager.getScreenWidth() / 2;
			offSet = buttonConvertor.mWidth / 30;
			RelativeLayout.LayoutParams paramsRight = new RelativeLayout.LayoutParams(
					getLayoutParams());
			RelativeLayout.LayoutParams paramsLeft = new RelativeLayout.LayoutParams(
					getLayoutParams());
			paramsLeft.topMargin = paramsRight.topMargin = buttonConvertor
					.getHeight() * 2 + buttonConvertor.getHeight() / 5;
			top = paramsLeft.topMargin + buttonContainerParams.topMargin;
			paramsLeft.leftMargin = middle - offSet - (i + 1)
					* buttonConvertor.mWidth;
			paramsRight.leftMargin = middle + offSet + i
					* buttonConvertor.mWidth;

			KeyView rightKey = new KeyView(context, KeyView.TYPE_RIGHT,
					KeyView.MODE_BUTTON, availableGuesses.get(k++));

			KeyView leftKey = new KeyView(context, KeyView.TYPE_LEFT,
					KeyView.MODE_BUTTON, availableGuesses.get(k++));

			rightKey.setXY(paramsRight.leftMargin, top);
			leftKey.setXY(paramsLeft.leftMargin, top);
			buttonContainer.addView(leftKey, paramsLeft);
			buttonContainer.addView(rightKey, paramsRight);
			buttons[j++] = rightKey;
			buttons[j - 1].index = j - 1;
			buttons[j++] = leftKey;
			buttons[j - 1].index = j - 1;

		}

		addView(buttonContainer, buttonContainerParams);

	}

	SizeConverter buttonConvertor;

	public int getEmptyAnswersCount() {
		int res = 0;
		for (KeyView answer : answers)
			if (answer.otherIndex == -1 && answer.state != KeyView.STATE_EMPTY)
				res++;

		return res;

	}

	public int getButtonEmptyCount() {
		int res = 0;
		for (KeyView answer : buttons) {
			if (answer.otherIndex == -1 && answer.state == KeyView.STATE_NORMAL)
				res++;

		}

		return res;

	}

	public String getAvailableGuesses() {
		String res = "";
		for (KeyView key : buttons)
			if (key.otherIndex == -1 && key.state != KeyView.STATE_REMOVED
					&& key.state != KeyView.STATE_TOUCHED) {
				res += (key.character + "");
			}

		return res;
	}

	int lenOfCharInString(String string, Character character) {
		int len = 0;
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == character)
				len++;
		}
		return len;
	}

	public boolean removeSome() {
		int len = getButtonEmptyCount() / 5;
		if (len >= getButtonEmptyCount()
				- level.answer.replace("/", "").replace(" ", "").length())
			return false;
		while (len != 0) {

			while (true) {
				int random = new Random().nextInt(buttons.length);

				if (buttons[random].otherIndex == -1
						&& buttons[random].state != KeyView.STATE_REMOVED
						&& buttons[random].state != KeyView.STATE_TOUCHED) {

					int lenInAnswer = lenOfCharInString(levelAnswer,
							buttons[random].character);
					int lenInButtons = lenOfCharInString(getAvailableGuesses(),
							buttons[random].character);
					if (lenInButtons > lenInAnswer) {
						buttons[random].setRemoved();
						len--;
					}
					if (len == 0)
						break;
				}
			}
		}
		return true;

	}

	public int findIndexOfCharacter(Character character) {

		if (!level.answer.contains("/")) {

			for (int i = 0; i < answers.length; i++) {
				if (answers[i].otherIndex == -1
						&& answers[i].state != KeyView.STATE_EMPTY) {
					// level.answer.replace("/", "").){
					// Log.d("keyboard" , "find it" + i);

					String ans = level.answer;
					if (ans.charAt(i) == character)
						return i;
					// return i;
				}

			}

		}

		else {
			String[] temp1 = level.answer.split("/");

			for (int i = 0; i < answers.length; i++) {
				if (answers[i].otherIndex == -1
						&& answers[i].state != KeyView.STATE_EMPTY) {
					// level.answer.replace("/", "").){
					// Log.d("keyboard" , "find it" + i);

					String ans = temp1[0];
					if (i < ans.length())
						if (ans.charAt(i) == character)
							return i;
					ans = temp1[1];
					if (i >= temp1[0].length())
						if (ans.charAt(i - temp1[0].length()) == character)
							return i;
					// return i;
				}

			}

		}

		return -1;
	}

	public boolean showOne() {
		int answerCount = getEmptyAnswersCount();
		int buttonEmptyCount = getButtonEmptyCount();
		if (answerCount == 0 || buttonEmptyCount == 0)
			return false;

		String temp = levelAnswer;
		while (true) {
			int random = new Random().nextInt(levelAnswer.length());
			Character character = levelAnswer.charAt(random);

			temp.replace(character + "", "");
			for (KeyView key : buttons) {
				if (key.character.equals(character)
						&& key.state == KeyView.STATE_NORMAL) {
					if (findIndexOfCharacter(key.character) != -1) {
						String temp2 = "";
						for (int i = 0; i < levelAnswer.length(); i++)
							if (i != random)
								temp2 += (levelAnswer.charAt(i) + "");
						levelAnswer = temp2;
						key.setAnswered();
						return true;
					} 
				}
			}
			if (temp.length() == 0)
				return false;

		}

		// return true;
	}

	public void clearAnswers() {
		for (int i = 0; i < answers.length; i++) {
			KeyView thisView = answers[i];

			if (thisView.otherIndex != -1
					&& thisView.state != KeyView.STATE_ANSWERED) {

				thisView.state = KeyView.STATE_NORMAL;
				int index = thisView.otherIndex;
				thisView.otherIndex = -1;
				buttons[index].state = KeyView.STATE_NORMAL;
				buttons[index].otherIndex = -1;
				buttons[index].imgView
						.setImageBitmap(ImageManager
								.scaledBitmapFromResource(
										getResources(),
										(buttons[index].type == KeyView.TYPE_LEFT) ? R.drawable.button_left
												: R.drawable.button_right,
										buttonConvertor.mHeight,
										buttonConvertor.mWidth,
										ImageManager.CACHE_IMPORTANT));

				int fromX = +thisView.x - buttons[index].x;
				int fromY = +thisView.y - buttons[index].y;
				int toX = 0;
				int toY = 0;
				TranslateAnimation translateAnimation = new TranslateAnimation(
						fromX, toX, fromY, toY);
				translateAnimation.setFillAfter(true);
				translateAnimation.setDuration(400);

				float scaleX = ((float) thisView.answerConverter.mWidth)
						/ buttonConvertor.mWidth;
				float scaleY = ((float) thisView.answerConverter.mHeight)
						/ buttonConvertor.mHeight;
				ScaleAnimation scaleAnimation = new ScaleAnimation(scaleX, 1,
						scaleY, 1);
				scaleAnimation.setFillAfter(true);
				scaleAnimation.setDuration(400);

				AnimationSet set = new AnimationSet(true);
				set.setFillAfter(true);
				set.addAnimation(scaleAnimation);
				set.addAnimation(translateAnimation);

				buttons[index].charTemp.startAnimation(set);

			}
		}
	}

	public interface OnKeyboardEvent {
		public void onAllAnswered(String guess);

		public void onHintClicked();
	}

	public boolean isAllAnswered() {
		for (KeyView answer : answers)
			if (answer.otherIndex == -1 && answer.state != KeyView.STATE_EMPTY)
				return false;
		return true;
	}

	public int getFirstEmptyIndexAnswers() {
		for (int i = 0; i < answers.length; i++)
			if (answers[i].otherIndex == -1
					&& answers[i].state != KeyView.STATE_EMPTY)
				return i;
		return -1;
	}

	public String getAllAswers() {
		String temp = "";
		for (KeyView answer : answers) {

			temp += (answer.otherIndex != -1) ? buttons[answer.otherIndex].character
					: " ";
		}
		// int len = temp.length();
		// String res = "";
		// for (int i = len - 1; i >= 0; i--)
		// res += temp.toCharArray()[i];
		return temp;
	}

	public class SwipeView extends View {

		public SwipeView(Context context) {
			super(context);

			// TODO Auto-generated constructor stub
		}

		float xStart;

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				xStart = event.getX();
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE) {

				if ((xStart <= leftX && event.getX() >= rightX)
						|| (xStart >= rightX && event.getX() <= leftX))
					if (event.getY() <= heightSwipe && event.getY() >= 0) {
						clearAnswers();
					}
			}

			return true;
		}
	}

	public class KeyView extends RelativeLayout {
		public int x = -1, y = -1;
		public int otherIndex = -1;
		public int index;
		public ImageView imgView;
		public ImageView charHolder;
		public ImageView charTemp;
		public boolean isCharTempAdded = false;
		public Character character;
		public int state = -1;
		public int type;
		public int mode;
		public static final int TYPE_LEFT = 0;
		public static final int TYPE_RIGHT = 1;
		public static final int TYPE_CENTER = 3;
		public static final int MODE_ANSWER = 0;
		public static final int MODE_BUTTON = 1;
		public static final int MODE_LAMP = 3;
		public static final int STATE_TOUCHED = 0;
		public static final int STATE_NORMAL = 1;
		public static final int STATE_EMPTY = 2;
		public static final int STATE_REMOVED = 4;
		public static final int STATE_ANSWERED = 8;

		public void setRemoved() {
			this.state = STATE_REMOVED;
			AlphaAnimation animation = new AlphaAnimation(1, 0);
			animation.setFillAfter(true);
			animation.setDuration(1000);

			AlphaAnimation animation2 = new AlphaAnimation(1, 0);
			animation2.setFillAfter(true);
			animation2.setDuration(1000);

			charHolder.startAnimation(animation2);
			charTemp.startAnimation(animation);

			imgView.setImageBitmap(ImageManager.scaledBitmapFromResource(
					getResources(),
					(type == TYPE_LEFT) ? R.drawable.button_left_touched
							: R.drawable.button_right_touched,
					buttonConvertor.mHeight, buttonConvertor.mWidth,
					ImageManager.CACHE_IMPORTANT));
		}

		public void setAnswered() {
			this.state = STATE_REMOVED;
			int mIndex = findIndexOfCharacter(this.character);

			answers[mIndex].otherIndex = index;
			answers[mIndex].state = STATE_ANSWERED;
			otherIndex = mIndex;

			int answerDrawable = 0;
			switch (answers[mIndex].type) {
			case TYPE_CENTER:
				answerDrawable = R.drawable.answer_vasat_green;
				break;
			case TYPE_LEFT:
				answerDrawable = R.drawable.answer_left_green;
				break;
			case TYPE_RIGHT:
				answerDrawable = R.drawable.answer_right_green;
				break;

			}
			answers[mIndex].imgView.setImageBitmap(ImageManager
					.scaledBitmapFromResource(getResources(), answerDrawable,
							answerConverter.mHeight, answerConverter.mWidth,
							ImageManager.CACHE_IMPORTANT));

			imgView.setImageBitmap(ImageManager.scaledBitmapFromResource(
					getResources(),
					(type == TYPE_LEFT) ? R.drawable.button_left_touched
							: R.drawable.button_right_touched,
					buttonConvertor.mHeight, buttonConvertor.mWidth,
					ImageManager.CACHE_IMPORTANT));
			this.charHolder.setVisibility(View.GONE);

			int toX = answers[mIndex].x - x;
			int toY = answers[mIndex].y - y;
			TranslateAnimation translateAnimation = new TranslateAnimation(0,
					toX, 0, toY);
			translateAnimation.setFillAfter(true);
			translateAnimation.setDuration(400);

			float scaleX = ((float) answerConverter.mWidth)
					/ buttonConvertor.mWidth;
			float scaleY = ((float) answerConverter.mHeight)
					/ buttonConvertor.mHeight;
			ScaleAnimation scaleAnimation = new ScaleAnimation(1, scaleX, 1,
					scaleY);
			scaleAnimation.setFillAfter(true);
			scaleAnimation.setDuration(400);
			if (!isCharTempAdded) {
				LayoutParams params = new LayoutParams(buttonConvertor.mWidth,
						buttonConvertor.mHeight);
				params.leftMargin = x;
				params.topMargin = y;
				KeyboardView.this.addView(charTemp, params);
				isCharTempAdded = true;
			}
			AnimationSet set = new AnimationSet(true);
			set.setFillAfter(true);
			set.addAnimation(scaleAnimation);
			set.addAnimation(translateAnimation);

			charTemp.startAnimation(set);

			if (isAllAnswered())
				if (onKeyboardEvent != null)
					onKeyboardEvent.onAllAnswered(getAllAswers());

		}

		SizeConverter answerConverter;

		// LAMP
		public KeyView(Context context) {
			this(context, 0, 3, '-');
		}

		public KeyView(Context context, int type, int mode) {
			this(context, type, mode, '-');
		}

		public KeyView(Context context, int type, int mode, Character character) {
			super(context);

			mInit(context, type, mode, character);
		}

		public KeyView(Context context, int type, int mode, boolean state) {

			super(context);
			this.state = (state) ? STATE_EMPTY : STATE_NORMAL;
			if (state) {
				SizeConverter yaroConvertor = SizeConverter
						.SizeConvertorFormHeight(
								SizeManager.getScreenHeight() * (0.75), 1200,
								1447);

				int baseWidth = (yaroConvertor.mWidth > SizeManager
						.getScreenWidth()) ? SizeManager.getScreenWidth()
						: yaroConvertor.mWidth;
				answerConverter = SizeConverter.SizeConvertorFromWidth(
						baseWidth * (6.f / 100), 77, 77);
				setLayoutParams(new RelativeLayout.LayoutParams(
						answerConverter.mWidth / 2, answerConverter.mHeight));

				return;
			} else
				mInit(context, type, mode, character);
		}

		SizeConverter yaroConvertor;

		public void mInit(Context context, int type, int mode,
				Character character) {
			this.mode = mode;
			this.type = type;
			if (this.state == -1)
				this.state = STATE_NORMAL;
			yaroConvertor = SizeConverter.SizeConvertorFormHeight(
					SizeManager.getScreenHeight() * (0.75), 1200, 1447);

			int baseWidth = (yaroConvertor.mWidth > SizeManager
					.getScreenWidth()) ? SizeManager.getScreenWidth()
					: yaroConvertor.mWidth;

			buttonConvertor = SizeConverter.SizeConvertorFromWidth(baseWidth
					* (10.6f / 100), 120, 132);
			answerConverter = SizeConverter.SizeConvertorFromWidth(baseWidth
					* (6.f / 100), 77, 77);
			RelativeLayout.LayoutParams params;
			if (mode == MODE_ANSWER) {
				setLayoutParams(new RelativeLayout.LayoutParams(
						answerConverter.mWidth, answerConverter.mHeight));

			} else {
				setLayoutParams(new RelativeLayout.LayoutParams(
						buttonConvertor.mWidth, buttonConvertor.mHeight));

			}

			imgView = new ImageView(context);
			imgView.setLayoutParams(getLayoutParams());
			imgView.setScaleType(ScaleType.FIT_XY);
			charHolder = new ImageView(context);
			charHolder.setLayoutParams(getLayoutParams());
			charHolder.setScaleType(ScaleType.FIT_XY);
			charTemp = new ImageView(context);
			charTemp.setLayoutParams(getLayoutParams());
			charTemp.setScaleType(ScaleType.FIT_XY);

			setText(character);
			addView(imgView);
			addView(charHolder);

			int drawAble = 0;
			int height = 0;
			int width = 0;

			switch (type) {
			case TYPE_LEFT:

				switch (mode) {
				case MODE_ANSWER:
					drawAble = R.drawable.answer_left;
					height = answerConverter.mHeight;
					width = answerConverter.mWidth;

					break;

				case MODE_BUTTON:
					drawAble = R.drawable.button_left;
					height = buttonConvertor.mHeight;
					width = buttonConvertor.mWidth;

					break;
				default:
					break;
				}

				break;
			case TYPE_RIGHT:

				switch (mode) {
				case MODE_ANSWER:
					drawAble = R.drawable.answer_right;
					height = answerConverter.mHeight;
					width = answerConverter.mWidth;
					break;

				case MODE_BUTTON:
					drawAble = R.drawable.button_right;
					height = buttonConvertor.mHeight;
					width = buttonConvertor.mWidth;

					break;

				default:
					break;
				}

				break;

			case TYPE_CENTER:
				if (mode == MODE_ANSWER) {
					drawAble = R.drawable.answer_vasat;
					height = answerConverter.mHeight;
					width = answerConverter.mWidth;
				}
			default:
				break;
			}

			if (mode == MODE_LAMP) {
				drawAble = R.drawable.hint;
				height = buttonConvertor.mHeight;
				width = buttonConvertor.mWidth;
			}
			imgView.setImageBitmap(ImageManager.scaledBitmapFromResource(
					getResources(), drawAble, height, width,
					ImageManager.CACHE_IMPORTANT));

			if (mode == MODE_LAMP) {
				drawAble = R.drawable.hint;
				height = buttonConvertor.mHeight;
				width = buttonConvertor.mWidth;
				imgView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {


						if (onKeyboardEvent != null)
							onKeyboardEvent.onHintClicked();
					}
				});

			} else if (mode == MODE_ANSWER) {
				imgView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						KeyView thisView = KeyView.this;
						if (thisView.otherIndex == -1
								|| thisView.state == STATE_ANSWERED)
							return;

						int index = otherIndex;
						otherIndex = -1;
						buttons[index].state = STATE_NORMAL;
						buttons[index].imgView.setImageBitmap(ImageManager
								.scaledBitmapFromResource(
										getResources(),
										(buttons[index].type == TYPE_LEFT) ? R.drawable.button_left
												: R.drawable.button_right,
										buttonConvertor.mHeight,
										buttonConvertor.mWidth,
										ImageManager.CACHE_IMPORTANT));

						int fromX = +x - buttons[index].x;
						int fromY = +y - buttons[index].y;
						int toX = 0;
						int toY = 0;
						TranslateAnimation translateAnimation = new TranslateAnimation(
								fromX, toX, fromY, toY);
						translateAnimation.setFillAfter(true);
						translateAnimation.setDuration(400);

						float scaleX = ((float) answerConverter.mWidth)
								/ buttonConvertor.mWidth;
						float scaleY = ((float) answerConverter.mHeight)
								/ buttonConvertor.mHeight;
						ScaleAnimation scaleAnimation = new ScaleAnimation(
								scaleX, 1, scaleY, 1);
						scaleAnimation.setFillAfter(true);
						scaleAnimation.setDuration(400);

						AnimationSet set = new AnimationSet(true);
						set.setFillAfter(true);
						set.addAnimation(scaleAnimation);
						set.addAnimation(translateAnimation);

						buttons[index].charTemp.startAnimation(set);

					}
				});

				imgView.setOnTouchListener(new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						if (event.getAction() != event.ACTION_UP)
							if (index == level.answer.replace("/", "").length() - 1
									|| index == 0) {
								MotionEvent e = event.obtain(event);
								e.setLocation(
										x + event.getX(),
										yaroConvertor.mHeight - y
												+ event.getY());
								// if (event.getAction() == event.ACTION_DOWN)
								swipeView.onTouchEvent(e);

							}
						return false;
					}
				});
			}

			else if (mode == MODE_BUTTON) {
				imgView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						KeyView thisView = (KeyView) KeyView.this;
						if (thisView.state == STATE_TOUCHED
								|| thisView.state == STATE_REMOVED)
							return;

						if (getFirstEmptyIndexAnswers() != -1
								|| state == STATE_REMOVED) {
							int mIndex = getFirstEmptyIndexAnswers();
							answers[mIndex].otherIndex = index;
							otherIndex = mIndex;
							thisView.state = STATE_TOUCHED;
							thisView.imgView.setImageBitmap(ImageManager
									.scaledBitmapFromResource(
											getResources(),
											(thisView.type == TYPE_LEFT) ? R.drawable.button_left_touched
													: R.drawable.button_right_touched,
											buttonConvertor.mHeight,
											buttonConvertor.mWidth,
											ImageManager.CACHE_IMPORTANT));
							int toX = answers[mIndex].x - x;
							int toY = answers[mIndex].y - y;
							TranslateAnimation translateAnimation = new TranslateAnimation(
									0, toX, 0, toY);
							translateAnimation.setFillAfter(true);
							translateAnimation.setDuration(400);

							float scaleX = ((float) answerConverter.mWidth)
									/ buttonConvertor.mWidth;
							float scaleY = ((float) answerConverter.mHeight)
									/ buttonConvertor.mHeight;
							ScaleAnimation scaleAnimation = new ScaleAnimation(
									1, scaleX, 1, scaleY);
							scaleAnimation.setFillAfter(true);
							scaleAnimation.setDuration(400);
							if (!isCharTempAdded) {
								LayoutParams params = new LayoutParams(
										buttonConvertor.mWidth,
										buttonConvertor.mHeight);
								params.leftMargin = x;
								params.topMargin = y;
								KeyboardView.this.addView(charTemp, params);
								isCharTempAdded = true;
							}
							AnimationSet set = new AnimationSet(true);
							set.setFillAfter(true);
							set.addAnimation(scaleAnimation);
							set.addAnimation(translateAnimation);

							charTemp.startAnimation(set);

							if (isAllAnswered())
								if (onKeyboardEvent != null)
									onKeyboardEvent
											.onAllAnswered(getAllAswers());
						}

					}
				});
			}

		}

		public void setText(Character character) {
			if (this.MODE_BUTTON != mode) {
				this.character = character;
				return;
			}
			charHolder.setImageBitmap(ImageManager.scaledBitmapFromResource(
					getResources(), getDrawable(character),
					buttonConvertor.mHeight, buttonConvertor.mWidth,
					ImageManager.CACHE_IMPORTANT));

			charTemp.setImageBitmap(ImageManager.scaledBitmapFromResource(
					getResources(), getDrawable(character),
					buttonConvertor.mHeight, buttonConvertor.mWidth,
					ImageManager.CACHE_IMPORTANT));
			this.character = character;
		}

		public void setXY(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public int getDrawable(Character s) {

		if (s.equals('ص'))
			return R.drawable.sad;

		if (s.equals('ه'))
			return R.drawable.hah;
		if (s.equals('و'))
			return R.drawable.vav;
		if (s.equals('پ'))
			return R.drawable.pe;
		if (s.equals('د'))
			return R.drawable.de;
		if (s.equals('ذ'))
			return R.drawable.zee;
		if (s.equals('ر'))
			return R.drawable.re;
		if (s.equals('ز'))
			return R.drawable.ze;
		if (s.equals('ط'))
			return R.drawable.ta;
		if (s.equals('ظ'))
			return R.drawable.za;
		if (s.equals('ض'))
			return R.drawable.zad;
		if (s.equals('ک'))
			return R.drawable.ke;
		if (s.equals('ی'))
			return R.drawable.ye;
		if (s.equals('س'))
			return R.drawable.sin;
		if (s.equals('ب'))
			return R.drawable.be;

		if (s.equals('ش'))
			return R.drawable.shin;
		if (s.equals('گ'))
			return R.drawable.ge;
		if (s.equals('ج'))
			return R.drawable.j;
		if (s.equals('غ'))
			return R.drawable.ghain;
		if (s.equals('خ'))
			return R.drawable.khe;
		if (s.equals('ح'))
			return R.drawable.he;
		if (s.equals('ل'))
			return R.drawable.lam;
		if (s.equals('ف'))
			return R.drawable.fe;
		if (s.equals('ا') || s.equals('آ'))
			return R.drawable.alef;
		if (s.equals('ق'))
			return R.drawable.ghe;
		if (s.equals('چ'))
			return R.drawable.che;
		if (s.equals('ث'))
			return R.drawable.se;
		if (s.equals('ن'))
			return R.drawable.non;
		if (s.equals('ع'))
			return R.drawable.ayn;
		if (s.equals('ت'))
			return R.drawable.te;
		if (s.equals('م'))
			return R.drawable.mim;
		if (s.equals('ژ'))
			return R.drawable.czhe;

		return 0;
	}

}
