package com.yecon.carsetting;

import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.yecon.carsetting.unitl.TouchPos;
import com.yecon.carsetting.unitl.Util;

public class TouchCalibrationMainActivity extends Activity {

	final static String TAG = "calibration";
	final static float OFFSET = 50;
	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;
	private Button button5;

	private int X_orientation = 0;
	private int Y_orientation = 0;
	private int XY_swap = 0;
	private double xZoom = 1.0;
	private double yZoom = 1.0;
	private int mClickNum = 0;

	public int mScreenWidth;
	public int mScreenHeight;
	int[][] posArr = new int[5][4];  
	private Vector<TouchPos> mTouchPosArray = new Vector<TouchPos>();
	private final int MSG_ID_POS = 5000;
	private final int DELAY_POS_TIME = 500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_touch_calibration);
		full(true);
		if (!Util.netlink_opened) {
			int ret = createNetLinkCalibration();
			if (ret >= 0) {
				Util.netlink_opened = true;
			}
		}

		if (Util.netlink_opened) {
			sendStsToNetLink(true);
		}

		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		button4 = (Button) findViewById(R.id.button4);
		button5 = (Button) findViewById(R.id.button5);
			
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mScreenWidth = displayMetrics.widthPixels;
		mScreenHeight = displayMetrics.heightPixels;
		buildQuiltDialog(this);// dialog
	}

	private void ShowAndHideButton(int n) {
		Button[] buttons = { button1, button2, button3, button4, button5 };
		for (int j = 0; j < 5; j++) {
			if (j == n)
				buttons[j].setVisibility(View.VISIBLE);
			else
				buttons[j].setVisibility(View.INVISIBLE);
		}
	}

	public void onBackPressed() {
		//sendStsToNetLink(false);
		mTouchPosArray.clear();
		quiltDialog.dismiss();
		finish();
	}
	private void full(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
          //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
           // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
	@Override
	protected void onStop() {
		if (Util.netlink_opened) {
			sendStsToNetLink(false);
		}
		quiltDialog.dismiss();
		finish();
		super.onStop();
	}

	public void sendStsToNetLink(boolean bFlag) {
		int ret = 0;
		if (Util.netlink_opened) {
			ret = sendStsNetLinkCalibration(bFlag);
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
Log.i(TAG, " mClickNum = " + mClickNum+ " ACTION =  " +		ev.getAction() );

		if (ev.getAction() != MotionEvent.ACTION_UP || mClickNum >= 5)
			return super.dispatchTouchEvent(ev);

		Message msg = Message.obtain();
		msg.what = MSG_ID_POS;
		msg.obj = (Object) ev;
		posHandler.removeMessages(MSG_ID_POS);
		posHandler.sendMessageDelayed(msg, DELAY_POS_TIME);

		return false;

	}

	static float mSaveTouchXY[][] = new float[5][2];
	Handler posHandler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.what != MSG_ID_POS)
				return;

			MotionEvent ev = (MotionEvent) msg.obj;
			Log.i(TAG, "x = " + ev.getRawX() + "  y=" + ev.getRawY() );

			mSaveTouchXY[mClickNum][0] = ev.getRawX();
			mSaveTouchXY[mClickNum][1] = ev.getRawY();

			switch (mClickNum) {
			case 0:
			//if ((int) ev.getRawY() - (int) ev.getRawX() > OFFSET) {
			//		XY_swap = 1;
			//		GetResult();
			//		finish();
			//	}
				
				button1.getLocationOnScreen(posArr[0]);				
				fillData((int) ev.getRawX(), (int) ev.getRawY());
				ShowAndHideButton(1);
				break;
			case 1:
				button2.getLocationOnScreen(posArr[1]);
				ShowAndHideButton(2);
				fillData((int) ev.getRawX(), (int) ev.getRawY());
				
	
				break;
			case 2:
				button3.getLocationOnScreen(posArr[2]);
				ShowAndHideButton(3);
				fillData((int) ev.getRawX(), (int) ev.getRawY());		
			
				break;
			case 3:
				button4.getLocationOnScreen(posArr[3]);
				ShowAndHideButton(4);
				fillData((int) ev.getRawX(), (int) ev.getRawY());
				button5.getLocationOnScreen(posArr[4]);
				break;
			case 4:
				fillData((int) ev.getRawX(), (int) ev.getRawY());			

				double hypot[] = new double[4];
				for (int i = 0; i < hypot.length; i++) {
					hypot[i] = Math.hypot(Math.abs(mSaveTouchXY[i + 1][0] - mSaveTouchXY[0][0]),
							Math.abs(mSaveTouchXY[i + 1][1] - mSaveTouchXY[0][1]));
				}
			
				double hypotScreenY1 = Math.hypot(Math.abs(posArr[2][0] - posArr[1][0]),
						Math.abs(posArr[2][1] - posArr[1][1]));
				double d1 =	 Math.hypot(Math.abs(mSaveTouchXY[2][0] - mSaveTouchXY[1][0]),
						Math.abs(mSaveTouchXY[2][1] - mSaveTouchXY[1][1]));
				
				double hypotScreenY2 =		Math.hypot(Math.abs(posArr[3][0] - posArr[4][0]),
						Math.abs(posArr[3][1] - posArr[4][1]));
				double d2 =		Math.hypot(Math.abs(mSaveTouchXY[3][0] - mSaveTouchXY[4][0]),
						Math.abs(mSaveTouchXY[3][1] - mSaveTouchXY[4][1]));
				
				double hypotScreenX =		Math.hypot(Math.abs(posArr[2][0] - posArr[3][0]),
						Math.abs(posArr[2][1] - posArr[3][1]));
				double d3 =		Math.hypot(Math.abs(mSaveTouchXY[2][0] - mSaveTouchXY[3][0]),
						Math.abs(mSaveTouchXY[2][1] - mSaveTouchXY[3][1]));
				if (Math.abs(hypot[0] - hypot[1]) < OFFSET
						&& Math.abs(hypot[0] - hypot[2]) < OFFSET
						&& Math.abs(hypot[0] - hypot[3]) < OFFSET
						&& Math.abs(hypot[1] - hypot[2]) < OFFSET
						&& Math.abs(hypot[1] - hypot[3]) < OFFSET
						&& Math.abs(hypot[2] - hypot[3]) < OFFSET
						&& Math.abs(hypot[0] + hypot[2]) > mScreenWidth / 2
						&& Math.abs(hypot[1] + hypot[3]) > mScreenWidth / 2
						&& isInRect() 
						&& Math.abs(d1 - d2) < OFFSET
						&& d3>d1 && d3>d2) {
					
					xZoom = hypotScreenX/d3;
					yZoom = (hypotScreenY1+hypotScreenY2)/(d1+d2);
					Log.i(TAG, " dratiox = "+ xZoom +" dratioY="+yZoom);
					GetResult();					
					
					finish();
				} else {
					mClickNum = -1;
					XY_swap = 0;
					X_orientation = 0;
					Y_orientation = 0;
					xZoom = 1.0;
					yZoom = 1.0;
					mTouchPosArray.clear();
					ShowAndHideButton(0);
					
					quiltDialog.show();
				}
				break;
			}
			
			mClickNum++;
		}

	};
