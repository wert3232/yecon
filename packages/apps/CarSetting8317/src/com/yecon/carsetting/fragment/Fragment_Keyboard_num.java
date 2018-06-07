package com.yecon.carsetting.fragment;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.yecon.carsetting.R;
import com.yecon.carsetting.unitl.Tag;

public class Fragment_Keyboard_num extends DialogFragment implements OnClickListener {

	private Context mContext;

	int ID_EditText[] = { R.id.key_edit };
	int ID_TextView[] = { R.id.key_0, R.id.key_1, R.id.key_2, R.id.key_3, R.id.key_4, R.id.key_5,
			R.id.key_6, R.id.key_7, R.id.key_8, R.id.key_9, R.id.key_del, R.id.key_enter };
	char CharSet[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	TextView textView[] = new TextView[12];
	EditText editText;

	StringBuffer mStrBuf = new StringBuffer();

	int mID;

	public Fragment_Keyboard_num() {

	}

	public Fragment_Keyboard_num(int id) {
		mID = id;
	}

	public Fragment_Keyboard_num(OnKeyboardListener customListener) {
		mKeyboardListener = customListener;
	}

	private void initData() {
		mContext = getActivity();
	}

	private void initView(View rootView) {

		int i = 0;
		for (TextView view : textView) {
			view = (TextView) rootView.findViewById(ID_TextView[i]);
			view.setOnClickListener(this);
			i++;
		}

		editText = (EditText) rootView.findViewById(ID_EditText[0]);
		// hide password
		editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Bitmap bk = BitmapFactory.decodeResource(getResources(), R.drawable.keyboard_bk);
		Window window = getDialog().getWindow();
		window.setLayout(bk.getWidth(), bk.getHeight());
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
		View mRootView = inflater.inflate(R.layout.fragment_keyboard_num, container, false);
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

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int i = 0;
		for (TextView tv : textView) {
			if (view.getId() == ID_TextView[i] && i < CharSet.length) {
				mStrBuf.setLength(0);
				mStrBuf.append(editText.getText().toString()).append(String.valueOf(CharSet[i]));
				editText.setText(mStrBuf);
				// editText.setSelection(editText.getText().length());
				setEditTextCursorLocation(editText);
			} else if (view.getId() == ID_TextView[CharSet.length]) {
				int len = editText.getText().length();
				if (len > 0)
					mStrBuf.setLength(len - 1);
				editText.setText(mStrBuf);
				setEditTextCursorLocation(editText);
				return;
			} else if (view.getId() == ID_TextView[CharSet.length + 1]) {
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

	private OnKeyboardListener mKeyboardListener;

	public void setOnKeyboardListener(OnKeyboardListener mListener) {
		mKeyboardListener = mListener;
	}

	// 定义dialog的回调事件
	public interface OnKeyboardListener {
		void back(int id, String str);
	}
}