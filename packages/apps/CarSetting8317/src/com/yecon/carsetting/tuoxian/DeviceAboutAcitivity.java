package com.yecon.carsetting.tuoxian;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.tuoxianui.tools.AtTimerHelpr;
import com.yecon.carsetting.FragmentTabAcitivity;
import com.yecon.carsetting.R;
import com.yecon.carsetting.fragment.Fragment_List;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.L;
import com.yecon.carsetting.unitl.tzutil;
import com.yecon.carsetting.util.AssetUtil;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DeviceAboutAcitivity extends Activity implements OnClickListener{
	private Activity self =this;
	private static Context mContext;
	private ListView listView;
	private int count = 0;
	private String totalMemory = "";
	private String availableMemory = "";
	private String deviceName = "";
	private String androidId = "";
	private String androidOSVesion = "";
	private String[] cpuInfo;
	private AtTimerHelpr mAtTimerHelpr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		setContentView(R.layout.activity_tuoxian_about);
		initView();
		
	}
	
	
	private void initView() {
		findViewById(R.id.back).setOnClickListener(this);
		listView = (ListView) findViewById(R.id.about_list);
		listView.setAdapter(new AboutAdapter(this));
		
		//FIXME: 一汽要求,不自动流转  mAtTimerHelpr = new AtTimerHelpr();
		AtTimerHelpr.AtTimerHelprDoItCallBack atTimerHelprDoItCallBack = new AtTimerHelpr.AtTimerHelprDoItCallBack() {
			@Override
			public void doIt() {
				backHandler.sendEmptyMessage(0);
			}
		};
		//FIXME: 一汽要求,不自动流转  mAtTimerHelpr.setCallBack(atTimerHelprDoItCallBack);
	}
	
	private void initData() {
		mContext = this;
		
	    /*L.e("DEVICE BRAND:" + android.os.Build.BRAND);
	    L.e("DEVICE CPU_ABI:" + android.os.Build.CPU_ABI);
	    L.e("DEVICE DISPLAY:" + android.os.Build.DISPLAY);
	    L.e("DEVICE FINGERPRINT:" + android.os.Build.FINGERPRINT);
	    L.e("DEVICE HOST:" + android.os.Build.HOST);
	    L.e("DEVICE MANUFACTURER:" + android.os.Build.MANUFACTURER);
	    L.e("DEVICE MODEL:" + android.os.Build.MODEL);
	    L.e("DEVICE PRODUCT:" + android.os.Build.PRODUCT);
	    L.e("DEVICE TAGS:" + android.os.Build.TAGS);
	    L.e("DEVICE TIME:" + android.os.Build.TIME);
	    L.e("DEVICE TYPE:" + android.os.Build.TYPE);
	    L.e("DEVICE USER:" + android.os.Build.USER );
	    L.e("DEVICE SERIAL:" + android.os.Build.SERIAL );
	    L.e("DEVICE MODEL:" + android.os.Build.MODEL);
	    L.e("DEVICE HOST:" + android.os.Build.HOST);
	    L.e("DEVICE HARDWARE:" + android.os.Build.HARDWARE);
	    androidOSVesion = android.os.Build.VERSION.RELEASE;
	    L.e("DEVICE CODENAME:" + android.os.Build.VERSION.CODENAME);
	    L.e("DEVICE INCREMENTAL:" + android.os.Build.VERSION.INCREMENTAL);
	    L.e("DEVICE SDK_INT:" + android.os.Build.VERSION.SDK_INT);
	    L.e("DEVICE Available:" + formatToMB(getAvailableInternalMemorySize()));
	    L.e("DEVICE Total:" + formatToMB(getTotalInternalMemorySize()));
	    runOnUiThread(new Runnable() {
			@Override
			public void run() {
				cpuInfo = getCpuInfo();
				if(listView != null){
					listView.setAdapter(new AboutAdapter(self));
				}
			}
		});*/
		deviceName =  Function.getCpuType();
	    androidId = new String(android.os.Build.SERIAL);
	    totalMemory = formatToMB(getTotalInternalMemorySize() + getTotalExternalMemorySize());
	    availableMemory = formatToMB(getAvailableInternalMemorySize() + getAvailableExternalMemorySize());
	}
	
	public void openDebug(){
		count = 0;
		startActivity(new Intent(mContext, FragmentTabAcitivity.class));
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}
	
	public String[] getCpuInfo() {
		String str1 = "/proc/cpuinfo";
		String str2="";
		String[] cpuInfo={"",""};
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
			}
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			cpuInfo[1] += arrayOfString[2];
			localBufferedReader.close();
		} catch (IOException e) {
		}
		return cpuInfo;
	}
	
	public long getTotalInternalMemorySize() {  
	    File path = Environment.getDataDirectory();  
	    StatFs statFs = new StatFs(path.getPath());
	    long blockSize = statFs.getBlockSize();
	    long totalBlocks = statFs.getBlockCount();
	    return blockSize * totalBlocks;     
	}  
	public long getAvailableInternalMemorySize() {  
	    File path = Environment.getDataDirectory();  
	    StatFs statFs = new StatFs(path.getPath());
	    long blockSize = statFs.getBlockSize();
	    long availableBlocks = statFs.getAvailableBlocks();
	    return blockSize * availableBlocks;     
	}
	
	
	//
	public long getTotalExternalMemorySize() {
		try {
	        if (externalMemoryAvailable()) {
	            File path = Environment.getExternalStorageDirectory();
	            StatFs stat = new StatFs(path.getPath());
	            long blockSize = stat.getBlockSize();
	            long totalBlocks = stat.getBlockCount();
	            return totalBlocks * blockSize;
	        } else {
	            return 0;
	        }
		} catch (Exception e) {
			L.e(e.toString());
		}
		return 0;
    }

	
	public long getAvailableExternalMemorySize() {
		try {
	        if (externalMemoryAvailable()) {
	            File path = Environment.getExternalStorageDirectory();
	            StatFs stat = new StatFs(path.getPath());
	            long blockSize = stat.getBlockSize();
	            long availableBlocks = stat.getAvailableBlocks();
	            return availableBlocks * blockSize;
	        } else {
	            return 0;
	        }
		} catch (Exception e) {
			L.e(e.toString());
		}
		return 0;
    }
	public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }
	//
	
	public String formatToMB(long memorySize) {  
	    double d = 0;;
	    d = memorySize / Math.pow(1024,2);
	    if(d > 1000){
	    	d = memorySize / Math.pow(1024,3);
	    	return String.format("%.2f", d) + " GB";  
	    }
	    return String.format("%.2f", d) + " MB";   
	}
	
	private  class AboutAdapter extends BaseAdapter{
		private View oneView;
		private View twoView;
		Resources res;
		String[] aboutList;
		LayoutInflater inflater;
		public AboutAdapter(Context context) {
			inflater = LayoutInflater.from(context);
			res = getResources();
			aboutList = res.getStringArray(R.array.about_list);
		}
		@Override
		public int getCount() {
			return aboutList.length;
		}

		@Override
		public Object getItem(int position) {
			return aboutList[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = inflater.inflate(R.layout.list_about_item,parent,false);
			if(oneView == null){
				oneView = view;
				oneView.setOnClickListener(new OnClickListener() {	
					@Override
					public void onClick(View v) {
						count++;
						L.e(" View touch:"+count + "");
						if(count >= 33 && count % 10 > 3){
							openDebug();
						}
					}
				});
			}else if(twoView == null){
				twoView = view;
				twoView.setOnClickListener(new OnClickListener() {	
					@Override
					public void onClick(View v) {
						count +=10;
						L.e(" View touch:"+count + "");
						if(count >= 33 && count % 10 > 3){
							openDebug();
						}
					}
				});
			}
			
			try {
				JSONObject jsonObject = new JSONObject(aboutList[position]);
				TextView name = (TextView) view.findViewById(R.id.about_item_name);
				TextView content = (TextView) view.findViewById(R.id.about_item_content);
				/*L.e("name : " + name + " content:" + content);
				name.setText(jsonObject.getString("name"));
				content.setText(jsonObject.getString("content")); */
				if(jsonObject.has("name")){	
					name.setText(jsonObject.getString("name") + ":");
				}
				if(jsonObject.has("content")){
					content.setText(jsonObject.getString("content"));
					switch (position) {
					case 0:
						content.setText(tzutil.getmcuVersion() + " - "+ deviceName);
						break;
					case 1:
						String kernel = getFormattedKernelVersion();
						content.setText(kernel);
						break;
					/*case 2:
						content.setText(androidOSVesion);
						break;*/
					/*case 3:
						content.setText(cpuInfo[0]);
						break;*/
					case 2:
						content.setText(totalMemory);
						break;
					case 3:
						content.setText(availableMemory);
						break;
					case 4:
						content.setText(androidId);
						break;
					default:
						break;
					}
				}
				return view;
			} catch (JSONException e) {
                e.printStackTrace();
            }
			return view;
		}}
	
	public String getFormattedKernelVersion() {
		try {
			return formatKernelVersion(readLine("/proc/version"));

		} catch (IOException e) {
			// L.e("","IO Exception when getting kernel version for Device Info screen",
			// e);

			return "Unavailable";
		}
	}
	
	private String readLine(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
		try {
			return reader.readLine();
		} finally {
			reader.close();
		}
	}
	
	public String formatKernelVersion(String rawKernelVersion) {
		// Example (see tests for more):
		// Linux version 3.0.31-g6fb96c9 (android-build@xxx.xxx.xxx.xxx.com) \
		// (gcc version 4.6.x-xxx 20120106 (prerelease) (GCC) ) #1 SMP PREEMPT \
		// Thu Jun 28 11:02:39 PDT 2012
		L.e("rawKernelVersion:" + rawKernelVersion);
		final String PROC_VERSION_REGEX = "Linux version (\\S+) " + /*
																	 * group 1:
																	 * "3.0.31-g6fb96c9"
																	 */
		"\\((\\S+?)\\) " + /* group 2: "x@y.com" (kernel builder) */
		"(?:\\(gcc.+? \\)) " + /* ignore: GCC version information */
		"(#\\d+) " + /* group 3: "#1" */
		"(?:.*?)?" + /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
		"((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /*
											 * group 4:
											 * "Thu Jun 28 11:02:39 PDT 2012"
											 */

		Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(rawKernelVersion);
		if (!m.matches()) {
			// Log.e(LOG_TAG, "Regex did not match on /proc/version: " +
			// rawKernelVersion);
			return "Unavailable";
		} else if (m.groupCount() < 4) {
			// Log.e(LOG_TAG, "Regex match on /proc/version only returned " +
			// m.groupCount()
			// + " groups");
			return "Unavailable";
		}
		/*return m.group(1) + "\n" + // 3.0.31-g6fb96c9
				m.group(2) + " " + m.group(3) + // x@y.com #1
				" " + m.group(4); // Thu Jun 28 11:02:39 PDT 2012
*/		
		/*return m.group(1) + "\n" + // 3.0.31-g6fb96c9
		"Android" + " " + m.group(3) + // x@y.com #1
		" " + m.group(4); // Thu Jun 28 11:02:39 PDT 2012
*/		
//		return "V1.1  " + m.group(4); // Thu Jun 28 11:02:39 PDT 2012
		return AssetUtil.getStringFromAssert(this, "kernel") + " " + m.group(4);
	}
	
	Handler backHandler = new Handler() {
		public void handleMessage(Message msg) {
			onBackPressed();
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		//FIXME: 一汽要求,不自动流转  mAtTimerHelpr.start(10);
	}
	@Override
	protected void onPause() {
		super.onPause();
		//FIXME: 一汽要求,不自动流转  mAtTimerHelpr.stop();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//FIXME: 一汽要求,不自动流转  mAtTimerHelpr.reset();
		return super.onTouchEvent(event);
	}
	
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//FIXME: 一汽要求,不自动流转  mAtTimerHelpr.reset();
		return super.dispatchTouchEvent(ev);
	}
}

