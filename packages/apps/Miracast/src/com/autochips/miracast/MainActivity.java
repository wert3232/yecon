package com.autochips.miracast;

import com.yecon.common.SourceManager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class MainActivity extends Activity implements OnClickListener {
	View layoutStart, layoutMain;
	Handler uiHandler = new Handler();
	private Animation mAnimAlphar;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.main);
		new BottomBar(this);
		
		layoutStart = this.findViewById(R.id.layout_start);
		layoutMain = this.findViewById(R.id.layout_main);
		this.findViewById(R.id.tvUsb).setOnClickListener(this);
		this.findViewById(R.id.tvWifi).setOnClickListener(this);
		this.findViewById(R.id.tvIphone).setOnClickListener(this);
		mAnimAlphar = AnimationUtils.loadAnimation(this, R.anim.alphar);
		uiHandler.postDelayed(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				layoutMain.setVisibility(View.VISIBLE);
				layoutMain.startAnimation(mAnimAlphar);
			}
			
		}, 2000);

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//SourceManager.acquireSource(this, SourceManager.SRC_NO.phonelink);
		Intent intent = new Intent(SourceManager.ACTION_PHONE_LINK_RESUME);
		intent.putExtra("flag", "main");
        sendBroadcast(intent);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mAnimAlphar.cancel();
		uiHandler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {

		Intent it;
		switch(arg0.getId()){
		case R.id.tvUsb:
			it = new Intent();
			it.setComponent(new ComponentName("com.phonelink.main","com.phonelink.main.PlActivity"));
			startActivity(it);
			break;
		case R.id.tvWifi:
			it = new Intent(MainActivity.this, MiracastActivity.class);
			startActivity(it);
			break;
		case R.id.tvIphone:
			it = new Intent();
			it.setComponent(new ComponentName("com.phonelink.iplay","com.phonelink.iplay.MainActivity"));
			startActivity(it);
			break;
		}
	}

}
