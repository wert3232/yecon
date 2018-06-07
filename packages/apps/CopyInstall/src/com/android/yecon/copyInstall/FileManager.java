package com.android.yecon.copyInstall;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.android.yecon.filemanager.IconifiedText;
import com.android.yecon.filemanager.IconifiedTextListAdapter;

public class FileManager extends ListActivity {
	private List<IconifiedText> directoryEntries = new ArrayList<IconifiedText>();
	private File currentDirectory = null;
	private String RootPath = "";
	private static int counter = 0;
	private final String TAG = "FileManager";
	private int mIndex = 3;

	String SDCARD_PATH = "/mnt/sdcard/";
	String EXT_SDCARD1_PATH = "/mnt/ext_sdcard1/";
	String EXT_SDCARD2_PATH = "/mnt/ext_sdcard2/";
	String UDISK1_PATH = "/mnt/udisk1/";
	String UDISK2_PATH = "/mnt/udisk2/";
	String UDISK3_PATH = "/mnt/udisk3/";
	String UDISK4_PATH = "/mnt/udisk4/";

	private void setWindowPara() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount = 0.5f;
		lp.alpha = 1.0f;
		lp.y -= 0;
		lp.width = dm.widthPixels * 2 / 3;
		lp.height = dm.heightPixels * 2 / 3;
		getWindow().setAttributes(lp);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// if (icicle != null && icicle.containsKey("currentPath")) {
		// RootPath = icicle.getString("currentPath");
		// } else {
		RootPath = "/mnt";// Environment.getExternalStorageDirectory() +
							// "/";
							// }

		currentDirectory = new File(RootPath);
		if (null != currentDirectory) {
			if (currentDirectory.isDirectory()) {
				File[] f = currentDirectory.listFiles();
				if (null != f) {
					browseToRoot();
				}
			}
		}

		this.getListView().setBackgroundColor(getResources().getColor(R.color.bgcolor));
		this.getListView().setCacheColorHint(0);
		this.getListView().setDividerHeight(0);
		GradientDrawable grad = new GradientDrawable(Orientation.LEFT_RIGHT, new int[] {
				getResources().getColor(R.color.listpressolor),
				getResources().getColor(R.color.listpressolor),
				getResources().getColor(R.color.listpressolor) });
		this.getListView().setSelector(grad);
		this.setSelection(0);

