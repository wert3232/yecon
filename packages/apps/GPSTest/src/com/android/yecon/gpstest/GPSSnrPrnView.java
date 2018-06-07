package com.android.yecon.gpstest;


import java.util.ArrayList;

import java.util.Iterator;

import com.android.yecon.gpstest.GPSTest.Item;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import android.util.AttributeSet;
import android.view.View;

public class GPSSnrPrnView extends View  {

	/*LocationManager mLoc;
	GpsStatus mGpsStatus;
	Iterator<GpsSatellite> mIterator;
	ArrayList<Item> mList;
	public boolean invalidateFlag = false;*/
	ArrayList<Item> mList=new ArrayList<Item>();
	boolean isLocation =false;
	
	
	public void setMList(ArrayList<Item> mList){
		this.mList=mList;
	}
	public void setIsLocation(boolean isLocation){
		this.isLocation=isLocation;
		
	}
	
	
	public GPSSnrPrnView(Context context, AttributeSet attrs) {
		super(context, attrs);
		System.out.println("context, attrs");
		/*this.postInvalidate();
		mLoc = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		mLoc.addGpsStatusListener(this);
		mList = new ArrayList<Item>();*/
		
		
	}
	
	/*class Item{
		float mSnr;
		int mPrn;
		public Item (float snr, int prn){
			mSnr = snr;
			mPrn = prn;
		}
	}*/
	
/*	public void onGpsStatusChanged(int event) {
		// TODO Auto-generated method stub
		if(event == GpsStatus.GPS_EVENT_SATELLITE_STATUS){
			GpsStatus xGpsStatus = mLoc.getGpsStatus(null);
			Iterable<GpsSatellite> iStatellite = xGpsStatus.getSatellites();
			mIterator = iStatellite.iterator();
			mList.clear();
			
			while(mIterator.hasNext()){
				if(!invalidateFlag) invalidateFlag = true;
				GpsSatellite satellite = mIterator.next();
				float snr = satellite.getSnr();
				int prn = satellite.getPrn();
				
				mList.add(new Item(snr, prn));
			}
			
			Collections.sort(mList, new Comparator<Item>() {

				public int compare(Item i1, Item i2) {
					// TODO Auto-generated method stub
					float snr1 = i1.mSnr;
					float snr2 = i2.mSnr;
					if(snr1 < snr2) return 1;
					else if(snr1 == snr2) return 0;
					else return -1;
				}
			});
			
			if(invalidateFlag){
				postInvalidate();
				invalidateFlag = false;
			}
		}
	}*/
	
	@Override
	protected void onDraw(Canvas canvas) {
		System.out.println("onDraw");
		  if(mList.isEmpty()){
			  DrawImage(canvas);
			  
			  
		  }else{
			  DrawSnr(canvas);
		  }

		    
			
		
	}

	private void DrawImage(Canvas canvas) {
		// TODO Auto-generated method stub
		
		
		Paint p3 = new Paint();
		p3.setColor(Color.BLACK);
		p3.setTextSize(15);
		p3.setAntiAlias(true);
		
		
		Paint p2 = new Paint();
		p2.setTextSize(16);
		p2.setAntiAlias(true);
		p2.setColor(Color.BLACK);
		
		canvas.drawText(getResources().getString(R.string.fetch_gps_data), 180, 20, p2);
		
		Paint p1 = new Paint();
		p1.setAntiAlias(true);
		p1.setColor(Color.GRAY);
		for(int i =0;i<12;i++){
			canvas.drawRect(45+i*35,  35, 70+i*35,  134, p1);
			
			canvas.drawText("0"  ,  (i+1)*35 + 18, 152, p3);
			
			
			
		}
	/*	(i+1)*35 + 18
	 * canvas.drawRect(45,  35, 70,  134, paint);
		canvas.drawRect(80,  35, 105, 134, paint);
		canvas.drawRect(115, 35, 140, 134, paint); 
		canvas.drawRect(150, 35, 175, 134, paint);
		canvas.drawRect(185, 35, 210, 134, paint);
		canvas.drawRect(220, 35, 245, 134, paint);
		canvas.drawRect(255, 35, 280, 134, paint);
		canvas.drawRect(290, 35, 315, 134, paint);
		canvas.drawRect(325, 35, 350, 134, paint);
		canvas.drawRect(360, 35, 385, 134, paint);
		canvas.drawRect(395, 35, 420, 134, paint);
		canvas.drawRect(430, 35, 455, 134, paint);*/
	}

	private void DrawSnr(Canvas canvas) {
		// TODO Auto-generated method stub
		/*int x = 0, step = 35;
		int i = 1;*/
		Paint p1 = new Paint();
		p1.setStyle(Style.FILL);
		
		if(isLocation){
			p1.setColor(Color.GREEN);
		}else{
			p1.setColor(Color.BLUE);
		}
		
		
		Paint p2 = new Paint();
		p2.setColor(Color.GRAY);
		
		Paint p3 = new Paint();
		p3.setColor(Color.BLACK);
		p3.setTextSize(15);
		p3.setAntiAlias(true);
		
		
		
		
		
		Iterator<Item> it = mList.iterator();
		
		for(int i =0;i<12;i++){
			if(it.hasNext()){
				Item item = it.next();
				int snr = (int)item.mSnr;
				
				int prn = item.mPrn;
				
				
				/*x = step * i++ ;*/
				
				canvas.drawRect(45+i*35, 35, 70+i*35, 134, p2);
				
				canvas.drawRect( 45+i*35, 134-2*snr, 70+i*35, 134, p1);
				
				
				canvas.drawText("" + snr, (i+1)*35 + 18, 30,  p3);
				
				canvas.drawText("" + prn, (i+1)*35 + 18, 152, p3);
			}else{
				canvas.drawRect(45+i*35,  35, 70+i*35,  134, p2);
				
				canvas.drawText("0"  , (i+1)*35 + 18, 152, p3);
				
			}
			
			
			
		}
		
		/*while(it.hasNext()){
			
			Item item = it.next();
			int snr = (int)item.mSnr;
			int prn = item.mPrn;
			
			p1.setStyle(Style.FILL);
			p1.setColor(Color.GREEN);
			p2.setColor(Color.GRAY);
			x = step * i++ ;
			canvas.drawRect( x + 10, 35, x + step, 134, p2);
			canvas.drawRect( x + 10, 134-snr, x + step, 134, p1);
			
			p3.setColor(Color.BLACK);
			p3.setTextSize(15);
			p3.setAntiAlias(true);
			canvas.drawText("" + snr, x + 18, 30,  p3);
			canvas.drawText("" + prn, x + 18, 152, p3);
		}*/
	}

	
}

















