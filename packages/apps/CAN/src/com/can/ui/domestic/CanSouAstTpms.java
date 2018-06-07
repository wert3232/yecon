package com.can.ui.domestic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.TPMSInfo;
import com.can.ui.CanFrament;
import com.can.ui.draw.ResDef;

public class CanSouAstTpms extends CanFrament {

	private View mObjView = null;
	private TextView[] mObjTextViews = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mObjHandler, this.getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mObjView == null) {
			mObjView = inflater.inflate(R.layout.souast_tpms, container, false);
			init();
		}
		return mObjView;
	}

	@Override
	public void onDestroy() {
		super.DeInit();
		super.onDestroy();
	}

	@Override
	public void onResume() {
		getData(DDef.TPMS_CMD_ID);
		super.onResume();
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
		for (@SuppressWarnings("unused") int iter : ResDef.mDz7TpmsText) {
			String strInfo = "";
			switch (iIndex) {
			case 0:
				strInfo = String.valueOf(data.mFLTirePressure);
				break;
			case 1:
				strInfo = String.valueOf(data.mFRTirePressure);
				break;
			case 2:
				strInfo = String.valueOf(data.mBLTirePressure);
				break;
			case 3:
				strInfo = String.valueOf(data.mBRTirePressure);
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
			switch (msg.what) {
			case DDef.FINISH_BIND:
				getData(DDef.TPMS_CMD_ID);
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
	
}
