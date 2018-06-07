package com.can.ui.dz;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.Protocol;
import com.can.parser.DDef.TPMSInfo;
import com.can.parser.raise.dz.ReMQB7Protocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.ResDef;

/**
 * ClassName:CanDz7Tpms
 * 
 * @function:胎压信息
 * @author Kim
 * @Date: 2016-7-8下午2:59:33
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanDz7Tpms extends CanFrament {

	private View mObjView = null;
	private TextView[] mObjTextViews = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.Init(mObjHandler, this.getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (mObjView == null) {
			mObjView = inflater.inflate(R.layout.dz7_tpms, container, false);
			init();
		}

		return mObjView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
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
			mObjTextViews = new TextView[ResDef.mDz7TpmsText.length];
			for (int iter : ResDef.mDz7TpmsText) {
				mObjTextViews[iIndex] = (TextView) mObjView.findViewById(iter);
				iIndex++;
			}
		}
	}

	private void setAttr(TPMSInfo data) {

		int iIndex = 0;
		StringBuffer strInfo = new StringBuffer();
		for (@SuppressWarnings("unused") int iter : ResDef.mDz7TpmsText) {
			strInfo.delete(0, strInfo.length());

			switch (iIndex) {
			case 0:
				strInfo.append(getResources().getString(
						ResDef.mDz7TpmsInfo[data.mFLTirePressure]));
				break;
			case 1:
				strInfo.append(getResources().getString(
						ResDef.mDz7TpmsInfo[data.mFRTirePressure]));
				break;
			case 2:
				strInfo.append(getResources().getString(
						ResDef.mDz7TpmsInfo[data.mBLTirePressure]));
				break;
			case 3:
				strInfo.append(getResources().getString(
						ResDef.mDz7TpmsInfo[data.mBRTirePressure]));
				break;
			}

			if (mObjTextViews[iIndex] != null) {
				mObjTextViews[iIndex].setText(strInfo);
			}

			iIndex++;
		}
	}

	private Handler mObjHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case DDef.FINISH_BIND:
				Inquiry(msg);
				break;
			case DDef.TPMS_CMD_ID:
				setAttr((TPMSInfo) msg.obj);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};
	
	private void Inquiry(Message msg) {
		byte[] bydata = new byte[2];
		bydata[0] = ReMQB7Protocol.DATA_TYPE_TPMS;
		bydata[1] = 0x00;
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, bydata,
				(Protocol) msg.obj);

	}
}
