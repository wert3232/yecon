package com.can.ui.draw;

import com.can.activity.R;
import com.can.parser.DDef.RadarInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.util.LruCache;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * ClassName:RadarSurface
 * 
 * @function:雷达画图
 * @author Kim
 * @Date: 2016-6-13 上午10:54:46
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class RadarSurface extends SurfaceView implements SurfaceHolder.Callback {

	private int milayoutId = -1;
	private Paint mObjPaint = null;
	private static RadarInfo mRadarInfo = null;
	private DrawThread mObjDrawThread = null;
	private SurfaceHolder mObjSurfaceHolder = null;

	private LruCache<Integer, Bitmap> mObjMemoryCache;

	public RadarSurface(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mObjSurfaceHolder = this.getHolder();
		mObjSurfaceHolder.addCallback(this);
		setZOrderOnTop(true);
	}

	public RadarSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mObjSurfaceHolder = this.getHolder();
		mObjSurfaceHolder.addCallback(this);

		mObjPaint = new Paint(); 
		mObjSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
		setZOrderOnTop(true);
	}

	public void setlayoutId(int iId) {
		milayoutId = iId;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mObjSurfaceHolder = this.getHolder();
		mObjSurfaceHolder.addCallback(this);
		mObjSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);

		// 初始化缓存空间
		int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
		int mCacheSize = maxMemory / 8;

		mObjMemoryCache = new LruCache<Integer, Bitmap>(mCacheSize) {

			// 必须重写此方法，来测量Bitmap的大小
			@Override
			protected int sizeOf(Integer key, Bitmap value) {
				// TODO Auto-generated method stub
				return value.getRowBytes() * value.getHeight();
			}
		};

		startThread();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		stopThread();
		clearCache();
	}

	/**
	 * 
	 * <p>
	 * Title: clearCache
	 * </p>
	 * <p>
	 * Description: 清除缓存
	 * </p>
	 */
	public void clearCache() {

		if (mObjMemoryCache != null) {

			if (mObjMemoryCache.size() > 0) {

				mObjMemoryCache.evictAll();
			}

			mObjMemoryCache = null;
		}
	}

	/**
	 * 
	 * <p>
	 * Title: startThread
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 */
	public void startThread() {

		if (mObjDrawThread == null) {

			mObjDrawThread = new DrawThread(mObjSurfaceHolder);

		} else if (mObjDrawThread != null && !mObjDrawThread.isAlive()) {

			mObjDrawThread = new DrawThread(mObjSurfaceHolder);
		}

		mObjDrawThread.mbDraw = true;
		mObjDrawThread.start();
	}

	/**
	 * 
	 * <p>
	 * Title: stopThread
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 */
	public void stopThread() {
		if (mObjDrawThread != null) {
			mObjDrawThread.mbDraw = false;
			this.getHandler().removeCallbacks(mObjDrawThread);
			mObjDrawThread.interrupt();
			mObjDrawThread = null;
		}
	}

	/**
	 * 
	 * <p>
	 * Title: getBitmap2Cache
	 * </p>
	 * <p>
	 * Description: 从缓存中获取图片
	 * </p>
	 * 
	 * @param key
	 * @return
	 */
	public synchronized Bitmap getBitmap2Cache(Integer key) {

		Bitmap bitmap = mObjMemoryCache.get(key);
		if (key != null) {
			return bitmap;
		}

		return null;
	}

	/**
	 * 
	 * <p>
	 * Title: addBitmap2Cache
	 * </p>
	 * <p>
	 * Description: 添加图片到缓存
	 * </p>
	 * 
	 * @param key
	 * @param bitmap
	 */
	public synchronized void addBitmap2Cache(Integer key, Bitmap bitmap) {

		if (getBitmap2Cache(key) == null && bitmap != null) {
			mObjMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * 
	 * <p>
	 * Title: setRadarInfo
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param radarInfo
	 */
	public static void setRadarInfo(RadarInfo radarInfo) {
		mRadarInfo = radarInfo;
	}

	/**
	 * 
	 * <p>
	 * Title: getRadarInfo
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return
	 */
	public static RadarInfo getRadarInfo() {
		return mRadarInfo;
	}

	/**
	 * 
	 * ClassName:DrawThread
	 * 
	 * @function:画图线程
	 * @author Kim
	 * @Date: 2016-6-14 上午10:58:54
	 * @Copyright: Copyright (c) 2016
	 * @version 1.0
	 */
	public class DrawThread extends Thread {

		public boolean mbDraw = false;
		private Canvas mObjCanvas = null;
		private SurfaceHolder mObjholder = null;

		public DrawThread(SurfaceHolder surfaceHolder) {

			this.mObjholder = surfaceHolder;
		}

		public void doDraw2Surface(byte byDis, int[] data, float fleft,
				float ftop) {
			Integer integerKey = 0;
			if (byDis <= data.length) {
				integerKey = (byDis == 0) ? 0 : (data[byDis - 1]);

				if (integerKey != 0) {
					Bitmap bitmap = getBitmap2Cache(integerKey);
					if (bitmap == null) {
						bitmap = BitmapFactory.decodeResource(getResources(),
								integerKey);

						if (bitmap != null) {
							addBitmap2Cache(integerKey, bitmap);
						}
					}

					if (bitmap != null) {
						mObjCanvas.drawBitmap(bitmap, fleft, ftop, mObjPaint);
					}
				}
			}
		}

		public int getResXY(int id) {
			return (int) getResources().getDimension(id);
		}

		public void Draw() {

			if (mRadarInfo != null && milayoutId != -1) {

				if (milayoutId == R.layout.small_radar) {
					mObjCanvas = mObjholder.lockCanvas(new Rect(33, 56,
							176 + 49, 357 + 70));

					if (mObjCanvas == null) {
						return;
					}

					mObjCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
					mObjCanvas
							.drawBitmap(BitmapFactory.decodeResource(
									getResources(), R.drawable.car), 67, 115,
									mObjPaint);

					// 前左
					doDraw2Surface(mRadarInfo.mFrontLeftDis, ResDef.mSmFLImage,
							44, 70);

					// 前左中
					doDraw2Surface(mRadarInfo.mFrontLeftCenterDis,
							ResDef.mSmFLCImage, 57, 32);

					// 前右中
					doDraw2Surface(mRadarInfo.mFrontRightCenterDis,
							ResDef.mSmFRCImage, 95, 32);

					// 前右
					doDraw2Surface(mRadarInfo.mFrontRightDis,
							ResDef.mSmFRImage, 107, 70);

					// 后左
					doDraw2Surface(mRadarInfo.mBackLeftDis, ResDef.mSmBLImage,
							44, 218);

					// 后左中
					doDraw2Surface(mRadarInfo.mBackLeftCenterDis,
							ResDef.mSmBLCImage, 57, 226);

					// 后右中
					doDraw2Surface(mRadarInfo.mBackRightCenterDis,
							ResDef.mSmBRCImage, 95, 226);

					// 后右
					doDraw2Surface(mRadarInfo.mBackRightDis, ResDef.mSmBRImage,
							107, 217);

					// 上左
					doDraw2Surface(mRadarInfo.mLeftUpDis, ResDef.mSmLUImage,
							30, 90);

					// 上左中
					doDraw2Surface(mRadarInfo.mLeftUpCenterDis,
							ResDef.mSmLUCImage, 28, 131);

					// 上右中
					doDraw2Surface(mRadarInfo.mLeftDnCenterDis,
							ResDef.mSmLDCImage, 28, 171);

					// 上右
					doDraw2Surface(mRadarInfo.mLeftDnDis, ResDef.mSmLDImage,
							29, 192);

					// 下左
					doDraw2Surface(mRadarInfo.mRightUpDis, ResDef.mSmRUImage,
							118, 89);

					// 下左中
					doDraw2Surface(mRadarInfo.mRightUpCenterDis,
							ResDef.mSmRUCImage, 124, 130);

					// 下右中
					doDraw2Surface(mRadarInfo.mRightDnCenterDis,
							ResDef.mSmRDCImage, 124, 171);

					// 下左
					doDraw2Surface(mRadarInfo.mRightDnDis, ResDef.mSmRDImage,
							118, 191);

				} else if (milayoutId == R.layout.big_radar) {
					mObjCanvas = mObjholder.lockCanvas();

					if (mObjCanvas == null) {
						return;
					}
					mObjCanvas.drawColor(Color.BLACK, Mode.CLEAR);
					mObjCanvas.drawBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.xbxk), 0, 0, mObjPaint);

					// 前左
					doDraw2Surface(mRadarInfo.mFrontLeftDis, ResDef.mBgFLImage,
							getResXY(R.dimen.big_radar_fl_x),
							getResXY(R.dimen.big_radar_fl_y));

					// 前左中
					doDraw2Surface(mRadarInfo.mFrontLeftCenterDis,
							ResDef.mBgFLCImage,
							getResXY(R.dimen.big_radar_flc_x),
							getResXY(R.dimen.big_radar_flc_y));

					// 前右中
					doDraw2Surface(mRadarInfo.mFrontRightCenterDis,
							ResDef.mBgFRCImage,
							getResXY(R.dimen.big_radar_frc_x),
							getResXY(R.dimen.big_radar_frc_y));

					// 前右
					doDraw2Surface(mRadarInfo.mFrontRightDis,
							ResDef.mBgFRImage,
							getResXY(R.dimen.big_radar_fr_x),
							getResXY(R.dimen.big_radar_fr_y));

					// 后左
					doDraw2Surface(mRadarInfo.mBackLeftDis, ResDef.mBgBLImage,
							getResXY(R.dimen.big_radar_bl_x),
							getResXY(R.dimen.big_radar_bl_y));

					// 后左中
					doDraw2Surface(mRadarInfo.mBackLeftCenterDis,
							ResDef.mBgBLCImage,
							getResXY(R.dimen.big_radar_blc_x),
							getResXY(R.dimen.big_radar_blc_y));

					// 后右中
					doDraw2Surface(mRadarInfo.mBackRightCenterDis,
							ResDef.mBgBRCImage,
							getResXY(R.dimen.big_radar_brc_x),
							getResXY(R.dimen.big_radar_brc_y));

					// 后右
					doDraw2Surface(mRadarInfo.mBackRightDis, ResDef.mBgBRImage,
							getResXY(R.dimen.big_radar_br_x),
							getResXY(R.dimen.big_radar_br_y));

					// 上左
					//doDraw2Surface(mRadarInfo.mLeftUpDis, ResDef.mBgULImage,
							//getResXY(R.dimen.big_radar_ul_x),
							//getResXY(R.dimen.big_radar_ul_y));

					// 上左中
					//doDraw2Surface(mRadarInfo.mLeftUpCenterDis,
							//ResDef.mBgULCImage,
							//getResXY(R.dimen.big_radar_ulc_x),
							//getResXY(R.dimen.big_radar_ulc_y));

					// 上右中
					//doDraw2Surface(mRadarInfo.mLeftDnCenterDis,
							//ResDef.mBgURCImage,
							//getResXY(R.dimen.big_radar_urc_x),
							//getResXY(R.dimen.big_radar_urc_y));

					// 上左
					//doDraw2Surface(mRadarInfo.mLeftDnDis, ResDef.mBgURImage,
							//getResXY(R.dimen.big_radar_ur_x),
							//getResXY(R.dimen.big_radar_ur_y));

					// 下左
					//doDraw2Surface(mRadarInfo.mRightUpDis, ResDef.mBgDLImage,
							//getResXY(R.dimen.big_radar_dl_x),
							//getResXY(R.dimen.big_radar_dl_y));

					// 下左中
					//doDraw2Surface(mRadarInfo.mRightUpCenterDis,
							//ResDef.mBgDLCImage,
							//getResXY(R.dimen.big_radar_dlc_x),
							//getResXY(R.dimen.big_radar_dlc_y));

					// 下右中
					//doDraw2Surface(mRadarInfo.mRightDnCenterDis,
							//ResDef.mBgDRCImage,
							//getResXY(R.dimen.big_radar_drc_x),
							//getResXY(R.dimen.big_radar_drc_y));

					// 下左
					//doDraw2Surface(mRadarInfo.mRightDnDis, ResDef.mBgDRImage,
							//getResXY(R.dimen.big_radar_dr_x),
							//getResXY(R.dimen.big_radar_dr_y));
				}
			} else {

			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (mbDraw) {
				try {

					synchronized (mObjholder) {
						Draw();
					}

					Thread.sleep(10);

				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {

					if (mObjCanvas != null) {

						mObjholder.unlockCanvasAndPost(mObjCanvas);
					}
				}
			}
		}
	}
}
