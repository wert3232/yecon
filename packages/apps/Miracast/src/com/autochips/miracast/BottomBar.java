package com.autochips.miracast;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class BottomBar{
	
	private Activity activity;
	private View  vPlay, vHome, vBack;
	private TextView tvContent;
	
	public BottomBar(Activity activity){
		this.activity = activity;
		this.tvContent = (TextView) activity.findViewById(R.id.tv_prompt);
	
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
					//runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
					BottomBar.this.activity.finish();
				}
				
			});
		}
	}
	
	
}
