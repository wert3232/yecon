package com.adnroid.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;
public class Util {


    /**
     * 内置
     */
    static String SDCARD_INTERNAL = "internal";


    /**
     * 外置
     */
    static String SDCARD_EXTERNAL = "external";
    
    private Context context;
    
    public Util(Context context) {
		this.context=context;
	}


    /**
     * API14以下通过读取Linux的vold.fstab文件来获取SDCard信息
     * 
     * @return
     */
    public static HashMap<String, SDCardInfo> getSDCardInfoBelow14() {
        HashMap<String, SDCardInfo> sdCardInfos = new HashMap<String, SDCardInfo>();
        BufferedReader bufferedReader = null;
        List<String> dev_mountStrs = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(Environment.getRootDirectory().getAbsoluteFile()
                    + File.separator + "etc" + File.separator + "vold.fstab"));
            dev_mountStrs = new ArrayList<String>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("dev_mount")) {
                    dev_mountStrs.add(line);
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; dev_mountStrs != null && i < dev_mountStrs.size(); i++) {
            SDCardInfo sdCardInfo = new SDCardInfo();
            String[] infoStr = dev_mountStrs.get(i).split(" ");
            sdCardInfo.setLabel(infoStr[1]);
            sdCardInfo.setMountPoint(infoStr[2]);
            if (sdCardInfo.getMountPoint().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                sdCardInfo.setMounted(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
                sdCardInfos.put(SDCARD_INTERNAL, sdCardInfo);
            } else if (sdCardInfo.getMountPoint().startsWith("/mnt")
                    && !sdCardInfo.getMountPoint().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                File file = new File(sdCardInfo.getMountPoint() + File.separator + "temp");
                if (file.exists()) {
                    sdCardInfo.setMounted(true);
                } else {
                    if (file.mkdir()) {
                        file.delete();
                        sdCardInfo.setMounted(true);
                    } else {
                        sdCardInfo.setMounted(false);
                    }
                }
                sdCardInfos.put(SDCARD_EXTERNAL, sdCardInfo);
            }
        }
        return sdCardInfos;
    }


    // // API14以上包括14通过改方法获取
    public  HashMap<String, SDCardInfo> getSDCardInfo() {
        HashMap<String, SDCardInfo> sdCardInfos = new HashMap<String, SDCardInfo>();
        String[] storagePathList = null;
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method getVolumePaths = storageManager.getClass().getMethod("getVolumePaths");
            storagePathList = (String[]) getVolumePaths.invoke(storageManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (storagePathList != null && storagePathList.length > 0) {
            String mSDCardPath = storagePathList[0];
            SDCardInfo internalDevInfo = new SDCardInfo();
            internalDevInfo.setMountPoint(mSDCardPath);
            internalDevInfo.setMounted(checkSDCardMount14(context, mSDCardPath));
            sdCardInfos.put(SDCARD_INTERNAL, internalDevInfo);
            if (storagePathList.length >= 2) {
                String externalDevPath = storagePathList[1];
                SDCardInfo externalDevInfo = new SDCardInfo();
                externalDevInfo.setMountPoint(storagePathList[1]);
                externalDevInfo.setMounted(checkSDCardMount14(context, externalDevPath));
                sdCardInfos.put(SDCARD_EXTERNAL, externalDevInfo);
            }
        }
        return sdCardInfos;
    }
    
    ArrayList<SDCardInfo> sdCardInfos1;
    
    @SuppressLint("ServiceCast") public  ArrayList<SDCardInfo> getSDCardInfo1() {
    	
    	ArrayList<SDCardInfo> sdCardInfos1 = new ArrayList<SDCardInfo>();
        String[] storagePathList = null;
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        	//UsbManager storageManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
           //StorageManager storageManager = (StorageManager) context.getSystemService(Context.USER_SERVICE);
            Method getVolumePaths = storageManager.getClass().getMethod("getVolumePaths");
            storagePathList = (String[]) getVolumePaths.invoke(storageManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (storagePathList != null && storagePathList.length > 0) {
        	for(int i=0;i<storagePathList.length;i++){
        		 String externalDevPath = storagePathList[i];
               SDCardInfo externalDevInfo = new SDCardInfo();
               externalDevInfo.setMountPoint(storagePathList[i]);
               externalDevInfo.setMounted(checkSDCardMount14(context, externalDevPath));
               sdCardInfos1.add(externalDevInfo);
        	}
//            String mSDCardPath = storagePathList[0];
//            SDCardInfo internalDevInfo = new SDCardInfo();
//            internalDevInfo.setMountPoint(mSDCardPath);
//            internalDevInfo.setMounted(checkSDCardMount14(context, mSDCardPath));
//            sdCardInfos1.add(internalDevInfo);
//            if (storagePathList.length >= 2) {
//                String externalDevPath = storagePathList[1];
//                SDCardInfo externalDevInfo = new SDCardInfo();
//                externalDevInfo.setMountPoint(storagePathList[1]);
//                externalDevInfo.setMounted(checkSDCardMount14(context, externalDevPath));
//                sdCardInfos1.add(externalDevInfo);
//            }
        }
        return sdCardInfos1;
    }


    /**
     * @Description:判断SDCard是否挂载上,返回值为true证明挂载上了，否则未挂载
     * @Date 2013-11-12
     * @param context 上下文
     * @param mountPoint 挂载点
     */
    protected static boolean checkSDCardMount14(Context context, String mountPoint) {
        if (mountPoint == null) {
            return false;
        }
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Method getVolumeState = storageManager.getClass().getMethod("getVolumeState", String.class);
            String state = (String) getVolumeState.invoke(storageManager, mountPoint);
            return Environment.MEDIA_MOUNTED.equals(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public ArrayList<String> getPaths(){
    	ArrayList<String> paths=new ArrayList<String>();
    	ArrayList<SDCardInfo> SDCardInfos=this.getSDCardInfo1();
    	if(SDCardInfos!=null){
    		for(int i=0;i<SDCardInfos.size();i++){
    			String path=SDCardInfos.get(i).getMountPoint();
    			boolean isGuazai=SDCardInfos.get(i).isMounted();
    			if(isGuazai){
    				paths.add(path);
    			}
    		}
    	};
    	return paths;
    }
    
    //将图片进行压缩的方法
    public static Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        newOpts.inJustDecodeBounds = true;//只读边,不读内容  
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
  
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        float hh = 800f;//  
        float ww = 480f;//  
        int be = 1;  
        if (w > h && w > ww) {  
            be = (int) (newOpts.outWidth / ww);  
        } else if (w < h && h > hh) {  
            be = (int) (newOpts.outHeight / hh);  
        }  
        if (be <= 0)  
            be = 1;  
        newOpts.inSampleSize = be;//设置采样率  
          
        newOpts.inPreferredConfig = Config.ARGB_8888;//该模式是默认的,可不设  
        newOpts.inPurgeable = true;// 同时设置才会有效  
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收  
          
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
       // return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩  
                                    //其实是无效的,大家尽管尝试  
        return bitmap;  
    }  
    
    
    //获得APK文件的图标
    public static Drawable getApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
                //Log.e("ApkIconLoader", e.toString());
                e.printStackTrace();
            }
        }
        return null;
    }
    
}







