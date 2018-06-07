package com.baidu.che.codriver.parse;

import android.app.Application;
import android.content.Intent;
import android.view.WindowManager;

public class MyApplication extends Application {
	private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

	public WindowManager.LayoutParams getMywmParams() {
		return wmParams;
	}
	
    @Override
    public void onCreate() {
        super.onCreate();
        Intent service = new Intent();
        service.setClass(this, CoDriverBackgroadService.class);
        this.startService(service);
    }
}
