package com.yecon.filemanager;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ToggleButton;

/**
 * Created by chenchu on 15-1-12.
 */
public class FileOperationFragment extends Fragment implements View.OnClickListener, IFileMiscs.OnViewClickListener {

    public static final String Tag = "file operation fragment";

    private static int count = 0;

    private static FSM mFSM = new FSM();

    public ToggleButton mButtonCheck;
    public Button mButtonDelete;
    public ToggleButton mButtonCopy;
    public Button mButtonPaste;
    public ToggleButton mButtonCut;

    public interface IFileOperationFragmentOp {
        public boolean getSelectedStatus();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        count++;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_op,container,false);
        mButtonCheck = (ToggleButton)ret.findViewById(R.id.action_check);
        mButtonCheck.setOnClickListener(this);
        mButtonDelete = (Button)ret.findViewById(R.id.action_delete);
        mButtonDelete.setOnClickListener(this);
        mButtonCopy = (ToggleButton)ret.findViewById(R.id.action_copy);
        mButtonCopy.setOnClickListener(this);
        mButtonPaste = (Button)ret.findViewById(R.id.action_paste);
        mButtonPaste.setOnClickListener(this);
        
        mButtonCut = (ToggleButton)ret.findViewById(R.id.action_cut);
        mButtonCut.setOnClickListener(this);
        

        return ret;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if((--count) == 0){
          setFastForward(false);
          setState(FSM.OP_UNSELECTED);
        }
    }

    public static void setFastForward(boolean b) {
        FSM.setFastForward(b);
    }

    public static boolean getFastForward() {
        return FSM.getFastForward();
    }

    @Override
    public void onResume() {
        setState(FSM.OP_QUERY);
        super.onResume();
    }

    public void setState(int op) {
        if (mFSM != null) {
            int result = mFSM.request(op);
            onStateChanged(result);
        }
    }

    private void onStateChanged(int result) {
        switch (result) {
            case FSM.STATE_NONE:
                onNone();
                break;
            case FSM.STATE_READY:
                onReady();
                break;
            case FSM.STATE_COPY:
                onCopy();
                break;
            case FSM.STATE_CUT:
            	onCut();
            	break;
            default:
        }
    }
    
    private void onCut() {
    	mButtonDelete.setEnabled(false);
        mButtonPaste.setEnabled(true);
        if (!mButtonCut.isChecked()) {
            mButtonCut.toggle();
        }
        mButtonCut.setEnabled(true);
        
        mButtonCopy.setEnabled(false);
    }

    private void onNone() {
        mButtonDelete.setEnabled(false);
        mButtonPaste.setEnabled(false);
        if (mButtonCopy.isChecked()) {
            mButtonCopy.toggle();
        }
        mButtonCopy.setEnabled(false);
        
        if (mButtonCut.isChecked()) {
            mButtonCut.toggle();
        }
        mButtonCut.setEnabled(false);
        
        
    }

    private void onReady() {
        mButtonDelete.setEnabled(true);
        mButtonPaste.setEnabled(false);
        if (mButtonCopy.isChecked()){
            mButtonCopy.toggle();
        }
        mButtonCopy.setEnabled(true);
        
        if (mButtonCut.isChecked()){
            mButtonCut.toggle();
        }
        mButtonCut.setEnabled(true);
    }

    private void onCopy() {
        mButtonDelete.setEnabled(false);
        mButtonPaste.setEnabled(true);
        if (!mButtonCopy.isChecked()) {
            mButtonCopy.toggle();
        }
        mButtonCopy.setEnabled(true);
        
        mButtonCut.setEnabled(false);
    }

    public void setInSelected(boolean isIn) {
        onSelected();
        if (isIn) {
            onFullySelected();
        }
    }

    public void onFullySelected() {
        if (!mButtonCheck.isEnabled()){
            mButtonCheck.setEnabled(true);
        }
        if (!mButtonCheck.isChecked()){
            mButtonCheck.toggle();
        }
    }

    public void onSelected() {
        if (!mButtonCheck.isEnabled()){
            mButtonCheck.setEnabled(true);
        }
        if (mButtonCheck.isChecked()){
            mButtonCheck.toggle();
        }
    }

    public void onFolderEmpty(boolean isEmpty) {
        if (isEmpty) {
            if (mButtonCheck.isChecked()){
                mButtonCheck.toggle();
            }
            if (mButtonCheck.isEnabled()){
                mButtonCheck.setEnabled(false);
            }
            return;
        }
        onSelected();
    }

    @Override
    public boolean onViewClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.action_check:
                if (mButtonCheck.isChecked()) {
                    Log.d(Tag,"select all");
                    setState(FSM.OP_SELECTED);
                } else {
                    setState(FSM.OP_UNSELECTED);
                }
                break;
            case R.id.action_copy:
                if (mButtonCopy.isChecked()) {
                    setState(FSM.OP_COPY);
                } else {
                    if (getActivity() instanceof IFileOperationFragmentOp) {
                        IFileOperationFragmentOp op = (IFileOperationFragmentOp)getActivity();
                        boolean b = op.getSelectedStatus();
                        Log.d(Tag,b?"selected":"unselected");
                        if (!b) {
                            Log.d(Tag,"setfastforward");
                            setFastForward(true);
                            FileManagerApp.getFileInfoManager().resetCopyedLocation();
                        }
                    }
                    setState(FSM.OP_CANCEL);
                }
                break;
            case R.id.action_cut:
                if (mButtonCut.isChecked()) {
                    setState(FSM.OP_CUT);
                } else {
                    if (getActivity() instanceof IFileOperationFragmentOp) {
                        IFileOperationFragmentOp op = (IFileOperationFragmentOp)getActivity();
                        boolean b = op.getSelectedStatus();
                        Log.d(Tag,b?"selected":"unselected");
                        if (!b) {
                            Log.d(Tag,"setfastforward");
                            setFastForward(true);
                            FileManagerApp.getFileInfoManager().resetCopyedLocation();
                        }
                    }
                    setState(FSM.OP_CANCEL);
                }
                break;
            case R.id.action_delete:
                Log.d(Tag,"on delete");
                showDeleteDialog();
                return true;
            case R.id.action_paste:
                Log.d(Tag,"on paste");
                break;
            default:

        }
        return false;
    }

    public void onPasteDone(boolean result) {
        if (result) {
            setState(FSM.OP_PASTE);
        }
        Log.d(Tag,"onpastedone(boolean)");
    }
    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
       

