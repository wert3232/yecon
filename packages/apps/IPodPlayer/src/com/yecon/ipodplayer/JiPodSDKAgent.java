package com.yecon.ipodplayer;

import com.autochips.ipod.IPodSDK;
import com.autochips.ipod.IPodSDK.*;



public class JiPodSDKAgent {	
	public int m_hReadMsgQueeue;
	public boolean m_bExitNotifyThread;
	public int m_hEvtNotifyThreadExit;
	public int m_hNotifyThread;
	public boolean m_bInPlaying;
	public int m_u4RepeatMode;
	public int m_u4ShuffleMode;
	public short m_dwTrackCount;
	public int m_u4UpadateTimerID;
	public int m_u4UpdateTimerID;
	public int m_u4PlaybackTrackIndex;
	public int m_u4PlaybackStatus;
	public int m_u4MaxSliderValue;

	public IPodSDK ipodsdk = IPodSDK.getInstance();

	boolean m_pfnIPOD_Init(){
		boolean ret = false;
		ret = ipodsdk.init();
		return ret;
	}
	boolean m_pfnIPOD_DeInit(){
		boolean ret = false;
		ret = ipodsdk.deinit();
		return ret;
	}
	boolean m_pfnIPOD_InitCBM(){
		boolean ret = false;
		ret = ipodsdk.initCBM();
		return ret;
	}
	boolean m_pfnIPOD_ReleaseCBM(){
		boolean ret = false;
		ret = ipodsdk.releaseCBM();
		return ret;
	}
	boolean m_pfnStartPlayAudio(){
		boolean ret = false;
		ret = ipodsdk.startPlayAudio();
		return ret;
	}
	boolean m_pfnStopPlayAudio(){
		boolean ret = false;
		ret = ipodsdk.stopPlayAudio();
		return ret;
	}
	boolean m_pfnGetAudioDSP(){
		boolean ret = false;
		ret = ipodsdk.getAudioDSP();
		return ret;
	}
	boolean m_pfnReleaseAudioDSP(){
		boolean ret = false;
		ret = ipodsdk.releaseAudioDSP();
		return ret;
	}
	boolean m_pfnSetADecSinkInfo(SWI_SINK_TYPE_T ADSPSink) throws IPodException{
		boolean ret = false;
		ret = ipodsdk.setADecSinkInfo(ADSPSink);
		return ret;
	}
	int m_pfnEnterRemoteUIMode() throws IPodException{
		int ret = 0;
		ipodsdk.enterRemoteUIMode();
		return ret;
	}
	PLAY_ST_INFO_T m_pfnGetPlayStatus(PLAY_ST_INFO_T prPbStatus) throws IPodException{
		PLAY_ST_INFO_T ret = null;
		ret = ipodsdk.getPlayStatus();
		return ret;
	}
	int m_pfnExitRemoteUIMode() throws IPodException{
		int ret = 0;
		ipodsdk.exitRemoteUIMode();
		return ret;
	}
	
