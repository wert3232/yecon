
#ifndef __CCdtOrder1028_h__
#define __CCdtOrder1028_h__
#include "CdtCommunication1028.h"

#ifdef ATVTYPE_ADT1028

class CCdtOrder1028
{
public:
	CCdtOrder1028(void);
	virtual ~CCdtOrder1028(void);

	void initI2c(){
		if(m_pCommunication!=NULL){
			m_pCommunication->init();
		}
	}
	void closeI2c(){
		if(m_pCommunication!=NULL){
			m_pCommunication->deinit();
		}
	}
	void CDT1028_ScanFreqSet(UINT32 dwVFreq);
	void CDT1028_NotchHandler(UINT32 FreqOffset, UINT32 startFreqReg, UINT32 endFreqReg, UINT16 * pReg);
	void CDT1028_NotchHarm(UINT8 HarmNum) ;
	void CDT1028_NotchDisabel(void);
	void CDT1028_Notch(UINT8 Mode, UINT32 Freq);
	UINT8 CDT1028_DSPInit(void);
	UINT8 CDT1028_LDOInit(void);
	UINT8 CDT1028_Tuning(void);
	void CDT1028_SetSystemMode(UINT8 SystemMode);//������ʽ��һ��Ҫ����дƵ�㣬������Ϊ��Ƶ�ͱ�Ƶֵ��ͬ���ͼ����
	void ATVSetRxOn_Tuning(void);
	UINT8 CDT1028_GetSystemMode(void);
	void CDT1028_SetPllDem27M(void);
	void CDT1028_SetPllDem31M(void);
	void CDT1028_SetSystemMode_31M(UINT8 SystemMode);
	void CDT1028_SetBBLvl(void);
	void CDT1028_FreqSet(UINT32 dwVFreq);
	void CDT1028_GetLnaTH(void);
	UINT8 CDT1028_SetPDN(UINT8 On);
	UINT8 CDT1028_Seek(void);
	void ATVSetRxOn_Seek(void);
	void CDT1028_ScanFreqSet2(UINT32 dwVFreq);
	UINT8 CDT1028_ScanChMode(void);
	UINT8 CDT1028_ScanChAllMode(UINT32 dwVFreq);
	void CDT1028_SetFreqOffset(UINT8 SysMode);
	void CDT1028_SetCvbsAm(UINT16 value);
	void CDT1028_SetAmDem27M(void);
	void CDT1028_SetAmDem31M(void);

	void CDT1028_Mode_M_27M(void);
	void CDT1028_Mode_M_X_27M(void) ;
	void CDT1028_Mode_N_27M(void) ;
	void CDT1028_Mode_N_X_27M(void) ;
	void CDT1028_Mode_BG_27M(void) ;
	void CDT1028_Mode_BG_X_27M(void) ;
	void CDT1028_Mode_I_27M(void) ;
	void CDT1028_Mode_I_X_27M(void) ;
	void CDT1028_Mode_DK_27M(void) ;
	void CDT1028_Mode_DK_X_27M(void) ;
	void CDT1028_Mode_M_31M(void);
	void CDT1028_Mode_M_X_31M(void) ;
	void CDT1028_Mode_N_31M(void) ;
	void CDT1028_Mode_N_X_31M(void);
	void CDT1028_Mode_BG_31M(void);
	void CDT1028_Mode_BG_X_31M(void);
	void CDT1028_Mode_I_31M(void);
	void CDT1028_Mode_I_X_31M(void);
	void CDT1028_Mode_DK_31M(void);
	void CDT1028_Mode_DK_X_31M(void);

	UINT8 CDT1028_Init(void);
	UINT8 CDT1028_ENTER_TV(void);

	UINT CDT1028_ReadReg(UINT16 RegAddr, UINT16 *Data);
	void printReg(UINT16 regAddr);
private:
	UINT16 LnaTH1, LnaTH2, LnaTH3; 

	UINT8 m_SystemMode;
	UINT32 m_nFreqOffset;
	CCdtCommunication1028*	m_pCommunication;
};

#endif
#endif
