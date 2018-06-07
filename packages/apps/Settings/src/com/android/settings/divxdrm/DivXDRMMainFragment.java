package com.android.settings.divxdrm;

import android.drm.DrmManagerClient;
import android.drm.DrmInfo;
import android.drm.DrmInfoRequest;
import com.android.settings.divxdrm.DivXDRMSettingFragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import com.android.settings.R;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;

import android.util.Log;

public class DivXDRMMainFragment extends Fragment implements Button.OnClickListener{
    Context mcontext;
    private String TAG = "DivXDRMMainFragment";
    private View mView = null;
    private Button mRegisterButton = null;
    private Button mDeRegisterButton = null;
    private Button mExitButton = null;
    public  DrmManagerClient mDrmManagerClient = null;
    private boolean mNeedReleaseDrmManagerClient = false;

    public DivXDRMMainFragment(){
	super();

	Log.d(TAG, "----[kuan] DivXDRMMainFragment null constructor");
    }

    public DivXDRMMainFragment(DrmManagerClient Client) {
        if (mDrmManagerClient == null)
        {
            Log.i(TAG, " Constructor -- set mDrmManagerClient to be Client\r\n");
            mDrmManagerClient = Client;
            mNeedReleaseDrmManagerClient = false;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, " onCreate\r\n");
        super.onCreate(savedInstanceState);
        mcontext = getActivity();
        if (mDrmManagerClient == null)
        {
            Log.i(TAG, " onCreate -- mDrmManagerClient\r\n");
            mDrmManagerClient = new DrmManagerClient(mcontext);
            mNeedReleaseDrmManagerClient = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.i(TAG, " onCreateView\r\n");
        mView = inflater.inflate(R.layout.drm_divx_main, null);
        mRegisterButton = (Button) mView.findViewById(R.id.bRegister);
        mRegisterButton.setOnClickListener(this);
        mRegisterButton.setEnabled(true);
        mDeRegisterButton = (Button) mView.findViewById(R.id.bDeregister);
        mDeRegisterButton.setOnClickListener(this);
        mDeRegisterButton.setEnabled(true);
        mExitButton = (Button) mView.findViewById(R.id.bExit);
        mExitButton.setOnClickListener(this);
        mExitButton.setEnabled(true);

        if (mDrmManagerClient == null)
        {
            Log.e(TAG, " onCreateView -- no drm Manager Client\r\n");
            mRegisterButton.setEnabled(false);
            mDeRegisterButton.setEnabled(false);
            return mView;
        }

        DrmInfoRequest drmInfoReq = new DrmInfoRequest(DrmInfoRequest.TYPE_RIGHTS_ACQUISITION_INFO, "video/avi");
        DrmInfo mdrmInfo = mDrmManagerClient.acquireDrmInfo(drmInfoReq);
        if ((mdrmInfo != null) && (mdrmInfo.getData() != null)) {
             byte[] drmactivestatus = mdrmInfo.getData();
             String ActiveStatusInfo = new String(drmactivestatus);

            if (ActiveStatusInfo.equals("NEVER_REGISTERED")) {
                Log.i(TAG, "divxdrm device is never registered\r\n");
                mDeRegisterButton.setEnabled(false);
            } else if (ActiveStatusInfo.equals("DEACTIVED")) {
                Log.i(TAG, " divxdrm device is deactived\r\n");
                mDeRegisterButton.setEnabled(true);
            } else if (ActiveStatusInfo.equals("ACTIVED")) {
                Log.i(TAG, " divxdrm device is actived\r\n");
                mDeRegisterButton.setEnabled(true);
            } else {
                Log.i(TAG, " divxdrm device active status is unknown\r\n");
                mDeRegisterButton.setEnabled(false);
            }
        } else {
            mDeRegisterButton.setEnabled(false);
        }

        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if ((mDrmManagerClient != null) && mNeedReleaseDrmManagerClient)
        {
            Log.i(TAG, "onDestroy -- release mDrmManagerClient!!!");
            mDrmManagerClient.release();
        }
        Log.i(TAG, "onDestroy exit!!!");
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bRegister:
                Log.i(TAG, "Click Register Button!\r\n");
                if (mDrmManagerClient != null) {
                    DrmInfoRequest drmInfoReq = new DrmInfoRequest(DrmInfoRequest.TYPE_RIGHTS_ACQUISITION_INFO, "video/avi");
                    DrmInfo mdrmInfo = mDrmManagerClient.acquireDrmInfo(drmInfoReq);
                    if ((mdrmInfo != null) && (mdrmInfo.getData() != null)) {
                        byte[] drmactivestatus = mdrmInfo.getData();
                        String ActiveStatusInfo = new String(drmactivestatus);
                        if (ActiveStatusInfo.equals("ACTIVED")) {
                            Log.i(TAG, " divxdrm device is actived\r\n");
                            DivXDRMSettingFragment ParentFragment = (DivXDRMSettingFragment)getParentFragment();
                            if (ParentFragment == null) {
                                Log.e(TAG, " divxdrm device -- no parent fragment\r\n");
                                getActivity().finish();
                            } else {
                                ParentFragment.ReRegisterDivXDRM();
                            }
                            break;
                        }
                        else
                        {
                            Log.i(TAG, " divxdrm device is deactived\r\n");
                            DivXDRMSettingFragment ParentFragment = (DivXDRMSettingFragment)getParentFragment();
                            if (ParentFragment == null)
                            {
                                Log.e(TAG, " divxdrm device -- no parent fragment\r\n");
                                getActivity().finish();
                            }
                            else
                            {
                                ParentFragment.RegisterDivXDRM();
                            }
                            break;
                        }
                    }
                }
                break;

            case R.id.bDeregister:
                Log.i(TAG, "Click DeRegister Button!\r\n");
                if (mDrmManagerClient != null) {
                    DrmInfoRequest drmInfoReq = new DrmInfoRequest(DrmInfoRequest.TYPE_RIGHTS_ACQUISITION_INFO, "video/avi");
                    DrmInfo mdrmInfo = mDrmManagerClient.acquireDrmInfo(drmInfoReq);
                    if ((mdrmInfo != null) && (mdrmInfo.getData() != null)) {
                        byte[] drmactivestatus = mdrmInfo.getData();
                        String ActiveStatusInfo = new String(drmactivestatus);
                        if (ActiveStatusInfo.equals("ACTIVED")) {
                            Log.i(TAG, " divxdrm device is actived\r\n");
                            DivXDRMSettingFragment ParentFragment = (DivXDRMSettingFragment)getParentFragment();
                            if (ParentFragment == null) {
                                Log.e(TAG, " divxdrm device -- no parent fragment\r\n");
                                getActivity().finish();
                            } else {
                                ParentFragment.ReRegisterDivXDRM();
                            }
                            break;
                        } else if (ActiveStatusInfo.equals("DEACTIVED")) {
                            Log.i(TAG, " divxdrm device is deactived\r\n");
                            DivXDRMSettingFragment ParentFragment = (DivXDRMSettingFragment)getParentFragment();
                            if (ParentFragment == null) {
                                Log.e(TAG, " divxdrm device -- no parent fragment\r\n");
                                getActivity().finish();
                            } else {
                                ParentFragment.DeRegisterDivXDRM();
                            }
                            break;
                        } else if (ActiveStatusInfo.equals("NEVER_REGISTERED")) {
                            Log.i(TAG, " divxdrm device is never registered, do nothing\r\n");
                        } else {
                            Log.e(TAG, " divxdrm device registered status unknown, do nothing\r\n");
                        }
                    }
                }
                break;
             case R.id.bExit:
                Log.i(TAG, "Click Exit Button!\r\n");
                getActivity().finish();
                break;
       }
    }
}


