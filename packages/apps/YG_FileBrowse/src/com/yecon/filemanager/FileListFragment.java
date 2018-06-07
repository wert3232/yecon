package com.yecon.filemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hcn.filetest.TestActivity;
import com.yecon.filemanager.FileManagerApp.FileInfoManager;
import com.yecon.filemanager.FileOperator.IProgressListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.PopupMenu;
import android.widget.Toast;
import net.micode.fileexplorer.IntentBuilder;
import net.micode.fileexplorer.Util;

/**
 * Created by chenchu1986 on 12/14/14.
 */

public class FileListFragment extends Fragment implements View.OnLongClickListener {
	public static final String Tag = "file list fragment";

	public static final String ACTION_FINISHED = "activity destroyed";

	private boolean isInputActivityResume = false;

	public static final int MSG_LOADER_FINISHED = 0;

	public static final int LIST_MODE = 0;
	public static final int GRID_MODE = 1;

	public static int AppDefaultViewMode = LIST_MODE;
	public static int CustomTempViewMode = LIST_MODE;
	public static int CustomPersistViewMode = LIST_MODE;

	private static final int[] viewStubId = { R.id.fragment_list_list_stub,
			R.id.fragment_list_grid_stub };

	private int mFragmentCurrViewMode = LIST_MODE;

	public int getFragmentCurrViewMode() {
		return mFragmentCurrViewMode;
	}

	private String mLocation;

	// private List<FileInfo> mLocalFileInfos = new ArrayList<FileInfo>();

	private FileGridViewAdapter mGridViewAdapter;

	private FileListViewAdapter mListViewAdapter;

	private FileFragmentViewRoot mViewRoot;

	private List<ListViewItemHolder> mSelectedViews = new ArrayList<ListViewItemHolder>();

	private boolean mLoadFinished = false;

	private boolean mNeedLoad = true;

	private boolean mNewFolder = false;

	private FileInfo mLongPressedInfo;

	private FileInfoManager mInfoManager;

	public String getLocation() {
		return mLocation;
	}

	private ProgressDialog mPd;

	private ProgressDialog onProgressDialogBuild(int msgId) {
		Activity parent = getActivity();
		ProgressDialog pd;
		boolean isCancelable = false;

		if (msgId == R.string.dialog_progress_delete) {
			isCancelable = true;
		}

		if (msgId != R.string.dialog_progress_paste) {
			pd = ProgressDialog.show(parent, getActivity().getString(msgId), "", true,
					isCancelable);
			if (msgId == R.string.dialog_progress_delete) {
				pd.setOnDismissListener(mOnDeleteDismissListener);
			}
			return pd;
		}
		pd = new ProgressDialog(parent, ProgressDialog.STYLE_HORIZONTAL);
		pd.setTitle(parent.getString(msgId));
		pd.setIndeterminate(false);
		pd.setCancelable(false);
		pd.setCanceledOnTouchOutside(false);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setProgressNumberFormat(null);
		pd.setMessage(getString(R.string.dialog_caculating));
		return pd;
	}

	public void onSearch(String name) {
		new FileSearcher().execute(new String[] { name });
	}

	private final class FileSearcher extends AsyncTask<String, Void, Boolean> {

		Map<String, List<String>> mResult = new HashMap<String, List<String>>();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String name = params[0];
			File sd1 = new File(FileStorageStateListener.pathSD1);
			FileOperator.searchFile(mResult, sd1, name);
			File sd2 = new File(FileStorageStateListener.pathSD2);
			FileOperator.searchFile(mResult, sd2, name);

