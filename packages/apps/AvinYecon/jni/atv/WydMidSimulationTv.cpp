#if  1//SOURCE_ATV_SUPPORT
#include "WydMidSimulationTv.h"

#define TAG "WydMidSimulationTv"
#include "../Common.h"

#define  TV_CONFIG_FILE	      TEXT("Tv.ini")

#define  LOG_ATV  1
// #define  ATV_MUTE         0
// #define  ATV_UNMUTE       1

#define IS_THERE_VIDEO_SINAL_TIMER_INTERNER	1500
#define ATV_SAVE         1
#define ATV_UNSAVE       0
#define ATV_MUTE         1
#define ATV_UNMUTE       0
enum
{
	ATV_MUTE_OFF = 0,
	ATV_MUTE_ON ,
};
enum
{
	ATV_CONFIG_AMS_FROM_MIN_TO_MAX =0,
	ATV_CONFIG_AMS_FROM_POINT_FRE_NODE,
};

UINT8 m_u1AMSPointPos = 0;
UINT8 m_u1PointSearcFunction = 0;

#define AREA_TV  0

#define  ATV_MAX_FRE_TABLE_SIZE	100


enum
{
	ATV_POINT_SEARCH_NULL,
	ATV_POINT_SEARCH_ASM,
	ATV_POINT_SEARCH_PS,
	ATV_POINT_SEARCH_INC,
	ATV_POINT_SEARCH_DEC,
};

enum
{
	SET_NULL,
	SET_FREQ_LOCK,
	SET_FREQ_AFC_VIF,
	SET_3360_DRVIER_SIGNAL,
	SET_SIGNAL,
	SET_PS_FREQ,
	SET_CUR_EQUAL_MAX,
	SET_PS_HAVE_SIGNAL,
	SET_PS_SYNC_OR_WINDOW_SIGNAL,
	SET_SYNC_OR_WINDOW_SIGNAL,
	SET_UPFRE_SYNC_AND_WINDOW_SIGNAL_ARRIVE_REAL_FREQ,
	SET_UPFRE_SYNC_AND_WINDOW_SIGNAL_BUT_NOT_ARRIVE_REAL_FREQ,
	SET_DOWNFRE_SYNC_AND_WINDOW_SIGNAL_ARRIVE_REAL_FREQ,
	SET_DOWNFRE_SYNC_AND_WINDOW_SIGNAL_BUT_NOT_ARRIVE_REAL_FREQ,
	SET_PS_COUNT_POS,
};
#if 0
enum
{
	ATVAREA_RUSSIA = 0,
	ATVAREA_EasternEurope,
	ATVAREA_China,
	ATVAREA_Italy,
	ATVAREA_Brazil,
	ATVAREA_Europe,
	ATVAREA_South_Africa,
};
#else
enum
{
	ATVAREA_RUSSIA = 0,//����˹
	ATVAREA_Europe=1,//ŷ��
	ATVAREA_China,//�й�
	ATVAREA_Italy,//�����
	ATVAREA_EasternEurope,//��ŷ
	ATVAREA_Brazil,//����
	ATVAREA_South_Africa,//�Ϸ�
};
#endif
typedef struct
{
	UINT32 HadSeachstationIndex;
	UINT32 SeakUporDownIndex;
	UINT   m_timer;
	BYTE   format;
	UINT32 CurrentFreq;
	UINT32 PreFreq;
	BYTE   TvFlag;
	BYTE   TvModeFlag;
	BYTE   m_u1AMSPS;
	UINT32 m_u4MemPsStatusTimer;
	TV_TUNER TvTuner;
	UINT8  TvTunerContinue;
	UINT8  TvTunerLastVif;
	UINT16 TvTunerLastFreq;
	UINT16 TvTunerFoundFreq;
	UINT8  TvTunerFoundFlag;
	UINT8  TvTunerFound;
	UINT32 m_u4MemSearchStatusTimer;
	UINT32 m_u4MemGetFreLockFlag;
	UINT32 m_u4MemSearchAbort;
	UINT32 m_u4MemSearchStatus ;
	int	   m_nGetAFCVIF;
	int	   m_nATvArea;
	UINT16 MinFreq;
	UINT16 MaxFreq;
	UINT8  StepFreq;
	UINT8  FastStep;
	UINT8  SlowStep;
	UINT32 u4SearchPreFreq;
	UINT32 u4KeyHandleState;
	UINT32 u4KeyValue;
	UINT8  u1KeyTimer;
	UINT32 m_u1MemMuteTimerPer20ms;
	UINT8  m_u1HomeMuteFlag;
}ATV_INFO;
typedef struct
{
	UINT32 uATVCurFreq;
	UINT32 uATVFormat;
	UINT32 u1ATVIndex;
	UINT32 uATVStationSize;
	UINT32 nList[ATV_STATION_SIZE];
	UINT32 u1ListFormat[ATV_STATION_SIZE];
}ATV_LIST;
ATV_LIST m_rATVStation[11];
ATV_INFO m_AtvDriverStruct;
ATV_LIST m_rATVDefaultStation;
//void PrintfFreq(UINT32 * ,int len);

#if 0
CODE CONST U16 TvFreq_PAL_DK[]=
{//(100 CHs)
	//--------------------0-----------------1-----------------2-----------------3----------------4-----------------5-----------------6-----------------7----------------8-----------------9------------------------
	/*00*/	_TV_FREQ_(49.750),_TV_FREQ_(57.750),_TV_FREQ_(65.750),_TV_FREQ_(77.250),_TV_FREQ_(85.250),_TV_FREQ_(112.25),_TV_FREQ_(120.25),_TV_FREQ_(128.25),_TV_FREQ_(136.25),_TV_FREQ_(144.25),
	/*10*/	_TV_FREQ_(152.25),_TV_FREQ_(160.25),_TV_FREQ_(168.25),_TV_FREQ_(176.25),_TV_FREQ_(184.25),_TV_FREQ_(192.25),_TV_FREQ_(200.25),_TV_FREQ_(208.25),_TV_FREQ_(216.25),_TV_FREQ_(224.25),
	/*20*/	_TV_FREQ_(232.25),_TV_FREQ_(240.25),_TV_FREQ_(248.25),_TV_FREQ_(256.25),_TV_FREQ_(264.25),_TV_FREQ_(272.25),_TV_FREQ_(280.25),_TV_FREQ_(288.25),_TV_FREQ_(296.25),_TV_FREQ_(304.25),
	/*30*/	_TV_FREQ_(312.25),_TV_FREQ_(320.25),_TV_FREQ_(328.25),_TV_FREQ_(336.25),_TV_FREQ_(344.25),_TV_FREQ_(352.25),_TV_FREQ_(360.25),_TV_FREQ_(368.25),_TV_FREQ_(376.25),_TV_FREQ_(384.25),
	/*40*/	_TV_FREQ_(392.25),_TV_FREQ_(400.25),_TV_FREQ_(408.25),_TV_FREQ_(416.25),_TV_FREQ_(424.25),_TV_FREQ_(432.25),_TV_FREQ_(440.25),_TV_FREQ_(448.25),_TV_FREQ_(456.25),_TV_FREQ_(464.25),
	/*50*/	_TV_FREQ_(471.25),_TV_FREQ_(479.25),_TV_FREQ_(487.25),_TV_FREQ_(495.25),_TV_FREQ_(503.25),_TV_FREQ_(511.25),_TV_FREQ_(519.25),_TV_FREQ_(527.25),_TV_FREQ_(535.25),_TV_FREQ_(543.25),
	/*60*/	_TV_FREQ_(551.25),_TV_FREQ_(559.25),_TV_FREQ_(567.25),_TV_FREQ_(575.25),_TV_FREQ_(583.25),_TV_FREQ_(591.25),_TV_FREQ_(599.25),_TV_FREQ_(607.25),_TV_FREQ_(615.25),_TV_FREQ_(623.25),
	/*70*/	_TV_FREQ_(631.25),_TV_FREQ_(639.25),_TV_FREQ_(647.25),_TV_FREQ_(655.25),_TV_FREQ_(663.25),_TV_FREQ_(671.25),_TV_FREQ_(679.25),_TV_FREQ_(687.25),_TV_FREQ_(695.25),_TV_FREQ_(703.25),
	/*80*/	_TV_FREQ_(711.25),_TV_FREQ_(719.25),_TV_FREQ_(727.25),_TV_FREQ_(735.25),_TV_FREQ_(743.25),_TV_FREQ_(751.25),_TV_FREQ_(759.25),_TV_FREQ_(767.25),_TV_FREQ_(775.25),_TV_FREQ_(783.25),
	/*90*/	_TV_FREQ_(791.25),_TV_FREQ_(799.25),_TV_FREQ_(807.25),_TV_FREQ_(815.25),_TV_FREQ_(823.25),_TV_FREQ_(831.25),_TV_FREQ_(839.25),_TV_FREQ_(847.25),_TV_FREQ_(855.25),_TV_FREQ_(863.25)
};

CODE CONST U16 TvFreq_PAL_I[]=
{//(100 CHs)
	//--------------------0-----------------1-----------------2-----------------3----------------4-----------------5-----------------6-----------------7----------------8-----------------9------------------------
	/*00*/	_TV_FREQ_(175.25),_TV_FREQ_(183.25),_TV_FREQ_(191.25),_TV_FREQ_(199.25),_TV_FREQ_(207.25),_TV_FREQ_(215.25),_TV_FREQ_(223.25),_TV_FREQ_(231.25),_TV_FREQ_(247.25),_TV_FREQ_(471.25),
	/*10*/	_TV_FREQ_(479.25),_TV_FREQ_(487.25),_TV_FREQ_(495.25),_TV_FREQ_(503.25),_TV_FREQ_(511.25),_TV_FREQ_(519.25),_TV_FREQ_(527.25),_TV_FREQ_(535.25),_TV_FREQ_(543.25),_TV_FREQ_(551.25),
	/*20*/	_TV_FREQ_(559.25),_TV_FREQ_(567.25),_TV_FREQ_(575.25),_TV_FREQ_(583.25),_TV_FREQ_(591.25),_TV_FREQ_(599.25),_TV_FREQ_(607.25),_TV_FREQ_(615.25),_TV_FREQ_(623.25),_TV_FREQ_(631.25),
	/*30*/	_TV_FREQ_(639.25),_TV_FREQ_(647.25),_TV_FREQ_(655.25),_TV_FREQ_(663.25),_TV_FREQ_(671.25),_TV_FREQ_(679.25),_TV_FREQ_(687.25),_TV_FREQ_(695.25),_TV_FREQ_(703.25),_TV_FREQ_(711.25),
	/*40*/	_TV_FREQ_(719.25),_TV_FREQ_(727.25),_TV_FREQ_(735.25),_TV_FREQ_(743.25),_TV_FREQ_(751.25),_TV_FREQ_(759.25),_TV_FREQ_(767.25),_TV_FREQ_(775.25),_TV_FREQ_(783.25),_TV_FREQ_(791.25),
	/*50*/	_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		
	/*60*/	_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		
	/*70*/	_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		
	/*80*/	_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		
	/*90*/	_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0)		
};

CODE CONST U16 TvFreq_PAL_BG[]=
{//(100 CHs)
	//-----------0-----------------1-----------------2-----------------3-----------------4-----------------5-----------------6-----------------7----------------8-----------------9------------------------
             _TV_FREQ_(48.250),_TV_FREQ_(55.250),_TV_FREQ_(62.250),_TV_FREQ_(175.25),_TV_FREQ_(182.25),_TV_FREQ_(189.25),_TV_FREQ_(196.25),_TV_FREQ_(203.25),
	/*10*/	_TV_FREQ_(210.25),_TV_FREQ_(217.25),_TV_FREQ_(224.25),_TV_FREQ_(0),	     _TV_FREQ_(0),	 _TV_FREQ_(0),	    _TV_FREQ_(0),	       _TV_FREQ_(0),	   _TV_FREQ_(0),	      _TV_FREQ_(0),
	/*20*/	_TV_FREQ_(0),	   _TV_FREQ_(471.25),_TV_FREQ_(479.25),_TV_FREQ_(487.25),_TV_FREQ_(495.25),_TV_FREQ_(503.25),_TV_FREQ_(511.25),_TV_FREQ_(519.25),_TV_FREQ_(527.25),_TV_FREQ_(535.25),
	/*30*/	_TV_FREQ_(543.25),_TV_FREQ_(551.25),_TV_FREQ_(559.25),_TV_FREQ_(567.25),_TV_FREQ_(575.25),_TV_FREQ_(583.25),_TV_FREQ_(591.25),_TV_FREQ_(599.25),_TV_FREQ_(607.25),_TV_FREQ_(615.25),
	/*40*/	_TV_FREQ_(623.25),_TV_FREQ_(631.25),_TV_FREQ_(639.25),_TV_FREQ_(647.25),_TV_FREQ_(655.25),_TV_FREQ_(663.25),_TV_FREQ_(671.25),_TV_FREQ_(679.25),_TV_FREQ_(687.25),_TV_FREQ_(695.25),
	/*50*/	_TV_FREQ_(703.25),_TV_FREQ_(711.25),_TV_FREQ_(719.25),_TV_FREQ_(727.25),_TV_FREQ_(735.25),_TV_FREQ_(743.25),_TV_FREQ_(751.25),_TV_FREQ_(759.25),_TV_FREQ_(767.25),_TV_FREQ_(775.25),
	/*60*/	_TV_FREQ_(783.25),_TV_FREQ_(791.25),_TV_FREQ_(799.25),_TV_FREQ_(807.25),_TV_FREQ_(815.25),_TV_FREQ_(823.25),_TV_FREQ_(831.25),_TV_FREQ_(839.25),_TV_FREQ_(847.25),_TV_FREQ_(855.25),	      
	/*70*/	_TV_FREQ_(0),	  _TV_FREQ_(0),	      _TV_FREQ_(0),	  _TV_FREQ_(0),	     _TV_FREQ_(0),	 _TV_FREQ_(0),	    _TV_FREQ_(0),	       _TV_FREQ_(0),	   _TV_FREQ_(0),	      _TV_FREQ_(0),	      
	/*80*/	_TV_FREQ_(0),	  _TV_FREQ_(0),	      _TV_FREQ_(0),	  _TV_FREQ_(0),	     _TV_FREQ_(0),	 _TV_FREQ_(0),	    _TV_FREQ_(0),	       _TV_FREQ_(0),	   _TV_FREQ_(0),	      _TV_FREQ_(0),	      
	/*90*/	_TV_FREQ_(0),	  _TV_FREQ_(0),	      _TV_FREQ_(0),	  _TV_FREQ_(0),	     _TV_FREQ_(0),	 _TV_FREQ_(0),	    _TV_FREQ_(0),	       _TV_FREQ_(0),	   _TV_FREQ_(0),	      _TV_FREQ_(0)	      
};

CODE CONST U16 TvFreq_NTSC_M[]=
{//(100 CHs)
	//--------------------0-----------------1----------------2-----------------3----------------4-----------------5-----------------6-----------------7----------------8-----------------9------------------------
	/*00*/	_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(55.250),_TV_FREQ_(61.250),_TV_FREQ_(67.250),_TV_FREQ_(77.250),_TV_FREQ_(83.250),_TV_FREQ_(175.25),_TV_FREQ_(181.25),_TV_FREQ_(187.25),
	/*01*/	_TV_FREQ_(193.25),_TV_FREQ_(199.25),_TV_FREQ_(205.25),_TV_FREQ_(211.25),_TV_FREQ_(471.25),_TV_FREQ_(477.25),_TV_FREQ_(483.25),_TV_FREQ_(489.25),_TV_FREQ_(495.25),_TV_FREQ_(501.25),
	/*02*/	_TV_FREQ_(507.25),_TV_FREQ_(513.25),_TV_FREQ_(519.25),_TV_FREQ_(525.25),_TV_FREQ_(531.25),_TV_FREQ_(537.25),_TV_FREQ_(543.25),_TV_FREQ_(549.25),_TV_FREQ_(555.25),_TV_FREQ_(561.25),
	/*03*/	_TV_FREQ_(567.25),_TV_FREQ_(573.25),_TV_FREQ_(579.25),_TV_FREQ_(585.25),_TV_FREQ_(591.25),_TV_FREQ_(597.25),_TV_FREQ_(603.25),_TV_FREQ_(609.25),_TV_FREQ_(615.25),_TV_FREQ_(621.25),
	/*04*/	_TV_FREQ_(627.25),_TV_FREQ_(633.25),_TV_FREQ_(639.25),_TV_FREQ_(645.25),_TV_FREQ_(651.25),_TV_FREQ_(657.25),_TV_FREQ_(663.25),_TV_FREQ_(669.25),_TV_FREQ_(675.25),_TV_FREQ_(681.25),
	/*05*/	_TV_FREQ_(687.25),_TV_FREQ_(693.25),_TV_FREQ_(699.25),_TV_FREQ_(705.25),_TV_FREQ_(711.25),_TV_FREQ_(717.25),_TV_FREQ_(723.25),_TV_FREQ_(729.25),_TV_FREQ_(735.25),_TV_FREQ_(741.25),
	/*06*/	_TV_FREQ_(747.25),_TV_FREQ_(753.25),_TV_FREQ_(759.25),_TV_FREQ_(765.25),_TV_FREQ_(771.25),_TV_FREQ_(777.25),_TV_FREQ_(783.25),_TV_FREQ_(789.25),_TV_FREQ_(795.25),_TV_FREQ_(801.25),
	/*07*/	_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		
	/*08*/	_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		
	/*09*/	_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0)		
};

