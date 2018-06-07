package com.yecon.carsetting.fragment;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.yecon.carsetting.R;
import com.yecon.carsetting.fragment.Fragment_Keyboard_num.OnKeyboardListener;

public class Fragment_Prompt_dialog extends DialogFragment implements OnClickListener {

	private Context mContext;

	int ID_TextView[] = { R.id.textview_title, R.id.textview_yes, R.id.textview_no };
	TextView textView[] = new TextView[3];
	String strTitle;
	private Drawable imageTitle;
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("")) {
			}
		}
	};

	private void initData() {
		mContext = getActivity();
		IntentFilter filter = new IntentFilter();
		mContext.registerReceiver(mBroadcastReceiver, filter);
	}

	public Fragment_Prompt_dialog(String str) {
		strTitle = str;
	}
	public Fragment_Prompt_dialog(String str,Drawable drawable) {
		strTitle = str;
		imageTitle = drawable;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Bitmap bk = BitmapFactory.decodeResource(getResources(), R.drawable.dialog_prompt_bk);
		Window window = getDialog().getWindow();
		window.setLayout(bk.getWidth(), bk.getHeight());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomizeActivityTheme);
		initData();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.fragment_prompt_dialog, container, false);
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
		mContext.unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	private void initView(View rootView) {
		int i = 0;
		for (TextView tv : textView) {
			textView[i] = (TextView) rootView.findViewById(ID_TextView[i]);
			textView[i].setOnClickListener(this);
			i++;
		}
		textView[0].setText(strTitle);
		if(imageTitle != null){
			textView[0].setCompoundDrawablesWithIntrinsicBounds(imageTitle, null, null, null);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0.getId() == ID_TextView[1]) {
			if (mConfirmListener != null)
				mConfirmListener.onConfirm();
			dismiss();
		} else if (arg0.getId() == ID_TextView[2]) {
			dismiss();
		}

	}

	private OnConfirmListener mConfirmListener;

	public void setOnConfirmListener(OnConfirmListener mListener) {
		mConfirmListener = mListener;
	}

	// 定义dialog的回调事件
	public interface OnConfirmListener {
		void onConfirm();
	}

}