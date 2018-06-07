package com.yecon.filemanager;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * Created by chenchu on 15-2-3.
 */
public class FileIconLoader {
    private static final String Tag = "FileIconLoader";
    private static Map<String,Integer> mMap = new HashMap<String,Integer>();
    private static final int INVALID_RESID = -1;
    private static final int UNKOWN_FILE = R.drawable.unknown;
    public static void addIcon(String[] cate, int resId) {
        Integer i = new Integer(resId);
        for(String str:cate) {
            mMap.put(str.toLowerCase(), i);
        }
    }

    public int getIcon(String path) {
        if (path == null) {
            return INVALID_RESID;
        }
        Integer result = mMap.get(path.toLowerCase());
        if (result == null) {
            return UNKOWN_FILE;
        }
        return result.intValue();
    }

    static {
        /*addIcon(new String[]{"mp3"},R.drawable.mp3);
        addIcon(new String[]{"mp4"},R.drawable.mp4);
        addIcon(new String[]{"aac"},R.drawable.aac);
        addIcon(new String[]{"avi"},R.drawable.avi);
        addIcon(new String[]{"flash"},R.drawable.flash);
        addIcon(new String[]{"txt"},R.drawable.txt);
        addIcon(new String[]{"mid"},R.drawable.mid);
        addIcon(new String[]{"mov"},R.drawable.mov);
        addIcon(new String[]{"wav"},R.drawable.wav);
        addIcon(new String[]{"zip","rar"},R.drawable.compressed);
        addIcon(new String[]{"apk"},R.drawable.apk);
        addIcon(new String[]{"wma"},R.drawable.wma);*/
		addIcon(new String[]{"mp3","aac","mid","wav","wma"},R.drawable.audio);
		addIcon(new String[]{"mp4","avi","flash","mov","wma"},R.drawable.video);
        addIcon(new String[]{"jpeg","png","gif","bmp","jpg"},R.drawable.pic);
        Log.i(Tag, "load finished");
    }

}
