
package com.yecon.fmradio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.mcu.McuBaseInfo;
import android.mcu.McuListener;
import android.mcu.McuManager;
import android.mcu.McuRadioBandInfo;
import android.mcu.McuRadioPresetListInfo;
import android.media.AudioManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.autochips.inputsource.AVIN;
import com.autochips.inputsource.InputSource;
import com.autochips.inputsource.InputSourceClient;
import com.yecon.common.SourceManager;
import com.yecon.fmradio.util.L;
import com.media.constants.MediaConstants;
import com.tuoxianui.tools.AtTimerHelpr;
import com.tuoxianui.view.MuteTextView;
import com.yecon.savedata.SaveData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import static com.yecon.fmradio.FMRadioConstant.*;
import static com.yecon.fmradio.DebugUtil.*;
import static android.mcu.McuExternalConstant.*;
import static android.constant.YeconConstants.*;

@SuppressLint({ "HandlerLeak", "DefaultLocale" })
public class FMRadioMainActivity extends Activity implements OnClickListener, OnLongClickListener {
    public static final String ACTION_AVIN_REQUEST_NOTIFY = "yecon.intent.action.AVIN.REQUEST";
    private static final Object lock = new Object();

    private static final int[] BAND_TEXT = {
            R.string.fmr_band_fm1, R.string.fmr_band_fm2, R.string.fmr_band_fm3,
            R.string.fmr_band_am1, R.string.fmr_band_am2
    };

    private static final int[] FREQ_TITLE_ID = {
            R.id.tv_freq_title1, R.id.tv_freq_title2, R.id.tv_freq_title3, R.id.tv_freq_title4,
            R.id.tv_freq_title5, R.id.tv_freq_title6, R.id.tv_freq_title7
    };

    private static final int MSG_SET_VOLUME = 0x1001;
    private static final int SET_VOLUME_DELAY = 100;

    private static final int PS_DISPLAY_DELAY = 500;
    private static final int AS_DISPLAY_DELAY = 100;
    private static final int PREVIEW_FREQ_NUM = 12;
    private static final int FREQ_TITLE_NUM = 7;

    private static final String BAND_TYPE_AM = "bandTypeAM";
    private static final String BAND_TYPE_FM = "bandTypeFM";
    
    private TextView[] mTVFreqTitle;
    
    private ViewGroup mSBTagsWrap;
    private SeekBar mSBFreq;
    private FreqNumImageView mFreqNumImageView;
    private FreqNumTextView mFreqNumTextView;
    private TextView mTVFreqUnit;
    private TextView mTVFreqBand;
    private TextView mTVPS;

    private ImageView mIVSub;
    private ImageView mIVAdd;

    private TextView mTVLoc;
    private TextView mIVUseST;
    
    private TextView mFmrMaxUnit;
    private TextView mFmrMinUnit;
    private FreqItemView mTVPreviewFreq1;
    private FreqItemView mTVPreviewFreq2;
    private FreqItemView mTVPreviewFreq3;
    private FreqItemView mTVPreviewFreq4;
    private FreqItemView mTVPreviewFreq5;
    private FreqItemView mTVPreviewFreq6;
    private FreqItemView mTVPreviewFreq7;
    private FreqItemView mTVPreviewFreq8;
    private FreqItemView mTVPreviewFreq9;
    private FreqItemView mTVPreviewFreq10;
    private FreqItemView mTVPreviewFreq11;
    private FreqItemView mTVPreviewFreq12;
    private FreqItemView[] mTVPreviewFreqArray;

    private TextView mBtnOpBand;
    private TextView mBtnOpLoc;
    private TextView mBtnOpStereo;
    private TextView mBtnOpAS;
    private TextView mBtnOpPS;
    private TextView mBtnDelete;
    private TextView mBtnFavorite;
    private TextView mBtnList;
    private TextView mBtnAuto;
    private TextView mBtnManual;
    private TextView mBtnSettings;
    private View mBtnMute;
    
    private int mCurrBand;
    private int mCurrBandFreq;
    private int mCurrBandMinFreq;
    private int mCurrBandMaxFreq;
    private int mCurrBandGranularity;
    private int mCurrPreset;
    private FreqItemView mCurrentSelectFreq;
	private ViewPager mViewPager;
	private MyPagerAdapter mAdapter;
	private ArrayList<View> aList;
	private ListView listView;
	private AtTimerHelpr mAtTimerHelpr;
    
    private boolean mIsStereo;
    private boolean mUseStereo;
    private boolean mIsLoc;

    private int[] mPresetList = new int[MCU_RADIO_PRESETLIST_LENGTH];
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Integer> mPresetListMap = new HashMap<Integer, Integer>();

    private int mCurrPSItem = 0;
    private int mCurrPSItemCount = 0;

    private boolean mStartPresetLoading = false;

    private boolean mPSIsVisible = false;

    private boolean mASIsVisible = false;
    private int mASCount = 0;

    private FMRadioHandler mHandler;
    private int mOpType;

    private boolean mAppIsFinished = false;

    private McuManager mMcuManager;
    private McuRadioBandInfo mBandInfo;
    private McuRadioPresetListInfo mPresetListInfo;

    private AudioManager mAudioManager;
    private SaveData mObjSaveData = null;

    public AVIN mAvinA;
    private int mCurrentFreqValue;
    private SharedPreferences.Editor mEditor ;
	private SharedPreferences mPref;
	private boolean isDisplaySelectedFreq = false;
	private boolean isDeleteSelectedFreq = false;
	private boolean isScanning = false;
	private boolean isSeeking= false;
	private boolean isAutoSeek = true;
	
	private List<Integer> freqList = new ArrayList<Integer>();
	private FreqAdapter freqAdapter;
	
	private TextView mInputFreq;
	private AlertDialog mDialog;
	private ProgressBar progressBar;
	private TextView progressFreq;
	private TextView progressText;
	private TextView progressButton;
	
	int progress = 0;
	int[] preset;
	
	protected class FreqAdapter extends BaseAdapter {

	    private List<Integer> list = null;

	    private Context context = null;

	    private LayoutInflater inflater = null;
		private int  selectItem=-1;

	    public FreqAdapter(List<Integer> list, Context context) {
	        this.list = list;
	        this.context = context;
	        // 布局装载器对象
	        inflater = LayoutInflater.from(context);
	    }

	    // 适配器中数据集中数据的个数
	    @Override
	    public int getCount() {
	        return list.size();
	    }

	    // 获取数据集中与指定索引对应的数据项
	    @Override
	    public Object getItem(int position) {
	        return list.get(position);
	    }

	    // 获取指定行对应的ID
	    @Override
	    public long getItemId(int position) {
	        return position;
	    }

	    // 获取每一个Item显示的内容
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {

	        final Integer item = list.get(position);

	        View view = inflater.inflate(R.layout.list_item, null);

	        TextView tvFreq = (TextView) view.findViewById(R.id.tv_freq);
			String valueStr;
    		if(isFMBand())
    			valueStr = String.format("%.01f",(float) item / 100) + getString(R.string.fmr_unit_mhz);
    		else
    			valueStr = item + getString(R.string.fmr_unit_khz);

	        tvFreq.setText(valueStr);
			//LocalListPosition();
			if (position == selectItem) {    
  
                view.setBackgroundResource(R.drawable.list_item_press);    
  
            }

	        return view;
	    }
		public  void setSelectItem(int selectItem) {    
  
			this.selectItem = selectItem;    
  
		}   
	}
	
	public void LocalListPosition(){
		listView.post(new Runnable() {  
		    @Override  
		    public void run() {
				if(listView!=null){
					freqAdapter.setSelectItem(-1);
					for(int i = 1; i<preset.length;i++ ){
						Log.d("TEST",preset[i]+" "+(mCurrentFreqValue));
						if(preset[i] == (mCurrentFreqValue)){
				        	listView.requestFocusFromTouch();//获取焦点  
							listView.setItemChecked(i-1,true);
				        	listView.setSelection(i-1);
							freqAdapter.setSelectItem(i-1);
							break;
						}
					}
				}
		    }  
		});  
	}
	
