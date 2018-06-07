#ifndef _WYD_ATV_MIDDLEWARE_PROTOCOL_H_
#define _WYD_ATV_MIDDLEWARE_PROTOCOL_H_
#include "AtvDef.h"
#include "Cdt1028.h"

#define SUPPORT_ATV_5218

typedef struct {
	UINT8	Mode;
	UINT8	Level;
	UINT8	Sync;
	UINT8	Window;
	UINT8	Vif;
	UINT8	WaitingFlag;
	UINT16	ScanWaiting;
	UINT16	BrowseWaiting;
}TV_TUNER;

#define MSG_WAIT_TIMEOUT			3000

typedef struct TV_TUNER_SYSTEM_T {

	UINT8   Tuner;
	UINT16	MinFreq;
	UINT16	MaxFreq;
	UINT8	StepFreq;
	UINT8	FastStep;
	UINT8	SlowStep;
}TV_TUNER_SYSTEM_EX;

typedef struct 
{
	UINT32 nFormatCurFreq;
	UINT32 nFreqNum;
	UINT32 CurFreqArray[100];
	UINT16 u2ArraySize;
}ATV_STATION_LIST;


#define ATV_SHOW_FREINFO_TIMER_INTETVAL 2000
#define	hide_no_signal
#define	LIST_ITEM_NUM													 6
#define	JUMPFRQ_INTERNAL									             900
#define	ENFORCE_SOLUTION_MUTE_INTERNAL									 300
#define	MSG_WAIT_TIMEOUT									             3000
#define	MAX_MCU_DATA_ONCE												 64
#define	BACK_VISBLE														 0
#define	MASK_ATV_SEARCH_STATUS_SEEK_UP								    (1L <<0)
#define	MASK_ATV_SEARCH_STATUS_SEEK_DOWN								(1L <<1)
#define	MASK_ATV_SEARCH_STATUS_SCAN									    (1L <<2)
#define	MASK_ATV_SEARCH_STATUS_PS										(1L <<3)
#define	MASK_ATV_SEARCH_STATUS_AMS									    (1L <<4)

enum
{
	AtvKey_Normal ,
	AtvKey_Handle
};
enum{

	ATV_CMD_IDLE,
	ATV_CMD_AMS,
	ATV_CMD_PS,
	ATV_CMD_SEARCHDOWN,
	ATV_CMD_SEARCHUP,
	ATV_CMD_NEXT_CH,
	ATV_CMD_PRE_CH,
	ATV_CMD_FREQ_DOWN,
	ATV_CMD_FREQ_UP,
	ATV_CMD_FORMAT,
	ATV_CMD_PS_FINISH,
	ATV_CMD_KEY,
	ATV_CMD_SRCH_FOCE_STOP,

	ATV_END
};

typedef struct{
	UINT32 uATVStationSize;
	UINT32 uATVFormat;
	UINT32 uATVCurFreq;
	UINT32 u1ATVIndex;
	UINT32 nList[ATV_STATION_SIZE];
	UINT32 u1ListFormat[ATV_STATION_SIZE];
}ATV_Station_T;
typedef struct{
	UINT32 u1AtvRegion;
	UINT32 i4RadioRegion;
	ATV_Station_T ATV_Station;
}SHARE_MEMORY_FileInfo_T;

class AtvAndArmProtocol {

public:
	AtvAndArmProtocol(void);
	~AtvAndArmProtocol(void);
	void   AtvInitInfo(void);
	void   AtvProtocalInit(int tvRegion, unsigned int *configData, unsigned int dataLen);
	void   AtvProtocalDeinit(void);
	void   AtvRadioRegionChange();
	void   AtvSetKeyState(UINT32 u4Key);
	UINT32 AtvGetKeyState();
	void   AtvSetKeyValue(UINT32 u4Key);
	UINT32 AtvGetKeyValue();

	BYTE   AtvGetState(void);
	void   AtvSetState(BYTE u1Status);
	void   AtvFreqUiRefresh(BYTE index);
	UINT32 AtvGetMinFreq(void);
	void   AtvSetMinFreq(UINT32 u4Freq);

	UINT32 AtvGetMaxFreq(void);
	void   AtvSetMaxFreq(UINT32 u4Freq);

    UINT32 AtvGetStep(void);
	UINT32 AtvGetFastStep(void);
	UINT32 AtvGetSlowStep(void);
	UINT32 AtvGetCurrentFreq(void);
	void   AtvSetCurrentFreq(UINT32 u1Freq, int nMode = 0);
	BOOL   AtvSetSearchFormat(void);
	void   AtvSetFormat(UINT8 u1Format);


