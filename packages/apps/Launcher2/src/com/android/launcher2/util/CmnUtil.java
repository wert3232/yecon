package com.android.launcher2.util;

import java.lang.reflect.Field;

import com.tuoxianui.view.R;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class CmnUtil {
	private  static Toast mToast;
	public static void showTuoXianToast(Context ct,int resid){
		if (mToast != null) {
        	mToast.cancel();
        }
        View toastRoot = ((LayoutInflater)ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.my_toast, null);
        mToast = new Toast(ct);
        mToast.setView(toastRoot);
		TextView tv=(TextView)toastRoot.findViewById(R.id.tvContent);
		tv.setText(resid);
		mToast.setGravity(Gravity.CENTER, 0, 0);

		try {
			Object mTN = null;
			mTN = getField(mToast, "mTN");
			if (mTN != null) {
				Object mParams = getField(mTN, "mParams");
				if (mParams != null
						&& mParams instanceof WindowManager.LayoutParams) {
					WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;
					params.windowAnimations = 0;
				}
			}
		} catch (Exception e) {
			L.e(e.toString());
		}
        mToast.show();
	}
	public static void cancelToast(){
		 if (mToast != null) {
			mToast.cancel();
			mToast=null;
		}
	}
	private static Object getField(Object object, String fieldName)
			throws NoSuchFieldException, IllegalAccessException {
		Field field = object.getClass().getDeclaredField(fieldName);
		if (field != null) {
			field.setAccessible(true);
			return field.get(object);
		}
		return null;
	}
}
