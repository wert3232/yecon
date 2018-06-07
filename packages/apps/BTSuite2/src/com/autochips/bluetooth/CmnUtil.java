package com.autochips.bluetooth;

import com.autochips.bluetooth.util.L;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CmnUtil {
	private  static Toast mToast;
	public static void showToast(Activity ct, int resid) {
        if (mToast != null) {
        	mToast.cancel();
        }
        View toastRoot = ct.getLayoutInflater().inflate(R.layout.toast, null);
        mToast=new Toast(ct);
        mToast.setView(toastRoot);
		TextView tv=(TextView)toastRoot.findViewById(R.id.tvContent);
		tv.setText(resid);
		mToast.setGravity(Gravity.BOTTOM, 0, 100);
        mToast.show();
    }
	public static void showTuoXianToast(Activity ct,int resid){
		L.e("showTuoXianToast");
		if (mToast != null) {
        	mToast.cancel();
        }
        View toastRoot = ct.getLayoutInflater().inflate(R.layout.tuoxian_toast, null);
        mToast=new Toast(ct);
        mToast.setView(toastRoot);
		TextView tv=(TextView)toastRoot.findViewById(R.id.tvContent);
		tv.setText(resid);
		mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
	}
	public static void cancelToast(){
		 if (mToast != null) {
			mToast.cancel();
			mToast=null;
		}
	}
}
