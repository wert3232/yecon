package com.can.ui.jeep;

import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.CDState;
import com.can.parser.DDef.CDTxInfo;
import com.can.parser.Protocol;
import com.can.parser.raise.jeep.ReJeepProtocol;
import com.can.tool.DataConvert;
import com.can.ui.CanFrament;
import com.can.ui.draw.FuelSeekBar;
import com.can.ui.draw.ResDef;

/**
 * ClassName:CanJeepCDInfo
 * 
 * @function:TODO
 * @author Kim
 * @Date: 2016-7-14下午5:15:11
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanJeepCDInfo extends CanFrament implements OnClickListener,
		OnItemClickListener {

	private View mObjView = null;
	private Protocol mObjProtocol = null;

	private TextView[] mObjBtnJeepCdInfo = null;
	private FuelSeekBar mObjSeekbarPlayPos = null;
	private TextView mObjTextPlayMode = null;
	private TextView mObjTextCdstate = null;
	private TextView mObjTextTrackNum = null;
	private TextView mObjTextPlayTime = null;
	private ListView mObjListJeepInfo = null;
	private MenuAdapter mObjAdapter = null;

	private int miListSel = -1;
	private HashMap<Integer, String> mHashMap = new HashMap<Integer, String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.Init(mObjHandler, getTag());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (mObjView == null) {
			mObjView = inflater.inflate(R.layout.jeep_cdinfo, container, false);
			init();
		}

		return mObjView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		ReJeepProtocol.mbCdInfo = false;
		super.DeInit();
		super.onDestroy();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	private void init() {
		if (mObjView != null) {

			int iIndex = 0;
			mObjBtnJeepCdInfo = new TextView[ResDef.mJeepCDInfoBtn.length];
			for (int iter : ResDef.mJeepCDInfoBtn) {
				mObjBtnJeepCdInfo[iIndex] = (TextView) mObjView
						.findViewById(iter);
				mObjBtnJeepCdInfo[iIndex].setOnClickListener(this);
				iIndex++;
			}

			mObjSeekbarPlayPos = (FuelSeekBar) mObjView
					.findViewById(R.id.bar_jeep_cdinfo_process);
			mObjTextPlayMode = (TextView) mObjView
					.findViewById(R.id.tx_jeep_cdinfo_mode);
			mObjTextCdstate = (TextView) mObjView
					.findViewById(R.id.tx_jeep_cdinfo_state);
			mObjTextTrackNum = (TextView) mObjView
					.findViewById(R.id.tx_jeep_cdinfo_track);
			mObjTextPlayTime = (TextView) mObjView
					.findViewById(R.id.tx_jeep_cdinfo_cur_time);

			mObjListJeepInfo = (ListView) mObjView
					.findViewById(R.id.list_jeep_cd_info);
			mObjAdapter = new MenuAdapter(getActivity());
			mObjListJeepInfo.setAdapter(mObjAdapter);
			mObjListJeepInfo.setOnItemClickListener(this);
		}
	}

	private void setSate(CDState objCdstate) {

		if (objCdstate.mbyCDStatus == 4) {
			getActivity().finish();
		}

		if (mObjTextPlayMode != null) {
			if (objCdstate.mbyCDPlayMode <= ResDef.mJeepCDPlayMode.length) {
				mObjTextPlayMode
						.setText(getString(ResDef.mJeepCDPlayMode[objCdstate.mbyCDPlayMode]));
			}
		}

		if (mObjTextCdstate != null) {
			if (objCdstate.mbyCDStatus <= ResDef.mJeepCDState.length) {
				mObjTextCdstate
						.setText(getString(ResDef.mJeepCDState[objCdstate.mbyCDStatus]));
			}
		}

		StringBuffer strInfo = new StringBuffer();

		if (mObjTextTrackNum != null) {
			strInfo.append(objCdstate.miCDCurTrack);
			strInfo.append("/");
			strInfo.append(objCdstate.miCDTotalTrack);
			mObjTextTrackNum.setText(strInfo.toString());
		}

		strInfo.delete(0, strInfo.length());
		if (mObjTextPlayTime != null) {
			strInfo.append(objCdstate.mbyCDPlayHour);
			strInfo.append(":");
			strInfo.append(objCdstate.mbyCDPlayMin);
			strInfo.append(":");
			strInfo.append(objCdstate.mbyCDPlaySec);
			strInfo.append("/");
			int ihour = objCdstate.miCDCurTrackTimeNum / 3600;
			int imin = (objCdstate.miCDCurTrackTimeNum - ihour * 3600) / 60;
			int iSec = (objCdstate.miCDCurTrackTimeNum - ihour * 3600) % 60;
			strInfo.append(ihour);
			strInfo.append(":");
			strInfo.append(imin);
			strInfo.append(":");
			strInfo.append(iSec);

			mObjTextPlayTime.setText(strInfo.toString());
		}

		if (mObjSeekbarPlayPos != null) {
			mObjSeekbarPlayPos.setMax(objCdstate.miCDCurTrackTimeNum);
			int ipos = objCdstate.mbyCDPlayHour * 3600
					+ objCdstate.mbyCDPlayMin * 60 + objCdstate.mbyCDPlaySec;
			mObjSeekbarPlayPos.setProgress(ipos);
		}
	}

	private void setCdlistInfo(CDTxInfo objCdTxInfo) {
		if (objCdTxInfo.miType == 0x80) {
			mHashMap.put(objCdTxInfo.miTrackNum, objCdTxInfo.mstrText);
			mObjAdapter.notifyDataSetChanged();
		}
	}

	private Handler mObjHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mObjProtocol = (Protocol) msg.obj;
				Inquiry();
				break;
			case DDef.CD_STATE_ID:
				setSate((CDState) msg.obj);
				break;
			case DDef.CD_TX_INFO_ID:
				setCdlistInfo((CDTxInfo) msg.obj);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};

	private void onHandle(int id) {
		int icmd = 0xFF;
		switch (id) {
		case R.id.btn_jeep_cdinfo_pre:
			icmd = 0x02;
			break;
		case R.id.btn_jeep_cdinfo_play:
			icmd = 0x13;
			if (mObjBtnJeepCdInfo[2] != null && mObjBtnJeepCdInfo[1] != null) {
				mObjBtnJeepCdInfo[1].setVisibility(View.GONE);
				mObjBtnJeepCdInfo[2].setVisibility(View.VISIBLE);
			}
			break;
		case R.id.btn_jeep_cdinfo_pause:
			icmd = 0x14;
			if (mObjBtnJeepCdInfo[2] != null && mObjBtnJeepCdInfo[1] != null) {
				mObjBtnJeepCdInfo[1].setVisibility(View.VISIBLE);
				mObjBtnJeepCdInfo[2].setVisibility(View.GONE);
			}
			break;
		case R.id.btn_jeep_cdinfo_next:
			icmd = 0x01;
			break;
		case R.id.btn_jeep_cdinfo_f_pre:
			icmd = 0x03;
			break;
		case R.id.btn_jeep_cdinfo_f_next:
			icmd = 0x04;
			break;
		case R.id.btn_jeep_cdinfo_repeat_on:
			icmd = 0x11;
			if (mObjBtnJeepCdInfo[6] != null && mObjBtnJeepCdInfo[7] != null) {
				mObjBtnJeepCdInfo[6].setVisibility(View.GONE);
				mObjBtnJeepCdInfo[7].setVisibility(View.VISIBLE);
			}
			break;
		case R.id.btn_jeep_cdinfo_repeat_off:
			icmd = 0x11;
			if (mObjBtnJeepCdInfo[6] != null && mObjBtnJeepCdInfo[7] != null) {
				mObjBtnJeepCdInfo[6].setVisibility(View.VISIBLE);
				mObjBtnJeepCdInfo[7].setVisibility(View.GONE);
			}
			break;
		case R.id.btn_jeep_cdinfo_random_on:
			icmd = 0x08;
			if (mObjBtnJeepCdInfo[8] != null && mObjBtnJeepCdInfo[9] != null) {
				mObjBtnJeepCdInfo[8].setVisibility(View.GONE);
				mObjBtnJeepCdInfo[9].setVisibility(View.VISIBLE);
			}
			break;
		case R.id.btn_jeep_cdinfo_random_off:
			icmd = 0x08;
			if (mObjBtnJeepCdInfo[8] != null && mObjBtnJeepCdInfo[9] != null) {
				mObjBtnJeepCdInfo[8].setVisibility(View.VISIBLE);
				mObjBtnJeepCdInfo[9].setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}

		if (icmd != 0xFF) {
			byte[] bydata = new byte[3];
			bydata[0] = (byte) icmd;
			bydata[1] = 0x00;
			bydata[2] = 0x00;
			super.sendMsg(ReJeepProtocol.DATA_TYPE_CD_CTRL, bydata,
					mObjProtocol);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		onHandle(arg0.getId());
	}

	public class MenuAdapter extends BaseAdapter {

		private Context mObjContext;
		private LayoutInflater mObjInflater;

		public MenuAdapter(Context context) {
			// TODO Auto-generated constructor stub
			mObjContext = context;
			mObjInflater = (LayoutInflater) mObjContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mHashMap.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mHashMap.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView objTextView = null;

			if (convertView == null) {
				convertView = mObjInflater.inflate(R.layout.jeep_list, null);
				objTextView = (TextView) convertView
						.findViewById(R.id.tx_jeep_list_info);

				convertView.setTag(objTextView);
			} else {
				objTextView = (TextView) convertView.getTag();
			}

			if (objTextView != null) {

				if (mHashMap.get(position) != null) {

					StringBuffer strInfo = new StringBuffer();
					strInfo.append(position);
					strInfo.append("    ");
					strInfo.append(mHashMap.get(position));
					objTextView.setText(strInfo.toString());
				} else {
					objTextView.setText("");
				}

				if (miListSel == position) {
					objTextView.setBackgroundResource(R.drawable.ford_lsit_s);
				} else {
					objTextView.setBackgroundResource(R.drawable.ford_lsit_n);
				}
			}

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (miListSel != arg2) {
			miListSel = arg2;

			if (mObjProtocol != null) {
				byte[] bydata = new byte[3];
				bydata[0] = 0x0f;
				byte[] itmp = DataConvert.Hi2Lo2Byte(arg2);
				bydata[1] = itmp[1];
				bydata[2] = itmp[0];
				super.sendMsg(ReJeepProtocol.DATA_TYPE_CD_CTRL, bydata,
						mObjProtocol);
			}
		}

		mObjAdapter.notifyDataSetChanged();
	}

	private void Inquiry() {
		byte[] bydata = new byte[3];
		bydata[0] = (byte) 0x82;
		bydata[1] = 0x00;
		bydata[2] = 0x00;
		super.sendMsg(ReJeepProtocol.DATA_TYPE_CD_CTRL, bydata, mObjProtocol);
		ReJeepProtocol.mbCdInfo = true;
	}
}
