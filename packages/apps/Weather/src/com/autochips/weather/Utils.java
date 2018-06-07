package com.autochips.weather;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;

public class Utils {

    public static String getHourMinute(boolean is24Hour) {
        String time = "";
        Calendar c = Calendar.getInstance();
        int hour = 0;
        hour = c.get(Calendar.HOUR_OF_DAY);
        if (is24Hour) {
            time += hour < 10 ? "0" + hour : hour;
        } else {
            int tens = hour > 12 ? (hour - 12) / 10 : hour / 10;
            int single = hour > 12 ? (hour - 12) % 10 : hour % 10;
            time = String.valueOf(tens) + String.valueOf(single);
        }
        int minute = c.get(Calendar.MINUTE);
        if (minute < 10) {
            time += ":0" + minute;
        } else
            time += ":" + minute;
        return time;
    }

    public static String getCurrentWeek(Context context) {
        final Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        String[] weekdays = context.getResources().getStringArray(
                R.array.weekday);
        return weekdays[week - 1];
    }

    public static String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    public static String[] getHourMinute(Context context) {
        String[] res = new String[2];
        String time = "";
        Calendar c = Calendar.getInstance();
        int hour = 0;
        hour = c.get(Calendar.HOUR_OF_DAY);
        if (is24HourFormat(context)) {
            time += hour < 10 ? "0" + hour : hour;
        } else {
            int tens = hour > 12 ? (hour - 12) / 10 : hour / 10;
            int single = hour > 12 ? (hour - 12) % 10 : hour % 10;
            time = String.valueOf(tens) + String.valueOf(single);
        }
        res[0] = time;
        int minute = c.get(Calendar.MINUTE);
        if (minute < 10) {
            time = "0" + minute;
        } else
            time = "" + minute;
        res[1] = time;
        return res;
    }

    public static boolean is24HourFormat(Context context) {
        return true;
        /*
         * ContentResolver cv = context.getContentResolver(); String
         * strTimeFormat = android.provider.Settings.System.getString(cv,
         * android.provider.Settings.System.TIME_12_24);
         * 
         * if (strTimeFormat != null && strTimeFormat.equals("24")) return true;
         * else return false;
         */
    }

