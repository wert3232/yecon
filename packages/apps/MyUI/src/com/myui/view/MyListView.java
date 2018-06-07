package com.myui.view;


import com.myui.view.suppport.ContextWrapperEdgeEffect;
import com.myui.view.suppport.MulticastOnScrollListener;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

public class MyListView extends android.widget.ListView {
    private OnScrollListener mLegacyOnScrollListener;
    private final MulticastOnScrollListener mMulticastOnScrollListener = new MulticastOnScrollListener();

    public MyListView(Context context) {
        this(context, null);
    }
    public MyListView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.listViewStyle);
    }
    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(new ContextWrapperEdgeEffect(context), attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        int color = context.getResources().getColor(R.color.default_edgeeffect_color);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Jazzy, defStyle, 0);
            color = a.getColor(R.styleable.Jazzy_edgeEffectColor, color);
            a.recycle();
        }
        setEdgeEffectColor(color);
    }

    public void setOnScrollListener(OnScrollListener listener) {
        if (listener != null) {
            checkPrecondition(mLegacyOnScrollListener == null);
            mLegacyOnScrollListener = listener;
            addOnScrollListener(mLegacyOnScrollListener);
        } else if (mLegacyOnScrollListener != null) {
            removeOnScrollListener(mLegacyOnScrollListener);
            mLegacyOnScrollListener = null;
        }
    }

    public void setEdgeEffectColor(int edgeEffectColor) {
        ((ContextWrapperEdgeEffect) getContext()).setEdgeEffectColor(edgeEffectColor);
    }

    public void addOnScrollListener(OnScrollListener listener) {
        mMulticastOnScrollListener.add(listener);
    }

    public void clearOnScrollListeners() {
        mMulticastOnScrollListener.clear();
    }

    public void removeOnScrollListener(OnScrollListener listener) {
        mMulticastOnScrollListener.remove(listener);
    }

    void checkPrecondition(boolean state) {
        if (!state) {
            throw new IllegalStateException();
        }
    }
}
