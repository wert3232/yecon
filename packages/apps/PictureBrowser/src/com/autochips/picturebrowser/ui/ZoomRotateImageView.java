package com.autochips.picturebrowser.ui;

import com.autochips.picturebrowser.utils.ImageStatus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class ZoomRotateImageView extends ImageView {
    private float mDh = 0.0f;
    private float mDw = 0.0f;
    private Bitmap mSrcBmp = null;
    private int mZoomLimit = 0;
    private static final int ZOOM_OUT_LIMITS = 2;
    private static final int ZOOM_IN_LIMITS = -2;
    private float mZoomRatio = 1.0f;
    private static final float ZOOM_OUT_STEP = 2.0f;
    private static final float ZOOM_IN_STEP = 0.5f;
    private float mRotateDegree = 0.0f;
    private static final float ROTATE_LEFT = -90.0f;
    private static final float ROTATE_RIGHT = 90.0f;
	private static final String TAG = "ZoomRotateImageView";

    public ZoomRotateImageView(Context context) {
        super(context);
    }

    public ZoomRotateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomRotateImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mSrcBmp = bm;
        mZoomLimit = 0;
        super.setImageBitmap(bm);
        fitScreen();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mDh = getHeight();
            mDw = getWidth();
            fitScreen();
        }
    }

    public void zoomOut() {
        if (!zoomValid(true)) {
            return;
        }
        mZoomLimit++;
        mZoomRatio = ZOOM_OUT_STEP;
        doZoom();
    }

    public void zoomOutAccordPoint(int x, int y) {
        if (!zoomValid(true)) {
            return;
        }
        mZoomLimit++;
        mZoomRatio = ZOOM_OUT_STEP;
        doZoom(x, y);
    }

    public void zoomIn() {
        if (!zoomValid(false)) {
            return;
        }
        mZoomLimit--;
        mZoomRatio = ZOOM_IN_STEP;
        doZoom();
    }

    public void rotateLeft() {
        if (!rotateValid()) {
            return;
        }
        mRotateDegree = ROTATE_LEFT;
        doRotate();
    }

    public void rotateRight() {
        if (!rotateValid()) {
            return;
        }
        mRotateDegree = ROTATE_RIGHT;
        doRotate();
    }

    public void fitScreen() {
        if (mSrcBmp == null) {
            return;
        }
        Matrix mtx = getImageMatrix();
        float imgH = mSrcBmp.getHeight();
        float imgW = mSrcBmp.getWidth();        
        boolean needZoomIn = (mDh <= imgH) || (mDw <= imgW);
        Log.i(TAG, "mDh:"+ mDh + " mDw:"+ mDw  + " imgH:"+ imgH  + " imgW:"+ imgW + "  needZoomIn:"+needZoomIn);
        mtx.reset();
        float zoomRatio = 1.0f;
        if (needZoomIn) {
            float zoomH = mDh / imgH;
            float zoomW = mDw / imgW;
            zoomRatio = (zoomH > zoomW) ? zoomW : zoomH;
            if(zoomRatio==1){
            	zoomRatio = 0.99f;
            }
            mtx.postScale(zoomRatio, zoomRatio);
        }
        center(mtx, zoomRatio);
        setImageMatrix(mtx);
    }

    private void center(Matrix mtx, float zoomRatio) {
        float imgH = mSrcBmp.getHeight() * zoomRatio;
        float imgW = mSrcBmp.getWidth() * zoomRatio;
        float centerH = (mDh - imgH) / 2;
        float centerW = (mDw - imgW) / 2;
        mtx.postTranslate(centerW, centerH);
    }

    private boolean rotateValid() {
        if (mSrcBmp == null) {
            return false;
        }
        return true;
    }

    public boolean zoomValid(boolean zoomOut) {
        do {
            if (mSrcBmp == null) {
                break;
            }
            if (zoomOut && ZOOM_OUT_LIMITS < (mZoomLimit + 1)) {
                break;
            }
            if (!zoomOut && (mZoomLimit - 1) < ZOOM_IN_LIMITS) {
                break;
            }
            return true;
        } while (false);
        return false;
    }

    private void doZoom() {
        float centerH = mDh / 2;
        float centerW = mDw / 2;
        Matrix mtx = getImageMatrix();
        mtx.postScale(mZoomRatio, mZoomRatio, centerW, centerH);
        setImageMatrix(mtx);
        invalidate();

        if (mZoomLimit == 0) {
            fitScreen();
        }
    }

    private void doZoom(int x, int y) {
        Matrix mtx = getImageMatrix();
        mtx.postScale(mZoomRatio, mZoomRatio, x, y);
        setImageMatrix(mtx);
        invalidate();

        if (mZoomLimit == 0) {
            fitScreen();
        }
    }

    public void ZoomUndo() {
        mZoomLimit = 0;
        mZoomRatio = 0.25f;
        doZoom();
    }

    public boolean isScrollEnabled() {
        return mZoomLimit > 0;
    }

    public ImageStatus getStatus() {
        Matrix matrix = this.getImageMatrix();
        Rect rect = this.getDrawable().getBounds();
        float[] values = new float[9];
        matrix.getValues(values);
        ImageStatus status = new ImageStatus();
        status.setLeft(values[2]);
        status.setTop(values[5]);
        status.setRight(status.getLeft() + rect.width() * values[0]);
        status.setBottom(status.getTop() + rect.height() * values[0]);
        return status;
    }

    public Rect getStatusRect() {
        Matrix matrix = this.getImageMatrix();
        Rect rect = this.getDrawable().getBounds();
        float[] values = new float[9];
        matrix.getValues(values);
        Rect retRect = new Rect((int) values[2], (int) values[5],
                (int) (values[2] + rect.width() * values[0]),
                (int) (values[5] + rect.height() * values[0]));
        return retRect;
    }

    private void doRotate() {
        float centerH = mDh / 2;
        float centerW = mDw / 2;
        Matrix mtx = getImageMatrix();
        mtx.postRotate(mRotateDegree, centerW, centerH);
        setImageMatrix(mtx);
        invalidate();
    }

    @Override
    public ScaleType getScaleType() {
        return ScaleType.CENTER_INSIDE;
    }

}
