package com.yecon.memoryflashtest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, OnCheckedChangeListener{
	private static final String TAG = "MainActivity";
	private MainTask mainTask;
	private MainTask exitTask;
	private TextView tvContent , tvDescription;
	private ScrollView svContent;
	private RadioGroup rg;
	private RadioButton rb1, rb2, rb3;
	private Button btStart, btStop, btReturn;
	private TextView tvLoop, tvStartTime, tvTotalTime;
	private View layout_info;
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
	private String startTime = "";
	private long startTimeMillis = 0;
	private static final int MSG_UPDATE_TIME = 0;
	private int crRed, crGreen;
	
	private Handler uiHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case MSG_UPDATE_TIME:
				updateTotalTime();
				this.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 1000);
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.layout_main);
		btStart = (Button) this.findViewById(R.id.btStart);
		btStart.setOnClickListener(this);
		btStop = (Button) this.findViewById(R.id.btStop);
		btStop.setOnClickListener(this);
		btReturn = (Button) this.findViewById(R.id.btReturn);
		btReturn.setOnClickListener(this);
		tvContent = (TextView)findViewById(R.id.tvContent);
		svContent = (ScrollView)findViewById(R.id.svContent);
		tvDescription = (TextView)findViewById(R.id.tvDescription);
		layout_info = findViewById(R.id.layout_info);
		tvLoop = (TextView)findViewById(R.id.tvLoop);
		tvStartTime = (TextView)findViewById(R.id.tvStartTime);
		tvTotalTime = (TextView)findViewById(R.id.tvTotalTime);
		rg = (RadioGroup)findViewById(R.id.rg);
		rb1 = (RadioButton)findViewById(R.id.rb1);
		rb1.setOnCheckedChangeListener(this);
		rb2 = (RadioButton)findViewById(R.id.rb2);
		rb2.setOnCheckedChangeListener(this);
		rb3 = (RadioButton)findViewById(R.id.rb3);
		rb3.setOnCheckedChangeListener(this);
		
		svContent.setVisibility(View.GONE);
		rb1.setChecked(true);		
		
		layout_info.setVisibility(View.INVISIBLE);
		
		crRed = this.getResources().getColor(R.color.red);
		crGreen = this.getResources().getColor(R.color.green);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		uiHandler.removeMessages(MSG_UPDATE_TIME);
		if(mainTask!=null){
			mainTask.cancel(true);
			mainTask=null;
		}
		MyLog.closeLogFile();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btStart:
			if(mainTask!=null && mainTask.getStatus() != AsyncTask.Status.FINISHED){
				break;
			}
			mainTask = new MainTask();
			if(rb1.isChecked()){
				mainTask.execute("/system/bin/memtest -m mf 500");
			}
			else if(rb2.isChecked()){
				mainTask.execute("/system/bin/memtest   -m m 500");
			}
			else if(rb3.isChecked()){
				mainTask.execute("/system/bin/memtest  -m f 500");
			}
			break;
		case R.id.btStop:
			if(mainTask!=null){
				mainTask.cancel(true);
				mainTask=null;
			}
			break;
		case R.id.btReturn:
			if(mainTask!=null){
				mainTask.cancel(true);
				mainTask=null;
			}
			finish();
			break;
		}
	}
	
	private void updateLoopView(String loopStr, boolean error){
		if(loopStr == null){
			layout_info.setVisibility(View.INVISIBLE);
			return;
		}
		layout_info.setVisibility(View.VISIBLE);
		tvLoop.setText(loopStr);
		tvStartTime.setText(startTime);		
		if(error){
			layout_info.setBackgroundColor(crRed);
		}
		else{
			layout_info.setBackgroundColor(crGreen);
		}
	}
	
	private void updateTotalTime(){
		if(layout_info.getVisibility()!=View.VISIBLE){
			return;
		}
		long time = SystemClock.uptimeMillis()-startTimeMillis;
		String totalTime = "";
		if(time>=0){
			time /= 1000;
			int hour = (int)(time/3600);
			int minute = (int)(time/60)%60;
			int second = (int)(time%60);
			totalTime = String.format("%02d:%02d:%02d", hour, minute, second);
		}
		tvTotalTime.setText(totalTime);
	}
	
	class MainTask extends AsyncTask<Object, String, Object>{
		
		private void postLogInfo(String tag, StringBuffer buf){
			final String text = new String(buf);
			this.publishProgress(new String[]{tag, text});
		}
		private void postLogInfo(String tag, String text){
			this.publishProgress(new String[]{tag, text});
		}
		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			if(params!=null){
				int exitCode = 0;
				String shell = (String) params[0];
				Log.i(TAG, "running shell: " + shell);
				BufferedReader reader = null;
				byte[] buffer = new byte[4096];
    			StringBuffer output = new StringBuffer();    			
    			int r;
				try {
					 Runtime runtime = Runtime.getRuntime();
				        Process proc = runtime.exec(shell);		
				        while(true){
				        	 try {
//						            if (proc.waitFor() != 0 ) {
//						                System.err.println("exit value = " + proc.exitValue());
//						            }
						        	if(this.isCancelled()){
						        		proc.destroy();
						        		runOnUiThread(new Runnable(){

											@Override
											public void run() {
												// TODO Auto-generated method stub
												tvContent.append("\n\nUser stoped.\n\n");	
												MyLog.v("", "\n\nUser stoped.\n\n");
											}
					    					
					    				});
						        		break;
						        	}
						        	else{						        		
						        		if(proc.getErrorStream().available()>0){
							        		if ((r = proc.getErrorStream().read(buffer, 0, buffer.length)) > -1) {
							        			output.append(new String(buffer, 0, r));
							        		}
							        		if(output.length()>0){	
							    				Log.e(TAG, output.toString());
							    				postLogInfo("e", output);
							    				output.setLength(0);
							    			}
						        		}
						        		if(proc.getInputStream().available()>0){
						        			if ((r = proc.getInputStream().read(buffer, 0, buffer.length)) > -1) {
							        			output.append(new String(buffer, 0, r));
							        		}
							    			if(output.length()>0){	
							    				Log.i(TAG, output.toString());
							    				postLogInfo("i", output);
							    				output.setLength(0);
							    			}
						        		}
						    			try{
						    				exitCode = proc.exitValue();								    			
						    				runOnUiThread(new Runnable(){

												@Override
												public void run() {
													// TODO Auto-generated method stub
													tvContent.append("\n\nTest process is exited.\n\n");	
													MyLog.v("", "\n\nTest process is exited.\n\n");
												}
						    					
						    				});
						    				break;
						    			}
						    			catch(IllegalThreadStateException e){
						    				//e.printStackTrace();
						    			}
						        		Thread.sleep(100);
						        	}
						        } catch (Exception e) {
						            System.err.println(e);
						        }
				        }				      
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally{
					if(reader!=null){
						  try {
							reader.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						  reader=null;
					}
				}
				Log.i(TAG, "exit " + exitCode);
			}						
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			uiHandler.removeMessages(MSG_UPDATE_TIME);
			uiHandler.postDelayed(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					svContent.fullScroll(ScrollView.FOCUS_DOWN);
				}
				
			}, 50);
			rb1.setEnabled(true);
			rb2.setEnabled(true);
			rb3.setEnabled(true);
			btStart.setEnabled(true);			
			//updateLoopView(null);
			super.onPostExecute(result);
		}
		
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			uiHandler.removeMessages(MSG_UPDATE_TIME);
			uiHandler.postDelayed(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					svContent.fullScroll(ScrollView.FOCUS_DOWN);
				}
				
			}, 50);
			rb1.setEnabled(true);
			rb2.setEnabled(true);
			rb3.setEnabled(true);
			btStart.setEnabled(true);
			super.onCancelled();
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			svContent.scrollTo(0, tvContent.getHeight());
			rb1.setEnabled(false);
			rb2.setEnabled(false);
			rb3.setEnabled(false);
			btStart.setEnabled(false);
			svContent.setVisibility(View.VISIBLE);
			MyLog.initLogFile(MainActivity.this);
			startTime = timeFormat.format(new Date());
			startTimeMillis = SystemClock.uptimeMillis();			
			updateLoopView("", false);
			uiHandler.sendEmptyMessage(MSG_UPDATE_TIME);
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			if(values!=null && values[0]!=null && values[1]!=null){
				String tag = (String) values[0];
				String text = (String) values[1];
				boolean error = false;
				if(tag.equals("i")){
					tvContent.append(Html.fromHtml("<font color='blue'>"+text.replace("\n","<br />")+"</font>"));		
					MyLog.i("", text);
				}
				else if(tag.equals("v")){
					tvContent.append(Html.fromHtml("<font color='white'>"+text.replace("\n","<br />")+"</font>"));	
					MyLog.v("", text);
				}
				else if(tag.equals("d")){
					tvContent.append(Html.fromHtml("<font color='blue'>"+text.replace("\n","<br />")+"</font>"));	
					MyLog.d("", text);
				}
				else if(tag.equals("w")){
					tvContent.append(Html.fromHtml("<font color='yellow'>"+text.replace("\n","<br />")+"</font>"));	
					MyLog.w("", text);
					error = true;
				}
				else if(tag.equals("e")){
					tvContent.append(Html.fromHtml("<font color='red'>"+text.replace("\n","<br />")+"</font>"));
					MyLog.e("", text);
					error = true;
				}
				int pos = text.indexOf("Loop");
				if(pos>=0){
					String loopStr = text.substring(pos, text.indexOf(":", pos));
					updateLoopView(loopStr, error);
				}
				uiHandler.postDelayed(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						svContent.fullScroll(ScrollView.FOCUS_DOWN);
					}
					
				}, 50);
				
			}			
			super.onProgressUpdate(values);
		}
			
		
	}
	
	private void getTestProcessId(){
		String ret = exec("ps|grep memtest");
		Log.i(TAG, ret);
	}
	
	private void KillTestProcess(){
		getTestProcessId();
		
	}
	
	public void execCommand(String command) throws IOException {
		 
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);
        
        try {
            if (proc.waitFor() != 0 ) {
                System.err.println("exit value = " + proc.exitValue());
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }

	private String exec(String command) {
		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			int read;
			char[] buffer = new char[4096];
			StringBuffer output = new StringBuffer();
			while ((read = reader.read(buffer)) > 0) {
				output.append(buffer, 0, read);
			}
			reader.close();
			process.waitFor();
			return output.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		
	}
	
	
}
