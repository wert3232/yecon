package com.yecon.carsetting.wifi;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.yecon.carsetting.R;

public class Fragment_Wifi_connect extends DialogFragment implements OnClickListener {

	public Context mContext;
	private OnConnectActionListener mActionListener;
	private TextView textView[] = new TextView[4];
	private String mStrSSID, mStrENCType;
	private TextView connectTx;
	private TextView disConnectTx;
	public Fragment_Wifi_connect() {

	}

	public Fragment_Wifi_connect(String ssid, String enctype, OnConnectActionListener actionListener) {
		mActionListener = actionListener;
		mStrSSID = ssid;
		mStrENCType = enctype;
	}

	public interface OnConnectActionListener {
		void connectAction(String mAction);
	}

	private void initData() {
		mContext = getActivity();
	}

	private void initView(View rootView) {
		textView[0] = (TextView) rootView.findViewById(R.id.wifi_connected_ssid);
		textView[1] = (TextView) rootView.findViewById(R.id.wifi_connected_enctype);
		textView[2] = (TextView) rootView.findViewById(R.id.dlg_btn_connect);
		textView[3] = (TextView) rootView.findViewById(R.id.dlg_btn_forget);
		textView[0].setText(mStrSSID);
		textView[1].setText(mStrENCType);
		connectTx = (TextView) rootView.findViewById(R.id.dlg_btn_connect);
		disConnectTx = (TextView) rootView.findViewById(R.id.dlg_btn_disconnect);
		
		disConnectTx.setOnClickListener(this);
		for (int i = 0; i < textView.length; i++) {
			textView[i].setOnClickListener(this);
		}
		
		if(isWifiConnected(getActivity())){
			connectTx.setVisibility(View.GONE);
			disConnectTx.setVisibility(View.VISIBLE);
		}else{
			connectTx.setVisibility(View.VISIBLE);
			disConnectTx.setVisibility(View.GONE);
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Bitmap bk = BitmapFactory.decodeResource(getResources(), R.drawable.dialog_set_bk);
		Window window = getDialog().getWindow();
		window.setLayout(bk.getWidth(), bk.getHeight());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomizeActivityTheme);
		initData();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.fragment_wifi_connect, container, false);
		initView(mRootView);
		return mRootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {

		case R.id.dlg_btn_connect:
			mActionListener.connectAction("connect");
			dismiss();
			break;

		case R.id.dlg_btn_forget:
			mActionListener.connectAction("forget");
			dismiss();
			break;
		case R.id.dlg_btn_disconnect:
			mActionListener.connectAction("disconnect");
			dismiss();
			break;

		default:
			break;
		}
	}
	
	public boolean isWifiConnected(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }
     
        return false ;
    }

}