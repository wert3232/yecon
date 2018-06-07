package com.yecon.filemanager;

import android.content.Intent;
import android.net.Uri;
import java.io.File;



/**
 * Created by chenchu on 14-12-17.
 */
public class FileIntentBuilder {

    public static Intent buildServiceIntent(String src, String dest, String action) {
        if (action == null) {
            throw new IllegalArgumentException();
        }

        Uri data;
        boolean isExtraSrc = true;
        if (src==null && dest != null) {
            data = Uri.fromFile(new File(dest));
        } else{
            data = Uri.fromFile(new File(src));
            isExtraSrc = false;
        }
        Intent intent = new Intent(action,data);
        intent.putExtra("extra info",isExtraSrc?src:dest);
        return intent;
    }
}
