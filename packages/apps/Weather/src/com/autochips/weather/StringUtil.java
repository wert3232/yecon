package com.autochips.weather;

import android.content.Context;

public class StringUtil {

    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0)
            return true;
        return false;
    }

    public static String replaceSymbol(String str) {
        if (StringUtil.isEmpty(str)) {
            return str;
        } else {
            return str.replaceAll("%20", " ");
        }
    }

    public static String getDegreeFormat(Context context, String degree,
            boolean isUnit) {
        int deg = 0;
        try {
            deg = Integer.valueOf(degree);
            String unit = context.getString(R.string.temperature_unit);
            if (!getTempUnit(context).equals(unit)) {
                unit = context.getString(R.string.temperature_unit);
                deg = Utils.cenToFah(deg);
            }
            degree = String.valueOf(deg);
            if (isUnit)
                degree += unit;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return degree;
    }

    public static String getTempUnit(Context context) {
        String defaultUnit = context.getString(R.string.temperature_unit);
        return defaultUnit;
    }
}
