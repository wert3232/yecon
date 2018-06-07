/**
 * @Title: MusicListActivity.java
 * @Package com.yecon.musicplayer
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年5月12日 下午3:26:19
 * @version V1.0
 */
package com.yecon.music;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.media.constants.MediaConstants;
import com.tuoxianui.tools.AtTimerHelpr;
import com.tuoxianui.view.TopBar;
import com.yecon.media.AlbumFragment;
import com.yecon.media.ArtistFragment;
import com.yecon.media.FavorFragment;
import com.yecon.media.FileFragment;
import com.yecon.media.FolderFragment;
import com.yecon.media.MediaListFragment;
import com.yecon.mediaprovider.YeconMediaStore;
import com.yecon.mediaservice.MediaBaseActivity;
import com.yecon.mediaservice.MediaObject;
import com.yecon.mediaservice.MediaPlayerService;
import com.yecon.mediaservice.MediaStorage;
import com.yecon.mediaservice.MediaPlayerContants.MediaListContants;
import com.yecon.mediaservice.MediaPlayerContants.MediaPlayerMessage;
import com.yecon.mediaservice.MediaPlayerContants.MediaType;
import com.yecon.mediaservice.MediaPlayerContants.PlayStatus;
import com.yecon.mediaservice.MediaPlayerContants.ServiceStatus;
import com.yecon.mediaservice.MediaPlayerContants.MediaListContants.Origin;
import com.yecon.mediaservice.MediaPlayerContants.MediaListContants.Type;
import com.yecon.music.R;
import com.yecon.music.msg.EventBusMsg;
import com.yecon.music.util.L;

/**
 * @ClassName: MusicListActivity
 * @Description: TODO
 * @author hzGuo
 * @date 2016年5月12日 下午3:26:19
 *
 */
public class MusicListActivity extends MediaBaseActivity implements OnClickListener {
	
	private Activity self = this;
	private static final String TAG = "MusicListActivity";
	private static final int TIME_BACK_TO_PALYER = 20;
	private static final String DEVICE = "attach_device";
	private static final int MSG_ATTACH_DEIVCE = 1;
	private static final int MSG_LOADING = 2;
	private static final int DELAY_MSG_TIME = 500;
	
	private final String VIEW_MODE = "view_mode";
	private int currentMode = 0;
	
	private SharedPreferences.Editor mEditor ;
	private SharedPreferences mPref;
	
	// widget  FIXME: btn name
	private TextView mBtnPlayList;
	private TextView mBtnFloderList;
	private TextView mBtnAlbumList;
	private TextView mBtnArtistList;
	private TextView mBtnFavorList;
	private TextView mBtnViewMode;
	private TextView mTableViewMode;
	
	private DeviceView mBtnDeviceSD;
	private DeviceView mBtnDeviceSD1;
	private DeviceView mBtnDeviceSD2;
	private DeviceView mBtnDeviceUSB1;
	private DeviceView mBtnDeviceUSB2;
	private DeviceView mBtnDeviceUSB3;
	private DeviceView mBtnDeviceUSB4;
	private DeviceView mBtnDeviceUSB5;
	
	private TextView mBtnDeviceList;
	private TextView mBtnReScan;
	private TextView mBtnBack;
	private TextView mPlayView;
	private TextView mTVLoading;
	
	private TextView mPrev;
	private TextView mPlay;
	private TextView mPause;
	private TextView mNext;
	
	private LinearLayout mLayoutLoading;
	private LinearLayout mLayoutListMain;
	
	// fragment
	MediaListFragment mFmPlayList;
	MediaListFragment mFmFloderList;
	MediaListFragment mFmAlbumList;
	MediaListFragment mFmArtistList;
	MediaListFragment mFmFavorList;
	
	MediaListFragment mCurActiveFm;
	
	LinearLayout mLayoutDevice;
	
	private AtTimerHelpr mAtTimerHelpr;
	
	private int miBackCount = TIME_BACK_TO_PALYER;
	
