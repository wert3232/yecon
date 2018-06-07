package com.yecon.imagebrowser;

import static android.mcu.McuExternalConstant.*;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import com.yecon.imagebrowser.R;
import com.yecon.mediaprovider.YeconMediaStore;
import com.yecon.mediaprovider.YeconMediaStore.YeconMediaFilesColumns;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ImageDetailActivity extends FragmentActivity implements OnClickListener {
	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "image_index";
	public static final String EXTRA_IMAGE_URLS = "image_urls";

	private HackyViewPager mPager;
	private int pagerPosition;
	private TextView mTVindicator, mTVPageNO, mTVFileName;
	private LinearLayout mBarTop, mBarBottom;
	private TextView mBtnWallPaper, mBtnRotate,mBtnRotateLeft, mBtnPrev, mBtnPlay, mBtnPause, mBtnNext, mBtnBack;
	private static final String URL_TAG = "file://";
	private ImagePagerAdapter mAdapter;
	private final static int MSG_AUTO_PLAY = 1;
	private final static int MSG_FULL_SCREEN = 2;
	private final static int MSG_SET_WALLPAPER = 3;
	private final static int DURATION_AUTO_PLAY = 3000;
	private final static int TIME_AUTO_FULLSREEN = 8 * 1000;
	private int miRotate = 0;
	Animation mAnimationfadeIn, mAnimationfadeOut;
	private int miWallPaperW, miWallPaperH;
	private SharedPreferences mPrf;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == MSG_AUTO_PLAY) {
				if (!mPager.isTouched()) {
					execNext();
				}
				execPPlay(true);
			} else if (msg.what == MSG_FULL_SCREEN) {
				showBar(false);
			} else if (msg.what == MSG_SET_WALLPAPER) {
				ShowPaperToast(msg.arg1 != 0);
			}
		}
		
	};
	
	private BroadcastReceiver mReceiverMCU = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(MCU_ACTION_MEDIA_NEXT)) {
				onClick(mBtnNext);
			} else if (action.equals(MCU_ACTION_MEDIA_PREVIOUS)) {
				onClick(mBtnPrev);
			} else if (action.equals(MCU_ACTION_MEDIA_PAUSE)){
				onClick(mBtnPause);
			} else if (action.equals(MCU_ACTION_MEDIA_PLAY)) {
				onClick(mBtnPlay);
			} else if (action.equals(MCU_ACTION_MEDIA_PLAY_PAUSE)) {
				execPPlay(!isPlaying());
			}
		}
	};
	
	private BroadcastReceiver mReceiverScan = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				String strAction = intent.getStringExtra(YeconMediaStore.ACTION);
				String strPath = intent.getStringExtra(YeconMediaStore.PATH);
				if (strAction.equals(YeconMediaStore.ACTION_SCAN_CANCEL)) {
					if (mAdapter.fileList.length > 0) {
						if (mAdapter.fileList[0].contains(strPath)) {
							finish();
						}
					}
				}
			}
		}
	};

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail_pager);
		mPrf = getSharedPreferences("img_setting", Context.MODE_PRIVATE);
		miWallPaperW = Integer.valueOf(getString(R.integer.screen_width));
		miWallPaperH = Integer.valueOf(getString(R.integer.screen_height));
		
		mPager = (HackyViewPager) findViewById(R.id.pager);
		String[] urls = null;
		Intent intent = getIntent();
		if (intent.getAction() != null) {
			urls = getUrls(intent);
		} else {
			pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
			urls = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);
		}
		mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), urls);
		mPager.setAdapter(mAdapter);
		mTVindicator = (TextView) findViewById(R.id.indicator);
		mTVPageNO = (TextView) findViewById(R.id.pageNo);
		mTVFileName = (TextView) findViewById(R.id.fileName);
		mBarTop = (LinearLayout) findViewById(R.id.topBar);
		mBarBottom = (LinearLayout) findViewById(R.id.bottomBar);
		
		mBtnBack = (TextView) findViewById(R.id.back);
		mBtnNext = (TextView) findViewById(R.id.next);
		mBtnPlay = (TextView) findViewById(R.id.play);
		mBtnPause = (TextView) findViewById(R.id.pause);
		mBtnPrev = (TextView) findViewById(R.id.prev);
		mBtnRotate = (TextView) findViewById(R.id.rotate_right);
		mBtnRotateLeft = (TextView) findViewById(R.id.rotate_left);
		mBtnWallPaper = (TextView) findViewById(R.id.setwallpaper);
		
		mBarBottom.setOnClickListener(this);
		mBtnBack.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
		mBtnPlay.setOnClickListener(this);
		mBtnPause.setOnClickListener(this);
		mBtnPrev.setOnClickListener(this);
		mBtnRotate.setOnClickListener(this);
		mBtnRotateLeft.setOnClickListener(this);
		mBtnWallPaper.setOnClickListener(this);
		
		mAnimationfadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.animator.fade_in);
		mAnimationfadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.animator.fade_out);
		
		CharSequence text = getString(R.string.viewpager_indicator, 1, mPager.getAdapter().getCount());
		mTVindicator.setText(text);
		
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				updatePageInfo(arg0);
				miRotate = 0;
				SetCurItemRotate(miRotate);
			}
		});
		
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}
		mPager.setCurrentItem(pagerPosition);
		updatePageInfo(pagerPosition);
		mBarBottom.setVisibility(View.INVISIBLE);
		mBarTop.setVisibility(View.INVISIBLE);
		
		IntentFilter filter = new IntentFilter();
        filter.addAction(MCU_ACTION_MEDIA_NEXT);
        filter.addAction(MCU_ACTION_MEDIA_PREVIOUS);
        filter.addAction(MCU_ACTION_MEDIA_PLAY_PAUSE);
        filter.addAction(MCU_ACTION_MEDIA_PLAY);
        filter.addAction(MCU_ACTION_MEDIA_PAUSE);
        registerReceiver(mReceiverMCU, filter);
        
        filter = new IntentFilter();
		filter.addAction(YeconMediaStore.ACTION_YECON_MEIDA_SCANER_STATUS);
		registerReceiver(mReceiverScan, filter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		sendBroadcast(new Intent("com.yecon.imagebrowser.treeWindow"));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiverMCU);
		unregisterReceiver(mReceiverScan);
	}

	public void FullScreen() {
		boolean bFull = mBarBottom.getVisibility() == View.VISIBLE ? true : false;
		showBar(!bFull);
		updatePageInfo(pagerPosition);
		PrepareFullScreen();
	}
	
	public void showBar(boolean bShow) {
		mBarBottom.setAnimation(bShow ? mAnimationfadeIn : mAnimationfadeOut);
		mBarTop.setAnimation(bShow ? mAnimationfadeIn : mAnimationfadeOut);
		mBarBottom.setVisibility(bShow ? View.VISIBLE : View.INVISIBLE);
		mBarTop.setVisibility(bShow ? View.VISIBLE : View.INVISIBLE);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
	}

	@SuppressLint("UseSparseArrays")
	private class ImagePagerAdapter extends FragmentStatePagerAdapter {

		public String[] fileList;
		public HashMap<String, WeakReference<ImageDetailFragment>> mMapFragment = new HashMap<String, WeakReference<ImageDetailFragment>>();

		public ImagePagerAdapter(FragmentManager fm, String[] fileList) {
			super(fm);
			this.fileList = fileList;
		}

		@Override
		public int getCount() {
			return fileList == null ? 0 : fileList.length;
		}

		@Override
		public Fragment getItem(int position) {
			String url = fileList[position];
			ImageDetailFragment item = null;
			if (mMapFragment.containsKey(url)) {
				item = mMapFragment.get(url).get();
			}
			if (item == null) {
				item = ImageDetailFragment.newInstance(url);
				mMapFragment.put(url, new WeakReference<ImageDetailFragment>(item));
			} else {
				item.setRotate(0);
			}
			return item;
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		boolean bStop = true;
		switch (v.getId()) {
		case R.id.setwallpaper:
			setWallPaper();
			break;
		case R.id.rotate_left:
			miRotate += 90;
			SetCurItemRotate(miRotate);
			break;
		case R.id.rotate_right:
			miRotate -= 90;
			SetCurItemRotate(miRotate);
			break;
		case R.id.prev:
			execPrev();
			break;
		case R.id.play:
			bStop = false;
			execPPlay(true);
			break;
		case R.id.pause:
			bStop = true;
			execPPlay(false);
			break;
		case R.id.next:
			execNext();
			break;
		case R.id.back:
			try {
				mPrf.edit().remove(ImageBrowserActivity.FILE_PATH).commit();
			} catch (Exception e) {
				Log.e(this.getClass().getName(), e.toString());
			}
			finish();
			break;
		default:
			bStop = false;
			break;
		}
		
		if (bStop) {
			execPPlay(false);
		}
		PrepareFullScreen();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		PrepareFullScreen();
		return super.onTouchEvent(event);
	}
	
	public void PrepareFullScreen() {
		mHandle.removeMessages(MSG_FULL_SCREEN);
		mHandle.sendEmptyMessageDelayed(MSG_FULL_SCREEN, TIME_AUTO_FULLSREEN);
	}

	public void SetCurItemRotate(int iRotate) {
		try {
			ImageDetailFragment item = (ImageDetailFragment) mAdapter
					.getItem(pagerPosition);
			if (item != null) {
				item.setRotate(iRotate);
			}
			mPager.invalidate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updatePageInfo(int arg0) {
		CharSequence text = getString(R.string.viewpager_indicator,
				arg0 + 1, mPager.getAdapter().getCount());
		mTVindicator.setText(text);
		mTVPageNO.setText(text);
		pagerPosition = arg0;
		try {
			mTVFileName.setText("");
			if (mAdapter.fileList != null) {
				if (arg0 < mAdapter.fileList.length) {
					String url = getFileName(mAdapter.fileList[arg0]);
					mPrf.edit().putString(ImageBrowserActivity.FILE_PATH, url).commit();
					L.e("FILE_PATH: " + url);
					if(url.matches("^/mnt/ext_sdcard.*")){
						url = url.replaceFirst("/mnt/ext_sdcard\\d+", getResources().getString(R.string.sd));
					}else if(url.matches("^/mnt/udisk.*")){
						url = url.replaceFirst("/mnt/udisk\\d+", getResources().getString(R.string.usb));
					}
					mTVFileName.setText(url);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void execPPlay(boolean bPlay) {
		mHandle.removeMessages(MSG_AUTO_PLAY);
		if (bPlay) {
			mHandle.sendEmptyMessageDelayed(MSG_AUTO_PLAY, DURATION_AUTO_PLAY);
			mBtnPause.setVisibility(View.VISIBLE);
			mBtnPlay.setVisibility(View.GONE);
		} else {
			mBtnPause.setVisibility(View.GONE);
			mBtnPlay.setVisibility(View.VISIBLE);
		}
	}
	
	public void execNext() {
		int iSize = 0;
		try {
			iSize = mAdapter.fileList.length;
			if (iSize > 0) {
				pagerPosition = ++pagerPosition % iSize;
				mPager.setCurrentItem(pagerPosition);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void execPrev() {
		int iSize = 0;
		try {
			iSize = mAdapter.fileList.length;
			if (iSize > 0) {
				pagerPosition = (--pagerPosition + iSize) % iSize;
				mPager.setCurrentItem(pagerPosition);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isPlaying() {
		return mHandle.hasMessages(MSG_AUTO_PLAY);
	}
	
	public String[] getUrls(Intent intent) {
		String[] urls = null;
		try {
			Uri uri = intent.getData();
	        if (uri == null) {
	            finish();
	            return null;
	        }
	        String file = uri.getPath();
	        int iStart = 0;
	        int iEnd = file.lastIndexOf('/');
	        String parent = file.substring(iStart, iEnd);
	        String database = ImageBrowserActivity.getDBFile(parent);
			// query database
			Cursor cursor = getContentResolver().query(
					YeconMediaStore.getContent(database,YeconMediaStore.TABLE_FILES), 
					new String[] {YeconMediaFilesColumns.DATA},
					YeconMediaFilesColumns.PARENT + "='" + parent + "' and media_type=3", null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				urls = new String[cursor.getCount()];
				for (int i = 0; i < urls.length; i++) {
					urls[i] = URL_TAG + cursor.getString(0);
					if (urls[i].contains(file)) {
						pagerPosition = i;
					}
					cursor.moveToNext();
				}
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return urls;
	}
	
	public String getFileName(String url) {
		try {
			return url.substring(url.indexOf(URL_TAG) + URL_TAG.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void ShowPaperToast(boolean bSuccess) {
		String str = bSuccess ? getString(R.string.str_tips_setwallpaper_success) : getString(R.string.str_tips_setwallpaper_failed);
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	public void setWallPaper() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				super.run();
				Message msg = Message.obtain();
				msg.what = MSG_SET_WALLPAPER;
				Context context = ImageDetailActivity.this;
				WallpaperManager mWallManager = WallpaperManager.getInstance(context);
				try {
					// decode
					Bitmap bmpSrc = BitmapFactory
							.decodeFile(getFileName(mAdapter.fileList[pagerPosition]));
					mWallManager.setBitmap(Bitmap.createScaledBitmap(bmpSrc, miWallPaperW, miWallPaperH, false));
					msg.arg1 = 1;
				} catch (Exception e) {
					msg.arg1 = 0;
					e.printStackTrace();
				} finally {
					mHandle.sendMessage(msg);
				}
			}
		};
		thread.start();
	}
}