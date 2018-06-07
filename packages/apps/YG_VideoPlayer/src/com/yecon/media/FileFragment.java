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

import com.tuoxianui.view.MuteTextView;
import com.yecon.mediaservice.MediaBaseActivity;
import com.yecon.mediaservice.MediaObject;
import com.yecon.mediaservice.MediaPlayerContants;
import com.yecon.mediaservice.MediaPlayerContants.MediaListContants.Type;
import com.yecon.video.R;

import android.view.View;
import android.widget.AdapterView;

/**
 * @ClassName: FileFragment
 * @Description: TODO
 * @author hzGuo
 * @date 2016年5月12日 下午3:29:13
 *
 */
public class FileFragment extends MediaListFragment {
	
	/**
	 * 创建一个新的实例 FileFragment. 
	 * Title:
	 * Description:
	 * @param musicListActivity
	 */
	public FileFragment(MediaBaseActivity activity) {
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
					(mActivity.getMediaSevice().getPlayListType() == Type.ALL_FILE ||
					 mActivity.getMediaSevice().getPlayListType() == Type.PLAY_FILE)) {
				bPlayed = true;
			}
			holder.item.setSelected(bPlayed ? true : false);
			//holder.play_indicator.setVisibility(bPlayed ? View.VISIBLE : View.GONE);
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
			if(mActivity.findViewById(R.id.btn_mute_state) != null){
				/*FIXME:一汽要求只有VOL和静音钮可解除静音
				*((MuteTextView)mActivity.findViewById(R.id.btn_mute_state)).setMute(false,400);
				*/
			}
			mActivity.getMediaSevice().requestPlay(Type.ALL_FILE, position);
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
				if (mActivity.getMediaSevice().getPlayListType() == Type.ALL_FILE ||
						 mActivity.getMediaSevice().getPlayListType() == Type.PLAY_FILE) {
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
		return Type.ALL_FILE;
	}
}