	int m_pfnResetDBSelection() throws IPodException{
		int ret = 0;
		ipodsdk.resetDBSelection();
		return ret;
	}
	int m_pfnSetRepeatMode(int u1RepeatMode, boolean fgRestore) throws IPodException{
		int ret = 0;
		ipodsdk.setRepeatMode((byte)u1RepeatMode,fgRestore);
		return ret;
	}
	int m_pfnGetNumEQProfile(int pu4NumEQProfile) throws IPodException{
		int ret = 0;
		ret = ipodsdk.getNumEQProfile();
		return ret;
	}
	int m_pfnGetCurEQProfileIndex(int pu4EQProfileIndex) throws IPodException{
		int ret = 0;
		ret = ipodsdk.getCurEQProfileIndex();
		return ret;
	}
	int m_pfnSetShuffleMode(int u1ShuffleMode, boolean fgRestore) throws IPodException{
		int ret = 0;
		ipodsdk.setShuffleMode((byte)u1ShuffleMode,fgRestore);
		return ret;
	}
	int m_pfnPlayCurrentSelection(int u4SelectTrackRecIndex) throws IPodException{
		int ret = 0;
		ipodsdk.playCurrentSelection(u4SelectTrackRecIndex);
		return ret;
	}
	int m_pfnSetCurrentPlayingTrack(int u4CurPlayingTrackIndex) throws IPodException{
		int ret = 0;
		ipodsdk.setCurrentPlayingTrack(u4CurPlayingTrackIndex);
		return ret;
	}
	int m_pfnSelectDBRecord(int u1DbCategory, int u4RecIndex) throws IPodException{
		int ret = 0;
		ipodsdk.selectDBRecord((byte)u1DbCategory,u4RecIndex);
		return ret;
	}
	int m_pfnSelectSortDBRecord(int u1DbCategory, int u4RecIndex, int u1SortOrder) throws IPodException{
		int ret = 0;
		ipodsdk.selectSortDBRecord((byte)u1DbCategory,u4RecIndex,(byte)u1SortOrder);
		return ret;
	}
	int m_pfnGetNumberCategorizedDBRecords(int u1DbCategory) throws IPodException{
		int ret = 0;
		ret = ipodsdk.getNumberCategorizedDBRecords((byte)u1DbCategory);
		return ret;
	}
	Object m_pfnRetrieveCategorizedDBRecords(int u1DbCategory,int u4StartRecIdx,int u4RecCnt,Object pvInfo) throws IPodException{
		Object ret = 0;
		ret = ipodsdk.retrieveCategorizedDBRecords((byte)u1DbCategory,u4StartRecIdx,u4RecCnt);
		return ret;
	}
	//get the current shuff
	int m_pfnGetShuffleSetting(int pu1ShuffMode) throws IPodException{
		int ret = 0;
		ret = ipodsdk.getShuffleSetting();
		return ret;
	}
	//get the repeat 
	int m_pfnGetRepeatSetting(int pu1RepeatMode) throws IPodException{
		int ret = 0;
		ret = ipodsdk.getRepeatSetting();
		return ret;
	}
	int m_pfnGetCurPlayingTrackIdx(int pu4CurTrackIndex) throws IPodException{
		int ret = 0;
		ret = ipodsdk.getCurPlayingTrackIdx();
		return ret;
	}
	String m_pfnGetIndexedPlayingTrackTitle(int u4PlaybackTrackIndex, String pszCurTrackTitle) throws IPodException{
		String ret = "";
		ret = ipodsdk.getIndexedPlayingTrackTitle(u4PlaybackTrackIndex);
		return ret;
	}
	String m_pfnGetIndexedPlayingTrackArtist(int u4PlaybackTrackIndex, String pszCurTrackArtist) throws IPodException{
		String ret = "";
		ret = ipodsdk.getIndexedPlayingTrackArtist(u4PlaybackTrackIndex);
		return ret;
	}
	String m_pfnGetIndexedPlayingTrackAlbum(int u4PlaybackTrackIndex, String pszCurTrackAlbum) throws IPodException{
		String ret = "";
		ret = ipodsdk.getIndexedPlayingTrackAlbum(u4PlaybackTrackIndex);
		return ret;
	}
	int m_pfnRequestLingoProtocolVersion(int u1LingoID, int pu4Version) throws IPodException{
		int ret = 0;
		ret = ipodsdk.requestLingoProtocolVersion((byte)u1LingoID);
		return ret;
	}
	Object m_pfnGetCurPlayingTrackChapterInfo(Object pvCharpterInfo) throws IPodException{
		Object ret = null;
		ret = ipodsdk.getCurPlayingTrackChapterInfo();
		return ret;
	}
	int m_pfnGetDbTrackInfo(int u4TrackDbStarIndex,int u4TrackCount, int u1TrackInfoBit, Object pvGotInfo) throws IPodException{
		int ret = 0;
		ret = ipodsdk.getDbTrackInfo(u4TrackDbStarIndex,u4TrackCount,(byte)u1TrackInfoBit);
		return ret;
	}
	void m_pfnSeek(int time) throws IPodException{
		ipodsdk.seek(time);
	}
	PLAY_ST_INFO_EX_T m_pfnGetPlayStatus_Ex(PLAY_ST_INFO_EX_T prPbStatus) throws IPodException{
		PLAY_ST_INFO_EX_T ret = null;
		ret = ipodsdk.getPlayStatus_Ex();
		return ret;
	}
	ARTWORK_DATA_T m_pfnGetTrackArtWorkData(
			int u4TrackIndex,
			int u2FmtID,
			int u4MsTime,
			ARTWORK_DATA_T pvArtWorkData) throws IPodException{
		ARTWORK_DATA_T ret = null;
		ipodsdk.getTrackArtWorkData(u4TrackIndex,(short)u2FmtID,u4MsTime,pvArtWorkData);
		return ret;
	}
	ARTWORK_FMTS_INFO_T m_pfnGetArtWorkFormats(ARTWORK_FMTS_INFO_T pvGotInfo) throws IPodException{
		ARTWORK_FMTS_INFO_T ret = null;
		ipodsdk.getArtWorkFormats(pvGotInfo);
		return ret;
	}
	AUTH_RESULT_T m_pfnGetAuthStatus(AUTH_RESULT_T prAuthResult) throws IPodException{
		AUTH_RESULT_T ret = null;
		ret = ipodsdk.getAuthStatus();
		return ret;
	}
	Object m_pfnPlayControl(int u1CtrlCode) throws IPodException{
		int ret = 0;
		ipodsdk.playControl((byte)u1CtrlCode);//����iphone���ط�״̬
		return ret;
	}
	PREFERENCES_T m_pfnGetiPodPreferences(int u1ClassID, PREFERENCES_T prIPodPreferences) throws IPodException{
		PREFERENCES_T ret = null;
		ret = ipodsdk.getiPodPreferences((byte)u1ClassID);
		return ret;
	}
	int m_pfnSetiPodPreferences(int u1ClassID, int u1SettingID) throws IPodException{
		int ret = 0;
		ipodsdk.setiPodPreferences((byte)u1ClassID,(byte)u1SettingID);
		return ret;
	}
	int m_pfnSetVolume(int u4Volume) throws IPodException{
		int ret = 3;
		ipodsdk.setVolume(u4Volume);
		return ret;
	}
	int m_pfnGetVolume(int pu4Volume) throws IPodException{
		int ret = 3;
		ret = ipodsdk.getVolume();
		return ret;
	}
	int m_pfnSetRemoteEvent(int u4EventMask) throws IPodException{
		int ret = 0;
		ipodsdk.setRemoteEvent(u4EventMask);
		return ret;
	}
	int m_pfnSetPlayStatusChangeNotification(int u4NotificationType,int u4NotificationMask) throws IPodException{
		int ret = 0;
		ipodsdk.setPlayStatusChangeNotification(u4NotificationType,u4NotificationMask);
		return ret;
	}
	PlayingTrackInfo m_pfnGetIndexedPlayingTrackInfo(int u4PlaybackTrackIndex,
			int u4ChapIndex,
			TRACK_INF_TYPE_E eTrackInfoType,
			PlayingTrackInfo pInfo) throws IPodException{
		PlayingTrackInfo ret = null;
		ret = ipodsdk.getIndexedPlayingTrackInfo(u4PlaybackTrackIndex,u4ChapIndex,eTrackInfoType);
		return ret;
	}
	ARTWORK_TIMES_T m_pfnGetTrackArtWorkTimes(int u4TrackIndex,
			int u2FmtID,
			int u2ArtWorkIndex,
			int u2ArtWorkCount,
			ARTWORK_TIMES_T pvGotInfo) throws IPodException{
		ARTWORK_TIMES_T ret = null;
		ipodsdk.getTrackArtWorkTimes(u4TrackIndex,(short)u2FmtID,(short)u2ArtWorkIndex,(short)u2ArtWorkCount,pvGotInfo);
		return ret;
	}
	int m_pfnGetNumPlayingTracks(int pdwTracksNum) throws IPodException{
		int ret = 0;
		ret = ipodsdk.getNumPlayingTracks();
		return ret;
	}
	
