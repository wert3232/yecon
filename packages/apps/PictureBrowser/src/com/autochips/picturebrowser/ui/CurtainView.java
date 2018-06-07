package com.autochips.picturebrowser.ui;

import com.autochips.picturebrowser.R;
import com.autochips.picturebrowser.utils.BaseTools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class CurtainView extends FrameLayout {
    private Context mContext = null;
    private Scroller mScroller = null;
    private boolean bOpen = true;
    private boolean bMove = false;
    private int mCurtainHeight = 0;
    private static final int SHOW_DURATION = 200;
    private static final int HIDE_DURATION = 200;

    public CurtainView(Context context) {
        super(context);
        init(context);
    }

    public CurtainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CurtainView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        Interpolator interpolator = new LinearInterpolator();
        mScroller = new Scroller(context, interpolator);
        mCurtainHeight = BaseTools.dp2px(mContext, R.dimen.curtain_hight);
    }

    public void showMoveAnim() {
        if (bOpen) {
            return;
        }
        int fromY = -mCurtainHeight;
        int duration = SHOW_DURATION;
        if (bMove) {
            duration = mScroller.getDuration();
            mScroller.abortAnimation();
            fromY = mScroller.getCurrY();
        }
        mScroller.startScroll(0, fromY, 0, mCurtainHeight, duration);
        bOpen = !bOpen;
        invalidate();
    }

    public void hideMoveAnim() {
        if (!bOpen) {
            return;
        }
        int toY = -mCurtainHeight;
        int duration = HIDE_DURATION;
        if (bMove) {
            duration = mScroller.getDuration();
            mScroller.abortAnimation();
            toY = mScroller.getCurrY();
        }
        mScroller.startScroll(0, 0, 0, toY, duration);
        bOpen = !bOpen;
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
            bMove = true;
        } else {
            bMove = false;
        }
        super.computeScroll();
    }
}
