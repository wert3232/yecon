package com.autochips.quickbootmanager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

public class TestResumeApp extends Activity {
    private static final String TAG = "TestResumeApp";
    private Button mSetBtn = null;
    private EditText mSetEdit = null;
    private static final String DEFAULT_INTERVAL = "10"; //3dcar //"600"; //glbenchmark
    private static final String mStartAppInfo = "P:com.sxiaoao.car3d3#A:com.sxiaoao.car3d3.Moto3DActivity%I:";//3dcar
        /*"P:com.glbenchmark.glbenchmark21#A:com.glbenchmark.activities.MainActivity" +
        "%P:com.glbenchmark.glbenchmark21#A:com.glbenchmark.activities.TestSelectActivity#E:batteryTest,false" +
        "%P:com.glbenchmark.glbenchmark21#A:com.glbenchmark.activities.GLBenchmarkActivity#C:tests," +
        "2000,2001,2002,2010,2100,2101,2003,2111,2950,2200,2201,2202,2300,2301,2302,2400,2401,2402,2500,2501,2600,2601,2700," +
        "2701,2702,3000,2901,2804,2805,2806,2807#E:batteryTest,false#E:warningBatteryPowerConnected,false" +
        "%I:";*/
        //glbenchmark

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.submit);
        mSetBtn = (Button)findViewById(R.id.set);
        mSetBtn.setOnClickListener(mButtonClicked);
        mSetEdit = (EditText)findViewById(R.id.interval);
    }

    private View.OnClickListener mButtonClicked =
            new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()) {
              case R.id.set:
                String temp = null;
                if (mSetEdit.getText() != null)
                    temp = mSetEdit.getText().toString();
                if (temp == null || temp.length() <= 0)
                    temp = DEFAULT_INTERVAL;
                QuickBootReceiver.parseStartAppInfo(mStartAppInfo + temp, StartAppInfo.NOT_SET_YET);
                QuickBootReceiver.doQuickBootPowerOn(TestResumeApp.this);
                break;
            }
        }
    };
}
