package com.android.yecon.copyInstall;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Environment;

public class StorageMounted extends Service
{
	
	//add
	private final String INTERNAL_SD = "/storage/sdcard0";
    private final String EXTERNAL_SD = "/storage/sdcard1";
    
    private Handler mHandler = new Handler();
    private String FILE_PATH = INTERNAL_SD + "/apk";
    private String APK_INTALL = "apk_install";
    private List<File> list = new ArrayList<File>();
    private int len;
    private Object[] apklist;
    private ProgressDialog mDialog = null;
    
    //只有这两个为true才会激活
    private boolean boot = false;
    private boolean mount = false;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
//        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
//        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
//        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
//        intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
//        intentFilter.addDataScheme("file");
//        registerReceiver(broadcastRec, intentFilter);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
//        unregisterReceiver(broadcastRec);
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
        int typeBoot = -1;
     try{
    	 typeBoot = intent.getIntExtra(StartUpBootReceicer.SEND_TYPE_BOOT_KEY, -1); 
    	 
     }catch (Exception e){
          typeBoot=-1;    	 
     }
        
        if(typeBoot == StartUpBootReceicer.SEND_TYPE_BOOT)
        {
            boot = true;
        }
        
        int typeMount=-1;
        try{
			 typeMount= intent.getIntExtra(StartUpBootReceicer.SEND_TYPE_MOUNT_KEY, -1);
			 
		}catch(Exception e){
			typeMount = -1;
			
		}
        if(typeMount == StartUpBootReceicer.SEND_TYPE_MOUNT)
        {
            mount = true;
        }
        
        if(boot && mount)
        {
            doFirstBoot(this);
        }
    }
    
    private void doFirstBoot(Context context)
    {
        getAllFiles(new File(FILE_PATH));
        File mkdir = new File(FILE_PATH);
        if (mDialog != null && mDialog.isShowing())
        {
            mDialog.dismiss();
        }
        if (!mkdir.exists() || len == 0)
        {
            StorageMounted.this.stopSelf();
            return;
        }

        Intent bootActivityIntent = new Intent(context, ApkAutoInstall_test.class);
        bootActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(bootActivityIntent);
        mHandler.postDelayed(new Runnable()
        {

            @Override
            public void run()
            {
                StorageMounted.this.stopSelf();
            }
        }
        , 3000);
    
    }

    protected void ShowMemory_PathDialog(int TitleID, int MessageID)
    {
        mDialog = new ProgressDialog(this);
        mDialog.setTitle(getResources().getString(TitleID));
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(false);
        mDialog.setMessage(getResources().getString(MessageID));
        mDialog.show();
    }

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
        apklist = list.toArray();
        len = apklist.length;
    }
}
