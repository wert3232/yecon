package com.autochips.miracast;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DialogHub {
    private static final String TAG = "MiracastActivity";
    private AlertDialog mDialog = null;
    private TextView mProgressText = null;

    public DialogHub(Context ctx) {
        LayoutInflater adbInflater = LayoutInflater.from(ctx);
        View progressLayout = adbInflater.inflate(com.android.internal.R.layout.progress_dialog, null);
        final ProgressBar progressBar = (ProgressBar)progressLayout.findViewById(com.android.internal.R.id.progress);
        progressBar.setIndeterminate(true);
        mProgressText = (TextView) progressLayout.findViewById(com.android.internal.R.id.message);

        mDialog = new AlertDialog.Builder(ctx).setView(progressLayout).create();
        mDialog.setCanceledOnTouchOutside(false);
    }

    private void popupDialogDetail() {
        Log.d(TAG, "[lingling]popupDialogDetail");
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public void dismissDialogDetail() {
        Log.d(TAG, "[lingling]dismissDialogDetail");
        if (null != mDialog) {
            Log.d(TAG, "[lingling]dismissDialogDetail dialogID: " + mDialog + " isshowing: " + mDialog.isShowing());
            mDialog.dismiss();
        }
    }

    public void prepareDialog(String text) {
        prepareDialog(text, null);
    }

    public void prepareDialog(String text, OnKeyListener listener) {
        mProgressText.setText(text);
        mDialog.setOnKeyListener(listener);
        popupDialogDetail();
    }
}