CODE CONST U16 TvFreq_SECAM_DK[]=
{//(100 CHs)
	//--------------------0-----------------1----------------2-----------------3----------------4-----------------5-----------------6-----------------7----------------8-----------------9------------------------
	/*00*/	_TV_FREQ_(49.750),_TV_FREQ_(59.250),_TV_FREQ_(77.250),_TV_FREQ_(85.250),_TV_FREQ_(93.250),_TV_FREQ_(175.25),_TV_FREQ_(183.25),_TV_FREQ_(191.25),_TV_FREQ_(199.25),_TV_FREQ_(207.25),
	/*10*/	_TV_FREQ_(215.25),_TV_FREQ_(223.25),_TV_FREQ_(49.750),_TV_FREQ_(49.750),_TV_FREQ_(49.750),_TV_FREQ_(49.750),_TV_FREQ_(49.750),_TV_FREQ_(49.750),_TV_FREQ_(49.750),_TV_FREQ_(49.750),
	/*20*/	_TV_FREQ_(471.25),_TV_FREQ_(479.25),_TV_FREQ_(487.25),_TV_FREQ_(495.25),_TV_FREQ_(503.25),_TV_FREQ_(511.25),_TV_FREQ_(519.25),_TV_FREQ_(527.25),_TV_FREQ_(535.25),_TV_FREQ_(543.25),
	/*30*/	_TV_FREQ_(551.25),_TV_FREQ_(559.25),_TV_FREQ_(567.25),_TV_FREQ_(575.25),_TV_FREQ_(583.25),_TV_FREQ_(591.25),_TV_FREQ_(599.25),_TV_FREQ_(607.25),_TV_FREQ_(615.25),_TV_FREQ_(623.25),
	/*40*/	_TV_FREQ_(631.25),_TV_FREQ_(639.25),_TV_FREQ_(647.25),_TV_FREQ_(655.25),_TV_FREQ_(663.25),_TV_FREQ_(671.25),_TV_FREQ_(679.25),_TV_FREQ_(687.25),_TV_FREQ_(695.25),_TV_FREQ_(703.25),
	/*50*/	_TV_FREQ_(711.25),_TV_FREQ_(719.25),_TV_FREQ_(727.25),_TV_FREQ_(735.25),_TV_FREQ_(743.25),_TV_FREQ_(751.25),_TV_FREQ_(759.25),_TV_FREQ_(767.25),_TV_FREQ_(775.25),_TV_FREQ_(783.25),
	/*60*/	_TV_FREQ_(791.25),_TV_FREQ_(799.25),_TV_FREQ_(807.25),_TV_FREQ_(815.25),_TV_FREQ_(823.25),_TV_FREQ_(831.25),_TV_FREQ_(839.25),_TV_FREQ_(847.25),_TV_FREQ_(855.25),_TV_FREQ_(0),
	/*70*/	_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		
	/*80*/	_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		
	/*90*/	_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0)		
};

CODE CONST U16 TvFreq_SECAM_BG[]=
{//(100 CHs)
	//--------------------0-----------------1----------------2-----------------3----------------4-----------------5-----------------6-----------------7----------------8-----------------9------------------------
	/*00*/	_TV_FREQ_(48.250),_TV_FREQ_(55.250),_TV_FREQ_(62.250),_TV_FREQ_(175.25),_TV_FREQ_(182.25),_TV_FREQ_(189.25),_TV_FREQ_(196.25),_TV_FREQ_(203.25),_TV_FREQ_(210.25),_TV_FREQ_(217.25),
	/*10*/	_TV_FREQ_(224.25),_TV_FREQ_(471.25),_TV_FREQ_(479.25),_TV_FREQ_(487.25),_TV_FREQ_(495.25),_TV_FREQ_(503.25),_TV_FREQ_(511.25),_TV_FREQ_(519.25),_TV_FREQ_(527.25),_TV_FREQ_(535.25),
	/*20*/	_TV_FREQ_(543.25),_TV_FREQ_(551.25),_TV_FREQ_(559.25),_TV_FREQ_(567.25),_TV_FREQ_(575.25),_TV_FREQ_(583.25),_TV_FREQ_(591.25),_TV_FREQ_(599.25),_TV_FREQ_(607.25),_TV_FREQ_(615.25),
	/*30*/	_TV_FREQ_(623.25),_TV_FREQ_(631.25),_TV_FREQ_(639.25),_TV_FREQ_(647.25),_TV_FREQ_(655.25),_TV_FREQ_(663.25),_TV_FREQ_(671.25),_TV_FREQ_(679.25),_TV_FREQ_(687.25),_TV_FREQ_(695.25),
	/*40*/	_TV_FREQ_(703.25),_TV_FREQ_(711.25),_TV_FREQ_(719.25),_TV_FREQ_(727.25),_TV_FREQ_(735.25),_TV_FREQ_(743.25),_TV_FREQ_(751.25),_TV_FREQ_(759.25),_TV_FREQ_(767.25),_TV_FREQ_(775.25),
	/*50*/	_TV_FREQ_(783.25),_TV_FREQ_(791.25),_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),
	/*60*/	_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		
	/*70*/	_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		
	/*80*/	_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		
	/*90*/	_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0),		_TV_FREQ_(0),	  _TV_FREQ_(0)		
};

#else
const UINT16 TvFreq_PAL[]=
{//(100 CHs)
			_TV_FREQ_(49.75), _TV_FREQ_(57.75), _TV_FREQ_(65.75), _TV_FREQ_(77.25), _TV_FREQ_(85.25), _TV_FREQ_(112.25),_TV_FREQ_(120.25),_TV_FREQ_(128.25),_TV_FREQ_(136.25),_TV_FREQ_(144.25),
			_TV_FREQ_(152.25),_TV_FREQ_(160.25),_TV_FREQ_(168.25),_TV_FREQ_(176.25),_TV_FREQ_(184.25),_TV_FREQ_(192.25),_TV_FREQ_(200.25),_TV_FREQ_(208.25),_TV_FREQ_(216.25),_TV_FREQ_(224.25),
			_TV_FREQ_(232.25),_TV_FREQ_(240.25),_TV_FREQ_(248.25),_TV_FREQ_(256.25),_TV_FREQ_(264.25),_TV_FREQ_(272.25),_TV_FREQ_(280.25),_TV_FREQ_(288.25),_TV_FREQ_(296.25),_TV_FREQ_(304.25),
			_TV_FREQ_(312.25),_TV_FREQ_(320.25),_TV_FREQ_(328.25),_TV_FREQ_(336.25),_TV_FREQ_(344.25),_TV_FREQ_(352.25),_TV_FREQ_(360.25),_TV_FREQ_(368.25),_TV_FREQ_(376.25),_TV_FREQ_(384.25),
			_TV_FREQ_(392.25),_TV_FREQ_(400.25),_TV_FREQ_(408.25),_TV_FREQ_(416.25),_TV_FREQ_(424.25),_TV_FREQ_(432.25),_TV_FREQ_(440.25),_TV_FREQ_(448.25),_TV_FREQ_(456.25),_TV_FREQ_(464.25),
			_TV_FREQ_(471.25),_TV_FREQ_(479.25),_TV_FREQ_(487.25),_TV_FREQ_(495.25),_TV_FREQ_(503.25),_TV_FREQ_(511.25),_TV_FREQ_(519.25),_TV_FREQ_(527.25),_TV_FREQ_(535.25),_TV_FREQ_(543.25),
			_TV_FREQ_(551.25),_TV_FREQ_(559.25),_TV_FREQ_(567.25),_TV_FREQ_(575.25),_TV_FREQ_(583.25),_TV_FREQ_(591.25),_TV_FREQ_(599.25),_TV_FREQ_(607.25),_TV_FREQ_(615.25),_TV_FREQ_(623.25),
			_TV_FREQ_(631.25),_TV_FREQ_(639.25),_TV_FREQ_(647.25),_TV_FREQ_(655.25),_TV_FREQ_(663.25),_TV_FREQ_(671.25),_TV_FREQ_(679.25),_TV_FREQ_(687.25),_TV_FREQ_(695.25),_TV_FREQ_(703.25),
			_TV_FREQ_(711.25),_TV_FREQ_(719.25),_TV_FREQ_(727.25),_TV_FREQ_(735.25),_TV_FREQ_(743.25),_TV_FREQ_(751.25),_TV_FREQ_(759.25),_TV_FREQ_(767.25),_TV_FREQ_(775.25),_TV_FREQ_(783.25),
			_TV_FREQ_(791.25),_TV_FREQ_(799.25),_TV_FREQ_(807.25),_TV_FREQ_(815.25),_TV_FREQ_(823.25),_TV_FREQ_(831.25),_TV_FREQ_(839.25),_TV_FREQ_(847.25),_TV_FREQ_(855.25),_TV_FREQ_(863.25),
};

const UINT16 TvFreq_NTSC[]=
{//(100 CHs)
	_TV_FREQ_(49.75), _TV_FREQ_(57.75), _TV_FREQ_(65.75), _TV_FREQ_(77.25), _TV_FREQ_(85.25), _TV_FREQ_(112.25),_TV_FREQ_(120.25),_TV_FREQ_(128.25),_TV_FREQ_(136.25),_TV_FREQ_(144.25),
	_TV_FREQ_(152.25),_TV_FREQ_(160.25),_TV_FREQ_(168.25),_TV_FREQ_(176.25),_TV_FREQ_(184.25),_TV_FREQ_(192.25),_TV_FREQ_(200.25),_TV_FREQ_(208.25),_TV_FREQ_(216.25),_TV_FREQ_(224.25),
	_TV_FREQ_(232.25),_TV_FREQ_(240.25),_TV_FREQ_(248.25),_TV_FREQ_(256.25),_TV_FREQ_(264.25),_TV_FREQ_(272.25),_TV_FREQ_(280.25),_TV_FREQ_(288.25),_TV_FREQ_(296.25),_TV_FREQ_(304.25),
	_TV_FREQ_(312.25),_TV_FREQ_(320.25),_TV_FREQ_(328.25),_TV_FREQ_(336.25),_TV_FREQ_(344.25),_TV_FREQ_(352.25),_TV_FREQ_(360.25),_TV_FREQ_(368.25),_TV_FREQ_(376.25),_TV_FREQ_(384.25),
	_TV_FREQ_(392.25),_TV_FREQ_(400.25),_TV_FREQ_(408.25),_TV_FREQ_(416.25),_TV_FREQ_(424.25),_TV_FREQ_(432.25),_TV_FREQ_(440.25),_TV_FREQ_(448.25),_TV_FREQ_(456.25),_TV_FREQ_(464.25),
	_TV_FREQ_(471.25),_TV_FREQ_(479.25),_TV_FREQ_(487.25),_TV_FREQ_(495.25),_TV_FREQ_(503.25),_TV_FREQ_(511.25),_TV_FREQ_(519.25),_TV_FREQ_(527.25),_TV_FREQ_(535.25),_TV_FREQ_(543.25),
	_TV_FREQ_(551.25),_TV_FREQ_(559.25),_TV_FREQ_(567.25),_TV_FREQ_(575.25),_TV_FREQ_(583.25),_TV_FREQ_(591.25),_TV_FREQ_(599.25),_TV_FREQ_(607.25),_TV_FREQ_(615.25),_TV_FREQ_(623.25),
	_TV_FREQ_(631.25),_TV_FREQ_(639.25),_TV_FREQ_(647.25),_TV_FREQ_(655.25),_TV_FREQ_(663.25),_TV_FREQ_(671.25),_TV_FREQ_(679.25),_TV_FREQ_(687.25),_TV_FREQ_(695.25),_TV_FREQ_(703.25),
	_TV_FREQ_(711.25),_TV_FREQ_(719.25),_TV_FREQ_(727.25),_TV_FREQ_(735.25),_TV_FREQ_(743.25),_TV_FREQ_(751.25),_TV_FREQ_(759.25),_TV_FREQ_(767.25),_TV_FREQ_(775.25),_TV_FREQ_(783.25),
	_TV_FREQ_(791.25),_TV_FREQ_(799.25),_TV_FREQ_(807.25),_TV_FREQ_(815.25),_TV_FREQ_(823.25),_TV_FREQ_(831.25),_TV_FREQ_(839.25),_TV_FREQ_(847.25),_TV_FREQ_(855.25),_TV_FREQ_(863.25),
};

//	SECAM
const UINT16 vFreq_SECAM[]=
{//(100 CHs)
	_TV_FREQ_(49.75), _TV_FREQ_(57.75), _TV_FREQ_(65.75), _TV_FREQ_(77.25), _TV_FREQ_(85.25), _TV_FREQ_(112.25),_TV_FREQ_(120.25),_TV_FREQ_(128.25),_TV_FREQ_(136.25),_TV_FREQ_(144.25),
	_TV_FREQ_(152.25),_TV_FREQ_(160.25),_TV_FREQ_(168.25),_TV_FREQ_(176.25),_TV_FREQ_(184.25),_TV_FREQ_(192.25),_TV_FREQ_(200.25),_TV_FREQ_(208.25),_TV_FREQ_(216.25),_TV_FREQ_(224.25),
	_TV_FREQ_(232.25),_TV_FREQ_(240.25),_TV_FREQ_(248.25),_TV_FREQ_(256.25),_TV_FREQ_(264.25),_TV_FREQ_(272.25),_TV_FREQ_(280.25),_TV_FREQ_(288.25),_TV_FREQ_(296.25),_TV_FREQ_(304.25),
	_TV_FREQ_(312.25),_TV_FREQ_(320.25),_TV_FREQ_(328.25),_TV_FREQ_(336.25),_TV_FREQ_(344.25),_TV_FREQ_(352.25),_TV_FREQ_(360.25),_TV_FREQ_(368.25),_TV_FREQ_(376.25),_TV_FREQ_(384.25),
	_TV_FREQ_(392.25),_TV_FREQ_(400.25),_TV_FREQ_(408.25),_TV_FREQ_(416.25),_TV_FREQ_(424.25),_TV_FREQ_(432.25),_TV_FREQ_(440.25),_TV_FREQ_(448.25),_TV_FREQ_(456.25),_TV_FREQ_(464.25),
	_TV_FREQ_(471.25),_TV_FREQ_(479.25),_TV_FREQ_(487.25),_TV_FREQ_(495.25),_TV_FREQ_(503.25),_TV_FREQ_(511.25),_TV_FREQ_(519.25),_TV_FREQ_(527.25),_TV_FREQ_(535.25),_TV_FREQ_(543.25),
	_TV_FREQ_(551.25),_TV_FREQ_(559.25),_TV_FREQ_(567.25),_TV_FREQ_(575.25),_TV_FREQ_(583.25),_TV_FREQ_(591.25),_TV_FREQ_(599.25),_TV_FREQ_(607.25),_TV_FREQ_(615.25),_TV_FREQ_(623.25),
	_TV_FREQ_(631.25),_TV_FREQ_(639.25),_TV_FREQ_(647.25),_TV_FREQ_(655.25),_TV_FREQ_(663.25),_TV_FREQ_(671.25),_TV_FREQ_(679.25),_TV_FREQ_(687.25),_TV_FREQ_(695.25),_TV_FREQ_(703.25),
	_TV_FREQ_(711.25),_TV_FREQ_(719.25),_TV_FREQ_(727.25),_TV_FREQ_(735.25),_TV_FREQ_(743.25),_TV_FREQ_(751.25),_TV_FREQ_(759.25),_TV_FREQ_(767.25),_TV_FREQ_(775.25),_TV_FREQ_(783.25),
	_TV_FREQ_(791.25),_TV_FREQ_(799.25),_TV_FREQ_(807.25),_TV_FREQ_(815.25),_TV_FREQ_(823.25),_TV_FREQ_(831.25),_TV_FREQ_(839.25),_TV_FREQ_(847.25),_TV_FREQ_(855.25),_TV_FREQ_(863.25),
};

