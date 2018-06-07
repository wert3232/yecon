package com.yecon.carwidget8317;

import android.app.Instrumentation;
import android.util.Log;

public class SendKey {
	private final static String TAG="SendKey";
	
	public static void TransKey(final int iKeyCode) {
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					Log.d(TAG, "widget--->send key="+iKeyCode);
					Instrumentation instrKey = new Instrumentation();
					instrKey.sendKeyDownUpSync(iKeyCode);

				} catch (Exception e) {
					// TODO: handle exception
				}

				super.run();
			}

		}.start();
	}
}