private boolean isInRect()
{
	if((mSaveTouchXY[0][0]> Math.min(mSaveTouchXY[3][0], mSaveTouchXY[2][0])) && 
			(mSaveTouchXY[0][0]< Math.max(mSaveTouchXY[3][0],mSaveTouchXY[2][0])) && 
			(mSaveTouchXY[0][1] > Math.min(mSaveTouchXY[1][1], mSaveTouchXY[2][1])) && 
			(mSaveTouchXY[0][1] < Math.max(mSaveTouchXY[1][1],mSaveTouchXY[2][1]))){
			    return true;
			}
			else
			{
				return false;
			}
}
	private void fillData(int xPos, int yPos) {
		TouchPos touchPos = new TouchPos(xPos, yPos);
		mTouchPosArray.add(touchPos);
	}
	private Dialog quiltDialog = null;
	private void buildQuiltDialog(Context contxt) {
		LayoutInflater factory = LayoutInflater.from(contxt);
		View editBTNameView = factory.inflate(R.layout.dialog_calibrate_learning, null);
		quiltDialog = new Dialog(contxt);
		if (quiltDialog == null) {
			return;
		}

		quiltDialog.setContentView(editBTNameView);
		((Button) quiltDialog.findViewById(R.id.ok)).setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View arg0) {
				if (arg0.getId() == R.id.ok) {
					quiltDialog.dismiss();
				}
			}
		});
	}
	private void GetResult() {
		if (XY_swap != 1) {
			if (mTouchPosArray.size() < 5) {
				return;
			}

			TouchPos touchPos1 = mTouchPosArray.get(0);
			TouchPos touchPos2 = mTouchPosArray.get(1);
			TouchPos touchPos3 = mTouchPosArray.get(2);
			TouchPos touchPos4 = mTouchPosArray.get(3);
			TouchPos touchPos5 = mTouchPosArray.get(4);

			if ((touchPos5.getXPos() > touchPos2.getXPos())
					&& (touchPos4.getXPos() > touchPos3.getXPos())) {
				X_orientation = 0;//
			}

			if ((touchPos5.getXPos() < touchPos2.getXPos())
					&& (touchPos4.getXPos() < touchPos3.getXPos())) {
				X_orientation = 1;//
			}

			if ((touchPos3.getYPos() > touchPos2.getYPos())
					&& (touchPos4.getYPos() > touchPos5.getYPos())) {
				Y_orientation = 0;//
			}

			if ((touchPos3.getYPos() < touchPos2.getYPos())
					&& (touchPos4.getYPos() < touchPos5.getYPos())) {
				Y_orientation = 1;//
			}
		}

		// if (touchPos1.getYPos() > touchPos1.getXPos()) {
		// XY_swap = 1;
		// }

		if (XY_swap == 1) {
			X_orientation = 0;
			Y_orientation = 0;
		}
		Log.i(TAG, " writeCalibrationData begin data XZoom="+(int)(xZoom*50)+" yZoom="+(int)(yZoom*50) );
		writeCalibrationData(XY_swap, 0, X_orientation, Y_orientation,(int)(xZoom*50),(int)(yZoom*50));
		
		//if (Util.netlink_opened) {
		//	sendStsToNetLink(false);
		//}

	}

	public native void writeCalibrationData(int u8SwapXY, int u8MirrorXY, int u8MirrorX,
			int u8MirrorY,int u8XZoom,int u8YZoom);

	public native int sendStsNetLinkCalibration(boolean sts);

	public native int createNetLinkCalibration();

	static {
		System.loadLibrary("WriteDataLib");
	}
}