const UINT16 TvFreq_PAL_M[]= // 6
{//(100 CHs)
	//--------------------0-----------------1----------------2-----------------3----------------4-----------------5-----------------6-----------------7----------------8-----------------9------------------------
	/*00*/	_TV_FREQ_(55.250),_TV_FREQ_(61.250),_TV_FREQ_(67.250),_TV_FREQ_(77.250),_TV_FREQ_(83.250),_TV_FREQ_(175.25),_TV_FREQ_(181.25),_TV_FREQ_(187.25),_TV_FREQ_(193.25),_TV_FREQ_(199.25),
		/*10*/	_TV_FREQ_(205.25),_TV_FREQ_(211.25),_TV_FREQ_(471.25),_TV_FREQ_(477.25),_TV_FREQ_(483.25),_TV_FREQ_(489.25),_TV_FREQ_(495.25),_TV_FREQ_(501.25),_TV_FREQ_(507.25),_TV_FREQ_(513.25),
		/*20*/	_TV_FREQ_(519.25),_TV_FREQ_(525.25),_TV_FREQ_(531.25),_TV_FREQ_(537.25),_TV_FREQ_(543.25),_TV_FREQ_(549.25),_TV_FREQ_(555.25),_TV_FREQ_(561.25),_TV_FREQ_(567.25),_TV_FREQ_(573.25),
		/*30*/	_TV_FREQ_(579.25),_TV_FREQ_(585.25),_TV_FREQ_(591.25),_TV_FREQ_(597.25),_TV_FREQ_(603.25),_TV_FREQ_(609.25),_TV_FREQ_(615.25),_TV_FREQ_(621.25),_TV_FREQ_(627.25),_TV_FREQ_(633.25),
		/*40*/	_TV_FREQ_(639.25),_TV_FREQ_(645.25),_TV_FREQ_(651.25),_TV_FREQ_(657.25),_TV_FREQ_(663.25),_TV_FREQ_(669.25),_TV_FREQ_(675.25),_TV_FREQ_(681.25),_TV_FREQ_(687.25),_TV_FREQ_(693.25),
		/*50*/	_TV_FREQ_(699.25),_TV_FREQ_(705.25),_TV_FREQ_(711.25),_TV_FREQ_(717.25),_TV_FREQ_(723.25),_TV_FREQ_(729.25),_TV_FREQ_(735.25),_TV_FREQ_(741.25),_TV_FREQ_(747.25),_TV_FREQ_(753.25),
		/*60*/	_TV_FREQ_(759.25),_TV_FREQ_(765.25),_TV_FREQ_(771.25),_TV_FREQ_(777.25),_TV_FREQ_(783.25),_TV_FREQ_(789.25),_TV_FREQ_(795.25),_TV_FREQ_(801.25),_TV_FREQ_(55.250),_TV_FREQ_(55.250),
		/*70*/	_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),
		/*80*/	_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),
		/*90*/	_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),_TV_FREQ_(55.250),
};
// Russia oirt SECAM DK
const UINT16 vFreq_OIRT_SECAM_DK[]=
{//(100 CHs)
	_TV_FREQ_(49.75), _TV_FREQ_(59.25), _TV_FREQ_(77.25), _TV_FREQ_(85.25), _TV_FREQ_(93.25), _TV_FREQ_(175.25),_TV_FREQ_(183.25),_TV_FREQ_(191.25),_TV_FREQ_(199.25),_TV_FREQ_(207.25),
	_TV_FREQ_(215.25),_TV_FREQ_(223.25),_TV_FREQ_(168.25),_TV_FREQ_(176.25),_TV_FREQ_(184.25),_TV_FREQ_(192.25),_TV_FREQ_(200.25),_TV_FREQ_(208.25),_TV_FREQ_(216.25),_TV_FREQ_(224.25),
	_TV_FREQ_(471.25),_TV_FREQ_(479.25),_TV_FREQ_(487.25),_TV_FREQ_(495.25),_TV_FREQ_(503.25),_TV_FREQ_(511.25),_TV_FREQ_(519.25),_TV_FREQ_(527.25),_TV_FREQ_(535.25),_TV_FREQ_(543.25),
	_TV_FREQ_(551.25),_TV_FREQ_(559.25),_TV_FREQ_(567.25),_TV_FREQ_(575.25),_TV_FREQ_(583.25),_TV_FREQ_(591.25),_TV_FREQ_(599.25),_TV_FREQ_(607.25),_TV_FREQ_(615.25),_TV_FREQ_(623.25),
	_TV_FREQ_(631.25),_TV_FREQ_(639.25),_TV_FREQ_(647.25),_TV_FREQ_(655.25),_TV_FREQ_(663.25),_TV_FREQ_(671.25),_TV_FREQ_(679.25),_TV_FREQ_(687.25),_TV_FREQ_(695.25),_TV_FREQ_(703.25),
	_TV_FREQ_(711.25),_TV_FREQ_(719.25),_TV_FREQ_(727.25),_TV_FREQ_(735.25),_TV_FREQ_(743.25),_TV_FREQ_(751.25),_TV_FREQ_(759.25),_TV_FREQ_(767.25),_TV_FREQ_(775.25),_TV_FREQ_(783.25),
	_TV_FREQ_(791.25),_TV_FREQ_(799.25),_TV_FREQ_(807.25),_TV_FREQ_(815.25),_TV_FREQ_(823.25),_TV_FREQ_(831.25),_TV_FREQ_(839.25),_TV_FREQ_(847.25),_TV_FREQ_(855.25),_TV_FREQ_(623.25),
	_TV_FREQ_(631.25),_TV_FREQ_(639.25),_TV_FREQ_(647.25),_TV_FREQ_(655.25),_TV_FREQ_(663.25),_TV_FREQ_(671.25),_TV_FREQ_(679.25),_TV_FREQ_(687.25),_TV_FREQ_(695.25),_TV_FREQ_(703.25),
	_TV_FREQ_(711.25),_TV_FREQ_(719.25),_TV_FREQ_(727.25),_TV_FREQ_(735.25),_TV_FREQ_(743.25),_TV_FREQ_(751.25),_TV_FREQ_(759.25),_TV_FREQ_(767.25),_TV_FREQ_(775.25),_TV_FREQ_(783.25),
	_TV_FREQ_(791.25),_TV_FREQ_(799.25),_TV_FREQ_(807.25),_TV_FREQ_(815.25),_TV_FREQ_(823.25),_TV_FREQ_(831.25),_TV_FREQ_(839.25),_TV_FREQ_(847.25),_TV_FREQ_(855.25),_TV_FREQ_(863.25),
};

// US NTSC M/N US CH & JPN CH
const UINT16 vFreq_USJPN_NTSC_MN[]=
{//(100 CHs)
	_TV_FREQ_(55.25), _TV_FREQ_(61.25), _TV_FREQ_(67.25), _TV_FREQ_(77.25), _TV_FREQ_(83.25), _TV_FREQ_(175.25),_TV_FREQ_(181.25),_TV_FREQ_(187.25),_TV_FREQ_(193.25),_TV_FREQ_(199.25),
	_TV_FREQ_(205.25),_TV_FREQ_(211.25),_TV_FREQ_(471.25),_TV_FREQ_(477.25),_TV_FREQ_(483.25),_TV_FREQ_(489.25),_TV_FREQ_(495.25),_TV_FREQ_(501.25),_TV_FREQ_(507.25),_TV_FREQ_(513.25),
	_TV_FREQ_(519.25),_TV_FREQ_(525.25),_TV_FREQ_(531.25),_TV_FREQ_(537.25),_TV_FREQ_(543.25),_TV_FREQ_(549.25),_TV_FREQ_(555.25),_TV_FREQ_(561.25),_TV_FREQ_(567.25),_TV_FREQ_(573.25),
    _TV_FREQ_(579.25),_TV_FREQ_(585.25),_TV_FREQ_(591.25),_TV_FREQ_(597.25),_TV_FREQ_(603.25),_TV_FREQ_(609.25),_TV_FREQ_(615.25),_TV_FREQ_(621.25),_TV_FREQ_(627.25),_TV_FREQ_(633.25),
	_TV_FREQ_(639.25),_TV_FREQ_(645.25),_TV_FREQ_(651.25),_TV_FREQ_(657.25),_TV_FREQ_(663.25),_TV_FREQ_(669.25),_TV_FREQ_(675.25),_TV_FREQ_(681.25),_TV_FREQ_(687.25),_TV_FREQ_(693.25),
	_TV_FREQ_(699.25),_TV_FREQ_(705.25),_TV_FREQ_(711.25),_TV_FREQ_(717.25),_TV_FREQ_(723.25),_TV_FREQ_(729.25),_TV_FREQ_(735.25),_TV_FREQ_(741.25),_TV_FREQ_(747.25),_TV_FREQ_(753.25),
	_TV_FREQ_(759.25),_TV_FREQ_(765.25),_TV_FREQ_(771.25),_TV_FREQ_(777.25),_TV_FREQ_(783.25),_TV_FREQ_(789.25),_TV_FREQ_(795.25),_TV_FREQ_(801.25),_TV_FREQ_(615.25),_TV_FREQ_(623.25),
	_TV_FREQ_(631.25),_TV_FREQ_(639.25),_TV_FREQ_(647.25),_TV_FREQ_(655.25),_TV_FREQ_(663.25),_TV_FREQ_(671.25),_TV_FREQ_(679.25),_TV_FREQ_(687.25),_TV_FREQ_(695.25),_TV_FREQ_(703.25),
	_TV_FREQ_(711.25),_TV_FREQ_(719.25),_TV_FREQ_(727.25),_TV_FREQ_(735.25),_TV_FREQ_(743.25),_TV_FREQ_(751.25),_TV_FREQ_(759.25),_TV_FREQ_(767.25),_TV_FREQ_(775.25),_TV_FREQ_(783.25),
	_TV_FREQ_(791.25),_TV_FREQ_(799.25),_TV_FREQ_(807.25),_TV_FREQ_(815.25),_TV_FREQ_(823.25),_TV_FREQ_(831.25),_TV_FREQ_(839.25),_TV_FREQ_(847.25),_TV_FREQ_(855.25),_TV_FREQ_(863.25),
};

// CCIR CH
const UINT16 vFreq_CCIR_NTSC_MN[]=
{//(100 CHs)
	_TV_FREQ_(48.25), _TV_FREQ_(55.25), _TV_FREQ_(62.25), _TV_FREQ_(175.25),_TV_FREQ_(182.25),_TV_FREQ_(189.25),_TV_FREQ_(196.25),_TV_FREQ_(203.25),_TV_FREQ_(210.25),_TV_FREQ_(217.25),
	_TV_FREQ_(224.25),_TV_FREQ_(211.25),_TV_FREQ_(471.25),_TV_FREQ_(477.25),_TV_FREQ_(483.25),_TV_FREQ_(489.25),_TV_FREQ_(495.25),_TV_FREQ_(501.25),_TV_FREQ_(507.25),_TV_FREQ_(513.25),
	_TV_FREQ_(519.25),_TV_FREQ_(525.25),_TV_FREQ_(531.25),_TV_FREQ_(537.25),_TV_FREQ_(543.25),_TV_FREQ_(549.25),_TV_FREQ_(555.25),_TV_FREQ_(561.25),_TV_FREQ_(567.25),_TV_FREQ_(573.25),
	_TV_FREQ_(579.25),_TV_FREQ_(585.25),_TV_FREQ_(591.25),_TV_FREQ_(597.25),_TV_FREQ_(603.25),_TV_FREQ_(609.25),_TV_FREQ_(615.25),_TV_FREQ_(621.25),_TV_FREQ_(627.25),_TV_FREQ_(633.25),
	_TV_FREQ_(631.25),_TV_FREQ_(639.25),_TV_FREQ_(647.25),_TV_FREQ_(655.25),_TV_FREQ_(663.25),_TV_FREQ_(671.25),_TV_FREQ_(679.25),_TV_FREQ_(687.25),_TV_FREQ_(695.25),_TV_FREQ_(703.25),
	_TV_FREQ_(711.25),_TV_FREQ_(719.25),_TV_FREQ_(727.25),_TV_FREQ_(735.25),_TV_FREQ_(743.25),_TV_FREQ_(751.25),_TV_FREQ_(759.25),_TV_FREQ_(767.25),_TV_FREQ_(775.25),_TV_FREQ_(783.25),
	_TV_FREQ_(785.25),_TV_FREQ_(786.25),_TV_FREQ_(787.25),_TV_FREQ_(788.25),_TV_FREQ_(789.25),_TV_FREQ_(790.25),_TV_FREQ_(791.25),_TV_FREQ_(792.25),_TV_FREQ_(793.25),_TV_FREQ_(794.25),
	_TV_FREQ_(797.25),_TV_FREQ_(799.25),_TV_FREQ_(807.25),_TV_FREQ_(808.25),_TV_FREQ_(809.25),_TV_FREQ_(810.25),_TV_FREQ_(811.25),_TV_FREQ_(812.25),_TV_FREQ_(813.25),_TV_FREQ_(814.25),
	_TV_FREQ_(815.25),_TV_FREQ_(816.25),_TV_FREQ_(817.25),_TV_FREQ_(818.25),_TV_FREQ_(819.25),_TV_FREQ_(820.25),_TV_FREQ_(821.25),_TV_FREQ_(823.25),_TV_FREQ_(825.25),_TV_FREQ_(827.25),
	_TV_FREQ_(829.25),_TV_FREQ_(831.25),_TV_FREQ_(833.25),_TV_FREQ_(835.25),_TV_FREQ_(837.25),_TV_FREQ_(839.25),_TV_FREQ_(843.25),_TV_FREQ_(847.25),_TV_FREQ_(855.25),_TV_FREQ_(863.25),
};
#endif

static TV_TUNER_SYSTEM_EX SystemTunerTab[] =
{
	{TV_TUNER_SYSTEM_PAL_DK,_TV_FREQ_(48.5),	_TV_FREQ_(870),	50,20,1},
	{TV_TUNER_SYSTEM_PAL_I,_TV_FREQ_(48.5),		_TV_FREQ_(870),	50,20,1},
	{TV_TUNER_SYSTEM_PAL_BG,_TV_FREQ_(48.5),	_TV_FREQ_(870),	50,20,1},
	{TV_TUNER_SYSTEM_NTSC_MN,_TV_FREQ_(48.5),	_TV_FREQ_(890),	50,20,1},
	{TV_TUNER_SYSTEM_SECAM_DK,_TV_FREQ_(48.5),	_TV_FREQ_(870),	50,20,1},
	{TV_TUNER_SYSTEM_SECAM_BG,_TV_FREQ_(48.5),	_TV_FREQ_(870),	50,20,1},
	{TV_TUNER_SYSTEM_PAL_M,	  _TV_FREQ_(54),	_TV_FREQ_(806),	50,20,1},

};
//static TV_TUNER_SYSTEM_EX SystemTunerTab = {_TV_FREQ_(48.5),_TV_FREQ_(870),	50,20,1};

enum {
	INIT_TUNNER_MAX_TAB,
	INIT_TUNNER_MIN_TAB,
	INIT_TUNNER_STEP_TAB,
	INIT_TUNNER_FASTSTEP_TAB,
	INIT_TUNNER_SLOWSTEP_TAB,
};

const static UINT32 TVMaxTunerTab[] =  {
	_TV_FREQ_(840),  _TV_FREQ_(850), _TV_FREQ_(860), _TV_FREQ_(870), 
	_TV_FREQ_(880), _TV_FREQ_(890), _TV_FREQ_(900),_TV_FREQ_(1000)
};

const static UINT32 TVMinTunerTab[] =  {
	_TV_FREQ_(46.5),_TV_FREQ_(47.5),_TV_FREQ_(48.5),_TV_FREQ_(49.5),
	_TV_FREQ_(50.5),_TV_FREQ_(51.5),_TV_FREQ_(52.5),_TV_FREQ_(53.5)
};

const static UINT32 TVStepTunerTab[] ={
	20,			30,			40,			50,			
	60,			70,			80,			90
};

const static UINT32 TVFastTunerTab[] ={		
	5,			10,			15,			20,				
	25,			30,			35,			40
};

const static UINT32 TVSlowTunerTab[] ={		
	1,			2,			3,			4,				
    5,			6,			7,			8,
};

#define  TV_TEST_TIME_INTERVAL 30
#ifndef ATVTYPE_ADT1028
extern void TvDriverSetFreq(int nFreq);
extern void TvDriverStateConfig();
extern void TvDriverStateChange();
extern void TvDriverStateChange_ex(BYTE* block);
extern void TvDriverStateDetect_ex(UINT8* afc,UINT8* uafc,UINT8* uvif);
extern void TvDriverStateDetect();
extern void TvDriverStateConfig_ex(UINT8 System);
extern void TvDriverDeInit();
extern UINT8 GetTvDriverBand();
#endif
//AtvAndArmProtocol AtvAndArmProtocol::m_Instance;
AtvAndArmProtocol::AtvAndArmProtocol()
{
	StartATVJumpFreqMuteTime();
	m_AtvDriverStruct.u4KeyHandleState = AtvKey_Normal;
	m_AtvDriverStruct.u4KeyValue = 0;
	m_AtvDriverStruct.u1KeyTimer = 0;
	m_AtvDriverStruct.m_u1HomeMuteFlag = ATV_MUTE;
	m_AtvDriverStruct.m_u1MemMuteTimerPer20ms = 0;
	m_uIsAtvAreaChanged = 0;
	m_uMute = ATV_MUTE_ON;
#ifdef ATVTYPE_ADT1028
	m_pAtvDriver = new CCdt1028;
#endif
}

AtvAndArmProtocol::~AtvAndArmProtocol()
{
	if (!m_uIsAtvAreaChanged)
	{
		SetATVStaionInfoToShareMemory();
	}
	if (AtvOnNotStopStatus())
		FinishSimSrch(AtvStatus_SearchStop);

	//KillTimer(NULL, m_AtvDriverStruct.m_timer);

	CloseI2C();
}

void AtvAndArmProtocol::ReadTVDefaultConfg()
{
	/*
	if(!GetOneTunnerDefaultConfig(INI_ATV_DEFAULT_MAX_CONFIG,INIT_TUNNER_MAX_TAB))
		return;
	if(!GetOneTunnerDefaultConfig(INI_ATV_DEFAULT_MIN_CONFIG,INIT_TUNNER_MIN_TAB))
		return;
	if(!GetOneTunnerDefaultConfig(INI_ATV_DEFAULT_STEP_CONFIG,INIT_TUNNER_STEP_TAB))
		return;
	if(!GetOneTunnerDefaultConfig(INI_ATV_DEFAULT_FASTSTEP_CONFIG,INIT_TUNNER_FASTSTEP_TAB))
		return;
	if(!GetOneTunnerDefaultConfig(INI_ATV_DEFAULT_SLOWSTEP_CONFIG,INIT_TUNNER_SLOWSTEP_TAB))
		return;
		*/
}

