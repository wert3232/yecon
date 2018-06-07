package com.yecon.imagebrowser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.autochips.storage.EnvironmentATC;
import com.yecon.common.SourceManager;
import com.yecon.common.YeconEnv;
import com.yecon.imagebrowser.R;
import com.yecon.mediaprovider.YeconMediaStore;
import com.yecon.mediaprovider.YeconMediaStore.YeconMediaFilesColumns;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;


public class ImageBrowserActivity extends Activity implements OnItemClickListener,OnClickListener{

	public static final String TAG = "MainActivity";
	
	private FileAdapter mFileAdapter;
	private GridView mFileGridView;
	
	private HashMap<String, ArrayList<FolderItem>> mMapFolderList;
	private ArrayList<FolderItem> mFolderList;
	private FolderAdapter mFolderAdapter;
	private GridView mFolderGridView;
	
	private FrameLayout mLayoutEmpty;
	
	ImageAsyncQueryHandler mQueryHandler;
	private int mFolderID = 0;
	private SharedPreferences mPrf;
	public final static String FOLDER_PATH = "folderPath";
	public final static String FILE_PATH = "filePath";
	private BroadcastReceiver mReceiverScan = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			synchronized (TAG) {
				if (intent != null) {
					final String action = intent.getAction();
					if (action.equals(YeconMediaStore.ACTION_YECON_MEIDA_SCANER_STATUS)) {
						String strAction = intent.getStringExtra(YeconMediaStore.ACTION);
						String strPath = intent.getStringExtra(YeconMediaStore.PATH);
						if (strAction != null && strPath != null) {
							if (strAction.equals(YeconMediaStore.ACTION_SCAN_ANALYSIS)) {
								if (mQueryHandler != null) {
									mQueryHandler.query(strPath);
								}
							} else if (strAction.equals(YeconMediaStore.ACTION_SCAN_CANCEL)) {
								mMapFolderList.remove(strPath);
								try {
									if (!mFileAdapter.isEmpty()) {
										if (mFileAdapter.getItem(0).contains(strPath)) {
											mFileGridView.setVisibility(View.GONE);
											mFolderGridView.setVisibility(View.VISIBLE);
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								if (mQueryHandler != null) {
									mQueryHandler.updateAdapter();
								}
							}
						}
					}else if("com.yecon.imagebrowser.treeWindow".equals(action)){
						if(sourceTocken != null){
							SourceManager.acquireSource(sourceTocken);
							sendSourceBroadcast();
						}
					}
				}
			}
		}
	};
	
	private void sendSourceBroadcast(){
		sendBroadcast(new Intent("com.media.action.hasOtherSourceOpen"));
		sendBroadcast(new Intent("com.action.source.open.no_audio"));
		try {
			if(YeconEnv.hasSD(this)){
				Intent intent = new Intent();
				String actionName = ACTION_SOURCE_STACK_SD;
        		intent.setAction(actionName);
				intent.putExtra(Context.EXTRA_APK_PACKAGENAME, "com.yecon.imagebrowser");
        		intent.putExtra(Context.EXTRA_APK_ACTIVITY, "com.yecon.imagebrowser.ImageBrowserActivity");
        		sendOrderedBroadcast(intent, null);
			}
			if(YeconEnv.hasUsb(this)){
				Intent intent = new Intent();
				String actionName = ACTION_SOURCE_STACK_USB;
        		intent.setAction(actionName);
				intent.putExtra(Context.EXTRA_APK_PACKAGENAME, "com.yecon.imagebrowser");
        		intent.putExtra(Context.EXTRA_APK_ACTIVITY, "com.yecon.imagebrowser.ImageBrowserActivity");
        		sendOrderedBroadcast(intent, null);
			}
		} catch (Exception e) {
			L.e(e.toString());
		}
	}
	private String[] storages = new String[] {
		YeconMediaStore.EXTERNAL_PATH,
		YeconMediaStore.EXT_SDCARD1_PATH,
		YeconMediaStore.EXT_SDCARD2_PATH,
		YeconMediaStore.UDISK1_PATH,
		YeconMediaStore.UDISK2_PATH,
		YeconMediaStore.UDISK3_PATH,
		YeconMediaStore.UDISK4_PATH,
		YeconMediaStore.UDISK5_PATH
	};
	
	private String[] file_columns = new String[] {
			YeconMediaFilesColumns.PARENT_ID,
			YeconMediaFilesColumns.DATA,
			YeconMediaFilesColumns.PARENT
	};
	
	private Object sourceTocken = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mPrf = getSharedPreferences("img_setting", Context.MODE_PRIVATE);
		sourceTocken = SourceManager.registerSourceListener(this, SourceManager.SRC_NO.photo);
		SourceManager.setAudioFocusNotify(sourceTocken, new SourceManager.AudioFocusNotify() {

            @Override
            public void onAudioFocusChange(int action) {
                switch (action) {
                    case AudioManager.AUDIOFOCUS_LOSS:
                    	finish();
                        break;
                }
            }
        });
		
		mFolderGridView = (GridView) findViewById(R.id.folderGridView);
		mFileGridView = (GridView) findViewById(R.id.fileGridView);
		mLayoutEmpty = (FrameLayout) findViewById(R.id.layout_empty);
		mQueryHandler = new ImageAsyncQueryHandler(getContentResolver());
		mFolderGridView.setOnItemClickListener(this);
		mFileGridView.setOnItemClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		mMapFolderList = new HashMap<String, ArrayList<FolderItem>>();
		mFolderList = new ArrayList<FolderItem>();
		EnvironmentATC env = new EnvironmentATC(this);
		for (String storage : storages) {
			L.e("mQueryHandler 1" +  storage);
			if (YeconEnv.checkStorageExist(env, storage)) {
				mQueryHandler.query(storage);
				L.e("mQueryHandler 2" +  storage);
			}
		}
		
		IntentFilter filter = null;
		filter = new IntentFilter();
		filter.addAction(YeconMediaStore.ACTION_YECON_MEIDA_SCANER_STATUS);
		filter.addAction("com.yecon.imagebrowser.treeWindow");
		registerReceiver(mReceiverScan, filter);
		findViewById(R.id.loading1).postDelayed(new Runnable() {
			@Override
			public void run() {
				findViewById(R.id.loading1).setVisibility(View.GONE);
			}
		}, 1000);
		sendOrderedBroadcast(new Intent("action.hzh.mediaPlayer.exitApp"), null);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SourceManager.acquireSource(sourceTocken);
		sendSourceBroadcast();
	}
	
