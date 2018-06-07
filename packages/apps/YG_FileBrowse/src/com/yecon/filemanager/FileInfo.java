package com.yecon.filemanager;

import net.micode.fileexplorer.Util;

import java.util.List;

/**
 * Created by chenchu on 14-12-16.
 */
public class FileInfo implements Cloneable {
    public String filePath = null;
    public String fileName = null;
    public boolean isDir =false;
    public boolean isSelected = false;
    public long modifiedTime = Long.MIN_VALUE;
    public String formattedTime = null;
    public long size = Long.MIN_VALUE;
    public String formattedSize = null;
    public int fileRes = Integer.MIN_VALUE;

    private static FileInfo protype = new FileInfo();
    private FileInfo(){};

    public static FileInfo getProtype() {
        return protype;
    }

    public FileInfo reset() {
        filePath = null;
        fileName = null;
        isDir =false;
        isSelected = false;
        modifiedTime = Long.MIN_VALUE;
        formattedTime = null;
        size = Long.MIN_VALUE;
        formattedSize = null;
        fileRes = Integer.MIN_VALUE;
        return this;
    }

    public Object clone() {
        FileInfo info = null;
        try {
            info = (FileInfo) super.clone();
        } catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return info;
    }

    @Override
    public String toString() {
        return Util.getExtFromFilename(fileName);
    }

    public static int selectAll(List<FileInfo> list, boolean isSelected) {
        if (list == null) {
            return 0;
        }
        int i = 0;
        for(FileInfo info: list) {
            info.isSelected = isSelected;
            ++i;
        }
        return i;
    }



}