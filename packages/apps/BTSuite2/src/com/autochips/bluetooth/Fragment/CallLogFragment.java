/**
 * 
 */
/**
 * @author Administrator
 *
 */
package com.autochips.bluetooth.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import android.bluetooth.client.pbap.BluetoothPbapClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.autochips.bluetooth.Bluetooth;
import com.autochips.bluetooth.BluetoothConstants;
import com.autochips.bluetooth.MainApp;
import com.autochips.bluetooth.MainBluetoothActivity;
import com.autochips.bluetooth.R;
import com.autochips.bluetooth.TuoXianDialActivity;
import com.autochips.bluetooth.MainBluetoothActivity.MyTouchListener;
import com.autochips.bluetooth.calllog.CallLogUtil;
import com.autochips.bluetooth.util.L;
import com.autochips.bluetooth.view.ConfirmDialog;
import com.tuoxianui.tools.AtTimerHelpr;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.constant.YeconConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
public class CallLogFragment extends BaseFragment implements OnClickListener, OnItemClickListener{
	private final String MISS = BluetoothPbapClient.MCH_PATH;
	private final String COMBINED = BluetoothPbapClient.CCH_PATH;
	private final String OUTGOING = BluetoothPbapClient.OCH_PATH;
	private final String INCOME = BluetoothPbapClient.ICH_PATH;
	private ListView mHistoryListView;
	private TextView mTipEmptyTextView;
	private TextView mBtnDelete;
	private TextView mBtnDeleteAll;
	private CallLogAdapter mAdapter;
	private ArrayList<CallLog> callLogList;
	private ArrayList<String> missList = new ArrayList<String>();
	private ArrayList<String> combinedList = new ArrayList<String>();
	private ArrayList<String> incomeList = new ArrayList<String>();
	private ArrayList<String> outGoingList = new ArrayList<String>();
	private String currentCallLogState = "";
	private AtTimerHelpr mAtTimerHelpr;
	private String currentPhoneMac = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.bt_callhistory, container, false);
		initView(mRootView);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CallLogUtil.ACITON_CALL_LOG);
        getActivity().registerReceiver(mReceiver, intentFilter);
        
        /*FIXME:11103 一汽要求,不自动流转 mAtTimerHelpr = new AtTimerHelpr();
        mAtTimerHelpr.setCallBack( new AtTimerHelpr.AtTimerHelprDoItCallBack() {
			@Override
			public void doIt() {
				if(Bluetooth.getInstance().iscallidle()  && !MainBluetoothActivity.isPowerOff){
					if(Bluetooth.getInstance().isA2DPPlaying()){ 
						getActivity().sendOrderedBroadcast(new Intent("action.bt.switch.fragment.music"),null);
					}
					else{
						Intent intent = new Intent(Intent.ACTION_MAIN);
				        intent.addCategory(Intent.CATEGORY_HOME);
				        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				        startActivity(intent);
						//
			        	Intent intent2 = new Intent(Context.ACTION_SOURCE_STACK_DO_OUT_AUTOEXIT);
			        	intent2.putExtra(Context.EXTRA_APK_PACKAGENAME, YeconConstants.BLUETOOTH_PACKAGE_NAME);
			        	intent2.putExtra(Context.EXTRA_APK_ACTIVITY, YeconConstants.BLUETOOTH_START_ACTIVITY);
			        	getActivity().sendOrderedBroadcast(intent2,null);
			        	
			        }
				}else{
					mAtTimerHelpr.start(10);
				}
			}
		} );*/
		return mRootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		try {
			//FIXME: 一汽要求,不自动流转 mAtTimerHelpr.start(10);
			((MainBluetoothActivity) getActivity()).registerMyTouchListener(touchListener);
			String mac = Bluetooth.getInstance().getconnectedmac();
			L.w("before currentPhoneMac: " + currentPhoneMac + "  mac:" + mac);
			if(mac != null && !mac.equals(currentPhoneMac)){
				missList.clear();
				combinedList.clear();
				incomeList.clear();
				outGoingList.clear();
				if(mAdapter != null){
					mAdapter.removeAll();
				}
				currentPhoneMac = mac;
			}
			L.w("after currentPhoneMac: " + currentPhoneMac + "  mac:" + mac);
		} catch (Exception e) {
			L.e(e.toString());
		}
	}
	
	@Override
	public void onPause() {
		try {
			//FIXME: 一汽要求,不自动流转 mAtTimerHelpr.stop();
			((MainBluetoothActivity) getActivity()).unRegisterMyTouchListener(touchListener);
		} catch (Exception e) {
			L.e(e.toString());
		}
		super.onPause();
	}
	
	@Override
	public void onDestroyView() {
		 getActivity().unregisterReceiver(mReceiver);
		super.onDestroyView();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@SuppressLint("ViewHolder")
	private  class CallLogAdapter extends BaseAdapter{
		Resources res;
		LayoutInflater inflater;
		ArrayList<CallLog> list;
		private int mSelectIdx = -2;
		public CallLogAdapter(Context context,ArrayList<CallLog> list) {
			inflater = LayoutInflater.from(context);
			res = getResources();
			this.list = list;
			mSelectIdx = -2;
		}
		public int getSelectIndex(){
			return mSelectIdx;
		}
		public ArrayList<CallLog> getList(){
			return list;
		}
		@Override
		public int getCount() {
			return list.size();
		}
		public void setSelect(int index){
			mSelectIdx = index;
			notifyDataSetChanged();
		}
		public void removeSelectItem(){
        	if(mSelectIdx > -1 && list.size() > mSelectIdx){	
        		list.remove(mSelectIdx);
        		mSelectIdx = -1;
        	}
        	notifyDataSetChanged();
        }
        public void removeAll(){
        	if(list != null && list.size() > 0){	
        		list.clear();
				missList.clear();
				combinedList.clear();
				incomeList.clear();
				outGoingList.clear();
				notifylistviewdataupdate();
        	}
        }
		
		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_call_log,parent,false);
			}
			holder.name = (TextView)convertView.findViewById(R.id.item_history_name);
			holder.phone = (TextView)convertView.findViewById(R.id.item_history_number);
			holder.image = (ImageView)convertView.findViewById(R.id.btn_call_history_img);
			holder.del = (ImageButton)convertView.findViewById(R.id.item_history_del);
			holder.date = (TextView)convertView.findViewById(R.id.item_call_log_date);
			holder.time = (TextView)convertView.findViewById(R.id.item_call_log_time);
			
            holder.name.setText(list.get(position).name);
            holder.phone.setText(list.get(position).number);
            
            if(TextUtils.isEmpty(list.get(position).name)){
            	holder.name.setText(getResources().getString(R.string.str_unknow));
            	holder.name.setVisibility(View.VISIBLE);
            	holder.phone.setVisibility(View.VISIBLE);
            }else{
            	holder.name.setVisibility(View.VISIBLE);
            	holder.phone.setVisibility(View.VISIBLE);
            }
            if(MISS.equals(currentCallLogState)){
            	holder.image.setImageDrawable(getResources().getDrawable(R.drawable.callhistory_missed));
            }else if(COMBINED.equals(currentCallLogState)){
            	//holder.image.setImageDrawable(getResources().getDrawable(R.drawable.callhistory_incoming));
            }else if(OUTGOING.equals(currentCallLogState)){
            	holder.image.setImageDrawable(getResources().getDrawable(R.drawable.callhistory_outgoing));
            }else if(INCOME.equals(currentCallLogState)){
            	holder.image.setImageDrawable(getResources().getDrawable(R.drawable.callhistory_incoming));
            }
            
            try {
            	String datetime = list.get(position).date.replace("T", "");
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            	Date date = sdf.parse(datetime);
            	holder.date.setText(new SimpleDateFormat("yyyy-MM-dd").format(date));
            	holder.time.setText(new SimpleDateFormat("HH:mm:ss").format(date));
			} catch (Exception e) {
				L.e(e.toString());
			}
            
            ViewGroup iv = (ViewGroup)convertView.findViewById(R.id.history_item);
            if(mSelectIdx == -1){
            	iv.setSelected(false);
            }
            else if(mSelectIdx == position){
  				iv.setSelected(true);
			}else{
				iv.setSelected(false);
			}
            
            /*holder.del.setTag(position);
            holder.del.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int pos = (Integer)v.getTag();
	                Bluetooth.getInstance().delrecord(mrecords_order.get(pos));
				}
			});*/
			return convertView;
		}
	}
	
	public final class ViewHolder {
        public TextView name;
        public TextView phone;
        public ImageView image;
        public ImageButton del;
        public TextView date;
        public TextView time;
    }
	
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	L.e("callLog","callLog onReceive");
            if(CallLogUtil.ACITON_CALL_LOG.equals(intent.getAction())){
            	ArrayList<String> list = intent.getStringArrayListExtra("list");
            	int type = intent.getIntExtra("logType", -1);
            	switch (type) {
					case BluetoothConstants.CALL_TYPE_MISS:{
						missList = list;
						if(BluetoothPbapClient.MCH_PATH.equals(currentCallLogState)){
							refreshCallLog(list);
						}
					}
					break;
					case BluetoothConstants.CALL_TYPE_COMBINED:{
						combinedList = list;
						if(BluetoothPbapClient.CCH_PATH.equals(currentCallLogState)){
							refreshCallLog(list);
						}
					}
					break;		
					case BluetoothConstants.CALL_TYPE_OUTGOING:{
						outGoingList = list;
						if(BluetoothPbapClient.OCH_PATH.equals(currentCallLogState)){
							refreshCallLog(list);
						}
					}
					break;
					case BluetoothConstants.CALL_TYPE_INCOME:{
						incomeList = list;
						if(BluetoothPbapClient.ICH_PATH.equals(currentCallLogState)){
							refreshCallLog(list);
						}
					}
					break;
            	}
            }
        }
    };
    
    private MyTouchListener touchListener = new MyTouchListener() {
		
		@Override
		public void onTouchEvent(MotionEvent event) {
			//FIXME: 一汽要求,不自动流转 mAtTimerHelpr.reset();
		}
	};
    
	private void initView(View rootView){
		callLogList = new ArrayList<CallLog>();
		mAdapter = new CallLogAdapter(getActivity(), callLogList);
    	mTipEmptyTextView = (TextView)rootView.findViewById(R.id.callhistoryisempty);
        mHistoryListView = (ListView) rootView.findViewById(R.id.history_listview);
        mBtnDelete = (TextView) rootView.findViewById(R.id.delete);
		mBtnDeleteAll = (TextView) rootView.findViewById(R.id.delete_all);
		mBtnDelete.setEnabled(false);
		mBtnDeleteAll.setEnabled(false);
		
		mBtnDelete.setOnClickListener(this);
		mBtnDeleteAll.setOnClickListener(this);
        rootView.findViewById(R.id.btn_call_log_back).setOnClickListener(this);
        rootView.findViewById(R.id.btn_pull_call_log).setOnClickListener(this);
        rootView.findViewById(R.id.btn_miss_call_log).setOnClickListener(this);
        rootView.findViewById(R.id.btn_answer_call_log).setOnClickListener(this);
        rootView.findViewById(R.id.btn_test).setOnClickListener(this);
        rootView.findViewById(R.id.btn_test_2).setOnClickListener(this);
        mHistoryListView.setAdapter(mAdapter);
        mHistoryListView.setOnItemClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		BluetoothDevice device = Bluetooth.getInstance().getConnectedHFPDevice();
		switch (v.getId()) {
		case R.id.btn_call_log_back:
//			getActivity().findViewById(R.id.tab_dial).performClick();
			L.i(this.getClass().getSimpleName() + " startDialActivity true");
			//FIXME:12004
//			startActivity(new Intent(this.getActivity(),TuoXianDialActivity.class));
			Bluetooth.handStartActivity(this.getActivity(),TuoXianDialActivity.class);
			//
			break;
		case R.id.btn_pull_call_log:{
				currentCallLogState = OUTGOING;
				if(Bluetooth.getInstance().isConnected() && device != null){
					L.w("outGoingList" + outGoingList.size());
					refreshCallLog(outGoingList);
					CallLogUtil.getInstance(getActivity()).pullOutGoing(device);
				}
			}
			break;
		case R.id.btn_miss_call_log:{
				currentCallLogState = MISS;
				if(Bluetooth.getInstance().isConnected() && device != null){
					L.w("missList" + missList.size());
					refreshCallLog(missList);
					CallLogUtil.getInstance(getActivity()).pullMiss(device);
				}
			}
			break;
		case R.id.btn_answer_call_log:{
				//已接
				currentCallLogState = INCOME;
				if(Bluetooth.getInstance().isConnected() && device != null){
					L.w("incomeList" + incomeList.size());
					refreshCallLog(incomeList);
					CallLogUtil.getInstance(getActivity()).pullInCome(device);
				}
			}		
			break;
		case R.id.btn_test:{
				//全部
				currentCallLogState = COMBINED;
				if(Bluetooth.getInstance().isConnected() && device != null){
					CallLogUtil.getInstance(getActivity()).pullCombined(device);
					refreshCallLog(combinedList);
				}
			}
			break;
		case R.id.btn_test_2:{
				/*if(cl != null){
					cl.close();
				}*/
				CallLogUtil.getInstance(getActivity()).close();
			}
			break;
		case R.id.delete:{
			if(mAdapter !=null && mAdapter.getSelectIndex() > -1){
				showDeleteOneDialog();
			}
    	}
    	break;
	    case R.id.delete_all:{
	    		showDeleteAllDialog();
	    	}
	    	break;
		}
	}

    
    private void notifylistviewdataupdate()
    {
    	mAdapter.notifyDataSetChanged();
		if (mAdapter.isEmpty()){
        	(mTipEmptyTextView).setVisibility(View.VISIBLE);	
			mBtnDelete.setEnabled(false);
			mBtnDeleteAll.setEnabled(false);
		}else{
        	(mTipEmptyTextView).setVisibility(View.INVISIBLE);
			//mBtnDelete.setEnabled(true);
			mBtnDeleteAll.setEnabled(true);
		}
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
		final CallLog log = mAdapter.getList().get(position);
		mAdapter.setSelect(position);
		mBtnDelete.setEnabled(true);
        if (!TextUtils.isEmpty(log.number)) {
        	ConfirmDialog callDilog = new ConfirmDialog(getActivity());
    		callDilog.setContent(getResources().getString(R.string.confirm_call_phone));
        	ConfirmDialog.Callback callback = new ConfirmDialog.Callback() {
    			@Override
    			public void onCallOK(Dialog dialog, TextView ok, TextView no) {
    				Bluetooth.getInstance().call(log.number);
    			}
    			
    			@Override
    			public void onCallCanncel(Dialog dialog, TextView ok, TextView no) {
    				dialog.dismiss();
    			}
    		};
    		callDilog.setCallBack(callback);
    		callDilog.show();
    		
//			Bluetooth.getInstance().call(log.number);
		}
        
        
	}
	
	
	
	public synchronized void refreshCallLog(ArrayList<String> list){
		mAdapter.setSelect(-1);
		mAdapter.getList().clear();
		try {
        	for (String josonStr : list) {
        		CallLog callLog = new CallLog();
				JSONObject object = new JSONObject(josonStr);
				JSONArray phones = object.getJSONArray("phones");
				String formatted = object.getString("formatted");
				String family = object.getString("family");
				JSONObject phone;
				String number = "";
				String type  = "";
				String lable  = "";
				if(phones.length() > 0){	
					phone = phones.getJSONObject(0);
					if(phone.has("number")){
						number = phone.getString("number").replaceAll("-", "");

					}
					if(phone.has("type")){
						type = phone.getString("type");
					}
					if(phone.has("label")){
						lable = phone.getString("label");
					}
				}
				callLog.name = TextUtils.isEmpty(formatted) ? "" : formatted;
				callLog.type = type;
				callLog.number = number;
				
				if(object.has("datetime")){	
					String datetime = object.getString("datetime");
					callLog.date = datetime;
				}
				L.i("callLog","josonStr : " + josonStr);
				L.i("callLog","formatted : " + formatted + ",  family : " + family);
				L.i("callLog","phones number: " + number +
						",  type : " + type +
						",  lable : " + lable + object.getString("datetime"));
				
				mAdapter.getList().add(callLog);
			}
        	notifylistviewdataupdate();
    	} catch (Exception e) {
    		L.e(this.getClass().getName(), e.toString());
    	}
	}
	private class CallLog{
		public String name;
		public String type;
		public String number;
		public String date;
	}
	
	private void showDeleteOneDialog(){
		ConfirmDialog deleteAllDialog;
		deleteAllDialog = new ConfirmDialog(getActivity());
		deleteAllDialog.setContent(getResources().getString(R.string.confirm_del_one_calllog));
    	ConfirmDialog.Callback callback = new ConfirmDialog.Callback() {
			@Override
			public void onCallOK(Dialog dialog, TextView ok, TextView no) {
				mAdapter.removeSelectItem();
        		notifylistviewdataupdate();
				mBtnDelete.setEnabled(false);
			}
			
			@Override
			public void onCallCanncel(Dialog dialog, TextView ok, TextView no) {
				dialog.dismiss();
			}
		};
		deleteAllDialog.setCallBack(callback);
		deleteAllDialog.show();
	}
	
	private void showDeleteAllDialog(){
		ConfirmDialog deleteAllDialog;
		deleteAllDialog = new ConfirmDialog(getActivity());
		deleteAllDialog.setContent(getResources().getString(R.string.confirm_del_all_calllog));
    	ConfirmDialog.Callback callback = new ConfirmDialog.Callback() {
			@Override
			public void onCallOK(Dialog dialog, TextView ok, TextView no) {
				mAdapter.removeAll();
				mBtnDelete.setEnabled(false);
				mBtnDeleteAll.setEnabled(false);
			}
			
			@Override
			public void onCallCanncel(Dialog dialog, TextView ok, TextView no) {
				dialog.dismiss();
			}
		};
		deleteAllDialog.setCallBack(callback);
		deleteAllDialog.show();
	}
}