	int m_pfnResetDBSelectionHierachy(int u1Hierarchy) throws IPodException{
		int ret = 0;
		ipodsdk.resetDBSelectionHierarchy((byte)u1Hierarchy);
		return ret;
	}
	int m_pfnSetVideoDalay(int u1VideoDelayMs) throws IPodException{
		int ret = 0;
		ipodsdk.setVideoDelay(u1VideoDelayMs);
		return ret;
	}
	long m_pfnGetiPodOptionsForLingo(int u1LingoID,long pu8Options) throws IPodException{
		long ret = 0;
		ret = ipodsdk.getiPodOptionsForLingo((byte)u1LingoID);
		return ret;
	}
	long m_pfnGetiPodOptions(long pu8Options) throws IPodException{
		long ret = 0;
		ret = ipodsdk.getiPodOptions();
		return ret;
	}
	


}

class MUSB_IPOD_PLAY_ST_INFO_T{
	int u4TrackIdx;
	int u4TrackLen;
	int u4TrackPos;
	MUSB_IPOD_PLAY_ST_EX_E ePbStatus;
}
enum MUSB_IPOD_PLAY_ST_EX_E{
	PB_ST_EX_STOP_0X00,
	PB_ST_EX_PLAY_0X01,
	PB_ST_EX_PAUSE_0X02,
	PB_ST_EX_PF_0X03,
	PB_ST_EX_FR_0X04,
	PB_ST_EX_REWIND_0X05,
	
