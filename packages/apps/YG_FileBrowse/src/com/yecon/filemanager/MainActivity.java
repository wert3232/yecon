package com.yecon.filemanager;

import java.io.File;
import java.util.List;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.PopupMenu;
import net.micode.fileexplorer.FileSortHelper;
import net.micode.fileexplorer.IntentBuilder;


public class MainActivity extends Activity implements View.OnClickListener,FileCategoryFragment.ICateFragmentOp
                                                      ,FileListFragment.IFileListFragmentOp
                                                       ,FileOperationFragment.IFileOperationFragmentOp
                                                       ,FileNeatMenuFragment.IMenuFragmentOp{


    public static final String Tag = "file main activity";

    //private  TextView mEmptyView;

    public static final int INPUT_ACTIVITY = 1;
    public static final int SEARCH_ACTIVITY = 2;

    private FileListFragment mCurrFragment = null;

    public  void setCurrFragment(FileListFragment fragment) {
        mCurrFragment = fragment;
    }

    public FileListFragment getCurrFragment() {
        return mCurrFragment;
    }

    private  FileOperationFragment mOpFragment;

    public  FileOperationFragment getOpFragment() {return mOpFragment;}

    public  FileNeatMenuFragment mMenuFragment;

    public  FileNeatMenuFragment getMenuFragment() {return mMenuFragment;}

    public  FileCategoryFragment mCateFragment;

    public  FileCategoryFragment getCateFragment() {return mCateFragment;}

    private String mountPoint;

    public boolean getSelectedStatus() {
        return FileManagerApp.getFileInfoManager().isInSelected(getCurrFragment().getLocation());
    }

    public void isInSelected(boolean isIn) {
        getOpFragment().setInSelected(isIn);
    }
    
    
    public String onGetRootLocation() {
    	// TODO Auto-generated method stub
    	if (mountPoint == null){
    		Log.d(Tag,"point is " + mountPoint);
    	}
    	return mountPoint;
    }

    public void isFolderEmpty(boolean isEmpty) {
            getOpFragment().onFolderEmpty(isEmpty);
    }

    public void onPasteDone(boolean result) {
        getOpFragment().onPasteDone(result);
    }

    public void onCateFragmentOp(String point) {
        if (mountPoint.startsWith(point)) {
        	Log.d(Tag,"to finish");
            finish();
        }
    }

    public String getMountPoint() { return mountPoint;}

    @Override
    public String onGetLocation() {
        return getCurrFragment().getLocation();
    }

    public void onPushToBacktask() {
        FileManagerApp.getFileInfoManager().selectAll(getCurrFragment().getLocation(),false);
        getOpFragment().setState(FSM.OP_RESET);
    }

    public void onSearch(String query) {
        EditText text = getMenuFragment().mSearchEditText;
        if (query != null && !query.isEmpty()) {
            Log.d(Tag,query);
            Intent intent = new Intent(MainActivity.this,FileNeatSearchActivity.class);
            intent.putExtra(Tag, query);
            startActivityForResult(intent, SEARCH_ACTIVITY);
            //getCurrFragment().onSearch(query);
            text.getText().clear();
        }
        ((ViewGroup)text.getParent()).requestFocusFromTouch();
    }

    private void showSearchFailDialog(int res) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getString(res)).setNeutralButton(R.string.activity_input_confirm,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_ACTIVITY) {
            if (resultCode == FileNeatSearchActivity.FIlE_SEARCH_RESULT_ZERO) {
                showSearchFailDialog(R.string.search_fail);
                return;
            }
            if (resultCode == Activity.RESULT_OK){
                String path = data.getStringExtra(FileOperationService.FILE_ACTION_SEARCH);
                Log.d(Tag,path);
                File file = new File(path);
                if (file.exists()) {
                    if (file.isDirectory()) {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.putExtra(FileManagerApp.Tag, path);
                        startActivity(intent);
                    } else {
                        IntentBuilder.viewFile(MainActivity.this, path);
                    }
                }
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        FileListMemory.clear();
        
        setContentView(R.layout.activity_main_ghost);
        //TextView mEmptyView = (TextView) findViewById(R.id.empty_view_content);
        Log.d(Tag,"on create");
        mOpFragment = (FileOperationFragment) getFragmentManager().findFragmentById(R.id.fragment_op);
        mMenuFragment = (FileNeatMenuFragment) getFragmentManager().findFragmentById(R.id.fragment_menu);
        mCateFragment = (FileCategoryFragment) getFragmentManager().findFragmentById(R.id.fragment_category);

        registerForContextMenu(mMenuFragment.mMoreButton);

        if (savedInstanceState == null) {
           mountPoint = getIntent().getStringExtra(FileManagerApp.Tag);
            Log.i(Tag,"mountPoint is "+mountPoint);
           // File root = Environment.getExternalStorageDirectory();
            //String location = root.getPath();
           // Log.i(Tag, location);
            //FileListFragment fragment = FileListFragment.newInstance(mountPoint);
            //getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            Log.d(Tag,"saved state is null");
        } else {
        	Log.d(Tag,"saved state is not null");
        	mountPoint = savedInstanceState.getString(Tag);
        }
  
        commitFragment(mountPoint);
    }


    private void commitFragment(String location) {
        FileListFragment fragment = FileListFragment.newInstance(location);
        
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
        //getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    protected void onDestroy() {
        Log.d(Tag,"on destroy");
        FileListMemory.clear();
        
        //unregisterForContextMenu(mMenuFragment.mMoreButton);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Tag, mountPoint);
    }

    @Override
    protected void onResume() {
        Log.d(Tag,"on resume");
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //TODO:
        Log.d(Tag,"on stop");
    }

    //TODO:fix a bug here in case back to actiivty
    @Override
    protected void onPause() {
        super.onPause();
        
        //this.finish();
        Log.d(Tag,"on pause");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(Tag,"on start");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(Tag,"on restart");
    }

    private  final PopupMenu.OnMenuItemClickListener mSortMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            boolean result = false;
            String location = getCurrFragment().getLocation();
            boolean isInFilterMode = getMenuFragment().isInFilterMode;
            List<FileInfo> infos  = getCurrFragment().getInfos(isInFilterMode);
            switch (id) {
                // on sort
                case R.id.action_sort_kind:
                    result = FileManagerApp.getFileInfoManager().sort(infos, FileSortHelper.SORT_TYPE);
                    break;
                case R.id.action_sort_name:
                    result = FileManagerApp.getFileInfoManager().sort(infos, FileSortHelper.SORT_NAME);
                    break;
                case R.id.action_sort_size:
                    result = FileManagerApp.getFileInfoManager().sort(infos, FileSortHelper.SORT_SIZE);
                    break;
                case R.id.action_sort_time:
                    result = FileManagerApp.getFileInfoManager().sort(infos, FileSortHelper.SORT_DATE);
                    break;
                default:
                    Log.i(Tag,"on sort");
            }
            if (result && location.equals(getCurrFragment().getLocation())) {
                getCurrFragment().refreshView();
            }
            return true;
        }
    };

    private Filter.FilterListener mFilterListener = new Filter.FilterListener() {
        @Override
        public void onFilterComplete(int count) {
            boolean b = getMenuFragment().isInFilterMode;
            getMenuFragment().setInfoEnabled(!b);
            if (!b) {
                hideOpFragment();
                Log.d(Tag, "hide opfragment in filterlistener");
            } else {
                showOpFragment();
                Log.d(Tag,"show opfragment in filterlistener");
            }

        }
    };

    private void hideOpFragment() {
        getFragmentManager().beginTransaction().hide(getOpFragment()).commit();
    }

    private void showOpFragment() {
        getFragmentManager().beginTransaction().show(getOpFragment()).commit();
    }

    private  final PopupMenu.OnMenuItemClickListener mFilterMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            /**
            if (id == R.id.menu_filter_all) {
                getCurrFragment().getAdapter().getFilter().filter(null);
                //test
                showOpFragment();
                return true;
            }
            **/
            //if (id == R.id.menu_filter_folder) {
                //Log.d(Tag, "on filter folder");
                //return true;
           // }
            String title = item.getTitle().toString();
            getCurrFragment().getAdapter().getFilter().filter(title,mFilterListener);
            //test

            return true;
            //TODO: need add these lines??????
            // if (result && location.equals(getCurrFragment().getLocation())) {
            //getCurrFragment().refreshView();
            //}
        }
    };

    private void setUpPopupMenu(View v, int menuRes, PopupMenu.OnMenuItemClickListener listener) {
        if (v!= null && listener != null){
            PopupMenu menu = new PopupMenu(MainActivity.this,v);
            menu.getMenuInflater().inflate(menuRes, menu.getMenu());
            menu.setOnMenuItemClickListener(listener);
            menu.show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String location;
        boolean isChecked;
        switch(id) {
            /*
            case R.id.action_listview:
                getMenuFragment().toggleFileView(R.id.action_listview);
                getCurrFragment().onSwitchToListView();
                break;
            case R.id.action_gridview:
                getMenuFragment().toggleFileView(R.id.action_gridview);
                getCurrFragment().onSwitchToGridView();
                break;
            case R.id.action_sort:
                setUpPopupMenu(v, R.menu.menu_sort, mSortMenuItemClickListener);
                break;
            case R.id.action_filter:
                Log.d(Tag,"on filter");
                setUpPopupMenu(v, R.menu.menu_filter, mFilterMenuItemClickListener);
                break;
            case R.id.action_newfolder:
                getCurrFragment().onNewFolder();
                break;

            case R.id.action_info:
                getCurrFragment().getAdapter().getFilter().filter(null,mFilterListener);
                break;
            */
            case R.id.action_check:
               location = getCurrFragment().getLocation();
               isChecked = getOpFragment().mButtonCheck.isChecked();
                if (FileManagerApp.getFileInfoManager().selectAll(location,isChecked)) {
                     getCurrFragment().refreshView();
                }
                Log.d(Tag,"checked");
                break;
            //TODO:fix the button status after delete
            case R.id.action_delete:
                getCurrFragment().onDeletion();
                Log.d(Tag,"delete");
                break;
            case R.id.action_copy:
                location =getCurrFragment().getLocation();
                isChecked = getOpFragment().mButtonCopy.isChecked();
                if (isChecked && FileManagerApp.getFileInfoManager().copy(location,false)) {
                    Log.d(Tag,"copy");
                } else {
                    Log.d(Tag,"cancel");
                }
                break;
            case R.id.action_cut:
                location =getCurrFragment().getLocation();
                isChecked = getOpFragment().mButtonCut.isChecked();
                if (isChecked && FileManagerApp.getFileInfoManager().copy(location,true)) {
                    Log.d(Tag,"cut");
                } else {
                    Log.d(Tag,"cancel");
                }
                break;
            case R.id.action_paste:
                getCurrFragment().onPaste(FileManagerApp.getFileInfoManager().isCut());
                Log.d(Tag,"paste");
                break;
            case R.id.action_more:
                setUpPopupMenu(v, R.menu.menu_more, mMenuMoreItemClickListener);
                Log.d(Tag,"more");
                break;
            //TODO:
            case R.id.action_sd1:
            case R.id.action_sd2:
            case R.id.action_usb1:
            case R.id.action_usb2:
            case R.id.action_usb3:
            case R.id.action_usb4:
            case R.id.action_usb5:
            case R.id.action_internal:
            case R.id.action_usb:
                Log.d(Tag,"default default");
                location = getCurrFragment().getLocation();
                FileManagerApp.getFileInfoManager().selectAll(location,false);
                //getOpFragment().resetChecked();
                break;
            default:
                Log.d(Tag,"default default");
        }
    }

    private PopupMenu.OnMenuItemClickListener mViewMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.action_listview:
                    getCurrFragment().onSwitchToListView();
                    break;
                case R.id.action_gridview:
                    getCurrFragment().onSwitchToGridView();
                    break;
                default:
            }
            return true;
        }
    };

    private PopupMenu.OnMenuItemClickListener mMenuMoreItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.action_sort:
                    setUpPopupMenu(getMenuFragment().mMoreButton, R.menu.menu_sort, mSortMenuItemClickListener);
                    break;
                case R.id.action_newfolder:
                    getCurrFragment().onNewFolder();
                    break;
                case R.id.action_view:
                    setUpPopupMenu(getMenuFragment().mMoreButton,R.menu.menu_view,mViewMenuItemClickListener);
                    break;
                default:

            }
            return true;
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(Tag,"on key back");
            
            
            String location = getCurrFragment().getLocation();
            FileManagerApp.getFileInfoManager().selectAll(location,false);
            }
        
        return super.onKeyUp(keyCode, event);
    }


}
