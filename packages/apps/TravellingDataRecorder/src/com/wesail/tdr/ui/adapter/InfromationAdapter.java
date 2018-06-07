package com.wesail.tdr.ui.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wesail.tdr.L;
import com.wesail.tdr.R;

public class InfromationAdapter extends BaseAdapter{
	private Resources res;
	private String[] strList;
	private LayoutInflater inflater;
	private Rect bounds = new Rect();
	private TextPaint paint;
	private int greatestWidth;
	
	public InfromationAdapter(Context context,int arrID) {
		inflater = LayoutInflater.from(context);
		res = context.getResources();
		strList = res.getStringArray(arrID);
	}
	public InfromationAdapter(Context context,List<String> list) {
		inflater = LayoutInflater.from(context);
		res = context.getResources();
		int size = list.size();  
		strList = (String[]) list.toArray(new String[size]);
	}
	@Override
	public int getCount() {
		return strList.length;
	}

	@Override
	public Object getItem(int position) {
		return strList[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tx = (TextView) inflater.inflate(R.layout.information_list_item,parent,false);
		try {
			String text = strList[position];
			
			//根据文字内容 自适应 parent 的宽度
			paint = tx.getPaint();
			paint.getTextBounds(text, 0, text.length(), bounds);
			if(bounds.width() > greatestWidth){	
				greatestWidth = bounds.width();
				parent.getLayoutParams().width = bounds.width() + 10;
			}
			
			tx.setText(text); 
			return tx;
		} catch (Exception e) {
            e.printStackTrace();
        }
		return tx;
	}
	public int getAdaptiveTextWidth(){
		TextView tx = (TextView) inflater.inflate(R.layout.information_list_item,null);
		TextPaint paint = tx.getPaint();
		Rect bounds = new Rect();
		int width = -1;
		if(strList == null) return -1;
		for(String text : strList){
			if(text != null){
				paint.getTextBounds(text, 0, text.length(), bounds);
				width = bounds.width() > width ? bounds.width() : width;
			}
		}
		return width;
	}
}
