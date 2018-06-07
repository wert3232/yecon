package com.can.ui.ford;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.can.activity.R;
import com.can.ui.CanFrament;

/**
 * ClassName:CanFordCarInfo
 * 
 * @function:TODO
 * @author Kim
 * @Date:  2016-7-12上午10:40:57
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanFordCarInfo extends CanFrament implements OnClickListener{

	private View mObjView = null;
	private CanFordCarSet mObjCanFordCarSet = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (mObjView == null) {
			mObjView = inflater.inflate(R.layout.ford_menu, container, false);
			init();
		}

		return mObjView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	private void init(){
		if (mObjView != null) {

			mObjCanFordCarSet = new CanFordCarSet();
			
			mObjView.findViewById(R.id.btn_ford_menu_sync).setOnClickListener(this);
			mObjView.findViewById(R.id.btn_ford_menu_carset).setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_ford_menu_sync:
			super.showAudioPage();
			break;
		case R.id.btn_ford_menu_carset:
			super.showPage(mObjCanFordCarSet);
			break;
		default:
			break;
		}
	}
}
