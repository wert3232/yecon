
package com.autochips.filebrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.EnvironmentATC;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.autochips.fileimpl.AtcFile;
import com.autochips.util.LogicManager;
import com.autochips.util.MultiMediaConstant;
import com.yecon.logo.YeconLogo;

public class FilebrowserActivity extends Activity implements
        OnItemClickListener, OnItemSelectedListener, OnPreparedListener,
        OnClickListener {

    public static String inner_sd_path = Environment
            .getExternalStorageDirectory().getAbsolutePath();
    public static String[] ext_sd_path = null;
    public static String[] udisk_path = null;
    public static String[] storage_path = null;

    private TextView vBottomLogoSet;
    private TextView vBottomVideo;
    private TextView vBottomMusic;
    private TextView vBottomPic;
    private TextView vBottomAllFile;
    private TextView vBottomUpLevl;
    private TextView vBottomShow;
    private EnvironmentATC environ;
    private TextView vTopFilePath;

    private TextView vTipText;
    private ListView vFileList;
    private GridView vFileGrid;
    private String filepath = MultiMediaConstant.ROOTPATH;
    private List<AtcFile> mFileList = new ArrayList<AtcFile>();
    private List<AtcFile> tmpList = new ArrayList<AtcFile>();
    private String devfilePath = MultiMediaConstant.ROOTPATH;

    private int showSelect = MultiMediaConstant.ALLTYPE;
    private String TAG = "FilebrowserActivity";
    private ListFileAdapter mlistAdapter;
    private GridFileAdapter mGridAdapter;
    private static final int INITLISTVIEW = 0;
    private static final int UPDATELISTVIEW = 1;
    private static final int STARTACTIVITY = 2;
    protected static final int LOADING = 3;
    private static final int GET_FAILED = 4;
    private static final int TIME_OUT = 5;
    private static final int UNKOWN_FAIL = 6;
    private static final int UPDATEGRIDVIEW = 7;
    private static final int UPDATENODEVICE = 8;
    private int tonext = 0;
    private int topre = 0;
    // private int isUsb = 0;
    // by lzy
    public static boolean m_bLogoSetting = false;
    private Dialog AlertDialog;

    private LogicManager mlogicmg;
    // private getListviewThread myUpdateThread;
    // private getListviewThread myInitThread;
    protected int test = 0;
    // private startActivityThread myStartActivityThread;
    private boolean bIsup = false;
    private int selected_type;
    private String tmpfilepath = null;
    private ArrayList<String> tolist = new ArrayList<String>();
    private ProgressDialog dialog;
    // private ExecutorService exe;
    private int initOK = 0;

    private boolean bListShow = true;
    private ContentResolver mContentResolver;

    private class ViewHolder {
        ImageView folderIcon;
        TextView folderName;
    }

    private class FileInfo {
        int index;
        String filePath;
    }

    private Stack<FileInfo> mFileStack = new Stack<FileInfo>();
    /*
     * dispatch the action
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case INITLISTVIEW:
                    doInitListview();
                    break;
                case UPDATELISTVIEW:
                    doUpdateShowView();
                    break;
                case STARTACTIVITY:
                    doStartActivity();
                    break;
                case GET_FAILED:
                    dofailedTip(0);
                    break;
                case TIME_OUT:
                    dofailedTip(1);
                    break;
                case UNKOWN_FAIL:
                    dofailedTip(2);
                    break;
                case UPDATEGRIDVIEW:
                    doUpdateThumbnail();
                    break;
                case UPDATENODEVICE:
                    showTipsDialog();
                    break;
                default:
                    break;
            }
        }
    };

    protected void doInitListview() {
        Log.d(TAG, "jt in doInitListview");

        tonext = 0;
        initOK = 1;
        mFileList.clear();
        if (!tmpList.isEmpty())// modify by jt
        {
            mFileList.addAll(tmpList);
        }
        vFileList.setAdapter(mlistAdapter);
        vFileGrid.setAdapter(mGridAdapter);

        // by lzy modify
        // vFileList.setCacheColorHint(0);
        // GradientDrawable grad = new GradientDrawable(Orientation.LEFT_RIGHT,
        // new int[] {0xff74AC23, Color.TRANSPARENT});
        // vFileList.setSelector(grad);

        // by lzy modify
        // if (m_bLogoSetting)
        // {
        ShapeDrawable mDrawable;
        mDrawable = new ShapeDrawable(new RectShape());
        mDrawable.getPaint().setColor(0xff00deff);
        mDrawable.getPaint().setStrokeWidth(3);
        mDrawable.getPaint().setStyle(Paint.Style.STROKE);
        vFileGrid.setSelector(mDrawable);
        vFileGrid.setSelection(0);
        // }

        showStyle(bListShow);
        dialog.cancel();
        if (!tmpList.isEmpty())// modify by jt
        {
            vTopFilePath.setText(filepath);
        } else {
            Log.d(TAG, "no such device.... ");
            showTipsDialog();
        }
    }

    protected void showStyle(boolean sign) {
        if (sign) {
            vFileGrid.setVisibility(View.GONE);
            vFileList.setVisibility(View.VISIBLE);
        } else {
            vFileList.setVisibility(View.GONE);
            vFileGrid.setVisibility(View.VISIBLE);
        }
    }

    protected void dofailedTip(int getFailed) {
        // TODO Auto-generated method stub
        String[] failtip = { "get filelist failed,please try again...",
                "get filelist time out, please try again...",
                "failed, please try again..." };

        if (tonext == 1) {
            File tmp = new File(filepath);
            if (tmp == null)
                return;
            filepath = tmp.getParentFile().getAbsolutePath();
            tonext = 0;
            Log.d(TAG, "tonext failed,modify file path to --" + filepath);
        } else if (topre == 1) {
            filepath = tmpfilepath;
            tmpfilepath = null;
            topre = 0;
            Log.d(TAG, "topre failed,modify file path to --" + filepath);
        } else {

        }

        Log.d(TAG, "dofailedTip file path is --" + filepath);
        if (initOK == 1) {
            mFileList.clear();
            mlistAdapter.notifyDataSetChanged();
            mGridAdapter.notifyDataSetChanged();
        }
        dialog.cancel();

        vTopFilePath.setText(filepath);

        Toast.makeText(FilebrowserActivity.this, failtip[getFailed],
                Toast.LENGTH_SHORT).show();
    }

    protected void doUpdateShowView() {
        // TODO Auto-generated method stub
        if (initOK == 0) {
            Log.d(TAG, "init sdcard failed... this should see only one....");
            // initOK = 1;
            doInitListview();
        } else {
            Log.d(TAG, "doUpdateShowView....");
            tonext = 0;
            mFileList.clear();
            mFileList.addAll(tmpList);
            mlistAdapter.notifyDataSetChanged();
            mGridAdapter.notifyDataSetChanged();

            showStyle(bListShow);
            if (bListShow) {
                // vFileGrid.removeAllViewsInLayout();
                // mlistAdapter.notifyDataSetChanged();

                if (bIsup) {
                    if (mFileStack.size() > 0) {
                        int selectIndex = mFileStack.pop().index;
                        Log.d(TAG, "JT list up index=" + selectIndex);
                        vFileList.setSelection(selectIndex);
                        bIsup = false;
                    } else {
                        vFileList.setSelection(0);
                    }
                } else {
                    vFileList.setSelection(0);
                }
            } else {
                // vFileList.removeAllViewsInLayout();
                // mGridAdapter.notifyDataSetChanged();
                // new createThumbnailThread().start();
                if (bIsup) {
                    if (mFileStack.size() > 0) {
                        int selectIndex = mFileStack.pop().index;
                        Log.d(TAG, "JT Grid up index=" + selectIndex);
                        vFileGrid.setSelection(selectIndex);
                    } else {
                        vFileGrid.setSelection(0);
                    }
                    bIsup = false;
                } else {
                    vFileGrid.setSelection(0);

                }
            }

            Log.d(TAG, "doUpdateListview....end");
            dialog.cancel();
            vTopFilePath.setText(filepath);

            // add by mtk94077 for add empty folder hint
            if (LogicManager.getInstance().isDir(filepath)) {
                File file = new File(filepath);
                if (file != null) {
                    File[] array = file.listFiles();

                    if (array != null && array.length == 0) {
                        Log.d(TAG, "atcatc vTipText.setVisibility VISIBLE");
                        dialog.cancel();
                        vFileGrid.setVisibility(View.GONE);
                        vFileList.setVisibility(View.GONE);
                        vTipText.setVisibility(View.VISIBLE);
                        vTipText.setText(R.string.empty_folder);
                        return;
                    } else {

                        vTipText.setVisibility(View.GONE); // hide it
                        showStyle(bListShow);
                    }
                }
            }
            // end by mtk94077
        }

    }

    protected void doTip() {
        //dialog.setTitle(R.string.loading_sign);
        //dialog.setMessage(getString(R.string.wait_sign));
        //dialog.show();
    }

    protected void doStartActivity() {
        tonext = 0;

        if (selected_type == MultiMediaConstant.VIDEO) {
            Intent killIntent = new Intent(
                    "com.android.music.musicservicecommand");
            killIntent.putExtra("command", "stopservice");
            sendBroadcast(killIntent);

//            Intent intent = new Intent("com.autochips.video");
//            // Intent intent = new Intent("com.test");
//            Bundle bundle = new Bundle();
//            bundle.putStringArrayList("PLAYLIST", tolist);
//            intent.putExtras(bundle);
            String fileName = tolist.get(0).toString();
            Log.d(TAG, "click video path =" + fileName);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(new File(fileName)),
                    "video/*");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                dialog.cancel();
                return;
            }
            dialog.cancel();

        } else if (selected_type == MultiMediaConstant.PHOTO) {
            // FIXME: consult the action name with photoactivity
            if (tolist.size() > 0) {
                String fileName = tolist.get(0).toString();
                Log.d(TAG, "click photo path =" + fileName);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                // intent.setType("image/*");
                intent.setDataAndType(Uri.fromFile(new File(fileName)),
                        "image/*");
                // Intent intent = new Intent("com.android.camera.ViewImage");
                // Bundle bundle = new Bundle();
                // bundle.putStringArrayList("PLAYLIST", tolist);
                // intent.putExtras(bundle);

                try {
                    // by lzy modify
                    if (m_bLogoSetting)
                        return;
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    dialog.cancel();
                    return;
                }
            }

            dialog.cancel();

        } else if (selected_type == MultiMediaConstant.AUDIO) {

            /*
             * Intent intent = new Intent("com.autochips.music"); Bundle bundle = new Bundle();
             * bundle.putStringArrayList("PLAYLIST", tolist); intent.putExtras(bundle);
             */
            if (tolist.size() > 0) {
                String fileName = tolist.get(0).toString();
                Log.d(TAG, "click AUDIO path =" + fileName);

                Intent killIntent = new Intent(
                        "com.android.music.musicservicecommand");
                killIntent.putExtra("command", "stopservice");
                sendBroadcast(killIntent);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                // intent.setType("image/*");
                intent.setDataAndType(Uri.fromFile(new File(fileName)),
                        "audio/*");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    dialog.cancel();
                    return;
                }
            }
            dialog.cancel();

        } else if (selected_type == MultiMediaConstant.TEXT) {
            // FIXME: consult the action name with photoactivity

            /*
             * Intent intent = new Intent("com.autochips.text"); //Intent intent = new
             * Intent("com.test"); Bundle bundle = new Bundle();
             * bundle.putStringArrayList("PLAYLIST", tolist); intent.putExtras(bundle); try {
             * startActivity(intent); } catch (ActivityNotFoundException e) { // TODO Auto-generated
             * catch block e.printStackTrace(); dialog.cancel(); return; }
             */

            dialog.cancel();

        } else if (selected_type == MultiMediaConstant.APK) {
            if (tolist.size() > 0) {
                String fileName = tolist.get(0).toString();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(fileName)),
                        "application/vnd.android.package-archive");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    dialog.cancel();
                    return;
                }
            }
            dialog.cancel();
        } else {
            dialog.cancel();
        }
    }

    public class TimeLimiter {

        private final ExecutorService executor;

        public TimeLimiter(ExecutorService executor) {
            this.executor = executor;
        }

        public <T> T callWithTimeout(Callable<T> callable,
                long timeoutDuration, TimeUnit timeoutUnit) {
            T obj = null;
            Future<T> future = executor.submit(callable);

            try {
                Log.d(TAG, "WORK thread id is --"
                        + Thread.currentThread().getId());
                obj = (T) future.get(timeoutDuration, timeoutUnit);
            } catch (InterruptedException e) {
                future.cancel(true);
                e.printStackTrace();
                // throw e;
                executor.shutdownNow();
                // shutdownAndAwaitTermination(executor);
            } catch (TimeoutException e) {
                future.cancel(true);
                // throw e;
                e.printStackTrace();
                executor.shutdownNow();
                // shutdownAndAwaitTermination(executor);
            } catch (Exception e) {
                future.cancel(true);
                e.printStackTrace();
                executor.shutdownNow();
                // throw e;
            }
            // executor.shutdownNow();
            return obj;
        }
    }

    protected void doUpdateThumbnail() {
        mGridAdapter.notifyDataSetChanged();
    }

    public void getListView(int acttype) {

        // exe = Executors.newCachedThreadPool();
        /*
         * exe = Executors.newFixedThreadPool(1); TimeLimiter timeLimiter = new TimeLimiter(exe);
         * String response = timeLimiter.callWithTimeout( new Callable<String>() { public String
         * call() throws Exception { Log.d(TAG,
         * "WORK new thread id is "+Thread.currentThread().getId()); //do something... int ret =
         * getFileList(); //ret = ((test++ == 0)?0:1); return ((ret == 0)? "SUCESS":"FAIL"); } },
         * 180, TimeUnit.SECONDS);
         */

        int ret = getFileList();
        String response = (ret == 0) ? "SUCESS" : "FAIL";
        Log.d(TAG, "getListView return --" + response);

        Message msg = new Message();
        if (response == "SUCESS") {
            msg.what = acttype;
        } else if (response == "FAIL") {
            msg.what = GET_FAILED;
        } else if (response == null) {
            msg.what = TIME_OUT;
        } else {
            msg.what = UNKOWN_FAIL;
        }
        mHandler.sendMessage(msg);
    }

    /*
     * the worker thread to get file thumbnail on background
     */

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "jt press back button");
            if (toPrePage()) {
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    /*
     * class createThumbnailThread extends Thread{ public void run(){ //
     * holder.folderIcon.setImageDrawable(getPreviewBmp(filePath)); Log.d(TAG,
     * Thread.currentThread().getId()+"--createThumbnailThread is started..."); for(int
     * i=0;i<mFileList.size();i++) { AtcFile tmpFile = mFileList.get(i); int fileType =
     * tmpFile.getFileType(); Bitmap image = getPreviewBmp(tmpFile.getPath(),fileType); if(image !=
     * null) { Log.d("jt", "getThumbnail image != null"); tmpFile.setThumbnail(image); Message msg =
     * new Message(); msg.what = UPDATEGRIDVIEW; mHandler.sendMessage(msg); } } Log.d(TAG,
     * Thread.currentThread().getId()+"--createThumbnailThread is finished..."); return; } }
     */
    class getThumbnailThread extends Thread {
        /*
         * private String mediaFile; public getThumbnailThread(String forder){ mediaFile = forder; }
         */
        public void run() {
            Log.d(TAG, Thread.currentThread().getId()
                    + "--getThumbnailThread is started...");
            for (int i = 0; i < mFileList.size(); i++) {
                AtcFile tmpFile = mFileList.get(i);
                if (tmpFile.isVideoFile() || tmpFile.isPhotoFile()) {
                    // Log.d("jt", "getThumbnail=" + tmpFile.getPath());
                    String whereID = MediaStore.Images.Media.DATA + " = '"
                            + tmpFile.getPath() + "'";
                    Cursor cursor = mContentResolver.query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            new String[] { MediaStore.Images.Media._ID },
                            whereID, null, null);
                    if (cursor != null) {
                        cursor.moveToNext();
                        String imageId = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Images.Media._ID));

                        long imageIdLong = Long.parseLong(imageId);
                        Log.d("jt", "getThumbnail before");
                        Bitmap image = MediaStore.Images.Thumbnails
                                .getThumbnail(mContentResolver, imageIdLong,
                                        Images.Thumbnails.MICRO_KIND, null);
                        Log.d("jt", "getThumbnail after");
                        if (image != null) {
                            Log.d("jt", "getThumbnail image != null");
                            tmpFile.setThumbnail(image);
                            Message msg = new Message();
                            msg.what = UPDATEGRIDVIEW;
                            mHandler.sendMessage(msg);
                        }

                        /*
                         * tmpFile.setThumbnailPath(imageId); Message msg = new Message(); msg.what
                         * = UPDATEGRIDVIEW; mHandler.sendMessage(msg);
                         */

                        /*
                         * String wherePath = MediaStore.Images.Thumbnails._ID + " = '" + imageId +
                         * "'"; Cursor nailCursor = mContentResolver
                         * .query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI , new
                         * String[]{MediaStore.Images.Thumbnails.DATA}, wherePath, null, null);
                         * if(nailCursor != null ) { nailCursor.moveToNext(); int index =
                         * nailCursor.getColumnIndex (MediaStore.Images.Thumbnails.DATA); String
                         * thumbnailPath = nailCursor.getString(index);
                         * tmpFile.setThumbnailPath(thumbnailPath); Log.d("jt", "getThumbnailpath="
                         * + thumbnailPath); Message msg = new Message(); msg.what = UPDATEGRIDVIEW;
                         * mHandler.sendMessage(msg); }
                         */
                    }

                }
            }

            Log.d(TAG, Thread.currentThread().getId()
                    + "--getThumbnailThread is finished...");
            return;
        }
    }

    /*
     * the worker thread to get filelist on background
     */

    class getListFileThread extends Thread {
        private int acttype;

        public getListFileThread(int msgtype) {
            acttype = msgtype;
        }

        public void run() {
            Log.d(TAG, Thread.currentThread().getId()
                    + "--getListviewThread is started...");
            /*
             * Message msg = new Message(); msg.what = acttype; backgroundThreadProcessing();
             * mHandler.sendMessage(msg);
             */
            // //////////////////////////modify by jt begin

            File dev;
            dev = new File(filepath);

            if (!dev.exists() || (dev.list() == null)) {
                Log.d(TAG, "jt no sdcard device.... ");
                dialog.cancel();

                mFileList.clear();
                /*
                 * if(bListShow) mlistAdapter.notifyDataSetChanged(); else
                 * mGridAdapter.notifyDataSetChanged(); showTipsDialog();
                 */
                Message msg = new Message();
                msg.what = UPDATENODEVICE;
                mHandler.sendMessage(msg);
                Log.d(TAG, Thread.currentThread().getId()
                        + "--getListviewThread is finished...");
                return;
            }
            // //////////////////////////modify by jt begin
            getListView(acttype);
            Log.d(TAG, Thread.currentThread().getId()
                    + "--getListviewThread is finished...");
            return;
        }
    }

    private void scanFileList(int msgtype) {
        File dev;
        dev = new File(filepath);

        if (!dev.exists() || (dev.list() == null)) {
            Log.d(TAG, "jt no sdcard device.... ");
            dialog.cancel();

            // mFileList.clear();
            /*
             * if(bListShow) mlistAdapter.notifyDataSetChanged(); else
             * mGridAdapter.notifyDataSetChanged(); showTipsDialog();
             */
            Message msg = new Message();
            msg.what = UPDATENODEVICE;
            mHandler.sendMessage(msg);
            Log.d(TAG, Thread.currentThread().getId()
                    + "--getListviewThread is finished...");
            return;
        }
        // //////////////////////////modify by jt begin
        getListView(msgtype);
        return;
    }

    /*
     * private void backgroundThreadProcessing(){ getFileList(); }
     */
    private int getFileList() {
        tmpList.clear();

        if (filepath.equals(devfilePath)) {
            int i = 0;
            String[] strPath = null;
            if (null == environ) {
                Log.e(TAG, "getFileList fail for environ is null");
                return 0;
            }
            strPath = environ.getStorageMountedPaths();
            if (null != strPath) {
                for (i = 0; i < strPath.length; i++) {
                    Log.e(TAG, "getFileList path(" + i + "): " + strPath[i]);
                    AtcFile aFile = new AtcFile(strPath[i],
                            AtcFile.FILE_TYPE_DIR);
                    if (!aFile.isHidden()) {
                        tmpList.add(aFile);
                    }
                }
            }
            return 0;
        }

        try {
            tmpList = LogicManager.getInstance().getList(filepath, showSelect);
        } catch (Exception e) {
            Log.e(TAG, "get File list Failed...");
            return 1;
        }

        Log.d(TAG, "tmpList size is ---" + tmpList.size());
        return 0;
    }

    /*
     * the worker thread to statt another activity
     */
    class startActivityThread extends Thread {
        private int acttype;

        public startActivityThread(int msgtype) {
            acttype = msgtype;
        }

        public void run() {
            Message msg = new Message();
            msg.what = acttype;

            getDataSourceToActivity();
            mHandler.sendMessage(msg);
            return;
        }
    }

    public void getDataSourceToActivity() {
        // TODO Auto-generated method stub
        synchronized (FilebrowserActivity.this) {
            AtcFile selectedfile = new AtcFile(filepath);
            mlogicmg = LogicManager.getInstance();
            selected_type = mlogicmg.getfiletype(selectedfile);

            if (selectedfile == null)
                return;
            if (selectedfile.getParentFile() == null)
                return;
            filepath = selectedfile.getParentFile().getAbsolutePath();
            AtcFile selecteddir = new AtcFile(filepath);

            tolist = mlogicmg.getSelectedList(selected_type, selectedfile,
                    selecteddir);

            if (tolist.isEmpty()) {
                return;
            }

            Log.d(TAG, "to activity list size is :" + tolist.size());

            // for(int i = 0; i< tolist.size(); i++){
            // Log.d(TAG,i+"--filepath is --"+tolist.get(i).toString());
            // }

        }
    }

    /** Called when the activity is first created. */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop.....");
        dialog.cancel();
        // vFileList.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onRestart();
        Log.d(TAG, "onResume.....");
        // vFileList.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart.....");
        // vFileList.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        /*
         * if (null != vDialog && vDialog.isShowing()) { vDialog.dismiss(); }
         */
        deinitReceiver();
        ThumbnailCache.clearCache();
        super.onDestroy();
        Log.d(TAG, "onDestroy.....");
    }

    @Override
    // begin add by mtk94077 for ORIENTATION_LANDSCAPE & ORIENTATION_PORTRAIT
    // support
    public void onConfigurationChanged(Configuration newConfig) {

        Log.d(TAG, "onConfigurationChanged.....");

        super.onConfigurationChanged(newConfig);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

        }

    }

    // end by mtk94077

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.filebrowser);

        /*
         * sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" +
         * Environment.getExternalStorageDirectory())));
         */

        // by lzy modify
        Intent intent = getIntent();
        m_bLogoSetting = intent.getBooleanExtra("IsLogoSetting", false);
        if (m_bLogoSetting)
        {
            showSelect = MultiMediaConstant.PHOTO;
            bListShow = false;
        }

        mContentResolver = getContentResolver();
        environ = new EnvironmentATC(this);
        if (null != environ) {
            ext_sd_path = environ.getSdAllPaths();
            udisk_path = environ.getUsbAllPaths();
            storage_path = environ.getStorageAllPaths();
        }
        findView();
        initDialog();
        getData();
        initReceiver();

    }

    private void findView() {
        /*
         * vTopSd = (ImageView) findViewById(R.id.dev_type_sd); vTopUsb = (ImageView)
         * findViewById(R.id.dev_type_usb); vTopExit = (ImageView) findViewById(R.id.dev_type_exit);
         */

        vBottomVideo = (TextView) findViewById(R.id.filter_video);
        vBottomMusic = (TextView) findViewById(R.id.filter_music);
        vBottomPic = (TextView) findViewById(R.id.filter_picture);
        vBottomUpLevl = (TextView) findViewById(R.id.filter_uplevel);
        vBottomAllFile = (TextView) findViewById(R.id.filter_allfile);
        vFileList = (ListView) findViewById(R.id.filebrowser_list);
        vTopFilePath = (TextView) findViewById(R.id.mmp_list_filepath);
        vTipText = (TextView) findViewById(R.id.mmp_tip_text);
        vBottomShow = (TextView) findViewById(R.id.filter_show);
        vFileGrid = (GridView) findViewById(R.id.filebrowser_grid);
        vBottomLogoSet = (TextView) findViewById(R.id.filter_logo_set);
        mlistAdapter = new ListFileAdapter(FilebrowserActivity.this);
        mGridAdapter = new GridFileAdapter(FilebrowserActivity.this);
        // dialog = new ProgressDialog(this);

        // by lzy modify
        if (m_bLogoSetting) {
            vBottomLogoSet.setVisibility(View.VISIBLE);
            // vBottomShow.setImageResource(R.xml.matrix_selected);
            vBottomShow.setSelected(true);
            buildAlertDialog(this);
        }

    }

    private void initDialog() {
        dialog = new ProgressDialog(this);
        // myInitThread = new getListviewThread(INITLISTVIEW);
        // myStartActivityThread = new startActivityThread(STARTACTIVITY);
    }

    private void getData() {
        filepath = devfilePath;
        Log.d(TAG, "getdata default file path is --" + filepath);
        doTip();
        new getListFileThread(INITLISTVIEW).start();
        // getListView(INITLISTVIEW);

        /*
         * vTopSd.setOnClickListener(this); vTopUsb.setOnClickListener(this);
         * //vTopDvd.setOnClickListener(this); vTopExit.setOnClickListener(this);
         */
        vBottomLogoSet.setOnClickListener(this);
        vBottomVideo.setOnClickListener(this);
        vBottomMusic.setOnClickListener(this);
        vBottomPic.setOnClickListener(this);
        vBottomAllFile.setOnClickListener(this);
        vBottomUpLevl.setOnClickListener(this);
        // vTopFilePath.setOnClickListener(this);
        vBottomShow.setOnClickListener(this);

        vFileList.setOnItemClickListener(this);
        vFileList.setOnItemSelectedListener(this);

        vFileGrid.setOnItemClickListener(this);
        vFileGrid.setOnItemSelectedListener(this);

    }

    private class GridFileAdapter extends BaseAdapter {
        private Context mContext;
        int[] rid = { R.drawable.brwoser_unknow_big,
                R.drawable.brwoser_photo_big, R.drawable.brwoser_video_big,
                R.drawable.brwoser_music_big, R.drawable.brwoser_unknow_big,
                R.drawable.brwoser_apk_big, R.drawable.brwoser_folder_big };

        public GridFileAdapter(Context context) {
            Log.d(TAG, "NEW GridFileAdapter---");
            mContext = context;

        }

        public int getCount() {
            return mFileList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        /*
         * flag :isunknow = 0, ispic = 1, isvideo = 2, ismusic = 3, istxt = 4,isapk = 5,isdir =6
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            // final AtcFile tmpFile;
            // if(mFileList.size()> 0 && mFileList.size()> position)
            // {
            final AtcFile tmpFile = mFileList.get(position);
            // }

            int flag = tmpFile.getFileType();

            if (convertView == null) {
                holder = new ViewHolder();

                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.filebr_griditem, null);
                holder.folderIcon = (ImageView) convertView
                        .findViewById(R.id.gridmode_icon);
                holder.folderName = (TextView) convertView
                        .findViewById(R.id.gridmode_foldername);

                convertView.setTag(holder);
            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            if (flag == 1 || flag == 2) {
                Bitmap bitmap = ThumbnailCache.getThumbnail(
                        tmpFile.getAbsolutePath(), flag,
                        new ThumbnailCache.ThumbnailLoadCallback() {

                            @Override
                            public void onLoad(Bitmap bitmap) {
                                holder.folderIcon.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onLoadFail() {

                            }

                        });
                if (bitmap == null) {
                    holder.folderIcon.setImageResource(rid[flag]);
                } else {
                    holder.folderIcon.setImageBitmap(bitmap);
                }
                /*
                 * Bitmap tmpBitmap = tmpFile.getThumbnail(); if(tmpBitmap != null) {
                 * holder.folderIcon.setImageBitmap(tmpBitmap); } else {
                 * holder.folderIcon.setImageResource(rid[flag]); }
                 */
                // holder.folderIcon.setImageDrawable(getPreviewBmp(tmpFile.getPath()));
                /*
                 * if(tmpFile.getThumbnail()!= null) {
                 * holder.folderIcon.setImageBitmap(tmpFile.getThumbnail()); } else {
                 * holder.folderIcon.setImageResource(rid[flag]); }
                 */
                // if(tmpFile.getThumbnail() != null)
                // {
                // holder.folderIcon.setImageBitmap(tmpFile.getThumbnail());
                // }

                // tmpFile.getThumbnail().recycle();
                // new getThumbnailThread(tmpFile).start();
                /*
                 * String tnPath = tmpFile.getThumbnailPath(); if(tnPath != null) {
                 * Log.d("jt","tnPath : " + tnPath); // Bitmap image =
                 * ThumbnailUtils.createVideoThumbnail(tnPath, Video.Thumbnails.MICRO_KIND);
                 * BitmapFactory.Options option = new BitmapFactory.Options(); option.inSampleSize =
                 * 4; Bitmap image = BitmapFactory.decodeFile(tnPath,option); if(image != null) {
                 * holder.folderIcon.setImageBitmap(image); //image.recycle(); image = null; }
                 * //Bitmap smallImage = Bitmap.createScaledBitmap(image, 50, 50,true); /*long
                 * imageIdLong = Long.parseLong(tnPath); Log.d("jt", "getThumbnail before"); Bitmap
                 * image =MediaStore.Images.Thumbnails.getThumbnail(mContentResolver, imageIdLong,
                 * Images.Thumbnails.MICRO_KIND, null); Log.d("jt", "getThumbnail after");
                 * holder.folderIcon.setImageBitmap(image); }
                 */
            } else {
                holder.folderIcon.setImageResource(rid[flag]);
            }

            holder.folderIcon.setVisibility(View.VISIBLE);
            holder.folderName.setText(mFileList.get(position).getName());
            // holder.folderName.setTextSize(getResources().getInteger(R.integer.gridfile_size));
            return convertView;
        }
    }

    private class ListFileAdapter extends BaseAdapter {
        private Context mContext;

        public ListFileAdapter(Context context) {
            Log.d(TAG, "NEW ListFileAdapter---");
            mContext = context;
        }

        public int getCount() {
            return mFileList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            int flag = 0; /*
                           * isunknow = 0, ispic = 1, isvideo = 2, ismusic = 3, istxt = 4,isapk =
                           * 5,isdir =6
                           */
            int[] rid = { R.drawable.brwoser_unknow_small,
                    R.drawable.brwoser_photo_small,
                    R.drawable.brwoser_video_small,
                    R.drawable.brwoser_music_small,
                    R.drawable.brwoser_unknow_small,
                    R.drawable.brwoser_apk_small,
                    R.drawable.brwoser_folder_small };
            AtcFile tmpFile;
            if (mFileList.size() > 0 && mFileList.size() > position) {
                tmpFile = mFileList.get(position);
            } else {
                return convertView;
            }

            flag = tmpFile.getFileType();

            if (convertView == null) {
                Log.d(TAG, "jt in LIST getView === " + tmpFile.getName()
                        + "position =" + position);
                holder = new ViewHolder();

                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.filebr_listitem, null);
                holder.folderIcon = (ImageView) convertView
                        .findViewById(R.id.listmode_icon);
                holder.folderName = (TextView) convertView
                        .findViewById(R.id.listmode_foldername);
                convertView.measure(0, 0);
                // convertView.setMinimumHeight(87);

                convertView.setTag(holder);
            } else {
                Log.d(TAG, "jt in LIST getView  " + tmpFile.getName()
                        + "position " + position);
                holder = (ViewHolder) convertView.getTag();
            }

            holder.folderIcon.setImageResource(rid[flag]);

            holder.folderIcon.setVisibility(View.VISIBLE);
            holder.folderName.setText(mFileList.get(position).getName());
            // holder.folderName.setTextSize(getResources().getInteger(R.integer.listfile_size));
            return convertView;
        }
        /*
         * public void initData(List<AtcFile> mFileList) { // TODO Auto-generated method stub
         * //mListContent = mFileList; }
         */
    }

    @Override
    public void onPrepared(MediaPlayer arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View view, int position,
            long id) {
        // TODO Auto-generated method stub
        Log.d("RECV", "selected position is -- " + position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onItemClick");
        enterAndRight(arg2);
    }

    private void enterAndRight(int arg2) {
        if (filepath.equals("/")) {
            filepath = devfilePath;
        } else {
            Log.d(TAG, "jt enterAndRight--" + arg2);
            filepath = mFileList.get(arg2).getAbsolutePath();
        }
        toNextPage();

        FileInfo file = new FileInfo();
        file.index = arg2;
        file.filePath = filepath;
        mFileStack.push(file);
    }

    private boolean fgIsStartsWithSDcardPath(String url) {
        int i = 0;

        if (null == ext_sd_path) {
            if (null != environ) {
                ext_sd_path = environ.getSdAllPaths();
            }
            if (null == ext_sd_path) {
                return false;
            }
        }
        for (i = 0; i < ext_sd_path.length; i++) {
            if (url.startsWith(ext_sd_path[i])) {
                return true;
            }
        }

        return false;
    }

    private boolean fgIsStartsWithUSBPath(String url) {
        int i = 0;

        if (null == udisk_path) {
            if (null != environ) {
                ext_sd_path = environ.getUsbAllPaths();
            }
            if (null == udisk_path) {
                return false;
            }
        }
        for (i = 0; i < udisk_path.length; i++) {
            if (url.startsWith(udisk_path[i])) {
                return true;
            }
        }

        return false;
    }

    private boolean fgIsEnterStoragePath(String url) {
        int i = 0;

        if (null == storage_path) {
            if (null != environ) {
                storage_path = environ.getStorageAllPaths();
            }
            if (null == storage_path) {
                return false;
            }
        }
        for (i = 0; i < storage_path.length; i++) {
            if (url.equals(storage_path[i])) {
                return true;
            }
        }

        return false;
    }

    private boolean fgIsEnterStorageMountPath(String url) {
        String strState = null;
        if (fgIsEnterStoragePath(url)) {
            strState = environ.getStorageState(url);
            if (strState.equals(Environment.MEDIA_MOUNTED)) {
                return true;
            }
        }
        return false;
    }

    private void toNextPage() {
        // TODO Auto-generated method stub
        if (fgIsEnterStoragePath(filepath)) {
            File dev = new File(filepath);
            if (!dev.exists() || (dev.list() == null)) {
                Log.d(TAG, "no such device.... ");
                showTipsDialog();
                return;
            }
        }

        if (LogicManager.getInstance().isDir(filepath)) {
            // dic
            Log.d(TAG, "to next page filepath is --" + filepath);
            tonext = 1;
            doTip();
            // new getListFileThread(UPDATELISTVIEW).start();
            scanFileList(UPDATELISTVIEW);
            // getListView(UPDATELISTVIEW);
        } else {
            // by lzy set
            if (!m_bLogoSetting)
            {
                doTip();
            }
            new startActivityThread(STARTACTIVITY).start();
            // filepath = LogicManager.getInstance().getParent();
            Log.d(TAG, "jt else-" + filepath);

        }
        return;
    }

    private boolean toPrePage() {
        boolean ret = false;
        Log.e(TAG, "pre page file path--" + filepath);
        if (fgIsEnterStorageMountPath(filepath)) {
            doTip();
            if (bListShow)
                vFileList.setVisibility(View.VISIBLE);
            else
                vFileGrid.setVisibility(View.VISIBLE);
            vTipText.setVisibility(View.GONE);
            topre = 1;
            filepath = devfilePath;
            Log.d(TAG, "after1 page file path--" + filepath);
            // new getListFileThread(UPDATELISTVIEW).start();
            scanFileList(UPDATELISTVIEW);
            ret = true;
        } else if (!filepath.isEmpty() && !filepath.equals(devfilePath)) {
            doTip();
            tmpfilepath = filepath;
            topre = 1;
            try {
                filepath = LogicManager.getInstance().getParent();
            } catch (Exception e) {
                return false;
            }
            Log.d(TAG, "after2 to pre page file path--" + filepath);
            // new getListFileThread(UPDATELISTVIEW).start();
            scanFileList(UPDATELISTVIEW);
            ret = true;
        } else {
            bIsup = false;
            Log.e(TAG, "do nothing !!!");
            // do nothing...
        }
        return ret;
    }

    @Override
    public void onClick(View v) {
        File dev;
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.filter_video:
                showSelect = MultiMediaConstant.VIDEO;
                break;
            case R.id.filter_music:
                showSelect = MultiMediaConstant.AUDIO;
                break;
            case R.id.filter_picture:
                showSelect = MultiMediaConstant.PHOTO;
                break;
            case R.id.filter_allfile:
                showSelect = MultiMediaConstant.ALLTYPE;
                break;
            case R.id.filter_uplevel:
                bIsup = true;
                Log.d(TAG, "TIP TEXT CLICKED!!");
                break;
            case R.id.filter_show:
                if (bListShow) {
                    Drawable drawable = getResources().getDrawable(R.drawable.tab_menu_matrix_bg);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                            .getMinimumHeight());
                    vBottomShow.setCompoundDrawables(null, drawable, null, null);
                    // vBottomShow.setImageResource(R.xml.matrix_selected);
                    bListShow = false;
                } else {
                    Drawable drawable = getResources().getDrawable(R.drawable.tab_menu_list_bg);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                            .getMinimumHeight());
                    vBottomShow.setCompoundDrawables(null, drawable, null, null);
                    // vBottomShow.setImageResource(R.xml.list_selected);
                    bListShow = true;
                }
                break;

            case R.id.filter_logo_set:
                // by lzy

                if (AlertDialog != null)
                {
                    if (!AlertDialog.isShowing())
                    {
                        AlertDialog.show();
                    }
                }

                return;

            default:
                devfilePath = MultiMediaConstant.ROOTPATH;
                filepath = devfilePath;
                showSelect = MultiMediaConstant.ALLTYPE;
                break;
        }

        Log.d(TAG, "cliked view filepath is--- " + filepath);

        if (bIsup) {
            toPrePage();
        } else {
            if (fgIsEnterStoragePath(filepath)) {
                dev = new File(filepath);
                if (!dev.exists() || (dev.list() == null)) {
                    mFileList.clear();
                    mlistAdapter.notifyDataSetChanged();
                    mGridAdapter.notifyDataSetChanged();
                    Log.d(TAG, "no such device.... ");
                    showTipsDialog();
                    return;
                }
            }

            doTip();
            // new getListFileThread(UPDATELISTVIEW).start();
            scanFileList(UPDATELISTVIEW);
        }
    }

    private void buildAlertDialog(Context contxt) {
        LayoutInflater factory = LayoutInflater.from(contxt);
        View alertDialogView = factory.inflate(R.layout.logo_alert_dialog, null);
        AlertDialog = new Dialog(contxt);
        if (AlertDialog == null) {
            return;
        }

        AlertDialog.setContentView(alertDialogView);
        AlertDialog.closeOptionsMenu();

        // AlertDialog.setTitle(R.string.logo_set);

        ((Button) AlertDialog.findViewById(R.id.btn_dialog_ok)).setOnClickListener(
                new OnClickListener() {
                    public void onClick(View arg0) {
                        if (m_bLogoSetting) {
                            if (tolist.size() > 0) {
                                String fileName = tolist.get(0).toString();
                                YeconLogo.ChangeLogo(fileName);
                                Toast.makeText(getApplicationContext(), R.string.alert_set_success,
                                        Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), R.string.alert_set_fail,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        AlertDialog.dismiss();
                    }
                });
        ((Button) AlertDialog.findViewById(R.id.btn_dialog_cancel)).setOnClickListener(
                new OnClickListener() {
                    public void onClick(View arg0) {
                        AlertDialog.dismiss();
                    }
                });
    }

    /*
     * no this device
     */
    private void showTipsDialog() {
        if (bListShow)
            vFileList.setVisibility(View.GONE);
        else
            vFileGrid.setVisibility(View.GONE);
        vTipText.setVisibility(View.VISIBLE);

        if (filepath.startsWith(inner_sd_path)) {
            vTipText.setText(R.string.no_sdcard);
        } else if (fgIsStartsWithUSBPath(filepath)) {
            vTipText.setText(R.string.no_usb);
        } else if (fgIsStartsWithSDcardPath(filepath)) {
            vTipText.setText(R.string.no_sdcard);
        }
        vTopFilePath.setText(filepath);
    }

    /*
     * @Override public void onDismiss(DialogInterface dialog) { // TODO Auto-generated method stub
     * /* reset the listview,default show the sdcard
     */
    /*
     * isUsb = 0; devfilePath = MultiMediaConstant.ROOTPATH; filepath = devfilePath; showSelect =
     * MultiMediaConstant.ALLTYPE; vTopFilePath.setText(filepath); //vTopPath.setText(filePath);
     * getFileList(); mliistAdapter.initData(mFileList); mliistAdapter.notifyDataSetChanged(); }
     */
    /*
     * @Override public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount,
     * int totalItemCount) { // TODO Auto-generated method stub Log.d(TAG,
     * "firstVisibleItem IS --"+firstVisibleItem); Log.d(TAG,
     * "visibleItemCount IS--"+visibleItemCount); Log.d(TAG, "totalItemCount IS--"+totalItemCount);
     * }
     * @Override public void onScrollStateChanged(AbsListView arg0, int arg1) { // TODO
     * Auto-generated method stub }
     */

    DiskLisenterReciver DiskReceiver;

    void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);

        filter.addDataScheme("file");
        DiskReceiver = new DiskLisenterReciver();
        registerReceiver(DiskReceiver, filter);
        Log.d(TAG, "RECEIVE--initReceiver ...");
    }

    void deinitReceiver() {
        unregisterReceiver(DiskReceiver);
        Log.d(TAG, "RECEIVE--deinitReceiver ...");
    }

    class DiskLisenterReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            final String action = intent.getAction();
            String path = intent.getData().toString()
                    .substring("file://".length());
            Log.e(TAG, "RECEIVE--onReceive..." + action + "path:" + path);
            if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                if (!(filepath.equals(devfilePath))) {
                    if (!filepath.startsWith(path)) {
                        return;
                    }
                }

                // current in sdcard view, now we should update
                if (bListShow)
                    vFileList.setVisibility(View.VISIBLE);
                else
                    vFileGrid.setVisibility(View.VISIBLE);
                vTipText.setVisibility(View.GONE);
                if (dialog.isShowing()) {
                    dialog.cancel();
                }

                doTip();

                showSelect = MultiMediaConstant.ALLTYPE;
                // new getListFileThread(UPDATELISTVIEW).start();
                scanFileList(UPDATELISTVIEW);
            } else if (intent.ACTION_MEDIA_REMOVED.equals(action)
                    || intent.ACTION_MEDIA_EJECT.equals(action)
                    || intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
                if (!(filepath.equals(devfilePath))) {
                    if (!filepath.startsWith(path)) {
                        return;
                    }
                }

                if (dialog.isShowing()) {
                    dialog.cancel();
                }

                doTip();
                if (bListShow)
                    vFileList.setVisibility(View.VISIBLE);
                else
                    vFileGrid.setVisibility(View.VISIBLE);
                vTipText.setVisibility(View.GONE);
                topre = 1;
                filepath = devfilePath;
                Log.d(TAG, "after1 page file path--" + filepath);
                scanFileList(UPDATELISTVIEW);
            } else {
                Log.d(TAG, "default--PATH IS --");
            }

        }

    }
}
