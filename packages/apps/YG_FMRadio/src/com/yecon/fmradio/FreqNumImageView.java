
package com.yecon.fmradio;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FreqNumImageView extends RelativeLayout {
    private ImageView mImageView1;
    private ImageView mImageView2;
    private ImageView mImageView3;
    private ImageView mImageView4;
    private ImageView mImageView5;
    private ImageView mImageView6;

    private int[] res = {
            R.drawable.freq_num_0,
            R.drawable.freq_num_1,
            R.drawable.freq_num_2,
            R.drawable.freq_num_3,
            R.drawable.freq_num_4,
            R.drawable.freq_num_5,
            R.drawable.freq_num_6,
            R.drawable.freq_num_7,
            R.drawable.freq_num_8,
            R.drawable.freq_num_9,
    };

    public FreqNumImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.freq_num_imageview, this);

        mImageView1 = (ImageView) findViewById(R.id.iv_imageview1);
        mImageView2 = (ImageView) findViewById(R.id.iv_imageview2);
        mImageView3 = (ImageView) findViewById(R.id.iv_imageview3);
        mImageView4 = (ImageView) findViewById(R.id.iv_imageview4);
        mImageView5 = (ImageView) findViewById(R.id.iv_imageview5);
        mImageView6 = (ImageView) findViewById(R.id.iv_imageview6);
    }

    public void setImageResource1(int resId, boolean visible) {
        if (visible) {
            mImageView1.setVisibility(View.VISIBLE);
        } else {
            mImageView1.setVisibility(View.GONE);
        }
        mImageView1.setImageResource(res[resId]);
    }

    public void setImageResource1Visible(boolean visible) {
        if (visible) {
            mImageView1.setVisibility(View.VISIBLE);
        } else {
            mImageView1.setVisibility(View.GONE);
        }
    }

    public void setImageResource2(int resId) {
        mImageView2.setImageResource(res[resId]);
    }

    public void setImageResource2Visible(boolean visible) {
        if (visible) {
            mImageView2.setVisibility(View.VISIBLE);
        } else {
            mImageView2.setVisibility(View.GONE);
        }
    }

    public void setImageResource3(int resId) {
        mImageView3.setImageResource(res[resId]);
    }

    public void setImageResource4Visible(boolean visible) {
        if (visible) {
            mImageView4.setVisibility(View.VISIBLE);
        } else {
            mImageView4.setVisibility(View.GONE);
        }
    }

    public void setImageResource5(int resId) {
        mImageView5.setImageResource(res[resId]);
    }

    public void setImageResource6(int resId) {
        mImageView6.setImageResource(res[resId]);
    }

}
