package com.adnroid.file;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.drm.DrmStore.Action;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adnroid.file.FileListAdapter.ViewHolder;
import com.adnroid.file.MyUtil.OperationEntity;
import com.adnroid.file.MyUtil.operationStates;
import com.adnroid.file.PopuwindowAdapter.ViewPopHolder;
import com.android.file.PhotoActivity;
import com.android.file.R;
import com.example.libmad.NativeMP3Decoder;
public class MyActivity extends Activity {
	private static String TAG = "MyActivity";
	private GridView mGrid;

	private ListView myList;

	private LinearLayout dataLayout;

	private LinearLayout layoutOperate, layouthotseat;

	private LayoutInflater inflater;

	private TextView currentPathView;

	public static boolean isOperation = false, isGridView = true;

	public ImageView btn_play;

	private boolean ischecked;

	private Animation showAction, hideAction;

	// 这个是路径的标记
	private String currentPath;

	private FileListAdapter adapter;

	private FileListAdapter adapterSearch;

	private String[] itemPathes;

	private int operationType = -1;

	private Integer[] values;

	private CharSequence[] entries;

	private View operationView;

	private OperationDialog od;

	private ArrayList<String> tempOperationFiles = new ArrayList<String>();

	private ArrayList<String> operationFiles = new ArrayList<String>();

	private searchEntity se;

	private LinearLayout ts_layout;

	private ImageView xunhuan;

	// 异步任务停止
	private static final int Postion = 2;

	Handler stop_handler;

	public static ProgressDialog m_Dialogts = null;

	private Message stopMessage;

	private Util util;
	// 这里是标记点击图片音乐等查询是防止返回到上一步

	private static final int UPDATE = 0;

	private boolean isback = false;
	// 这是是弹出框的GridView
	private GridView lvPopupGridList;
	private PopupWindow pwMyPopWindow, pwMusicPopWindow;
	private PopuwindowAdapter popupWindowBaseAdapter;
	// PopWindow 宽高
	private static final int mWithOfPopWindow = 561;
	private static final int mHeightOfPopWindow = 350;
	// PopWindow x y 的偏移量
	private static final int xscanPopWindow = -200;
	private static final int yscanPopWindow = 0;

	// 音乐播放
	private SeekBar musicSeekBar;
	private Message musicMessage;

	private static final int SeekPostion = 1;
	private ImageView playiImageView;

	private Thread mThread;
	private short[] audioBuffer;
	private AudioTrack mAudioTrack;

	private int position = 0;
	private File audioFile;
	private int positionAvg;
	private int samplerate; // 采样率
	private int mAudioMinBufSize;
	private int ret;
	private NativeMP3Decoder MP3Decoder;
	private String filePath;
	private volatile boolean mThreadFlag;
	int m = 0;
	AudioManager am;
	// 0表示没有点击循环，1表示点击了循环
	private int xflag = 0;
	String copyPath = "";
	static String copyPath2 = "";
	File copyfilebf;
	
	private TextView mBtnNew;
	private TextView mBtnRename;
	private TextView mBtnCopy;
	private TextView mBtnCut;
	private TextView mBtnPaste;
	private TextView mBtnDel;
	private TextView mBtnCopyTo;

	private LocaleChangedBroadcastReceiver mLocaleChangedBroadcastReceiver;
	private static String a="";
	private static boolean flag = false;
	
