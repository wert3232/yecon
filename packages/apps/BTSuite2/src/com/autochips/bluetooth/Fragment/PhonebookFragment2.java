/**
 * 
 */
/**
 * @author Administrator
 *
 */
package com.autochips.bluetooth.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.android.internal.app.LocalePicker;
import com.autochips.bluetooth.BTService;
import com.autochips.bluetooth.Bluetooth;
import com.autochips.bluetooth.BluetoothReceiver;
import com.autochips.bluetooth.FormatTelNumber;
import com.autochips.bluetooth.LocalBluetoothProfileManager;
import com.autochips.bluetooth.MainBluetoothActivity;
import com.autochips.bluetooth.R;
import com.autochips.bluetooth.TuoXianDialActivity;
import com.autochips.bluetooth.LocalBluetoothProfileManager.BTProfile;
import com.autochips.bluetooth.MainBluetoothActivity.MyTouchListener;
import com.autochips.bluetooth.PbSyncManager.PBRecord;
import com.autochips.bluetooth.PbSyncManager.PBSyncManagerService;
import com.autochips.bluetooth.PbSyncManager.PBSyncStruct;
import com.autochips.bluetooth.util.CharacterParser;
import com.autochips.bluetooth.util.L;
import com.autochips.bluetooth.util.SystemContactsManager;
import com.autochips.bluetooth.view.ConfirmDialog;
import com.autochips.bluetooth.view.MarqueeText;
import com.tuoxianui.tools.AtTimerHelpr;

import android.app.Dialog;
import android.constant.YeconConstants;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemProperties;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