			File usb1 = new File(FileStorageStateListener.pathUSB1);
			FileOperator.searchFile(mResult, usb1, name);
			File usb2 = new File(FileStorageStateListener.pathUSB2);
			FileOperator.searchFile(mResult, usb2, name);
			File usb3 = new File(FileStorageStateListener.pathUSB3);
			FileOperator.searchFile(mResult, usb3, name);
			File usb4 = new File(FileStorageStateListener.pathUSB4);
			FileOperator.searchFile(mResult, usb4, name);
			File usb5 = new File(FileStorageStateListener.pathUSB5);
			FileOperator.searchFile(mResult, usb5, name);
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			super.onPostExecute(aBoolean);
			if (mResult.size() == 0) {
				Log.d(Tag, "search size is 0");
			} else {
				Set<String> keySet = mResult.keySet();
				Iterator<String> iterator = keySet.iterator();
				while (iterator.hasNext()) {
					String name = iterator.next();
					List<String> list = mResult.get(name);
					int size = list.size();
					for (int i = 0; i < size; ++i) {
						Log.d(Tag, "{" + name + " : " + list.get(i) + "}");
					}
				}
			}
		}
	}

	private static final class ProgressInfo {
		String name;
		int current;
		static int max;

		private static ProgressInfo info;

		static ProgressInfo getInstance() {
			if (info == null) {
				info = new ProgressInfo();
			}
			return info;
		}
	}

	public void onDeletion() {
		// new FileDeleter(mLocation,true).execute(mInfoManager.get(mLocation));
		setDeleter(new FileDeleter(mLocation, true));
		// mTester = new FileTester();
		// mTester.execute();
	}

	private FileDeleter mDeleter;

	public void setDeleter(FileDeleter deleter) {
		mDeleter = deleter;
		if (mDeleter != null) {
			mDeleter.execute(mInfoManager.get(mLocation));
		}
	}

	boolean isInDelete = false;

	public boolean cancelDelete() {
		boolean result = false;
		if (mDeleter != null) {
			result = mDeleter.cancel(true);
		}
		/*
		 * if (mTester != null) { result = mTester.cancel(true); }
		 */
		return result;
	}

	/*
	 * private FileTester mTester; private class FileTester extends
	 * AsyncTask<Void, Void, Void> { long i = 0; ProgressDialog pd;
	 * 
	 * @Override protected void onPreExecute() { // TODO Auto-generated method
	 * stub super.onPreExecute(); isInDelete = true; pd =
	 * onProgressDialogBuild(R.string.dialog_progress_delete); pd.show(); }
	 * 
	 * @Override protected Void doInBackground(Void... params) { // TODO
	 * Auto-generated method stub while(true) { if (isCancelled()) { return
	 * null; } i++; if (i == Long.MAX_VALUE>>1) { return null; } } }
	 * 
	 * @Override protected void onCancelled() { // TODO Auto-generated method
	 * stub Toast.makeText(FileListFragment.this.getActivity(),
	 * "CancelledKeyException counter is"+i,Toast.LENGTH_LONG).show();
	 * isInDelete = false; if (pd != null && pd.isShowing()) { pd.dismiss(); } }
	 * 
	 * @Override protected void onPostExecute(Void result) { // TODO
	 * Auto-generated method stub super.onPostExecute(result);
	 * Toast.makeText(FileListFragment.this.getActivity(),"post counter is"
	 * +i,Toast.LENGTH_LONG).show(); isInDelete = false; } }
	 */

	private DialogInterface.OnDismissListener mOnDeleteDismissListener = new DialogInterface.OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {
			// TODO Auto-generated method stub
			if (isInDelete) {
				if (cancelDelete()) {
					Log.d(Tag, "cancel delete done");
				} else {
					Log.d(Tag, "cancel delete fail");
				}
			}
		}
	};

	private void deleteEntryInDB(List<String> path) {
		int size = path.size();
		String[] paths = new String[size];
		path.toArray(paths);
		String selection = MediaStore.Images.Media.DATA + " = ? ";
		String sel = "";
		for (int i = size; i > 0; i--) {
			sel += selection;
			if (i != 1) {
				sel += " OR ";
			}
		}

		ContentResolver resolver = getActivity().getContentResolver();

		ContentProviderOperation.Builder builder = ContentProviderOperation
				.newDelete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		ContentProviderOperation operation = builder.withSelection(sel, paths).build();

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		ops.add(operation);
		try {
			// authority?????????
			resolver.applyBatch(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getAuthority(), ops);
		} catch (OperationApplicationException e) {
			Log.d(Tag, e.toString());
		} catch (RemoteException e) {
			Log.d(Tag, e.toString());
		}

		// int rowsDeleted =
		// resolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,sel,paths);
		// Log.d(Tag, "delete database rows of "+rowsDeleted);
	}

	private void deleteEntryInDB(String path) {
		ContentResolver resolver = getActivity().getApplicationContext().getContentResolver();
		String[] paths = new String[] { path };
		String selection = MediaStore.Images.Media.DATA + " = ? ";
		resolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, paths);
	}

	private void deleteEntryInDB_video(String path) {
		ContentResolver resolver = getActivity().getApplicationContext().getContentResolver();
		String[] paths = new String[] { path };
		String selection = MediaStore.Video.Media.DATA + " = ? ";
		resolver.delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, selection, paths);
	}

	private void deleteEntryInDB_audio(String path) {
		ContentResolver resolver = getActivity().getApplicationContext().getContentResolver();
		String[] paths = new String[] { path };
		String selection = MediaStore.Audio.Media.DATA + " = ? ";
		resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selection, paths);
	}

	// TODO: NOTIFICATION
	/*
	 * private class FileNotificationManager{ private NotificationCompat.Builder
	 * mDeleteBuilder; private static NotificationManager manager;
	 * 
	 * private static final int deleteId = 0x1111;
	 * 
	 * private static final int pasteId = 0x1112;
	 * 
	 * private static FileNotificationManager;
	 * 
	 * static void setFileNotificationManager(Context context) { mContext =
	 * context; }
	 * 
	 * public static synchronized FileNotificationManager
	 * getFileNotificationManager() { if (mContext != null) { manager =
	 * (NotificationManager)
	 * mContext.getSystemService(Context.NOTIFICATION_SERVICE); } }
	 * 
	 * public NotificationCompat.Builder startDeleteNotification() {; if
	 * (mDeleteBuilder == null) { mDeleteBuilder = new
	 * NotificationCompat.Builder(mContext);
	 * mDeleteBuilder.setSmallIcon(R.drawable.ic_launcher)
	 * .setContentTitle(mContext.getString(R.string.app_name))
	 * .setContentText(mContext.getString(R.string.dialog_progress_delete))
	 * .setProgress(0,0,true); } manager.notify(deleteId,
	 * mDeleteBuilder.build()); return mDeleteBuilder; }
	 * 
	 * public NotificationCompat.Builder finishDeleteNotification(boolean done){
	 * if (mDeleteBuilder != null) { String result = done?
	 * mContext.getString(R.string.) } } }
	 */

	private final class FileDeleter extends AsyncTask<List<FileInfo>, ProgressInfo, Boolean> {

		private static final int sNId = 0x1111;

		List<FileInfo> mInfos = new ArrayList<FileInfo>();
		private ProgressDialog pd = null;

		List<String> mFilePaths = new LinkedList<String>();

		private String mDeleteLocation;
		private boolean mIsPdShow;

		public FileDeleter(String location, boolean isPdShow) {
			mDeleteLocation = location;
			mIsPdShow = isPdShow;
		}

		private IProgressListener mListener = new IProgressListener() {

			@Override
			public boolean onProgressUpdate(File file, long length) {
				// TODO Auto-generated method stub
				return FileDeleter.this.isCancelled();
			}

			@Override
			public void onAddNewFile(String old, String newPath) {
				// TODO Auto-generated method stub
				// mFilePaths.add(path);
				String ext = Util.getExtFromFilename(Util.getNameFromFilepath(newPath));
				String type = null;
				type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
				if (type == null) {
					if (MyMediaTools.isVideoFileType(ext)) {
						type = "video";
					}else if (MyMediaTools.isAudioFileType(ext)) {
						type = "audio";
					}else if (MyMediaTools.isImageFileType(ext)) {
						type = "image";
					}
				}else {
					if (ext.equalsIgnoreCase("rm")) {
						type = "video";
					}
				}
				
				if (type != null) {
					if (type.startsWith("image")) {
						// Log.d(Tag, "send scann file");
						FileListFragment.this.deleteEntryInDB(newPath);
					} else if (type.startsWith("audio")) {
						FileListFragment.this.deleteEntryInDB_audio(newPath);
					} else if (type.startsWith("video")) {
						FileListFragment.this.deleteEntryInDB_video(newPath);
					}
				}
			}
		};

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (mIsPdShow) {
				pd = onProgressDialogBuild(R.string.dialog_progress_delete);
			}

			isInDelete = true;

			Log.d(Tag, "pre execute");
		}

		private boolean isAndroidSecureIn = false;

		@Override
		protected Boolean doInBackground(List<FileInfo>... fileInfos) {
			Log.d(Tag, "file deleter do in background");
			List<FileInfo> infos = fileInfos[0];
			boolean result = true;
			ProgressInfo.max = mInfoManager.getSelectedCount(mDeleteLocation);
			int i = 0;
			for (FileInfo info : infos) {
				if (info.isSelected) {
					if (info.filePath.equals(Util.ANDROID_SECURE)) {
						isAndroidSecureIn = true;
						result = false;
						break;
					}
					ProgressInfo pinfo = ProgressInfo.getInstance();
					pinfo.name = info.fileName;
					pinfo.current = i;
					publishProgress(pinfo);
					if (!FileOperator.deleteFile(new File(info.filePath), mListener)) {
						result = false;
						break;
					}
					Log.d(Tag, "add info");
					mInfos.add(info);
					++i;
				}
			}

			for (FileInfo info : mInfos) {
				mInfoManager.removeFileInfo(mLocation, info);
				Log.d(Tag, "remove");
			}

			return result;
		}

		@Override
		protected void onProgressUpdate(ProgressInfo... values) {
			ProgressInfo pinfo = values[0];

			pd.setMax(pinfo.max);
			pd.setMessage(pinfo.name);
			pd.setProgress(pinfo.current);
			if (!pd.isShowing()) {
				pd.show();
			}
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			super.onPostExecute(aBoolean);

			onPostProcess();

			if (!aBoolean) {
				if (isAndroidSecureIn) {
					showIllegalDialog(TYPE_ERROR_PASTE_ANDROIDSECURE);
				}
			}
			//
			Log.d(Tag, "post execute");
		}

		@Override
		protected void onCancelled(Boolean result) {
			// TODO Auto-generated method stub
			if (!result) {
				Log.d(Tag, "on cancel");
				// mInfoManager.selectAll(mLocation, false);
				onPostProcess();
				Toast.makeText(FileListFragment.this.getActivity(),
						getString(R.string.dialog_delete_abort), Toast.LENGTH_LONG).show();
			}
		}

		private void onPostProcess() {
			/*
			 * for(FileInfo info:mInfos) {
			 * mInfoManager.removeFileInfo(mLocation,info); Log.d(Tag,"remove");
			 * }
			 */
			mInfoManager.selectAll(mLocation, false);
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}
			mHandler.sendEmptyMessage(FileListFragment.MSG_LOADER_FINISHED);

			// update database
			// deleteEntryInDB(mFilePaths);

			isInDelete = false;
			mDeleter = null;
		}
	}

	public void onPaste(boolean isRemoved) {
		String root = null;
		if (getActivity() instanceof IFileListFragmentOp) {
			root = ((IFileListFragmentOp) getActivity()).getMountPoint();
		}
		new FilePaster(getActivity().getApplicationContext(), mLocation, root, isRemoved)
				.execute(mInfoManager.getCopyedInfos());
	}

	private boolean isPasteLegal(String dest, List<String> src) {
		int size = src.size();
		for (int i = 0; i < size; ++i) {
			String path = src.get(i);
			if (Util.containsPath(path, dest)) {
				return false;
			}
		}
		return true;
	}

	private boolean isWritableDest(String dest) {
		File file = new File(dest);
		if (!file.exists() || !file.canWrite()) {
			return false;
		}
		return true;
	}

	private boolean isAndroidSecureIn(List<String> src) {
		for (String path : src) {
			if (path.equals(Util.ANDROID_SECURE)) {
				return true;
			}
		}
		return false;
	}

	private final class FilePaster extends AsyncTask<List<String>, ProgressInfo, Boolean> {

		final String mDst;
		final boolean removed;
		boolean isContained = false;
		boolean isNospace = false;
		boolean isCutToSameDir = false;
		boolean isAndroidSecureIn = false;
		boolean isNotWritable = false;
		ArrayList<String> mPaths = new ArrayList<String>();
		// List<String> mFilePaths = new LinkedList<String>();
		ArrayList<String> mInfos = new ArrayList<String>();
		private ProgressDialog pd = null;

		private String mRoot;

		private Context mContext;

		// private MediaScannerConnection mScanner;

		public FilePaster(Context context, String dst, String root, boolean isRemoved) {
			super();
			mContext = context;
			// mScanner = new MediaScannerConnection(mContext,null);
			mDst = dst;
			removed = isRemoved;
			mRoot = root;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = onProgressDialogBuild(R.string.dialog_progress_paste);
			if (!pd.isShowing()) {
				pd.show();
			}
			/*
			 * if (!mScanner.isConnected()) { mScanner.connect(); }
			 */
			Log.d(Tag, "pre execute" + pd.toString());
		}

		private long part = 0;
		private int mi = 0;
		private Object mlock = new Object();

		long total = 0;
		long space = 0;

		@Override
		protected Boolean doInBackground(List<String>... fileInfos) {
			Log.d(Tag, "file copyer do in background");
			List<String> infos = fileInfos[0];

			if (!isWritableDest(mLocation)) {
				isNotWritable = true;
				return false;
			}

			if (!isPasteLegal(mLocation, infos)) {
				isContained = true;
				return false;
			}

			if (isAndroidSecureIn(infos)) {
				isAndroidSecureIn = true;
				return false;
			}

			if (removed) {
				String pathString = Util.getPathFromFilepath(infos.get(0));
				Log.d(Tag, "path is" + pathString + ":" + mDst);
				if (mDst.equals(pathString)) {
					isCutToSameDir = true;
					return false;
				}
			}

			int msize = infos.size();
			Log.d(Tag, "msize :" + msize);

			int i = 0;
			File[] files = new File[msize];
			for (i = 0; i < msize; i++) {
				files[i] = new File(infos.get(i));
			}
			space = FileOperator.getFilesTotalSpace(files, true);
			Log.d(Tag, "space " + Util.convertStorage(space));
			Log.d(Tag, "root is " + mRoot);

			ProgressInfo.max = 10000;// infos.size();

			long items = 0;
			for (String info : infos) {
				items += FileOperator.getFileCount(info);
			}
			Log.d(Tag, "item is" + items);
			if (items == 0) {
				items = 1;
			}
			part = (int) (((float) ProgressInfo.max) / items);

			Log.d(Tag, "part is" + part);

			if (space > 0) {
				long freespace = Util.getCardInfo(mRoot).free;
				Log.d(Tag, "" + Util.convertStorage(freespace));
				if (space >= freespace) {
					isNospace = true;
					return false;
				}
			}
			// TODO:if space < 0 or space = 0 all dirs if set exclude dir to
			// false

			for (String info : infos) {

				String path = FileOperator.pasteFile(info, mDst,
						new FileOperator.IProgressListener() {
							@Override
							public boolean onProgressUpdate(File file, long length) {
								// Log.d(Tag,"mbuffer"+length);
								long mlength = file.length();
								ProgressInfo pinfo = ProgressInfo.getInstance();

								pinfo.current = (int) ((((double) (total + length)) / space)
										* (ProgressInfo.max));

								if (length < mlength) {
									// pinfo.current = (int) (part * (mi +
									// ((float) length) / mlength));

								} else {
									// mi++;
									// pinfo.current = (int) (part * mi);
									total += mlength;

								}
								// Log.d(Tag, "current :" + pinfo.current+" file
								// :"+file.getName()+ " mi :"+mi+ " length
								// :"+length+" size : "+mlength);
								pinfo.name = file.getName();

								publishProgress(pinfo);
								return true;
							}

							@Override
							public void onAddNewFile(String old, String newPath) {
								// TODO Auto-generated method stub
								// mFilePaths.add(path);
								/*
								 * if (path != null) { if (mScanner != null){ if
								 * (!mScanner.isConnected()) {
								 * mScanner.connect(); } mScanner.scanFile(path,
								 * Util.imageMime); } }
								 */
								// String[] paths = new String[]{path};
								// MediaScannerConnection.scanFile(FileListFragment.this.getActivity(),
								// paths, Util.images, null);

								if (mContext != null && newPath != null) {
									String ext = Util.getExtFromFilename(Util.getNameFromFilepath(newPath));
									Log.d(Tag, "ext is" + ext);
									String type = null;
									type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
									if (type == null) {
										if (MyMediaTools.isVideoFileType(ext)) {
											type = "video";
										}else if (MyMediaTools.isAudioFileType(ext)) {
											type = "audio";
										}else if (MyMediaTools.isImageFileType(ext)) {
											type = "image";
										}
									}else {
										if (ext.equalsIgnoreCase("rm")) {
											type = "video";
										}
									}

									if (type != null) {
										if (type.startsWith("image") || type.startsWith("audio")
												|| type.startsWith("video")) {

											Log.d(Tag, "send scann file");

											String[] paths = new String[] { newPath };
											MediaScannerConnection.scanFile(mContext, paths, null,
													null);

											if (removed && old != null) {
												if (type.startsWith("image")) {
													FileListFragment.this.deleteEntryInDB(old);
												} else if (type.startsWith("audio")) {
													FileListFragment.this
															.deleteEntryInDB_audio(old);
												} else if (type.startsWith("video")) {
													FileListFragment.this
															.deleteEntryInDB_video(old);
												}
											}
										}
									}
								}
							}
						});

				// Log.d(Tag,"paster new path is"+path);
				if (path == null) {
					return false;
				}
				/*
				 * if(removed) { mInfos.add(info); }
				 */
				mPaths.add(path);

				i++;
			}

			// update database

			// int size = mFilePaths.size();
			// String[] paths = new String[size];
			// mFilePaths.toArray(paths);
			// seems a potential bug here triggered by scanning while the
			// application terminated without absolutely unbinding the service
			// not handle well here
			// MediaScannerConnection.scanFile(getActivity().getApplicationContext(),
			// paths, Util.images, null);

			if (removed)

			{
				for (File file : files) {
					FileOperator.deleteFile(file, null);
				}
				// deleteEntryInDB(infos);
			}

			if (pd.getProgress() != ProgressInfo.max) {
				ProgressInfo in = ProgressInfo.getInstance();
				in.current = ProgressInfo.max;
				publishProgress(in);
			}
			return true;
		}

		@Override
		protected void onProgressUpdate(ProgressInfo... values) {
			ProgressInfo pinfo = values[0];
			pd.setMax(pinfo.max);
			pd.setMessage(pinfo.name);
			pd.setProgress(pinfo.current);
			if (!pd.isShowing()) {
				pd.show();
			}
		}

		private void onPasteDone() {
			if (getActivity() instanceof IFileListFragmentOp) {
				IFileListFragmentOp op = (IFileListFragmentOp) getActivity();

				op.onPasteDone(true);
				Log.d(Tag, "onpaste done");
			}
		}

		/*
		 * @Override protected void onCancelled(Boolean result) { // TODO
		 * Auto-generated method stub
		 * 
		 * if (mScanner != null ) { if (mScanner.isConnected()) {
		 * mScanner.disconnect(); } mScanner = null; }
		 * 
		 * }
		 */

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			super.onPostExecute(aBoolean);
			/*
			 * for(String info:mInfos) {
			 * mInfoManager.removeFileInfo(mInfoManager.getCopyedLocation(),info
			 * ); }
			 */
			/*
			 * if (mScanner != null ) { if (mScanner.isConnected()) {
			 * mScanner.disconnect(); } mScanner = null; }
			 */
			pd.dismiss();
			onPasteDone();
			if (aBoolean) {

				for (String path : mPaths) {
					getNewFileInfo(new File(path));
				}

				// update database

				/*
				 * int size = mFilePaths.size(); String[] paths = new
				 * String[size]; mFilePaths.toArray(paths);
				 * MediaScannerConnection.scanFile(getActivity(), paths, null,
				 * null);
				 */
				//
				if (removed) {
					// update database

					mInfoManager.resetCutLocation();
				}
				boolean iscleared = mInfoManager.resetCopyedLocation();
				Log.d(Tag, iscleared ? "is cleared " : "not cleared");
				mHandler.sendEmptyMessage(FileListFragment.MSG_LOADER_FINISHED);
			} else {
				if (isNotWritable) {
					showIllegalDialog(TYPE_ERROR_PASTE_NOTWRITEABLE);
					return;
				}

				if (isContained) {
					showIllegalDialog(TYPE_ERROR_PASTE_CONTAINED);
					return;
				}
				if (isNospace) {
					showIllegalDialog(TYPE_ERROR_PASTE_NOSPACE);
					return;
				}

				if (isAndroidSecureIn) {
					showIllegalDialog(TYPE_ERROR_PASTE_ANDROIDSECURE);
					return;
				}

				if (isCutToSameDir) {
					boolean iscleared = mInfoManager.resetCopyedLocation();
					Log.d(Tag, iscleared ? "is cleared " : "not cleared");
					mHandler.sendEmptyMessage(FileListFragment.MSG_LOADER_FINISHED);
					return;
				}
			}

			Log.d(Tag, "post execute");
		}
	}

	private final static int TYPE_ERROR_PASTE_CONTAINED = 0;
	private final static int TYPE_ERROR_PASTE_NOSPACE = 1;
	private final static int TYPE_ERROR_PASTE_ANDROIDSECURE = 2;
	private final static int TYPE_ERROR_PASTE_NOTWRITEABLE = 3;

	private void showIllegalDialog(int type) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.dialog_error_title).setCancelable(false)
				.setNeutralButton(R.string.activity_input_confirm, null);
		switch (type) {
		case TYPE_ERROR_PASTE_CONTAINED:
			builder.setMessage(R.string.dialog_paste_error_contained);
			break;
		case TYPE_ERROR_PASTE_NOSPACE:
			builder.setMessage(getString(R.string.dialog_paste_error_nospace));
			break;
		case TYPE_ERROR_PASTE_ANDROIDSECURE:
			builder.setMessage(getString(R.string.dialog_paste_error_androidsecure));
			break;
		case TYPE_ERROR_PASTE_NOTWRITEABLE:
			builder.setMessage(getString(R.string.dialog_paste_error_notwritable));
			break;
		default:

		}
		builder.create().show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MainActivity.INPUT_ACTIVITY) {
			isInputActivityResume = false;
			if (resultCode == Activity.RESULT_CANCELED) {
				Log.d(Tag, "canceled");
				return;
			}
			if (resultCode == Activity.RESULT_OK) {
				String action = data.getAction();
				Uri uri = data.getData();
				Log.d(Tag, uri.toString());
				String extra = data.getStringExtra(FileOperationService.Tag);
				Log.d(Tag, "extra is" + extra);
				String oldPath = uri.getPath();
				String ext = Util.getExtFromFilename(oldPath);
				File old = new File(oldPath);
				if (old.exists()) {
					Log.d(Tag, "old exists");
				}
				if (action.equals(FileOperationService.FILE_ACTION_RENAME)) {
					File file = new File(old.getParent(),
							ext.equals("") ? extra : extra + "." + ext);
					if (old.renameTo(file)) {
						updateFileInfo(mLongPressedInfo, file);
						mHandler.sendEmptyMessage(FileListFragment.MSG_LOADER_FINISHED);
					} else {
						showServiceAlertDialog(data);
					}
					// Log.d(Tag,"result ok file action ok = "+extra);
					return;
				}
				if (action.equals(FileOperationService.FILE_ACTION_NEW_FOLDER)) {

					File file = new File(old, extra);
					Log.d(Tag, "new folder file is" + file.toString());
					Log.d(Tag, "ready to create new folder");
					if (file.mkdirs()) {
						Log.d(Tag, "make dir");
						getNewFileInfo(file);
						mHandler.sendEmptyMessage(FileListFragment.MSG_LOADER_FINISHED);
					} else {
						showServiceAlertDialog(data);
					}
					return;
				}

				Log.d(Tag, "ok");
			}
		}
	}

	private FileInfo getNewFileInfo(File file) {
		return updateFileInfo(null, file);
	}

	private FileInfo updateFileInfo(FileInfo info, File file) {
		return mInfoManager.updateFileInfo(mLocation, info, file);
	}

	// TODO: show service alert dialog
	private void showServiceAlertDialog(Intent data) {
		Log.d(Tag, "show service alert dialog");
	}

	private void toInputActivity(Intent intent) {
		startActivityForResult(intent, MainActivity.INPUT_ACTIVITY);
		isInputActivityResume = true;
	}

	public void onNewFolder() {
		toInputActivity(buildInputIntent(FileOperationService.FILE_ACTION_NEW_FOLDER, mLocation));
	}

	public void onRename() {
		toInputActivity(
				buildInputIntent(FileOperationService.FILE_ACTION_RENAME, getSelectedFilePath()));
	}

	private String getSelectedFilePath() {
		if (mLongPressedInfo == null) {
			throw new IllegalStateException("file list fragment mLongPressInfo is null");
		}
		return mLongPressedInfo.filePath;
	}

	private Intent buildInputIntent(String action, String path) {
		Intent intent = new Intent(getActivity(), FileInputActivity.class);
		intent.setAction(action);
		if (path != null) {
			intent.setData(Uri.fromFile(new File(path)));
		}
		return intent;
	}

	private void toTestActivity() {
		// if (!mLongPressedInfo.isDir) {
		Intent intent = new Intent(getActivity(), TestActivity.class);
		intent.putExtra(Tag, mLongPressedInfo.filePath);
		startActivity(intent);
		// }
	}

	private PopupMenu.OnMenuItemClickListener mPopupMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			int id = item.getItemId();
			switch (id) {
			case R.id.popup_menu_rename:
				Log.d(Tag, "popup menu rename click");
				onRename();
				break;
			//case R.id.popup_menu_info:
			//	Log.d(Tag, "info menu click");
			//	toInfoActivity();
			//	break;
			/*
			 * case R.id.popup_menu_test: toTestActivity(); break;
			 */
			default:
				Log.d(Tag, "popup menu default");
			}
			return true;
		}
	};

	private void toInfoActivity() {
		Intent intent = new Intent(getActivity(), FileDetailInfoActivity.class);
		Uri data = Uri.fromFile(new File(mLongPressedInfo.filePath));
		intent.setData(data);
		startActivity(intent);
	}

	public PopupMenu.OnMenuItemClickListener getPopupMenuItemClickListener() {
		return mPopupMenuItemClickListener;
	}

	private FileItemGestureDetector.OnListItemTouchListener mOnListItemTouchListener = new FileItemGestureDetector.OnListItemTouchListener() {
		@Override
		public boolean onDoubleTap(View view, MotionEvent e) {
			FileInfo info = (FileInfo) view.getTag();
			Log.d(Tag, info.filePath);
			if (info.isDir) {
				toFileListFragment(info.filePath);
			} else {
				toActivity(info.filePath);
			}
			Log.d(Tag, "double tapped processed");
			return false;
		}

		// TODO:when getSelectedCount == size???
		@Override
		public boolean onSignleTapConfirmed(View view) {
			Log.d(Tag, "on single tap confirmed");
			FileInfo info = (FileInfo) view.getTag();
			if (info.isSelected) {
				// view.setBackgroundColor(Color.TRANSPARENT);
				view.setBackgroundResource(R.drawable.list_n);
			} else {
				// view.setBackgroundColor(Color.BLUE);
				view.setBackgroundResource(R.drawable.list_p);
			}
			info.isSelected = !info.isSelected;

			FileOperationFragment fragment = ((MainActivity) getActivity()).getOpFragment();
			boolean isFullySelected = mInfoManager.isFullSelected(mLocation);
			fragment.setInSelected(isFullySelected);
			boolean isSelected = mInfoManager.isInSelected(mLocation);
			fragment.setState(isSelected ? FSM.OP_SELECTED : FSM.OP_UNSELECTED);
			return false;
		}

		@Override
		public boolean onLongPress(View view, MotionEvent e) {

			showPopMenu(view);
			mLongPressedInfo = (FileInfo) view.getTag();
			return false;
		}
	};

	private void showPopMenu(View v) {
		PopupMenu mPopupMenu = new PopupMenu(getActivity(), v);
		mPopupMenu.getMenuInflater().inflate(R.menu.popup_menu, mPopupMenu.getMenu());
		mPopupMenu.setOnMenuItemClickListener(mPopupMenuItemClickListener);
		mPopupMenu.show();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case (FileListFragment.MSG_LOADER_FINISHED): {
				onFinishLoading();
				break;
			}
			default:
				super.handleMessage(msg);
			}
		}
	};

	private class SimpleLoader implements Runnable {
		@Override
		public void run() {
			/*
			 * if(mLocation != null){ File dir = new File(mLocation); /* if
			 * (mFileInfos == null) { mFileInfos = new ArrayList<FileInfo>(); }
			 * else { mFileInfos.clear(); }
			 * 
			 * File[] files = dir.listFiles(); for(File file: files) {
			 * getNewFileInfo(file); } }
			 */
			if (mInfoManager.load(mLocation)) {

				// mHandler.sendEmptyMessage(FileListFragment.MSG_LOADER_FINISHED);
				mLoadFinished = true;
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						onFinishLoading();
					}
				});
			} else {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
//						if (mPd != null && mPd.isShowing()) {
//							mPd.hide();
//						}
						Toast.makeText(getActivity(), R.string.load_error, Toast.LENGTH_SHORT)
								.show();
						if (getFragmentManager().popBackStackImmediate()) {
							Log.d(Tag, "popup the load-error fragment ok");
						} else {
							Log.d(Tag, "popup the load-error fragment fail");
						}

					}
				});

			}

		}
	}

	private void onStartLoading() {
//		if (mPd == null) {
//			mPd = onProgressDialogBuild(R.string.dialog_progress_load);
//		}
//		if (!mPd.isShowing()) {
//			Log.d(Tag, "show the progressdialog");
//			mPd.show();
//		}
		new Thread(new SimpleLoader()).start();
	}

	public interface IFileListFragmentOp {
		public void isFolderEmpty(boolean isEmpty);

		public void onPasteDone(boolean result);

		public void isInSelected(boolean isIn);

		public String getMountPoint();
	}

	private void onFinishLoading() {
		if (mPd != null && mPd.isShowing()) {
			mPd.hide();
		}
		informActivity();
		refreshView();
	}

	private void informActivity() {
		if (getActivity() instanceof IFileListFragmentOp) {
			IFileListFragmentOp op = ((IFileListFragmentOp) getActivity());
			op.isFolderEmpty(mInfoManager.getCount(mLocation) == 0);
			Log.d(Tag, "informactivity");
			// op.isInSelected(mInfoManager.isFullSelected(mLocation));
		}
	}

	public static FileListFragment newInstance(String location) {
		FileListFragment fragment = new FileListFragment();
		Bundle bundle = new Bundle();
		bundle.putCharSequence(FileListFragment.Tag, location);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(Tag, "on attach");
		mLocation = getArguments().getString(FileListFragment.Tag);

		mInfoManager = ((FileManagerApp) activity.getApplication()).getFileInfoManager();
		if (mInfoManager.register(mLocation) > 1) {
			mLoadFinished = true;
			mNeedLoad = false;
			Log.d(Tag, ">>>>>1");
		}

		// setHasOptionsMenu(true);
		mFragmentCurrViewMode = FileListFragment.CustomTempViewMode;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(Tag, "on destroy");
		mInfoManager.unregister(mLocation);
		if (mPd != null && mPd.isShowing()) {
			mPd.dismiss();
		}
		if (isInputActivityResume) {
			startActivity(buildInputIntent(ACTION_FINISHED, null));
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(Tag, "on detach");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(Tag, " on stop");
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(Tag, "on start");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(Tag, "on resume");
		Log.d(Tag, "curr view mode is" + mFragmentCurrViewMode);

		if (mLoadFinished && !mNeedLoad) {
			// TODO: onresume
			mHandler.sendEmptyMessage(FileListFragment.MSG_LOADER_FINISHED);
		} else {
			Log.d(Tag, "start loading");
			onStartLoading();

		}

		// mHandler.sendEmptyMessage(FileListFragment.MSG_LOADER_FINISHED);

		// TODO:
		/*
		 * FileMenuFragment fragment = ((MainActivity)
		 * getActivity()).getMenuFragment(); if (mFragmentCurrViewMode ==
		 * FileListFragment.LIST_MODE) {
		 * fragment.toggleFileView(R.id.action_listview); } else if
		 * (mFragmentCurrViewMode == FileListFragment.GRID_MODE) {
		 * fragment.toggleFileView(R.id.action_gridview); }
		 */
		CustomTempViewMode = mFragmentCurrViewMode;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(Tag, "on create view");
		mViewRoot = (FileFragmentViewRoot) inflater.inflate(R.layout.fragment_list, container,
				false);
		mViewRoot.registerViewStubId(FileListFragment.viewStubId);
		((MainActivity) getActivity()).setCurrFragment(FileListFragment.this);

		return mViewRoot;
	}

	// TODO: need observe files

	@Override
	public void onPause() {
		super.onPause();
		Log.d(Tag, "pause");
		mNeedLoad = false;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(Tag, "on activity created");

		// Toast.makeText(getActivity(),mLocation,Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(Tag, "on destroy view");
		if (getActivity() != null) {
			MainActivity activity = (MainActivity) getActivity();
			FileOperationFragment fragment = activity.getOpFragment();
			if (fragment != null) {
				fragment.setState(FSM.OP_RESET);
			} else {
				Log.d(Tag, "getopfragment is null");
			}
		} else {
			Log.d(Tag, "getactivity is null");
		}
		mInfoManager.selectAll(mLocation, false);
	}

	public void onSwitchToGridView() {
		if (mFragmentCurrViewMode != GRID_MODE) {
			List<FileInfo> infos = resetGridView();
			mFragmentCurrViewMode = GRID_MODE;
			FileListFragment.CustomTempViewMode = mFragmentCurrViewMode;
			if (infos != null) {
				getAdapter().setInfos(infos);
			}
		}
	}

	public void onSwitchToListView() {
		if (mFragmentCurrViewMode != LIST_MODE) {
			List<FileInfo> infos = resetListView();
			mFragmentCurrViewMode = LIST_MODE;
			FileListFragment.CustomTempViewMode = mFragmentCurrViewMode;
			if (infos != null) {
				getAdapter().setInfos(infos);
			}
		}
	}

	public AbsFileItemAdapter<?> getAdapter() {
		AbsFileItemAdapter<?> adapter = null;
		if (mFragmentCurrViewMode == LIST_MODE) {
			adapter = mListViewAdapter;
			Log.d(Tag, "get listview adapter");
		}
		if (mFragmentCurrViewMode == GRID_MODE) {
			adapter = mGridViewAdapter;
			Log.d(Tag, "get gridview adapter");
		}
		return adapter;
	}

	public List<FileInfo> getInfos(boolean isInFilterMode) {
		return isInFilterMode ? getAdapter().getInfos() : mInfoManager.get(mLocation);
	}

	@Override
	public boolean onLongClick(View v) {
		if (v.getId() == R.id.empty_view_content) {
			showPopMenu(v);
		}
		return true;
	}

	private List<FileInfo> resetListView() {
		FileListView v = (FileListView) mViewRoot.inflateViewStub(R.id.fragment_list_list_stub);
		if (v.getAdapter() == null) {
			v.setEmptyViewHelper(R.id.empty_view_content, this);
			mListViewAdapter = new FileListViewAdapter(getActivity(), mOnListItemTouchListener,
					mInfoManager.get(mLocation));
			v.setAdapter(mListViewAdapter);
			
			Bundle bundle = FileListMemory.find(mLocation);
			if (bundle != null) {
				int top = bundle.getInt("top");
				int pos = bundle.getInt("pos");
				v.mListView.setSelectionFromTop(pos, top);
			}
			
			return null;
		}
		Log.d(Tag, "listview adapter is not null");
		return getAdapter().getInfos();
	}

	private List<FileInfo> resetGridView() {
		FileGridView v = (FileGridView) mViewRoot.inflateViewStub(R.id.fragment_list_grid_stub);
		if (v.getAdapter() == null) {
			v.setEmptyViewHelper(R.id.empty_view_content, this);
			mGridViewAdapter = new FileGridViewAdapter(getActivity(), mOnListItemTouchListener,
					mInfoManager.get(mLocation));
			v.setAdapter(mGridViewAdapter);
			
			Bundle bundle = FileListMemory.find(mLocation);
			if (bundle != null) {
				int top = bundle.getInt("top");
				int pos = bundle.getInt("pos");
				v.setSelection(pos);
			}
			
			return null;
		}
		Log.d(Tag, "gridview adapter is not null");
		return getAdapter().getInfos();
	}

	public List<FileInfo> refreshView() {
		Log.d(Tag, "refresh view");
		return showView(mFragmentCurrViewMode);
	}

	public List<FileInfo> showView(int viewMode) {
		switch (viewMode) {
		case LIST_MODE:
			return resetListView();
		case GRID_MODE:
			return resetGridView();
		default:
			return resetListView();
		}
	}

	
	boolean setCurScrollXY(int viewMode) {
		if (viewMode == LIST_MODE) {
			FileListView vList = (FileListView) mViewRoot.inflateViewStub(R.id.fragment_list_list_stub);
			if (vList == null) {
				return false;
			}
			
			View c = vList.mListView.getChildAt(0);
			if (c == null) {
				return false;
			}
			
			int firstVisiblePosition = vList.mListView.getFirstVisiblePosition();
			int top = c.getTop();
			
			FileListMemory.add(top, firstVisiblePosition, mLocation);
			return true;
		}else if (viewMode == GRID_MODE) {
			FileGridView vGrid = (FileGridView) mViewRoot.inflateViewStub(R.id.fragment_list_grid_stub);
			if (vGrid == null) {
				return false;
			}
			
			int firstVisiblePosition = vGrid.getFirstVisiblePosition();
			FileListMemory.add(0, firstVisiblePosition, mLocation);
			return true;
		}else {
			return false;
		}
	}
	
	private void toFileListFragment(String location) {
		Log.d(Tag, "start a new fragment for this folder");
		
		setCurScrollXY(mFragmentCurrViewMode);
		
		FileListFragment next = FileListFragment.newInstance(location);
		// next.setTargetFragment(this,0);

		getFragmentManager().beginTransaction().replace(R.id.fragment_container, next)
				.addToBackStack(location).commit();
	}

	private void toActivity(String filePath) {
		Log.d(Tag, "start a new activity for this file");
		IntentBuilder.viewFile(getActivity(), filePath);
	}

}