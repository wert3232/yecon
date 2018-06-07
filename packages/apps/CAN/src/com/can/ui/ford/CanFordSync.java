package com.can.ui.ford;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.CanAudio;
import com.can.parser.DDef.SyncMediaTime;
import com.can.parser.DDef.SyncMenu;
import com.can.parser.DDef.SyncOption;
import com.can.parser.DDef.SyncState;
import com.can.parser.DDef.SyncTalkTime;
import com.can.parser.Protocol;
import com.can.services.CanTxRxStub;
import com.can.ui.CanFrament;
import com.can.ui.draw.ResDef;

/**
 * ClassName:CanFordSync
 * 
 * @function:TODO
 * @author Kim
 * @Date: 2016-7-12上午10:42:16
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanFordSync extends CanFrament implements OnClickListener,
		OnItemClickListener {

	private View mObjView = null;
	private Protocol mObjProtocol = null;
	private LinearLayout mObjlayoutSyncDnbar;
	private LinearLayout mObjlayoutSyncKeybroad;

	private ImageView[] mObjImageSyncState;
	private ImageView[] mObjImageSyncState1;

	private ImageView mObjImageSyncMode;
	private TextView mObjTextSyncTime;

	private Button[] mObjBtnSolftKey;

	private int miListSel = 0;
	private ListView mObjMenulistInfo;
	private MenuAdapter mObjMenuAdapter;
	private Sync_listInfo mObjSynclistInfo;
	private LinearLayout mObjSyncSolftKey;
	private CanAudio mObjCanAudio = new CanAudio();
	public HashMap<Integer, DDef.Sync_listInfo> mObjHashMap = new HashMap<Integer, DDef.Sync_listInfo>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		super.Init(mObjHandler, this.getTag());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (mObjView == null) {
			mObjView = inflater.inflate(R.layout.ford_sync, container, false);
			init();
		}

		return mObjView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.sendMsg(mObjProtocol.sendcmd((byte) 0x82));
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
			mObjView.findViewById(R.id.btn_ford_sync_dnbar_up)
					.setOnClickListener(this);
			mObjView.findViewById(R.id.btn_ford_sync_dnbar_dn)
					.setOnClickListener(this);

			mObjlayoutSyncDnbar = (LinearLayout) mObjView
					.findViewById(R.id.btn_ford_sync_dnbar_dn);
			mObjlayoutSyncKeybroad = (LinearLayout) mObjView
					.findViewById(R.id.layout_ford_sync_keybroad);

			int iIndex = 0;
			mObjImageSyncState = new ImageView[ResDef.mSyncStateId.length];
			for (int iter : ResDef.mSyncStateId) {
				mObjImageSyncState[iIndex] = (ImageView) mObjView
						.findViewById(iter);
				iIndex++;
			}

			iIndex = 0;
			mObjImageSyncState1 = new ImageView[ResDef.mSyncState1Id.length];
			for (int iter : ResDef.mSyncState1Id) {
				mObjImageSyncState1[iIndex] = (ImageView) mObjView
						.findViewById(iter);
				iIndex++;
			}

			iIndex = 0;
			mObjBtnSolftKey = new Button[ResDef.mSyncSolftKey.length];
			for (int iter : ResDef.mSyncSolftKey) {
				mObjBtnSolftKey[iIndex] = (Button) mObjView.findViewById(iter);
				mObjBtnSolftKey[iIndex].setOnClickListener(this);
				iIndex++;
			}

			for (int iter : ResDef.mSyncBtnId) {
				mObjView.findViewById(iter).setOnClickListener(this);
			}

			mObjImageSyncMode = (ImageView) mObjView
					.findViewById(R.id.img_ford_sync_mode);
			mObjTextSyncTime = (TextView) mObjView
					.findViewById(R.id.tx_sync_time);

			mObjMenulistInfo = (ListView) mObjView
					.findViewById(R.id.list_ford_sync_info);
			mObjMenulistInfo.setOnItemClickListener(this);
			mObjMenuAdapter = new MenuAdapter(getActivity());
			mObjMenulistInfo.setAdapter(mObjMenuAdapter);

			mObjSynclistInfo = new Sync_listInfo();
			mObjSynclistInfo.mlineInfo = new HashMap<Integer, Integer>();
			mObjSyncSolftKey = (LinearLayout) mObjView
					.findViewById(R.id.layout_ford_sync_solftk);
		}
	}

	private void setCanAudio(CanAudio audio) {
		mObjCanAudio = audio;
		if (mObjProtocol != null) {
			super.sendMsg(mObjProtocol.sendcmd((byte) 0x83));
		}
	}

	private void setSyncState(SyncState state) {
		if (state != null) {

			if (state.mbySyncMode <= 1) {
				if (mObjCanAudio.mbyAudioState == 0x02
						|| mObjCanAudio.mbyAudioState == 0x05) {
					if (this.getTag().equals(CanTxRxStub.CAN_PHONE_MODE)) {
						getActivity().finish();
					}
				}
			} else if (state.mbySyncMode == 2){
				if (mObjCanAudio.mbyAudioState == 0x02) {
					if (this.getTag().equals(CanTxRxStub.CAN_PHONE_MODE)) {
						getActivity().finish();
					}
				}
			}

			if ((state.mbyConnBt == 1) && (state.mbyPresentDev == 1)) {

				for (int iIndex = 0; iIndex < mObjImageSyncState.length; iIndex++) {

					switch (iIndex) {
					case 0:
						int bshow = (state.mbySignal > 4) ? View.INVISIBLE
								: View.VISIBLE;
						if (mObjImageSyncState[iIndex] != null
								&& mObjImageSyncState1[iIndex] != null) {

							if (state.mbySignal <= 4) {
								mObjImageSyncState[iIndex]
										.setImageResource(ResDef.mSyncStatesig[state.mbySignal]);
								mObjImageSyncState1[iIndex]
										.setImageResource(ResDef.mSyncStatesig[state.mbySignal]);
							}

							mObjImageSyncState[iIndex].setVisibility(bshow);
							mObjImageSyncState1[iIndex].setVisibility(bshow);
						}
						break;
					case 1:
						bshow = (state.mbyPower > 4) ? View.INVISIBLE
								: View.VISIBLE;
						if (mObjImageSyncState[iIndex] != null
								&& mObjImageSyncState1[iIndex] != null) {

							if (state.mbySignal <= 4) {
								mObjImageSyncState[iIndex]
										.setImageResource(ResDef.mSyncStatebattery[state.mbyPower]);
								mObjImageSyncState1[iIndex]
										.setImageResource(ResDef.mSyncStatebattery[state.mbyPower]);
							}

							mObjImageSyncState[iIndex].setVisibility(bshow);
							mObjImageSyncState1[iIndex].setVisibility(bshow);
						}
						break;
					case 2:
						if (mObjImageSyncState[iIndex] != null
								&& mObjImageSyncState1[iIndex] != null) {
							mObjImageSyncState[iIndex]
									.setImageResource((state.mbyTalking == 0) ? R.drawable.ford_dail_1
											: R.drawable.ford_dail_2);
							mObjImageSyncState1[iIndex]
									.setImageResource((state.mbyTalking == 0) ? R.drawable.ford_dail_1
											: R.drawable.ford_dail_2);
						}
						break;
					case 3:
						if (mObjImageSyncState[iIndex] != null
								&& mObjImageSyncState1[iIndex] != null) {
							bshow = (state.mbyShowMsg == 0) ? View.INVISIBLE
									: View.VISIBLE;

							mObjImageSyncState[iIndex].setVisibility(bshow);
							mObjImageSyncState1[iIndex].setVisibility(bshow);
						}
						break;
					case 4:
						if (mObjImageSyncState[iIndex] != null
								&& mObjImageSyncState1[iIndex] != null) {
							mObjImageSyncState[iIndex]
									.setImageResource((state.mbyConnBt == 0) ? R.drawable.ford_sync_bt
											: R.drawable.ford_sync_bts);
							mObjImageSyncState1[iIndex]
									.setImageResource((state.mbyConnBt == 0) ? R.drawable.ford_sync_bt
											: R.drawable.ford_sync_bts);
						}
						break;
					default:
						break;
					}
				}
			}
		}
	}

	public class Sync_listInfo {
		public byte mbylistType; // 0 - menu / 1 - dailog
		public HashMap<Integer, Integer> mlineInfo;
	}

	private void setSyncMenu(SyncMenu menu) {

		if (menu != null) {

			if (mObjImageSyncMode != null) {
				int iResId = R.drawable.ford_mode_sync;

				switch (menu.mbyMenuIcon) {
				case 0x02:
					iResId = R.drawable.ford_mode_bt;
					break;
				case 0x0A:
					iResId = R.drawable.ford_mode_usb;
					break;
				case 0x08:
					iResId = R.drawable.ford_mode_adp;
					break;
				case 0x05:
					iResId = R.drawable.ford_mode_ain;
					break;
				case 0x0C:
					iResId = R.drawable.ford_mode_ipod;
					break;
				default:
					break;
				}

				mObjImageSyncMode.setImageResource(iResId);
			}

			miListSel = (menu.mbySelOption == 0) ? -1 : (menu.mbySelOption - 1);
			mObjHashMap = menu.mHashMap;
			mObjMenuAdapter.notifyDataSetChanged();
		}
	}

	private void setSyncOption(SyncOption option) {

		mObjSynclistInfo.mbylistType = option.mbyTextType;

		if (mObjSynclistInfo.mbylistType == 0
				|| mObjSynclistInfo.mbylistType == 1) {
			mObjSynclistInfo.mlineInfo.put(Integer.valueOf(option.mbyRowNum),
					Integer.valueOf(option.mbyEnable));
		} else {
			if (mObjSyncSolftKey != null
					&& mObjBtnSolftKey[option.mbyRowNum] != null) {
				mObjSyncSolftKey.setVisibility(View.VISIBLE);

				if (option.mbyRowNum != 0) {
					mObjBtnSolftKey[option.mbyRowNum].setSelected(false);
				}

				mObjBtnSolftKey[option.mbyRowNum].setText("");

				switch (option.mbyKeyState) {
				case 0x00:
					mObjSyncSolftKey.setVisibility(View.INVISIBLE);
					break;
				case 0x02:
					break;
				case 0x03:
					mObjBtnSolftKey[option.mbyRowNum].setSelected(true);
					break;
				case 0x0a:
				case 0x0b:
					mObjBtnSolftKey[option.mbyRowNum].setText(option.strText);
					break;
				default:
					break;
				}
			}
		}
	}

	private void setSyncMediaTime(SyncMediaTime mediaTime) {
		if (mObjTextSyncTime != null) {

			StringBuffer strMediaTime = new StringBuffer();
			strMediaTime.append(mediaTime.mbyHuor);
			strMediaTime.append(":");
			strMediaTime.append(mediaTime.mbyMinute);
			strMediaTime.append(":");
			strMediaTime.append(mediaTime.mbySecond);

			mObjTextSyncTime.setText(strMediaTime.toString());
		}
	}

	private void setSyncTalkTime(SyncTalkTime talkTime) {
		if (mObjTextSyncTime != null) {

			StringBuffer strMediaTime = new StringBuffer();
			strMediaTime.append(talkTime.mbyHuor);
			strMediaTime.append(":");
			strMediaTime.append(talkTime.mbyMinute);
			strMediaTime.append(":");
			strMediaTime.append(talkTime.mbySecond);

			mObjTextSyncTime.setText(strMediaTime.toString());
		}
	}

	private Handler mObjHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mObjProtocol = (Protocol) msg.obj;
				sendMedia();
				break;
			case DDef.CAN_AUDIO_INFO:
				setCanAudio((CanAudio) msg.obj);
				break;
			case DDef.SYNC_STATE:
				setSyncState((SyncState) msg.obj);
				break;
			case DDef.SYNC_MENU:
				setSyncMenu((SyncMenu) msg.obj);
				break;
			case DDef.SYNC_OPTION:
				setSyncOption((SyncOption) msg.obj);
				break;
			case DDef.SYNC_MEDTIME:
				setSyncMediaTime((SyncMediaTime) msg.obj);
				break;
			case DDef.SYNC_TALKTIME:
				setSyncTalkTime((SyncTalkTime) msg.obj);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};

	private void sendMedia() {
		if (mObjProtocol != null) {

			if (this.getTag().equals(CanTxRxStub.CAN_SOURCE_MODE)) {
				super.sendMsg(mObjProtocol.sendcmd((byte) 0x81));
			}
		}
	}

	private void switchKeybroad(boolean bshow) {
		if (mObjlayoutSyncDnbar != null && mObjlayoutSyncKeybroad != null) {

			mObjlayoutSyncDnbar.setVisibility(bshow ? View.INVISIBLE
					: View.VISIBLE);
			mObjlayoutSyncKeybroad.setVisibility(bshow ? View.VISIBLE
					: View.INVISIBLE);
		}
	}

	private void sendSyncCmd(int iId) {
		byte bycmd = 0x00;

		switch (iId) {
		case R.id.btn_ford_sync_kb_speech:
			bycmd = 0x01;
			break;
		case R.id.btn_ford_sync_kb_phone:
			bycmd = 0x03;
			break;
		case R.id.btn_ford_sync_kb_last:
			bycmd = 0x08;
			break;
		case R.id.btn_ford_sync_kb_next:
			bycmd = 0x09;
			break;
		case R.id.btn_ford_sync_up:
			bycmd = 0x0A;
			break;
		case R.id.btn_ford_sync_dn:
			bycmd = 0x0B;
			break;
		case R.id.btn_ford_sync_ok:
			bycmd = 0x0C;
			break;
		case R.id.btn_ford_sync_kb_huang:
			bycmd = 0x14;
			break;
		case R.id.btn_ford_sync_kb_0:
			bycmd = 0x0D;
			break;
		case R.id.btn_ford_sync_kb_1:
			bycmd = 0x0E;
			break;
		case R.id.btn_ford_sync_kb_2:
			bycmd = 0x0F;
			break;
		case R.id.btn_ford_sync_kb_3:
			bycmd = 0x10;
			break;
		case R.id.btn_ford_sync_kb_4:
			bycmd = 0x11;
			break;
		case R.id.btn_ford_sync_kb_5:
			bycmd = 0x12;
			break;
		case R.id.btn_ford_sync_kb_6:
			bycmd = 0x13;
			break;
		case R.id.btn_ford_sync_kb_7:
			bycmd = 0x14;
			break;
		case R.id.btn_ford_sync_kb_8:
			bycmd = 0x15;
			break;
		case R.id.btn_ford_sync_kb_9:
			bycmd = 0x16;
			break;
		case R.id.btn_ford_sync_kb_a:
			bycmd = 0x17;
			break;
		case R.id.btn_ford_sync_kb_b:
			bycmd = 0x18;
			break;
		case R.id.btn_ford_sync_left:
			bycmd = 0x19;
			break;
		case R.id.btn_ford_sync_right:
			bycmd = 0x1A;
			break;
		case R.id.btn_ford_sync_kb_swicth:
			bycmd = 0x1B;
			break;
		case R.id.btn_ford_sync_solft1:
			bycmd = 0x1C;
			break;
		case R.id.btn_ford_sync_solft2:
			bycmd = 0x1D;
			break;
		case R.id.btn_ford_sync_solft3:
			bycmd = 0x1E;
			break;
		case R.id.btn_ford_sync_solft4:
			bycmd = 0x1F;
			break;
		default:
			break;
		}

		if (mObjProtocol != null && bycmd != 0x00) {
			super.sendMsg(mObjProtocol.sendcmd(bycmd));
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_ford_sync_dnbar_up:
			switchKeybroad(false);
			break;
		case R.id.btn_ford_sync_dnbar_dn:
			switchKeybroad(true);
			break;
		case R.id.btn_ford_sync_kb_1:
		case R.id.btn_ford_sync_kb_2:
		case R.id.btn_ford_sync_kb_3:
		case R.id.btn_ford_sync_kb_4:
		case R.id.btn_ford_sync_kb_5:
		case R.id.btn_ford_sync_kb_6:
		case R.id.btn_ford_sync_kb_7:
		case R.id.btn_ford_sync_kb_8:
		case R.id.btn_ford_sync_kb_9:
		case R.id.btn_ford_sync_kb_0:
		case R.id.btn_ford_sync_kb_a:
		case R.id.btn_ford_sync_kb_b:
		case R.id.btn_ford_sync_kb_speech:
		case R.id.btn_ford_sync_kb_phone:
		case R.id.btn_ford_sync_kb_last:
		case R.id.btn_ford_sync_kb_next:
		case R.id.btn_ford_sync_kb_swicth:
		case R.id.btn_ford_sync_kb_huang:
		case R.id.btn_ford_sync_up:
		case R.id.btn_ford_sync_dn:
		case R.id.btn_ford_sync_left:
		case R.id.btn_ford_sync_right:
		case R.id.btn_ford_sync_ok:
			sendSyncCmd(arg0.getId());
			break;
		default:
			break;
		}
	}

	public class Hoder {
		RelativeLayout mItemLayout;
		ImageView mImageLeftIcon;
		TextView mTextlistInfo;
		ImageView mImageRightIcon;
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
			return mObjHashMap.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mObjHashMap.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Hoder objHoder = null;

			if (convertView == null) {
				convertView = mObjInflater.inflate(R.layout.ford_sync_list,
						null);
				objHoder = new Hoder();
				objHoder.mItemLayout = (RelativeLayout) convertView
						.findViewById(R.id.layout_ford_list_item);
				objHoder.mImageLeftIcon = (ImageView) convertView
						.findViewById(R.id.img_sync_list_left_icon);
				objHoder.mTextlistInfo = (TextView) convertView
						.findViewById(R.id.tx_sync_list_info);
				objHoder.mImageRightIcon = (ImageView) convertView
						.findViewById(R.id.img_sync_list_right_icon);

				convertView.setTag(objHoder);
			} else {
				objHoder = (Hoder) convertView.getTag();
			}

			if (objHoder != null) {

				if (mObjHashMap.get(position) != null) {
					objHoder.mTextlistInfo
							.setText(mObjHashMap.get(position).strText);

					objHoder.mImageLeftIcon
							.setImageResource(ResDef.mSyncleftIcon[mObjHashMap
									.get(position).ilefticon]);
					objHoder.mImageRightIcon
							.setImageResource(ResDef.mSyncRightIcon[mObjHashMap
									.get(position).irighticon]);
				} else {
					objHoder.mTextlistInfo.setText("");
					objHoder.mImageLeftIcon
							.setImageResource(ResDef.mSyncleftIcon[0]);
					objHoder.mImageRightIcon
							.setImageResource(ResDef.mSyncRightIcon[0]);
				}

				if (miListSel == position) {
					objHoder.mItemLayout
							.setBackgroundResource(R.drawable.ford_lsit_s);
				} else {
					objHoder.mItemLayout
							.setBackgroundResource(R.drawable.ford_lsit_n);
				}
			}

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		int iEnable = (mObjSynclistInfo.mlineInfo.get(Integer.valueOf(arg2)) == null) ? 0
				: mObjSynclistInfo.mlineInfo.get(Integer.valueOf(arg2));

		if (miListSel != arg2) {
			if (iEnable == 1) {
				miListSel = arg2;

				if (mObjProtocol != null) {
					super.sendMsg(mObjProtocol.sendcmd((byte) (arg2 + 0x91)));
				}
			}
		}

		mObjMenuAdapter.notifyDataSetChanged();
	}
}
