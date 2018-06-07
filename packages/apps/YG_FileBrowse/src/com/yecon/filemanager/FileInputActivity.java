package com.yecon.filemanager;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by chenchu on 14-12-25.
 */

public class FileInputActivity extends Activity {
    public static final String Tag = "file input activity";

    private EditText mEditText;
    private File mDir;
    private String mSelectedFileName;
    private String[] mFiles;
    private AlertDialog.Builder mBuilder;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (action.equals(FileListFragment.ACTION_FINISHED)) {
            finish();
        }
    }

    private String mName;

    private DialogInterface.OnCancelListener mOnCancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            Log.d(Tag,"func onCancel");
            performCancel();
        }
    };

    private DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            Log.d(Tag,"func onDismiss");
            mEditText.getText().clear();
            mEditText.requestFocus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_input);
        mEditText =(EditText) findViewById(R.id.activity_input_text);
        init();
    }

    public void onConfirm(View v) {
       if (isNameLegal(mEditText.getText().toString())) {
            performOK();
            return;
        }
        showAlertDialog();
    }

    public void onCancel(View v) {
        performCancel();
    }

    private void performCancel() {
        setResult(Activity.RESULT_CANCELED);
        Log.d(Tag," func performCancel");
        finish();
    }

    private void performOK() {
        Intent intent = getIntent();
        intent.putExtra(FileOperationService.Tag,mName);
        setResult(Activity.RESULT_OK,intent);

        Log.d(Tag,"func perfromOK");
        finish();
    }

    private void showAlertDialog() {
        if (mBuilder == null) {
            mBuilder = new AlertDialog.Builder(FileInputActivity.this);
            mBuilder.setOnCancelListener(mOnCancelListener)
                    .setOnDismissListener(mOnDismissListener)
                    .setMessage(R.string.alert_invaild_name)
                    .setPositiveButton(R.string.alert_rename, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(Tag, "positive rename");
                            dialog.dismiss();

                        }
                    })
                    .setNegativeButton(R.string.alert_back, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(Tag, "negative back");
                            dialog.cancel();
                        }
                    });
        }
        mBuilder.show();
    }

    private boolean isNameLegal(String filename) {
        if (filename == null || TextUtils.isEmpty(filename)) {
            return false;
        }
        if (filename.equals(mSelectedFileName)){
            return false;
        }
        if (mFiles == null && mDir != null) {
            Log.d(Tag,"list mFiles");
            mFiles = mDir.list();
        }
        for(String file :mFiles) {
            if (file.equals(filename)){
                Log.d(Tag,"func isNameLegal return false:file =" + file);
                return false;
            }
            Log.d(Tag,"func isNameLegal:file =" + file);
        }
        Log.d(Tag,"func isNameLegal return true =" + filename);
        mName = filename;
        return true;
    }


    private void init() {
        parseIntent();
        setTitleText();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        Log.d(Tag,"parseIntent() action"+action);
        Uri uri = intent.getData();
        String path = uri.getPath();
        Log.d(Tag,"parseIntent() path"+path);
        File file = new File(path);
        if (action.equals(FileOperationService.FILE_ACTION_NEW_FOLDER)){
            mDir = file;
        }
        if (action.equals(FileOperationService.FILE_ACTION_RENAME)) {
            mDir = file.getParentFile();
            Log.d(Tag,"parseIntent() mDir"+mDir.toString());
            mSelectedFileName = uri.getLastPathSegment();
            Log.d(Tag,"parseIntent() rename"+mSelectedFileName);
        }
    }

    private void setTitleText() {
        TextView v = ((TextView)findViewById(R.id.activity_input_title));
        if (mSelectedFileName == null){
            v.setText(R.string.activity_title_new_folder);
            return;
        }
        v.setText(mSelectedFileName);
    }
}
