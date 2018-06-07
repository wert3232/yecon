
package com.yecon.fmradio;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FreqNumTextView extends RelativeLayout {
    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;
    private TextView mTextView5;
    private TextView mTextView6;

    private int[] res = {
            R.string.fmr_freq_num0,
            R.string.fmr_freq_num1,
            R.string.fmr_freq_num2,
            R.string.fmr_freq_num3,
            R.string.fmr_freq_num4,
            R.string.fmr_freq_num5,
            R.string.fmr_freq_num6,
            R.string.fmr_freq_num7,
            R.string.fmr_freq_num8,
            R.string.fmr_freq_num9,
    };

    public FreqNumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.freq_num_textview, this);

        mTextView1 = (TextView) findViewById(R.id.tv_textview1);
        mTextView2 = (TextView) findViewById(R.id.tv_textview2);
        mTextView3 = (TextView) findViewById(R.id.tv_textview3);
        mTextView4 = (TextView) findViewById(R.id.tv_textview4);
        mTextView5 = (TextView) findViewById(R.id.tv_textview5);
        mTextView6 = (TextView) findViewById(R.id.tv_textview6);
    }

    public void setImageResource1(int resId, boolean visible) {
        if (visible) {
            mTextView1.setVisibility(View.VISIBLE);
        } else {
            mTextView1.setVisibility(View.GONE);
        }
        mTextView1.setText(res[resId]);
    }

    public void setImageResource1Visible(boolean visible) {
        if (visible) {
            mTextView1.setVisibility(View.VISIBLE);
        } else {
            mTextView1.setVisibility(View.GONE);
        }
    }

    public void setImageResource2(int resId) {
        mTextView2.setText(res[resId]);
    }

    public void setImageResource2Visible(boolean visible) {
        if (visible) {
            mTextView2.setVisibility(View.VISIBLE);
        } else {
            mTextView2.setVisibility(View.GONE);
        }
    }

    public void setImageResource3(int resId) {
        mTextView3.setText(res[resId]);
    }

    public void setImageResource4Visible(boolean visible) {
        if (visible) {
            mTextView4.setVisibility(View.VISIBLE);
        } else {
            mTextView4.setVisibility(View.GONE);
        }
    }
    public void setImageResource6Visible(boolean visible) {
        if (visible) {
            mTextView6.setVisibility(View.VISIBLE);
        } else {
            mTextView6.setVisibility(View.GONE);
        }
    }


    public void setImageResource5(int resId) {
        mTextView5.setText(res[resId]);
    }

    public void setImageResource6(int resId) {
        mTextView6.setText(res[resId]);
    }

}
