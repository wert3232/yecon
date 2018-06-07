package com.yecon.ipodplayer;

//import com.example.com.atc.musicplayer.R;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.autochips.ipod.IPodSDK.ARTWORK_CNT_INFO_T;
import com.autochips.ipod.IPodSDK.ARTWORK_CNT_T;
import com.autochips.ipod.IPodSDK.ARTWORK_DATA_T;
import com.autochips.ipod.IPodSDK.ARTWORK_FMTS;
import com.autochips.ipod.IPodSDK.ARTWORK_FMTS_INFO_T;
import com.autochips.ipod.IPodSDK.ARTWORK_TIMES_T;
import com.autochips.ipod.IPodSDK.AUTH_RESULT_T;
import com.autochips.ipod.IPodSDK.DB_CATEGORY_TYPE_E;
import com.autochips.ipod.IPodSDK.DB_RECITEM_T;
import com.autochips.ipod.IPodSDK.E_IPOD_AUTHENTICATION_STATUS;
import com.autochips.ipod.IPodSDK.EventCallback;
import com.autochips.ipod.IPodSDK.IPodException;
import com.autochips.ipod.IPodSDK.NOTIFY_INFO_T;
import com.autochips.ipod.IPodSDK.PB_NOTIFY_STATUS_INF_T;
import com.autochips.ipod.IPodSDK.PLAY_ST_EX_E;
import com.autochips.ipod.IPodSDK.PLAY_ST_INFO_T;
import com.autochips.ipod.IPodSDK.STATUS_E;
import com.autochips.ipod.IPodSDK.STATUS_T;
import com.autochips.ipod.IPodSDK.SWI_SINK_TYPE_T;
import com.autochips.ipod.IPodSDK.TRACK_CAP_INF_T;
import com.autochips.ipod.IPodSDK.TRACK_GENRE_INFO_T;
import com.autochips.ipod.IPodSDK.TRACK_INF_TYPE_E;
import com.yecon.common.SourceManager;
import com.yecon.ipodplayer.adapter.MusicAdapterNew;
import com.yecon.ipodplayer.adapter.MusicAdapterNew.MusicInfo;
import com.yecon.ipodplayer.utils.L;
import com.yecon.ipodplayer.view.CustomProgressDialog;
import com.yecon.ipodplayer.view.FboxPopuWindow;
import com.yecon.ipodplayer.view.FboxPopuWindow.onPopuListener;
import com.yecon.ipodplayer.view.*;

import static android.constant.YeconConstants.*;