	private void updateScrollBar() {
		if(listView==null)
			return;
        try {
			if(freqList.size()>6){
				listView.setFastScrollAlwaysVisible(true);
			}else{
				listView.setFastScrollAlwaysVisible(false);
			}
			
            /*Field f = AbsListView.class.getDeclaredField("mFastScroller");
            f.setAccessible(true);
            Object o = f.get(listView);
            f = f.getType().getDeclaredField("mThumbDrawable");
            f.setAccessible(true);
            Drawable drawable = (Drawable) f.get(o);
			if(freqList.size()>6){
	            drawable = getResources().getDrawable(R.drawable.scroll_thumb);
				listView.setFastScrollAlwaysVisible(true);
				//drawable.setBounds(20,20,20,20);
			}else{
				drawable = null;
				listView.setFastScrollAlwaysVisible(false);
			}
			//drawable.width(40);
			//drawable.height = (100);
            f.set(o, drawable);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
	
    private McuListener mMcuListener = new McuListener() {

        @Override
        public void onMcuInfoChanged(McuBaseInfo mcuBaseInfo, int infoType) {
            if (mcuBaseInfo == null) {
                printLog("onMcuInfoChanged - mcuBaseInfo is null", true);
                return;
            }

            switch (infoType) {
                case MCU_RADIO_BAND_INFO_TYPE: {
                    onRadioBandInfoChanged(mcuBaseInfo);
                    boolean t_isScanning = isScanning;
                    boolean t_isSeeking = isSeeking;
                    McuRadioBandInfo baseInfo = mcuBaseInfo.getRadioBandInfo();
                    isScanning = mcuBaseInfo.getRadioBandInfo().getAutoMemoryScanStatus() != 0;
                    isSeeking = mcuBaseInfo.getRadioBandInfo().getSeekStatus() > 0;
                    L.e("t_isScanning:" + t_isScanning + "   isScanning:"  + isScanning + "  : " + mcuBaseInfo.getRadioBandInfo().getAutoMemoryScanStatus() + " mCurrBandFreq:" + mCurrBandFreq);
                    L.e("isSeeking:" + baseInfo.getSeekStatus());
                    if(isScanning != t_isScanning && t_isScanning){
                        setFreqItemSelected(null);
                    	for(int index = 0; index < mPresetList.length; index++){
                        	int freq = mPresetList[index];
                        	if(freq == 0){
                        		continue;
                        	}
                        	if(freq == mCurrBandFreq){
                        		setFreqItemSelected(mTVPreviewFreqArray[index]);
                        		break;
                        	}
//                        	L.e("isScanning index:" + index + " " + freq + " " + (freq == mCurrBandFreq));
                        }
                    }else if(isSeeking != t_isSeeking && t_isSeeking){
                        setFreqItemSelected(null);
                    	for(int index = 0; index < mPresetList.length; index++){
                        	int freq = mPresetList[index];
                        	if(freq == 0){
                        		continue;
                        	}
                        	if(freq == mCurrBandFreq){
                        		setFreqItemSelected(mTVPreviewFreqArray[index]);
                        		break;
                        	}
                        	//L.e("isSeeking index:" + index + " " + freq + " " + (freq == mCurrBandFreq));
                        }
                    }
                    /*if(!isScanning){
                    	setFreqItemSelected(null);
                    	for(int index = 0; index < mPresetList.length; index++){
                        	int freq = mPresetList[index];
                        	if(freq == 0){
                        		continue;
                        	}
                        	if(freq == mCurrBandFreq){
                        		setFreqItemSelected(mTVPreviewFreqArray[index]);
                        		break;
                        	}
                        	L.e("isScanning index:" + index + " " + freq + " " + (freq == mCurrBandFreq));
                        }
                    }*/
					if(0x222E == baseInfo.getCurrBandMinFreq())
						preset = baseInfo.getFMPreset();
					else
						preset = baseInfo.getAMPreset();
					freqList.clear();
					for(int i = 1; i < preset.length; i++ ){
						Log.d("TEST",i + " " + preset[i]);
						freqList.add(preset[i]);
					}
					freqAdapter.notifyDataSetChanged();
					updateScrollBar();
					isAutoSeek = !baseInfo.isAutoScan();
					if(isAutoSeek){
						mBtnAuto.setVisibility(View.VISIBLE);
						mBtnManual.setVisibility(View.GONE);
					}else{
						mBtnAuto.setVisibility(View.GONE);
						mBtnManual.setVisibility(View.VISIBLE);
					}
                    break;
                }

                case MCU_RADIO_PRESET_LIST_INFO_TYPE: {
                    onRadioPresetListInfoChanged(mcuBaseInfo);
                    break;
                }

            }
        }

        private void onRadioBandInfoChanged(McuBaseInfo mcuBaseInfo) {
            synchronized (lock) {
                mBandInfo = mcuBaseInfo.getRadioBandInfo();

                mCurrBandMinFreq = mBandInfo.getCurrBandMinFreq();
                mCurrBandMaxFreq = mBandInfo.getCurrBandMaxFreq();

                mCurrBandGranularity = mBandInfo.getGranularity();
                mIsLoc = mBandInfo.isLoc();

                if (isFMBand()) {
                    if (mBandInfo != null) {
                        mIsStereo = mBandInfo.isStereo();
                        mUseStereo = mBandInfo.isUseStereo();
                    } else {
                        mIsStereo = false;
                        mUseStereo = false;
                    }
                } else {
                    mIsStereo = false;
                    mUseStereo = false;
                }

                StringBuffer sb = new StringBuffer();
                sb.append("onRadioBandInfoChanged - ");
                sb.append("min freq: ");
                sb.append(mCurrBandMinFreq);
                sb.append(" - max freq: ");
                sb.append(mCurrBandMaxFreq);
                sb.append(" - curr granularity: ");
                sb.append(mCurrBandGranularity);
                sb.append(" - is loc: ");
                sb.append(mIsLoc);
                sb.append(" - is stereo: ");
                sb.append(mIsStereo);
                sb.append(" - use stereo: ");
                sb.append(mUseStereo);
                sb.append(" - mOpType: ");
                sb.append(mOpType);
                printLog(sb.toString(), true);

                FMRadioThread thread = new FMRadioThread(mHandler, MCU_RADIO_BAND_INFO_TYPE);
                thread.start();
            }
        }

        private void onRadioPresetListInfoChanged(McuBaseInfo mcuBaseInfo) {
            synchronized (lock) {
                mPresetListInfo = mcuBaseInfo.getRadioPresetListInfo();
                mCurrBand = mPresetListInfo.getCurrBand();
                mCurrBandFreq = mPresetListInfo.getCurrBandFreq();
                mCurrPreset = mPresetListInfo.getCurrPreset();
                mPresetList = mPresetListInfo.getPresetList();

//                L.e("onRadioPresetListInfoChanged:" + mPresetList.toString());
                if (isFMBand()) {
                    SystemProperties.set(PROPERTY_KEY_AVIN_TYPE, "fm_type");

                    if (mBandInfo != null) {
                        mIsStereo = mBandInfo.isStereo();
                        mUseStereo = mBandInfo.isUseStereo();
                    } else {
                        mIsStereo = false;
                        mUseStereo = false;
                    }
                } else {
                    SystemProperties.set(PROPERTY_KEY_AVIN_TYPE, "am_type");

                    mIsStereo = false;
                    mUseStereo = false;
                }

                int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

                StringBuffer sb = new StringBuffer();
                sb.append("onRadioPresetListInfoChanged - ");
                sb.append(" - mOpType: ");
                sb.append(mOpType);
                sb.append(" - curr band: ");
                sb.append(mCurrBand);
                sb.append(" - curr freq: ");
                sb.append(mCurrBandFreq);
                sb.append(" - curr preset: ");
                sb.append(mCurrPreset);
                sb.append(" - preset list: ");
                for (int i = 0; i < 6; i++) {
                    sb.append(mPresetList[i]);
                    sb.append(" ");
                }
                printLog(sb.toString(), true);

                FMRadioThread thread = new FMRadioThread(mHandler, MCU_RADIO_PRESET_LIST_INFO_TYPE);
                thread.start();

                mObjSaveData.setCurBand(mCurrBand);
                mObjSaveData.setCurFreq(mCurrBandFreq);
                mObjSaveData.setRadioListId(mCurrPreset);
            }
        }
    };

    private AVIN.OnCbmCmdListener mListenerCbmCmd = new AVIN.OnCbmCmdListener() {

        @Override
        public void onCmd(int what, int extra) {
            if (what == InputSourceClient.INPUTSOURCE_CBM_STOP) {
                try {
                    if (mPSHandler.hasCallbacks(mPSRunnable)) {
                        mPSHandler.removeCallbacks(mPSRunnable);
                        mBtnOpPS.performClick();
                    }

                    if (mASHandler.hasCallbacks(mASRunnable)) {
                        mASHandler.removeCallbacks(mASRunnable);
                      
                        //mBtnOpAS.performClick();
                    }
                    //mMcuManager.RPC_SetSource(MCU_SOURCE_OFF, 0x00);
                } catch (Exception e) {
                	L.e(e.toString());
                }
            } else if (what == InputSourceClient.INPUTSOURCE_CBM_START) {

            }
        }
    };

    private Handler mPSHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            synchronized (lock) {
                printLog("mPSHandler - mPSIsVisible: " + mPSIsVisible);

                if (mPSIsVisible) {
                    mTVPS.setVisibility(View.INVISIBLE);
                    mPSIsVisible = false;
                } else {
                    mTVPS.setVisibility(View.VISIBLE);
                    mPSIsVisible = true;
                }

                mCurrBandFreq = mPresetListInfo.getCurrBandFreq();
                printLog("mPSHandler - mCurrBandFreq: " + mCurrBandFreq);
                if (mCurrBandFreq < mCurrBandMinFreq || mCurrBandFreq > mCurrBandMaxFreq) {
                    mPSHandler.postDelayed(mPSRunnable, PS_DISPLAY_DELAY);
                    return;
                }

                if (isFMBand()) {
                    mSBFreq.setProgress((mCurrBandFreq - mCurrBandMinFreq) / 10);
                } else {
                    mSBFreq.setProgress(mCurrBandFreq - mCurrBandMinFreq);
                }

                int tmpBand = mPresetListInfo.getCurrBand();
                int tmpPreset = mPresetListInfo.getCurrPreset();

                if ((mCurrBand != tmpBand) || (mCurrPreset != tmpPreset)) {
                    mCurrPSItem++;
                }

                mCurrBand = tmpBand;
                mCurrPreset = tmpPreset;

                mTVFreqBand.setText(BAND_TEXT[mCurrBand]);
				mBtnOpBand.setText(BAND_TEXT[mCurrBand]);
                setFmrMaxMinUnit(getResources().getString(BAND_TEXT[mCurrBand]));
                
                Log.e(this.getClass().getSimpleName(),"[mPSHandler]");
                for (int i = 0; i < PREVIEW_FREQ_NUM; i++) {
                    if (i + 1 == mCurrPreset) {
//                        mTVPreviewFreqArray[i].setSelected(true);
                        Log.e(this.getClass().getSimpleName(),"setSelected [mPSHandler] mCurrPreset:" + i);
                    } else {
//                        mTVPreviewFreqArray[i].setSelected(false);
                    }
                }

                int psStatus = mBandInfo.getPreviewScanStatus();
                printLog("mPSHandler - psStatus: " + psStatus);
                if (psStatus == 0) {
                    mTVPS.setVisibility(View.INVISIBLE);
                    mPSIsVisible = false;

                    mPSHandler.removeCallbacks(mPSRunnable);
                    return;
                }

                printLog("mPSHandler - mCurrPSItem = " + mCurrPSItem + " - mCurrPSItemCount: "
                        + mCurrPSItemCount);
                if (mCurrPSItem > mCurrPSItemCount) {
                    mTVPS.setVisibility(View.INVISIBLE);
                    mPSIsVisible = false;

                    mPSHandler.removeCallbacks(mPSRunnable);
                } else {
                    mPSHandler.postDelayed(mPSRunnable, PS_DISPLAY_DELAY);
                }
            }
        }

    };

    private Runnable mPSRunnable = new Runnable() {

        @Override
        public void run() {
            mPSHandler.sendEmptyMessage(0);
        }
    };

    private Handler mASHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            synchronized (lock) {
                printLog("mASHandler - mASIsVisible: " + mASIsVisible);

                mASCount++;

                if (mASCount == (PS_DISPLAY_DELAY / 100)) {
                    mASCount = 0;
                    if (mASIsVisible) {
                        mASIsVisible = false;
                    } else {
                        mASIsVisible = true;
                    }
                }

                mCurrBandFreq = mPresetListInfo.getCurrBandFreq();
                if (mCurrBandFreq < mCurrBandMinFreq || mCurrBandFreq > mCurrBandMaxFreq) {
                    mASHandler.postDelayed(mASRunnable, AS_DISPLAY_DELAY);
                    return;
                }

                if (isFMBand()) {
                    mSBFreq.setProgress((mCurrBandFreq - mCurrBandMinFreq) / 10);
					if(progressBar!=null){
						if(progressBar.getProgress()<(mCurrBandFreq - mCurrBandMinFreq) / 10){
							progressBar.setProgress((mCurrBandFreq - mCurrBandMinFreq) / 10);
							progressFreq.setText(""+(float)mCurrBandFreq/100);
						}
					}
                } else {
                    mSBFreq.setProgress(mCurrBandFreq - mCurrBandMinFreq);
					if(progressBar!=null){
						if(progressBar.getProgress()<(mCurrBandFreq - mCurrBandMinFreq)){
							progressBar.setProgress(mCurrBandFreq - mCurrBandMinFreq);
							progressFreq.setText(""+mCurrBandFreq);
						}
					}
                }
				mAtTimerHelpr.reset();
				//Log.d("TEST","mCurrBandFreq "+mCurrBandFreq +" getProgress "+progressBar.getProgress() + " " + (mCurrBandFreq - mCurrBandMinFreq));
				if(mCurrBandFreq==mCurrBandMaxFreq || (progressBar!=null && (mCurrBandFreq - mCurrBandMinFreq)/(isFMBand()?10:1) < progressBar.getProgress())){
					progressBar.setProgress(progressBar.getMax());
					progressText.setText(R.string.progress_text_finish);
					progressFreq.setVisibility(View.GONE);
					progressButton.setVisibility(View.VISIBLE);
					new Thread(new Runnable(){
						public void run(){
							try{
								Thread.sleep(2000);
								if(progressButton!=null)
									progressButton.callOnClick();
							}catch(Exception e){}
						}
					}).start();
					int freq = mCurrBandMinFreq;
					if(freqList.size()>0)
						freq = freqList.get(0);
					setFreqNumTextView(freq);
	                byte[] param = new byte[4];
	                param[0] = (byte) (freq >> 24 & 0xFF);
	                param[1] = (byte) (freq >> 16 & 0xFF);
	                param[2] = (byte) (freq >> 8 & 0xFF);
	                param[3] = (byte) (freq & 0xFF);
	                printLog(param, 4);
	                try {
	                    if (isFMBand()) {
	                        mMcuManager.RPC_KeyCommand(T_RADIO_FM_FREQ, param);
	                    } else {
	                        mMcuManager.RPC_KeyCommand(T_RADIO_AM_FREQ, param);
	                    }

	                } catch (RemoteException e) {
	                	L.e(e.toString());
		            }
				}

                int asStatus = mBandInfo.getAutoMemoryScanStatus();
                printLog("FMRadioMainActivity - mASHandler - asStatus: " + asStatus);

                if (asStatus == 0) {
                    mASIsVisible = false;

                    mCurrBand = mPresetListInfo.getCurrBand();
                    mCurrPreset = mPresetListInfo.getCurrPreset();

                    mTVFreqBand.setText(BAND_TEXT[mCurrBand]);
					mBtnOpBand.setText(BAND_TEXT[mCurrBand]);
                    setFmrMaxMinUnit(getResources().getString(BAND_TEXT[mCurrBand]));
                    
                    Log.e(this.getClass().getSimpleName(),"[mASHandler]");
                    for (int i = 0; i < PREVIEW_FREQ_NUM; i++) {
                        if (i + 1 == mCurrPreset) {
//                            mTVPreviewFreqArray[i].setSelected(true);
                        	Log.e(this.getClass().getSimpleName(),"setSelected [mASHandler] mCurrPreset:" + i);
                        } else {
//                            mTVPreviewFreqArray[i].setSelected(false);
                        }
                    }

                    mASHandler.removeCallbacks(mASRunnable);
                } else {
                    mASHandler.postDelayed(mASRunnable, AS_DISPLAY_DELAY);
                }

            }
        }

    };

    private Runnable mASRunnable = new Runnable() {

        @Override
        public void run() {
            mASHandler.sendEmptyMessage(0);
        }

    };

    private class FMRadioHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            synchronized (lock) {
                int what = msg.what;
                if (what == MSG_SET_VOLUME) {
                    boolean isPowerKey = SystemProperties.getBoolean(PROPERTY_KEY_POWERKEY,
                            false);
                    if (!isPowerKey) {
                        setVolumeMute(false);
                    }
                    return;
                }

                if (what == MCU_RADIO_BAND_INFO_TYPE) {
                    // if (isFMBand()) {
                    // mSBFreq.setMax((mCurrBandMaxFreq -
                    // mBandInfo.getCurrBandMinFreq()) / 10);
                    // } else {
                    // mSBFreq.setMax(mCurrBandMaxFreq -
                    // mBandInfo.getCurrBandMinFreq());
                    // }
                } else if (what == MCU_RADIO_PRESET_LIST_INFO_TYPE) {
                    mTVFreqBand.setText(BAND_TEXT[mCurrBand]);
					mBtnOpBand.setText(BAND_TEXT[mCurrBand]);
                    setFmrMaxMinUnit(getResources().getString(BAND_TEXT[mCurrBand]));

                    setFreqTitle();

                    setFreqUnit();

                    setPresetList();
					if(mBandInfo!=null){
                    if (isFMBand()) {
                        mSBFreq.setMax((mCurrBandMaxFreq - mBandInfo.getCurrBandMinFreq()) / 10);
                        mSBFreq.setProgress((mCurrBandFreq - mCurrBandMinFreq) / 10);
                    } else {
                        mSBFreq.setMax(mCurrBandMaxFreq - mBandInfo.getCurrBandMinFreq());
                        mSBFreq.setProgress(mCurrBandFreq - mCurrBandMinFreq);
                    }
					}

                    printLog("FMRadioHandler - mCurrBandFreq:" + mCurrBandFreq);

//                    setFreqNumImageView(mCurrBandFreq);
                    setFreqNumTextView(mCurrBandFreq);
                    readSelectFreqAndDisPlay();
                    
                }

                if (isFMBand()) {
//                    mTVLoc.setVisibility(View.VISIBLE);
                	mTVLoc.setVisibility(View.INVISIBLE);
                    mBtnOpLoc.setEnabled(true);

                    if (mIsLoc) {
                        mTVLoc.setText(R.string.fmr_loc);
                    } else {
                        // mTVLoc.setText(R.string.fmr_dx);
                        mTVLoc.setText("");
                    }

                    mBtnOpStereo.setEnabled(true);
                    if (mUseStereo) {
                        mIVUseST.setVisibility(View.INVISIBLE);
//                        mIVUseST.setSelected(false);
                        if (mIsStereo) {
//                            mIVUseST.setSelected(true);
                            mIVUseST.setVisibility(View.VISIBLE);
                        } else {
//                            mIVUseST.setSelected(false);
                        	mIVUseST.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        mIVUseST.setVisibility(View.INVISIBLE);
//                        mIVUseST.setSelected(false);
                    }
                } else {
                    mTVLoc.setVisibility(View.INVISIBLE);
                    mIVUseST.setVisibility(View.INVISIBLE);
//                    mIVUseST.setSelected(false);
                    mBtnOpLoc.setEnabled(false);
                    mBtnOpStereo.setEnabled(false);
                }

            }
        }

    }

    private class FMRadioThread extends Thread {
        private Handler mHandler;
        private int mOpType;

        public FMRadioThread(Handler handler, int opType) {
            super();

            mHandler = handler;
            mOpType = opType;
        }

        @Override
        public void run() {
            super.run();

            Message msg = new Message();
            msg.what = mOpType;
            mHandler.sendMessage(msg);
        }

    }

    private BroadcastReceiver mMediaKeyListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int source = intent.getIntExtra("cbm_source", 0);
            printLog("FMRadioMainActivity - mMediaKeyListener - source: " + source);
            if (ACTION_AVIN_REQUEST_NOTIFY.equals(intent.getAction())) {
                deinitAvin();
                finish();
            }

            // if (CBManager.SRC_AVIN_A != source) {
            // return;
            // }

            String action = intent.getAction();

            printLog("FMRadioMainActivity - mMediaKeyListener - action: " + action);
            if (action.equals(MCU_ACTION_MEDIA_NEXT)) {
                doFreqAdd();

                // doPreRight();
            } else if (action.equals(MCU_ACTION_MEDIA_PREVIOUS)) {
                doFreqSub();

                // doPreLeft();
            } else if (action.equals(MCU_ACTION_MEDIA_PLAY_PAUSE)) {
                mBtnOpAS.performClick();
            } else if (action.equals(MCU_ACTION_RADIO_PRESET_PRE)) {
                doPresetPre();
            } else if (action.equals(MCU_ACTION_RADIO_PRESET_NEXT)) {
                doPresetNext();
            }
        }
    };

    private BroadcastReceiver mFiveHandTouchListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            final String action = arg1.getAction();
            if (action.equals(TouchCheckLineLayout.ACTION_FIVEHAND_TOUCH)) {
                Utils.onCalibartion(getApplicationContext());
            }
        }

    };

    private BroadcastReceiver mQuickBootListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (null == action) {
                return;
            }

            printLog("mQuickBootListener - action: " + action);

            if (ACTION_QB_POWERON.equals(action) || MCU_ACTION_ACC_ON.equals(action)) {
                new Thread() {

                    @Override
                    public void run() {
                        synchronized (lock) {
                            SystemClock.sleep(200);

                            printLog("mQuickBootListener - mAppIsFinished: " + mAppIsFinished);

                            if (mAppIsFinished) {
                                return;
                            }

                            deinitAvin();

                            initAvin();
                        }
                    }

                }.start();
            } else if (MCU_ACTION_ACC_OFF.equals(action)) {
                deinitAvin();
            } else if (ACTION_QB_POWEROFF.equals(action) || ACTION_QB_PREPOWEROFF.equals(action)) {
                
            	/*FIXME:yfzhang 去除假关机时关闭app
            	 * finish();*/
            }
        }
    };

    private Object sourceTocken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.i("onCreate");
        isDisplaySelectedFreq = true;
        sourceTocken = SourceManager.registerSourceListener(this, SourceManager.SRC_NO.radio);
        SourceManager.setAudioFocusNotify(sourceTocken, new SourceManager.AudioFocusNotify() {

            @Override
            public void onAudioFocusChange(int action) {
                printLog("onAudioFocusChange - action: " + action);
                switch (action) {
                    case AudioManager.AUDIOFOCUS_LOSS:
                        finish();
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        pauseAvin();
                        break;

                    case AudioManager.AUDIOFOCUS_GAIN:
                        resumeAvin(true);
                        break;
                }
            }
        });

        printLog("onCreate - start");

        sendBroadcast(new Intent(ACTION_AVIN_REQUEST_NOTIFY));

        SystemClock.sleep(100);

        initData();

        initUI();

        printLog("onCreate - end");
        //mMcuManager.Init_RPC_Volume();
        Handler openVolumeHandler = new Handler();
        openVolumeHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				/*FIXME:一汽要求只有VOL和静音钮可解除静音
	        	openVolume();
				*/
			}
		}, 800); 
    }

    @Override
    protected void onResume() {
        super.onResume();
		
        sendBroadcast(new Intent(MediaConstants.CURRENT_MEDIA_IS_RADIO), null);
        
        {
        	Intent intent = new Intent(Context.ACTION_SOURCE_STACK_NOMAL);
        	intent.putExtra(MediaConstants.EXTRA_APK_PACKAGENAME, "com.yecon.fmradio");
        	intent.putExtra(MediaConstants.EXTRA_APK_ACTIVITY, "com.yecon.fmradio.FMRadioMainActivity");
        	sendOrderedBroadcast(intent,null);
        }
        
        if(mCurrentFreqValue == 0){
        	sendBroadcast(new Intent(MediaConstants.ACTION_INIT_MCU_SOURCE, null));
        }
        SystemProperties.set(PROPERTY_KEY_STARTFM, "true");

        /*try {
            mMcuManager.RPC_SetSource(MCU_SOURCE_RADIO, 0x00);
        } catch (RemoteException e) {
        	L.e(e.toString());
        }*/
        initAvin();
        mMcuManager.Init_RPC_Volume();
        SourceManager.acquireSource(sourceTocken);
		if(progressBar != null && progress == progressBar.getProgress())
			mDialog.dismiss();
		if(mAtTimerHelpr != null)
			mAtTimerHelpr.start(1);
	}

    @Override
    protected void onPause() {
        super.onPause();

        SystemProperties.set(PROPERTY_KEY_STARTFM, "false");

        printLog("onPause - start");

        // reset();
		
		if(progressBar!=null)
			progress = progressBar.getProgress();
		if(mAtTimerHelpr!=null)
			mAtTimerHelpr.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();

        printLog("onStop - start");

        // reset();

    }

    @Override
    protected void onDestroy() {

        printLog("onDestroy - start");

        mAppIsFinished = true;

        SourceManager.unregisterSourceListener(sourceTocken);

        // AudioManager.setAudHWVolMax(0x20000);

        deinitAvin();

        reset();

        unregisterReceiver(mMediaKeyListener);

        unregisterReceiver(mQuickBootListener);

        unregisterReceiver(mFiveHandTouchListener);

        unregisterReceiver(controlReceiver);

       /* try {
            mMcuManager.RPC_SetSource(MCU_SOURCE_OFF, 0x00);
        } catch (RemoteException e) {
            L.e(e.toString());
        }*/
        super.onDestroy();
    }

    private void reset() {
        try {
            mMcuManager.RPC_RemoveMcuInfoChangedListener(mMcuListener);
        } catch (RemoteException e) {
        	L.e(e.toString());
        }

        if (mPSHandler.hasCallbacks(mPSRunnable)) {
            mPSHandler.removeCallbacks(mPSRunnable);
        }

        if (mASHandler.hasCallbacks(mASRunnable)) {
            mASHandler.removeCallbacks(mASRunnable);
        }
    }

    private void setVolumeMute(boolean mute) {
        if (mMcuManager != null) {
           /* try {
                boolean isMuted = (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0);
                L.e("isMuted: " + isMuted + "   mute: " + mute );
                if (!isMuted) {
                    mMcuManager.RPC_SetVolumeMute(isMuted);
                }
            } catch (RemoteException e) {
                L.e(e.toString());
            }*/
        }
    }

    private void initData() {
    	mPref = this.getSharedPreferences("radio_settings", Context.MODE_PRIVATE);
		mEditor = mPref.edit();
        mMcuManager = (McuManager) getSystemService(Context.MCU_SERVICE);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mObjSaveData = new SaveData();
        mHandler = new FMRadioHandler();

        IntentFilter mediaKeyFilter = new IntentFilter();
        mediaKeyFilter.addAction(ACTION_AVIN_REQUEST_NOTIFY);
        mediaKeyFilter.addAction(MCU_ACTION_MEDIA_NEXT);
        mediaKeyFilter.addAction(MCU_ACTION_MEDIA_PREVIOUS);
        mediaKeyFilter.addAction(MCU_ACTION_MEDIA_PLAY_PAUSE);
        mediaKeyFilter.addAction(MCU_ACTION_RADIO_PRESET_PRE);
        mediaKeyFilter.addAction(MCU_ACTION_RADIO_PRESET_NEXT);
        registerReceiver(mMediaKeyListener, mediaKeyFilter);

        IntentFilter qbFilter = new IntentFilter();
        qbFilter.addAction(ACTION_QB_POWERON);
        qbFilter.addAction(ACTION_QB_POWEROFF);
        qbFilter.addAction(ACTION_QB_PREPOWEROFF);
        qbFilter.addAction(MCU_ACTION_ACC_ON);
        qbFilter.addAction(MCU_ACTION_ACC_OFF);
        registerReceiver(mQuickBootListener, qbFilter);

        IntentFilter qbfiveHandFilter = new IntentFilter();
        qbfiveHandFilter.addAction(TouchCheckLineLayout.ACTION_FIVEHAND_TOUCH);
        registerReceiver(mFiveHandTouchListener, qbfiveHandFilter);
        
        
        IntentFilter controlFilter = new IntentFilter();
        controlFilter.addAction(MediaConstants.DO_PREV);
        controlFilter.addAction(MediaConstants.DO_NEXT);
        controlFilter.addAction(MediaConstants.CURRENT_MEDIA);
        controlFilter.addAction(MediaConstants.DO_EXIT_APP);
		registerReceiver(controlReceiver, controlFilter);
		
		freqAdapter = new FreqAdapter(freqList,this);
		
        try {
            if (mMcuManager != null) {
                printLog("initData - RPC_RequestMcuInfoChangedListener");
                mMcuManager.RPC_RequestMcuInfoChangedListener(mMcuListener);

                printLog("initData - RPC_SetSource");
                sendBroadcast(new Intent(MediaConstants.CURRENT_MEDIA_IS_RADIO), null);
                mOpType = OP_TYPE_INIT;

                printLog("initData - AVIN - start");

                initAvin();

                printLog("initData - AVIN - end");
            } else {
                printLog("initData - mMcuManager is null");
            }
        } catch (RemoteException e) {
        	L.e(e.toString());
        }
    }

    private void initAvin() {
        if (mAvinA == null) {
            setVolumeMute(true);

            mAvinA = new AVIN();

            SourceManager.acquireSource(sourceTocken);

            mAvinA.setOnCbmCmdListener(mListenerCbmCmd);

            mAvinA.setDestination(InputSource.DEST_TYPE_FRONT);

            int retValueAudio = mAvinA.setSource(InputSource.SOURCE_TYPE_AVIN,
                    AVIN.PORT_NONE, AVIN.PORT3, AVIN.PRIORITY_IN_CBM_LEVEL_DEFAULT);
            printLog("initAvin - initData = " + retValueAudio);

            // AudioManager.setAudHWVolMax(0xA0000);

            // SystemClock.sleep(300);

            mAvinA.play();

            mHandler.removeMessages(MSG_SET_VOLUME);
            Message msg = new Message();
            msg.what = MSG_SET_VOLUME;
            mHandler.sendMessageDelayed(msg, SET_VOLUME_DELAY);
        } else {
            resumeAvin(false);
        }
    }

    private void deinitAvin() {
        if (mAvinA != null) {
            mAvinA.stop();
            mAvinA.release();
            mAvinA = null;
        }
    }

    private void resumeAvin(boolean needMute) {
        if (mAvinA != null) {
        	MuteTextView mBtnState = (MuteTextView) findViewById(R.id.btn_mute_state);
        	L.e("mBtnState.isActivated(): " + mBtnState.isActivated() + "   needMute: " + needMute );
        	if(mBtnState.isActivated()){
        		needMute = true;
        	}
            if (needMute) {
            	L.e("   needMute: " + needMute );
                setVolumeMute(true);
            }

            mAvinA.play();

            if (needMute) {
                mHandler.removeMessages(MSG_SET_VOLUME);
                Message msg = new Message();
                msg.what = MSG_SET_VOLUME;
                mHandler.sendMessageDelayed(msg, SET_VOLUME_DELAY);
            }
        }
    }

    private void pauseAvin() {
        if (mAvinA != null) {
            mAvinA.stop();
        }
    }
	public class MyPagerAdapter extends PagerAdapter {
	    private ArrayList<View> viewLists;

	    public MyPagerAdapter() {
	    }

	    public MyPagerAdapter(ArrayList<View> viewLists) {
	        super();
	        this.viewLists = viewLists;
	    }

	    @Override
	    public int getCount() {
	        return viewLists.size();
	    }

	    @Override
	    public boolean isViewFromObject(View view, Object object) {
	        return view == object;
	    }

	    @Override
	    public Object instantiateItem(ViewGroup container, int position) {
	        container.addView(viewLists.get(position));
	        return viewLists.get(position);
	    }

	    @Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
	        container.removeView(viewLists.get(position));
	    }
	}
