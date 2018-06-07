package com.yecon.music;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DeviceView extends LinearLayout{
	private Context mContext;
	private String text = "";
	private Drawable drawable;
	public DeviceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.device);
		text = ta.getString(R.styleable.device_text);
		drawable = ta.getDrawable(R.styleable.device_icon);
		init();
		ta.recycle();
	}
	public DeviceView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}
	
	public DeviceView(Context context) {
		this(context,null);
	}
	public TextView getTextView(){
		return (TextView) this.findViewById(R.id.text);
	}
	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		TextView textView = (TextView) this.findViewById(R.id.text);
		ImageView imageView = (ImageView) this.findViewById(R.id.icon);
		textView.setSelected(selected);
		imageView.setSelected(selected);
	}
	@Override
	public void setActivated(boolean activated) {
		super.setActivated(activated);
		TextView textView = (TextView) this.findViewById(R.id.text);
		ImageView imageView = (ImageView) this.findViewById(R.id.icon);
		if(textView != null){	
			textView.setActivated(activated);
		}
		if(imageView != null){
			imageView.setActivated(activated);
		}
	}
	private void init() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View view = layoutInflater.inflate(R.layout.device_item, this);
		if(!TextUtils.isEmpty(text)){
			TextView textView = (TextView) view.findViewById(R.id.text);
			textView.setText(text);
		}
		if(drawable != null){
			ImageView imageView = (ImageView) view.findViewById(R.id.icon);
			imageView.setImageDrawable(drawable);
		}
	}

}
