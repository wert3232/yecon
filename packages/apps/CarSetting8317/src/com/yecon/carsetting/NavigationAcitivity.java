package com.yecon.carsetting;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class NavigationAcitivity extends Activity {

	private Context mContext;
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("")) {
			}
		}
	};

	private void initData() {
		mContext = this;
		IntentFilter filter = new IntentFilter();
		filter.addAction("");
		mContext.registerReceiver(mBroadcastReceiver, filter);
	}

	private void setWindowPara() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeigh = dm.heightPixels;
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount = 0.0f;
		lp.alpha = 1.0f;
		lp.x = 0;
		lp.y = 0;
		lp.width = screenWidth;
		lp.height = screenHeigh;
		getWindow().setAttributes(lp);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.fragment_navigation);
		setWindowPara();
		initData();
		initView();
		onNavigation();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				finish();
			}
		}, 1000);
	}
	
	


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	private void initView() {
	}

	private void onNavigation() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		String packageName = null;
		String className = null;
		String mapPackage = SystemProperties.get("persist.sys.maps", "nothing");
		if (!mapPackage.equals("nothing")) {
			packageName = mapPackage.split("#")[0];
			className = mapPackage.split("#")[1];
			intent.setComponent(new ComponentName(packageName, className));
			startActivity(intent);
		} else {
			intent = new Intent(mContext, NavigationsetActivity.class);
			startActivity(intent);
		}
	}

}