/*
BOOL AtvAndArmProtocol::GetOneTunnerDefaultConfig(TCHAR* wszName,int ntype)
{
	RWIni AppIni;
	TCHAR wszTemp[64];
	TCHAR szTemp[64];
	TCHAR szTempValue[2];
	BOOL bRet = TRUE;

	AppIni.InitStorage(INI_FILE_NAME);
	// ��
	memset(wszTemp,0,sizeof(wszTemp));
	wsprintf(wszTemp,_T("%s"),_T("333533"));
	if(AppIni.GetIniString(INI_TYPE_DEFAULT_SET,wszName,szTemp) == INI_RETURN_ERR)
		return FALSE;
	else
	{
		memset(wszTemp,0,sizeof(wszTemp));
		wsprintf(wszTemp,_T("%s"),szTemp);
	}
	int nStart = 0;
	int nArrayIndex = 0;
	// max 
	for(nStart = 0; nStart < 6; nStart++ )
	{
		if( wszTemp[nStart] >= '0' && wszTemp[nStart] <= '9' )
			szTempValue[0] = wszTemp[nStart] - '0';
		else if( wszTemp[nStart] >= 'A' && wszTemp[nStart] <= 'F' )
			szTempValue[0] = wszTemp[nStart] - 'A' + 10;
		else if( wszTemp[nStart] >= 'a' && wszTemp[nStart] <= 'a' )
			szTempValue[0] = wszTemp[nStart] - 'a' + 10;
		nArrayIndex = szTempValue[0];
		if ( nArrayIndex < 8 )
		{
			switch(ntype)
			{
			case INIT_TUNNER_MAX_TAB:
				SystemTunerTab[nStart].MaxFreq = TVMaxTunerTab[nArrayIndex];
				break;
			case INIT_TUNNER_MIN_TAB:
				SystemTunerTab[nStart].MinFreq = TVMinTunerTab[nArrayIndex];
				break;
			case INIT_TUNNER_STEP_TAB:
				SystemTunerTab[nStart].StepFreq = TVStepTunerTab[nArrayIndex];
				break;
			case INIT_TUNNER_FASTSTEP_TAB:
				SystemTunerTab[nStart].FastStep = TVFastTunerTab[nArrayIndex];
				break;
			case INIT_TUNNER_SLOWSTEP_TAB:
				SystemTunerTab[nStart].SlowStep = TVSlowTunerTab[nArrayIndex];
				break;
			}
		}
	}
	return TRUE;
}
*/
void AtvAndArmProtocol::AtvGetTunerConfigByRadioArea()
{
	BYTE area=AREA_TV;
	int nRecSys=TV_TUNER_SYSTEM_PAL_DK;

	switch (m_AtvDriverStruct.m_nATvArea)
	{
	case ATVAREA_Europe:
		nRecSys=TV_TUNER_SYSTEM_PAL_BG;
		break;
	case ATVAREA_China:
		nRecSys=TV_TUNER_SYSTEM_PAL_DK;
		break;
	case ATVAREA_Brazil:
		nRecSys=TV_TUNER_SYSTEM_PAL_M;
		break;
//	case ATVAREA_Southeast_Asia://NTSC
	case ATVAREA_Italy:
		nRecSys=TV_TUNER_SYSTEM_NTSC_MN;
		break;
	case ATVAREA_EasternEurope://SECAM
	case ATVAREA_RUSSIA:
		nRecSys=TV_TUNER_SYSTEM_SECAM_DK;
		break;
	case ATVAREA_South_Africa:
		nRecSys=TV_TUNER_SYSTEM_PAL_I;
		break;

	//case RADIOAREA_Europe:
	//	nRecSys=TV_TUNER_SYSTEM_PAL_BG;
	//	break;
	//case RADIOAREA_Brazil:
	//	nRecSys=TV_TUNER_SYSTEM_PAL_I;
	//	break;
	//case RADIOAREA_Aust://NTSC
	//case RADIOAREA_MidEast:
	//case RADIOAREA_America1:
	//case RADIOAREA_America2:
	//case RADIOAREA_America3:
	//case RADIOAREA_America4:
	//case RADIOAREA_Japan:
	//	nRecSys=TV_TUNER_SYSTEM_NTSC_MN;
	//	break;
	//case RADIOAREA_OIRT://SECAM
	//	nRecSys=TV_TUNER_SYSTEM_SECAM_DK;
	//	break;
	//default: 
	//	break;
	}
	SetTvDriverSystemFormat(nRecSys);
	//m_AtvDriverStruct.format = nRecSys;
	//m_rATVStation[area].uATVFormat=nRecSys;//��ͬ��������ȱʡ�Ľ�����ʽ
	m_AtvDriverStruct.MinFreq  = SystemTunerTab[nRecSys].MinFreq;
	m_AtvDriverStruct.MaxFreq  = SystemTunerTab[nRecSys].MaxFreq;
	m_AtvDriverStruct.StepFreq = SystemTunerTab[nRecSys].StepFreq;
	m_AtvDriverStruct.FastStep = SystemTunerTab[nRecSys].FastStep;
	m_AtvDriverStruct.SlowStep = SystemTunerTab[nRecSys].SlowStep;
	AtvSetFormat(nRecSys);
}

BOOL AtvAndArmProtocol::AtvInitStationData()
{
	BOOL bRet = TRUE;
	BYTE area = AREA_TV;
	BYTE index = 0;
	UINT32 u4Freq = 0;

	// ���翪��
	m_AtvDriverStruct.m_nATvArea=pShareFileInfo->u1AtvRegion;
	AtvSetRadioArea(pShareFileInfo->u1AtvRegion);
	ReadTVDefaultConfg();
	AtvGetTunerConfigByRadioArea();
	AtvSetCurrentPresetListIndex(0);
	AtvSetHadSearchedStationIndex(0);
	AtvSetDefaultStation();
	if(pShareFileInfo->ATV_Station.uATVStationSize == 0)
	{
		//if(!AtvGetIsValidFormat())
		//	m_rATVStation[area].uATVFormat = 0;
	}
 	else
 		SetATVShareMemoryToArray();

	if(SetTvDriverSystemFormat(m_rATVStation[area].uATVFormat))
	{
		if(AtvGetValidStationNum() > 0 )
		{
			//index = AtvGetCurrentPresetListIndex();
			u4Freq = AtvGetCurrentFreq();//AtvGetCurrentPresetListOne(index);
		}
		else
		{
			// index = AtvGetCurrentDefaultPresetIndex();
			u4Freq = AtvGetCurrentFreq();//AtvGetCurrentDefaultPresetListOneFreq(index);
		}
		AtvSetCurrentFreq(u4Freq);
	}

	//RETAILMSG(TRUE, (TEXT("[SimSrch ] AtvInitStationData  AtvGetValidStationNum() %d  AtvGetCurrentFreq() %d AtvGetCurrentPresetListIndex() %d \r\n"), AtvGetValidStationNum(),AtvGetCurrentFreq(),AtvGetCurrentPresetListIndex()));
	LOGI("[SimSrch ] AtvInitStationData  AtvGetValidStationNum() %d  AtvGetCurrentFreq() %d AtvGetCurrentPresetListIndex() %d \r\n", AtvGetValidStationNum(),AtvGetCurrentFreq(),AtvGetCurrentPresetListIndex());
	return TRUE;
}

void AtvAndArmProtocol::SetATVStaionInfoToShareMemory()
{ 
	BYTE area=AREA_TV;
	UINT32 n = 0;
	UINT8 u1Num = 0;

	if(pShareFileInfo)
	{
		if(AtvGetValidStationNum())
		{
			// format
			pShareFileInfo->ATV_Station.uATVFormat = AtvGetTvDriverSystemFormat();//AtvGetValidStationNum();//m_rATVStation[area].uATVFormat;
			// cur fre
			pShareFileInfo->ATV_Station.uATVCurFreq =AtvGetCurrentFreq();
			// size
			u1Num = AtvGetValidStationNum();
			pShareFileInfo->ATV_Station.uATVStationSize = ( u1Num >= ATV_STATION_SIZE) ? ATV_STATION_SIZE:u1Num;
			// fre list
			for(n = 0 ; n < pShareFileInfo->ATV_Station.uATVStationSize ; n++)
			{
				pShareFileInfo->ATV_Station.nList[n] = m_rATVStation[area].nList[n];
				pShareFileInfo->ATV_Station.u1ListFormat[n] = m_rATVStation[area].u1ListFormat[n] ;
			}
			pShareFileInfo->ATV_Station.u1ATVIndex = m_rATVStation[area].u1ATVIndex;

		}
		else
		{
			// format
			pShareFileInfo->ATV_Station.uATVFormat = AtvGetTvDriverSystemFormat();//AtvGetValidStationNum();//m_rATVStation[area].uATVFormat;
			// cur fre
			pShareFileInfo->ATV_Station.uATVCurFreq = AtvGetCurrentFreq();//Atvgetcm_rATVStation[area].uATVCurFreq;
			// size
			u1Num = AtvGetDefaultStationNum();
			pShareFileInfo->ATV_Station.uATVStationSize = ( u1Num >= ATV_STATION_SIZE) ? ATV_STATION_SIZE:u1Num;
			// fre list
			for(n = 0 ; n < pShareFileInfo->ATV_Station.uATVStationSize ; n++)
			{
				pShareFileInfo->ATV_Station.nList[n] = m_rATVDefaultStation.nList[n];
				pShareFileInfo->ATV_Station.u1ListFormat[n] = m_rATVDefaultStation.u1ListFormat[n] ;
			}
			pShareFileInfo->ATV_Station.u1ATVIndex = m_rATVDefaultStation.u1ATVIndex;

		}

		//RETAILMSG(TRUE, (TEXT("[SimSrch ] SetATVStaionInfoToShareMemory  AtvGetValidStationNum() %d  AtvGetCurrentFreq() %d AtvGetCurrentPresetListIndex() %d \r\n"), AtvGetValidStationNum(),AtvGetCurrentFreq(),AtvGetCurrentPresetListIndex()));
		LOGI("[SimSrch ] SetATVStaionInfoToShareMemory  AtvGetValidStationNum() %d  AtvGetCurrentFreq() %d AtvGetCurrentPresetListIndex() %d \r\n", AtvGetValidStationNum(),AtvGetCurrentFreq(),AtvGetCurrentPresetListIndex());
	}


}

BOOL AtvAndArmProtocol::InitI2C()
{
#ifdef ATVTYPE_ADT1028
	if (m_pAtvDriver != NULL)
	{
		m_pAtvDriver->TvDriverInit();
	}
#else
	TvDriverInit();
#endif
	return TRUE;
}

BOOL AtvAndArmProtocol::CloseI2C()
{
#ifdef ATVTYPE_ADT1028
	if (m_pAtvDriver != NULL)
	{
		m_pAtvDriver->TvDriverDeInit();
	}
#else
	TvDriverDeInit();
#endif
	return TRUE;
}

// UINT8  AtvGetTvDriverSystemFormat();
void   AtvAndArmProtocol::AtvSetTvDriverSystemFormat(UINT8 u1Format)
{
	BYTE area = AREA_TV;
	m_rATVStation[area].uATVFormat = u1Format;
	m_AtvDriverStruct.format = u1Format;
}

UINT8  AtvAndArmProtocol::AtvGetTvDriverSystemFormat()
{
	return m_AtvDriverStruct.format;
}

BOOL AtvAndArmProtocol::SetTvDriverSystemFormat(int nFormat)
{
	BOOL bRet = FALSE;
	BYTE area = AREA_TV;

	if(nFormat < TV_TUNER_SYSTEM_END)
	{
		m_AtvDriverStruct.format=nFormat;
#ifdef ATVTYPE_ADT1028
		if (m_pAtvDriver != NULL)
		{
			m_pAtvDriver->TvDriverStateConfig_ex(nFormat);
		}
#else
		TvDriverStateConfig_ex(nFormat);
#endif
		bRet = TRUE;
	}
	return bRet;
}

BOOL AtvAndArmProtocol::AtvFreqIsValid( UINT32 nFre)
{
		return TRUE;
}

void AtvAndArmProtocol::SetTvDriverFreq(UINT32 nFreq, int nMode/* = 0*/)
{
#ifdef ATVTYPE_ADT1028
	if (m_pAtvDriver != NULL)
	{
		m_pAtvDriver->TvDriverSetFreq(nFreq, nMode);
	}
#else
	TvDriverSetFreq(nFreq);
#endif
}

int AtvAndArmProtocol::GetTvDriverLock()
{
	UINT8 by = 0;
#ifdef ATVTYPE_ADT1028
	if (m_pAtvDriver != NULL)
	{
		m_pAtvDriver->TvDriverStateChange_ex(&by);
	}
#else
	TvDriverStateChange_ex(&by);
#endif
	return by;
}

int AtvAndArmProtocol::GetTvDriverAfc(UINT8* uafc,UINT8* uvif)
{
	BYTE by = 0;
#ifdef ATVTYPE_ADT1028
	if (m_pAtvDriver != NULL)
	{
		m_pAtvDriver->TvDriverStateDetect_ex(&by, uafc, uvif);
	}
#else
	TvDriverStateDetect_ex(&by, uafc, uvif);
#endif
	return by;
}

void AtvAndArmProtocol::SetATVShareMemoryToArray()
{
	int area=AREA_TV;
	BYTE value = 0; 
#if 1
	//if(!AtvGetIsValidFormat())
	//	return;
	if(!pShareFileInfo)
		return ;
	for( int n = 0; n < ATV_STATION_SIZE; n++ )
	{
		m_rATVStation[area].nList[n] = pShareFileInfo->ATV_Station.nList[n];
		m_rATVDefaultStation.nList[n] = pShareFileInfo->ATV_Station.nList[n];
		m_rATVStation[area].u1ListFormat[n] = pShareFileInfo->ATV_Station.u1ListFormat[n];
		m_rATVDefaultStation.u1ListFormat[n] = pShareFileInfo->ATV_Station.u1ListFormat[n];
	}
	if(pShareFileInfo->ATV_Station.uATVFormat == 0)
	{
		AtvSetValidStationNum( 0 );
		if(pShareFileInfo->ATV_Station.u1ATVIndex < AtvGetDefaultStationNum())
		{
		}
		else
		{
			pShareFileInfo->ATV_Station.u1ATVIndex = 0;
		}
		m_rATVStation[area].uATVFormat = m_rATVDefaultStation.u1ListFormat[pShareFileInfo->ATV_Station.u1ATVIndex];
		AtvSetCurrentDefaultPresetIndex(pShareFileInfo->ATV_Station.u1ATVIndex);
		m_rATVStation[area].uATVCurFreq = pShareFileInfo->ATV_Station.uATVCurFreq;
		AtvSetCurrentFreq( pShareFileInfo->ATV_Station.uATVCurFreq);// AtvGetCurrentPresetListOne(value) );
	}
	else
	{
		AtvSetValidStationNum(pShareFileInfo->ATV_Station.uATVStationSize);
		if(AtvGetValidStationNum() > pShareFileInfo->ATV_Station.u1ATVIndex)
		{
		}
		else
		{
			pShareFileInfo->ATV_Station.u1ATVIndex = 0;
		}
		m_rATVStation[area].uATVFormat = m_rATVStation[area].u1ListFormat[pShareFileInfo->ATV_Station.u1ATVIndex];
		AtvSetCurrentPresetListIndex( pShareFileInfo->ATV_Station.u1ATVIndex );
		m_rATVStation[area].uATVCurFreq = pShareFileInfo->ATV_Station.uATVCurFreq;
		AtvSetCurrentFreq( pShareFileInfo->ATV_Station.uATVCurFreq);// AtvGetCurrentPresetListOne(value) );
	}
#else
	if(!AtvGetIsValidFormat())
		return;
	if(!pShareFileInfo)
		return ;
	for( int n = 0; n < ATV_STATION_SIZE; n++ )
	{
		m_rATVStation[area].nList[n] = pShareFileInfo->ATV_Station.nList[n];
		m_rATVStation[area].u1ListFormat[n] = pShareFileInfo->ATV_Station.u1ListFormat[n];
	}
	AtvSetValidStationNum(pShareFileInfo->ATV_Station.uATVStationSize);
	value = AtvGetValidStationNum();

	if( (value > 0) && (pShareFileInfo->ATV_Station.u1ATVIndex < value) )
	{
		AtvSetCurrentPresetListIndex( pShareFileInfo->ATV_Station.u1ATVIndex );
		value = AtvGetCurrentPresetListIndex();
		m_rATVStation->uATVCurFreq = pShareFileInfo->ATV_Station.uATVCurFreq;
		AtvSetCurrentFreq( pShareFileInfo->ATV_Station.uATVCurFreq);// AtvGetCurrentPresetListOne(value) );
	}
	else
	{
		AtvSetValidStationNum( 0 );
		AtvSetCurrentPresetListIndex( 0 );
		AtvSetCurrentFreq( AtvGetCurrentDefaultPresetListOneFreq(0) );
	}
#endif
}


