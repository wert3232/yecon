/**
 * @Title: AlbumListFragment.java
 * @Package com.yecon.musicplayer
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年5月12日 下午3:30:33
 * @version V1.0
 */
package com.yecon.media;


import com.yecon.mediaservice.MediaBaseActivity;
import com.yecon.mediaservice.MediaObject;
import com.yecon.mediaservice.MediaPlayerContants;
import com.yecon.mediaservice.MediaPlayerContants.MediaListContants.Type;

import android.view.View;
import android.widget.AdapterView;

/**
 * @ClassName: AlbumFragment
 * @Description: TODO
 * @author hzGuo
 * @date 2016年5月12日 下午3:30:33
 *
 */
public class AlbumFragment extends MediaListFragment {
	
	/**
	 * 创建一个新的实例 AlbumFragment. 
	 * Title:
	 * Description:
	 * @param activity
	 */
	public AlbumFragment(MediaBaseActivity activity) {
		super(activity);
	}

	/**
	 * Title: PackListItem
	 * Description:
	 * @param holder
	 * @param cv
	 * @see com.yecon.media.MediaListFragment#PackListItem(com.yecon.media.MediaListFragment.ViewHolder, android.content.ContentValues, int)
	 */
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
						mActivity.getMediaSevice().getPlayListType() == Type.ALBUM_FILE) {
					bPlayed = true;
				}
			} else {
				PackAlbumItem(holder, cv, position);
				if (position == mActivity.getMediaSevice().getAlbumPosInList()) {
					bPlayed = true;
				}
			}
			
			holder.play_indicator.setVisibility(bPlayed ? View.VISIBLE : View.GONE);
			holder.icon.setSelected(bPlayed ? true : false);
			holder.line1.setSelected(bPlayed ? true : false);
			holder.line2.setSelected(bPlayed ? true : false);
			holder.line3.setSelected(bPlayed ? true : false);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
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
			if (!mActivity.isBindService()) {
				return;
			}
			if (IsFileList()) {
				mActivity.getMediaSevice().requestPlay(Type.ALBUM_FILE, position);
				mActivity.finish();
			} else {
				mActivity.getMediaSevice().requestFileListByAlbum(position);
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
				if (mActivity.getMediaSevice().getPlayListType() == Type.ALBUM_FILE) {
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
		return IsFileList() ? Type.ALBUM_FILE : Type.ALL_ALBUM;
	}
	
}
