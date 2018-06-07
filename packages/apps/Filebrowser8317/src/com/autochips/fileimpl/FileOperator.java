package com.autochips.fileimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

//import com.autochips.mmpcm.MmpTool;

import android.os.Environment;
import android.util.Log;

public class FileOperator {

    public static final int SORT_NAME = 1;
    public static final int SORT_DATE = 2;
    public static final int SORT_GENRE = 3;
    public static final int SORT_ARTIST = 4;
    public static final int SORT_ALBUM = 5;

    public static final int MMP_FF_VIDEO = 1;
    public static final int MMP_FF_PHOTO = 2;
    public static final int MMP_FF_AUDIO = 3;
    public static final int MMP_FF_TEXT = 4;
    public static final int MMP_FF_ALLTYPE = 5;
    public static final int MMP_FF_APK = 6;

    static private FileOperator fileoperator = null;
    static private AtcFile currentDir = null;
    static private int curSortType = SORT_NAME;

    private int copyedLen;
    private boolean abortCopy = false;
    private List<AtcFile> del = new ArrayList<AtcFile>();
    private OnCopyProgressListener mOnCopyProgressListener;

    private FileOperator() {

    }

    public static FileOperator getInstance() {
        if (fileoperator == null) {
            synchronized (FileOperator.class) {
                if (fileoperator == null) {
                    fileoperator = new FileOperator();
                }
            }
        }
        return fileoperator;
    }

    public static AtcFile getMountPoint() {
        return new AtcFile(Environment.getExternalStorageDirectory());
    }

    public static String getMountPointState(AtcFile mountpoint) {
        return Environment.getExternalStorageState();
    }

    public static String getMountPointName(AtcFile mountpoint) {
        return mountpoint.getName();
    }

    public boolean setCurrentDir(AtcFile dir) {
        if (null == dir) {
            throw new NullPointerException("currentDir is null!");
        }
        currentDir = dir;
        return (currentDir != null);
    }

    public AtcFile getCurrentDir() {
        return currentDir;
    }

    public AtcFile getParent() {

        AtcFile file=null;
        if(null != currentDir){
                file = new AtcFile(currentDir.getParentFile());
            }
            
        
        return file;
    }

    private List<AtcFile> listDirectory(AtcFile dir) {
        List<AtcFile> file = new ArrayList<AtcFile>();

        
        if(dir != null && dir.listFiles()!= null){
            for (File f : dir.listFiles()) {
                if (f.isDirectory() && (f.isHidden() == false)) {
                    file.add(new AtcFile(f));
                }
            }
        }
        
        AtcFile[] fa = new AtcFile[file.size()];

        SortFiles(file.toArray(fa), curSortType);

        return Arrays.asList(fa);
    }

    public List<AtcFile> listDirectory() {
        return listDirectory(currentDir);
    }

    private List<AtcFile> listDir(int file_filter, AtcFile dir) {
        List<AtcFile> fileDir = new ArrayList<AtcFile>();
        fileDir = listDirectory();

        List<AtcFile> file = new ArrayList<AtcFile>();
        if(dir != null && dir.listFiles()!= null){
            for (File f : dir.listFiles()) {
                if (!f.isHidden()) {
	            	AtcFile mf = new AtcFile(f);
	                if (file_filter == MMP_FF_VIDEO) {
	                    if (mf.isVideoFile() && mf.isFile()) {
	                        file.add(mf);
	                    }
	                } else if (file_filter == MMP_FF_PHOTO) {
	                    if (mf.isPhotoFile() && mf.isFile()) {
	                         file.add(mf);
	                    }
	                } else if (file_filter == MMP_FF_AUDIO) {
	                    if (mf.isAudioFile() && mf.isFile()) {
	                         file.add(mf);
	                    }
	                } else if (file_filter == MMP_FF_TEXT) {
	                    if (mf.isTextFile() && mf.isFile()) {
	                         file.add(mf);
	                    }
	                } else if (file_filter == MMP_FF_ALLTYPE) {
	                    if (mf.isFile()) {
	                         file.add(mf);
	                    }
	                }else if (file_filter == MMP_FF_APK) {
	                    if (mf.isApkFile()) {
	                        file.add(mf);
	                   }
	               }else {
	                    file.add(mf);
	                }
                }
            }
        }
        
        AtcFile[] fa = new AtcFile[file.size()];

        SortFiles(file.toArray(fa), curSortType);
        return mergeList(fileDir, Arrays.asList(fa));
    }

