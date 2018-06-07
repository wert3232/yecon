#include "Cdt1028.h"
#ifdef ATVTYPE_ADT1028
//#define __DEBUG_Cdt1028

//#include "TV_Model.cpp"

#define TAG "CCdt1028"
#include "../Common.h"

TV_DRIVER TvDriver;

CCdt1028::CCdt1028(void)
{
	m_pCdtOrder = new CCdtOrder1028;
	m_TvTunerSeekFlag = TRUE;
}

CCdt1028::~CCdt1028(void)
{
	if (m_pCdtOrder != NULL)
	{
		delete m_pCdtOrder;
	}
}

static UINT8 TvCdt1028DriverContinue;
static UINT8 TvCdt1028DriverMute;
static UINT8 TvCdt1028PollingTimeCnt=0;

void CCdt1028::TvDriverDeInit(void)
{
	memset(&TvDriver, 0, sizeof(TvDriver));
	//ATV_DeInit();
	m_pCdtOrder->closeI2c();
}

void CCdt1028::TvDriverSetFreq(int nFreq, int nMode/* = 0*/)
{
	SET_TV_DRIVER_FREQ( nFreq);
	if (nMode == 0)
	{
		TvCdt1028DriverStateSetting();
	}
	else
	{
		TvDriverSetTuningFreq();
	}
}

void CCdt1028::TvDriverInit(void)
{
	memset(&TvDriver, 0, sizeof(TvDriver));
	//ATV_Init();
	m_pCdtOrder->initI2c();
	TvCdt1028DriverStatePower();
}

void CCdt1028::SetTunerSeekFlag(UINT8 uSeek)
{
	m_TvTunerSeekFlag = uSeek;
}

UINT8 CCdt1028::GetTunerSeekFlag()
{
	return m_TvTunerSeekFlag;
}

UINT CCdt1028::ExchangeFormat(UINT uSrcFormat)
{
	switch(uSrcFormat)
	{
	case TV_TUNER_SYSTEM_PAL_DK:
		TvDriver.NextSystem = (CDTMTV_VSTD_DK);
		break;
	case TV_TUNER_SYSTEM_PAL_I:
		TvDriver.NextSystem = (CDTMTV_VSTD_I);
		break;
	case TV_TUNER_SYSTEM_PAL_BG:
		TvDriver.NextSystem = (CDTMTV_VSTD_BG);
		break;
	case TV_TUNER_SYSTEM_PAL_M:
	case TV_TUNER_SYSTEM_NTSC_MN:
		TvDriver.NextSystem = (CDTMTV_VSTD_M);
		break;
// 	case TV_TUNER_SYSTEM_PAL_N:
// 		TvDriver.NextSystem = (CDTMTV_VSTD_N);
// 		break;
	case TV_TUNER_SYSTEM_SECAM_DK:
		TvDriver.NextSystem = (CDTMTV_VSTD_DK);
		break;
	case TV_TUNER_SYSTEM_SECAM_BG:
		TvDriver.NextSystem = (CDTMTV_VSTD_BG);
		break;
	}
	return TRUE;
}

UINT8 CCdt1028::ExchangeFormatDrv2App(UINT8 uDrvFormat)
{
	UINT8 uAppFormat;
	switch(uDrvFormat)
	{
	case CDTMTV_VSTD_DK:
		uAppFormat = TV_TUNER_SYSTEM_PAL_DK;
		break;
	case CDTMTV_VSTD_I:
		uAppFormat = TV_TUNER_SYSTEM_PAL_I;
		break;
	case CDTMTV_VSTD_BG:
		uAppFormat = TV_TUNER_SYSTEM_PAL_BG;
		break;
	case CDTMTV_VSTD_M:
		uAppFormat = TV_TUNER_SYSTEM_PAL_M;// TV_TUNER_SYSTEM_NTSC_MN
		break;
// 	case CDTMTV_VSTD_N:
// 		uAppFormat = TV_TUNER_SYSTEM_PAL_N;
// 		break;
	}
	return uAppFormat;
}

void CCdt1028::TvDriverStateConfig_ex(UINT8 systemformat)
{
	if(!GET_TV_DRIVER_ENABLE)
	{
		if (CDTMTV_RT_IIC_ERROR != m_pCdtOrder->CDT1028_ENTER_TV())
		{
			SET_TV_DRIVER_ENABLE(TRUE);
		}
	}
	ExchangeFormat(systemformat);// TV_TUNER_SYSTEM_PAL_I;

	m_pCdtOrder->CDT1028_SetSystemMode(TvDriver.NextSystem);
	m_pCdtOrder->CDT1028_FreqSet(GET_TV_DRIVER_FREQ*50);
	SET_TV_DRIVER_SYSTEM(TvDriver.NextSystem);

#if (Cdt1028_debug)
	LOGI("Tv Config-----System:%d\n",(int)GET_TV_DRIVER_SYSTEM);
#endif

	SET_TV_DRIVER_STATE(TV_DRIVER_STATE_SETTING);
}

void CCdt1028::TvDriverStateDetect_ex(UINT8* afc,UINT8* uafc,UINT8* uvif)
{
	*uafc = 1;
	*uvif = 1;
	*afc = 1;
}

UINT8 CCdt1028::GetTvDriverBand()
{
	return GET_TV_DRIVER_BAND;
}