	PB_ST_EX_FFW_SEEK_STOP,
	PB_ST_EX_REW_SEEK_STOP,
	PB_ST_EX_FFW_SEEK_START,
	PB_ST_EX_REW_SEEK_START,
	
	PB_ST_EX_ERR_0XFF
}
class MUSB_IPOD_PLAY_ST_INFO_EX_T{
	int u4TrackLen;
	int u4TrackPos;
	MUSB_IPOD_PLAY_ST_EX_E ePbStatus;
}
class MUSB_AUTH_RESULT_T{
	int u4AuthResult;
	boolean fgIdpMode;
	int u1CategoryMode;
	boolean fgInAuthoring;
	boolean fgCanReTry;
}
class MUSB_IPOD_PREFERENCES_T{
	int bPreferenceClassID;
	int bPreferenceSettingID;
}
enum MUSB_TRACK_INF_TYPE_E{
	TRACK_CAP_INF,
	TRACK_LOCALCOASTING_NAME,
	TRACK_DATE_INFOTYPE_CODE,
	TRACK_DESCRIPTION,
	TRACK_SONG_LYRICS,
	TRACK_GENRE,
	TRACK_ARTWORK_CNT,
	TRACK_INF_NONE
}
enum MUSB_IPOD_PLAY_CTRL_CODE_E{
	PLAY_CTRL_RESERVED, 
	PLAY_CTRL_PLAY_PAUSE, 
	PLAY_CTRL_STOP, 
	PLAY_CTRL_NEXT_TRACK, 
	PLAY_CTRL_PRE_TRACK, 
	PLAY_CTRL_START_FF, 
	PLAY_CTRL_START_REW, 
	PLAY_CTRL_END_FF_REW, 
	PLAY_CTRL_NEXT, 
	PLAY_CTRL_PRE, 
	PLAY_CTRL_PLAY, 
	PLAY_CTRL_PAUSE, 
	PLAY_CTRL_NEXT_CHAP, 
	PLAY_CTRL_PRE_CHAP
}

