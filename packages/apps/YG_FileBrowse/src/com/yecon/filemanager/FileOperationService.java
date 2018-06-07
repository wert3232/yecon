package com.yecon.filemanager;


import java.io.File;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by chenchu on 14-12-17.
 */
public class FileOperationService extends IntentService {
    public static final String Tag = "file operation service";

    public static final String RESULT_OK = "com.hcn.filemanager.OK";
    public static final String RESULT_FAIL = "com.hcn.filemanager.FAIL";

    public static final String FILE_ACTION_MOVE ="com.yecon.filemanager.FILE_MOVE";
    public static final String FILE_ACTION_COPY ="com.yecon.filemanager.FILE_COPY";
    public static final String FILE_ACTION_NEW_FOLDER = "com.yecon.filemanager.FILE_NEW_FOLDER";
    public static final String FILE_ACTION_DELETE ="com.yecon.filemanager.file.FILE_DELETE";
    public static final String FILE_ACTION_RENAME = "com.yecon.filemanager.file.FILE_RENAME";
    public static final String FILE_ACTION_SEARCH= "com.yecon.filemanager.file.FILE_SEARCH";

    private Handler mFileServiceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {

            }
        }
    };

    public FileOperationService() {
        super("file operation service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        Uri uri = intent.getData();
        String extra = intent.getStringExtra(FileOperationService.Tag);
        File old = new File(uri.getPath());


        if (action.equals(FILE_ACTION_NEW_FOLDER)) {
            File file = new File(old, extra);
            broadcast(intent, file.mkdir(),file);
            Log.i(Tag, FILE_ACTION_NEW_FOLDER);
            return;
        }

        if (action.equals(FILE_ACTION_COPY)) {
            Log.i(Tag, FILE_ACTION_COPY);
            return;
        }

        if (action.equals(FILE_ACTION_MOVE)) {
            Log.i(Tag, FILE_ACTION_MOVE);
            return;
        }
        
        if (action.equals(FILE_ACTION_DELETE)) {
            Log.i(Tag, FILE_ACTION_DELETE);
            return;
        }

        if (action.equals(FILE_ACTION_RENAME)) {
            File file = new File(old.getParent(), extra);
            broadcast(intent, old.renameTo(file),file);
            Log.i(Tag, FILE_ACTION_RENAME);
            return;
        }
    }

    private void broadcast(Intent intent, boolean result,File file) {
        String cat = result?RESULT_OK:RESULT_FAIL;
        intent.addCategory(cat);
        if (result) {

            return;
        }
        sendBroadcast(intent);
    }



}
