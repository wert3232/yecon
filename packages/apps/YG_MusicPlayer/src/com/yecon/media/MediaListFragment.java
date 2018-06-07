/**
 * @Title: MediaListFragment.java
 * @Package com.yecon.musicplayer
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年5月12日 下午7:48:17
 * @version V1.0
 */
package com.yecon.media;

import java.util.ArrayList;
import java.util.List;

import com.yecon.mediaservice.MediaBaseActivity;
import com.yecon.mediaservice.MediaObject;
import com.yecon.mediaservice.MediaPlayerContants;
import com.yecon.mediaservice.MediaPlayerContants.MediaType;
import com.yecon.mediaservice.MediaPlayerContants.MediaListContants.Origin;
import com.yecon.music.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @ClassName: MediaListFragment
 * @Description: TODO
 * @author hzGuo
 * @date 2016年5月12日 下午7:48:17
 *
 */
public abstract class MediaListFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener {
	
//	private static final String TAG = "MediaListFragment";
	
	private static final int LINE = 5;

	// 绑定的界面
	protected MediaBaseActivity mActivity;

	protected MediaListAdapter mAdapter;

	protected ListView mListView;
	
	protected TextView mTVEmpty;
	
	protected TextView mBtnPosition;

	protected List<MediaObject> mDataList = new ArrayList<MediaObject>();

	public MediaListFragment(MediaBaseActivity activity) {
		// TODO Auto-generated constructor stub
		mActivity = activity;
		mAdapter = new MediaListAdapter(mActivity);
	}

	// 更新列表
	public void UpdateList(List<MediaObject> ls) {
		// TODO Auto-generated method stub
		mDataList = ls;
		mAdapter.notifyDataSetChanged();
		showEmpty();
//		LocalListPosition();
	}
	
	public abstract void PackListItem(ViewHolder holder, MediaObject cv, int position);
	
	public abstract int getSelectedPosition();
	
	public abstract int getCurrentListType();
	
