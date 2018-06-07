package com.yecon.filemanager;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.app.Application;
import android.os.FileObserver;
import android.util.Log;
import net.micode.fileexplorer.FileSortHelper;
import net.micode.fileexplorer.Util;

/**
 * Created by chenchu1986 on 1/2/15.
 */
public class FileManagerApp extends Application {
    public static final String Tag = " file manager application";

    private static FileInfoManager mFileInfoManager = new FileInfoManager();

    public static FileInfoManager getFileInfoManager() {
        return mFileInfoManager;
    }

    private static class FileObserverImpl extends FileObserver {

        final String mLocation;

        public FileObserverImpl(String location) {
            super(location);
            mLocation = location;
            Log.d(Tag,"file observer on"+location);
        }

        public FileObserverImpl(String location, int event) {
            super(location,event);
            mLocation = location;
        }

        @Override
        public void onEvent(int event, String path) {
            int e = event & FileObserver.ALL_EVENTS;
            Log.d(Tag,"event is "+e);
            switch(e){
                case FileObserver.ATTRIB:
                    Log.d(Tag,"attrib is changed"+ Util.makePath(mLocation,path));
                    break;
                case FileObserver.CREATE:
                    Log.d(Tag,"creat with"+ Util.makePath(mLocation,path));
                    //test

                    break;
                case FileObserver.DELETE:
                    Log.d(Tag,"delete on"+ Util.makePath(mLocation,path));
                    File file = new File(mLocation,path);

                    break;
                case FileObserver.ACCESS:
                    Log.d(Tag,"access on"+ Util.makePath(mLocation,path));
                    break;
                case FileObserver.DELETE_SELF:
                    Log.d(Tag,"delete self on"+ Util.makePath(mLocation,path));
                    break;
                case FileObserver.CLOSE_NOWRITE:
                    Log.d(Tag,"close no write on"+ Util.makePath(mLocation,path));
                    break;
                case FileObserver.CLOSE_WRITE:
                    Log.d(Tag,"close write on"+ Util.makePath(mLocation,path));
                    break;
                case FileObserver.MODIFY:
                    Log.d(Tag,"modify on"+ Util.makePath(mLocation,path));
                    break;
                case FileObserver.MOVE_SELF:
                    Log.d(Tag,"move self on"+ Util.makePath(mLocation,path));
                    break;
                case FileObserver.OPEN:
                    Log.d(Tag,"open on"+ Util.makePath(mLocation,path));
                    break;
                case FileObserver.MOVED_FROM:
                    Log.d(Tag,"moved from "+ Util.makePath(mLocation,path));
                    break;
                case FileObserver.MOVED_TO:
                    Log.d(Tag,"moved to"+ Util.makePath(mLocation,path));
                    break;
                default:
                    Log.d(Tag,"default event"+ Util.makePath(mLocation,path));
                    return;
            }
        }
    }

    static class FileInfoManager {
        //TODO: need to set the cap in cash apk crashes?
        private static final FileIconLoader mLoader = new FileIconLoader();

        private Map<String, List<FileInfo>> mFileInfos = new HashMap<String, List<FileInfo>>();
        private Map<String,Integer> mUserInfos = new HashMap<String,Integer>();
        private Map<String,FileObserverImpl> mFileObserverInfos = new HashMap<String,FileObserverImpl>();
        private Stack<FileInfo> mStackInfo = new Stack<FileInfo>();

        private final Object mLock = new Object();

        private List<String> mCopyedInfos;

        private String mCopyedLocation;

        private final static FileSortHelper sortHelper = new FileSortHelper();

        private static final String search_path = "^o^";

        private Map<String,List<String>> mSearchResult;
        
        private boolean mIsCut = false;
        
        
        public boolean isCut() {
        	return mIsCut;
        }

        public boolean unregister(String location) {
            if (mFileInfos.containsKey(location) && mUserInfos.containsKey(location)) {
                int count = minusRegisterCount(location);
                Log.d(Tag,"unregister location ="+location+" count="+count);
                //selectAll(location,false);
                if (count == 0){
                    resetList(mFileInfos.get(location));
                    mFileInfos.remove(location);
                   // mFileObserverInfos.get(location).stopWatching();
                   // mFileObserverInfos.put(location,null);
                    Log.d(Tag,"users count at"+location+"="+getUsers(location));
                    return true;
                }
            }
            return false;
        }

