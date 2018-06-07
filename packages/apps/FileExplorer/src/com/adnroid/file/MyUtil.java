package com.adnroid.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.android.file.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.LinearLayout;

public class MyUtil {

	/**
	 * �ж��ļ�MimeType�ķ���
	 * 
	 * @param f
	 * @param isOpen
	 *            Ŀ�Ĵ򿪷�ʽΪtrue
	 * @return
	 */
	public static String getMIMEType(File f, boolean isOpen) {
		String type = "";
		String fName = f.getName();
		/* ȡ����չ�� */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();
		if (isOpen) {
			/* �������������;���MimeType */
			if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
					|| end.equals("xmf") || end.equals("ogg")
					|| end.equals("wav")) {
				type = "audio";
			} else if (end.equals("3gp") || end.equals("mp4")
					|| end.equals("mov") || end.equals("m4v")
					|| end.equals("f4v") || end.equals("3gpp2")
					|| end.equals("3g2") || end.equals("mkv")
					|| end.equals("avi") || end.equals("divx")
					|| end.equals("mpg") || end.equals("mpeg")
					|| end.equals("vob") || end.equals("mts")
					|| end.equals("m2ts") || end.equals("ts")
					|| end.equals("tp") || end.equals("trp")
					|| end.equals("ogm") || end.equals("asf")
					|| end.equals("asx") || end.equals("wmv")
					|| end.equals("ogg") || end.equals("wav")
					|| end.equals("flac") || end.equals("rmvb")
					|| end.equals("rm")) {
				type = "video";
			} else if (end.equals("jpg") || end.equals("gif")
					|| end.equals("png") || end.equals("jpeg")
					|| end.equals("bmp")) {
				type = "image";
			} else {
				/* ����޷�ֱ�Ӵ򿪣�����������б���û�ѡ��? */
				type = "*";
			}
			type += "/*";
		} else {
			if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
					|| end.equals("xmf") || end.equals("ogg")
					|| end.equals("wav")) {
				type = "audio";
			} else if (end.equals("3gp") || end.equals("mp4")
					|| end.equals("mov") || end.equals("m4v")
					|| end.equals("f4v") || end.equals("3gpp2")
					|| end.equals("3g2") || end.equals("mkv")
					|| end.equals("avi") || end.equals("divx")
					|| end.equals("mpg") || end.equals("mpeg")
					|| end.equals("vob") || end.equals("mts")
					|| end.equals("m2ts") || end.equals("ts")
					|| end.equals("tp") || end.equals("trp")
					|| end.equals("ogm") || end.equals("asf")
					|| end.equals("asx") || end.equals("wmv")
					|| end.equals("ogg") || end.equals("wav")
					|| end.equals("flac") || end.equals("rmvb")
					|| end.equals("rm")) {
				type = "video";
			} else if (end.equals("jpg") || end.equals("gif")
					|| end.equals("png") || end.equals("jpeg")
					|| end.equals("bmp")) {
				type = "image";
			} else if (end.equals("apk")) {
				type = "apk";
			} else if (end.equals("doc") || end.equals("docx")) {
				type = "doc";
			} else if (end.equals("xls") || end.equals("xlsx")) {
				type = "xls";
			} else if (end.equals("pdf")) {
				type = "pdf";
			} else if (end.equals("ppt")) {
				type = "ppt";
			} else if (end.equals("txt")) {
				type = "txt";
			}
		}
		return type;
	}

	/**
	 * ����ͼƬ�ķ���
	 * 
	 * @param bitMap
	 * @param x
	 * @param y
	 * @param newWidth
	 * @param newHeight
	 * @param matrix
	 * @param isScale
	 * @return
	 */
	public static Bitmap fitSizePic(File f) {
		Bitmap resizeBmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// ����Խ�������ͼƬռ�õ�heapԽС ��Ȼ�������?
		if (f.length() < 20480) { // 0-20k
			opts.inSampleSize = 1;
		} else if (f.length() < 51200) { // 20-50k
			opts.inSampleSize = 2;
		} else if (f.length() < 307200) { // 50-300k
			opts.inSampleSize = 4;
		} else if (f.length() < 819200) { // 300-800k
			opts.inSampleSize = 6;
		} else if (f.length() < 1048576) { // 800-1024k
			opts.inSampleSize = 8;
		} else {
			opts.inSampleSize = 10;
		}
		resizeBmp = BitmapFactory.decodeFile(f.getPath(), opts);
		return resizeBmp;
	}

	/**
	 * �ļ���С����
	 * 
	 * @param f
	 * @return
	 */
	public static String fileSizeMsg(File f) {
		int sub_index = 0;
		String show = "";
		if (f.isFile()) {
			long length = f.length();
			if (length >= 1073741824) {
				sub_index = (String.valueOf((float) length / 1073741824))
						.indexOf(".");
				show = ((float) length / 1073741824 + "000").substring(0,
						sub_index + 3) + "GB";
			} else if (length >= 1048576) {
				sub_index = (String.valueOf((float) length / 1048576))
						.indexOf(".");
				show = ((float) length / 1048576 + "000").substring(0,
						sub_index + 3) + "MB";
			} else if (length >= 1024) {
				sub_index = (String.valueOf((float) length / 1024))
						.indexOf(".");
				show = ((float) length / 1024 + "000").substring(0,
						sub_index + 3) + "KB";
			} else if (length < 1024) {
				show = String.valueOf(length) + "B";
			}
		}
		return show;
	}

	/**
	 * У��������ļ��������Ƿ�Ϸ�
	 * 
	 * @param newName
	 * @return
	 */
	public static boolean checkDirPath(String newName) {
		boolean ret = false;
		if (newName.indexOf("\\") == -1) {
			ret = true;
		}
		return ret;
	}

	/**
	 * У��������ļ������Ƿ�Ϸ�
	 * 
	 * @param newName
	 * @return
	 */
	public static boolean checkFilePath(String newName) {
		boolean ret = false;
		if (newName.indexOf("\\") == -1) {
			ret = true;
		}
		return ret;
	}

	public static boolean copyFile(String d_old, String d_new,
			OperationEntity oe) {
		File sourceFile = new File(d_old);
		File targetFile = new File(d_new);
		if (targetFile.exists()) {// �ļ����� ��ʾ�Ƿ����?
			targetFile.delete();
			targetFile = new File(d_new);
		}
		try {
			// �½��ļ����������������л���
			FileInputStream input = new FileInputStream(sourceFile);
			BufferedInputStream inBuff = new BufferedInputStream(input);

			// �½��ļ���������������л���?
			FileOutputStream output = new FileOutputStream(targetFile);
			BufferedOutputStream outBuff = new BufferedOutputStream(output);

			// ��������
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// ˢ�´˻���������
			outBuff.flush();

			// �ر���
			inBuff.close();
			outBuff.close();
			output.close();
			input.close();
		} catch (Exception e) {
			oe.returnMsgs.add(d_old + "���Ʋ��ɹ���");
		}
		return true;
	}

	// �����ļ���
	public static void copyDirectiory(String sourceDir, String targetDir,
			OperationEntity oe) {

		Log.e("lll", "拷贝目录");

		// �½�Ŀ��Ŀ¼
		File newFile = new File(targetDir);
		if (!newFile.exists()) {
			newFile.mkdirs();
		}
		// ��ȡԴ�ļ��е�ǰ�µ��ļ���Ŀ¼
		File[] file = (new File(sourceDir)).listFiles();
		if (file != null)
			for (int i = 0; i < file.length; i++) {
				if (file[i].isFile()) {
					// Դ�ļ�
					File sourceFile = file[i];
					// Ŀ���ļ�
					File targetFile = new File(
							new File(targetDir).getAbsolutePath()
									+ File.separator + file[i].getName());
					copyFile(sourceFile.getPath(), targetFile.getPath(), oe);
				}
				if (file[i].isDirectory()) {
					// ׼�����Ƶ�Դ�ļ���
					String dir1 = sourceDir + "/" + file[i].getName();
					// ׼�����Ƶ�Ŀ���ļ���
					String dir2 = targetDir + "/" + file[i].getName();
					copyDirectiory(dir1, dir2, oe);
				}
			}
	}

	/**
	 * ɾ���ļ�
	 * 
	 * @param file
	 * @return
	 */
	public static boolean delFile(File f, OperationEntity oe) {
		boolean ret = false;
		try {
			if (f.exists()) {
				if (f.isDirectory()) {
					File[] files = f.listFiles();
					for (int i = 0; i < files.length; i++) {
						if (files[i].isDirectory()) {
							delFile(files[i], oe);
						} else {
							files[i].delete();
						}
					}
				}
				f.delete(); // ɾ�����ļ���
			}
		} catch (Exception e) {
			oe.returnMsgs.add(f.getPath() + "ɾ��ʧ�ܣ�");
		}
		return ret;
	}

	/**
	 * �½��ļ�
	 * 
	 * @param file
	 * @return
	 */
	public static boolean newFile(File f) {
		try {
			f.createNewFile();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * ��������ö��
	 * 
	 * @author wanghb COPYPASTE:����ճ��,CUTPASTE������ճ��,DELETE��ɾ��
	 */
	public static enum operationStates {
		COPYPASTE, CUTPASTE, DELETE
	}

	/**
	 * �ļ�������Ϣʵ��
	 * 
	 * @author wanghb
	 * 
	 */
	public static class OperationEntity {

		// ��������
		public operationStates state;
		// �������ļ�·��
		public ArrayList<String> operationPaths = new ArrayList<String>();
		// Ŀ���ļ���
		public String goalPath;
		// ����������Ϣ
		public ArrayList<String> returnMsgs = new ArrayList<String>();
		// �������?
		public boolean operationResult;
		// ������
		public FileListAdapter adapter;

		// public LinearLayout dataLayout;//xuefan add
		//
		// public Context context;//xuefan add
	}

	/**
	 * @Description:�ļ���������
	 * @Author: wanghb
	 * @Email: wanghb@foryouge.com.cn
	 * @param oe
	 *            {@link #OperationEntity}
	 * @Others:
	 */
	public static void operationFiles(OperationEntity oe) {
		if (oe.state.equals(operationStates.DELETE)) {
			for (String path : oe.operationPaths) {
				delFile(new File(path), oe);
				File newFile = new File(oe.goalPath + File.separator
						+ new File(path).getName());
				if (!newFile.exists()) {
					oe.adapter.delItems(new File[] { newFile });
					oe.adapter.notifyDataSetChanged();
					// if(oe.adapter.getCount()==0){
					// //dataLayout.setBackground(getResources().getDrawable(id));
					// oe.dataLayout.setBackgroundColor(oe.context.getResources().getColor(R.color.info_target_hover_tint));
					// }else{
					// oe.dataLayout.setBackgroundColor(0);
					// }
				}
			}
		} else {
			// ���Ƶ���ʱĿ¼
			String tempFolder = createTempFolder();
			for (String path : oe.operationPaths) {
				copyDirOrFile(path, tempFolder, oe);
			}

			// ����ʱĿ¼�ƶ���Ŀ��Ŀ¼
			OperationEntity tempOe = new OperationEntity();
			File tempFolderFile = new File(tempFolder);
			File[] childrenFiles = tempFolderFile.listFiles();
			if (childrenFiles != null)
				for (File file : childrenFiles) {
					copyDirOrFile(file.getPath(), oe.goalPath, tempOe);
					File newFile = new File(oe.goalPath + File.separator
							+ file.getName());
					if (newFile.exists()) {
						oe.adapter.delItems(new File[] { newFile });
						oe.adapter.addItems(new File[] { newFile });
						oe.adapter.notifyDataSetChanged();
						// if(oe.adapter.getCount()==0){
						// //dataLayout.setBackground(getResources().getDrawable(id));
						// oe.dataLayout.setBackgroundColor(oe.context.getResources().getColor(R.color.info_target_hover_tint));
						// }else{
						// oe.dataLayout.setBackgroundColor(0);
						// }
					}
				}

			if (oe.state.equals(operationStates.CUTPASTE)) {
				for (String path : oe.operationPaths) {
					delFile(new File(path), new OperationEntity());
				}
			}
			delFile(new File(tempFolder), new OperationEntity());
		}
	}

	private static String createTempFolder() {
		Long now = System.currentTimeMillis();
		String rootPath = Environment.getExternalStorageDirectory().getPath();
		if (rootPath.endsWith(File.separator)) {
			rootPath = rootPath + now;
		} else {
			rootPath = rootPath + File.separator + now;
		}
		new File(rootPath).mkdirs();
		return rootPath;
	}

	/**
	 * 
	 * @Description:
	 * @Author: wanghb
	 * @Email: wanghb@foryouge.com.cn
	 * @param oldPath
	 * @param newPath
	 * @param oe
	 * @Others:
	 */
	private static void copyDirOrFile(String oldPath, String newPath,
			OperationEntity oe) {
		File oldFile = new File(oldPath);
		File newFile = new File(newPath);
		if (!newFile.exists()) {
			newFile.mkdirs();
		}
		if (!oldFile.exists()) {
			oe.returnMsgs.add(oldFile.getPath() + "�Ѳ����� !");
			return;
		}
		newPath = newPath + File.separator + oldFile.getName();
		if (oldFile.isDirectory()) {
			copyDirectiory(oldPath, newPath, oe);
		} else {
			copyFile(oldPath, newPath, oe);
		}
	}

	//找出指定路径下所有的图片
	public static String[] ListFile(String path) {

		File file = new File(path);
		File[] f = file.listFiles();
		int k=0;
		String Path1[] = new String[f.length];
		String Path[] = null ;
		for (int i = 0; i < f.length; i++) {
			String fname = f[i].getName();
			String end = fname.substring(fname.lastIndexOf(".") + 1,
					fname.length()).toLowerCase();
			if (end.equals("jpg") || end.equals("gif") || end.equals("png")
					|| end.equals("jpeg") || end.equals("bmp")) {
				Path1[k++] = f[i].getPath();
			}	
		}
		Path = new String[k];
		for (int i = 0; i < k; i++) {
			Path[i] =Path1[i];
		}
		
		//Log.e("循环完成", "循环完成");
		return Path;

	}

}
