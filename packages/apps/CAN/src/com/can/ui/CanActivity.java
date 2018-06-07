package com.can.ui;

import com.can.activity.R;
import com.can.assist.CanXml;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

/**
 * ClassName:CanActivity
 * 
 * @function:Can信息显示主Activity
 * @author Kim
 * @Date: 2016-5-26 上午11:52:11
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanActivity extends Activity {

	public static String mStrFragment = "";
	protected final String TAG = this.getClass().getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_can);

		CanFrament objFragment = CanXml.create(getApplicationContext());

		if (objFragment != null) {
			ShowPage(objFragment, objFragment.getClass().getName());
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
	 * Description: 显示对应页面
	 * @param fragment
	 * @param strTag
	 */
	private void ShowPage(Fragment fragment, String strTag) {
		FragmentManager Objmanager = getFragmentManager();
		FragmentTransaction ObjTransaction = Objmanager.beginTransaction();
		ObjTransaction.add(R.id.fragment_container, fragment, strTag);
		ObjTransaction.commit();
	}
}
