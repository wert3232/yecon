package com.can.ui;

import java.util.ArrayList;
import com.can.activity.R;
import com.can.assist.CanXml;
import com.can.assist.CanXml.CarType_Info;
import com.can.assist.CanXml.E_Update_Type;
import com.can.ui.draw.AutoText;
import com.can.ui.draw.PopDialog;
import com.can.ui.draw.PopDialog.E_POPDIALOG_TYPE;
import com.can.ui.draw.PopDialog.OnConfirmListener;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * ClassName:CanChoose
 * 
 * @function:车型选择界面
 * @author Kim
 * @Date: 2016-6-4 上午9:32:54
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanChoose extends Activity implements OnClickListener,
		OnItemClickListener {

	private static CarType_Info mObjCarTypeInfo = null;
	protected final String TAG = this.getClass().getName();
	protected final String CAN_ICON = "persist.sys.fun.canbus";

	protected AutoText mObjTextCanBox    = null;
	protected AutoText mObjTextCanSeries = null;
	protected AutoText mObjTextCanType   = null;
	protected AutoText mObjTextCanCfg    = null;
	
	protected Button   mObjBtnCanBox     = null;
	protected Button   mObjBtnCanSeries  = null;
	protected Button   mObjBtnCanType    = null;
	protected Button   mObjBtnCanCfg     = null;
	
	protected ListView mObjListInfo   = null;
	protected listAdapter mObjlistAdapter = null;

	public ArrayList<String> mArrayList = new ArrayList<String>();
	private E_Update_Type mEUpdateType = E_Update_Type.eUpdate_Type_None;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		init();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		this.finish();
		super.onPause();
	}

	private void init() {
		mObjCarTypeInfo = CanXml.getInfo(getApplicationContext());
		mObjBtnCanBox = (Button) findViewById(R.id.btn_can_box);
		mObjBtnCanBox.setOnClickListener(this);
		mObjBtnCanSeries = (Button) findViewById(R.id.btn_can_line);
		mObjBtnCanSeries.setOnClickListener(this);
		mObjBtnCanType = (Button) findViewById(R.id.btn_can_type);
		mObjBtnCanType.setOnClickListener(this);
		mObjBtnCanCfg = (Button) findViewById(R.id.btn_can_cfg);
		mObjBtnCanCfg.setOnClickListener(this);
		Button ObjBtnSure = (Button) findViewById(R.id.btn_can_sure);
		ObjBtnSure.setOnClickListener(this);
		
		mObjTextCanBox    = (AutoText) findViewById(R.id.tx_can_box_name);
		mObjTextCanSeries = (AutoText) findViewById(R.id.tx_can_line_name);
		mObjTextCanType   = (AutoText) findViewById(R.id.tx_can_type_name);
		mObjTextCanCfg    = (AutoText) findViewById(R.id.tx_can_cfg_name);
		
		mObjlistAdapter = new listAdapter(getApplicationContext());
		mObjListInfo = (ListView) findViewById(R.id.list_can_info);
		mObjListInfo.setAdapter(mObjlistAdapter);
		mObjListInfo.setOnItemClickListener(this);

		boolean bret = false;
		Log.i(TAG, "++++++++++++++++++====:"+SystemProperties.getBoolean(CAN_ICON, bret));
		
		CanXml.parseCanxml(this);
		setValue(null);
	}

	public class listAdapter extends BaseAdapter {

		private Context mObjContext;
		private LayoutInflater mObjInflater;

		public listAdapter(Context context) {
			// TODO Auto-generated constructor stub
			mObjContext = context;
			mObjInflater = (LayoutInflater) mObjContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
				convertView = mObjInflater.inflate(R.layout.choose_listview, null);
				objTextView = (TextView) convertView.findViewById(R.id.tx_list_info);

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

	public void setValue(String strVal) {
		StringBuffer strText = new StringBuffer();
		CarType_Info objCarTypeInfo = null;

		if (strVal != null) {

			switch (mEUpdateType) {
			case eUpdate_Type_CanBox:
				objCarTypeInfo = CanXml.getAttr(mEUpdateType, strVal, null, null);
				break;
			case eUpdate_Type_CanSeries:
				objCarTypeInfo = CanXml.getAttr(mEUpdateType, mObjTextCanBox.getText().toString(), strVal, null);
				break;
			case eUpdate_Type_CanType:
				objCarTypeInfo = CanXml.getAttr(mEUpdateType, mObjTextCanBox.getText().toString(), mObjTextCanSeries.getText().toString(), strVal);
				break;
			case eUpdate_Type_CanCfg:
				break;
			default:
				break;
			}
		} else {
			objCarTypeInfo = mObjCarTypeInfo;
		}

		if (objCarTypeInfo != null) {
	
			if (objCarTypeInfo.strBoxName != null) {
				strText.append(objCarTypeInfo.strBoxName.toString());
			}

			mObjTextCanBox.setText(strText.toString());

			strText.delete(0, strText.length());
	
			if (objCarTypeInfo.strSeriesName != null) {
				strText.append(objCarTypeInfo.strSeriesName.toString());
			}

			mObjTextCanSeries.setText(strText.toString());

			strText.delete(0, strText.length());

			if (objCarTypeInfo.strTypeName != null) {
				strText.append(objCarTypeInfo.strTypeName.toString());
			}
			
			mObjTextCanType.setText(strText.toString());
		
			strText.delete(0, strText.length());

			if (objCarTypeInfo.strCfgName != null) {
				strText.append(objCarTypeInfo.strCfgName.toString());
			}

			mObjTextCanCfg.setText(strText.toString());
		}
	}

	private void sure() {

		if (!mObjCarTypeInfo.strBoxName.equals(mObjTextCanBox.getText().toString())
				|| !mObjCarTypeInfo.strSeriesName.equals(mObjTextCanSeries.getText().toString())
				|| !mObjCarTypeInfo.strTypeName.equals(mObjTextCanType.getText().toString())
				|| !mObjCarTypeInfo.strCfgName.equals(mObjTextCanCfg.getText().toString())) {

			// 比较是否选择相同的车型 条件是只要有一个不相等就认为要改变
			CarType_Info objCarTypeInfo = CanXml.getAttr(mObjTextCanBox.getText().toString(), 
					mObjTextCanSeries.getText().toString(), 
					mObjTextCanType.getText().toString(),
					mObjTextCanCfg.getText().toString());

			if (objCarTypeInfo != null) {
				//// 能到这里保证车型绝对是有效的 除非xml配置错误
				// 设置波特率
				// 设置桌面图标状态
				SystemProperties.set(CAN_ICON, (objCarTypeInfo.iNeedIcon == 1) ? "1" : "0");
				// 保存车型数据
				if (CanXml.setCanDescribe(objCarTypeInfo)) {
					Log.i(TAG, "set CarTypeInfo: "
							+ mObjTextCanBox.getText().toString() + " "
							+ mObjTextCanSeries.getText().toString() + " "
							+ mObjTextCanType.getText().toString() + " "
							+ mObjTextCanCfg.getText().toString());
					
					showTips(getResources().getString(R.string.tx_can_tips_str), true);
				}
			}
		}else {
			showTips(getResources().getString(R.string.tx_can_tips_same), false);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int iID = v.getId();

		switch (iID) {
		case R.id.btn_can_box:
		case R.id.btn_can_line:
		case R.id.btn_can_type:
		case R.id.btn_can_cfg:
		case R.id.btn_can_sure:
			setlist(iID);
			break;
		default:
			break;
		}
	}

	private void setlist(int iID) {

		boolean bChange = true;
		ArrayList<String> ObjArrayList = null;
		E_Update_Type eType = E_Update_Type.eUpdate_Type_None;

		switch (iID) {
		case R.id.btn_can_box:
			ObjArrayList = CanXml.getCanboxlist();
			eType = E_Update_Type.eUpdate_Type_CanBox;
			break;
		case R.id.btn_can_line:
			ObjArrayList = CanXml.getCanSeries(mObjTextCanBox.getText().toString());
			eType = E_Update_Type.eUpdate_Type_CanSeries;
			break;
		case R.id.btn_can_type:
			ObjArrayList = CanXml.getCanType(mObjTextCanBox.getText().toString(), mObjTextCanSeries.getText().toString());
			eType = E_Update_Type.eUpdate_Type_CanType;
			break;
		case R.id.btn_can_cfg:
			ObjArrayList = CanXml.getCanCfg();
			eType = E_Update_Type.eUpdate_Type_CanCfg;
			break;
		case R.id.btn_can_sure:
			bChange = false;
			sure();
			break;

		default:
			bChange = false;
			break;
		}

		if (bChange) {
			if (!ObjArrayList.isEmpty()) {

				mEUpdateType = eType;
				mArrayList = ObjArrayList;
				mObjListInfo.setVisibility(View.VISIBLE);
				mObjlistAdapter.notifyDataSetChanged();
			} else {
				Log.i(TAG, "ObjArrayList is empty!");
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		setValue((String) mObjlistAdapter.getItem(position));
	}
	
	private void showTips(String string, final boolean boff){
		
		PopDialog dialog = new PopDialog(string, E_POPDIALOG_TYPE.ePopDialog_choose);
		dialog.setOnConfirmListener(new OnConfirmListener() {
			
			@Override
			public void onConfirm() {
				// TODO Auto-generated method stub
				if (boff) {
					PowerManager pManager=(PowerManager) getSystemService(Context.POWER_SERVICE);  
                    pManager.reboot("");  
				}
			}

			@Override
			public void onSelPos(int iPos) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSeekVal(int iVal) {
				// TODO Auto-generated method stub
				
			}
		});
		
		dialog.show(getFragmentManager(), "sw");
	}
}
