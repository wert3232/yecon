package com.autochips.picturebrowser;

//import static android.mcu.McuExternalConstant.ACTION_UPDATE_ACTIVITY_TITLE;
//import static android.mcu.McuExternalConstant.INTENT_EXTRA_ACTIVITY_TITLE;
import static android.mcu.McuExternalConstant.MCU_ACTION_MEDIA_NEXT;
import static android.mcu.McuExternalConstant.MCU_ACTION_MEDIA_PAUSE;
import static android.mcu.McuExternalConstant.MCU_ACTION_MEDIA_PLAY;
import static android.mcu.McuExternalConstant.MCU_ACTION_MEDIA_PLAY_PAUSE;
import static android.mcu.McuExternalConstant.MCU_ACTION_MEDIA_PREVIOUS;
import static android.mcu.McuExternalConstant.MCU_ACTION_MEDIA_STOP;

import java.io.File;
import java.util.Iterator;

import com.autochips.picturebrowser.data.BitmapContext;
import com.autochips.picturebrowser.data.LocalData;
import com.autochips.picturebrowser.data.SeenScreenData;
import com.autochips.storage.EnvironmentATC;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yecon.common.SourceManager;
import com.yecon.common.YeconEnv;

import android.app.Activity;
import android.app.Presentation;
import android.constant.YeconConstants;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
//import android.os.SystemProperties;
//import com.autochips.cbmctx.CBMCtx;

import static android.constant.YeconConstants.*;

public class ThumbnailsActivity extends Activity {
	private static final String TAG = "ThumbnailsActivity";
    private static final String ACTION_CLOSEREAR = "autochips.intent.action.closerear";
    
    private static final int MSG_CHECK_SCAN_STATUS = 0;
    private static final int MSG_CHECK_AQUIRE_MEDIA_BUTTON = 1;
    private AudioManager mAudioManager;
    
    private GridView mGridView = null;
    private DataNotify mUpdateUI = null;
    private boolean mIsActivityPause = false;
    private SeenScreenData mScreenData = null;
    private ThumbnailsAdapter mThumbAdapter = null;
    // private CBMCtx mCBMCtx = null;
    private View layout_error, layout_main;
    private TextView tvErrorHint;

    private MediaRouter mMediaRouter = null;
    private ThumbPresentation mPresentation = null;
    private ScreenStatusReceiver mScreenStatusReceiver = null;
    public static String lastPlayPath ="";
	public final static String PREFERENCE = "play_context";
	public static ThumbnailsActivity instance = null;
	private static DataManager dataManager=null;
	public static DataManager getDataManager(){
		return dataManager;
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.thumbnails);
        
        mUpdateUI = new UpdateUI();
        dataManager = new DataManager(this, null);
        dataManager.getData().setNotifyGrid(mUpdateUI);
        dataManager.load(); 
        
        layout_error = this.findViewById(R.id.layout_error);
        layout_main = this.findViewById(R.id.laytou_main);
        tvErrorHint = (TextView)this.findViewById(R.id.tv_sd_error);
        
        mMediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
        mGridView = (GridView) findViewById(R.id.theThumbnails);
        // mCBMCtx = new CBMCtx();

        mScreenData = new SeenScreenData();
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        mScreenData.mSeenWidth = rect.width();
        mScreenData.mSeenHeight = rect.height();

        mGridView.setColumnWidth(mScreenData.mSeenWidth / 4);
        mThumbAdapter = new ThumbnailsAdapter(getApplicationContext(),
                (mScreenData.mSeenWidth - 15) / 4);
        mGridView.setAdapter(mThumbAdapter);

