
package com.yecon.gpstest;

public class GPSInfoItem {
    float mSnr; // 返回卫星的信噪比
    int mPrn; // 返回伪随机噪声码
    float mAzi; // 返回卫星的方位角，方位角范围0至360度
    float mEle; // 返回卫星的高度角，高度角范围0至90度

    public GPSInfoItem(float snr, int prn, float azi, float ele) {
        mSnr = snr;
        mPrn = prn;
        mAzi = azi;
        mEle = ele;
    }
}
