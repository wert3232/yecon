package com.can.ui.gm;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.can.activity.R;
import com.can.activity.R.string;
import com.can.parser.DDef;
import com.can.parser.DDef.CanAudio;
import com.can.parser.DDef.GmOnStar;
import com.can.parser.Protocol;
import com.can.parser.raise.gm.ReGmProtocol;
import com.can.services.CanTxRxStub;
import com.can.ui.CanFrament;
import com.can.ui.draw.AutoText;

public class CanGmOnStar extends CanFrament implements OnClickListener{
	private View mView = null;
	private String mStrNum = "";
	private GmOnStar mOnStar = null;
	private CanAudio mCanAudio = null;
	private Protocol mProtocol = null;
	private AutoText mTxtNotify = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mHandler, getTag());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.gm_on_star, null);
			initView();
		}
		return mView;
	}

	private void initView() {
		if (mView != null) {
			mView.findViewById(R.id.gm_btn_hung).setOnClickListener(this);
			mView.findViewById(R.id.gm_btn_call).setOnClickListener(this);
			mView.findViewById(R.id.gm_btn_call_one).setOnClickListener(this);
			mView.findViewById(R.id.gm_btn_call_two).setOnClickListener(this);
			mView.findViewById(R.id.gm_btn_call_three).setOnClickListener(this);
			mView.findViewById(R.id.gm_btn_call_four).setOnClickListener(this);
			mView.findViewById(R.id.gm_btn_call_five).setOnClickListener(this);
			mView.findViewById(R.id.gm_btn_call_six).setOnClickListener(this);
			mView.findViewById(R.id.gm_btn_call_seven).setOnClickListener(this);
			mView.findViewById(R.id.gm_btn_call_eight).setOnClickListener(this);
			mView.findViewById(R.id.gm_btn_call_nine).setOnClickListener(this);
			mView.findViewById(R.id.gm_btn_call_zero).setOnClickListener(this);
			mView.findViewById(R.id.gm_btn_del_num).setOnClickListener(this);
			mView.findViewById(R.id.gm_btn_call_recall).setOnClickListener(this);
			mView.findViewById(R.id.gm_btn_call_star).setOnClickListener(this);
			
			mTxtNotify = (AutoText) mView.findViewById(R.id.text_call_info);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (getTag().equals(CanTxRxStub.CAN_SOURCE_MODE)) {
			sendData(ReGmProtocol.DATA_TYPE_ON_START_STATE_REQUEST, (byte) 0x01);
		} else {
			FullScreen(getActivity(), true);
		}
		super.getData(DDef.CAN_AUDIO_INFO);
		super.getData(DDef.ON_STAR_ID);
	}

	@Override
	public void onPause() {
		FullScreen(getActivity(), false);
		super.onPause();
	}

	@Override
	public void onDestroy() {
		sendData(ReGmProtocol.DATA_TYPE_ON_START_STATE_REQUEST, (byte) 0x00);
		super.DeInit();
		super.onDestroy();
	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mProtocol = (Protocol) msg.obj;
				if (getTag().equals(CanTxRxStub.CAN_SOURCE_MODE)) {
					sendData(ReGmProtocol.DATA_TYPE_ON_START_STATE_REQUEST, (byte) 0x01);
				} else {
					getData(DDef.ON_STAR_ID);
					getData(DDef.CAN_AUDIO_INFO);
				}
				break;
			case DDef.ON_STAR_ID:
				mOnStar = (GmOnStar)msg.obj;
				setOnStar(mOnStar);
				break;
			case DDef.CAN_AUDIO_INFO:
				mCanAudio = (CanAudio)msg.obj;
				setOnStar(mOnStar);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onClick(View v) {
		if (mCanAudio != null) {
			switch (v.getId()) {
			case R.id.gm_btn_call:
				if (mCanAudio.mbyAudioState == ReGmProtocol.ON_STAR_INCOMING) {
					sendData(ReGmProtocol.DATA_TYPE_ON_START_STATE_REQUEST, (byte) 0x02);
				} else if (mCanAudio.mbyAudioState == ReGmProtocol.ON_STAR_SYS_OPEN) {
					byte[] datas = mStrNum.getBytes();
					byte[] sendDatas = new byte[10];
					for (int i = 0; i < sendDatas.length; i++) {
						if (i*2+1 == datas.length) {
							sendDatas[i] = (byte) (((byteToNum(datas[i*2]) & 0xFF) << 4) | 0x0F);
						} else if (i*2 == datas.length) {
							sendDatas[i] = (byte) 0xF0;
						} else if (i*2+1 < datas.length ) {
							sendDatas[i] = (byte) (((byteToNum(datas[i*2]) & 0xFF) << 4) | (byteToNum(datas[i*2+1]) & 0xFF));
						}
					}
					sendMsg(ReGmProtocol.DATA_TYPE_ON_START_PHONE_REUQEST, sendDatas, mProtocol);
				}
				break;
			case R.id.gm_btn_hung:
				if (isCalling()) {
					sendData(ReGmProtocol.DATA_TYPE_ON_START_STATE_REQUEST, (byte) 0x03);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					sendData(ReGmProtocol.DATA_TYPE_ON_START_STATE_REQUEST, (byte) 0x00);
				}
				break;
			case R.id.gm_btn_call_zero:
				onBtnClick(0x00);
				break;
			case R.id.gm_btn_call_one:
				onBtnClick(0x01);
				break;
			case R.id.gm_btn_call_two:
				onBtnClick(0x02);
				break;
			case R.id.gm_btn_call_three:
				onBtnClick(0x03);
				break;
			case R.id.gm_btn_call_four:
				onBtnClick(0x04);
				break;
			case R.id.gm_btn_call_five:
				onBtnClick(0x05);
				break;
			case R.id.gm_btn_call_six:
				onBtnClick(0x06);
				break;
			case R.id.gm_btn_call_seven:
				onBtnClick(0x07);
				break;
			case R.id.gm_btn_call_eight:
				onBtnClick(0x08);
				break;
			case R.id.gm_btn_call_nine:
				onBtnClick(0x09);
				break;
			case R.id.gm_btn_call_star:
				onBtnClick(0x0A);
				break;
			case R.id.gm_btn_call_recall:
				onBtnClick(0x0B);
				break;
			case R.id.gm_btn_del_num:
				if (!isCalling() && mStrNum.length() >=1) {
					mStrNum = mStrNum.substring(0, mStrNum.length()-1);
					mTxtNotify.setText(mStrNum);
				}
				break;
			default:
				break;
			}
		}
	}
	
	private byte byteToNum(byte byCmd) {
		byte byNum = 0;
		if (byCmd >= 48 && byCmd <= 57) {
			byNum = (byte) (byCmd - 48);
		} else if (byCmd == 42) {
			byNum = 0x0A;
		} else if (byCmd == 35) {
			byNum = 0x0B;
		}
		
		return byNum;
	}
	
	private void setOnStar(GmOnStar gmOnStar) {
		if (mCanAudio != null && mCanAudio.mbyAudioState == ReGmProtocol.ON_STAR_SYS_CLOSE) {
			getActivity().finish();
		}
		
		if (isCalling()) {
			
			FullScreen(getActivity(), true);
			if (mView != null) {
				String str = "";
				if (mCanAudio.mbyAudioState == ReGmProtocol.ON_STAR_INCOMING) {
					str = getString(R.string.str_onstar_incoming);
					mStrNum = "";
				} else if (mCanAudio.mbyAudioState == ReGmProtocol.ON_STAR_OUTGOING) {
					str = getString(R.string.str_onstar_outgoing);
					mStrNum = "";
				} else if (mCanAudio.mbyAudioState == ReGmProtocol.ON_STAR_SPEAKING) {
					str = getString(R.string.str_onstar_speaking);
				}
				
				if (gmOnStar != null) {
					str += gmOnStar.mStrNum;
					if (!mStrNum.equals("")) {
						str += "-" + mStrNum;
					}
				}
				mTxtNotify.setText(str);
			}
		}
		
	}
	
	private boolean isCalling() {
		if (mCanAudio != null) {
			if (mCanAudio.mbyAudioState == ReGmProtocol.ON_STAR_INCOMING ||
				mCanAudio.mbyAudioState == ReGmProtocol.ON_STAR_OUTGOING ||
				mCanAudio.mbyAudioState == ReGmProtocol.ON_STAR_SPEAKING) {
				return true;
			}
		}
		return false;
	}
	private void onBtnClick(int value) {
		if (mCanAudio.mbyAudioState == ReGmProtocol.ON_STAR_SPEAKING ||
			mCanAudio.mbyAudioState == ReGmProtocol.ON_STAR_SYS_OPEN) {
			byte byCmd = 0x00;
			if (value >= 0x00 && value <= 0x09) {
				byCmd = (byte) (0x80 | value);
				mStrNum += value;
			} else if (value == 0x0A || value == 0x0B) {
				if (value == 0x0A) {
					mStrNum += "*";
				} else if (value == 0x0B) {
					mStrNum += "#";
				}
				byCmd = (byte) (0x80 | value);
			}
			
			if (mCanAudio.mbyAudioState == ReGmProtocol.ON_STAR_SYS_OPEN) {
				mTxtNotify.setText(mStrNum);
			}
			
			if (byCmd != 0x00 && mCanAudio.mbyAudioState == ReGmProtocol.ON_STAR_SPEAKING) {
				sendData(ReGmProtocol.DATA_TYPE_ON_START_STATE_REQUEST, byCmd);
			}
		}
	}
	
	private void sendData(byte byHead, byte byCmd) {
		byte[] datas = new byte[1];
		datas[0] = byCmd;
		sendMsg(byHead, datas, mProtocol);
	}
}
