
package com.yecon.volumeadjust;

import android.content.Context;

import org.apache.http.util.EncodingUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class VolumeAdjustUtil {

    /**
     * 从/data/data/<packagename>/files/目录中读取文件内容
     * 
     * @param context
     * @param filename
     * @return
     */
    public static String readFileFromData(Context context, String filename) {
        String text = "";
        try {
            FileInputStream fis = context.openFileInput(filename);
            int len = fis.available();
            byte[] buf = new byte[len];
            fis.read(buf);
            text = EncodingUtils.getString(buf, "UTF-8");
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }

    /**
     * 在/data/data/<packagename>/files/目录中创建文件，并写入内容
     * 
     * @param context
     * @param filename
     * @param contents
     */
    public static void writeFileToData(Context context, String filename, String contents) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(contents.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从内置SD卡上读取文件内容
     * 
     * @param context
     * @param path
     * @return
     */
    public static String readFileFromSDCard(Context context, String path) {
        String text = "";
        try {
            FileInputStream fis = new FileInputStream(path);
            int len = fis.available();
            byte[] buf = new byte[len];
            fis.read(buf);
            text = EncodingUtils.getString(buf, "UTF-8");
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }

    /**
     * 在内置SD卡上创建文件，并写入内容
     * 
     * @param context
     * @param path
     * @param contents
     */
    public static void writeFileToSDCard(Context context, String path, String contents) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            osw.write(contents);
            osw.flush();
            fos.close();
            osw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
