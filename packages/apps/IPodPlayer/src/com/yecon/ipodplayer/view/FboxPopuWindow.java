package com.yecon.ipodplayer.view;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.yecon.ipodplayer.R;

/**
 * 琛ㄦ紨鑰�鎾斁鍒楄〃锛屼笓杈戯紝姝屾洸 绫诲瀷锛岄�闆�
 * 
 * */
public class FboxPopuWindow extends PopupWindow implements View.OnClickListener {
	@Override
	public void dismiss() {
		super.dismiss();
		mPopuClickListener.getIsDismiss();
	}

	private Context mContext;
	public ListView mListV;

	//public RadioGroup m_RadioGroup;
	//public RadioButton m_Radio_playlist, m_Radio_album, m_Radio_song,
	//		m_Radio_performer;
	private View vArtist, vAlbum, vList;

	public FboxPopuWindow(Context context) {
		this.mContext = context;
		// initializeDefault();
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);

		final WallpaperManager wallpaperManager = WallpaperManager
				.getInstance(mContext);
		final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
		setBackgroundDrawable(wallpaperDrawable);
		
		// setBackgroundDrawable(new BitmapDrawable());
		//modify by lzy 2015.9.21
//		setAnimationStyle(R.style.PopupAnimation);
		setHeight(WindowManager.LayoutParams.MATCH_PARENT);
		setWidth(WindowManager.LayoutParams.MATCH_PARENT);

		// final WindowManager windowManager = (WindowManager) mContext
		// .getSystemService(Context.WINDOW_SERVICE);
		// DisplayMetrics dm = new DisplayMetrics();
		// windowManager.getDefaultDisplay().getMetrics(dm);
	}

	public void setContentView(int layoutId) {
		setContentView(LayoutInflater.from(mContext).inflate(layoutId, null,
				true));
		initView();
	}

	public ListView getListView() {
		return mListV;
	}

	private void initView() {
		final View contentView = getContentView();
		mListV = (ListView) contentView.findViewById(R.id.music_list_select);

		vArtist = contentView.findViewById(R.id.vArtist);
		vArtist.setOnClickListener(this);
		vAlbum = contentView.findViewById(R.id.vAlbum);
		vAlbum.setOnClickListener(this);
		vList = contentView.findViewById(R.id.vList);
		vList.setOnClickListener(this);
		
	}

	public void showPopu(View mView) {
		if(mView!=null){
			showAtLocation(mView, Gravity.LEFT | Gravity.TOP, 0, 0);
		}		
		update();
		mPopuClickListener.showPopuWindow();
		//m_RadioGroup.check(R.id.Radio_performer);
		vArtist.performClick();
	}
	
	@Override
	public void onClick(View mView) {
		switch (mView.getId()) {
		case R.id.vAlbum:
			vArtist.setSelected(false);
			vAlbum.setSelected(true);
			vList.setSelected(false);
			mPopuClickListener.onList_album();
			break;
		case R.id.vArtist:
			vArtist.setSelected(true);
			vAlbum.setSelected(false);
			vList.setSelected(false);
			mPopuClickListener.onList_performer();
			break;
		case R.id.vList:
			vArtist.setSelected(false);
			vAlbum.setSelected(false);
			vList.setSelected(true);
			mPopuClickListener.onList_song();
			break;
			
//		case R.id.list_playlist_normal:
//			mPopuClickListener.onList_PlayList();
//			break;
//		case R.id.list_back:
//			mPopuClickListener.onList_back();
//			break;
		default:
			break;
		}
	}

	public interface onPopuListener {

		public void onList_song();

		public void onList_album();

		public void onList_PlayList();

		public void onList_performer();

		public void onList_back();

		public void getIsDismiss();

		public void showPopuWindow();

	}

	public static onPopuListener mPopuClickListener;

	public void setPopuListener(onPopuListener mP) {
		mPopuClickListener = mP;
	}

}