	MediaPlayerService mMediaPlayerService;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == MSG_ATTACH_DEIVCE) {
				Bundle bundle =msg.getData(); 
				if (bundle != null) {
					AttachStorage(bundle.getString(DEVICE), MediaType.MEDIA_AUDIO);
				}
			} else if (msg.what == MSG_LOADING) {
				boolean bshow = (msg.arg1 == 1);
				mLayoutLoading.setVisibility(bshow ? View.VISIBLE : View.GONE);
				mLayoutLoading.getBackground().setAlpha(100);
				mLayoutListMain.setClickable(!bshow);
				mLayoutListMain.setEnabled(!bshow);
			}
		}
		
	};
	
	public class ViewDevice {
		DeviceView deviceView;
		TextView tv;
		boolean play;
	}
	
	private List<ViewDevice> mListDeviceView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mListDeviceView = new ArrayList<ViewDevice>();
		mPref = this.getSharedPreferences("Music_settings", 3);
		mEditor = mPref.edit();
		InitUI();
		mMediaPlayerService = (MediaPlayerService) getMediaSevice();
		UpdateDevices();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(MediaConstants.TO_PLAY);
		filter.addAction(MediaConstants.TO_PAUSE);
		filter.addAction(MediaConstants.TO_STOP);
		registerReceiver(mReceiver, filter);
		
		//一汽要求,不自动流转 mAtTimerHelpr = new AtTimerHelpr();
		AtTimerHelpr.AtTimerHelprDoItCallBack atTimerHelprDoItCallBack = new AtTimerHelpr.AtTimerHelprDoItCallBack() {
			@Override
			public void doIt() {
				try {
					timerHandler.sendEmptyMessage(0);
				} catch (Exception e) {
					L.e(e.toString());
				}
			}
		};
		//一汽要求,不自动流转 mAtTimerHelpr.setCallBack(atTimerHelprDoItCallBack);
	}
	
	private void showLoading(boolean bshow) {
		mHandler.removeMessages(MSG_LOADING);
		Message msg = Message.obtain();
		msg.what = MSG_LOADING;
		msg.arg1 = bshow ? 1 : 0;
		mHandler.sendMessageDelayed(msg, bshow ? 0 : DELAY_MSG_TIME);
		miBackCount = TIME_BACK_TO_PALYER;
	}
	
	//FIXME: init view
	private void InitUI() {
		setContentView(R.layout.yecon_music_list_activity);
		
		mLayoutDevice = (LinearLayout) findViewById(R.id.layout_device);
		mLayoutLoading = (LinearLayout) findViewById(R.id.layout_loading);
		mLayoutListMain = (LinearLayout) findViewById(R.id.layout_listmain);
		mBtnDeviceList = (TextView) findViewById(R.id.btn_tab_device);
		mBtnReScan = (TextView) findViewById(R.id.btn_tab_rescan);
		mBtnViewMode = (TextView) findViewById(R.id.btn_tab_view_mode);
		mBtnBack = (TextView) findViewById(R.id.back);
		mTableViewMode = (TextView) findViewById(R.id.table_mode_text);
		mPlayView = (TextView)findViewById(R.id.btn_tab_play_view);
		  /*//yfzhang Commented-Out Cod  
		mBtnPlayList = (TextView) findViewById(R.id.btn_tab_list);
		mBtnFloderList = (TextView) findViewById(R.id.btn_tab_folder);
		mBtnAlbumList = (TextView) findViewById(R.id.btn_tab_album);
		mBtnArtistList = (TextView) findViewById(R.id.btn_tab_artist);
		mBtnFavorList = (TextView) findViewById(R.id.btn_tab_favorite);*/
		
		mBtnDeviceSD = (DeviceView) findViewById(R.id.btn_device_SD);
		mBtnDeviceSD1 = (DeviceView) findViewById(R.id.btn_device_extsd1);
		mBtnDeviceSD2 = (DeviceView) findViewById(R.id.btn_device_extsd2);
		mBtnDeviceUSB1 = (DeviceView) findViewById(R.id.btn_device_USB1);
		mBtnDeviceUSB2 = (DeviceView) findViewById(R.id.btn_device_USB2);
		mBtnDeviceUSB3 = (DeviceView) findViewById(R.id.btn_device_USB3);
		mBtnDeviceUSB4 = (DeviceView) findViewById(R.id.btn_device_USB4);
		mBtnDeviceUSB5 = (DeviceView) findViewById(R.id.btn_device_USB5);
		
		mPrev = (TextView)findViewById(R.id.prev);
		mPlay = (TextView)findViewById(R.id.play);
		mPause = (TextView)findViewById(R.id.pause);
		mNext = (TextView)findViewById(R.id.next);
		
		mTVLoading = (TextView) findViewById(R.id.tv_loading);
		mTVLoading.setVisibility(View.GONE);

		//FIXME:111001
		TopBar topBar = (TopBar) findViewById(R.id.topbar);
		topBar.setTopBarBackCall(new TopBar.TopBarBackCall() {
			@Override
			public void onHomeReturn() {
				if(mHandler != null){
					sendOrderedBroadcast(new Intent(MediaConstants.DO_EXIT_APP),null);
				}
			}
		});//


		/*//  yfzhang Commented-Out Cod 
		mBtnPlayList.setOnClickListener(this);
		mBtnFloderList.setOnClickListener(this);
		mBtnAlbumList.setOnClickListener(this);
		mBtnArtistList.setOnClickListener(this);
		mBtnFavorList.setOnClickListener(this);*/
		mBtnDeviceList.setOnClickListener(this);
		mBtnReScan.setOnClickListener(this);
		mLayoutLoading.setOnClickListener(this);
		mBtnViewMode.setOnClickListener(this);
		mBtnBack.setOnClickListener(this);
		mPlayView.setOnClickListener(this);
		
		mBtnDeviceSD.setOnClickListener(this);
		mBtnDeviceSD1.setOnClickListener(this);
		mBtnDeviceSD2.setOnClickListener(this);
		mBtnDeviceUSB1.setOnClickListener(this);
		mBtnDeviceUSB2.setOnClickListener(this);
		mBtnDeviceUSB3.setOnClickListener(this);
		mBtnDeviceUSB4.setOnClickListener(this);
		mBtnDeviceUSB5.setOnClickListener(this);
		
		mPrev.setOnClickListener(this);
		mPlay.setOnClickListener(this);
		mPause.setOnClickListener(this);
		mNext.setOnClickListener(this);
		
		mFmAlbumList = new AlbumFragment(this);
		mFmArtistList = new ArtistFragment(this);
		mFmPlayList = new FileFragment(this);
		mFmFloderList = new FolderFragment(this);
		mFmFavorList = new FavorFragment(this);
		
		mFmFloderList.UpdateList(getList(Type.ALL_DIR));
		mFmAlbumList.UpdateList(getList(Type.ALL_ALBUM));
		mFmArtistList.UpdateList(getList(Type.ALL_ARTIST));
		mFmPlayList.UpdateList(getList(Type.ALL_FILE));
		mFmFavorList.UpdateList(getList(Type.FAVORITE_FILE));
		showLoading(false);
		
		recoveryState();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		sendBroadcast(new Intent(MediaConstants.CURRENT_MEDIA_IS_MUSIC), null);
		int iState;
		try {
			
			iState = getMediaSevice().getPlayStatus();
			if (iState == PlayStatus.PAUSED) {
				mPlay.setVisibility(View.VISIBLE);
				mPause.setVisibility(View.GONE);
			} else if (iState == PlayStatus.STARTED) {
				mPlay.setVisibility(View.GONE);
				mPause.setVisibility(View.VISIBLE);
			}else {
			}
		} catch (RemoteException e) {
			Log.e(this.getClass().getName(),e.toString());
		}
		//一汽要求,不自动流转 mAtTimerHelpr.start(5);
	}
	
	@Override
	protected void onPause() {
	//FIX ME YeconMedia
		/*List<MediaObject> list = getMediaSevice().getPlayingDirList();
		try {
			if(getMediaSevice().getPlayListType() == Type.DIR_FILE){
				getMediaSevice().requestFileListByDir(getMediaSevice().getPlayingDirPos());
			}
		} catch (RemoteException e) {
			L.e(e.toString());
		}*/
		//一汽要求,不自动流转 mAtTimerHelpr.stop();
		super.onPause();
		
	}
	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//一汽要求,不自动流转 mAtTimerHelpr.reset();
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		miBackCount = TIME_BACK_TO_PALYER;
		//一汽要求,不自动流转 mAtTimerHelpr.reset();
		return super.dispatchTouchEvent(ev);
	}

	private void recoveryState() {
		// TODO Auto-generated method stub
		try {
			/* //  yfzhang Commented-Out Cod 
			mBtnPlayList.setSelected(false);
			mBtnFloderList.setSelected(false);
			mBtnAlbumList.setSelected(false);
			mBtnArtistList.setSelected(false);
			mBtnFavorList.setSelected(false);*/
			if (!isBindService()) {
				return;
			}
			int iType = getMediaSevice().getPlayListType();
			int iOrigin = MediaListContants.getListOrigin(iType);
//			currentMode = mPref.getInt(VIEW_MODE, iOrigin);
//			currentMode = mPref.getInt(VIEW_MODE, Origin.DIR); //默认设定为文件夹
			currentMode = Origin.FILE;
			mEditor.putInt(VIEW_MODE, currentMode).commit();
			switch (currentMode) {
			case Origin.FILE:
//				mBtnPlayList.setSelected(true); //  yfzhang Commented-Out Cod 
				mTableViewMode.setText(R.string.playlists_title);
				LoadFragment(mFmPlayList);
				break;
			case Origin.ARTIST:
//				mBtnArtistList.setSelected(true);
				mTableViewMode.setText(R.string.artists_title);
				LoadFragment(mFmArtistList);
				break;
			case Origin.DIR:
//				mBtnFloderList.setSelected(true); //  yfzhang Commented-Out Cod 
				mTableViewMode.setText(R.string.str_tab_folder);
				LoadFragment(mFmFloderList);
				break;
			case Origin.ALBUM:
//				mBtnAlbumList.setSelected(true); //  yfzhang Commented-Out Cod 
				mTableViewMode.setText(R.string.albums_title);
				LoadFragment(mFmAlbumList);
				break;
			case Origin.FAVORITE:
//				mBtnFavorList.setSelected(true); //  yfzhang Commented-Out Cod
				mTableViewMode.setText(R.string.str_tab_favor);
				LoadFragment(mFmFavorList);
				break;
			default:
				break;
			}
			
			switch (iType) {
			case Type.ARTIST_FILE:
				mFmArtistList.UpdateList(getList(Type.ARTIST_FILE));
				break;
			case Type.ALBUM_FILE:
				mFmAlbumList.UpdateList(getList(Type.ALBUM_FILE));
				break;
			case Type.DIR_FILE:
				mFmFloderList.UpdateList(getList(Type.DIR_FILE));
				break;

			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * FIXME: OnClickListener onClick
	 * Title: onClick
	 * Description:
	 * @param v
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int iID = v.getId(); 
		switch (iID) {
		/*  //yfzhang Commented-Out Cod  
		case R.id.btn_tab_list:
			LoadFragment(mFmPlayList);
			mBtnPlayList.setSelected(true);
			mBtnFloderList.setSelected(false);
			mBtnAlbumList.setSelected(false);
			mBtnArtistList.setSelected(false);
			mBtnFavorList.setSelected(false);
			break;
			
		case R.id.btn_tab_folder:
			LoadFragment(mFmFloderList);
			mBtnPlayList.setSelected(false);
			mBtnFloderList.setSelected(true);
			mBtnAlbumList.setSelected(false);
			mBtnArtistList.setSelected(false);
			mBtnFavorList.setSelected(false);
			break;
			
		case R.id.btn_tab_album:
			LoadFragment(mFmAlbumList);
			mBtnPlayList.setSelected(false);
			mBtnFloderList.setSelected(false);
			mBtnAlbumList.setSelected(true);
			mBtnArtistList.setSelected(false);
			mBtnFavorList.setSelected(false);
			break;
			
		case R.id.btn_tab_artist:
			LoadFragment(mFmArtistList);
			mBtnPlayList.setSelected(false);
			mBtnFloderList.setSelected(false);
			mBtnAlbumList.setSelected(false);
			mBtnArtistList.setSelected(true);
			mBtnFavorList.setSelected(false);
			break;
			
		case R.id.btn_tab_favorite:
			LoadFragment(mFmFavorList);
			mBtnPlayList.setSelected(false);
			mBtnFloderList.setSelected(false);
			mBtnAlbumList.setSelected(false);
			mBtnArtistList.setSelected(false);
			mBtnFavorList.setSelected(true);
			break;*/
			
		case R.id.btn_tab_device:
			if (mLayoutDevice.getVisibility() == View.VISIBLE) {
				mLayoutDevice.setVisibility(View.GONE);
			} else {
				mLayoutDevice.setVisibility(View.VISIBLE);
			}
			break;
			
		case R.id.btn_tab_rescan:
			try {
				String path = getMediaSevice().getPlayingStorage().getPath();
				if (path != null) {
					if (getMediaSevice().requestReScaning(path)) {
						showLoading(true);
					}
				} else {
					Toast.makeText(getApplicationContext(), "请选定磁盘", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.btn_tab_view_mode:
			LoadNextFragment();
			break;
		case R.id.back:
			onBackPressed();
			break;
		case R.id.btn_tab_play_view:
			super.onBackPressed();
			break;
		case R.id.btn_device_SD:
			AttachDevice(YeconMediaStore.EXTERNAL_PATH);
			break;

		case R.id.btn_device_extsd1:
			AttachDevice(YeconMediaStore.EXT_SDCARD1_PATH);
			break;

		case R.id.btn_device_extsd2:
			AttachDevice(YeconMediaStore.EXT_SDCARD2_PATH);
			break;

		case R.id.btn_device_USB1:
			AttachDevice(YeconMediaStore.UDISK1_PATH);
			break;
			
		case R.id.btn_device_USB2:
			AttachDevice(YeconMediaStore.UDISK2_PATH);
			break;
			
		case R.id.btn_device_USB3:
			AttachDevice(YeconMediaStore.UDISK3_PATH);
			break;
			
		case R.id.btn_device_USB4:
			AttachDevice(YeconMediaStore.UDISK4_PATH);
			break;
			
		case R.id.btn_device_USB5:
			AttachDevice(YeconMediaStore.UDISK5_PATH);
			break;
		case R.id.prev:{
			Intent intent = new Intent();
			String mediaType = MediaConstants.CURRENT_MEDIA_IS_MUSIC;
			intent.setAction(MediaConstants.DO_PREV);
			intent.putExtra("media_type", mediaType);
			sendOrderedBroadcast(intent,null);
		}
		break;
		case R.id.play:{
			Intent intent = new Intent();
			String mediaType = MediaConstants.CURRENT_MEDIA_IS_MUSIC;
			intent.setAction(MediaConstants.DO_PLAY);
			intent.putExtra("media_type", mediaType);
			sendOrderedBroadcast(intent,null);
		}
		break;
		case R.id.pause:{
			Intent intent = new Intent();
			String mediaType = MediaConstants.CURRENT_MEDIA_IS_MUSIC;
			intent.setAction(MediaConstants.DO_PAUSE);
			intent.putExtra("media_type", mediaType);
			sendOrderedBroadcast(intent,null);
		}
		break;
		case R.id.next:
			Intent intent = new Intent();
			String mediaType = MediaConstants.CURRENT_MEDIA_IS_MUSIC;
			/*intent.setAction(MediaConstants.DO_PLAY);
			intent.setAction(MediaConstants.DO_PAUSE);
			intent.setAction(MediaConstants.DO_PREV);*/
			intent.setAction(MediaConstants.DO_NEXT);
			intent.putExtra("media_type", mediaType);
			sendOrderedBroadcast(intent,null);
		break;
		default:
			break;
		}
	}
	
	public void AttachDevice(String strDisk) {
		//if(strDisk.equals())
		//findViewById(R.id.layout_content).setVisibility(View.VISIBLE);
		mLayoutDevice.setVisibility(View.GONE);
		mHandler.removeMessages(MSG_ATTACH_DEIVCE);
		Message msg = Message.obtain();
		msg.what = MSG_ATTACH_DEIVCE;
		Bundle data = new Bundle();
		data.putString(DEVICE, strDisk);
		msg.setData(data);
		mHandler.sendMessageDelayed(msg, DELAY_MSG_TIME);
		//showLoading(true);
	}
	
	public void UpdateDevices() {
		mBtnDeviceSD.setVisibility(View.GONE);
		mBtnDeviceSD1.setVisibility(View.GONE);
		mBtnDeviceSD2.setVisibility(View.GONE);
		mBtnDeviceUSB1.setVisibility(View.GONE);
		mBtnDeviceUSB2.setVisibility(View.GONE);
		mBtnDeviceUSB3.setVisibility(View.GONE);
		mBtnDeviceUSB4.setVisibility(View.GONE);
		mBtnDeviceUSB5.setVisibility(View.GONE);
		
		List<MediaStorage> lsStorages;
		try {
			mListDeviceView.clear();
			lsStorages = getMediaSevice().getStorageList();
			for (MediaStorage mediaStorage : lsStorages) {
				L.e(TAG, "storage:" + mediaStorage.getPath() + " music:" + mediaStorage.getAudioCount());
				ViewDevice vd = new ViewDevice();
				if (mediaStorage.getPath()
						.equals(YeconMediaStore.EXTERNAL_PATH)) {
					vd.tv = (mBtnDeviceSD.getTextView());
					vd.deviceView = mBtnDeviceSD;
				} else if (mediaStorage.getPath().equals(
						YeconMediaStore.EXT_SDCARD1_PATH)) {
					vd.tv = (mBtnDeviceSD1.getTextView());
					vd.deviceView = mBtnDeviceSD1;
				} else if (mediaStorage.getPath().equals(
						YeconMediaStore.EXT_SDCARD2_PATH)) {
					vd.tv = (mBtnDeviceSD2.getTextView());
					vd.deviceView = mBtnDeviceSD2;
				} else if (mediaStorage.getPath().equals(
						YeconMediaStore.UDISK1_PATH)) {
					vd.tv = (mBtnDeviceUSB1.getTextView());
					vd.deviceView = mBtnDeviceUSB1;
				} else if (mediaStorage.getPath().equals(
						YeconMediaStore.UDISK2_PATH)) {
					vd.tv = (mBtnDeviceUSB2.getTextView());
					vd.deviceView = mBtnDeviceUSB2;
				} else if (mediaStorage.getPath().equals(
						YeconMediaStore.UDISK3_PATH)) {
					vd.tv = (mBtnDeviceUSB3.getTextView());
					vd.deviceView = mBtnDeviceUSB3;
				} else if (mediaStorage.getPath().equals(
						YeconMediaStore.UDISK4_PATH)) {
					vd.tv = (mBtnDeviceUSB4.getTextView());
					vd.deviceView = mBtnDeviceUSB4;
				} else if (mediaStorage.getPath().equals(
						YeconMediaStore.UDISK5_PATH)) {
					vd.tv = (mBtnDeviceUSB5.getTextView());
					vd.deviceView = mBtnDeviceUSB5;
				}
				vd.play = mediaStorage.getPlaying();
				if (vd.tv != null) {
					mListDeviceView.add(vd);
				}
				try {
					if (isBindService()) {
						if(getMediaSevice().getPlayStatus()==PlayStatus.IDLE && mediaStorage.getAudioCount()==0)
							findViewById(R.id.bottom_other).setVisibility(View.INVISIBLE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < mListDeviceView.size(); i++) {
			TextView tv = mListDeviceView.get(i).tv;
			DeviceView dv = mListDeviceView.get(i).deviceView;
			//if(tv.getId() != R.id.btn_device_SD){ //因为不许要显示内部存储，所以隐藏掉
				tv.setVisibility(View.VISIBLE);
				if(dv != null){
					dv.setVisibility(View.VISIBLE);
				}
			//}
			if (mListDeviceView.get(i).play) {
//				tv.setBackground(getResources().getDrawable(R.drawable.board_p));
				tv.setSelected(true);
				if(dv != null){
					dv.setSelected(true);
				}
			} else {
//				tv.setBackground(getResources().getDrawable(R.drawable.board_n));
				tv.setSelected(false);
				if(dv != null){
					dv.setSelected(false);
				}
			}
		}
//		mLayoutDevice.setVisibility(mListDeviceView.isEmpty() ? View.GONE : View.VISIBLE);
//		mBtnDeviceList.setVisibility(mListDeviceView.isEmpty() ? View.GONE : View.VISIBLE);
	}
	
	public void LightDevice(int iID) {
		mBtnDeviceSD.setSelected(iID == R.id.btn_device_SD);
		mBtnDeviceSD1.setSelected(iID == R.id.btn_device_extsd1);
		mBtnDeviceSD2.setSelected(iID == R.id.btn_device_extsd2);
		mBtnDeviceUSB1.setSelected(iID == R.id.btn_device_USB1);
		mBtnDeviceUSB2.setSelected(iID == R.id.btn_device_USB2);
		mBtnDeviceUSB3.setSelected(iID == R.id.btn_device_USB3);
		mBtnDeviceUSB4.setSelected(iID == R.id.btn_device_USB4);
		mBtnDeviceUSB5.setSelected(iID == R.id.btn_device_USB5);
	}

	public void LoadFragment(MediaListFragment fm) {
		if(fm instanceof FileFragment){
			mTableViewMode.setText(R.string.playlists_title);
		}else if(fm instanceof FolderFragment){
			mTableViewMode.setText(R.string.str_tab_folder);
		}else if(fm instanceof AlbumFragment){
			mTableViewMode.setText(R.string.albums_title);
		}else if(fm instanceof ArtistFragment){
			mTableViewMode.setText(R.string.artists_title);
		}else if(fm instanceof FavorFragment){
			mTableViewMode.setText(R.string.str_tab_favor);
		}
		else{
			mTableViewMode.setText(R.string.no_playlists_title);
		}
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.layout_content, fm);
		mCurActiveFm = fm;
		ft.commitAllowingStateLoss();
	}
	public void LoadNextFragment(){
		
		MediaListFragment nextfm = null;
		int nextMode = Origin.FILE;
		switch (currentMode) {
			case Origin.FILE:
				nextfm= mFmFloderList;
				nextMode = Origin.DIR;
				break;
			case Origin.DIR:
				nextfm= mFmAlbumList;
				nextMode = Origin.ALBUM;
				break;
			case Origin.ALBUM:
				nextfm= mFmArtistList;
				nextMode = Origin.ARTIST;
				break;
			case Origin.ARTIST:
				nextfm= mFmFavorList;	
				nextMode = Origin.FAVORITE;
				break;
			case Origin.FAVORITE:
				nextfm= mFmPlayList;
				nextMode = Origin.FILE;
				break;
			default:
				break;
		}
		if(nextfm != null){
			currentMode = nextMode; 
			mEditor.putInt(VIEW_MODE, nextMode).commit();
			LoadFragment(nextfm);
		}
	}
	
	private void goMainPlayView(){
		try {
			if (isBindService()) {
				if(getMediaSevice().getPlayStatus()!=PlayStatus.IDLE)
					super.onBackPressed();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		L.e("onKeyDown keyCode : " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT || keyCode == KeyEvent.KEYCODE_YECON_NEXT){
			mNext.callOnClick();
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS || keyCode == KeyEvent.KEYCODE_YECON_PREV){
			mPrev.callOnClick();
			return true;
		}
		else return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		L.e("onKeyUp keyCode : " + keyCode);
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		if (!isScaningAttachedDevice()) {
			if (mCurActiveFm != null) {
				if (mCurActiveFm.IsFileList()) {
					if (mCurActiveFm.equals(mFmAlbumList)) {
						mCurActiveFm.UpdateList(getList(Type.ALL_ALBUM));
						return;
					} else if (mCurActiveFm.equals(mFmArtistList)) {
						mCurActiveFm.UpdateList(getList(Type.ALL_ARTIST));
						return;
					} else if (mCurActiveFm.equals(mFmFloderList)) {
						mCurActiveFm.UpdateList(getList(Type.ALL_DIR));
						return;
					}
				}
			}
		}
		if(findViewById(R.id.layout_content).getVisibility()!=View.VISIBLE)
			super.onBackPressed();
		else{
			findViewById(R.id.layout_content).setVisibility(View.GONE);
			mLayoutDevice.setVisibility(View.VISIBLE);
			findViewById(R.id.bottom_other).setVisibility(View.INVISIBLE);
		}
//		moveTaskToBack(true);
		//onBackPressedToHome();
	}
	private void onBackPressedToHome() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
    }
	
	/**
	 * Title: handleMessage
	 * Description:
	 * @param msg
	 * @return
	 * @see android.os.Handler.Callback#handleMessage(android.os.Message)
	 */
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case MediaPlayerMessage.UPDATE_SERVICE_STATE:
			if (msg.arg1 == ServiceStatus.SCANED) {
				recoveryState();
				if (!isScaningAttachedDevice()) {
					showLoading(false);
					if(mLayoutDevice.getVisibility() != View.VISIBLE){
						findViewById(R.id.layout_content).setVisibility(View.VISIBLE);
						try {
							if (isBindService()) {
								if(getMediaSevice().getPlayStatus()==PlayStatus.STARTED)
									findViewById(R.id.bottom_other).setVisibility(View.VISIBLE);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} else if (msg.arg1 == ServiceStatus.SCANING) {
				if (isScaningAttachedDevice()) {
					//showLoading(true);
				}
			}
			UpdateDevices();
			break;
		case MediaPlayerMessage.UPDATE_LIST_DATA:
			showLoading(false);
			if(mLayoutDevice.getVisibility() != View.VISIBLE){
				findViewById(R.id.layout_content).setVisibility(View.VISIBLE);
				if(getList(msg.arg1).size()>0)
					findViewById(R.id.bottom_other).setVisibility(View.VISIBLE);
			}
			int iListAttr = MediaListContants.getListOrigin(msg.arg1);
			switch (iListAttr) {
			case Origin.FILE:
				mFmPlayList.UpdateList(getList(msg.arg1));
//				mFmFloderList.UpdateList(getList(Type.DIR_FILE));
				break;
			case Origin.DIR:
				mFmFloderList.UpdateList(getList(msg.arg1));
				break;
			case Origin.ALBUM:
				mFmAlbumList.UpdateList(getList(msg.arg1));
				break;
			case Origin.ARTIST:
				mFmArtistList.UpdateList(getList(msg.arg1));
				break;
			case Origin.FAVORITE:
				mFmFavorList.UpdateList(getList(msg.arg1));
				break;
			default:
				break;
			}
			break;
			
		case MediaPlayerMessage.UPDATE_PLAY_PROGRESS:
			L.e(TAG, "count" + miBackCount);
			if (miBackCount-- <= 0) {
				if (mLayoutLoading.getVisibility() != View.VISIBLE) {
					//一汽要求,不自动流转 finish();
				}
			}
			break;

		default:
			break;
		}
		return false;
	}
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(MediaConstants.TO_PLAY.equalsIgnoreCase(action)){
				mPlay.setVisibility(View.GONE);
				mPause.setVisibility(View.VISIBLE);
				mCurActiveFm.LocalListPosition();
			}else if(MediaConstants.TO_PAUSE.equalsIgnoreCase(action)){
				mPlay.setVisibility(View.VISIBLE);
				mPause.setVisibility(View.GONE);
				
			}else if(MediaConstants.TO_STOP.equalsIgnoreCase(action)){
				mPlay.setVisibility(View.VISIBLE);
				mPause.setVisibility(View.GONE);
				
			}
		}
	};
	
	Handler timerHandler = new Handler() {
		public void handleMessage(Message msg) {
			goMainPlayView();
		}
	};
}
