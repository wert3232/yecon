package com.yecon.filemanager;

import java.io.File;

import android.content.Context;
import net.micode.fileexplorer.MediaFile;

/**
 * Created by chenchu on 15-3-13.
 */
public class FileTypeLoader {

    private Context mContext;

    public FileTypeLoader(Context context) {
        super();
        mContext = context;
    }

    public String getType(String path) {
        String result = mContext.getString(R.string.file_type_other);
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                result = mContext.getString(R.string.file_type_folder);
                return result;
            }
        }
        MediaFile.MediaFileType fileType = MediaFile.getFileType(path);
        if (fileType == null) {
            return result;
        }
        int type = fileType.fileType;
        if (MediaFile.isAudioFileType(type)){
            result = mContext.getString(R.string.file_type_audio);
            return result;
        }
        if (MediaFile.isVideoFileType(type)) {
            result = mContext.getString(R.string.file_type_video);
            return result;
        }
        if (MediaFile.isImageFileType(type)) {
            result = mContext.getString(R.string.file_type_image);
            return result;
        }
        return result;
    }
}
