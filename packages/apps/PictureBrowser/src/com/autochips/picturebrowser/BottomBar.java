package com.autochips.picturebrowser;

import static android.mcu.McuExternalConstant.*;

import java.io.IOException;
import java.util.ArrayList;
import com.yecon.common.SourceManager;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class BottomBar {
	private Context activity;
	private View vHome, vBack;
	private TextView tvContent;
	public BottomBar(Activity activity){
		this.activity = activity;
		this.tvContent = (TextView) activity.findViewById(R.id.tv_prompt);
		tvContent.setVisibility(View.INVISIBLE);
		this.tvContent.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(BottomBar.this.activity instanceof ThumbnailsActivity){
					((ThumbnailsActivity)BottomBar.this.activity).playLast();
				}
			}
			
		});
		this.vHome = activity.findViewById(R.id.btn_home);
		if(this.vHome!=null){
			this.vHome.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Intent.ACTION_MAIN);
			        intent.addCategory(Intent.CATEGORY_HOME);
			        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
			                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			        BottomBar.this.activity.startActivity(intent);
				}
				
			});
		}
		this.vBack = activity.findViewById(R.id.btn_back);
		if(this.vBack!=null){
			this.vBack.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Runtime runtime = Runtime.getRuntime();
					try {
						runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			});
		}
	}
	
	public void init(){
		/*
		if(activity!=null){
			IntentFilter filter = new IntentFilter();
	        filter.addAction(ACTION_UPDATE_NAVI_ICON_TEXT);
	        activity.registerReceiver(mBroadcastReceiver, filter);
		}
		*/
	}
	public void release(){
		/*
		if(activity!=null){
			activity.unregisterReceiver(mBroadcastReceiver);
		}
		*/
	}
	/*
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent intent) {
			final String action = intent.getAction();
			if (ACTION_UPDATE_NAVI_ICON_TEXT.equals(action)) {
				String strNaviTitle = intent
						.getStringExtra(INTENT_EXTRA_NAVI_ICON_TEXT);
				if (strNaviTitle.equalsIgnoreCase(String
						.valueOf(MCU_ACTION_MEDIA_PLAY_PAUSE)))
					return;
				setNaviTitleInfo(strNaviTitle);
			}
		}
	};
	*/
	/*
	void setNaviTitleInfo(String strInfo) {
        ArrayList<String> stringTitle = getStringArray(strInfo, "#");
        String info_text = "";
        if (stringTitle.size() == 2) {
            int mSource = Integer.valueOf(stringTitle.get(0));
            info_text = stringTitle.get(1);
            // mSource = Integer.valueOf(strInfo.split("#")[0]);
            // String info_text = strInfo.split("#")[1];
            switch (mSource) {
                case NAVI_TYPE_PHOTO:
                	//tvContent.setBackgroundResource(R.drawable.ic_bottombar_play_bg);
                    break;
                default:
                	//tvContent.setBackgroundResource(R.color.transparent);
                	info_text = "";
                    break;
            }

            tvContent.setText(info_text);
            if (info_text.equalsIgnoreCase("")) {
            	tvContent.setBackgroundResource(R.color.transparent);
            	tvContent.setVisibility(View.INVISIBLE);
            } else {
            	tvContent.setVisibility(View.VISIBLE);
            }
        } else {
        	tvContent.setBackgroundResource(R.color.transparent);
        	tvContent.setText("");
        	tvContent.setVisibility(View.INVISIBLE);
        }
    }
	*/
	public static void notifyNavigator(Context context, String content){
		if(context!=null){
	    	if(canUpdateNavigator()){
				/*
				Intent intent = new Intent(ACTION_UPDATE_NAVI_ICON_TEXT);
				intent.putExtra(INTENT_EXTRA_NAVI_ICON_TEXT,  NAVI_TYPE_PHOTO+"#"+content);
				context.sendBroadcast(intent);
				*/
	    	}
		}
	}
	
	public static boolean canUpdateNavigator(){
    	int srcNo = SourceManager.lastSource();
    	return ((srcNo == SourceManager.SRC_NO.photo)
    			|| (srcNo == -1));
    }
	
	public static ArrayList<String> getStringArray(String value,String strCheck) {

        int nStartPos = 0;
        int nPos = 0;

        ArrayList<String> list = new ArrayList<String>();
        while (true) {
            nPos = value.indexOf(strCheck, nStartPos);
            if (nPos < nStartPos) {
                String strTmp = value.substring(nStartPos);
                list.add(strTmp);
                break;
            } else {
                String strTmp = value.substring(nStartPos, nPos);
                list.add(strTmp);
                nStartPos = nPos + 1;
            }
        }

        return list;
    }
}
