
package com.yecon.mediaservice;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.mcu.McuExternalConstant;
import android.net.Uri;
import android.os.Binder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import com.autochips.storage.EnvironmentATC;
import com.media.constants.MediaConstants;
import com.yecon.common.SourceManager;
import com.yecon.common.SourceManager.AudioFocusNotify;
import com.yecon.common.SourceManager.SRC_ACTION;
import com.yecon.common.SourceManager.SourceNotify;
import com.yecon.mediaprovider.YeconMediaStore;
import com.yecon.mediaservice.IMediaListQueryHandler;
import com.yecon.mediaservice.IMediaPlayerActivity;
import com.yecon.mediaservice.IMediaPlayerService;
import com.yecon.mediaservice.IMultiMediaPlayer;
import com.yecon.mediaservice.LogUtil;
import com.yecon.mediaservice.MediaListQueryHandler;
import com.yecon.mediaservice.MediaObject;
import com.yecon.mediaservice.MediaPlayerContants;
import com.yecon.mediaservice.MediaPlayerContants.MediaListContants.Origin;
import com.yecon.mediaservice.MediaPlayerContants.MediaListContants.Type;
import com.yecon.mediaservice.MediaPlayerContants.RandomMode;
import com.yecon.mediaservice.MediaStorage;
import com.yecon.mediaservice.MediaTrackInfo;
import com.yecon.mediaservice.MultiMediaPlayer;
import com.yecon.mediaservice.MediaPlayerContants.MediaListContants;
import com.yecon.mediaservice.MediaPlayerContants.MediaType;
import com.yecon.mediaservice.MediaPlayerContants.PlayStatus;
import com.yecon.mediaservice.MediaPlayerContants.RepeatMode;
import com.yecon.savedata.SaveData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class MediaPlayerService extends Binder implements IMediaPlayerService,
		IMediaListQueryHandler, IMultiMediaPlayer {
	public static final String EXTRA_APK_PACKAGENAME = "packageName";
	public static final String EXTRA_APK_ACTIVITY = "activity";
	private static final String TAG = "MediaPlayerService";
	private static final float DEFAULT_APIC_H = 256.0F;
	private static final float DEFAULT_APIC_W = 256.0F;
	public static final String ACTION_MEDIA_QUERY = "yecon.intent.action.MEDIA_QUREY";
	public static final String ACTION_MEDIA_RESCAN = "yecon.intent.action.MEDIA_RESCAN";
	public static final String ACTION_MEDIA_INFO = "action.media.info";
	public static final String ACTION_MEDIA_RANDOM = "action.media.random";
	public static final String ACTION_MEDIA_REPEAT = "action.media.repeat";
	public static final String ACTION_QB_POWERON = "autochips.intent.action.QB_POWERON";
	public static final String ACTION_QB_POWEROFF = "autochips.intent.action.QB_POWEROFF";
	public static final String ACTION_QB_PREPOWEROFF = "autochips.intent.action.QB_PREPOWEROFF";
	public static final String MEDIA_INFO_ARTIST = "artist";
	public static final String MEDIA_INFO_SONG = "song";
	private long mlSystemOnOffTime = 0L;
	private int IGNORE_TIME = 5000;
	private Context mContext;
	private String mDBFile = "";
	private int mMediaType = 1;
	private List<IMediaPlayerActivity> mListUI = new ArrayList();
	private MediaListQueryHandler mListQueryHandler = null;
	private MediaPlayerService.MediaList mList = new MediaPlayerService.MediaList();
	private MediaPlayerService.MediaPlayStatus mStatus = new MediaPlayerService.MediaPlayStatus();
	private int defaultListType = Type.DIR_FILE;
	private boolean isInitPlaying = false;
	private int mCurrentPlayDirPos = -1;
	MediaPlayerService.MediaStorageState mStorageState;
	private BroadcastReceiver mPowerOnOffReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action != null) {
				MediaPlayerService.this.mlSystemOnOffTime = SystemClock
						.uptimeMillis();
				LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
						action);
				if ("autochips.intent.action.QB_POWEROFF".equals(action)
						|| "autochips.intent.action.QB_PREPOWEROFF"
								.equals(action)) {
					if (MediaPlayerService.this.mPlayer != null) {
						MediaPlayerService.this.mPlayer.acquireWakeLock();
					}

					MediaPlayerService.this.updateServiceStatus(9,
							(String) null);
				}
			}

		}
	};
	private MultiMediaPlayer mPlayer = null;
	private int mPlayErrorID = -1;
	private SaveData mSaveData;
	private boolean mbPauseByAudioFocus = false;
	private boolean mbLostbAudioFocusForever = true;
	private boolean mbOccupyAudioFocus = false;
	private boolean mbPauseByBackCar = false;
	private boolean mbBackCar = false;
	private static final int WAIT_LOADING = 60;
	private static final int WAIT_FINISH = 0;
	private static final int WAIT_MOUNT = 5;
	private int miWaitDatabase = 0;
	private EnvironmentATC mEnv;
	private Object mSourceTocken = null;
	private AudioFocusNotify mAudioFocusCB;
	private SourceNotify mSourceNotifyCB;
	private boolean mbPrevAction = false;
	private SharedPreferences mPrf;
	private BroadcastReceiver mReceiverScan = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String arg2 = "MediaPlayerService";
			synchronized ("MediaPlayerService") {
				if (intent != null) {
					LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
							"[mReceiverScan]" + intent);
					String action = intent.getAction();
					if (action
							.equals("yecon.intent.action.MEDIA_SCANER_STATUS")) {
						String strAction = intent.getStringExtra("action");
						String strPath = intent.getStringExtra("path");
						if (strAction != null && strPath != null) {
							strPath = YeconMediaStore.getStoragePath(strPath);
							MediaStorage storage = MediaPlayerService.this.mStorageState
									.obtainStorage(strPath);
							storage.setState(strAction);
							if (!strAction.equals("scan_analysis")
									&& !strAction.equals("scan_finish")) {
								if (strAction.equals("scan_start")) {
									MediaPlayerService.this
											.updateServiceStatus(1, strPath);
								}
							} else {
								try {
									String e = MediaPlayerService.this
											.getDBFile(strPath);
									if (MediaPlayerService.this.mDBFile
											.equals(e)) {
										if (MediaPlayerService.this.mStatus.mPlayingTrackInfo != null) {
											MediaPlayerService.this.mStatus.mPlayingTrackInfo
													.recycle();
											MediaPlayerService.this.mStatus.mPlayingTrackInfo = null;
										}

										MediaPlayerService.this
												.recoverStorage(strPath);
									}
								} catch (Exception arg8) {
									arg8.printStackTrace();
								}
							}

							MediaPlayerService.this.mStorageState
									.UpdateStorage(storage);
							LogUtil.printError("MediaPlayerService",
									LogUtil._FUNC_(), "[mReceiverScan] Action:"
											+ strAction + " Path:" + strPath);
						}
					}
				}

			}
		}
	};
	
	private BroadcastReceiver mReceiverStorage = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String arg2 = "MediaPlayerService";
			synchronized ("MediaPlayerService") {
				if (intent != null) {
					LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
							"[mStorageReceiver]" + intent);
					String action = intent.getAction();
					Uri uri = intent.getData();
					String path = YeconMediaStore.getStoragePath(uri.getPath());
					if (!action.equals("android.intent.action.MEDIA_UNMOUNTED")
							&& !action
									.equals("android.intent.action.MEDIA_EJECT")
							&& !action
									.equals("android.intent.action.MEDIA_REMOVED")
							&& !action
									.equals("android.intent.action.MEDIA_BAD_REMOVAL")) {
						//yfzhang: 插入设备接收广播
						if (action.equals("android.intent.action.MEDIA_MOUNTED") && path != null) {
							/*FIXME: 一汽-插入不要自动播  data:2018-05-22
							LogUtil.printError("MediaPlayerService",LogUtil._FUNC_(),"[mStorageReceiver] storage plug in " + path);
							MediaStorage storage1 = 1MediaPlayerService.this.mStorageState.obtainStorage(path);
							storage1.setState(MediaPlayerService.getStorageStateFromSP(MediaPlayerService.this.mContext, path));
							MediaPlayerService.this.mStorageState.UpdateStorage(storage1);
							if(MediaPlayerService.this.mMediaType == MediaType.MEDIA_AUDIO){
								SystemProperties.set("persist.sys.music_init_state", "empty"); 
								SystemProperties.set("persist.sys.video_init_state", path);
								//Toast.makeText(mContext, "device41:" + path + "  " + MediaPlayerService.this.mMediaType + "    " + SystemProperties.get("persist.sys.video_init_state", "empty"), Toast.LENGTH_LONG).show();
							}else if(MediaPlayerService.this.mMediaType == MediaType.MEDIA_VIDEO){
								SystemProperties.set("persist.sys.music_init_state", path); 
								SystemProperties.set("persist.sys.video_init_state", "empty");
								//Toast.makeText(mContext, "device42:" + path + "  " + MediaPlayerService.this.mMediaType + "    " + SystemProperties.get("persist.sys.music_init_state", "empty"), Toast.LENGTH_LONG).show();
							}
							if (!storage1.getState().equals("scan_finish") && !storage1.getState().equals("scan_analysis")) {
								MediaPlayerService.this.updateServiceStatus(PlayStatus.DECODING, path);
							} else {
								MediaPlayerService.this.updateServiceStatus(PlayStatus.DECODED, path);
							}*/
						}
					} else {
						try {
							if (MediaPlayerService.this.mPlayer != null
									&& (path == null || MediaPlayerService.this.mPlayer
											.getSourcePath().contains(path))) {
								MediaPlayerService.this.mPlayer.release();
							}
						} catch (Exception arg8) {
							arg8.printStackTrace();
						}

						if (path != null){// && !path.equals("/mnt/sdcard")) {
							MediaPlayerService.this.mStorageState
									.RemoveStorage(path);
							if (MediaPlayerService.this.mDBFile
									.equals(MediaPlayerService.this
											.getDBFile(path))) {
								MediaPlayerService.this.miWaitDatabase = 0;
								MediaPlayerService.this.mRecoverThread
										.interrupt();
								if (!MediaPlayerService.this.isSystemOnOff()) {
									MediaPlayerService.this.mDBFile = "";
									MediaPlayerService.this.mStatus.strFile = "";
									Log.e("MediaPlayerService",
											"clearPreference LAST_MEMORY_DEVICE: "
													+ path);
									SharedPreferences storage = MediaPlayerService.this.mContext
											.getSharedPreferences(
													MediaPlayerContants.LAST_MEMORY_DEVICE, 0);
									Editor editor = storage.edit();
									editor.clear();
									editor.commit();
								}
							}

							MediaPlayerService.this.mStatus.clearPreference(
									MediaPlayerService.this.mContext,
									MediaPlayerService.this.getDBFile(path),
									MediaPlayerService.this.mMediaType);
							LogUtil.printError("MediaPlayerService",
									LogUtil._FUNC_(),
									"current attached storage:" + path
											+ " plugout");
							MediaPlayerService.this
									.updateServiceStatus(6, path);
						}
					}
				}

			}
		}
	};
	private BroadcastReceiver mReceiverMCUKey = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String arg2 = "MediaPlayerService";
			synchronized ("MediaPlayerService") {
				String action = intent.getAction();

				try {
					if (action != null) {
						LogUtil.printError("MediaPlayerService",LogUtil._FUNC_(), action);
						if (MediaPlayerService.this.mPlayer != null
								&& !MediaPlayerService.this.mPlayer.isStop()) {
							if ("com.yecon.action.ACC_OFF".equals(action)) {
								MediaPlayerService.this.mPlayer.pause();
							} else if ("com.yecon.action.ACC_ON".equals(action)) {
								MediaPlayerService.this.mPlayer.play();
							} else if (action
									.equals(McuExternalConstant.MCU_ACTION_MEDIA_NEXT)) {
								MediaPlayerService.this.requestNext();
							} else if (action
									.equals(McuExternalConstant.MCU_ACTION_MEDIA_PREVIOUS)) {
								MediaPlayerService.this.requestPrev();
							} else if (action.equals("com.yecon.action.MEDIA_PLAY_PAUSE")) {
								MediaPlayerService.this.requestPause();
							} else if (action.equals("com.yecon.action.MEDIA_PLAY")) {
								if (MediaPlayerService.this.mPlayer.getCurrentRealState() == 5) {
									MediaPlayerService.this.mPlayer.play();
								}
							} else if ((action.equals("com.yecon.action.MEDIA_PAUSE") || action
									.equals("com.yecon.action.MEDIA_STOP"))
									&& MediaPlayerService.this.mPlayer.getCurrentRealState() == 4) {
								MediaPlayerService.this.mPlayer.pause();
							}
						}

						int e;
						IMediaPlayerActivity song;
						Iterator cursor;
						if (action.equals("action.media.random")) {
							e = intent.getIntExtra("action.media.random", 1);
							MediaPlayerService.this.mStatus.iRandomMode = e == 1 ? 1
									: 0;
							cursor = MediaPlayerService.this.mListUI.iterator();

							while (cursor.hasNext()) {
								song = (IMediaPlayerActivity) cursor.next();
								song.updateRandomStatus(MediaPlayerService.this.mStatus.iRandomMode);
							}
						} else if (action.equals("action.media.repeat")) {
							e = intent.getIntExtra("action.media.repeat", 1);
							MediaPlayerService.this.mStatus.iRepeatMode = e % 3;
							cursor = MediaPlayerService.this.mListUI.iterator();

							while (cursor.hasNext()) {
								song = (IMediaPlayerActivity) cursor.next();
								song.updateRepeatStatus(MediaPlayerService.this.mStatus.iRepeatMode);
							}

							MediaPlayerService.this.mStatus.iRandomMode = 0;
							cursor = MediaPlayerService.this.mListUI.iterator();

							while (cursor.hasNext()) {
								song = (IMediaPlayerActivity) cursor.next();
								song.updateRandomStatus(MediaPlayerService.this.mStatus.iRandomMode);
							}
						} else if (action.equals("action.media.info")) {
							String arg16 = intent.getStringExtra("artist");
							String arg17 = intent.getStringExtra("song");
							Log.e("MediaPlayerService", "artist:" + arg16
									+ " song:" + arg17);
							if ((!arg16.equals("") || !arg17.equals(""))
									&& MediaPlayerService.this.mDBFile != null) {
								cursor = null;
								int iArtistID = -1;
								int iFileID = -1;
								Uri iPosition;
								String[] object;
								String selection;
								Object selectionArgs;
								Object orderBy;
								Cursor arg18;
								if (!arg16.equals("")) {
									iPosition = YeconMediaStore.getContent(
											MediaPlayerService.this.mDBFile,
											"artist");
									object = MediaPlayerContants.ARTIST_COLUMNS;
									selection = "artist_name like \'%" + arg16
											+ "%\'";
									selectionArgs = null;
									orderBy = null;
									arg18 = MediaPlayerService.this.mListQueryHandler
											.startQuery(iPosition, object,
													selection,
													(String[]) selectionArgs,
													(String) orderBy);
									if (arg18 != null) {
										arg18.moveToFirst();
										if (arg18.getCount() > 0) {
											iArtistID = arg18.getInt(0);
										}

										arg18.close();
										cursor = null;
									}
								}

								if (!arg17.equals("") || iArtistID != -1) {
									iPosition = YeconMediaStore.getContent(
											MediaPlayerService.this.mDBFile,
											"files");
									object = MediaPlayerContants.FILE_COLUMNS;
									selection = null;
									if (!arg17.equals("")) {
										selection = "file_name like \'%"
												+ arg17 + "%\'";
									}

									if (iArtistID != -1) {
										if (selection != null) {
											selection = selection + " and ";
											selection = selection
													+ "artist_id=" + iArtistID;
										} else {
											selection = "artist_id="
													+ iArtistID;
										}
									}

									selectionArgs = null;
									orderBy = null;
									arg18 = MediaPlayerService.this.mListQueryHandler
											.startQuery(iPosition, object,
													selection,
													(String[]) selectionArgs,
													(String) orderBy);
									if (arg18 != null) {
										arg18.moveToFirst();
										if (arg18.getCount() > 0) {
											iFileID = arg18.getInt(0);
										}

										arg18.close();
									}
								}

								int arg19 = 0;
								if (iFileID != -1) {
									MediaObject arg20;
									Iterator arg21;
									if (iArtistID != -1
											&& !MediaPlayerService.this.mList.mListArtist
													.isEmpty()) {
										for (arg21 = MediaPlayerService.this.mList.mListArtist
												.iterator(); arg21.hasNext(); ++arg19) {
											arg20 = (MediaObject) arg21.next();
											if (arg20.getID() == iArtistID) {
												break;
											}
										}

										arg19 %= MediaPlayerService.this.mList.mListArtist
												.size();
										MediaPlayerService.this
												.queryArtistFileList(false,
														arg19);
										if (!MediaPlayerService.this.mList.mListArtistFile
												.isEmpty()) {
											arg19 = 0;

											for (arg21 = MediaPlayerService.this.mList.mListArtistFile
													.iterator(); arg21
													.hasNext(); ++arg19) {
												arg20 = (MediaObject) arg21
														.next();
												if (arg20.getID() == iFileID) {
													break;
												}
											}

											arg19 %= MediaPlayerService.this.mList.mListArtistFile
													.size();
											MediaPlayerService.this
													.requestPlay(Type.ARTIST_FILE, arg19);
										}
									} else if (!MediaPlayerService.this.mList.mListFile
											.isEmpty()) {
										for (arg21 = MediaPlayerService.this.mList.mListFile
												.iterator(); arg21.hasNext(); ++arg19) {
											arg20 = (MediaObject) arg21.next();
											if (arg20.getID() == iFileID) {
												break;
											}
										}

										arg19 %= MediaPlayerService.this.mList.mListFile
												.size();
										MediaPlayerService.this.requestPlay(Type.ALL_FILE,
												arg19);
									}
								}
							}
						}
					}
				} catch (Exception arg14) {
					arg14.printStackTrace();
				}

			}
		}
	};
	
	private BroadcastReceiver otherReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.e("MediaPlayerService", "otherReceiver Recv : " + action);
			if("com.media.action.hasOtherSourceOpen".equals(action)){
				mbLostbAudioFocusForever = true;
			}
			
		}
	};
	
	
	private MediaPlayerService.RecorverThread mRecoverThread;

	public void recoverStorage(String path) {
		String arg1 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			Log.e("MediaPlayerService", "++recoverStorage++ : " + path);
			MediaStorage storage = this.mStorageState.obtainStorage(path);
			storage.setState("scan_analysis");
			this.mStorageState.UpdateStorage(storage);
			this.mStorageState.AttachStorage(path);
			if (this.mList != null) {
				if (this.mList.mListPlay.isEmpty()) {
					this.mStatus = new MediaPlayerService.MediaPlayStatus();
					this.mStatus.readPreference(this.mContext, this.mDBFile,
							this.mMediaType);
					this.queryDirList(false);
					this.queryAlbumList(false);
					this.queryArtistList(false);
					this.queryAllFileList(false);
					this.queryFavoriteList(false);
					if (this.mList.mListFile.isEmpty()) {
						if (YeconMediaStore.checkStorageExist(this.mEnv, path)) {
							LogUtil.printError("MediaPlayerService",
									LogUtil._FUNC_(), this.mDBFile
											+ ": not exist media file");
							this.updateServiceStatus(5, path);
						} else {
							LogUtil.printError("MediaPlayerService",
									LogUtil._FUNC_(), this.mDBFile
											+ ": no storage");
							this.updateServiceStatus(7, path);
						}

						return;
					}

					switch (this.mStatus.iPlayListType) {
					case Type.DIR_FILE:
						this.queryDirFileList(false, this.mStatus.iDirPos);
						break;
					case Type.ALBUM_FILE:
						this.queryAlbumFileList(false, this.mStatus.iAlbumPos);
						break;
					case Type.ARTIST_FILE:
						this.queryArtistFileList(false, this.mStatus.iArtistPos);
					}

					this.updatePlayList();
					if (!this.recoverPlay()) {
						try {
							if (!this.mStatus.strFile.isEmpty()) {
								LogUtil.printError("MediaPlayerService",
										LogUtil._FUNC_(), "recover failed!!! "
												+ this.mStatus.strFile);
							}
							//FIXME:yfzhang
							if(defaultListType == Type.DIR_FILE){
								isInitPlaying = true;
								int index = 0;
								//add FIXME:yfzhang insert usbdisck or sdcard break point momery
								try {
									List<MediaObject> dirList = getDirList(); 
									String type = getMediaStorageType(path);
									String json = mPrf.getString(type, null);
									//Log.e(TAG,"yfzhang:"  + type + "  json: " + json);
									if(!TextUtils.isEmpty(json)){
										JSONObject jsonObject = new JSONObject(json);
										String lastPath = jsonObject.getString("path");
										
										//FIXME:this add momery for mount unmount
										for(int i = 0; i < dirList.size(); i++){
											if(lastPath.contains(dirList.get(i).getPath())){
												/* yfzhang 加上这里可以有插拔断点文件夹记忆
												 * index = i;*/
												break;
											} 
										}
									}
								} catch (Exception e) {
									Log.e(TAG,"yfzhang:"  +   e.toString());
								}
								// add end
								//Log.e(TAG,"yfzhang:"  +  index);
								requestFileListByDir(index);
							}
							else{
								this.requestPlay(Type.ALL_FILE, 0);
							}
							//end
							//FIXME:INIT requestPlay
						} catch (Exception arg4) {
							arg4.printStackTrace();
						}
					}
				} else {
					this.queryDirList(true);
					this.queryAlbumList(true);
					this.queryArtistList(true);
					this.queryAllFileList(true);
					this.updateTrackIndex();
					Log.e("MediaPlayerService",
							"scan status change, update list");
				}
			}

			this.miWaitDatabase = 0;
			this.updateServiceStatus(2, path);
			Log.e("MediaPlayerService", "--recoverStorage-- : " + path);
			
			//add
			this.mList.mCurrentPlayList.clear();
			for(MediaObject mediaObject : this.mList.mListPlay){
				this.mList.mCurrentPlayList.add(mediaObject);
			}
			mCurrentPlayDirPos = mStatus.iDirPos;
			//add end
		}
	}

	private boolean recoverPlay() {
		boolean bRecovered = false;
		try {
			this.updateTrackIndex();
			if (!this.mStatus.strFile.isEmpty()) {
				bRecovered = true;
				this.mPlayer.setVolume(0.0F);
				if (this.decode(this.mStatus.strFile)) {
					this.mPlayer.seek(this.mStatus.iProgress);
				}

				this.mPlayer.setVolume(1.0F);
			}
		} catch (Exception arg2) {
			arg2.printStackTrace();
		}

		return bRecovered;
	}

	private void updateTrackIndex() {
		int iSize = this.mList.mListPlay.size();
		if (iSize > 0 && this.mStatus.iFilePos != -1) {
			MediaObject cv = (MediaObject) this.mList.mListPlay.get(0);
			String strFile = "";
			if (!this.mStatus.strFile.isEmpty()) {
				for (int i = 0; i < this.mList.mListPlay.size(); ++i) {
					cv = (MediaObject) this.mList.mListPlay.get(i);
					if (cv.getPath().equals(this.mStatus.strFile)) {
						strFile = this.mStatus.strFile;
						this.mStatus.iFilePos = i;
						break;
					}
				}
			}

			if (strFile.isEmpty()) {
				this.mStatus.strFile = "";
			}
		}

	}

	private void updatePlayList() {
		boolean bLog = true;
		switch (this.mStatus.iPlayListType) {
		case Type.ALL_FILE:
			this.mList.mListPlay = this.mList.mListFile;
			break;
		case Type.DIR_FILE:
			this.mList.mListPlay = this.mList.mListDirFile;
			break;
		case Type.ALBUM_FILE:
			this.mList.mListPlay = this.mList.mListAlbumFile;
			break;
		case Type.ARTIST_FILE:
			this.mList.mListPlay = this.mList.mListArtistFile;
			break;
		case Type.FAVORITE_FILE:
			this.mList.mListPlay = this.mList.mListFavorFile;
			break;
		default:
			bLog = false;
		}

		if (bLog) {
			Log.e("MediaPlayerService",
					"[updatePlayList] mList.mListPlay.size() = "
							+ this.mList.mListPlay.size());
		}

	}

	private int queryRealFileID(int iList, int iPos) {
		int iID = -1;
		switch (iList) {
		case Type.PLAY_FILE:
			if (iPos < this.mList.mListPlay.size()) {
				iID = ((MediaObject) this.mList.mListPlay.get(iPos)).getID();
			}
			break;
		case Type.ALL_FILE:
			if (iPos < this.mList.mListFile.size()) {
				iID = ((MediaObject) this.mList.mListFile.get(iPos)).getID();
			}
			break;
		case Type.DIR_FILE:
			if (iPos < this.mList.mListDirFile.size()) {
				iID = ((MediaObject) this.mList.mListDirFile.get(iPos)).getID();
			}
			break;
		case Type.ALBUM_FILE:
			if (iPos < this.mList.mListAlbumFile.size()) {
				iID = ((MediaObject) this.mList.mListAlbumFile.get(iPos))
						.getID();
			}
			break;
		case Type.ARTIST_FILE:
			if (iPos < this.mList.mListAlbumFile.size()) {
				iID = ((MediaObject) this.mList.mListAlbumFile.get(iPos))
						.getID();
			}
			break;
		case Type.FAVORITE_FILE:
			if (iPos < this.mList.mListFavorFile.size()) {
				iID = ((MediaObject) this.mList.mListFavorFile.get(iPos))
						.getID();
			}
		}

		return iID;
	}

	public MediaPlayerService(Context context) {
		assert context != null;

		LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
				"construct MediaPlayerService");
		this.mContext = context;
		this.mEnv = new EnvironmentATC(context);
		this.mListQueryHandler = new MediaListQueryHandler(
				context.getContentResolver(), this);
		this.mSaveData = new SaveData();
		if (SystemProperties.getBoolean("persist.sys.qbpoweron", false)) {
			this.mlSystemOnOffTime = SystemClock.uptimeMillis();
		}

		this.mAudioFocusCB = new AudioFocusNotify() {
			public void onAudioFocusChange(int arg0) {
				Log.i("MediaPlayerService", "onAudioFocusChange:" + arg0);

				try {
					MediaPlayerService.this.mbLostbAudioFocusForever = false;
					switch (arg0) {
					case -3:
					case -2:
						Log.e("MediaPlayerService",
								"onAudioFocusChange:duck Audio Focus!");
						MediaPlayerService.this.mbOccupyAudioFocus = false;
						if (MediaPlayerService.this.mPlayer != null
								&& MediaPlayerService.this.mPlayer
										.getPlayState() == 4) {
							MediaPlayerService.this.mbPauseByAudioFocus = true;
							MediaPlayerService.this.mPlayer.pause();
						}
						break;
					case -1:
						Log.e("MediaPlayerService",
								"onAudioFocusChange:Lost Audio Focus!");
						MediaPlayerService.this.mbLostbAudioFocusForever = true;
						MediaPlayerService.this.updateServiceStatus(8,
								(String) null);
					case 0:
					default:
						break;
					case 1:
						Log.e("MediaPlayerService",
								"onAudioFocusChange:get Audio Focus!");
						if (MediaPlayerService.this.mbPauseByAudioFocus) {
							MediaPlayerService.this.mbPauseByAudioFocus = false;
							if (MediaPlayerService.this.mPlayer != null) {
								MediaPlayerService.this.mPlayer.play();
							}
						}

						MediaPlayerService.this.mbOccupyAudioFocus = true;
					}
				} catch (Exception arg2) {
					arg2.printStackTrace();
				}

			}
		};
		this.mSourceNotifyCB = new SourceNotify() {
			public void onSourceNotify(int arg0, int arg1) {
				if (arg0 == 12 || arg0 == 11) {
					if (arg1 == SRC_ACTION.START.ordinal()) {
						Log.e("MediaPlayerService",
								"onSourceNotify bt_phone/backcar start");
						MediaPlayerService.this.mbBackCar = true;
						if (MediaPlayerService.this.mPlayer != null
								&& MediaPlayerService.this.mPlayer
										.getPlayState() == 4) {
							MediaPlayerService.this.mbPauseByBackCar = true;
							MediaPlayerService.this.mPlayer.pause();
						}
					} else if (arg1 == SRC_ACTION.STOP.ordinal()) {
						Log.e("MediaPlayerService",
								"onSourceNotify bt_phone/backcar stop");
						MediaPlayerService.this.mbBackCar = false;
						if (MediaPlayerService.this.mbPauseByBackCar
								&& MediaPlayerService.this.mPlayer != null) {
							MediaPlayerService.this.mPlayer.play();
						}

						MediaPlayerService.this.mbPauseByBackCar = false;
					}
				}

			}
		};
		this.mPlayer = new MultiMediaPlayer(this, this.mContext);
		this.mStorageState = new MediaPlayerService.MediaStorageState();
	}

	public void Initialize() {
		mPrf = this.mContext.getSharedPreferences(MediaPlayerContants.LAST_MEMORY_MEDIA_INFO, Context.MODE_PRIVATE);
		IntentFilter filter = null;
		filter = new IntentFilter();
		filter.addAction("yecon.intent.action.MEDIA_SCANER_STATUS");
		this.mContext.registerReceiver(this.mReceiverScan, filter);
		filter = new IntentFilter();
		filter.addAction("android.intent.action.MEDIA_MOUNTED");
		filter.addAction("android.intent.action.MEDIA_EJECT");
		filter.addAction("android.intent.action.MEDIA_REMOVED");
		filter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
		filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
		filter.addDataScheme("file");
		this.mContext.registerReceiver(this.mReceiverStorage, filter);
		filter = new IntentFilter();
		filter.addAction(McuExternalConstant.MCU_ACTION_MEDIA_NEXT);
		filter.addAction(McuExternalConstant.MCU_ACTION_MEDIA_PREVIOUS);
		filter.addAction("com.yecon.action.MEDIA_PLAY_PAUSE");
		filter.addAction("com.yecon.action.MEDIA_PLAY");
		filter.addAction("com.yecon.action.MEDIA_PAUSE");
		filter.addAction("com.yecon.action.MEDIA_STOP");
		filter.addAction("action.media.info");
		filter.addAction("action.media.random");
		filter.addAction("action.media.repeat");
		filter.addAction("com.yecon.action.ACC_ON");
		filter.addAction("com.yecon.action.ACC_OFF");
		this.mContext.registerReceiver(this.mReceiverMCUKey, filter);
		filter = new IntentFilter();
		filter.addAction("autochips.intent.action.QB_POWEROFF");
		filter.addAction("autochips.intent.action.QB_PREPOWEROFF");
		filter.addAction("autochips.intent.action.QB_POWERON");
		this.mContext.registerReceiver(this.mPowerOnOffReceiver, filter);
		IntentFilter otherFilter = new IntentFilter();
		otherFilter.addAction("com.media.action.hasOtherSourceOpen");
		this.mContext.registerReceiver(this.otherReceiver, otherFilter);
	}

	public void UnInitialize() {
		if (this.mPlayer != null) {
			this.mPlayer.release();
		}

		this.mContext.unregisterReceiver(this.mReceiverScan);
		this.mContext.unregisterReceiver(this.mReceiverStorage);
		this.mContext.unregisterReceiver(this.mPowerOnOffReceiver);
		this.mContext.unregisterReceiver(this.mReceiverMCUKey);
		this.mContext.unregisterReceiver(this.otherReceiver);
		if (this.mPlayer != null) {
			this.mPlayer.releaseWakeLock();
		}

	}

	private int queryFileCount(String path, int type) {
		int iCount = 0;

		try {
			Uri e = YeconMediaStore.getContent(this.getDBFile(path), "files");
			String selection = "media_type=" + type;
			Cursor c = this.mContext.getContentResolver().query(e,
					(String[]) null, selection, (String[]) null, (String) null);
			if (c != null) {
				iCount = c.getCount();
				c.close();
			}
		} catch (Exception arg6) {
			arg6.printStackTrace();
		}

		return iCount;
	}

	public void RegisterSource(int mediaType) {
		LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
				"++RegisterSource++" + mediaType);

		try {
			this.mMediaType = mediaType;
			if (this.mMediaType == MediaType.MEDIA_AUDIO) {
				this.mSourceTocken = SourceManager.registerSourceListener(
						this.mContext, 1);
			} else if (this.mMediaType == MediaType.MEDIA_VIDEO) {
				this.mSourceTocken = SourceManager.registerSourceListener(
						this.mContext, 2);
			}

			if (this.mSourceTocken != null) {
				SourceManager.setAudioFocusNotify(this.mSourceTocken,
						this.mAudioFocusCB);
				SourceManager.setSourceNotify(this.mSourceTocken,
						this.mSourceNotifyCB);
				this.requestActiveSource();
			}
		} catch (Exception arg2) {
			arg2.printStackTrace();
		}

		LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
				"--RegisterSource--"
						+ (this.mSourceTocken == null ? " failed "
								: " success "));
	}

	public void UnregisterSource() {
		LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),"++UnregisterSource++");

		try {
			this.mPlayer.release();
			if (this.mSaveData != null) {
				this.mSaveData.setMediaSongName("");
				this.mSaveData.setMediaTrack(0);
				this.mSaveData.setMediaTotalNum(0);
				this.mSaveData.setMediaPlayTime(0);
			}

			SourceManager.unregisterSourceListener(this.mSourceTocken);
			this.mSourceTocken = null;
			this.miWaitDatabase = 0;
		} catch (Exception arg1) {
			arg1.printStackTrace();
		}

		LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
				"--UnregisterSource--");
	}

	@SuppressLint({ "WorldReadableFiles" })
	public boolean requestAttachStorage(String strPath) throws RemoteException {
		String arg1 = "MediaPlayerService";
		Log.e("YG_MusicPlayer", "strPath : " + strPath);
		synchronized ("MediaPlayerService") {
			boolean bLastMemoryDevice = false;
			if (strPath == null) {
				bLastMemoryDevice = true;
			} else if (strPath.equals(MediaPlayerContants.LAST_MEMORY_DEVICE)) {
				bLastMemoryDevice = true;
			} else {
				strPath = YeconMediaStore.getStoragePath(strPath);
			}

			try {
				if (bLastMemoryDevice) {
					strPath = this.getLastMemoryStorage(this.mMediaType);
					strPath = YeconMediaStore.getStoragePath(strPath);
					if (strPath == null
							|| !YeconMediaStore.checkStorageExist(this.mEnv,
									strPath)) {
						if (YeconMediaStore.checkStorageExist(this.mEnv,
								"/mnt/ext_sdcard1")) {
							strPath = "/mnt/ext_sdcard1";
						} else if (YeconMediaStore.checkStorageExist(this.mEnv,
								"/mnt/ext_sdcard2")) {
							strPath = "/mnt/ext_sdcard2";
						} else if (YeconMediaStore.checkStorageExist(this.mEnv,
								"/mnt/udisk1")) {
							strPath = "/mnt/udisk1";
						} else if (YeconMediaStore.checkStorageExist(this.mEnv,
								"/mnt/udisk2")) {
							strPath = "/mnt/udisk2";
						} else if (YeconMediaStore.checkStorageExist(this.mEnv,
								"/mnt/udisk3")) {
							strPath = "/mnt/udisk3";
						} else if (YeconMediaStore.checkStorageExist(this.mEnv,
								"/mnt/udisk4")) {
							strPath = "/mnt/udisk4";
						} else if (YeconMediaStore.checkStorageExist(this.mEnv,
								"/mnt/udisk5")) {
							strPath = "/mnt/udisk5";
						} else {
							strPath = "/mnt/sdcard";
						}
					}
				}
			} catch (Exception arg7) {
				arg7.printStackTrace();
			}

			if (strPath != null) {
				String strDBFile = this.getDBFile(strPath);
				strPath = YeconMediaStore.getStoragePath(strPath);
				LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
						"attach storage : " + strPath + ", database:"
								+ strDBFile);
				if (!this.mDBFile.equals(strDBFile)) {
					if (this.mRecoverThread != null) {
						this.mRecoverThread.interrupt();
						this.mRecoverThread = null;
					}

					try {
						if (this.mPlayer != null) {
							if (this.mPlayer.getPlayState() == PlayStatus.STARTED
									|| this.mPlayer.getPlayState() == PlayStatus.PAUSED) {
								LogUtil.printError("MediaPlayerService",
										LogUtil._FUNC_(), "save last memory!!!"
												+ strPath);
								this.mStatus.writePreference(this.mContext,
										this.mDBFile, this.mMediaType);
							}

							this.mPlayer.release();
						}
					} catch (Exception arg6) {
						arg6.printStackTrace();
					}

					LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
							"read last memory!!!" + strPath);
					this.mDBFile = strDBFile;
					this.mList = new MediaPlayerService.MediaList();
					this.mRecoverThread = new MediaPlayerService.RecorverThread();
					this.mRecoverThread.setName(strPath);
					this.mRecoverThread.start();

					try {
						if (this.mSourceTocken != null) {
							SourceManager.updatePlayingPath(this.mSourceTocken,
									strPath);
						}
					} catch (Exception arg5) {
						arg5.printStackTrace();
					}
					
					{
						boolean hasSourceStack = false;
			        	Intent intent = new Intent();
			        	if(MediaPlayerContants.LAST_MEMORY_MEDIA_TAG_SD.equals(getMediaStorageType(strPath))){
			        		String actionName = Context.ACTION_SOURCE_STACK_SD;
			        		intent.setAction(actionName);
			        		hasSourceStack = true;
			        	}else if(MediaPlayerContants.LAST_MEMORY_MEDIA_TAG_USB.equals(getMediaStorageType(strPath))){
			        		String actionName = Context.ACTION_SOURCE_STACK_USB;
			        		intent.setAction(actionName);
			        		hasSourceStack = true;
			        	}
			        	String packageName = mContext.getPackageName();
			        	if("com.yecon.music".equals(packageName)){
			        		intent.putExtra(EXTRA_APK_PACKAGENAME, "com.yecon.music");
			        		intent.putExtra(EXTRA_APK_ACTIVITY, "com.yecon.music.MusicPlaybackMainActivity");
			        	}else if("com.yecon.video".equals(packageName)){
			        		intent.putExtra(EXTRA_APK_PACKAGENAME, "com.yecon.video");
			        		intent.putExtra(EXTRA_APK_ACTIVITY, "com.yecon.video.VideoPlaybackMainActivity");
			        	}else{
			        		hasSourceStack = false;
			        	}
			        	
			        	if(hasSourceStack){
			        		mContext.sendOrderedBroadcast(intent,null);
			        	}
			        }
					
					this.SaveLastMemoryStorage(strPath, this.mMediaType);
					return true;
				}

				String action = getStorageStateFromSP(this.mContext, strPath);
				if (!action.equals("scan_analysis")
						&& !action.equals("scan_finish")) {
					this.updateServiceStatus(1, strPath);
				} else {
					this.updateServiceStatus(2, strPath);
				}
			} else if (this.mStorageState != null
					&& this.mStorageState.mListStorage.isEmpty()) {
				this.updateServiceStatus(7, (String) null);
			}

			return false;
		}
	}

	public String getDBFile(String strPath) {
		String strDatabase = "external";
		if (strPath.contains("/mnt/ext_sdcard1")) {
			strDatabase = "sdcard1";
		} else if (strPath.contains("/mnt/ext_sdcard2")) {
			strDatabase = "sdcard2";
		} else if (strPath.contains("/mnt/udisk1")) {
			strDatabase = "udisk1";
		} else if (strPath.contains("/mnt/udisk2")) {
			strDatabase = "udisk2";
		} else if (strPath.contains("/mnt/udisk3")) {
			strDatabase = "udisk3";
		} else if (strPath.contains("/mnt/udisk4")) {
			strDatabase = "udisk4";
		} else if (strPath.contains("/mnt/udisk5")) {
			strDatabase = "udisk5";
		} else if (strPath.contains("/mnt/sdcard")) {
			strDatabase = "external";
		} else {
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),"unknown path " + strPath);
		}

		return strDatabase;
	}

	public void registerUserInterface(IMediaPlayerActivity mpi)
			throws RemoteException {
		String arg1 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			Iterator arg3 = this.mListUI.iterator();

			while (arg3.hasNext()) {
				IMediaPlayerActivity ui = (IMediaPlayerActivity) arg3.next();
				if (ui.equals(mpi)) {
					return;
				}
			}

			this.mListUI.add(mpi);
		}
	}

	public void unregisterUserInterface(IMediaPlayerActivity mpi)
			throws RemoteException {
		String arg1 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			Iterator arg3 = this.mListUI.iterator();

			while (arg3.hasNext()) {
				IMediaPlayerActivity ui = (IMediaPlayerActivity) arg3.next();
				if (ui.equals(mpi)) {
					this.mListUI.remove(ui);
					return;
				}
			}

		}
	}

	public void requestRandom() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			this.mStatus.iRandomMode = this.mStatus.iRandomMode != 0 ? 0 : 1;
			Iterator arg2 = this.mListUI.iterator();

			IMediaPlayerActivity iMediaPlayerActivity;
			while (arg2.hasNext()) {
				iMediaPlayerActivity = (IMediaPlayerActivity) arg2.next();
				iMediaPlayerActivity
						.updateRandomStatus(this.mStatus.iRandomMode);
			}

			if (this.mStatus.iRandomMode == 1 && this.mStatus.iRepeatMode == 1) {
				this.mStatus.iRepeatMode = 2;
				arg2 = this.mListUI.iterator();

				while (arg2.hasNext()) {
					iMediaPlayerActivity = (IMediaPlayerActivity) arg2.next();
					iMediaPlayerActivity
							.updateRepeatStatus(this.mStatus.iRepeatMode);
				}
			}

		}
	}

	public void requestRepeat() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			this.mStatus.iRepeatMode = (this.mStatus.iRepeatMode + 1) % 3;
			Iterator arg2 = this.mListUI.iterator();

			IMediaPlayerActivity iMediaPlayerActivity;
			while (arg2.hasNext()) {
				iMediaPlayerActivity = (IMediaPlayerActivity) arg2.next();
				iMediaPlayerActivity
						.updateRepeatStatus(this.mStatus.iRepeatMode);
			}

			if (this.mStatus.iRepeatMode == 1 && this.mStatus.iRandomMode == 1) {
				this.mStatus.iRandomMode = 0;
				arg2 = this.mListUI.iterator();

				while (arg2.hasNext()) {
					iMediaPlayerActivity = (IMediaPlayerActivity) arg2.next();
					iMediaPlayerActivity
							.updateRandomStatus(this.mStatus.iRandomMode);
				}
			}

		}
	}

	public int getMediaType() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return this.mMediaType;
		}
	}

	public void SetPlayListType(int iType) {
		if (MediaListContants.isFileList(iType)) {
			if (iType != this.mStatus.iPlayListType) {
				this.mStatus.iPlayListType = iType;
				this.mStatus.iFilePos = -1;
			}

			switch (iType) {
			case Type.ALL_FILE:
				this.mList.mListPlay = this.mList.mListFile;
				this.mStatus.iDirPos = -1;
				this.mStatus.iCurDirPos = -1;
				this.mStatus.iCurAlbumPos = -1;
				this.mStatus.iCurArtistPos = -1;
				this.mStatus.iAlbumPos = -1;
				this.mStatus.iArtistPos = -1;
				break;
			case Type.DIR_FILE:
				this.mList.mListPlay = this.mList.mListDirFile;
				this.mStatus.iDirPos = this.mStatus.iCurDirPos;
				this.mStatus.iCurAlbumPos = -1;
				this.mStatus.iCurArtistPos = -1;
				this.mStatus.iAlbumPos = -1;
				this.mStatus.iArtistPos = -1;
				break;
			case Type.ALBUM_FILE:
				this.mList.mListPlay = this.mList.mListAlbumFile;
				this.mStatus.iAlbumPos = this.mStatus.iCurAlbumPos;
				this.mStatus.iCurDirPos = -1;
				this.mStatus.iCurArtistPos = -1;
				this.mStatus.iDirPos = -1;
				this.mStatus.iArtistPos = -1;
				break;
			case Type.ARTIST_FILE:
				this.mList.mListPlay = this.mList.mListArtistFile;
				this.mStatus.iArtistPos = this.mStatus.iCurArtistPos;
				this.mStatus.iCurDirPos = -1;
				this.mStatus.iCurAlbumPos = -1;
				this.mStatus.iDirPos = -1;
				this.mStatus.iAlbumPos = -1;
				break;
			case Type.FAVORITE_FILE:
				this.mList.mListPlay = this.mList.mListFavorFile;
				this.mStatus.iDirPos = -1;
				this.mStatus.iCurDirPos = -1;
				this.mStatus.iCurAlbumPos = -1;
				this.mStatus.iCurArtistPos = -1;
				this.mStatus.iAlbumPos = -1;
				this.mStatus.iArtistPos = -1;
			}

			try {
				Iterator arg2 = this.mListUI.iterator();

				while (arg2.hasNext()) {
					IMediaPlayerActivity e = (IMediaPlayerActivity) arg2.next();
					e.updateListData(iType);
				}
			} catch (Exception arg3) {
				arg3.printStackTrace();
			}
		} else {
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"requestSetPlayList is not a file list!!!");
		}

	}

	public void requestPlayList() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			if (this.mList.mListPlay.size() > 0) {
				Iterator arg2 = this.mListUI.iterator();

				while (arg2.hasNext()) {
					IMediaPlayerActivity iMediaPlayerActivity = (IMediaPlayerActivity) arg2
							.next();
					iMediaPlayerActivity.updateListData(1);
				}
			} else {
				this.queryAllFileList(true);
			}

		}
	}

	public void requestFavoriteList() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			this.queryFavoriteList(true);
		}
	}

	public boolean requestFavoriteAdd(int iList, int iPos)
			throws RemoteException {
		String arg2 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return this.updateFavorite(iList, iPos, true);
		}
	}

	public boolean requestFavoriteErase(int iList, int iPos)
			throws RemoteException {
		String arg2 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return this.updateFavorite(iList, iPos, false);
		}
	}

	private boolean updateFavorite(int iList, int iPos, boolean bFavor) {
		String arg3 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			boolean bRet = false;

			try {
				int e = this.queryRealFileID(iList, iPos);
				if (e != -1 && this.mContext != null) {
					Uri uri = YeconMediaStore.getContent(this.mDBFile, "files");
					ContentValues values = new ContentValues();
					values.put("favorite", Integer.valueOf(bFavor ? 1 : 0));
					String where = "_id=" + e;
					if (this.mContext.getContentResolver().update(uri, values,
							where, (String[]) null) > 0) {
						bRet = true;
						this.queryAllFileList(true);
						if (iList == Type.ALBUM_FILE) {
							this.queryAlbumFileList(true,
									this.mStatus.iCurAlbumPos);
						} else if (iList == Type.DIR_FILE) {
							this.queryDirFileList(true, this.mStatus.iCurDirPos);
						} else if (iList == Type.ARTIST_FILE) {
							this.queryArtistFileList(true,
									this.mStatus.iCurArtistPos);
						}

						if (this.mStatus.iPlayListType == Type.FAVORITE_FILE) {
							if (!bFavor) {
								this.queryFavoriteList(false);
								if (this.mStatus.iFilePos > iPos) {
									this.mStatus.iFilePos = this.mStatus.iFilePos - 1;
								} else if (this.mStatus.iFilePos == iPos) {
									this.mStatus.iFilePos = this.mStatus.iFilePos - 1;
									this.requestNext();
								}
							} else {
								this.queryFavoriteList(true);
							}

							this.mStatus.mPlayingTrackInfo.recycle();
							this.mStatus.mPlayingTrackInfo = null;
							this.updatePlayState();
						} else {
							this.queryFavoriteList(true);
						}
					}
				}
			} catch (Exception arg9) {
				arg9.printStackTrace();
			}

			return bRet;
		}
	}

	public void requestAllFileList() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			this.queryAllFileList(true);
		}
	}

	private void queryFavoriteList(boolean bAsync) {
		Uri uri = YeconMediaStore.getContent(this.mDBFile, "files");
		String[] columns = MediaPlayerContants.FILE_COLUMNS;
		String selection = "media_type=" + this.mMediaType + " and "
				+ "favorite" + "=1";
		Object selectionArgs = null;
		Object orderBy = null;
		this.mListQueryHandler.startQuery(Boolean.valueOf(bAsync), Type.FAVORITE_FILE,
				(Object) null, uri, columns, selection,
				(String[]) selectionArgs, (String) orderBy);
	}

	private void queryAllFileList(boolean bAsync) {
		Uri uri = YeconMediaStore.getContent(this.mDBFile, "files");
		String[] columns = MediaPlayerContants.FILE_COLUMNS;
		String selection = "media_type=" + this.mMediaType;
		Object selectionArgs = null;
		Object orderBy = null;
		this.mListQueryHandler.startQuery(Boolean.valueOf(bAsync), Type.ALL_FILE,
				(Object) null, uri, columns, selection,
				(String[]) selectionArgs, (String) orderBy);
	}

	public void requestFileListByDir(int iDirPos) throws RemoteException {
		String arg1 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			this.queryDirFileList(true, iDirPos);
		}
	}

	private void queryDirFileList(boolean bAsync, int iDirPos) {
		Uri uri = YeconMediaStore.getContent(this.mDBFile, "files");
		String[] columns = MediaPlayerContants.FILE_COLUMNS;
		String selection = "media_type=" + this.mMediaType;
		if (iDirPos >= 0 && iDirPos < this.mList.mListDir.size()) {
			this.mStatus.iCurDirPos = iDirPos;
			selection = selection + " and ";
			int selectionArgs = ((MediaObject) this.mList.mListDir.get(iDirPos))
					.getID();
			selection = selection + "parent_id=" + selectionArgs;
		}

		Object selectionArgs1 = null;
		Object orderBy = null;
		this.mListQueryHandler.startQuery(Boolean.valueOf(bAsync), Type.DIR_FILE,
				(Object) null, uri, columns, selection,
				(String[]) selectionArgs1, (String) orderBy);
	}
	//FIXME:getPlayingMediaPosInDir;
	public int getPlayingMediaPosInDir() throws RemoteException{
		if(this.mList.mListDir != null && this.mList.mListDir.size() > 0){
			MediaTrackInfo trackInfo = getPlayingFileInfo();
			if(trackInfo != null){
				for(int i = 0; i < this.mList.mListDir.size(); i++){
					MediaObject mediaObject = this.mList.mListDir.get(i);
					String dirPath = mediaObject.getPath();
					if(TextUtils.isEmpty(trackInfo.getPath()) || TextUtils.isEmpty(dirPath)){
					}else if(trackInfo.getPath().contains(dirPath)){
						Log.e(TAG, "PlayingMediaPosInDir:" + dirPath + "    ||   " + trackInfo.getPath() + "  pos:" + i);
						return i;
					}
					Log.e(TAG, "PlayingMediaPosInDir:" + dirPath + "    ||   " + trackInfo.getPath());
				}
			}
		}
		return -1;
	}
	
	public void requestFileListByAlbum(int iAlbumPos) throws RemoteException {
		String arg1 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			this.queryAlbumFileList(true, iAlbumPos);
		}
	}

	private void queryAlbumFileList(boolean bAsync, int iAlbumPos) {
		Uri uri = YeconMediaStore.getContent(this.mDBFile, "files");
		String[] columns = MediaPlayerContants.FILE_COLUMNS;
		String selection = "media_type=" + this.mMediaType;
		if (iAlbumPos >= 0 && iAlbumPos < this.mList.mListAlbum.size()) {
			this.mStatus.iCurAlbumPos = iAlbumPos;
			selection = selection + " and ";
			int selectionArgs = ((MediaObject) this.mList.mListAlbum
					.get(iAlbumPos)).getID();
			selection = selection + "album_id=" + selectionArgs;
		}

		Object selectionArgs1 = null;
		Object orderBy = null;
		this.mListQueryHandler.startQuery(Boolean.valueOf(bAsync), Type.ALBUM_FILE,
				(Object) null, uri, columns, selection,
				(String[]) selectionArgs1, (String) orderBy);
	}

	public void requestFileListByArtist(int iArtistPos) throws RemoteException {
		String arg1 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			this.queryArtistFileList(true, iArtistPos);
		}
	}

	private void queryArtistFileList(boolean bAsync, int iArtistPos) {
		Uri uri = YeconMediaStore.getContent(this.mDBFile, "files");
		String[] columns = MediaPlayerContants.FILE_COLUMNS;
		String selection = "media_type=" + this.mMediaType;
		if (iArtistPos >= 0 && iArtistPos < this.mList.mListArtist.size()) {
			this.mStatus.iCurArtistPos = iArtistPos;
			selection = selection + " and ";
			int selectionArgs = ((MediaObject) this.mList.mListArtist
					.get(iArtistPos)).getID();
			selection = selection + "artist_id=" + selectionArgs;
		}

		Object selectionArgs1 = null;
		Object orderBy = null;
		this.mListQueryHandler.startQuery(Boolean.valueOf(bAsync), Type.ARTIST_FILE,
				(Object) null, uri, columns, selection,
				(String[]) selectionArgs1, (String) orderBy);
	}

	public void requestDirList() {
		this.queryDirList(true);
	}

	private void queryDirList(boolean bAsyc) {
		Uri uri = YeconMediaStore.getContent(this.mDBFile, "dir");
		String[] columns = MediaPlayerContants.DIR_COLUMNS;
		Object selection = null;
		Object selectionArgs = null;
		Object orderBy = null;
		this.mListQueryHandler.startQuery(Boolean.valueOf(bAsyc), Type.ALL_DIR,
				(Object) null, uri, columns, (String) selection,
				(String[]) selectionArgs, (String) orderBy);
	}

	public void requestAlbumList() {
		this.queryAlbumList(true);
	}

	private void queryAlbumList(boolean bAsyc) {
		Uri uri = YeconMediaStore.getContent(this.mDBFile, "album");
		String[] columns = MediaPlayerContants.ALBUM_COLUMNS;
		Object selection = null;
		Object selectionArgs = null;
		Object orderBy = null;
		this.mListQueryHandler.startQuery(Boolean.valueOf(bAsyc), Type.ALL_ALBUM,
				(Object) null, uri, columns, (String) selection,
				(String[]) selectionArgs, (String) orderBy);
	}

	public void requestArtistList() {
		this.queryArtistList(true);
	}

	private void queryArtistList(boolean bAsyc) {
		Uri uri = YeconMediaStore.getContent(this.mDBFile, "artist");
		String[] columns = MediaPlayerContants.ARTIST_COLUMNS;
		Object selection = null;
		Object selectionArgs = null;
		Object orderBy = null;
		this.mListQueryHandler.startQuery(Boolean.valueOf(bAsyc), Type.ALL_ARTIST,
				(Object) null, uri, columns, (String) selection,
				(String[]) selectionArgs, (String) orderBy);
	}

	public void requestSaveLastMemory() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			this.mStatus.writePreference(this.mContext, this.mDBFile,
					this.mMediaType);
		}
	}

	public void requestPlay(int iListType, int iPos) throws RemoteException {
		String arg2 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			this.SetPlayListType(iListType);
			if (iPos != -1 && iPos < this.mList.mListPlay.size()) {
				String path = ((MediaObject) this.mList.mListPlay.get(iPos))
						.getPath();
				if (this.mStatus.strFile.equals(path)
						&& this.mStatus.iFilePos == iPos
						&& this.mStatus.iPlayListType == iListType) {
					this.mPlayer.play();
				} else {
					MediaObject cv = (MediaObject) this.mList.mListPlay
							.get(iPos);
					String strFile = cv.getPath();
					this.mStatus.iFilePos = iPos;
					this.mStatus.iProgress = 0;
					this.decode(strFile);
				}
			}
			//add
			if(iListType == Type.DIR_FILE){
				this.mList.mCurrentPlayList.clear();
				for(MediaObject mediaObject : this.mList.mListPlay){
					this.mList.mCurrentPlayList.add(mediaObject);
				}
				mCurrentPlayDirPos = mStatus.iDirPos;
			}
			//add end
		}
	}

	public void requestPause() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			if (this.mPlayer.getCurrentRealState() == 4) {
				this.mPlayer.pause();
			} else if (this.mPlayer.getCurrentRealState() == 2
					|| this.mPlayer.getCurrentRealState() == 5
					|| this.mPlayer.getCurrentRealState() == 1) {
				this.mPlayer.play();
			}

		}
	}

	public void requestStart() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			if (this.mPlayer != null) {
				this.mStatus.readPreference(this.mContext, this.mDBFile,
						this.mMediaType);
				this.requestActiveSource();
				this.recoverPlay();
			}
			
			this.mList.mCurrentPlayList.clear();
			for(MediaObject mediaObject : this.mList.mListPlay){
				this.mList.mCurrentPlayList.add(mediaObject);
			}
			mCurrentPlayDirPos = mStatus.iDirPos;

		}
	}

	public void requestStop() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			if (this.mPlayer != null) {
				this.mStatus.writePreference(this.mContext, this.mDBFile,
						this.mMediaType);
				this.mPlayer.release();
				SourceManager.releaseSource(this.mSourceTocken);
				this.mSourceTocken = null;
				this.mbLostbAudioFocusForever = true;
			}

		}
	}

	public void requestSetFrontDisplay(SurfaceHolder holder)
			throws RemoteException {
		String arg1 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			if (!isBackCarORPhone(this.mContext)) {
				this.mPlayer.setFrontDisplay(holder);
			} else {
				Log.e("MediaPlayerService",
						"[requestSetFrontDisplay] under backcar or bt call, ignore");
			}

		}
	}

	public void requestSetRearDisplay(SurfaceHolder holder)
			throws RemoteException {
		String arg1 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			if (!isBackCarORPhone(this.mContext)) {
				this.mPlayer.setRearDisplay(holder);
			} else {
				Log.e("MediaPlayerService",
						"[requestSetRearDisplay] under backcar or bt call, ignore");
			}

		}
	}

	public void requestSeek(int lPos) throws RemoteException {
		String arg1 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			if (this.mPlayer.getDuration() > lPos) {
				this.mPlayer.seek(lPos);
			} else {
				LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
						"illegal parameter, ignore seek!!!");
			}

		}
	}

	public void requestNext() throws RemoteException {
		this.execNext();
	}

	public void requestPrev() throws RemoteException {
		this.execPrev();
	}

	private void execNext() {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			try {
				int size = this.mList.mListPlay.size();
				if (size == 0) {
					Log.e("MediaPlayerService",
							"current list is empty ! ignore play next");
					this.mPlayer.stop();
					return;
				}

				boolean iID = true;
				int pos;
				if (this.mStatus.iRepeatMode == 1) {
					pos = this.mStatus.iFilePos;
				} else {
					int icount;
					if (this.mStatus.iRepeatMode == 0) {
						if (this.mStatus.iRandomMode == 0) {
							if (this.mStatus.iFilePos >= size - 1) {
								if (this.getPlayStatus() != 5
										&& this.getPlayStatus() != 4) {
									this.mStatus.iFilePos = -1;
								}
								String path = null;

								try {
									path = this.getPlayingStorage().getPath();
								} catch (Exception ex) {
									ex.printStackTrace();
								}

								this.updateServiceStatus(4, path);
								return;
							}

							pos = this.mStatus.iFilePos + 1;
						} else {
							pos = this.getRandom(0, size);
							if (size > 1) {
								for (icount = 10; pos == this.mStatus.iFilePos
										&& icount-- > 0; pos = this.getRandom(
										0, size)) {
									;
								}
							}
						}
					} else if (this.mStatus.iRandomMode == 0) {
						pos = (this.mStatus.iFilePos + 1) % size;
					} else {
						pos = this.getRandom(0, size);
						if (size > 1) {
							for (icount = 10; pos == this.mStatus.iFilePos
									&& icount-- > 0; pos = this
									.getRandom(0, size)) {
								;
							}
						}
					}
				}

				if (pos != -1 && pos < size) {
					this.mStatus.iFilePos = -1;
					this.requestPlay(this.mStatus.iPlayListType, pos);
				} else {
					this.requestStop();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private void execPrev() {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			try {
				this.mbPrevAction = true;
				boolean bl = true;
				int iSize = this.mList.mListPlay.size();
				if (iSize != 0) {
					int pos;
					if (this.mStatus.iRepeatMode == 1) {
						pos = this.mStatus.iFilePos;
					} else {
						int icount;
						if (this.mStatus.iRepeatMode == 0) {
							if (this.mStatus.iRandomMode == 0) {
								if (this.mStatus.iFilePos > 0) {
									pos = this.mStatus.iFilePos - 1;
								} else {
									pos = this.mStatus.iFilePos;
								}
							} else {
								pos = this.getRandom(0, iSize);
								if (iSize > 1) {
									for (icount = 10; pos == this.mStatus.iFilePos
											&& icount-- > 0; pos = this
											.getRandom(0, iSize)) {
										;
									}
								}
							}
						} else if (this.mStatus.iRandomMode == 0) {
							if (this.mStatus.iFilePos > 0) {
								pos = this.mStatus.iFilePos - 1;
							} else {
								pos = iSize - 1;
							}
						} else {
							pos = this.getRandom(0, iSize);
							if (iSize > 1) {
								for (icount = 10; pos == this.mStatus.iFilePos
										&& icount-- > 0; pos = this.getRandom(
										0, iSize)) {
									;
								}
							}
						}
					}

					if (pos != -1 && pos < iSize) {
						this.mStatus.iFilePos = -1;
						this.requestPlay(this.mStatus.iPlayListType, pos);
						return;
					}

					this.requestStop();
					return;
				}

				Log.e("MediaPlayerService",
						"current list is empty ! ignore play prev");
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

		}
	}

	public void requestActiveSource() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			try {
				if (this.mSourceTocken != null && this.mbLostbAudioFocusForever) {
					SourceManager.acquireSource(this.mSourceTocken);
					Log.e("MediaPlayerService", "requestActiveSource");
					this.mbOccupyAudioFocus = true;
					this.mbLostbAudioFocusForever = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public void requestDeactiveSource() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			if (this.getPlayingStorage() != null) {
				if (this.mContext != null && this.mDBFile != null) {
					this.mStatus.writePreference(this.mContext, this.mDBFile,
							this.mMediaType);
				}
			} else {
				try {
					//FIXME:yfzhang 退出进源(yecon)
					if (this.mSourceTocken != null) {
						if (this.mMediaType == 1) {
							/*SourceManager.hotPlugSource(this.mContext, 1,
									(String) null, false);*/
						} else {
							/*SourceManager.hotPlugSource(this.mContext, 2,
									(String) null, false);*/
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	public int getPlayStatus() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return this.mPlayer.getPlayState();
		}
	}

	public int getRandomStatus() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return this.mStatus.iRandomMode;
		}
	}

	public int getRepeatStatus() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return this.mStatus.iRepeatMode;
		}
	}

	public long getFilePosInList() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			int iPos = -1;
			switch (this.mStatus.iPlayListType) {
			case Type.PLAY_FILE:
				iPos = this.mStatus.iFilePos;
				break;
			case Type.ALL_FILE:
				iPos = this.mStatus.iFilePos;
				break;
			case Type.DIR_FILE:
				if (this.mStatus.iDirPos != -1
						&& this.mStatus.iCurDirPos == this.mStatus.iDirPos) {
					iPos = this.mStatus.iFilePos;
				}
				break;
			case Type.ALBUM_FILE:
				if (this.mStatus.iAlbumPos != -1
						&& this.mStatus.iCurAlbumPos == this.mStatus.iAlbumPos) {
					iPos = this.mStatus.iFilePos;
				}
				break;
			case Type.ARTIST_FILE:
				if (this.mStatus.iArtistPos != -1
						&& this.mStatus.iCurArtistPos == this.mStatus.iArtistPos) {
					iPos = this.mStatus.iFilePos;
				}
				break;
			case Type.FAVORITE_FILE:
				iPos = this.mStatus.iFilePos;
			}

			return (long) iPos;
		}
	}

	public long getDirPosInList() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return (long) this.mStatus.iDirPos;
		}
	}

	public long getArtistPosInList() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return (long) this.mStatus.iArtistPos;
		}
	}

	public long getAlbumPosInList() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return (long) this.mStatus.iAlbumPos;
		}
	}

	public byte[] getApicData(int iFileID) throws RemoteException {
		String arg1 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			byte[] buf = null;
			if (this.mDBFile != null) {
				Uri uri = YeconMediaStore.getContent(this.mDBFile, "apic");
				String selection = "file_id=" + iFileID;
				Cursor c = this.mContext.getContentResolver().query(uri,
						(String[]) null, selection, (String[]) null,
						(String) null);
				if (c != null) {
					c.moveToFirst();
					if (c.getCount() > 0) {
						int columnIndex = c.getColumnIndex("img_data");
						Log.e("MediaPlayerService", c.getColumnCount() + " "
								+ columnIndex);
						buf = c.getBlob(columnIndex);
					}

					c.close();
				}
			}

			return buf;
		}
	}

	public void onQueryComplete(int token, Object cookie, Cursor cursor) {
		String arg3 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			boolean bNeedUpdateUI = true;
			if (cursor != null) {
				cursor.moveToFirst();
				int iMediaPlayerIndex;
				label117: switch (token) {
				case Type.PLAY_FILE:
					this.mList.mListPlay.clear();
					iMediaPlayerIndex = 0;

					while (true) {
						if (iMediaPlayerIndex >= cursor.getCount()) {
							break label117;
						}

						this.mList.mListPlay
								.add(this.getMediaObject(cursor, 1));
						cursor.moveToNext();
						++iMediaPlayerIndex;
					}
				case Type.ALL_FILE:
					this.mList.mListFile.clear();
					iMediaPlayerIndex = 0;

					while (true) {
						if (iMediaPlayerIndex >= cursor.getCount()) {
							break label117;
						}

						this.mList.mListFile
								.add(this.getMediaObject(cursor, 1));
						cursor.moveToNext();
						++iMediaPlayerIndex;
					}
				case Type.ALL_DIR:
					this.mList.mListDir.clear();
					iMediaPlayerIndex = 0;

					while (true) {
						if (iMediaPlayerIndex >= cursor.getCount()) {
							break label117;
						}

						byte iAmountColumns = 3;
						if (this.mMediaType == 2) {
							iAmountColumns = 5;
						} else if (this.mMediaType == 3) {
							iAmountColumns = 4;
						}

						if (cursor.getInt(iAmountColumns) > 0) {
							this.mList.mListDir.add(this.getMediaObject(cursor,
									2));
						}

						cursor.moveToNext();
						++iMediaPlayerIndex;
					}
				case Type.DIR_FILE:{
					this.mList.mListDirFile.clear();
					iMediaPlayerIndex = 0;
					int index = 0;
					while (true) {
						if (iMediaPlayerIndex >= cursor.getCount()) {
							//FIXME:yfzhang
							try {
								if(isInitPlaying && defaultListType == Type.DIR_FILE){
									isInitPlaying = false;
									requestPlay(Type.DIR_FILE, index);
								}
							} catch (Exception e) {
								Log.e(TAG,e.toString());
							}
							//
							break label117;
						}
						
						MediaObject mediaObject = this.getMediaObject(cursor,1);
						this.mList.mListDirFile.add(mediaObject);
						
						//FIXME:yfzhang insert usbdisck or sdcard break point momery
						try {
							if(isInitPlaying && defaultListType == Type.DIR_FILE){
								String type = getMediaStorageType(mediaObject.getPath());
								String json = mPrf.getString(type, null);
								if(!TextUtils.isEmpty(json)){
									JSONObject jsonObject = new JSONObject(json);
									String lastPath = jsonObject.getString("path");
									//Log.e(TAG,"yfzhang:"  + mediaObject.getPath() + "  json: " + lastPath);
									if(mediaObject.getPath().equals(lastPath)){
										//FIXME:this add momery for mount unmount
										/* 加上这里可以有插拔断点媒体对象记忆
										 * index = iMediaPlayerIndex;
										mPrf.edit().putBoolean(type + "_hasProgres", true).commit();*/
//										Log.w("MediaPlayerService", "_hasProgres:" + true);
									} 
								}
							}
						} catch (Exception e) {Log.e(TAG,"yfzhang" + e.toString());}
						//
						
						cursor.moveToNext();
						++iMediaPlayerIndex;
					}
					
				}
				case Type.ALL_ALBUM:
					this.mList.mListAlbum.clear();
					iMediaPlayerIndex = 0;

					while (true) {
						if (iMediaPlayerIndex >= cursor.getCount()) {
							break label117;
						}

						this.mList.mListAlbum.add(this
								.getMediaObject(cursor, 3));
						cursor.moveToNext();
						++iMediaPlayerIndex;
					}
				case Type.ALBUM_FILE:
					this.mList.mListAlbumFile.clear();
					iMediaPlayerIndex = 0;

					while (true) {
						if (iMediaPlayerIndex >= cursor.getCount()) {
							break label117;
						}

						this.mList.mListAlbumFile.add(this.getMediaObject(
								cursor, 1));
						cursor.moveToNext();
						++iMediaPlayerIndex;
					}
				case Type.ALL_ARTIST:
					this.mList.mListArtist.clear();
					iMediaPlayerIndex = 0;

					while (true) {
						if (iMediaPlayerIndex >= cursor.getCount()) {
							break label117;
						}

						this.mList.mListArtist.add(this.getMediaObject(cursor,
								4));
						cursor.moveToNext();
						++iMediaPlayerIndex;
					}
				case Type.ARTIST_FILE:
					this.mList.mListArtistFile.clear();
					iMediaPlayerIndex = 0;

					while (true) {
						if (iMediaPlayerIndex >= cursor.getCount()) {
							break label117;
						}

						this.mList.mListArtistFile.add(this.getMediaObject(
								cursor, 1));
						cursor.moveToNext();
						++iMediaPlayerIndex;
					}
				case Type.FAVORITE_FILE:
					this.mList.mListFavorFile.clear();
					iMediaPlayerIndex = 0;

					while (true) {
						if (iMediaPlayerIndex >= cursor.getCount()) {
							break label117;
						}

						this.mList.mListFavorFile.add(this.getMediaObject(
								cursor, 1));
						cursor.moveToNext();
						++iMediaPlayerIndex;
					}
				default:
					bNeedUpdateUI = false;
				}

				cursor.close();
				this.updatePlayList();
				System.gc();
			}

			if (bNeedUpdateUI) {
				Iterator arg10 = this.mListUI.iterator();

				while (arg10.hasNext()) {
					IMediaPlayerActivity arg11 = (IMediaPlayerActivity) arg10
							.next();

					try {
						arg11.updateListData(token);
					} catch (RemoteException arg8) {
						arg8.printStackTrace();
					}
				}
			}

		}
	}

	private MediaObject getMediaObject(Cursor cursor, int iMode) {
		MediaObject obj = new MediaObject();
		obj.setObjectType(iMode);
		switch (iMode) {
		case Origin.FILE:
			//yfzhang:给名字增加扩展名
			String extName = "";
			try {	
				extName = "." + (cursor.getString(1)).substring(cursor.getString(1).lastIndexOf(".") + 1);
			} catch (Exception e) {
				Log.e(TAG,e.toString());
			}
			obj.setID(cursor.getInt(0));
			obj.setName(cursor.getString(2) + extName);
			obj.setAlbum(cursor.getString(4));
			obj.setArtist(cursor.getString(5));
			obj.setTitle(cursor.getString(3));
			obj.setPath(cursor.getString(1));
			obj.setFavor(cursor.getInt(10));
			break;
		case Origin.DIR:
			obj.setID(cursor.getInt(0));
			obj.setName(cursor.getString(2));
			obj.setPath(cursor.getString(1));
			obj.setAudioCount(cursor.getInt(3));
			obj.setVideoCount(cursor.getInt(5));
			obj.setImageCount(cursor.getInt(4));
			break;
		case Origin.ALBUM:
			obj.setID(cursor.getInt(0));
			obj.setName(cursor.getString(1));
			obj.setAudioCount(cursor.getInt(2));
			break;
		case Origin.ARTIST:
			obj.setID(cursor.getInt(0));
			obj.setName(cursor.getString(1));
			obj.setAudioCount(cursor.getInt(2));
		}

		return obj;
	}

	public int getRandom(int iMin, int iMax) {
		return (int) (Math.random() * (double) (iMax - iMin) + (double) iMin);
	}

	boolean decode(String str) {
		String arg1 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			this.mStatus.strFile = str;
			if (this.mStatus.mPlayingTrackInfo != null) {
				this.mStatus.mPlayingTrackInfo.recycle();
				this.mStatus.mPlayingTrackInfo = null;
			}

			this.mPlayer.acquireWakeLock();
			return this.mPlayer.decode(str);
		}
	}

	public void updatePlayState() {
		boolean bExcuteNextSong = false;
		int iStatus = this.mPlayer.getPlayState();

		try {
			this.getPlayingFileInfo();
		} catch (Exception arg6) {
			arg6.printStackTrace();
		}

		Iterator arg3 = this.mListUI.iterator();

		while (arg3.hasNext()) {
			IMediaPlayerActivity iMediaPlayerActivity = (IMediaPlayerActivity) arg3
					.next();

			try {
				iMediaPlayerActivity.updatePlayStatus(iStatus);
			} catch (RemoteException arg5) {
				arg5.printStackTrace();
			}
		}

		if (iStatus == PlayStatus.ERROR) { 
			if (this.mStatus.iRepeatMode == 1) {
				LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
						"single file repeat, can not play next !!!");
			} else if (this.mPlayErrorID == this.mStatus.iFilePos) {
				LogUtil.printError("MediaPlayerService", LogUtil._FILE_(),
						"has been tried all files, cat not play next !!!");
			} else {
				bExcuteNextSong = true;
			}

			if (this.mPlayErrorID == -1) {
				this.mPlayErrorID = this.mStatus.iFilePos;
			}

			if (this.mPlayer != null) {
				this.mPlayer.reset();
			}

			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"finish, play next song !!!");
		} else if (iStatus == PlayStatus.FINISH) {
			bExcuteNextSong = true;
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"error, play next song !!!");
		} else if (iStatus == PlayStatus.STARTED || iStatus == PlayStatus.DECODED || iStatus == PlayStatus.PAUSED) {
			if (iStatus == PlayStatus.DECODED) {
				this.mPlayErrorID = -1;
				if (isBackCarORPhone(this.mContext)) {
					this.mbBackCar = true;
				}

				if (this.mbBackCar || !this.mbOccupyAudioFocus) {
					if (this.mbBackCar) {
						this.mbPauseByBackCar = true;
					} else if (!this.mbOccupyAudioFocus) {
						this.mbPauseByAudioFocus = true;
					}

					this.mPlayer.pause();
					Log.e("MediaPlayerService", "can not play! mbBackCar:"
							+ this.mbBackCar + " mbOccupyAudioFocus:"
							+ this.mbOccupyAudioFocus);
				}
			}

			this.mbPrevAction = false;
			this.mStatus.writePreference(this.mContext, this.mDBFile,
					this.mMediaType);
		}

		if (bExcuteNextSong) {
			if (this.mbPrevAction) {
				this.execPrev();
			} else {
				this.execNext();
			}
		}

	}

	public void updateServiceStatus(int iState, String strData) {
		Iterator arg3 = this.mListUI.iterator();

		while (arg3.hasNext()) {
			IMediaPlayerActivity iMediaPlayerActivity = (IMediaPlayerActivity) arg3
					.next();

			try {
				iMediaPlayerActivity.updateServiceStatus(iState, strData);
			} catch (RemoteException arg5) {
				arg5.printStackTrace();
			}
		}

	}

	public void updatePlayProgress(int iProgress, int iDuration) {
		Iterator arg3 = this.mListUI.iterator();

		while (arg3.hasNext()) {
			IMediaPlayerActivity iMediaPlayerActivity = (IMediaPlayerActivity) arg3
					.next();

			try {
				iMediaPlayerActivity.updateTimeProcess(iProgress, iDuration);
			} catch (RemoteException arg5) {
				arg5.printStackTrace();
			}
		}

		this.mStatus.iProgress = iProgress;
		this.mStatus.iDuration = iDuration;
		
		try {
//			Log.w("YeconMultiMediaPlayer", "" + this.mStatus.iProgress);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("strFile", this.mStatus.strFile);
			jsonObject.put("iProgress", this.mStatus.iProgress);
			jsonObject.put("iPlayMode", this.mStatus.iPlayListType);
			mPrf.edit().putString(getMediaStorageType(this.mStatus.strFile) + "_progress",jsonObject.toString()).commit();
			this.mStatus.saveProgress(this.mContext, this.mDBFile,this.mMediaType);
		} catch (Exception e) {
			Log.e("MediaPlayerService",e.toString());
		}
	}

	public void updateMediaInfo(int iInfo, int iExtra) {
		Iterator arg3 = this.mListUI.iterator();

		while (arg3.hasNext()) {
			IMediaPlayerActivity iMediaPlayerActivity = (IMediaPlayerActivity) arg3
					.next();

			try {
				iMediaPlayerActivity.updateMediaInfo(iInfo, iExtra);
			} catch (Exception arg5) {
				arg5.printStackTrace();
			}
		}

	}

	public List<MediaStorage> getStorageList() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			this.mStorageState.queryStatus(this.mContext);
			return this.mStorageState.mListStorage;
		}
	}

	public List<MediaObject> getPlayList() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return this.mList.mListPlay;
		}
	}

	public List<MediaObject> getAllFileList() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return this.mList.mListFile;
		}
	}

	public List<MediaObject> getDirList() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return this.mList.mListDir;
		}
	}

	public List<MediaObject> getAlbumList() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return this.mList.mListAlbum;
		}
	}

	public List<MediaObject> getArtistList() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return this.mList.mListArtist;
		}
	}

	public List<MediaObject> getCurDirFileList() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return this.mList.mListDirFile;
		}
	}

	public List<MediaObject> getCurArtistFileList() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return this.mList.mListArtistFile;
		}
	}

	public List<MediaObject> getCurAlbumFileList() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return this.mList.mListAlbumFile;
		}
	}

	public List<MediaObject> getFavoriteFileList() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return this.mList.mListFavorFile;
		}
	}

	public int getPlayListType() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return this.mStatus.iPlayListType;
		}
	}

	public MediaTrackInfo getPlayingFileInfo() throws RemoteException {
		SharedPreferences mediaInfoPref = this.mContext.getSharedPreferences(MediaPlayerContants.LAST_MEMORY_MEDIA_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mediaInfoPref.edit();
		String arg0 = "MediaPlayerService";
		
		//add
		List<MediaObject> mediaPlayingList = this.mList.mListPlay;
		if(this.mList.mCurrentPlayList != null && !this.mList.mCurrentPlayList.isEmpty()){
			mediaPlayingList = this.mList.mCurrentPlayList;
		}
		//
		synchronized ("MediaPlayerService") {
			if (this.mStatus.mPlayingTrackInfo != null && (this.mStatus.mPlayingTrackInfo.getDuration() == 0
							|| this.mStatus.mPlayingTrackInfo.getDuration() != this.mPlayer.getDuration()
							 || this.mList == null
							|| mediaPlayingList == null
							|| mediaPlayingList.isEmpty() || this.mStatus.mPlayingTrackInfo
							.getTrackTotal() != mediaPlayingList.size())) {
				this.mStatus.mPlayingTrackInfo.recycle();
				this.mStatus.mPlayingTrackInfo = null;
			}

			if (this.mStatus.mPlayingTrackInfo == null) {
				MediaTrackInfo trackInfo = new MediaTrackInfo();
				if (this.mList != null && mediaPlayingList != null
						&& mediaPlayingList.size() > this.mStatus.iFilePos
						&& this.mStatus.iFilePos != -1) {
					try {
						MediaObject e = (MediaObject) mediaPlayingList.get(this.mStatus.iFilePos);
						trackInfo.setName(e.getName());
						trackInfo.setTitle(e.getTitle());
						trackInfo.setAlbum(e.getAlbum());
						trackInfo.setArtist(e.getArtist());
						trackInfo.setFileID(e.getID());
						trackInfo.setDuration(this.mPlayer.getDuration());
						trackInfo.setTrackID(this.mStatus.iFilePos);
						trackInfo.setTrackTotal(mediaPlayingList.size());
						trackInfo.setPath(e.getPath());
						this.mSaveData.setMediaSongName(trackInfo.getName());
						this.mSaveData.setMediaTrack(trackInfo.getTrackID());
						this.mSaveData.setMediaTotalNum(trackInfo.getTrackTotal());
						this.mSaveData.setMediaPlayTime(trackInfo.getDuration());
						byte[] buf = this.getApicData(e.getID());
						if (buf != null && buf.length > 0) {
							Bitmap bmpSrc = BitmapFactory.decodeByteArray(buf,0, buf.length);
							int bmpW = bmpSrc.getWidth();
							int bmpH = bmpSrc.getHeight();
							if (bmpW > 0 && bmpH > 0) {
								Matrix m = new Matrix();
								m.postScale(256.0F / (float) bmpW,256.0F / (float) bmpH);
								trackInfo.setApicBmp(Bitmap.createBitmap(bmpSrc, 0, 0, bmpW, bmpH, m, true));
							} else {
								Log.e("MediaTrackInfo", String.format("error size bmpW:%d, bmpH:%d",
										new Object[] { Integer.valueOf(bmpW),
												Integer.valueOf(bmpH) }));
							}
						}
						JSONObject jsonObject = trackInfo.getJoson();
						if(jsonObject != null && !TextUtils.isEmpty(trackInfo.getPath()) && trackInfo.getDuration() == 0){
							String storageType = getMediaStorageType(trackInfo.getPath());
							if(storageType == MediaPlayerContants.LAST_MEMORY_MEDIA_TAG_SD){	
								mEditor.putString(storageType, jsonObject.toString());
								mEditor.commit();
								//Log.e(TAG,"yfzhang:" + "  storageType:" +storageType + "   " + jsonObject.toString());  
							}else if(storageType == MediaPlayerContants.LAST_MEMORY_MEDIA_TAG_USB){
								mEditor.putString(storageType, jsonObject.toString());
								mEditor.commit();
								//Log.e(TAG,"yfzhang:" + "  storageType:" +storageType + "   " + jsonObject.toString());  
							}
						}
					} catch (Exception arg8) {
						arg8.printStackTrace();
					}
				}

				this.mStatus.mPlayingTrackInfo = trackInfo;
			}

			return this.mStatus.mPlayingTrackInfo;
		}
	}
	
	public static String getMediaStorageType(String path){
		
		if(path == null){
			return null;
		}
		
		if(path.matches("(.*)ext_sdcard(.*)")){
			return MediaPlayerContants.LAST_MEMORY_MEDIA_TAG_SD;
		}
		if(path.matches("(.*)udisk(.*)")){
			return MediaPlayerContants.LAST_MEMORY_MEDIA_TAG_USB;
		}
		return null;
	}
	
	public MediaStorage getPlayingStorage() throws RemoteException {
		String arg0 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			return this.mStorageState.getPlayingStorage();
		}
	}

	public boolean requestReScaning(String path) throws RemoteException {
		String arg1 = "MediaPlayerService";
		synchronized ("MediaPlayerService") {
			try {
				LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(), path);
				Uri e = Uri.parse("file://" + path);
				this.mContext.sendBroadcast(new Intent(
						"yecon.intent.action.MEDIA_RESCAN", e));
			} catch (Exception arg3) {
				arg3.printStackTrace();
				return false;
			}

			return true;
		}
	}

	@SuppressLint({ "WorldReadableFiles" })
	public static String getStorageStateFromSP(Context context, String path) {
		String action = "scan_cancel";
		if (context != null) {
			try {
				Context contextProvider = context.createPackageContext(
						"com.yecon.yeconmediaprovider", 2);
				SharedPreferences mPef = contextProvider.getSharedPreferences(
						"YeconMediaStoreSharePreference", 5);
				action = mPef.getString(path, "scan_cancel");
			} catch (Exception arg4) {
				arg4.printStackTrace();
			}
		}

		LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
				"getStorageStateFromSP:" + path + ":" + action);
		return action;
	}

	public static void killMediaProvider(Context context) {
		if (context != null) {
			ActivityManager manager = (ActivityManager) context
					.getSystemService("activity");
			manager.killBackgroundProcesses("com.android.providers.media");
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"killMediaProvider!!!");
		}

	}

	public static boolean isBackCarORPhone(Context context) {
		if (context != null) {
			try {
				String[] e = new String[] { "com.yecon.backcar",
						"com.autochips.bluetooth" };
				ActivityManager manager = (ActivityManager) context
						.getSystemService("activity");
				List taskInfo = manager.getRunningTasks(1);
				LogUtil.printError(
						"MediaPlayerService",
						LogUtil._FUNC_(),
						"top:"
								+ ((RunningTaskInfo) taskInfo.get(0)).topActivity
										.getPackageName());
				if (taskInfo.size() > 0) {
					for (int i = 0; i < e.length; ++i) {
						if (e[i].equalsIgnoreCase(((RunningTaskInfo) taskInfo
								.get(0)).topActivity.getPackageName())) {
							LogUtil.printError("MediaPlayerService",
									LogUtil._FUNC_(), e[i] + " is actived");
							return true;
						}
					}
				}
			} catch (Exception arg4) {
				arg4.printStackTrace();
			}
		}

		return false;
	}

	public boolean isSystemOnOff() {
		boolean bRet = false;
		if (SystemClock.uptimeMillis() - this.mlSystemOnOffTime < (long) this.IGNORE_TIME) {
			Log.e("MediaPlayerService", "isSystemOnOff:true");
			bRet = true;
		} else {
			Log.e("MediaPlayerService", "isSystemOnOff:false");
		}

		return bRet;
	}

	@SuppressLint({ "WorldReadableFiles" })
	public String getLastMemoryStorage(int mediaType) {
		SharedPreferences splm = this.mContext.getSharedPreferences(
				MediaPlayerContants.LAST_MEMORY_DEVICE, 5);
		return splm.getString("LAST_MEMORY_DEVICE:" + mediaType, "");
	}

	@SuppressLint({ "WorldReadableFiles" })
	public void SaveLastMemoryStorage(String strPath, int mediaType) {
		SharedPreferences splm = this.mContext.getSharedPreferences(
				MediaPlayerContants.LAST_MEMORY_DEVICE, 5);
		Editor editor = splm.edit();
		editor.putString("LAST_MEMORY_DEVICE:" + mediaType, strPath);
		editor.commit();
	}

	private class MediaList {
		private List<MediaObject> mListPlay;
		private List<MediaObject> mListFile;
		private List<MediaObject> mListDir;
		private List<MediaObject> mListDirFile;
		private List<MediaObject> mListArtist;
		private List<MediaObject> mListArtistFile;
		private List<MediaObject> mListAlbum;
		private List<MediaObject> mListAlbumFile;
		private List<MediaObject> mListFavorFile;
		private List<MediaObject> mCurrentPlayList;
		
		private MediaList() {
			this.mListPlay = new ArrayList();
			this.mListFile = new ArrayList();
			this.mListDir = new ArrayList();
			this.mListDirFile = new ArrayList();
			this.mListArtist = new ArrayList();
			this.mListArtistFile = new ArrayList();
			this.mListAlbum = new ArrayList();
			this.mListAlbumFile = new ArrayList();
			this.mListFavorFile = new ArrayList();
			this.mCurrentPlayList = new ArrayList();
		}
	}

	private class MediaPlayStatus {
		private int iPlayListType;
		private int iRepeatMode;
		private int iRandomMode;
		private int iFilePos;
		private int iProgress;
		private int iDuration;
		private String strFile;
		private MediaTrackInfo mPlayingTrackInfo;
		private int iDirPos;
		private int iCurDirPos;
		public int iCurrentDIrPos = -1;
		private int iAlbumPos;
		private int iCurAlbumPos;
		private int iArtistPos;
		private int iCurArtistPos;
		private MediaPlayStatus() {
			this.iPlayListType = Type.ALL_FILE;
			//this.iPlayListType = Type.ALL_DIR;
			this.iRepeatMode = RepeatMode.ALL;
			this.iRandomMode = RandomMode.OFF;
			this.iFilePos = -1;
			this.iProgress = 0;
			this.iDuration = 0;
			this.strFile = "";
			this.iDirPos = -1;
			this.iCurDirPos = -1;
			this.iAlbumPos = -1;
			this.iCurAlbumPos = -1;
			this.iArtistPos = -1;
			this.iCurArtistPos = -1;
		}

		private void readPreference(Context context, String strDBPath,
				int mediaType) {
			if (strDBPath != null) {
				strDBPath = strDBPath + "_MediaType_" + mediaType;
				Log.e("MediaPlayerService", "readPreference : " + strDBPath);
				SharedPreferences sp = context.getSharedPreferences(strDBPath,
						0);
				this.iPlayListType = sp.getInt("iPlayMode", Type.ALL_FILE);
				//this.iPlayListType = sp.getInt("iPlayMode", Type.ALL_DIR);
				this.iRepeatMode = sp.getInt("iRepeatMode", 2);
				this.iRandomMode = sp.getInt("iRandomMode", 0);
				this.iFilePos = sp.getInt("iFilePos", -1);
				this.iDirPos = sp.getInt("iDirPos", -1);
				this.iCurDirPos = sp.getInt("iCurDirPos", -1);
				this.iAlbumPos = sp.getInt("iAlbumPos", -1);
				this.iCurAlbumPos = sp.getInt("iCurAlbumPos", -1);
				this.iArtistPos = sp.getInt("iArtistPos", -1);
				this.iCurArtistPos = sp.getInt("iCurArtistPos", -1);
				this.iProgress = sp.getInt("iProgress", 0);
				this.iDuration = sp.getInt("iDuration", 0);
				this.strFile = sp.getString("strFile", "");
				this.printPreference();
			}

		}

		private void clearPreference(Context context, String strDBPath,
				int mediaType) {
			strDBPath = strDBPath + "_MediaType_" + mediaType;
			if (MediaPlayerService.this.isSystemOnOff()) {
				Log.e("MediaPlayerService",
						"machine loading, ignore clearPreference : "
								+ strDBPath);
			} else {
				Log.e("MediaPlayerService", "clearPreference : " + strDBPath);
				SharedPreferences sp = context.getSharedPreferences(strDBPath,
						0);
				Editor editor = sp.edit();
				editor.clear();
				editor.commit();
			}

		}

		private void writePreference(Context context, String strDBPath,
				int mediaType) {
			if (strDBPath != null && MediaPlayerService.this.mPlayer != null
					&& MediaPlayerService.this.mPlayer.isPlaying()) {
				strDBPath = strDBPath + "_MediaType_" + mediaType;
				try {
					Log.e("MediaPlayerService", "writePreference : " + strDBPath  + "  playState : " + getPlayStatus());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				SharedPreferences sp = context.getSharedPreferences(strDBPath,
						0);
				Editor editor = sp.edit();
				editor.putInt("iPlayMode", this.iPlayListType);
				editor.putInt("iRepeatMode", this.iRepeatMode);
				editor.putInt("iRandomMode", this.iRandomMode);
				editor.putInt("iFilePos", this.iFilePos);
				editor.putInt("iDirPos", this.iDirPos);
				editor.putInt("iCurDirPos", this.iCurDirPos);
				iCurrentDIrPos = this.iCurDirPos;
				editor.putInt("iAlbumPos", this.iAlbumPos);
				editor.putInt("iCurAlbumPos", this.iCurAlbumPos);
				editor.putInt("iArtistPos", this.iArtistPos);
				editor.putInt("iCurArtistPos", this.iCurArtistPos);
				
				editor.putInt("iProgress", this.iProgress);
				editor.putInt("iDuration", this.iDuration);
				editor.putString("strFile", this.strFile);
				editor.commit();
				this.printPreference();
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("strFile", this.strFile);
					jsonObject.put("iProgress", this.iProgress);
					Log.w("MediaPlayerService", "iProgress save:" + iProgress);
					jsonObject.put("iPlayMode", this.iPlayListType);
					mPrf.edit().putString(getMediaStorageType(strFile) + "_progress",jsonObject.toString()).commit();
				} catch (JSONException e) {
					Log.e("MediaPlayerService",e.toString());
				}
			}

		}
		private void saveProgress(Context context, String strDBPath,int mediaType){
			if (strDBPath != null && MediaPlayerService.this.mPlayer != null
					&& MediaPlayerService.this.mPlayer.isPlaying()) {
				strDBPath = strDBPath + "_MediaType_" + mediaType;
				SharedPreferences sp = context.getSharedPreferences(strDBPath,
						0);
				Editor editor = sp.edit();
				editor.putInt("iProgress", this.iProgress).commit();
			}
		}
		private void printPreference() {
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"[state]: mStatus.iPlayMode :"
							+ MediaPlayerService.this.mStatus.iPlayListType);
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"[state]: mStatus.iRepeatMode :"
							+ MediaPlayerService.this.mStatus.iRepeatMode);
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"[state]: mStatus.iRandomMode :"
							+ MediaPlayerService.this.mStatus.iRandomMode);
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"[state]: mStatus.iFilePos :"
							+ MediaPlayerService.this.mStatus.iFilePos);
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"[state]: mStatus.iDirPos :"
							+ MediaPlayerService.this.mStatus.iDirPos);
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"[state]: mStatus.iCurDirPos :"
							+ MediaPlayerService.this.mStatus.iCurDirPos);
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"[state]: mStatus.iAlbumPos :"
							+ MediaPlayerService.this.mStatus.iAlbumPos);
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"[state]: mStatus.iCurAlbumPos :"
							+ MediaPlayerService.this.mStatus.iCurAlbumPos);
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"[state]: mStatus.iArtistPos :"
							+ MediaPlayerService.this.mStatus.iArtistPos);
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"[state]: mStatus.iCurArtistPos :"
							+ MediaPlayerService.this.mStatus.iCurArtistPos);
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"[state]: mStatus.iProgress : "
							+ MediaPlayerService.this.mStatus.iProgress);
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"[state]: mStatus.iDuration : "
							+ MediaPlayerService.this.mStatus.iDuration);
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"[state]: mStatus.strFile : "
							+ MediaPlayerService.this.mStatus.strFile);
		}
	}

	private class MediaStorageState {
		private List<MediaStorage> mListStorage;

		private MediaStorageState() {
			this.mListStorage = new ArrayList();
		}

		public void queryStatus(Context context) {
			List arg1 = this.mListStorage;
			synchronized (this.mListStorage) {
				String strPlayPath = null;

				try {
					strPlayPath = MediaPlayerService.this
							.getLastMemoryStorage(MediaPlayerService.this.mMediaType);
					if (strPlayPath == null) {
						strPlayPath = "/mnt/sdcard";
					}

					strPlayPath = YeconMediaStore.getStoragePath(strPlayPath);
				} catch (Exception arg10) {
					arg10.printStackTrace();
				}

				try {
					String[] e = new String[] { "/mnt/ext_sdcard1",
							"/mnt/ext_sdcard2", "/mnt/udisk1", "/mnt/udisk2",
							"/mnt/udisk3", "/mnt/udisk4", "/mnt/udisk5",
							"/mnt/sdcard" };
					int path = 0;

					while (path < this.mListStorage.size()) {
						if (((MediaStorage) this.mListStorage.get(path))
								.getState().equals("scan_cancel")) {
							this.mListStorage.remove(path);
						} else {
							++path;
						}
					}

					String[] arg7 = e;
					int arg6 = e.length;

					for (int arg5 = 0; arg5 < arg6; ++arg5) {
						String arg13 = arg7[arg5];
						String scan = MediaPlayerService.getStorageStateFromSP(
								context, arg13);
						if (!scan.equals("scan_cancel")) {
							MediaStorage storage = this.obtainStorage(arg13);
							storage.setState(scan);
							this.UpdateStorage(storage);
							LogUtil.printError("MediaPlayerService",
									LogUtil._FUNC_(), arg13 + ":" + scan);
						}
					}
				} catch (Exception arg11) {
					arg11.printStackTrace();
				}

				this.AttachStorage(strPlayPath);
			}
		}

		private void UpdateStorage(MediaStorage storage) {
			List arg1 = this.mListStorage;
			synchronized (this.mListStorage) {
				this.RemoveStorage(storage.getPath());
				if (!storage.getState().equals("scan_analysis")
						&& !storage.getState().equals("scan_finish")) {
					if (storage.getState().isEmpty()
							|| storage.getState().equals("scan_cancel")) {
						return;
					}
				} else if (!YeconMediaStore.checkStorageExist(
						MediaPlayerService.this.mEnv, storage.getPath())) {
					return;
				}

				LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
						storage.getPath() + ":" + storage.getState());
				this.mListStorage.add(storage);
			}
		}

		private void RemoveStorage(String path) {
			List arg1 = this.mListStorage;
			synchronized (this.mListStorage) {
				Iterator arg3 = this.mListStorage.iterator();

				while (arg3.hasNext()) {
					MediaStorage mediaStorage = (MediaStorage) arg3.next();
					if (path.equals(mediaStorage.getPath())) {
						this.mListStorage.remove(mediaStorage);
						return;
					}
				}

			}
		}

		public MediaStorage obtainStorage(String path) {
			List arg1 = this.mListStorage;
			synchronized (this.mListStorage) {
				path = YeconMediaStore.getStoragePath(path);
				Iterator arg4 = this.mListStorage.iterator();

				while (arg4.hasNext()) {
					MediaStorage mediaStorage = (MediaStorage) arg4.next();
					if (path.equals(mediaStorage.getPath())) {
						return mediaStorage;
					}
				}

				MediaStorage objMediaStorage = new MediaStorage();
				objMediaStorage.setPath(path);
				return objMediaStorage;
			}
		}

		public void AttachStorage(String path) {
			List arg1 = this.mListStorage;
			synchronized (this.mListStorage) {
				path = YeconMediaStore.getStoragePath(path);
				Iterator arg3 = this.mListStorage.iterator();

				while (arg3.hasNext()) {
					MediaStorage mediaStorage = (MediaStorage) arg3.next();
					mediaStorage
							.setPlaying(mediaStorage.getPath().equals(path));
				}

				LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
						"set playing srotage:" + path);
			}
		}

		public MediaStorage getPlayingStorage() {
			List arg0 = this.mListStorage;
			synchronized (this.mListStorage) {
				Iterator arg2 = this.mListStorage.iterator();

				while (arg2.hasNext()) {
					MediaStorage mediaStorage = (MediaStorage) arg2.next();
					if (mediaStorage.getPlaying()) {
						return mediaStorage;
					}
				}

				return null;
			}
		}
	}

	private class RecorverThread extends Thread {
		private RecorverThread() {
		}

		public void run() {
			MediaPlayerService.this.miWaitDatabase = 60;
			String path = this.getName();
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"++mRecorverThread:" + path);
			if (MediaPlayerService.getStorageStateFromSP(
					MediaPlayerService.this.mContext, path)
					.equals("scan_start")) {
				MediaPlayerService.this.updateServiceStatus(1, path);
			}

			if (MediaPlayerService.this.mStorageState.mListStorage.isEmpty()) {
				MediaPlayerService.this.mStorageState
						.queryStatus(MediaPlayerService.this.mContext);
			}

			Uri uri = Uri.parse("file://" + path);
			MediaPlayerService.this.mContext.sendBroadcast(new Intent(
					"yecon.intent.action.MEDIA_QUREY", uri));
			MediaPlayerService.this.mStorageState.AttachStorage(path);

			while (!this.isInterrupted()
					&& MediaPlayerService.this.miWaitDatabase != 0) {
				MediaPlayerService arg9999 = MediaPlayerService.this;
				int arg10001 = MediaPlayerService.this.miWaitDatabase;
				arg9999.miWaitDatabase = arg10001 - 1;
				if (arg10001 == 1) {
					MediaPlayerService.this.updateServiceStatus(3, path);
				}

				try {
					MediaStorage e = MediaPlayerService.this.mStorageState
							.obtainStorage(path);
					String action = MediaPlayerService.getStorageStateFromSP(
							MediaPlayerService.this.mContext, path);
					if (action.equals("scan_analysis")
							|| action.equals("scan_finish")) {
						e.setState(action);
						MediaPlayerService.this.mStorageState.UpdateStorage(e);
						LogUtil.printError(
								"MediaPlayerService",
								LogUtil._FUNC_(),
								"finish scan!!! path:"
										+ path
										+ "->"
										+ MediaPlayerService.this.miWaitDatabase);
						break;
					}

					Thread.sleep(1000L);
					if (60 - MediaPlayerService.this.miWaitDatabase > 5
							&& !YeconMediaStore.checkStorageExist(
									MediaPlayerService.this.mEnv, path)) {
						LogUtil.printError("MediaPlayerService",
								LogUtil._FUNC_(),
								"wait for device Mount failed!!! path:" + path);
						MediaPlayerService.this.updateServiceStatus(6, path);
						break;
					}

					if (MediaPlayerService.this.miWaitDatabase == 55
							&& MediaPlayerService.getStorageStateFromSP(
									MediaPlayerService.this.mContext, path)
									.equals("scan_cancel")) {
						MediaPlayerService
								.killMediaProvider(MediaPlayerService.this.mContext);
						MediaPlayerService.this.mContext
								.sendBroadcast(new Intent(
										"yecon.intent.action.MEDIA_QUREY", uri));
					}

					LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
							"wait for finish scan!!! path:" + path + "->"
									+ MediaPlayerService.this.miWaitDatabase);
				} catch (InterruptedException arg4) {
					arg4.printStackTrace();
				}
			}

			MediaPlayerService.this.miWaitDatabase = 0;
			MediaPlayerService.this.recoverStorage(path);
			LogUtil.printError("MediaPlayerService", LogUtil._FUNC_(),
					"--mRecorverThread:" + path);
		}
	}

	@Override
	public List<MediaObject> getPlayingDirList() {
		return this.mList.mCurrentPlayList;
	}
	
	@Override
	public int getPlayingDirPos() {
		return mCurrentPlayDirPos;
	}
}