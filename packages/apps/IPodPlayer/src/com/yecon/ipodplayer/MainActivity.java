
package com.yecon.ipodplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

import com.yecon.common.SourceManager;

import static android.mcu.McuExternalConstant.*;

public class MainActivity extends Activity {
    private final static long TIMER_DELAY = 2000;
    private final String TAG = "StartupActivity";    
    private Timer mTimer;

    private TimerTask mTask = new TimerTask() {

        @Override
        public void run() {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }

            finish();
        }
    };

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(WorkActivity.m_iPodSDKAgent == null){
        	WorkActivity.m_iPodSDKAgent = new JiPodSDKAgent();
        }
        
        if(WorkActivity.m_iPodSDKAgent.m_pfnIPOD_Init()){
				Log.i(TAG, "m_pfnIPOD_Init true ");
			
            Intent intent = new Intent(this, WorkActivity.class);
            startActivity(intent);
            finish();
            return;
        } else{
        	Log.i(TAG, "m_pfnIPOD_Init false ");
        	
        	//通知源管理ipod不存在。
        	SourceManager.hotPlugSource(this, SourceManager.SRC_NO.ipod, "", false);
        	
            setContentView(R.layout.startup_activity);
            
          	 WindowManager.LayoutParams lp = getWindow().getAttributes();  
             lp.dimAmount =0f;
             getWindow().setAttributes(lp);
           
            mTimer = new Timer();
            mTimer.schedule(mTask, TIMER_DELAY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

}
