package com.yecon.filemanager;

public class MyMediaTools {
	public static final String[] mVideoExt=
		{	"mkv", 
			"rmvb",
			"vid",
			"divx",
			"asf",
			"mp4",
			"m4a",
			"mpg",
			"mpeg",
			"dat",
			"vob",
			"3gp",
			"m2v",
			"3g2",
			"m4v",
			"ogg",
			"ogm",
			"rm",
			"rmv",
			"flv",
			"mov",
			"wmv",
			"ts",
			"m2ts",
			"mfs"
		};
	
	public static final String[] mAudioExt=
		{	"mp3", 
			"wma",
			"ac3",
			"aac",
			"ra",
			"ram",
			"rmm",
			"pcm",
			"wav",
			"mp1",
			"mp2",
			"flac",
			"ape",			
		};
	
	public static final String[] mImageExt=
		{	"jpg", 
			"jpe",
			"jpeg",
			"bmp",
			"gif",
			"png",
		};
	
	/**
	 * 判断是否视频文件
	 * @param ext
	 * @return
	 */
	public static boolean isVideoFileType(String ext) {
		for (int i=0; i<mVideoExt.length; i++) {
			if (ext.equalsIgnoreCase(mVideoExt[i])) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 判断是否音频文件
	 * @param ext
	 * @return
	 */
	public static boolean isAudioFileType(String ext) {
		for (int i=0; i<mAudioExt.length; i++) {
			if (ext.equalsIgnoreCase(mAudioExt[i])) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isImageFileType(String ext) {
		for (int i=0; i<mImageExt.length; i++) {
			if (ext.equalsIgnoreCase(mImageExt[i])) {
				return true;
			}
		}
		
		return false;
	}
	
	public static String getFileExt(String filename) {
		int start = filename.lastIndexOf('.');
		int len = filename.length();
		
		String ext = filename.substring(start+1, len);
		
		return ext;
	}
}
