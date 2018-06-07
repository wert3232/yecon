package com.yecon.fmradio;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


public class FreqItemView extends FrameLayout{
	private Context mContext;
	private String noStr = "";
	private String valueStr = "";
	private String unitStr = "";
	private FreqItemView self = this;
	public FreqItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FreqItemButton);
		noStr = ta.getString(R.styleable.FreqItemButton_no);
		valueStr = ta.getString(R.styleable.FreqItemButton_value);
		unitStr = ta.getString(R.styleable.FreqItemButton_unit);
		init();
		ta.recycle();
	}
	public FreqItemView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}
	public boolean valueIsEmpty(){
		TextView valueView= (TextView) this.findViewById(R.id.freq_info_value);
		return TextUtils.isEmpty(valueView.getText());
	}
	public FreqItemView(Context context) {
		this(context,null);
	}
	public TextView getNo(){
		return (TextView) this.findViewById(R.id.freq_info_no);
	}
	public TextView getValue(){
		return (TextView) this.findViewById(R.id.freq_info_value);
	}
	public TextView getUnit(){
		return (TextView) this.findViewById(R.id.freq_info_unit);
	}
	public void setText(int no,String value,String unit){
		getNo().setText(no + "");
		if(TextUtils.isEmpty(value) || Float.parseFloat(value) == 0){
			getValue().setText(R.string.fmr_empty);
			getUnit().setText("");
		}else{	
			getValue().setText(value);
			getUnit().setText(unit);
		}
	}
	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		TextView no = (TextView) this.findViewById(R.id.freq_info_no);
		TextView value = (TextView) this.findViewById(R.id.freq_info_value);
		TextView unit = (TextView) this.findViewById(R.id.freq_info_unit);
		no.setSelected(selected);
		value.setSelected(selected);
		unit.setSelected(selected);
	}
	@Override
	public void setActivated(boolean activated) {
		super.setActivated(activated);
		TextView no = (TextView) this.findViewById(R.id.freq_info_no);
		TextView value = (TextView) this.findViewById(R.id.freq_info_value);
		TextView unit = (TextView) this.findViewById(R.id.freq_info_unit);
		if(no != null){	
			no.setActivated(activated);
		}
		if(value != null){
			value.setActivated(activated);
		}
		if(unit != null){
			unit.setActivated(activated);
		}
	}
	private void init() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View view = layoutInflater.inflate(R.layout.freq_item_layout, this);
		if(!TextUtils.isEmpty(noStr)){
			TextView textView = (TextView) view.findViewById(R.id.freq_info_no);
			textView.setText(noStr);
		}
		if(!TextUtils.isEmpty(valueStr)){
			TextView textView = (TextView) view.findViewById(R.id.freq_info_value);
			textView.setText(valueStr);
		}
		if(!TextUtils.isEmpty(unitStr)){
			TextView textView = (TextView) view.findViewById(R.id.freq_info_unit);
			textView.setText(unitStr);
		}
	}
	public void blink(){
//		final boolean currentSelect = this.isSelected();
		final boolean currentSelect = false;
		this.setSelected(true);
		postDelayed(new Runnable() {
			@Override
			public void run() {
				self.setSelected(currentSelect);
			}
		}, 200);
	}
}
