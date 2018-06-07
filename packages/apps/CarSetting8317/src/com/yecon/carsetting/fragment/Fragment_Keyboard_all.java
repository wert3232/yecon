package com.yecon.carsetting.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.HideReturnsTransformationMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.yecon.carsetting.R;

public class Fragment_Keyboard_all extends DialogFragment implements OnClickListener {

	private Context mContext;

	int ID_EditText[] = { R.id.key_edit };
	int ID_TextView[] = { R.id.key_1, R.id.key_2, R.id.key_3, R.id.key_4, R.id.key_5, R.id.key_6,
			R.id.key_7, R.id.key_8, R.id.key_9, R.id.key_0, R.id.key_q, R.id.key_w, R.id.key_e,
			R.id.key_r, R.id.key_t, R.id.key_y, R.id.key_u, R.id.key_i, R.id.key_o, R.id.key_p,
			R.id.key_a, R.id.key_s, R.id.key_d, R.id.key_f, R.id.key_g, R.id.key_h, R.id.key_j,
			R.id.key_k, R.id.key_l, R.id.key_comma, R.id.key_z, R.id.key_x, R.id.key_c, R.id.key_v,
			R.id.key_b, R.id.key_n, R.id.key_m, R.id.key_del, R.id.key_capsloc, R.id.key_enter };

	char CharSet[] = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'Q', 'W', 'E', 'R', 'T',
			'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', ',', 'Z', 'X',
			'C', 'V', 'B', 'N', 'M' };

	boolean isUpCase = true;
	TextView textView[] = new TextView[ID_TextView.length];
	EditText editText;

	StringBuffer mStrBuf = new StringBuffer();

	int mID;

	public Fragment_Keyboard_all() {

	}

	public Fragment_Keyboard_all(int id) {
		mID = id;
	}

	public Fragment_Keyboard_all(OnKeyboardAllListener customListener) {
		mKeyboardListener = customListener;
	}

	private void initData() {
		mContext = getActivity();
	}

	private void initView(View rootView) {

		int i = 0;
		for (TextView view : textView) {
			textView[i] = (TextView) rootView.findViewById(ID_TextView[i]);
			if (i < CharSet.length)
				textView[i].setText(String.valueOf(CharSet[i]));
			textView[i].setOnClickListener(this);
			i++;
		}

		editText = (EditText) rootView.findViewById(ID_EditText[0]);
		// show password
		editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Bitmap bk = BitmapFactory.decodeResource(getResources(), R.drawable.keyboard_all_bk);
		Window window = getDialog().getWindow();
		window.setLayout(bk.getWidth(), bk.getHeight());

		final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
		layoutParams.gravity = Gravity.BOTTOM;
		getDialog().getWindow().setAttributes(layoutParams);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomizeActivityTheme);
		initData();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.fragment_keyboard_all, container, false);
		initView(mRootView);
		return mRootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void toLowerCase() {
		int i = 0;
		for (char ch : CharSet) {
			textView[i].setText(String.valueOf(CharSet[i]).toLowerCase());
			i++;
		}
	}

	private void toUpperCase() {
		int i = 0;
		for (char ch : CharSet) {
			textView[i].setText(String.valueOf(CharSet[i]).toUpperCase());
			i++;
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int i = 0;
		for (TextView tv : textView) {
			if (view.getId() == ID_TextView[i] && i < CharSet.length) {
				mStrBuf.setLength(0);
				mStrBuf.append(editText.getText().toString()).append(textView[i].getText());
				editText.setText(mStrBuf);
				setEditTextCursorLocation(editText);
			} else if (view.getId() == ID_TextView[CharSet.length]) {
				int len = editText.getText().length();
				if (len > 0)
					mStrBuf.setLength(len - 1);
				editText.setText(mStrBuf);
				setEditTextCursorLocation(editText);
				return;
			} else if (view.getId() == ID_TextView[CharSet.length + 1]) {
				if (isUpCase)
					toLowerCase();
				else
					toUpperCase();
				isUpCase = !isUpCase;
				return;
			} else if (view.getId() == ID_TextView[CharSet.length + 2]) {
				dismiss();
				if (mKeyboardListener != null && !editText.getText().toString().equals(""))
					mKeyboardListener.back(mID, editText.getText().toString());
				return;
			}
			i++;
		}

	}

	public void setEditTextCursorLocation(EditText editText) {
		CharSequence text = editText.getText();
		if (text instanceof Spannable) {
			Spannable spanText = (Spannable) text;
			Selection.setSelection(spanText, text.length());
		}
	}

	private OnKeyboardAllListener mKeyboardListener;

	public void setOnKeyboardAllListener(OnKeyboardAllListener mListener) {
		mKeyboardListener = mListener;
	}

	// 定义dialog的回调事件
	public interface OnKeyboardAllListener {
		void back(int id, String str);
	}

}