	// private ArrayList<String> operationFiles = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		hideAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		hideAction.setDuration(500);
		showAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
		showAction.setDuration(500);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);

		Log.e("onCreate::", "onCreate");
		Handler handlerdes;
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		od = new OperationDialog(this, this);
		util = new Util(this);
		isOperation = false;
		layouthotseat = (LinearLayout) findViewById(R.id.hotseat1);
		layoutOperate = (LinearLayout) findViewById(R.id.hotseat2);
		dataLayout = (LinearLayout) findViewById(R.id.data_layout);
		currentPathView = (TextView) findViewById(R.id.current_path);
		btn_play = (ImageView) findViewById(R.id.btn_play);
		ts_layout = (LinearLayout) findViewById(R.id.ts_layout);
		
		mBtnNew = (TextView) findViewById(R.id.new_btn);
		mBtnRename = (TextView) findViewById(R.id.rename_btn);
		mBtnCopy = (TextView) findViewById(R.id.copi_btn);
		mBtnCut = (TextView) findViewById(R.id.cut_btn);
		mBtnPaste = (TextView) findViewById(R.id.paste_btn);
		mBtnDel = (TextView) findViewById(R.id.delte_btn);
		mBtnCopyTo = (TextView) findViewById(R.id.copyto_btn);

		mBtnNew.setEnabled(false);
		mBtnRename.setEnabled(false);
		mBtnCopy.setEnabled(false);
		mBtnCut.setEnabled(false);
		mBtnPaste.setEnabled(false);
		mBtnDel.setEnabled(false);
		mBtnCopyTo.setEnabled(false);
		
		// String mCardPath =
		// Environment.getExternalStorageDirectory().getPath();
		// currentPath = mCardPath;
		
		if(flag==false){
			currentPath = "/mnt";
		}else{
			currentPath = a;
			unregisterReceiver(mLocaleChangedBroadcastReceiver);
		}
		inflater = LayoutInflater.from(this);
		adapter = new FileListAdapter(this, null);
		if (savedInstanceState != null) {
			setDataLayout();
			String pt = savedInstanceState.getString("currentPath");
			if (pt != null && !"".equals(pt)) {
				currentPath = pt;
			}
			itemPathes = savedInstanceState.getStringArray("itemPathes");
			if (itemPathes != null) {
				new refushAsyncTask(this).execute("");
			}
		} else {
			setDataLayout();
			new loadAsyncTask(this).execute(currentPath);
		}
		initReceiver();
		initWindowBaseAdapter();
		initPopupWindow();
		Log.e("Oncreate====CurrentPath", currentPath);
		
		mLocaleChangedBroadcastReceiver = new LocaleChangedBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
		registerReceiver(mLocaleChangedBroadcastReceiver, intentFilter);
		
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		String language = getResources().getConfiguration().locale.getLanguage();
		InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		List<InputMethodInfo> inputMethods = mInputMethodManager.getInputMethodList();
		for(int i = 0;i < inputMethods.size(); i++){
			InputMethodInfo info = inputMethods.get(i);
			//L.i(info.getPackageName() + "  " + info.getId()); 
			if("zh".equalsIgnoreCase(language) && "com.android.inputmethod.pinyin.PinyinIME".equalsIgnoreCase(info.getServiceName())){
				//L.i("setInputMethod : com.android.inputmethod.pinyin.PinyinIME");
				mInputMethodManager.setInputMethod(null, info.getId());
				break;
			}else if(i == inputMethods.size() - 1 && "com.android.inputmethod.latin.LatinIME".equalsIgnoreCase(info.getServiceName())){
				//L.i("setInputMethod : com.android.inputmethod.latin.LatinIME");
				mInputMethodManager.setInputMethod(null, info.getId());
			}
		}
	}

	// 重写OnPause方法，使得点击Home键暂停播放
	@Override
	protected void onPause() {
		if (pwMusicPopWindow != null && pwMusicPopWindow.isShowing()) {
			mAudioTrack.stop();
			mAudioTrack.release();// 关闭并释放资源
			mThreadFlag = false;// 音频线程暂停
			mAudioTrack = null;
			MP3Decoder.closeAduioFile();
			pwMusicPopWindow.dismiss();
			pwMusicPopWindow = null;
		}
		super.onPause();
	}
	
	
	
	@Override
	protected void onStop() {
		Log.e("OnStop()", ".......");
		super.onStop();
	}
	
	//监听系统语言设置后的操作
	class LocaleChangedBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("LocaleChangedBroadcastReceiver", "dkkkkkkkkkk");
			finish();
			a = currentPath;
			flag = true;
		}
}

	// 监听返回按键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (pwMusicPopWindow != null && pwMusicPopWindow.isShowing()) {
				mAudioTrack.stop();
				mAudioTrack.release();// 关闭并释放资源
				mThreadFlag = false;// 音频线程暂停
				mAudioTrack = null;
				MP3Decoder.closeAduioFile();
				pwMusicPopWindow.dismiss();
				pwMusicPopWindow = null;
			}
			backs();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	// 监听点击Activity以外的按键
	/*
	 * @Override public boolean onTouchEvent(MotionEvent event) { if
	 * (pwMusicPopWindow != null && pwMusicPopWindow.isShowing()) { Log.e("YYY",
	 * "YYYYYU");
	 * 
	 * mAudioTrack.stop(); mAudioTrack.release();// 关闭并释放资源 mThreadFlag =
	 * false;// 音频线程暂停 MP3Decoder.closeAduioFile(); pwMusicPopWindow.dismiss();
	 * 
	 * pwMusicPopWindow = null;
	 * 
	 * } return super.onTouchEvent(event); }
	 */

	private void initWindowBaseAdapter() {
		popupWindowBaseAdapter = new PopuwindowAdapter(this, null);
		ArrayList<String> paths = util.getPaths();
		File[] files = new File[paths.size()];
		for (int i = 0; i < paths.size(); i++) {
			File f = new File(paths.get(i));
			files[i] = f;
		}
		popupWindowBaseAdapter.refurbish(files);
		int a = popupWindowBaseAdapter.getCount();
		if (a != 0) {

		}
	}

	// 在里是PopupWindow的操作
	private void initPopupWindow() {

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.task_detail_popupwindow, null);
		// View layout =
		// MyActivity.this.inflater.inflate(R.layout.task_detail_popupwindow,
		// null);
		lvPopupGridList = (GridView) layout.findViewById(R.id.myPopGrid);
		pwMyPopWindow = new PopupWindow(layout);
		pwMyPopWindow.setFocusable(true);// 加上这个popupwindow中的ListView才可以接收点击事件

		lvPopupGridList.setAdapter(popupWindowBaseAdapter);
		lvPopupGridList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				operationType = 2;
				if(!updatePasteBoard())
					return;
				ViewPopHolder itemPop = (ViewPopHolder) popupWindowBaseAdapter
						.getItem(position);
				String itemPopPath = itemPop.path;
				ViewHolder itemPopPath1 = new ViewHolder();
				itemPopPath1.path = itemPopPath;
				itemPopPath1.isDirectory = true;
				File file = new File(itemPopPath);
				itemPopPath1.label = file.getName();
				openFile(itemPopPath1, view);
				pwMyPopWindow.dismiss();
				ts_layout.setVisibility(View.GONE);
				isback = false;
				
				if (!operationFiles.isEmpty()) {
					OperationEntity oe = new MyUtil.OperationEntity();
					if (operationType == 2) {
						oe.state = operationStates.COPYPASTE;
					} else if (operationType == 3) {
						oe.state = operationStates.CUTPASTE;
					}
					oe.goalPath = itemPopPath;
					oe.operationPaths = operationFiles;
					oe.adapter = adapter;
					// oe.dataLayout=dataLayout;
					od.execute(oe);
					// MyUtil.operationFiles(oe);
				} else {
					Toast.makeText(getApplicationContext(), R.string.msg_error_nocontent,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		// 控制popupwindow的宽度和高度自适应
		lvPopupGridList.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);

		WindowManager wm = (WindowManager) getApplicationContext()
				.getSystemService(WINDOW_SERVICE);
		int w = wm.getDefaultDisplay().getWidth();
		int h = wm.getDefaultDisplay().getHeight();
		if (w == 1024 && h == 600) {
			pwMyPopWindow.setWidth(mWithOfPopWindow);
			pwMyPopWindow.setHeight(mHeightOfPopWindow);
		} else {
			pwMyPopWindow.setWidth(400);
			pwMyPopWindow.setHeight(260);
		}

		// 控制popupwindow点击屏幕其他地方消失
		pwMyPopWindow.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.equipment_bg));// 设置背景图片，不能在布局中设置，要通过代码来设置
		pwMyPopWindow.setOutsideTouchable(true);// 触摸popupwindow外部，popupwindow消失。这个要求你的popupwindow要有背景图片才可以成功，如上
	}

	public void select_equipment(View v) {
		if (pwMyPopWindow.isShowing()) {
			pwMyPopWindow.dismiss();// 关闭
		} else {
			// v这个是那个按钮
			WindowManager wm = (WindowManager) getApplicationContext()
					.getSystemService(WINDOW_SERVICE);
			int h = wm.getDefaultDisplay().getHeight();
			int w = wm.getDefaultDisplay().getWidth();
			if (w == 1024 && h == 600) {

				pwMyPopWindow.showAsDropDown(v, xscanPopWindow, yscanPopWindow);// 显示
			} else {
				pwMyPopWindow.showAsDropDown(v, -150, 0);// 显示
			}
		}
	}

	// 视频查询
	public void selectVideo(View v) {
		se = new searchEntity();
		se.type = "video";
		se.path = currentPath;
		se.isKeyworld = false;
		new searchAsyncTask(MyActivity.this).execute(se);
		isback = true;
	}

	// 图片查询
	public void selectImage(View v) {
		se = new searchEntity();
		se.type = "image";
		se.path = currentPath;
		se.isKeyworld = false;
		new searchAsyncTask(MyActivity.this).execute(se);
		isback = true;
	}

	// 音乐查询
	public void selectMusic(View v) {
		se = new searchEntity();
		se.type = "audio";
		se.path = currentPath;
		se.isKeyworld = false;
		new searchAsyncTask(MyActivity.this).execute(se);
		isback = true;
	}

	// APK查询
	public void selectAPK(View v) {
		se = new searchEntity();
		se.type = "apk";
		se.path = currentPath;
		se.isKeyworld = false;
		new searchAsyncTask(MyActivity.this).execute(se);
		isback = true;
	}

	// 异步这里是查询操作
	private class refushAsyncTask extends
			AsyncTask<String, Object, CharSequence> {

		private Context mContext;
		private Dialog mDialog;

		public refushAsyncTask(Context c) {
			mContext = c;
		}

		protected void onPreExecute() {
			mDialog = ProgressDialog.show(mContext, "11111111", "22222", true,
					false);
		}

		@Override
		protected CharSequence doInBackground(String... arg0) {
			ArrayList<File> fileArray = new ArrayList<File>();
			for (String path : itemPathes) {
				File f = new File(path);
				fileArray.add(f);
			}
			adapter.refurbish(fileArray.toArray(new File[0]));
			publishProgress();
			return null;
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			// 如果为空,即搜到的文件为空时
			super.onProgressUpdate(values);
			mDialog.dismiss();
			dataLayout.setVisibility(View.GONE);
			adapter.notifyDataSetChanged();

			dataLayout.startAnimation(AnimationUtils.loadAnimation(
					MyActivity.this, R.anim.view_anim));
			dataLayout.setVisibility(View.VISIBLE);
			currentPathView.setText(currentPath);
			setScroll();
		}
	}

	private void setScroll() {
		int count = adapter.getCount();
		if (isGridView) {
			if (count > 0) {
				mGrid.setSelection(0);
			}
		} else {
			if (count > 0) {
				myList.setSelectionFromTop(0, 0);
			}
		}
	}

	private class loadAsyncTask extends AsyncTask<String, Object, CharSequence> {
		private Context mContext;

		public loadAsyncTask(Context c) {
			mContext = c;
		}

		protected void onPreExecute() {
			currentPathView.setText(currentPath);
		}

		@Override
		protected CharSequence doInBackground(String... arg0) {
			String path = arg0[0];
			if (path.equals("/mnt")) {
				ArrayList<String> paths = util.getPaths();
				File[] files = new File[paths.size()];
				for (int i = 0; i < paths.size(); i++) {
					File f = new File(paths.get(i));
					files[i] = f;
				}
				adapter.refurbish(files);
			} else {
				File f = new File(path);
				if (f.listFiles() != null) {
					ArrayList<String> tempPthes = new ArrayList<String>();
					File[] files = f.listFiles();
					for (int i = 0; i < files.length; i++) {
						File file = files[i];
						tempPthes.add(file.getPath());
					}
					itemPathes = tempPthes.toArray(new String[0]);
				} else {
					itemPathes = null;
				}
				adapter.refurbish(f.listFiles());
			}
			publishProgress();
			// Log.e("iiii", "开始执行");
			return null;
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			super.onProgressUpdate(values);

			dataLayout.setVisibility(View.GONE);
			adapter.notifyDataSetInvalidated();
			// dataLayout.startAnimation(hideAction);
			// if(adapter.getCount()==0){
			// //dataLayout.setBackground(getResources().getDrawable(id));
			// dataLayout.setBackgroundColor(MyActivity.this.getResources().getColor(R.color.info_target_hover_tint));
			// }else{
			// dataLayout.setBackgroundColor(0);
			// }
			// 恢复背景设置
			dataLayout.setBackgroundColor(0);

			/*
			 * dataLayout.startAnimation(AnimationUtils.loadAnimation(
			 * MyActivity.this, R.anim.view_anim));
			 */
			dataLayout.setVisibility(View.VISIBLE);
			currentPathView.setText(currentPath);
			setScroll();
			// Log.e("RRR", "完成");
			if (stop_handler != null) {
				stopMessage = new Message();
				stopMessage.what = 2;
				stop_handler.sendMessage(stopMessage);
			}
		}
	}

	public class searchEntity {
		String path;
		boolean isKeyworld;
		String keyworld;
		String type = "video";
	}

	// class searchThread extends Thread{
	// ArrayList<File> fileArray = new ArrayList<File>();
	// private Context mContext;
	// private Dialog mDialog = new Dialog(getApplicationContext());
	// private Dialog mDialog2 = new Dialog(getApplicationContext());
	// private boolean mIsCanceled;
	// @Override
	// public void run() {
	// mDialog = ProgressDialog.show(mContext,
	// getResources().getString(R.string.data_load),
	// getResources().getString(R.string.data_load_msg), true,
	// false);
	// fileArray = new FileHelper().scanAllAPKFile();
	// if (fileArray != null) {
	// ArrayList<String> tempPthes = new ArrayList<String>();
	// for (int i = 0; i < fileArray.size(); i++) {
	// tempPthes.add(fileArray.get(i).getPath());
	// }
	// itemPathes = tempPthes.toArray(new String[0]);
	// } else {
	// itemPathes = null;
	// }
	// adapter.refurbish(fileArray.toArray(new File[0]));
	// super.run();
	// }
	// }

	private class searchAsyncTask extends
			AsyncTask<searchEntity, Object, CharSequence> {
		ArrayList<File> fileArray = new ArrayList<File>();
		private Context mContext;
		public Dialog mDialog = new Dialog(getApplicationContext());
		public Dialog mDialog2 = new Dialog(getApplicationContext());

		private boolean mIsCanceled;

		//
		private int a = 0;

		public searchAsyncTask(Context c) {
			mContext = c;
			mIsCanceled = false;
		}

		protected void onPreExecute() {
			mDialog = ProgressDialog.show(mContext,
					getResources().getString(R.string.data_load),
					getResources().getString(R.string.data_load_msg), true,
					false);
			mDialog.setCancelable(true);
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					searchAsyncTask.this.cancel(true);

					if (currentPath.equals("/mnt")) {
						mDialog2 = ProgressDialog.show(
								mContext,
								getResources().getString(R.string.data_quxiao),
								getResources().getString(
										R.string.data_load_msg2), true, false);
						new loadAsyncTask(MyActivity.this).execute("/mnt");

						stop_handler = new Handler() {
							@Override
							public void handleMessage(Message msg) {
								super.handleMessage(msg);
								if (msg.what == 2) {
									mDialog2.dismiss();
								}
							}
						};
					} else {
						mDialog2 = ProgressDialog.show(
								mContext,
								getResources().getString(R.string.data_quxiao),
								getResources().getString(
										R.string.data_load_msg2), true, false);

						// Log.e("llll", "执行完了");
						new loadAsyncTask(MyActivity.this).execute(currentPath);
						stop_handler = new Handler() {
							@Override
							public void handleMessage(Message msg) {
								super.handleMessage(msg);
								if (msg.what == 2) {
									mDialog2.dismiss();
								}
							}
						};
					}
				}
			});
		}

		@Override
		protected CharSequence doInBackground(final searchEntity... arg0) {
			try {
				fileArray = new FileHelper().scanAllAPKFile(arg0[0]);
				if (fileArray != null) {

					ArrayList<String> tempPthes = new ArrayList<String>();

					for (int i = 0; i < fileArray.size(); i++) {
						tempPthes.add(fileArray.get(i).getPath());
					}
					itemPathes = tempPthes.toArray(new String[0]);
				} else {
					itemPathes = null;
				}
				a = 1;
				// List<searchEntity> list=refurbish();
				/*
				 * if(!mIsCanceled) { Log.i("Test",
				 * "doInBackground  !isCancelled()");
				 */
				// adapter.refurbish(fileArray.toArray(new File[0]));
				// adapterSearch = new FileListAdapter(MyActivity.this,
				// null);
				adapter.refurbish(fileArray.toArray(new File[0]));
				/*
				 * if (isCancelled()) { //adapterSearch=adapter;
				 * //adapterSearch.refurbish(fileArray.toArray(new File[0]));
				 * itemPathes = null; return null; } else {
				 */
				// Log.e("ccc", "进来判断不为null");
				// adapter.refurbish(fileArray.toArray(new File[0]));

				publishProgress();

			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			super.onProgressUpdate(values);

			a = 2;
			/*
			 * if(!mIsCanceled){ Log.i("Test", "onProgressUpdate adapter ");
			 * adapter = adapterSearch; }
			 */
			mDialog.dismiss();
			// dataLayout.setVisibility(View.GONE);
			//
			adapter.notifyDataSetChanged();

			if (adapter.getCount() == 0) {
				ts_layout.setVisibility(View.VISIBLE);
			} else {
				dataLayout.setBackgroundColor(0);
				ts_layout.setVisibility(View.GONE);
			}
			// dataLayout.startAnimation(hideAction);
			dataLayout.setVisibility(View.VISIBLE);
		}

	}

	// 隐藏操作
	public void hideOperate(View v) {
		if(isOperation && ischecked){
			checkClick(null);
		}
		isOperation = !isOperation;
		//layoutOperate.setVisibility(isOperation ? View.VISIBLE : View.GONE);
		//layouthotseat.setVisibility(isOperation ? View.GONE : View.VISIBLE);
		adapter.notifyDataSetChanged();
	}

	// 显示操作
	public void showOperate(View v) {
		isOperation = !isOperation;
		layoutOperate.setVisibility(isOperation ? View.VISIBLE : View.GONE);
		layouthotseat.setVisibility(isOperation ? View.GONE : View.VISIBLE);
		adapter.notifyDataSetChanged();
	}

	// 返回上一步
	public void backLast(View v) {
		// 返回
		backs();

	}
	
	private void scanFile(String path){
		/*Intent intent_mount = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		intent_mount.setData(Uri.fromFile(new File(path)));
		sendBroadcast(intent_mount);*/
		sendBroadcast(new Intent("yecon.intent.action.MEDIA_RESCAN", Uri.parse("file://" + path)));
	}

	// 复制
	public void copyClick(View v) {
		operationType = 2;
		updatePasteBoard();
		isOperation = true;
		hideOperate(null);
	}

	private boolean updatePasteBoard() {
		if (tempOperationFiles.isEmpty()) {
			Toast.makeText(this, R.string.msg_error_noobject,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		operationFiles.clear();
		operationFiles.addAll(tempOperationFiles);
		return true;
	}

	// 剪切
	public void cutClick(View v) {
		operationType = 3;
		updatePasteBoard();
		isOperation = true;
		hideOperate(null);
	}

	// 张贴
	public void pasteClick(View v) {
		doPaste();
	}

	// 删除
	private void delOne(File file) {
		if (file.exists()) {
			OperationEntity oe = new MyUtil.OperationEntity();
			oe.state = operationStates.DELETE;
			oe.goalPath = currentPath;
			oe.operationPaths.add(file.getPath());
			oe.adapter = adapter;
			MyUtil.operationFiles(oe);
			scanFile(currentPath);
		}
	}

	// 删除
	public void delClick(View v) {
		Dialog comfirmdialog = new AlertDialog.Builder(MyActivity.this)
				.setTitle(getString(R.string.confirm_delete_title))
				.setMessage(getString(R.string.confirm_delete_msg))
				.setPositiveButton(getString(R.string.str_ok),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								doDelete();
								dialog.dismiss();
							}
						})
				.setNeutralButton(getString(R.string.str_cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		comfirmdialog.show();
		// tempOperationFiles.clear();
	}

	private void doDelete() {
		if (!tempOperationFiles.isEmpty()) {
			OperationEntity oe = new MyUtil.OperationEntity();
			oe.state = operationStates.DELETE;
			oe.goalPath = currentPath;
			oe.operationPaths = tempOperationFiles;
			oe.adapter = adapter;
			od.execute(oe);
			scanFile(currentPath);
		} else {
			Toast.makeText(this, R.string.msg_error_noobject,
					Toast.LENGTH_SHORT).show();
		}
		mBtnRename.setEnabled(false);
		mBtnCopy.setEnabled(false);
		mBtnCut.setEnabled(false);
		mBtnPaste.setEnabled(false);
		mBtnDel.setEnabled(false);
		mBtnCopyTo.setEnabled(false);
	}

	// 全部选中
	public void checkClick(View v) {
		isOperation = true;
		ischecked = !ischecked;
		adapter.checkAll(ischecked);
		tempOperationFiles.clear();
		if (ischecked) {
			List<ViewHolder> lst = adapter.getAllItems();
			for (ViewHolder viewHolder : lst) {
				tempOperationFiles.add(viewHolder.path);
			}
			mBtnRename.setEnabled(true);
			mBtnCopy.setEnabled(true);
			mBtnCut.setEnabled(true);
			//mBtnPaste.setEnabled(true);
			mBtnDel.setEnabled(true);
			mBtnCopyTo.setEnabled(true);
			if(tempOperationFiles.size()!=1)
				mBtnRename.setEnabled(false);
		}else{
			mBtnRename.setEnabled(false);
			mBtnCopy.setEnabled(false);
			mBtnCut.setEnabled(false);
			mBtnPaste.setEnabled(false);
			mBtnDel.setEnabled(false);
			mBtnCopyTo.setEnabled(false);
		}
		// 这里是图片转换的
		// hiddenToolbarImageView[0].setImageResource(ischecked ?
		// R.drawable.grid_check_on : R.drawable.grid_check_off);
		adapter.notifyDataSetChanged();
	}

	// listview和grideView的转换
	public void grid_listClick(View v) {
		isGridView = !isGridView;
		setDataLayout();
		adapter.notifyDataSetChanged();
	}
	
	public void rename(View v){
		/*if(operationView!=null){
			final ViewHolder vh3 = (ViewHolder) operationView.getTag();
			rename(new File(vh3.path));
		}*/
		if(tempOperationFiles.size()==1)
			rename(new File(tempOperationFiles.get(0)));
	}
	
	public void newDirOrFile(View v){
		newDirOrFile();
	}

	private void setDataLayout() {
		if (isGridView) {
			mGrid = (GridView) inflater.inflate(R.layout.grid, null);
			mGrid.setOnItemClickListener(itemClickListener);
			//mGrid.setOnItemLongClickListener(longClick);
			mGrid.setAdapter(adapter);
			// yuxiang
			dataLayout.removeAllViews();
			dataLayout.addView(mGrid);
		} else {
			myList = (ListView) inflater.inflate(R.layout.list, null);
			myList.setOnItemClickListener(itemClickListener);
			myList.setOnItemLongClickListener(longClick);
			myList.setAdapter(adapter);
			dataLayout.removeAllViews();
			dataLayout.addView(myList);
		}
	}

	public String toTime(int time) {
		time /= 1000;
		int minute = time / 60;
		// int hour = minute / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

	private void initAudioPlayer() {
		// TODO Auto-generated method stub
		samplerate = MP3Decoder.getAudioSamplerate();
		samplerate = samplerate / 2;

		// 声音文件一秒钟buffer的大小
		mAudioMinBufSize = AudioTrack.getMinBufferSize(samplerate,
				AudioFormat.CHANNEL_CONFIGURATION_STEREO,
				AudioFormat.ENCODING_PCM_16BIT);

		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, // 指定在流的类型
				// STREAM_ALARM：警告声
				// STREAM_MUSCI：音乐声，例如music等
				// STREAM_RING：铃声
				// STREAM_SYSTEM：系统声音
				// STREAM_VOCIE_CALL：电话声音

				samplerate,// 设置音频数据的采样率
				AudioFormat.CHANNEL_CONFIGURATION_STEREO,// 设置输出声道为双声道立体声
				AudioFormat.ENCODING_PCM_16BIT,// 设置音频数据块是8位还是16位
				mAudioMinBufSize, AudioTrack.MODE_STREAM);// 设置模式类型，在这里设置为流类型
	}

	private void playAudio(File f, View view) {
		// 创建Musichelper对象
		// Musichelper mhMusichelper=new Musichelper(frequency, channel,
		// sampbit);
		// 拿到当前选中的文件的绝对路径
		MP3Decoder = new NativeMP3Decoder();
		filePath = f.getAbsolutePath();

		// Log.e("aaaa:", "aaaa:" + filePath);
		ret = MP3Decoder.init(filePath, 0);// 可以控制从哪里播放、、、、、、、、、、、、、、、、、、、、、、、、、、、、、
		final int time = getTime(filePath);
		if (ret == -1) {
			Log.e(TAG, "Couldn't open file '" + filePath + "'");
		} else {
			mThreadFlag = true;
			initAudioPlayer();
			audioBuffer = new short[1024 * 1024];
			mThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						while (mThreadFlag) {
							audioFile = new File(filePath);
							positionAvg = (int) audioFile.length() / 100;
							if (mAudioTrack != null) {
								if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED
										&& mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PAUSED) {
									// ****从libmad处获取data******/

									MP3Decoder.getAudioBuf(audioBuffer,
											mAudioMinBufSize);
									mAudioTrack.write(audioBuffer, 0,
											mAudioMinBufSize);

								} else {
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
						}
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			});
			mThread.start();
			PlayMusic(); // 播放开始
		}
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.music_layout, null);
		TextView songname = (TextView) layout.findViewById(R.id.music_name);
		musicSeekBar = (SeekBar) layout.findViewById(R.id.jump_music);
		playiImageView = (ImageView) layout.findViewById(R.id.btn_play);
		xunhuan = (ImageView) layout.findViewById(R.id.xunhuan);
		pwMusicPopWindow = new PopupWindow(layout);

		// 获取屏幕宽高，设置不同情况的弹出框的大小
		WindowManager wm = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		int w = wm.getDefaultDisplay().getWidth();
		int h = wm.getDefaultDisplay().getHeight();
		// Log.e("uuuuuuuuuuuuuuuuuu", "宽度："+w+"   高度："+h);
		// 如果是1024x600
		if (w == 1024 && h == 600) {

			pwMusicPopWindow.setWidth(650);
			pwMusicPopWindow.setHeight(225);
		} else {
			pwMusicPopWindow.setWidth(520);
			pwMusicPopWindow.setHeight(205);
		}

		pwMusicPopWindow.setFocusable(true);
		// 设置弹出的popuWindows的背景色
		pwMusicPopWindow.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.popbg));
		pwMusicPopWindow.setOutsideTouchable(true);

		pwMusicPopWindow
				.setOnDismissListener(new PopupWindow.OnDismissListener() {
					@Override
					public void onDismiss() {
						pwMusicPopWindow = null;
						if (mAudioTrack != null) {
							if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED
									&& mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PAUSED)
								mAudioTrack.stop();
							mAudioTrack.release(); // 关闭并释放资源
							MP3Decoder.closeAduioFile();
							MP3Decoder = null;
							mAudioTrack = null;
						}
						mThreadFlag = false;// 音频线程暂停
					}
				});

		pwMusicPopWindow.showAtLocation(findViewById(R.id.main_layout),
				Gravity.CENTER, 0, 0);
		songname.setText(f.getName());
		// 进度条
		new Thread(new Runnable() {
			int a = 0;
			int position = 0;
			int b = time / 100;

			@Override
			public void run() {
				while (mThreadFlag) {
					if (null != mAudioTrack
							&& mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
						// int total = getTime(filePath);
						position = musicSeekBar.getProgress() * positionAvg
								+ positionAvg;
						Bundle bundle = new Bundle();
						bundle.putInt("position", position);
						musicMessage = new Message();
						musicMessage.what = SeekPostion;
						musicMessage.obj = bundle;
						musicHandler.sendMessage(musicMessage);
						//
					}
					try {
						Thread.sleep(b);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		musicSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				//
				MP3Decoder.closeAduioFile();
				// System.gc();
				ret = MP3Decoder.init(filePath, (musicSeekBar.getProgress()
						* (int) audioFile.length() / 100));// 可以控制从哪里播放

				if (ret == -1) {
					Log.e(TAG, "Couldn't open file '" + filePath + "'");
				} else {
					if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
						playiImageView.setImageResource(R.drawable.zts);
					}
					PlayMusic();
				}

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

				if (musicSeekBar.getProgress() >= 100 && xflag == 1) {
					try {
						// MP3Decoder.closeAduioFile();
						// NativeMP3Decoder mp3Decoder = new NativeMP3Decoder();

						musicSeekBar.setProgress(0);
						ret = MP3Decoder.init(
								filePath,
								(musicSeekBar.getProgress()
										* (int) audioFile.length() / 100));// 可以控制从哪里播放
						if (ret == -1) {
							Log.e(TAG, "Couldn't open file '" + filePath + "'");
						} else {
							PlayMusic();
						}
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

		});
	}

	Handler musicHandler = new Handler() {
		int progress = 1;

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SeekPostion:
				Bundle bundle = new Bundle();
				bundle = (Bundle) msg.obj;
				int position = bundle.getInt("position");
				// Log.e(TAG,
				// "position="+position+"  positionAvg=: "+positionAvg+"getTime(filePath)/1000="+getTime(filePath)/1000);
				progress = (position * 101) / (int) audioFile.length();

				musicSeekBar.setProgress(progress);
				musicSeekBar.invalidate();
				break;
			}
			super.handleMessage(msg);
		}
	};

	public void PlayMusic() {
		if (ret == -1) {
			Log.i("conowen", "Couldn't open file '" + filePath + "'");
		} else {
			if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED) {
				// mThreadFlag = true;// 音频线程开始
				// mAudioTrack.setPlaybackRate();
				mAudioTrack.play();
			} else if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
				// mThreadFlag = true;// 音频线程开始
				mAudioTrack.play();
			} else if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
				mAudioTrack.play();
			}
		}
	}

	public void PauseMusic() {
		if (ret == -1) {
			Log.i("conowen", "Couldn't open file '" + filePath + "'");
		} else {
			if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
				mAudioTrack.pause();
			}
		}
	}

	public void Xunhuan(View v) {
		if (xflag == 0) {
			// 表示循环播放
			xflag = 1;
			xunhuan.setImageResource(R.drawable.xunhuan_p);
		} else {
			xflag = 0;
			xunhuan.setImageResource(R.drawable.xunhuan_n);
		}
	}

	public void PlayPause_music(View v) {
		if (ret == -1) {
			Log.e(TAG, "Couldn't open file '" + filePath + "'");
		} else {
			if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED
					|| mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
				Message msg = new Message();
				msg.what = 1;
				handlerpt.sendMessage(msg);
				mAudioTrack.play();

			} else if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
				{
					Message msg = new Message();
					msg.what = 2;
					handlerpt.sendMessage(msg);
					// 这里会耗时，会影响播放图片的切换速度，所以新开一个线程
					new Thread() {
						public void run() {
							mAudioTrack.pause();
						};
					}.start();
				}
			}
		}
	}

	// 更新播放与暂停按钮
	Handler handlerpt = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 2) {
				playiImageView.setImageResource(R.drawable.szbf);
			} else if (msg.what == 1) {
				playiImageView.setImageResource(R.drawable.zts);
			}
			super.handleMessage(msg);
		}
	};

	public int getTime(String filepath) {
		String end = filepath.substring(filepath.lastIndexOf(".") + 1,
				filepath.length()).toLowerCase();
		// Log.e("tttttttttttttttttttttttttttttttttt:", end+"");

		if (end.equals("mp3")) {

			File file = new File(filepath);

			if (OperationDialog.getDirSize(file) == 0) {
				return 0;
			} else {
				MediaPlayer player = MediaPlayer.create(MyActivity.this,
						Uri.fromFile(new File(filepath)));
				int time = 0;
				if(player !=null){
					time = player.getDuration();

					player.release();
					player = null;
				}
				return time;
			}
		} else {
			// 返回1，表示不支持此格式的播放
			return 1;
		}

	}

	// 这里是打开文件夹
	private void openFile(ViewHolder vh, View view) {
		File f = null;
		if (!vh.isDirectory) {
			if(false){
			f = new File(vh.path);
			if ("apk".equals(vh.type)) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				final Uri uri = Uri.fromFile(f);
				intent.setDataAndType(uri,
						"application/vnd.android.package-archive");
				MyActivity.this.startActivity(intent);
				return;
			} else if ("audio".equals(vh.type)) {
				int time = getTime(f.getAbsolutePath());
				if (time == 0) {
					Toast toast = new Toast(MyActivity.this);
					toast = Toast.makeText(MyActivity.this,R.string.file_isnot_complete, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
							
				}else if(time==1){
					Toast toast = new Toast(MyActivity.this);
					toast = Toast.makeText(MyActivity.this,R.string.fileExceplorerCanotPlay, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}else {
					// Log.e("aa", f.getAbsolutePath());
					Log.e("com.android.file", f.getAbsolutePath());
					playAudio(f, view);
				}
				return;
			} else if ("image".equals(vh.type)) {
				if (MyUtil.fileSizeMsg(f).substring(0, 1).equals("0")) {
					Toast toast = new Toast(MyActivity.this);
					toast = Toast.makeText(MyActivity.this,R.string.file_isnot_complete, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else {
					dataLayout.setEnabled(false);
					// m_Dialogts =
					// ProgressDialog.show(MyActivity.this,getResources().getString(R.string.photo_load),
					// getResources().getString(R.string.photo_load_msg) , true
					// , false);
					int a = 0;
					String[] numsz = MyUtil.ListFile(f.getParentFile()
							.getAbsolutePath());
					// Log.e("循环完成后", numsz.length+"");
					
					for (int i = 0; i < numsz.length; i++) {
						if (numsz[i].equals(f.getAbsolutePath())) {
							a = i;
							break;
						}
					}
					
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putStringArray("photopath", numsz);
					Log.e("RRRRRRRRRR", "总有照片：" + numsz.length);
					// bundle.putInt("num", a);
					bundle.putString("fpath", f.getAbsolutePath());
					bundle.putString("dirpath", f.getParentFile().toString());
					intent.setClass(this, PhotoActivity.class);
					intent.putExtras(bundle);
					Log.e("bundle", bundle + "");

					startActivity(intent);

					return;
				}
			} else if ("video".equals(vh.type)) {
				Toast toast = new Toast(MyActivity.this);
				toast = Toast.makeText(MyActivity.this,
						R.string.canotplayvedio, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			String type = "*/*";
			type = MyUtil.getMIMEType(f, true);
			intent.setDataAndType(Uri.fromFile(f), type);
			intent.addCategory("android.intent.category.DEFAULT");
			if (intent.resolveActivity(getPackageManager()) != null) {
				startActivity(intent);
			}
			}
		} else {
			CharSequence fgfg = vh.label;
			currentPath = vh.path;
			new loadAsyncTask(MyActivity.this).execute(currentPath);
			// }
			currentPathView.setText(currentPath);
			// ischecked = false;
			tempOperationFiles.clear();
		}
	}

	// 这里是每一项的监听
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {

			ViewHolder hhh = (ViewHolder) adapter.getItem(position);
			if (isOperation) {
				if (hhh.isSelected) {
					tempOperationFiles.remove(hhh.path);
					if(tempOperationFiles.isEmpty()){
						mBtnRename.setEnabled(false);
						mBtnCopy.setEnabled(false);
						mBtnCut.setEnabled(false);
						mBtnPaste.setEnabled(false);
						mBtnDel.setEnabled(false);
						mBtnCopyTo.setEnabled(false);
					}else if(tempOperationFiles.size()==1){
						mBtnRename.setEnabled(true);
					}
				} else {
					tempOperationFiles.add(hhh.path);
					mBtnRename.setEnabled(true);
					mBtnCopy.setEnabled(true);
					mBtnCut.setEnabled(true);
					//mBtnPaste.setEnabled(true);
					mBtnDel.setEnabled(true);
					mBtnCopyTo.setEnabled(true);
					if(tempOperationFiles.size()!=1)
						mBtnRename.setEnabled(false);
				}
				hhh.isSelected = !hhh.isSelected;
				ImageView checkBox = (ImageView) v
						.findViewById(R.id.grid_check);
				checkBox.setImageResource(hhh.isSelected ? R.drawable.grid_check_on
						: R.drawable.grid_check_off);
				v.setTag(hhh);
			} else {
				openFile(hhh, v);
				mBtnRename.setEnabled(false);
				mBtnCopy.setEnabled(false);
				mBtnCut.setEnabled(false);
				mBtnDel.setEnabled(false);
				mBtnCopyTo.setEnabled(false);
				if(operationFiles.size()>0 && !new File(operationFiles.get(0)).getParent().equals(currentPath))
					mBtnPaste.setEnabled(true);
				else
					mBtnPaste.setEnabled(false);
				mBtnNew.setEnabled(true);
			}
		}
	};
	// 每项的长按监听
	private OnItemLongClickListener longClick = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View v,
				int position, long id) {
			isReleased = true;
			isMoved = true;
			// if (!isOperation) {
			operationView = v;
			CharSequence[] operation_entries = MyActivity.this.getResources()
					.getStringArray(R.array.file_operation_entries);
			int[] operation_values = MyActivity.this.getResources()
					.getIntArray(R.array.file_operation_values);
			ArrayList<CharSequence> tempEntries = new ArrayList<CharSequence>();
			ArrayList<Integer> tempValues = new ArrayList<Integer>();
			for (int i = 0; i < operation_entries.length; i++) {
				tempEntries.add(operation_entries[i]);
				tempValues.add(operation_values[i]);
			}
			if (operationFiles.isEmpty()) {
				tempEntries.remove(4);
				tempValues.remove(4);
			}
			entries = tempEntries.toArray(new CharSequence[0]);
			values = tempValues.toArray(new Integer[0]);

			new AlertDialog.Builder(MyActivity.this)
					.setItems(entries, listener_list)
					.setPositiveButton(android.R.string.cancel, null).show();
			// }
			return true;
		}
	};
	// 接收消息
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (System.currentTimeMillis() - downTime >= 1100) {
				//mLongPressRunnable.run();
			}
		}
	};

	// 线程发一个消息
	public boolean postDelayed(Runnable action, long delayMillis) {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1100);
					Message msg = new Message();
					MyActivity.this.mHandler.sendMessage(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
		return true;
	}

	private long downTime;

	private static final int TOUCH_SLOP = 20;

	private int mLastMotionX, mLastMotionY;

	private boolean isMoved = true;

	private boolean isReleased = true;

	private int mCounter;

	public boolean dispatchTouchEvent(MotionEvent event) {
		int dataPreY = dataLayout.getTop();
		int x = (int) event.getX();
		int y = (int) event.getY();
		if (y >= (dataPreY + 20)) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downTime = System.currentTimeMillis();
				mLastMotionX = x;
				mLastMotionY = y;
				mCounter++;
				isReleased = false;
				isMoved = false;
				//postDelayed(mLongPressRunnable,
				//		ViewConfiguration.getLongPressTimeout());
				break;

			case MotionEvent.ACTION_MOVE:
				if (isMoved)
					break;
				if (Math.abs(mLastMotionX - x) > TOUCH_SLOP
						|| Math.abs(mLastMotionY - y) > TOUCH_SLOP) {
					isMoved = true;
				}
				break;
			case MotionEvent.ACTION_UP:
				isReleased = true;
				break;
			}
		}
		return super.dispatchTouchEvent(event);
	}

	private Runnable mLongPressRunnable = new Runnable() {
		@Override
		public void run() {
			mCounter--;
			if (isReleased || isMoved)
				return;

			CharSequence[] operation_entries = MyActivity.this.getResources()
					.getStringArray(R.array.file_operation_entries);
			int[] operation_values = MyActivity.this.getResources()
					.getIntArray(R.array.file_operation_values);
			ArrayList<CharSequence> tempEntries = new ArrayList<CharSequence>();
			ArrayList<Integer> tempValues = new ArrayList<Integer>();
			tempEntries.add(operation_entries[1]);
			tempValues.add(operation_values[1]);
			if (!operationFiles.isEmpty()
					&& (operationType == 2 || operationType == 3)) {
				tempEntries.add(operation_entries[4]);
				tempValues.add(operation_values[4]);
			}
			tempEntries.add(operation_entries[8]);
			tempValues.add(operation_values[8]);
			values = tempValues.toArray(new Integer[0]);
			new AlertDialog.Builder(MyActivity.this)
					.setItems(tempEntries.toArray(new CharSequence[0]),
							listener_list)
					.setPositiveButton(android.R.string.cancel, null).show();
		}
	};
	// AlertDialog监听
	private OnClickListener listener_list = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			Integer index = values[which];
			switch (index) {
			case 0:
				final ViewHolder vh = (ViewHolder) operationView.getTag();
				openFile(vh, null);
				break;
			case 1:
				uplevelClick(null);
				break;
			case 2:
				copyPath = currentPath;
				final ViewHolder vh1 = (ViewHolder) operationView.getTag();
				operationType = index;
				operationFiles.clear();
				operationFiles.add(vh1.path);

				copyPath2 = vh1.path;
				// copyfilebf=new File(vh1.path);
				break;
			case 3:
				final ViewHolder vh2 = (ViewHolder) operationView.getTag();
				operationType = index;
				operationFiles.clear();
				operationFiles.add(vh2.path);
				break;
			case 4:
				doPaste();
				// }

				break;
			case 5:
				final ViewHolder vh3 = (ViewHolder) operationView.getTag();
				rename(new File(vh3.path));
				break;
			case 6:
				final ViewHolder vh4 = (ViewHolder) operationView.getTag();
				Dialog comfirmdialog = new AlertDialog.Builder(MyActivity.this)
						.setTitle(getString(R.string.confirm_delete_title))
						.setMessage(getString(R.string.confirm_delete_msg))
						.setPositiveButton(getString(R.string.str_ok),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										delOne(new File(vh4.path));
										dialog.dismiss();
									}
								})
						.setNeutralButton(getString(R.string.str_cancel),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create();
				comfirmdialog.show();
				break;
			case 7:
				showProperty(operationView);
				break;
			case 8:
				newDirOrFile();
				break;
			default:
				break;
			}
		}
	};

	public void uplevelClick(View v) {
		backLast(v);
	}

	// 显示信息
	private void showProperty(View v) {
		ViewHolder vh = (ViewHolder) v.getTag();
		File f = new File(vh.path);

		View view = View.inflate(MyActivity.this, R.layout.file_property, null);

		TableLayout table = (TableLayout) view.findViewById(R.id.file_table);
		table.setColumnShrinkable(1, true);

		TextView fnv = (TextView) view.findViewById(R.id.file_name_value);
		fnv.setText(f.getName());

		TextView fpv = (TextView) view.findViewById(R.id.file_path_value);
		fpv.setText(f.getPath());

		TextView fdv = (TextView) view.findViewById(R.id.file_date_value);
		fdv.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
				.format(new Date(f.lastModified())));

		Dialog dialog = new AlertDialog.Builder(MyActivity.this).setView(view)
				.setTitle(R.string.file_property)
				.setPositiveButton(android.R.string.ok, null).create();
		dialog.show();
	}
	private OperationEntity paste;
	// 粘贴
	private void doPaste() {
		if (operationFiles.size()>0) {
			final String temp = operationFiles.get(0);
			paste = new MyUtil.OperationEntity();
			if (operationType == 2) {
				paste.state = operationStates.COPYPASTE;
			} else if (operationType == 3) {
				paste.state = operationStates.CUTPASTE;
			}
			paste.goalPath = currentPath;
			paste.operationPaths = operationFiles;
			paste.adapter = adapter;
			// oe.dataLayout=dataLayout;
			boolean exists = false;
			for(int i= 0;i<operationFiles.size();i++){
				Log.d("TEST","paste file "+currentPath+"//"+new File(operationFiles.get(i)).getName());
				if(new File(currentPath+"//"+new File(operationFiles.get(i)).getName()).exists()){
					exists = true;
					break;
				}
			}
			if(exists){
					Dialog comfirmdialog = new AlertDialog.Builder(MyActivity.this)
							.setTitle("")
							.setMessage(getString(R.string.confirm_replace_msg))
							.setPositiveButton(getString(R.string.str_ok),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											od.execute(paste,true);
											scanFile(currentPath);
											scanFile(temp);
											operationFiles.clear();
											dialog.dismiss();
										}
									})
							.setNeutralButton(getString(R.string.str_cancel),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											od.execute(paste);
											scanFile(currentPath);
											scanFile(temp);
											operationFiles.clear();
											dialog.dismiss();
										}
									}).create();
					comfirmdialog.show();
			}else{
				od.execute(paste);
				scanFile(currentPath);
				scanFile(temp);
				operationFiles.clear();
			}
			// MyUtil.operationFiles(oe);
		} else {
			Toast.makeText(this, R.string.msg_error_nocontent,
					Toast.LENGTH_SHORT).show();
		}
		mBtnRename.setEnabled(false);
		mBtnCopy.setEnabled(false);
		mBtnCut.setEnabled(false);
		mBtnPaste.setEnabled(false);
		mBtnDel.setEnabled(false);
		mBtnCopyTo.setEnabled(false);
	}

	// 重新命名
	@SuppressWarnings("deprecation")
	private void rename(File f) {
		final File f_old = f;
		LayoutInflater factory = LayoutInflater.from(MyActivity.this);
		final View myView = factory.inflate(R.layout.rename_alert, null);
		final EditText myEditText = (EditText) myView
				.findViewById(R.id.rename_edit);
		myEditText.setText(f_old.getName());
		OnClickListener listenerFileEdit = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				/* ȡ���޸ĺ���ļ�·�� */
				final String modName = myEditText.getText().toString(); // ȡ���޸ĵ��ļ���
				final String pFile = f_old.getParentFile().getPath() + "/"; // ȡ�ø��ļ�·��
				final String newPath = pFile + modName; // �µ��ļ�·��+�ļ���
				final File f_new = new File(newPath);
				if (f_new.exists()) {
					Toast.makeText(MyActivity.this, R.string.msg_error_rename, Toast.LENGTH_LONG).show();
				} else {
					if (f_old.renameTo(f_new)) {
						adapter.addItems(new File[] { f_new });
						adapter.delItems(new File[] { f_old });
						adapter.notifyDataSetChanged();
					} else {
						Log.e("kkkkkk", "进来了吗？");
						Toast.makeText(MyActivity.this,
								R.string.msg_error_faild, Toast.LENGTH_LONG)
								.show();
					}
				}
			};
		};

		AlertDialog renameDialog = new AlertDialog.Builder(MyActivity.this)
				.create();
		renameDialog.setView(myView);
		renameDialog.setButton(getResources().getString(android.R.string.ok),
				listenerFileEdit);
		renameDialog.setButton2(
				getResources().getString(android.R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		renameDialog.show();
	}

	// 新建一个文件或文件夹
	@SuppressWarnings("deprecation")
	private void newDirOrFile() {
		AlertDialog nameDialog = new AlertDialog.Builder(MyActivity.this)
				.create();
		LayoutInflater factory = LayoutInflater.from(MyActivity.this);
		final View myView = factory.inflate(R.layout.new_alert, null);
		final RadioButton rb_dir = (RadioButton) myView
				.findViewById(R.id.newdir_radio);
		final RadioGroup radioGroup = (RadioGroup) myView
				.findViewById(R.id.new_radio);
		final EditText myEditText = (EditText) myView
				.findViewById(R.id.new_edit);
		nameDialog.setView(myView);

		nameDialog.setButton(getResources().getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						final int checkedId = radioGroup
								.getCheckedRadioButtonId();
						final String newName = myEditText.getText().toString();
						final String newPath = currentPath + "/" + newName;
						final File f_new = new File(newPath);
						if (f_new.exists()) {
							Toast.makeText(MyActivity.this,
									R.string.msg_error_rename,
									Toast.LENGTH_LONG).show();
							return;
						} else {
							if (checkedId == rb_dir.getId()) {
								if (MyUtil.checkDirPath(newPath)) {
									if (f_new.mkdirs()) {
										adapter.addItems(new File[] { f_new });
										adapter.notifyDataSetChanged();
									} else {
										Toast.makeText(MyActivity.this,
												R.string.msg_error_error,
												Toast.LENGTH_SHORT).show();
									}
								}
							} else {
								if (MyUtil.checkFilePath(newPath)) {
									if (MyUtil.newFile(f_new)) {
										adapter.addItems(new File[] { f_new });
										adapter.notifyDataSetChanged();
									} else {
										Toast.makeText(MyActivity.this,
												R.string.msg_error_error,
												Toast.LENGTH_SHORT).show();
									}
								}
							}

						}
					}
				});
		nameDialog.setButton2(
				getResources().getString(android.R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		nameDialog.show();
	}

	class DiskLisenterReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			String path = intent.getData().toString()
					.substring("file://".length());
			if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
				if (currentPath.equals("/mnt")) {
					new loadAsyncTask(MyActivity.this).execute(currentPath);
				}
				// 这里是刷新popupWindowBaseAdapter
				ArrayList<String> paths = util.getPaths();
				File[] files = new File[paths.size()];
				for (int i = 0; i < paths.size(); i++) {
					File f = new File(paths.get(i));
					files[i] = f;
				}
				popupWindowBaseAdapter.refurbish(files);
				popupWindowBaseAdapter.notifyDataSetChanged();

			} else if (intent.ACTION_MEDIA_REMOVED.equals(action)
					|| intent.ACTION_MEDIA_EJECT.equals(action)
					|| intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
				if (currentPath.equals("/mnt")) {
					new loadAsyncTask(MyActivity.this).execute(currentPath);
				}
				// 这里是当拔sd或其他时候的刷新
				ArrayList<String> paths = util.getPaths();
				File[] files = new File[paths.size()];
				for (int i = 0; i < paths.size(); i++) {
					File f = new File(paths.get(i));
					files[i] = f;
				}
				popupWindowBaseAdapter.refurbish(files);
				popupWindowBaseAdapter.notifyDataSetChanged();
				// 这里是当拔sd或其他时候的判断当前目录是否在拔掉的卡里 在就返回根目录
				if (currentPath.startsWith(path)) {
					Log.e("path", path);
					if (pwMusicPopWindow != null
							&& pwMusicPopWindow.isShowing()) {
						mAudioTrack.stop();
						mAudioTrack.release();// 关闭并释放资源
						mThreadFlag = false;// 音频线程暂停
						mAudioTrack = null;
						Log.e("MP31", MP3Decoder + "");
						MP3Decoder.closeAduioFile();
						Log.e("MP32", MP3Decoder + "");
						pwMusicPopWindow.dismiss();
						pwMusicPopWindow = null;
					} else if (ts_layout.getVisibility() == View.VISIBLE) {

						ts_layout.setVisibility(View.GONE);
					}

					currentPath = "/mnt";
					new loadAsyncTask(MyActivity.this).execute(currentPath);
				} else if (currentPath.equals("/mnt")) {
					if (ts_layout.getVisibility() == View.VISIBLE) {
						ts_layout.setVisibility(View.GONE);
					}
				}
			}
		}
	}

	DiskLisenterReciver DiskReceiver;

	void initReceiver() {
		DiskReceiver = new DiskLisenterReciver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(1000);
		filter.addAction(Intent.ACTION_MEDIA_EJECT);
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		filter.addDataScheme("file");
		registerReceiver(DiskReceiver, filter);
	}

	void deinitReceiver() {
		unregisterReceiver(DiskReceiver);
	}

	static {
		System.loadLibrary("mad");
	}

	@Override
	protected void onDestroy() {
		deinitReceiver();
		unregisterReceiver(mLocaleChangedBroadcastReceiver);
		Log.e("OnDestory()===", currentPath);
		super.onDestroy();
	}

	// 封装返回的函数
	public void backs() {
		isOperation = false;
		String sdPath = "/mnt";
		ts_layout.setVisibility(View.GONE);
		if (sdPath.equals(currentPath) || currentPath == null) {
			if (isback) {
				new loadAsyncTask(this).execute(currentPath);
				isback = false;
			} else {
				finish();
			}
		} else {
			if (isback) {
				File file = new File(currentPath);
				new loadAsyncTask(this).execute(currentPath);
				currentPathView.setText(currentPath);
				ischecked = false;
				isback = false;
			} else {
				File file = new File(currentPath);
				currentPath = file.getParent();
				new loadAsyncTask(this).execute(currentPath);
				currentPathView.setText(currentPath);
				ischecked = false;
			}
			tempOperationFiles.clear();
		}
		mBtnRename.setEnabled(false);
		mBtnCopy.setEnabled(false);
		mBtnCut.setEnabled(false);
		mBtnDel.setEnabled(false);
		mBtnCopyTo.setEnabled(false);
		if(operationFiles.size()>0 && !new File(operationFiles.get(0)).getParent().equals(currentPath))
			mBtnPaste.setEnabled(true);
		else
			mBtnPaste.setEnabled(false);
		if(currentPath.equals("/mnt")){
			mBtnNew.setEnabled(false);
			mBtnPaste.setEnabled(false);
		}
	}

	// Handler handlerdes = new Handler(){
	// public void handleMessage(Message msg) {
	// if(msg.what==0){
	// Intent intent=new Intent();
	// intent.setClass(MyActivity.this, MyActivity.class);
	// startActivity(intent);
	// }
	// };
	// };

}
