package com.autochips.picturebrowser.utils;

import android.content.Context;
import android.content.res.Resources;

public class BaseTools {

    public static int dp2px(Context context, int dpId) {
        Resources res = context.getResources();
        return res.getDimensionPixelSize(dpId);
    }

}
