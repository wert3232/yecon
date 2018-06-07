package com.adnroid.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.Intent;
import android.net.Uri;
import com.adnroid.file.MyUtil.OperationEntity;
import com.adnroid.file.MyUtil.operationStates;
import com.android.file.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @Filename: OperationDialog.java
 * @Author: wanghb
 * @Email: wanghb@foryouge.com.cn
 * @CreateDate: 2011-7-3
 * @Description: description of the new class
 * @Others: comments
 * @ModifyHistory:
 */
public class OperationDialog extends AlertDialog {

	private Activity mActivity;
	private View mView;
	private OperationEntity currentOe;
	private updateUIEntity ue = new updateUIEntity();

	private TextView f_from;
	private TextView f_to;
	private TextView f_title;
	private TextView operationPath;
	private Button cancel;
	private ImageView gotoIcon;

	private static final int CLOSE = 0;
	private static final int UPDATEUI = 1;
	private static final int UPDATELIST = 2;
	private static final int DELETE = 3;
	private static final int ERROR = 4;

	// ȡ��
	private static boolean isCancel;

	File uFile;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CLOSE:
				refurbishList();
				dismiss();
				isCancel = false;
				break;
			case UPDATEUI:
				updateDialogUI();
				break;
			case UPDATELIST:
				// currentOe.adapter.notifyDataSetChanged();
				break;
			case DELETE:
				ArrayList<String> operationPathes = new ArrayList<String>();
				operationPathes.addAll(currentOe.operationPaths);
				currentOe.operationPaths.clear();
				ArrayList<File> files = new ArrayList<File>();
				for (String path : operationPathes) {
					File f = new File(path);
					if (!f.exists()) {
						files.add(f);
					}
				}
				File[] fs = files.toArray(new File[0]);
				currentOe.adapter.delItems(fs);
				currentOe.adapter.notifyDataSetChanged();
				dismiss();
				break;
			case ERROR:
				Toast.makeText(mActivity, R.string.msg_error_childpath,
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};

	/**
	 * ˢ��UI
	 * 
	 * @Description:
	 * @Author: wanghb
	 * @Email: wanghb@foryouge.com.cn
	 * @Others:
	 */
	private void refurbishList() {
		String currentPath = currentOe.goalPath;
		File file = new File(currentPath);
		File[] files = file.listFiles();
		currentOe.adapter.refurbish(files);
		currentOe.adapter.notifyDataSetChanged();
	}

	/**
	 * @Description:���¶Ի������ui
	 * @Author: wanghb
	 * @Email: wanghb@foryouge.com.cn
	 * @Others:
	 */
	private void updateDialogUI() {
		f_title.setVisibility(ue.title == null ? View.INVISIBLE : View.VISIBLE);
		f_title.setText(ue.title);
		f_from.setVisibility(ue.from_file == null ? View.INVISIBLE
				: View.VISIBLE);
		f_from.setText(ue.from_file);
		f_to.setVisibility(ue.goto_file == null ? View.INVISIBLE : View.VISIBLE);
		f_to.setText(ue.goto_file);
		operationPath.setVisibility(ue.operationPath == null ? View.INVISIBLE
				: View.VISIBLE);
		operationPath.setText(ue.operationPath);
	}

