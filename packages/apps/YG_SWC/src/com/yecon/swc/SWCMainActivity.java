
package com.yecon.swc;

import android.mcu.McuBaseInfo;
import android.mcu.McuListener;
import android.mcu.McuManager;
import android.mcu.McuSWCInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import java.util.HashMap;

import static android.mcu.McuExternalConstant.*;
import static com.yecon.swc.DebugUtil.*;
import static com.yecon.swc.SWCConstants.*;

@SuppressLint("HandlerLeak")
public class SWCMainActivity extends Activity implements OnClickListener, OnTouchListener,
        OnCheckedChangeListener {
    private static final Object lock = new Object();

    private int[] btn_views;
    private int[]  btn_titles;
    private int[] btn_icons;
    private int[] key_index;

    private SWCItemView[] mLayoutSWCBtn;

    private TextView mTVPrompt;
    private TextView mTVReset;

    private McuManager mMcuManager;
    private McuSWCInfo mSWCInfo;

    private SWCHandler mHandler;

    private boolean mNeedUpadateUI = false;

    private McuListener mMcuListener = new McuListener() {

        @Override
        public void onMcuInfoChanged(McuBaseInfo mcuBaseInfo, int infoType) {
            if (mcuBaseInfo == null) {
                return;
            }

            if (infoType == MCU_SWC_DATA_INFO_TYPE || infoType == MCU_SWC_SAMPLING_INFO_TYPE
                    || infoType == MCU_SWC_RESISTANCE_INFO_TYPE) {
                mSWCInfo = mcuBaseInfo.getSWCInfo();

                printLog("SWCMainActivity - onMcuInfoChanged - infoType: " + infoType, true);
                //printLog(mSWCInfo.getSWCData(), MCU_SWC_DATA_LENGTH, true);

                if (mNeedUpadateUI && infoType == MCU_SWC_DATA_INFO_TYPE) {
                    mNeedUpadateUI = false;

                    updateSWCUI();
                } else if (infoType == MCU_SWC_SAMPLING_INFO_TYPE) {
                    updateSWCPrompt();
                } else if (infoType == MCU_SWC_RESISTANCE_INFO_TYPE) {
                    updateSWCResistance();
                }
            }
        }

    };
    
    private int getBtnIndexByKeyIndex(int keyIndex){
    	for( int i=0;i<key_index.length;i++){
    		if(keyIndex == key_index[i]){
    			return i;
    		}
    	}
    	return -1;
    }
    
    private Toast toast = null;
    private void showNotSupport(boolean hide){
    	if(toast!=null){
    		toast.cancel();
    		toast = null;
    	}
    	if(hide){
    		return;
    	}
    	toast = Toast.makeText(SWCMainActivity.this,
				R.string.not_support_key,
				Toast.LENGTH_SHORT);
    	toast.show();
    }
    private class SWCHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            synchronized (lock) {
                printLog("SWCMainActivity - handleMessage - msg.what: " + msg.what, true);

                switch (msg.what) {
                    case MCU_SWC_DATA_INFO_TYPE: {
                        int[] swcData = mSWCInfo.getSWCData();
                        if (swcData == null) {
                            printLog("SWCMainActivity - handleMessage - swcData is null", true);
                            return;
                        }
                        int btnIndex;
                        showNotSupport(true);
                        for (int keyIndex = 0; (keyIndex * 2) < swcData.length; keyIndex++) {
                            /**
                             * Byte0: FLAG -- 0:unlock, 1:lock Byte1: IR_KEY
                             */
                        	btnIndex = getBtnIndexByKeyIndex(keyIndex+1);
                        	if(btnIndex >= 0){
	                            if (swcData[keyIndex * 2] == 1) {                            	
	                                mLayoutSWCBtn[btnIndex].setSelected(true);
	                            } else {
	                                mLayoutSWCBtn[btnIndex].setSelected(false);
	                            }
                        	}
                        }
                        break;
                    }

                    case MCU_SWC_SAMPLING_INFO_TYPE: {
                        int[] samplingResults = mSWCInfo.getSWCSamplingResults();
                        if (samplingResults[0] == MCU_DATA_NOT_SAVED) {
                            mTVPrompt.setText(R.string.swc_prompt);
                        } else if (samplingResults[0] == MCU_DATA_SAVED) {
                            mTVPrompt.setText(R.string.swc_warning);
                        }
                        break;
                    }
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        printLog("SWCMainActivity - onCreate - start", true);

        if(!initData()){
        	finish();
        	return;
        }

        initUI();

        printLog("SWCMainActivity - onCreate - end", true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        printLog("SWCMainActivity - onResume - start", true);

        try {
            if (mMcuManager != null) {
                mNeedUpadateUI = true;

                mMcuManager.RPC_RequestMcuInfoChangedListener(mMcuListener);

                mMcuManager.RPC_SWCGetTable();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        printLog("SWCMainActivity - onResume - end", true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        showNotSupport(true);
        printLog("SWCMainActivity - onPause", true);
    }

    @Override
    protected void onStop() {
        super.onStop();

        printLog("SWCMainActivity - onStop", true);

        try {
            mMcuManager.RPC_SWCCommand(MCU_SWC_SAVE);

            mMcuManager.RPC_SWCCommand(MCU_SWC_END);

            mMcuManager.RPC_RemoveMcuInfoChangedListener(mMcuListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        printLog("SWCMainActivity - onDestroy", true);

        // try {
        // mMcuManager.RPC_SWCCommand(MCU_SWC_SAVE);
        //
        // mMcuManager.RPC_SWCCommand(MCU_SWC_END);
        // } catch (RemoteException e) {
        // e.printStackTrace();
        // }
    }

    private boolean initData() {
        boolean ret = initItemInfo();

        mMcuManager = (McuManager) getSystemService(Context.MCU_SERVICE);
        if (mMcuManager != null) {
            printLog("SWCMainActivity - mMcuManager: " + mMcuManager, true);

            try {
                mMcuManager.RPC_SWCCommand(MCU_SWC_STUDY);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        mHandler = new SWCHandler();
        
        return ret;
    }

	private boolean  initItemInfo() {
		String[] available_swc_keys = getResources().getStringArray(
				R.array.available_swc_keys);
		String[] display_keys = getResources().getStringArray(
				R.array.display_keys);
		String[] viewid = getResources().getStringArray(R.array.view_id);
		String[] titleid = getResources().getStringArray(R.array.title_id);
		String[] iconid = getResources().getStringArray(R.array.icon_id);

		// init key_index
		key_index = new int[display_keys.length];
		for (int i = 0; i < key_index.length; i++) {
			key_index[i] = 0;
		}
		for (int i = 0; i < display_keys.length; i++) {
			for (int j = 0; j < available_swc_keys.length; j++) {
				if (display_keys[i].equals(available_swc_keys[j])) {
					key_index[i] = j + 1;
				}
			}
		}
		for (int i = 0; i < key_index.length; i++) {
			if (key_index[i] <= 0) {
				Toast.makeText(
						this.getApplicationContext(),
						"Key Index Init Failed, Pls Check your display_defines.xml",
						Toast.LENGTH_LONG).show();
				return false; 
			}
		}

		// init view id
		String pgkName = this.getPackageName();
		this.btn_views = new int[display_keys.length];
		for (int i = 0; i < display_keys.length; i++) {
			btn_views[i] = getResources().getIdentifier(viewid[i], "id", pgkName);
			if (btn_views[i] == 0) {
				Toast.makeText(
						this.getApplicationContext(),
						viewid[i] + " View Id  Init Failed, Pls Check your display_defines.xml",
						Toast.LENGTH_LONG).show();
				return false; 
			}
		}

		// init title id
		this.btn_titles = new int[display_keys.length];
		for (int i = 0; i < display_keys.length; i++) {
			btn_titles[i] = getResources().getIdentifier(titleid[i], "string",
					pgkName);
			if (btn_titles[i] == 0) {
				Toast.makeText(
						this.getApplicationContext(),
						titleid[i] + " Title Id Init Failed, Pls Check your display_defines.xml",
						Toast.LENGTH_LONG).show();
				return false; 
			}
		}

		// init btn_icons
		this.btn_icons = new int[display_keys.length];
		for (int i = 0; i < display_keys.length; i++) {
			btn_icons[i] = getResources().getIdentifier(iconid[i], "drawable",
					pgkName);
			if (btn_icons[i] == 0) {
				Toast.makeText(
						this.getApplicationContext(),
						iconid[i] + " Icon  Id Init Failed, Pls Check your display_defines.xml",
						Toast.LENGTH_LONG).show();
				return false; 
			}
		}
		
		return true;
	}

    private void initUI() {
        setContentView(R.layout.swc_main_activity);        
        
        if(btn_views!=null && btn_views.length>0){
        	mLayoutSWCBtn = new SWCItemView[btn_views.length];
            for (int i = 0; i < btn_views.length; i++) {
                //SWCItemInfo info = mSWCItemArray[i];
            	
                SWCItemView itemView = (SWCItemView) findViewById(btn_views[i]);
                if(itemView == null){
                	Toast.makeText(
        					this.getApplicationContext(),
        					"FindVIewById Failed, Pls Check your display_defines.xml",
        					Toast.LENGTH_LONG).show();
        			finish();
        			return;
                }
                itemView.setIndex(key_index[i]);
                itemView.setTitle(btn_titles[i]);
                itemView.setIcon(btn_icons[i]);
                itemView.setOnTouchListener(this);

                mLayoutSWCBtn[i] = itemView;
            }
        }
        
        mTVPrompt = (TextView) findViewById(R.id.tv_prompt);

        mTVReset = (TextView) findViewById(R.id.tv_reset);
        mTVReset.setOnClickListener(this);
    }

    private void updateSWCUI() {
        new Thread() {

            @Override
            public void run() {
                super.run();

                printLog("SWCMainActivity - updateSWCUI - start", true);

                mHandler.sendEmptyMessage(MCU_SWC_DATA_INFO_TYPE);
            }

        }.start();
    }

    private void updateSWCPrompt() {
        new Thread() {

            @Override
            public void run() {
                super.run();

                printLog("SWCMainActivity - updateSWCPrompt - start", true);

                mHandler.sendEmptyMessage(MCU_SWC_SAMPLING_INFO_TYPE);
            }

        }.start();
    }

    private void updateSWCResistance() {
        new Thread() {

            @Override
            public void run() {
                super.run();

                printLog("SWCMainActivity - updateSWCPrompt - start", true);

                mHandler.sendEmptyMessage(MCU_SWC_RESISTANCE_INFO_TYPE);
            }

        }.start();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {

            case R.id.tv_reset: {
                try {
                    mNeedUpadateUI = true;

                    mMcuManager.RPC_SWCCommand(MCU_SWC_RESET);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int id = view.getId();
        int action = event.getAction();
        int index = -1;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            view.playSoundEffect(android.view.SoundEffectConstants.CLICK);
        }

        for (int i = 0; i < mLayoutSWCBtn.length; i++) {
            if (id == btn_views[i]) {
                if (action == KeyEvent.ACTION_DOWN) {
                    

                    index = key_index[i];
                    // not support extend key btns
                    if(index>(MCU_SWC_DATA_LENGTH/2)){
                    	showNotSupport(false);
                    }
                    else{
                    	mLayoutSWCBtn[i].setSelected(true);
                    }                   
                    
                } else if (action == KeyEvent.ACTION_UP) {
                    index = -1;

                    updateSWCUI();

                }
                break;
            }
        }

        printLog("SWCMainActivity - onTouch - index: " + index, true);

        if (index != -1) {
            try {
                byte[] param = new byte[4];
                param[0] = (byte) index;
                param[1] = (byte) 0x00;
                param[2] = (byte) 0x00;
                param[3] = (byte) 0x00;
                mMcuManager.RPC_KeyCommand(T_SEND_WHEEL_STUDY, param);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        byte[] param = new byte[4];
        if (isChecked) {
            param[0] = (byte) 0x00;
            param[1] = (byte) 0x00;
            param[2] = (byte) 0x00;
            param[3] = (byte) 0x00;
        } else {
            param[0] = (byte) 0x01;
            param[1] = (byte) 0x00;
            param[2] = (byte) 0x00;
            param[3] = (byte) 0x00;
        }
        try {
            mMcuManager.RPC_KeyCommand(T_SET_WHEEL_R_SW, param);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
