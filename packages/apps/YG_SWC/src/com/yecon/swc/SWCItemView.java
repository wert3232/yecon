
package com.yecon.swc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class SWCItemView extends FrameLayout {
    private TextView mTVIndex;
    private TextView mTVTitle;
    private ImageView mIVIcon;

    public SWCItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initData();

        initUI(context);
    }

    private void initData() {

    }

    private void initUI(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.swc_item_layout, this);

        mTVIndex = (TextView) findViewById(R.id.tv_index);
        mTVTitle = (TextView) findViewById(R.id.tv_title);
        mIVIcon = (ImageView) findViewById(R.id.iv_icon);
        
        if(!context.getResources().getBoolean(R.bool.show_index)){
        	mTVIndex.setVisibility(View.GONE);
        }
    }

    public void setIndex(int index) {
        mTVIndex.setText(index + "");
    }

    public void setTitle(int resid) {
        mTVTitle.setText(resid);
    }

    public void setIcon(int resid) {
        mIVIcon.setImageResource(resid);
    }

}
