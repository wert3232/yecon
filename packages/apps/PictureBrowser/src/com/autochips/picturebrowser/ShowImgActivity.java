package com.autochips.picturebrowser;

//import static android.mcu.McuExternalConstant.ACTION_UPDATE_ACTIVITY_TITLE;
//import static android.mcu.McuExternalConstant.INTENT_EXTRA_ACTIVITY_TITLE;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Presentation;
import android.constant.YeconConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.MediaRouter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.autochips.picturebrowser.PictureApplication;
import com.autochips.picturebrowser.data.BitmapContext;
import com.autochips.picturebrowser.data.LocalData;
import com.autochips.picturebrowser.data.SeenScreenData;
import com.autochips.picturebrowser.status.PlayStatus;
import com.autochips.picturebrowser.status.StatusHub;
import com.autochips.picturebrowser.ui.CurtainView;
import com.autochips.picturebrowser.ui.ZoomRotateImageView;
import com.autochips.picturebrowser.utils.ImageStatus;
import com.autochips.picturebrowser.utils.Logger;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yecon.common.SourceManager;
import static android.mcu.McuExternalConstant.*;
import static android.constant.YeconConstants.*;

//import android.os.SystemProperties;
//import com.autochips.cbmctx.CBMCtx;

public class ShowImgActivity extends Activity {
    private static final String ACTION_CLOSEREAR = "autochips.intent.action.closerear";

    public static final int HIDE_LONG_TIMEOUT = 5000; // second
    public static final int HIDE_SHORT_TIMEOUT = 200; // 0.2s

    private static final int RESUME_BG_COLOR = Color.parseColor("#99000000");
    private static final int PAUSE_BG_COLOR = Color.parseColor("#ff000000");
	private static final String TAG = "ShowImgActivity";

    private TouchProcess mTouchProcess = null;
    private CurtainView mCurtainView = null;
    private ZoomRotateImageView mCurImage = null;
    private TextView mFileName = null;
    private TextView pageNo = null;
    private View topBar = null;

    private boolean mIsActivityPause = false;
    private PlayStatus mPlayStatus = null;
    private SeenScreenData mScreenData = null;

    DisplayImageOptions mDisplayImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

    private boolean mIsShownStatusbar = false;
    private AHandler mAHandler = new AHandler();

    private MediaRouter mMediaRouter = null;
    private PicturePresentation mPresentation = null;
    // private CBMCtx mCBMCtx = null;

    private PointF startPoint = new PointF();
    private Matrix matrix = new Matrix();
    private Matrix currentMatrix = new Matrix();
    private ImageStatus imgStatus = new ImageStatus();
    private ImageStatus currentImgStatus = new ImageStatus();

    private GestureDetector mDetector = null;
    private int mStatusBarHeight = 0;
    private Receiver receiver = null;
    private DataManager dataManager=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.print("onCreate");
        setContentView(R.layout.activity_main);

        mDetector = new GestureDetector(this, new PicGestureListener());
        mMediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
        // mCBMCtx = new CBMCtx();
        
        initReceiver();
        
        if(!loadDataFromBrowser()){
        	dataManager = ThumbnailsActivity.getDataManager();
        	initView();
        	loadData();
        }
        
