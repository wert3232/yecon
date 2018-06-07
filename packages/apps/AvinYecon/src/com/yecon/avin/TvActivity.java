package com.yecon.avin;

import com.yecon.avin.R;
import com.yecon.avin.R.id;

import android.app.Activity;
import android.content.Intent;
import android.exdevport.AtvPort;
import android.exdevport.I2cPort;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class TvActivity extends Activity{
	protected static final String TAG = "TvActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String tvType = SystemProperties.get(AVINConstant.PERSYS_TV_TYPE);
		Log.i(TAG, "tvType "+tvType);
		if(tvType==null){
			tvType = AVINConstant.support_tv_type[0];
		}
		
		//for test
		//Intent it = new Intent(this, DTvActivity.class);
		//startActivity(it);
		//
		
		if(tvType.equals(AVINConstant.support_tv_type[0])){
			Intent it = new Intent(this, ATvActivity.class);
			startActivity(it);
		}
		else{
			Intent it = new Intent(this, DTvActivity.class);
			startActivity(it);
		}
		
		finish();
	}
	

	
}
