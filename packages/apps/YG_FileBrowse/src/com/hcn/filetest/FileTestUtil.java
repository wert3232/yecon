package com.hcn.filetest;

/**
 * Created by chenchu on 15-2-27.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

import android.util.Log;

public class FileTestUtil {

    private int counter  = 0;
    public  static final String TAG = "file util";
    private List<String> mCheckSums = null;
    private String newFile = null;
    private String head = null;

    private boolean isHeadChecked = false;
    String oldCheck = null;

    public static String eLog = null;

    public List<String> getChecksumList() {
        return mCheckSums;
    }

    private boolean isCRC = true;

    public void setCRC(boolean b) {
        isCRC = b;
    }

    public  boolean copyOnce(String src,String dest,boolean isRecorded) {
        if (!isHeadChecked) {
            oldCheck = getCheckSum(src,isRecorded);
            if (oldCheck == null) {
                Log.d(TAG, "old check is null");
                return false;
            }
            head = src;
            isHeadChecked = true;
        }
        String temp = copyFile(src, dest, true);
        if (temp == null) {
            Log.d(TAG, "temp is null");
            return false;
        }
        newFile = copyFile(temp, dest,true);
        String newCheck = getCheckSum(newFile,isRecorded);
        boolean result = oldCheck.equals(newCheck);
        oldCheck = newCheck;
        return result;
    }

    public String getCheckSum(String filePath,boolean isRecorded) {
        String result = isCRC?getCRC32(filePath):getMD5(filePath);
        if (isRecorded && mCheckSums != null && result != null) {
            mCheckSums.add(result);
        }
        return result;
    }


    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public void test(int maxLoop, String src, String dest,boolean isRecorded) {
        int max = maxLoop;
        if (isRecorded) {
            mCheckSums = new ArrayList<String>(max);
        }
        newFile = src;
        String dir = dest;
        Log.d(TAG,"test start");
        for(;counter < max;++counter ) {
            if (!copyOnce(newFile,dir,isRecorded)) {
                Log.d(TAG, "test fail");
                return;
            } else {
                Log.i(TAG,"counter = "+counter+" checksum = "+oldCheck);
            }
        }
        Log.d(TAG,"test done");

        //rename();
    }

    public void rename() {
        File file = new File(newFile);
        File headFile = new File(head);
        if (file.exists()) {
            file.renameTo(headFile);
        }
    }

    public void reset() {
        eLog = null;
        mCheckSums = null;
        counter = 0;
        isHeadChecked = false;
        oldCheck = null;
    }

    public static String getCRC32(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("getCRC32()+filePath= "+filePath);
        }

        FileInputStream in = null;
        CRC32 crc = new CRC32();;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[8192];
            int byteCount;
            while ((byteCount = in.read(bytes)) > 0) {
                crc.update(bytes, 0, byteCount);
            }
        } catch(IOException  e) {
            Log.d(TAG,"getCRC32()  "+e.toString());
            eLog = "getCRC32()  "+e.toString();
        }
        finally {
            try {
                if (in !=null) {
                    in.close();
                }}catch(IOException e) {
                Log.d(TAG, e.toString());
            }
        }
        return Long.toHexString(crc.getValue());
    }

    public static String getMD5(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        FileInputStream in = null;
        MessageDigest digester = null;
        byte[] digest = null;
        try {
            in = new FileInputStream(file);
            digester = MessageDigest.getInstance("MD5");
            byte[] bytes = new byte[8192];
            int byteCount;
            while ((byteCount = in.read(bytes)) > 0) {
                digester.update(bytes, 0, byteCount);
            }
            digest = digester.digest();
        } catch(IOException  e) {
            Log.d(TAG,"getMD5()"+e.toString());
            eLog = "getMD5()"+e.toString();
        } catch (NoSuchAlgorithmException e){
            Log.d(TAG, "getMD5()"+e.toString());
        }
        finally {
            try {
                if (in !=null) {
                    in.close();
                }}catch(IOException e) {
                Log.d(TAG, e.toString());
            }
        }
        return bytesToHexString(digest);
    }


    public  String copyFile(String src, String dest,boolean isRemoved) {
        File file = new File(src);
        if (!file.exists() || file.isDirectory()) {
            Log.d(TAG, "copyFile: file not exist or is directory, " + src);
            eLog = "copyFile: file not exist or is directory, " + src;
            return null;
        }
        FileInputStream fi = null;
        FileOutputStream fo = null;
        try {
            fi = new FileInputStream(file);
            File destPlace = new File(dest);
            if (!destPlace.exists()) {
                if (!destPlace.mkdirs())
                    return null;
            }

            String destPath = null;
            File destFile;

            File o = new File(head);
            if (o.exists()) {
                 String destName = getNameFromFilename(file.getName()) + "_Copy."
                            + getExtFromFilename(file.getName());
                  destPath = makePath(dest, destName);
                  destFile = new File(destPath);

            } else {
                   destPath = head;
                   destFile = o;
            }

            if (!destFile.createNewFile())
                return null;

            fo = new FileOutputStream(destFile);
            int count = 102400;
            byte[] buffer = new byte[count];
            int read = 0;
            while ((read = fi.read(buffer, 0, count)) != -1) {
                fo.write(buffer, 0, read);
            }

            if (isRemoved) {
                file.delete();
            }
            return destPath;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "copyFile: file not found, " + src);
            eLog = "copyFile: file not found, " + src;
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "copyFile: " + e.toString());
            eLog = "copyFile: file not found, " + src;
        } finally {
            try {
                if (fi != null)
                    fi.close();
                if (fo != null)
                    fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public  String getNameFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(0, dotPosition);
        }
        return "";
    }

    public  String makePath(String path1, String path2) {
        if (path1.endsWith(File.separator))
            return path1 + path2;

        return path1 + File.separator + path2;
    }

    public  String getExtFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(dotPosition + 1, filename.length());
        }
        return "";
    }

    public String getParentPath(String path) {
        File file = new File(path);
        String result = null;
        if (file.exists()) {
            result = file.getParent();
        }
        return result;
    }
}