public class WorkActivity extends Activity implements View.OnClickListener,
OnItemClickListener,onPopuListener{
	final private String TAG = "MainActivity";
	public static final String MCU_ACTION_ACC_OFF = "com.yecon.action.ACC_OFF";
	public static final String MCU_ACTION_ACC_ON = "com.yecon.action.ACC_ON";
	public static final String ACTION_BACKCAR_START = "com.yecon.action.BACKCAR_START";
	public static final String ACTION_BACKCAR_STOP = "com.yecon.action.BACKCAR_STOP";
	//bar top
	private TextView back_IView,back_list_IView,back_audio_IView,bar_top_close;  

	private  FboxPopuWindow mFboxPopu=null;
	private MusicAdapterNew musicAdapterNew;
	private MusicInfo mMusicInfo;
	//player info
	private View player_icon_IView;
	private TextView player_album_TView,player_track_TView,player_artist_TView;
	private TextView player_order_TView;
	//play progress
	private SeekBar play_SeekBar;
	private TextView   play_all_time_TView, play_current_time_TView;
	// control button
	private View play_prev_IView,play_stop_IVew,play_list_IVew,
	play_next_IView,play_playing_IView,play_modrandom_IView,play_replay_IView;
	//init connect
	private	LoadingAsyncTask   mLoadAsyncTask;  
	private final int AsyncTask_Fail=-1;
	private final int AsyncTask_Success=0;
	private CustomProgressDialog progressDialog = null;

	//this is control select to update
	private final static int handler_detail_info = 100;
	private final static int handler_update_tabView = 200;
	private final static int handler_update_image = 300;
	private final static int handler_update_list = 400;
	private final static int handler_update_Repeat = 500;
	private final static int handler_Play = 600;
	
	//auths
	boolean m_biPodInited = false;
	boolean m_bNeedSendEND_FF_REW;
	int m_u4Volume = 0;
	boolean m_bAuthPassed = false;
	ARTWORK_FMTS_INFO_T m_rArtWorkFmtInfo = new ARTWORK_FMTS_INFO_T();
	ARTWORK_FMTS []m_rArtWorkFmts = new ARTWORK_FMTS[8];

	public static boolean IS_QB_POWEROFF = false;
	public static boolean IS_QB_ACCOFF = false;
	public static boolean IS_BACKCAR_START = false;

	// music
	//private List<Track> tracks = new ArrayList<Track>();// tracklist
	//private Track curTrack; // 
	private List<Integer> playList = new ArrayList<Integer>();// 
	private List<String> items = new ArrayList<String>(); // 
	private List<ImageView> liv = new ArrayList<ImageView>(); // 
	private LinearLayout oldll;
	private long mLastTime = 0; 
	private long mCurTime = 0; 


	private Handler handler = new Handler();
	private int increment = 1; 
	private int pro = 0; 


	private int intRepeatNum = 0; // 0-off 2-all_tracks 1-one_track
	private byte[] byteRepeatNum={0x00,0x01,0x02};
	private int intShuffleNum = 0; // 0-off  1-open 2-album
	private byte[] byteShuffleNum={0x00,0x01,0x02};
	private int intCurrentIndex=0;

	private SWI_SINK_TYPE_T ADSPSink; // 0-None 3-Front_Rear 1-Front 2-Rear
	private boolean ADSP = true; // true on false off
	private boolean isFirst = true; // true first false not first comes into
	// this program
	private boolean isDoubleTap = false;
	public static JiPodSDKAgent m_iPodSDKAgent;
	private int m_u1CurDbCategory = 1000;
	private int m_u1SubDbCategory = 0;
	private final int RET_IPOD_OK = 0;
	private final int RET_IPOD_FAIL = 1;
	private final int RET_IPOD_BUSY = 2;
	private final int RET_IPOD_BAD_PARAM = 3;
	private String music = "";
	private int m_u4PlaybackStatus;
	private int m_u4PlaybackStatus_save;
	private int m_u4MaxSliderValue;
	private int m_u4RepeatMode;
	private int m_u4ShuffleMode;
	private int m_dwTrackCount;
	private int m_u4UpdateTimerID;
	private final int DB_SUB_TYPE_GENRE_GENRE = 1;
	private final int DB_SUB_TYPE_GENRE_ARTIST = 2;
	private final int DB_SUB_TYPE_GENRE_ALBUM = 3;
	private final int DB_SUB_TYPE_GENRE_TRACK = 4;
	private final int DB_SUB_TYPE_PLAYLIST_PLAYLIST = 1;
	private final int DB_SUB_TYPE_PLAYLIST_TRACK = 2;
	private final int DB_SUB_TYPE_ARTIST_ARTIST = 1;
	private final int DB_SUB_TYPE_ARTIST_ALBUM = 2;
	private final int DB_SUB_TYPE_ARTIST_TRACK = 3;
	private final int DB_SUB_TYPE_ALBUM_ALBUM = 1;
	private final int DB_SUB_TYPE_ALBUM_TRACK = 2;
	private final int DB_SUB_TYPE_TRACK_TRACK = 1;
	private final int DB_SUB_TYPE_AUDIOBOOK_TRACK = 1;
	private final int DB_SUB_TYPE_PODCAST_PODCAST = 1;
	private final int DB_SUB_TYPE_PODCAST_TRACK = 2;
	private final int DB_SUB_TYPE_COMPOSER_COMPOSER = 1;
	private final int DB_SUB_TYPE_COMPOSER_TRACK = 2;
	private byte m_pbTrackArtworkData[];
	private int m_u4ArtworkDataSize = 0;
	private long g_dwInterval = 0;
	private int m_u4MaxArtworkHeight = 0;
	//add the receiver
	private	QBReceiver mQBReceiver = null;

	NOTIFY_INFO_T rIPodNotify;
	STATUS_E ePodStatus = STATUS_E.PLUG_IN;
	//tab
	boolean m_bNeedUpdateTrackCount;
	boolean m_bConnected = false;
	Handler myHandler = new Handler(){ 
		public void handleMessage(Message msg){
			//L.v("myHandler_______"+msg.what);
			if(msg.what == handler_detail_info){// update display tab
				try{
					//initDisplayTab(m_u4PlaybackTrackIndex);
					if(mFboxPopu!=null&&mFboxPopu.isShowing())mFboxPopu.dismiss();
					initDisplayTab();
				}catch(Exception e){
					e.printStackTrace();
				}
				return;
			}else if(msg.what == handler_update_tabView){// update audio tab
				View v = (View)msg.obj;
				selectTypePlay(MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_PLAYLIST
						,DB_SUB_TYPE_PLAYLIST_TRACK);
				return;
			}else if(msg.what == handler_update_image){//update image
				HandleTrackArtwork();
				return;
			}else if(msg.what == handler_update_list){//update sublist
				int arr[] = (int[])msg.obj;
				UpdateListCtrl(arr[0],arr[1],arr[2]);
				return;
			}else if(msg.what == handler_update_Repeat){
				//SetRepeatMode(intShuffleNum,intRepeatNum);
				SetRepeatMode(intRepeatNum);
				SetShuffleMode(intShuffleNum);
				return;
			}
			else if(msg.what == handler_Play)
			{
				try {
					m_iPodSDKAgent.m_pfnPlayControl(MUSB_IPOD_PLAY_CTRL_CODE_E.PLAY_CTRL_PLAY.ordinal());
				} catch (IPodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}

			NOTIFY_INFO_T rIPodNotify = (NOTIFY_INFO_T)msg.obj;
			switch(((NOTIFY_INFO_T)(rIPodNotify)).eNotifyType){
			case AUTH_STATE://0x00
				Log.d("IPodEventCallback","-------AUTH_STATE---------");
				//bar_top_scan.setText(R.string.back_connet);
				//	int u4TrackIndex2 = ((PB_NOTIFY_STATUS_INF_T)(rIPodNotify.rNotify)).trackIndex;
				if (mLoadAsyncTask != null && mLoadAsyncTask.getStatus() != AsyncTask.Status.FINISHED)
				{
					mLoadAsyncTask.cancel(true);
				}
				mLoadAsyncTask = new LoadingAsyncTask();
				mLoadAsyncTask.execute(1);

				break;
			case PB_STATUS://0x01
				if(((PB_NOTIFY_STATUS_INF_T)(rIPodNotify.rNotify)).trackSecOffsetValid){
					int u4TrackSecOffset = ((PB_NOTIFY_STATUS_INF_T)(rIPodNotify.rNotify)).trackSecOffset;
					play_SeekBar.setProgress(u4TrackSecOffset*1000);
				}
				if(((PB_NOTIFY_STATUS_INF_T)(rIPodNotify.rNotify)).trackIndexValid){
					int u4TrackIndex = ((PB_NOTIFY_STATUS_INF_T)(rIPodNotify.rNotify)).trackIndex;
					//int u4TrackIndex5 = ((PB_NOTIFY_STATUS_INF_T)(rIPodNotify.rNotify)).chapterIndex;
					try{
						if(u4TrackIndex==-1){
							u4TrackIndex=0;
						}
						int dwTrackCount = 0;
						dwTrackCount = m_iPodSDKAgent.m_pfnGetNumPlayingTracks(dwTrackCount);
						L.e(u4TrackIndex+"--------track index valid-------"+dwTrackCount);
						player_order_TView.setText((u4TrackIndex+1)+"/"+dwTrackCount);
						HandleTrackIndexChanged(u4TrackIndex);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				if(((PB_NOTIFY_STATUS_INF_T)(rIPodNotify.rNotify)).pbStatuValid != 0){
					Log.e("HANDLER","--------status valid-------");
					PLAY_ST_EX_E eStatus = ((PB_NOTIFY_STATUS_INF_T)(rIPodNotify.rNotify)).eStatus;
					try{
						HandlePlaybackStatusChanged(eStatus.ordinal());
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				if(((PB_NOTIFY_STATUS_INF_T)(rIPodNotify.rNotify)).shuffleStateValid){
					intShuffleNum = ((PB_NOTIFY_STATUS_INF_T)(rIPodNotify.rNotify)).shuffleState;
					SetShuffleMode(intShuffleNum);
				}
				if(((PB_NOTIFY_STATUS_INF_T)(rIPodNotify.rNotify)).repeatStateValid){
					intRepeatNum = ((PB_NOTIFY_STATUS_INF_T)(rIPodNotify.rNotify)).repeatState;
					SetRepeatMode(intRepeatNum);
				}
				//if(((PB_NOTIFY_STATUS_INF_T)(rIPodNotify.rNotify)).trackUIDValid){
				//Log.e("HANDLER","--------UID valid-------");
				//}
				//else {
				//
				//}
				break;
			case DIGITALAUIDO_CFG:  //digitalauido
				Log.i("IPodEventCallback","--------DIGITALAUIDO_CFG---------------");
				break;
			case STATUS_NOTIFY:
				Log.e("IPodEventCallback","-----------------STATUS_NOTIFY---------------");
				if(((STATUS_T)(rIPodNotify.rNotify)).iPodStatus == STATUS_E.PLUG_IN){
					ePodStatus = STATUS_E.PLUG_IN;
					Log.i("IPodEventCallback","--------PLUG_IN---------------");
					// if (mLoadAsyncTask != null && mLoadAsyncTask.getStatus() != AsyncTask.Status.FINISHED)
					//{mLoadAsyncTask.cancel(true);}
					//mLoadAsyncTask=new LoadingAsyncTask();
					//mLoadAsyncTask.execute(1);
					//bar_top_scan.setText(R.string.back_conneting);
										
				}else if(((STATUS_T)(rIPodNotify.rNotify)).iPodStatus == STATUS_E.PLUG_OUT){
					Log.i("IPodEventCallback","--------PLUG_OUT---------------");
					ePodStatus = STATUS_E.PLUG_OUT;
					HandleDisConnect();
					if(!IS_QB_POWEROFF&&!IS_QB_ACCOFF)
					{
						finish(); 
					}

				}else{
					Log.e("HANDLER","-------:"+((STATUS_T)(rIPodNotify.rNotify)).iPodStatus.ordinal());
				}
				break;
			}
		}
	};
	private Object sourceTocken;
	private AudioManager mAudioManager;
	private boolean isPausedByAudioFocus = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sourceTocken = SourceManager.registerSourceListener(this, SourceManager.SRC_NO.ipod);
		 SourceManager.setAudioFocusNotify(sourceTocken, new SourceManager.AudioFocusNotify() {
				
				@Override
				public void onAudioFocusChange(int arg0) {
					// TODO Auto-generated method stub
					Log.i(TAG, "onAudioFocusChange:" +arg0 );
					switch (arg0) {
	                case AudioManager.AUDIOFOCUS_LOSS:
	                	finish();
	                	break;
	                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: 
	                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
	                	isPausedByAudioFocus = doPausePlay(false);
	                    break;
	                case AudioManager.AUDIOFOCUS_GAIN: 
	                	if(isPausedByAudioFocus){
	                		doPausePlay(true);
	                		isPausedByAudioFocus = false;
	                	}	                	
	                    break;
					}
					
				}
			});
		
		m_rArtWorkFmts = new ARTWORK_FMTS[8]; 
		m_rArtWorkFmtInfo.prArtworkFmt = m_rArtWorkFmts;
//		final WallpaperManager wallpaperManager = WallpaperManager
//				.getInstance(this);
//		final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
//		this.getWindow().setBackgroundDrawable(wallpaperDrawable);
		setContentView(R.layout.activity_main);
		initView();	
		initData();
		
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		if (mLoadAsyncTask != null && mLoadAsyncTask.getStatus() != AsyncTask.Status.FINISHED)
		{
			mLoadAsyncTask.cancel(true);
		}
		mLoadAsyncTask=new LoadingAsyncTask();
		mLoadAsyncTask.execute(1);
	}
	
	private void initView(){
		//player info
		player_icon_IView=(View)findViewById(R.id.player_icon);
		//player_icon_IView.setOnClickListener(this);
		player_album_TView=(MarqueeText)findViewById(R.id.player_album);
		//player_album_TView.setOnClickListener(this);
		player_track_TView=(MarqueeText)findViewById(R.id.player_track);
		//player_track_TView.setOnClickListener(this);
		player_artist_TView=(MarqueeText)findViewById(R.id.player_artist);
		//player_artist_TView.setOnClickListener(this);
		player_order_TView=(TextView)findViewById(R.id.player_order);
		//music_list_select_LView=(ListView)findViewById(R.id.music_list_select);
		//music_list_select_LView.setOnItemClickListener(this);
		//play prgressbar
		play_SeekBar=(SeekBar)findViewById(R.id.play_seekBar);
		play_current_time_TView=(TextView)findViewById(R.id.play_current_time);
		play_all_time_TView=(TextView)findViewById(R.id.play_all_time);
		//control button
		play_prev_IView=(View)findViewById(R.id.play_prev_IView);
		play_prev_IView.setOnClickListener(this);
		play_next_IView=(View)findViewById(R.id.play_next_IView);
		play_next_IView.setOnClickListener(this);
		play_playing_IView=(View)findViewById(R.id.play_playing_IView);
		play_playing_IView.setOnClickListener(this);
		play_modrandom_IView=(View)findViewById(R.id.play_modrandom_IView);
		play_modrandom_IView.setOnClickListener(this);
		play_replay_IView=(View)findViewById(R.id.play_replay_IView);
		play_replay_IView.setOnClickListener(this);
		play_stop_IVew = null;//(View)findViewById(R.id.play_stop_IView);
		if(play_stop_IVew!=null)
			play_stop_IVew.setOnClickListener(this);
		play_list_IVew = (View)findViewById(R.id.play_list_IView);
		play_list_IVew.setOnClickListener(this);
		findViewById(R.id.iv_control_audio).setOnClickListener(this);
		
		play_SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				int pro = play_SeekBar.getProgress();
				try{
					m_iPodSDKAgent.m_pfnSeek(pro);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if(m_bConnected == false) return;
				int pro = play_SeekBar.getProgress();
				String s = "";
				s = new SimpleDateFormat("mm:ss").format(new Date(
						pro + 0));
				play_current_time_TView.setText(s+"");

			}
		});	

		mFboxPopu=new FboxPopuWindow(this);
		mFboxPopu.setPopuListener(this);
		mFboxPopu.setContentView(R.layout.list_view);
		mFboxPopu.getListView().setOnItemClickListener(this);
		//mFboxPopu.setListen(this);
	}
	
	public void initData(){
		initIntentReceiver();
		musicAdapterNew = new MusicAdapterNew(this);
		musicAdapterNew.clear();
		//
		//music_list_select_LView.setAdapter(musicAdapterNew);
		mFboxPopu.getListView().setAdapter(musicAdapterNew);
		g_dwInterval = System.currentTimeMillis();	
		//mCbManager.setOnActionListener(mCBMListener);
	}
	
	private boolean doPausePlay(boolean play){
		boolean ret = false;
		if(m_iPodSDKAgent==null)
			return false;
		PLAY_ST_INFO_T rPbStatus = new PLAY_ST_INFO_T(); 		
		try{
			rPbStatus = m_iPodSDKAgent.m_pfnGetPlayStatus(rPbStatus);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		if(play)
		{
			if(rPbStatus.ePbStatus != PLAY_ST_EX_E.PB_ST_EX_PLAY_0X01){
				try {
					ret = true;
					m_iPodSDKAgent.m_pfnPlayControl(MUSB_IPOD_PLAY_CTRL_CODE_E.PLAY_CTRL_PLAY.ordinal());
				} catch (IPodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}
		else
		{
			if(rPbStatus.ePbStatus != PLAY_ST_EX_E.PB_ST_EX_PAUSE_0X02
					&& rPbStatus.ePbStatus != PLAY_ST_EX_E.PB_ST_EX_STOP_0X00){
				try {
					ret = true;
					m_iPodSDKAgent.m_pfnPlayControl(MUSB_IPOD_PLAY_CTRL_CODE_E.PLAY_CTRL_PAUSE.ordinal());
				} catch (IPodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}
		return ret;
	}

	@Override
	public void onClick(View mView) {
		int imageId=0, stringId=0;
		int id =mView.getId();
		switch (id) {
		case R.id.iv_control_audio:
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setComponent(new ComponentName(SOUND_SETTING_PACKAGE_NAME,
                    SOUND_SETTING_STARTUP_ACTIVITY));
            startActivity(intent);
			break;
		case R.id.play_list_IView:
			openFbox();
			break;
		case R.id.play_prev_IView:
			try {
				int dwTrackCount = 0;
				dwTrackCount = m_iPodSDKAgent.m_pfnGetNumPlayingTracks(dwTrackCount);
				if(intCurrentIndex==0 && intRepeatNum!=2){
					return ;
				}

				m_iPodSDKAgent
				.m_pfnPlayControl(MUSB_IPOD_PLAY_CTRL_CODE_E.PLAY_CTRL_PRE_TRACK
						.ordinal());
			} catch (Exception e) {
				Log.e("mylistener","------error------playControl");
				e.printStackTrace();
			}
			
			break;
		case R.id.play_next_IView:// when the next is >  count 
			try{
				int dwTrackCount = 0;
				dwTrackCount = m_iPodSDKAgent.m_pfnGetNumPlayingTracks(dwTrackCount);
				if(intCurrentIndex==dwTrackCount-1 && intRepeatNum!=2){
					return ;
				}
				m_iPodSDKAgent
				.m_pfnPlayControl(MUSB_IPOD_PLAY_CTRL_CODE_E.PLAY_CTRL_NEXT_TRACK
						.ordinal());

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.play_playing_IView:
			
			PLAY_ST_INFO_T rPbStatus = new PLAY_ST_INFO_T(); 		
			try{
				rPbStatus = m_iPodSDKAgent.m_pfnGetPlayStatus(rPbStatus);
			} catch(Exception e){
				e.printStackTrace();
			}
			
			if(rPbStatus.ePbStatus != PLAY_ST_EX_E.PB_ST_EX_PLAY_0X01)
			{
				try {
					m_iPodSDKAgent.m_pfnPlayControl(MUSB_IPOD_PLAY_CTRL_CODE_E.PLAY_CTRL_PLAY.ordinal());
				} catch (IPodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				try {
					m_iPodSDKAgent.m_pfnPlayControl(MUSB_IPOD_PLAY_CTRL_CODE_E.PLAY_CTRL_PAUSE.ordinal());
				} catch (IPodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
//		case R.id.play_stop_IView:
//			PLAY_ST_INFO_T rPbStatus1 = new PLAY_ST_INFO_T(); 		
//			try{
//				rPbStatus = m_iPodSDKAgent.m_pfnGetPlayStatus(rPbStatus1);
//			} catch(Exception e){
//				e.printStackTrace();
//			}
//			
//			if(rPbStatus1.ePbStatus != PLAY_ST_EX_E.PB_ST_EX_STOP_0X00)
//			{
//				try {
//					m_iPodSDKAgent.m_pfnPlayControl(MUSB_IPOD_PLAY_CTRL_CODE_E.PLAY_CTRL_STOP.ordinal());
//				} catch (IPodException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			break;
		case R.id.play_modrandom_IView:

			intShuffleNum = (intShuffleNum + 1) % 2;	
			intRepeatNum = 0;
			SetShuffleMode(intShuffleNum);
			SetRepeatMode(intRepeatNum);
			try {
				m_iPodSDKAgent.m_pfnSetRepeatMode(intRepeatNum, true);
				m_iPodSDKAgent.m_pfnSetShuffleMode(intShuffleNum, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.play_replay_IView:

			intRepeatNum = (intRepeatNum + 1) % 3;
			intShuffleNum   = 0;
			SetShuffleMode(intShuffleNum);
			SetRepeatMode(intRepeatNum);
			try {
				m_iPodSDKAgent.m_pfnSetRepeatMode(intRepeatNum, true);
				m_iPodSDKAgent.m_pfnSetShuffleMode(intShuffleNum, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			break;
		default:
			break;
		}
	}
	
	
	void SetShuffleMode(int isShuffle)
	{
		int imageId = 0, stringId = 0;
//		if (isShuffle == 0)
//			tvShuffle.setText(getResources().getString(R.string.Off));
//		else if (isShuffle == 2)
//			tvShuffle.setText(getResources().getString(R.string.Album));
//		else if (isShuffle == 1)
//			tvShuffle.setText(getResources().getString(R.string.Tracks));
		
		if (intShuffleNum == 0){
			imageId = R.xml.selector_randomclose_state;
			stringId = R.string.str_btn_rand_off;
		}
		else if (intShuffleNum == 2){
			imageId = R.xml.selector_random_state;
			stringId = R.string.str_btn_rand;
		}
		else if (intShuffleNum == 1){
			imageId = R.xml.selector_random_state;
			stringId = R.string.str_btn_rand;
		}		
		if(play_modrandom_IView instanceof ImageView){
			((ImageView)play_modrandom_IView).setImageResource(imageId);
		}
		else if(play_modrandom_IView instanceof TextView){
			((TextView)play_modrandom_IView).setCompoundDrawablesWithIntrinsicBounds(0, imageId, 0, 0);
			((TextView)play_modrandom_IView).setText(stringId);
		}
	}
	
	void SetRepeatMode(int u1RepeatMode)
	{
		int imageId = 0, stringId = 0;
		if (intRepeatNum == 0){
			imageId = R.xml.selector_repeat_close_state;
			stringId = R.string.str_btn_loop_off;
		}
		else if (intRepeatNum == 2){
			imageId = R.xml.selector_repeatall_state;
			stringId = R.string.str_btn_loop;
		}
		else if (intRepeatNum == 1){
			imageId = R.xml.selector_repeatone_state;
			stringId = R.string.str_btn_loop_current;
		}		
		if(play_replay_IView instanceof ImageView){
			((ImageView)play_replay_IView).setImageResource(imageId);
		}
		else if(play_replay_IView instanceof TextView){
			((TextView)play_replay_IView).setCompoundDrawablesWithIntrinsicBounds(0, imageId, 0, 0);
			((TextView)play_replay_IView).setText(stringId);
		}
	}

	private void openFbox() {
		selectTypePlay(MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ARTIST
				,DB_SUB_TYPE_ARTIST_ARTIST);
//		selectTypePlay(MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_TRACK
//				,DB_SUB_TYPE_TRACK_TRACK); 
		//mFboxPopu.showPopu(findViewById(R.id.bar_top_audio));
		mFboxPopu.showPopu(findViewById(R.id.layout_main));
	}
	
	
	private boolean checkBtnInterval(int n) {
		long count = System.currentTimeMillis() - g_dwInterval;
		if (count < n)
			return false;
		return true;
	}
	
	public void UpdateListCtrl(int u1SelectDbCategory, int u1QueryDbCategory,
			int nRecIndex) {
		int i = 0;
		int u4NumRecords = 0;
		int u4StartRecIdx = 0;
		int u4RecCnt = 1;
		DB_RECITEM_T rRecItem = new DB_RECITEM_T();
		String szRecord;
		musicAdapterNew.clear();

		try{
			m_iPodSDKAgent.m_pfnSelectDBRecord(u1SelectDbCategory, nRecIndex);
		}catch(Exception e){
			e.printStackTrace();
		}
		try {
			u4NumRecords = m_iPodSDKAgent.m_pfnGetNumberCategorizedDBRecords(u1QueryDbCategory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (i = 0; i < u4NumRecords; i++) {
			u4StartRecIdx = i;
			try{
				rRecItem = (DB_RECITEM_T)(m_iPodSDKAgent.m_pfnRetrieveCategorizedDBRecords
						(u1QueryDbCategory,u4StartRecIdx, u4RecCnt, rRecItem));
				mMusicInfo=musicAdapterNew.getMusicInfo();
				mMusicInfo.setsName(new String(rRecItem.szRecInfo));
				mMusicInfo.setsNum(i+"");
				musicAdapterNew.addItem(mMusicInfo);
			}catch(Exception e){
				Log.e("updateListCtrl","------error------retireveCategorizedDBRecords");
				e.printStackTrace();
			}
		}
		
		musicAdapterNew.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/**
	 * add the broadCast  for qb_power
	 * 
	 * */

	private void initIntentReceiver() {

		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_QB_POWERON);
		filter.addAction(ACTION_QB_POWEROFF);
        filter.addAction(ACTION_QB_PREPOWEROFF);
		filter.addAction(MCU_ACTION_ACC_ON);
		filter.addAction(MCU_ACTION_ACC_OFF);
		filter.addAction(ACTION_BACKCAR_START);
		filter.addAction(ACTION_BACKCAR_STOP);
		
		mQBReceiver = new QBReceiver(); 
		registerReceiver(mQBReceiver, filter);
		Log.d("init-receiver", "RECEIVE--initReceiver ...");
	}


	private void deinitReceiver(){
		unregisterReceiver(mQBReceiver);
		Log.d("deinit-receiver", "RECEIVE--deinitReceiver ...");
	}
	
	private class QBReceiver extends BroadcastReceiver {
		private String LOG_TAG = "iPodReceiver";
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(ACTION_QB_POWERON.equals(action) || MCU_ACTION_ACC_ON.equals(action)) {
                if (action.equals(ACTION_QB_POWERON)) {
                	Log.i(LOG_TAG, "iPodReceiver Power On");
                    IS_QB_POWEROFF = false;
                } else if (action.equals(MCU_ACTION_ACC_ON)) {
                	Log.i(LOG_TAG, "iPodReceiver Acc On");
                	IS_QB_ACCOFF = false;
                }
				
				if(ePodStatus == STATUS_E.PLUG_IN)
				{
					if (mLoadAsyncTask != null && mLoadAsyncTask.getStatus() != AsyncTask.Status.FINISHED)
					{
						mLoadAsyncTask.cancel(true);
					}
					mLoadAsyncTask=new LoadingAsyncTask();
					mLoadAsyncTask.execute(1);	
				}
				else
				{
					finish();
				}
			} else if (MCU_ACTION_ACC_OFF.equals(action) ) {
                if (action.equals(ACTION_QB_POWEROFF) || action.equals(ACTION_QB_PREPOWEROFF)) {
                	Log.i(LOG_TAG, "iPodReceiver Power Off");
                    IS_QB_POWEROFF = true;
                } else if (action.equals(MCU_ACTION_ACC_OFF)) {
                	Log.i(LOG_TAG, "iPodReceiver Acc Off");
                	IS_QB_ACCOFF = true;
                }
			
				 if(m_iPodSDKAgent != null)
				 {
					   m_u4PlaybackStatus_save = m_u4PlaybackStatus;
					   L.e("lzy..................................m_u4PlaybackStatus_accoff = "+m_u4PlaybackStatus_save);
					   HandleDisConnect();
					   m_biPodInited = false;
					   m_bAuthPassed = false;				 
				 }
			} else if (action.equals(ACTION_QB_POWEROFF) || ACTION_QB_PREPOWEROFF.equals(action)) {
			    finish();
			} else if(ACTION_BACKCAR_START.equals(action)){
				IS_BACKCAR_START = true;
			} else if(ACTION_BACKCAR_STOP.equals(action)){
				IS_BACKCAR_START = false;
			}
		}
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		if (mLoadAsyncTask != null && mLoadAsyncTask.getStatus() != AsyncTask.Status.FINISHED)
		{
			mLoadAsyncTask.cancel(true);
		}
		HandleDisConnect();
		deinitReceiver();

		Log.e("DESTORY","===========destory===========");
		//mylistener.onClick(btnDisconnect);
		SourceManager.unregisterSourceListener(sourceTocken);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SourceManager.acquireSource(sourceTocken);
	}
	
	private class IPodEventCallback implements EventCallback{
		public void onEvent(NOTIFY_INFO_T rIPodNotify) {
			Message message = Message.obtain();
			message.what = rIPodNotify.eNotifyType.ordinal();
			message.obj = rIPodNotify;
			myHandler.sendMessage(message);
		}
	}

	void HandleDisConnect(){

		if(m_iPodSDKAgent != null)
		{
			PLAY_ST_INFO_T rPbStatus = new PLAY_ST_INFO_T(); 		
			try{
				rPbStatus = m_iPodSDKAgent.m_pfnGetPlayStatus(rPbStatus);
			} catch(Exception e){
				e.printStackTrace();
			}
			
			if(rPbStatus.ePbStatus != PLAY_ST_EX_E.PB_ST_EX_STOP_0X00)
			{
				try{
					m_iPodSDKAgent.m_pfnPlayControl(MUSB_IPOD_PLAY_CTRL_CODE_E.PLAY_CTRL_PAUSE.ordinal());
				} catch (IPodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				m_iPodSDKAgent.m_pfnStopPlayAudio();
			}
			m_iPodSDKAgent.m_pfnIPOD_ReleaseCBM();
			m_iPodSDKAgent.m_pfnIPOD_DeInit();	
			
			m_iPodSDKAgent = null;
		}
		
		//finish(); 
	}

	void HandlePlaybackPositionChanged(int u4Offset){
		int u4MsPosition = u4Offset * 1000;
		if(u4MsPosition > m_u4MaxSliderValue){
			u4MsPosition = m_u4MaxSliderValue;
		}
		String s = "";
		s = new SimpleDateFormat("mm:ss").format(new Date(u4MsPosition + 0));
		play_current_time_TView.setText(s+"");
	}

	boolean HandleTrackArtwork(){
		int i = 0;
		int j = 0;

		int u4ArtworkCount = 0;
		ARTWORK_CNT_T rArtWorkCnt[] = new ARTWORK_CNT_T[8];
		ARTWORK_CNT_INFO_T rArtWorkCntInfo = new ARTWORK_CNT_INFO_T();
		rArtWorkCntInfo.infoNum = 0;
		//rArtWorkCntInfo.artworkCnt = rArtWorkCnt;

		Log.e("artwork","HandleTrackArtwork------start------");

		try{
			rArtWorkCntInfo = (ARTWORK_CNT_INFO_T)
					(m_iPodSDKAgent.m_pfnGetIndexedPlayingTrackInfo(intCurrentIndex,
							0, TRACK_INF_TYPE_E.TRACK_ARTWORK_CNT,rArtWorkCntInfo));

			rArtWorkCnt = rArtWorkCntInfo.artworkCnt;

			for(i=0;i<rArtWorkCntInfo.infoNum;i++){

				u4ArtworkCount += rArtWorkCnt[i].artWorkCnt;
			}

			if(u4ArtworkCount == 0){
				player_icon_IView.setBackgroundResource(R.drawable.icon_null);
				return true;
			}

			int u4FmtsIndex = 0;
			boolean bFound = false;
			for(i=0;i<m_rArtWorkFmtInfo.fmtNum;i++){
				for(j=0;j<rArtWorkCntInfo.infoNum;j++){
					if((rArtWorkCnt[j].formatID == m_rArtWorkFmts[i].formatID) &&
							(rArtWorkCnt[j].artWorkCnt != 0)){
						u4FmtsIndex = i;
						bFound = true;
						break;
					}
				}
				if(bFound) break;
			}
			int u4MsTimePos[] = new int[8];
			ARTWORK_TIMES_T rArtWorkTimes = new ARTWORK_TIMES_T();
			rArtWorkTimes.infoNum = 0;
			rArtWorkTimes.timePos = u4MsTimePos;
			int u2ArtWorkIndex = 0;
			int u2ArtWorkCount = 8;
			int u2FmtID = m_rArtWorkFmts[u4FmtsIndex].formatID;

			ARTWORK_DATA_T rArtWorkData = new ARTWORK_DATA_T();
			try{
				m_iPodSDKAgent.m_pfnGetTrackArtWorkTimes(intCurrentIndex, u2FmtID, u2ArtWorkIndex, u2ArtWorkCount, rArtWorkTimes);
				if(rArtWorkTimes.infoNum == 0){
					//ShowPICForNoArtwork();
					return true;
				}else{
					for(j=0;j<rArtWorkTimes.infoNum;j++){
						m_u4ArtworkDataSize = m_rArtWorkFmts[u4FmtsIndex].imgHeight * m_rArtWorkFmts[u4FmtsIndex].imgWidth * 2;
						m_pbTrackArtworkData = new byte[m_u4ArtworkDataSize];	
						rArtWorkData.pvData = m_pbTrackArtworkData;
						rArtWorkData.dataBufSize = m_u4ArtworkDataSize;
						Bitmap bitmap=null;
						try{
							m_iPodSDKAgent.m_pfnGetTrackArtWorkData(intCurrentIndex, u2FmtID, u4MsTimePos[j], rArtWorkData);
							bitmap = Bitmap.createBitmap(rArtWorkData.imgWidth,rArtWorkData.imgHeight,Bitmap.Config.RGB_565);
							ByteBuffer byteBuff = ByteBuffer.allocate(rArtWorkData.pvData.length);
							byteBuff.put(rArtWorkData.pvData);
							byteBuff.position(0);
							bitmap.copyPixelsFromBuffer(byteBuff);
							player_icon_IView.setBackgroundDrawable(new BitmapDrawable(bitmap));
							//player_icon_IView.
							//bitmap.recycle();
							return true;
						}catch(Exception e){
							Log.e("artwork","------error------getTrackArtWorkData");
							e.printStackTrace();
							if(bitmap!=null)
								bitmap.recycle();	
						}
					}

				}  
			}catch(Exception e){
				Log.e("artwork","------error------getTrackArtworTimes");
				e.printStackTrace();

			}
		}catch(Exception e){
			Log.e("artwork","------error------getIndexedPlayingTrackInfo");	
			e.printStackTrace();
		}

		return false;
	}
	
	void HandleTrackIndexChanged(int u4TrackIndex) throws Exception{
		musicAdapterNew.setSelectedPos(u4TrackIndex);
		musicAdapterNew.notifyDataSetChanged();
		play_SeekBar.setProgress(0);
		play_current_time_TView.setText("00:00");
		play_all_time_TView.setText("00:00");
		m_u4MaxSliderValue = 0;

		PLAY_ST_INFO_T rPbStatus = new PLAY_ST_INFO_T(); 
		try{
			rPbStatus = m_iPodSDKAgent.m_pfnGetPlayStatus(rPbStatus);
			//u4TrackIndex = rPbStatus.trackIdx;
		} catch(Exception e){
			e.printStackTrace();
		}
		if(u4TrackIndex == -1 && rPbStatus.ePbStatus != PLAY_ST_EX_E.PB_ST_EX_STOP_0X00) {
			Log.e("INDEX","-----------frist track!---------------");
			u4TrackIndex = 0;
		}else if (rPbStatus.ePbStatus == PLAY_ST_EX_E.PB_ST_EX_STOP_0X00) {
			Log.e("INDEX","-----------stop!---------------");
			return;
		}
		intCurrentIndex = u4TrackIndex;

		//tvCounter.setText(dwTrackCount+"/"+u4TrackIndex);	
		//player_order_TView.setText((intCurrentIndex+1)+"/"+dwTrackCount);
		String szCurTrackTitle = "";
		String szCurTrackArtist = "";
		String szCurTrackAlbum = "";
		TRACK_GENRE_INFO_T szCurTrackGenre = new TRACK_GENRE_INFO_T();

		TRACK_CAP_INF_T rTrackCapInfo = new TRACK_CAP_INF_T();

		rTrackCapInfo = (TRACK_CAP_INF_T)(m_iPodSDKAgent.m_pfnGetIndexedPlayingTrackInfo(
				intCurrentIndex,0,TRACK_INF_TYPE_E.TRACK_CAP_INF,rTrackCapInfo));
		play_SeekBar.setMax((int)rTrackCapInfo.timeLen);
		String s = ""; 
		s = new SimpleDateFormat("mm:ss").format(new Date( rTrackCapInfo.timeLen + 0));
		play_all_time_TView.setText(s);
		m_u4MaxSliderValue = rTrackCapInfo.timeLen;
		szCurTrackTitle = m_iPodSDKAgent.m_pfnGetIndexedPlayingTrackTitle(intCurrentIndex,
				szCurTrackTitle);

		player_track_TView.setText(szCurTrackTitle);
		//ma.setSelectedPos(szCurTrackTitle);
		//ma.notifyDataSetChanged();
		szCurTrackArtist = m_iPodSDKAgent.m_pfnGetIndexedPlayingTrackArtist(intCurrentIndex, szCurTrackArtist);
		player_artist_TView.setText(szCurTrackArtist);

		szCurTrackAlbum = m_iPodSDKAgent.m_pfnGetIndexedPlayingTrackAlbum(intCurrentIndex, szCurTrackAlbum);
		player_album_TView.setText(szCurTrackAlbum);
		//Log.e("TIME","1==============="+szCurTrackAlbum+"==================");

		szCurTrackGenre = (TRACK_GENRE_INFO_T)(m_iPodSDKAgent.m_pfnGetIndexedPlayingTrackInfo(intCurrentIndex, 0,TRACK_INF_TYPE_E.TRACK_GENRE, szCurTrackGenre));

		//m_iPodSDKAgent.m_pfnPlayCurrentSelection(intCurrentIndex);
		//m_iPodSDKAgent.m_pfnStartPlayAudio();


		if(szCurTrackGenre.title != null){
			//player_order_TView.setText(new String(szCurTrackGenre.title));
		}

		Message message = Message.obtain();
		message.what = handler_update_image;
		myHandler.sendMessage(message);
	}

	void HandlePlaybackStatusChanged(int u4Status){
		if(m_u4PlaybackStatus == u4Status|| m_bConnected == false){
			return ;
		}
		
		int imageId = 0;
		int stringId = 0;
		if(u4Status == PLAY_ST_EX_E.PB_ST_EX_STOP_0X00.ordinal()){
			imageId = R.xml.selector_play_state;
			stringId = R.string.str_btn_play;
		}else if(u4Status == PLAY_ST_EX_E.PB_ST_EX_PLAY_0X01.ordinal()){
			imageId = R.xml.selector_pause_state;
			stringId = R.string.str_btn_pause;
		}else if(u4Status == PLAY_ST_EX_E.PB_ST_EX_PAUSE_0X02.ordinal()){
			imageId = R.xml.selector_play_state;
			stringId = R.string.str_btn_play;
		}else if(u4Status == PLAY_ST_EX_E.PB_ST_EX_PF_0X03.ordinal()){
			imageId = R.xml.selector_pause_state;
			stringId = R.string.str_btn_pause;
		}else if(u4Status == PLAY_ST_EX_E.PB_ST_EX_FR_0X04.ordinal()){
			imageId = R.xml.selector_pause_state;
			stringId = R.string.str_btn_pause;
		}else if(u4Status == PLAY_ST_EX_E.PB_ST_EX_REWIND_0X05.ordinal()){
			imageId = R.xml.selector_pause_state;
			stringId = R.string.str_btn_pause;
		}
		else{
			imageId = R.xml.selector_play_state;
			stringId = R.string.str_btn_play;
		}
		
		if(play_playing_IView instanceof ImageView){
			((ImageView)play_playing_IView).setImageResource(imageId);
		}
		else if(play_playing_IView instanceof TextView){
			((TextView)play_playing_IView).setCompoundDrawablesWithIntrinsicBounds(0, imageId, 0, 0);
			((TextView)play_playing_IView).setText(stringId);
		}		
		m_u4PlaybackStatus = u4Status;
	}

	void HandleiPodStatusChanged(int u4IPodStatus){
		if(u4IPodStatus == STATUS_E.PLUG_IN.ordinal()){
			m_bAuthPassed = false;
			if(m_u4UpdateTimerID == 0){
			}
		} else if(u4IPodStatus == STATUS_E.PLUG_OUT.ordinal()){
			m_biPodInited = false;
			m_bAuthPassed = false;
		}
	}
	/*
	 *       <td>0x00</td>
	 *       <td>Shuffle off</td>
	 *   </tr>
	 *   <tr>
	 *       <td>0x01</td>
	 *       <td>Shuffle tracks</td>
	 *   </tr>
	 *   <tr>
	 *       <td>0x02</td>
	 *       <td>Shuffle albums</td>
	 */

	boolean GetiPodArtworkFormats(){
		try{
			m_iPodSDKAgent.m_pfnGetArtWorkFormats(m_rArtWorkFmtInfo);
			ARTWORK_FMTS rTempArtWorkFmts = new ARTWORK_FMTS();
			int i,j;
			for(i=0;i<m_rArtWorkFmtInfo.fmtNum;i++){
				for(j=i+1;j<m_rArtWorkFmtInfo.fmtNum;j++){
					if(m_rArtWorkFmts[i].imgWidth<m_rArtWorkFmts[j].imgWidth){
						rTempArtWorkFmts.pixFormat = m_rArtWorkFmts[i].pixFormat;
						rTempArtWorkFmts.formatID = m_rArtWorkFmts[i].formatID;
						rTempArtWorkFmts.imgWidth = m_rArtWorkFmts[i].imgWidth;
						rTempArtWorkFmts.imgHeight = m_rArtWorkFmts[i].imgHeight;
						m_rArtWorkFmts[i].pixFormat = m_rArtWorkFmts[j].pixFormat;
						m_rArtWorkFmts[i].formatID = m_rArtWorkFmts[j].formatID;
						m_rArtWorkFmts[i].imgWidth = m_rArtWorkFmts[j].imgWidth;
						m_rArtWorkFmts[i].imgHeight = m_rArtWorkFmts[j].imgHeight;
						m_rArtWorkFmts[j].pixFormat = rTempArtWorkFmts.pixFormat;
						m_rArtWorkFmts[j].formatID = rTempArtWorkFmts.formatID;
						m_rArtWorkFmts[j].imgWidth = rTempArtWorkFmts.imgWidth;
						m_rArtWorkFmts[j].imgHeight = rTempArtWorkFmts.imgHeight;
					}
				}
			}
			m_u4MaxArtworkHeight = m_rArtWorkFmts[0].imgHeight;
		}catch(Exception e){

		}
		return true;
	}

	void HandleAuthDone(int u4AuthState){
		boolean bRetVal = false;
		if(u4AuthState == E_IPOD_AUTHENTICATION_STATUS.IPOD_AUTHENTICATION_PASS.ordinal()){
			if(m_bAuthPassed == true) return;
			int u4CurTrackIndex = 0;
			m_bAuthPassed = true;
//			if(m_biPodInited == false){
//				m_iPodSDKAgent.m_pfnIPOD_Init();
//				m_biPodInited = true;
//			}
			PLAY_ST_INFO_T rPbStatus = null;
			try{
				m_iPodSDKAgent.m_pfnEnterRemoteUIMode();			
				int u4RemoteEventMask = 0x0000818a;
				m_iPodSDKAgent.m_pfnSetRemoteEvent(u4RemoteEventMask);
				rPbStatus = new PLAY_ST_INFO_T();
				m_iPodSDKAgent.m_pfnGetPlayStatus(rPbStatus); 
				u4CurTrackIndex = rPbStatus.trackIdx;
				if(u4CurTrackIndex == -1){
					u4CurTrackIndex = 0;
				} 
				
				GetiPodArtworkFormats();
				m_iPodSDKAgent.m_pfnGetAudioDSP();  //缂侇垵宕电划娲偨鐎圭媭鍤瀉udio闁汇劌瀚繛鍥偨閵婏附缍�梻鍕舵嫹	
				//m_iPodSDKAgent.m_pfnPlayControl(MUSB_IPOD_PLAY_CTRL_CODE_E.PLAY_CTRL_PLAY.ordinal());
				m_iPodSDKAgent.m_pfnStartPlayAudio();
				//by lzy 
				if(!IS_BACKCAR_START && (isFirst || m_u4PlaybackStatus_save == PLAY_ST_EX_E.PB_ST_EX_PLAY_0X01.ordinal()))
				{
					isFirst = false;
					Message message1 = Message.obtain();
					message1.what = handler_Play;
					myHandler.sendMessageDelayed(message1,1000);
				}
				
				//btnADSP.setText(getResources().getString(R.string.ADSPOn));
				
				intShuffleNum = m_iPodSDKAgent.m_pfnGetShuffleSetting(intShuffleNum);
				intRepeatNum = m_iPodSDKAgent.m_pfnGetRepeatSetting(intRepeatNum);
				Message message = Message.obtain();
				message.what = handler_update_Repeat;
				myHandler.sendMessage(message);
				
				int u4Volume = 0;
				u4Volume = m_iPodSDKAgent.m_pfnGetVolume(u4Volume);
				m_iPodSDKAgent.m_pfnSetVolume(u4Volume);
				//int dwTrackCount = 0;
				//dwTrackCount = m_iPodSDKAgent.m_pfnGetNumPlayingTracks(dwTrackCount);

				//the first time ipod has no sound, we have to do this.
				int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                //
			}catch(Exception e){
				L.e("------error------getNumPlayingTracks---"+e);
				e.printStackTrace();
			}			
		}
	}
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int mPosition, long arg3) {
		musicAdapterNew.notifyDataSetChanged();
		if (m_u1CurDbCategory == DB_CATEGORY_TYPE_E.DB_TYPE_TRACK
				.ordinal()) {
			if (m_u1SubDbCategory == DB_SUB_TYPE_TRACK_TRACK) { 
				try {

					m_iPodSDKAgent.m_pfnPlayCurrentSelection(mPosition);

					Message message = Message.obtain();
					message.what = handler_detail_info;
					myHandler.sendMessageDelayed(message,500);
				} catch (Exception e) {
					Log.e("listviewListener","------error------playCurrentSelection");
					e.printStackTrace();
				}
			}
		} else if (m_u1CurDbCategory == DB_CATEGORY_TYPE_E.DB_TYPE_GENRE
				.ordinal()) {
			if (m_u1SubDbCategory == DB_SUB_TYPE_GENRE_GENRE) {
				try {
					//musicAdapterNew.setSelectedPos(-1);
					Message message = Message.obtain();
					message.what = handler_update_list;
					int arr[] = {MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_GENRE .ordinal(),
							MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ARTIST.ordinal(),mPosition};	 
					message.obj = arr;
					myHandler.sendMessageDelayed(message,500);
					m_u1SubDbCategory = DB_SUB_TYPE_GENRE_ARTIST;
				} catch (Exception e) {
					Log.e("listviewListener","------error------playCurrentSelection");
					e.printStackTrace();
				}
			} else if (m_u1SubDbCategory == DB_SUB_TYPE_GENRE_ARTIST) {
				try {
					Message message = Message.obtain();
					message.what = handler_update_list;
					int arr[] = {MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ARTIST.ordinal(),
							MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ALBUM.ordinal(),mPosition};	 
					message.obj = arr;
					myHandler.sendMessageDelayed(message,500);
					m_u1SubDbCategory = DB_SUB_TYPE_GENRE_ALBUM;
				} catch (Exception e) {
					Log.e("listviewListener","------error------playCurrentSelection");
					e.printStackTrace();
				}
			} else if (m_u1SubDbCategory == DB_SUB_TYPE_GENRE_ALBUM) {
				try {
					Message message = Message.obtain();
					message.what = handler_update_list;
					int arr[] = {MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ALBUM.ordinal(),
							MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_TRACK.ordinal(),mPosition};	 
					message.obj = arr;
					myHandler.sendMessageDelayed(message,500);
					m_u1SubDbCategory = DB_SUB_TYPE_GENRE_TRACK;
				} catch (Exception e) {
					Log.e("listviewListener","------error------playCurrentSelection");
					e.printStackTrace();
				}
			} else if (m_u1SubDbCategory == DB_SUB_TYPE_GENRE_TRACK) {
				try {
					m_iPodSDKAgent.m_pfnPlayCurrentSelection(mPosition);
					Message message = Message.obtain();
					message.what = handler_detail_info;
					myHandler.sendMessageDelayed(message,500);
				} catch (Exception e) {

					Log.e("listviewListener","------error------playCurrentSelection");
					e.printStackTrace();
				}
			}
		} else if (m_u1CurDbCategory == MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_PLAYLIST
				.ordinal()) {
			if (m_u1SubDbCategory == DB_SUB_TYPE_PLAYLIST_PLAYLIST) { 
				Message message = Message.obtain();
				message.what = handler_update_list;
				int arr[] = {MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_PLAYLIST.ordinal(),
						MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_TRACK.ordinal(),mPosition};	 
				message.obj = arr;
				myHandler.sendMessageDelayed(message,500);
				m_u1SubDbCategory = DB_SUB_TYPE_PLAYLIST_TRACK;
			} else if (m_u1SubDbCategory == DB_SUB_TYPE_PLAYLIST_TRACK) { 
				try { 
					m_iPodSDKAgent.m_pfnPlayCurrentSelection(mPosition);
					Message message = Message.obtain();
					message.what = handler_detail_info;
					myHandler.sendMessageDelayed(message,500);
				} catch (Exception e) {

					Log.e("listviewListener","------error------playCurrentSelection");
					e.printStackTrace();
				}
			}
		} else if (m_u1CurDbCategory == MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ARTIST
				.ordinal()) {
			L.v("m_u1SubDbCategory&&&&&"+m_u1SubDbCategory+"$$$m_u1CurDbCategory$$"+m_u1CurDbCategory);

			if (m_u1SubDbCategory == DB_SUB_TYPE_ARTIST_ARTIST) {
				Message message = Message.obtain();
				message.what = handler_update_list;
				int arr[] = {MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ARTIST.ordinal(),
						MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ALBUM.ordinal(),mPosition};	 
				message.obj = arr;
				myHandler.sendMessageDelayed(message,500);
				m_u1SubDbCategory = DB_SUB_TYPE_ARTIST_ALBUM;
			} else if (m_u1SubDbCategory == DB_SUB_TYPE_ARTIST_ALBUM) {
				Message message = Message.obtain();
				message.what = handler_update_list;
				int arr[] = {MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ALBUM.ordinal(),
						MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_TRACK.ordinal(),mPosition};	 
				message.obj = arr;
				myHandler.sendMessageDelayed(message,500);
				m_u1SubDbCategory = DB_SUB_TYPE_ARTIST_TRACK;
			} else if (m_u1SubDbCategory == DB_SUB_TYPE_ARTIST_TRACK) {
				try {
					m_iPodSDKAgent.m_pfnPlayCurrentSelection(mPosition);
					Message message = Message.obtain();
					message.what = handler_detail_info;
					myHandler.sendMessageDelayed(message,500);

				} catch (Exception e) {
					Log.e("listviewListener","------error------playCurrentSelection");
					e.printStackTrace();
				}

			}
		} else if (m_u1CurDbCategory == MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ALBUM
				.ordinal()) {
			if (m_u1SubDbCategory == DB_SUB_TYPE_ALBUM_ALBUM) {
				//musicAdapterNew.setSelectedPos(-1);
				Message message = Message.obtain();
				message.what = handler_update_list;
				int arr[] = {MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ALBUM
						.ordinal(),
						MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_TRACK
						.ordinal(),mPosition};	 
				message.obj = arr;
				myHandler.sendMessageDelayed(message,500);
				m_u1SubDbCategory = DB_SUB_TYPE_ALBUM_TRACK;
			} else if (m_u1SubDbCategory == DB_SUB_TYPE_ALBUM_TRACK) {
				try {
					m_iPodSDKAgent.m_pfnPlayCurrentSelection(mPosition);
					Message message = Message.obtain();
					message.what = handler_detail_info;
					myHandler.sendMessageDelayed(message,500);
				} catch (Exception e) {
					Log.e("listviewListener","------error------playCurrentSelection");
					e.printStackTrace();
				}
			}
		} else if (m_u1CurDbCategory == MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_AUDIOBOOK
				.ordinal()) {
			if (m_u1SubDbCategory == DB_SUB_TYPE_AUDIOBOOK_TRACK) {
				try {
					m_iPodSDKAgent.m_pfnPlayCurrentSelection(mPosition);
					Message message = Message.obtain();
					message.what = handler_detail_info;
					myHandler.sendMessageDelayed(message,500);
				} catch (Exception e) {
					Log.e("listviewListener","------error------playCurrentSelection");
					e.printStackTrace();
				}
			}
		} else if (m_u1CurDbCategory == MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_PODCAST
				.ordinal()) {
			if (m_u1SubDbCategory == DB_SUB_TYPE_PODCAST_PODCAST) {//闂佽法鍠愰弸濠氬箯閻戣姤鏅搁柡鍌樺�鐎氾拷				//musicAdapterNew.setSelectedPos(-1);
				Message message = Message.obtain();
				message.what = handler_update_list;
				int arr[] = {MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_PODCAST
						.ordinal(),
						MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_TRACK
						.ordinal(),mPosition};	 
				message.obj = arr;
				myHandler.sendMessageDelayed(message,500);
				m_u1SubDbCategory = DB_SUB_TYPE_PODCAST_TRACK;
			} else if (m_u1SubDbCategory == DB_SUB_TYPE_PODCAST_TRACK) {
				try {
					m_iPodSDKAgent.m_pfnPlayCurrentSelection(mPosition);
					Message message = Message.obtain();
					message.what = handler_detail_info;
					myHandler.sendMessageDelayed(message,500);
				} catch (Exception e) {
					Log.e("listviewListener","------error------playCurrentSelection");
					e.printStackTrace();
				}
			}
		} else if (m_u1CurDbCategory == MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_COMPOSER
				.ordinal()) {
			if (m_u1SubDbCategory == DB_SUB_TYPE_COMPOSER_COMPOSER) {
				//musicAdapterNew.setSelectedPos(-1);
				Message message = Message.obtain();
				message.what = handler_update_list;
				int arr[] = {MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_COMPOSER
						.ordinal(),
						MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_TRACK
						.ordinal(),mPosition};	 
				message.obj = arr;
				myHandler.sendMessageDelayed(message,500);
				m_u1SubDbCategory = DB_SUB_TYPE_COMPOSER_TRACK;
			} else if (m_u1SubDbCategory == DB_SUB_TYPE_COMPOSER_TRACK) {
				try {
					m_iPodSDKAgent.m_pfnPlayCurrentSelection(mPosition);
					Message message = Message.obtain();
					message.what = handler_detail_info;
					myHandler.sendMessageDelayed(message,500);
				} catch (Exception e) {
					Log.e("listviewListener","------error------playCurrentSelection");
					e.printStackTrace();
				}
			}
		}
	}
	void initDisplayTab() throws Exception{
		Log.e("INIT_DISPLAY","-----------initDisplayTab-----------");

		play_SeekBar.setProgress(0);
		play_current_time_TView.setText("00:00");
		play_all_time_TView.setText("00:00");
		m_u4MaxSliderValue = 0;

		int u4TrackIndex = 0;
		try{
			u4TrackIndex = m_iPodSDKAgent.m_pfnGetCurPlayingTrackIdx(u4TrackIndex);
		}catch (Exception e){
			Log.e("mylistener","------error------getCurPlayingTrackIdx");
			e.printStackTrace();
		}	
		if(u4TrackIndex == -1)u4TrackIndex = 0;
		intCurrentIndex = u4TrackIndex;
		//Log.e("INIT_DISPLAY","--------------:"+intCurrentIndex);

		int dwTrackCount = 0;
		dwTrackCount = m_iPodSDKAgent.m_pfnGetNumPlayingTracks(dwTrackCount);

		//tvCounter.setText(dwTrackCount+"/"+u4TrackIndex);	
		player_order_TView.setText((u4TrackIndex+1)+"/"+dwTrackCount);

		String szCurTrackTitle = "";
		String szCurTrackArtist = "";
		String szCurTrackAlbum = "";
		TRACK_GENRE_INFO_T szCurTrackGenre = new TRACK_GENRE_INFO_T();

		TRACK_CAP_INF_T rTrackCapInfo = new TRACK_CAP_INF_T();

		rTrackCapInfo = (TRACK_CAP_INF_T)(m_iPodSDKAgent.m_pfnGetIndexedPlayingTrackInfo(intCurrentIndex,0,TRACK_INF_TYPE_E.TRACK_CAP_INF,rTrackCapInfo));
		play_SeekBar.setMax((int)rTrackCapInfo.timeLen);
		String s = ""; 
		s = new SimpleDateFormat("mm:ss").format(new Date( rTrackCapInfo.timeLen + 0));
		play_all_time_TView.setText(s);
		m_u4MaxSliderValue = rTrackCapInfo.timeLen;

		szCurTrackTitle = m_iPodSDKAgent.m_pfnGetIndexedPlayingTrackTitle(intCurrentIndex, szCurTrackTitle);
		player_track_TView.setText(szCurTrackTitle);

		szCurTrackArtist = m_iPodSDKAgent.m_pfnGetIndexedPlayingTrackArtist(intCurrentIndex, szCurTrackArtist);
		player_artist_TView.setText(szCurTrackArtist);

		szCurTrackAlbum = m_iPodSDKAgent.m_pfnGetIndexedPlayingTrackAlbum(intCurrentIndex, szCurTrackAlbum);
		player_album_TView.setText(szCurTrackAlbum);

		szCurTrackGenre = (TRACK_GENRE_INFO_T)(m_iPodSDKAgent.m_pfnGetIndexedPlayingTrackInfo(intCurrentIndex, 0,TRACK_INF_TYPE_E.TRACK_GENRE, szCurTrackGenre));
		if(szCurTrackGenre.title != null){
			//tvGenre.setText(new String(szCurTrackGenre.title));
		}

		Message message = Message.obtain();
		message.what = handler_update_image;
		myHandler.sendMessage(message);
	}
	
	@Override
	public void onList_song() {
		selectTypePlay(MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_TRACK
				,DB_SUB_TYPE_TRACK_TRACK); 

	}
	
	@Override
	public void onList_album() {
		selectTypePlay(MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ALBUM
				,DB_SUB_TYPE_ALBUM_ALBUM); 
	}
	
	@Override
	public void onList_PlayList() {
		selectTypePlay(MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_PLAYLIST
				,DB_SUB_TYPE_PLAYLIST_PLAYLIST);
	}
	
	@Override
	public void onList_performer() {
		m_u1SubDbCategory = DB_SUB_TYPE_ARTIST_ARTIST;
		selectTypePlay(MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ARTIST
				,DB_SUB_TYPE_ARTIST_ARTIST); 
	}
	
	@Override
	public void onList_back() {
		//if(musicAdapterNew!=null&&musicAdapterNew.getCount()<=0)mFboxPopu.dismiss()
		if(!m_bConnected)mFboxPopu.dismiss();
		if(m_u1CurDbCategory==MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_PLAYLIST.ordinal()){
			if(m_u1SubDbCategory==DB_SUB_TYPE_PLAYLIST_PLAYLIST){
				mFboxPopu.dismiss();
			}
			selectTypePlay(MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_PLAYLIST
					,DB_SUB_TYPE_PLAYLIST_PLAYLIST);

		}else if(m_u1CurDbCategory==MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ARTIST.ordinal()){
			if(m_u1SubDbCategory==DB_SUB_TYPE_ARTIST_ARTIST){//1
				mFboxPopu.dismiss();
			}else if(m_u1SubDbCategory==DB_SUB_TYPE_ARTIST_ALBUM){//2
				selectTypePlay(MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ARTIST
						,DB_SUB_TYPE_ARTIST_ARTIST); 
			}else if(m_u1SubDbCategory==DB_SUB_TYPE_ARTIST_TRACK){//3
				selectTypePlay(MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ARTIST
						,DB_SUB_TYPE_ARTIST_ARTIST); 
			}
			L.v("####"+m_u1CurDbCategory+"###"+m_u1SubDbCategory);

		}else if(m_u1CurDbCategory==MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_TRACK.ordinal()){
			if(m_u1SubDbCategory==DB_SUB_TYPE_TRACK_TRACK){
				mFboxPopu.dismiss();
			}
			selectTypePlay(MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_PLAYLIST
					,DB_SUB_TYPE_TRACK_TRACK);

		}else if(m_u1CurDbCategory==MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ALBUM.ordinal()){
			if(m_u1SubDbCategory==DB_SUB_TYPE_ALBUM_ALBUM){
				mFboxPopu.dismiss();
			}
			selectTypePlay(MUSB_IPOD_DB_CATEGORY_TYPE_E.DB_TYPE_ALBUM
					,DB_SUB_TYPE_ALBUM_ALBUM); 
		}else{
			mFboxPopu.dismiss();
		}
	}
	
	@Override
	public void getIsDismiss() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void showPopuWindow() {
		// TODO Auto-generated method stub
	}
	
	void selectTypePlay(MUSB_IPOD_DB_CATEGORY_TYPE_E type_id, int type_index ){
		try{
			m_iPodSDKAgent.m_pfnResetDBSelection(); 
		}catch(Exception e){
			e.printStackTrace();
		}
		try {
			m_u1CurDbCategory = type_id.ordinal();

			L.v(m_u1SubDbCategory+"-----HandleAudioTab-m_u1CurDbCategory---#"+m_u1CurDbCategory);

			int u4NumRecords = m_iPodSDKAgent
					.m_pfnGetNumberCategorizedDBRecords(m_u1CurDbCategory);
			int u4StartRecIdx = 0;
			int u4RecCnt = 1;
			DB_RECITEM_T rRecItem = new DB_RECITEM_T();
			musicAdapterNew.clear();
			for (int i = 0; i < u4NumRecords; i++) {
				u4StartRecIdx = i;
				try{
					rRecItem = (DB_RECITEM_T)(m_iPodSDKAgent.m_pfnRetrieveCategorizedDBRecords
							(m_u1CurDbCategory, u4StartRecIdx,u4RecCnt, rRecItem));
					mMusicInfo=musicAdapterNew.getMusicInfo();
					mMusicInfo.setsName(new String(rRecItem.szRecInfo));
					mMusicInfo.setsNum(i+"");
					musicAdapterNew.addItem(mMusicInfo);
				}catch(Exception e){
					e.printStackTrace();
					Log.e("mylistener","------error------retrieveCategorizedDBRecords");
				}
			}
			m_u1SubDbCategory = type_index;
			musicAdapterNew.notifyDataSetChanged();
		} catch (Exception e) {
			Log.e("mylistener","------error------getNumberCategorizedDBRecords");
			e.printStackTrace();
		}

	}
	
	private void showDialog(){
		if (progressDialog == null){
			progressDialog = CustomProgressDialog.createDialog(this);
			// progressDialog.setMessage("濠殿喗绻愮徊钘夛耿椤忓牆绀夐柣妯煎劋缁佹澘鈽夐幙鍕..");
		}
		progressDialog.show();
	}

	private void stopProgressDialog(){
		if (progressDialog != null&&progressDialog.isShowing()){
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public class LoadingAsyncTask extends AsyncTask<Integer, Integer, String>{
		
		public LoadingAsyncTask(){
		}
		
		@Override
		protected String doInBackground(Integer... params) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			try{
				boolean need2Init = false;
				if(m_iPodSDKAgent == null){
				    m_iPodSDKAgent = new JiPodSDKAgent();
					need2Init = true;				
				}
				if (!m_iPodSDKAgent.m_pfnIPOD_InitCBM()) {
					Log.i(TAG, "m_pfnIPOD_InitCBM false ");
					return "";
				}
				if(need2Init && !m_iPodSDKAgent.m_pfnIPOD_Init()){
					Log.i(TAG, "m_pfnIPOD_Init false ");
					return "";
				}
				m_biPodInited = true;
				
				AUTH_RESULT_T rAuthResult = new AUTH_RESULT_T();
				rAuthResult = m_iPodSDKAgent.m_pfnGetAuthStatus(rAuthResult);
				if(rAuthResult.authResult == E_IPOD_AUTHENTICATION_STATUS.IPOD_AUTHENTICATION_PASS.ordinal())
				{ 
					m_bConnected = true;
					HandleAuthDone(E_IPOD_AUTHENTICATION_STATUS.IPOD_AUTHENTICATION_PASS.ordinal());
					//publishProgress(AsyncTask_Success);
				}else{
					publishProgress(rAuthResult.authResult);
				}
			}catch(Exception e){
				publishProgress(AsyncTask_Fail);
				e.printStackTrace();//AUTH_RESULT_T rAuthResult = new AUTH_RESULT_T();
			}
			
			try{
				m_iPodSDKAgent.ipodsdk.setEventCallback(new IPodEventCallback());
				//publishProgress(AsyncTask_Success);
			}catch(Exception e){
				//publishProgress(AsyncTask_Fail);
				//Log.e("mylistener","------error------getEventCallback");
				e.printStackTrace();
			}
			
			return params[0].intValue()+"";
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			//if(values[0] == -1){
				Log.i(TAG, "Ipod init failed " + values[0]);
				
			//}
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {//over
			Message message = Message.obtain();
			message.what = handler_detail_info;
			myHandler.sendMessage(message);
			stopProgressDialog();
		}
		
		@Override
		protected void onPreExecute() {//start
			showDialog();
			super.onPreExecute();
		}
		
		@Override
		protected void onCancelled() {
			stopProgressDialog();
			super.onCancelled();
		}	
	}
	
/**
    DB_TYPE_TRACK   // 濞戞挻鎹佺欢锟�   DB_SUB_TYPE_TRACK_TRACK

    DB_TYPE_GENRE// 濞存粌鎼幃锟�   DB_SUB_TYPE_GENRE_GENRE
    DB_SUB_TYPE_GENRE_ARTIST
    DB_SUB_TYPE_GENRE_ALBUM
    DB_SUB_TYPE_GENRE_TRACK

    DB_TYPE_PLAYLIST     //闁圭虎鍘介弬渚�礆濡ゅ嫨锟�    DB_SUB_TYPE_PLAYLIST_PLAYLIST
    DB_SUB_TYPE_PLAYLIST_TRACK

    DB_TYPE_ARTIST     // 闁煎湱鍎ゅ﹢宕囷拷閿燂拷   DB_SUB_TYPE_ARTIST_ARTIST
    DB_SUB_TYPE_ARTIST_ALBUM
    DB_SUB_TYPE_ARTIST_TRACK

    DB_TYPE_ALBUM   //   濞戞挻鎹佺欢锟�   DB_SUB_TYPE_ALBUM_ALBUM
    DB_SUB_TYPE_ALBUM_TRACK

    DB_TYPE_AUDIOBOOK  //  闁哄牆顦婚敍鎰嫚閼姐倕鈷�    DB_SUB_TYPE_AUDIOBOOK_TRACK

    DB_TYPE_PODCAST    // 妤犵偞瀵ч幐锟�   DB_SUB_TYPE_PODCAST_PODCAST
    DB_SUB_TYPE_PODCAST_TRACK

    DB_TYPE_COMPOSER   //濞达絾绮嶅ú鍝ワ拷閿燂拷   DB_SUB_TYPE_COMPOSER_COMPOSER
    DB_SUB_TYPE_COMPOSER_TRACK 
 **/
	
	private void onBackPressedToHome() {
		finish();
	}
}
