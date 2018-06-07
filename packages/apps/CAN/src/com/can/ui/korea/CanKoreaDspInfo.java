package com.can.ui.korea;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.PowerAmplifier;
import com.can.parser.Protocol;
import com.can.parser.raise.korea.ReKoreaProtocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.DspBalance;
import com.can.ui.draw.DspBalance.OnTouchListener;

public class CanKoreaDspInfo extends CanFrament implements OnClickListener,
		OnSeekBarChangeListener {

	private final int BALANCE_MAX = 14;
	private View mObjView = null;
	private Protocol mObjProtocol = null;

	private SeekBar mObjSeekbarBass = null;
	private SeekBar mObjSeekbarMid = null;
	private SeekBar mObjSeekbarTre = null;
	private DspBalance mObjDspBalance = null;

	private TextView mObjTextBassVal = null;
	private TextView mObjTextMidVal = null;
	private TextView mObjTextTreVal = null;
	
	private int miFad = 0;
	private int miBal = 0;

	private byte[] byEqData = new byte[3];
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mObjhHandler, getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mObjView == null) {
			mObjView = inflater.inflate(R.layout.korea_dsp, container, false);
			init();
		}
		return mObjView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private void init() {
		if (mObjView != null) {

			mObjSeekbarBass = (SeekBar) mObjView.findViewById(R.id.korea_dsp_bass);
			mObjSeekbarMid = (SeekBar) mObjView.findViewById(R.id.korea_dsp_mid);
			mObjSeekbarTre = (SeekBar) mObjView.findViewById(R.id.korea_dsp_tre);

			mObjDspBalance = (DspBalance) mObjView.findViewById(R.id.korea_dsp_balance);
			mObjDspBalance.setDefMaxVal(14);
			mObjDspBalance.setBalenceListener(objListener);
			mObjDspBalance.setBalanceVal(0, 0);
			
			mObjTextBassVal = (TextView) mObjView.findViewById(R.id.korea_dsp_bass_val);
			mObjTextMidVal = (TextView) mObjView.findViewById(R.id.korea_dsp_mid_val);
			mObjTextTreVal = (TextView) mObjView.findViewById(R.id.korea_dsp_tre_val);
			mObjView.findViewById(R.id.korea_im_add_front).setOnClickListener(this);
			mObjView.findViewById(R.id.korea_im_add_rear).setOnClickListener(this);
			mObjView.findViewById(R.id.korea_im_add_left).setOnClickListener(this);
			mObjView.findViewById(R.id.korea_im_add_right).setOnClickListener(this);

			mObjSeekbarBass.setOnSeekBarChangeListener(this);
			mObjSeekbarMid.setOnSeekBarChangeListener(this);
			mObjSeekbarTre.setOnSeekBarChangeListener(this);
			mObjSeekbarBass.setMax(20);
			mObjSeekbarMid.setMax(20);
			mObjSeekbarTre.setMax(20);
			
			mObjSeekbarBass.setProgress(10);
			mObjSeekbarMid.setProgress(10);
			mObjSeekbarTre.setProgress(10);
		}
	}

	@Override
	public void onClick(View v) {
		if (mObjDspBalance != null) {
		
			switch (v.getId()) {
			case R.id.korea_im_add_front:
				miBal -= 1;
				break;
			case R.id.korea_im_add_rear:
				miBal += 1;
				break;
			case R.id.korea_im_add_left:
				miFad -= 1;
				break;
			case R.id.korea_im_add_right:
				miFad += 1;
				break;
			}
			
			mObjDspBalance.setBalanceVal(miFad, miBal);
		}
	}


	private Handler mObjhHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mObjProtocol = (Protocol) msg.obj;
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
		int iVal = progress;
		StringBuffer strVal = new StringBuffer();
		iVal -= 10;
		strVal.append(iVal);

		switch (seekBar.getId()) {
		case R.id.korea_dsp_bass:
			if (mObjTextBassVal != null) {
				mObjTextBassVal.setText(strVal.toString());
				byEqData[0] = (byte) progress;
			}
			break;
		case R.id.korea_dsp_mid:
			if (mObjTextMidVal != null) {
				mObjTextMidVal.setText(strVal.toString());
				byEqData[1] = (byte) progress;
			}
			break;
		case R.id.korea_dsp_tre:
			if (mObjTextTreVal != null) {
				mObjTextTreVal.setText(strVal.toString());
				byEqData[2] = (byte) progress;
			}
			break;
		default:
			return;
		}
		sendMsg(ReKoreaProtocol.DATA_TYPE_EQ_REQUEST, byEqData, mObjProtocol);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	private OnTouchListener objListener = new OnTouchListener() {
		
		@Override
		public void onBalance(float fFad, float fBal) {
			int iFad = (int)fFad;
			int iBal = (int)fBal;
			if (iFad > BALANCE_MAX || iFad < 0 
				|| iBal > BALANCE_MAX || iBal < 0 ) {
				return;
			} 
			
			miFad = iFad;
			miBal = iBal;
			byte[] bydata = new byte[2];
			bydata[0] = (byte) miFad;
			bydata[1] = (byte) miBal;

			sendMsg(ReKoreaProtocol.DATA_TYPE_FADE_BAL_REQUEST, bydata, mObjProtocol);
		}
	};
}
