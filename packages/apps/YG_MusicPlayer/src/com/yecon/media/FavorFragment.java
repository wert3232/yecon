/**
 * @Title: PlayListFragment.java
 * @Package com.yecon.musicplayer
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年5月12日 下午3:29:13
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
 * @ClassName: FileFragment
 * @Description: TODO
 * @author hzGuo
 * @date 2016年5月12日 下午3:29:13
 *
 */
public class FavorFragment extends MediaListFragment {
	
	/**
	 * 创建一个新的实例 FileFragment. 
	 * Title:
	 * Description:
	 * @param musicListActivity
	 */
	public FavorFragment(MediaBaseActivity activity) {
		super(activity);
	}

	/**
	 * Title: PackListItem
	 * Description:
	 * @param holder
	 * @param cv
	 * @see com.yecon.media.MediaListFragment#PackListItem(com.yecon.media.MediaListFragment.ViewHolder, android.content.ContentValues)
	 */
	@Override
	public void PackListItem(ViewHolder holder, MediaObject cv, int position) {
		// TODO Auto-generated method stub
		try {
			boolean bPlayed = false;
			PackFileItem(holder, cv, position);
			if (!mActivity.isBindService()) {
				return;
			}
			if (position == mActivity.getMediaSevice().getFilePosInList() && 
					(mActivity.getMediaSevice().getPlayListType() == Type.FAVORITE_FILE)) {
				bPlayed = true;
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
			mActivity.getMediaSevice().requestPlay(Type.FAVORITE_FILE, position);
			mActivity.finish();
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
				if (mActivity.getMediaSevice().getPlayListType() == Type.FAVORITE_FILE) {
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
		return Type.FAVORITE_FILE;
	}
}