UINT8 CCdt1028::DetectCdt1028Change(void)
{
	UINT8 Change = FALSE;
	UINT8 Band;

	if(GET_TV_DRIVER_FREQ <= _TV_FREQ_(147.25))
	{
		Band =  CDT1028_BAND_VHF_LOW;
	}
	else if(GET_TV_DRIVER_FREQ <= _TV_FREQ_(463.25))
	{
		Band =  CDT1028_BAND_VHF_HIGH;
	}
	else
	{
		Band =  CDT1028_BAND_UHF;
	}

	if(Band != GET_TV_DRIVER_BAND)
	{
		SET_TV_DRIVER_BAND(Band);
		Change = TRUE;
	}
	return Change;
}

UINT8 CCdt1028::GetTvCdt1028SeekState(void)
{
	switch(GET_TV_DRIVER_SEARCHMODE)
	{
	case AtvStatus_SeekUp:
	case AtvStatus_SeekDown:
	case AtvStatus_PS:
	case AtvStatus_AS:
	case AtvStatus_Search:
		return TRUE;
	default:break;
	}
	return FALSE;
}

void CCdt1028::TvDriverStateChange_ex(UINT8* lock)
{
	if(!GET_TV_DRIVER_ENABLE)
	{
		if (CDTMTV_RT_IIC_ERROR != m_pCdtOrder->CDT1028_ENTER_TV())
		{
			SET_TV_DRIVER_ENABLE(TRUE);
		}
	}
	UINT16 reg = 0;
	
	TvDriver.Status = CDTMTV_RT_SCAN_FAIL;
	TvDriver.Status = m_pCdtOrder->CDT1028_ScanChAllMode(GET_TV_DRIVER_FREQ*50);

#if (Cdt1028_debug)
	LOGI("SearchFreq status: %d, freq:%d \r\n", TvDriver.Status, GET_TV_DRIVER_FREQ*50);
#endif
	if(TvDriver.Status == CDTMTV_RT_SCAN_DONE)
	{
#if (Cdt1028_debug)
		LOGI("NextSystemMode:%d, CurSystemMode:%d \r\n", GET_TV_DRIVER_SYSTEM, m_pCdtOrder->CDT1028_GetSystemMode());
#endif
		*lock = 1;
	}
	else
	{
		*lock = 0;
	}
}

BOOL CCdt1028::GetCurFormat(UINT8* uFormat)
{
	if (GET_TV_DRIVER_SYSTEM != m_pCdtOrder->CDT1028_GetSystemMode())
	{
		*uFormat = ExchangeFormatDrv2App(m_pCdtOrder->CDT1028_GetSystemMode());
		return TRUE;
	}
	return FALSE;
}

void CCdt1028::TvDriverSetTuningFreq(void)
{
	if(!GET_TV_DRIVER_ENABLE)
	{
		if (CDTMTV_RT_IIC_ERROR != m_pCdtOrder->CDT1028_ENTER_TV())
		{
			SET_TV_DRIVER_ENABLE(TRUE);
		}
	}
	TvCdt1028DriverMute = TRUE;

	DetectCdt1028Change();
	m_pCdtOrder->CDT1028_Tuning();
	m_pCdtOrder->CDT1028_SetSystemMode(GET_TV_DRIVER_SYSTEM);
	m_pCdtOrder->CDT1028_FreqSet(GET_TV_DRIVER_FREQ*50);
	SET_TV_DRIVER_STATE(TV_DRIVER_STATE_CHANGE);
}

void CCdt1028::TvCdt1028DriverStateSetting(void)
{
	if(!GET_TV_DRIVER_ENABLE)
	{
		if (CDTMTV_RT_IIC_ERROR != m_pCdtOrder->CDT1028_ENTER_TV())
		{
			SET_TV_DRIVER_ENABLE(TRUE);
		}
	}

	TvCdt1028DriverMute = TRUE;

	DetectCdt1028Change();
	if(GetTvCdt1028SeekState())
	{
#if (Cdt1028_debug)
		LOGI("TvCdt1028DriverStateSetting DoSeek--\r\n");
#endif
		if (GetTunerSeekFlag())
		{
			SetTunerSeekFlag(FALSE);
			m_pCdtOrder->CDT1028_ENTER_TV();
			/*
			if(!GET_TV_DRIVER_ENABLE)
			{
				if (CDTMTV_RT_IIC_ERROR != m_pCdtOrder->CDT1028_ENTER_TV())
				{
					SET_TV_DRIVER_ENABLE(TRUE);
				}
			}
			*/
#if (Cdt1028_debug)
			LOGI("Seek and Enter TV \r\n");
#endif
		}
		m_pCdtOrder->CDT1028_Seek();
		m_pCdtOrder->CDT1028_ScanFreqSet(GET_TV_DRIVER_FREQ*50);
	}
	else
	{
		m_pCdtOrder->CDT1028_Tuning();
		m_pCdtOrder->CDT1028_SetSystemMode(GET_TV_DRIVER_SYSTEM);
		m_pCdtOrder->CDT1028_FreqSet(GET_TV_DRIVER_FREQ*50);
	}	
	SET_TV_DRIVER_STATE(TV_DRIVER_STATE_CHANGE);
}

void CCdt1028::TvCdt1028DriverStatePower(void)
{
	if (CDTMTV_RT_IIC_ERROR != m_pCdtOrder->CDT1028_ENTER_TV())
	{
		m_pCdtOrder->CDT1028_Tuning();
		SET_TV_DRIVER_ENABLE(TRUE);
	}

#if (Cdt1028_debug)
	LOGI("Tv Tuner Power On\n");
#endif
}

#endif
