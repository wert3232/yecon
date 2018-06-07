package com.android.calibrate;

public class Calibrate {
	static{
		System.loadLibrary("ts_calibrate");
	}
	public native int write_props();
	public native int write_OldCalibrate_props();
	public native int perform_calibrate(int[] xList, int[] yList, int[] xTs, int[] yTs);

}
