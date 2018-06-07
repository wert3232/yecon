package com.autochips.bluetooth;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.autochips.bluetooth.PbSyncManager.PBRecord;
import com.autochips.bluetooth.PbSyncManager.PBSyncManagerService;
import com.autochips.bluetooth.PbSyncManager.PBSyncStruct;
import com.autochips.bluetooth.util.CharacterParser;
import com.autochips.bluetooth.util.L;
import com.autochips.bluetooth.view.ConfirmDialog;
import com.tuoxianui.tools.AtTimerHelpr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneBookActivity  extends Activity  implements View.OnClickListener, AdapterView.OnItemClickListener,TextView.OnEditorActionListener {
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
    public static ArrayList<PhoneBookItem> allPhonebooks = new ArrayList<PhoneBookItem>();
    private PhoneBookAdapter adapter;
    private SearchTask searchTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApp.addActivity(this);
        setContentView(R.layout.bt_phonebook_2);
        initView();
        initReceiver();
        tryshowphonebooklist();
        initInputMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(allPhonebooks != null && !allPhonebooks.isEmpty()){
            mTipEmptyTextView.setVisibility(View.GONE);
            mBtnDelete.setEnabled(true);
            mBtnDeleteAll.setEnabled(true);
        }else{
            mTipEmptyTextView.setVisibility(View.VISIBLE);
            mBtnDelete.setEnabled(false);
            mBtnDeleteAll.setEnabled(false);
        }
        if(!Bluetooth.getInstance().isdownloadidle()) {
            mBtnSyn.setEnabled(true);
        }
        initInputMode();
    }

    @Override
    public void onPause() {
        hideKeyboard(mSearchText.getWindowToken());
        super.onPause();
    }

    @Override
    public void onDestroy() {
        BluetoothReceiver.unregisterNotifyHandler(uiHandler);
        BTService.unregisterNotifyHandler(uiHandler);
        super.onDestroy();
    }
    private void initView(){
        adapter = new PhoneBookAdapter(this);
        mDownloadLayout = (LinearLayout) findViewById(R.id.download_phonebook_layout);
        mDownloadTextView = (TextView) findViewById(R.id.tv_download_phonebook_text);
        mPhonebookListView = (ListView)findViewById(R.id.phonebook_listview);
        mTipEmptyTextView = (TextView)findViewById(R.id.phonebookisempty);
        mSearchText = (EditText) findViewById(R.id.search_edittext);
        mBtnDelete = (TextView) findViewById(R.id.delete);
        mBtnDeleteAll = (TextView) findViewById(R.id.delete_all);

        mBtnDelete.setOnClickListener(this);
        mBtnDeleteAll.setOnClickListener(this);
        mBtnSyn = (TextView)findViewById(R.id.search_syn);
        mBtnSyn.setOnClickListener(this);
        findViewById(R.id.btn_phone_book_back).setOnClickListener(this);
        findViewById(R.id.search_confim).setOnClickListener(this);

        mSearchText.setOnEditorActionListener(this);
        mSearchText.addTextChangedListener(mTextWatcher);
        mSearchText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                //点击后可以编辑
                mSearchText.setFocusableInTouchMode(true);
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
        callDilog = new ConfirmDialog(this);
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
//FIXME 11002                startActivity(new Intent(this,TuoXianDialActivity.class));
                Bluetooth.handStartActivity(this,TuoXianDialActivity.class);
                break;
            }
            case R.id.search_syn:{
                if (Bluetooth.getInstance().isdownloadidle()) {
                    mBtnSyn.setEnabled(false);
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
        String language = this.getResources().getConfiguration().locale.getLanguage();
        InputMethodManager mInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
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
        deleteAllDialog = new ConfirmDialog(this);
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
        deleteAllDialog = new ConfirmDialog(this);
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
        initInputMode();
        mSearchText.clearFocus();
        mSearchText.setFocusableInTouchMode(touchMode);
        hideKeyboard(editText.getWindowToken());
    }

    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    }else{
                        loadAllData();
                    }
                }else if(recievedAction.equals(LocalBluetoothProfileManager.ACTION_PROFILE_STATE_UPDATE)) {
                    LocalBluetoothProfileManager.BTProfile profilename = (LocalBluetoothProfileManager.BTProfile)intent.getSerializableExtra(
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
                                break;
                        }
                    }
                }
            }
        }
    };


    class LoadTask extends AsyncTask<Integer, Integer, ArrayList<PhoneBookItem>> {
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
                    if (!isCallnum(pbNum)) {
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
                if(result.size() > 0){
                    mSearchText.setText("");
                    mPhonebookListView.setVisibility(View.VISIBLE);
                }else{
                    mPhonebookListView.setVisibility(View.INVISIBLE);
                }
            }else {
                Log.d("TEST", "onPostExecute null");
            }
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
        View view = this.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
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
        private List<PhoneBookItem> phoneBookList = new ArrayList<PhoneBookItem>();

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

    public boolean isCallnum(String num){
        Pattern p = Pattern.compile("[0-9\\+]+");
        Matcher m = p.matcher(num);
        return m.matches();
    }
}
