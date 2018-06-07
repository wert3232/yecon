package com.can.ui.mazda;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.Protocol;
import com.can.parser.raise.mazda.ReMazdaProtocol;
import com.can.ui.CanFrament;

/**
 * ClassName:CanMazdaCarInfo
 * 
 * @function:TODO
 * @author Kim
 * @Date: 2016-7-18下午2:24:16
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanMazdaCarInfo extends CanFrament implements OnClickListener {

	private View mObjView = null;
	private Protocol mObjProtocol = null;

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
			mObjView = inflater.inflate(R.layout.mazda_info, container, false);
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
			mObjView.findViewById(R.id.btn_mazda_sethour).setOnClickListener(
					this);
			mObjView.findViewById(R.id.btn_mazda_setmin).setOnClickListener(
					this);
			mObjView.findViewById(R.id.btn_mazda_setsen).setOnClickListener(
					this);
		}
	}

	public Handler mObjHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
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
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_mazda_sethour:
		case R.id.btn_mazda_setmin:
		case R.id.btn_mazda_setsen:
			setClock(arg0.getId());
			break;
		default:
			break;
		}
	}

	private void setClock(int id) {

		byte[] bydata = new byte[2];
		bydata[0] = 0x00;

		if (id == R.id.btn_mazda_sethour) {
			bydata[1] = 0x40;
		} else if (id == R.id.btn_mazda_setmin) {
			bydata[1] = (byte) 0x80;
		} else if (id == R.id.btn_mazda_setsen) {
			bydata[1] = 0x20;
		}

		super.sendMsg(ReMazdaProtocol.DATA_TYPE_SET_CLOCK, bydata, mObjProtocol);

		bydata[1] = 0x00;
		SystemClock.sleep(100);
		super.sendMsg(ReMazdaProtocol.DATA_TYPE_SET_CLOCK, bydata, mObjProtocol);
	}
}