private int limitTemp = 0; /** 临界中间值 */
private int picnum;
    private void initUI() {
        setContentView(R.layout.fmradio_main_activity);
		
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		
        aList = new ArrayList<View>();
        LayoutInflater li = getLayoutInflater();
        aList.add(li.inflate(R.layout.viewpager_one,null,false));
        aList.add(li.inflate(R.layout.viewpager_two,null,false));
        aList.add(li.inflate(R.layout.viewpager_three, null, false));
        mAdapter = new MyPagerAdapter(aList);
        mViewPager.setAdapter(mAdapter);
		picnum = aList.size();
		final LinearLayout pointContainer = (LinearLayout) findViewById(R.id.pointContainer);
         for(int i=0;i<picnum;i++){
             ImageView imageView = new ImageView(this);//(ImageView) LayoutInflater.from(this).inflate(R.layout.viewpager_point,pointContainer,false);
             imageView.setImageResource(R.drawable.white_point);
			 pointContainer.addView(imageView);
         }
        ((ImageView) pointContainer.getChildAt(0)).setImageResource(R.drawable.color_point);
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
             @Override
             public void onPageScrolled(int i, float v, int i1) {
 
             }
 
             @Override
             public void onPageSelected(int i) { /** 先于 instantiateItem 执行 */
                 Log.d("onPageSelected","onPageSelected ->"+i);
                 /** 为了减少 CPU 和 内存的 绘图消耗，这里不采用 for 等循环的方式改 点背景，改用条件语句 */
                 if(false){
                     Log.d("onPageSelected","i is ->"+i +" limitTemp is "+limitTemp);
                     /** 循环情况临界点的颜色恢复 */
                     if((i % picnum)==(picnum-1) && limitTemp == 0){  /** 左滑 */
                         ((ImageView) pointContainer.getChildAt(0)).setImageResource(R.drawable.white_point);
                     }else {
                         if (i >= picnum && i % picnum == 0) { /** 右滑 */
                             ((ImageView) pointContainer.getChildAt(picnum - 1)).setImageResource(R.drawable.white_point);
                         }
                     }
                 }
                 i = i % picnum;
                 ((ImageView) pointContainer.getChildAt(i)).setImageResource(R.drawable.color_point);
                 if (i != 0 && i != picnum - 1) { /** 非临界值，两边都要修改 */
                     ((ImageView) pointContainer.getChildAt(i>limitTemp ? i-1:i+1)).setImageResource(R.drawable.white_point);
                 }else {
                     ((ImageView) pointContainer.getChildAt(i==picnum-1 ? i-1:i+1)).setImageResource(R.drawable.white_point);
                 }
                 limitTemp = i;
             }
 
             @Override
             public void onPageScrollStateChanged(int i) {
 
             }
         });
        
        initFreq();

        initPreviewFreq();

        initOp();
        
        
        mFmrMaxUnit = (TextView) findViewById(R.id.fmr_maximum_unit);
        mFmrMinUnit = (TextView) findViewById(R.id.fmr_minimum_unit);
        mBtnFavorite = (TextView) findViewById(R.id.btn_freq_favorite);
        mBtnDelete = (TextView)findViewById(R.id.btn_freq_del);
        mBtnList = (TextView) findViewById(R.id.btn_op_list);
        mBtnAuto = (TextView) findViewById(R.id.btn_op_auto);
        mBtnManual = (TextView)findViewById(R.id.btn_op_manual);
        mBtnSettings = (TextView) findViewById(R.id.btn_op_settings);
        mBtnMute = findViewById(R.id.btn_mute);
        mBtnMute.setOnClickListener(this);
        findViewById(R.id.btn_freq_favorite).setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mBtnList.setOnClickListener(this);
        mBtnAuto.setOnClickListener(this);
        mBtnManual.setOnClickListener(this);
        mBtnSettings.setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    private void initFreq() {
    	mSBTagsWrap = (ViewGroup) findViewById(R.id.sb_fmr_tags_wrap);
        mTVFreqTitle = new TextView[FREQ_TITLE_NUM];
        for (int i = 0; i < FREQ_TITLE_NUM; i++) {
            mTVFreqTitle[i] = (TextView) findViewById(FREQ_TITLE_ID[i]);
        }

        mSBFreq = (SeekBar) findViewById(R.id.sb_fmr_freq);
        mSBFreq.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int freq = 0;
                if (isFMBand()) {
                    freq = seekBar.getProgress() * 10 + mCurrBandMinFreq;
                } else {
                    int progress = seekBar.getProgress();
                    if (progress == 0) {
                        freq = mCurrBandMinFreq;
                    } else if (progress > 0 && progress < mCurrBandGranularity) {
                        if (progress < 5) {
                            freq = mCurrBandMinFreq;
                        } else {
                            freq = mCurrBandMinFreq + mCurrBandGranularity;
                        }
                    } else if (progress >= mCurrBandGranularity) {
                        int temp = progress % mCurrBandGranularity;
                        if (temp == 0) {
                            freq = progress + mCurrBandMinFreq;
                        } else if (temp < 5) {
                            freq = progress - temp + mCurrBandMinFreq;
                        } else {
                            freq = progress - temp + mCurrBandGranularity + mCurrBandMinFreq;
                        }
                    }
                }

                // mCurrBandGranularity
                printLog("onStopTrackingTouch: " + freq);

                byte[] param = new byte[4];
                param[0] = (byte) (freq >> 24 & 0xFF);
                param[1] = (byte) (freq >> 16 & 0xFF);
                param[2] = (byte) (freq >> 8 & 0xFF);
                param[3] = (byte) (freq & 0xFF);
                printLog(param, 4);
                try {
                    if (isFMBand()) {
                        mMcuManager.RPC_KeyCommand(T_RADIO_FM_FREQ, param);
                    } else {
                        mMcuManager.RPC_KeyCommand(T_RADIO_AM_FREQ, param);
                    }

                    mOpType = OP_TYPE_SEEKBAR;
                } catch (RemoteException e) {
                	L.e(e.toString());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int freq = 0;
                if (isFMBand()) {
                    freq = seekBar.getProgress() * 10 + mCurrBandMinFreq;
                } else {
                    freq = seekBar.getProgress() + mCurrBandMinFreq;
                }

                printLog("onStartTrackingTouch: " + freq);

                seekBar.playSoundEffect(android.view.SoundEffectConstants.CLICK);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int freq = 0;
                if (isFMBand()) {
                    freq = seekBar.getProgress() * 10 + mCurrBandMinFreq;
                } else {
                    if (progress == 0) {
                        freq = mCurrBandMinFreq;
                    } else if (progress > 0 && progress < mCurrBandGranularity) {
                        if (progress < 5) {
                            freq = mCurrBandMinFreq;
                        } else {
                            freq = mCurrBandMinFreq + mCurrBandGranularity;
                        }
                    } else if (progress >= mCurrBandGranularity) {
                        int temp = progress % mCurrBandGranularity;
                        if (temp == 0) {
                            freq = progress + mCurrBandMinFreq;
                        } else if (temp < 5) {
                            freq = progress - temp + mCurrBandMinFreq;
                        } else {
                            freq = progress - temp + mCurrBandGranularity + mCurrBandMinFreq;
                        }
                    }
                }

                printLog("onProgressChanged - freq:" + freq);

//                setFreqNumImageView(freq);
                setFreqNumTextView(freq);
            }
        });
        // mFreqNumImageView = (FreqNumImageView) findViewById(R.id.view_freq_num);
        mFreqNumTextView = (FreqNumTextView) findViewById(R.id.view_freq_num);

        mIVSub = (ImageView) findViewById(R.id.iv_freq_sub);
        mIVSub.setOnClickListener(this);
        //mIVSub.setOnLongClickListener(this);
        mIVAdd = (ImageView) findViewById(R.id.iv_freq_add);
        mIVAdd.setOnClickListener(this);
        //mIVAdd.setOnLongClickListener(this);

        mTVFreqUnit = (TextView) findViewById(R.id.tv_freq_unit);

        mTVFreqBand = (TextView) findViewById(R.id.tv_fmr_band);

        mTVPS = (TextView) findViewById(R.id.tv_ps);

        mTVLoc = (TextView) findViewById(R.id.tv_loc);
        mIVUseST = (TextView) findViewById(R.id.iv_use_st);
    }

    private void initPreviewFreq() {
        mTVPreviewFreq1 = (FreqItemView) aList.get(0).findViewById(R.id.tv_freq1);
        mTVPreviewFreq1.setOnClickListener(this);
        mTVPreviewFreq1.setOnLongClickListener(this);

        mTVPreviewFreq2 = (FreqItemView) aList.get(0).findViewById(R.id.tv_freq2);
        mTVPreviewFreq2.setOnClickListener(this);
        mTVPreviewFreq2.setOnLongClickListener(this);

        mTVPreviewFreq3 = (FreqItemView) aList.get(0).findViewById(R.id.tv_freq3);
        mTVPreviewFreq3.setOnClickListener(this);
        mTVPreviewFreq3.setOnLongClickListener(this);
        mTVPreviewFreq4 = (FreqItemView) aList.get(0).findViewById(R.id.tv_freq4);
        mTVPreviewFreq4.setOnClickListener(this);
        mTVPreviewFreq4.setOnLongClickListener(this);

        mTVPreviewFreq5 = (FreqItemView) aList.get(1).findViewById(R.id.tv_freq5);
        mTVPreviewFreq5.setOnClickListener(this);
        mTVPreviewFreq5.setOnLongClickListener(this);

        mTVPreviewFreq6 = (FreqItemView) aList.get(1).findViewById(R.id.tv_freq6);
        mTVPreviewFreq6.setOnClickListener(this);
        mTVPreviewFreq6.setOnLongClickListener(this);

        mTVPreviewFreq7 = (FreqItemView) aList.get(1).findViewById(R.id.tv_freq7);
        mTVPreviewFreq7.setOnClickListener(this);
        mTVPreviewFreq7.setOnLongClickListener(this);

        mTVPreviewFreq8 = (FreqItemView) aList.get(1).findViewById(R.id.tv_freq8);
        mTVPreviewFreq8.setOnClickListener(this);
        mTVPreviewFreq8.setOnLongClickListener(this);

        mTVPreviewFreq9 = (FreqItemView) aList.get(2).findViewById(R.id.tv_freq9);
        mTVPreviewFreq9.setOnClickListener(this);
        mTVPreviewFreq9.setOnLongClickListener(this);

        mTVPreviewFreq10 = (FreqItemView) aList.get(2).findViewById(R.id.tv_freq10);
        mTVPreviewFreq10.setOnClickListener(this);
        mTVPreviewFreq10.setOnLongClickListener(this);

        mTVPreviewFreq11 = (FreqItemView) aList.get(2).findViewById(R.id.tv_freq11);
        mTVPreviewFreq11.setOnClickListener(this);
        mTVPreviewFreq11.setOnLongClickListener(this);

        mTVPreviewFreq12 = (FreqItemView) aList.get(2).findViewById(R.id.tv_freq12);
        mTVPreviewFreq12.setOnClickListener(this);
        mTVPreviewFreq12.setOnLongClickListener(this);

        mPresetListMap.put(R.id.tv_freq1, 1);
        mPresetListMap.put(R.id.tv_freq2, 2);
        mPresetListMap.put(R.id.tv_freq3, 3);
        mPresetListMap.put(R.id.tv_freq4, 4);
        mPresetListMap.put(R.id.tv_freq5, 5);
        mPresetListMap.put(R.id.tv_freq6, 6);
        mPresetListMap.put(R.id.tv_freq7, 7);
        mPresetListMap.put(R.id.tv_freq8, 8);
        mPresetListMap.put(R.id.tv_freq9, 9);
        mPresetListMap.put(R.id.tv_freq10, 10);
        mPresetListMap.put(R.id.tv_freq11, 11);
        mPresetListMap.put(R.id.tv_freq12, 12);

        mTVPreviewFreqArray = new FreqItemView[PREVIEW_FREQ_NUM];
        mTVPreviewFreqArray[0] = mTVPreviewFreq1;
        mTVPreviewFreqArray[1] = mTVPreviewFreq2;
        mTVPreviewFreqArray[2] = mTVPreviewFreq3;
        mTVPreviewFreqArray[3] = mTVPreviewFreq4;
        mTVPreviewFreqArray[4] = mTVPreviewFreq5;
        mTVPreviewFreqArray[5] = mTVPreviewFreq6;
        mTVPreviewFreqArray[6] = mTVPreviewFreq7;
        mTVPreviewFreqArray[7] = mTVPreviewFreq8;
        mTVPreviewFreqArray[8] = mTVPreviewFreq9;
        mTVPreviewFreqArray[9] = mTVPreviewFreq10;
        mTVPreviewFreqArray[10] = mTVPreviewFreq11;
        mTVPreviewFreqArray[11] = mTVPreviewFreq12;
        
        for(int i = 0; i < mTVPreviewFreqArray.length; i++){
        	mTVPreviewFreqArray[i].setTag(i);
        }
    }

    private void initOp() {
        mBtnOpBand = (TextView) findViewById(R.id.btn_op_band);
        mBtnOpBand.setOnClickListener(this);

        mBtnOpLoc = (TextView) findViewById(R.id.btn_op_loc);
        mBtnOpLoc.setOnClickListener(this);

        mBtnOpStereo = (TextView) findViewById(R.id.btn_op_stereo);
        mBtnOpStereo.setOnClickListener(this);

        mBtnOpAS = (TextView) findViewById(R.id.btn_op_as);
        mBtnOpAS.setOnClickListener(this);

        mBtnOpPS = (TextView) findViewById(R.id.btn_op_ps);
        mBtnOpPS.setOnClickListener(this);
    }
    private void setFreqItemSelected(View selectFreqView){
    	if(selectFreqView == null){	
    		for(View freqView : mTVPreviewFreqArray){
    			freqView.setSelected(false);
    		}
    		mBtnDelete.setVisibility(View.GONE);
    		//mBtnFavorite.setVisibility(View.VISIBLE);
    	}else{
    		for(View freqView : mTVPreviewFreqArray){
    			if(freqView.getId() == selectFreqView.getId()){
    				freqView.setSelected(true);
    				mCurrentSelectFreq = (FreqItemView) freqView;
    				
    				if(mCurrentSelectFreq.valueIsEmpty()){
    					mBtnDelete.setVisibility(View.GONE);
    		    		//mBtnFavorite.setVisibility(View.VISIBLE);
    				}else{
    					//mBtnDelete.setVisibility(View.VISIBLE);
    		    		mBtnFavorite.setVisibility(View.GONE);
    				}
    				
    			}else{
    				freqView.setSelected(false);
    			}
    		}
    	}
    }
    private boolean isFMBand() {
        if (mCurrBand >= 0 && mCurrBand <= 2) {
            return true;
        } else {
            return false;
        }
    }

    private void setFreqTitle() {
        int gap = (mCurrBandMaxFreq - mCurrBandMinFreq) / FREQ_TITLE_NUM;
        printLog("setFreqTitle - mCurrBandMaxFreq: " + mCurrBandMaxFreq
                + " - mCurrBandMinFreq: " + mCurrBandMinFreq + " - mCurrBandGranularity: "
                + mCurrBandGranularity + " - gap: " + gap);

        int data[] = new int[FREQ_TITLE_NUM];
        if (isFMBand()) {
            if (mCurrBandGranularity != 0 && gap / mCurrBandGranularity != 0) {
                int temp = gap % mCurrBandGranularity;
                gap -= temp;
            }
            data[0] = (mCurrBandMinFreq + mCurrBandMinFreq + gap) / 2 + mCurrBandGranularity;
            data[3] = (mCurrBandMinFreq + mCurrBandMaxFreq) / 2 + mCurrBandGranularity;
            data[6] = (mCurrBandMaxFreq + mCurrBandMaxFreq - gap) / 2 + mCurrBandGranularity;

            data[1] = data[0] + gap;
            data[2] = data[1] + gap;
            data[4] = data[3] + gap;
            data[5] = data[6] - gap;

            for (int i = 0; i < FREQ_TITLE_NUM; i++) {
                String title = String.format("%.02f", data[i] / 100.0f);
                mTVFreqTitle[i].setText(title);
            }
        } else {
            if (mCurrBandGranularity != 0 && gap / mCurrBandGranularity != 0) {
                int temp = gap % mCurrBandGranularity;
                gap -= temp;
            }
            // data[0] = (mCurrBandMinFreq + mCurrBandMinFreq + gap) / 2;
            data[3] = (mCurrBandMinFreq + mCurrBandMaxFreq) / 2;
            // data[6] = (mCurrBandMaxFreq + mCurrBandMaxFreq - gap) / 2;
            int m = data[3] - mCurrBandMinFreq;
            int n = m % mCurrBandGranularity;
            if (n != 0) {
                data[3] -= n;
            }

            data[2] = data[3] - gap - mCurrBandGranularity;
            data[1] = data[2] - gap;
            data[0] = data[1] - gap - mCurrBandGranularity;

            data[4] = data[3] + gap + mCurrBandGranularity;
            data[5] = data[4] + gap;
            data[6] = data[5] + gap + mCurrBandGranularity;

            for (int i = 0; i < FREQ_TITLE_NUM; i++) {
                String title = String.format("%d", data[i]);
                mTVFreqTitle[i].setText(title);
            }
        }
    }

    private void setFreqNumImageView(int freq) {
        if (freq < mCurrBandMinFreq || freq > mCurrBandMaxFreq) {
            return;
        }

        if (mFreqNumImageView == null) {
            return;
        }

        printLog("setFreqNumImageView - freq: " + freq);

        if (isFMBand()) {
            int resId1 = freq / 10000;
            if (resId1 == 0) {
                mFreqNumImageView.setImageResource1(resId1, false);
            } else {
                mFreqNumImageView.setImageResource1(resId1, true);
            }

            int resId2 = (freq % 10000) / 1000;
            mFreqNumImageView.setImageResource2Visible(true);
            mFreqNumImageView.setImageResource2(resId2);

            int resId3 = (freq % 1000) / 100;
            mFreqNumImageView.setImageResource3(resId3);

            mFreqNumImageView.setImageResource4Visible(true);

            int resId5 = (freq % 100) / 10;
            mFreqNumImageView.setImageResource5(resId5);

            int resId6 = freq % 10;
            mFreqNumImageView.setImageResource6(resId6);
        } else {
            mFreqNumImageView.setImageResource1Visible(false);

            int resId2 = freq / 1000;
            if (resId2 == 0) {
                mFreqNumImageView.setImageResource2Visible(false);
            } else {
                mFreqNumImageView.setImageResource2Visible(true);

                mFreqNumImageView.setImageResource2(resId2);
            }

            int resId3 = (freq % 1000) / 100;
            mFreqNumImageView.setImageResource3(resId3);

            mFreqNumImageView.setImageResource4Visible(false);

            int resId5 = (freq % 100) / 10;
            mFreqNumImageView.setImageResource5(resId5);

            int resId6 = freq % 10;
            mFreqNumImageView.setImageResource6(resId6);
        }
    }

    private void setFreqNumTextView(int freq) {
		Log.d("TEST","setFreqNumTextView "+freq);
        if (freq < mCurrBandMinFreq || freq > mCurrBandMaxFreq) {
            return;
        }

        if (mFreqNumTextView == null) {
            return;
        }

        printLog("setFreqNumView - freq: " + freq);
        mCurrentFreqValue = freq;
        if (isFMBand()) {
            int resId1 = freq / 10000;
            if (resId1 == 0) {
                mFreqNumTextView.setImageResource1(resId1, false);
            } else {
                mFreqNumTextView.setImageResource1(resId1, true);
            }

            int resId2 = (freq % 10000) / 1000;
            mFreqNumTextView.setImageResource2Visible(true);
            mFreqNumTextView.setImageResource2(resId2);

            int resId3 = (freq % 1000) / 100;
            mFreqNumTextView.setImageResource3(resId3);

            mFreqNumTextView.setImageResource4Visible(true);

            int resId5 = (freq % 100) / 10;
            mFreqNumTextView.setImageResource5(resId5);

            int resId6 = freq % 10;
            mFreqNumTextView.setImageResource6(resId6);
            mFreqNumTextView.setImageResource6Visible(false);
        } else {
            mFreqNumTextView.setImageResource1Visible(false);

            int resId2 = freq / 1000;
            if (resId2 == 0) {
                mFreqNumTextView.setImageResource2Visible(false);
            } else {
                mFreqNumTextView.setImageResource2Visible(true);

                mFreqNumTextView.setImageResource2(resId2);
            }

            int resId3 = (freq % 1000) / 100;
            mFreqNumTextView.setImageResource3(resId3);

            mFreqNumTextView.setImageResource4Visible(false);

            int resId5 = (freq % 100) / 10;
            mFreqNumTextView.setImageResource5(resId5);

            int resId6 = freq % 10;
            mFreqNumTextView.setImageResource6(resId6);
            mFreqNumTextView.setImageResource6Visible(true);
        }
    }

    private void setFreqUnit() {
        if (isFMBand()) {
            mTVFreqUnit.setText(R.string.fmr_unit_mhz);
        } else {
            mTVFreqUnit.setText(R.string.fmr_unit_khz);
        }
    }

    private void setPresetList() {
    	//mBtnFavorite.setVisibility(View.VISIBLE);
		mBtnDelete.setVisibility(View.GONE);
        /*if (isFMBand()) {
            String freq1 = String.format("%.01f", (float) mPresetList[0] / 100);          
            String freq2 = String.format("%.01f", (float) mPresetList[1] / 100);           
            String freq3 = String.format("%.01f", (float) mPresetList[2] / 100);         
            String freq4 = String.format("%.01f", (float) mPresetList[3] / 100);           
            String freq5 = String.format("%.01f", (float) mPresetList[4] / 100);
            String freq6 = String.format("%.01f", (float) mPresetList[5] / 100);
            
            mTVPreviewFreq1.setText(1,freq1,getResources().getString(R.string.fmr_unit_mhz));
            mTVPreviewFreq2.setText(2,freq2,getResources().getString(R.string.fmr_unit_mhz));
            mTVPreviewFreq3.setText(3,freq3,getResources().getString(R.string.fmr_unit_mhz));
            mTVPreviewFreq4.setText(4,freq4,getResources().getString(R.string.fmr_unit_mhz));
            mTVPreviewFreq5.setText(5,freq5,getResources().getString(R.string.fmr_unit_mhz));
            mTVPreviewFreq6.setText(6,freq6,getResources().getString(R.string.fmr_unit_mhz));
        } else {
          mTVPreviewFreq1.setText(1,mPresetList[0] + "",getResources().getString(R.string.fmr_unit_khz));
          mTVPreviewFreq2.setText(2,mPresetList[1] + "",getResources().getString(R.string.fmr_unit_khz));
          mTVPreviewFreq3.setText(3,mPresetList[2] + "",getResources().getString(R.string.fmr_unit_khz));
          mTVPreviewFreq4.setText(4,mPresetList[3] + "",getResources().getString(R.string.fmr_unit_khz));
          mTVPreviewFreq5.setText(5,mPresetList[4] + "",getResources().getString(R.string.fmr_unit_khz));
          mTVPreviewFreq6.setText(6,mPresetList[5] + "",getResources().getString(R.string.fmr_unit_khz));
        }*/
        boolean isfmband = isFMBand();
		for(int i = 0; i < mPresetList.length; i++){
    		float value = mPresetList[i];
    		if(isfmband){
    			String valueStr = String.format("%.01f",(float) value / 100);
    			try {
	    			if(isScanning && value > 0){
	    				String oldValue = mTVPreviewFreqArray[i].getValue().getText().toString();
	    				if(TextUtils.isEmpty(oldValue) || !valueStr.equals(oldValue)){
	    					try {
								if(mPresetList[5] == 0){
									mTVPreviewFreqArray[i].blink();
								}else{
									mTVPreviewFreqArray[5].blink();
								}
							} catch (Exception e) {
								L.e(e.toString());
							}
	    				}
	    			}
	    		} catch (Exception e) {
					L.e(e.toString());
				}
    			mTVPreviewFreqArray[i].setText(i + 1, valueStr, getString(R.string.fmr_unit_mhz));
    		}else{
    			try {	
	    			if(isScanning && value > 0){
	    				String valueStr = String.format("%.00f",value);
	    				String oldValue = mTVPreviewFreqArray[i].getValue().getText().toString();
	    				if(TextUtils.isEmpty(oldValue) || !valueStr.equals(oldValue)){
//	    					mTVPreviewFreqArray[i].blink();
	    					try {
								if(mPresetList[5] == 0){
									mTVPreviewFreqArray[i].blink();
								}else{
									mTVPreviewFreqArray[5].blink();
								}
							} catch (Exception e) {
								L.e(e.toString());
							}
	    				}
	    			}
    			} catch (Exception e) {
    				L.e(e.toString());
    			}
    			mTVPreviewFreqArray[i].setText(i + 1,mPresetList[i] + "",getResources().getString(R.string.fmr_unit_khz));
    		}
    		if(mCurrentSelectFreq != null && mCurrentSelectFreq.isSelected()){
    			if(mCurrentSelectFreq.getId() == mTVPreviewFreqArray[i].getId()){
    				if(value == 0){
    					//mBtnFavorite.setVisibility(View.VISIBLE);
    		    		mBtnDelete.setVisibility(View.GONE);
    				}else{
    					mBtnFavorite.setVisibility(View.GONE);
    		    		//mBtnDelete.setVisibility(View.VISIBLE);
    				}
    			}
    		}
		}
		
		if(isFMBand()){
			addFmrTags(mPresetList, 8750, 10800);
		}else{
			addFmrTags(mPresetList, 531, 1629);
		}
		
        Log.e(this.getClass().getSimpleName(),"[setPresetList]");
        for (int i = 0; i < PREVIEW_FREQ_NUM; i++) {
            if (i + 1 == mCurrPreset) {
//                mTVPreviewFreqArray[i].setSelected(true);
            	Log.e(this.getClass().getSimpleName(),"setSelected [setPresetList] mCurrPreset:" + i);
            } else {
//                mTVPreviewFreqArray[i].setSelected(false);
            }
        }
    }

    @SuppressWarnings("unused")
    private void progressDynamicChange(final int loadFreq) {
        new Thread() {

            @Override
            public void run() {
                super.run();

                SystemClock.sleep(50);

                synchronized (lock) {
                    mStartPresetLoading = false;
                }
                int currFreq = 0;
                if (isFMBand()) {
                    currFreq = mSBFreq.getProgress() * 10 + mCurrBandMinFreq;
                } else {
                    currFreq = mSBFreq.getProgress() + mCurrBandMinFreq;
                }

                if (loadFreq > currFreq) {
                    int freq = 0;
                    for (freq = currFreq; freq <= loadFreq; freq += mCurrBandGranularity) {
                        synchronized (lock) {
                            if (mStartPresetLoading) {
                                return;
                            }
                        }

                        if (isFMBand()) {
                            mSBFreq.setProgress((freq - mCurrBandMinFreq) / 10);
                        } else {
                            mSBFreq.setProgress(freq - mCurrBandMinFreq);
                        }

                        SystemClock.sleep(10);
                    }

                    if (freq > loadFreq) {
                        if (isFMBand()) {
                            mSBFreq.setProgress((loadFreq - mCurrBandMinFreq) / 10);
                        } else {
                            mSBFreq.setProgress(loadFreq - mCurrBandMinFreq);
                        }
                    }
                } else {
                    int freq = 0;
                    for (freq = currFreq; freq >= loadFreq; freq -= mCurrBandGranularity) {
                        synchronized (lock) {
                            if (mStartPresetLoading) {
                                return;
                            }
                        }

                        if (isFMBand()) {
                            mSBFreq.setProgress((freq - mCurrBandMinFreq) / 10);
                        } else {
                            mSBFreq.setProgress(freq - mCurrBandMinFreq);
                        }

                        SystemClock.sleep(10);
                    }

                    if (freq < loadFreq) {
                        if (isFMBand()) {
                            mSBFreq.setProgress((loadFreq - mCurrBandMinFreq) / 10);
                        } else {
                            mSBFreq.setProgress(loadFreq - mCurrBandMinFreq);
                        }
                    }
                }
            }

        }.start();
    }

    private void onPresetListItemClick(int id, int clickType) {
        if (clickType == 0) {
            try {
                byte[] param = new byte[4];
                Integer index = mPresetListMap.get(id);
                if (index != null) {
                    param[0] = (byte) index.intValue();
                } else {
                    param[0] = 1;
                }
                mMcuManager.RPC_KeyCommand(T_RADIO_PRESET_LOAD, param);

                synchronized (lock) {
                    mStartPresetLoading = true;
                }

                Log.e(this.getClass().getSimpleName(),"[onPresetListItemClick]");
                for (int i = 0; i < PREVIEW_FREQ_NUM; i++) {
                    if (i + 1 == mCurrPreset) {
//                        mTVPreviewFreqArray[i].setSelected(true);
                    	Log.e(this.getClass().getSimpleName(),"setSelected true [onPresetListItemClick] mCurrPreset:" + i);
                    } else {
//                        mTVPreviewFreqArray[i].setSelected(false);
                    }
                }

                // progressDynamicChange(mPresetList[index.intValue() - 1]);

                mOpType = OP_TYPE_PS_LOAD;
            } catch (RemoteException e) {
                L.e(e.toString());
            }
        } else if (clickType == 1) {
            try {
                byte[] param = new byte[4];
                Integer index = mPresetListMap.get(id);
                if (index != null) {
                    param[0] = (byte) index.intValue();
                } else {
                    param[0] = 1;
                }

                mMcuManager.RPC_KeyCommand(T_RADIO_PRESET_SAVE, param);
            } catch (RemoteException e) {
            	 L.e(e.toString());
            }

            mOpType = OP_TYPE_PS_SAVE;
        }
    }
    
    private void deletePreset(int id){
    	try {
	    	byte[] param = new byte[4];
	        Integer index = mPresetListMap.get(id);
	    	if (index != null) {
	            param[0] = (byte) index.intValue();
	            mMcuManager.RPC_KeyCommand(T_RADIO_DEL_PRESET, param);
	        }
    	}catch (RemoteException e) {
            L.e(e.toString());
        }
    	mOpType = OP_TYPE_PS_DEL;
    }
    
    private void doFreqSub() {
        try {
            printLog("FMRadioMainActivity - doFreqSub");

            mMcuManager.RPC_KeyCommand(T_RADIO_TUNING_DOWN, null);

            mOpType = OP_TYPE_CLICK;
        } catch (RemoteException e) {
            L.e(e.toString());
        }
    }

    private void doFreqAdd() {
        try {
            printLog("FMRadioMainActivity - doFreqAdd");

            mMcuManager.RPC_KeyCommand(T_RADIO_TUNE_UP, null);

            mOpType = OP_TYPE_CLICK;
        } catch (RemoteException e) {
        	 L.e(e.toString());
        }
    }

    private void doPresetPre() {
        try {
            printLog("FMRadioMainActivity - doPreset pre");

            mMcuManager.RPC_KeyCommand(T_RADIO_PRESET_PRE, null);

            mOpType = OP_TYPE_CLICK;
        } catch (RemoteException e) {
        	L.e(e.toString());
        }
    }

    private void doPresetNext() {
        try {
            printLog("FMRadioMainActivity - doPreset next");

            mMcuManager.RPC_KeyCommand(T_RADIO_PRESET_NEXT, null);

            mOpType = OP_TYPE_CLICK;
        } catch (RemoteException e) {
        	L.e(e.toString());
        }
    }

    private void doFreqLongSub() {
        try {
            printLog("FMRadioMainActivity - doFreqLongSub");

            mASCount = 0;
            mASIsVisible = false;

            mOpType = OP_TYPE_INIT;

            mMcuManager.RPC_KeyCommand(T_RADIO_SEEK_DOWN, null);

            // mOpType = OP_TYPE_CLICK;

            // if (mASHandler.hasCallbacks(mASRunnable)) {
            // mASHandler.removeCallbacks(mASRunnable);
            // }
            // mASHandler.postDelayed(mASRunnable, AS_DISPLAY_DELAY);
        } catch (RemoteException e) {
        	L.e(e.toString());
        }
    }

    private void doFreqLongAdd() {
        try {
            printLog("FMRadioMainActivity - doFreqLongAdd");

            mASCount = 0;
            mASIsVisible = false;

            mOpType = OP_TYPE_INIT;

            mMcuManager.RPC_KeyCommand(T_RADIO_SEEK_UP, null);

            // mOpType = OP_TYPE_CLICK;

            // if (mASHandler.hasCallbacks(mASRunnable)) {
            // mASHandler.removeCallbacks(mASRunnable);
            // }
            // mASHandler.postDelayed(mASRunnable, AS_DISPLAY_DELAY);
        } catch (RemoteException e) {
        	L.e(e.toString());
        }
    }
    
    private void setFmrMaxMinUnit(String fmr){
    	if(!TextUtils.isEmpty(fmr)){
    		fmr = fmr.toLowerCase();
    		if(fmr.startsWith("am")){
    			mFmrMaxUnit.setText(R.string.fmr_maximum_unit_am_value);
    			mFmrMinUnit.setText(R.string.fmr_minimum_unit_am_value);
    			
    		}else if(fmr.startsWith("fm")){
    			mFmrMaxUnit.setText(R.string.fmr_maximum_unit_fm_value);
    			mFmrMinUnit.setText(R.string.fmr_minimum_unit_fm_value);
    		} 
    	}
    }
    boolean is = true;
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.iv_freq_sub: {
				if(isAutoSeek){
	            	/*FIXME:一汽要求只有VOL和静音钮可解除静音
		        	openVolume();
					*/
	            	setFreqItemSelected(null);
	            	doFreqSub();
				}else
					doFreqLongSub();
                break;
            }

            case R.id.iv_freq_add: {
				if(isAutoSeek){
	            	/*FIXME:一汽要求只有VOL和静音钮可解除静音
		        	openVolume();
					*/
	            	setFreqItemSelected(null);
	            	doFreqAdd();
				}else
					doFreqLongAdd();
                break;
            }

            case R.id.btn_op_band: {
            	/*FIXME:一汽要求只有VOL和静音钮可解除静音
	        	openVolume();
				*/
            	isDisplaySelectedFreq = true;
            	setFreqItemSelected(null);
                try {
                	Log.e(this.getClass().getSimpleName(),"T_RADIO_BAND :" + T_RADIO_BAND  + " OP_TYPE_INIT:" + OP_TYPE_INIT);
                    mMcuManager.RPC_KeyCommand(T_RADIO_BAND, null);
                    mOpType = OP_TYPE_INIT;
                } catch (RemoteException e) {
                	L.e(e.toString());
                }
                break;
            }

            case R.id.btn_op_loc: {
            	/*FIXME:一汽要求只有VOL和静音钮可解除静音
	        	openVolume();
				*/
                try {
                    mMcuManager.RPC_KeyCommand(T_RADIO_LOC, null);

                    mOpType = OP_TYPE_LOC;
                } catch (RemoteException e) {
                	L.e(e.toString());
                }
                break;
            }

            case R.id.btn_op_stereo: {
            	/*FIXME:一汽要求只有VOL和静音钮可解除静音
	        	openVolume();
				*/
                if (isFMBand()) {
                    try {
                        mMcuManager.RPC_KeyCommand(T_RADIO_STEREO_STATE_CHANGE, null);

                        mOpType = OP_TYPE_USE_ST;
                    } catch (RemoteException e) {
                    	L.e(e.toString());
                    }
                }
                break;
            }

			case R.id.list_as:
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					View progressView = View.inflate(this, R.layout.progress_layout, null);
					progressText = (TextView)progressView.findViewById(R.id.progress_text);
					progressBar = (ProgressBar)progressView.findViewById(R.id.progressbar);
					progressFreq = (TextView)progressView.findViewById(R.id.progress_freq);
					progressButton = (TextView)progressView.findViewById(R.id.progress_button);
					//builder.setView(settingsView);
					mDialog = builder.create();
					if (isFMBand()) {
	                    progressBar.setMax((mCurrBandMaxFreq - mCurrBandMinFreq) / 10);
	                } else {
	                    progressBar.setMax(mCurrBandMaxFreq - mCurrBandMinFreq);
	                }
					
					mDialog.show();
					Window win = mDialog.getWindow();
					win.getDecorView().setPadding(0, 0, 0, 0);
					WindowManager.LayoutParams lp = win.getAttributes();
        			lp.width = WindowManager.LayoutParams.FILL_PARENT;
        			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        			win.setAttributes(lp);
					//builder.setView(settingsView);
					mDialog.setContentView(progressView);
					progressView.findViewById(R.id.progress_button).setOnClickListener(new OnClickListener(){
						public void onClick(View v){
							mDialog.dismiss();
							LocalListPosition();
						}
					});
					mAtTimerHelpr = new AtTimerHelpr();
			        mAtTimerHelpr.setCallBack( new AtTimerHelpr.AtTimerHelprDoItCallBack() {
						@Override
						public void doIt() {
							if(mDialog!=null)
								mDialog.dismiss();
						}
					} );
					mAtTimerHelpr.start(1);
					freqAdapter.setSelectItem(-1);
				}
            case R.id.btn_op_as: {
            	/*FIXME:一汽要求只有VOL和静音钮可解除静音
	        	openVolume();
				*/
            	setFreqItemSelected(null);
                try {
                    mASIsVisible = false;
                    mASCount = 0;

                    byte[] param = new byte[4];
                    param[0] = 0;
                    mMcuManager.RPC_KeyCommand(T_RADIO_AS, param);

                    mOpType = OP_TYPE_AS;

                    if (mASHandler.hasCallbacks(mASRunnable)) {
                        mASHandler.removeCallbacks(mASRunnable);
                    }
                    // if (mPSHandler.hasCallbacks(mPSRunnable)) {
                    // mPSHandler.removeCallbacks(mPSRunnable);
                    // }
                    mASHandler.postDelayed(mASRunnable, AS_DISPLAY_DELAY);
                } catch (RemoteException e) {
                	L.e(e.toString());
                }
                break;
            }

            case R.id.btn_op_ps: {
            	/*FIXME:一汽要求只有VOL和静音钮可解除静音
	        	openVolume();
				*/
                try {
                    if (isFMBand()) {
                        /*mCurrPSItemCount = 18;*/
                    	mCurrPSItemCount = 6;
                    } else {
                        /*mCurrPSItemCount = 12;*/
                    	mCurrPSItemCount = 6;
                    }
                    printLog("mCurrPSItemCount = " + mCurrPSItemCount);

                    mCurrPSItem = 0;
                    mCurrPreset = 0;
                    mPSIsVisible = false;

                    // if (mASHandler.hasCallbacks(mASRunnable)) {
                    // mASHandler.removeCallbacks(mASRunnable);
                    // }
                    if (mPSHandler.hasCallbacks(mPSRunnable)) {
                        mPSHandler.removeCallbacks(mPSRunnable);
                    }
                    mPSHandler.postDelayed(mPSRunnable, PS_DISPLAY_DELAY);

                    mMcuManager.RPC_KeyCommand(T_RADIO_PS, null);
                } catch (RemoteException e) {
                	L.e(e.toString());
                }
                break;
            }
            case R.id.btn_mute:
				((MuteTextView)findViewById(R.id.btn_mute_state)).toggle();
				break;
			case R.id.btn_freq_favorite:{
				/*FIXME:一汽要求只有VOL和静音钮可解除静音
	        	openVolume();
				*/
				if(mCurrentSelectFreq != null && mCurrentSelectFreq.isSelected()){
					Log.e("FMRadioMainActivity", "favorite success");
					onPresetListItemClick(mCurrentSelectFreq.getId(), 1);
					
					if(mCurrentSelectFreq != null){
						String valueStr = "";
						if(isFMBand()){
							valueStr = String.format("%.01f",(float) mCurrentFreqValue / 100);
						}else{
							valueStr = mCurrentFreqValue + "";
						}
						saveSelectFreq(mCurrentSelectFreq.getId(), valueStr);
					}
					
				}else{
					Log.e("FMRadioMainActivity", "favorite fail");
				}
			}
			break;
			case R.id.btn_freq_del:{
				/*FIXME:一汽要求只有VOL和静音钮可解除静音
	        	openVolume();
				*/
				if(mCurrentSelectFreq != null && mCurrentSelectFreq.isSelected()){
					isDeleteSelectedFreq = true;
					saveSelectFreq(0,""); //set saved SelectFreq is empty
					Log.e("FMRadioMainActivity", "delete freq  success");
					deletePreset(mCurrentSelectFreq.getId());			
				}else{
					Log.e("FMRadioMainActivity", "delete freq fail");
				}
			}
			break;
            case R.id.tv_freq1:
            case R.id.tv_freq2:
            case R.id.tv_freq3:
            case R.id.tv_freq4:
            case R.id.tv_freq5:
            case R.id.tv_freq6:
            case R.id.tv_freq7:
            case R.id.tv_freq8:
            case R.id.tv_freq9:
            case R.id.tv_freq10:
            case R.id.tv_freq11:
            case R.id.tv_freq12: {
                onPresetListItemClick(id, 0);
                setFreqItemSelected(view);
                if(mCurrentSelectFreq != null && !mCurrentSelectFreq.valueIsEmpty()){
                	/*FIXME:一汽要求只有VOL和静音钮可解除静音
		        	openVolume();
					*/
		    		saveSelectFreq(mCurrentSelectFreq);
                }
                break;
            }
			case R.id.settings_kb_back:
			case R.id.settings_back:
			case R.id.list_back: 
	           new Thread() {     //不可在主线程中调用  
	                public void run() {  
	                    try {  
	                        Instrumentation inst = new Instrumentation();  
	                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);  
	                   } catch (Exception e) {  
	                        e.printStackTrace();  
	                    }  
	                 }  
         
	            }.start();
				break;
            case R.id.back:{
            	onBackPressed();
            }
            	break;
			case R.id.btn_op_auto:
			case R.id.btn_op_manual:
				/*isAutoSeek = !isAutoSeek;
				if(isAutoSeek){
					mBtnAuto.setVisibility(View.VISIBLE);
					mBtnManual.setVisibility(View.GONE);
				}else{
					mBtnAuto.setVisibility(View.GONE);
					mBtnManual.setVisibility(View.VISIBLE);
				}*/
		 		try{
                    mMcuManager.RPC_KeyCommand(T_RADIO_AUTO_SEEK, null);
                } catch (RemoteException e) {
                	L.e(e.toString());
                }
				break;
			case R.id.btn_op_list:
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					View listDialog = View.inflate(this, R.layout.list_layout, null);
					
        			listDialog.findViewById(R.id.list_back).setOnClickListener(this);
        			listDialog.findViewById(R.id.list_as).setOnClickListener(this);
					this.listView = ((ListView)listDialog.findViewById(R.id.listview));
					((ListView)listDialog.findViewById(R.id.listview)).setAdapter(freqAdapter);
					updateScrollBar();
					((ListView)listDialog.findViewById(R.id.listview)).setOnItemClickListener(new OnItemClickListener(){
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							setFreqNumTextView(freqList.get(position));
			                byte[] param = new byte[4];
			                param[0] = (byte) (freqList.get(position) >> 24 & 0xFF);
			                param[1] = (byte) (freqList.get(position) >> 16 & 0xFF);
			                param[2] = (byte) (freqList.get(position) >> 8 & 0xFF);
			                param[3] = (byte) (freqList.get(position) & 0xFF);
			                printLog(param, 4);
			                try {
			                    if (isFMBand()) {
			                        mMcuManager.RPC_KeyCommand(T_RADIO_FM_FREQ, param);
			                    } else {
			                        mMcuManager.RPC_KeyCommand(T_RADIO_AM_FREQ, param);
			                    }

			                } catch (RemoteException e) {
			                	L.e(e.toString());
			                }
							//listView.setItemChecked(position,true);
							freqAdapter.setSelectItem(position);
				           new Thread() {     //不可在主线程中调用  
				                public void run() {  
				                    try {  
				                        Instrumentation inst = new Instrumentation();  
				                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);  
				                   } catch (Exception e) {  
				                        e.printStackTrace();  
				                    }  
				                 }  
         
				            }.start();
						}
					});
					LocalListPosition();
					//builder.setView(listView);
					final AlertDialog dialog = builder.create();
					
					dialog.show();
					Window win = dialog.getWindow();
					win.getDecorView().setPadding(0, 0, 0, 0);
					WindowManager.LayoutParams lp = win.getAttributes();
        			lp.width = WindowManager.LayoutParams.FILL_PARENT;
        			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        			win.setAttributes(lp);
					//builder.setView(listView);
					dialog.setContentView(listDialog);
				}
				break;
			case R.id.btn_op_settings:
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					View settingsView = View.inflate(this, R.layout.settings_layout, null);
        			settingsView.findViewById(R.id.settings_back).setOnClickListener(this);
        			settingsView.findViewById(R.id.input_freq).setOnClickListener(this);
        			settingsView.findViewById(R.id.clean_freq).setOnClickListener(this);
					//builder.setView(settingsView);
					mDialog = builder.create();
					
					mDialog.show();
					Window win = mDialog.getWindow();
					win.getDecorView().setPadding(0, 0, 0, 0);
					WindowManager.LayoutParams lp = win.getAttributes();
        			lp.width = WindowManager.LayoutParams.FILL_PARENT;
        			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        			win.setAttributes(lp);
					//builder.setView(settingsView);
					mDialog.setContentView(settingsView);
				}
				break;
				
			case R.id.kb_dot:
				{
					String text = mInputFreq.getText().toString();
					if(!isFMBand()||text.contains(".")||text.length()<2||Float.parseFloat(text)<(int)(mCurrBandMinFreq/100))
						break;
				}
			case R.id.kb_1:
			case R.id.kb_2:
			case R.id.kb_3:
			case R.id.kb_4:
			case R.id.kb_6:
			case R.id.kb_7:
			case R.id.kb_8:
			case R.id.kb_9:
			case R.id.kb_0:
			case R.id.kb_5:
				{
					String text = mInputFreq.getText().toString();
					if(isFMBand()&&(text.contains(".")&&(text.length() - text.indexOf(".")) == 2))
						break;
				}
			//case R.id.kb_0:
			//case R.id.kb_5:
				{
					String text = mInputFreq.getText().toString()+((TextView)view).getText();
					float temp = Float.parseFloat(text);
					int freq = (int)temp;
					if(isFMBand())
						freq = (int)(temp * 100);
					else if(freq > mCurrBandMinFreq && (freq -mCurrBandMinFreq)%9!=0){
						break;
					}
					if(freq <= mCurrBandMaxFreq &&
					(!text.contains(".")||((text.length() - text.indexOf(".")) <= 3))
					&& (!text.contains(".") || text.endsWith(".") || (text.contains(".") && !text.endsWith(".") && freq >=mCurrBandMinFreq))
					&& (temp >= (int)(mCurrBandMinFreq / Math.pow(10, Math.round(Math.log10(mCurrBandMinFreq)) - Math.round(Math.log10(temp)))))
					&& (temp <= (int)(mCurrBandMaxFreq / Math.pow(10, Math.round(Math.log10(mCurrBandMinFreq)) - Math.round(Math.log10(temp)))))
					 )
						mInputFreq.setText(text);
				}
				break;
			case R.id.kb_bs:
				try{
					mInputFreq.setText(mInputFreq.getText().toString().substring(0,mInputFreq.getText().toString().length()-1));
				}catch(Exception e){}
				break;
			case R.id.kb_ok:
				{
	                try {
						float temp = Float.parseFloat(mInputFreq.getText().toString());
						if(isFMBand())
							temp *= 100;
						int freq = (int)temp;
						if(freq < mCurrBandMinFreq)
							break;
						setFreqNumTextView(freq);
		                byte[] param = new byte[4];
		                param[0] = (byte) (freq >> 24 & 0xFF);
		                param[1] = (byte) (freq >> 16 & 0xFF);
		                param[2] = (byte) (freq >> 8 & 0xFF);
		                param[3] = (byte) (freq & 0xFF);
		                printLog(param, 4);
	                    if (isFMBand()) {
	                        mMcuManager.RPC_KeyCommand(T_RADIO_FM_FREQ, param);
	                    } else {
	                        mMcuManager.RPC_KeyCommand(T_RADIO_AM_FREQ, param);
	                    }
			           new Thread() {     //不可在主线程中调用  
			                public void run() {  
			                    try {  
			                        Instrumentation inst = new Instrumentation();  
			                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);  
									mDialog.dismiss();
			                   } catch (Exception e) {  
			                        e.printStackTrace();  
			                    }  
			                 }  
         
			            }.start();

	                } catch (Exception e) {
	                	L.e(e.toString());
	                }
		 		}
				break;
				
				
			case R.id.input_freq:
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					View kbView = View.inflate(this, R.layout.settings_kb_layout, null);
					mInputFreq = ((TextView)kbView.findViewById(R.id.kb_text));
					kbView.findViewById(R.id.settings_kb_back).setOnClickListener(this);
					kbView.findViewById(R.id.kb_0).setOnClickListener(this);
					kbView.findViewById(R.id.kb_1).setOnClickListener(this);
					kbView.findViewById(R.id.kb_2).setOnClickListener(this);
					kbView.findViewById(R.id.kb_3).setOnClickListener(this);
					kbView.findViewById(R.id.kb_4).setOnClickListener(this);
					kbView.findViewById(R.id.kb_5).setOnClickListener(this);
					kbView.findViewById(R.id.kb_6).setOnClickListener(this);
					kbView.findViewById(R.id.kb_7).setOnClickListener(this);
					kbView.findViewById(R.id.kb_8).setOnClickListener(this);
					kbView.findViewById(R.id.kb_9).setOnClickListener(this);
					kbView.findViewById(R.id.kb_dot).setOnClickListener(this);
					kbView.findViewById(R.id.kb_ok).setOnClickListener(this);
					kbView.findViewById(R.id.kb_bs).setOnClickListener(this);
					((TextView)kbView.findViewById(R.id.kb_ok)).setText(R.string.other_ok);
					((TextView)kbView.findViewById(R.id.kb_0)).setText("0");
					((TextView)kbView.findViewById(R.id.kb_1)).setText("1");
					((TextView)kbView.findViewById(R.id.kb_2)).setText("2");
					((TextView)kbView.findViewById(R.id.kb_3)).setText("3");
					((TextView)kbView.findViewById(R.id.kb_4)).setText("4");
					((TextView)kbView.findViewById(R.id.kb_5)).setText("5");
					((TextView)kbView.findViewById(R.id.kb_6)).setText("6");
					((TextView)kbView.findViewById(R.id.kb_7)).setText("7");
					((TextView)kbView.findViewById(R.id.kb_8)).setText("8");
					((TextView)kbView.findViewById(R.id.kb_9)).setText("9");
					((TextView)kbView.findViewById(R.id.kb_dot)).setText(".");
					final AlertDialog dialog = builder.create();
					
					dialog.show();
					Window win = dialog.getWindow();
					win.getDecorView().setPadding(0, 0, 0, 0);
					WindowManager.LayoutParams lp = win.getAttributes();
        			lp.width = WindowManager.LayoutParams.FILL_PARENT;
        			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        			win.setAttributes(lp);
					//builder.setView(listView);
					dialog.setContentView(kbView);
				}
				break;
				
			case R.id.clean_freq:
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					View confirm = View.inflate(this, R.layout.prompt_dialog, null);
					((TextView)confirm.findViewById(R.id.textview_title)).setText(R.string.clean_freq_confirm);
					builder.setView(confirm);
					final AlertDialog dialog = builder.create();
					dialog.show();
					confirm.findViewById(R.id.textview_yes).setOnClickListener(new OnClickListener(){
						public void onClick(View v){
					 		try{
			                    mMcuManager.RPC_KeyCommand(T_RADIO_DEL_PRESET, null);
			                } catch (RemoteException e) {
			                	L.e(e.toString());
			                }
							dialog.dismiss();
							mDialog.dismiss();
						}
					});
					confirm.findViewById(R.id.textview_no).setOnClickListener(new OnClickListener(){
						public void onClick(View v){
							dialog.dismiss();
						}
					});
				}
				break;
        }
    }
    
    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_freq_sub) {
            doFreqLongSub();
        } else if (id == R.id.iv_freq_add) {
            doFreqLongAdd();
        } else {
        	if(isScanning || isSeeking){
        		return true;
        	}
			/*FIXME:一汽要求只有VOL和静音钮可解除静音
        	openVolume();
			*/
            onPresetListItemClick(id, 1);
            setFreqItemSelected(view);
            
            //
            if(mCurrentSelectFreq != null){
				String valueStr = "";
				if(isFMBand()){
					valueStr = String.format("%.01f",(float) mCurrentFreqValue / 100);
				}else{
					valueStr = mCurrentFreqValue + "";
				}
				saveSelectFreq(mCurrentSelectFreq.getId(), valueStr);
			}
            //
        }

        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        printLog("FMRadioMainActivity - onKeyUp - keyCode: " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_YECON_TUNER) {
            mBtnOpBand.performClick();
        } else if (keyCode == KeyEvent.KEYCODE_YECON_RADIO_FM) {
            //try {
                mOpType = OP_TYPE_INIT;

                //mMcuManager.RPC_KeyCommand(T_RADIO_FM, null);
            //} catch (RemoteException e) {
            //	L.e(e.toString());
            //}
        } else if (keyCode == KeyEvent.KEYCODE_YECON_RADIO_AM) {
            //try {
                mOpType = OP_TYPE_INIT;

                //mMcuManager.RPC_KeyCommand(T_RADIO_AM, null);
            //} catch (RemoteException e) {
            //	L.e(e.toString());
            //}
        } else if (keyCode == KeyEvent.KEYCODE_YECON_RADIO_AS) {
            mBtnOpAS.performClick();
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        printLog("FMRadioMainActivity - onBackPressed");

        // deinitAvin();

//        this.finish();
//        moveTaskToBack(true);

         onBackPressedToHome();
    }

    private void onBackPressedToHome() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
    }
    
    BroadcastReceiver controlReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String mediaType = intent.getStringExtra("media_type");
			Log.e(this.getClass().getName(), "action:" + action + "  mediaType:" + mediaType);
			if(MediaConstants.CURRENT_MEDIA_IS_RADIO.equals(mediaType)){
				try {	
					if		(MediaConstants.DO_PREV.equalsIgnoreCase(action)){
						doFreqSub();
					}else if(MediaConstants.DO_NEXT.equalsIgnoreCase(action)){
						doFreqAdd();
					}else if(MediaConstants.DO_EXIT_APP.equalsIgnoreCase(action)){
					    sendOrderedBroadcast(new Intent(MediaConstants.CURRENT_MEDIA_IS_NONE),null);
                        if(mAtTimerHelpr != null){
                            mAtTimerHelpr.stop();
                        }
                        deinitAvin();
                        finish();
                        try {
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }catch (Exception e){
                            L.e(e.toString());
                        }
                    }
				} catch (Exception e) {
					Log.e(this.getClass().getName(), e.toString());
				}
			}
		}
	};
	
	public void clearTags(){
		if(mSBTagsWrap != null){	
			mSBTagsWrap.removeAllViews();
		}
	}
	
	/*public void addFmrTags(List<Integer> list,int minValue,int maxValue){
		if(mSBTagsWrap != null){
			MarginLayoutParams mSBTagsWrapLp = (MarginLayoutParams) mSBTagsWrap.getLayoutParams();
			clearTags();
			for(int value : list){
				float pre = (value - minValue) / (maxValue - minValue);
				ImageView imageView = new ImageView(this);
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.fmr_add_n));
				imageView.setScaleType(ScaleType.CENTER_INSIDE);
				MarginLayoutParams layoutParams = (MarginLayoutParams) imageView.getLayoutParams();
				int marginLeft = (int) (mSBTagsWrapLp.width * pre);
				layoutParams.setMargins(marginLeft, 0, 0, 0);
				mSBTagsWrap.addView(imageView);
			}
		}
	}*/
	
	public void addFmrTags(int[] list,int minValue,int maxValue){
		if(mSBTagsWrap != null){
			clearTags();
//			int ableWidthSpace = mSBTagsWrap.getMeasuredWidth() - mSBTagsWrap.getPaddingLeft() - mSBTagsWrap.getPaddingRight();
			int ableWidthSpace = (int) (mSBTagsWrap.getMeasuredWidth() - 9);
//			ableWidthSpace = 591;
			for(int value : list){
				if (value == 0 ) {
					continue;
				}
				float pre = (float)(value - minValue) / (float)(maxValue - minValue);
				ImageView imageView = new ImageView(this);
				imageView.setBackground(getResources().getDrawable(R.drawable.fmr_thumb_n));
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				int marginLeft = (int) (ableWidthSpace * pre) + 3;
				lp.setMargins(marginLeft, 0, 0, 0);
				imageView.setLayoutParams(lp);
				mSBTagsWrap.addView(imageView);
				L.e(ableWidthSpace +" " + mSBTagsWrap.getMeasuredWidth() +" " + value + " " + pre +" "+ marginLeft + "  " + minValue + "  " + maxValue);
			}
		}
	}
	

	public void saveSelectFreq(FreqItemView freqItemView){
		if(freqItemView == null){
			return;
		}
		int index = freqItemView.getId();
		String value = freqItemView.getValue().getText().toString();
		if(TextUtils.isEmpty(value)){
			index = 0;
			value = "";
		}
		saveSelectFreq(index,value);
	}
	
	public void saveSelectFreq(int index,String value){
		if(isFMBand()){
			saveSelectFreq(BAND_TYPE_FM,index,value);
		}else{
			saveSelectFreq(BAND_TYPE_AM,index,value);
		}
		
	}
	public void saveSelectFreq(String bandType,int index,String value){
		if(TextUtils.isEmpty(bandType)){
			return;
		}
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("bandType", bandType);
			jsonObject.put("index", index);
			jsonObject.put("value", value);
			mEditor.putString(bandType, jsonObject.toString());
			mEditor.commit();
			L.e("save select : " + bandType + " index:" + index + "  value:" + value);
		} 
		catch (Exception e) {
			L.e(e.toString());
		}
	}
	public void readSelectFreqAndDisPlay(){
		try {
			String mCurrentFreqValueStr = "";
			if(isFMBand()){
				mCurrentFreqValueStr = String.format("%.01f",(float) mCurrentFreqValue / 100);
			}else{
				mCurrentFreqValueStr = mCurrentFreqValue + "";
			}
			if(isDisplaySelectedFreq){
		     	isDisplaySelectedFreq = false;
				String json = isFMBand() ? mPref.getString(BAND_TYPE_FM, "") : mPref.getString(BAND_TYPE_AM, "");
				
				if(TextUtils.isEmpty(json)){}
				else{
					JSONObject jsonObject = new JSONObject(json);
					String jBandType = jsonObject.getString("bandType");
					int jIndex = jsonObject.getInt("index");
					String jValue = jsonObject.getString("value");
					if(jIndex == 0 && TextUtils.isEmpty(jValue)){
					}else{
						for(FreqItemView view : mTVPreviewFreqArray){
							if(view.getId() == jIndex && view.getValue().getText().equals(jValue) && mCurrentFreqValueStr.equals(jValue)){
								mCurrentSelectFreq = view;
								setFreqItemSelected(mCurrentSelectFreq);
							}
						}
					}
				}
			}
			 
			if(isDeleteSelectedFreq){
				for(FreqItemView freqItemView : mTVPreviewFreqArray){
					if(!TextUtils.isEmpty(mCurrentFreqValueStr) 
							&& mCurrentFreqValueStr.equals(freqItemView.getValue().getText().toString())){
						saveSelectFreq(freqItemView);
						setFreqItemSelected(freqItemView);
						isDeleteSelectedFreq = false;
						break;
					}
				}
			}
		} catch (Exception e) {
			L.e(e.toString());
		}
		isDeleteSelectedFreq = false;
		
	}
	
	public void openVolume(){
		if(findViewById(R.id.btn_mute_state) != null){
			((MuteTextView)findViewById(R.id.btn_mute_state)).setMute(false,400);
		}
	}
}
