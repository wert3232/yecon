package com.yecon.carsetting.tuoxian;


import com.tuoxianui.tools.AtTimerHelpr;
import com.yecon.carsetting.R;
import com.yecon.carsetting.fragment.Fragment_List;
import com.yecon.carsetting.fragment.Fragment_Prompt_dialog;
import com.yecon.carsetting.fragment.Fragment_Prompt_dialog.OnConfirmListener;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.XmlRW;
import com.yecon.carsetting.unitl.tzutil;
import static android.mcu.McuExternalConstant.*;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.mcu.McuManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.SystemProperties;
public class TFactroySettingsActivity extends Activity implements OnClickListener{
	private FragmentManager mFragmentManager;
	private Fragment_List mDialog;
	private static Context mContext;
	private AtTimerHelpr mAtTimerHelpr;
	private SharedPreferences.Editor mEditor ;
	private SharedPreferences mPref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		setContentView(R.layout.activity_tuoxian_factroy_setting);
		mPref = this.getSharedPreferences("light_settings", MODE_PRIVATE);
		mEditor = mPref.edit();
		initView();
		
	}
	
	
	private void initView() {
		findViewById(R.id.factory_reset_def).setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		
		//一汽要求,不自动流转  mAtTimerHelpr = new AtTimerHelpr();
		/** 一汽要求,不自动流转 AtTimerHelpr.AtTimerHelprDoItCallBack atTimerHelprDoItCallBack = new AtTimerHelpr.AtTimerHelprDoItCallBack() {
			@Override
			public void doIt() {
				backHandler.sendEmptyMessage(0);
			}
		};**/
		//一汽要求,不自动流转  mAtTimerHelpr.setCallBack(atTimerHelprDoItCallBack);
	}


	private void initData() {
		mFragmentManager = getFragmentManager();
		mContext = this;
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.factory_reset_def:
			Fragment_Prompt_dialog dialog = Function.onSet_Prompt_dialog(mContext,
					mFragmentManager, getResources().getString(R.string.factory_reset_def_confirm),
					getResources().getDrawable(R.drawable.ban));
			dialog.setOnConfirmListener(new OnConfirmListener() {
				@Override
				public void onConfirm() {
					// TODO Auto-generated method stub
					mEditor.putInt("light_level", 0x9f).commit();
					Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,0x9f);
					if(SystemProperties.get("persist.sys.rear.mute").equals("1"))
						try {
							// mMcuManager.RPC_SetVolumeMute(true);
							byte[] param = new byte[4];
							param[0] = (byte) 0x00;
							param[1] = (byte) 0x00;
							param[2] = (byte) 0x00;
							param[3] = (byte) 0x00;
							McuManager mMcuManager = (McuManager) getSystemService(Context.MCU_SERVICE);
							mMcuManager.RPC_KeyCommand(K_REAR_MUTE, param);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					//SystemProperties.set("persist.sys.hzh_factroy_reset","" + 1);
					SystemProperties.set("persist.sys.showtime","1");
					XmlRW.readXMLFile(Tag.XML_PATH_DEF);
					mHandler.sendEmptyMessage(Tag.HANDLER_WRITE_XMLFILE);
					Function.RebootSystem(mContext, mFragmentManager);
				}
			});
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}
	
	public static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (Tag.HANDLER_RESET_FACTORY == msg.what) {
				tzutil.ResetFactory(mContext);
			} else if (Tag.HANDLER_WRITE_XMLFILE == msg.what) {
				XmlRW.writeXMLFile(Tag.XML_PATH_USER);
				if(mContext != null){
					String language = mContext.getResources().getConfiguration().locale.getLanguage();
					String country = mContext.getResources().getConfiguration().locale.getCountry();
					XmlRW.setSystemProperties(language,country);
					String IMEName="com.android.inputmethod.latin/.LatinIME";
					if(language.equals("zh"))
						IMEName = "com.android.inputmethod.pinyin/.PinyinIME";
					android.provider.Settings.Secure.putString( mContext.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, IMEName );
				}else{
					XmlRW.setSystemProperties();
				}
			}
		}
	};
	
	Handler backHandler = new Handler() {
		public void handleMessage(Message msg) {
			onBackPressed();
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		//一汽要求,不自动流转  mAtTimerHelpr.start(10);
	}
	@Override
	protected void onPause() {
		super.onPause();
		//一汽要求,不自动流转  mAtTimerHelpr.stop();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//一汽要求,不自动流转  mAtTimerHelpr.reset();
		return super.onTouchEvent(event);
	}
	
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//一汽要求,不自动流转 mAtTimerHelpr.reset();
		return super.dispatchTouchEvent(ev);
	}
}
