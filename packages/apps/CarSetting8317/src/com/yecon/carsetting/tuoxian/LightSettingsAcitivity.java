package com.yecon.carsetting.tuoxian;

import com.tuoxianui.tools.AtTimerHelpr;
import com.yecon.carsetting.R;
import com.yecon.carsetting.fragment.Fragment_List;
import com.yecon.carsetting.view.MyRadioGroup;
import com.yecon.carsetting.view.MyRadioGroup.OnCheckedChangeListener;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class LightSettingsAcitivity extends Activity implements OnClickListener,OnCheckedChangeListener{
	
	private FragmentManager mFragmentManager;
	private Fragment_List mDialog;
	private MyRadioGroup rg;
	private static Context mContext;
	private SharedPreferences.Editor mEditor ;
	private SharedPreferences mPref;
	private AtTimerHelpr mAtTimerHelpr;
	private int normalValue = 0x9f;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		setContentView(R.layout.activity_tuoxian_light_settings);
		initView();
		
	}
	private void initData() {
		mContext = this;
		mFragmentManager = getFragmentManager();	
		mPref = this.getSharedPreferences("light_settings", MODE_PRIVATE);
		mEditor = mPref.edit();
		normalValue = mPref.getInt("light_level", 0x9f);
		setScreenBrightness(normalValue);
	}
	private void initView() {
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.img_light_level_1).setOnClickListener(this);
		findViewById(R.id.img_light_level_2).setOnClickListener(this);
		findViewById(R.id.img_light_level_3).setOnClickListener(this);
		findViewById(R.id.img_light_level_4).setOnClickListener(this);
		rg = (MyRadioGroup)this.findViewById(R.id.radioGroup);
		rg.setOnCheckedChangeListener(this);
		switch (normalValue) {
			case 0x10:{
					rg.check(R.id.light_level_1);
				}
				break;
			case 0x5f:{
					rg.check(R.id.light_level_2);
				}
				break;
			case 0x9f:{
					rg.check(R.id.light_level_3);
				}
				break;
			case 0xdf:{
					rg.check(R.id.light_level_4);
				}
				break;
			default:
				break;
		}
		
		
		//一汽要求,不自动流转 mAtTimerHelpr = new AtTimerHelpr();
		AtTimerHelpr.AtTimerHelprDoItCallBack atTimerHelprDoItCallBack = new AtTimerHelpr.AtTimerHelprDoItCallBack() {
			@Override
			public void doIt() {
				backHandler.sendEmptyMessage(0);
			}
		};
		//一汽要求,不自动流转 mAtTimerHelpr.setCallBack(atTimerHelprDoItCallBack);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.img_light_level_1:
					rg.check(R.id.light_level_1);
		break;
		case R.id.img_light_level_2:
					rg.check(R.id.light_level_2);
		break;
		case R.id.img_light_level_3:
					rg.check(R.id.light_level_3);
		break;
		case R.id.img_light_level_4:
					rg.check(R.id.light_level_4);
		break;
		default:
			break;
		}
	}
	@Override
	public void onCheckedChanged(MyRadioGroup group, int checkedId) {
		switch (checkedId) {
			case R.id.light_level_1:
				mEditor.putInt("light_level", 0x10).commit();
				setScreenBrightness(0x10);
				break;
			case R.id.light_level_2:
				mEditor.putInt("light_level", 0x5f).commit();
				setScreenBrightness(0x5f);
				break;
			case R.id.light_level_3:
				mEditor.putInt("light_level", 0x9f).commit();
				setScreenBrightness(0x9f);
				break;
			case R.id.light_level_4:
				mEditor.putInt("light_level", 0xdf).commit();
				setScreenBrightness(0xdf);
				break;
			default:
				break;
		}
	}
	
	private void setScreenBrightness(int brightness){
		Intent intent = new Intent("action.set.backlight");
		intent.putExtra("brightness",brightness);
		sendBroadcast(intent);
	}
	
	Handler backHandler = new Handler() {
		public void handleMessage(Message msg) {
			onBackPressed();
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		//一汽要求,不自动流转 mAtTimerHelpr.start(10);
	}
	@Override
	protected void onPause() {
		super.onPause();
		//一汽要求,不自动流转 mAtTimerHelpr.stop();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//一汽要求,不自动流转 mAtTimerHelpr.reset();
		return super.onTouchEvent(event);
	}
	
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//一汽要求,不自动流转 mAtTimerHelpr.reset();
		return super.dispatchTouchEvent(ev);
	}

}
