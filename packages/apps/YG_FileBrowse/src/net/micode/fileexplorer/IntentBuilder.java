/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.micode.fileexplorer;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.yecon.filemanager.FileInfo;
import com.yecon.filemanager.MyMediaTools;
import com.yecon.filemanager.R;

public class IntentBuilder {
	
	private static void startActivityHelper(Context context, Intent intent) {
		 if (intent.resolveActivity(context.getPackageManager()) != null) {
             context.startActivity(intent);
         } else {
         	Context ctx = context.getApplicationContext();
         	Toast.makeText(ctx,ctx.getString(R.string.no_support_apk) , Toast.LENGTH_SHORT).show();
         }
	}

    public static void viewFile(final Context context, final String filePath) {
        String type = getMimeType(filePath);
        
        if (type == null || TextUtils.isEmpty(type) || TextUtils.equals(type, "*/*")) {
        	//有些后缀名他不认识
        	String ext = MyMediaTools.getFileExt(filePath);
        	if (MyMediaTools.isVideoFileType(ext)) {
        		type = "video";
        	}else if (MyMediaTools.isAudioFileType(ext)) {
        		type = "audio";
        	}else if (MyMediaTools.isImageFileType(ext)) {
        		type = "image";
        	}
        }else if (type != null) {
        	String ext = MyMediaTools.getFileExt(filePath);
        	if (ext.equalsIgnoreCase("rm")) {
        		type = "video";
        	}
        }

        if (!TextUtils.isEmpty(type) && !TextUtils.equals(type, "*/*")) {
            /* 设置intent的file与MimeType */
            Intent intent = new Intent();
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(filePath)), type);
           startActivityHelper(context, intent);
           
        } else {
            // unknown MimeType
           /* AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setTitle(R.string.dialog_select_type);

            CharSequence[] menuItemArray = new CharSequence[] {
                    context.getString(R.string.dialog_type_text),
                    context.getString(R.string.dialog_type_audio),
                    context.getString(R.string.dialog_type_video),
                    context.getString(R.string.dialog_type_image) };
            dialogBuilder.setItems(menuItemArray,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                */
               //             String selectType = "*/*";
        	   /*
                            switch (which) {
                            case 0:
                                selectType = "text/plain";
                                break;
                            case 1:
                                selectType = "audio/*";
                                break;
                            case 2:
                                selectType = "video/*";
                                break;
                            case 3:
                                selectType = "image/*";
                                break;
                            }
                            Intent intent = new Intent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(filePath)), selectType);
                            startActivityHelper(context, intent);
                    }});
            dialogBuilder.show();*/
        }
    }

    public static Intent buildSendFile(ArrayList<FileInfo> files) {
        ArrayList<Uri> uris = new ArrayList<Uri>();

        String mimeType = "*/*";
        for (FileInfo file : files) {
            if (file.isDir)
                continue;

            File fileIn = new File(file.filePath);
            mimeType = getMimeType(file.fileName);
            Uri u = Uri.fromFile(fileIn);
            uris.add(u);
        }

        if (uris.size() == 0)
            return null;

        boolean multiple = uris.size() > 1;
        Intent intent = new Intent(multiple ? Intent.ACTION_SEND_MULTIPLE
                : Intent.ACTION_SEND);

        if (multiple) {
            intent.setType("*/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        } else {
            intent.setType(mimeType);
            intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
        }

        return intent;
    }

    private static String getMimeType(String filePath) {
        int dotPosition = filePath.lastIndexOf('.');
        if (dotPosition == -1)
            return "*/*";

        String ext = filePath.substring(dotPosition + 1, filePath.length()).toLowerCase();
        String mimeType = MimeUtils.guessMimeTypeFromExtension(ext);
        //if (ext.equals("mtz")) {
            //mimeType = "application/miui-mtz";
        //}

        return mimeType != null ? mimeType : "*/*";
    }
}