void AtvAndArmProtocol::AtvSetDefaultStation()
{
	int nStart;
	BYTE area=AREA_TV;
	int nRecSys=TV_TUNER_SYSTEM_PAL_DK;

	if(!AtvGetIsValidFormat())
		m_rATVStation[area].uATVFormat = 0;
	switch(AtvGetRadioArea())
	{
	//case RADIOAREA_China://PAL
	//	nRecSys=TV_TUNER_SYSTEM_PAL_DK;
	//	AtvClearAmsPresetList();
	//	for(nStart = 0; nStart < ATV_STATION_SIZE; nStart++)
	//	{
	//		m_rATVStation[area].nList[nStart] = TvFreq_PAL[nStart];
	//		m_rATVDefaultStation.nList[nStart] = TvFreq_PAL[nStart]; 
	//		m_rATVStation[area].u1ListFormat[nStart] = nRecSys;
	//	};
	//	break;
	case ATVAREA_Europe:
		nRecSys=TV_TUNER_SYSTEM_PAL_BG;
		AtvClearAmsPresetList();
		for(nStart = 0; nStart < ATV_STATION_SIZE; nStart++)
		{
			m_rATVStation[area].nList[nStart] = TvFreq_PAL[nStart];
			m_rATVDefaultStation.nList[nStart] = TvFreq_PAL[nStart]; 
			m_rATVStation[area].u1ListFormat[nStart] = nRecSys;
			m_rATVDefaultStation.u1ListFormat[nStart] = nRecSys;
		}
		AtvSetDefaultStationNum(100);
		// AtvSetCurrentFreq();
		m_AtvDriverStruct.CurrentFreq = m_rATVDefaultStation.nList[0];
		m_rATVStation[area].uATVCurFreq = m_rATVDefaultStation.nList[0];
		AtvSetMaxFreq(_TV_FREQ_(870));
		AtvSetMinFreq(_TV_FREQ_(48.5));
		break;

	case ATVAREA_South_Africa:
		nRecSys=TV_TUNER_SYSTEM_PAL_I;
		AtvClearAmsPresetList();
		for(nStart = 0; nStart < ATV_STATION_SIZE; nStart++)
		{
			m_rATVStation[area].nList[nStart] = TvFreq_PAL[nStart];
			m_rATVDefaultStation.nList[nStart] = TvFreq_PAL[nStart]; 
			m_rATVStation[area].u1ListFormat[nStart] = nRecSys;
			m_rATVDefaultStation.u1ListFormat[nStart] = nRecSys;
		}
		AtvSetDefaultStationNum(100);
		// AtvSetCurrentFreq();
		m_AtvDriverStruct.CurrentFreq = m_rATVDefaultStation.nList[0];
		m_rATVStation[area].uATVCurFreq = m_rATVDefaultStation.nList[0];
		AtvSetMaxFreq(_TV_FREQ_(870));
		AtvSetMinFreq(_TV_FREQ_(48.5));
		break;

	case ATVAREA_China:
		nRecSys=TV_TUNER_SYSTEM_PAL_DK;
		AtvClearAmsPresetList();
		for(nStart = 0; nStart < ATV_STATION_SIZE; nStart++)
		{
			m_rATVStation[area].nList[nStart] = TvFreq_PAL[nStart];
			m_rATVDefaultStation.nList[nStart] = TvFreq_PAL[nStart]; 
			m_rATVStation[area].u1ListFormat[nStart] = nRecSys;
			m_rATVDefaultStation.u1ListFormat[nStart] = nRecSys;
		}
		AtvSetDefaultStationNum(100);
		m_rATVStation[area].uATVCurFreq = m_rATVDefaultStation.nList[0];
		m_AtvDriverStruct.CurrentFreq = m_rATVDefaultStation.nList[0];
		AtvSetMaxFreq(_TV_FREQ_(870));
		AtvSetMinFreq(_TV_FREQ_(48.5));
		break;

	case ATVAREA_Brazil:
		AtvClearAmsPresetList();
		nRecSys=TV_TUNER_SYSTEM_PAL_M;
		for(nStart = 0; nStart < ATV_STATION_SIZE; nStart++)
		{
			m_rATVStation[area].nList[nStart] = TvFreq_PAL_M[nStart];
			m_rATVDefaultStation.nList[nStart] = TvFreq_PAL_M[nStart]; 
			m_rATVStation[area].u1ListFormat[nStart] = nRecSys;
			m_rATVDefaultStation.u1ListFormat[nStart] = nRecSys;

		}
		AtvSetDefaultStationNum(68);
		m_rATVStation[area].uATVCurFreq = m_rATVDefaultStation.nList[0];
		m_AtvDriverStruct.CurrentFreq = m_rATVDefaultStation.nList[0];
		AtvSetMaxFreq(_TV_FREQ_(806));
		AtvSetMinFreq(_TV_FREQ_(54));
		break;
//	case ATVAREA_Southeast_Asia://NTSC
	case ATVAREA_Italy:
		AtvClearAmsPresetList();
		nRecSys=TV_TUNER_SYSTEM_NTSC_MN;
		for(nStart = 0; nStart < ATV_STATION_SIZE; nStart++)
		{
			m_rATVStation[area].nList[nStart] = vFreq_CCIR_NTSC_MN[nStart];
			m_rATVDefaultStation.nList[nStart] = vFreq_CCIR_NTSC_MN[nStart]; 
			m_rATVStation[area].u1ListFormat[nStart] = nRecSys;
			m_rATVDefaultStation.u1ListFormat[nStart] = nRecSys;
		}
		AtvSetDefaultStationNum(60);
		AtvSetMaxFreq(_TV_FREQ_(863));
		AtvSetMinFreq(_TV_FREQ_(47));
		m_rATVStation[area].uATVCurFreq = m_rATVDefaultStation.nList[0];
		m_AtvDriverStruct.CurrentFreq = m_rATVDefaultStation.nList[0];
		break;
	/*case RADIOAREA_America1:
	case RADIOAREA_America2:
	case RADIOAREA_America3:
	case RADIOAREA_America4:
	case RADIOAREA_Japan:
		AtvClearAmsPresetList();
		nRecSys=TV_TUNER_SYSTEM_NTSC_MN;
		for(nStart = 0; nStart < ATV_STATION_SIZE; nStart++)
		{
			m_rATVStation[area].nList[nStart] = vFreq_USJPN_NTSC_MN[nStart];
			m_rATVDefaultStation.nList[nStart] = vFreq_USJPN_NTSC_MN[nStart]; 
			m_rATVStation[area].u1ListFormat[nStart] = nRecSys;
		}
		m_rATVStation[area].uATVCurFreq = 0;
		break;*/
	case ATVAREA_EasternEurope://SECAM
	case ATVAREA_RUSSIA:
		AtvClearAmsPresetList();
		nRecSys=TV_TUNER_SYSTEM_SECAM_DK;
		for(nStart = 0; nStart < ATV_STATION_SIZE; nStart++)
		{
			m_rATVStation[area].nList[nStart] = vFreq_OIRT_SECAM_DK[nStart];
			m_rATVDefaultStation.nList[nStart] = vFreq_OIRT_SECAM_DK[nStart]; 
			m_rATVStation[area].u1ListFormat[nStart] = nRecSys;
			m_rATVDefaultStation.u1ListFormat[nStart] = nRecSys;
		}
		AtvSetDefaultStationNum(69);
		AtvSetMaxFreq(_TV_FREQ_(870));
		AtvSetMinFreq(_TV_FREQ_(48.5));
		m_rATVStation[area].uATVCurFreq = m_rATVDefaultStation.nList[0];
		m_AtvDriverStruct.CurrentFreq = m_rATVDefaultStation.nList[0];
		break;
	default:
		AtvSetDefaultStationNum(ATV_STATION_SIZE);
		break;
	}
	AtvSetValidStationNum(0);
	AtvSetTvDriverSystemFormat(nRecSys);
	// AtvSetDefaultTVAreaFormat(nRecSys);
}

BOOL AtvAndArmProtocol::SearchFreqStep(int nCurFreq,int nStep,BOOL bAddorDec)
{
	BOOL bRet = FALSE;
	BYTE area=AREA_TV;

	if(!AtvGetIsValidFormat())
		return bRet;
	if(AtvFreqIsValid(nCurFreq))
	{
		if(AtvGetState() == AtvStatus_AS || AtvGetState() == AtvStatus_SeekDown)
		{
			if(nCurFreq ==m_AtvDriverStruct.MaxFreq )
				return bRet;
		}
		else if(AtvGetState() == AtvStatus_SeekUp)
		{
			if(nCurFreq ==m_AtvDriverStruct.MinFreq )
				return bRet;
		}	
	}
	else
		return bRet;
	bRet=TRUE;
	int Freq = (int)(nStep)*m_AtvDriverStruct.StepFreq;
	if(bAddorDec)
		Freq *= m_AtvDriverStruct.FastStep;
	else
		Freq *= m_AtvDriverStruct.SlowStep;
	m_AtvDriverStruct.CurrentFreq += Freq/(1000/_TV_FREQ_MUL_);
	if(m_AtvDriverStruct.CurrentFreq < m_AtvDriverStruct.MinFreq)
		m_AtvDriverStruct.CurrentFreq = m_AtvDriverStruct.MinFreq;
	else if(m_AtvDriverStruct.CurrentFreq > m_AtvDriverStruct.MaxFreq)
		m_AtvDriverStruct.CurrentFreq = m_AtvDriverStruct.MaxFreq;
	return bRet;
}

BOOL AtvAndArmProtocol::GetTvSigalValid()
{
	if( (m_AtvDriverStruct.TvTuner.Window) && (m_AtvDriverStruct.TvTuner.Sync) )
		return TRUE;
	return FALSE;
}

extern "C" BOOL isAvinSignalReady();
BOOL AtvAndArmProtocol::QueryTVSignalState()
{
	/*
	HMEDIAGRAPH hVMediaGraph = GPlayerApp::GetInstance()->GetMediaGraph();
	BOOL fgIsAVSignalReady = FALSE;
	MediaGraph_VdoInGetIsSignalReady(hVMediaGraph,&fgIsAVSignalReady);
	return fgIsAVSignalReady;
	*/
	return isAvinSignalReady();
}

void AtvAndArmProtocol::TvTunerDetect()
{
	UINT8 uafcvwin = 0;
	UINT8 uvif = 0;

	memset(&m_AtvDriverStruct.TvTuner, 0, sizeof(m_AtvDriverStruct.TvTuner));
	m_AtvDriverStruct.TvTuner.Sync 	= FALSE;
	m_AtvDriverStruct.TvTuner.Sync  = QueryTVSignalState();
	m_AtvDriverStruct.TvTuner.Vif = GetTvDriverAfc(&uafcvwin,&uvif);
	if(uafcvwin == 1 && uvif == 1)
		m_AtvDriverStruct.TvTuner.Window = 1;
	else
	{
		m_AtvDriverStruct.TvTuner.Window = 0;
		m_AtvDriverStruct.TvTuner.Vif = 0;
	}
	m_AtvDriverStruct.TvTunerFound	= FALSE;
}

void AtvAndArmProtocol::FinishSimSrch(int ntvFun)
{
	BYTE area=AREA_TV;
	m_AtvDriverStruct.m_u4MemSearchStatusTimer = 0;
	m_AtvDriverStruct.m_u4MemSearchAbort = 0;
	m_AtvDriverStruct.m_u4MemSearchStatus = SET_NULL;

	UINT32 u4hadSerachIndex = AtvGetHadSearchedStationIndex();

	//RETAILMSG(TRUE, (TEXT("[SimSrch ] FinishSimSrch() ntvFun %d \r\n"), ntvFun));
	LOGI("[SimSrch ] FinishSimSrch() ntvFun %d \r\n", ntvFun);

	AtvSetState(AtvStatus_Normal);
	AtvSetCurrentDefaultPresetIndex(0);
	switch(ntvFun)
	{
		case AtvStatus_AS:
			if(AtvGetValidStationNum() <= 0)
			{
				AtvSetCurrentPresetListIndex(0);
				AtvSetHadSearchedStationIndex(0);
				AtvSetDefaultStation();
				AtvSetCurrentFreq(AtvGetCurrentDefaultPresetListOneFreq(0));
			}
			else
			{
				AtvSetHadSearchedStationIndex(0); // 
				AtvSetCurrentPresetListIndex(0); 
				AtvSetCurrentFreq(AtvGetCurrentPresetListOne(0));
			}
			break;
		case AtvStatus_PS:
			if(AtvGetValidStationNum() > 0)
			{
				AtvSetCurrentPresetListIndex(0);
				AtvSetCurrentFreq(AtvGetCurrentPresetListOne(0));
			}
			else
			{
				AtvSetCurrentDefaultPresetIndex(0);
				AtvSetCurrentFreq(AtvGetCurrentPresetListOne(0));
			}
			break;
		case AtvStatus_SeekUp:
		case AtvStatus_SeekDown:
			AtvSetCurrentDefaultPresetIndex(u4hadSerachIndex);
			AtvSetCurrentFreq(GetSearchPreFreq());		
			break;

		case AtvStatus_SearchStop://
			//AtvSetCurrentDefaultPresetIndex(u4hadSerachIndex);
			AtvSetCurrentFreq(AtvGetCurrentDefaultPresetListOneFreq(u4hadSerachIndex));
			//AtvSetCurrentFreq(GetSearchPreFreq());		
			break;
	}
}

void AtvAndArmProtocol::SetSearchPreFreq(UINT32 u4Fre)
{
	m_AtvDriverStruct.u4SearchPreFreq = u4Fre;
}

UINT32 AtvAndArmProtocol::GetSearchPreFreq()
{
	return m_AtvDriverStruct.u4SearchPreFreq;
}

void AtvAndArmProtocol::StopAtvSearchState(void)
{
	BYTE area=AREA_TV;
	m_AtvDriverStruct.m_u4MemSearchStatusTimer = 0;
	m_AtvDriverStruct.m_u4MemSearchAbort = 0;
	m_AtvDriverStruct.m_u4MemSearchStatus = SET_NULL;
	BYTE bAtvState = AtvGetState();

#ifdef ATVTYPE_ADT1028
	AtvSetState(AtvStatus_Normal);
#endif
	switch(bAtvState)
	{
	case AtvStatus_AS:
		if(AtvGetValidStationNum() <= 0)
		{
			AtvSetDefaultStation();
		}
		AtvSetCurrentDefaultPresetIndex(0);
/*	    if(AtvGetCurrentPresetListIndex() <= 0)
			GetATVDefaultStation();
		else
			AtvSetValidStationNum(AtvGetCurrentPresetListIndex());
*/
		AtvSetHadSearchedStationIndex(0);// 
		AtvSetCurrentPresetListIndex(0);
		AtvSetCurrentFreq(AtvGetCurrentPresetListOne(0));
		break;
	case AtvStatus_PS:
		AtvSetCurrentDefaultPresetIndex(0);
		AtvSetHadSearchedStationIndex(0);// 
		AtvSetCurrentPresetListIndex(0);
		AtvSetCurrentFreq(AtvGetCurrentPresetListOne(0));
		break;
	default:
		break;
	}
	
	AtvSetState(AtvStatus_Normal);
}

void SendAtvHomeMute(BYTE nOff)
{
#if 0//def ATVTYPE_ADT1028
	HWND hHomeWnd = FindWindow(HOME_TITLE_NAME, NULL);

	if(hHomeWnd == NULL)
		return;
	if( nOff == ATV_MUTE)
	{
		if(m_AtvDriverStruct.m_u1HomeMuteFlag != ATV_MUTE)
		{
			m_AtvDriverStruct.m_u1HomeMuteFlag = ATV_MUTE;
			PostMessage(hHomeWnd, MSG_FM_TO_HOME_MUTE, 0, MSG_LPARAM_TV);
		}
		m_AtvDriverStruct.m_u1MemMuteTimerPer20ms = 0;
	}
	else
	{
		m_AtvDriverStruct.m_u1HomeMuteFlag = ATV_UNMUTE;
		PostMessage(hHomeWnd, MSG_FM_TO_HOME_MUTE, 1, MSG_LPARAM_TV);
	}
#endif
}

BYTE GetAtvHomeMute(void)
{
	return m_AtvDriverStruct.m_u1HomeMuteFlag;
}

//static void CALLBACK SimSrch(HWND hwnd, UINT uMsg, UINT idEvent, DWORD dwTime)

