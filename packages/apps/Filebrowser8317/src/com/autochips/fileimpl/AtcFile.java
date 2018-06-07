package com.autochips.fileimpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.Time;

import com.autochips.filebrowser.FilebrowserActivity;

import android.content.Context;
import android.graphics.Bitmap;

public class AtcFile extends File {

    /**
     * 
     */
    private static final long serialVersionUID = 12323L;

//   private static final String photoSuffix[] = { ".png", ".bmp", ".jpg",".gif",
//            ".PNG", ".jpeg" };
    
    private static String photoSuffix[]; 

    private static final String textSuffix[] = { ".txt" };

    private static final String apkSuffix[] = { ".apk" };

    private static final String audioSuffix[] = { ".mp3", ".wma", ".wav", ".mp2", 
            ".mp1", ".pcm", ".aac", ".ac3", ".ape", ".flac", ".ra", ".amr",
            ".awb",".mka",".imy",".mid"};

    private static final String videoSuffix[] = { ".avi", ".divx", ".mp4",
            ".m4a", ".m4v", ".mp4v", ".mpg", ".mpeg", ".vob", ".dat", ".m2v",
            ".rm", ".rmvb", ".mkv", ".flv", ".f4v", ".f4p", ".f4a", "f4b",
            ".ogm", ".ogv", ".ogg", ".oga", ".asf", ".wmv", ".3gp", ".mov", ".ts",
            ".webm", ".ram", ".3g2"};

    public static final int SMALL = 1;
    public static final int MEDIUM = 2;
    public static final int LARGE = 3;

    public static final int FILE_TYPE_UNKNOW = 0;
    public static final int FILE_TYPE_PHOTO = 1;
    public static final int FILE_TYPE_VIDEO = 2;
    public static final int FILE_TYPE_AUDIO = 3;
    public static final int FILE_TYPE_TEXT = 4;
    public static final int FILE_TYPE_APK = 5;
    public static final int FILE_TYPE_DIR = 6;

    private boolean isMarked = false;
    private Bitmap thumbnail;
    private String thumbnailPath;
    private int fileType = FILE_TYPE_UNKNOW;
    
    public boolean getMark() {
        return isMarked;
    }

    public void setMark(boolean mark) {
        isMarked = mark;
    }

    public AtcFile(File f) {
        super(f.getPath());
        if(FilebrowserActivity.m_bLogoSetting)
        {
           photoSuffix= new String[]{".bmp"};
        }
        else
        {
        	photoSuffix= new String[]{ ".png", ".bmp", ".jpg",".gif",
              ".PNG", ".jpeg" };
        }
        parserFileType();
        // super(f.getAbsolutePath());
    }

    public AtcFile(URI uri) {
        super(uri);
        // TODO Auto-generated constructor stub
    }

    public AtcFile(String dirPath, String name) {
        super(dirPath, name);
        // TODO Auto-generated constructor stub
    }

    public AtcFile(String path,int type) {
        super(path);
        fileType = type;
        // TODO Auto-generated constructor stub
    }

    public AtcFile(String path) {
        super(path);
        // TODO Auto-generated constructor stub
    }
    
    public AtcFile(File dir, String name) {
        super(dir, name);
        // TODO Auto-generated constructor stub
    }
    
    private void parserFileType()
    {
        if(this.isDirectory()){
            fileType = FILE_TYPE_DIR;
            return;
        }
        
        for (String s : photoSuffix) {
            if (this.getName().toLowerCase().endsWith(s)) {
                fileType = FILE_TYPE_PHOTO;
                return;
            }
        }
        
        for (String s : videoSuffix) {
            if (this.getName().toLowerCase().endsWith(s)) {
                fileType = FILE_TYPE_VIDEO;
                return;
            }
        }
        
        for (String s : audioSuffix) {
            if (this.getName().toLowerCase().endsWith(s)) {
                fileType = FILE_TYPE_AUDIO;
                return;
            }
        }
        
        for (String s : textSuffix) {
            if (this.getName().toLowerCase().endsWith(s)) {
                fileType = FILE_TYPE_TEXT;
                return;
            }
        }
        
        for (String s : apkSuffix) {
            if (this.getName().toLowerCase().endsWith(s)) {
                fileType = FILE_TYPE_APK;
                return;
            }
        }    
        
        fileType = FILE_TYPE_UNKNOW;
        return;
    }
    
