/**
 * 
 */
/**
 * @author Administrator
 *
 */
package com.autochips.bluetooth.Fragment;


import com.autochips.bluetooth.Bluetooth;
import com.autochips.bluetooth.BluetoothReceiver;
import com.autochips.bluetooth.CmnUtil;
import com.autochips.bluetooth.FormatTelNumber;
import com.autochips.bluetooth.LocalBluetoothProfileManager;
import com.autochips.bluetooth.R;
import com.autochips.bluetooth.LocalBluetoothProfileManager.BTProfile;
import com.autochips.bluetooth.hf.BluetoothHfUtility;
import com.autochips.bluetooth.util.L;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
public class DialFragment extends BaseFragment implements OnClickListener, View.OnLongClickListener{

	private final int MAXPHONENUM = 20; 
    TextView m_callnumber_et;
    private View callBtn;
    public static String minputstr = "";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.bt_call, container, false);
		initView(mRootView);
		BluetoothReceiver.registerNotifyHandler(uiHandler);
		return mRootView;
	}
	@Override
	public void onDestroy() {
		BluetoothReceiver.unregisterNotifyHandler(uiHandler);
		super.onDestroy();
	}

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_del_num) {
            deleteAllDialPadString();
            return true;
        }
        return false;
    }
	@Override
	public void onClick(View v) {/*
		if (!Bluetooth.getInstance().isConnected()){
			CmnUtil.showToast(getActivity(), R.string.str_connect_hf);
			return;
		}*/
        switch (v.getId()) {
        case R.id.btn_call_back:
        	getActivity().onBackPressed();
        	break;
        case R.id.btn_call:
        	onCall();
            break;
        case R.id.btn_call_zero:
            addDialPadInputString("0");
            break;
        case R.id.btn_add:
        	addDialPadInputString("+");
        	break;
        case R.id.btn_call_one:
            addDialPadInputString("1");
            break;
        case R.id.btn_call_two:
            addDialPadInputString("2");
            break;
        case R.id.btn_call_three:
            addDialPadInputString("3");
            break;
        case R.id.btn_call_four:
            addDialPadInputString("4");
            break;
        case R.id.btn_call_five:
            addDialPadInputString("5");
            break;
        case R.id.btn_call_six:    
            addDialPadInputString("6");
            break;
        case R.id.btn_call_seven:
            addDialPadInputString("7");
            break;
        case R.id.btn_call_eight:
            addDialPadInputString("8");
            break;
        case R.id.btn_call_nine:    
            addDialPadInputString("9");
            break;
        case R.id.btn_call_asterisk:
            addDialPadInputString("*");
            break;
        case R.id.btn_call_pound:    
            addDialPadInputString("#");
            break;
        case R.id.btn_del_num:
            deleteOneDialPadString();
            break;
        case R.id.bluetooth_pairing:
        	getActivity().findViewById(R.id.tab_setting).performClick();
        	break;
        case R.id.call_bt_music:
        	getActivity().findViewById(R.id.tab_music).performClick();
        	break;
        case R.id.call_phone_book:
        	getActivity().findViewById(R.id.tab_phonebook).performClick();
        	break;
        case R.id.call_history:
        	getActivity().findViewById(R.id.tab_callhistory).performClick();
        	break;
        default:
            break;
        }
	}

	private void initView(View rootview){
        m_callnumber_et = (TextView)rootview.findViewById(R.id.text_call_info);
        m_callnumber_et.setText(minputstr);
        m_callnumber_et.addTextChangedListener(textViewWatcher);
        callBtn = (rootview.findViewById(R.id.btn_call));
        callBtn.setOnClickListener(this);
        (rootview.findViewById(R.id.btn_call_zero)).setOnClickListener(this);
        (rootview.findViewById(R.id.btn_call_one)).setOnClickListener(this);
        (rootview.findViewById(R.id.btn_call_two)).setOnClickListener(this);
        (rootview.findViewById(R.id.btn_call_three)).setOnClickListener(this);
        (rootview.findViewById(R.id.btn_call_four)).setOnClickListener(this);
        (rootview.findViewById(R.id.btn_call_five)).setOnClickListener(this);
        (rootview.findViewById(R.id.btn_call_six)).setOnClickListener(this);
        (rootview.findViewById(R.id.btn_call_seven)).setOnClickListener(this);
        (rootview.findViewById(R.id.btn_call_eight)).setOnClickListener(this);
        (rootview.findViewById(R.id.btn_call_nine)).setOnClickListener(this);
        (rootview.findViewById(R.id.btn_call_asterisk)).setOnClickListener(this);
        (rootview.findViewById(R.id.btn_call_pound)).setOnClickListener(this);
        (rootview.findViewById(R.id.btn_del_num)).setOnClickListener(this);
        (rootview.findViewById(R.id.btn_del_num)).setOnLongClickListener(this);
        (rootview.findViewById(R.id.btn_add)).setOnClickListener(this);
        (rootview.findViewById(R.id.bluetooth_pairing)).setOnClickListener(this);
        (rootview.findViewById(R.id.btn_call_back)).setOnClickListener(this);
        (rootview.findViewById(R.id.call_phone_book)).setOnClickListener(this);
        (rootview.findViewById(R.id.call_history)).setOnClickListener(this);
        (rootview.findViewById(R.id.call_bt_music)).setOnClickListener(this);
	}

	private Handler uiHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == BluetoothReceiver.MSG_BT_STATUS_NOTIFY) {
				Intent intent = (Intent) msg.obj;
				String recievedAction = intent.getAction();
				if (recievedAction.equals(LocalBluetoothProfileManager.ACTION_PROFILE_STATE_UPDATE)) {
					BTProfile profilename = (BTProfile) intent.getSerializableExtra(LocalBluetoothProfileManager.EXTRA_PROFILE);
					if (profilename == null)
						return;
					if (profilename.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_HF)) {
				        int profilestate = intent.getIntExtra(LocalBluetoothProfileManager.EXTRA_NEW_STATE,LocalBluetoothProfileManager.STATE_DISCONNECTED);
				        if(profilestate == LocalBluetoothProfileManager.STATE_DISCONNECTED){
				        	deleteAllDialPadString();
				        }
					}
				}else if(recievedAction.equals(BluetoothHfUtility.ACTION_CALL_STATE_CHANGE)){
		            int phoneCallState = intent.getIntExtra(BluetoothHfUtility.EXTRA_CALL_STATE, 0);
	                if(phoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_INCOMING ||
	                    phoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_OUTGOING){
		                
		            }else if(phoneCallState == BluetoothHfUtility.HFP_UTILITY_CALLSTATE_SPEAKING){
		            } else {
		            	deleteAllDialPadString();
	                }
	            }
			}
		}
	};
	
    public void onCall(){
        if (!minputstr.isEmpty()) {
        	Bluetooth.getInstance().call(minputstr);
		}else{
			minputstr = Bluetooth.getInstance().getlastcallnum();
			formatshownum();
		}
    }
    private boolean addDialPadInputString(CharSequence str) {
        if(str == null)
            return false;
        if(minputstr.length() > MAXPHONENUM){
        	CmnUtil.showToast(getActivity(),  R.string.str_call_number_is_too_long);
            return false;
    	}
        minputstr += str;
        formatshownum();
        return true;
    }

    private boolean deleteAllDialPadString() {
    	minputstr = "";
        m_callnumber_et.setText(minputstr);
        return true ;
    }

    private boolean deleteOneDialPadString() {
    	if (minputstr.isEmpty()) {
			return true;
		}
    	minputstr = minputstr.substring(0, minputstr.length() - 1);
    	formatshownum();
        return true ;
    }
    private void formatshownum(){
        String callnum_afterformat 	= FormatTelNumber.ui_format_tel_number(minputstr);
        m_callnumber_et.setText(callnum_afterformat);
    }
    
    private TextWatcher textViewWatcher = new TextWatcher() {
        private String temp;
        private int editStart ;
        private int editEnd ;
        private int length;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            temp = s.toString().trim();
            length = temp.length();
            L.w("Dial", "m_callnumber_et Text:" + s + " start:" + start + " before:" + before + " count:" + count + " length:" + length);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            editStart = m_callnumber_et.getSelectionStart();
            editEnd = m_callnumber_et.getSelectionEnd();
            if(length > 0){
            	callBtn.setEnabled(true);
            }else{
            	callBtn.setEnabled(false);
            }
        }
    };
}