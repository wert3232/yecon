package com.can.ui.toyota;

import com.can.activity.R;
import com.can.ui.CanFrament;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * ClassName:CanToyotaInfo
 * 
 * @function:丰田原车信息
 * @author Kim
 * @Date:  2016-6-22 下午5:52:04
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanToyotaInfo extends CanFrament implements OnClickListener{

	private View mObjView = null;
	private CanToyotaCarSet  mObjToyotaCarSet;
	private CanToyotaCurFuel mObjToyotaCurFuel;
	private CanToyotaHisFuel mObjToyotaHisFuel;
	private CanToyotaTpms	 mObjToyotaTpmsInfo;
	private CanToyotaHyBrid  mObjToyotaHyBrid;
	private CanToyotaDspInfo mObjToyotaDspInfo;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (mObjView == null) {
			mObjView = inflater.inflate(R.layout.toyota_menu, container, false);
			init();
		}
		return mObjView;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}
	
	private void init(){
		
		mObjToyotaCarSet   = new CanToyotaCarSet();
		mObjToyotaCurFuel  = new CanToyotaCurFuel();
		mObjToyotaHisFuel  = new CanToyotaHisFuel();
		mObjToyotaTpmsInfo = new CanToyotaTpms();
		mObjToyotaHyBrid   = new CanToyotaHyBrid();
		mObjToyotaDspInfo  = new CanToyotaDspInfo();

		if (mObjView != null) {
			mObjView.findViewById(R.id.btn_menu_carset).setOnClickListener(this);
			mObjView.findViewById(R.id.btn_menu_curfuel).setOnClickListener(this);
			mObjView.findViewById(R.id.btn_menu_hisfuel).setOnClickListener(this);
			mObjView.findViewById(R.id.btn_menu_tpms).setOnClickListener(this);
			mObjView.findViewById(R.id.btn_menu_hybrid).setOnClickListener(this);
			mObjView.findViewById(R.id.btn_menu_dsp).setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_menu_carset:
			super.showPage(mObjToyotaCarSet);
			break;
		case R.id.btn_menu_curfuel:
			super.showPage(mObjToyotaCurFuel);
			break;
		case R.id.btn_menu_hisfuel:
			super.showPage(mObjToyotaHisFuel);
			break;
		case R.id.btn_menu_hybrid:
			super.showPage(mObjToyotaHyBrid);
			break;
		case R.id.btn_menu_tpms:
			super.showPage(mObjToyotaTpmsInfo);
			break;
		case R.id.btn_menu_dsp:
			super.showPage(mObjToyotaDspInfo);
			break;
		default:
			break;
		}
	}
}
