package com.can.ui.honda;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef.UsbIpodInfo;
import com.can.parser.raise.honda.Re12CrvProtocol;
import com.can.parser.DDef;
import com.can.parser.Protocol;
import com.can.ui.CanFrament;

public class CanCrvUsbIpod extends CanFrament implements OnClickListener {
	private Protocol mProtocol = null;
	private View mView = null;
	
	private TextView mTvTrack = null;
	private TextView mTvCurTime = null;
	
	private SeekBar mSeekBar = null;
	private TextView mTvState = null;
	private TextView mTvUsbFolder = null;
	
	private int[] mStateId = { R.string.str_usbipod_close, R.string.str_usbipod_loading,
							   R.string.str_usbipod_uncon, R.string.str_usbipod_con,
							   R.string.str_usbipod_playing, R.string.str_usbipod_pause};
	
	private String[] mStateStr = new String[mStateId.length];
	private TextView mTvUsb = null;
	private TextView mTvIpod = null;
	@Override
	public void showPage(CanFrament frament) {
		super.showPage(frament);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mHandler, getTag());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.honda_usb_ipod, null);
			initView();
		}
		return mView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		byte[] datas = new byte[2];
		datas[0] = 0x01;
		datas[1] = 0x00;
		sendMsg(Re12CrvProtocol.DATA_TYPE_USB_IPOD_REQUEST, datas, mProtocol);
	}

	
	@Override
	public void onPause() {
		super.onPause();
		byte[] datas = new byte[2];
		datas[0] = 0x02;
		datas[1] = 0x00;
		sendMsg(Re12CrvProtocol.DATA_TYPE_USB_IPOD_REQUEST, datas, mProtocol);
	}

	@Override
	public void onDestroy() {
		super.DeInit();
		super.onDestroy();
	}

	private void initView() {
		if (mView != null) {
			mView.findViewById(R.id.usb_ipod_btn_pre).setOnClickListener(this);
			mView.findViewById(R.id.usb_ipod_btn_next).setOnClickListener(this);
			mView.findViewById(R.id.usb_ipod_btn_f_next).setOnClickListener(this);
			mView.findViewById(R.id.usb_ipod_btn_f_pre).setOnClickListener(this);
			mView.findViewById(R.id.usb_ipod_btn_play).setOnClickListener(this);
			mView.findViewById(R.id.usb_ipod_btn_pause).setOnClickListener(this);
			
			mSeekBar = (SeekBar) mView.findViewById(R.id.usb_ipod_process);
			mTvTrack = (TextView) mView.findViewById(R.id.usb_ipod_track);
			mTvCurTime = (TextView) mView.findViewById(R.id.usb_ipod_cur_time);
			mTvState = (TextView) mView.findViewById(R.id.usb_ipod_state);
			mTvUsb = (TextView) mView.findViewById(R.id.usb_ipod_tv_usb);
			mTvIpod = (TextView) mView.findViewById(R.id.usb_ipod_tv_ipod);
			mTvUsbFolder = (TextView) mView.findViewById(R.id.usb_ipod_folder_index);
			
			mSeekBar.setMax(100);
			
			for (int i = 0; i < mStateId.length; i++) {
				mStateStr[i] = getString(mStateId[i]);
			}
		}
		
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mProtocol = (Protocol) msg.obj;
				byte[] datas = new byte[2];
				datas[0] = 0x01;
				datas[1] = 0x00;
				sendMsg(Re12CrvProtocol.DATA_TYPE_USB_IPOD_REQUEST, datas, mProtocol);
				break;
			case DDef.USB_IPOD_INFO:
				setUsbIpod((UsbIpodInfo)msg.obj);
				break;
			default:
				break;
			}
		}
		
	};
	
	private void setUsbIpod(UsbIpodInfo usbIpodInfo) {
		if (isVisible()) {
			String time = String.format("%02d:%02d", usbIpodInfo.USBPlayMin, usbIpodInfo.USBPlaySec);
			mTvCurTime.setText(time);
			int iCurTrack = (usbIpodInfo.USBCurrentTrackH & 0xFF) * 256 + (usbIpodInfo.USBCurrentTrackL & 0xFF);
			int iTotalTrack = (usbIpodInfo.USBTotalTrackH & 0xFF) * 256 + (usbIpodInfo.USBTotalTrackL & 0xFF);
			String tarck = String.format("%2d/%2d", iCurTrack, iTotalTrack);
			mTvTrack.setText(tarck);
			mSeekBar.setProgress(usbIpodInfo.USBPlayRate);
			
			if (usbIpodInfo.USBPlayState >=0 &&  usbIpodInfo.USBPlayState < mStateStr.length) {
				mTvState.setText(mStateStr[usbIpodInfo.USBPlayState]);
//				if (usbIpodInfo.USBPlayState == 0x00) {
//					mTvPause.setVisibility(View.GONE);
//					mTvPlay.setVisibility(View.VISIBLE);
//				} else {
//					mTvPlay.setVisibility(View.GONE);
//					mTvPause.setVisibility(View.VISIBLE);
//				}
			}
			
			if (usbIpodInfo.USBStatus == 1 && mTvIpod.getVisibility() != View.VISIBLE) {
				mTvUsb.setVisibility(View.GONE);
				mTvUsbFolder.setVisibility(View.GONE);
				mTvIpod.setVisibility(View.VISIBLE);
			} else if (usbIpodInfo.USBStatus == 2) {
				if (mTvUsb.getVisibility() != View.VISIBLE) {
					mTvIpod.setVisibility(View.GONE);
					mTvUsb.setVisibility(View.VISIBLE);
					mTvUsbFolder.setVisibility(View.VISIBLE);
				}
				mTvUsbFolder.setText(getString(R.string.str_usbipod_cur_folder) + usbIpodInfo.USBFolder);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		byte[] datas = new byte[2];
		datas[1] = 0;
		switch (v.getId()) {
		case R.id.usb_ipod_btn_pre:
			datas[0] = 0x03;
			break;
		case R.id.usb_ipod_btn_next:
			datas[0] = 0x04;
			break;		
		case R.id.usb_ipod_btn_f_pre:
			datas[0] = 0x05;
			break;
		case R.id.usb_ipod_btn_f_next:
			datas[0] = 0x06;
			break;
		case R.id.usb_ipod_btn_play:
			datas[0] = 0x01;
			break;
		case R.id.usb_ipod_btn_pause:
			datas[0] = 0x02;
			break;
		default:
			return;
		}
		sendMsg(Re12CrvProtocol.DATA_TYPE_USB_IPOD_REQUEST, datas, mProtocol);
	}
}