int SimSrch(AtvAndArmProtocol * atvAndArmProtocol)
{
	UINT8 uAfc = 0;
	BYTE area=AREA_TV;

	UINT8 uafcwin = 0;
	UINT8 uviflev = 0;
	int nSearchMode=atvAndArmProtocol->AtvGetState();
	UINT32 u4Preset = 0;
	UINT32 u4Freq = 0;
    UINT8 value = 0;
	static int nLastSearchMode = nSearchMode;

#ifdef ATVTYPE_ADT1028
	if (nLastSearchMode != nSearchMode)
	{
		if (atvAndArmProtocol->m_pAtvDriver->GetTvCdt1028SeekState())
		{
			if (atvAndArmProtocol->m_pAtvDriver != NULL)
			{
				atvAndArmProtocol->m_pAtvDriver->SetTunerSeekFlag(TRUE);
			}
		}
		nLastSearchMode= nSearchMode;
	}
#endif

	if (AtvStatus_Normal == nSearchMode)
	{
#ifdef ATVTYPE_ADT1028
		if(atvAndArmProtocol->AtvGetMuteStatue() == ATV_MUTE_OFF)
		{
			if(m_AtvDriverStruct.m_u1MemMuteTimerPer20ms++ > 100)
			{
				m_AtvDriverStruct.m_u1MemMuteTimerPer20ms = 0;
				BOOL bSignal = atvAndArmProtocol->QueryTVSignalState();
				if (bSignal)
				{
					atvAndArmProtocol->AtvSendHomeMute(ATV_MUTE_ON);
				}
			}
		}
#else
		if( GetAtvHomeMute() == ATV_MUTE )
		{
			if(m_AtvDriverStruct.m_u1MemMuteTimerPer20ms++ > 100)
			{
				m_AtvDriverStruct.m_u1MemMuteTimerPer20ms = 0;
				SendAtvHomeMute(ATV_UNMUTE);
			}
		}
#endif
		return nSearchMode;
	}
	switch (m_AtvDriverStruct.m_u4MemSearchStatus)
	{
	case SET_PS_HAVE_SIGNAL:
		//RETAILMSG(TRUE, (TEXT("[SimSrch ] SET_PS_HAVE_SIGNAL  Freq:%d\r\n"), AtvAndArmProtocol::GetInstance()->AtvGetCurrentFreq(),AtvAndArmProtocol::GetInstance()->AtvGetCurrentPresetListIndex()));
		LOGI("[SimSrch ] SET_PS_HAVE_SIGNAL  Freq:%d index:%d \r\n", atvAndArmProtocol->AtvGetCurrentFreq(),atvAndArmProtocol->AtvGetCurrentPresetListIndex());
		switch(nSearchMode)
		{
		case  AtvStatus_PS:
			//RETAILMSG(TRUE, (TEXT("[SimSrch ] SET_PS_HAVE_SIGNAL AtvStatus_PS Freq:%d index %d \r\n"), atvAndArmProtocol->AtvGetCurrentFreq(),atvAndArmProtocol->AtvGetCurrentPresetListIndex()));
			LOGI("[SimSrch ] SET_PS_HAVE_SIGNAL AtvStatus_PS Freq:%d index %d \r\n", atvAndArmProtocol->AtvGetCurrentFreq(),atvAndArmProtocol->AtvGetCurrentPresetListIndex());
			if(m_AtvDriverStruct.m_u4MemPsStatusTimer++ >= 500)//PS��������ĳ��Ƶ�����źţ���5s������һ��Ƶ���ŵ�Ƶ��
			{
				m_AtvDriverStruct.m_u4MemPsStatusTimer = 0;
				if(atvAndArmProtocol->AtvGetValidStationNum() > 0)
				{
#ifdef ATV_debug
					 value = atvAndArmProtocol->AtvGetCurrentPresetListIndex();
					 value+=1;
					 atvAndArmProtocol->AtvSetCurrentPresetListIndex(value);

#endif
				}
				else
				{
#ifdef ATV_debug	
					 value = atvAndArmProtocol->AtvGetCurrentDefaultPresetIndex();
					 value+=1;
					 atvAndArmProtocol->AtvSetCurrentDefaultPresetIndex(value);
#endif
				}

				m_AtvDriverStruct.m_u4MemSearchStatus = SET_PS_COUNT_POS;
			}
			break;
		case AtvStatus_AS:
			u4Preset = atvAndArmProtocol->AtvGetValidStationNum();
			u4Freq = atvAndArmProtocol->AtvGetCurrentFreq();
			atvAndArmProtocol->AtvSetCurrentPresetListOneFreq(u4Preset, u4Freq);
			atvAndArmProtocol->AtvSetValidStationNum(u4Preset+1);
			atvAndArmProtocol->AtvSetCurrentPresetListIndex(u4Preset+1);
			m_AtvDriverStruct.m_u4MemSearchStatus = SET_NULL;
			break;
		case AtvStatus_SeekUp:
		case AtvStatus_SeekDown:
			atvAndArmProtocol->FinishSimSrch(AtvStatus_SearchStop);
			break;
		}
		break;
	case SET_FREQ_LOCK: // ��̨��ʶ
		{
			int uLock = atvAndArmProtocol->GetTvDriverLock();
			//RETAILMSG(TRUE, (TEXT("[SimSrch ] SET_FREQ_LOCK uLock %d ,nSearchMode %d Freq:%d atvAndArmProtocol->AtvGetHadSearchedStationIndex() %d \r\n"), uLock,nSearchMode,atvAndArmProtocol->AtvGetCurrentFreq(),atvAndArmProtocol->AtvGetHadSearchedStationIndex()));
			LOGI("[SimSrch ] SET_FREQ_LOCK uLock %d ,nSearchMode %d Freq:%d atvAndArmProtocol->AtvGetHadSearchedStationIndex() %d \r\n", uLock,nSearchMode,atvAndArmProtocol->AtvGetCurrentFreq(),atvAndArmProtocol->AtvGetHadSearchedStationIndex());
			if( uLock )
			{
				m_AtvDriverStruct.m_u4MemSearchStatusTimer = 0;
				m_AtvDriverStruct.m_u4MemGetFreLockFlag = 0;
				m_AtvDriverStruct.m_u4MemSearchStatus = SET_FREQ_AFC_VIF;
				break;
			}
			else
			{
				// ����5�Σ���Ȼ��ס̨����ѭ��
#ifdef ATVTYPE_ADT1028
				if( m_AtvDriverStruct.m_u4MemGetFreLockFlag++ > 20 )// 12 
#else
				if( m_AtvDriverStruct.m_u4MemGetFreLockFlag++ > 35 )// 12 
#endif
				{
					m_AtvDriverStruct.m_u4MemGetFreLockFlag = 0;
					atvAndArmProtocol->SearchNextChannelWhenCurrFreqNoSignal(nSearchMode);
				}
				else
					m_AtvDriverStruct.m_u4MemSearchStatus = SET_FREQ_LOCK;
			}
		}
		break;

	case SET_PS_COUNT_POS:
		LOGI("[SimSrch ] SET_PS_COUNT_POS");
		if(atvAndArmProtocol->AtvGetValidStationNum() > 0)
		{
			if(atvAndArmProtocol->AtvGetCurrentPresetListIndex() < atvAndArmProtocol->AtvGetValidStationNum() )
			{
				value = atvAndArmProtocol->AtvGetCurrentPresetListIndex();
				if(value >= atvAndArmProtocol->AtvGetValidStationNum())// zgy 0716
					atvAndArmProtocol->FinishSimSrch(nSearchMode);
				else
				{
					value+=1;
					atvAndArmProtocol->AtvSetCurrentPresetListIndex(value);
					m_AtvDriverStruct.m_u4MemSearchStatus = SET_NULL;
				}
			}
			else
				atvAndArmProtocol->FinishSimSrch(nSearchMode);

		}
		else
		{
			if(atvAndArmProtocol->AtvGetCurrentDefaultPresetIndex() < atvAndArmProtocol->AtvGetDefaultStationNum() )
			{
				value = atvAndArmProtocol->AtvGetCurrentDefaultPresetIndex();
				if(value >= atvAndArmProtocol->AtvGetDefaultStationNum()-1)
					atvAndArmProtocol->FinishSimSrch(nSearchMode);
				else
				{
					value+=1;
					atvAndArmProtocol->AtvSetCurrentDefaultPresetIndex(value);
					m_AtvDriverStruct.m_u4MemSearchStatus = SET_NULL;
				}

			}
			else
				atvAndArmProtocol->FinishSimSrch(nSearchMode);
		}
		break;

	case SET_NULL:// ����Ƶ��
		{
			LOGI("[SimSrch ] SET_NULL");
			switch(nSearchMode)
			{
			case AtvStatus_AS:
				if (atvAndArmProtocol->AtvGetHadSearchedStationIndex() <  atvAndArmProtocol->AtvGetDefaultStationNum())
				{
					UINT8 nFormat = atvAndArmProtocol->AtvGetTvDriverSystemFormat();
					value = atvAndArmProtocol->AtvGetHadSearchedStationIndex();
					// value++;
					if(value >= atvAndArmProtocol->AtvGetDefaultStationNum()){
						LOGI("[SimSrch ] AtvStatus_AS end 1");
						atvAndArmProtocol->FinishSimSrch(nSearchMode);
					}
					else
					{
						u4Freq = atvAndArmProtocol->AtvGetCurrentPresetListOne(value);
						LOGI("[SimSrch ]: %0.02f index:%d", (float)u4Freq/20, value);
						atvAndArmProtocol->AtvSetCurrentFreq(u4Freq);
						atvAndArmProtocol->AtvSetTvDriverSystemFormat(nFormat);
						value+=1;				
						atvAndArmProtocol->AtvSetHadSearchedStationIndex(value);
						m_AtvDriverStruct.m_u4MemSearchStatus = SET_FREQ_LOCK;
						m_AtvDriverStruct.m_nGetAFCVIF = 0;
					}
				}
				else{
					LOGI("[SimSrch ] AtvStatus_AS end 2");
					atvAndArmProtocol->FinishSimSrch(nSearchMode);
				}
				break;

			case AtvStatus_PS:
				if(atvAndArmProtocol->AtvGetValidStationNum() > 0)
				{
					//RETAILMSG(TRUE, (TEXT("[SimSrch ] AtvGetValidStationNum() > 0  SET_NULL AtvStatus_PS Freq:%d index %d total %d \r\n"), atvAndArmProtocol->AtvGetCurrentFreq(),atvAndArmProtocol->AtvGetCurrentPresetListIndex(),atvAndArmProtocol->AtvGetValidStationNum()));
					LOGI("[SimSrch ] AtvGetValidStationNum() > 0  SET_NULL AtvStatus_PS Freq:%d index %d total %d \r\n", atvAndArmProtocol->AtvGetCurrentFreq(),atvAndArmProtocol->AtvGetCurrentPresetListIndex(),atvAndArmProtocol->AtvGetValidStationNum());
					if(atvAndArmProtocol->AtvGetCurrentPresetListIndex() < atvAndArmProtocol->AtvGetValidStationNum() )
					{
						UINT8 nFormat = atvAndArmProtocol->AtvGetFormat();
						atvAndArmProtocol->AtvSetFormat(nFormat);
						atvAndArmProtocol->AtvSetTvDriverSystemFormat(nFormat);
						value = atvAndArmProtocol->AtvGetCurrentPresetListIndex();
						//value++;
						if(value >= atvAndArmProtocol->AtvGetValidStationNum())
							atvAndArmProtocol->FinishSimSrch(nSearchMode);
						else
						{
							u4Freq = atvAndArmProtocol->AtvGetCurrentPresetListOne(value);
							atvAndArmProtocol->AtvSetCurrentFreq(u4Freq);
							//value+=1;
							//atvAndArmProtocol->AtvSetCurrentPresetListIndex(value);
							m_AtvDriverStruct.m_u4MemSearchStatus = SET_FREQ_LOCK;
							m_AtvDriverStruct.m_nGetAFCVIF = 0;
						}
					}
					else
						atvAndArmProtocol->FinishSimSrch(nSearchMode);

				}
				else
				{

					// RETAILMSG(TRUE, (TEXT("[SimSrch ] AtvGetValidStationNum() > 0  SET_NULL AtvStatus_PS Freq:%d index %d \r\n"), atvAndArmProtocol->AtvGetCurrentFreq(),atvAndArmProtocol->AtvGetCurrentPresetListIndex()));
					LOGI("[SimSrch ] AtvGetValidStationNum() > 0  SET_NULL AtvStatus_PS Freq:%d index %d \r\n", atvAndArmProtocol->AtvGetCurrentFreq(),atvAndArmProtocol->AtvGetCurrentPresetListIndex());
					if(atvAndArmProtocol->AtvGetCurrentDefaultPresetIndex() < atvAndArmProtocol->AtvGetDefaultStationNum() )
					{
						UINT8 nFormat = atvAndArmProtocol->AtvGetFormat();
						atvAndArmProtocol->AtvSetFormat(nFormat);
						atvAndArmProtocol->AtvSetTvDriverSystemFormat(nFormat);
						value = atvAndArmProtocol->AtvGetCurrentDefaultPresetIndex();
						//value++;
						if(value >= atvAndArmProtocol->AtvGetDefaultStationNum()-1)
							atvAndArmProtocol->FinishSimSrch(nSearchMode);
						else
						{
		
							u4Freq = atvAndArmProtocol->AtvGetCurrentDefaultPresetListOneFreq(value);
							atvAndArmProtocol->AtvSetCurrentFreq(u4Freq);
							//value+=1;
							//atvAndArmProtocol->AtvSetCurrentDefaultPresetIndex(value);
							m_AtvDriverStruct.m_u4MemSearchStatus = SET_FREQ_LOCK;
							m_AtvDriverStruct.m_nGetAFCVIF = 0;
						}
					}
					else
						atvAndArmProtocol->FinishSimSrch(nSearchMode);
				}
				break;
			case AtvStatus_SeekUp:
			case AtvStatus_SeekDown:
				{
					if((atvAndArmProtocol->AtvGetHadSearchedStationIndex() < atvAndArmProtocol->AtvGetDefaultStationNum())
						&&(atvAndArmProtocol->AtvGetHadSearchedStationIndex() >= 0))
					{

						UINT8 nFormat = atvAndArmProtocol->AtvGetTvDriverSystemFormat();
						atvAndArmProtocol->AtvSetTvDriverSystemFormat(nFormat);

						u4Freq = atvAndArmProtocol->AtvGetCurrentDefaultPresetListOneFreq(atvAndArmProtocol->AtvGetHadSearchedStationIndex());
						atvAndArmProtocol->AtvSetCurrentFreq(u4Freq);
						if (u4Freq != atvAndArmProtocol->GetSearchPreFreq())
						{
							m_AtvDriverStruct.m_u4MemSearchStatus = SET_FREQ_LOCK;
							m_AtvDriverStruct.m_nGetAFCVIF = 0;
						}
						else
							atvAndArmProtocol->FinishSimSrch(nSearchMode);
					}
					else
						atvAndArmProtocol->FinishSimSrch(nSearchMode);
				}
				break;
			}
		}
		break;
	case SET_FREQ_AFC_VIF: // ��ȡvif afc ��ʶ
		{
			if(m_AtvDriverStruct.m_nGetAFCVIF < 5)
			{
				m_AtvDriverStruct.m_nGetAFCVIF += 1;
				break;
			}
			m_AtvDriverStruct.m_nGetAFCVIF = 0;
			atvAndArmProtocol->TvTunerDetect();
			UINT16 LastFreq;
			LastFreq = m_AtvDriverStruct.TvTunerLastFreq;
			m_AtvDriverStruct.TvTunerLastFreq = GET_TV_DRIVER_FREQ;
			if(m_AtvDriverStruct.TvTuner.Sync || m_AtvDriverStruct.TvTuner.Window)
			{
				// ������Ч��̨
				//  if ( m_AtvDriverStruct.TvTuner.Sync && m_AtvDriverStruct.TvTuner.Window )
				{
					m_AtvDriverStruct.m_u4MemSearchStatusTimer = 0;
					m_AtvDriverStruct.m_u4MemSearchStatus = SET_PS_SYNC_OR_WINDOW_SIGNAL;
				}
			}
			else // û��tvģ���ʶ�͵�����Ƶ�ź�
				atvAndArmProtocol->SearchNextChannelWhenCurrFreqNoSignal(nSearchMode);
		}
		break;
		case SET_PS_SYNC_OR_WINDOW_SIGNAL:
			if(m_AtvDriverStruct.m_u4MemSearchStatusTimer++ < 150)
			{
				BOOL bSignal = atvAndArmProtocol->QueryTVSignalState();
				//RETAILMSG(TRUE, (TEXT("[SimSrch ] SET_PS_SYNC_OR_WINDOW_SIGNAL bSignal%d Freq:%d\r\n"), bSignal,atvAndArmProtocol->AtvGetCurrentFreq(),atvAndArmProtocol->AtvGetCurrentPresetListIndex()));
				LOGI("[SimSrch ] SET_PS_SYNC_OR_WINDOW_SIGNAL bSignal%d Freq:%d index:%d\r\n", bSignal,atvAndArmProtocol->AtvGetCurrentFreq(),atvAndArmProtocol->AtvGetCurrentPresetListIndex());
				if(bSignal)
				{
#ifdef ATVTYPE_ADT1028
					switch(nSearchMode)
					{
					case AtvStatus_PS:
						{
							atvAndArmProtocol->AtvSetCurrentFreq(atvAndArmProtocol->AtvGetCurrentFreq(), 1);
						}
						break;
					}
#endif
					m_AtvDriverStruct.m_u4MemSearchStatusTimer = 0;
					m_AtvDriverStruct.m_u4MemSearchStatus  = SET_PS_HAVE_SIGNAL;
				}
			}
			else
				atvAndArmProtocol->SearchNextChannelWhenCurrFreqNoSignal(nSearchMode);
		break;
	default:
		break;

	}

	return nSearchMode;
}

void AtvAndArmProtocol::SearchNextChannelWhenCurrFreqNoSignal(UINT32 nSearchMode)
{
	UINT8 value = 0;

	switch(nSearchMode)
	{
	case AtvStatus_AS:
		m_AtvDriverStruct.m_u4MemSearchStatus = SET_NULL;
		break;
	case AtvStatus_PS:
		//if(AtvGetCurrentPresetListIndex() < AtvGetValidStationNum() )
		//	AtvSetCurrentPresetListIndex(AtvGetCurrentPresetListIndex() +1);
		if(AtvGetValidStationNum() > 0)
		{
#ifdef ATV_debug
			 value = AtvGetCurrentPresetListIndex();
			 value+=1;
			 AtvSetCurrentPresetListIndex(value);
#endif
		}
		else
		{
#ifdef ATV_debug
			 value = atvAndArmProtocol->AtvGetCurrentDefaultPresetIndex();
			 value+=1;
			 AtvSetCurrentDefaultPresetIndex(value);
#endif
		}
		m_AtvDriverStruct.m_u4MemSearchStatus = SET_PS_COUNT_POS;
		break;
	case AtvStatus_SeekUp:
		if(AtvGetHadSearchedStationIndex() >0 && AtvGetHadSearchedStationIndex() < AtvGetDefaultStationNum()  )
			AtvSetHadSearchedStationIndex(AtvGetHadSearchedStationIndex() -1);
		else
			AtvSetHadSearchedStationIndex(AtvGetDefaultStationNum()-1);
		m_AtvDriverStruct.m_u4MemSearchStatus = SET_NULL;
		break;
	case AtvStatus_SeekDown:
		if(AtvGetHadSearchedStationIndex() >=0 && AtvGetHadSearchedStationIndex() < AtvGetDefaultStationNum()-1  )
			AtvSetHadSearchedStationIndex(AtvGetHadSearchedStationIndex() +1);
		else
			AtvSetHadSearchedStationIndex(0);
		m_AtvDriverStruct.m_u4MemSearchStatus = SET_NULL;
		break;
	}
}

void AtvAndArmProtocol::StartATVJumpFreqMuteTime( )
{
	UINT32 u4Interval = 20;

	//m_AtvDriverStruct.m_timer = SetTimer(NULL, 0, u4Interval, SimSrch);
}

BOOL AtvAndArmProtocol::AtvGetIsValidFormat()
{
	BYTE area = AREA_TV;

	if( m_rATVStation[area].uATVFormat >= TV_TUNER_SYSTEM_END || m_rATVStation[area].uATVFormat < TV_TUNER_SYSTEM_PAL_DK)
		return FALSE;
	return TRUE;
}

void AtvAndArmProtocol::AtvSetCurrentPresetListIndex(UINT32 u4Index)
{
	if(u4Index < 0 || u4Index > AtvGetValidStationNum())
		return ;
	m_rATVStation[AREA_TV].u1ATVIndex = u4Index;
}

UINT32 AtvAndArmProtocol::AtvGetCurrentPresetListIndex( )
{
	return m_rATVStation[AREA_TV].u1ATVIndex;
}

UINT32 AtvAndArmProtocol::AtvGetRadioArea()
{
	return m_AtvDriverStruct.m_nATvArea;
}

void   AtvAndArmProtocol::AtvSetRadioArea(UINT32 u4RadioArea)
{
	m_AtvDriverStruct.m_nATvArea = u4RadioArea;
}

