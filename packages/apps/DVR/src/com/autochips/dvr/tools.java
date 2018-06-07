package com.autochips.dvr;

import java.io.File;

public class tools {
	public static String millions2hour(int million) {
		int hour = million / (60 * 60 * 1000);
		int minute = (million - hour * 60 * 60 * 1000) / (60 * 1000);
		int seconds = (million - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000;

		if (seconds >= 60) {
			seconds = seconds % 60;
			minute += seconds / 60;
		}
		if (minute >= 60) {
			minute = minute % 60;
			hour += minute / 60;
		}

		String sh = "";
		String sm = "";
		String ss = "";
		if (hour < 10) {
			sh = "0" + String.valueOf(hour);
		} else {
			sh = String.valueOf(hour);
		}
		if (minute < 10) {
			sm = "0" + String.valueOf(minute);
		} else {
			sm = String.valueOf(minute);
		}
		if (seconds < 10) {
			ss = "0" + String.valueOf(seconds);
		} else {
			ss = String.valueOf(seconds);
		}

		return sh + ":" + sm + ":" + ss;
	}
	
	/**
	 * delete file
	 * @param filePath
	 * @return
	 */
	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.isFile() && file.exists()) {
			return file.delete();
		}
		return false;
	}
	
	public static String getOnlyFile(String str) {
		int start = str.lastIndexOf('/')+1;
		int end = str.length();
		return str.substring(start, end);
	}
}
