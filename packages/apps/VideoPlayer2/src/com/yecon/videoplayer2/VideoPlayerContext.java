
package com.yecon.videoplayer2;

import static com.yecon.videoplayer2.VideoPlaybackConstant.KEY_AUDIO_INDEX;
import static com.yecon.videoplayer2.VideoPlaybackConstant.KEY_SUB_INDEX;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.yecon.common.SourceManager;
import com.yecon.common.YeconEnv;
import com.yecon.videoplayer2.FileListManager.FileInfo;
import com.yecon.videoplayer2.PlayListData.BindDataCallback;
import com.yecon.videoplayer2.PlayListData.LoadDataCallback;
import com.yecon.videoplayer2.VideoPlaybackListActivity.VideoInfo;
import com.yecon.videoplayer2.VideoPlaybackMainActivity.PlayStatus;
import com.yecon.videoplayer2.FileScanner;

public class VideoPlayerContext {
    public static final String OPEN_LIST = "open_list";
    public static final String OPEN_POS = "open_pos";
    public static final String OPEN_SEEK = "open_seek";
    public static final String ACTION_PLAY_FILE_CHANGED = "con.yecon.videoplayer.play_file_changed";
	protected static final String TAG = "VideoPlayerContext";
    private PlayStatus playStatus = PlayStatus.STATUS_NONE;
    private int currentPos = 0;
    private LinkedList<FileListManager.FileInfo> playList = new LinkedList<FileListManager.FileInfo>();
    private VideoPlayerApp videoPlayerApp;
    private PlayListData playListData;
        
    public VideoPlayerContext(VideoPlayerApp videoPlayerApp) {
        // TODO Auto-generated constructor stub
        this.videoPlayerApp = videoPlayerApp;
        playListData = new PlayListData(videoPlayerApp);
    }

    // return true means user chosen another track.
    public boolean openList(List<VideoInfo> videoList, int pos, boolean save) {
        boolean posChanged = false;
        boolean listChanged = false;
        synchronized (playList) {
            if (pos != currentPos) {
                posChanged = true;
            }

            if (videoList.size() != playList.size()) {
                listChanged = true;
            }
            else {
                for (int i = 0; i < playList.size(); i++) {
                    if (!videoList.get(i).filePath.equals(playList.get(i).getPath())) {
                        listChanged = true;
                        break;
                    }
                }
            }

            if (listChanged && videoList.size() > 0) {
                FileInfo movie;
                File file;
                playList.clear();
                for (VideoInfo videoInfo : videoList) {
                    movie = new FileInfo();
                    movie.setId(videoInfo.id);
                    file = new File(videoInfo.filePath);
                    movie.setName(file.getName());
                    movie.setPath(file.getAbsolutePath());
                    playList.add(movie);
                }
            }
        }
        
        if(save && playList.size()>0){
        	playListData.savePlayList(playList.size(), new BindDataCallback(){

				@Override
				public String getPath(int index) {
					// TODO Auto-generated method stub
					return playList.get(index).getPath();
				}

				@Override
				public String getName(int index) {
					// TODO Auto-generated method stub
					return playList.get(index).getName();
				}

				@Override
				public int getSeekPos(int index) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public int getDuration(int index) {
					// TODO Auto-generated method stub
					return 0;
				}
        		
        	});
        	
        }
        return (posChanged | listChanged);
    }
    