		setWindowPara();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// outState.putString("currentPath",
		// currentDirectory.getAbsolutePath());
		super.onSaveInstanceState(outState);
	}

	private int iFlag = 0;

	private void findFileByDir(String dirPath) {

		if (dirPath.contains("/.thumbnails"))
			return;

		File file = new File(dirPath);
		File[] files = file.listFiles();
		if (null != files) {
			if (files.length != 0) {
				for (int i = 0, j = files.length; i < j; i++) {
					if (files[i].isFile()) {
						iFlag++;
						break;
					} else if (files[i].isDirectory()) {
						findFileByDir(dirPath + files[i].getName() + "/");
					}
				}
			}
		}
	}

	// ����ļ�ϵͳ�ĸ�Ŀ¼
	private void browseToRoot() {
		browseTo(new File(RootPath));
	}

	// ���ָ����Ŀ¼,������ļ�����д򿪲���
	private void browseTo(final File file) {
		this.setTitle(file.getAbsolutePath());
		if (file.isDirectory()) {
			this.currentDirectory = file;
			fill(file.listFiles());
		} else {
			if (mListener != null && mIndex == 3) {
				mListener.onSetListItem(3, file.getPath());
				finish();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (null != this.currentDirectory)
			browseTo(this.currentDirectory);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	// ������һ��Ŀ¼
	private void upOneLevel() {
		String parent = this.currentDirectory.getParent();
		if (null != parent && !parent.toLowerCase().equals("/"))
			this.browseTo(this.currentDirectory.getParentFile());
	}

	// ����������Ϊ����ListActivity��Դ
	private void fill(File[] files) {
		// ����б�
		this.directoryEntries.clear();

		// ���һ����ǰĿ¼��ѡ��
		this.directoryEntries.add(new IconifiedText(getString(R.string.current_dir), getResources()
				.getDrawable(R.drawable.refresh)));
		// ����Ǹ�Ŀ¼�������һ��Ŀ¼��
		String parent = this.currentDirectory.getParent();
		if (null != parent && !parent.toLowerCase().equals("/"))
			this.directoryEntries.add(new IconifiedText(getString(R.string.up_one_level),
					getResources().getDrawable(R.drawable.uponelevel)));

		Drawable currentIcon = null;

		if (null != files) {
			// �ж��ļ����Ƿ�Ҫ���
			boolean isAddDir;
			for (File currentFile : files) {
				isAddDir = false;
				// �ж���һ���ļ��л���һ���ļ�
				if (currentFile.isDirectory()) {
					// �ж��ļ������Ƿ���ָ�����͵��ļ�
					String dirPath = currentFile.getAbsolutePath() + "/";
					if (dirPath.indexOf(SDCARD_PATH) != -1
							|| dirPath.indexOf(EXT_SDCARD1_PATH) != -1
							|| dirPath.indexOf(EXT_SDCARD2_PATH) != -1
							|| dirPath.indexOf(UDISK1_PATH) != -1
							|| dirPath.indexOf(UDISK2_PATH) != -1
							|| dirPath.indexOf(UDISK3_PATH) != -1
							|| dirPath.indexOf(UDISK4_PATH) != -1) {
						findFileByDir(currentFile.getAbsolutePath() + "/");
					}
					if (iFlag > 0) {
						currentIcon = getResources().getDrawable(R.drawable.folder);
						isAddDir = true;
						iFlag = 0;
					}
				} else if (mIndex == 3) {
					// ȡ���ļ���
					String fileName = currentFile.getName().toLowerCase();
					// ����ļ������ж��ļ����ͣ����ò�ͬ��ͼ��
					if (checkEndsWithInStringArray(fileName,
							getResources().getStringArray(R.array.fileEndingImage))) {
						currentIcon = getResources().getDrawable(R.drawable.image);
					} else if (checkEndsWithInStringArray(fileName,
							getResources().getStringArray(R.array.fileEndingWebText))) {
						currentIcon = getResources().getDrawable(R.drawable.webtext);
					} else if (checkEndsWithInStringArray(fileName,
							getResources().getStringArray(R.array.fileEndingPackage))) {
						currentIcon = getResources().getDrawable(R.drawable.packed);
					} else if (checkEndsWithInStringArray(fileName,
							getResources().getStringArray(R.array.fileEndingAudio))) {
						currentIcon = getResources().getDrawable(R.drawable.audio);
					} else if (checkEndsWithInStringArray(fileName,
							getResources().getStringArray(R.array.fileEndingVideo))) {
						currentIcon = getResources().getDrawable(R.drawable.video);
					} else if (checkEndsWithInStringArray(fileName,
							getResources().getStringArray(R.array.fileEndingApk))) {
						currentIcon = getResources().getDrawable(R.drawable.apk);
					} else {
						currentIcon = getResources().getDrawable(R.drawable.text);
					}
				}
				// ȷ��ֻ��ʾ�ļ�����ʾ·���磺/sdcard/111.txt��ֻ����ʾ111.txt
				String fileName1 = currentFile.getName().toLowerCase();
				int currentPathStringLenght = this.currentDirectory.getAbsolutePath().length();
				// if (isAddDir
				// || checkEndsWithInStringArray(fileName1, getResources()
				// .getStringArray(R.array.fileEndingImage))
				// || checkEndsWithInStringArray(fileName1, getResources()
				// .getStringArray(R.array.fileEndingAudio))
				// || checkEndsWithInStringArray(fileName1, getResources()
				// .getStringArray(R.array.fileEndingVideo))
				// || checkEndsWithInStringArray(fileName1, getResources()
				// .getStringArray(R.array.fileEndingWebText))
				// || checkEndsWithInStringArray(fileName1, getResources()
				// .getStringArray(R.array.fileEndingText))
				// || checkEndsWithInStringArray(fileName1, getResources()
				// .getStringArray(R.array.fileEndingApk))) {
				if (currentFile.isDirectory()) {
					if (isAddDir)
						this.directoryEntries.add(new IconifiedText(currentFile.getAbsolutePath()
								.substring(currentPathStringLenght), currentIcon));
				} else if (mIndex == 3) {
					this.directoryEntries.add(new IconifiedText(currentFile.getAbsolutePath()
							.substring(currentPathStringLenght + 1), currentIcon));
				}

				// }

			}
		}

		Collections.sort(this.directoryEntries);
		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
		// �������õ�ListAdapter��
		itla.setListItems(this.directoryEntries);
		// ΪListActivity���һ��ListAdapter
		this.setListAdapter(itla);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// ȡ��ѡ�е�һ����ļ���
		String selectedFileString = this.directoryEntries.get(position).getText();
		if (selectedFileString.equals(getString(R.string.current_dir)))// ˢ��
		{
			// ���ѡ�е���ˢ��
			this.browseTo(this.currentDirectory);
		} else if (selectedFileString.equals(getString(R.string.up_one_level))) {
			// ������һ��Ŀ¼
			this.upOneLevel();
		} else {
			File clickedFile = null;
			clickedFile = new File(getCurDirectory() + "/"
					+ this.directoryEntries.get(position).getText());
			this.setTitle(getCurDirectory());
			counter = 0;
			int value = stringNumbers(clickedFile.getPath());

			Intent intent = getIntent();
			mIndex = intent.getIntExtra("id", 0);
			switch (mIndex) {
			case 0:
				if (clickedFile != null && value <= 2) {
					this.browseTo(clickedFile);
				} else {
					if (mListener != null)
						mListener.onSetListItem(0, clickedFile.getPath());
					finish();
				}
				break;
			case 1:
				if (mListener != null)
					mListener.onSetListItem(1, clickedFile.getPath());
				finish();
				break;
			case 2:
				if (clickedFile != null && value <= 2) {
					this.browseTo(clickedFile);
				} else {
					if (mListener != null)
						mListener.onSetListItem(2, clickedFile.getPath());
					finish();
				}
				break;
			case 3:
				if (clickedFile != null) {
					this.browseTo(clickedFile);
				}
				break;
			case 4:
				if (clickedFile != null && value <= 2) {
					this.browseTo(clickedFile);
				} else {
					if (mListener != null)
						mListener.onSetListItem(4, clickedFile.getPath());
					finish();
				}
			default:
				break;
			}
		}
	}

	public static int stringNumbers(String str) {
		if (str.indexOf("/") == -1) {
			return 0;
		} else if (str.indexOf("/") != -1) {
			counter++;
			stringNumbers(str.substring(str.indexOf("/") + 1));
			return counter;
		}
		return 0;
	}

	// ͨ���ļ����ж���ʲô���͵��ļ�
	private boolean checkEndsWithInStringArray(String checkItsEnd, String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}

	// �õ���ǰĿ¼�ľ��·��
	public String getCurDirectory() {
		return this.currentDirectory.getAbsolutePath();
	}

	public static onListItemClickListener mListener;

	public static void setOnItemClickListener(onListItemClickListener Listener) {
		// TODO Auto-generated method stub
		mListener = Listener;
	}

	public interface onListItemClickListener {
		void onSetListItem(int id, String value);
	}
}