        mGridView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {

                if (null != mPresentation) {
                    GridView gridView = mPresentation.getGridView();
                    if (null != gridView) {
                        int firstVisiblePic = mGridView
                                .getFirstVisiblePosition();
                        if (firstVisiblePic >= 0
                                && firstVisiblePic < mThumbAdapter.getCount()) {
                            gridView.smoothScrollToPositionFromTop(
                                    firstVisiblePic, mGridView.getChildAt(0)
                                            .getTop());
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_FLING:
                    if (null != mThumbAdapter) {
                        mThumbAdapter.setFlagBusy(true);
                    }
                    break;
                case OnScrollListener.SCROLL_STATE_IDLE:
                    if (null != mThumbAdapter) {
                        mThumbAdapter.setFlagBusy(false);
                    }
                    break;
                case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    if (null != mThumbAdapter) {
                        mThumbAdapter.setFlagBusy(false);
                    }
                    break;

                default:
                    break;
                }
                if (null != mThumbAdapter) {
                    mThumbAdapter.notifyDataSetChanged();
                }
            }
        });

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(android.widget.AdapterView<?> parent,
                    View view, int position, long id) {

                BitmapContext bmpCtx = dataManager.getData().position(position, true);
                Intent intent = new Intent(ThumbnailsActivity.this,
                        ShowImgActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("filePath", bmpCtx.getFilePath());
                intent.putExtra("fileName", bmpCtx.getFileName());
                startActivity(intent);
            }

        });
        
        initReceiver();
        
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    private class UpdateUI implements DataNotify {
        @Override
        public void onDataChanged() {
            mThumbAdapter = new ThumbnailsAdapter(getApplicationContext(),
                    (mScreenData.mSeenWidth - 15) / 4);

            if (null == mThumbAdapter || 0 == mThumbAdapter.getCount()) {
                if (!mIsActivityPause) {
                    //Toast.makeText(ThumbnailsActivity.this,
                    //       R.string.no_pic_msg, Toast.LENGTH_SHORT).show();
                }
                Cursor cursor = getContentResolver().query(MediaStore.getMediaScannerUri(),
                        null, null, null, null);
                if (cursor != null) {
                    cursor.close();
                    tvErrorHint.setText(R.string.scanning);                   
                } 
                else{
                	tvErrorHint.setText(R.string.no_pic_msg);
                }
                layout_error.setVisibility(View.VISIBLE);
            	layout_main.setVisibility(View.GONE);
            }
            else{
            	layout_error.setVisibility(View.GONE);
            	layout_main.setVisibility(View.VISIBLE);
            }

            mGridView.setAdapter(mThumbAdapter);
            if (null != mPresentation) {
                GridView gridView = mPresentation.getGridView();
                if (null != gridView) {
                    gridView.setAdapter(mThumbAdapter);
                }
            }
        }

		@Override
		public void onLoadStart() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onLoadFinish() {
			// TODO Auto-generated method stub
			
		}
    }

    @Override
    public PictureApplication getApplicationContext() {
        return (PictureApplication) super.getApplicationContext();
    }

    public boolean isShowRearButton() {
        return false;
    }

    public void onRearButtonClick() {
        if (getApplicationContext().isRearShow()) {
            getApplicationContext().DismissRearShow();
            getApplicationContext().setIsRearShow(false);
            // mCBMCtx.closeRear();
            return;
        }
        updatePresentation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsActivityPause = true;
    }

    @Override
    protected void onResume() {
    	//SourceManager.acquireSource(this, SourceManager.SRC_NO.photo);
        if (getApplicationContext().isRearShow()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updatePresentation();
                }
            }, 500);
        }

        mIsActivityPause = false;
        super.onResume();

        if (!dataManager.getData().isDataValid()) {
            //Toast.makeText(this, R.string.no_pic_msg, Toast.LENGTH_SHORT)
             //       .show();
        	Cursor cursor = getContentResolver().query(MediaStore.getMediaScannerUri(),
                    null, null, null, null);
            if (cursor != null) {
                cursor.close();
                tvErrorHint.setText(R.string.scanning);                   
            } 
            else{
            	tvErrorHint.setText(R.string.no_pic_msg);
            }
            layout_error.setVisibility(View.VISIBLE);
        	layout_main.setVisibility(View.GONE);
        }
        else{
        	layout_error.setVisibility(View.GONE);
        	layout_main.setVisibility(View.VISIBLE);
        }
        /*
        StringBuffer title = new StringBuffer();
        //title.append(getString(R.string.title_media));
        //title.append(">");
        title.append(getString(R.string.app_name));
        Intent intent = new Intent(ACTION_UPDATE_ACTIVITY_TITLE);
        intent.putExtra(INTENT_EXTRA_ACTIVITY_TITLE, title.toString());
        sendBroadcast(intent);
        */
        if (dataManager.getData().isDataValid()) {
	        boolean gotLast = false;
	    	if(lastPlayPath.length()>0){
		    	File file = new File(lastPlayPath);
		    	if(file.exists()){
		    		gotLast = true;
		    		BottomBar.notifyNavigator(this, file.getName());
		    	}
	    	}
	    	if(!gotLast){
	    		BottomBar.notifyNavigator(this, this.getResources().getString(R.string.app_name));
	    	}
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        MediaButtonIntentReceiver.unregisterListener(mediaButtonCallback);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        MediaButtonIntentReceiver.registerListener(mediaButtonCallback);
        setupMediaKeyReceiver(true);
    }      

    @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
    	Log.i("", "onBackPressed");
		super.onBackPressed();
	}

	@Override
    protected void onDestroy() {
        if (getApplicationContext().isRearShow()) {
            getApplicationContext().DismissRearShow();
            getApplicationContext().setIsRearShow(false);
            // mCBMCtx.closeRear();
        }
        unregisterReceiver(mScreenStatusReceiver);
        unregisterReceiver(mediaReceiver);        
        instance = null;
        removeMediaKey();
        dataManager.unLoad();
        dataManager = null;
        super.onDestroy();
    }

    private void updatePresentation() {
        // Get the current route and its presentation display
        MediaRouter.RouteInfo route = mMediaRouter
                .getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = (null != route) ? route
                .getPresentationDisplay() : null;

        // Dismiss the current presentation if the display has changed.
        if (null != mPresentation
                && mPresentation.getDisplay() != presentationDisplay) {
            mPresentation.dismiss();
            mPresentation = null;
            getApplicationContext().setThumbPresentation(null);
        }

        // Show a new presentation if needed.
        if (null == mPresentation && null != presentationDisplay) {
            // String value = SystemProperties.get("atc.rearproperty", "NULL");
            // if ("NULL".equals(value)) {
            // return;
            // } else if ("FRSS".equals(value)) {
            // mPresentation = new ThumbPresentation(this, presentationDisplay);
            // } else if ("FRDS".equals(value)) {
            // mPresentation = new ThumbPresentation(getApplication(),
            // presentationDisplay);
            // }
            mPresentation = new ThumbPresentation(this, presentationDisplay);
            getApplicationContext().setThumbPresentation(mPresentation);
            mPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mPresentation.show();
                if (null != mPresentation) {
                    GridView gridView = mPresentation.getGridView();
                    if (null != gridView) {
                        int firstVisiblePic = mGridView
                                .getFirstVisiblePosition();
                        if (firstVisiblePic >= 0
                                && firstVisiblePic < mThumbAdapter.getCount()) {
                            gridView.smoothScrollToPositionFromTop(
                                    firstVisiblePic, mGridView.getChildAt(0)
                                            .getTop());
                        }
                    }
                }

                getApplicationContext().setIsRearShow(true);
                // mCBMCtx.openRear(getComponentName().getPackageName(),
                // getComponentName().getClassName());
            } catch (WindowManager.InvalidDisplayException e) {

            } catch (Exception e) {

            }
        }
    }

    public final class ThumbPresentation extends Presentation {
        private GridView mmGridView = null;
        private LayoutInflater mInflater = null;
        private View mView = null;

        public ThumbPresentation(Context context, Display display) {
            super(context, display);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            mView = mInflater.inflate(R.layout.thumbnails, null);
            setContentView(mView);

            Window win = getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            params.type = WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 26;
            win.setAttributes(params);

            mmGridView = (GridView) mView.findViewById(R.id.theThumbnails);
            if (null == mThumbAdapter) {
                mThumbAdapter = new ThumbnailsAdapter(getApplicationContext(),
                        (mScreenData.mSeenWidth - 15) / 4);
            }
            mmGridView.setAdapter(mThumbAdapter);
        }

        public GridView getGridView() {
            return mmGridView;
        }

    }

    private final DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {

        @Override
        public void onDismiss(DialogInterface dialog) {
            if (mPresentation == dialog) {
                mPresentation = null;
                getApplicationContext().setThumbPresentation(null);
            }
        }
    };

    /*
     * adapter for thumbnails view (GridView)
     */
    public class ThumbnailsAdapter extends BaseAdapter {

        private LayoutInflater mInflater = null;
        private int mThumbnailSize = 0;
        private DisplayImageOptions mOptions = null;
        private ImageSize mImageSize = null;
        private boolean mBusy = false;
        private Bitmap mGrayBitmap = null;

        public ThumbnailsAdapter(Context context, int thumbnailSize) {
            mInflater = LayoutInflater.from(context);
            mThumbnailSize = thumbnailSize;
            mGrayBitmap = Bitmap.createBitmap(mThumbnailSize, mThumbnailSize,
                    Bitmap.Config.ARGB_8888);
            mOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.bottom_bar_bg)
                    .showImageForEmptyUri(R.drawable.bottom_bar_bg)
                    .cacheInMemory(true).considerExifParams(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
            mImageSize = new ImageSize(mThumbnailSize, mThumbnailSize);
        }

        protected void setFlagBusy(boolean isBusy) {
            mBusy = isBusy;
        }

        @Override
        public int getCount() {
            if (!(dataManager.getData()).isDataValid()) {
                return 0;
            }

            return (dataManager.getData()).size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.thumbnails_item,
                        parent, false);
            }

            if (!mBusy) {
                final ImageView img = (ImageView) convertView
                        .findViewById(R.id.dataThumbnail);
                img.setMinimumHeight(mThumbnailSize);
                img.setMinimumWidth(mThumbnailSize);

                BitmapContext bmpCtx = dataManager.getData().position(position, true);
                String uri = Scheme.FILE.wrap(bmpCtx.getFilePath());
                ImageLoader.getInstance().loadImage(uri, mImageSize, mOptions,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri,
                                    View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view,
                                        loadedImage);

                                int width = loadedImage.getWidth();
                                int height = loadedImage.getHeight();
                                if (height > width) {
                                    Bitmap tmp = Bitmap.createBitmap(
                                            loadedImage, 0,
                                            (height - width) / 2, width, width);
                                    img.setImageBitmap(tmp);
                                } else {
                                    img.setImageBitmap(loadedImage);
                                }
                            }

                            @Override
                            public void onLoadingFailed(String imageUri,
                                    View view, FailReason failReason) {
                                super.onLoadingFailed(imageUri, view,
                                        failReason);
                                if (null != mGrayBitmap) {
                                    Canvas canvas = new Canvas(mGrayBitmap);
                                    if (null != canvas) {
                                        canvas.drawColor(Color.DKGRAY);
                                    }
                                }

                                img.setImageBitmap(mGrayBitmap);
                            }
                        });
            }

            return convertView;
        }
    }

    private void initReceiver() {
        mScreenStatusReceiver = new ScreenStatusReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_QB_POWEROFF);
        filter.addAction(ACTION_QB_PREPOWEROFF);
        filter.addAction(ACTION_QB_POWERON);
        filter.addAction(ACTION_CLOSEREAR);
        filter.addAction(YeconConstants.ACTION_QUIT_APK);
        registerReceiver(mScreenStatusReceiver, filter);
        
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        //filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        //filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addDataScheme("file");
        registerReceiver(mediaReceiver, filter);
    }
    
    public class ScreenStatusReceiver extends BroadcastReceiver {

		@Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_QB_PREPOWEROFF.equals(action)) {
                if (null != mPresentation) {
                    mPresentation.dismiss();
                    mPresentation = null;
                    getApplicationContext().setThumbPresentation(null);
                }
            } else if (ACTION_QB_POWERON.equals(action)) {
            } else if (ACTION_CLOSEREAR.equals(action)) {
                // Logger.print("[vend30]: ACTION_CLOSEREAR");
                // if (getApplicationContext().isRearShow()) {
                // getApplicationContext().DismissRearShow();
                // getApplicationContext().setIsRearShow(false);
                // mCBMCtx.closeRear();
                // }
            } else if (YeconConstants.ACTION_QUIT_APK.equals(action)) {
            	finish();
			}
        }
    }
    
    private void updateScanStatus(long delayMilli){
    	uiHandler.removeMessages(0);
    	uiHandler.sendEmptyMessageDelayed(0, delayMilli);
    }
    
    private Handler uiHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what == MSG_CHECK_SCAN_STATUS){
		    	if (!dataManager.getData().isDataValid()) {
		    		Cursor cursor = getContentResolver().query(MediaStore.getMediaScannerUri(),
		                    null, null, null, null);
		            if (cursor != null) {
		                cursor.close();
		                tvErrorHint.setText(R.string.scanning);                   
		            } 
		            else{
		            	tvErrorHint.setText(R.string.no_pic_msg);
		            }
		            layout_error.setVisibility(View.VISIBLE);
		        	layout_main.setVisibility(View.GONE);
		        }
		        else{
		        	layout_error.setVisibility(View.GONE);
		        	layout_main.setVisibility(View.VISIBLE);
		        }
			}
			else if(msg.what == MSG_CHECK_AQUIRE_MEDIA_BUTTON){
				checkMediaButton(false);
			}
		}
    	
    };
    
    private BroadcastReceiver mediaReceiver = new BroadcastReceiver() {

		@Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)){
            	updateScanStatus(10);
            }
            else if(Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)){
            	updateScanStatus(3000);
            }
            else if(Intent.ACTION_MEDIA_EJECT.equals(action)){
            	if(!YeconEnv.checkStorageExist(new EnvironmentATC(context))){
            		finish();
            	}
            }
        }
    };

	public void playLast() {
		// TODO Auto-generated method stub
		if(lastPlayPath.length()>0){
			if(new File(lastPlayPath).exists()){
				 int position = dataManager.getData().getPosisionByPath(lastPlayPath);
				 if(position>=0){
					 BitmapContext bmpCtx = dataManager.getData().position(position, true);
			         Intent intent = new Intent(ThumbnailsActivity.this,
			                 ShowImgActivity.class);
			         intent.putExtra("position", position);
			         intent.putExtra("filePath", bmpCtx.getFilePath());
			         intent.putExtra("fileName", bmpCtx.getFileName());
			         startActivity(intent);
				 }
			}
		}		
	}
	
	public void setupMediaKeyReceiver(boolean checkLater) {
		uiHandler.removeMessages(MSG_CHECK_AQUIRE_MEDIA_BUTTON );
		mAudioManager.registerMediaButtonEventReceiver(new ComponentName(
				getPackageName(), MediaButtonIntentReceiver.class.getName()));
		if(checkLater){
			checkMediaButtonLater();
		}
	}
	private void checkMediaButtonLater(){
		uiHandler.removeMessages(MSG_CHECK_AQUIRE_MEDIA_BUTTON );
		uiHandler.sendEmptyMessageDelayed(MSG_CHECK_AQUIRE_MEDIA_BUTTON , 2000);
	}
	private void checkMediaButton(boolean checkLater){
		Log.i(TAG, "checkMediaButton");
		 String receiverName = Settings.System.getStringForUser(getContentResolver(),
                Settings.System.MEDIA_BUTTON_RECEIVER, UserHandle.USER_CURRENT);
		 Log.i(TAG, "checkMediaButton : "+receiverName);
        if (!receiverName.contains(getPackageName())) {
       	 Log.i(TAG, "checkMediaButton registerMediaButtonEventReceiver again ");
       	 mAudioManager.registerMediaButtonEventReceiver(new ComponentName(
       			 getPackageName(), MediaButtonIntentReceiver.class.getName()));
       	 setupMediaKeyReceiver(checkLater);
        }
        else{
       	 Log.i(TAG, "checkMediaButton registerMediaButtonEventReceiver OK ");
        }	
	}
	
	public void cancelCheckMediaButton(){
		uiHandler.removeMessages(MSG_CHECK_AQUIRE_MEDIA_BUTTON);
	}
	
	private void removeMediaKey(){
		Log.i(TAG, "removeMediaKey");
		cancelCheckMediaButton();
		mAudioManager.unregisterMediaButtonEventReceiver(
				 new ComponentName(getPackageName(),
						 MediaButtonIntentReceiver.class.getName()));
	}
	
	MediaButtonIntentReceiver.Callback mediaButtonCallback = new MediaButtonIntentReceiver.Callback() {

		@Override
		public void onMediaKey(int action, int keycode) {
			// TODO Auto-generated method stub
			if (action == KeyEvent.ACTION_DOWN) {
				switch (keycode) {
				case KeyEvent.KEYCODE_MEDIA_NEXT:

					break;

				case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
					
					break;

				case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
				case KeyEvent.KEYCODE_MEDIA_PLAY:
					BitmapContext bmpCtx =dataManager.getData().position(0, true);
					if(bmpCtx!=null){
						Intent intent = new Intent(ThumbnailsActivity.this,
		                        ShowImgActivity.class);
		                intent.putExtra("position", 0);
		                intent.putExtra("filePath", bmpCtx.getFilePath());
		                intent.putExtra("fileName", bmpCtx.getFileName());
		                startActivity(intent);
					}	                
					break;

				case KeyEvent.KEYCODE_MEDIA_PAUSE:

					break;

				case KeyEvent.KEYCODE_MEDIA_STOP:

					break;
				}
			}
		}
	};
	
}
