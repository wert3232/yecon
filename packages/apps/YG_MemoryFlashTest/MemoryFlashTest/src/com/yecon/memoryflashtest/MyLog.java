package com.yecon.memoryflashtest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.util.Log;
import com.autochips.storage.EnvironmentATC;
/**
 * 带日志文件输入的，又可控开关的日志调试
 * 
 * @author BaoHang
 * @version 1.0
 * @data 2012-2-20
 */
public class MyLog {
	private static Boolean MYLOG_SWITCH=true; // 日志文件总开关
	private static Boolean MYLOG_WRITE_TO_FILE=true;// 日志写入文件开关
	private static char MYLOG_TYPE='v';// 输入日志类型，w代表只输出告警信息等，v代表输出所有信息
	private static String MYLOG_PATH_SDCARD_DIR="/sdcard/";// 日志文件在sdcard中的路径
	private static final String MYLOG_SUB_DIR_NAME="yecon_memory_test";
	private static int SDCARD_LOG_FILE_SAVE_DAYS = 0;// sd卡中日志文件的最多保存天数
	private static String MYLOGFILEName = "Log.txt";// 本类输出的日志文件名称
	private static SimpleDateFormat myLogSdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");// 日志的输出格式
	private static SimpleDateFormat logfileFormat = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式

	public static void w(String tag, Object msg) { // 警告信息
		log(tag, msg.toString(), 'w');
	}

	public static void e(String tag, Object msg) { // 错误信息
		log(tag, msg.toString(), 'e');
	}

	public static void d(String tag, Object msg) {// 调试信息
		log(tag, msg.toString(), 'd');
	}

	public static void i(String tag, Object msg) {//
		log(tag, msg.toString(), 'i');
	}

	public static void v(String tag, Object msg) {
		log(tag, msg.toString(), 'v');
	}

	public static void w(String tag, String text) {
		log(tag, text, 'w');
	}

	public static void e(String tag, String text) {
		log(tag, text, 'e');
	}

	public static void d(String tag, String text) {
		log(tag, text, 'd');
	}

	public static void i(String tag, String text) {
		log(tag, text, 'i');
	}

	public static void v(String tag, String text) {
		log(tag, text, 'v');
	}

	/**
	 * 根据tag, msg和等级，输出日志
	 * 
	 * @param tag
	 * @param msg
	 * @param level
	 * @return void
	 * @since v 1.0
	 */
	private static void log(String tag, String msg, char level) {
		if (MYLOG_SWITCH) {
			if ('e' == level && ('e' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) { // 输出错误信息
				Log.e(tag, msg);
			} else if ('w' == level && ('w' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
				Log.w(tag, msg);
			} else if ('d' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
				Log.d(tag, msg);
			} else if ('i' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
				Log.i(tag, msg);
			} else {
				Log.v(tag, msg);
			}
			if (MYLOG_WRITE_TO_FILE)
				writeLogtoFile(String.valueOf(level), tag, msg);
		}
	}
	
	private static File logFile=null;
	public static void initLogFile(Context context){
		try{
			EnvironmentATC env  = new EnvironmentATC(context);
			String device[] = env.getStorageMountedPaths();		
			if(device.length>0){
				MYLOG_PATH_SDCARD_DIR = device[device.length-1] + "/" + MYLOG_SUB_DIR_NAME + "/";
				Log.i("", "initLogFile path: " + MYLOG_PATH_SDCARD_DIR);
				File dir = new File(MYLOG_PATH_SDCARD_DIR);
				if(!dir.isDirectory() || !dir.exists()){
					dir.mkdirs();
				}
				Date nowtime = new Date();
				String needWriteFiel = logfileFormat.format(nowtime);			
				logFile = new File(MYLOG_PATH_SDCARD_DIR, needWriteFiel
						+ MYLOGFILEName);
				if(logFile!=null){
					Log.i("", "initLogFile ok: " + logFile.getAbsolutePath());
				}
				else{
					Log.i("", "initLogFile failed" );
				}
				
			}			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void closeLogFile(){
		if(logFile!=null){
			logFile = null;
		}
	}
	/**
	 * 打开日志文件并写入日志
	 * 
	 * @return
	 * **/
	private static void writeLogtoFile(String mylogtype, String tag, String text) {// 新建或打开日志文件
		Date nowtime = new Date();
		if(logFile!=null){
			if(tag.equals("e")){
				tag = "error";
			}
			else if(tag.equals("w")){
				tag = "warning";
			}
			String needWriteMessage = myLogSdf.format(nowtime) + " " + mylogtype
					+ " " + tag + " :\n" + text;
			try {
				FileWriter filerWriter = new FileWriter(logFile, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
				BufferedWriter bufWriter = new BufferedWriter(filerWriter);
				bufWriter.write(needWriteMessage);
				bufWriter.newLine();
				bufWriter.close();
				filerWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logFile = null;
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * 删除制定的日志文件
	 * */
	public static void delFile() {// 删除日志文件
		String needDelFiel = logfileFormat.format(getDateBefore());
		File file = new File(MYLOG_PATH_SDCARD_DIR, needDelFiel + MYLOGFILEName);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
	 * */
	private static Date getDateBefore() {
		Date nowtime = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(nowtime);
		now.set(Calendar.DATE, now.get(Calendar.DATE)
				- SDCARD_LOG_FILE_SAVE_DAYS);
		return now.getTime();
	}

}
