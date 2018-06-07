package com.android.yecon.gpstest;

import java.util.ArrayList;
import java.util.Iterator;
import java.lang.Math;


import com.android.yecon.gpstest.GPSTest.Item;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;




public class GpsEarthView extends View {

	Context mContext;
	
	Bitmap mEarthBmp;
	
	private ArrayList<Item> mList=new ArrayList<Item>();
	private int heightSize;
	private int widthSize;

	private boolean isLocation;
	
	public void setMList(ArrayList<Item> mList){
		this.mList=mList;
		
		
		
	}
	public void setIsLocation(boolean isLocation){
		this.isLocation=isLocation;
		
	}
	
	
	public GpsEarthView(Context context, AttributeSet attrs) {
		super(context, attrs);
		System.out.println("eeeecontext, attrs");
		/*mEarthBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.earth);*/
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		System.out.println("onMeasure");
		heightSize = MeasureSpec.getSize(heightMeasureSpec);
		 
		widthSize = heightSize;
		 
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		System.out.println("onDraw");
		
		/*Paint paint = new Paint();
		paint.setTextSize(16);
		paint.setAntiAlias(true);
		paint.setColor(Color.GRAY);
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
		
		Matrix m = new Matrix();
//		m.setScale(getWidth()/mEarthBmp.getWidth(), getHeight()/mEarthBmp.getHeight());
		m.setScale(1.0f, 1.0f);
		
		canvas.drawBitmap(mEarthBmp, m, paint);
//		canvas.drawBitmap(mEarthBmp, 0, 0, paint);
		
		*/
		
		if(!mList.isEmpty()){
			Paint p3 = new Paint();
			p3.setColor(Color.WHITE);
			p3.setTextSize(8);
			p3.setAntiAlias(true);
			
			Paint p1 =new Paint();
			if(isLocation){
				p1.setColor(Color.GREEN);
			}else{
				p1.setColor(Color.BLUE);
			}
			
			Iterator<Item> it = mList.iterator();
			while(it.hasNext()){
					
				Item item = it.next();
					
				int prn = item.mPrn;
				float azi = item.mAzi;
				float ele = item.mEle;
				
				float a=(float) (heightSize/2+(heightSize*ele/180)*Math.sin(azi));
				float b=(float) (heightSize/2-(heightSize*ele/180)*Math.cos(azi));
				
				canvas.drawCircle(a, b, 8, p1);
				/*canvas.drawText(""+prn , a, b, p3);*/
			
					
				
				}
			 
			 
		 }
			  
			  
		  
		

	}
	
	

}