	public void PackFileItem(ViewHolder holder, MediaObject cv, int position) {
        try {
    		holder.line1.setText(cv.getName());
    		if (mActivity.isBindService()) {
				if (mActivity.getMediaType() == MediaType.MEDIA_AUDIO) {
					String strInfo = cv.getTitle();
		            strInfo += " / ";
		            strInfo += cv.getAlbum();
		            strInfo += " / ";
		            strInfo += cv.getArtist();
		            holder.line2.setText(strInfo);
				} else {
					holder.line2.setText(cv.getPath());
				}
			}
    		//holder.line2.setVisibility(View.VISIBLE);
            holder.line3.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void PackDirItem(ViewHolder holder, MediaObject cv, int position) {
        try {
    		holder.line1.setText(cv.getName());
            //holder.line2.setVisibility(View.GONE);
    		String path = cv.getPath();
    		if(path.matches("^/mnt/ext_sdcard.*")){
    			path = path.replaceFirst("/mnt/ext_sdcard\\d+", getResources().getString(R.string.sd));
			}else if(path.matches("^/mnt/udisk.*")){
				path = path.replaceFirst("/mnt/udisk\\d+", getResources().getString(R.string.usb));
			}
    		holder.line2.setText(path);
//    		holder.line2.setText(cv.getPath());
            int iAmount = cv.getAudioCount();
            if (mActivity.isBindService()) {
				if (mActivity.getMediaType() == MediaType.MEDIA_AUDIO) {
					iAmount = cv.getAudioCount();
				}
			}
            holder.line3.setText("" + iAmount + " 个音乐");
            holder.line3.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void PackAlbumItem(ViewHolder holder, MediaObject cv, int position) {
        try {
    		holder.line1.setText(cv.getName());
            holder.line2.setVisibility(View.GONE);
            int iAmount = cv.getAudioCount();
            holder.line3.setText("" + iAmount + " 个音乐");
            holder.line3.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void PackArtistItem(ViewHolder holder, MediaObject cv, int position) {
        try {
    		holder.line1.setText(cv.getName());
            holder.line2.setVisibility(View.GONE);
            int iAmount = cv.getAudioCount();
            holder.line3.setText("" + iAmount + " 个音乐");
            holder.line3.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.yecon_list_fragment_layout,
				container, false);
		
		
		mListView = (ListView) view.findViewById(R.id.lv_Medialist);
		if (mActivity.getMediaType() == MediaType.MEDIA_VIDEO) {
			mTVEmpty = (TextView) view.findViewById(R.id.tv_video_error);
		} else {
			mTVEmpty = (TextView) view.findViewById(R.id.tv_music_error);
		}
		mBtnPosition = (TextView) view.findViewById(R.id.btn_position);
		
		mBtnPosition.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LocalListPosition();
			}
		});
		
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		if (mActivity.isBindService()) {
			try {
				if (mActivity.getMediaType() == MediaType.MEDIA_AUDIO) {
					//mListView.setOnItemLongClickListener(this);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		showEmpty();
		LocalListPosition();
		return view;
	}
	
	public void LocalListPosition() {
		if (mListView != null) {
			int iPosition = getSelectedPosition();
			if (iPosition == MediaPlayerContants.ID_INVALID) {
				if (mDataList != null) {
					if (!mDataList.isEmpty()) {
						iPosition = 0;
					}
				}
			}
			if (mBtnPosition != null && mDataList != null) {
//				mBtnPosition.setVisibility(mDataList.size() <= LINE ? View.GONE : View.VISIBLE); 
			}
			mListView.setSelection(iPosition);
		}
	}

	protected class MediaListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MediaListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return (mDataList != null) ? mDataList.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			showEmpty();
			return (mDataList != null) ? mDataList.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.media_list_item_group,
						null);
				holder.item = (ImageView) convertView.findViewById(R.id.item);
				holder.line1 = (TextView) convertView.findViewById(R.id.line1);
				holder.line2 = (TextView) convertView.findViewById(R.id.line2);
				holder.line3 = (TextView) convertView.findViewById(R.id.line3);
				holder.play_indicator = (ImageView) convertView.findViewById(R.id.play_indicator);
				holder.favor_indicator = (ImageView) convertView.findViewById(R.id.favor_indicator);
				holder.icon = (TextView) convertView.findViewById(R.id.icon);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			try {
				MediaObject item = mDataList.get(position);
				holder.icon.setText((position + 1) + ". ");
				holder.favor_indicator.setVisibility(IsFileList() && item.getFavor() ? View.VISIBLE : View.GONE);
				if (!mDataList.isEmpty() && mActivity != null && mActivity.isBindService()) {
					PackListItem(holder, item, position);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		int iPos = getSelectedPosition();
		if (mListView != null && iPos > 0) {
			mListView.setSelection(iPos);
		}
		showEmpty();
	}
	
	public void showEmpty() {
		if (mTVEmpty != null) {
			if (mDataList == null || mDataList.isEmpty()) {
				mTVEmpty.setVisibility(View.VISIBLE);
			} else {
				mTVEmpty.setVisibility(View.GONE);
			}
		}
	}

	public boolean IsFileList() {
		if (mDataList.size() > 0) {
			if (mDataList.get(0).getObjectType() == Origin.FILE) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		boolean bRet = false;				
		try {
			ViewHolder holder = (ViewHolder)view.getTag();
			if (holder.favor_indicator != null && IsFileList() && mActivity.isBindService()) {
				boolean bFavor = (holder.favor_indicator.getVisibility() != View.VISIBLE);
				if (bFavor) {
					bRet = mActivity.getMediaSevice().requestFavoriteAdd(getCurrentListType(), position);
				} else {
					bRet = mActivity.getMediaSevice().requestFavoriteErase(getCurrentListType(), position);
				}
				if (bRet) {
					holder.favor_indicator.setVisibility(bFavor ? View.VISIBLE : View.GONE);
					mDataList.get(position).setFavor(bFavor);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bRet;
	}

	protected static class ViewHolder {
		ImageView item;
		TextView icon;
		TextView line1;
		TextView line2;
		TextView line3;
		ImageView play_indicator;
		ImageView favor_indicator;
	}
}