        StatusHub.addStatusOwner(mPlayStatus);
        mPlayStatus.onLoad();
        triggerShownStatusbarAndCurtainView();
       
    }

    @Override
    public PictureApplication getApplicationContext() {
        return (PictureApplication) super.getApplicationContext();
    }

    private void resetControlBar() {
    	/*
        ImageView image = (ImageView) findViewById(R.id.cicle);
        if (mPlayStatus.isLoopAll()) {
            image.setImageResource(R.drawable.play_all);
        } else {
            image.setImageResource(R.drawable.play_single);
        }

        image = (ImageView) findViewById(R.id.shuffle);
        if (mPlayStatus.isShuffleMode()) {
            image.setImageResource(R.drawable.shuffle);
        } else {
            image.setImageResource(R.drawable.shuffle_no);
        }
		*/
    	View image = (View) findViewById(R.id.play);
        if (mPlayStatus.isPlaying()) {
        	if(image instanceof ImageView){
        		((ImageView)image).setImageResource(R.drawable.main_pause);
        	}
        	else if(image instanceof TextView){
        		((TextView)image).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.main_pause,0,0);
        		((TextView)image).setText(R.string.str_btn_pause);
        	}
            
        } else {
            if(image instanceof ImageView){
        		((ImageView)image).setImageResource(R.drawable.main_play);
        	}
        	else if(image instanceof TextView){
        		((TextView)image).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.main_play,0,0);
        		((TextView)image).setText(R.string.str_btn_play);
        	}
        }
    }

    public int getSeenHeight() {
        return mScreenData.mSeenHeight;
    }
    
    private final String ACTION_MODE = "action_mode";
    private boolean loadDataFromBrowser() {
    	Intent intent = getIntent();
    	if(intent!=null){
	    	 if (Intent.ACTION_VIEW.equalsIgnoreCase(intent.getAction())){
	    		 intent.putExtra(ACTION_MODE, 1);
	    		 try{
		    		 String data = intent.getData().getPath();
		    		 String path = data.substring(0, data.lastIndexOf("/"));
		    		 String name = data.substring(data.lastIndexOf("/")+1);
		    		 Log.i(TAG, "loadData: path:"+path+ " name:" + name);
		    		 dataManager = new DataManager(this, path);
		    		 initView();
		    		 dataManager.getData().setNotifyShowImg(new DataNotify(){

						@Override
						public void onLoadStart() {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onDataChanged() {
							// TODO Auto-generated method stub
							dataManager.load();
						}

						@Override
						public void onLoadFinish() {
							// TODO Auto-generated method stub
							if (dataManager.getData().isDataValid()) {
					            BitmapContext bmpCtx = dataManager.getData().current();
					            setCurrentImageASync(bmpCtx);
					        }
							else {
					            setCurrentImageASync(null);
					            if (!mIsActivityPause) {
					                Toast.makeText(ShowImgActivity.this, R.string.no_pic_msg,
					                        Toast.LENGTH_SHORT).show();
					            }
					            mIsShownStatusbar = true;
					            triggerShownStatusbarAndCurtainView();
					            finish();
					        }
							resetControlBar();
						}
		    			 
		    		 });
					dataManager.load(data);
					if (null == mPlayStatus) {
						mPlayStatus = new PlayStatus(dataManager, this);
					}	    	        
	    		 }
	    		 catch(Exception e){
	    			 e.printStackTrace();
	    			 finish();
	    		 }
	    		 return true;
	    	 }
	    }
    	return false;
    }
    
    private void loadData(){    	
    	if(dataManager==null){
    		Log.w(TAG, "dataManager is null");
    		finish();
    		return;
    	}
    	dataManager.getData().setNotifyShowImg(new DataNotify(){

			@Override
			public void onLoadStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDataChanged() {
				// TODO Auto-generated method stub
				loadData();
			}

			@Override
			public void onLoadFinish() {
				// TODO Auto-generated method stub
				
			}
    		
    	});
        if (dataManager.getData().isDataValid()) {
            int position = (null == getIntent()) ? 0 : getIntent().getIntExtra(
                    "position", 0);
            BitmapContext bmpCtx = dataManager.getData().position(position, true);
            setCurrentImageASync(bmpCtx);
        } else {
            setCurrentImageASync(null);
            if (!mIsActivityPause) {
                Toast.makeText(ShowImgActivity.this, R.string.no_pic_msg,
                        Toast.LENGTH_SHORT).show();
            }
            mIsShownStatusbar = true;
            triggerShownStatusbarAndCurtainView();
            finish();
        }
        
        if (null == mPlayStatus) {
            mPlayStatus = new PlayStatus(dataManager, this);
        }

        resetControlBar();
    }

    private void deinitData() {
        mCurImage.setImageBitmap(null);
        resetControlBar();
    }

    private void initView() {
        if (mTouchProcess == null){
            mTouchProcess = new TouchProcess(dataManager, this);
        }
        /*
        ImageView image = (ImageView) findViewById(R.id.zoom_out);
        image.setOnTouchListener(mTouchProcess);
        image = (ImageView) findViewById(R.id.zoom_in);
        image.setOnTouchListener(mTouchProcess);
        image = (ImageView) findViewById(R.id.rotate_left);
        image.setOnTouchListener(mTouchProcess);
        */
        View image = (View) findViewById(R.id.rotate_right);
        //image.setOnTouchListener(mTouchProcess);
        image.setOnClickListener(mTouchProcess);
        image = (View) findViewById(R.id.prev);
        //image.setOnTouchListener(mTouchProcess);
        image.setOnClickListener(mTouchProcess);
        image = (View) findViewById(R.id.next);
        //image.setOnTouchListener(mTouchProcess);
        image.setOnClickListener(mTouchProcess);
        /*
        image = (ImageView) findViewById(R.id.cicle);
        image.setOnTouchListener(mTouchProcess);
        image = (ImageView) findViewById(R.id.shuffle);
        image.setOnTouchListener(mTouchProcess);
        */
        image = (View) findViewById(R.id.play);
        //image.setOnTouchListener(mTouchProcess);
        image.setOnClickListener(mTouchProcess);
        image = (View) findViewById(R.id.back);
        //image.setOnTouchListener(mTouchProcess);
        image.setOnClickListener(mTouchProcess);
        
        topBar = findViewById(R.id.topBar);
    	pageNo = (TextView) findViewById(R.id.pageNo);
        if (mFileName == null) {
            mFileName = (TextView) findViewById(R.id.fileName);
            /*
            mStatusBarHeight = Resources.getSystem().getDimensionPixelSize(
                    Resources.getSystem().getIdentifier("status_bar_height",
                            "dimen", "android"));
            mFileName.setPadding(0, mStatusBarHeight, 0, 0);
            */
        }

        if (mCurtainView == null) {
            mCurtainView = (CurtainView) findViewById(R.id.curtain_view);
        }
    	
    	 if (mScreenData == null) {
             mScreenData = new SeenScreenData();
             Rect rect = new Rect();
             getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
             mScreenData.mSeenWidth = rect.width();
             mScreenData.mSeenHeight = rect.height();
         }

         if (mCurImage == null) {
             mCurImage = (ZoomRotateImageView) findViewById(R.id.cur_image);
         }
    }

    public ImageView getCurrentImageView() {
        return mCurImage;
    }

    public SeenScreenData getScreenData() {
        return mScreenData;
    }

    public AHandler getHandler() {
        return mAHandler;
    }

    public PlayStatus getPlayStatus() {
        return mPlayStatus;
    }

    public void setCurrentImageASync(BitmapContext bmpCtx) {
        mCurImage.setImageBitmap(null);
        mFileName.setText(null);
        pageNo.setText(null);
        if (bmpCtx != null) {
        	ThumbnailsActivity.lastPlayPath = bmpCtx.getFilePath();
        	BottomBar.notifyNavigator(this, bmpCtx.getFileName());
            setCurrentImage(bmpCtx);
        } else {
        	ThumbnailsActivity.lastPlayPath = "";
        	//BottomBar.notifyNavigator(this, this.getResources().getString(R.string.app_name));
            if (null != mPresentation) {
                ZoomRotateImageView imageView = mPresentation.getCurImage();
                if (null != imageView) {
                    imageView.setImageBitmap(null);
                }
                TextView textView = mPresentation.getFileName();
                if (null != textView) {
                    textView.setText(null);
                }
            }
        }
    }

    public void setCurrentImage(BitmapContext bmpCtx) {
        if (bmpCtx != null && bmpCtx.getFileName() != null
                && bmpCtx.getFilePath() != null) {
            final String filePath = bmpCtx.getFilePath();
            String uri = Scheme.FILE.wrap(filePath);
            ImageLoader.getInstance().displayImage(uri, mCurImage,
                    mDisplayImageOptions, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                FailReason failReason) {
                            super.onLoadingFailed(imageUri, view, failReason);
                            Toast.makeText(getApplicationContext(),
                                    R.string.decode_err_msg, Toast.LENGTH_SHORT)
                                    .show();

                            Bitmap bitmap = createGrayBmp(filePath);
                            mCurImage.setImageBitmap(bitmap);
                            if (null != mPresentation) {
                                ZoomRotateImageView imageView = mPresentation
                                        .getCurImage();
                                if (null != imageView) {
                                    imageView.setImageBitmap(bitmap);
                                }
                            }
                        }
                    });
            mFileName.setText(bmpCtx.getFileName());
            pageNo.setText(String.format("%d/%d", dataManager.getData().getPosision()+1, dataManager.getData().getCount()));
            updateRearContents(bmpCtx);
        } else {
            mCurImage.setImageBitmap(null);
            mFileName.setText(null);
            pageNo.setText(null);
            if (null != mPresentation) {
                ZoomRotateImageView imageView = mPresentation.getCurImage();
                if (null != imageView) {
                    imageView.setImageBitmap(null);
                }
                TextView textView = mPresentation.getFileName();
                if (null != textView) {
                    textView.setText(null);
                }
            }
        }
    }

    private void delayHideStatusBar() {
        if (null != mAHandler) {
            if (mAHandler.hasMessages(AHandler.HIDE_CURTAIN_MSG)) {
                mAHandler.removeMessages(AHandler.HIDE_CURTAIN_MSG);
            }

            if (mAHandler.hasMessages(AHandler.HIDE_STATUSBAR_MSG)) {
                mAHandler.removeMessages(AHandler.HIDE_STATUSBAR_MSG);
            }

            // hide after 5s
            if (!mAHandler.hasMessages(AHandler.HIDE_STATUSBAR_MSG)) {
                mAHandler.sendEmptyMessageDelayed(AHandler.HIDE_STATUSBAR_MSG,
                        ShowImgActivity.HIDE_LONG_TIMEOUT);
            }

            if (!mAHandler.hasMessages(AHandler.HIDE_CURTAIN_MSG)) {
                mAHandler.sendEmptyMessageDelayed(AHandler.HIDE_CURTAIN_MSG,
                        ShowImgActivity.HIDE_LONG_TIMEOUT + HIDE_SHORT_TIMEOUT);
            }
        }
    }

    private Bitmap createGrayBmp(String imageUri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeFile(imageUri, options);
        } catch (Exception e) {
            return null;
        }

        Bitmap bitmap = null;
        int width = options.outWidth;
        int height = options.outHeight;

        if (-1 == width || -1 == height) {
            bitmap = Bitmap.createBitmap(mScreenData.mSeenWidth - 200,
                    mScreenData.mSeenHeight, Bitmap.Config.ARGB_8888);
        } else {
            while (width > mScreenData.mSeenWidth
                    || height > mScreenData.mSeenHeight) {
                width = (int) (width * 0.98);
                height = (int) (height * 0.98);
            }

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        if (null != bitmap) {
            Canvas canvas = new Canvas(bitmap);
            if (null != canvas) {
                canvas.drawColor(Color.DKGRAY);
            }
        }

        return bitmap;
    }

    private void resetBackground(int color) {
        Object srv = getSystemService("statusbar");
        try {
            Class<?> cls = Class.forName("android.app.StatusBarManager");
            Method mtd = cls.getMethod("resetBackground", int.class);
            mtd.invoke(srv, color);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Logger.print("onConfigurationChanged");
    }

    public void jumpToGrid() {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Logger.print("onDestroy");
        mPlayStatus.onStore();
        StatusHub.removeLastStatusOwner();
        deinitData();

        if (null != mPresentation) {
            mPresentation.dismiss();
            mPresentation = null;
            getApplicationContext().setPicturePresentation(null);
        }

        unregisterReceiver(receiver);
        //unregisterReceiver(mMediaKeyListener);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Logger.print("onPause");
        mIsActivityPause = true;

        super.onPause();
        //resetBackground(PAUSE_BG_COLOR);
        mPlayStatus.onPause();

        if (null != mAHandler && mIsShownStatusbar) {
            if (mAHandler.hasMessages(AHandler.HIDE_CURTAIN_MSG)) {
                mAHandler.removeMessages(AHandler.HIDE_CURTAIN_MSG);
            }

            if (mAHandler.hasMessages(AHandler.HIDE_STATUSBAR_MSG)) {
                mAHandler.removeMessages(AHandler.HIDE_STATUSBAR_MSG);
            }
        }
    }

    @Override
    protected void onRestart() {
        Logger.print("onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Logger.print("onResume");
        mIsActivityPause = false;
        //SourceManager.acquireSource(this, SourceManager.SRC_NO.photo);
        super.onResume();
        /*
        StringBuffer title = new StringBuffer();
        //title.append(getString(R.string.title_media));
        //title.append(">");
        title.append(getString(R.string.app_name));
        Intent intent = new Intent(ACTION_UPDATE_ACTIVITY_TITLE);
        intent.putExtra(INTENT_EXTRA_ACTIVITY_TITLE, title.toString());
        sendBroadcast(intent);
        */
        BottomBar.notifyNavigator(this, mFileName.getText().toString());
                
        //resetBackground(RESUME_BG_COLOR);
        mPlayStatus.onResume();

        if (!dataManager.getData().isDataValid()) {
            //Toast.makeText(ShowImgActivity.this, R.string.no_pic_msg,
            //        Toast.LENGTH_SHORT).show();
        }

        if (null != mAHandler && mIsShownStatusbar) {
            // hide after 5s
            if (!mAHandler.hasMessages(AHandler.HIDE_STATUSBAR_MSG)) {
                mAHandler.sendEmptyMessageDelayed(AHandler.HIDE_STATUSBAR_MSG,
                        ShowImgActivity.HIDE_LONG_TIMEOUT + HIDE_SHORT_TIMEOUT);
            }

            if (!mAHandler.hasMessages(AHandler.HIDE_CURTAIN_MSG)) {
                mAHandler.sendEmptyMessageDelayed(AHandler.HIDE_CURTAIN_MSG,
                        ShowImgActivity.HIDE_LONG_TIMEOUT);
            }
        } else if (null != mAHandler && !mIsShownStatusbar) {
            // if Activity retreated to the background while in full screen
            // browsing, when it come back, the statusbar follow other app
            // will not disapear.
            // hide after 200ms
            if (!mAHandler.hasMessages(AHandler.HIDE_STATUSBAR_MSG)) {
                mAHandler.sendEmptyMessageDelayed(AHandler.HIDE_STATUSBAR_MSG,
                        HIDE_SHORT_TIMEOUT);
            }
        }

        if (getApplicationContext().isRearShow()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updatePresentation();
                }
            }, 500);
        }
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
            getApplicationContext().setPicturePresentation(null);
        }

        // Show a new presentation if needed.
        if (null == mPresentation && null != presentationDisplay) {
            // String value = SystemProperties.get("atc.rearproperty", "NULL");
            // if ("NULL".equals(value)) {
            // return;
            // } else if ("FRSS".equals(value)) {
            // mPresentation = new PicturePresentation(this,
            // presentationDisplay);
            // } else if ("FRDS".equals(value)) {
            // mPresentation = new PicturePresentation(getApplication(),
            // presentationDisplay);
            // }
            mPresentation = new PicturePresentation(this, presentationDisplay);
            getApplicationContext().setPicturePresentation(mPresentation);
            mPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mPresentation.show();
                if (null != mPresentation) {
                    View play = mPresentation.getPlay();
                    if (null != play) {
                        if(play instanceof ImageView){
                    		((ImageView)play).setImageResource(mPlayStatus.isPlaying() ? R.drawable.main_pause : R.drawable.main_play);
                    	}
                    	else if(play instanceof TextView){
                    		((TextView)play).setCompoundDrawablesWithIntrinsicBounds(0, mPlayStatus.isPlaying() ? R.drawable.main_pause : R.drawable.main_play,0,0);
                    		((TextView)play).setText(mPlayStatus.isPlaying() ?R.string.str_btn_pause:R.string.str_btn_play);
                    	}
                    }
                    ImageView shuffle = mPresentation.getShuffle();
                    if (null != shuffle) {
                        shuffle.setImageResource(mPlayStatus.isShuffleMode() ? R.drawable.shuffle
                                : R.drawable.shuffle_no);
                    }
                    ImageView circle = mPresentation.getCircle();
                    if (null != circle) {
                        circle.setImageResource(mPlayStatus.isLoopAll() ? R.drawable.play_all
                                : R.drawable.play_single);
                    }
                }
                getApplicationContext().setIsRearShow(true);
                // mCBMCtx.openRear(getComponentName().getPackageName(),
                // getComponentName().getClassName());
            } catch (WindowManager.InvalidDisplayException e) {

            } catch (Exception e) {

            }
        }

        if (dataManager.getData().isDataValid()) {
            BitmapContext bmpCtx =dataManager.getData().current();
            updateRearContents(bmpCtx);
        }
    }

    // update contents of presentation
    public void updateRearContents(BitmapContext bmpCtx) {
        if (null != mPresentation && null != bmpCtx
                && bmpCtx.getFileName() != null && bmpCtx.getFilePath() != null) {
            String uri = Scheme.FILE.wrap(bmpCtx.getFilePath());
            ImageLoader.getInstance().displayImage(uri,
                    mPresentation.getCurImage(), mDisplayImageOptions);
            mPresentation.getFileName().setText(bmpCtx.getFileName());
        }
    }

    private final DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {

        @Override
        public void onDismiss(DialogInterface dialog) {
            if (mPresentation == dialog) {
                mPresentation = null;
                getApplicationContext().setPicturePresentation(null);
            }
        }

    };

    @Override
    protected void onStart() {
        Logger.print("onStart");
        MediaButtonIntentReceiver.registerListener(mediaButtonCallback);
        if(null!=ThumbnailsActivity.instance && !ThumbnailsActivity.instance.isFinishing()){
        	ThumbnailsActivity.instance.setupMediaKeyReceiver(true);
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        Logger.print("onStop");
        MediaButtonIntentReceiver.unregisterListener(mediaButtonCallback);
        super.onStop();
        mAHandler.removeMessages(AHandler.HIDE_STATUSBAR_MSG);
        mAHandler.removeMessages(AHandler.HIDE_CURTAIN_MSG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    private void hideStatusbar() {
        //mCurImage.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    	
    }

    private void hideStatusbarAndCurtainView() {
        // This is a workaround, if hide CurtainView directly,
        // the animation would be blocked serveral times.
        mAHandler.sendEmptyMessageDelayed(AHandler.HIDE_CURTAIN_MSG,
                HIDE_SHORT_TIMEOUT);
        mAHandler.sendEmptyMessageDelayed(AHandler.HIDE_STATUSBAR_MSG,
                HIDE_SHORT_TIMEOUT * 2);
    }

    private void showStatusbarAndCurtainView() {
        mCurtainView.showMoveAnim();

        Animation fadeIn = AnimationUtils.loadAnimation(
                getApplicationContext(), R.animator.fade_in);
        topBar.startAnimation(fadeIn);
        topBar.setVisibility(View.VISIBLE);

        //mCurImage.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // hide after 5s
        if (!mAHandler.hasMessages(AHandler.HIDE_CURTAIN_MSG)) {
            mAHandler.sendEmptyMessageDelayed(AHandler.HIDE_CURTAIN_MSG,
                    HIDE_LONG_TIMEOUT);
        }

        if (!mAHandler.hasMessages(AHandler.HIDE_STATUSBAR_MSG)) {
            mAHandler.sendEmptyMessageDelayed(AHandler.HIDE_STATUSBAR_MSG,
                    HIDE_LONG_TIMEOUT + HIDE_SHORT_TIMEOUT);
        }
    }

    private void triggerShownStatusbarAndCurtainView() {
        mAHandler.removeMessages(AHandler.HIDE_STATUSBAR_MSG);
        mAHandler.removeMessages(AHandler.HIDE_CURTAIN_MSG);
        if (mIsShownStatusbar) {
            hideStatusbarAndCurtainView();

            if (null != mPresentation) {
                mPresentation.showBar(false);
            }
        } else {
            showStatusbarAndCurtainView();

            if (null != mPresentation) {
                mPresentation.showBar(true);
            }
        }
        mIsShownStatusbar = !mIsShownStatusbar;
    }
    private void  showStatusbarAndCurtain() {
        mAHandler.removeMessages(AHandler.HIDE_STATUSBAR_MSG);
        mAHandler.removeMessages(AHandler.HIDE_CURTAIN_MSG);
        if (!mIsShownStatusbar) {
            showStatusbarAndCurtainView();

            if (null != mPresentation) {
                mPresentation.showBar(true);
            }
            mIsShownStatusbar = true;
        }
    }

    @SuppressLint("HandlerLeak")
    public class AHandler extends Handler {
        public static final int HIDE_STATUSBAR_MSG = 0;
        public static final int HIDE_CURTAIN_MSG = 1;
        public static final int SHOW_CURTAIN_MSG = 2;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SHOW_CURTAIN_MSG:
            	showStatusbarAndCurtain();
            	break;
            case HIDE_CURTAIN_MSG:
            	if(topBar.getVisibility()!=View.INVISIBLE){
	                mCurtainView.hideMoveAnim();
	
	                if (null != mPresentation) {
	                    mPresentation.showBar(false);
	                }
	
	                Animation fadeOut = AnimationUtils.loadAnimation(
	                        getApplicationContext(), R.animator.fade_out);
	                topBar.startAnimation(fadeOut);
	                topBar.setVisibility(View.INVISIBLE);
            	}
                break;
            case HIDE_STATUSBAR_MSG:
                hideStatusbar();
                mIsShownStatusbar = false;
                break;
            }
        }
    }

    public PicturePresentation getPicturePresentation() {
        return mPresentation;
    }

    public final class PicturePresentation extends Presentation {

        private ZoomRotateImageView mmCurImage = null;
        private TextView mFileName = null;
        private CurtainView mCurtainView = null;
        private ImageView mCircle = null;
        private ImageView mShuffle = null;
        private View mPlay = null;
        private LayoutInflater mInflater = null;
        private View mView = null;

        public PicturePresentation(Context context, Display display) {
            super(context, display);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            mView = mInflater.inflate(R.layout.activity_main, null);
            setContentView(mView);
            /*
            Window win = getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            params.type = WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 26;
            win.setAttributes(params);
			*/
            mmCurImage = (ZoomRotateImageView) mView
                    .findViewById(R.id.cur_image);
            topBar = mView.findViewById(R.id.topBar);
            mFileName = (TextView) mView.findViewById(R.id.fileName);
            pageNo = (TextView) mView.findViewById(R.id.pageNo);
            this.mFileName.setPadding(0, mStatusBarHeight, 0, 0);
            mCurtainView = (CurtainView) mView.findViewById(R.id.curtain_view);
            /*
            mCircle = (ImageView) mView.findViewById(R.id.cicle);
            mShuffle = (ImageView) mView.findViewById(R.id.shuffle);
            */
            mPlay = (View) mView.findViewById(R.id.play);
        }

        public ZoomRotateImageView getCurImage() {
            return mmCurImage;
        }

        public TextView getFileName() {
            return mFileName;
        }

        public ImageView getCircle() {
            return mCircle;
        }

        public ImageView getShuffle() {
            return mShuffle;
        }

        public View getPlay() {
            return mPlay;
        }

        public CurtainView getCurtainView() {
            return mCurtainView;
        }

        public void showBar(boolean isShow) {
            if (isShow) {
                topBar.setVisibility(View.VISIBLE);
                mCurtainView.showMoveAnim();
                mCurtainView.setVisibility(View.VISIBLE);
            } else {
                topBar.setVisibility(View.INVISIBLE);
                mCurtainView.hideMoveAnim();
                mCurtainView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            currentMatrix.set(mCurImage.getImageMatrix());
            currentImgStatus = mCurImage.getStatus();
            startPoint.set(event.getX(), event.getY());
            break;
        case MotionEvent.ACTION_MOVE:
            break;
        case MotionEvent.ACTION_UP:
            delayHideStatusBar();
            break;
        default:
            break;
        }
        
        mDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    private class PicGestureListener extends
            GestureDetector.SimpleOnGestureListener {
        private int minDistance = 30;
        private int minVelocity = 20;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
            if (!mCurImage.isScrollEnabled()
                    || (currentImgStatus.getWidth() < mScreenData.mSeenWidth && currentImgStatus
                            .getHeight() < mScreenData.mSeenHeight)
                    || !mCurImage.getStatusRect().contains((int) startPoint.x,
                            (int) startPoint.y)) {
                return false;
            }

            float dx = e2.getX() - startPoint.x;
            float dy = e2.getY() - startPoint.y;
            matrix.set(currentMatrix);
            matrix.postTranslate(dx, dy);
            mCurImage.setImageMatrix(matrix);

            imgStatus = mCurImage.getStatus();
            float deltaX = 0;
            float deltaY = 0;
            if (imgStatus.getWidth() >= mScreenData.mSeenWidth) {
                if (imgStatus.getLeft() > 0) {
                    deltaX = -imgStatus.getLeft();
                }

                if (imgStatus.getRight() < mScreenData.mSeenWidth) {
                    deltaX = mScreenData.mSeenWidth - imgStatus.getRight();
                }
            }

            if (imgStatus.getHeight() > mScreenData.mSeenHeight) {
                if (imgStatus.getTop() > 0) {
                    deltaY = -imgStatus.getTop();
                }

                if (imgStatus.getBottom() < mScreenData.mSeenHeight) {
                    deltaY = mScreenData.mSeenHeight - imgStatus.getBottom();
                }
            }

            if (imgStatus.getWidth() < mScreenData.mSeenWidth) {
                deltaX = mScreenData.mSeenWidth * 0.5f - imgStatus.getRight()
                        + 0.5f * imgStatus.getWidth();
            }

            if (imgStatus.getHeight() < mScreenData.mSeenHeight) {
                deltaY = mScreenData.mSeenHeight * 0.5f - imgStatus.getBottom()
                        + 0.5f * imgStatus.getHeight();
            }

            matrix.postTranslate(deltaX, deltaY);
            mCurImage.setImageMatrix(matrix);

            if (null != mPresentation) {
                ZoomRotateImageView imageView = mPresentation.getCurImage();
                if (null != imageView) {
                    imageView.setImageMatrix(mCurImage.getImageMatrix());
                }
            }

            if (null != mPlayStatus) {
                mPlayStatus.pause();
            }

            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
            if (mCurImage.isScrollEnabled()) {
                return false;
            }

            if (e1.getX() - e2.getX() > minDistance
                    && Math.abs(velocityX) > minVelocity) {
                // to left
                if (dataManager.getData().isDataValid()) {
                    BitmapContext bmpCtx = null;
                    bmpCtx = dataManager.getData().next();
                    if (bmpCtx != null) {
                        setCurrentImageASync(bmpCtx);
                    }
                }
            } else if (e2.getX() - e1.getX() > minDistance
                    && Math.abs(velocityX) > minVelocity) {
                // to right
                if (dataManager.getData().isDataValid()) {
                    BitmapContext bmpCtx = null;
                    bmpCtx = dataManager.getData().prev();
                    if (bmpCtx != null) {
                        setCurrentImageASync(bmpCtx);
                    }
                }
            }

            if (null != mPlayStatus) {
                mPlayStatus.pause();
            }

            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            triggerShownStatusbarAndCurtainView();
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mCurImage.zoomValid(true)) {
                Rect rect = mCurImage.getStatusRect();
                if (rect.width() * 2 > mScreenData.mSeenWidth
                        && rect.height() * 2 > mScreenData.mSeenHeight) {
                    mCurImage
                            .zoomOutAccordPoint((int) e.getX(), (int) e.getY());
                } else {
                    mCurImage.zoomOut();
                }
            } else {
                mCurImage.ZoomUndo();
            }

            if (null != mPresentation) {
                ZoomRotateImageView imageView = mPresentation.getCurImage();
                if (null != imageView) {
                    imageView.setImageMatrix(mCurImage.getImageMatrix());
                }
            }

            if (null != mPlayStatus) {
                mPlayStatus.pause();
            }

            return false;
        }
    }

    private void initReceiver() {
    	receiver = new Receiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_QB_POWEROFF);
        filter.addAction(ACTION_QB_POWERON);
        filter.addAction(ACTION_QB_PREPOWEROFF);
        filter.addAction(ACTION_CLOSEREAR);
        filter.addAction(YeconConstants.ACTION_QUIT_APK);
        registerReceiver(receiver, filter);
        
//        filter = new IntentFilter();
//        filter.addAction(MCU_ACTION_MEDIA_NEXT);
//        filter.addAction(MCU_ACTION_MEDIA_PREVIOUS);
//        filter.addAction(MCU_ACTION_MEDIA_PLAY_PAUSE);
//        registerReceiver(mMediaKeyListener, filter); 
    }

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_QB_PREPOWEROFF.equals(action)) {
                if (null != mPresentation) {
                    mPresentation.dismiss();
                    mPresentation = null;
                    getApplicationContext().setPicturePresentation(null);
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
    
//    private BroadcastReceiver mMediaKeyListener = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            synchronized (this) {
//                if(SourceManager.lastSource()!=SourceManager.SRC_NO.photo){
//					return;
//				}
//                String action = intent.getAction();
//                if (action.equals(MCU_ACTION_MEDIA_NEXT)) {
//                	mTouchProcess.keyActionNext();
//                } else if (action.equals(MCU_ACTION_MEDIA_PREVIOUS)) {
//                	mTouchProcess.keyActionPrevious();
//                } else if (action.equals(MCU_ACTION_MEDIA_PLAY_PAUSE)) {
//                	mTouchProcess.keyActionPlay();
//                }
//            }
//        }
//    };
    
    MediaButtonIntentReceiver.Callback mediaButtonCallback = new MediaButtonIntentReceiver.Callback() {

		@Override
		public void onMediaKey(int action, int keycode) {
			// TODO Auto-generated method stub
			if (action == KeyEvent.ACTION_DOWN) {
				switch (keycode) {
				case KeyEvent.KEYCODE_MEDIA_NEXT:
					mTouchProcess.keyActionNext();
					break;
				case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
					mTouchProcess.keyActionPrevious();
					break;
				case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
					mTouchProcess.keyActionPlay();
					break;
				case KeyEvent.KEYCODE_MEDIA_PLAY:
					
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