    public boolean resume2Play(Context context){
    	boolean ret = false;
    	if(RebootStatus.isReboot(RebootStatus.SOURCE.SOURCE_VIDEO)){
    		playListData.clearPlayingList();
    		playListData.clearPlayedList();
    		playListData.clearPlaying();
    		//clear media removed flag
    		SourceManager.clearMediaEverRemovedFlag(SourceManager.SRC_NO.video);
    		return false;
    	}
    	PlayListData.ListItem  listItem = getPlaying();
    	if(listItem!=null){
    		String path = listItem.getPath();
    		if(SourceManager.isMediaEverRemoved(SourceManager.SRC_NO.video, path)){
    			playListData.removePlayListByDevPath(null, YeconEnv.getMeidaPathByFilePath(path));
    			return false;
    		}    		
    		PlayListData.STATUS status = listItem.getStatus();
    		if((status == PlayListData.STATUS.playing) && path!=null && new File(path).exists()){
    			final ArrayList<VideoInfo> list = new ArrayList<VideoInfo>();
    			playListData.loadPlayingList(new LoadDataCallback(){

					@Override
					public void loadData(String path, String name, int duration) {
						// TODO Auto-generated method stub
						if(new File(path).exists()){
							list.add(new VideoInfo(path, name, duration));
						}
					}
    				
    			});
    			if(list.size()>0){
    				Intent intent = new Intent(context, VideoPlaybackMainActivity.class);
        	        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        	        intent.putExtra(VideoPlayerContext.OPEN_LIST, "list");
        	        int pos = (int) playListData.getPosById(null, listItem.getId());
        	        pos = pos<0?0:pos;
        	        if (VideoPlayerApp.GetInstance().getVideoPlayerContext().openList(list, pos,false)) {
        	            intent.putExtra(VideoPlayerContext.OPEN_POS, pos);
        	        }
        	        context.startActivity(intent);
        	        ret = true;
    			}    			
    		}
    	}
    	return ret;
    }

    public boolean openFileList(List<FileInfo> videoList, int pos, boolean save) {
    	boolean posChanged = false, listChanged = false;
        synchronized (playList) {
        	if (pos != currentPos) {
                posChanged = true;
            }
            if (videoList.size() != playList.size()) {
                listChanged = true;
            }
            else {
                for (int i = 0; i < playList.size(); i++) {
                    if (!videoList.get(i).getPath().equals(playList.get(i).getPath())) {
                        listChanged = true;
                        break;
                    }
                }
            }
            
            if (listChanged && videoList.size() > 0) {
                playList.clear();
                playList.addAll(videoList);
                currentPos = pos;
            }
        }
        
        if(save && playList.size()>0){
        	playListData.savePlayList(playList.size(), new BindDataCallback(){

				@Override
				public String getPath(int index) {
					// TODO Auto-generated method stub
					return playList.get(index).getPath();
				}

				@Override
				public String getName(int index) {
					// TODO Auto-generated method stub
					return playList.get(index).getName();
				}

				@Override
				public int getSeekPos(int index) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public int getDuration(int index) {
					// TODO Auto-generated method stub
					return 0;
				}
        		
        	});
        	
        }
        
        return (posChanged | listChanged);
    }

    public int getListSize() {
        return playList.size();
    }

    public String getTrackPathByPos(int pos) {
        String ret = null;
        synchronized (playList) {
            if (pos < playList.size() && pos >= 0) {
                ret = playList.get(pos).getPath();
            }
        }
        return ret;
    }

    public String getCurrentTrackPath() {
        String ret = null;
        synchronized (playList) {
            if (currentPos < playList.size()) {
                ret = playList.get(currentPos).getPath();
            }
        }
        return ret;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int pos) {
        synchronized (playList) {
            this.currentPos = pos;
        }
    }

    public void clearPlayList() {
        synchronized (playList) {
            playList.clear();
            currentPos = 0;
        }
    }

    public PlayStatus getPlayStatus() {
        return playStatus;
    }
    
    private boolean stopedByBackground = false;
    public boolean isStopedByBackground(){
    	return stopedByBackground;
    }
    public void setStopedByBackground(boolean stopedByBackground){
    	this.stopedByBackground = stopedByBackground;
    }
    
    private boolean pausedByAudioFocus = false;
    public boolean isPausedByAudioFocus(){
    	return pausedByAudioFocus;
    }
    public void setPausedByAudioFocus(boolean pausedByAudioFocus){
    	this.pausedByAudioFocus = pausedByAudioFocus;
    }
    private boolean pausedByAccOff = false;
    public boolean isPausedByAccOff(){
    	return pausedByAccOff;
    }
    public void setPausedByAccOff(boolean pausedByAccOff){
    	this.pausedByAccOff = pausedByAccOff;
    }

