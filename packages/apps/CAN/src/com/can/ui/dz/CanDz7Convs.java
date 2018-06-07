package com.can.ui.dz;

import java.util.ArrayList;
import java.util.Iterator;
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
import com.can.parser.DDef.ConvConsumers;
import com.can.parser.Protocol;
import com.can.parser.raise.dz.ReMQB7Protocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.ResDef;

/**
 * ClassName:CanDz7Convs
 * 
 * @function:TODO
 * @author Kim
 * @Date: 2016-7-7下午4:03:57
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanDz7Convs extends CanFrament {

	private View mObjView = null;

	protected ListView mObjListInfo = null;
	protected listAdapter mObjlistAdapter = null;
	public ArrayList<String> mArrayList = new ArrayList<String>();

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
			mObjView = inflater
					.inflate(R.layout.dz7_convinfo, container, false);
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
			mObjlistAdapter = new listAdapter(this.getActivity());
			mObjListInfo = (ListView) mObjView
					.findViewById(R.id.list_dz7_conv_info);
			mObjListInfo.setAdapter(mObjlistAdapter);
		}
	}

	private void setAttr(ConvConsumers convs) {

		for (byte iter : convs.mbyConvConsumers) {
			if (iter < ResDef.mDz7ConvsInfo.length) {
				Merge(getResources().getString(ResDef.mDz7ConvsInfo[iter]));
			}
		}

		mObjlistAdapter.notifyDataSetChanged();
	}

	public void Merge(String strInfo) {

		boolean bAdd = true;

		if (strInfo != null) {
			Iterator<String> iter = mArrayList.iterator();
			while (iter.hasNext()) {
				String str = (String) iter.next();
				if (str.equals(strInfo)) {
					bAdd = false;
				}
			}

			if (bAdd) {
				mArrayList.add(strInfo);
			}
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
			case DDef.CONV_CONSUMERS:
				setAttr((ConvConsumers) msg.obj);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};
	
	private void Inquiry(Message msg) {
		byte[] bydata = new byte[2];
		bydata[0] = ReMQB7Protocol.DATA_TYPE_CONV_CONSUMERS;
		bydata[1] = 0x00;
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, bydata,
				(Protocol) msg.obj);

	}

	public class listAdapter extends BaseAdapter {

		private Context mObjContext;
		private LayoutInflater mObjInflater;

		public listAdapter(Context context) {
			// TODO Auto-generated constructor stub
			mObjContext = context;
			mObjInflater = (LayoutInflater) mObjContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mArrayList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mArrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView objTextView;

			if (convertView == null) {
				convertView = mObjInflater.inflate(R.layout.choose_listview,
						null);
				objTextView = (TextView) convertView
						.findViewById(R.id.tx_list_info);

				convertView.setTag(objTextView);
			} else {
				objTextView = (TextView) convertView.getTag();
			}

			if (objTextView != null) {
				objTextView.setText(mArrayList.get(position));
			}

			return convertView;
		}
	}
}
