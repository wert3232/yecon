package com.can.ui.honda;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.FuelMilInfo;
import com.can.parser.Protocol;
import com.can.parser.raise.honda.ReHondaProtocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.FuelSeekBar;

public class CanHondaFuelMil extends CanFrament implements OnClickListener{
	private Protocol mProtocol = null;
	private View mView = null;
	private LinearLayout mLayoutCurDriving = null;
	private LinearLayout mLayoutResumDriving = null;
	private TextView mTvCurDriving = null;
	private TextView mTvHisDriving = null;
	
	private TextView mTvInstanceFuel = null;
	private TextView mTvCurFuel = null;
	private TextView mTvLastFuel = null;
	private TextView mTvCurDriRange = null;
	private TextView mTvCurTripA = null;
	private TextView mTvCurTripAAvg = null;
	private TextView mTvFirstTripA = null;
	private TextView mTvFirstAvg = null;
	private TextView mTvSecondTripA = null;
	private TextView mTvSecondAvg = null;
	private TextView mTvThirdTripA = null;
	private TextView mTvThirdAvg = null;
	private TextView mTvHisDriRange = null;
	private FuelSeekBar mSkBarInstance = null;
	private FuelSeekBar mSkBarCur = null;
	private FuelSeekBar mSkBarLast = null;
	private FuelSeekBar mSkBarHAverage = null;
	private FuelSeekBar mSkBarHFirst = null;
	private FuelSeekBar mSkBarHSecond = null;
	private FuelSeekBar mSkBarHThird = null;
	private int[] mFuelTack = { 60, 10, 12, 20, 30, 40, 50, 60, 70, 80, 90, 100};
	private String[] mStrFuelUint = {"  MPG", "  KM/L", "  L/100KM"};
	private String[] mStrDistanceUint = {"  KM", "  MIL"};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mHandler, getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.honda_fuel_mil, null);
			initView();
		}
		return mView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getData(DDef.FUELMIL_CMD_ID);
	}

	@Override
	public void onDestroy() {
		super.DeInit();
		super.onDestroy();
	}

	private void initView() {
		if (mView != null) {
			mLayoutCurDriving = (LinearLayout) mView.findViewById(R.id.ll_cur_driving);
			mLayoutResumDriving = (LinearLayout) mView.findViewById(R.id.ll_resume_driving);
			
			mTvCurDriving = (TextView) mView.findViewById(R.id.btn_honda_cur_driving);
			mTvHisDriving = (TextView) mView.findViewById(R.id.btn_honda_resume_driving);
			mTvCurDriving.setSelected(true);
			mTvHisDriving.setOnClickListener(this);
			mTvCurDriving.setOnClickListener(this);
			mView.findViewById(R.id.btn_delete_record).setOnClickListener(this);
			
			mTvInstanceFuel = (TextView) mView.findViewById(R.id.tv_honda_fuel_instance);
			mTvCurFuel = (TextView) mView.findViewById(R.id.tv_honda_fuel_cur);
			mTvLastFuel = (TextView) mView.findViewById(R.id.tv_honda_fuel_last);
			mTvCurDriRange = (TextView) mView.findViewById(R.id.tv_honda_fuel_cur_driving_range);
			
			mTvCurTripA = (TextView) mView.findViewById(R.id.tv_honda_hisfuel_cur_trip);
			mTvCurTripAAvg = (TextView) mView.findViewById(R.id.tv_honda_hisfuel_average);
			mTvFirstTripA = (TextView) mView.findViewById(R.id.tv_honda_hisfuel_first_trip);
			mTvFirstAvg = (TextView) mView.findViewById(R.id.tv_honda_hisfuel_first_average);
			mTvSecondTripA = (TextView) mView.findViewById(R.id.tv_honda_hisfuel_second_trip);
			mTvSecondAvg = (TextView) mView.findViewById(R.id.tv_honda_hisfuel_second_average);
			mTvThirdTripA = (TextView) mView.findViewById(R.id.tv_honda_hisfuel_third_trip);
			mTvThirdAvg = (TextView) mView.findViewById(R.id.tv_honda_hisfuel_third_average);
			mTvHisDriRange = (TextView) mView.findViewById(R.id.tv_honda_hisfuel_driving_range);
			
			mSkBarInstance = (FuelSeekBar) mView.findViewById(R.id.skbar_fuel_instance);
			mSkBarCur = (FuelSeekBar) mView.findViewById(R.id.skbar_fuel_cur);
			mSkBarLast = (FuelSeekBar) mView.findViewById(R.id.skbar_fuel_last);
			mSkBarHFirst = (FuelSeekBar) mView.findViewById(R.id.skbar_fuel_his_first);
			mSkBarHSecond = (FuelSeekBar) mView.findViewById(R.id.skbar_fuel_his_second);
			mSkBarHThird = (FuelSeekBar) mView.findViewById(R.id.skbar_fuel_his_third);
			mSkBarHAverage = (FuelSeekBar) mView.findViewById(R.id.skbar_fuel_his_average);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_honda_cur_driving:
			mLayoutResumDriving.setVisibility(View.GONE);
			mLayoutCurDriving.setVisibility(View.VISIBLE);
			mTvCurDriving.setSelected(true);
			mTvHisDriving.setSelected(false);
			break;
		case R.id.btn_honda_resume_driving:
			mLayoutCurDriving.setVisibility(View.GONE);
			mLayoutResumDriving.setVisibility(View.VISIBLE);
			mTvHisDriving.setSelected(true);
			mTvCurDriving.setSelected(false);
			break;
		case R.id.btn_delete_record:
			byte[] datas = { 0x33, 0x03 };
			super.sendMsg(ReHondaProtocol.DATA_TYPE_CAR_INFO_REQUEST, datas, mProtocol);
			break;
		default:
			break;
		}
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mProtocol = (Protocol) msg.obj;
				initData();
				break;
			case DDef.FUELMIL_CMD_ID:
				setFuelMilInfo((FuelMilInfo) msg.obj);
				break;
			default:
				break;
			}
		}
		
	};
	
	private void setFuelMilInfo(FuelMilInfo fuelMilInfo) {
		if (isVisible()) {
			if (fuelMilInfo.mIndex == 1) {
				mTvInstanceFuel.setText(String.valueOf(fuelMilInfo.mImmediateFuel) + mStrFuelUint[fuelMilInfo.mImmediateFuelUnit]);
				mTvCurFuel.setText(String.valueOf((float)fuelMilInfo.mCurrentAverFuel/10) + mStrFuelUint[fuelMilInfo.mCHAverFuelUnit]);
				mTvLastFuel.setText(String.valueOf((float)fuelMilInfo.mHistoryAverFuel/10) + mStrFuelUint[fuelMilInfo.mCHAverFuelUnit]);
				mTvCurDriRange.setText(String.valueOf(fuelMilInfo.mCanDriveMil) + mStrDistanceUint[fuelMilInfo.mCanDriveMilUnit]);
				mTvHisDriRange.setText(String.valueOf(fuelMilInfo.mCanDriveMil) + mStrDistanceUint[fuelMilInfo.mCanDriveMilUnit]);
				mTvCurTripA.setText(String.valueOf((float)fuelMilInfo.mTripaIndex1/10) + mStrDistanceUint[fuelMilInfo.mTripaUnit]);
				mTvCurTripAAvg.setText(String.valueOf((float)fuelMilInfo.mAverFuel/10) + mStrFuelUint[fuelMilInfo.mAverFuelUnit]);
				
				mSkBarInstance.setMax(mFuelTack[fuelMilInfo.mFuelRange]*10);
				mSkBarCur.setMax(mFuelTack[fuelMilInfo.mFuelRange]*10);
				mSkBarLast.setMax(mFuelTack[fuelMilInfo.mFuelRange]*10);
				mSkBarHAverage.setMax(mFuelTack[fuelMilInfo.mFuelRange]*10);
				mSkBarInstance.setProgress(fuelMilInfo.mImmediateFuel);
				mSkBarCur.setProgress(fuelMilInfo.mCurrentAverFuel);
				mSkBarLast.setProgress(fuelMilInfo.mHistoryAverFuel);
				mSkBarHAverage.setProgress(fuelMilInfo.mAverFuel);
			} else if (fuelMilInfo.mIndex == 2) {
				mTvFirstTripA.setText(String.valueOf((float)fuelMilInfo.mFirstTripaRecord/10) + mStrDistanceUint[fuelMilInfo.mTripaUnit]);
				mTvFirstAvg.setText(String.valueOf((float)fuelMilInfo.mFirstAverFuelRecord/10) + mStrFuelUint[fuelMilInfo.mImmediateFuelUnit]);
				mTvSecondTripA.setText(String.valueOf((float)fuelMilInfo.mSecondTripaRecord/10) + mStrDistanceUint[fuelMilInfo.mTripaUnit]);
				mTvSecondAvg.setText(String.valueOf((float)fuelMilInfo.mSecondAverFuelRecord/10) + mStrFuelUint[fuelMilInfo.mImmediateFuelUnit]);
				mTvThirdTripA.setText(String.valueOf((float)fuelMilInfo.mThirdTripaRecord/10) + mStrDistanceUint[fuelMilInfo.mTripaUnit]);
				mTvThirdAvg.setText(String.valueOf((float)fuelMilInfo.mThirdAverFuelRecord/10) + mStrFuelUint[fuelMilInfo.mImmediateFuelUnit]);

				mSkBarHFirst.setMax(mFuelTack[fuelMilInfo.mFuelRange]*10);
				mSkBarHSecond.setMax(mFuelTack[fuelMilInfo.mFuelRange]*10);
				mSkBarHThird.setMax(mFuelTack[fuelMilInfo.mFuelRange]*10);
				mSkBarHFirst.setProgress(fuelMilInfo.mFirstAverFuelRecord);
				mSkBarHSecond.setProgress(fuelMilInfo.mSecondAverFuelRecord);
				mSkBarHThird.setProgress(fuelMilInfo.mThirdAverFuelRecord);
			}
		}
	}
	
	private void initData() {
		byte[] datas1 = { 0x33, 0x01 };
		byte[] datas2 = { 0x33, 0x02 };
		super.sendMsg(ReHondaProtocol.DATA_TYPE_CAR_INFO_REQUEST, datas1, mProtocol);
		super.sendMsg(ReHondaProtocol.DATA_TYPE_CAR_INFO_REQUEST, datas2, mProtocol);
	}
}
