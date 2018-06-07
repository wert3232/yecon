#ifndef __ATVDEF_H__
#define __ATVDEF_H__

#include <stdio.h>
#include <linux/types.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdlib.h>

#include "TypeDef.h"

#define ATVTYPE_ADT1028

#ifdef ATVTYPE_ADT1028

#define ATV_STATION_SIZE 100
#define DEVICE_NAME "/dev/i2c-1"

enum{
	TV_DRIVER_STATE_IDLE,
	TV_DRIVER_STATE_SETTING,
	TV_DRIVER_STATE_POWER,
	TV_DRIVER_STATE_CONFIG,
	TV_DRIVER_STATE_CHANGE,
	TV_DRIVER_STATE_DETECT,
	TV_DRIVER_STATE_END,
};

enum
{
	TV_TUNER_SYSTEM_PAL_DK,
	TV_TUNER_SYSTEM_PAL_I,
	TV_TUNER_SYSTEM_PAL_BG,
	TV_TUNER_SYSTEM_NTSC_MN,
	TV_TUNER_SYSTEM_SECAM_DK,
	TV_TUNER_SYSTEM_SECAM_BG,
	TV_TUNER_SYSTEM_PAL_M,
	TV_TUNER_SYSTEM_END,
};

#ifndef _TV_FREQ_MUL_
#define _TV_FREQ_MUL_		20
#define _TV_FREQ_(x)		(UINT16(((x)*_TV_FREQ_MUL_ )))
#endif

#define TV_POWER_ON
#define TV_POWER_OFF

#define _B7_  7
#define _B0_								(1<<0)


#define DISABLE   0

#define ENABLE 1

#define GET_TV_SYSTEM   2


typedef  void (*V_FUNC_V)(void);

enum
{
	AtvStatus_Normal		= 0x00,
	AtvStatus_SeekUp		= 0x01,
	AtvStatus_SeekDown	    = 0x02,
	AtvStatus_PS			= 0x03,
	AtvStatus_AS			= 0x04,
	AtvStatus_Search        = 0x05,
	AtvStatus_PS_Finish		= 0x06,
	AtvStatus_SearchStop    = 0x07,
	AtvStatus_PAS			= 0x08,
};

typedef struct TV_DRIVER_Tag{
	UINT16		Time;						
	UINT8		State;	
	UINT8		System;
	UINT8		NextSystem;
	UINT16		Freq;
	UINT8		Band;
	UINT8		Window;
	UINT8		Vif;
	UINT8		Level;
	UINT8		Enable;
	UINT8		SearchMode;
	UINT8		Status;
}TV_DRIVER;

extern TV_DRIVER TvDriver;

#define TV_DRIVER_POLLING_TIME				(8)
#define GET_TV_DRIVER_TIME					(TvDriver.Time)
#define SET_TV_DRIVER_TIME(x)				(GET_TV_DRIVER_TIME = (x)/TV_DRIVER_POLLING_TIME)

#define GET_TV_DRIVER_STATE					(TvDriver.State)
#define SET_TV_DRIVER_STATE(x)				(GET_TV_DRIVER_STATE = x)

#define GET_TV_DRIVER_SYSTEM				(TvDriver.System)
#define SET_TV_DRIVER_SYSTEM(x)				(GET_TV_DRIVER_SYSTEM = x)

#define GET_TV_DRIVER_FREQ					(TvDriver.Freq)
#define SET_TV_DRIVER_FREQ(x)				(GET_TV_DRIVER_FREQ = x)

#define GET_TV_DRIVER_BAND					(TvDriver.Band)
#define SET_TV_DRIVER_BAND(x)				(GET_TV_DRIVER_BAND = x)

#define GET_TV_DRIVER_WINDOW				(TvDriver.Window)
#define SET_TV_DRIVER_WINDOW(x)				(GET_TV_DRIVER_WINDOW = x)

#define GET_TV_DRIVER_VIF					(TvDriver.Vif)
#define SET_TV_DRIVER_VIF(x)				(GET_TV_DRIVER_VIF = x)

