package com.can.ui;

import java.util.ArrayList;
import java.util.List;

import com.can.assist.CanKey;
import com.can.assist.CanKey.OnCanKeyListener;
import com.can.assist.GdSharedData;
import com.can.assist.GdSharedData.OnGdDataListener;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CanAudio;
import com.can.parser.DDef.OutTemputerInfo;
import com.can.parser.DDef.ParkAssistInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.Protocol;
import com.can.services.CanTxRxStub;
import com.can.services.CanUIService;
import com.can.ui.draw.RadarSurface;
import com.can.ui.draw.Windlv;
import com.can.activity.R;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Message;
import android.provider.Settings;

/**
 * ClassName:CanPopWind
 * 
 * @function:Can信息显示弹出窗口 (空调信息/车门信息/警告信息/雷达信息/轨迹信息)
 * @author Kim
 * @Date: 2016-5-30 上午10:10:00
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanPopWind extends CanUIService {

	public static boolean sIsAirActivityShow = false;
	public static String sFragmentSw = "";

	private boolean mbRadarMute = false;
	private boolean mbParkType  = false;
	private boolean mbReverse   = false;

	private static long   mlshowAirTime  = 0;
	private static long   mlshowDoorTime = 0;
	private static boolean mbInquiryFlag = false;
	private static boolean mbAirAutoCloseFlag  = false;
	private static boolean mbDoorAutoCloseFlag = false;

	private CanKey    mObjCanKey      = null;
	private Protocol  mObjProtocol    = null;
	public static GdSharedData mObjGdSharedData = null;

	private Dialog mObjAirDialog 	  = null;
	private Dialog mObjDoorDialog     = null;
	private Dialog mObjSmRadarDialog  = null;
	private Dialog mObjBigRadarDialog = null;

	private LayoutInflater mObjInflater        = null;
	private LinearLayout   mObjAirDialogView   = null;
	private RelativeLayout mObjDoorDialogView  = null;
	private FrameLayout    mObjSmRadarLayout   = null;
	private RadarSurface   mObjSmRadarSurface  = null;
	private RadarSurface   mObjBigRadarSurface = null;
	private LinearLayout   mObjSmRadarRLayout  = null;
	private LinearLayout   mObjSmRadarRState   = null;
	private LinearLayout   mObjBigRadarLayout  = null;
	private BtnClickListener mObjBtnClickListener = null;
	
	protected ImageView mObjAirDual    = null;
	protected ImageView mObjAirAuto    = null;
	protected ImageView mObjAirAc      = null;
	
	protected ImageView mObjAirAqsState=null;
	protected ImageView mObjAirCircle = null;
	protected ImageView mObjAirFfogger= null;
	protected ImageView mObjAirRfogger= null;
	protected ImageView mObjAirMaxFront=null;
	protected ImageView mObjAirRearlock=null;
	protected ImageView mObjAirAcMax   = null;
	protected Windlv    mObjAirWindlv  = null;
	protected TextView  mObjAirECO     = null;
	protected TextView  mObjAirSync    = null;
	
	protected ImageView mObjAirLUpWind= null;
	protected ImageView mObjAirLPaWind= null;
	protected ImageView mObjAirLDwWind= null;
	protected ImageView mObjAirLHotSeat = null;

	protected ImageView mObjAirRUpWind= null;
	protected ImageView mObjAirRPaWind= null;
	protected ImageView mObjAirRDwWind= null;
	protected ImageView mObjAirRHotSeat = null;

	protected TextView mObjAirLeftTemp= null;
	protected TextView mObjAirRightTemp=null;
	protected TextView mObjAirOutTemp = null;
	
	protected ImageView mObjDoorLf = null;
	protected ImageView mObjDoorRf = null;
	protected ImageView mObjDoorLr = null;
	protected ImageView mObjDoorRr = null;
	protected ImageView mObjDoorTb = null;
	protected ImageView mObjDoorFb = null;
	
	protected ImageButton mObjBtnLeftShow = null;
	protected ImageButton mObjBtnLeftHide = null;
	protected ImageButton mObjBtnRightShow = null;
	protected ImageButton mObjBtnRightHide = null;
	protected ImageButton mObjBtnSmRadarMute = null;
	protected ImageButton mObjBtnSmRadarPark = null;
	protected ImageButton mObjBtnSmRadar2Big = null;
	
	protected ImageButton mObjBtnBigRadar2Video = null;
	protected ImageButton mObjBtnBigRadarMute = null;

	public final int mIType = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR  
			| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
			| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

	protected final String TAG = this.getClass().getName();

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		this.init();
		
		mObjCanKey = new CanKey(getApplicationContext());
		mObjCanKey.setOnCanKeyListener(onCanKeyListener);
		
		mObjGdSharedData = new GdSharedData(getApplicationContext());
		mObjGdSharedData.setOnGdDataListener(onGdDataListener);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private boolean getRadarShow(){
		boolean bshow = false;
		
		if (mObjSmRadarDialog != null && mObjBigRadarDialog != null) {
			
			if (mObjSmRadarDialog.isShowing() || mObjBigRadarDialog.isShowing()) {
				bshow = true;
			}
		}
		
		return bshow;
	}
	
	private void removeother(){
		
		if (mObjAirDialog != null) {
			
			if (mObjAirDialog.isShowing()) {
				hide(mObjAirDialog);
			}
		}
		
		if (mObjDoorDialog != null) {
			
			if (mObjDoorDialog.isShowing()) {
				hide(mObjDoorDialog);
			}
		}
	}
	
	private void init(){
		
		Log.i(TAG, "++init++");
		
		mObjAirDialog     = new Dialog(getApplicationContext(), R.style.dialog_style);
		mObjDoorDialog    = new Dialog(getApplicationContext(), R.style.car_dialog_style);
		mObjSmRadarDialog = new Dialog(getApplicationContext(), R.style.radar_dialog_style);
		mObjBigRadarDialog= new Dialog(getApplicationContext(), R.style.radar_dialog_style);
		
		mObjInflater      = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mObjAirDialogView = (LinearLayout)mObjInflater.inflate(R.layout.air, null);
		mObjDoorDialogView = (RelativeLayout)mObjInflater.inflate(R.layout.door, null);
		mObjSmRadarLayout  = (FrameLayout)mObjInflater.inflate(R.layout.small_radar, null);
		mObjBigRadarLayout = (LinearLayout)mObjInflater.inflate(R.layout.big_radar, null);
		
		mObjAirAc      = (ImageView)mObjAirDialogView.findViewById(R.id.tv_ac);
		mObjAirDual    = (ImageView)mObjAirDialogView.findViewById(R.id.tv_dual);
		mObjAirAuto    = (ImageView)mObjAirDialogView.findViewById(R.id.tv_auto_wind);
		mObjAirECO     = (TextView)mObjAirDialogView.findViewById(R.id.tx_air_eco);
		mObjAirSync    = (TextView)mObjAirDialogView.findViewById(R.id.tx_air_sync);
		mObjAirAqsState= (ImageView)mObjAirDialogView.findViewById(R.id.iv_inner_aqs);
		mObjAirCircle  = (ImageView)mObjAirDialogView.findViewById(R.id.iv_inner_loop);
		mObjAirFfogger = (ImageView)mObjAirDialogView.findViewById(R.id.iv_rear_glass);
		mObjAirRfogger = (ImageView)mObjAirDialogView.findViewById(R.id.iv_front_glass);
		mObjAirMaxFront= (ImageView)mObjAirDialogView.findViewById(R.id.iv_max_front);
		mObjAirRearlock= (ImageView)mObjAirDialogView.findViewById(R.id.iv_rear_lock);
		mObjAirAcMax   = (ImageView)mObjAirDialogView.findViewById(R.id.iv_ac_max);
		mObjAirWindlv = (Windlv) mObjAirDialogView.findViewById(R.id.iv_wind_lv);
		
		mObjAirLUpWind = (ImageView)mObjAirDialogView.findViewById(R.id.iv_blow_head_l);
		mObjAirLPaWind = (ImageView)mObjAirDialogView.findViewById(R.id.iv_blow_hands_l);
		mObjAirLDwWind = (ImageView)mObjAirDialogView.findViewById(R.id.iv_blow_feet_l);
		mObjAirLHotSeat = (ImageView)mObjAirDialogView.findViewById(R.id.iv_heat_seat_l);

		mObjAirRUpWind = (ImageView)mObjAirDialogView.findViewById(R.id.iv_blow_head_r);
		mObjAirRPaWind = (ImageView)mObjAirDialogView.findViewById(R.id.iv_blow_hands_r);
		mObjAirRDwWind = (ImageView)mObjAirDialogView.findViewById(R.id.iv_blow_feet_r);
		mObjAirRHotSeat = (ImageView)mObjAirDialogView.findViewById(R.id.iv_heat_seat_r);

		mObjAirLeftTemp = (TextView)mObjAirDialogView.findViewById(R.id.tv_temp_l);
		mObjAirRightTemp= (TextView)mObjAirDialogView.findViewById(R.id.tv_temp_r);
		mObjAirOutTemp = (TextView) mObjAirDialogView.findViewById(R.id.tx_air_outtemp);
		
		mObjDoorLf = (ImageView)mObjDoorDialogView.findViewById(R.id.iv_left_front_door);
		mObjDoorRf = (ImageView)mObjDoorDialogView.findViewById(R.id.iv_right_front_door);
		mObjDoorLr = (ImageView)mObjDoorDialogView.findViewById(R.id.iv_left_back_door);
		mObjDoorRr = (ImageView)mObjDoorDialogView.findViewById(R.id.iv_right_back_door);
		mObjDoorTb = (ImageView)mObjDoorDialogView.findViewById(R.id.iv_rear_door_open);
		mObjDoorFb = (ImageView)mObjDoorDialogView.findViewById(R.id.iv_front_cover_open);
	
		RelativeLayout layout = (RelativeLayout) mObjSmRadarLayout.findViewById(R.id.the_radarlayout).findViewById(R.id.top_layout);
		LinearLayout leftlayout = (LinearLayout) layout.findViewById(R.id.left_layout);
		
		mObjBtnClickListener = new BtnClickListener();
		mObjSmRadarSurface = (RadarSurface)leftlayout.findViewById(R.id.radar_surfaceview);
		mObjBtnLeftShow    = (ImageButton)leftlayout.findViewById(R.id.btn_radar_left_show);
		mObjBtnLeftHide    = (ImageButton)leftlayout.findViewById(R.id.btn_radar_left_hide);
		mObjBtnLeftShow.setOnClickListener(mObjBtnClickListener);
		mObjBtnLeftHide.setOnClickListener(mObjBtnClickListener);
		mObjSmRadarSurface.setlayoutId(R.layout.small_radar);

		mObjSmRadarRState = (LinearLayout) layout.findViewById(R.id.right_layout);
		mObjSmRadarRLayout = (LinearLayout) mObjSmRadarRState.findViewById(R.id.button_group);
		mObjBtnRightShow = (ImageButton) mObjSmRadarRState.findViewById(R.id.btn_radar_right_show);
		mObjBtnRightHide = (ImageButton) mObjSmRadarRState.findViewById(R.id.btn_radar_rigth_hide);
		mObjBtnSmRadarMute = (ImageButton) mObjSmRadarRLayout.findViewById(R.id.btn_radar_right_volume);
		mObjBtnSmRadarPark = (ImageButton) mObjSmRadarRLayout.findViewById(R.id.btn_radar_right_park);
		mObjBtnSmRadar2Big = (ImageButton) mObjSmRadarRLayout.findViewById(R.id.btn_radar_right_magnify);
		mObjBtnRightShow.setOnClickListener(mObjBtnClickListener);
		mObjBtnRightHide.setOnClickListener(mObjBtnClickListener);
		mObjBtnSmRadarMute.setOnClickListener(mObjBtnClickListener);
		mObjBtnSmRadarPark.setOnClickListener(mObjBtnClickListener);
		mObjBtnSmRadar2Big.setOnClickListener(mObjBtnClickListener);
		
		RelativeLayout relativeLayout = (RelativeLayout)mObjBigRadarLayout.findViewById(R.id.radar_bottom_layout).findViewById(R.id.radar_botton_group); 
		mObjBigRadarSurface   = (RadarSurface)mObjBigRadarLayout.findViewById(R.id.big_radar_surfaceview);
		mObjBtnBigRadar2Video = (ImageButton)relativeLayout.findViewById(R.id.btn_radar_video);
		mObjBtnBigRadarMute   = (ImageButton)relativeLayout.findViewById(R.id.big_radar_mute);
		mObjBtnBigRadar2Video.setOnClickListener(mObjBtnClickListener);
		mObjBtnBigRadarMute.setOnClickListener(mObjBtnClickListener);
		mObjBigRadarSurface.setlayoutId(R.layout.big_radar);
		
		Log.i(TAG, "--init--");
	}
	
	protected class BtnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_radar_left_show:
				mObjBtnLeftShow.setVisibility(View.GONE);	
				mObjSmRadarSurface.setVisibility(View.VISIBLE);
				mObjBtnLeftHide.setVisibility(View.VISIBLE);
				break;
			case R.id.btn_radar_left_hide:
				mObjSmRadarSurface.stopThread();
				mObjBtnLeftShow.setVisibility(View.VISIBLE);
				mObjSmRadarSurface.setVisibility(View.GONE);
				mObjBtnLeftHide.setVisibility(View.GONE);
				break;
			case R.id.btn_radar_right_show:
				mObjBtnRightShow.setVisibility(View.GONE);	
				mObjSmRadarRLayout.setVisibility(View.VISIBLE);
				mObjBtnRightHide.setVisibility(View.VISIBLE);
				break;
			case R.id.btn_radar_rigth_hide:
				mObjBtnRightShow.setVisibility(View.VISIBLE);	
				mObjSmRadarRLayout.setVisibility(View.GONE);
				mObjBtnRightHide.setVisibility(View.GONE);
				break;
			case R.id.btn_radar_right_magnify:
				switchRadar(true);
				break; 
			case R.id.btn_radar_video:
				switchRadar(false);
				break;
			case R.id.big_radar_mute:
			case R.id.btn_radar_right_volume:
				muteRadar();
				break;
			case R.id.btn_radar_right_park:
				parkType();
				break;
			default:
				break;
			}
		}
		
	}
	
	private void show(Dialog dialog, View wView, int istyle, int itype, int iwidth, int iheigh){
		
		if (dialog == null){
			dialog = new Dialog(getApplicationContext(), itype);
		}
		
		dialog.getWindow().setContentView(wView); 
		dialog.getWindow().setType(itype);
		
		if (false == dialog.isShowing()) {
			dialog.show();
		}

		DisplayMetrics dMetrics = new DisplayMetrics();
		WindowManager objWManager = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		objWManager.getDefaultDisplay().getMetrics(dMetrics);
		WindowManager.LayoutParams lparams = dialog.getWindow().getAttributes();
		
		lparams.width  = dMetrics.widthPixels - iwidth;
		lparams.height = dMetrics.heightPixels - iheigh;
			
		dialog.getWindow().setAttributes(lparams);
	}
	
	private void hide(Dialog dialog){
		
		if (dialog != null) {
			
			if (dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
		}
	}
	
	Runnable ObjAirAutoClose = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			long lcurtime = System.currentTimeMillis();
			
			if ((lcurtime - mlshowAirTime) >= 3*1000) {
				
				hide(mObjAirDialog);
				mbAirAutoCloseFlag = false;
			}
			
			if (mbAirAutoCloseFlag) {
				mObjHandler.postDelayed(ObjAirAutoClose, 500);
			}
		}
	};
	
	Runnable ObjDoorAutoClose = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			long lcurtime = System.currentTimeMillis();
			
			if ((lcurtime - mlshowDoorTime) >= 3*1000) {
				
				hide(mObjDoorDialog);
				mbDoorAutoCloseFlag = false;
			}
			
			if (mbDoorAutoCloseFlag) {
				mObjHandler.postDelayed(ObjDoorAutoClose, 500);
			}
		}
	};
	
	private void setAirAttr(AirInfo airInfo){
		
		mObjAirAc.setVisibility(GetGoneSate(airInfo.mAcState));
		mObjAirDual.setVisibility(GetGoneSate(airInfo.mDaulLight));
		mObjAirECO.setVisibility(GetGoneSate(airInfo.mEco));
		mObjAirSync.setVisibility(GetGoneSate(airInfo.mSync));
		mObjAirAqsState.setVisibility(GetGoneSate(airInfo.mAqsInCircle));
		mObjAirCircle.setImageResource((airInfo.mCircleState == 0x01) ? R.drawable.ac_inner_loop : R.drawable.ac_outer_loop);
		mObjAirFfogger.setVisibility(GetGoneSate(airInfo.mFWDefogger));
		mObjAirRfogger.setVisibility(GetGoneSate(airInfo.mRearLight));
		mObjAirMaxFront.setVisibility(GetGoneSate(airInfo.mMaxForntLight));
		mObjAirRearlock.setVisibility(GetGoneSate(airInfo.mRearLock));
		mObjAirAcMax.setVisibility(GetGoneSate(airInfo.mAcMax));
		
		mObjAirLUpWind.setVisibility(GetVisibSate(airInfo.mUpwardWind));
		mObjAirLPaWind.setVisibility(GetVisibSate(airInfo.mParallelWind));
		mObjAirLDwWind.setVisibility(GetVisibSate(airInfo.mDowmWind));
		
		mObjAirRUpWind.setVisibility(GetVisibSate(airInfo.mUpwardWind));
		mObjAirRPaWind.setVisibility(GetVisibSate(airInfo.mParallelWind));
		mObjAirRDwWind.setVisibility(GetVisibSate(airInfo.mDowmWind));

		if (mObjAirWindlv != null) {
			mObjAirWindlv.setMaxLevel(airInfo.mMaxWindlv);
			mObjAirWindlv.setCurLevel(airInfo.mWindRate);
			mObjAirWindlv.invalidate();
		}

		if (airInfo.mAutoLight1 == 0x01) 
		{
			mObjAirAuto.setImageResource(R.drawable.ac_autobigwind);
			mObjAirAuto.setVisibility(View.VISIBLE);
		}
		else if (airInfo.mAutoLight2 == 0x01) 
		{
			mObjAirAuto.setImageResource(R.drawable.ac_autosmallwind);
			mObjAirAuto.setVisibility(View.VISIBLE);
		}
		else 
		{
			mObjAirAuto.setVisibility(View.GONE);
		}

		int iDrawId = 0;
		
		switch (airInfo.mLeftHotSeatTemp)
		{
		case 0x00:
			iDrawId = R.drawable.ac_heat_seat_l1;
			break;
		case 0x01:
			iDrawId = R.drawable.ac_heat_seat_l2;
			break;
		case 0x02:
			iDrawId = R.drawable.ac_heat_seat_l3;
			break;
		case 0x03:
			iDrawId = R.drawable.ac_heat_seat_l4;
			break;
		}
		
		mObjAirLHotSeat.setImageResource(iDrawId);
		
		switch (airInfo.mRightHotSeatTemp) {
		case 0x00:
			iDrawId = R.drawable.ac_heat_seat_r1;
			break;
		case 0x01:
			iDrawId = R.drawable.ac_heat_seat_r2;
			break;
		case 0x02:
			iDrawId = R.drawable.ac_heat_seat_r3;
			break;
		case 0x03:
			iDrawId = R.drawable.ac_heat_seat_r4;
			break;
		}
		
		mObjAirRHotSeat.setImageResource(iDrawId);
		
		//Temp
		int iTempUint = airInfo.mTempUnit;
		String strTempUnit = getString((iTempUint == 0) ? R.string.ac_temp_unit_c : 
			R.string.ac_temp_unit_f);
			
		if (mObjAirLeftTemp != null) {		
			mObjAirLeftTemp.setVisibility(airInfo.mbshowLTemp ? View.VISIBLE : View.INVISIBLE);
			
			if (airInfo.mbshowLTempLv) {
				strTempUnit = getString(R.string.ac_temp_lv);
				mObjAirLeftTemp.setText(airInfo.mbyLeftTemplv + strTempUnit);
			}else {
				if (airInfo.mLeftTemp <= airInfo.mMinTemp) {
					mObjAirLeftTemp.setText(R.string.ac_temp_lo);	
				} 
				else if (airInfo.mLeftTemp >= airInfo.mMaxTemp) {
					mObjAirLeftTemp.setText(R.string.ac_temp_hi);
				} 
				else {
					mObjAirLeftTemp.setText(airInfo.mLeftTemp + strTempUnit);
				}
			}
		}
		
		if (mObjAirRightTemp != null) {
			mObjAirRightTemp.setVisibility(airInfo.mbshowRTemp ? View.VISIBLE : View.INVISIBLE);
			
			if (airInfo.mbshowRTempLv) {
				strTempUnit = getString(R.string.ac_temp_lv);
				mObjAirRightTemp.setText(airInfo.mbyRightTemplv + strTempUnit);
			}else {
				if (airInfo.mRightTemp <= airInfo.mMinTemp) {
					mObjAirRightTemp.setText(R.string.ac_temp_lo);
				} 
				else if (airInfo.mRightTemp >= airInfo.mMaxTemp) {
					mObjAirRightTemp.setText(R.string.ac_temp_hi);
				} 
				else {
					mObjAirRightTemp.setText(airInfo.mRightTemp + strTempUnit);
				}
			}
		}
		
		if (airInfo.mOutTempEnable) {
			if (mObjAirOutTemp != null) {
				StringBuffer strInfo = new StringBuffer();
				strInfo.append("OUT:");
				strInfo.append(" ");
				strInfo.append(airInfo.mOutTemp);
				strTempUnit = getString((iTempUint == 0) ? R.string.ac_temp_unit_c : 
					R.string.ac_temp_unit_f);
				mObjAirOutTemp.setText(strInfo.toString() + strTempUnit);
			}
		}
	}
	
	private int GetGoneSate(byte byState){
		return (byState == 0x01) ? View.VISIBLE : View.GONE;
	}
	
	private int GetVisibSate(byte byState){
		return (byState == 0x01) ? View.VISIBLE : View.INVISIBLE;
	}
	
	private void setDoorAtrr(BaseInfo baseInfo){
		
		mObjDoorLf.setVisibility(GetVisibSate(baseInfo.mLeftFrontDoor));
		mObjDoorRf.setVisibility(GetVisibSate(baseInfo.mRightFrontDoor));
		mObjDoorLr.setVisibility(GetVisibSate(baseInfo.mLeftBackDoor));
		mObjDoorRr.setVisibility(GetVisibSate(baseInfo.mRightBackDoor));
		mObjDoorTb.setVisibility(GetVisibSate(baseInfo.mTailBoxDoor));
		mObjDoorFb.setVisibility(GetVisibSate(baseInfo.mFrontBoxDoor));
	}
	
	private void doReverse(final Boolean bState){
		
		if (bState) {
			if (mObjBigRadarDialog.isShowing()) {
				hide(mObjBigRadarDialog);
			}
			
			if (mObjGdSharedData != null) {
				mObjGdSharedData.showDyncTrack(true);
			}
			
			removeother();
			
			show(mObjSmRadarDialog, mObjSmRadarLayout, R.style.radar_dialog_style, mIType, 0, 0);
		}else {
			
			mObjSmRadarSurface.stopThread();
			hide(mObjSmRadarDialog);
			
			if (mObjBigRadarDialog.isShowing()) {
				mObjBigRadarSurface.stopThread();
				hide(mObjBigRadarDialog);
			}
			
			if (mObjGdSharedData != null) {
				mObjGdSharedData.showDyncTrack(false);
			}
		}
		
		mbInquiryFlag = bState;
	}
	
	private void doDyncTrack(WheelInfo wheelInfo){

		if (mObjGdSharedData != null) {
			mObjGdSharedData.setDyncTrackData(wheelInfo.mEps);
		}
	}
	
	private void switchRadar(boolean bshow){
		
		if (mObjGdSharedData != null) {
			mObjGdSharedData.showDyncTrack(!bshow);
		}
		
		if (bshow) {
			mObjSmRadarSurface.stopThread();
			hide(mObjSmRadarDialog);
			mObjBtnBigRadar2Video.setVisibility(View.VISIBLE);
		    show(mObjBigRadarDialog, mObjBigRadarLayout, R.style.radar_dialog_style, mIType, 0, 0);
		}else {
			mObjBigRadarSurface.stopThread();
			hide(mObjBigRadarDialog);
			show(mObjSmRadarDialog, mObjSmRadarLayout, R.style.radar_dialog_style, mIType, 0, 0);
		}
	}
	
	private void muteRadar(){
		
		mbRadarMute = !mbRadarMute;
		
		int iResId = mbRadarMute ? R.drawable.radar_rbtn_mute : R.drawable.radar_rbtn_unmute;
		
		mObjBtnSmRadarMute.setImageResource(iResId);
		mObjBtnBigRadarMute.setImageResource(iResId);
		
		if (mObjCanProxy != null) {
			mObjCanProxy.sendMsg2Can(mObjProtocol.getMuteRadarData(mbRadarMute));
		}
	}
	
	private void parkType(){
		
		mbParkType = !mbParkType;
		
		int iParkType = mbParkType ? R.drawable.radar_rbtn_park2 : R.drawable.radar_rbtn_park1;
		
		mObjBtnSmRadarPark.setImageResource(iParkType);
		
		if (mObjCanProxy != null) {
			mObjCanProxy.sendMsg2Can(mObjProtocol.getParkData(mbParkType));
		}
	}
	
	private void setParkAttr(ParkAssistInfo parkAssistInfo){
		
		int iResId = (parkAssistInfo.mRadarSoundState == 0) ? R.drawable.radar_rbtn_mute : R.drawable.radar_rbtn_unmute;
		
		mObjBtnSmRadarMute.setImageResource(iResId);
		mObjBtnBigRadarMute.setImageResource(iResId);
		
		//P键 全雷达
		if (mObjSmRadarDialog.isShowing()) {
			//倒车不处理
		}else {
			if (parkAssistInfo.mParkSystemState == 0x01) {
				if (!mObjBigRadarDialog.isShowing()) {
					mObjBtnBigRadar2Video.setVisibility(View.INVISIBLE);
					show(mObjBigRadarDialog, mObjBigRadarLayout, R.style.radar_dialog_style, mIType, 0, 0);				
				}

			}else {
				if (mObjBigRadarDialog.isShowing()) {
					mObjBigRadarSurface.stopThread();
					hide(mObjBigRadarDialog);
				}
			}
		}
	}

	private void setRadarRshowType(byte byType) {

		if (mObjSmRadarRState != null) {
			switch (byType) {
			case 0:
				mObjSmRadarRState.setVisibility(View.INVISIBLE);
				break;
			case 1:
				mObjSmRadarRState.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}

		if (mbReverse && !mObjSmRadarDialog.isShowing()) {
			doReverse(true);
		}
	}

	private void setOutTempInfo(OutTemputerInfo info) {
		if (info != null) {
			if (mObjAirOutTemp != null) {
				StringBuffer strInfo = new StringBuffer();
				strInfo.append("OUT:");
				strInfo.append(" ");

				if (info.mbEnable) {
					strInfo.append(info.mOutCTemp);
					strInfo.append(getString(R.string.ac_temp_unit_c));
					mObjAirOutTemp.setText(strInfo.toString());
				} else if (info.mbFEndble) {
					strInfo.append(info.mOutFTemp);
					strInfo.append(getString(R.string.ac_temp_unit_f));
					mObjAirOutTemp.setText(strInfo.toString());
				}
			}
		}
	}
	
	/**
	 * 
	* <p>Title: IsForeground</p>
	* <p>Description: 当前显示的Activity是否最顶</p>
	* @param  content
	* @param  className
	* @return boolean
	 */
    public boolean IsForeground(Context content, String className) {
        if (content == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) content.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }
	/**
	 * 
	* <p>Title: launch</p>
	* <p>Description: 拉起Can音频源</p>
	* @param  objCanAudio
	* @return void
	 */
	private void launch(CanAudio objCanAudio){
		
		if (objCanAudio.mbshow) {
			if (!IsForeground(getApplicationContext(), CanSource.class.getName())) {
				Intent intent = new Intent();
				
				String strMode = "";
				
				if (objCanAudio.mbyMode == 0 || objCanAudio.mbyMode == 1) {
					strMode = CanTxRxStub.CAN_SOURCE_MODE;
				}else {
					strMode = CanTxRxStub.CAN_PHONE_MODE;
				}
				intent.putExtra(CanTxRxStub.CAN_MODE, strMode);
				intent.setClass(getApplicationContext(), CanSource.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		}
	}

	/**
	 * 实现共享数据的接口
	 */
	private OnGdDataListener onGdDataListener = new OnGdDataListener() {
		
		@Override
		public int onCycle2send(int iloopTick) {
			// TODO Auto-generated method stub
			int ilen = 0;
			
			if (mObjProtocol != null) {
				
				/**
				 * 媒体信息发送
				 */
				if (mObjProtocol.IsSuportMeInfo()) {
					ArrayList<Message> msg = mObjProtocol.getMediaInfo(mObjGdSharedData.getMediaInfo());
					
					if (msg != null) {
						ilen = msg.size();
						
						if (mObjCanProxy != null) {
							mObjCanProxy.sendMsg2Can(msg.get(iloopTick));
						}
					}
				}
				
				/**
				 * 动态轨迹查询
				 */
				if (mbInquiryFlag && mObjCanProxy != null) {

					mObjCanProxy.sendMsg2Can(mObjProtocol.getInquiryTrackData());
				}
			}
			
			return ilen;
		}

		@Override
		public void onReverse(boolean bReverse) {
			// TODO Auto-generated method stub
			if (RadarSurface.getRadarInfo() != null) {
				doReverse(bReverse);
			}

			mbReverse = bReverse;
		}

		@Override
		public void onBtInfo(int iType, int iValue) {
			// TODO Auto-generated method stub
			if (mObjProtocol != null) {
				
				if (mObjProtocol.IsSuportMeInfo()) {
					
					if (mObjCanProxy != null) {
						mObjCanProxy.sendMsg2Can(mObjProtocol.getBtInfo(iType, iValue, mObjGdSharedData.getMediaInfo()));
					}		
				}
			}
		}

		@Override
		public void onVolInfo(int iVol) {
			// TODO Auto-generated method stub
			
			if (mObjProtocol != null) {
				
				if (mObjProtocol.IsSuportMeInfo()) {
					
					if (mObjCanProxy != null) {
						mObjCanProxy.sendMsg2Can(mObjProtocol.getVolInfo(iVol));
					}
				}
			}
		}

		@Override
		public void onAcc(boolean bState) {
			// TODO Auto-generated method stub
			if (mObjProtocol != null) {
				if (mObjCanProxy != null) {
					mObjCanProxy.sendMsg2Can(mObjProtocol.disconnect(bState));
				}
			}
		}
	};
	/**
	 * 回调按键处理
	 */
	private OnCanKeyListener onCanKeyListener = new OnCanKeyListener() {
		
		@Override
		public void ondo(String strKeyCode, boolean bInternal) {
			// TODO Auto-generated method stub
			if (bInternal) {
				if (IsForeground(getApplicationContext(), CanPopActivity.class.getName())) {
					Message msg = Message.obtain(null, DDef.CAN_FRAGMENT_SW);
					msg.obj = strKeyCode;
					send2UIUser(msg);
				} else {
					sFragmentSw = strKeyCode;
					Intent intent = new Intent(getApplicationContext(), CanPopActivity.class);
//					intent.putExtra(CanTxRxStub.CAN_FRAGMENT, strKeyCode);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
			} else	if (mObjProtocol != null && mObjCanProxy != null) {
				mObjCanProxy.sendMsg2Can(mObjProtocol.getCanKeyAttr(strKeyCode));
			}
		}
	};

	@Override
	public void doMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what)
		{
		case DDef.FINISH_BIND:
			mObjProtocol = (Protocol)msg.obj;
			mObjGdSharedData.start(300); 
			break;
		case DDef.WHEEL_CMD_ID:
			WheelInfo objWheelInfo = (WheelInfo)msg.obj;
			doDyncTrack(objWheelInfo);
			break;
		case DDef.RADAR_CMD_ID:
			RadarInfo radarInfo = (RadarInfo)msg.obj;
			RadarSurface.setRadarInfo(radarInfo);
			setRadarRshowType(radarInfo.mbyRightShowType);
			break;
		case DDef.PARK_CMD_ID:
			ParkAssistInfo objParkInfo = (ParkAssistInfo)msg.obj;
			setParkAttr(objParkInfo);
			break;
		case DDef.CANKEY_CMD_ID:
			WheelKeyInfo objKeyInfo = (WheelKeyInfo) msg.obj;
			mObjCanKey.sendCankey(objKeyInfo.mstrKeyCode, objKeyInfo.mKeyStatus,
					objKeyInfo.mKnobSteps, objKeyInfo.mbyKeyRepeat, objKeyInfo.bInternal);
			break;
		case DDef.LIGHT_CMD_ID:
			BackLightInfo objBackLight = (BackLightInfo)msg.obj;
			Settings.System.putInt(getApplicationContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, objBackLight.mLight);
			break;
		case DDef.AIR_CMD_ID:
			AirInfo objAirInfo = (AirInfo)msg.obj;
			setAirAttr(objAirInfo);
			if (objAirInfo.bAirUIShow && !getRadarShow() && !sIsAirActivityShow) {
				mlshowAirTime = System.currentTimeMillis();
				show(mObjAirDialog, mObjAirDialogView, R.style.dialog_style, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, 
						(int)getResources().getDimension(R.dimen.air_width), (int)getResources().getDimension(R.dimen.air_height));
				mObjHandler.post(ObjAirAutoClose);
				mbAirAutoCloseFlag = true;
			}
			break;
		case DDef.BASE_CMD_ID:
			BaseInfo objBaseInfo = (BaseInfo)msg.obj;
			setDoorAtrr(objBaseInfo);
            
            if (objBaseInfo.getDoorValid() && !getRadarShow()) 
            {
            	mlshowDoorTime = System.currentTimeMillis();
            	
            	show(mObjDoorDialog, mObjDoorDialogView, R.style.car_dialog_style, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, 
            			(int)getResources().getDimension(R.dimen.door_width), (int)getResources().getDimension(R.dimen.door_height));
            	mObjHandler.post(ObjDoorAutoClose);
            	mbDoorAutoCloseFlag = true;
			}
			break;
		case DDef.OUT_TEMP_ID:
			setOutTempInfo((OutTemputerInfo) msg.obj);
			break;
		case DDef.CAN_AUDIO_INFO:
			launch((CanAudio) msg.obj);
			break;
		}
	}
}