	public OperationDialog(Context context, Activity mActivity) {
		super(context);
		this.mActivity = mActivity;
		LayoutInflater factory = LayoutInflater.from(mActivity);
		/* ��ʼ��myChoiceView��ʹ��new_alertΪlayout */
		final View myView = factory.inflate(R.layout.operation_dialog, null);
		this.mView = myView;
		f_from = (TextView) mView.findViewById(R.id.f_from);
		f_to = (TextView) mView.findViewById(R.id.f_to);
		f_title = (TextView) mView.findViewById(R.id.f_title);
		cancel = (Button) mView.findViewById(R.id.cancel);
		cancel.setText(mActivity.getResources().getString(
				android.R.string.cancel));
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isCancel = true;
			}
		});
		operationPath = (TextView) mView.findViewById(R.id.operationPath);
		setView(myView);

		gotoIcon = (ImageView) mView.findViewById(R.id.view3);
		final View v3 = mView.findViewById(R.id.view2);
		v3.setVisibility(View.VISIBLE);
		final TranslateAnimation ta = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
				7.3f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f);
		ta.setDuration(3000);
		ta.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				v3.setVisibility(View.VISIBLE);
				// ta.setDuration(3000);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				v3.setVisibility(View.GONE);
				v3.startAnimation(ta);
			}
		});
		v3.startAnimation(ta);
	}

	/**
	 * ִ�в���
	 * 
	 * @Description:
	 * @Author: wanghb
	 * @Email: wanghb@foryouge.com.cn
	 * @Others:
	 */
	public void execute(final OperationEntity operationEntity) {
		execute(operationEntity, false);
	}
	public void execute(final OperationEntity operationEntity, final boolean replace) {
		isCancel = false;
		currentOe = operationEntity;
		if (operationStates.DELETE.equals(operationEntity.state)) {
			gotoIcon.setImageResource(R.drawable.recycle);
		} else {
			gotoIcon.setImageResource(R.drawable.folder_dialog);
		}
		new Thread() {
			public void run() {
				operationFiles(operationEntity,replace);
			}
		}.start();
		show();
	}

	/**
	 * @Description:�ļ���������
	 * @Author: wanghb
	 * @Email: wanghb@foryouge.com.cn
	 * @param operationEntity
	 *            {@link #OperationEntity}
	 * @Others:
	 */
	private void operationFiles(OperationEntity operationEntity) {
		operationFiles(operationEntity, false);
	}
	private void operationFiles(OperationEntity operationEntity, boolean replace) {
		if (operationEntity.state.equals(operationStates.DELETE)) {
			ue.title = mActivity.getResources().getString(
					R.string.operation_delete);
			for (int i = 0; i < operationEntity.operationPaths.size(); i++) {
				String path = operationEntity.operationPaths.get(i);
				File delFile = new File(path);
				ue.from_file = delFile.getParentFile().getName();
				ue.goto_file = "";
				if (!delFile(delFile, operationEntity)) {
					break;
				}
			}
			Message updatelistmsg = new Message();
			updatelistmsg.what = DELETE;
			mHandler.sendMessage(updatelistmsg);
			ue = new updateUIEntity();
			return;
		} else {
			// ------------------------------------------------------------------------
			// ɨ�赽�ڴ���
			ue.title = mActivity.getResources().getString(
					R.string.operation_prepare);
			Message msg = new Message();
			msg.what = UPDATEUI;
			mHandler.sendMessage(msg);
			// HashMap<String, ArrayList<String>> pathMap =
			// scanPath(oe.operationPaths);
			HashMap<String, ArrayList<String>> pathMap = scanPath(operationEntity.operationPaths);

			// ׼���С�����
			if (pathMap != null) {
				Set<String> keys = pathMap.keySet();
				File goalFile = new File(operationEntity.goalPath);
				// Log.e("path", ""+goalFile.getAbsolutePath());
				for (int i = 0; i < keys.size(); i++) {
					if (isCancel) {
						break;
					}
					String key = keys.toArray(new String[0])[i];
					ue.title = mActivity.getResources().getString(
							R.string.operation_copy);
					File fatherFile = new File(key);
					String fatherPerantPath = fatherFile.getParent();
					uFile = new File(key.replaceFirst(fatherPerantPath,
							operationEntity.goalPath));
					if (uFile.getPath().indexOf(fatherFile.getPath()) > -1
							&& operationEntity.state
									.equals(operationStates.CUTPASTE)) {
						Message updateuimsg = new Message();
						updateuimsg.what = ERROR;
						mHandler.sendMessage(updateuimsg);
						continue;
					}
					ue.from_file = fatherFile.getName();
					ue.goto_file = goalFile.getName();

					ArrayList<String> pathes = pathMap.get(key);
					for (int k = 0; k < pathes.size(); k++) {
						String path = pathes.get(k);
						if (isCancel) {
							break;
						}
						ue.operationPath = path;
						String newPath = path.replaceFirst(fatherPerantPath,
								operationEntity.goalPath);
						File fileNew = new File(newPath);
						File file = new File(path);
						Message updateuimsg = new Message();
						updateuimsg.what = UPDATEUI;
						mHandler.sendMessage(updateuimsg);
						File newFile = null;
						if (file.isDirectory()) {
							if (!fileNew.exists()) {
								new File(newPath).mkdir();
							}
//							else{
//								for (int j = 0; j < 100000; j++) {
//									newPath = fileNew.getParent() + File.separator + "复件"
//											+ (i == 0 ? "" : "(" + i + ")")
//											+ fileNew.getName();
//									// 如果newPath的文件不存在就break;
//									 newFile= new File(newPath);
//									if (!newFile.exists()) {
//										break;
//									}
//								}
//								newFile.mkdir();
//							}
							// yu
						} else {
							if (!copyFile(path, newPath, operationEntity, replace)) {
								break;
							}
							scanFile(newPath);
							/*Log.e("targetFile","path:" + path);
							Log.e("targetFile","newPath:" + newPath);*/
						}
					}
					// if(operationEntity.state.equals(operationStates.COPYPASTE)){
					// Log.e("ww", "wwwwwwwwwwww");
					//
					// }
					// TODO ����������list
					if (uFile.exists()) {
						// uFile=new File("/mnt/ext_sdcard1/eee/副本-rr");
						// Log.e("副本", ""+uFile.getAbsolutePath());
						operationEntity.adapter.addItems(new File[] { uFile });
						
						//打印出来是目标目录
						Log.e("", "" + uFile.getAbsolutePath());
						
						Message updatelistmsg = new Message();
						updatelistmsg.what = UPDATELIST;
						mHandler.sendMessage(updatelistmsg);
					}
				}
			}
		}
		ue = new updateUIEntity();
		Message msg = new Message();
		msg.what = CLOSE;
		mHandler.sendMessage(msg);
		if (operationEntity.state.equals(operationStates.CUTPASTE)) {
			operationEntity.operationPaths.clear();
		} else if (operationEntity.state.equals(operationStates.DELETE)) {
			operationEntity.operationPaths.clear();
		}
	}
	private void scanFile(String path){
		/*Intent intent_mount = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		intent_mount.setData(Uri.fromFile(new File(path)));
		sendBroadcast(intent_mount);*/
		getContext().sendBroadcast(new Intent("yecon.intent.action.MEDIA_RESCAN", Uri.parse("file://" + path)));
	}
	/**
	 * 
	 * @Description:ɨ�赽�ڴ���
	 * @Author: wanghb
	 * @Email: wanghb@foryouge.com.cn
	 * @param scanPaths
	 * @Others:
	 */
	private static HashMap<String, ArrayList<String>> scanPath(
			ArrayList<String> scanPaths) {
		if (scanPaths != null) {
			HashMap<String, ArrayList<String>> retMap = new HashMap<String, ArrayList<String>>();
			for (String scanPath : scanPaths) {
				if (isCancel) {
					return retMap;
				}
				ArrayList<String> scanedPath = new ArrayList<String>();
				scanedPath.add(scanPath);
				if (new File(scanPath).isDirectory()) {
					getFiles(scanPath, scanedPath);
				}
				retMap.put(scanPath, scanedPath);
			}
			return retMap;
		}
		return null;
	}

	private static void getFiles(String path, ArrayList<String> retLst) {
		File f = new File(path);
		File[] files = f.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File ff = files[i];
				retLst.add(ff.getPath());
				if (ff.isDirectory()) {
					getFiles(ff.getPath(), retLst);
				}
			}
		}
	}

	public static double getDirSize(File file) {
		// 判断文件是否存在
		double size = 0;
		if (file.exists()) {
			// 如果是目录则递归计算其内容的总大小
			if (file.isDirectory()) {
				File[] children = file.listFiles();
				size = 0;
				for (File f : children)
					size += getDirSize(f);
				return size;
			} else {// 如果是文件则直接返回其大小,以“兆”为单位
				size = (double) file.length() / 1024 / 1024;
				return size;
			}
		}
		return size;
	}

	private boolean copyFile(String d_old, String d_new,
			OperationEntity oe) {
		return copyFile(d_old, d_new, oe, false);
	}
	private boolean copyFile(String d_old, String d_new,
			OperationEntity oe, boolean replace) {

		File sourceFile = new File(d_old);
		File lsFile = new File("/mnt/sdcard/tmplswj");
		File targetFile = new File(d_new);
		String newPath = "";
		File newFile = null;
		// Runtime.getRuntime().exec("cp " + lsFile.getAbsolutePath() +
		// " /....");
		Log.e("old", ""+d_old);
		
		if (targetFile.exists()) {
			if(true){
				if(replace){
					OperationEntity del = new MyUtil.OperationEntity();
					delFile(targetFile,del);
				}else
					return true;
			}
			else{
				try {
					lsFile.createNewFile();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				for (int i = 0; i < 100000; i++) {
					newPath = sourceFile.getParent() + File.separator + "复件"
							+ (i == 0 ? "" : "(" + i + ")")
							+ sourceFile.getName();
					// 如果newPath的文件不存在就break;
					newFile = new File(newPath);
					if (!newFile.exists()) {
						break;
					}
				}
				try {
					// 拷到临时文件夹中
					newFile.createNewFile();
					try {
						FileInputStream input = new FileInputStream(sourceFile);
						BufferedInputStream inBuff = new BufferedInputStream(
								input);
						Log.e("targetFile12", targetFile.getAbsolutePath());
						// �½��ļ���������������л���
						FileOutputStream output = new FileOutputStream(lsFile);
						Log.e("targetFile2", targetFile.getAbsolutePath());
						BufferedOutputStream outBuff = new BufferedOutputStream(
								output);

						// ��������
						byte[] b = new byte[1024 * 8];
						int len;
						while ((len = inBuff.read(b)) != -1) {
							if (isCancel) {
								break;
							}
							outBuff.write(b, 0, len);
						}
						// ˢ�´˻���������
						outBuff.flush();

						// �ر���
						inBuff.close();
						outBuff.close();
						output.close();
						input.close();

						if (isCancel) {
							return false;
						}
					} catch (Exception e) {
						oe.returnMsgs.add(d_old + "���Ʋ��ɹ���");
					}

				} catch (IOException e1) {
					e1.printStackTrace();
				}}
			}
		// 拷到目标目录
		try {
			FileInputStream input;
			BufferedInputStream inBuff;
			FileOutputStream output;
			if (targetFile.exists()) {
				input = new FileInputStream(lsFile);
				inBuff = new BufferedInputStream(input);
				output = new FileOutputStream(newFile);
			} else {
				input = new FileInputStream(sourceFile);
				inBuff = new BufferedInputStream(input);
				output = new FileOutputStream(targetFile);
			}

			Log.e("targetFile2", targetFile.getAbsolutePath());
			BufferedOutputStream outBuff = new BufferedOutputStream(output);

			byte[] b = new byte[1024 * 8];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				if (isCancel) {
					break;
				}
				outBuff.write(b, 0, len);
			}

			outBuff.flush();

			inBuff.close();
			outBuff.close();
			output.close();
			input.close();
			lsFile.delete();
			if (isCancel) {
				return false;
			}
		} catch (Exception e) {
			oe.returnMsgs.add(d_old + "���Ʋ��ɹ���");
		}
		Log.d("TEST","copyFile "+oe.state+d_old);
		if (oe.state.equals(operationStates.CUTPASTE)) {
			delFile(new File(d_old), new OperationEntity());
		}
		return true;
	}

	/**
	 * ɾ���ļ�
	 * 
	 * @param file
	 * @return
	 */
	private boolean delFile(File f, OperationEntity oe) {
		try {
			if (f.exists()) {
				if (f.isDirectory()) {
					if (isCancel) {
						return false;
					}
					File[] files = f.listFiles();
					for (int i = 0; i < files.length; i++) {
						if (isCancel) {
							return false;
						}
						ue.operationPath = files[i].getPath();
						Message msg = new Message();
						msg.what = UPDATEUI;
						mHandler.sendMessage(msg);
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
		return true;
	}

	private static class updateUIEntity {

		public String title;
		public String from_file;
		public String goto_file;
		public String operationPath;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			isCancel = true;
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
