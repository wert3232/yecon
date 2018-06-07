package com.yecon.canbus.ui;

import static com.yecon.canbus.info.CANBusConstants.CANBUS_DATA_OFF;
import static com.yecon.canbus.info.CANBusConstants.MSG_UPDATE_UI;

import com.yecon.canbus.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
abstract public class CanbusCIActivity extends CANBusUI implements
		OnClickListener {
	private enum SystemInfoType {
		OIL_LEFT, OUTDOOR_TEMP, BATTERY_VOLTAGE, ENGINE_SPEED, TRAVLING_SPEED, DRIVEN_DISTANCE, LAST_TYPE
	};

	private final SystemInfoType[] mNeedShowSystemInfo = {
			SystemInfoType.OIL_LEFT, SystemInfoType.OUTDOOR_TEMP,
			SystemInfoType.BATTERY_VOLTAGE, SystemInfoType.ENGINE_SPEED,
			SystemInfoType.TRAVLING_SPEED, SystemInfoType.DRIVEN_DISTANCE };

	private AllCarInfoAdapter mAdapter;

	private LinearLayout mLayoutCarInfo;

	private ListView mLVAllCarInfo;

	private TextView mTVOilLeft;
	private TextView mTVOutdoorTemp;
	private TextView mTVBatteryVoltage;
	private TextView mTVSeatBelts;
	private TextView mTVRearDoorOpen;
	private TextView mTVHandbrake;
	private TextView mTVCleaningFluid;
	private TextView mTVEngineSpeed;
	private TextView mTVTravelingspeed;
	private TextView mTVDrivenDistance;
	private TextView mTVDoorOpenWarning;

	private ImageView mIVLeftFrontDoor;
	private ImageView mIVRightFrontDoor;
	private ImageView mIVLeftBackDoor;
	private ImageView mIVRightBackDoor;
	private ImageView mIVFueling;
	private ImageView mIVFrontCoverOpen;
	private ImageView mIVRearDoorOpen;

	private String mStrOilLeft;
	private String mStrOutdoorTemp;
	private String mStrBatteryVoltage;
	private String mStrSeatBelts;
	private String mStrRearDoorOpen;
	private String mStrHandbrake;
	private String mStrCleaningFluid;
	private String mStrEnginSpeed;
	private String mStrTravlingSpeed;
	private String mStrDrivenDistance;

	private String mStrStatusNormal;
	private String mStrStatusNoLine;
	private String mStrStatusNoRelease;
	private String mStrStatusNoClose;
	private String mStrStatusLow;

	private String mStrLeftFrontDoorOpen;
	private String mStrRightFrontDoorOpen;
	private String mStrLeftBackDoorOpen;
	private String mStrRightBackDoorOpen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initData();

		initUI();
	}

	@Override
	protected void onResume() {
		super.onResume();

		updateUI();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initData() {

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch (msg.what) {
				case MSG_UPDATE_UI:
					updateUI();

					if (mLVAllCarInfo.getVisibility() == View.VISIBLE) {
						mAdapter.notifyDataSetChanged();
					}
					break;
				}
			}
		};

		mAdapter = new AllCarInfoAdapter(this);
		Resources res = getResources();
		mStrOilLeft = res.getString(R.string.ci_oil_left);
		mStrOutdoorTemp = res.getString(R.string.ci_outdoor_temp);
		mStrBatteryVoltage = res.getString(R.string.ci_battery_voltage);
		mStrSeatBelts = res.getString(R.string.ci_seat_belts);
		mStrRearDoorOpen = res.getString(R.string.ci_rear_door);
		mStrHandbrake = res.getString(R.string.ci_handbrake);
		mStrCleaningFluid = res.getString(R.string.ci_cleaning_fluid);
		mStrEnginSpeed = res.getString(R.string.ci_engine_speed);
		mStrTravlingSpeed = res.getString(R.string.ci_traveling_speed);
		mStrDrivenDistance = res.getString(R.string.ci_driven_distance);

		mStrStatusNormal = res.getString(R.string.ci_status_normal);
		mStrStatusNoLine = res.getString(R.string.ci_status_no_line);
		mStrStatusNoRelease = res.getString(R.string.ci_status_no_release);
		mStrStatusNoClose = res.getString(R.string.ci_status_no_close);
		mStrStatusLow = res.getString(R.string.ci_status_low);

		mStrLeftFrontDoorOpen = res.getString(R.string.ci_left_front_door_open);
		mStrRightFrontDoorOpen = res
				.getString(R.string.ci_right_front_door_open);
		mStrLeftBackDoorOpen = res.getString(R.string.ci_left_back_door_open);
		mStrRightBackDoorOpen = res.getString(R.string.ci_left_back_door_open);
	}

	private void initUI() {
		setContentView(R.layout.canbus_ci_activity);
		mLayoutCarInfo = (LinearLayout) findViewById(R.id.layout_car_info);

		mTVOilLeft = (TextView) findViewById(R.id.tv_oil_left);
		mTVOutdoorTemp = (TextView) findViewById(R.id.tv_outdoor_temp);
		mTVBatteryVoltage = (TextView) findViewById(R.id.tv_battery_voltage);
		mTVSeatBelts = (TextView) findViewById(R.id.tv_seat_belts);
		mTVRearDoorOpen = (TextView) findViewById(R.id.tv_rear_door_open);
		mTVHandbrake = (TextView) findViewById(R.id.tv_handbrake);
		mTVCleaningFluid = (TextView) findViewById(R.id.tv_cleaning_fluid);
		mTVEngineSpeed = (TextView) findViewById(R.id.tv_engine_speed);
		mTVTravelingspeed = (TextView) findViewById(R.id.tv_traveling_speed);
		mTVDrivenDistance = (TextView) findViewById(R.id.tv_driven_distance);
		mTVDoorOpenWarning = (TextView) findViewById(R.id.tv_door_open_warning);

		mIVLeftFrontDoor = (ImageView) findViewById(R.id.iv_left_front_door);
		mIVRightFrontDoor = (ImageView) findViewById(R.id.iv_right_front_door);
		mIVLeftBackDoor = (ImageView) findViewById(R.id.iv_left_back_door);
		mIVRightBackDoor = (ImageView) findViewById(R.id.iv_right_back_door);
		mIVFueling = (ImageView) findViewById(R.id.iv_fueling);
		mIVFueling.setVisibility(View.GONE);
		mIVFrontCoverOpen = (ImageView) findViewById(R.id.iv_front_cover_open);
		mIVFrontCoverOpen.setVisibility(View.GONE);
		mIVRearDoorOpen = (ImageView) findViewById(R.id.iv_rear_door_open);

		ImageButton ibCarInfo = (ImageButton) findViewById(R.id.ib_car_info);
		ibCarInfo.setOnClickListener(this);

		mLVAllCarInfo = (ListView) findViewById(R.id.lv_all_car_info);
		mLVAllCarInfo.setAdapter(mAdapter);
		mLVAllCarInfo.setCacheColorHint(0);
	}

	protected void updateUI() {
		if (isConnectedService()) {

			StringBuffer strBuffer = new StringBuffer();

			strBuffer.setLength(0);
			strBuffer.append(mStrOutdoorTemp);
			strBuffer.append("\n");
			strBuffer.append(getCar().getOutdoorTemp());
			strBuffer.append(" ℃");
			mTVOutdoorTemp.setText(strBuffer.toString());

			strBuffer.setLength(0);
			strBuffer.append(mStrSeatBelts);
			strBuffer.append("\n");
			if (getCar().getSeatBelts() == CANBUS_DATA_OFF
					&& getCar().getDriverBelts() == CANBUS_DATA_OFF) {
				strBuffer.append(mStrStatusNormal);

				mTVSeatBelts.setSelected(false);
			} else {
				strBuffer.append(mStrStatusNoLine);

				mTVSeatBelts.setSelected(true);
			}
			mTVSeatBelts.setText(strBuffer.toString());

			strBuffer.setLength(0);
			strBuffer.append(mStrHandbrake);
			strBuffer.append("\n");
			if (getCar().getHandbrake() == CANBUS_DATA_OFF) {
				strBuffer.append(mStrStatusNormal);

				mTVHandbrake.setSelected(false);
			} else {
				strBuffer.append(mStrStatusNoRelease);

				mTVHandbrake.setSelected(true);
			}
			mTVHandbrake.setText(strBuffer.toString());

			strBuffer.setLength(0);
			strBuffer.append(mStrCleaningFluid);
			strBuffer.append("\n");
			if (getCar().getCleaningFluid() == CANBUS_DATA_OFF) {
				strBuffer.append(mStrStatusNormal);

				mTVCleaningFluid.setSelected(false);
			} else {
				strBuffer.append(mStrStatusLow);

				mTVCleaningFluid.setSelected(true);
			}
			mTVCleaningFluid.setText(strBuffer.toString());

			strBuffer.setLength(0);
			strBuffer.append(mStrEnginSpeed);
			strBuffer.append("\n");
			strBuffer.append(getCar().getEnginSpeed());
			strBuffer.append(" Rpm");
			mTVEngineSpeed.setText(strBuffer.toString());

			strBuffer.setLength(0);
			strBuffer.append(mStrTravlingSpeed);
			strBuffer.append("\n");
			strBuffer.append(getCar().getTravelingSpeed());
			strBuffer.append(" Km/h");
			mTVTravelingspeed.setText(strBuffer.toString());

			strBuffer.setLength(0);
			strBuffer.append(mStrDrivenDistance);
			strBuffer.append("\n");
			strBuffer.append(getCar().getDrivenDistance());
			strBuffer.append(" Km");
			mTVDrivenDistance.setText(strBuffer.toString());

			updateOilUI();

			updateBatteryVotageUI();

			updateDoorUI();
		}
	}

	private void updateOilUI() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append(mStrOilLeft);
		strBuffer.append("\n");

		if (getCar().getOilLow() == CANBUS_DATA_OFF) {
			strBuffer.append(getCar().getOilLeft());
			strBuffer.append(" L");

			mTVOilLeft.setSelected(false);
		} else {
			strBuffer.append(mStrStatusLow);

			mTVOilLeft.setSelected(true);
		}

		mTVOilLeft.setText(strBuffer.toString());
	}

	private void updateBatteryVotageUI() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.setLength(0);
		strBuffer.append(mStrBatteryVoltage);
		strBuffer.append("\n");

		if (getCar().getBatteryLow() == CANBUS_DATA_OFF) {
			strBuffer.append(getCar().getBatteryVoltage());
			strBuffer.append(" V");

			mTVBatteryVoltage.setSelected(false);
		} else {
			strBuffer.append(mStrStatusLow);

			mTVBatteryVoltage.setSelected(true);
		}

		mTVBatteryVoltage.setText(strBuffer.toString());
	}

	private void updateDoorUI() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append(mStrRearDoorOpen);
		strBuffer.append("\n");

		if (getCar().getBonnet() == CANBUS_DATA_OFF) {
			mIVFrontCoverOpen.setSelected(false);

			mIVFrontCoverOpen.setVisibility(View.GONE);
		} else {
			mIVFrontCoverOpen.setSelected(true);

			mIVFrontCoverOpen.setVisibility(View.VISIBLE);
		}

		if (getCar().getRearDoor() == CANBUS_DATA_OFF) {
			strBuffer.append(mStrStatusNormal);

			mTVRearDoorOpen.setSelected(false);

			mIVRearDoorOpen.setVisibility(View.GONE);
		} else {
			strBuffer.append(mStrStatusNoClose);

			mTVRearDoorOpen.setSelected(true);

			mIVRearDoorOpen.setVisibility(View.VISIBLE);
		}
		mTVRearDoorOpen.setText(strBuffer.toString());

		if (getCar().getLeftFrontDoor() == CANBUS_DATA_OFF) {
			mIVLeftFrontDoor.setVisibility(View.GONE);
		} else {
			mIVLeftFrontDoor.setVisibility(View.VISIBLE);

			mTVDoorOpenWarning.setText(mStrLeftFrontDoorOpen);
		}

		if (getCar().getRightFrontDoor() == CANBUS_DATA_OFF) {
			mIVRightFrontDoor.setVisibility(View.GONE);
		} else {
			mIVRightFrontDoor.setVisibility(View.VISIBLE);

			mTVDoorOpenWarning.setText(mStrRightFrontDoorOpen);
		}

		if (getCar().getLeftBackDoor() == CANBUS_DATA_OFF) {
			mIVLeftBackDoor.setVisibility(View.GONE);
		} else {
			mIVLeftBackDoor.setVisibility(View.VISIBLE);

			mTVDoorOpenWarning.setText(mStrLeftBackDoorOpen);
		}

		if (getCar().getRightBackDoor() == CANBUS_DATA_OFF) {
			mIVRightBackDoor.setVisibility(View.GONE);
		} else {
			mIVRightBackDoor.setVisibility(View.VISIBLE);

			mTVDoorOpenWarning.setText(mStrRightBackDoorOpen);
		}
	}

	@Override
	public void onBackPressed() {
		if (mLVAllCarInfo.getVisibility() == View.VISIBLE) {
			mLayoutCarInfo.setVisibility(View.VISIBLE);
			mLVAllCarInfo.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.ib_car_info:
			mLayoutCarInfo.setVisibility(View.GONE);
			mLVAllCarInfo.setVisibility(View.VISIBLE);

			mAdapter.notifyDataSetChanged();
			break;
		}
	}

	@SuppressLint("InflateParams")
	private class AllCarInfoAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater mInflater;

		public class ViewHolder {
			TextView mTVTitle;
			TextView mTVData;
			TextView mTVUnit;
		}

		public AllCarInfoAdapter(Context context) {
			mContext = context;
			mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		@Override
		public int getCount() {
			return mNeedShowSystemInfo.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listview_item_layout,
						null);
				holder.mTVTitle = (TextView) convertView
						.findViewById(R.id.tv_item_title);
				holder.mTVData = (TextView) convertView
						.findViewById(R.id.tv_item_data);
				holder.mTVUnit = (TextView) convertView
						.findViewById(R.id.tv_item_unit);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			updateSystemInfo(position, holder);

			return convertView;
		}

		private void updateSystemInfo(int position, ViewHolder holder) {
			switch (mNeedShowSystemInfo[position]) {
			case OIL_LEFT:
				holder.mTVTitle.setText(mStrOilLeft);
				if (getCar().getOilLow() == CANBUS_DATA_OFF) {
					holder.mTVData.setText(getCar().getOilLeft() + "");
					holder.mTVUnit.setText("L");

					holder.mTVUnit.setVisibility(View.VISIBLE);
				} else {
					holder.mTVData.setText(mStrStatusLow);

					holder.mTVUnit.setVisibility(View.INVISIBLE);
				}
				break;

			case OUTDOOR_TEMP:
				holder.mTVTitle.setText(mStrOutdoorTemp);
				holder.mTVData.setText(getCar().getOutdoorTemp() + "");
				holder.mTVUnit.setText("℃");
				break;

			case BATTERY_VOLTAGE:
				holder.mTVTitle.setText(mStrBatteryVoltage);
				if (getCar().getBatteryLow() == CANBUS_DATA_OFF) {
					holder.mTVData.setText(getCar().getBatteryVoltage() + "");
					holder.mTVUnit.setText("V");

					holder.mTVUnit.setVisibility(View.VISIBLE);
				} else {
					holder.mTVData.setText(mStrStatusLow);

					holder.mTVUnit.setVisibility(View.INVISIBLE);
				}
				break;

			case ENGINE_SPEED:
				holder.mTVTitle.setText(mStrEnginSpeed);
				holder.mTVData.setText(getCar().getEnginSpeed() + "");
				holder.mTVUnit.setText("Rpm");
				break;

			case TRAVLING_SPEED:
				holder.mTVTitle.setText(mStrTravlingSpeed);
				holder.mTVData.setText(getCar().getTravelingSpeed() + "");
				holder.mTVUnit.setText("Km/h");
				break;

			case DRIVEN_DISTANCE:
				holder.mTVTitle.setText(mStrDrivenDistance);
				holder.mTVData.setText(getCar().getDrivenDistance() + "");
				holder.mTVUnit.setText("Km");
				break;

			default:
				break;
			}
		}
	}
}
