package com.adnroid.file;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.file.R;

public class FileListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<ViewHolder> items;
	
	private List<ViewHolder> items2;
	
	private Drawable mIcon_folder;
	private Drawable mIcon_file;
	private Drawable mIcon_image;
	private Drawable mIcon_audio;
	private Drawable mIcon_video;
	private Drawable mIcon_xls;
	private Drawable mIcon_doc;
	private Drawable mIcon_txt;
	private Drawable mIcon_pdf;
	private Drawable mIcon_ppt;
	private Context context;


	public FileListAdapter(Context context, File[] files) {
		mInflater = LayoutInflater.from(context);
		items = new ArrayList<ViewHolder>();
		mIcon_folder = context.getResources().getDrawable(R.drawable.folder);
		mIcon_file = context.getResources().getDrawable(R.drawable.file);
		mIcon_image = context.getResources().getDrawable(R.drawable.pic);
		mIcon_audio = context.getResources().getDrawable(R.drawable.audio);
		mIcon_video = context.getResources().getDrawable(R.drawable.video);
		mIcon_xls = context.getResources().getDrawable(R.drawable.excel);
		mIcon_doc = context.getResources().getDrawable(R.drawable.word);
		mIcon_txt = context.getResources().getDrawable(R.drawable.text);
		mIcon_pdf = context.getResources().getDrawable(R.drawable.pdf);
		mIcon_ppt = context.getResources().getDrawable(R.drawable.ppt);
		this.context = context;
		inItems(files);
	}

	private void inItems(File[] files) {
		items.clear();
		if (files != null) {
			ArrayList<File> _files = new ArrayList<File>();
			ArrayList<File> _folders = new ArrayList<File>();
			for (File file : files) {
				if (file.isDirectory()) {
					_folders.add(file);
				} else {
					_files.add(file);
				}
			}
			_folders.addAll(_files);
			for (File f : _folders) {
				doItem(f);
			}
		}
	}

	private void doItem(File f) {
		ViewHolder vh = new ViewHolder();
		vh.path = f.getPath();
		vh.label = f.getName();
		vh.isDirectory = f.isDirectory();
		Drawable icon = mIcon_folder;
		String f_type = MyUtil.getMIMEType(f, false);
		if (!f.isDirectory()) {
			vh.type = f_type;
			if ("image".equals(f_type)) {
				icon = mIcon_image;
			} else if ("audio".equals(f_type)) {
				icon = mIcon_audio;
			} else if ("video".equals(f_type)) {
				icon = mIcon_video;
			} else if ("apk".equals(f_type)) {
//				String path = f.getPath();
//				String PATH_PackageParser = "android.content.pm.PackageParser";
//				String PATH_AssetManager = "android.content.res.AssetManager";
//				
//				try {
//					Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
//					Class<?>[] typeArgs = new Class[1];
//					typeArgs[0] = String.class;
//					Constructor<?> pkgParserCt = pkgParserCls.getConstructor(typeArgs);
//					Object[] valueArgs = new Object[1];
//					valueArgs[0] = path;
//					Object pkgParser = pkgParserCt.newInstance(valueArgs);
//					DisplayMetrics metrics = new DisplayMetrics();
//					metrics.setToDefaults();
//					typeArgs = new Class[4];
//					typeArgs[0] = File.class;
//					typeArgs[1] = String.class;
//					typeArgs[2] = DisplayMetrics.class;
//					typeArgs[3] = Integer.TYPE;
//					Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod("parsePackage", typeArgs);
//					valueArgs = new Object[4];
//					valueArgs[0] = new File(path);
//					valueArgs[1] = path;
//					valueArgs[2] = metrics;
//					valueArgs[3] = 0;
//					Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, valueArgs);
//					Field appInfoFld = pkgParserPkg.getClass().getDeclaredField("applicationInfo");
//					ApplicationInfo info = (ApplicationInfo) appInfoFld.get(pkgParserPkg);
//					Class<?> assetMagCls = Class.forName(PATH_AssetManager);
//					Constructor<?> assetMagCt = assetMagCls.getConstructor((Class[]) null);
//					Object assetMag = assetMagCt.newInstance((Object[]) null);
//					typeArgs = new Class[1];
//					typeArgs[0] = String.class;
//					Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath", typeArgs);
//					valueArgs = new Object[1];
//					valueArgs[0] = path;
//					assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
//					Resources res = context.getResources();
//					typeArgs = new Class[3];
//					typeArgs[0] = assetMag.getClass();
//					typeArgs[1] = res.getDisplayMetrics().getClass();
//					typeArgs[2] = res.getConfiguration().getClass();
//					Constructor<?> resCt = Resources.class.getConstructor(typeArgs);
//					valueArgs = new Object[3];
//					valueArgs[0] = assetMag;
//					valueArgs[1] = res.getDisplayMetrics();
//					valueArgs[2] = res.getConfiguration();
//					res = (Resources) resCt.newInstance(valueArgs);
//					// CharSequence label = null;
//
//					PackageManager pm = context.getPackageManager();
//					PackageInfo packageInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
//					ApplicationInfo appInfo = packageInfo.applicationInfo;				
//					if (info.icon != 0) {
//						icon = res.getDrawable(info.icon);
//						icon.setBounds(0, 0, 140, 150);
//						
//					} else {
//						icon = pm.getApplicationIcon(appInfo);
//					}
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
				
				 PackageManager pm = context.getPackageManager();
			     PackageInfo info = pm.getPackageArchiveInfo(f.getPath(),
			                PackageManager.GET_ACTIVITIES);
			        if (info != null) {
			            ApplicationInfo appInfo = info.applicationInfo;
			            appInfo.sourceDir = f.getPath();
			            appInfo.publicSourceDir = f.getPath();
			          
			            try {
			                icon = appInfo.loadIcon(pm);
			              //  icon.setBounds(1, 1, 20, 20);
//			                appInfo = null;
//			                info = null;
			            } catch (OutOfMemoryError e) {
			               e.printStackTrace();
			            }
			        }else{
						 icon = context.getResources().getDrawable(R.drawable.ic_launcher);
			        }  
			        
			        pm = null;
			        System.gc();
			        
			} else if ("xls".equals(f_type)) {
				icon = mIcon_xls;
			} else if ("doc".equals(f_type)) {
				icon = mIcon_doc;
			} else if ("pdf".equals(f_type)) {
				icon = mIcon_pdf;
			} else if ("ppt".equals(f_type)) {
				icon = mIcon_ppt;
			} else if ("txt".equals(f_type)) {
				icon = mIcon_txt;
			} else {
				icon = mIcon_file;
			}
			vh.size = MyUtil.fileSizeMsg(f);
		}
		vh.icon = icon;
		items.add(vh);
	}


	/* ��̳�BaseAdapter������д���·��� */
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup par) {
		try {
			ViewHolder holder = items.get(position);
			if (MyActivity.isGridView) {
				if (convertView == null) {
					convertView = mInflater.inflate(R.layout.grid_item, par, false);
				}
				/* �����ļ����ļ��е�������icon */
				final TextView textView = (TextView) convertView.findViewById(R.id.name);
				
				//获取屏幕宽高，设置不同情况的弹出框的大小
				WindowManager wm=(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
				int w=wm.getDefaultDisplay().getWidth();
				int h=wm.getDefaultDisplay().getHeight();
				if(w==1024&&h==600){
					holder.icon.setBounds(0, 0,170,155);
					textView.setPadding(4, 10, 4, 13);
					//textView.setHeight(220);
//					textView.setCompoundDrawablesWithIntrinsicBounds(null, holder.icon, null, null);
					textView.setLines(2);
				}else{
					
					holder.icon.setBounds(0, 0,96,96);
					textView.setPadding(2, 3, 2, 3);
					//textView.setCompoundDrawablesWithIntrinsicBounds(null, holder.icon, null, null);
					textView.setLines(2);
				}
				textView.setCompoundDrawables(null, holder.icon, null, null);
				
				//textView.setCompoundDrawablesWithIntrinsicBounds(null, holder.icon, null, null);
				
				ImageView checkBox = (ImageView) convertView.findViewById(R.id.grid_check);
				if (MyActivity.isOperation) {
					checkBox.setImageResource(holder.isSelected?R.drawable.grid_check_on:R.drawable.grid_check_off);
				} else {
					checkBox.setImageResource(0);
				}
				checkBox.setVisibility(MyActivity.isOperation ? View.VISIBLE : View.GONE);
				if(holder.label.length()>15){
					String a=holder.label.toString().substring(0,14)+"...";
					textView.setText(a);
				}else{
					textView.setText(holder.label);
				}
			} else {
				if (convertView == null) {
					 convertView = mInflater.inflate(R.layout.list_items, par, false);
				}
				((TextView) convertView.findViewById(R.id.f_title)).setText(holder.label);
				((TextView) convertView.findViewById(R.id.f_text)).setText(holder.size);
				ImageView icon = (ImageView) convertView.findViewById(R.id.f_icon);
				icon.setImageDrawable(holder.icon);
				ImageView checkBox = (ImageView) convertView.findViewById(R.id.grid_check);
				if (MyActivity.isOperation) {
					checkBox.setImageResource(holder.isSelected?R.drawable.grid_check_on:R.drawable.grid_check_off);
				} else {
					checkBox.setImageResource(0);
				}
				checkBox.setVisibility(MyActivity.isOperation ? View.VISIBLE : View.GONE);
			}
			convertView.setTag(holder);
		} catch (Exception e) {
			
		}
		return convertView;
	}
	/**
	 * ������дget set�������Ч�� class ViewHolder
	 **/
	public static class ViewHolder {
		CharSequence label;
		Drawable icon;
		String path;
		boolean isDirectory;
		String type;
		String size = "";
		boolean isSelected;
	}

	private static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
	   //drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumWidth());		
		drawable.draw(canvas);
		return bitmap;
	}
	/**
	 * ����
	 * 
	 * @Description:
	 * @Author: wanghb
	 * @Email: wanghb@foryouge.com.cn
	 * @param files
	 * @Others:
	 */
	public List refurbish(File[] files) {
		inItems(files);
		return items;
	}
	/**
	 * ɾ��
	 * 
	 * @Description:
	 * @Author: wanghb
	 * @Email: wanghb@foryouge.com.cn
	 * @param bhs
	 * @Others:
	 */
	
	public void delItems(File[] files) {
		if (!items.isEmpty()) {
			ArrayList<ViewHolder> tempItems = new ArrayList<ViewHolder>();
			tempItems.addAll(items);
			for (File file : files) {
				for (ViewHolder vh : items) {
					if (vh.path.equals(file.getPath())){
						tempItems.remove(vh);
					}
				}
			}
			items = tempItems;
		}
	}

	/**
	 * ����
	 * 
	 * @Description:
	 * @Author: wanghb
	 * @Email: wanghb@foryouge.com.cn
	 * @param files
	 * @Others:
	 */
	public void addItems(File[] files) {
		if (files != null)
			for (File file : files) {
				doItem(file);
			}
	}

	/**
	 * 
	 * 
	 * @Description:
	 * @Author: wanghb
	 * @Email: wanghb@foryouge.com.cn
	 * @param ischecked
	 * @Others:
	 */
	public void checkAll(boolean ischecked) {
		if (!items.isEmpty()) {
			ArrayList<ViewHolder> tempItems = new ArrayList<ViewHolder>();
			for (ViewHolder vh : items) {
				vh.isSelected = ischecked;
				tempItems.add(vh);
			}
			items = tempItems;
		}
	}
	public List<ViewHolder> getAllItems() {
		return this.items;
	}
	public void selectType(String string){
		items.clear();
		File file=new File(string);
		if (file != null) {
			ArrayList<File> _files = new ArrayList<File>();
			ArrayList<File> _folders = new ArrayList<File>();
				if (file.isDirectory()) {
					_folders.add(file);
				} else {
					_files.add(file);
				}
			_folders.addAll(_files);
			for (File f : _folders) {
				doItem(f);	
			}
		}
		
	}
}