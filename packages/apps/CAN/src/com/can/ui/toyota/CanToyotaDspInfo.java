package com.can.ui.toyota;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.PowerAmplifier;
import com.can.parser.Protocol;
import com.can.parser.raise.toyota.ReToyotaProtocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.DspBalance;
import com.can.ui.draw.DspBalance.OnTouchListener;

/**
 * ClassName:CanToyotaDspInfo
 * 
 * @function:功放信息
 * @author Kim
 * @Date: 2016-6-23 下午2:30:33
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanToyotaDspInfo extends CanFrament implements OnClickListener,
		OnSeekBarChangeListener, OnCheckedChangeListener {

	private View mObjView = null;
	private Protocol mObjProtocol = null;

	private SeekBar mObjSeekbarBass = null;
	private SeekBar mObjSeekbarMid = null;
	private SeekBar mObjSeekbarTre = null;
	private DspBalance mObjDspBalance = null;

	private TextView mObjTextBassVal = null;
	private TextView mObjTextMidVal = null;
	private TextView mObjTextTreVal = null;

	private CheckBox mObjTextAslstate = null;
	private CheckBox mObjTextsurround = null;

	private int miFad = 0;
	private int miBal = 0;
	private PowerAmplifier mObjDspInfo = new PowerAmplifier();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.Init(mObjhHandler, this.getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (mObjView == null) {

			mObjView = inflater.inflate(R.layout.toyota_dsp, container, false);
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
		super.getData(DDef.DSP_CMD_ID);
		setDspInfo(mObjDspInfo);
	}

	private void init() {
		if (mObjView != null) {

			mObjSeekbarBass = (SeekBar) mObjView
					.findViewById(R.id.toyota_dsp_bass);
			mObjSeekbarMid = (SeekBar) mObjView
					.findViewById(R.id.toyota_dsp_mid);
			mObjSeekbarTre = (SeekBar) mObjView
					.findViewById(R.id.toyota_dsp_tre);

			mObjDspBalance = (DspBalance) mObjView
					.findViewById(R.id.dsp_balance);
			mObjDspBalance.setDefMaxVal(14);
			mObjDspBalance.setBalenceListener(objListener);

			mObjTextBassVal = (TextView) mObjView
					.findViewById(R.id.toyota_dsp_bass_val);
			mObjTextMidVal = (TextView) mObjView
					.findViewById(R.id.toyota_dsp_mid_val);
			mObjTextTreVal = (TextView) mObjView
					.findViewById(R.id.toyota_dsp_tre_val);
			mObjTextAslstate = (CheckBox) mObjView
					.findViewById(R.id.dsp_check_asl);
			mObjTextsurround = (CheckBox) mObjView
					.findViewById(R.id.dsp_check_surround);

			mObjView.findViewById(R.id.im_add_front).setOnClickListener(this);
			mObjView.findViewById(R.id.im_add_rear).setOnClickListener(this);
			mObjView.findViewById(R.id.im_add_left).setOnClickListener(this);
			mObjView.findViewById(R.id.im_add_right).setOnClickListener(this);
			mObjView.findViewById(R.id.toyota_btn_asl).setOnClickListener(this);
			mObjView.findViewById(R.id.toyota_btn_surround).setOnClickListener(
					this);

			mObjSeekbarBass.setOnSeekBarChangeListener(this);
			mObjSeekbarMid.setOnSeekBarChangeListener(this);
			mObjSeekbarTre.setOnSeekBarChangeListener(this);

			mObjTextAslstate.setOnCheckedChangeListener(this);
			mObjTextsurround.setOnCheckedChangeListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mObjDspBalance != null) {

			switch (v.getId()) {
			case R.id.im_add_front:
				miBal -= 1;
				break;
			case R.id.im_add_rear:
				miBal += 1;
				break;
			case R.id.im_add_left:
				miFad -= 1;
				break;
			case R.id.im_add_right:
				miFad += 1;
				break;
			}

			mObjDspBalance.setBalanceVal(miFad, miBal);
		}
	}

	private void setDspInfo(PowerAmplifier objDspInfo) {
		if (this.isVisible()) {
			if (mObjSeekbarBass != null && mObjSeekbarMid != null
					&& mObjSeekbarTre != null) {
				mObjSeekbarBass.setMax(10);
				mObjSeekbarBass.setProgress(objDspInfo.mBASS);
				mObjSeekbarMid.setMax(10);
				mObjSeekbarMid.setProgress(objDspInfo.mMID);
				mObjSeekbarTre.setMax(10);
				mObjSeekbarTre.setProgress(objDspInfo.mTRE);
			}

			if (mObjTextBassVal != null && mObjTextMidVal != null
					&& mObjTextTreVal != null) {
				StringBuffer strVal = new StringBuffer();

				int iVal = (int) objDspInfo.mBASS - 7;
				iVal = (iVal == -7) ? 0 : iVal;
				strVal.append(iVal);
				mObjTextBassVal.setText(strVal.toString());

				strVal.delete(0, strVal.length());
				iVal = (int) objDspInfo.mMID - 7;
				iVal = (iVal == -7) ? 0 : iVal;
				strVal.append(iVal);
				mObjTextMidVal.setText(strVal);

				strVal.delete(0, strVal.length());
				iVal = (int) objDspInfo.mTRE - 7;
				iVal = (iVal == -7) ? 0 : iVal;
				strVal.append(iVal);
				mObjTextTreVal.setText(strVal);
			}

			if (mObjTextAslstate != null && mObjTextsurround != null) {
				if (objDspInfo.mASL == 0x01) {
					mObjTextAslstate.setChecked(false);
				} else if (objDspInfo.mASL == 0x08) {
					mObjTextAslstate.setChecked(true);
				}

				mObjTextsurround.setChecked((objDspInfo.mVolByASL) == 0 ? false
						: true);
			}

			if (mObjDspBalance != null) {
				miFad = objDspInfo.mFAD;
				miBal = objDspInfo.mBAL;
				mObjDspBalance.setBalanceVal(objDspInfo.mFAD, objDspInfo.mBAL);
			}
		}
	}

	private Handler mObjhHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mObjProtocol = (Protocol) msg.obj;
				break;
			case DDef.DSP_CMD_ID:
				mObjDspInfo = (PowerAmplifier)msg.obj;
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		int iVal = progress;
		StringBuffer strVal = new StringBuffer();
		iVal -= 5;
		strVal.append(iVal);

		switch (seekBar.getId()) {
		case R.id.toyota_dsp_bass:
			if (mObjTextBassVal != null) {
				mObjTextBassVal.setText(strVal.toString());
				setVal(0x04, progress + 2);
			}
			break;
		case R.id.toyota_dsp_mid:
			if (mObjTextMidVal != null) {
				mObjTextMidVal.setText(strVal.toString());
				setVal(0x06, progress + 2);
			}
			break;
		case R.id.toyota_dsp_tre:
			if (mObjTextTreVal != null) {
				mObjTextTreVal.setText(strVal.toString());
				setVal(0x05, progress + 2);
			}
			break;
		}
	}

	private void setVal(int icmd, int iVal) {
		// TODO Auto-generated method stub
		byte[] bydata = new byte[2];
		bydata[0] = (byte) icmd;
		bydata[1] = (byte) iVal;

		super.sendMsg(ReToyotaProtocol.DATA_TYPE_DSP_REQUEST, bydata,
				mObjProtocol);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.dsp_check_asl:
			setVal(0x03, isChecked ? 0x08 : 0x01);
			break;
		case R.id.dsp_check_surround:
			setVal(0x09, isChecked ? 0x01 : 0x00);
			break;
		}
	}

	private OnTouchListener objListener = new OnTouchListener() {

		@Override
		public void onBalance(float fFad, float fBal) {
			// TODO Auto-generated method stub
			miFad = (int) fFad;
			miBal = (int) fBal;
			setVal(0x01, (int) fFad);
			setVal(0x02, (int) fBal);
		}
	};
}
