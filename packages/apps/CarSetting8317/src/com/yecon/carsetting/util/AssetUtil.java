package com.yecon.carsetting.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.yecon.carsetting.unitl.L;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class AssetUtil {
	private static String config;
	public static synchronized String getStringFromAssert(Context context,String valueName){ 
        String content = null; //结果字符串 
        if(TextUtils.isEmpty(config)){
	        try{ 
	        	InputStream is = context.getResources().getAssets().open("config.json"); //打开文件 
	            int ch=0; 
	            ByteArrayOutputStream out = new  ByteArrayOutputStream(); //实现了一个输出流 
	            while((ch=is.read()) != -1){ 
	               out.write(ch); //将指定的字节写入此 byte 数组输出流 
	            }
	            byte[] buff = out.toByteArray();//以 byte 数组的形式返回此输出流的当前内容 
	            out.close(); //关闭流 
	            is.close(); //关闭流 
	            content = new String(buff,"UTF-8"); //设置字符串编码 
	            config = content;
	         }catch(Exception e){ 
	        	 Log.e(AssetUtil.class.getName(),e.toString());
	         } 
        }
        
        if(!TextUtils.isEmpty(config)){	
        	try {
				JSONObject jsonObject = new JSONObject(config);
				if(jsonObject.has(valueName)){
					return jsonObject.getString(valueName);
				}
			} catch (JSONException e) {
				Log.e(AssetUtil.class.getName(),e.toString());
			}
        }
    	return null;
  }
}