    public int getFileType()
    {
        return fileType;
    }
    
    public boolean isPhotoFile() {
        for (String s : photoSuffix) {
            if (this.getName().toLowerCase().endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean isTextFile() {
        for (String s : textSuffix) {
            if (this.getName().toLowerCase().endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    
    public boolean isApkFile() {
        for (String s : apkSuffix) {
            if (this.getName().toLowerCase().endsWith(s)) {
                return true;
            }
        }
        return false;
    }
    
    
    public boolean isAudioFile() {
        for (String s : audioSuffix) {
            if (this.getName().toLowerCase().endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean isVideoFile() {
        for (String s : videoSuffix) {
            if (this.getName().toLowerCase().endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public String getLastModified() {
        return new Time(lastModified()).toString();
    }

    public static void openFileInfo(Context context) {

    }
    
    public void setThumbnailPath(String path){
        thumbnailPath = path;
        return;
    }
    
    public void setThumbnail(Bitmap file){
        thumbnail = file;
        return;
    }
    
    public String getThumbnailPath(){
        return thumbnailPath;
    }
    
    public Bitmap getThumbnail() {
            
 /*       Log.d("jt", "getThumbnail=" + getPath());
        
        String whereClause = MediaStore.Images.Media.DATA + " = '" + getPath() + "'";

        String uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString();
        Log.d("jt", "uri=" + uri);
        
       Cursor cursor = FilebrowserActivity.mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
               new String[]{MediaStore.Images.Media._ID}, whereClause, null, null);
       
       cursor.moveToNext();
       String imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
       long imageIdLong = Long.parseLong(imageId);

       thumbnail =MediaStore.Images.Thumbnails.getThumbnail(FilebrowserActivity.mContentResolver, imageIdLong,  Images.Thumbnails.MICRO_KIND, null);

        Uri fileURI = Uri.fromFile(this);
        long id = ContentUris.parseId(fileURI);
        
    //    ContentResolver mContentResolver = mContext.getContentResolver();

        if(isVideoFile())
        {
            thumbnail =MediaStore.Video.Thumbnails.getThumbnail(FilebrowserActivity.mContentResolver, id,  Video.Thumbnails.MICRO_KIND, null);
        }
        else if(isPhotoFile())
        {
            thumbnail =MediaStore.Images.Thumbnails.getThumbnail(FilebrowserActivity.mContentResolver, id,  Images.Thumbnails.MICRO_KIND, null);
        }    
  */    
    //    ThumbnailUtils.createVideoThumbnail(getPath(), Video.Thumbnails.MICRO_KIND);
        return thumbnail;
    }

    public String getSize() {
        long lSize = 0;
        if (isDirectory()) {
            String cmd = "du -s " + this.getPath();

            Process pid = null;
            try {
                pid = Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (pid == null) {
                return null;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pid.getInputStream()));
            try {
                pid.waitFor();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                String line = bufferedReader.readLine();
                if (line == null) {
                    lSize = 0;
                } else {
                    line = line.split("\t")[0];
                    if (line == "") {
                        lSize = 0;
                    } else {
                        lSize = Long.parseLong(line);
                        lSize *= 1024;
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            String a = this.getAbsolutePath();
            lSize = length();
        }

        if (lSize / 1024 / 1024 != 0) {
            return getMegaSize(lSize);
        } else if (lSize / 1024 != 0) {
            return getKiloSize(lSize);
        } else
            return lSize + " B";
    }

    public String getMegaSize(long lSize) {
        return lSize / 1024 / 1024 + "MB";
    }

    public String getKiloSize(long lSize) {
        return lSize / 1024 + "KB";
    }

    // String getTest() {
    // StringBuffer buffer = new StringBuffer();
    // if (this.isDirectory()) {
    // buffer.append("Diretory: ");
    // } else {
    // buffer.append("Unknow: ");
    // }
    // return buffer.append(getName()).append(" ").append(getKiloSize())
    // .append(" ").append(getLastModified()).toString();
    // }

}