    public void setPlayStatus(PlayStatus playStatus) {
        this.playStatus = playStatus;
    }
    
    public void savePlaying(String path, int seekPos, int duration){
    	PlayListData.STATUS status;
    	if(isStopedByBackground() || isPausedByAudioFocus() || isPausedByAccOff()){
    		status = PlayListData.STATUS.playing;
    	}
    	else{
    		if(PlayStatus.STATUS_NONE == playStatus
        			|| PlayStatus.STATUS_STOP == playStatus){
        		status = PlayListData.STATUS.stoped;
        		seekPos = 0;
        	}
        	else if(PlayStatus.STATUS_PAUSE == playStatus){
        		status = PlayListData.STATUS.paused;
        	}
        	else{
        		status = PlayListData.STATUS.playing;
        	}
    	}    	
    	playListData.savePlaying(path, seekPos, duration, "", PlayListData.LISTTYPE.all,  status);
    }
    
    public void savePlayed(String path, int seekPos, int duration){
    	playListData.add2PlayedList(null, path, seekPos, duration);
    }
    
    public void removePlayed(String path){
    	playListData.removeFromPlayedList(null, path);
    }
    
    public void removePlaying(){
    	playListData.clearPlaying();
    }
    public void removePlayListByDevPath(String devPath){
    	playListData.removePlayListByDevPath(null, devPath);
    }
    PlayListData.ListItem getPlaying(){
    	//for test
    	PlayListData.ListItem listItem = playListData.getPlaying();
    	//Log.i(TAG, "playListData.getPlaying:"+listItem.getPath()+" "+listItem.getSeekPos() + " " + listItem.getDuration() + " " + listItem.getStatus());
    	return listItem;
    }
    
    
    //private String playingPath = "";
    private int subIndex = -1;
    //private int playedTime = 0;
    private int audioIndex = -1;
    
//    public String getPlayingPath() {
//		return playingPath;
//	}
	public int getSubIndex() {
		return subIndex;
	}
//	public int getPlayedTime() {
//		return playedTime;
//	}
	public int getAudioIndex() {
		return audioIndex;
	}
	public void resumePlayContext(){
    	SharedPreferences uiState = videoPlayerApp.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        //editor.putInt(KEY_AUDIO_INDEX, 0);
        //editor.putInt(KEY_SUB_INDEX, 0);
        if (RebootStatus.isReboot(RebootStatus.SOURCE.SOURCE_VIDEO)) {
            editor.remove(KEY_AUDIO_INDEX);
            editor.remove(KEY_SUB_INDEX);
            //editor.remove(KEY_PLAY_TIME);
            //editor.remove(KEY_PLAY_PATH);
            editor.commit();
//            playingPath = "";
//            playedTime = 0;
            subIndex = -1;
            audioIndex = -1;
        }
        else{
        	//playingPath = uiState.getString(KEY_PLAY_PATH, "");
            //playedTime = uiState.getInt(KEY_PLAY_TIME, 0);
            subIndex = uiState.getInt(KEY_SUB_INDEX, -1);
            audioIndex = uiState.getInt(KEY_AUDIO_INDEX, -1);
        }
    }
    private void savePlayContext(String path, int playedTime, int audioIndex, int subIndex){
    	SharedPreferences uiState = videoPlayerApp.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putInt(KEY_AUDIO_INDEX, audioIndex);
        //editor.putInt(KEY_PLAY_TIME, playedTime);
        //editor.putString(KEY_PLAY_PATH, path);
        editor.putInt(KEY_SUB_INDEX, subIndex);
        editor.commit();        
        videoPlayerApp.getVideoPlayerContext().savePlaying(path, playedTime, 0);
    }

	public int getSeekPos(String playUri) {
		// TODO Auto-generated method stub
		return playListData.getSeekPosFromPlayedList(playUri);
	}
}
