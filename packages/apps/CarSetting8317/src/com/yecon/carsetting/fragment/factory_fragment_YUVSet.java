package com.yecon.carsetting.fragment;

import java.util.Timer;
import java.util.TimerTask;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.yecon.carsetting.R;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.XmlParse;

public class factory_fragment_YUVSet extends DialogFragment implements OnClickListener, OnTouchListener,
        OnSeekBarChangeListener, OnCheckedChangeListener {

    private Context mContext;
    Timer timer;
    RadioGroup radioGroup;
    RadioButton radioButton[] = new RadioButton[4];
    ImageView imageView[][] = new ImageView[3][2];
    SeekBar seekBar[] = new SeekBar[3];
    TextView textView[] = new TextView[3];

    int mIndex = 0;

    public factory_fragment_YUVSet(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle(getResources().getString(R.string.factory_yuv_set));
        // getDialog().getActionBar().setBackgroundDrawable(arg0);
        View rootView = inflater.inflate(R.layout.factory_fragment_yuv_set, container);
        initView(rootView, mContext);
        return rootView;
    }

    void initView(View rootView, Context context) {

        radioGroup = (RadioGroup) rootView.findViewById(R.id.RadioGroup_menu);
        radioGroup.setOnCheckedChangeListener(this);
        radioButton[0] = (RadioButton) rootView.findViewById(R.id.Radio_style_1);
        radioButton[1] = (RadioButton) rootView.findViewById(R.id.Radio_style_2);
        radioButton[2] = (RadioButton) rootView.findViewById(R.id.Radio_style_3);
        radioButton[3] = (RadioButton) rootView.findViewById(R.id.Radio_style_4);

        imageView[0][0] = (ImageView) rootView.findViewById(R.id.ygain_minus);
        imageView[0][1] = (ImageView) rootView.findViewById(R.id.ygain_add);
        imageView[1][0] = (ImageView) rootView.findViewById(R.id.ugain_minus);
        imageView[1][1] = (ImageView) rootView.findViewById(R.id.ugain_add);
        imageView[2][0] = (ImageView) rootView.findViewById(R.id.vgain_minus);
        imageView[2][1] = (ImageView) rootView.findViewById(R.id.vgain_add);

        for (int i = 0; i < imageView.length; i++) {
            for (int j = 0; j < imageView[0].length; j++) {
                imageView[i][j].setOnClickListener(this);
                imageView[i][j].setOnTouchListener(this);
            }
        }

        seekBar[0] = (SeekBar) rootView.findViewById(R.id.seekBar_ygain);
        seekBar[1] = (SeekBar) rootView.findViewById(R.id.seekBar_ugain);
        seekBar[2] = (SeekBar) rootView.findViewById(R.id.seekBar_vgain);

        for (int i = 0; i < seekBar.length; i++) {
            seekBar[i].setOnSeekBarChangeListener(this);
        }

        textView[0] = (TextView) rootView.findViewById(R.id.text_ygain_value);
        textView[1] = (TextView) rootView.findViewById(R.id.text_ugain_value);
        textView[2] = (TextView) rootView.findViewById(R.id.text_vgain_value);

        radioGroup.check(radioButton[0].getId());
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        getDialog().getWindow().setLayout(700, 400);
    }

    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        super.dismiss();
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        ChangeValue(arg0.getId());
    }

    @Override
    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
        case R.id.seekBar_ygain:
            setValue(mIndex, 0, arg1);
            break;
        case R.id.seekBar_ugain:
            setValue(mIndex, 1, arg1);
            break;
        case R.id.seekBar_vgain:
            setValue(mIndex, 2, arg1);
            break;
        default:
            break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCheckedChanged(RadioGroup arg0, int arg1) {
        // TODO Auto-generated method stub
        switch (arg1) {
        case R.id.Radio_style_1:
            mIndex = Tag.VIDEOTYPE.AVIN_1.ordinal();
            setYUVGain(mIndex);
            break;
        case R.id.Radio_style_2:
            mIndex = Tag.VIDEOTYPE.BACKCAR.ordinal();
            setYUVGain(mIndex);
            break;
        case R.id.Radio_style_3:
            mIndex = Tag.VIDEOTYPE.USB.ordinal();
            setYUVGain(mIndex);
            break;
        case R.id.Radio_style_4:
            mIndex = Tag.VIDEOTYPE.DVD.ordinal();
            setYUVGain(mIndex);
            break;
        default:
            break;
        }
    }

    private void setYUVGain(int index) {
        for (int j = 0; j < Tag.PERSYS_YUV_GAIN[0].length; j++)
            setValue(index, j, XmlParse.yuv_gain[index][j]);
    }

    private void setValue(int i, int j, int value) {
        if (value < 0)
            value = 0;
        else if (value > 0x1ff)
            value = 0x1ff;
        seekBar[j].setProgress(value);
        textView[j].setText(String.valueOf(value));
        XmlParse.yuv_gain[i][j] = value;
        // SystemProperties.set(Tag.PERSYS_GAIN[i][j], XmlParse.yuv_gain[i][j] +
        // "");
        // text_ygain_value.setText("0x" +
        // Integer.toHexString(XmlParse.yuv_gain[i][j]).toUpperCase());
    }

    private void sendMessage(final int value) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = value;
                mHandler.sendMessage(message);
            }
        };
        timer.schedule(task, 500, 100);
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        // TODO Auto-generated method stub
        switch (arg1.getAction()) {
        case MotionEvent.ACTION_DOWN:
            timer = new Timer();
            sendMessage(arg0.getId());
            break;
        case MotionEvent.ACTION_UP:
            timer.cancel();
            mHandler.removeMessages(arg0.getId());
            break;
        default:
            break;
        }
        return false;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChangeValue(msg.what);
        }
    };

    private void ChangeValue(int id) {
        switch (id) {

        case R.id.ygain_minus:
            setValue(mIndex, 0, --XmlParse.yuv_gain[mIndex][0]);
            break;
        case R.id.ygain_add:
            setValue(mIndex, 0, ++XmlParse.yuv_gain[mIndex][0]);
            break;
        case R.id.ugain_minus:
            setValue(mIndex, 1, --XmlParse.yuv_gain[mIndex][1]);
            break;
        case R.id.ugain_add:
            setValue(mIndex, 1, ++XmlParse.yuv_gain[mIndex][1]);
            break;
        case R.id.vgain_minus:
            setValue(mIndex, 2, --XmlParse.yuv_gain[mIndex][2]);
            break;
        case R.id.vgain_add:
            setValue(mIndex, 2, ++XmlParse.yuv_gain[mIndex][2]);
            break;
        }
    }
}
