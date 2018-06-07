package com.can.ui.psa;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.PsaDiagInfo;
import com.can.parser.DDef.PsaFuncInfo;
import com.can.parser.DDef.PsaWarnInfo;
import com.can.parser.Protocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.AutoText;

public class CanPsaWarnInfo extends CanFrament {
	private ListView mListView = null;
	private listAdapter mListAdapter = null;
	public ArrayList<String> mArrayList = new ArrayList<String>();
	private PsaWarnInfo mPsaWarnInfo = null;
	private PsaDiagInfo mPsaDiagInfo = null;
	private PsaFuncInfo mPsaFuncInfo = null;
	private Protocol mProtocol = null;
	private View mView = null;
	
	private TextView mTvWarnType = null;
	private TextView mTvWarnNumType = null;
	private TextView mTvWarnNum = null;
	
	private int[] mStrId = {
		R.string.str_psa_warn00,
		R.string.str_psa_warn01,
		R.string.str_psa_warn02,
		R.string.str_psa_warn03,
		R.string.str_psa_warn04,
		R.string.str_psa_warn05,
		R.string.str_psa_warn06,
		R.string.str_psa_warn07,
		R.string.str_psa_warn08,
		R.string.str_psa_warn09,
		R.string.str_psa_warn0A,
		R.string.str_psa_warn0B,
		R.string.str_psa_warn0C,
		R.string.str_psa_warn0D,
		R.string.str_psa_warn0E,
		R.string.str_psa_warn0F,
		R.string.str_psa_warn10,
		R.string.str_psa_warn11,
		R.string.str_psa_warn12,
		R.string.str_psa_warn13,
		R.string.str_psa_warn14,
		R.string.str_psa_warn15,
		R.string.str_psa_warn16,
		R.string.str_psa_warn17,
		R.string.str_psa_warn18,
		R.string.str_psa_warn19,
		R.string.str_psa_warn1A,
		R.string.str_psa_warn1B,
		R.string.str_psa_warn1C,
		R.string.str_psa_warn1D,
		R.string.str_psa_warn1E,
		R.string.str_psa_warn1F,
		R.string.str_psa_warn20,
		R.string.str_psa_warn21,
		R.string.str_psa_warn22,
		R.string.str_psa_warn23,
		R.string.str_psa_warn24,
		R.string.str_psa_warn25,
		R.string.str_psa_warn26,
		R.string.str_psa_warn27,
		R.string.str_psa_warn28,
		R.string.str_psa_warn29,
		R.string.str_psa_warn2A,
		R.string.str_psa_warn2B,
		R.string.str_psa_warn2C,
		R.string.str_psa_warn2D,
		R.string.str_psa_warn2E,
		R.string.str_psa_warn2F,
		R.string.str_psa_warn30,
		R.string.str_psa_warn31,
		R.string.str_psa_warn32,
		R.string.str_psa_warn33,
		R.string.str_psa_warn34,
		R.string.str_psa_warn35,
		R.string.str_psa_warn36,
		R.string.str_psa_warn37,
		R.string.str_psa_warn38,
		R.string.str_psa_warn39,
		R.string.str_psa_warn3A,
		R.string.str_psa_warn3B,
		R.string.str_psa_warn3C,
		R.string.str_psa_warn3D,
		R.string.str_psa_warn3E,
		R.string.str_psa_warn3F,
		R.string.str_psa_warn40,
		R.string.str_psa_warn41,
		R.string.str_psa_warn42,
		R.string.str_psa_warn43,
		R.string.str_psa_warn44,
		R.string.str_psa_warn45,
		R.string.str_psa_warn46,
		R.string.str_psa_warn47,
		R.string.str_psa_warn48,
		R.string.str_psa_warn49,
		R.string.str_psa_warn4A,
		R.string.str_psa_warn4B,
		R.string.str_psa_warn4C,
		R.string.str_psa_warn4D,
		R.string.str_psa_warn4E,
		R.string.str_psa_warn4F,
		R.string.str_psa_warn50,
		R.string.str_psa_warn51,
		R.string.str_psa_warn52,
		R.string.str_psa_warn53,
		R.string.str_psa_warn54,
		R.string.str_psa_warn55,
		R.string.str_psa_warn56,
		R.string.str_psa_warn57,
		R.string.str_psa_warn58,
		R.string.str_psa_warn59,
		R.string.str_psa_warn5A,
		R.string.str_psa_warn5B,
		R.string.str_psa_warn5C,
		R.string.str_psa_warn5D,
		R.string.str_psa_warn5E,
		R.string.str_psa_warn5F,
		R.string.str_psa_warn60,
		R.string.str_psa_warn61,
		R.string.str_psa_warn62,
		R.string.str_psa_warn63,
		R.string.str_psa_warn64,
		R.string.str_psa_warn65,
		R.string.str_psa_warn66,
		R.string.str_psa_warn67,
		R.string.str_psa_warn68,
		R.string.str_psa_warn69,
		R.string.str_psa_warn6A,
		R.string.str_psa_warn6B,
		R.string.str_psa_warn6C,
		R.string.str_psa_warn6D,
		R.string.str_psa_warn6E,
		R.string.str_psa_warn6F,
		R.string.str_psa_warn70,
		R.string.str_psa_warn71,
		R.string.str_psa_warn72,
		R.string.str_psa_warn73,
		R.string.str_psa_warn74,
		R.string.str_psa_warn75,
		R.string.str_psa_warn76,
		R.string.str_psa_warn77,
		R.string.str_psa_warn78,
		R.string.str_psa_warn79,
		R.string.str_psa_warn7A,
		R.string.str_psa_warn7B,
		R.string.str_psa_warn7C,
		R.string.str_psa_warn7D,
		R.string.str_psa_warn7E,
		R.string.str_psa_warn7F,
		R.string.str_psa_warn80,
		R.string.str_psa_warn81,
		R.string.str_psa_warn82,
		R.string.str_psa_warn83,
		R.string.str_psa_warn84,
		R.string.str_psa_warn85,
		R.string.str_psa_warn86,
		R.string.str_psa_warn87,
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mHandler,getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.psa_can_warn_info, null);
			initView();
		}
		return mView;
	}

	private void initView() {
		if (mView != null) {
			mTvWarnType = (TextView) mView.findViewById(R.id.psa_tv_warn_type);
			mTvWarnNumType = (TextView) mView.findViewById(R.id.psa_tv_warn_num_type);
			mTvWarnNum = (TextView) mView.findViewById(R.id.psa_tv_warn_num);
			mListAdapter = new listAdapter(getActivity());
			mListView = (ListView) mView.findViewById(R.id.psa_list_warn_info);
			mListView.setAdapter(mListAdapter);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getData();
	}

	@Override
	public void onDestroy() {
		super.DeInit();
		super.onDestroy();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mProtocol = (Protocol) msg.obj;
				getData();
				break;
			case DDef.PSA_WARN_INFO:
				mPsaWarnInfo = (PsaWarnInfo)msg.obj;
				setWarnInfo();
				break;
			case DDef.PSA_DIOG_INFO:
				mPsaDiagInfo = (PsaDiagInfo)msg.obj;
				setWarnInfo();
				break;
			case DDef.PSA_FUNC_INFO:
				mPsaFuncInfo = (PsaFuncInfo)msg.obj;
				setWarnInfo();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	private void getData() {
		if (CanPsaCarInfo.sWarnType == 0x00) {
			super.getData(DDef.PSA_WARN_INFO);
		} else if (CanPsaCarInfo.sWarnType == 0x01) {
			super.getData(DDef.PSA_DIOG_INFO);
		} else if (CanPsaCarInfo.sWarnType == 0x02) {
			super.getData(DDef.PSA_FUNC_INFO);
		}
	}
	
	private void setWarnInfo() {
		if (mView != null) {
			mArrayList.clear();
			mTvWarnNum.setVisibility(View.GONE);
			if (CanPsaCarInfo.sWarnType == 0x00) {
				mTvWarnType.setText(getString(R.string.str_psa_warn));
				mTvWarnNumType.setText(getString(R.string.str_psa_warn_num));
				if (mPsaWarnInfo != null) {
					mTvWarnNum.setVisibility(View.VISIBLE);
					mTvWarnNum.setText(String.valueOf(mPsaWarnInfo.mWarnInfoTotal));
					addStrToAdapter(mPsaWarnInfo.mWarnInfo);
				}
			} else if (CanPsaCarInfo.sWarnType == 0x01) {
				mTvWarnType.setText(getString(R.string.str_psa_func));
				mTvWarnNumType.setText(getString(R.string.str_psa_func_num));
				if (mPsaFuncInfo != null) {
					mTvWarnNum.setVisibility(View.VISIBLE);
					mTvWarnNum.setText(String.valueOf(mPsaFuncInfo.mFuncInfoTotal));
					addStrToAdapter(mPsaFuncInfo.mFuncInfo);
				}
			} else if (CanPsaCarInfo.sWarnType == 0x02) {
				mTvWarnType.setText(getString(R.string.str_psa_diog));
				mTvWarnNumType.setText(getString(R.string.str_psa_diog_num));
				if (mPsaDiagInfo != null) {
					mTvWarnNum.setVisibility(View.VISIBLE);
					mTvWarnNum.setText(String.valueOf(mPsaDiagInfo.mDiagInfoTotal));
					addStrToAdapter(mPsaDiagInfo.mDiagInfo);
				}
			} 
			mListAdapter.notifyDataSetChanged();
		}
	}
	
	private void addStrToAdapter(int[] data) {
		if (data != null) {
			for (int iter : data) {
				String str = "";
				if (iter == 0xF0) {
					str = getString(R.string.str_psa_warnF0);
				} else if (iter == 0xFF) {
					str = getString(R.string.str_psa_warnFF);
				} else if (iter < mStrId.length) {
					str = getString(mStrId[iter]);
				}
				mArrayList.add(str);
			}
		}
	}
	
	public class listAdapter extends BaseAdapter {

		private Context mObjContext;
		private LayoutInflater mObjInflater;

		public listAdapter(Context context) {
			mObjContext = context;
			mObjInflater = (LayoutInflater) mObjContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mArrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return mArrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AutoText objTextView;

			if (convertView == null) {
				convertView = mObjInflater.inflate(R.layout.psa_listview,
						null);
				objTextView = (AutoText) convertView
						.findViewById(R.id.psa_tx_list_info);

				convertView.setTag(objTextView);
			} else {
				objTextView = (AutoText) convertView.getTag();
			}

			if (objTextView != null) {
				objTextView.setText(mArrayList.get(position));
			}

			return convertView;
		}
	}
}
