package com.android.calibrate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
//Jade add;

 

public class AndroidCalibrate extends Activity {
    /** Called when the activity is first created. */
	
	final String TAG = "AndroidCalibrate";

	static int UI_SCREEN_WIDTH = 800;
	static int UI_SCREEN_HEIGHT = 480;
	
	int direction, ret;
	float[] sampleX = new float[128];
	float[] sampleY = new float[128];
	int index = 0;
	int show_flag = 1;
	int show_count = 2;
	CrossView myView;
	
	int xList[] = {0,0,0,0,0};

	int yList[] = {0,0,0,0,0};

	int xTs[] = {0,0,0,0,0};
	int yTs[] = {0,0,0,0,0};

	Calibrate cal = new Calibrate();
	boolean cal_flag = false;
    
	static void setNotTitle(Activity act) 
	{
	    act.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	static void setFullScreen(Activity act) 
	{
	    setNotTitle(act);
	    act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
    public boolean onTouchEvent(MotionEvent event)
    {
		myView.invalidate();
		int action = event.getAction();
		Log.i(TAG, "action=" + action);
		/*if(action==MotionEvent.ACTION_UP){
			Log.i("OnTouch", event.getX() + "," + event.getY());
			xTs[direction] = (int) event.getX();
			yTs[direction] = (int) event.getY();
			String string = "cal:(" + xTs[direction] + "," + yTs[direction] + ")";
			Toast.makeText(getBaseContext(), string, Toast.LENGTH_SHORT).show();

		if(direction == 4){
			Toast.makeText(getBaseContext(), "Calibrate Done!", Toast.LENGTH_SHORT).show();
			AndroidCalibrate.this.finish();
		}*/
    	if(action != MotionEvent.ACTION_CANCEL && action != MotionEvent.ACTION_UP){	
			int p = event.getPointerCount();
			Log.i(TAG, "p=" + p);
			for(int j = 0; j < p; j++){
				float mCurX = event.getX(j);
				float mCurY =  event.getY(j);
				sampleX[index] = mCurX;
				sampleY[index] = mCurY;
				Log.i(TAG, "index=" + index);
				Log.i(TAG, "mCurX=" + sampleX[index]);
				Log.i(TAG, "mCurY=" + sampleY[index]);
				index++;
    			//sampleX += mCurX;
    			//sampleY += mCurY;
    			
    			//Log.i("sampleX", " " + sampleX);
    			//Log.i("sampleY", " " + sampleY);
			}	
		}
    	if(action == MotionEvent.ACTION_UP){
    		//sampleX = 0;
    		//sampleY = 0;
			Arrays.sort(sampleX,0,index);
			Arrays.sort(sampleY,0,index);
			for(int i = 0; i < index; i++){
				Log.i(TAG, "sampleX= " + sampleX[i]);
				Log.i(TAG, "sampleY=" + sampleY[i]);
			}
			int middle = index/2;
			try{
				if((index & 1) == 1){
					xTs[direction] = (int) sampleX[middle];
					yTs[direction] = (int) sampleY[middle];
				}else {
					xTs[direction] = (int) ((sampleX[middle-1]+sampleX[middle])/2);
					yTs[direction] = (int) ((sampleY[middle-1]+sampleY[middle])/2);
				}
    			}catch(ArrayIndexOutOfBoundsException e){
				Log.i(TAG,"array index out of bounds");
				e.printStackTrace();
				AndroidCalibrate.this.finish();
			}

			Log.i(TAG, "onTouch: "+xTs[direction]+ "," + yTs[direction]);
			index = 0;
			if(direction == 4){
				ret = cal.perform_calibrate(xList, yList, xTs, yTs);
				if(ret == 0){
					Toast.makeText(getBaseContext(), R.string.calibrate_success, Toast.LENGTH_LONG).show();	
					Log.i(TAG,"perform_calibrate done");
					cal_flag = true;
					//Begin : Added by yongwu.zhou  2012/06/04
					SharedPreferences sp = getSharedPreferences("BOOT_CALIBRATE", 0);
	                                sp.edit().putInt("TAG", 2).commit();
	                                //End : Added by yongwu.zhou  2012/06/04
					AndroidCalibrate.this.finish();
				} else {
					cal_flag = false;
					if (show_flag >= show_count){
					    Toast.makeText(getBaseContext(), R.string.calibrate_failed, Toast.LENGTH_LONG).show();
					    AndroidCalibrate.this.finish();
					}else{
					    Toast.makeText(getBaseContext(), R.string.calibrate_re, Toast.LENGTH_LONG).show();
					    show_flag ++;
					    direction = 0;
					    return false;
					}
				}
			}
			direction++;
			if(direction>4){
				Log.i(TAG,"direct:"+direction);
				direction=0;
				Log.i(TAG,"direct1:"+direction);
			}
			
		}

		return false;
    
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
         setFullScreen(this);    
	
     	WindowManager manage=getWindowManager();
        Display display=manage.getDefaultDisplay();
        this.UI_SCREEN_HEIGHT=display.getHeight();
        this.UI_SCREEN_WIDTH=display.getWidth();

	Log.i(TAG,"heigth:"+this.UI_SCREEN_HEIGHT);
	Log.i(TAG,"width:"+this.UI_SCREEN_WIDTH);

	int ctp_flag = SystemProperties.getInt("persist.sys.ctp.flag",0);
	Log.d(TAG,"ctp_flag="+ctp_flag);
	if(ctp_flag > 0)
	{
		Log.i(TAG,"Capacitive Touch Screen DON'T need calibrate...");
		Toast.makeText(getBaseContext(), R.string.no_need_calibrate, Toast.LENGTH_LONG).show(); 
		cal_flag = true;
		this.finish();
		return;
	}
	
	this.xList[0]=50;
	this.xList[1]=(this.UI_SCREEN_WIDTH - 50);
	this.xList[2]=(this.UI_SCREEN_WIDTH - 50);
	this.xList[3]=50;
	this.xList[4]=(this.UI_SCREEN_WIDTH / 2);

	this.yList[0]=50;
	this.yList[1]=50;
	this.yList[2]=(this.UI_SCREEN_HEIGHT - 50);
	this.yList[3]=(this.UI_SCREEN_HEIGHT - 50);
	this.yList[4]=( this.UI_SCREEN_HEIGHT / 2);
	
	File showCountFile = new File("/data4write/data/com.android.calibrate/retry");
        BufferedReader reader = null;
        String count = null;
	if(showCountFile.exists()){
       		try {
			reader = new BufferedReader(new FileReader(showCountFile));	
			count = reader.readLine();
			reader.close();
       		} catch (FileNotFoundException e) {
			Log.i(TAG, "open file fail!");
			e.printStackTrace();
		} catch (IOException e) {
			Log.i(TAG, "read file fail!");
			e.printStackTrace();
		}
		this.show_count	= Integer.parseInt(count);
	}
	Log.i(TAG,"show_count:"+this.show_count);

        myView = new CrossView(this);

        setContentView(myView);
        direction = 0;
	Log.i("write_props", " " + direction);  
        
        try{
            File file = new File("/data4write/data/com.android.calibrate/pointercal");
            if(!file.exists()){
                if(!file.createNewFile())
                    throw new Exception("create pointercal file failed!\n");
                String ts_cal = "53979272 -71561 1543 -2670860 -479 76872 65536";
                FileWriter fw= new FileWriter(file);
                fw.write(ts_cal);
                fw.flush();
                fw.close(); 
            }
            
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        /*myView.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("OnTouch", event.getX() + "," + event.getY());
				
                v.invalidate();

                if (direction < 4) {
                    Log.i("XXW time onTouchListener", " " + direction);
                    
//                    	Log.i("OnTouch", event.getX() + "," + event.getY());
                    	xTs[direction] = (int) event.getX();
                        yTs[direction] = (int) event.getY();
    		    	
                    
                }

                if (direction == 4) {
                    Log.i("XXW", "calibrate_main");
              
//                    	Log.i("OnTouch", event.getX() + "," + event.getY());
                    	xTs[direction] = (int) event.getX();
                        yTs[direction] = (int) event.getY();
                        //ret = cal.perform_calibrate(xList, yList, xTs, yTs);
                		
                		 Log.i("XXW time perform_calibrate", " " + ret);
                         
    		                 
                    Toast.makeText(getBaseContext(), "Calibrate Done!", Toast.LENGTH_SHORT).show();
                    AndroidCalibrate.this.finish();
                }

                direction++;
                Log.i("XXW time onTouchListener", " " + direction);

                return false;

			}
        	
        });*/
       
    }
    protected void onPause(){
    	Log.i("onPause", "pause");
    	super.onPause();
    }
    protected void onResume(){
    	ret = cal.write_props();
    	if(ret != 0){
    		Toast.makeText(getBaseContext(), R.string.ts_failed, Toast.LENGTH_SHORT).show();
			cal_flag = false;
			AndroidCalibrate.this.finish();
    	}
    	Log.i("onResume", "resume");
    	super.onResume();
    }
    protected void onStart(){
    	Log.i("onStart", "start");
    	super.onStart();
    }
    protected void onRestart(){
    	Log.i("onRestart", "Restart");
    	super.onRestart();
    }
    protected void onStop(){
    	Log.i("onStop", "stop");
    	super.onStop();
    }
    protected void onDestroy(){
    	if(!cal_flag){
    		ret = cal.write_OldCalibrate_props();
    		if(ret != 0){
    			Toast.makeText(getBaseContext(), R.string.calibrate_restart, Toast.LENGTH_SHORT).show();
    		}
    	}
    	Log.i("onDestroy", "destroy");
		super.onDestroy();
    }
    protected void onSaveInstanceState(Bundle savedInstanceState){
    	ret = cal.write_OldCalibrate_props();
		if(ret != 0){
			Toast.makeText(getBaseContext(), R.string.calibrate_failed, Toast.LENGTH_SHORT).show();
			AndroidCalibrate.this.finish();
		}
    	Log.i("onSaveInstanceState", "oSaveInstanceState");
    	super.onSaveInstanceState(savedInstanceState);
    }
    public class CrossView extends View {

        public CrossView(Context context) {
            super(context);
        }

		public void onDraw(Canvas canvas) {
			Log.i("XXW time onTouchListener", "onDraw ");
			Paint paint = new Paint();

			paint.setColor(Color.GREEN);
			paint.setStrokeWidth(3);

			int ADJUST;
			if(UI_SCREEN_WIDTH == 800)
			{
				ADJUST = 6;
			}
			else
			{
				ADJUST = 8;
			}
			
			if (direction == 0) {

			    canvas.drawLine(40-ADJUST, 50, 60+ADJUST, 50, paint);

			    canvas.drawLine(50, 40-ADJUST, 50, 60+ADJUST, paint);

			    paint.setColor(Color.WHITE);

			} else if (direction == 1) {

			    canvas.drawLine(UI_SCREEN_WIDTH - 60 - ADJUST, 50, UI_SCREEN_WIDTH - 40 + ADJUST, 50, paint);

			    canvas.drawLine(UI_SCREEN_WIDTH - 50, 40 - ADJUST, UI_SCREEN_WIDTH - 50, 60 + ADJUST, paint);

			    paint.setColor(Color.WHITE);

			} else if (direction == 2) {

				canvas.drawLine(UI_SCREEN_WIDTH - 60 - ADJUST, UI_SCREEN_HEIGHT - 50, UI_SCREEN_WIDTH - 40 + ADJUST,
					UI_SCREEN_HEIGHT - 50, paint);

				canvas.drawLine(UI_SCREEN_WIDTH - 50, UI_SCREEN_HEIGHT - 60 - ADJUST, UI_SCREEN_WIDTH - 50,
					UI_SCREEN_HEIGHT - 40 + ADJUST, paint);

				paint.setColor(Color.WHITE);

            } else if (direction == 3) {

				canvas.drawLine(40-5, UI_SCREEN_HEIGHT - 50, 60 + ADJUST, UI_SCREEN_HEIGHT - 50, paint);

				canvas.drawLine(50, UI_SCREEN_HEIGHT - 60 - ADJUST, 50, UI_SCREEN_HEIGHT - 40 + ADJUST, paint);

				paint.setColor(Color.WHITE);

            } else if (direction == 4) {

				canvas.drawLine(UI_SCREEN_WIDTH / 2 - 10 - ADJUST, UI_SCREEN_HEIGHT / 2,
					UI_SCREEN_WIDTH / 2 + 10 + ADJUST, UI_SCREEN_HEIGHT / 2, paint);

				canvas.drawLine(UI_SCREEN_WIDTH / 2, UI_SCREEN_HEIGHT / 2 - 10 - ADJUST,
					UI_SCREEN_WIDTH / 2, UI_SCREEN_HEIGHT / 2 + 10 + ADJUST, paint);

				paint.setColor(Color.WHITE);

            } else {

            }
            
			if(UI_SCREEN_WIDTH == 800)
			{
				paint.setTextSize(20);
			}
			else
			{
				paint.setTextSize(22);
			}
			
			paint.setTextAlign(Align.CENTER);
			paint.setTypeface(Typeface.SANS_SERIF);
			
			FontMetrics fontMetrics = paint.getFontMetrics(); 
			float fontHeight = fontMetrics.bottom - fontMetrics.top; 
			float textBaseY = UI_SCREEN_HEIGHT - (UI_SCREEN_HEIGHT - fontHeight) / 2 - fontMetrics.bottom; 
    		canvas.drawText(getString(R.string.start_show), UI_SCREEN_WIDTH / 2, textBaseY, paint);

			super.onDraw(canvas);
        }

    }
}

