package com.yecon.filemanager;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by chenchu on 15-2-5.
 */
public class LauncherActivity  extends Activity {
    private static final String Tag = "LauncherActivity";

    public String getVersion() {
        try {
            PackageManager pm = getPackageManager();
            return pm.getPackageInfo(getPackageName(), 0).versionName;
        } catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
            return "~~";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Tag, "launcheractivity");
        setContentView(R.layout.activity_launcher);
       // ((TextView) findViewById(R.id.fragment_launcher_version)).setText(getVersion());
        //for test
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Log.d(Tag,dm.toString());
    }
    

}
