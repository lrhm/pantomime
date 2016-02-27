package com.irpulse.lamp;

import com.irpulse.Utilities.SizeManager;
import com.irpulse.Utilities.Utils;

import com.irpulse.lamp.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class HelpDialog extends DialogFragment {

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		dismiss();
	}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		Dialog mDialog = new Dialog(getActivity(), R.style.Theme_Dialog);
		mDialog.setTitle(null);
		setCancelable(true);

		mDialog.setCanceledOnTouchOutside(true);
		mDialog.setContentView(R.layout.help_dialog);

		TextView coinText = (TextView) mDialog
				.findViewById(R.id.help_dialog_text);
		coinText.setTextColor(Color.WHITE);
		coinText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		coinText.setTypeface(Utils.getFont());

		String text = "شما باید در هر مرحله عبارتی را که فرد انجام می دهد حدس بزنید و در جای جواب بنویسید، برای مثال جواب این مرحله می شود";// "شما باید در هر مرحله عبارتی را که فرد انجام می دهد حدس بزنید و در جای جواب بنویسید، برای مثال جواب این مرحله می شود \"گل یا پوچ\"";
		text += "\n";
		text += "\"گل یا پوچ\"";
		text += "\n";
		text += "با لمس علامت لامپ می توانید از راهنمایی ها استفاده کنید";
		text += ".\n";
		text += "برای مرور دوباره ی مرحله روی شخص کلیک کنید";

		text += ".";
		coinText.setSingleLine(false);
		coinText.setText(text);
		coinText.setGravity(Gravity.CENTER);

		mDialog.findViewById(R.id.help_dialog_container).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dismiss();
					}
				});

		return mDialog;
	}
}
