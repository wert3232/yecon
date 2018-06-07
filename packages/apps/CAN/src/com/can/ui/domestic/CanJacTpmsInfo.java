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
import com.can.parser.DDef.TpmsWarn;
import com.can.ui.CanFrament;
import com.can.ui.draw.ResDef;

/**
 * ClassName:CanJacTpmsInfo
 * 
 * @function:TODO
 * @author Kim
 * @Date: 2016-8-4下午3:46:08
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanJacTpmsInfo extends CanFrament {

	private View mObjView = null;
	private TextView[] mObjTextViews = null;
	private TpmsWarn mObjTpmsWarn = new TpmsWarn();

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
			mObjView = inflater.inflate(R.layout.jac_tpms, container, false);
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
		super.getData(DDef.TPMS_CMD_ID);
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
		for (TextView iter : mObjTextViews) {
			strInfo.delete(0, strInfo.length());

			switch (iIndex) {
			case 0:
				strInfo.append(getString((mObjTpmsWarn.mbyFLTireWarn > 0) ? R.string.str_jac_tpms_warn
						: R.string.str_jac_tpms_normal));
				strInfo.append("\n");
				strInfo.append("Temp:");
				strInfo.append(data.mFLTireTemp);
				strInfo.append(getString(R.string.ac_temp_unit_c));
				strInfo.append("\n");
				strInfo.append(data.mfFLTirePressure);
				strInfo.append("BAR");
				break;
			case 1:
				strInfo.append(getString((mObjTpmsWarn.mbyFRTireWarn > 0) ? R.string.str_jac_tpms_warn
						: R.string.str_jac_tpms_normal));
				strInfo.append("\n");
				strInfo.append("Temp:");
				strInfo.append(data.mFRTireTemp);
				strInfo.append(getString(R.string.ac_temp_unit_c));
				strInfo.append("\n");
				strInfo.append(data.mfFRTirePressure);
				strInfo.append("BAR");
				break;
			case 2:
				strInfo.append(getString((mObjTpmsWarn.mbyBLTireWarn > 0) ? R.string.str_jac_tpms_warn
						: R.string.str_jac_tpms_normal));
				strInfo.append("\n");
				strInfo.append("Temp:");
				strInfo.append(data.mBLTireTemp);
				strInfo.append(getString(R.string.ac_temp_unit_c));
				strInfo.append("\n");
				strInfo.append(data.mfBLTirePressure);
				strInfo.append("BAR");

				break;
			case 3:	
				strInfo.append(getString((mObjTpmsWarn.mbyBRTireWarn > 0) ? R.string.str_jac_tpms_warn
						: R.string.str_jac_tpms_normal));
				strInfo.append("\n");
				strInfo.append("Temp:");
				strInfo.append(data.mBRTireTemp);
				strInfo.append(getString(R.string.ac_temp_unit_c));
				strInfo.append("\n");
				strInfo.append(data.mfBRTirePressure);
				strInfo.append("BAR");
				break;
			}

			if (iter != null) {
				iter.setText(strInfo);
			}

			iIndex++;
		}
	}

	private void setTpmsWarn(TpmsWarn tpmsWarn) {
		mObjTpmsWarn = tpmsWarn;
		super.getData(DDef.TPMS_CMD_ID);
	}

	private Handler mObjHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case DDef.FINISH_BIND:
				break;
			case DDef.TPMS_CMD_ID:
				setAttr((TPMSInfo) msg.obj);
				break;
			case DDef.SYSINFO_CMD_ID:
				setTpmsWarn((TpmsWarn) msg.obj);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};
}