	UINT32 AtvGetHadSearchedStationIndex();// 
	void   AtvSetHadSearchedStationIndex(UINT32 u4StationIndex);//
	void   AtvSetSeekUpOrDownIndex(UINT32 u4SeekuporDown);
	UINT32 AtvGetSeekUpOrDownIndex();
	UINT32 AtvGetCurrFreqInDefaultListPos(UINT32 u4Freq);
	BYTE   AtvGetFormat(void);
	BYTE   AtvGetCurrentPresetLoad( );
	BYTE   AtvGetValidStationNum( );
	UINT32 AtvGetDefaultStationNum();
	void   AtvSetDefaultStationNum(UINT8 u1Num);
	void   SearchNextChannelWhenCurrFreqNoSignal(UINT32 nSearchMode);
	BOOL   AtvGetIsValidFormat();
	void   AtvSetCurrentPresetListOneFreq(BYTE u1Num, UINT32 u2Freq);
    UINT32 AtvGetCurrentPresetListOne(  BYTE u1Num);
	UINT32 AtvGetCurrentDefaultPresetListOneFreq(  BYTE u1Num);
	UINT32 AtvGetCurrentDefaultPresetIndex(void);
	void   AtvSetCurrentDefaultPresetIndex(  BYTE u1Num);
	void   AtvSetCurrentPresetListIndex(UINT32 u4Index);
	UINT32 AtvGetCurrentPresetListIndex();
	UINT32 AtvGetRadioArea();
	void   AtvSetRadioArea(UINT32 u4RadioArea);
	void   AtvSwapSeekUpOrDownIndexToSearchIndex(UINT8 cmd);

	void   AtvSetCurChannelFreq(UINT32 u2Freq);
	void   AtvSetValidStationNum(  BYTE value);
	BYTE   AtvClearAmsPresetList( );
	BYTE   AtvSearchJumpOneFreq(WORD nFreq);
	void   AtvFreqUpOrDown(BYTE up);
	void   AtvSearchSeekUpDown(BYTE up);
	void   AtvSearchPS(void);
	void   AtvSearchAMS(void);

	UINT8  AtvGetTvDriverSystemFormat();
	void   AtvSetTvDriverSystemFormat(UINT8 u1Format);

	void   AtvDigitalKeyJumpOneFreqByIndex(UINT32 u4Index);

	void   AtvSearchChannelUpOrDown(BYTE up);
	void   AtvSendHomeMute(int nOff);
	BOOL   AtvOnNotStopStatus();
	int    AtvSendMidCmd(UINT8 cmd,UINT32 u4Key = 0);              // export function for UI send msg to middle ware
	int    AtvGetMidStatus(UINT32* pData);            // export function for UI display
	int AtvGetMidStatusLen(){
		return 6;
	}
	void   AtvHandleDigitalKey(UINT32 u4Key);

	//BOOL   GetOneTunnerDefaultConfig(TCHAR* wszName,int ntype);
	void   AtvGetTunerConfigByRadioArea();						
	void   ReadTVDefaultConfg();                              // max min step fast step slow step 
	BOOL   AtvInitStationData();
	void   SetATVStaionInfoToShareMemory();
	int    GetCurFormatFreq();
	int    FreqInFreqArrayPos(BOOL bup);
	BOOL   InitI2C();
	BOOL   CloseI2C();
	BOOL   SetTvDriverSystemFormat(int nFormat);
	void   SetTvDriverFreq(UINT32 nFreq, int nMode = 0);
	BOOL   AtvFreqIsValid(  UINT32 nFre);
	int    GetTvDriverLock();
	int    GetTvDriverAfc(UINT8* uafc,UINT8* uvif);
	int    FreqInFreqArrayCH(int nFre);
	void   SetATVShareMemoryToArray();
	void   AtvSetDefaultStation();
	BOOL   SearchFreqStep(int nCurFreq,int nStep,BOOL bAddorDec);
	void   FinishSimSrch(int ntvFun);
	void   StopAtvSearchState();
	BOOL   QueryTVSignalState();
	BOOL   GetTvSigalValid();
	void   TvTunerDetect();
	void   FormatChangeSendFreq();
	void   StartATVJumpFreqMuteTime();
	//static	AtvAndArmProtocol * GetInstance(){return &m_Instance;};

	void SetSearchPreFreq(UINT32 u4Fre);

	UINT32 GetSearchPreFreq();

	void ResetDefaultAtvStation();

	int AtvGetMuteStatue();
	void AtvAreaChanged(int nArea);
	void SetCurFreqFormat(int nFormat);
	void array2Config(unsigned int *configData, unsigned int dataLen);
	void config2Array(unsigned int *configData, unsigned int dataLen);
	void printFormat(int format);
private:
	static	AtvAndArmProtocol m_Instance;
	ATV_STATION_LIST m_ATVStation;
	int m_uMute;
	UINT8	m_uIsAtvAreaChanged;
public:
	SHARE_MEMORY_FileInfo_T* pShareFileInfo;
#ifdef ATVTYPE_ADT1028
	CCdt1028*	m_pAtvDriver;
#endif
};

extern "C"{
	int SimSrch(AtvAndArmProtocol *);
}

#endif // #ifndef _WYD_ATV_MIDDLEWARE_PROTOCOL_H_
