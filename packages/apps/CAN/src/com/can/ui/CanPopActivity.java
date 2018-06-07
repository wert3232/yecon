package com.can.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import com.can.activity.R;
import com.can.assist.CanXml;

public class CanPopActivity extends Activity{
protected final String TAG = this.getClass().getName();
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_pop_can);
		CanFrament objFrament = CanXml.getPopFrament(this);
		if (objFrament != null) {
			ShowPage(objFrament, objFrament.getClass().getName());
		} else {
			Log.i(TAG, "get page is empty!");
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
	private void ShowPage(Fragment fragment, String strTag) {
		FragmentManager Objmanager = getFragmentManager();
		FragmentTransaction ObjTransaction = Objmanager.beginTransaction();
		ObjTransaction.add(R.id.fragment_pop_container, fragment, strTag);
		ObjTransaction.commit();
	}
}