	public void imageDetail(Context context, int position, String[] urls) {
		Intent intent = new Intent(context, ImageDetailActivity.class);
		intent.putExtra(ImageDetailActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(ImageDetailActivity.EXTRA_IMAGE_INDEX, position);
		context.startActivity(intent);
	}
	
	public static String getDBFile(String strPath) {
		String strDatabase = YeconMediaStore.EXTERNAL_VOLUME;
		if (strPath.contains(YeconMediaStore.EXT_SDCARD1_PATH)) {
			strDatabase = YeconMediaStore.SDCARD_VOLUME1;
		} else if (strPath.contains(YeconMediaStore.EXT_SDCARD2_PATH)) {
			strDatabase = YeconMediaStore.SDCARD_VOLUME2;
		} else if (strPath.contains(YeconMediaStore.UDISK1_PATH)) {
			strDatabase = YeconMediaStore.UDISK_VOLUME1;
		} else if (strPath.contains(YeconMediaStore.UDISK2_PATH)) {
			strDatabase = YeconMediaStore.UDISK_VOLUME2;
		} else if (strPath.contains(YeconMediaStore.UDISK3_PATH)) {
			strDatabase = YeconMediaStore.UDISK_VOLUME3;
		} else if (strPath.contains(YeconMediaStore.UDISK4_PATH)) {
			strDatabase = YeconMediaStore.UDISK_VOLUME4;
		} else if (strPath.contains(YeconMediaStore.UDISK5_PATH)) {
			strDatabase = YeconMediaStore.UDISK_VOLUME5;
		} else if (strPath.contains(YeconMediaStore.EXTERNAL_PATH)) {
			strDatabase = YeconMediaStore.EXTERNAL_VOLUME;
		}
		return strDatabase;
	}
	
	public String[] getFileArray(int position) {
		FolderItem folder = mFolderList.get(position);
		String[] array = new String[folder.files.size()];
		folder.files.toArray(array);
		return array;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		case R.id.folderGridView:
			/*mPrf.edit().putString(FOLDER_PATH, mFolderList.get(position).folder).commit();
			L.e("FOLDER_PATH: " + mFolderList.get(position).folder);
			mFolderID = position;
			mFileAdapter = new FileAdapter(ImageBrowserActivity.this, getFileArray(position));
			mFileGridView.setAdapter(mFileAdapter);
			mFileGridView.setVisibility(View.VISIBLE);
			mFolderGridView.setVisibility(View.GONE);*/
			showFileWindow(position);
			break;
			
		case R.id.fileGridView:
			L.e("FILE_PATH: " + mFolderList.get(mFolderID).files.get(position));
			imageDetail(ImageBrowserActivity.this, position, getFileArray(mFolderID));
			break;

		default:
			break;
		}
	}
	
	private void showFileWindow(int position){
		mPrf.edit().putString(FOLDER_PATH, mFolderList.get(position).folder).commit();
		L.e("FOLDER_PATH: " + mFolderList.get(position).folder);
		mFolderID = position;
		mFileAdapter = new FileAdapter(ImageBrowserActivity.this, getFileArray(position));
		mFileGridView.setAdapter(mFileAdapter);
		mFileGridView.setVisibility(View.VISIBLE);
		mFolderGridView.setVisibility(View.GONE);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (mFolderGridView.getVisibility() == View.VISIBLE) {
			super.onBackPressed();
		} else {
			mFileGridView.setVisibility(View.GONE);
			mFolderGridView.setVisibility(View.VISIBLE);
		}
	}
	