        private void resetList(List<FileInfo> list) {
            if (list == null) {
                throw new IllegalArgumentException(Tag+"list is null");
            }
            for(FileInfo info: list) {
                info.reset();
                mStackInfo.push(info);
            }
            Log.d(Tag,"push a file list = "+mStackInfo.size());
        }

        private void resetFileInfo(String location, FileInfo info) {
            List<FileInfo> infos = mFileInfos.get(location);
            if (infos != null) {
                if (infos.remove(info)) {
                    info.reset();
                    mStackInfo.push(info);
                }
            }
        }

        public void releaseFileInfo(FileInfo info) {
            synchronized (mLock) {
                mStackInfo.push(info.reset());
            }
        }

        //TODO:init the list to the capacity of the location
        public int register(String location) {
            if (location == null) {
                throw new IllegalArgumentException(Tag+"location is null");
            }
            if (mFileInfos.get(location) == null) {
                Log.d(Tag,"register fileinfo location ="+location);
                mFileInfos.put(location,new ArrayList<FileInfo>());
                //if (mFileObserverInfos.get(location)== null){
                   // mFileObserverInfos.put(location, new FileObserverImpl(location));
                   // mFileObserverInfos.get(location).startWatching();
               // }
            }
            if(mUserInfos.get(location) == null ){
                Log.d(Tag,"register userinfo location ="+location);
                mUserInfos.put(location,1);
                Log.d(Tag,"register userinfo count = 1");
                return 1;
            }
            if (getRegisterCount(location) >=0){
                Log.d(Tag,"register userinfo count = = ="+getUsers(location));
                return (int)plusRegisterCount(location);
            }
            throw new IllegalStateException(Tag);
        }

        public List<FileInfo> get(String location) {
            List<FileInfo> infos = null;
            if (location == null || !mFileInfos.containsKey(location)) {
                Log.d(Tag,"get(string location)");
                return infos;
            }
            //keyword final changes immutability???? looks no
            infos = mFileInfos.get(location);
            Log.d(Tag,"get list<fileinfo> at location ="+location);
            return infos;
        }

        public boolean isRegistered(String location) {
            return (getRegisterCount(location)>0) || (mUserInfos.get(location) == null);
        }

        public boolean load(String location) {
            if (!isRegistered(location)) {
                throw new IllegalArgumentException("load illegal argument ");
            }
            List<FileInfo> infos = get(location);
            if (infos == null)
                throw new IllegalStateException("load illegal state");
            File dir = new File(location);
            if (!dir.exists() || !dir.canRead() || !dir.canExecute())
                return false;
            File[] files = dir.listFiles();
            if (files == null) {
            	return false;
            }
            for(File file:files) {
                 updateFileInfo(location,null,file);
            }
            return true;
        }

         FileInfo updateFileInfo(String location,FileInfo info, File file) {
             if (file == null) {
                 throw new IllegalArgumentException("file is null");
             }
             if (info == null) {
                 info = addFileInfo(location);
             }
             if (info != null) {
                 info.filePath = file.getPath();
                 info.fileName = file.getName();
                 info.isDir = file.isDirectory();
                 //info.isSelected = false;
                 info.modifiedTime = file.lastModified();
                 info.size = file.length();
                 info.formattedTime = DateFormat.getDateTimeInstance().format(new Date(info.modifiedTime));
                 info.formattedSize = Util.convertStorage(info.size);
                 //for test only
                 info.fileRes = info.isDir ? R.drawable.folder : mLoader.getIcon(Util.getExtFromFilename(info.fileName));
                 Log.d(Tag, "get new file info is " + file.toString());
             }
             return info;
         }

        private FileInfo getNewFileInfo() {
            FileInfo info;
            if (mStackInfo.size()>0 && mStackInfo.peek() != null) {
                info = mStackInfo.pop();
                Log.d(Tag,"pop a new fileinfo");
            } else {
                info = (FileInfo) FileInfo.getProtype().clone();
                Log.d(Tag,"clone a fileinfo");
            }
            return info;
        }

        public FileInfo addFileInfo(String location) {
            //FileInfo info = new FileInfo();
           FileInfo info = getNewFileInfo();
           Log.d(Tag,"add a fileinfo at location ="+location);
            return getInfos(location).add(info)?info:null;
        }

        public boolean removeFileInfo(String location, FileInfo info) {
            Log.d(Tag,"remove a fileinfo ="+info.toString()+"at location ="+location);
            List<FileInfo> infos = mFileInfos.get(location);
            if (infos == null) {
                return false;
            }
            boolean result = getInfos(location).remove(info);
            if (result) {
                mStackInfo.push(info.reset());
            }
            return result;
        }