    public static boolean isWiFiConnected(Context inContext) {
        Context context = inContext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                    // if (info[i].getTypeName().equalsIgnoreCase("WIFI")
                    // && info[i].isConnected()) {
                    // return true;
                    // }
                }
            }
        }
        return false;
    }

    public static boolean copyDatabaseFile(Context context, boolean isfored,
            String dbName) {
        String DATABASE_NAME = dbName;
        String DATABASES_DIR = Environment.getDataDirectory().getPath()
                + "/data/" + context.getPackageName() + "/databases/";
        InputStream in = null;
        try {
            in = context.getAssets().open(DATABASE_NAME);
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        }
        File dir = new File(DATABASES_DIR);

        if (!dir.exists() || isfored) {
            try {
                dir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File dest = new File(dir, DATABASE_NAME);
        if (dest.exists() && !isfored) {
            return false;
        }
        try {
            if (dest.exists()) {
                dest.delete();
            }
            dest.createNewFile();
            int size = in.available();
            byte buf[] = new byte[size];
            in.read(buf);
            in.close();
            FileOutputStream out = new FileOutputStream(dest);
            out.write(buf);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getJsonFromWeatherCache(Context context, Dao dao) {
        StringBuffer res = new StringBuffer();
        Weather weather = getShowWeather(dao);
        if (weather != null) {
            String unit = StringUtil.getTempUnit(context);
            res.append("{conditions:\"");
            res.append(weather.condition);
            res.append("\",");
            res.append("high:\"");
            res.append(weather.highTemp);
            res.append("\",");
            res.append("low:\"");
            res.append(weather.lowTemp);
            res.append("\",");
            res.append("city:\"");
            res.append(getCurrentCity(dao));
            res.append("\",");
            res.append("code:\"");
            res.append(weather.code);
            res.append("\",");
            res.append("current_temp:\"");
            res.append(weather.currentTemp);
            res.append("\",");
            res.append("temp_unit:\"");
            res.append(unit);
            res.append("\"}");
        } else {
            res.append("null");
        }
        return res.toString();
    }

    private static String getCurrentCity(Dao dao) {
        CountryRegion bean = dao.getCurrentCity();
        if (Constants.IS_EN) {
            return StringUtil.replaceSymbol(bean.getCityNameEn());
        } else {
            return StringUtil.replaceSymbol(bean.getCityName());
        }
    }

    public static Weather getShowWeather(Dao dao) {
        Weather weather = null;
        WeatherCache cache = dao.getWeatherCache();
        CountryRegion city = dao.getCurrentCity();
        if (cache != null && city != null
                /*&& city.getWoeid().equals(cache.getWoeid())*/) {
            long updatetime = Long.valueOf(cache.getUpdateTime());
            long[] time_limit = Utils.getTimeLimit(updatetime);
            long now = System.currentTimeMillis();
            if (now <= time_limit[0]) {
                weather = new Weather();
                weather.condition = cache.getFirstdayCondition();
                weather.code = cache.getFirstdayCode();
                weather.highTemp = cache.getFirstdayHigh();
                weather.lowTemp = cache.getFirstdayLow();
            } else if (now > time_limit[0] && now <= time_limit[1]) {
                weather = new Weather();
                weather.condition = cache.getSecdayCondition();
                weather.code = cache.getSecdayCode();
                weather.highTemp = cache.getSecdayHigh();
                weather.lowTemp = cache.getSecdayLow();
            }
        }
        return weather;
    }

    public static long[] getTimeLimit(long updateTime) {
        long[] lim = new long[2];
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(updateTime);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        c.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        c.set(Calendar.DATE, calendar.get(Calendar.DATE));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        lim[0] = c.getTimeInMillis();

        calendar.roll(java.util.Calendar.DAY_OF_YEAR, 1);
        c = Calendar.getInstance();
        c.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        c.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        c.set(Calendar.DATE, calendar.get(Calendar.DATE));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        lim[1] = c.getTimeInMillis();

        return lim;
    }

    public static Weather parseWeatherJson(String jsonString) {
        Weather weather = null;
        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            int code = Integer.valueOf(jsonObj.getString("code"));
            String low = jsonObj.getString("low");
            String high = jsonObj.getString("high");
            String unit = jsonObj.getString("temp_unit");
            String cityname = jsonObj.getString("city");
            String conditions = jsonObj.getString("conditions");
            weather = new Weather();
            weather.condition = conditions;
            weather.code = code;
            weather.highTemp = high;
            weather.lowTemp = low;
            weather.unit = unit;
            weather.cityname = cityname;
        } catch (JSONException e) {
        } catch (NullPointerException e) {
        }
        return weather;
    }

    public static int cenToFah(int centigrade) {
        double f = 9 * centigrade / 5.0 + 32;
        return (int) f;
    }

    public static void sendUpdateWeatherBroadcast(Context context, Dao dao) {
        Intent intent = new Intent(Constants.ACTION_WEATHER_INFO_UPDATE);
        Bundle bundle = new Bundle();
        bundle.putString("weather_info", getJsonFromWeatherCache(context, dao));
        intent.putExtras(bundle);
        context.sendStickyBroadcast(intent);
    }
    public static void sendUpdateCityBroadcast(Context context, Dao dao) {
        Intent intent = new Intent(Constants.ACTION_CITY_INFO_UPDATE);
        Bundle bundle = new Bundle();
        CountryRegion city = dao.getCurrentCity();
        bundle.putString("city_info", city.getCityName());
        intent.putExtras(bundle);
        context.sendStickyBroadcast(intent);
    }
    
    public static boolean isServiceRunning(Context mContext, String serviceName) {  
        boolean isWork = false;  
        ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);  
        List<RunningServiceInfo> myList = myAM.getRunningServices(100);  
        if (myList.size() <= 0) {  
            return false;  
        }  
        for (int i = 0; i < myList.size(); i++) {  
            String mName = myList.get(i).service.getClassName().toString();  
            if (mName.equals(serviceName)) {  
                isWork = true;  
                break;  
            }  
        }  
        return isWork;  
    } 
}