	@SuppressLint("HandlerLeak")
	class ImageAsyncQueryHandler extends AsyncQueryHandler {

		public ImageAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}
		
		//FIXME:不用关心是哪张或USB的关键地方
		public void query(String storage) {
			//media_type 1音频        2视频            3图片
			startQuery(0, storage, YeconMediaStore.getContent(getDBFile(storage),
					YeconMediaStore.TABLE_FILES), file_columns, "media_type=3",
					null, "parent_id asc");
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			// TODO Auto-generated method stub
			super.onQueryComplete(token, cookie, cursor);
			if (cursor != null) {
				cursor.moveToFirst();
				updateFolderAdapter(cookie, cursor);
				cursor.close();
			}
		}
		
		public void updateFolderAdapter(Object cookie, Cursor c) {
			synchronized (TAG) {
				String dbName = (String)cookie;
				//Log.d("TEST","dbName "+dbName);
				ArrayList<FolderItem> list = new ArrayList<FolderItem>();
				if (c != null) {
					c.moveToFirst();
					FolderItem folder = new FolderItem();
					for (int i = 0; i < c.getCount(); ) {
						//Log.e(this.getClass().getName(),"folder_id:"+ c.getInt(0) + " " + (folder.folder_id == c.getInt(0)) + " folder:" + c.getString(2)  + " path:" + c.getString(1));
						if(folder.folder_id == c.getInt(0) ||
						   folder.folder_id == -1) {
							//folder.folder_id = c.getInt(0);
							folder.files.add("file://" + c.getString(1));
							folder.folder = dbName;//c.getString(2);
							i++;
							c.moveToNext();
						} else {
							if (folder.files.size() > 0) {
								folder.amount = folder.files.size();
								list.add(folder);
							}
							folder = new FolderItem();
						}
					}
					if (folder.files.size() > 0) {
						folder.amount = folder.files.size();
						list.add(folder);
					}
				}
				mMapFolderList.put(dbName, list);
				updateAdapter();
			}
		}
		
		public void updateAdapter() {
			synchronized (TAG) {
				mFolderList.clear();
				for (String storage : storages) {
					if (mMapFolderList.containsKey(storage)) {
						mFolderList.addAll(mMapFolderList.get(storage));
					}
				}
				mFolderAdapter = new FolderAdapter(ImageBrowserActivity.this, mFolderList);
				mFolderGridView.setAdapter(mFolderAdapter);
				if (mFolderList.isEmpty()) {
					mLayoutEmpty.setVisibility(View.VISIBLE);
				} else {
					mLayoutEmpty.setVisibility(View.GONE);
					
					L.e("updateAdapter");
					String momeryFolder = mPrf.getString(FOLDER_PATH, "");
					try {
						if(TextUtils.isEmpty(momeryFolder) || TextUtils.isEmpty(mPrf.getString(FILE_PATH, ""))){
							findViewById(R.id.loading1).setVisibility(View.GONE);
						}
					} catch (Exception e) {
						L.e(e.toString());
					}
					if(!TextUtils.isEmpty(momeryFolder)){
						for(int i = 0; i < mFolderList.size(); i ++){
							FolderItem folderItem = mFolderList.get(i);
							if(momeryFolder.equals(folderItem.folder)){
								showFileWindow(i);
								final int index = i;
								mLayoutEmpty.postDelayed(new Runnable() {
									@Override
									public void run() {
										String momeryFile = mPrf.getString(FILE_PATH, "");
										String[] files = getFileArray(index);
										L.e(files.length + "");
										if(!TextUtils.isEmpty(momeryFile) && files != null){
											for(int position = 0; position < files.length; position++){
												String file = files[position];
												L.e("file_" + file.substring(7));
												L.e("file*" + momeryFile);
												if(!TextUtils.isEmpty(file) && file.substring(7).equals(momeryFile)){
													L.e("file=:" + momeryFile);
													imageDetail(ImageBrowserActivity.this, position, files);
													break;
												}
											}
										}
									}
								}, 200);
								/*if(!TextUtils.isEmpty(momeryFile) && files != null){
									for(int position = 0; position < folderItem.files.size(); i++){
										String file = folderItem.files.get(position);
										L.e("file_" + file.substring(7));
										L.e("fileM" + momeryFile);
										if(!TextUtils.isEmpty(file) && file.matches(momeryFile)){
											imageDetail(ImageBrowserActivity.this, position, files);
											break;
										}
									}
								}*/
								break;
							}
						}
					}
					
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SourceManager.unregisterSourceListener(sourceTocken);
		unregisterReceiver(mReceiverScan);
		//android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			try {
				/*mPrf.edit().remove(FOLDER_PATH).commit();
				mPrf.edit().remove(FILE_PATH).commit();*/
				mPrf.edit().clear().commit();
			} catch (Exception e) {
				Log.e(this.getClass().getName(), e.toString());
			}
			onBackPressed();
			break;
		default:
			break;
		}
	}
}