enum MUSB_IPOD_DB_CATEGORY_TYPE_E{
	DB_TYPE_TOP_LEVEL,//level  0
	DB_TYPE_PLAYLIST,  // 1
	DB_TYPE_ARTIST,   //artist 2
	DB_TYPE_ALBUM,    //3
	DB_TYPE_GENRE,    //4
	DB_TYPE_TRACK,    //5
	DB_TYPE_COMPOSER, //6
	DB_TYPE_AUDIOBOOK, //7
	DB_TYPE_PODCAST,   //8
	DB_TYPE_NESTED_PLAYLIST, //9
	DB_TYPE_GENIUS_MIXES   //10
}

class MUSB_IPOD_DB_RecItem_T{
	int u4RecordItem;
	String szRecInfo;
}

class MUSB_IPOD_CHAPTER_INFO_T{
	int u4ChapterCnt;
	int u4ChapterIndex;
}

class MUSB_IPOD_ARTWORK_DATA_T{
	int u2DescritorIndex;
	int u1DisplayPixFmtCode;
	int u2ImgWidth;
	int u2ImgHeight;
	int u2TopLeftX;
	int u2TopLeftY;
	int u2BotRightX;
	int u2BotRightY;
	int u4DataSizeInByte;
	int u4DataBufSize;
	Byte pvData[];
}

class MUSB_IPOD_ARTWORK_FMTS_INFO_T{
	int u4FmtNum;
	MUSB_IPOD_ARTWORK_FMTS prArtworkFmt = new MUSB_IPOD_ARTWORK_FMTS();
}

class MUSB_IPOD_ARTWORK_FMTS{
	int u2FormatID;
	int u1PixFormat;
	int u2ImgWidth;
	int u2ImgHeight;
}

class MUSB_IPOD_ARTWORK_TIMES_T{
	int u4InfoNum;
	Integer pi4MsTimePos = 0;
}

class MUSB_IPOD_TRACK_RELEASE_DATE_T{
	int u1Second;
	int u1Minute;
	int u1Hour;
	int u1Day;
	int u1Month;
	int u1WeekDay;
	int u2Year;
}

enum E_IPOD_NOTIFY_INFO_E{
	IPOD_AUTH_STATE,
	IPOD_PB_STATUS,
	IPOD_DIGITALAUIDO_CFG,
	IPOD_STATUS_NOTIFY
}

class MUSB_IPOD_PB_NOTIFY_STATUS_INF_T{
	byte fgpbStatuValid;
	PLAY_ST_EX_E eStatus;

	boolean fgTrackIndexValid;
	int u4TrackIndex;

	boolean fgTrackMsOffsetValid;
	int u4TrackMsOffset;

	boolean fgChapterIndexValid;
	int u4ChapterIndex;

	boolean fgTrackSecOffsetValid;
	int u4TrackSecOffset;

	boolean fgChaptMsOffsetValid;
	int u4ChapsetMsOffset;

	boolean fgChapSecOffsetValid;
	int u4ChaptSecOffset;

	boolean fgTrackUIDValid;
	long u8TrackUID;

	boolean fgTrackPlayModeValid;
	byte u4TrackPlayMode;

	boolean fgLyricInfoValid;
	boolean fglyricReady;

	boolean fgTrackCapa;
	int u4TrackCapaBits;

}

class MUSB_IPOD_DIGITALAUDIO_CONFIGUE_T{
	int u4SampleRateSupportFromIpod;
}

enum MUSB_IPOD_STATUS_E {
	IPOD_STATUS_PLUG_IN,
	IPOD_STATUS_PLUG_OUT
}

class NOTIFY_INF_A{
	AUTH_RESULT_T rAuthResult;
	MUSB_IPOD_PB_NOTIFY_STATUS_INF_T rPbInfo;
	MUSB_IPOD_DIGITALAUDIO_CONFIGUE_T rDigitalAudio;
	MUSB_IPOD_STATUS_E eiPodStatus;
}

class MUSB_IPOD_NOTIFY_INFO_T{
	E_IPOD_NOTIFY_INFO_E eNotifyType;
	NOTIFY_INF_A rNotify;
}
