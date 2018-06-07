package com.yecon.carsetting;

import com.yecon.carsetting.unitl.Util;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.SeekBar;
import android.widget.TextView;
import static android.widget.SeekBar.OnSeekBarChangeListener;

public class SettingSize extends Activity{

	TextView showProgress;
	SeekBar seekBar;
	DrawRound drawRound;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_key_learing_setting_size);
		
	    showProgress = (TextView)findViewById(R.id.showProgress);
	    showProgress.setText(String.valueOf(Util.pos_threshold));
		drawRound = (DrawRound)findViewById(R.id.drawRound);
	    
		drawRound.invalidate();//
		seekBar = (SeekBar) findViewById(R.id.seekbar);
		seekBar.setProgress(Util.pos_threshold );
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			// 当拖动条的滑块位置发生改变时触发该方法
			@Override
			public void onProgressChanged(SeekBar arg0, int progress,
										  boolean fromUser)
			{
				showProgress.setText(String.valueOf(progress));		
				Util.pos_threshold = progress;
				drawRound.invalidate();
			}
			@Override
			public void onStartTrackingTouch(SeekBar bar)
			{
			}
			@Override
			public void onStopTrackingTouch(SeekBar bar)
			{
			}
		});
	}

	
}