#define GET_TV_DRIVER_LEVEL					(TvDriver.Level)
#define SET_TV_DRIVER_LEVEL(x)				(GET_TV_DRIVER_LEVEL =x)

#define GET_TV_DRIVER_ENABLE				(TvDriver.Enable)
#define SET_TV_DRIVER_ENABLE(x)				(GET_TV_DRIVER_ENABLE = x)

#define GET_TV_DRIVER_SEARCHMODE			(TvDriver.SearchMode)
#define SET_TV_DRIVER_SEARCHMODE(x)			(GET_TV_DRIVER_SEARCHMODE = x)

typedef enum
{
	CDTMTV_RT_SUCCESS = 0,
	CDTMTV_RT_IIC_ERROR,
	CDTMTV_RT_SCAN_FAIL,
	CDTMTV_RT_SCAN_DONE,
	CDTMTV_RT_SCAN_TIMEOUT,
	CDTMTV_RT_SCAN_IN_AFCWIN,
	CDTMTV_RT_SCAN_GOT_YSIGN,
	CDTMTV_RT_SCAN_EMPTY_CH,
#ifdef DEBUG_SCAN
	CDTMTV_RT_SCAN_YSNR_FAIL,
	CDTMTV_RT_SCAN_FMC_FAIL,   
	CDTMTV_RT_SCAN_HV_FAIL,
#endif	
	CDTMTV_RT_ERROR
} TV_RES;

// ---------------------------------------------------------------------------
// Commonly used video formats and FM mode are included here.  
// ---------------------------------------------------------------------------
enum{
	CDTMTV_VSTD_M, 
	CDTMTV_VSTD_M_X,
	CDTMTV_VSTD_N,
	CDTMTV_VSTD_N_X,
	CDTMTV_VSTD_BG,
	CDTMTV_VSTD_BG_X,
	CDTMTV_VSTD_I,
	CDTMTV_VSTD_I_X,
	CDTMTV_VSTD_DK,
	CDTMTV_VSTD_DK_X,

	CDTMTV_VSTD_FM,
	CDTMTV_VSTD_NONE = 255   // No Video Standard Specified
};


#ifdef LONGKE
#define AGC_VGA_TH (0x2E08)//(0x2A08)//(0x2408)//(0x2B08)
#else
#define AGC_VGA_TH (0x3000) //(0x2A08)//(0x2408)//(0x2B08) edit by ZhengJH 120426 for lower Seek
#define AGC_VGA        (0x2E)
#endif
#define Y_AFC_TH  (1092) // 1092 * 206 = 225KHz
#define FM_AFC_TH (32) // 32 * 6.6K
#define FM_RSSI_TH (0x13)//(0x20)
#define STANDARD_LNATH 0x30 // 0x8030(-50db_th) + th - 0x8000

#define BAND_VH_START (126000) //(110000)//(126000)
#define BAND_U_START (356000)


// CDT1028 page definition.
#define CDT1028_PAGE_0            	(0x0)
#define CDT1028_PAGE_1            	(0x1)
#define CDT1028_PAGE_UNDEFINED    	(0xff)
#define CHECK_SEEK_READY_TIMES    	(3)


#define TV_CDT1028_TUNER_MIN_FREQ							(45.25)
#define TV_CDT1028_TUNER_MAX_FREQ							(863.25)

enum{
	CDT1028_BAND_VHF_LOW,
	CDT1028_BAND_VHF_HIGH,
	CDT1028_BAND_UHF,
};

#define			DELAY_1ms(x)			usleep(x*1000)
#define			Cdt1028_debug			1

#define CDT1028_DEVICE_ADDR					(0x61)
#define CDT1028_CONTROL_READ_ADDR			(CDT1028_DEVICE_ADDR|_B0_)
#define CDT1028_CONTROL_WRITE_ADDR			(CDT1028_DEVICE_ADDR)

#define _CDT1028_REG_MAP_ERROR_				(3)

#define CDT1028_TINERMEDIA_FREQ					_TV_FREQ_(38.9)
#endif

#endif
