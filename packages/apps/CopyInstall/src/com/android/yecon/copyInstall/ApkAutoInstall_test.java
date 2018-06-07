package com.android.yecon.copyInstall;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class ApkAutoInstall_test extends Activity
{
    
	private final String INTERNAL_SD = "/mnt/sdcard";
    private final String EXTERNAL_SD = "/mnt/ext_sdcard1";
    
    private String FILE_PATH =  CopyInstallActivity.SOURCE_PATH_APK;
    private String APK_INTALL = "apk_install";
    private String INSTALL_YES = "install_yes";
    private String INSTALL_CANCEL = "install_cancel";
    private String absolutePath;
    private SharedPreferences mSPf;

    private int lowMemoryPercent = 12;
    private float lowmemorySize;
    private long availSize;
    private long TotalSize;
    private long LowMemory;

    private List<File> list;
    private Object[] apklist;
    private String fileName;
	
    private ProgressDialog mDialog = null;

    private AlertDialog.Builder lowMemoryAlertDialog = null;
    private AlertDialog.Builder isAlreadyInstallApkDialog = null;
    private AlertDialog.Builder noApk = null;
    private AlertDialog.Builder notiInstallApk = null;
    private AlertDialog.Builder installApkFinish = null;

    private AlertDialog lowMemoryDialog;
    private AlertDialog isAlreadInstallDialog;
    private AlertDialog mNotiAlert;
    private AlertDialog installApkFinishDialog;

    private int len = 0;
    private final int SCANNING_DIALOG = 0;
    private final int INSTALLING_DIALOG = 1;
    // send handler message
    private final int HANDLE_INSTALL_MESSAGE = 2;
    private final int HANDLE_INSTALL_APK = 102;
    private final int HANDLE_FINISH_INSTALL = 104;
    private final int HANDLE_LOWMEMORY = 101;

    private String result = ""; // display the result after installed APK
    private static int mPid = -1;

    private final String pathString = INTERNAL_SD + "/APK";
    private final static String TAG = "PackInstaller";
    PackageManager pm = null;
    private int installNum = 0;
	private PackageInstallObserver observer = new PackageInstallObserver();
	private boolean isInstalling = false;

    /*
     * 一步： 先判断路径是否存在，内存是否足够 二步： 获取指定目录下apk文件路径 三步：开始安装apk，没安装完一个apk都会更新进度条，且更新安装apk文件名与个数
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mSPf = getSharedPreferences(APK_INTALL, Context.MODE_PRIVATE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        showInstallApkFinishAlertDialog();// 安装完成关闭 初始化
        list = new ArrayList<File>();
        // 获取内存使用量
        // if (mSPf.getInt(INSTALL_YES, -1) != 0) {
        GetSystemMemroy();
        // 首先判断路径，与存储区是否挂载上；再判断 安装区内存大小是否ok
        MemoryOrPath();
        // 获取文件路径,并显示获取到的文件;如果没有apk，那么提示。

        // }else {
        // ShowMemory_PathDialog(R.string.already_install_title,R.string.already_install_message);
        // finish();
        // }
    }

    // 判断路径是否存在，内存是否充足
    private void MemoryOrPath()
    {
        File tmpFile = new File(FILE_PATH);
        // 文件夹不存在，或者没有挂载完成
        if (!tmpFile.exists()
            || !Environment.getStorageState(INTERNAL_SD)
                .equals(Environment.MEDIA_MOUNTED))
        {
            sendMsg(100);
        }
        else if (availSize < LowMemory)
        {
            sendMsg(101);
        }
        else
        {
            getAllFiles(new File(FILE_PATH));
            apklist = list.toArray();
            len = apklist.length;
            if (len == 0)
            {
                sendMsg(99);
            }
            else
            {
                // showDialog(SCANNING_DIALOG);
                showReadyInstall();
            }
        }
    }

    private Handler mHandler_Main = new Handler()
    {
        public void handleMessage(Message msg)
        {

            switch (msg.getData().getInt("status"))
            {
                // 文件夹内无apk文件
                case 99:
                    ShowMemory_PathDialog(R.string.no_apk_title, R.string.no_apk_message);
                    break;
                // 路径不存在
                case 100:
                    ShowMemory_PathDialog(R.string.path_title, R.string.path_message);
                    break;
                // 安装内存不足
                case HANDLE_LOWMEMORY:
                    ShowMemory_PathDialog(R.string.low_memory_title, R.string.low_memory_message);
                    break;
                // 正在安装
                case HANDLE_INSTALL_APK:
                    showDialog(INSTALLING_DIALOG);
                    // shieldKey();
                    Log.v("jzx", "--------show install Dialog");
                    new Thread()
                    {
                        @Override
                        public void run()
                        {
                            Looper.prepare();
                            for (int i = 0; i < len; i++)
                            {
                                GetSystemMemroy();
                                while (availSize < LowMemory)
                                {
                                    i = len + 1;
                                    mDialog.dismiss();
                                    // /mHandler_Main.sendEmptyMessage(HANDLE_LOWMEMORY);
                                    sendMsg(HANDLE_LOWMEMORY);
                                    return;
                                }
                                absolutePath = list.get(i).getAbsolutePath().toString();
                                Log.v("jzx", "------absolutePath: " + absolutePath);
                                sendMsg(103);
								int tryCount = 0;
								isInstalling = true;
                                 boolean success = autoInstall(list.get(i));
								//if fail try 3 sec.
 								while(isInstalling)
								{
								   try
								   {
								      Thread.sleep(1000);
								   }
								   catch(Exception e)
								   {
								     
								   }
								}
                            }
                            Looper.loop();
                        }
                    }.start();
                    break;

                // 更新进度条
                case 103:
                    if (mDialog != null && mDialog.isShowing())
                    {
                        // if(installNum==0){}
                        mDialog.setMessage(getResources().getString(R.string.installing_message) + '\n'
                            + '\n' + list.get(installNum).getAbsolutePath().toString());
                        mDialog.setProgress(installNum);
                    }
                    break;
                // 安装完成
                case 104:
                    mDialog.dismiss();
                    // mSPf.edit().putInt(INSTALL_YES, 0).commit();
                    installApkFinishDialog.show();
                    // mSPf.getInt(INSTALL_YES, -1)
                    mSPf.edit().putInt(INSTALL_YES, 100).commit();
                    mHandler_Main.postDelayed(mApkInstallFinished, 3000);
                    // showInstallApkFinishAlertDialog();
                    Log.v("jzx", "----------quit Activity");
                    break;

                default:

                    break;
            }
        }
    };

    Runnable mApkInstallFinished = new Runnable()
    {
        public void run()
        {
            installApkFinishDialog.dismiss();
            Log.e("jzx", "--installfinish");
            finish();
        }
    };

    private boolean autoInstall(File file)
    {
        fileName = file.getAbsolutePath();
        // PackageManager pm = mContext.getPackageManager();
        ApplicationInfo appInfo = null;
        String packageName = "xx.xx";
        pm = getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(fileName, PackageManager.GET_ACTIVITIES);
        if (info != null)
        {
            appInfo = info.applicationInfo;
            packageName = appInfo.packageName; // 得到安装包名称
        }
        Log.v("hede", "----filename*,packageName**" + fileName + "--,--" + packageName);
        if (appInfo == null)
        {
		    doInstallOneApkError();
            return false;
        }
       return instatllBatch(appInfo, file, packageName);
    }

    public void install(String path, String packageName)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    public boolean instatllBatch(ApplicationInfo mAppInfo, File file, String packageName)
    {
        Log.i(TAG, "path=" + file.getAbsolutePath());
        int installFlags = 0;
        PackageManager pm = getPackageManager();
        try
        {
            if (mAppInfo.packageName == null)
            {
			    doInstallOneApkError();
                return false;
            }
            PackageInfo pi = pm.getPackageInfo(mAppInfo.packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            if (pi != null)
            {
                installFlags |= PackageManager.INSTALL_REPLACE_EXISTING;
            }
        }
        catch (NameNotFoundException e)
        {
        }
        if ((installFlags & PackageManager.INSTALL_REPLACE_EXISTING) != 0)
        {
            Log.w(TAG, "Replacing package:" + mAppInfo.packageName);
        }

        Uri mPackageURI = Uri.fromFile(file);
        String installerPackageName = getIntent().getStringExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME);

        
        pm.installPackage(mPackageURI, observer, installFlags, installerPackageName);
		return true;
    }

    private class PackageInstallObserver extends IPackageInstallObserver.Stub
    {
        public void packageInstalled(String packageName, int returnCode)
        {
            Log.i(TAG, "====INSTALL_COMPLETE----,returnCode,packageName**" + returnCode + "**,**"
                + packageName);
		    isInstalling = false;
            installNum++;
            if (installNum >= len)
            {
                sendMsg(104);
            }
            else
            {
                sendMsg(103);
            }
        }
    }
	
	private void doInstallOneApkError()
	{
	    isInstalling = false;
	    installNum++;
        if (installNum >= len)
        {
            sendMsg(104);
        }
        else
        {
            sendMsg(103);
        }
		
		
	}

    private void GetSystemMemroy()
    {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        int blockSize = stat.getBlockSize();
        int AllSize = stat.getBlockCount();
        int avail = stat.getAvailableBlocks();
        availSize = avail;
        TotalSize = AllSize;
        lowmemorySize = (float) lowMemoryPercent / 100;
        LowMemory = (long) (TotalSize * lowmemorySize);
    }

    // 获取指定目录下所有apk文件
    private void getAllFiles(final File file)
    {
        final File[] files = file.listFiles();
        if (files != null)
        {
            for (File f : files)
            {
                if (f.isDirectory())
                {
                    getAllFiles(f);
                }
                else
                {
                    if (f.getName().endsWith(".apk") || f.getName().endsWith(".APK"))
                        list.add(f);
                }
            }
        }

    }

    protected void ShowMemory_PathDialog(int TitleID, int MessageID)
    {
        new AlertDialog.Builder(this).setTitle(getResources().getString(TitleID))
            .setMessage(getResources().getString(MessageID))
            .setPositiveButton(getResources().getString(R.string.yes_exit),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        finish();
                    }
                })
            .show();
    }

    protected void showInstallApkFinishAlertDialog()
    {
        installApkFinish = new AlertDialog.Builder(ApkAutoInstall_test.this);
        installApkFinish.setTitle(getResources().getString(R.string.finish_install_apk_title))
            .setCancelable(false)
            .setMessage(getResources().getString(R.string.finish_install_apk_message));
        installApkFinish.setPositiveButton(getString(R.string.ok), new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (null != installApkFinishDialog)
                {
                    finish();
                }

            }
        });
        installApkFinishDialog = installApkFinish.create();
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case SCANNING_DIALOG:
                mDialog = new ProgressDialog(this);
                mDialog.setTitle(getResources().getString(R.string.scanning_title));
                mDialog.setMax(len);
                mDialog.setCancelable(false);
                mDialog.setMessage(getResources().getString(R.string.scanning_message));
                mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                break;
            case INSTALLING_DIALOG:
                mDialog = new ProgressDialog(this);
                mDialog.setTitle(getResources().getString(R.string.installing_title));
                mDialog.setMax(len);
                mDialog.setCancelable(false);
                mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mDialog.setMessage(getResources().getString(R.string.installing_message) + '\n' + '\n'
                    + absolutePath);
                break;
        }
        return mDialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog)
    {
        switch (id)
        {
            case SCANNING_DIALOG:
                mDialog.incrementProgressBy(-mDialog.getProgress());
                // mDialog.incrementProgressBy(1);
                // new Thread()
                // {
                // @Override
                // public void run()
                // {
                // Looper.prepare();
                // for (int i = 0; i <= len; i++)
                // {
                //
                // if (mDialog.getProgress() >= len)
                // {
                // try
                // {
                // Thread.sleep(100);
                // }
                // catch (InterruptedException e)
                // {
                // e.printStackTrace();
                // }
                // }
                // try
                // {
                // Thread.sleep(100);
                // }
                // catch (Exception e)
                // {
                // e.printStackTrace();
                // }
                // }
                // mDialog.dismiss();
                //
                // Looper.loop();
                // }
                // }.start();

                showReadyInstall();
                break;
        }
    }

    protected void showReadyInstall()
    {
        notiInstallApk = new AlertDialog.Builder(this);
        notiInstallApk.setMessage(getResources().getString(R.string.alert_message, len))
            .setTitle(getResources().getString(R.string.alert_title))
            .setCancelable(false)
            .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // mDialog.dismiss();
                    sendMsg(HANDLE_INSTALL_APK);
                }
            })
            .setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mSPf.edit().putInt(INSTALL_CANCEL, 0).commit();
                        // mDialog.dismiss();
                        mNotiAlert.dismiss();
                        finish();
                    }
                });
        mNotiAlert = notiInstallApk.create();
        mNotiAlert.show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void sendMsg(int flag)
    {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("status", flag);
        msg.setData(bundle);
        mHandler_Main.sendMessage(msg);
    }
}
