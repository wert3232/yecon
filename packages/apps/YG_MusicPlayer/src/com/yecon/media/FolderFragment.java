/**
 * @Title: FolderListFragment.java
 * @Package com.yecon.musicplayer
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年5月12日 下午3:30:06
 * @version V1.0
 */
package com.yecon.media;

import java.util.List;

import com.tuoxianui.view.MuteTextView;
import com.yecon.mediaservice.MediaBaseActivity;
import com.yecon.mediaservice.MediaObject;
import com.yecon.mediaservice.MediaPlayerContants;
import com.yecon.mediaservice.MediaPlayerContants.PlayStatus;
import com.yecon.mediaservice.MediaPlayerContants.MediaListContants.Type;
import com.yecon.music.MusicListActivity;
import com.yecon.music.R;
import com.yecon.music.util.L;

import android.app.Activity;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

/**
 * @ClassName: FolderFragment
 * @Description: TODO
 * @author hzGuo
 * @date 2016年5月12日 下午3:30:06
 *
 */
public class FolderFragment extends MediaListFragment {
	
//	private static final String TAG = "FolderFragment";
	
	/**
	 * 创建一个新的实例 FolderFragment. 
	 * Title:
	 * Description:
	 * @param musicListActivity
	 */
	public FolderFragment(MediaBaseActivity activity) {
		super(activity);
	}

	/**
	 * Title: PackListItem
	 * Description:
	 * @param holder
	 * @param cv
	 * @see com.yecon.media.MediaListFragment#PackListItem(com.yecon.media.MediaListFragment.ViewHolder, android.content.ContentValues, int)
	 */
	MediaBaseActivity  parentActivity;
	@Override
	public void PackListItem(ViewHolder holder, MediaObject cv, int position) {
		// TODO Auto-generated method stub
		try {
			boolean bPlayed = false;
			if (!mActivity.isBindService()) {
				return;
			}
			if (IsFileList()) {
				PackFileItem(holder, cv, position);
				if (position == mActivity.getMediaSevice().getFilePosInList() &&
						mActivity.getMediaSevice().getPlayListType() == Type.DIR_FILE) {
					bPlayed = true;
				}
			} else {
				PackDirItem(holder, cv, position);
				if (position == mActivity.getMediaSevice().getDirPosInList()) {
					bPlayed = true;
				}
			}
			String curPath = getMediaPathInFileList();
			if(!TextUtils.isEmpty(curPath)){
				bPlayed = curPath.contains(cv.getPath()) ? true : false;
				//L.e("bPlayed :" + bPlayed + "  curPath:" + curPath + "  cv path:" + cv.getPath());
			}
			holder.play_indicator.setVisibility(bPlayed ? View.VISIBLE : View.GONE);
			holder.icon.setSelected(bPlayed ? true : false);
			holder.line1.setSelected(bPlayed ? true : false);
			holder.line2.setSelected(bPlayed ? true : false);
			holder.line3.setSelected(bPlayed ? true : false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public String getMediaPathInFileList(){
		try {
			int iStatus = mActivity.getMediaSevice().getPlayStatus();
			if(mActivity.getMediaSevice().getPlayListType() == Type.ALL_FILE){
				MediaObject mo = mActivity.getList(Type.ALL_FILE).get((int) mActivity.getMediaSevice().getFilePosInList());
				String path = mo.getPath();
				L.e(mo.getPath());
				return path;
			}
		} catch (Exception e) {
			L.e(e.toString());
		}
		return null;
	}
	
	/**
	 * Title: onItemClick
	 * Description:
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		try {
			if (mActivity.isBindService()) {
				if(mActivity.findViewById(R.id.btn_mute_state) != null){
					/*FIXME:一汽要求只有VOL和静音钮可解除静音
					*((MuteTextView)mActivity.findViewById(R.id.btn_mute_state)).setMute(false,400);
					*/
					
				}
				if (IsFileList()) {
					mActivity.getMediaSevice().requestPlay(Type.DIR_FILE, position);
					mActivity.finish();
				} else {
					mActivity.getMediaSevice().requestFileListByDir(position);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Title: getSelectedPosition
	 * Description:
	 * @return
	 * @see com.yecon.media.MediaListFragment#getSelectedPosition()
	 */
	@Override
	public int getSelectedPosition() {
		int iPosition = MediaPlayerContants.ID_INVALID;
		try {
			if (mActivity.isBindService()) {
				if (mActivity.getMediaSevice().getPlayListType() == Type.DIR_FILE) {
					iPosition = (int) mActivity.getMediaSevice().getFilePosInList();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iPosition;
	}

	@Override
	public int getCurrentListType() {
		// TODO Auto-generated method stub
		return IsFileList() ? Type.DIR_FILE : Type.ALL_DIR;
	}
}