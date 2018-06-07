package com.autochips.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import android.util.Log;

import com.autochips.fileimpl.FileOperator;
import com.autochips.fileimpl.AtcFile;


public class LogicManager {

    private static final String TAG = "LogicManager";
    private static FileOperator Operator;
    private static LogicManager mLogicManager = new LogicManager();

    private LogicManager() {

    }

    public static LogicManager getInstance() {
        Operator = FileOperator.getInstance();
        return mLogicManager;
    }

    public void clear() {
        //mPlaybackService = null;
    }

    public ArrayList<String> getSelectedList(int selected_type, AtcFile sfile, AtcFile dir) {
        
        ArrayList<String> slist = new ArrayList<String>();
        List<AtcFile> file = new ArrayList<AtcFile>();
        List<AtcFile> tmpfile = new ArrayList<AtcFile>();
        int i = 0;
        
        //file.add(sfile);

        if(null == dir || null == dir.listFiles()){
        	return slist;
        	// return null;
        }
        
        for (File f : dir.listFiles()) {
            if (!f.isHidden()) {
                AtcFile mf = new AtcFile(f);
	            if (selected_type == MultiMediaConstant.VIDEO) {
	                if (mf.isVideoFile() && mf.isFile()) {
	                    file.add(mf);
	                }
	            } else if (selected_type == MultiMediaConstant.PHOTO) {
	                if (mf.isPhotoFile() && mf.isFile()) {
	                     file.add(mf);
	                }
	            } else if (selected_type == MultiMediaConstant.AUDIO) {
	                if (mf.isAudioFile() && mf.isFile()) {
	                     file.add(mf);
	                }
	            } else if (selected_type == MultiMediaConstant.TEXT) {
	                if (mf.isTextFile() && mf.isFile()) {
	                     file.add(mf);
	                }
	            }else if (selected_type == MultiMediaConstant.APK) {
	                if (mf.isApkFile() && mf.isFile()) {
	                    file.add(mf);
	               }
           }else {
                //unkown file
            }
         }
        }
        
        AtcFile[] fa = new AtcFile[file.size()];

        AtcSortFiles(file.toArray(fa), 0);
        
        tmpfile = Arrays.asList(fa);
        /*
        for (int j = 0; j <tmpfile.size(); j++){
            //slist.add(i, file.get(i).getAbsolutePath());
            Log.d("xgx", "tmpfile file path is --"+tmpfile.get(j).getAbsolutePath());
        }
        */
        String curFile = sfile.getAbsolutePath();
        slist.add(curFile);
        //slist.add(0, "xxx");
        for (i = 0; i < tmpfile.size(); i++){
            String tmpPath = tmpfile.get(i).getAbsolutePath();
        //    if(!curFile.equals(tmpPath))
        //    {
                slist.add(tmpPath);    
        //    }
        }

        return slist;
    }
    public int getfiletype(AtcFile sfile) {
        // TODO Auto-generated method stub
        int ret;
         if (sfile.isVideoFile()){
             ret = MultiMediaConstant.VIDEO;
         }
         else if (sfile.isPhotoFile()){
             ret = MultiMediaConstant.PHOTO;
         }
         else if (sfile.isAudioFile()){
             ret = MultiMediaConstant.AUDIO;
         }
         else if (sfile.isTextFile()){
             ret = MultiMediaConstant.TEXT;
         }
         else if (sfile.isApkFile()){
             ret = MultiMediaConstant.APK;
         }
         else{
             ret = MultiMediaConstant.UNKOWN;
         }
         return ret;
    }

    // file
    public void fromArrayToArrayList(List<AtcFile> mList, AtcFile[] fa) {
        FileOperator.SortFiles((AtcFile[]) fa, FileOperator.SORT_NAME);
        for (int i = 0; i < fa.length; ++i) {
            mList.add(fa[i]);
        }
    }

    public String getPageSize(int index, int size) {
        String s = "0001/0001";
        Log.d(TAG, "index:" + index + "size:" + size);
        if (size < 10000 && size > 0) {
            size = size + 10000;
            String page = String.valueOf(size);
            page = page.substring(1);
            String indexString = "";
            if (index < 10000 && index > 0) {
                index = index + 10000;
                indexString = String.valueOf(index);
                indexString = indexString.substring(1);
            }
            s = indexString + "/" + page;
        }

        return s;
    }

    public boolean isDir(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            return true;
        }
        return false;
    }

    public boolean isExist(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }
    
    public String getParent() {
          //start modify by mtk94077 for klocwork
        String strRet=null;
        if(Operator!=null){
            AtcFile file=Operator.getParent();
            if(file!=null){
                strRet= file.getAbsolutePath();
            }    
        }
        //end modify by mtk94077 for klocwork

        return strRet;
    }
    
    public void name() {
//        Operator.
    }

    public List<AtcFile> getList(String path, int type) {
        List<AtcFile> mFileList = new ArrayList<AtcFile>();
        Operator.setCurrentDir(new AtcFile(path));
        if (type == MultiMediaConstant.PHOTO) {
            type = FileOperator.MMP_FF_PHOTO;
        } else if (type == MultiMediaConstant.AUDIO) {
            type = FileOperator.MMP_FF_AUDIO;
        } else if (type == MultiMediaConstant.VIDEO) {
            type = FileOperator.MMP_FF_VIDEO;
        } else if (type == MultiMediaConstant.TEXT) {
            type = FileOperator.MMP_FF_TEXT;
        }else if (type == MultiMediaConstant.ALLTYPE) {
            type = FileOperator.MMP_FF_ALLTYPE;
        }else if (type == MultiMediaConstant.APK) {
            type = FileOperator.MMP_FF_APK;
        }
        mFileList = Operator.listDir(type);
        return mFileList;
    }
    
    private void AtcSortFiles(AtcFile[] mf, int mode) {
        switch (mode) {
        case 0/*SORT_NAME*/:
            Arrays.sort(mf, new Comparator<AtcFile>() {
                public int compare(AtcFile object1, AtcFile object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
            break;
        case 1/*SORT_DATE*/:
            Arrays.sort(mf, new Comparator<AtcFile>() {
                public int compare(AtcFile object1, AtcFile object2) {
                    if (object1.lastModified() < object2.lastModified()) {
                        return -1;
                    } else if (object1.lastModified() == object2.lastModified()) {
                        return 0;
                    } else
                        return 1;
                }
            });

            break;

        default:
            break;
        }
    }

}