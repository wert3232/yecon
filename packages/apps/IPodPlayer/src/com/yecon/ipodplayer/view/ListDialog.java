package com.yecon.ipodplayer.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class ListDialog extends Dialog{

	public ListDialog(Context context) {
		super(context);
		 
	}

	@Override
	public void addContentView(View view, LayoutParams params) {
		 
		super.addContentView(view, params);
	}

	@Override
	public void setContentView(int layoutResID) {
		 
		super.setContentView(layoutResID);
	}

	@Override
	public void setOnCancelListener(OnCancelListener listener) {
		 
		super.setOnCancelListener(listener);
	}

}
