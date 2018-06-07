package com.wesail.tdr.ui.view;

import com.wesail.tdr.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class ConnectAlertDialog extends Dialog implements android.view.View.OnClickListener{
	private Window window;
    private ConnectAlertDialog self = this;
    private TextView ok;
    private TextView no;
    private TextView title;
    private Callback callback;
	public ConnectAlertDialog(Context context, int theme) {
		super(context, theme);
		initView();
        initData();
        setListener();
	}

	public ConnectAlertDialog(Context context) {
		this(context,0);
	}
	
	public void setCallBack(Callback callback){
		this.callback = callback;
	}
	
	public void setContent(String content){	
		if(title != null) {
			title.setText(content);
		}
	}
	
	private void setListener() {
		ok.setOnClickListener(this);
		no.setOnClickListener(this);
	}

	private void initData() {

		
	}

	private void initView() {
		this.window = this.getWindow();
        this.window.requestFeature(Window.FEATURE_NO_TITLE);
        this.window.setBackgroundDrawable(null);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_confirm);
		ok = (TextView) findViewById(R.id.textview_yes);
		no = (TextView) findViewById(R.id.textview_no);
		title  = (TextView) findViewById(R.id.textview_title);
	}
	public interface Callback{
		public void onCallOK(Dialog dialog,TextView ok,TextView no);
		public void onCallCanncel(Dialog dialog,TextView ok,TextView no);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textview_yes:
			this.dismiss();
			if (callback != null) {
				callback.onCallOK(this, ok, no);
			}
			break;
		case R.id.textview_no:
			this.dismiss();
			if (callback != null) {
				callback.onCallCanncel(this, ok, no);
			}
			break;
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

        super.show();
        getWindow().setLayout((int) (size.x * 9 / 10),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
