
package com.yecon.music;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.autochips.storage.EnvironmentATC;
import com.yecon.music.MusicUtils;
import com.yecon.music.MusicUtils.ServiceToken;

import static com.yecon.music.MusicConstant.EXT_SDCARD1_PATH;
import static com.yecon.music.MusicConstant.EXT_SDCARD2_PATH;
import static com.yecon.music.MusicConstant.GOTO_DELAY_TIME;
import static com.yecon.music.MusicConstant.GOTO_DELAY_WHAT;
import static com.yecon.music.MusicConstant.INT_SDCARD_PATH;
import static com.yecon.music.MusicConstant.UDISK1_PATH;
import static com.yecon.music.MusicConstant.UDISK2_PATH;
import static com.yecon.music.MusicConstant.UDISK3_PATH;
import static com.yecon.music.MusicConstant.UDISK4_PATH;
import static com.yecon.music.MusicConstant.UDISK5_PATH;
import static com.yecon.music.util.DebugUtil.*;

@SuppressLint("HandlerLeak")
public class MusicTrackActivity extends Activity implements ServiceConnection, OnClickListener {
    private static final int TAB_ID_UNKNOWN = -1;
    private static final int TAB_ID_LIST = 0;
    private static final int TAB_ID_FOLDER = 1;
    private static final int TAB_ID_ALBUM = 2;
    private static final int TAB_ID_ARTIST = 3;

    private static final String CURR_TAB_PREF = "curr_tab_pref";

    private static final boolean FUNC_AUTO_EXIT = true;

    private int mCurrTab = TAB_ID_FOLDER;

    private TextView mBtnTabArtist;
    private TextView mBtnTabFolder;
    private TextView mBtnTabList;
    private TextView mBtnTabAlbum;

    private ArtistFragment mArtistFragment;
    private FolderFragment mFolderFragment;
    private ListFragment mListFragment;
    private AlbumFragment mAlbumFragment;

    private FragmentManager mFragmentManager;

    private ServiceToken mToken;
    private IMediaPlaybackService mService = null;

    private EnvironmentATC mEnv;