public class PhonebookFragment2 extends BaseFragment implements OnClickListener, OnItemClickListener,OnEditorActionListener{
	private LinearLayout mDownloadLayout;
    private TextView mDownloadTextView;
    private ListView mPhonebookListView;
    private TextView mTipEmptyTextView;
	private TextView mBtnSyn;
	private TextView mBtnDelete;
	private TextView mBtnDeleteAll;
    private EditText mSearchText;
    private LoadTask loadTask;
    private ConfirmDialog callDilog;
    private PinyinComparator pinyinComparator = new PinyinComparator();
    public static ArrayList<PhoneBookItem> allPhonebooks = new ArrayList<PhonebookFragment2.PhoneBookItem>();
    private PhoneBookAdapter adapter;
    private SearchTask searchTask;
    private AtTimerHelpr mAtTimerHelpr;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.bt_phonebook, container, false);
		initView(mRootView);
		initReceiver();
		tryshowphonebooklist();
		
		/**FIXME:11103 一汽要求,不自动流转 mAtTimerHelpr = new AtTimerHelpr();
        mAtTimerHelpr.setCallBack( new AtTimerHelpr.AtTimerHelprDoItCallBack() {
			@Override
			public void doIt() {
				boolean isLoading  = false;
				if(mDownloadLayout != null){
					isLoading = mDownloadLayout.getVisibility() == View.VISIBLE ? true : false;
				}
				//L.e("isLoading:" + isLoading + " "  + mDownloadLayout.getVisibility() + "  " + mDownloadTextView.getVisibility());  
				if(Bluetooth.getInstance().iscallidle() && !isLoading  && !MainBluetoothActivity.isPowerOff){
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
		} );**/
		initInputMode();
		
		return mRootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//L.e("mTipEmptyTextView: " + mTipEmptyTextView.getVisibility() + " size : " + allPhonebooks.size());
		if(allPhonebooks != null && !allPhonebooks.isEmpty()){
			mTipEmptyTextView.setVisibility(View.GONE);
			mBtnDelete.setEnabled(true);
			mBtnDeleteAll.setEnabled(true);
		}else{
			Log.d("TEST","onResume show mTipEmptyTextView");
			mTipEmptyTextView.setVisibility(View.VISIBLE);
			mBtnDelete.setEnabled(false);
			mBtnDeleteAll.setEnabled(false);
		}
		if(!Bluetooth.getInstance().isdownloadidle())
			mBtnSyn.setEnabled(true);
		try {
			//FIXME: 一汽要求,不自动流转 mAtTimerHelpr.start(10);
			((MainBluetoothActivity) getActivity()).registerMyTouchListener(touchListener);
		} catch (Exception e) {
			L.e(e.toString());
		}
		initInputMode();
	}
	
	@Override
	public void onPause() {
		try {
			//FIXME: 一汽要求,不自动流转 mAtTimerHelpr.stop();
			((MainBluetoothActivity) getActivity()).unRegisterMyTouchListener(touchListener);
		} catch (Exception e) {
			L.e(e.toString());
		}
		hideKeyboard(mSearchText.getWindowToken());
		super.onPause();
	}
	
	private MyTouchListener touchListener = new MyTouchListener() {
		
		@Override
		public void onTouchEvent(MotionEvent event) {
			//FIXME: 一汽要求,不自动流转 mAtTimerHelpr.reset();
		}
	};
	
	@Override
	public void onDestroy() {
		BluetoothReceiver.unregisterNotifyHandler(uiHandler);
		BTService.unregisterNotifyHandler(uiHandler);
		super.onDestroy();
	}
	private void initView(View rootView){
		adapter = new PhoneBookAdapter(this.getActivity());
		
		mDownloadLayout = (LinearLayout) rootView.findViewById(R.id.download_phonebook_layout);
        mDownloadTextView = (TextView) rootView.findViewById(R.id.tv_download_phonebook_text);
        mPhonebookListView = (ListView) rootView.findViewById(R.id.phonebook_listview);
        mTipEmptyTextView = (TextView)rootView.findViewById(R.id.phonebookisempty);
		mSearchText = (EditText) rootView.findViewById(R.id.search_edittext);
		mBtnDelete = (TextView) rootView.findViewById(R.id.delete);
		mBtnDeleteAll = (TextView) rootView.findViewById(R.id.delete_all);
		
		
		mBtnDelete.setOnClickListener(this);
		mBtnDeleteAll.setOnClickListener(this);
		mBtnSyn = (TextView)rootView.findViewById(R.id.search_syn);
		mBtnSyn.setOnClickListener(this);
		rootView.findViewById(R.id.btn_phone_book_back).setOnClickListener(this);
		rootView.findViewById(R.id.search_confim).setOnClickListener(this);
		
		mSearchText.setOnEditorActionListener(this);	
		mSearchText.addTextChangedListener(mTextWatcher);
		mSearchText.setOnTouchListener(new View.OnTouchListener() {	
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				//点击后可以编辑
				mSearchText.setFocusableInTouchMode(true);
				//FIXME: 一汽要求,不自动流转 mAtTimerHelpr.stop();
				return false;
			}
		});
		mPhonebookListView.setOnItemClickListener(this);
		mPhonebookListView.setAdapter(adapter);
	}
	private void initReceiver(){
		BluetoothReceiver.registerNotifyHandler(uiHandler);
		BTService.registerNotifyHandler(uiHandler);
	}
	
	@Override
	public boolean onEditorAction(TextView tx, int actionId, KeyEvent event) {
		if(tx.getId() == R.id.search_edittext){
			setFocusableInTouchMode(false,mSearchText);
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(adapter.getPhoneBookList() == null || adapter.getCount() == 0 || adapter.getCount() <= position){
			return;
		}
		adapter.setSelect(position);
		mBtnDelete.setEnabled(true);
		final String pbNumber = adapter.getPhoneBookList().get(position).pbNumber;
		callDilog = new ConfirmDialog(getActivity());
		callDilog.setContent(getResources().getString(R.string.confirm_call_phone));
    	ConfirmDialog.Callback callback = new ConfirmDialog.Callback() {
			@Override
			public void onCallOK(Dialog dialog, TextView ok, TextView no) {
		        if (pbNumber != null) {
		        	String pbNumberTemp = pbNumber.replaceAll("-", "");
		        	pbNumberTemp = pbNumberTemp.replaceAll("\\+", "");
		        	pbNumberTemp = pbNumberTemp.replaceAll(" ", "");
					Bluetooth.getInstance().call(pbNumberTemp);
				}
			}
			
			@Override
			public void onCallCanncel(Dialog dialog, TextView ok, TextView no) {
				dialog.dismiss();
				//FIXME: 一汽要求,不自动流转 mAtTimerHelpr.start(10);
			}
		};
		callDilog.setCallBack(callback);
		callDilog.show();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.search_confim:{
				setFocusableInTouchMode(false,mSearchText);
				break;
			}
			case R.id.btn_phone_book_back:{
				L.i(this.getClass().getSimpleName() + " startDialActivity true");
				//FIXME:12004
//			startActivity(new Intent(this.getActivity(),TuoXianDialActivity.class));
				Bluetooth.handStartActivity(this.getActivity(),TuoXianDialActivity.class);
				break;
			}
			case R.id.search_syn:{
				if (Bluetooth.getInstance().isdownloadidle()) {
					mBtnSyn.setEnabled(false);
					Log.d("TEST","search_syn");
	                allPhonebooks.clear();
					mBtnDelete.setEnabled(false);
					mBtnDeleteAll.setEnabled(false);
	                adapter.clear();
	            	Bluetooth.getInstance().downloadphonebook();
	                updatedownloadui(true, 0);
	                adapter.notifyDataSetInvalidated();
				}
				break;
			}
			case R.id.delete:{
				showDeleteOneDialog();
				break;
			}
			case R.id.delete_all:{
				showDeleteAllDialog();
				break;
			}
		}
			
	}
	
	private void initInputMode(){
		String language = getActivity().getResources().getConfiguration().locale.getLanguage();
		InputMethodManager mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		List<InputMethodInfo> inputMethods = mInputMethodManager.getInputMethodList();
		for(int i = 0;i < inputMethods.size(); i++){
			InputMethodInfo info = inputMethods.get(i);
			L.i(info.getPackageName() + "  " + info.getId()); 
			if("zh".equalsIgnoreCase(language) && "com.android.inputmethod.pinyin.PinyinIME".equalsIgnoreCase(info.getServiceName())){
				L.i("setInputMethod : com.android.inputmethod.pinyin.PinyinIME");
				mInputMethodManager.setInputMethod(null, info.getId());
				break;
			}else if(//i == inputMethods.size() - 1 && 
			"com.android.inputmethod.latin.LatinIME".equalsIgnoreCase(info.getServiceName())){
				L.i("setInputMethod : com.android.inputmethod.latin.LatinIME");
				mInputMethodManager.setInputMethod(null, info.getId());
			}
		}
	}
	
	private void showDeleteOneDialog(){
		if(adapter == null || adapter.getCount() == 0){
			return;
		}
		ConfirmDialog deleteAllDialog;
		deleteAllDialog = new ConfirmDialog(getActivity());
		deleteAllDialog.setContent(getResources().getString(R.string.confirm_del_one_contact));
    	ConfirmDialog.Callback callback = new ConfirmDialog.Callback() {
			@Override
			public void onCallOK(Dialog dialog, TextView ok, TextView no) {
				adapter.removeSelectItem();
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
		if(adapter == null || adapter.getCount() == 0){
			return;
		}
		ConfirmDialog deleteAllDialog;
		deleteAllDialog = new ConfirmDialog(getActivity());
		deleteAllDialog.setContent(getResources().getString(R.string.confirm_del_all_contact));
    	ConfirmDialog.Callback callback = new ConfirmDialog.Callback() {
			@Override
			public void onCallOK(Dialog dialog, TextView ok, TextView no) {
				adapter.removeAll();
			}
			
			@Override
			public void onCallCanncel(Dialog dialog, TextView ok, TextView no) {
				dialog.dismiss();
			}
		};
		deleteAllDialog.setCallBack(callback);
		deleteAllDialog.show();
	}
	
	private void setFocusableInTouchMode(boolean touchMode,TextView editText){
		//String IMEName="com.android.inputmethod.latin/.LatinIME";
		//android.provider.Settings.Secure.putString( getActivity().getContentResolver(), android.provider.Settings.Secure.DEFAULT_INPUT_METHOD, IMEName );
		if(touchMode){
			//FIXME: 一汽要求,不自动流转 mAtTimerHelpr.stop();
		}
		else{
			//FIXME: 一汽要求,不自动流转 mAtTimerHelpr.start(10);
		}
		initInputMode();
		mSearchText.clearFocus();
		mSearchText.setFocusableInTouchMode(touchMode);
		hideKeyboard(editText.getWindowToken());
	}
	
	private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
	
	
	private void updatedownloadui(boolean bshow, int num){
    	if (bshow) {
    		mDownloadTextView.getText();
	        mDownloadTextView.setText(getString(R.string.str_download_phonebook, num));
	        mDownloadTextView.setVisibility(View.VISIBLE);
	        mDownloadLayout.setVisibility(View.VISIBLE);
	        mTipEmptyTextView.setVisibility(View.GONE);
			//mBtnDelete.setEnabled(true);
			mBtnDeleteAll.setEnabled(true);
		}else{
            mDownloadLayout.setVisibility(View.GONE);
	        mDownloadTextView.setVisibility(View.GONE);
            if (allPhonebooks.size() == 0) {
			Log.d("TEST","updatedownloadui show mTipEmptyTextView");
            	mTipEmptyTextView.setVisibility(View.VISIBLE);
				mBtnDelete.setEnabled(false);
				mBtnDeleteAll.setEnabled(false);
			}else{
				mTipEmptyTextView.setVisibility(View.GONE);
				//mBtnDelete.setEnabled(true);
				mBtnDeleteAll.setEnabled(true);
            }
		}
    }
	
	private void tryshowphonebooklist(){
		Log.d("TEST","tryshowphonebooklist "+Bluetooth.getInstance().GetPhonebookRecCnt() + Bluetooth.getInstance().isdownloadidle() + allPhonebooks.isEmpty());
    	if (Bluetooth.getInstance().GetPhonebookRecCnt() != 0 
    			&& Bluetooth.getInstance().isdownloadidle()
    			//&& allPhonebooks.isEmpty()
				) {
			loadAllData();
		}else{
			mBtnDelete.setEnabled(false);
			mBtnDeleteAll.setEnabled(false);
		}
    }
	
	private void loadAllData(){
    	loadTask  = new LoadTask();
        loadTask.execute(0);
		//mBtnDelete.setEnabled(true);
		mBtnDeleteAll.setEnabled(true);
    }
	
	private void notifySearchKey(String searchkey){
		if(searchTask != null){
			searchTask.cancel(true);
			searchTask = null;
		}
		
		if(searchkey != null && !TextUtils.isEmpty(searchkey)){
			searchTask = new SearchTask();
			searchTask.execute(new SearchParams(searchkey, allPhonebooks));
		}else{
			mBtnDelete.setEnabled(false);
			adapter.clear();
			if(allPhonebooks.size() != 0){
				adapter.getPhoneBookList().addAll(allPhonebooks);
			}
			adapter.notifyDataSetInvalidated();
		}
	}
	
	TextWatcher mTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int count, int after) {
			//FIXME: 一汽要求,不自动流转 mAtTimerHelpr.reset();
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable editable) {
			String searchKey = editable.toString();
			if(TextUtils.isEmpty(searchKey)){
				searchKey = "";
			}
			searchKey = searchKey.trim();
			notifySearchKey(searchKey);
		}
	};
	
	private Handler uiHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == BluetoothReceiver.MSG_BT_STATUS_NOTIFY) {
				Intent intent = (Intent) msg.obj;
				String recievedAction = intent.getAction();
				if (recievedAction.equals(PBSyncManagerService.ACTION_STARTDOWNLOAD_POSITION)) {
	                int iDownloadPath = intent.getIntExtra(PBSyncManagerService.EXTRA_PBSYNC_FOLDER,
	                        PBSyncStruct.NUM_OF_BT_PBAP_SYNC_PATH);
	                if(iDownloadPath != PBSyncStruct.BT_PBAP_SYNC_PATH_ALL_PHONEBOOK) {
	                    return;
	                }
	                int nDownloadedNum = intent.getIntExtra(PBSyncManagerService.EXTRA_PBSYNC_START_POSITION, 0);
	            }else if (recievedAction.equals(PBSyncManagerService.ACTION_DOWNLOAD_ONESTEP)) {
	                int iDownloadPath = intent.getIntExtra(PBSyncManagerService.EXTRA_PBSYNC_FOLDER,
	                        PBSyncStruct.NUM_OF_BT_PBAP_SYNC_PATH);
	                if(iDownloadPath != PBSyncStruct.BT_PBAP_SYNC_PATH_ALL_PHONEBOOK) {
	                    return;
	                }
	                updatedownloadui(true, Bluetooth.getInstance().GetPhonebookRecCnt());
	            }else if(recievedAction.equals(PBSyncManagerService.ACTION_DOWNLOAD_FINISH)) {
					mBtnSyn.setEnabled(true);
                    int iDownloadPath = intent.getIntExtra(PBSyncManagerService.EXTRA_PBSYNC_FOLDER,
                            PBSyncStruct.NUM_OF_BT_PBAP_SYNC_PATH);
                    if(iDownloadPath != PBSyncStruct.BT_PBAP_SYNC_PATH_ALL_PHONEBOOK) {
						Log.d("TEST","PBSyncManagerService.ACTION_DOWNLOAD_FINISH iDownloadPath "+ iDownloadPath);
                        return;
                    }
                    boolean bSupportFlag = intent.getBooleanExtra(PBSyncManagerService.EXTRA_PBSYNC_SUPPORT_FOLDER, false);
					Log.d("TEST","PBSyncManagerService.ACTION_DOWNLOAD_FINISH bSupportFlag "+ bSupportFlag);
                    if(false == bSupportFlag) {
                        allPhonebooks.clear();
						mBtnDelete.setEnabled(false);
						mBtnDeleteAll.setEnabled(false);
                        adapter.clear();
                        adapter.notifyDataSetInvalidated();
//                        HashMap<String, String> map = new HashMap<String, String>();
//                        map.put("item_phonebook_name", getString(R.string.bt_notsupport_download_phonebook));
//                        map.put("item_phonebook_number", "");
//                        mPBList.add(map);
//                        notifylistviewdataupdate();
                    }else{
                    	loadAllData();
                    }
	            }else if(recievedAction.equals(LocalBluetoothProfileManager.ACTION_PROFILE_STATE_UPDATE)) {
	                BTProfile profilename = (BTProfile)intent.getSerializableExtra(
	                		LocalBluetoothProfileManager.EXTRA_PROFILE);
	                int profilestate = intent.getIntExtra(
	                		LocalBluetoothProfileManager.EXTRA_NEW_STATE,LocalBluetoothProfileManager.STATE_DISCONNECTED);
	                if(profilename == null) {
	                     return;
	                }
	                if (profilename.equals(LocalBluetoothProfileManager.BTProfile.Bluetooth_HF)) {
	                    switch (profilestate) {
	                    case LocalBluetoothProfileManager.STATE_CONNECTED:
	                    	tryshowphonebooklist();
	                        break;
	                    case LocalBluetoothProfileManager.STATE_DISCONNECTED:
	                    case LocalBluetoothProfileManager.STATE_DISABLED:
	                    	allPhonebooks.clear();
							mBtnDelete.setEnabled(false);
							mBtnDeleteAll.setEnabled(false);
	                    	adapter.clear();
	                    	adapter.notifyDataSetInvalidated();
	    	                Log.d("TEST","profilestate "+profilestate);
							updatedownloadui(false, 0);
	    	               /* notifyInputChange("");
	    	                notifylistviewdataupdate();*/
	                        break;
	                    }
	                }
	            }
			}
		}
	};
	
	
	class LoadTask extends AsyncTask<Integer, Integer, ArrayList<PhoneBookItem>>{  
		@Override
		protected ArrayList<PhoneBookItem> doInBackground(Integer... arg0) {
		 	ArrayList<PhoneBookItem> result = new ArrayList<PhoneBookItem>();
	        List<PBRecord> record = new ArrayList<PBRecord>();
	        boolean bRes = Bluetooth.getInstance().GetPhonebook(record);
	        if (bRes) {
	            for (int i = 0; i < record.size() && !isCancelled(); i++) {
	                PhoneBookItem bookItem = new PhoneBookItem();
	                int id = i;
	                String pbName = record.get(i).getName();
	                String pbNum = record.get(i).getNumber();
	                if (!MainBluetoothActivity.mdbmanager.isCallnum(pbNum)) {
						continue;
					}
	                pbNum  = FormatTelNumber.ui_format_tel_number(pbNum);
	                StringBuilder []pinyin = CharacterParser.getSelling(pbName);
	                String py = pinyin[0].toString();
	                String pyhead = pinyin[1].toString();
	                
	                bookItem.id = id;
	                bookItem.pbName = pbName;
	                bookItem.pbNumber = pbNum;
	                bookItem.py = py;
	                bookItem.pyhead = pyhead;
	               // L.e("id:" + id + " pbName:" + pbName + " pbNumber:" + py);
	                result.add(bookItem);
	            }
	            if( !isCancelled()){
	            	Collections.sort(result, pinyinComparator);
	            }
	        }
	        return result;
		}
		
		@Override
		protected void onPreExecute() {
			mDownloadLayout.setVisibility(View.VISIBLE);
			mDownloadTextView.setVisibility(View.GONE);
			Log.d("TEST","onPreExecute show mTipEmptyTextView");
        	mTipEmptyTextView.setVisibility(View.INVISIBLE);
			super.onPreExecute();
		}

		@Override
		protected void onCancelled(ArrayList<PhoneBookItem> result) {
			super.onCancelled(result);
		}

		@Override
		protected void onPostExecute(ArrayList<PhoneBookItem> result) {
			if(result != null){
				allPhonebooks.clear();
				mBtnDelete.setEnabled(false);
				mBtnDeleteAll.setEnabled(false);
				adapter.clear();
				allPhonebooks.addAll(result);
				adapter.getPhoneBookList().addAll(allPhonebooks);
				adapter.notifyDataSetInvalidated();
//                notifylistviewdataupdate();
                if(result.size() > 0){
                	mSearchText.setText("");
                	mPhonebookListView.setVisibility(View.VISIBLE);
                }else{
                	mPhonebookListView.setVisibility(View.INVISIBLE);
                }
			}else
				Log.d("TEST","onPostExecute null");
			Log.d("TEST","onPostExecute updatedownloadui");
		    updatedownloadui(false, 0);
			super.onPostExecute(result);
		}
		
    };
	
    public class PinyinComparator implements Comparator<PhoneBookItem> {  
  	  
        public int compare(PhoneBookItem o1, PhoneBookItem o2) {  
            if (TextUtils.isEmpty(o2.py)) {  
                return -1;  
            } else if (TextUtils.isEmpty(o1.py)) {  
                return 1;  
            } else {  
                return o1.py.compareTo(o2.py);  
            }  
        }  
    } 
    private void closeKeyboard() {
        View view = getActivity().getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private static class SearchParams {
		String input;
		ArrayList<PhoneBookItem> list;
		SearchParams(String input, ArrayList<PhoneBookItem>list) {
			this.input = input;
			if(list!=null){
				this.list = new ArrayList<PhoneBookItem>();
				this.list.addAll(list);
			}
		}
	}
    
    private class SearchTask extends AsyncTask<SearchParams, Integer, ArrayList<PhoneBookItem>>{  
		@Override
		protected ArrayList<PhoneBookItem> doInBackground(SearchParams... arg0) {
		 	String input = arg0[0].input.toLowerCase();
		 	ArrayList<PhoneBookItem>list = arg0[0].list;
		 	if(list != null && input != null && input.length() > 0){
			 	ArrayList<PhoneBookItem> result = new ArrayList<PhoneBookItem>();
			 	int n=0; String py,pyhead,pbName,pbNumber;
			 	while(!this.isCancelled() && n < list.size()){
			 		py = list.get(n).py;
			 		pyhead  = list.get(n).pyhead;
			 		pbName  = list.get(n).pbName;
			 		pbNumber  = list.get(n).pbNumber;
			 		if((py!=null&&py.contains(input))
						||(pyhead!=null&&pyhead.contains(input))
						||(pbName!=null&&pbName.contains(input))
						||(pbNumber!=null&&(pbNumber.contains(input)||pbNumber.replace("-","").contains(input)))) {
				 		result.add(list.get(n));
			 		}
			 		n++;
			 	}
		        return result;
		 	}
		 	return null;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(ArrayList<PhoneBookItem> result) {
			L.e("result size:" + result.size());
//			if(result != null && result.size() > 0){
//				/*lSearch.clear();
//				lSearch.addAll(result);*/
//				mPhonebookListView.setVisibility(View.VISIBLE);
//			}else{
//
//				mPhonebookListView.setVisibility(View.INVISIBLE);
//			}
//			notifylistviewdataupdate();
			if(adapter.getPhoneBookList().size() == 0 && (result == null ||result.size() == 0)){
				
			}else{
				mBtnDelete.setEnabled(false);
				adapter.clear();
				if(result != null && result.size() > 0){
					adapter.getPhoneBookList().addAll(result);
				}
				adapter.notifyDataSetInvalidated();
			
			}
			super.onPostExecute(result);
		}
		
    };
    
	private  class PhoneBookAdapter extends BaseAdapter{
		private final int UNSELECT = -1;
		private Context mContext;
		private LayoutInflater inflater;
		private int mSelectIndex = UNSELECT;
		private List<PhoneBookItem> phoneBookList = new ArrayList<PhonebookFragment2.PhoneBookItem>();
		
		public PhoneBookAdapter(Context context){
			mContext = context;
			inflater = LayoutInflater.from(context);
		}
		
		public List<PhoneBookItem> getPhoneBookList(){
			return phoneBookList;
		}
		public void clear(){
			getPhoneBookList().clear();
			mSelectIndex = UNSELECT;
		}
		public void setPhoneBookList(List<PhoneBookItem> phoneBookList) {
			this.phoneBookList = phoneBookList;
		}
		
		public void setSelect(int index){
			mSelectIndex = index;
			notifyDataSetChanged();
		}
		
		public void removeSelectItem(){
			if(mSelectIndex > -1 && phoneBookList.size() > mSelectIndex){
				allPhonebooks.remove(phoneBookList.get(mSelectIndex));
				phoneBookList.remove(mSelectIndex);
				mSelectIndex = -1;
        		notifyDataSetChanged();
	            if (allPhonebooks.size() == 0) {
			Log.d("TEST","removeSelectItem show mTipEmptyTextView");
	            	mTipEmptyTextView.setVisibility(View.VISIBLE);
					mBtnDelete.setEnabled(false);
					mBtnDeleteAll.setEnabled(false);
				}
        	}
        }
		
		public void removeAll(){
			Log.d("TEST","removeAll");
        	if(phoneBookList != null && phoneBookList.size() > 0){
            	allPhonebooks.clear();	
        		allPhonebooks.removeAll(phoneBookList);
        		phoneBookList.clear();
				mBtnDelete.setEnabled(false);
				mBtnDeleteAll.setEnabled(false);
        		notifyDataSetChanged();
				Bluetooth.getInstance().delephonebook(true);
        	}
        }
		
		public int getCount() {
			return phoneBookList.size();
		}

		@Override
		public Object getItem(int position) {
			return phoneBookList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = inflater.inflate(R.layout.phonebook_listitem,parent,false);
			TextView nameText = (TextView)view.findViewById(R.id.item_phonebook_name);
			TextView phoneText = (TextView)view.findViewById(R.id.item_phonebook_number);
			
            if(TextUtils.isEmpty(phoneBookList.get(position).pbName)){
            	nameText.setVisibility(View.GONE);
            }else{
            	nameText.setText(phoneBookList.get(position).pbName);
            }
            phoneText.setText(phoneBookList.get(position).pbNumber);

            ViewGroup iv = (ViewGroup)view.findViewById(R.id.phonebook_item);
			if(mSelectIndex == position){
  				iv.setSelected(true);
			}else{
				iv.setSelected(false);
			}
			return view;
		}
	}
	
	private class PhoneBookItem{
		public int id;
		public String pbName;
		public String pbNumber;
		public String py;
		public String pyhead;
	}
}