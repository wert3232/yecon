#ifndef __CCDT1028_H__
#define __CCDT1028_H__
#include "CdtOrder1028.h"

#ifdef ATVTYPE_ADT1028
class CCdt1028
{
public:
	CCdt1028(void);
	virtual ~CCdt1028(void);

	void tv_cdt1028SetTvState();

	void TvDriverInit(void);
	void TvDriverDeInit(void);
	void TvDriverSetFreq(int nFreq, int nMode = 0);
	UINT ExchangeFormat(UINT uSrcFormat);
	void TvDriverStateChange_ex(UINT8* lock);
	void TvDriverStateConfig_ex(UINT8 systemformat);
	void TvDriverStateDetect_ex(UINT8* afc,UINT8* uafc,UINT8* uvif);
	UINT8 GetTvDriverBand();
	UINT8 DetectCdt1028Change(void);
	UINT8 GetTvCdt1028SeekState(void);

	void TvCdt1028DriverStateSetting(void);
	void TvCdt1028DriverStatePower(void);
	void TvDriverSetTuningFreq(void);

	void SetTunerSeekFlag(UINT8 uSeek);
	UINT8 GetTunerSeekFlag();
	UINT8 ExchangeFormatDrv2App(UINT8 uDrvFormat);
	BOOL GetCurFormat(UINT8* uFormat);

private:
	CCdtOrder1028* m_pCdtOrder;
	UINT8 m_TvTunerSeekFlag;
};
#endif
#endif
