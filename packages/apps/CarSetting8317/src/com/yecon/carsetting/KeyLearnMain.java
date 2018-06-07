package com.yecon.carsetting;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.yecon.carsetting.unitl.TouchData;
import com.yecon.carsetting.unitl.TouchMsg;
import com.yecon.carsetting.unitl.Util;
import com.yecon.carsetting.unitl.XmlParse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.mcu.McuVersionInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class KeyLearnMain extends Activity implements OnClickListener {
	// ,OnTouchListener{

	

	String strTag = "com.yecon.carsetting.KeyLearnMain:";
	Button controlImageBtn;
	Button modeImageBtn;

	TextView showTextView;
	SettingSize settingSize;

	private Dialog quiltDialog = null;
	public int mScreenWidth;
	public int mScreenHeight;
	// public int mFragmentType = Util.mode_fragment_type;
	public  boolean mTouchType = false;// long/short key type
	private  Button mCurBtn = null;

	int mCurRestOrExitBtnID = 0;

	Button[] controlButtons = new Button[13];
	Button[] modeButtons = new Button[12];
	Button[] mainButtons = new Button[5];
	private  ArrayList<TouchMsg> touchArrayList = new ArrayList<TouchMsg>();
	public ArrayList<TouchData> touchDataArrayList = new ArrayList<TouchData>();
	Vector<Integer> touchKeyArray = new Vector<Integer>();

	boolean mNetLinkSuccess = false;
	
	Timer mNetLinkTimer = new Timer();
	private boolean bTimerRunning = false;
	public class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			while(bTimerRunning)
			{			
					int ret = -1;					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ret = recvNetLink();
					if (ret >= 0) {
						int xPos = 0;
						int yPos = 0;
						xPos = getXPosNetLink();
						yPos = getYPosNetLink();						
						Message msg = Message.obtain();
						msg.what = Util.PUBLIC_MSG_ID;
						msg.arg1 = (int) xPos;
						msg.arg2 = (int) yPos;
						mMainHandler.removeMessages(Util.PUBLIC_MSG_ID);
						mMainHandler.sendMessage(msg);
				}				
			}
		}

	}
	TimerTask mNetLinkTaskTimer  = new MyTimerTask();
	public Handler mMainHandler;
	private boolean mbInSizeDialog;
	/*
	 * public ArrayList<TouchMsg> touchConArrayList = new ArrayList<TouchMsg>();
	 * public ArrayList<TouchData> touchConDataArrayList = new
	 * ArrayList<TouchData>();
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
		initData();

		
		mMainHandler = new Handler() {
			@SuppressLint("ResourceAsColor")
			public void handleMessage(Message msg) {

				if (msg.what == Util.PUBLIC_MSG_ID) {
					if( mCurBtn != null)
					{
						if(touchArrayList.size() > 15)
						{
							mCurRestOrExitBtnID = 0;
							((TextView) quiltDialog.findViewById(R.id.dialog_caption)).setText(R.string.factory_tocuh_key_beyond);						
							quiltDialog.show();
							return;
						}
						TouchMsg touchMsg = new TouchMsg();
						touchMsg.x = msg.arg1;
						touchMsg.y = msg.arg2;
						touchMsg.mKeyCode = Util.curKeyCode;
						touchMsg.mCurBtnID = mCurBtn.getId();
						touchMsg.mTouchType = mTouchType;
						touchArrayList.add(touchMsg);
						Log.i(strTag, "add key :mCurBtn="+mCurBtn+" xPos = " + touchMsg.x + " yPos = " + touchMsg.y
								+ " keyCode = " + Util.curKeyCode + "  touchType  " + touchMsg.mTouchType + "touchArraylist size:"+touchArrayList.size());
						mCurBtn.setTextColor(Color.YELLOW);
						mCurBtn.invalidate();
						setLearningText(true);
						mCurBtn = null;

						
					}
					else
					{
						Log.i(strTag, "add key  mCurBtn="+mCurBtn+"，threadId: " + Thread.currentThread().getId());
					}

				}
			}
		};
		if (!Util.netlink_opened) {
			int ret = createNetLink();
			if (ret < 0)
				closeNetLink();
			else {
				Util.netlink_opened = true;
			}
		}

		if (Util.netlink_opened) {
			// mNetLinkSuccess = true;
			Log.i(strTag, "  net link init success!!!!!!!");
			bTimerRunning = true;
			mNetLinkTimer.schedule(mNetLinkTaskTimer, 0);
			

		} else {
			Log.i(strTag, "  net link init failed!!!!!!!");
		}
		Button btn = null;
		for (int i = 0; i < XmlParse.touch_key_array.size(); i++) {
			btn = (Button) findViewById(XmlParse.touch_key_array.get(i));
			if(btn != null)				
				btn.setTextColor(Color.YELLOW);
		}

	}

	private void initUI() {
		setContentView(R.layout.activity_key_learning_main);

		controlImageBtn = (Button) findViewById(R.id.controlImageButton);
		modeImageBtn = (Button) findViewById(R.id.modeImageButton);
		showTextView = (TextView) findViewById(R.id.show);

		mainButtons[0] = (Button) findViewById(R.id.sizeSetting);
		mainButtons[1] = (Button) findViewById(R.id.longpress);
		mainButtons[2] = (Button) findViewById(R.id.start);
		mainButtons[3] = (Button) findViewById(R.id.reset);
		mainButtons[4] = (Button) findViewById(R.id.quit);

		// mode buttons
		modeButtons[0] = (Button) findViewById(R.id.mainActivity);
		modeButtons[1] = (Button) findViewById(R.id.back);
		modeButtons[2] = (Button) findViewById(R.id.model);
		modeButtons[3] = (Button) findViewById(R.id.band);
		modeButtons[4] = (Button) findViewById(R.id.sound);
		modeButtons[5] = (Button) findViewById(R.id.navigation);
		modeButtons[6] = (Button) findViewById(R.id.dvd);
		modeButtons[7] = (Button) findViewById(R.id.music);
		modeButtons[8] = (Button) findViewById(R.id.video);
		modeButtons[9] = (Button) findViewById(R.id.television);
		modeButtons[10] = (Button) findViewById(R.id.bluetoothphone);
		modeButtons[11] = (Button) findViewById(R.id.appmanager);
		
		// control buttons
		controlButtons[0] = (Button) findViewById(R.id.shutdown);
		controlButtons[1] = (Button) findViewById(R.id.auto_scan);
		controlButtons[2] = (Button) findViewById(R.id.browse);
		controlButtons[3] = (Button) findViewById(R.id.play_or_stop);
		controlButtons[4] = (Button) findViewById(R.id.decrease);
		controlButtons[5] = (Button) findViewById(R.id.increase);
		controlButtons[6] = (Button) findViewById(R.id.out);
		controlButtons[7] = (Button) findViewById(R.id.previous);
		controlButtons[8] = (Button) findViewById(R.id.next);
		controlButtons[9] = (Button) findViewById(R.id.hang_up);
		controlButtons[10] = (Button) findViewById(R.id.balance);
		controlButtons[11] = (Button) findViewById(R.id.mute);
		controlButtons[12] = (Button) findViewById(R.id.off_screen);

		disableAllButton(); // all the buttons disable,text color is white
		buildQuiltDialog(this);// dialog

	}

	private void initData() {
		controlImageBtn.setOnClickListener(this);
		modeImageBtn.setOnClickListener(this);

		for (int i = 0; i < controlButtons.length; i++) {
			controlButtons[i].setOnClickListener(this);
		}
		for (int j = 0; j < modeButtons.length; j++) {
			modeButtons[j].setOnClickListener(this);
		}
		for (int k = 0; k < mainButtons.length; k++) {
			mainButtons[k].setOnClickListener(this);
		}

		DisplayMetrics displayMetrics = new DisplayMetrics();// 1920
		// DisplayMetrics displayMetrics =
		// getResources().getDisplayMetrics();//1776
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mScreenWidth = displayMetrics.widthPixels;
		mScreenHeight = displayMetrics.heightPixels;
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();

		switch (id) {
		case R.id.modeImageButton: { // hide the whole control buttons,show the
										// whole mode buttons
			setControlBtnInvisible();
			controlImageBtn.setBackgroundResource(R.drawable.control_btn_not);
			modeImageBtn.setBackgroundResource(R.drawable.control_btn_hot);
			// mFragmentType = Util.mode_fragment_type;
		}
			break;
		case R.id.controlImageButton: { // hide the whole mode buttons,show the
										// whole control buttons
			setModeBtnInvisible();
			controlImageBtn.setBackgroundResource(R.drawable.control_btn_hot);
			modeImageBtn.setBackgroundResource(R.drawable.control_btn_not);
			// mFragmentType = Util.con_fragment_type;
		}
			break;
		case R.id.sizeSetting: { // setting the size
			mbInSizeDialog = true;
			Intent intent = new Intent(this, SettingSize.class);
			startActivity(intent);
		}
			break;
		case R.id.longpress: { // longpresss
			if (mTouchType) {
				mTouchType = false;
				mainButtons[1].setText(R.string.factory_KeyName_shortpress);
			} else {
				mTouchType = true;
				mainButtons[1].setText(R.string.factory_KeyName_longpress);
			}
		}
			break;
		case R.id.start: { // start
			mCurBtn = null;
			
			touchArrayList.clear();
			touchDataArrayList.clear();
			setLearningText(false);
			mTouchType = false;

			if (!mNetLinkSuccess)
				setNetLinkSts(true);

			enableAllButton();
			setEnableBackgroundAllButton();
			showTextView.setText(R.string.factory_studying);
			Log.i(strTag, "onClick  R.id.start mCurBtn=null touchArrayList size:"+touchArrayList.size());
		}
			break;
		case R.id.reset: { // reset

			mCurRestOrExitBtnID = R.id.reset;
			((TextView) quiltDialog.findViewById(R.id.dialog_caption))
					.setText(R.string.factory_reset_title);
			quiltDialog.show();

		}
			break;
		case R.id.quit: { // quilt

			mCurRestOrExitBtnID = R.id.quit;
			((TextView) quiltDialog.findViewById(R.id.dialog_caption))
					.setText(R.string.factory_tocuh_key_save_info);
			quiltDialog.show();

		}
			break;

		case R.id.mainActivity: { // mainActivity
			modeButtons[0].setTextColor(Color.RED);
			setCurBtn(modeButtons[0]);
		}
			break;
		case R.id.back: { // back
			modeButtons[1].setTextColor(Color.RED);
			setCurBtn(modeButtons[1]);
		}
			break;
		case R.id.model: { // mode
			modeButtons[2].setTextColor(Color.RED);
			setCurBtn(modeButtons[2]);
		}
			break;
		case R.id.band: { // band
			modeButtons[3].setTextColor(Color.RED);
			setCurBtn(modeButtons[3]);
		}
			break;
		case R.id.sound: { // sound
			modeButtons[4].setTextColor(Color.RED);
			setCurBtn(modeButtons[4]);
		}
			break;
		case R.id.navigation: { // navigation
			modeButtons[5].setTextColor(Color.RED);
			setCurBtn(modeButtons[5]);
		}
			break;
		case R.id.dvd: { // DVD
			modeButtons[6].setTextColor(Color.RED);
			setCurBtn(modeButtons[6]);
		}
			break;
		case R.id.music: { // music
			modeButtons[7].setTextColor(Color.RED);
			setCurBtn(modeButtons[7]);
		}
			break;
		case R.id.video: { // video
			modeButtons[8].setTextColor(Color.RED);
			setCurBtn(modeButtons[8]);
		}
			break;
		case R.id.television: { // television
			modeButtons[9].setTextColor(Color.RED);
			setCurBtn(modeButtons[9]);
		}
			break;
		case R.id.bluetoothphone: { // bluetoothPhone
			modeButtons[10].setTextColor(Color.RED);
			setCurBtn(modeButtons[10]);
			
		}
			break;
		case R.id.appmanager: { // bluetoothPhone
			modeButtons[11].setTextColor(Color.RED);
			setCurBtn(modeButtons[11]);
			
		}
			break;
		case R.id.shutdown: { // power
			controlButtons[0].setTextColor(Color.RED);
			setCurBtn(controlButtons[0]);
		}
			break;
		case R.id.auto_scan: { // autoScan
			controlButtons[1].setTextColor(Color.RED);
			setCurBtn(controlButtons[1]);
		}
			break;
		case R.id.browse: { // browse
			controlButtons[2].setTextColor(Color.RED);
			setCurBtn(controlButtons[2]);
		}
			break;
		case R.id.play_or_stop: { // play/stop
			controlButtons[3].setTextColor(Color.RED);
			setCurBtn(controlButtons[3]);
		}
			break;
		case R.id.decrease: { // voice-
			controlButtons[4].setTextColor(Color.RED);
			setCurBtn(controlButtons[4]);
		}
			break;
		case R.id.increase: { // voice+
			controlButtons[5].setTextColor(Color.RED);
			setCurBtn(controlButtons[5]);
		}
			break;
		case R.id.out: { // out the dvd
			controlButtons[6].setTextColor(Color.RED);
			setCurBtn(controlButtons[6]);
		}
			break;
		case R.id.previous: { // previous
			controlButtons[7].setTextColor(Color.RED);
			setCurBtn(controlButtons[7]);
		}
			break;
		case R.id.next: { // next
			controlButtons[8].setTextColor(Color.RED);
			setCurBtn(controlButtons[8]);
		}
			break;
		case R.id.hang_up: { // hang-up
			controlButtons[9].setTextColor(Color.RED);
			setCurBtn(controlButtons[9]);
		}
			break;
		case R.id.balance: { // balance
			controlButtons[10].setTextColor(Color.RED);
			setCurBtn(controlButtons[10]);
		}
			break;
		case R.id.mute: { // mute
			controlButtons[11].setTextColor(Color.RED);
			setCurBtn(controlButtons[11]);
		}
			break;
		case R.id.off_screen: { //
			controlButtons[12].setTextColor(Color.RED);
			setCurBtn(controlButtons[12]);
		}
			break;
		}
	}

	

	/*
	 * @Override public boolean onTouch(View arg0, MotionEvent event) { switch
	 * (event.getAction() ) { case MotionEvent.ACTION_DOWN:{ if(mCurBtn == null)
	 * return false;
	 * 
	 * if (event.getX() > mScreenWidth || event.getY() >mScreenHeight) { Message
	 * msg = Message.obtain(); msg.what = Util.PUBLIC_MSG_ID; msg.arg1 =
	 * (int)event.getRawX(); msg.arg2 = (int)event.getRawY();
	 * mMainHandler.removeMessages(Util.PUBLIC_MSG_ID);
	 * mMainHandler.sendMessageDelayed(msg, Util.delayTime); } } break; default:
	 * break; } return true; }
	 */

	/*
	 * //定义�?��包含接口的集�? private ArrayList<MyOnTouchListener> onTouchListeners =
	 * new ArrayList<MyOnTouchListener>();
	 * 
	 * @Override public boolean dispatchTouchEvent(MotionEvent ev) { //处理触摸事件触发
	 * for (MyOnTouchListener listener : onTouchListeners) {
	 * listener.onTouch(ev); //分发到onTouch函数 } return
	 * super.dispatchTouchEvent(ev);//事件向下分发 }
	 * 
	 * public void registerMyOnTouchListener(MyOnTouchListener
	 * myOnTouchListener) //�?��合增加接�? {
	 * onTouchListeners.add(myOnTouchListener); }
	 * 
	 * public void unregisterMyOnTouchListener(MyOnTouchListener
	 * myOnTouchListener) { //�?��合删除接�?
	 * onTouchListeners.remove(myOnTouchListener) ; }
	 * 
	 * public interface MyOnTouchListener { //定义接口 public boolean
	 * onTouch(MotionEvent ev); }
	 */
	public void fillData(TouchMsg touchMsg, TouchData touchData) {
		touchData.x0 = touchMsg.x - Util.pos_threshold;
		if (touchData.x0 < 0) {
			touchData.x0 = 0;
		}

		/*if (touchData.x0 < mScreenWidth) {
			touchData.x0 = mScreenWidth + 1;
		}*/

		touchData.y0 = touchMsg.y - Util.pos_threshold;
		if (touchData.y0 < 0) {
			touchData.y0 = 0;
		}

		touchData.x1 = touchMsg.x + Util.pos_threshold;
		touchData.y1 = touchMsg.y + Util.pos_threshold;
		if (!touchMsg.mTouchType)// short key
			touchData.shortKeyCode = touchMsg.mKeyCode;
		else {
			if (/* touchData.btnID != R.id.shutdown && */
			/* touchData.btnID != R.id.off_screen && */
			touchData.btnID != R.id.increase && touchData.btnID != R.id.decrease) {

				touchData.longKeyCode = 0x5000 + (touchMsg.mKeyCode);

			} else {
				touchData.longKeyCode = touchMsg.mKeyCode;
			}

		}

		touchData.btnID = touchMsg.mCurBtnID;

	}

	public void saveData() {
		ArrayList<TouchMsg> tempArrayMsgs = new ArrayList<TouchMsg>();
		boolean bFalg = true;
		tempArrayMsgs.addAll(touchArrayList);
		
		Log.i(strTag, "saveData touchArrayList:size:"+touchArrayList.size());
		// if(tempArrayMsgs.size() <= 0)
		// return;

		XmlParse.touch_key_array.clear();
		// XmlParse.touch_key_array = touchKeyArray;
		TouchMsg tocuhMsgTemp;
		TouchData touchDataTemp;

		TouchData filledData;

		for (int i = 0; i < tempArrayMsgs.size(); i++) {
			tocuhMsgTemp = tempArrayMsgs.get(i);
			touchDataTemp = new TouchData();
			fillData(tocuhMsgTemp, touchDataTemp);
			for (int j = i + 1; j < tempArrayMsgs.size(); j++) {
				if (tocuhMsgTemp.mCurBtnID == tempArrayMsgs.get(j).mCurBtnID) {
					fillData(tempArrayMsgs.get(j), touchDataTemp);
				}
			}

			for (int j = 0; j < touchDataArrayList.size(); j++) {
				filledData = touchDataArrayList.get(j);
				if (filledData.btnID == touchDataTemp.btnID) {
					bFalg = false;
				}
			}

			if (bFalg) {
				touchDataArrayList.add(touchDataTemp);
				XmlParse.touch_key_array.add(touchDataTemp.btnID);
			}

			bFalg = true;

		}

		initData(touchDataArrayList.size(), 0, 0);
		for (int index = 0; index < touchDataArrayList.size(); index++) {
			touchDataTemp = new TouchData();
			touchDataTemp = touchDataArrayList.get(index);
			fillKeyElement(touchDataTemp.x0, touchDataTemp.y0, touchDataTemp.x1, touchDataTemp.y1,
					touchDataTemp.shortKeyCode, touchDataTemp.longKeyCode, 1);

			touchKeyArray.add(touchDataTemp.btnID);
		}

		XmlParse.touch_key_array.addAll(touchKeyArray);

		writeTouchKeyData();

	}

	private void buildQuiltDialog(Context contxt) {
		LayoutInflater factory = LayoutInflater.from(contxt);
		View editBTNameView = factory.inflate(R.layout.quilt_dialog_key_learning, null);
		quiltDialog = new Dialog(contxt);
		if (quiltDialog == null) {
			return;
		}

		quiltDialog.setContentView(editBTNameView);
		/*
		 * TextView mTxtCaption =
		 * (TextView)quiltDialog.findViewById(R.id.dialog_caption);
		 * 
		 * if (mCurRestOrExitBtnID == R.id.reset ) { //
		 * quiltDialog.setTitle(R.string.factory_reset_dialog_title);
		 * mTxtCaption.setText(R.string.factory_reset_dialog_title); }else if
		 * (mCurRestOrExitBtnID == R.id.quit) {
		 * //quiltDialog.setTitle(R.string.factory_tocuh_key_save_info);
		 * mTxtCaption.setText(R.string.factory_tocuh_key_save_info); }
		 */

		((Button) quiltDialog.findViewById(R.id.ok)).setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View arg0) {

				if (mCurRestOrExitBtnID == R.id.reset) {

					disableAllButton();
					setDisableBackgroundAllButton();
					setNetLinkSts(false);
					touchArrayList.clear();
					touchDataArrayList.clear();
					touchKeyArray.clear();
					mCurBtn = null;
					Log.i(strTag, "onClick  R.id.reset mCurBtn=null touchDataArrayList size:"+touchDataArrayList.size());
					quiltDialog.dismiss();
					setLearningText(false);
					mTouchType = false;

				} else if (mCurRestOrExitBtnID == R.id.quit) {
					saveData();
					// setNetLinkSts(false);
					// closeNetLink();
					quiltDialog.dismiss();
					finish();
				}
				else
				{
					quiltDialog.dismiss();
				}
			}
		});
		((Button) quiltDialog.findViewById(R.id.cancel)).setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View arg0) {
			if(mCurRestOrExitBtnID == 0)
			{
				quiltDialog.dismiss();				
			}
			else
			{
				//quiltDialog.dismiss();
				setNetLinkSts(false);

				//
				quiltDialog.dismiss();
				finish();
				}
			}
		});

	}

	public void onBackPressed() {

		mCurRestOrExitBtnID = R.id.quit;
		((TextView) quiltDialog.findViewById(R.id.dialog_caption))
				.setText(R.string.factory_tocuh_key_save_info);

		if (!quiltDialog.isShowing()) {
			quiltDialog.show();
		}

	}

	private void setCurBtn(Button btn) { // KeyCode
		if(mCurBtn != btn && mCurBtn != null)
		{
			mCurBtn.setTextColor(Color.WHITE);
		}
		
		mCurBtn = btn;
		Log.i(strTag, "setCurBtn  mCurBtn="+mCurBtn+"，threadId: " + Thread.currentThread().getId());
	/*	if (Util.netlink_opened) {		
			 if (mNetLinkTaskTimer != null){
				 mNetLinkTaskTimer.cancel();  
			      }
			 mNetLinkTaskTimer = new MyTimerTask();
			mNetLinkTimer.schedule(mNetLinkTaskTimer, 0);
			Log.i(strTag, "  setCurBtn sendkey timer!!!!!!!");
		} */
		switch (btn.getId()) {
		case R.id.mainActivity: {
			Util.curKeyCode = Util.HOME;
		}
			break;
		case R.id.back: {
			Util.curKeyCode = Util.BACK_WAKE_DROPPED;
		}
			break;
		case R.id.model: {
			Util.curKeyCode = Util.YECON_SOURCE_MODE;
		}
			break;
		case R.id.band: {
			Util.curKeyCode = Util.YECON_TUNER;
		}
			break;
		case R.id.sound: {
			Util.curKeyCode = Util.YECON_ISSSR;
		}
			break;
		case R.id.navigation: {
			Util.curKeyCode = Util.YECON_NAVI;
		}
			break;

		case R.id.dvd: {
			Util.curKeyCode = Util.YECON_DVD;
		}
			break;
		// case R.id.setting: {
		// Util.curKeyCode = Util.YECON_SETTING;
		// }
		// break;
		case R.id.music: {
			Util.curKeyCode = Util.YECON_MUSIC;
		}
			break;
		case R.id.video: {
			Util.curKeyCode = Util.YECON_VIDEO;
		}
			break;
		case R.id.television: {
			Util.curKeyCode = Util.YECON_TV;
		}
			break;
		case R.id.bluetoothphone: {
			Util.curKeyCode = Util.YECON_TEL;
		}
			break;
		case R.id.appmanager: {
			Util.curKeyCode = Util.YECON_APP_MANAGER;
		}
			break;
		// CONTROL
		case R.id.shutdown: {
			/*
			 * if (!KeyLearnMain.mTouchType) { Util.curKeyCode =
			 * Util.YECON_BLACKOUT; } else { Util.curKeyCode = Util.YECON_POWER;
			 * }
			 */
			// Util.curKeyCode = Util.YECON_BLACKOUT;
			Util.curKeyCode = Util.YECON_POWER;
		}
			break;
		case R.id.auto_scan: {
			Util.curKeyCode = Util.YECON_RADIO_AS;
		}
			break;
		case R.id.browse: {
			Util.curKeyCode = Util.YECON_RADIO_PS;
		}
			break;
		case R.id.play_or_stop: {
			Util.curKeyCode = Util.YECON_PLAY;
		}
			break;
		case R.id.decrease: {
			/*
			 * if(!KeyLearnMain.mTouchType){ Util.curKeyCode =32882; }else
			 */{
				Util.curKeyCode = 32882;
			}
		}
			break;
		case R.id.increase: {
			/*
			 * if (!KeyLearnMain.mTouchType) { Util.curKeyCode = 32883; } else
			 */{
				Util.curKeyCode = 32883;
			}
		}
			break;
		case R.id.out: {
			Util.curKeyCode = Util.YECON_DVD_EJECT;
		}
			break;
		case R.id.previous: {
			Util.curKeyCode = Util.YECON_PREV;
		}
			break;
		case R.id.next: {
			Util.curKeyCode = Util.YECON_NEXT;
		}
			break;
		case R.id.hang_up: {
			Util.curKeyCode = Util.YECON_PHONE_OFF;
		}
			break;
		case R.id.balance: {
			Util.curKeyCode = Util.YECON_SET_EQ;
		}
			break;
		case R.id.mute: {
			Util.curKeyCode = Util.VOLUME_MUTE;
		}
			break;
		case R.id.off_screen: {

			// if (!KeyLearnMain.mTouchType) {
			Util.curKeyCode = Util.YECON_BLACKOUT;
			/* } else */{
				// Util.curKeyCode = 21013;
			}

		}
			break;
		default:
			return;
		}
		setLearningText(false);
	}

	private void setLearningText(boolean bFlagSuc) {

		if (bFlagSuc) {
			showTextView.setText(R.string.factory_touch_key_learning_success);
			showTextView.setTextColor(Color.YELLOW);
		} else {
			showTextView.setText(R.string.factory_studying);
			showTextView.setTextColor(Color.WHITE);
		}
	}

	private void disableAllButton() { 

		for (int i = 0; i < controlButtons.length; i++) {
			controlButtons[i].setEnabled(false);
			controlButtons[i].setTextColor(Color.WHITE);
		}
		for (int j = 0; j < modeButtons.length; j++) {
			modeButtons[j].setEnabled(false);
			modeButtons[j].setTextColor(Color.WHITE);
		}
	}

	private void enableAllButton() { 
		for (int i = 0; i < controlButtons.length; i++) {
			controlButtons[i].setEnabled(true);
			controlButtons[i].setTextColor(Color.WHITE);
		}
		for (int j = 0; j < modeButtons.length; j++) {
			modeButtons[j].setEnabled(true);
			modeButtons[j].setTextColor(Color.WHITE);
		}
	}

	private void setEnableBackgroundAllButton() { 
		for (int i = 0; i < controlButtons.length; i++) {
			controlButtons[i].setBackgroundResource(R.drawable.btn_notpress);
		}
		for (int j = 0; j < modeButtons.length; j++) {
			modeButtons[j].setBackgroundResource(R.drawable.btn_notpress);
		}

	}

	private void setDisableBackgroundAllButton() { 
		for (int i = 0; i < controlButtons.length; i++) {
			controlButtons[i].setBackgroundResource(R.drawable.keyname_btn_nor);
		}
		for (int j = 0; j < modeButtons.length; j++) {
			modeButtons[j].setBackgroundResource(R.drawable.keyname_btn_nor);
		}
	}

	private void setModeBtnInvisible() { 
		for (int i = 0; i < modeButtons.length; i++) {
			modeButtons[i].setVisibility(View.GONE);
		}
		for (int j = 0; j < controlButtons.length; j++) {
			controlButtons[j].setVisibility(View.VISIBLE);
		}
	}

	private void setControlBtnInvisible() { 
		for (int i = 0; i < controlButtons.length; i++) {
			controlButtons[i].setVisibility(View.GONE);
		}
		for (int j = 0; j < modeButtons.length; j++) {
			modeButtons[j].setVisibility(View.VISIBLE);
		}
	}

	public void onResume() {
		mbInSizeDialog = false;
		super.onResume();
		// setNetLinkSts(true);
	}

	public void onPause() {
		super.onPause();
		// setNetLinkSts(false);
	}
	@Override
	protected void onStop() {
		if(!mbInSizeDialog)
		{
			bTimerRunning = false;		
			mNetLinkTimer.cancel();
			mNetLinkTaskTimer.cancel();
			mMainHandler.removeMessages(Util.PUBLIC_MSG_ID);
			setNetLinkSts(false);
			quiltDialog.dismiss();
			finish();
		}
		
		super.onStop();
	}
	
	public void onDestroy() {
		super.onDestroy();
		bTimerRunning = false;		
		mNetLinkTimer.cancel();
		mNetLinkTaskTimer.cancel();
		mMainHandler.removeMessages(Util.PUBLIC_MSG_ID);
		//setNetLinkSts(false);
		
	}

	private void setNetLinkSts(boolean flag) {

		int ret = -1;
		if (flag) {
			ret = sendStsNetLink(flag);
			if (ret >= 0) {
				mNetLinkSuccess = true;
			} else {
				mNetLinkSuccess = false;
			}

		} else {
			mNetLinkSuccess = false;
			ret = sendStsNetLink(flag);
		}

	}

	public native void writeTouchKeyData();

	public native void readTocuhKeyData();

	public native void fillKeyElement(int xSPoint, int ySPoint, int xEPoint, int yEPoint,
			int shortKeyCode, int longKeyCode, int keyType);

	public native void initData(int keyCount, int xShift, int yShift);

	public native int createNetLink();

	public native void closeNetLink();

	public native int sendStsNetLink(boolean sts);

	public native int recvNetLink();

	public native int getXPosNetLink();

	public native int getYPosNetLink();

	public native int readCalibrationData();

	public native int getCalibrationSwapXY();

	public native int getCalibrationMirrorX();

	public native int getCalibrationMirrorY();

	static {
		System.loadLibrary("WriteDataLib");
	}

}
