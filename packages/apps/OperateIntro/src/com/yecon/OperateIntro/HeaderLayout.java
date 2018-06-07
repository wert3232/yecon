package com.yecon.OperateIntro;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HeaderLayout extends LinearLayout {
    private static final String TextView = null;
    private LayoutInflater mInflater;
    private View mHeader;
    private View mHeaderLayout;
    private TextView mSubTitle, mHintTitle;
    private String item_subTitle, item_hintTitle;

    private int style_id = 1;

    private onOneButtonListener mOneButtonListener;
    private Button mOnlyOneButton;

//    HeaderLayout getInstance() {
//        HeaderLayout mHeaderLayout = new HeaderLayout(mContext);
//        return mHeaderLayout;
//    }

    public HeaderLayout(Context context) {
        super(context);

    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HeaderLayout);
        style_id = ta.getInt(R.styleable.HeaderLayout_item_stytle, 0);
        item_subTitle = ta.getString(R.styleable.HeaderLayout_item_subTitle);
        item_hintTitle = ta.getString(R.styleable.HeaderLayout_item_hintTitle);
        ta.recycle();
        init(context);
    }

    public void init(Context context) {
        mInflater = LayoutInflater.from(context);
        mHeader = mInflater.inflate(R.layout.item_parent, null);
        addView(mHeader);
        initViews();
    }

    public void initViews() {
        mSubTitle = (TextView) findViewByHeaderId(R.id.item_sub_title);
        mHintTitle = (TextView) findViewByHeaderId(R.id.item_hint_title);

        setSubTitle(item_subTitle);
        setHintTitle(item_hintTitle);

        mHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (mOneButtonListener != null) {
                    mOneButtonListener.onOneButtonClick(mHeaderLayout);
                }
            }
        });

    }

    public View findViewByHeaderId(int id) {
        return mHeader.findViewById(id);
    }

    public void setSubTitle(CharSequence title) {
        if (title != null) {
        	mSubTitle.setVisibility(View.VISIBLE);
            mSubTitle.setText(title);
        } else {
            mSubTitle.setVisibility(View.GONE);
        }
    }

    public void setHintTitle(CharSequence title) {
        if (title != null) {
            mHintTitle.setVisibility(View.VISIBLE);
            mHintTitle.setText(title);
        } else {
            mHintTitle.setVisibility(View.GONE);
        }
    }

    public TextView getHintTitle() {
        return mHintTitle;
    }

    public void setOneButtonListener(HeaderLayout headlayout, final onOneButtonListener listener) {
        mHeaderLayout = headlayout;
        setOneButtonListener(listener);
    }

    public void setOneButtonListener(onOneButtonListener listener) {
        mOneButtonListener = listener;
    }

    public interface onOneButtonListener {
        void onOneButtonClick(View view);
    }

}
