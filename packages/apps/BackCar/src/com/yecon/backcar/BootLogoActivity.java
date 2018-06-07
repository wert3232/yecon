
package com.yecon.backcar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class BootLogoActivity extends Activity {
    private static final String TAG = "BootLogoActivity";

    View myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "BootLogoActivity - onCreate");

        myView = new View(this);

        setContentView(myView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e(TAG, "BootLogoActivity - onResume");

        if (BootLogoService.getInstance() != null) {
            Log.e(TAG, "BootLogoActivity - onResume - setActivity");
            BootLogoService.getInstance().setActivity(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e(TAG, "BootLogoActivity - onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.e(TAG, "BootLogoActivity - onStop");
    }

}