void AtvAndArmProtocol::AtvSetCurrentPresetListOneFreq(BYTE u1Num, UINT32 u2Freq)
{
	BYTE area = AREA_TV;
    
	if( u1Num < (sizeof(m_rATVStation[area].nList) / sizeof (UINT32)) )
		m_rATVStation[area].nList[u1Num] = u2Freq;
}

UINT32 AtvAndArmProtocol::AtvGetCurrentDefaultPresetListOneFreq(  BYTE u1Num)
{
	UINT8 u1Max = AtvGetDefaultStationNum();

	if(u1Num >= u1Max)
		u1Num = 0;
	UINT8 u1BackIndex = AtvGetCurrentDefaultPresetIndex();

	if(AtvGetRadioArea() == ATVAREA_RUSSIA)
	{
		if(u1BackIndex == 11 && u1Num > 11)
			u1Num = 20;
		else if(u1BackIndex == 20 && u1Num < 20)
			u1Num = 11;

	}
	return m_rATVDefaultStation.nList[u1Num];
}

UINT32 AtvAndArmProtocol::AtvGetCurrentDefaultPresetIndex(void)
{
	return m_rATVDefaultStation.u1ATVIndex;
}

void AtvAndArmProtocol::AtvSetCurrentDefaultPresetIndex(  BYTE u1Num)
{
	UINT8 u1Max = AtvGetDefaultStationNum();

	if(u1Num >= u1Max)
		u1Num = 0;

	UINT8 u1BackIndex = AtvGetCurrentDefaultPresetIndex();

	if(AtvGetRadioArea() == ATVAREA_RUSSIA)
	{
		if(u1BackIndex == 11 && u1Num > 11)
			u1Num = 20;
		else if(u1BackIndex == 20 && u1Num < 20)
			u1Num = 11;

	}
	 m_rATVDefaultStation.u1ATVIndex = u1Num;
}

UINT32 AtvAndArmProtocol::AtvGetCurrentPresetListOne(  BYTE u1Num)
{
	UINT8 u1Max = AtvGetDefaultStationNum();

	if(u1Num >= u1Max)
		u1Num = 0;
	return m_rATVStation[AREA_TV].nList[u1Num];
}

void AtvAndArmProtocol::AtvSetCurChannelFreq(UINT32 u2Freq)
{
	BYTE area = AREA_TV;
	if(AtvFreqIsValid( u2Freq))
		m_rATVStation[area].nList[m_rATVStation[area].uATVCurFreq] = u2Freq;
}

BYTE AtvAndArmProtocol::AtvGetValidStationNum( )
{
	return m_rATVStation[AREA_TV].uATVStationSize;
}

UINT32 AtvAndArmProtocol::AtvGetDefaultStationNum()
{
	return m_rATVDefaultStation.uATVStationSize;
}

void AtvAndArmProtocol::AtvSetDefaultStationNum(UINT8 u1Num)
{
	if(u1Num > ATV_MAX_FRE_TABLE_SIZE)
		u1Num = ATV_MAX_FRE_TABLE_SIZE;
	m_rATVDefaultStation.uATVStationSize = u1Num;
}

void AtvAndArmProtocol::AtvSetValidStationNum( BYTE value)
{
	UINT8 u1Max = AtvGetDefaultStationNum();

	if(value > u1Max)
		value = 0;
	m_rATVStation[AREA_TV].uATVStationSize =value;
}

BYTE AtvAndArmProtocol::AtvClearAmsPresetList( )
{
	BYTE area = AREA_TV;
	memset(m_rATVStation[area].nList, 0, sizeof(m_rATVStation[area].nList));
	AtvSetValidStationNum( 0 );
	return 1;
}

void AtvAndArmProtocol::AtvInitInfo()
{
	m_AtvDriverStruct.format=TV_TUNER_SYSTEM_PAL_DK;
	m_AtvDriverStruct.TvFlag = 0;
	m_AtvDriverStruct.m_u4MemPsStatusTimer = 0;
	m_AtvDriverStruct.TvTunerFoundFlag=FALSE;
	m_AtvDriverStruct.TvTunerFound=FALSE;
	m_AtvDriverStruct.m_u4MemSearchStatusTimer = 0;
	m_AtvDriverStruct.m_u4MemGetFreLockFlag = 0;
	m_AtvDriverStruct.m_u4MemSearchAbort = 0;
	m_AtvDriverStruct.m_u4MemSearchStatus = 0;
	m_AtvDriverStruct.m_nGetAFCVIF = 0;
	m_AtvDriverStruct.HadSeachstationIndex = 0;
	m_AtvDriverStruct.SeakUporDownIndex = 0;
	m_AtvDriverStruct.u4SearchPreFreq=0;
}

void AtvAndArmProtocol::array2Config(unsigned int *configData, unsigned int dataLen){
	if(configData !=NULL && dataLen == (sizeof(SHARE_MEMORY_FileInfo_T)/4)){
		int n = 0, i=0;
		pShareFileInfo->u1AtvRegion = configData[i++];
		pShareFileInfo->i4RadioRegion = configData[i++];
		pShareFileInfo->ATV_Station.uATVStationSize = configData[i++];
		pShareFileInfo->ATV_Station.uATVFormat = configData[i++];
		pShareFileInfo->ATV_Station.uATVCurFreq = configData[i++];
		pShareFileInfo->ATV_Station.u1ATVIndex = configData[i++];
		for(n=0;i<dataLen, n<ATV_STATION_SIZE; i++,n++){
			pShareFileInfo->ATV_Station.nList[n] =configData[i];
		}
		for(n=0;i<dataLen, n<ATV_STATION_SIZE;i++,n++){
			pShareFileInfo->ATV_Station.u1ListFormat[n] =configData[i];
		}
	}
}
void AtvAndArmProtocol::config2Array(unsigned int *configData, unsigned int dataLen){
	//update config first.
	//SetATVStaionInfoToShareMemory();
	//config to array.
	if(configData !=NULL && dataLen == (sizeof(SHARE_MEMORY_FileInfo_T)/4)){
		int n = 0, i=0;
		configData[i++] =pShareFileInfo->u1AtvRegion;
		configData[i++]=pShareFileInfo->i4RadioRegion;
		configData[i++]=pShareFileInfo->ATV_Station.uATVStationSize;
		configData[i++]=pShareFileInfo->ATV_Station.uATVFormat;
		configData[i++]=pShareFileInfo->ATV_Station.uATVCurFreq;
		configData[i++]=pShareFileInfo->ATV_Station.u1ATVIndex;
		for(n=0;i<dataLen, n<ATV_STATION_SIZE; i++,n++){
			configData[i] = pShareFileInfo->ATV_Station.nList[n];
		}
		for(n=0;i<dataLen, n<ATV_STATION_SIZE;i++,n++){
			configData[i] = pShareFileInfo->ATV_Station.u1ListFormat[n];
		}
	}
}
void AtvAndArmProtocol::AtvProtocalInit(int tvRegion, unsigned int *configData, unsigned int dataLen)
{
	BYTE area=AREA_TV;
	//pShareFileInfo = (SHARE_MEMORY_FileInfo_T *)GPlayerApp::GetInstance()->GetShareMemDataPoint();
	//init config data
	pShareFileInfo = new SHARE_MEMORY_FileInfo_T;
	memset(pShareFileInfo, 0, sizeof(SHARE_MEMORY_FileInfo_T));
	array2Config(configData, dataLen);
	pShareFileInfo->u1AtvRegion = tvRegion;
	//
	if(!pShareFileInfo)
		return;
	SendAtvHomeMute(ATV_MUTE);
	AtvInitInfo();
	AtvInitStationData();
}

void AtvAndArmProtocol::AtvProtocalDeinit( void )
{
	if (!m_uIsAtvAreaChanged)
	{
		SetATVStaionInfoToShareMemory();
	}
}

void AtvAndArmProtocol::AtvRadioRegionChange()
{
	BYTE area=AREA_TV;
	SHARE_MEMORY_FileInfo_T* pAtvShareFileInfo=NULL;// = (SHARE_MEMORY_FileInfo_T *)GPlayerApp::GetInstance()->GetShareMemDataPoint();
	if(pAtvShareFileInfo)
	{
		if(pAtvShareFileInfo->i4RadioRegion != AtvGetRadioArea())
		{
			AtvGetTunerConfigByRadioArea();
			AtvSetRadioArea(pAtvShareFileInfo->i4RadioRegion);
			AtvSetDefaultStation();
			AtvSetValidStationNum(0);
			AtvSetFormat(AtvGetFormat());
			AtvSetCurrentPresetListIndex(0);
			AtvSetCurrentFreq(AtvGetCurrentPresetListOne(0));
		}
	}
}

void   AtvAndArmProtocol::AtvSetMinFreq(UINT32 u4Freq)
{
	 m_AtvDriverStruct.MinFreq = u4Freq;
}


UINT32 AtvAndArmProtocol::AtvGetMinFreq(void)
{
    return m_AtvDriverStruct.MinFreq;
}

UINT32 AtvAndArmProtocol::AtvGetMaxFreq(void)
{
    return m_AtvDriverStruct.MaxFreq;
}

void   AtvAndArmProtocol::AtvSetMaxFreq(UINT32 u4Freq)
{
	m_AtvDriverStruct.MaxFreq = u4Freq;
}

UINT32 AtvAndArmProtocol::AtvGetStep(void)
{
    return m_AtvDriverStruct.StepFreq;
}

UINT32 AtvAndArmProtocol::AtvGetFastStep(void)
{
	return m_AtvDriverStruct.FastStep;
}

UINT32 AtvAndArmProtocol::AtvGetSlowStep(void)
{
	return m_AtvDriverStruct.SlowStep;
}

UINT32 AtvAndArmProtocol::AtvGetCurrentFreq(void)
{
	return m_AtvDriverStruct.CurrentFreq;
}

UINT32 AtvAndArmProtocol::AtvGetHadSearchedStationIndex()
{
	return m_AtvDriverStruct.HadSeachstationIndex;
}

void AtvAndArmProtocol::AtvSetHadSearchedStationIndex(UINT32 u4StationIndex)
{
	UINT8 u1BackIndex = AtvGetHadSearchedStationIndex();

	if(AtvGetRadioArea() == ATVAREA_RUSSIA)
	{
		if(u1BackIndex == 11 && u4StationIndex > 11)
			u4StationIndex = 20;
		else if(u1BackIndex == 20 && u4StationIndex < 20)
			u4StationIndex = 11;

	}

	m_AtvDriverStruct.HadSeachstationIndex = u4StationIndex;
}

void AtvAndArmProtocol::AtvSetSeekUpOrDownIndex(UINT32 u4SeekuporDown)
{
	m_AtvDriverStruct.SeakUporDownIndex = u4SeekuporDown;
}

UINT32 AtvAndArmProtocol::AtvGetSeekUpOrDownIndex()
{
	return m_AtvDriverStruct.SeakUporDownIndex;
}

UINT32 AtvAndArmProtocol::AtvGetCurrFreqInDefaultListPos(UINT32 u4Freq)
{
	UINT32 u4Pos = 0;
	UINT32 i = 0;

	for (; i < m_rATVDefaultStation.uATVStationSize; i++)
	{
		if(m_rATVDefaultStation.nList[i] == u4Freq)
			return i;
	}
	return 0;
}

void AtvAndArmProtocol::AtvSetCurrentFreq(UINT32 u1Freq, int nMode/* = 0*/)
{
	BYTE area = AREA_TV;

	if(!AtvFreqIsValid( u1Freq))
		return;

	LOGI("AtvSetCurrentFreq: %0.02f nMode:%d", (float)u1Freq/20, nMode);

	m_AtvDriverStruct.CurrentFreq = u1Freq;
	SetTvDriverFreq(u1Freq, nMode);
}

UINT32 AtvAndArmProtocol::AtvGetKeyValue()
{
	return m_AtvDriverStruct.u4KeyValue;
}

void AtvAndArmProtocol::AtvSetKeyValue(UINT32 u4Key)
{
	m_AtvDriverStruct.u4KeyValue = u4Key;
}

UINT32 AtvAndArmProtocol::AtvGetKeyState()
{
	return m_AtvDriverStruct.u4KeyHandleState;
}

void AtvAndArmProtocol::AtvSetKeyState(UINT32 u4Key)
{
  m_AtvDriverStruct.u4KeyHandleState = u4Key;
}

BYTE AtvAndArmProtocol::AtvGetState(void)
{
	if ( m_AtvDriverStruct.TvFlag & (1<< AtvStatus_SeekUp) )
		return AtvStatus_SeekUp;
	else if ( m_AtvDriverStruct.TvFlag & (1<< AtvStatus_SeekDown) )
		return AtvStatus_SeekDown;
	else if ( m_AtvDriverStruct.TvFlag & (1<< AtvStatus_PS) )
		return AtvStatus_PS;
	else if ( m_AtvDriverStruct.TvFlag & (1<< AtvStatus_AS) )
		return AtvStatus_AS;
	return  AtvStatus_Normal;
}

void AtvAndArmProtocol::AtvSetState(BYTE u1Status)
{
#ifdef ATVTYPE_ADT1028
	SET_TV_DRIVER_SEARCHMODE(u1Status);
#endif
	m_AtvDriverStruct.TvFlag = 0;
	if( u1Status  == AtvStatus_SeekUp)
		m_AtvDriverStruct.TvFlag |= (1<< AtvStatus_SeekUp);
	else if (u1Status == AtvStatus_SeekDown)
		m_AtvDriverStruct.TvFlag |= (1<< AtvStatus_SeekDown);
	else if (u1Status == AtvStatus_PS)
		m_AtvDriverStruct.TvFlag |= (1<< AtvStatus_PS);
	else if (u1Status == AtvStatus_AS)
		m_AtvDriverStruct.TvFlag |= (1<< AtvStatus_AS);
	else if( u1Status == AtvStatus_PAS)
		m_AtvDriverStruct.TvFlag |= (1<< AtvStatus_PAS);
	else if (u1Status == AtvStatus_Normal)
		m_AtvDriverStruct.TvFlag = 0;
}

BYTE AtvAndArmProtocol::AtvSearchJumpOneFreq(WORD nFreq)
{
	if( (nFreq > AtvGetMaxFreq()) || (nFreq < AtvGetMinFreq()) )
		return 0;
	AtvSetCurrentFreq(nFreq);
	return 1;
}

BYTE AtvAndArmProtocol::AtvGetCurrentPresetLoad( )
{
	BYTE area = AREA_TV;
	return m_rATVStation[area].uATVCurFreq;
}

void AtvAndArmProtocol::AtvFreqUpOrDown(BYTE up)
{
	WORD nFreq = 0;

	if(up)
	{
		m_AtvDriverStruct.TvTunerLastVif = 8;
		nFreq = AtvGetCurrentFreq();
		nFreq -= AtvGetSlowStep();
	}
	else
	{
		m_AtvDriverStruct.TvTunerLastVif = 7;

		nFreq = AtvGetCurrentFreq();
		nFreq += AtvGetSlowStep();
	}
	if(nFreq > AtvGetMaxFreq())
		nFreq = AtvGetMaxFreq();
	else if(nFreq < AtvGetMinFreq())
		nFreq = AtvGetMinFreq();
	AtvSetCurrentFreq(nFreq);
//	AtvSetCurChannelFreq(nFreq);
	//LOGI("Current Freq: %0.02f", (float)nFreq/20);
	return;
}

void AtvAndArmProtocol::AtvSearchSeekUpDown(BYTE up)
{
	WORD nFreq = 0;

	if(up)
		AtvSetState(AtvStatus_SeekUp);
	else
		AtvSetState(AtvStatus_SeekDown);
	SetSearchPreFreq(AtvGetCurrentFreq());
	UINT32 u4Pos = AtvGetCurrFreqInDefaultListPos(AtvGetCurrentFreq());
	if(!up)
	{
		if(u4Pos == (AtvGetDefaultStationNum()-1))
			u4Pos = 0;
		else
			u4Pos+=1;
	}
	else
	{
		if(u4Pos == 0)
			u4Pos = AtvGetDefaultStationNum()-1;
		else
			u4Pos -=1;
	}
	AtvSetHadSearchedStationIndex(u4Pos);
	return;
}

void AtvAndArmProtocol::AtvSearchPS(void)
{
	DWORD nFreq = 0;
	BYTE area = AREA_TV;

	AtvSetCurrentDefaultPresetIndex(0);

	if(AtvGetValidStationNum())
	{
		AtvSetState(AtvStatus_PS);
		m_rATVStation[area].uATVCurFreq = 0;
		AtvSetCurrentPresetListIndex(0);
		AtvSetCurrentFreq(AtvGetCurrentPresetListOne( 0));
	}
	else
	{
		AtvSetState(AtvStatus_PS);
		AtvSetCurrentPresetListIndex(0);
		m_rATVStation[area].uATVCurFreq = 0;
		AtvSetCurrentDefaultPresetIndex(0);
		AtvSetHadSearchedStationIndex(0);
		AtvSetCurrentFreq(AtvGetCurrentDefaultPresetListOneFreq( 0));
	}
}

void AtvAndArmProtocol::AtvSearchAMS(void)
{
	BYTE area = AREA_TV;

	AtvSetCurrentPresetListIndex(0);
	AtvSetHadSearchedStationIndex(0);
	AtvSetDefaultStation();
	AtvSetState(AtvStatus_AS);
	m_rATVStation[area].uATVCurFreq = AtvGetCurrentPresetListOne(0);
	AtvSetCurrentFreq(AtvGetCurrentPresetListOne(0));
	AtvSetValidStationNum(0);
}