//        builder.setMessage(R.string.dialog_delete_warning).setNegativeButton(R.string.activity_input_cancel,new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Log.d(Tag,"on dialog cancel clicked");
//            }
//        }).setPositiveButton(R.string.activity_input_confirm,new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                setState(FSM.OP_DELETE);
//                if (getActivity() instanceof View.OnClickListener) {
//                    ((View.OnClickListener)getActivity()).onClick(mButtonDelete);
//                }
//            }
//        }).setCancelable(false);
        //by lzy
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.show();
        Window window = mAlertDialog.getWindow(); 
        LayoutInflater inflater = LayoutInflater.from(getActivity());  
        View view = inflater.inflate(R.layout.alertdialog, null);  
        window.setContentView(view);
        window.setLayout(408, 241);
        
		Button ok = (Button)window.findViewById(R.id.btn_tips_dialog_OK);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
              setState(FSM.OP_DELETE);
              if (getActivity() instanceof View.OnClickListener) {
                  ((View.OnClickListener)getActivity()).onClick(mButtonDelete);
               }
              mAlertDialog.dismiss();
		   }
		});
		
		Button cancel = (Button)window.findViewById(R.id.btn_tips_dialog_Cancel);
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mAlertDialog.dismiss();
			}
		});
        
       
    }

    @Override
    public void onClick(View v) {
        if (onViewClick(v)) {
            return;
        }
        if (getActivity() instanceof View.OnClickListener) {
            ((View.OnClickListener)getActivity()).onClick(v);
        }
    }
}
