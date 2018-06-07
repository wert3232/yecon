package com.yecon.carsetting.tuoxian;

import java.util.Locale;

import com.android.internal.app.LocalePicker;
import com.tuoxianui.tools.AtTimerHelpr;
import com.yecon.carsetting.R;
import com.yecon.carsetting.fragment.Fragment_List;
import com.yecon.carsetting.unitl.L;
import com.yecon.carsetting.view.MyRadioGroup;
import com.yecon.carsetting.view.MyRadioGroup.OnCheckedChangeListener;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.os.SystemProperties;
public class LanguageAcitivity extends Activity implements OnClickListener,OnCheckedChangeListener{
	
	private FragmentManager mFragmentManager;
	private Fragment_List mDialog;
	private MyRadioGroup rg;
	private static Context mContext;
	private int index_language;
	private AtTimerHelpr mAtTimerHelpr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		setContentView(R.layout.activity_tuoxian_language);
		initView();
	}

	private void initData() {
		mContext = this;
		mFragmentManager = getFragmentManager();	
		
		String language = getResources().getConfiguration().locale.getLanguage();
		if (language.equals("zh")) {
			if(getResources().getConfiguration().locale.getCountry().equals("TW")){
				index_language = 1;
			}else{
				index_language = 0;
			}
		}  else if (language.equals("en")) {
			index_language = 2;
		}
	}
	private void initView() {
		findViewById(R.id.back).setOnClickListener(this);
		rg = (MyRadioGroup)this.findViewById(R.id.radioGroup);
		rg.setOnCheckedChangeListener(this);
		
		switch (index_language) {
			case 0:{
					rg.check(R.id.cn_simplified);
				}
				break;
			case 2:{
					rg.check(R.id.english);
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
		default:
			break;
		}
	}
	@Override
	public void onCheckedChanged(MyRadioGroup group, int checkedId) {
		switch (checkedId) {
			case R.id.cn_simplified:{	
				index_language = 0;
				final Locale l = new Locale("zh","CN");
				LocalePicker.updateLocale(l);
				SystemProperties.set("persist.sys.hzh.language","zh");
				SystemProperties.set("persist.sys.hzh.country","CN");
				SystemProperties.set("persist.sys.hzh.locale.change","true");
					String IMEName="com.android.inputmethod.pinyin/.PinyinIME";
					android.provider.Settings.Secure.putString( mContext.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, IMEName );
			}
				
				break;
			case R.id.english:{		
				index_language = 2;
				final Locale l = new Locale("en","US");
				LocalePicker.updateLocale(l);
				SystemProperties.set("persist.sys.hzh.language","en");
				SystemProperties.set("persist.sys.hzh.country","US");
				SystemProperties.set("persist.sys.hzh.locale.change","true");
					String IMEName="com.android.inputmethod.latin/.LatinIME";
					android.provider.Settings.Secure.putString( mContext.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, IMEName );
			}
				break;
			default:
				break;
		}
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
