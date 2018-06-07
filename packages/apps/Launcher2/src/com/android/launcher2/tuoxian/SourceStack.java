package com.android.launcher2.tuoxian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.constant.YeconConstants;
import android.os.SystemProperties;
import android.util.Log;

import com.android.launcher2.util.L;

public class SourceStack {
	private ArrayList<SourceItem> sourceList = new ArrayList<SourceStack.SourceItem>();
	public static final int SD = 0;
	public static final int USB = 1;
	public synchronized void pushNormal(String apkPackageName , String apkActivity){
		sourceList.clear();
		push(new NormalSource(apkPackageName, apkActivity));
	}
	public synchronized void pushDeviceSD(String apkPackageName , String apkActivity){
		for(int i = sourceList.size() - 1; i >= 0; i-- ){
			SourceItem item = sourceList.get(i);
			if(item instanceof DeviceSDSource){
				remove(item);
			}
		}
		push(new DeviceSDSource(apkPackageName, apkActivity));
	} 
	public synchronized void pushDeviceUSB(String apkPackageName , String apkActivity){
		for(int i = sourceList.size() - 1; i >= 0; i-- ){
			SourceItem item = sourceList.get(i);
			if(item instanceof DeviceUSBSource){
				remove(item);
			}
		}
		push(new DeviceUSBSource(apkPackageName, apkActivity));
	} 
	public synchronized void pushAutoExitSource(String apkPackageName , String apkActivity){
		for(int i = sourceList.size() - 1; i >= 0; i-- ){
			SourceItem item = sourceList.get(i);
			if(item instanceof AutoExitSource && item.apkPackageName.equalsIgnoreCase(apkPackageName)){
				remove(item);
				break;
			}
		}
		push(new AutoExitSource(apkPackageName, apkActivity));
	}
	public synchronized SourceItem doOutDeviceNormal(String apkPackageName){
		for(int i = sourceList.size() - 1; i >= 0; i-- ){
			SourceItem item = sourceList.get(i);
			if(item instanceof NormalSource && item.apkPackageName.equalsIgnoreCase(apkPackageName)){
				remove(item);
				break;
			}
		}
		return getTop();
	}
	public synchronized  Map<String, SourceItem>  doOutDeviceSD(){
		Map<String, SourceItem> map = new HashMap<String, SourceStack.SourceItem>();
		
		if(sourceList.size() > 0){
			SourceItem temp = sourceList.get(sourceList.size() - 1).clone();
			map.put("oldTopSourceItem",temp);
		}
		
		for(int i = sourceList.size() - 1; i >= 0; i-- ){
			SourceItem item = sourceList.get(i);
			if(item instanceof DeviceSDSource){
				remove(item);
			}
		}
		map.put("newTopSourceItem",getTop());
		return map;
		
	}
	public synchronized Map<String, SourceItem> doOutDeviceUSB(){
		Map<String, SourceItem> map = new HashMap<String, SourceStack.SourceItem>();
		
		if(sourceList.size() > 0){
			SourceItem temp = sourceList.get(sourceList.size() - 1).clone();
			map.put("oldTopSourceItem",temp);
		}
		
		for(int i = sourceList.size() - 1; i >= 0; i-- ){
			SourceItem item = sourceList.get(i);
			if(item instanceof DeviceUSBSource){
				remove(item);
			}
		}
		map.put("newTopSourceItem",getTop());
		return map;
	}
	public synchronized SourceItem doOutAutoExitSource(String apkPackageName){
		for(int i = sourceList.size() - 1; i >= 0; i-- ){
			SourceItem item = sourceList.get(i);
			/*L.e("SourceItem instanceof:" + (item instanceof AutoExitSource));
			L.e("SourceItem equalsIgnoreCase:" + (item.apkPackageName.equals(apkPackageName)));*/
			if(item instanceof AutoExitSource && item.apkPackageName.equalsIgnoreCase(apkPackageName)){
				boolean bl = remove(item);
//				L.e("SourceItem : remove   " + item.apkPackageName);
				break;
			}
		}
		for(int i = sourceList.size() - 1; i >= 0; i-- ){
			SourceItem item2 = sourceList.get(i);
//			L.e("SourceItem : " + item2.apkPackageName + "   " + item2.getClass().getName());
		}
		return getTop();
	}
	public synchronized SourceItem getTop(){
		for(int i = sourceList.size() - 1; i >= 0; i-- ){
			SourceItem item2 = sourceList.get(i);
		}
		if(sourceList == null ||  sourceList.size() < 1){	
			return null;
		}else{
			Log.d("TEST","persist.sys.fun.bt.enable "+SystemProperties.getInt("persist.sys.fun.bt.enable",0));
			Log.d("TEST",sourceList.get(sourceList.size() -1).apkPackageName);
			Log.d("TEST",sourceList.get(sourceList.size() -1).apkActivity);
			if(SystemProperties.getInt("persist.sys.fun.bt.enable",0) == 0
			 && sourceList.get(sourceList.size() -1).apkPackageName == YeconConstants.BLUETOOTH_PACKAGE_NAME){
				if(sourceList.size() < 2)
					return null;
				else
					return sourceList.get(sourceList.size() -2);
			}
			return sourceList.get(sourceList.size() -1);
		}
	}
	private synchronized boolean remove(SourceItem  item){
		if(sourceList.contains(item)){
			return sourceList.remove(item);
		}
		return false;
	}
	private synchronized void push(SourceItem  item){
		sourceList.add(item);
		L.e("SourceItem========================================================");
		for(int i = sourceList.size() - 1; i >= 0; i-- ){
			SourceItem item2 = sourceList.get(i);
			L.e("SourceItem : " + item2.apkPackageName+ " ACTICITY:" + item2.apkActivity + "  class:" + item2.getClass().getName() );
		}
		L.e("SourceItem========================================================");
	}
	
	
	
	
	public class SourceItem implements Cloneable{
		
		public String apkPackageName = "";
		public String apkActivity = "";
		public SourceItem(String apkPackageName,String apkActivity) {
			this.apkPackageName = apkPackageName == null ? "" : apkPackageName;
			this.apkActivity = apkActivity == null ? "" : apkActivity;
		}
		public SourceItem clone(){
			try {
				return (SourceItem) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	private class NormalSource extends SourceItem{

		public NormalSource(String apkPackageName, String apkActivity) {
			super(apkPackageName, apkActivity);
			// TODO Auto-generated constructor stub
		}
	}
	private class DeviceSDSource extends SourceItem{

		public DeviceSDSource(String apkPackageName, String apkActivity) {
			super(apkPackageName, apkActivity);
			// TODO Auto-generated constructor stub
		}
		
	}
	private class DeviceUSBSource extends SourceItem{

		public DeviceUSBSource(String apkPackageName, String apkActivity) {
			super(apkPackageName, apkActivity);
			// TODO Auto-generated constructor stub
		}
		
	}
	private class AutoExitSource extends SourceItem{

		public AutoExitSource(String apkPackageName, String apkActivity) {
			super(apkPackageName, apkActivity);
			// TODO Auto-generated constructor stub
		}
		
	}
}