    private List<AtcFile> mergeList(List<AtcFile> listDir,
            List<AtcFile> listFile) {
        List<AtcFile> mList = new ArrayList<AtcFile>();
        int i;

        for (i = 0; i < listDir.size(); i++) {
            mList.add(listDir.get(i));
        }

        for (i = 0; i < listFile.size(); i++) {
            mList.add(listFile.get(i));
        }

        return mList;
    }

    public List<AtcFile> listDir(int file_filter) {
        return listDir(file_filter, currentDir);
    }

    public List<AtcFile> listAllFiles() {

        List<AtcFile> fileDir = new ArrayList<AtcFile>();
        fileDir = listDirectory();

        List<AtcFile> file = new ArrayList<AtcFile>();

        if(currentDir != null && currentDir.listFiles()!= null){

            for (File f : currentDir.listFiles()) {
               if (!f.isHidden()) {
            	   AtcFile mf = new AtcFile(f);
                   if (mf.isDirectory() == false) {
                       file.add(mf);
                   }
			    }          	
            }
        }
        
        AtcFile[] fa = new AtcFile[file.size()];

        SortFiles(file.toArray(fa), curSortType);

        return mergeList(fileDir, Arrays.asList(fa));
    }

 
    static public AtcFile[] ShuffleFiles(AtcFile f[]) {
        List<AtcFile> list = new ArrayList<AtcFile>(Arrays.asList(f));
        Collections.shuffle(list);
        return list.toArray(new AtcFile[list.size()]);
    }

    static public void SortFiles(AtcFile[] mf, int mode) {
        switch (mode) {
        case SORT_NAME:
            Arrays.sort(mf, new Comparator<AtcFile>() {
                public int compare(AtcFile object1, AtcFile object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
            break;
        case SORT_DATE:
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

    

    public void setSortType(int sort_type) {
        curSortType = sort_type;
    }

    public int getSortType() {
        return curSortType;
    }

    public int getFileType(AtcFile file) {
        if (file.isTextFile()) {
            return AtcFile.FILE_TYPE_TEXT;
        } else if (file.isPhotoFile()) {
            return AtcFile.FILE_TYPE_PHOTO;
        } else if (file.isVideoFile()) {
            return AtcFile.FILE_TYPE_VIDEO;
        } else if (file.isAudioFile()) {
            return AtcFile.FILE_TYPE_AUDIO;
        } else if (file.isApkFile()) {
            return AtcFile.FILE_TYPE_APK;
        }

        return AtcFile.FILE_TYPE_PHOTO;
    }

    public String getFileExtension(AtcFile file) {
        if (file.isDirectory() == false) {
            String name = file.getName();
            int lastIndexOfDot = name.lastIndexOf('.');
            int fileLength = name.length();

            return name.substring(lastIndexOfDot + 1, fileLength);
        }
        return null;
    }

    public void setCopyAbort(boolean abort) {
        abortCopy = abort;
    }

    public interface OnCopyProgressListener {
        void onSetProgress(long len);
    }

    public void setOnCopyProgressListener(OnCopyProgressListener listener) {
        mOnCopyProgressListener = listener;
    }

    public boolean AddFileToDeleteList(AtcFile f) {
        if (f == null)
            throw new NullPointerException("delete file is null!");
        return del.add(f);
    }

    public void DeleteFiles() {
        Iterator<AtcFile> it = del.iterator();
        while (it.hasNext()) {
            it.next().delete();
            it.remove();
        }
    }

}
