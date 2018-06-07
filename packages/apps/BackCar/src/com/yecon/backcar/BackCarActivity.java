
package com.yecon.backcar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class BackCarActivity extends Activity {
    /** Called when the activity is first created. */
    private static final String TAG = "BackCarActivity";

    final int UI_SCREEN_WIDTH = 800;
    final int UI_SCREEN_HEIGHT = 480;
    int direction, ret;
    float[] sampleX = new float[128];
    float[] sampleY = new float[128];
    int index = 0;
    int show_flag = 1;
    View myView;

    int xList[] = {
            50, UI_SCREEN_WIDTH - 50, UI_SCREEN_WIDTH - 50, 50,
            UI_SCREEN_WIDTH / 2
    };

    int yList[] = {
            50, 50, UI_SCREEN_HEIGHT - 50, UI_SCREEN_HEIGHT - 50,
            UI_SCREEN_HEIGHT / 2
    };

    int xTs[] = {
            0, 0, 0, 0, 0
    };
    int yTs[] = {
            0, 0, 0, 0, 0
    };

    public boolean onTouchEvent(MotionEvent event) {
        myView.invalidate();
        int action = event.getAction();
        Log.i("TAG", "BackCarActivity -  " + action);

        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "BackCarActivity - onCreate");

        super.onCreate(savedInstanceState);

        myView = new View(this);

        setContentView(myView);
    }

    public void onBackPressed() {
        Log.i(TAG, "BackCarActivity - onBackPressed");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "BackCarActivity - onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            Log.i(TAG, "BackCarActivity - KEY MENU");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.i(TAG, "BackCarActivity - onKeyUp");
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            Log.i(TAG, "BackCarActivity - KEY MENU");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i(TAG, "BackCarActivity - dispatchKeyEvent");
        return true;

        // return super.dispatchKeyEvent(event);
    }

    protected void onUserLeaveHint() {
        Log.i(TAG, "BackCarActivity - onUserLeaveHint");

        // super.onUserLeaveHint();
    }

    @SuppressWarnings("unused")
    private boolean IsMuteEnable() {
        return SystemProperties.getBoolean(BackCarService.PERSYS_BACKCAR_MUTE, false);
    }

    protected void onPause() {
        Log.i(TAG, "BackCarActivity - pause");

        super.onPause();
    }

    protected void onResume() {
        Log.i(TAG, "BackCarActivity - onResume");

        // YeconSettings.initVideoRgb(YeconSettings.RGBTYPE.BACKCAR);

        if (null != BackCarService.getInstance()) {
            BackCarService.getInstance().setActivity(this);
        }

        super.onResume();
    }

    protected void onStart() {
        Log.i(TAG, "BackCarActivity - start");
        super.onStart();
    }

    protected void onRestart() {
        Log.i(TAG, "BackCarActivity - Restart");
        super.onRestart();
    }

    protected void onStop() {
        Log.i(TAG, "BackCarActivity - stop");

        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
        // gInst = null;
    }

    public class CrossView extends View {

        public CrossView(Context context) {
            super(context);
        }

    }
}
