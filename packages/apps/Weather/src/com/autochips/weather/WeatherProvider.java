
package com.autochips.weather;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WeatherProvider extends AppWidgetProvider {
	public static boolean mbCheckCurCity = true;
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
    	
        // if (WeatherApp.IS_WIFI_CONNECTED) {
        context.startService(new Intent(context, WeatherService.class));
        // }
        context.startService(new Intent(context, WeatherUpdateService.class));
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.weather_widget_layout);
            Intent timeIntent = new Intent(Intent.ACTION_VIEW);
            timeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            timeIntent.setComponent(new ComponentName("com.yecon.carsetting",
                    "com.yecon.carsetting.DatetimeSetActivity"));
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 0, timeIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.frontview01, pendingIntent);

            Intent intent = new Intent(context, SettingsActivity.class);
            PendingIntent pendingActivityIntent = PendingIntent.getActivity(
                    context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.frontview02,
                    pendingActivityIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        SharedPreferences sp =context.getSharedPreferences("chkCurCity", Context.MODE_PRIVATE);
        mbCheckCurCity = sp.getBoolean("chkCurCity",true);
        locationManager.getInstance().createloacation(context);
        context.startService(new Intent(context, WeatherUpdateService.class));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        locationManager.getInstance().removeUpdates();  
        context.stopService(new Intent(context, WeatherUpdateService.class));
    }

    public static class WeatherUpdateService extends Service {
        private static String[] conditionItems = null;
        public static String[] conditionImages = null;
        private final static IntentFilter intentFilter;
        private long mLastClickTime;
        private final int mImageWidth = 46;
        private final int mImageHeight = 70;
        private final int mGap = 4;
        private Canvas mTimeCanvas;
        private Bitmap mTimeBitmap;
        private Bitmap mMaoHaoBitmap;
        private List<Bitmap> mNumBitmaps;
        private Paint mPaint;
        private int[] mImageResArray = { R.drawable.t0, R.drawable.t1,
                R.drawable.t2, R.drawable.t3, R.drawable.t4, R.drawable.t5,
                R.drawable.t6, R.drawable.t7, R.drawable.t8, R.drawable.t9 };

        static {
            intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_TIME_TICK);
            intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
            intentFilter.addAction(Constants.ACTION_WEATHER_INFO_UPDATE);
            intentFilter.addAction(Constants.ACTION_WEATHER_ON_CLICK);
            intentFilter.addAction(Constants.ACTION_CITY_INFO_UPDATE);
        }

        private final BroadcastReceiver updateReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                update(context, intent);
            }
        };

        @Override
        public void onCreate() {
            super.onCreate();
            registerReceiver(updateReceiver, intentFilter);
            mNumBitmaps = new ArrayList<Bitmap>();
            mMaoHaoBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.maohao);
            for (int i = 0; i < mImageResArray.length; i++) {
                mNumBitmaps.add(BitmapFactory.decodeResource(getResources(),
                        mImageResArray[i]));
            }
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            unregisterReceiver(updateReceiver);
        }

        @Override
        public void onStart(Intent intent, int startId) {
            conditionItems = this.getApplicationContext().getResources()
                    .getStringArray(R.array.yahoo_condition);
            update(this, intent);
        }

        private void update(Context context, Intent intent) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.weather_widget_layout);
            updateUI(this, intent, remoteViews);
            notifyWidget(context, remoteViews, WeatherProvider.class);
        }

        @Override
        public IBinder onBind(Intent intent) {

            return null;
        }

        public void updateUI(Context context, Intent intent,
                RemoteViews remoteViews) {
            if (intent != null) {
                String action = intent.getAction();
                if (Constants.ACTION_WEATHER_INFO_UPDATE.equals(action)) {
                    updateWeather(remoteViews, context, intent);
                }
                if (Constants.ACTION_CITY_INFO_UPDATE.equals(action)) {
                	updateCity(remoteViews, context, intent);
                }
                if (Constants.ACTION_WEATHER_ON_CLICK.equals(action)) {
                    onClick();
                }
            }
            updateDateAndTime(remoteViews, context);
        }

        public void updateWeather(RemoteViews views, Context context,
                Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String jsonStr = bundle.getString("weather_info");
                if (!TextUtils.isEmpty(jsonStr)) {
                    Weather weather = Utils.parseWeatherJson(jsonStr);
                    if (weather != null) {
                        views.setTextViewText(R.id.city, weather.cityname);
                        views.setTextViewText(R.id.weather,
                                conditionItems[weather.code]);
                        views.setTextViewText(R.id.high_temp, weather.highTemp);
                        views.setTextViewText(R.id.temperature_unit01,
                                weather.unit);
                        views.setTextViewText(R.id.split_symbol, "~");
                        views.setTextViewText(R.id.low_temp, weather.lowTemp);
                        views.setTextViewText(R.id.temperature_unit02,
                                weather.unit);

                        BitmapDrawable bd = (BitmapDrawable) context
                                .getResources()
                                .getDrawable(
                                        getImageIdByCode(weather.code, context));
                        views.setImageViewBitmap(R.id.weather_img,
                                bd.getBitmap());
                    }
                }
            }
        }
        public void updateCity(RemoteViews views, Context context,
                Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String jsonStr = bundle.getString("city_info");
                views.setTextViewText(R.id.city, jsonStr);
                
                
            }
        }
        public void onClick() {
            long now = SystemClock.elapsedRealtime();
            if ((now - mLastClickTime) > 3000) {
                if (WeatherApp.IS_WIFI_CONNECTED) {
                    getToast().setText(getApplicationContext().getString(
                            R.string.updateing));
                    getToast().show();
                    getApplicationContext().startService(
                            new Intent(getApplicationContext(),
                                    WeatherService.class));
                } else {
                    //getToast().setText(getApplicationContext().getString(R.string.error));
                    //getToast().show();
                }
            }
        }

        private Toast mToast;

        @SuppressLint("ShowToast")
        public Toast getToast() {
            if (mToast == null) {
                mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
            }
            return mToast;
        }

        public void updateDateAndTime(RemoteViews views, Context context) {
            views.setTextViewText(R.id.week, Utils.getCurrentWeek(context));
            views.setTextViewText(R.id.date, Utils.getDate());

            if (mTimeBitmap == null) {
                mTimeBitmap = Bitmap.createBitmap(240, 200,
                        Bitmap.Config.ARGB_8888);
                mTimeCanvas = new Canvas(mTimeBitmap);
            }
            views.setImageViewBitmap(
                    R.id.times,
                    getTimeBitmap(mTimeBitmap, Utils.getHourMinute(context),
                            context));
        }

        public static int getImageIdByCode(int c, Context context) {
            Resources resources = context.getResources();
            if (conditionImages == null) {
                conditionImages = context.getResources().getStringArray(
                        R.array.yahoo_condition_image);
            }
            int code = c;
            try {
                int imageId = resources.getIdentifier(conditionImages[code],
                        "drawable", context.getPackageName());
                return imageId;
            } catch (Exception e) {
            }
            return R.drawable.cloudy;
        }

        public Bitmap getTimeBitmap(Bitmap bitmap, String[] times,
                Context context) {
            if (bitmap != null) {
                mTimeCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                Bitmap[] bitmaps = getBitmapsByTime(times);
                for (int i = 0; i < bitmaps.length; i++) {
                    int offset = 0;
                    if (i > 2) {
                        offset = 28;
                    }
                    int l = i * (mImageWidth + mGap) - offset;
                    int t = 0;
                    int r = i * (mImageWidth + mGap) + mImageWidth - offset;
                    int b = mImageHeight;

                    Rect rect = new Rect(l, t, r, b);
                    mTimeCanvas.drawBitmap(bitmaps[i], null, rect, mPaint);
                }
            }
            return bitmap;
        }

        private Bitmap[] getBitmapsByTime(String[] hour_minute) {
            String timeStr = hour_minute[0] + "a" + hour_minute[1];

            Bitmap[] bitmaps = new Bitmap[5];
            int num = 0;
            for (int i = 0; i < bitmaps.length; i++) {
                try {
                    num = Integer.valueOf(String.valueOf(timeStr.charAt(i)));
                    bitmaps[i] = mNumBitmaps.get(num);
                } catch (Exception e) {
                    bitmaps[i] = mMaoHaoBitmap;
                }

            }
            return bitmaps;
        }

        public void notifyWidget(Context context, RemoteViews remoteViews,
                Class<?> class1) {
            AppWidgetManager awm = AppWidgetManager.getInstance(context);
            int[] appIds = awm.getAppWidgetIds(new ComponentName(context,
                    class1));
            awm.updateAppWidget(appIds, remoteViews);
        }
    }

}
