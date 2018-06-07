package com.yecon.carsetting.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.autochips.whitelist.WhiteList;
import com.yecon.carsetting.R;
import com.yecon.carsetting.DateBase.MapListDB;
import com.yecon.carsetting.adapter.AppListAdpter;
import com.yecon.carsetting.adapter.AppListAdpter.onCheckBoxListener;
import com.yecon.carsetting.bean.AppInfo;
import com.yecon.carsetting.unitl.L;

public class Fragment_MapAppSelect extends DialogFragment implements onCheckBoxListener,
		OnItemClickListener {
	private Context mContext;
	private ListView all_list_view;
	private TextView text_prompt;
	private PackageManager packageManager;
	AppListAdpter mAppListAdpter;
	private static final int UPDATE_APP_LIST = 1;
	private MapListDB mMapListDB;

	public Fragment_MapAppSelect() {

	}

	public Fragment_MapAppSelect(Context context) {
		mContext = context;
		packageManager = context.getPackageManager();
		mMapListDB = new MapListDB(mContext);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Bitmap bk = BitmapFactory.decodeResource(getResources(), R.drawable.dialog_set_bk);
		Window window = getDialog().getWindow();
		window.setLayout(bk.getWidth(), bk.getHeight());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomizeActivityTheme);
		initData();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(mContext.getString(R.string.nav_maps_list_title));
		// getDialog().getActionBar().setBackgroundDrawable(arg0);
		View rootView = inflater.inflate(R.layout.navigation_all_list_main, container);
		initView(rootView, mContext);
		return rootView;
	}

	private void initView(View rootView, Context context) {
		text_prompt = (TextView) rootView.findViewById(R.id.text_prompt);
		all_list_view = (ListView) rootView.findViewById(R.id.all_list_view);
		if (mAppListAdpter.getCount() > 0)
			text_prompt.setVisibility(View.GONE);
		// all_list_view.setOnItemClickListener(this);
		myHandler.sendEmptyMessage(UPDATE_APP_LIST);
		AppListAdpter.setCheckBoxListener(this);
	}

	private void initData() {
		mAppListAdpter = new AppListAdpter(mContext, getAllApps());
	}

	public List<AppInfo> getAllApps() {
		List<AppInfo> list = new ArrayList<AppInfo>();
		AppInfo myAppInfo;
		// 获取到所有安装了的应用程序的信息，包括那些卸载了的，但没有清除数据的应用程序
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
		for (PackageInfo info : packageInfos) {

			// Only display the non-system app info
			// if((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==0)
			// {

			myAppInfo = new AppInfo();
			String packageName = info.packageName;
			ApplicationInfo appInfo = info.applicationInfo;
			// Drawable icon = appInfo.loadIcon(packageManager);
			Drawable icon = packageManager.getApplicationIcon(appInfo); // Jade
																		// modify
			// String className=info.applicationInfo.className;
			String className = findActivitiesForPackage(mContext, packageName);
			if (className == null)
				continue;
			// 拿到应用程序的程序名
			if (WhiteList.getPackageType(packageName) == WhiteList.APP_NAVIGATOR) { // Jade
																					// add;
				String appName = appInfo.loadLabel(packageManager).toString();
				myAppInfo.setPackageName(packageName);
				myAppInfo.setAppName(appName);
				myAppInfo.setAppIcon(icon);
				myAppInfo.setClassName(className);
				myAppInfo.setCheck(mMapListDB.isExistApp(packageName));
				// L.v(packageName+"&&&getAllApps&&&&"+className+"&&&&"+mMapListDB.isExistApp(packageName));
				list.add(myAppInfo);// 如果非系统应用，则添加至appList
			}
			// }
		}
		return list;
	}

	/**
	 * Query the package manager for MAIN/LAUNCHER activities in the supplied
	 * package.
	 */
	private String findActivitiesForPackage(Context context, String packageName) {
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mainIntent.setPackage(packageName);
		final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
		if (apps.size() == 0)
			return null;
		return apps.get(0).activityInfo.name;
		// return apps != null ? apps : new ArrayList<ResolveInfo>();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View mView, int position, long arg3) {
		L.v(mAppListAdpter.getItem(position).appName + "&&&&&"
				+ mAppListAdpter.getItem(position).className);
	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_APP_LIST:
				all_list_view.setAdapter(mAppListAdpter);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCheckedChanged(boolean isCheck, AppInfo mAppInfo) {
		if (isCheck) {
			mMapListDB.insertMapInfo(mAppInfo);
		} else {
			mMapListDB.delMap_Appinfo(mAppInfo);
		}

	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (mListener != null)
			mListener.onCheckedChanged();
		AppListAdpter.setCheckBoxListener(null);
		super.onCancel(dialog);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		if (mListener != null)
			mListener.onCheckedChanged();
	}

	public interface onCheckBoxListener {
		public void onCheckedChanged();
	}

	public static onCheckBoxListener mListener;

	public static void setCheckBoxListener(onCheckBoxListener mL) {
		mListener = mL;
	}

}