    private Handler mGotoHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (GOTO_DELAY_WHAT == msg.what) {
                if (FUNC_AUTO_EXIT) {
                    mCurrTab = TAB_ID_UNKNOWN;
                    onBackPressed();
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainApp.addActivity(this);

        initData();

        initUI();
    }

    private void initData() {
        mToken = MusicUtils.bindToService(this, this,null);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mFragmentManager = getFragmentManager();

        mArtistFragment = new ArtistFragment();
        mFolderFragment = new FolderFragment();
        mListFragment = new ListFragment();
        mAlbumFragment = new AlbumFragment();

        mEnv = new EnvironmentATC(this);
    }

    private void initUI() {
        setContentView(R.layout.yecon_music_track_activity);

        mBtnTabArtist = (TextView) findViewById(R.id.btn_tab_artist);
        mBtnTabArtist.setOnClickListener(this);
        mBtnTabFolder = (TextView) findViewById(R.id.btn_tab_folder);
        mBtnTabFolder.setOnClickListener(this);
        mBtnTabList = (TextView) findViewById(R.id.btn_tab_list);
        mBtnTabList.setOnClickListener(this);
        mBtnTabAlbum = (TextView) findViewById(R.id.btn_tab_album);
        mBtnTabAlbum.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mGotoHandler.removeMessages(GOTO_DELAY_WHAT);

        MainApp.removeActivity(this);

        MusicUtils.unbindFromService(mToken);

        printLog("MusicTrackActivity - onDestroy", true);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        printLog("MusicTrackActivity - onServiceConnected", true);

        mService = IMediaPlaybackService.Stub.asInterface(service);
        mCurrTab = MusicUtils.getIntPref(this, CURR_TAB_PREF, TAB_ID_FOLDER);
        printLog("MusicTrackActivity - mCurrTab: " + mCurrTab, false);

        setCurrTabUI(mCurrTab);

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        switch (mCurrTab) {
            case TAB_ID_ARTIST:
                ft.replace(R.id.layout_content, mArtistFragment);
                break;

            case TAB_ID_FOLDER:
                ft.replace(R.id.layout_content, mFolderFragment);
                break;

            case TAB_ID_LIST:
                ft.replace(R.id.layout_content, mListFragment);
                break;

            case TAB_ID_ALBUM:
                ft.replace(R.id.layout_content, mAlbumFragment);
                break;
        }
        ft.commit();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        printLog("MusicTrackActivity - onServiceDisconnected", true);
    }

    public IMediaPlaybackService getService() {
        return mService;
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();

        int id = view.getId();
        switch (id) {
            case R.id.btn_tab_artist:
                setCurrTabUI(TAB_ID_ARTIST);

                ft.replace(R.id.layout_content, mArtistFragment);
                break;

            case R.id.btn_tab_folder:
                setCurrTabUI(TAB_ID_FOLDER);

                ft.replace(R.id.layout_content, mFolderFragment);
                break;

            case R.id.btn_tab_list:
                setCurrTabUI(TAB_ID_LIST);

                ft.replace(R.id.layout_content, mListFragment);
                break;

            case R.id.btn_tab_album:
                setCurrTabUI(TAB_ID_ALBUM);

                ft.replace(R.id.layout_content, mAlbumFragment);
                break;

            default:
                return;
        }

        ft.commit();
    }

    @Override
    public void onBackPressed() {
        switch (mCurrTab) {
            case TAB_ID_LIST:
                break;

            case TAB_ID_FOLDER:
                if (!mFolderFragment.onBackPressed()) {
                    return;
                }
                break;

            case TAB_ID_ALBUM:
                if (!mAlbumFragment.onBackPressed()) {
                    return;
                }
                break;

            case TAB_ID_ARTIST:
                if (!mArtistFragment.onBackPressed()) {
                    return;
                }
                break;
        }

        super.onBackPressed();
    }

    public void autoExit() {
        mGotoHandler.removeMessages(GOTO_DELAY_WHAT);
        mGotoHandler.sendEmptyMessageDelayed(GOTO_DELAY_WHAT, GOTO_DELAY_TIME);
    }

    public boolean checkStorageExist() {
        String intSdcardState = mEnv.getStorageState(INT_SDCARD_PATH);
        if (intSdcardState.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }

        String sdcard1State = mEnv.getStorageState(EXT_SDCARD1_PATH);
        if (sdcard1State.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }

        String sdcard2State = mEnv.getStorageState(EXT_SDCARD2_PATH);
        if (sdcard2State.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }

        String udisk1State = mEnv.getStorageState(UDISK1_PATH);
        String udisk2State = mEnv.getStorageState(UDISK2_PATH);
        String udisk3State = mEnv.getStorageState(UDISK3_PATH);
        String udisk4State = mEnv.getStorageState(UDISK4_PATH);
        String udisk5State = mEnv.getStorageState(UDISK5_PATH);
        if (udisk1State.equals(Environment.MEDIA_MOUNTED)
                || udisk2State.equals(Environment.MEDIA_MOUNTED)
                || udisk3State.equals(Environment.MEDIA_MOUNTED)
                || udisk4State.equals(Environment.MEDIA_MOUNTED)
                || udisk5State.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }

        return false;
    }

    private void setCurrTabUI(int tabId) {
        autoExit();

        mCurrTab = tabId;
        MusicUtils.setIntPref(this, CURR_TAB_PREF, mCurrTab);

        mBtnTabArtist.setSelected(false);
        mBtnTabFolder.setSelected(false);
        mBtnTabList.setSelected(false);
        mBtnTabAlbum.setSelected(true);

        switch (tabId) {
            case TAB_ID_ARTIST:
                mBtnTabArtist.setSelected(true);
                mBtnTabFolder.setSelected(false);
                mBtnTabList.setSelected(false);
                mBtnTabAlbum.setSelected(false);
                break;

            case TAB_ID_FOLDER:
                mBtnTabArtist.setSelected(false);
                mBtnTabFolder.setSelected(true);
                mBtnTabList.setSelected(false);
                mBtnTabAlbum.setSelected(false);
                break;

            case TAB_ID_LIST:
                mBtnTabArtist.setSelected(false);
                mBtnTabFolder.setSelected(false);
                mBtnTabList.setSelected(true);
                mBtnTabAlbum.setSelected(false);
                break;

            case TAB_ID_ALBUM:
                mBtnTabArtist.setSelected(false);
                mBtnTabFolder.setSelected(false);
                mBtnTabList.setSelected(false);
                mBtnTabAlbum.setSelected(true);
                break;
        }
    }

}
