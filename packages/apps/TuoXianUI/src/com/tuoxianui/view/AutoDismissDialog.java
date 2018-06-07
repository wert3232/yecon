package com.tuoxianui.view;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class AutoDismissDialog extends Dialog implements android.view.View.OnClickListener{
	private Window window;
    private AutoDismissDialog self = this;
    private TextView title;
    private Timer timer = null;  
	public AutoDismissDialog(Context context, int theme) {
		super(context, theme);
		initView();
        initData();
        setListener();
	}

	public AutoDismissDialog(Context context) {
		this(context,0);
	}
	
	
	public void setContent(String content){	
		if(title != null) {
			title.setText(content);
		}
	}
	
	private void setListener() {
	}

	private void initData() {

		
	}

	private void initView() {
		this.window = this.getWindow();
        this.window.requestFeature(Window.FEATURE_NO_TITLE);
        this.window.setBackgroundDrawable(null);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_auto_dismiss);
		title  = (TextView) findViewById(R.id.textview_title);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}
	
	@Override
    public void show() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        if(timer != null){
        	timer.cancel();
        	timer.purge();
        	timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask(){    
   	     public void run(){    
		 	     	self.dismiss();  
		 	    }    
	 	}, 2000);
        super.show();
        getWindow().setLayout(  ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }
	
/*	private TimerTask task = new TimerTask(){    
	     public void run(){    
	     	self.dismiss();  
	    }    
	};   */
}