void AtvAndArmProtocol::AtvSearchChannelUpOrDown(BYTE up)
{
	BYTE   area = AREA_TV;	
	UINT32 nCHNum= AtvGetCurrentPresetListIndex();
	UINT32 u4ValidNum = AtvGetValidStationNum();
	UINT32 nFreq;

	if(u4ValidNum >= 1)
	{
		if(up == 0) // add
		{
			nCHNum++;
			if ( nCHNum >= u4ValidNum)
				nCHNum = 0;
		}
		else // sub 
		{
			if (nCHNum)
				nCHNum--;
			else
				nCHNum = u4ValidNum - 1;
		}
		nFreq=AtvGetCurrentPresetListOne(nCHNum);
		AtvSetCurrentPresetListIndex(nCHNum);
		AtvSetCurrentFreq(nFreq);
	}
	else if (u4ValidNum == 0)
	{
		u4ValidNum = AtvGetDefaultStationNum();
		nCHNum = AtvGetCurrentDefaultPresetIndex();
		if(up == 0) // add
		{
			nCHNum++;
			if ( nCHNum >= u4ValidNum)
				nCHNum = 0;
		}
		else // sub 
		{
			if (nCHNum)
				nCHNum--;
			else
				nCHNum = u4ValidNum - 1;
		}
		nFreq=AtvGetCurrentDefaultPresetListOneFreq(nCHNum);
		AtvSetCurrentDefaultPresetIndex(nCHNum);
		AtvSetCurrentFreq(nFreq);		
	}
	LOGI("AtvSearchChannelUpOrDown freq:%d", nFreq);
}

void AtvAndArmProtocol::AtvDigitalKeyJumpOneFreqByIndex(UINT32 u4Index)
{
	if(AtvGetRadioArea() == ATVAREA_Brazil || AtvGetRadioArea() == ATVAREA_RUSSIA)
		u4Index -=1;

	if(u4Index < 0 )
	{
		return;
	}

	UINT32 u4ValidNum = AtvGetValidStationNum();
	UINT32 nFreq;

	if(u4ValidNum > 0)
	{
		if(u4Index >= u4ValidNum)
			return;

		nFreq=AtvGetCurrentPresetListOne(u4Index);
		AtvSetCurrentPresetListIndex(u4Index);
		AtvSetCurrentFreq(nFreq);

	}
	else
	{
		u4ValidNum = AtvGetDefaultStationNum();
		if(AtvGetRadioArea() == ATVAREA_Brazil)
		{
			u4Index -=1;
		}
		if(u4Index >= u4ValidNum)
			return;
		if(AtvGetRadioArea() == ATVAREA_RUSSIA )
		{
			if(u4Index <= 19 && u4Index >= 12)
				return ;
		}
		if( u4Index < 0)
			return;

		nFreq=AtvGetCurrentDefaultPresetListOneFreq(u4Index);
		AtvSetCurrentDefaultPresetIndex(u4Index);
		AtvSetCurrentFreq(nFreq);		

	}
}

BOOL AtvAndArmProtocol::AtvOnNotStopStatus()
{
	BOOL bRet = FALSE;

	switch (AtvGetState())
	{
	case AtvStatus_SeekUp:
		bRet = TRUE;
		break;
	case AtvStatus_SeekDown:
		bRet = TRUE;
		break;
	case AtvStatus_PS:
		bRet = TRUE;
		break;
	case AtvStatus_AS:
		bRet = TRUE;
		break;
	default:
		bRet = FALSE;
		break;
	}
	return bRet;
};

void AtvAndArmProtocol::AtvSwapSeekUpOrDownIndexToSearchIndex(UINT8 cmd)
{
	BYTE value = 0;

	switch(AtvGetState())
	{
	case AtvStatus_SeekUp:
	case AtvStatus_SeekDown:
		AtvSetCurrentPresetListIndex(AtvGetSeekUpOrDownIndex());
		AtvSetSeekUpOrDownIndex(0);
		value = AtvGetCurrentPresetListIndex();
		AtvSetCurrentFreq( AtvGetCurrentPresetListOne(value) );
		break;
	default:
		break;
	}
}

// view output interface cmd to Middle Ware
int AtvAndArmProtocol::AtvSendMidCmd(UINT8 cmd,UINT32 u4KeyValue)
{
	if(AtvOnNotStopStatus())
	{
		StopAtvSearchState();
		return 0;
	}
	switch(cmd)
	{
	case ATV_CMD_FREQ_UP:
		SendAtvHomeMute(ATV_MUTE);
		AtvFreqUpOrDown(1);
		break;
	case ATV_CMD_FREQ_DOWN:
		SendAtvHomeMute(ATV_MUTE);
		AtvFreqUpOrDown(0);
		break;
	case ATV_CMD_SEARCHUP:
		SendAtvHomeMute(ATV_MUTE);
		AtvSearchSeekUpDown(1);
		break;
	case ATV_CMD_SEARCHDOWN:
		SendAtvHomeMute(ATV_MUTE);
		AtvSearchSeekUpDown(0);
		break;
	case ATV_CMD_AMS:
		SendAtvHomeMute(ATV_MUTE);
		AtvSearchAMS();
		break;
	case ATV_CMD_PS:
		SendAtvHomeMute(ATV_MUTE);
		AtvSearchPS();
		break;
	case  ATV_CMD_NEXT_CH:
		SendAtvHomeMute(ATV_MUTE);
		AtvSearchChannelUpOrDown(0);
		break;
	case ATV_CMD_PRE_CH:
		SendAtvHomeMute(ATV_MUTE);
		AtvSearchChannelUpOrDown(1);
		break;
	case  ATV_CMD_FORMAT:
		SendAtvHomeMute(ATV_MUTE);
		AtvSetSearchFormat();
		break;
	case ATV_CMD_KEY:
		AtvHandleDigitalKey(u4KeyValue);
		break;
	default:
		break;
	}

	if (cmd > ATV_CMD_IDLE && cmd < ATV_CMD_FORMAT )
		AtvSendHomeMute(ATV_MUTE_OFF);

	return 1;
}


void AtvAndArmProtocol::AtvHandleDigitalKey(UINT32 u4Key)
{
	if(AtvGetState() != AtvStatus_Normal)
		return ;

	UINT32 u4KeyValue = AtvGetKeyValue();
	if(u4Key == 10 || u4Key == -10)
	{

	}
	else 
	{
		if (u4KeyValue > 0)
		{
			u4KeyValue = u4KeyValue*10;
		}

	}
	AtvSetKeyValue(u4Key + u4KeyValue);
	AtvSetKeyState(AtvKey_Handle);
	m_AtvDriverStruct.u1KeyTimer = 0;
}

// view output interface for UI display
int AtvAndArmProtocol::AtvGetMidStatus(UINT32* pData)
{
	BYTE area = AREA_TV;
	static UINT8 s_MemTimer = 0;

	if( pData )
	{
		if(AtvGetState() == AtvStatus_AS)
		{
			pData[0] = AtvGetState();					//����״̬
			pData[1] = AtvGetCurrentFreq();				//��ǰ��Ƶ��
			pData[2] = AtvGetFormat();					//������ʽ
			pData[3] = AtvGetValidStationNum();
			if(AtvGetRadioArea() == ATVAREA_Brazil || AtvGetRadioArea() == ATVAREA_RUSSIA)
				pData[4] = AtvGetCurrentPresetListIndex()+1;	//Ƶ����
			else
				pData[4] = AtvGetCurrentPresetListIndex();	//Ƶ����
#ifdef ATVTYPE_ADT1028
			pData[5] = m_pAtvDriver->GetTvDriverBand();
#else
			pData[5] = GetTvDriverBand();				//Ƶ��
#endif
		}
		else
		{
			if(AtvGetValidStationNum() > 0)
			{
				pData[0] = AtvGetState();					//����״̬
				pData[1] = AtvGetCurrentFreq();				//��ǰ��Ƶ��
				pData[2] = AtvGetFormat();					//������ʽ
				pData[3] = AtvGetValidStationNum();
				if(AtvGetRadioArea() == ATVAREA_Brazil || AtvGetRadioArea() == ATVAREA_RUSSIA)
					pData[4] = AtvGetCurrentPresetListIndex()+1;//Ƶ����
				else
					pData[4] = AtvGetCurrentPresetListIndex();
#ifdef ATVTYPE_ADT1028
				if (m_pAtvDriver != NULL)
				{
					pData[5] = m_pAtvDriver->GetTvDriverBand();
				}
#else
				pData[5] = GetTvDriverBand();				//Ƶ��
#endif
			}
			else
			{
				pData[0] = AtvGetState();					//����״̬
				pData[1] = AtvGetCurrentFreq();				//��ǰ��Ƶ��
				pData[2] = AtvGetFormat();					//������ʽ
				pData[3] = AtvGetDefaultStationNum();
				if(AtvGetRadioArea() == ATVAREA_Brazil)
					pData[4] = AtvGetCurrentDefaultPresetIndex()+2;	//Ƶ����
				else if(AtvGetRadioArea() == ATVAREA_RUSSIA)
					pData[4] = AtvGetCurrentDefaultPresetIndex()+1;
				else
					pData[4] = AtvGetCurrentDefaultPresetIndex();

#ifdef ATVTYPE_ADT1028
				if (m_pAtvDriver != NULL)
				{
					pData[5] = m_pAtvDriver->GetTvDriverBand();
				}
#else
				pData[5] = GetTvDriverBand();				//Ƶ��
#endif
			}
		}
	}
	if(s_MemTimer++ >20)
	{
		// ��Ƶ�ʸı䲢����Searchֹͣ״̬�»���PS����״̬�¼�⵽���źŵ�����½�Mute
#ifdef ATVTYPE_ADT1028
		if (QueryTVSignalState())
		{
			if(AtvGetMuteStatue() == ATV_MUTE_OFF)
				AtvSendHomeMute(ATV_MUTE_ON);
		}
		else if (m_AtvDriverStruct.PreFreq != pData[1] && (AtvOnNotStopStatus()))
			AtvSendHomeMute(ATV_MUTE_OFF);
#else
		if (/*m_AtvDriverStruct.PreFreq != pData[1] && */(!AtvOnNotStopStatus() || SET_PS_HAVE_SIGNAL == m_AtvDriverStruct.m_u4MemSearchStatus))
		{
			if(AtvGetMuteStatue() == ATV_MUTE_OFF)
				AtvSendHomeMute(ATV_MUTE_ON);
		}
		else if (m_AtvDriverStruct.PreFreq != pData[1] && (AtvOnNotStopStatus()))
			AtvSendHomeMute(ATV_MUTE_OFF);
#endif
		s_MemTimer = 0;
	}
		m_AtvDriverStruct.PreFreq=pData[1];
		switch(AtvGetKeyState())
		{
		case AtvKey_Normal:
			break;
		case AtvKey_Handle:
			if(m_AtvDriverStruct.u1KeyTimer++ >= 40)
			{
				m_AtvDriverStruct.u1KeyTimer = 0;
				AtvSetKeyState(AtvKey_Normal);
				AtvDigitalKeyJumpOneFreqByIndex(AtvGetKeyValue());
				AtvSetKeyValue(0);
			}
			break;
	}
	return 1;
}

int AtvAndArmProtocol::AtvGetMuteStatue()
{
	return m_uMute;
}


void AtvAndArmProtocol::AtvSendHomeMute(int nOff)
{
	m_uMute = nOff;
	//GPlayerApp::GetInstance()->Mute(nOff);
}

BYTE AtvAndArmProtocol::AtvGetFormat( void )
{
	BYTE area=AREA_TV;
	UINT32 u4num = AtvGetCurrentPresetListIndex();
	if(AtvGetValidStationNum() >0)
	{
		if(u4num < 0 || u4num > AtvGetValidStationNum())
		{
			return AtvGetTvDriverSystemFormat();
		}
		return m_rATVStation[area].u1ListFormat[u4num];
	
	}
	else
	{
		u4num = AtvGetCurrentDefaultPresetIndex();
		if(u4num < 0 || u4num > AtvGetDefaultStationNum())
			return AtvGetTvDriverSystemFormat();
		return m_rATVDefaultStation.u1ListFormat[u4num];

	}
}

void AtvAndArmProtocol::AtvSetFormat(UINT8 u1Format)
{
	BYTE area=AREA_TV;
	m_rATVStation[area].uATVFormat = u1Format%TV_TUNER_SYSTEM_END;
	UINT32 u4num = AtvGetCurrentPresetListIndex();

	if(AtvGetValidStationNum() >0)
	{
		if(u4num < 0 || u4num > AtvGetValidStationNum())
			return;
#ifdef ATVTYPE_ADT1028
		if (m_pAtvDriver != NULL)
		{
			m_pAtvDriver->TvDriverStateConfig_ex(m_rATVStation[area].u1ListFormat[u4num]);
		}
#else
		TvDriverStateConfig_ex(m_rATVStation[area].u1ListFormat[u4num]);
#endif
	}
	else
	{
		u4num = AtvGetCurrentDefaultPresetIndex();
		if(u4num < 0 || u4num > AtvGetDefaultStationNum())
			return;
#ifdef ATVTYPE_ADT1028
		if (m_pAtvDriver != NULL)
		{
			m_pAtvDriver->TvDriverStateConfig_ex(m_rATVDefaultStation.u1ListFormat[u4num]);
		}
#else
		TvDriverStateConfig_ex(m_rATVDefaultStation.u1ListFormat[u4num]);
#endif
	}

}

void AtvAndArmProtocol::SetCurFreqFormat(int nFormat)
{
	if (nFormat >= TV_TUNER_SYSTEM_END && nFormat < TV_TUNER_SYSTEM_PAL_DK)
	{
		return ;
	}
	BYTE area=AREA_TV;
	UINT32 u4num = AtvGetCurrentPresetListIndex();
	if(AtvGetValidStationNum() >0)
	{
		if(u4num < 0 || u4num > AtvGetValidStationNum())
			return ;

		m_rATVStation[area].u1ListFormat[u4num] = nFormat;
#ifdef ATVTYPE_ADT1028
		if (m_pAtvDriver != NULL)
		{
			m_pAtvDriver->TvDriverStateConfig_ex(m_rATVStation[area].u1ListFormat[u4num]);
		}
#else
		TvDriverStateConfig_ex(m_rATVStation[area].u1ListFormat[u4num]);
#endif
	}
	else
	{
		u4num = AtvGetCurrentDefaultPresetIndex();
		if(u4num < 0 || u4num > AtvGetDefaultStationNum())
			return ;
		m_rATVDefaultStation.u1ListFormat[u4num] = nFormat;
#ifdef ATVTYPE_ADT1028
		if (m_pAtvDriver != NULL)
		{
			m_pAtvDriver->TvDriverStateConfig_ex(m_rATVStation[area].u1ListFormat[u4num]);
		}
#else
		TvDriverStateConfig_ex(m_rATVStation[area].u1ListFormat[u4num]);
#endif
	}
}

BOOL AtvAndArmProtocol::AtvSetSearchFormat()
{
	BYTE area=AREA_TV;
	int format;
	UINT32 u4num = AtvGetCurrentPresetListIndex();
	if(AtvGetValidStationNum() >0)
	{
		if(u4num < 0 || u4num > AtvGetValidStationNum())
			return TRUE;

		m_rATVStation[area].u1ListFormat[u4num] +=1;
		m_rATVStation[area].u1ListFormat[u4num] = (m_rATVStation[area].u1ListFormat[u4num]%TV_TUNER_SYSTEM_END);
		format = m_rATVStation[area].u1ListFormat[u4num];
#ifdef ATVTYPE_ADT1028
		if (m_pAtvDriver != NULL)
		{
			m_pAtvDriver->TvDriverStateConfig_ex(m_rATVStation[area].u1ListFormat[u4num]);
		}
#else
		TvDriverStateConfig_ex(m_rATVStation[area].u1ListFormat[u4num]);
#endif
		
	}
	else
	{
		u4num = AtvGetCurrentDefaultPresetIndex();
		if(u4num < 0 || u4num > AtvGetDefaultStationNum())
			return TRUE;
		m_rATVDefaultStation.u1ListFormat[u4num] +=1;
		m_rATVDefaultStation.u1ListFormat[u4num] = (m_rATVDefaultStation.u1ListFormat[u4num]%TV_TUNER_SYSTEM_END);
		format = m_rATVDefaultStation.u1ListFormat[u4num];
#ifdef ATVTYPE_ADT1028
		if (m_pAtvDriver != NULL)
		{
			m_pAtvDriver->TvDriverStateConfig_ex(m_rATVDefaultStation.u1ListFormat[u4num]);
		}
#else
		TvDriverStateConfig_ex(m_rATVDefaultStation.u1ListFormat[u4num]);
#endif
	}

	printFormat(format);
	return TRUE;
}
void AtvAndArmProtocol::printFormat(int format){
	switch(format){
	case TV_TUNER_SYSTEM_PAL_DK:
		LOGI("Current format is TV_TUNER_SYSTEM_PAL_DK");
			break;
	case TV_TUNER_SYSTEM_PAL_BG:
		LOGI("Current format is TV_TUNER_SYSTEM_PAL_BG");
			break;
	case TV_TUNER_SYSTEM_NTSC_MN:
		LOGI("Current format is TV_TUNER_SYSTEM_NTSC_MN");
			break;
	case TV_TUNER_SYSTEM_SECAM_DK:
		LOGI("Current format is TV_TUNER_SYSTEM_SECAM_DK");
			break;
	case TV_TUNER_SYSTEM_SECAM_BG:
		LOGI("Current format is TV_TUNER_SYSTEM_SECAM_BG");
			break;
	case TV_TUNER_SYSTEM_PAL_M:
		LOGI("Current format is TV_TUNER_SYSTEM_PAL_M");
			break;
	}
}
void AtvAndArmProtocol::AtvAreaChanged(int nArea)
{
	//RETAILMSG(LOG_ON, (TEXT("[MMPlayer] AtvArea Chaned:%d \r\n"), nArea));
	LOGI("[MMPlayer] AtvArea Chaned:%d \r\n", nArea);
	m_uIsAtvAreaChanged = 1;
}
#endif