        public Integer getRegisterCount(String location) {
            if (location == null || !mUserInfos.containsKey(location)) {
                throw new IllegalArgumentException(Tag+"location is null");
            }
            Integer num ;
               synchronized (mLock) {
                   num = mUserInfos.get(location);
               }
            return num;
        }

        public int getSelectedCount(String location) {
            List<FileInfo> list = getInfos(location);
            if (list == null) {
            	return -1;
            }
            int counter = 0;
            for(FileInfo info:list) {
                if (info.isSelected){
                    counter++;
                }
            }
            return counter;
        }

        public int getCount(String location) {
            return getInfos(location).size();
        }

        private int plusRegisterCount(String location) {
                Integer old = getRegisterCount(location);
                int value = old.intValue()+1;
                Integer n = new Integer(value);
                mUserInfos.put(location,n);
                return value;
        }

        private int minusRegisterCount(String location) {
            Integer old = getRegisterCount(location);
            int value = old.intValue()-1;
            Integer n = new Integer(value);
            mUserInfos.put(location,n);
            return value;
        }

        private List<FileInfo> getInfos(String location) {
            if (location == null || !mFileInfos.containsKey(location)) {
               return null;
            }
            List<FileInfo> infos = mFileInfos.get(location);
            if (infos == null) {
                throw new IllegalStateException(Tag+":key="+location+"value=null");
            }
            return infos;
        }

        private int getUsers(String location) {
            if (location == null || !mUserInfos.containsKey(location)) {
                throw new IllegalArgumentException(Tag+"location is null");
            }
            Integer integer;
            synchronized (mLock) {
                integer = mUserInfos.get(location);
            }
            if (integer == null) {
                throw new IllegalStateException(Tag+"value of "+location+"in mUserInfos is null");
            }
            return integer;
        }

        public boolean selectAll(String location ,boolean isSelected) {
            List<FileInfo> infos = get(location);
            if (infos == null) {
                return false;
            }
            boolean selected = isSelected;
            for(FileInfo info: infos){
                info.isSelected =  selected;
            }
            return true;
        }

        public boolean sort(String location, String sortName) {
           List<FileInfo> infos = get(location);
           return sort(infos,sortName);
        }

        public boolean sort(List<FileInfo> infos,String sortName) {
            Comparator<FileInfo> comparator = sortHelper.getComparator(sortName);
            if (comparator == null){
                return false;
            }
            if (infos == null) {
                return false;
            }
            Collections.sort(infos,comparator);
            return true;
        }

        public boolean copy(String location,boolean isCopy) {
            if (mCopyedInfos == null) {
                mCopyedInfos = new ArrayList<String>();
            }
            if (mCopyedInfos.size() > 0) {
                mCopyedInfos.clear();
            }
            List<FileInfo> infos = get(location);
            if (infos == null || infos.size() == 0) {
                return false;
            }
            for(FileInfo info:infos) {
                if (info.isSelected) {
                    mCopyedInfos.add(info.filePath);
                }
            }
            if (mCopyedInfos.size() ==0) {
                return false;
            }
            mCopyedLocation = location;
            mIsCut = isCopy;
            Log.d(Tag,"location is " + location);
            return true;
        }

        public boolean resetCopyedLocation() {
        	Log.d(Tag,"reset copyed location" + mCopyedLocation);
            return selectAll(mCopyedLocation,false);
        }
        
        public boolean resetCutLocation() {
        	if (mCopyedInfos == null) {
        		return false;
        	}
        	 List<FileInfo> infos = get(mCopyedLocation);
             if (infos == null) {
                 return true;
             } else {
            	 for(String path:mCopyedInfos) {
            		 List<FileInfo> infoss = get(mCopyedLocation);
            			 for(FileInfo info:infoss) {
            				 if (path.equals(info.filePath)) {
            					 boolean result = infoss.remove(info);
            			            if (result) {
            			                mStackInfo.push(info.reset());
            			            }
            					 Log.d(Tag, "on remove :"+path);
            					 break;
            				 }
            			 }
            	 }
            	 }
        	return true;
        }

        public List<String> getCopyedInfos() {
            return mCopyedInfos;
        }

        public String getCopyedLocation() {
            return mCopyedLocation;
        }

        public boolean isInSelected(String location) {
            return getSelectedCount(location)>0?true:false;
        }

        public boolean isFullSelected(String location) {
            return getSelectedCount(location) == getCount(location);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Tag, "app on create");
    }
}
