package com.can.ui;

import com.can.activity.R;
import com.can.assist.CanXml;
import com.can.services.CanTxRxStub;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

/**
 * ClassName:CanSource
 * 
 * @function:Can带音频的源
 * @author Kim
 * @Date:  2016-7-11上午10:12:08
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanSource extends Activity{

	protected final String TAG = this.getClass().getName();
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_source);
		
		CanFrament objFrament = CanXml.getAudio(this);
		
		if (objFrament != null) {
			ShowPage(objFrament, getIntent().getStringExtra(CanTxRxStub.CAN_MODE));
		} else {
			Log.i(TAG, "get page is empty!");
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	/**
	 * 
	* Title: ShowPage
	* Description: 显示页面
	* @param fragment
	* @param strTag
	* @return void
	 */
	private void ShowPage(Fragment fragment, String strTag) {
		FragmentManager Objmanager = getFragmentManager();
		FragmentTransaction ObjTransaction = Objmanager.beginTransaction();
		ObjTransaction.add(R.id.fragment_source_container, fragment, strTag);
		ObjTransaction.commit();
	}
}
