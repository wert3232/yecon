
package com.yecon.music.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import com.autochips.storage.EnvironmentATC;

import java.util.ArrayList;

import static com.yecon.music.util.DebugUtil.*;

public class StorageDevicePath {
    // 内置存储共享设备路径
    private final static String SDCARD_ROOT_PATH = Environment
            .getExternalStorageDirectory().getPath();

    private EnvironmentATC mEnvironmentATC;

    private ArrayList<String> mAllStoragePathsList = new ArrayList<String>();
    private ArrayList<String> mAllSdStoragePathsList = new ArrayList<String>();
    private ArrayList<String> mAllUsbStoragePathsList = new ArrayList<String>();

    private ArrayList<String> mAllMountedStoragePathsList = new ArrayList<String>();
    private ArrayList<String> mAllMountedSdStoragePathsList = new ArrayList<String>();
    private ArrayList<String> mAllMountedUsbStoragePathsList = new ArrayList<String>();

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public StorageDevicePath(Context context) {
        mEnvironmentATC = new EnvironmentATC(context);

        try {
            String[] allStoragePaths = mEnvironmentATC.getStorageAllPaths();
            for (int i = 0; i < allStoragePaths.length; i++) {
                String path = allStoragePaths[i];
                printLog("allStoragePaths: " + path + " - mount state: "
                        + cheackStoragePath(path));
                mAllStoragePathsList.add(path);
            }

            String[] allSdStoragePaths = mEnvironmentATC.getSdAllPaths();
            for (int i = 0; i < allSdStoragePaths.length; i++) {
                mAllSdStoragePathsList.add(allSdStoragePaths[i]);
            }

            String[] allUsbStoragePaths = mEnvironmentATC.getUsbAllPaths();
            for (int i = 0; i < allUsbStoragePaths.length; i++) {
                mAllUsbStoragePathsList.add(allUsbStoragePaths[i]);
            }

            String[] allMonutedStoragePaths = mEnvironmentATC.getStorageMountedPaths();
            for (int i = 0; i < allMonutedStoragePaths.length; i++) {
                String path = allMonutedStoragePaths[i];
                printLog("allMonutedStoragePaths: " + path);
                mAllMountedStoragePathsList.add(path);
            }

            String[] allMountedSdStoragePathsList = mEnvironmentATC.getSdMountedPaths();
            for (int i = 0; i < allMountedSdStoragePathsList.length; i++) {
                mAllMountedSdStoragePathsList.add(allMountedSdStoragePathsList[i]);
            }

            String[] allMountedUsbStoragePathsList = mEnvironmentATC.getUsbMountedPaths();
            for (int i = 0; i < allMountedUsbStoragePathsList.length; i++) {
                mAllMountedUsbStoragePathsList.add(allMountedUsbStoragePathsList[i]);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public boolean cheackStoragePath(String path) {
        if (path != null && !path.equals("")) {
            try {
                String status = mEnvironmentATC.getStorageState(path);
                if (Environment.MEDIA_MOUNTED.equals(status)) {
                    return true;
                } else {
                    return false;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 获取外置SD卡路径
     * 
     * @return
     */
    public ArrayList<String> getSdStoragePath() {
        for (int i = 0; i < mAllStoragePathsList.size(); i++) {
            if (!mAllStoragePathsList.get(i).equals(SDCARD_ROOT_PATH)) {
                if (mAllStoragePathsList.get(i).contains("sd")) {
                    String path = mAllStoragePathsList.get(i);
                    printLog("getSdStoragePath: " + path + " - mount state: "
                            + cheackStoragePath(path), true);
                    mAllSdStoragePathsList.add(path);
                }
            }
        }
        return mAllSdStoragePathsList;
    }

    /**
     * 获取内置SD卡路径
     * 
     * @return
     */
    public String getInterStoragePath() {
        printLog("getInterStoragePath: " + SDCARD_ROOT_PATH + " - mount state: "
                + cheackStoragePath(SDCARD_ROOT_PATH), true);
        return SDCARD_ROOT_PATH;
    }

    /**
     * 获取USB路径
     * 
     * @return
     */
    public ArrayList<String> getUsbStoragePath() {
        for (int i = 0; i < mAllStoragePathsList.size(); i++) {
            if (!mAllStoragePathsList.get(i).equals(SDCARD_ROOT_PATH)) {
                if (mAllStoragePathsList.get(i).contains("usb")
                        || mAllStoragePathsList.get(i).contains("udisk")) {
                    String path = mAllStoragePathsList.get(i);
                    printLog("getUsbStoragePath(): " + path + " - mount state: "
                            + cheackStoragePath(path), true);
                    mAllUsbStoragePathsList.add(path);
                }
            }
        }
        return mAllUsbStoragePathsList;
    }

    /**
     * 获取所有存储设备路径
     * 
     * @return
     */
    public ArrayList<String> getAllStoragePath() {
        return mAllStoragePathsList;
    }

    public ArrayList<String> getAllStoragePathsList() {
        return mAllStoragePathsList;
    }

    public ArrayList<String> getmllSdStoragePathsList() {
        return mAllSdStoragePathsList;
    }

    public ArrayList<String> getAllUsbStoragePathsList() {
        return mAllUsbStoragePathsList;
    }

    public ArrayList<String> getAllMountedStoragePathsList() {
        return mAllMountedStoragePathsList;
    }

    public ArrayList<String> getAllMountedSdStoragePathsList() {
        return mAllMountedSdStoragePathsList;
    }

    public ArrayList<String> getAllMountedUsbStoragePathsList() {
        return mAllMountedUsbStoragePathsList;
    }

}
