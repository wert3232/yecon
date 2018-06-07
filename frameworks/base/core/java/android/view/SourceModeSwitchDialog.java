
package android.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.internal.R;
import android.view.WindowManager.LayoutParams;

public class SourceModeSwitchDialog extends Handler {
    private static final String TAG = "SourceModeSwitchDialog";

    private static final int TIMEOUT_DELAY = 3000;

    private static final int MSG_SOURCE_CHANGED = 0;
    private static final int MSG_TIMEOUT = 1;

    private Context mContext;

    private final Dialog mDialog;
    private final View mView;

    private TextView mTVSourceModeSwitch;

    private String[] mSourceType;

    public SourceModeSwitchDialog(Context context) {
        Log.e(TAG, "SourceModeSwitchDialog create");

        mContext = context;

        mSourceType = mContext.getResources().getStringArray(R.array.sourceTypes);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.source_mode_switch_dialog, null);
        mView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                resetTimeout();
                return false;
            }
        });

        mTVSourceModeSwitch = (TextView) mView.findViewById(R.id.source_mode_switch_text);

        mDialog = new Dialog(context, R.style.Theme_Panel_Volume) {
            public boolean onTouchEvent(MotionEvent event) {
                if (isShowing() && event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    forceTimeout();
                    return true;
                }
                return false;
            }
        };
        mDialog.setTitle("Source mode switch");
        mDialog.setContentView(mView);

        Window window = mDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        LayoutParams lp = window.getAttributes();
        lp.token = null;
        // Offset from the top
        lp.y = 100;
        lp.type = LayoutParams.TYPE_VOLUME_OVERLAY;
        lp.width = 300;
        lp.height = LayoutParams.WRAP_CONTENT;
        lp.privateFlags |= LayoutParams.PRIVATE_FLAG_FORCE_SHOW_NAV_BAR;
        window.setAttributes(lp);
        window.addFlags(LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
    }

    @Override
    public void handleMessage(Message msg) {
        Log.e(TAG, "SourceModeSwitchDialog - handleMessage");
        switch (msg.what) {
            case MSG_SOURCE_CHANGED:
                onSourceChanged(msg.arg1);
                break;

            case MSG_TIMEOUT:
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                break;
        }
    }

    public void postSourceChanged(int source) {
        Log.e(TAG, "SourceModeSwitchDialog - postSourceChange - source: " + source);

        removeMessages(MSG_SOURCE_CHANGED);
        obtainMessage(MSG_SOURCE_CHANGED, source, 0).sendToTarget();
    }

    private void onSourceChanged(int source) {
        Log.e(TAG, "SourceModeSwitchDialog - onSourceChanged");

        mTVSourceModeSwitch.setText(mSourceType[source]);

        if (!mDialog.isShowing()) {
            mDialog.setContentView(mView);
            mDialog.show();
        }

        resetTimeout();
    }

    private void resetTimeout() {
        removeMessages(MSG_TIMEOUT);
        sendMessageDelayed(obtainMessage(MSG_TIMEOUT), TIMEOUT_DELAY);
    }

    private void forceTimeout() {
        removeMessages(MSG_TIMEOUT);
        sendMessage(obtainMessage(MSG_TIMEOUT));
    }

}
