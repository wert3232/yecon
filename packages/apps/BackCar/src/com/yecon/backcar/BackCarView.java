
package com.yecon.backcar;

import android.content.Context;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Surface;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.autochips.backcar.BackCar;

import android.os.SystemProperties;
import android.util.AttributeSet;
import android.util.Log;

public class BackCarView extends SurfaceView {

    private String TAG = "BackCarView";

    public static SurfaceHolder mSurfaceHolder = null;

    // private Paint mUIPaint = null;
    // private Bitmap mCarBmp = null;
    // private int mDistance = -1;
    // private Rect mDrawRect = new Rect();

    @SuppressWarnings("deprecation")
    private void init() {
        // mUIPaint = new Paint();
        //
        // mCarBmp = getResource(R.drawable.car, 0x89);

        Log.i(TAG, "BackCarView - BackCarView object init");

        getHolder().addCallback(mSHCallback);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @SuppressWarnings("unused")
    private Bitmap getResource(int id, int alpha) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inMutable = true;
        opts.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id, opts);

        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Log.i(TAG, "bitmap size - w = " + w + ", h = " + h);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int color = bmp.getPixel(j, i) & 0x00FFFFFF;

                if (color == 0) {
                    bmp.setPixel(j, i, 0);
                }
                else if (alpha >= 0) {
                    bmp.setPixel(j, i, color | (alpha << 24));
                }
                // Log.i(TAG, "color -> " + Integer.toHexString(color));
            }
        }

        return bmp;
    }

    public BackCarView(Context context) {
        super(context);

        init();
    }

    public BackCarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public BackCarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Log.i(TAG, "BackCarView - BackCarView object create");

        init();
    }

    public void update(int dist) {
        Log.i(TAG, "BackCarView - update - start");

        // mDistance = dist;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.i(TAG, "BackCarView - onDraw - start");

        // if (mSurfaceHolder != null) {
        // canvas = mSurfaceHolder.lockCanvas();
        // if (canvas != null) {
        // canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
        // mSurfaceHolder.unlockCanvasAndPost(canvas);
        // }
        // }

        // mDrawRect.set(0, 0, mCarBmp.getWidth(), mCarBmp.getHeight());
        // Log.i(TAG, "onDraw - car bitmap size: w = " + mCarBmp.getWidth() +
        // ", h = "
        // + mCarBmp.getHeight());
        // canvas.drawBitmap(mCarBmp, null, mDrawRect, mUIPaint);
        // int numbmp_id = -1;
        // if (mDistance == 9) {
        // numbmp_id = R.drawable.num_9;
        // } else if (mDistance == 8) {
        // numbmp_id = R.drawable.num_8;
        // } else if (mDistance == 7) {
        // numbmp_id = R.drawable.num_7;
        // } else if (mDistance == 6) {
        // numbmp_id = R.drawable.num_6;
        // } else if (mDistance == 5) {
        // numbmp_id = R.drawable.num_5;
        // } else if (mDistance == 4) {
        // numbmp_id = R.drawable.num_4;
        // } else if (mDistance == 3) {
        // numbmp_id = R.drawable.num_3;
        // } else if (mDistance == 2) {
        // numbmp_id = R.drawable.num_2;
        // } else if (mDistance == 1) {
        // numbmp_id = R.drawable.num_1;
        // } else if (mDistance == 0) {
        // // numbmp_id = R.drawable.num_0;
        // }
        // if (numbmp_id != -1) {
        //
        // Bitmap num_bmp = getResource(numbmp_id, -1);
        // mDrawRect.set(90, 430, 90 + num_bmp.getWidth(), 430 +
        // num_bmp.getHeight());
        // canvas.drawBitmap(num_bmp, null, mDrawRect, mUIPaint);
        //
        // Bitmap m_bmp = getResource(R.drawable.m, -1);
        // mDrawRect.left = 120;
        // mDrawRect.right = mDrawRect.left + m_bmp.getWidth();
        // canvas.drawBitmap(m_bmp, null, mDrawRect, mUIPaint);
        // }
        // else if (mDistance == 0) {
        // Bitmap please_bmp = getResource(R.drawable.please, -1);
        // Bitmap stop_bmp = getResource(R.drawable.stop, -1);
        // Bitmap carpic_bmp = getResource(R.drawable.carpic, -1);
        // Bitmap exclamation_bmp = getResource(R.drawable.exclamation, -1);
        // mDrawRect.set(40, 430, 40 + please_bmp.getWidth(), 430 +
        // please_bmp.getHeight());
        // canvas.drawBitmap(please_bmp, null, mDrawRect, mUIPaint);
        // mDrawRect.left = 90;
        // mDrawRect.right = mDrawRect.left + stop_bmp.getWidth();
        // canvas.drawBitmap(stop_bmp, null, mDrawRect, mUIPaint);
        // mDrawRect.left = 140;
        // mDrawRect.right = mDrawRect.left + carpic_bmp.getWidth();
        // canvas.drawBitmap(carpic_bmp, null, mDrawRect, mUIPaint);
        // mDrawRect.left = 190;
        // mDrawRect.right = mDrawRect.left + exclamation_bmp.getWidth();
        // canvas.drawBitmap(exclamation_bmp, null, mDrawRect, mUIPaint);
        // }
    }

    private SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            Log.i(TAG, "mSHCallback - surfaceChanged, w = " + w + ", h = " + h);

            // Canvas canvas = holder.lockCanvas();
            // canvas.drawARGB(0xFF, 0, 0, 0);
            // canvas.drawColor(0xFF000000);
            // holder.unlockCanvasAndPost(canvas);
            Surface surface = holder.getSurface();
            if (null != surface) {
                BackCar.setVideoSurface(surface);
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "mSHCallback - surfaceCreated");
            BackCar.setVideoSurface(null);
            Surface surface = holder.getSurface();
            mSurfaceHolder = holder;
            if (null != surface) {
                BackCar.setVideoSurface(surface);
                BackCarSetMirror();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.w(TAG, "mSHCallback - surfaceDestroyed");
            BackCar.setVideoSurface(null);
            mSurfaceHolder = null;
        }
    };

    public void BackCarSetMirror()
    {
        int mirrorIndex = 0;
        mirrorIndex = Integer.parseInt(SystemProperties.get(BackCarService.PERSYS_BACKCAR_MIRROR,
                mirrorIndex + ""));
        BackCar.SetMirror(mirrorIndex);
    }
}
