#include "CdtOrder1028.h"
#define TAG "CCdtOrder1028"
#include "../Common.h"
#ifdef ATVTYPE_ADT1028

//#define __DEBUG_Cdt1028

CCdtOrder1028::CCdtOrder1028(void)
{
	m_SystemMode = CDTMTV_VSTD_NONE;
	m_nFreqOffset = 0;
	m_pCommunication = new CCdtCommunication1028;
}

CCdtOrder1028::~CCdtOrder1028(void)
{
	if (m_pCommunication != NULL)
	{
		delete m_pCommunication;
	}
}

//-------------------------------------------------------------------------------------------------//
//-------------------------------------------------------------------------------------------------//
//-------------------------------------------------------------------------------------------------//

void CCdtOrder1028::CDT1028_NotchHandler(UINT32 FreqOffset, UINT32 startFreqReg, UINT32 endFreqReg, UINT16 * pReg) 
{
	if(startFreqReg < 0x8000)
		startFreqReg += 0x10000;
	if(endFreqReg < 0x8000)
		endFreqReg += 0x10000;

	*pReg = (UINT16)(0xffff&(startFreqReg + (endFreqReg -startFreqReg) /50 * (FreqOffset / 10)));
}

void CCdtOrder1028::CDT1028_NotchHarm(UINT8 HarmNum) 
{
	if(HarmNum == 0) //Harm1
	{
		m_pCommunication->CDT1028_WriteReg(0x0146, 0x03e0);
	}
	else //Harm2 
	{
		m_pCommunication->CDT1028_WriteReg(0x0146, 0x001f);
	}
}

void CCdtOrder1028::CDT1028_NotchDisabel(void) 
{
	m_pCommunication->CDT1028_WriteReg(0x0146, 0x0000);
}

void CCdtOrder1028::CDT1028_Notch(UINT8 Mode, UINT32 Freq) 
{
	UINT16 reg;
	if(Mode == 0) // 27M
	{
		m_pCommunication->CDT1028_WriteReg(0x0148, 0x3f68);
		m_pCommunication->CDT1028_WriteReg(0x0149, 0x3fb4);
		m_pCommunication->CDT1028_WriteReg(0x014b, 0x3f68);
		m_pCommunication->CDT1028_WriteReg(0x014c, 0x3fb4);
		if(Freq < 250)
		{
			CDT1028_NotchHandler(Freq, 0x8000, 0x8174, &reg);
		}
		else if((Freq >= 250)&&(Freq < 750))
		{
			CDT1028_NotchHandler(Freq -250, 0x8174, 0x8847, &reg);
		}
		else if((Freq >= 750)&&(Freq < 1250))
		{
			CDT1028_NotchHandler(Freq -750, 0x8847, 0x958e, &reg);
		}
		else if((Freq >= 1250)&&(Freq < 1750))
		{
			CDT1028_NotchHandler(Freq -1250, 0x958e, 0xa891, &reg);
		}
		else if((Freq >= 1750)&&(Freq < 2250))
		{
			CDT1028_NotchHandler(Freq -1750, 0xa891, 0xc04c, &reg);
		}
		else if((Freq >= 2250)&&(Freq < 2750))
		{
			CDT1028_NotchHandler(Freq -2250, 0xc04c, 0xdb76, &reg);
		}
		else if((Freq >= 2750)&&(Freq < 3250))
		{
			CDT1028_NotchHandler(Freq -2750, 0xdb76, 0xf898, &reg);
		}
		else if((Freq >= 3250)&&(Freq < 3750))
		{
			CDT1028_NotchHandler(Freq -3250, 0xf898, 0x1620, &reg);
		}
		else if((Freq >= 3750)&&(Freq < 4250))
		{
			CDT1028_NotchHandler(Freq -3750, 0x1620, 0x3277, &reg);
		}
		else if((Freq >= 4250)&&(Freq < 4750))
		{
			CDT1028_NotchHandler(Freq -4250, 0x3277, 0x4c15, &reg);
		}
		else if((Freq >= 4750)&&(Freq < 5250))
		{
			CDT1028_NotchHandler(Freq -4750, 0x4c15, 0x6199, &reg);
		}
		else if((Freq >= 5250)&&(Freq < 5750))
		{
			CDT1028_NotchHandler(Freq -5250, 0x6199, 0x71db, &reg);
		}
		else if(Freq > 5750)
		{
			CDT1028_NotchHandler(Freq -5250, 0x71db, 0x8000, &reg);
		}
	}
	else
	{
		m_pCommunication->CDT1028_WriteReg(0x0148, 0x3f7c);
		m_pCommunication->CDT1028_WriteReg(0x0149, 0x3fbe);
		m_pCommunication->CDT1028_WriteReg(0x014b, 0x3f7c);
		m_pCommunication->CDT1028_WriteReg(0x014c, 0x3fbe);
		if(Freq < 250)
		{
			CDT1028_NotchHandler(Freq, 0x8000, 0x812c, &reg);
		}
		else if((Freq >= 250)&&(Freq < 750))
		{
			CDT1028_NotchHandler(Freq -250, 0x812c, 0x865d, &reg);
		}
		else if((Freq >= 750)&&(Freq < 1250))
		{
			CDT1028_NotchHandler(Freq -750, 0x865d, 0x9089, &reg);
		}
		else if((Freq >= 1250)&&(Freq < 1750))
		{
			CDT1028_NotchHandler(Freq -1250, 0x9089, 0x9f45, &reg);
		}
		else if((Freq >= 1750)&&(Freq < 2250))
		{
			CDT1028_NotchHandler(Freq -1750, 0x9f45, 0xb1f7, &reg);
		}
		else if((Freq >= 2250)&&(Freq < 2750))
		{
			CDT1028_NotchHandler(Freq -2250, 0xb1f7, 0xc7db, &reg);
		}
		else if((Freq >= 2750)&&(Freq < 3250))
		{
			CDT1028_NotchHandler(Freq -2750, 0xc7db, 0xe00c, &reg);
		}
		else if((Freq >= 3250)&&(Freq < 3750))
		{
			CDT1028_NotchHandler(Freq -3250, 0xe00c, 0xf98b, &reg);
		}
		else if((Freq >= 3750)&&(Freq < 4250))
		{
			CDT1028_NotchHandler(Freq -3750, 0xf98b, 0x134e, &reg);
		}
		else if((Freq >= 4250)&&(Freq < 4750))
		{
			CDT1028_NotchHandler(Freq -4250, 0x134e, 0x2c47, &reg);
		}
		else if((Freq >= 4750)&&(Freq < 5250))
		{
			CDT1028_NotchHandler(Freq -4750, 0x2c47, 0x436f, &reg);
		}
		else if((Freq >= 5250)&&(Freq <= 5750))
		{
			CDT1028_NotchHandler(Freq -5250, 0x436f, 0x57d5, &reg);
		}
		else if(Freq > 5750)
		{
			CDT1028_NotchHandler(Freq -5750, 0x57d5, 0x8000, &reg);
		}
	}
	m_pCommunication->CDT1028_WriteReg(0x0147, reg);
	m_pCommunication->CDT1028_WriteReg(0x014a, reg);
}

void CCdtOrder1028::CDT1028_Mode_M_27M(void) 
{
	m_pCommunication->CDT1028_WriteReg(0x0106, 0x82f0); // atv_mode=101); //
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x0000); //line: 625 or 525 
	DELAY_1ms(2);
}

void CCdtOrder1028::CDT1028_Mode_M_X_27M(void) 
{
	m_pCommunication->CDT1028_WriteReg(0x0106, 0x82f0); // atv_mode=101); //
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x3ec0); //line: 625 or 525 
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_Mode_N_27M(void) 
{
	m_pCommunication->CDT1028_WriteReg(0x0106, 0x8270); // atv_mode=100); //
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x0000); //line: 625 or 525 
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_Mode_N_X_27M(void) 
{
	m_pCommunication->CDT1028_WriteReg(0x0106, 0x8270); // atv_mode=100); //
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x2eb4); //line: 625 or 525
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_Mode_BG_27M(void) 
{
	m_pCommunication->CDT1028_WriteReg(0x106, 0x8170); //atv_mode=010;
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x0000); //line: 625 or 525
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_Mode_BG_X_27M(void) 
{
	m_pCommunication->CDT1028_WriteReg(0x106, 0x8170); //atv_mode=010;
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x2eb4); //line: 625 or 525
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_Mode_I_27M(void) 
{
	m_pCommunication->CDT1028_WriteReg(0x106, 0x80f0); //atv_mode=001;	
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x0000); //line: 625 or 525
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_Mode_I_X_27M(void) 
{
	m_pCommunication->CDT1028_WriteReg(0x106, 0x80f0); //atv_mode=001;
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x2eb4); //line: 625 or 525
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_Mode_DK_27M(void) 
{
	m_pCommunication->CDT1028_WriteReg(0x106, 0x8070); //atv_mode=000;
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x0000); //line: 625 or 525
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_Mode_DK_X_27M(void) 
{
	m_pCommunication->CDT1028_WriteReg(0x106, 0x8070); //atv_mode=000;
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x2eb4); //line: 625 or 525
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_Mode_M_31M(void) 
{
	m_pCommunication->CDT1028_WriteReg(0x0106, 0x8af0); // atv_mode=101); //
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x0000); //line: 625 or 525 
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_Mode_M_X_31M(void) 
{
	m_pCommunication->CDT1028_WriteReg(0x0106, 0x8af0); // atv_mode=101); //
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x3fc0); //line: 625 or 525 
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_Mode_N_31M(void) 
{
	m_pCommunication->CDT1028_WriteReg(0x0106, 0x8a70); // atv_mode=100); //
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x0000); //line: 625 or 525 
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_Mode_N_X_31M(void) 
{
	m_pCommunication->CDT1028_WriteReg(0x0106, 0x8a70); // atv_mode=100); //
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x2fb2); //line: 625 or 525
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_Mode_BG_31M(void)
{
	m_pCommunication->CDT1028_WriteReg(0x0106, 0x8970); // atv_mode=010); //
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x0000); //line: 625 or 525
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_Mode_BG_X_31M(void)
{
	m_pCommunication->CDT1028_WriteReg(0x0106, 0x8970); // atv_mode=010); //
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x2fb2); //line: 625 or 525
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_Mode_I_31M(void)
{
	m_pCommunication->CDT1028_WriteReg(0x0106, 0x88f0); // atv_mode=001); //
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x0000); //line: 625 or 525
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_Mode_I_X_31M(void)
{
	m_pCommunication->CDT1028_WriteReg(0x0106, 0x88f0); // atv_mode=001); //
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x2fb2); //line: 625 or 525
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}
void CCdtOrder1028::CDT1028_Mode_DK_31M(void)
{
	m_pCommunication->CDT1028_WriteReg(0x0106, 0x8870); // atv_mode=000); //
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x0000); //line: 625 or 525
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_Mode_DK_X_31M(void)
{
	m_pCommunication->CDT1028_WriteReg(0x0106, 0x8870); // atv_mode=000); //
	m_pCommunication->CDT1028_WriteReg(0x16c, 0x2fb2); //line: 625 or 525
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
}

void CCdtOrder1028::CDT1028_SetFreqOffset(UINT8 SysMode)
{
	switch(SysMode)
	{
	case CDTMTV_VSTD_M:
	case CDTMTV_VSTD_N:
	case CDTMTV_VSTD_M_X:
	case CDTMTV_VSTD_N_X:	
#if 1
		m_nFreqOffset = 2000;//1725;//KHz
#else
		m_nFreqOffset = 1875;//1725;//KHz
#endif
		break;

	case CDTMTV_VSTD_BG:
	case CDTMTV_VSTD_BG_X:	
#if 1
		m_nFreqOffset = 2675;//2125;
#else
		m_nFreqOffset = 2425;//2125;
#endif
		break;

	case CDTMTV_VSTD_I:	
	case CDTMTV_VSTD_I_X:	
		m_nFreqOffset = 3025;
		break;

	case CDTMTV_VSTD_DK:
	case CDTMTV_VSTD_DK_X:	
#if 1
		m_nFreqOffset = 2900;
#else
		m_nFreqOffset = 2825;
#endif
		break;

	case CDTMTV_VSTD_FM:
		m_nFreqOffset = 225;
		break;

	default:
		m_nFreqOffset = 0;
		break;
	}
}

UINT8 CCdtOrder1028::CDT1028_DSPInit(void)
{
	UINT8 rt = CDTMTV_RT_SUCCESS;

	//============================================
	//item: initial_DIG_Common
	rt = 
		m_pCommunication->CDT1028_WriteReg(0x0100, 0x0000); // soft_resetn=0); //
	m_pCommunication->CDT1028_WriteReg(0x0100, 0x0001); // soft_resetn=1); //
	m_pCommunication->CDT1028_WriteReg(0x0107, 0x3870); //
	m_pCommunication->CDT1028_WriteReg(0x0108, 0xb740); // clk edge
	m_pCommunication->CDT1028_WriteReg(0x0109, 0x0020); // tmr_agc_rssi
	m_pCommunication->CDT1028_WriteReg(0x010f, 0x8003); //
	m_pCommunication->CDT1028_WriteReg(0x0110, 0x3024); // 
	m_pCommunication->CDT1028_WriteReg(0x0111, 0x12ff); // 
	m_pCommunication->CDT1028_WriteReg(0x0112, 0x8080); // 
	m_pCommunication->CDT1028_WriteReg(0x0113, 0x1dff); // 
	m_pCommunication->CDT1028_WriteReg(0x0114, 0x2002); // 
	m_pCommunication->CDT1028_WriteReg(0x0117, 0x0200); // 
	m_pCommunication->CDT1028_WriteReg(0x0118, 0x03cc); // 
	m_pCommunication->CDT1028_WriteReg(0x0119, 0x8600); // 
	m_pCommunication->CDT1028_WriteReg(0x011a, 0x8201); // 
	m_pCommunication->CDT1028_WriteReg(0x011b, 0xb41a); //
	m_pCommunication->CDT1028_WriteReg(0x011e, 0x7f0b); //
	m_pCommunication->CDT1028_WriteReg(0x011f, 0x24a0); //
	m_pCommunication->CDT1028_WriteReg(0x0121, 0x8180); // 
	m_pCommunication->CDT1028_WriteReg(0x0122, 0x0000); // bypass agc_vga_rst
	//m_pCommunication->CDT1028_WriteReg(0x012f, 0x0001); //add by ZhengJH 120504
	m_pCommunication->CDT1028_WriteReg(0x0130, 0x0408); // 
	m_pCommunication->CDT1028_WriteReg(0x0131, 0x8f00); // 
	m_pCommunication->CDT1028_WriteReg(0x0132, 0x3044); //
	m_pCommunication->CDT1028_WriteReg(0x0133, 0x00e0); //
	m_pCommunication->CDT1028_WriteReg(0x0134, 0x112c); //
	m_pCommunication->CDT1028_WriteReg(0x0137, 0x7200); // 
	m_pCommunication->CDT1028_WriteReg(0x0138, 0x0aa0); // 
	m_pCommunication->CDT1028_WriteReg(0x0139, 0xe03f); // 
	m_pCommunication->CDT1028_WriteReg(0x014d, 0xa000);//m_pCommunication->CDT1028_WriteReg(0x014d, 0x3800); edit by ZhengJH 120503 for fail shape
	m_pCommunication->CDT1028_WriteReg(0x0153, 0x6c78);
	m_pCommunication->CDT1028_WriteReg(0x0158, 0x8c21); // 0x8c29 ebit by ZhengJH 120417
	m_pCommunication->CDT1028_WriteReg(0x015a, 0xb080); // 0xa080 edit by ZhengJH 120321
	m_pCommunication->CDT1028_WriteReg(0x0160, 0x61c2); //
	m_pCommunication->CDT1028_WriteReg(0x017e, 0xf08a); // dac_offset=11110000); //
	m_pCommunication->CDT1028_WriteReg(0x0181, 0xfd40); // lvcal_target
	m_pCommunication->CDT1028_WriteReg(0x0194, 0x0000); // 162/27*2^24
	m_pCommunication->CDT1028_WriteReg(0x0195, 0x0600); //
	m_pCommunication->CDT1028_WriteReg(0x0196, 0xa12f); // 155/27*2^24
	m_pCommunication->CDT1028_WriteReg(0x0197, 0x05bd); //
	m_pCommunication->CDT1028_WriteReg(0x01a2, 0x0004); // 
	m_pCommunication->CDT1028_WriteReg(0x01a5, 0x0004); //
	m_pCommunication->CDT1028_WriteReg(0x01a7, 0x0802);
	m_pCommunication->CDT1028_WriteReg(0x01da, 0x0489); // sk_cmp_grp_en
	m_pCommunication->CDT1028_WriteReg(0x01de, 0xffff); // sk_timer1); // sk_timer2); // sk_timer3
	m_pCommunication->CDT1028_WriteReg(0x01df, 0x0090); // 
	m_pCommunication->CDT1028_WriteReg(0x01e3, 0x2040/*0x2346*/); // noise_h_th); // noise_l_th 0x2040 0x21C3 edit by ZhengJH 120320  
	m_pCommunication->CDT1028_WriteReg(0x01e5, 0x0c18); // offset_h_th); // offset_l_th //0x0c18 edit by ZhengJH 120320 0x1E3C
	m_pCommunication->CDT1028_WriteReg(0x01e6, 0xfd05); // sk_timer4
	m_pCommunication->CDT1028_WriteReg(0x01ee, 0x081a); // afc_range_fm 0x081a edit by ZhengJH 120321

	m_pCommunication->CDT1028_WriteReg(0x0109, 0x0120);  //tmr_agc_rssi edit by ZhengJH 2012.5.16 for Y RSSI
	m_pCommunication->CDT1028_WriteReg(0x0122, 0x0001);  //bypass agc_vga_rst	

	m_pCommunication->CDT1028_WriteReg(0x01da, 0x0489); //sk_cmp_grp_en; luoyang 3/7
	m_pCommunication->CDT1028_WriteReg(0x01e2, 0x172e); 

	m_pCommunication->CDT1028_WriteReg(0x01a7, 0x0802); // 0x01a7[11:0] == 0x0112[15:4]   for agc_halt

	if(rt)
	{
		rt = CDTMTV_RT_IIC_ERROR;
	}
	return rt;
}

UINT8 CCdtOrder1028::CDT1028_LDOInit(void)
{
	UINT8 rt = CDTMTV_RT_SUCCESS;
	//uint16 reg;
	//item: initial_RF
	rt = 
		m_pCommunication->CDT1028_WriteReg(0x001, 0x18d5); // lna_s_ibit=001
	m_pCommunication->CDT1028_WriteReg(0x003, 0x0800); // mixer cal
	m_pCommunication->CDT1028_WriteReg(0x004, 0x1820); // mixer cal
	m_pCommunication->CDT1028_WriteReg(0x005, 0x0820); // mixer cal
	m_pCommunication->CDT1028_WriteReg(0x006, 0x007f); // mixer ibit
	m_pCommunication->CDT1028_WriteReg(0x007, 0x0fff); // mixer cal
	m_pCommunication->CDT1028_WriteReg(0x008, 0x0008); // filter
	m_pCommunication->CDT1028_WriteReg(0x00D, 0x208F); // filter
	m_pCommunication->CDT1028_WriteReg(0x00E, 0x9189); // 
	//pll
	m_pCommunication->CDT1028_WriteReg(0x012, 0x2488); //
	m_pCommunication->CDT1028_WriteReg(0x013, 0x4440); //
	m_pCommunication->CDT1028_WriteReg(0x014, 0x0018); //
	m_pCommunication->CDT1028_WriteReg(0x015, 0x0127); //
	m_pCommunication->CDT1028_WriteReg(0x016, 0x3015); //
	m_pCommunication->CDT1028_WriteReg(0x017, 0x0222); //
	m_pCommunication->CDT1028_WriteReg(0x018, 0x54ff); //m_pCommunication->CDT1028_WriteReg(0x018, 0x14ff); edit by ZhengJH 120426 for lower Seek
	m_pCommunication->CDT1028_WriteReg(0x00c, 0x247c); // dac
	m_pCommunication->CDT1028_WriteReg(0x00f, 0x8c50); // buf
	//agc table
	m_pCommunication->CDT1028_WriteReg(0x026, 0x0000); // 
	m_pCommunication->CDT1028_WriteReg(0x029, 0x137f); // i2v_gain
	m_pCommunication->CDT1028_WriteReg(0x02A, 0xf731); // i2v_cap 0x7310 edit by ZhengJH 120426 for lower Seek
	m_pCommunication->CDT1028_WriteReg(0x02B, 0x03ff); // filter_gain
	m_pCommunication->CDT1028_WriteReg(0x02c, 0x1111); // adc
	m_pCommunication->CDT1028_WriteReg(0x02e, 0x0000); // pd loop out
	m_pCommunication->CDT1028_WriteReg(0x02F, 0x0060); /*0060: AUDIO, 0040: SIF*/// pd_lna_s=0 
	m_pCommunication->CDT1028_WriteReg(0x035, 0x6978); // xtal=27M
	m_pCommunication->CDT1028_WriteReg(0x036, 0x07B0); 
	m_pCommunication->CDT1028_WriteReg(0x037, 0x15CA); // LNA_THD_2=375

	if(rt)
	{
		rt = CDTMTV_RT_IIC_ERROR;
	}
	return rt;
}
UINT8 CCdtOrder1028::CDT1028_Tuning(void)
{
	UINT8 rt = CDTMTV_RT_SUCCESS;

	//;============================================
	//item: initial_DIG_tuning
	rt = 
		m_pCommunication->CDT1028_WriteReg(0x0106, 0x8070); // blind_seek_en=0); //
	m_pCommunication->CDT1028_WriteReg(0x010d, 0x2000); // ysnr_sk_bypass=0); //
	m_pCommunication->CDT1028_WriteReg(0x012c, 0x0204); // 
	m_pCommunication->CDT1028_WriteReg(0x013b, 0x0400); // 
	m_pCommunication->CDT1028_WriteReg(0x0140, 0x10c0); // 
	m_pCommunication->CDT1028_WriteReg(0x0141, 0x00a0); //
	m_pCommunication->CDT1028_WriteReg(0x0142, 0x080a); //
	m_pCommunication->CDT1028_WriteReg(0x0143, 0x4488); //
	m_pCommunication->CDT1028_WriteReg(0x0144, 0x0000); //
	m_pCommunication->CDT1028_WriteReg(0x0150, 0x08a8); //
	m_pCommunication->CDT1028_WriteReg(0x0151, 0x0365); //
	m_pCommunication->CDT1028_WriteReg(0x0152, 0x0666); //
	m_pCommunication->CDT1028_WriteReg(0x0154, 0x2913); //
	m_pCommunication->CDT1028_WriteReg(0x0159, 0x8080); //
	m_pCommunication->CDT1028_WriteReg(0x016f, 0x6180); //
	m_pCommunication->CDT1028_WriteReg(0x0183, 0x9180);//m_pCommunication->CDT1028_WriteReg(0x0183, 0x90C0); // clip_target
	m_pCommunication->CDT1028_WriteReg(0x01c4, 0x3da1); //

	return rt;
}
void CCdtOrder1028::CDT1028_SetSystemMode(UINT8 SystemMode)//������ʽ��һ��Ҫ����дƵ�㣬������Ϊ��Ƶ�ͱ�Ƶֵ��ͬ���ͼ����
{
	m_SystemMode = SystemMode;
	switch(m_SystemMode)
	{
	case CDTMTV_VSTD_M:
		CDT1028_Mode_M_27M();
		break;

	case CDTMTV_VSTD_M_X:
		CDT1028_Mode_M_X_27M();
		break;

	case CDTMTV_VSTD_N:
		CDT1028_Mode_N_27M();
		break;

	case CDTMTV_VSTD_N_X:
		CDT1028_Mode_N_X_27M();
		break;

	case CDTMTV_VSTD_BG:
		CDT1028_Mode_BG_27M();
		break;

	case CDTMTV_VSTD_BG_X:
		CDT1028_Mode_BG_X_27M();
		break;

	case CDTMTV_VSTD_I:
		CDT1028_Mode_I_27M();
		break;

	case CDTMTV_VSTD_I_X:
		CDT1028_Mode_I_X_27M();
		break;

	case CDTMTV_VSTD_DK:
		CDT1028_Mode_DK_27M();
		break;

	case CDTMTV_VSTD_DK_X:
		CDT1028_Mode_DK_X_27M();
		break;

	case CDTMTV_VSTD_FM:
		//CDT1028_FM_Init();
		break;

	default:
		break;
	}

	CDT1028_SetFreqOffset(m_SystemMode);//��������Ƶƫ
}
/* Init */
void CCdtOrder1028::ATVSetRxOn_Tuning(void)
{
	//RXON
	m_pCommunication->CDT1028_WriteReg(0x030, 0x0026);
	m_pCommunication->CDT1028_WriteReg(0x111, 0x12FF); //LNA FAST MODE
	m_pCommunication->CDT1028_WriteReg(0x113, 0x1DFF);
	m_pCommunication->CDT1028_WriteReg(0x118, 0x03CC);
	m_pCommunication->CDT1028_WriteReg(0x132, 0x3044);
	m_pCommunication->CDT1028_WriteReg(0x134, 0x112C);
	DELAY_1ms(5*3);//Delay(Delay5ms);//cdt_delay_ms(5);
	m_pCommunication->CDT1028_WriteReg(0x030, 0x002e);
	DELAY_1ms(5*3);//Delay(Delay5ms);//cdt_delay_ms(5);

	//AGC_LNA_RESET
	m_pCommunication->CDT1028_WriteReg(0x110, 0x3424);
	//AGC_I2V_RESET
	m_pCommunication->CDT1028_WriteReg(0x117, 0x0804);	
	//DSP RESET
	m_pCommunication->CDT1028_WriteReg(0x101, 0x0000);
	DELAY_1ms(5*3);//Delay(Delay5ms);//cdt_delay_ms(5);
	m_pCommunication->CDT1028_WriteReg(0x110, 0x3024); 
	m_pCommunication->CDT1028_WriteReg(0x117, 0x0800); 
	m_pCommunication->CDT1028_WriteReg(0x101, 0x0001); 
	DELAY_1ms(5*3);//Delay(Delay5ms);//cdt_delay_ms(25);
	m_pCommunication->CDT1028_WriteReg(0x111, 0x16FF); //LNA SLOW MODE
	m_pCommunication->CDT1028_WriteReg(0x113, 0x1F3C);
	m_pCommunication->CDT1028_WriteReg(0x118, 0x03CC);
	m_pCommunication->CDT1028_WriteReg(0x132, 0x304C);
	m_pCommunication->CDT1028_WriteReg(0x134, 0x1329);
}

void CCdtOrder1028::ATVSetRxOn_Seek(void)
{
	//RXON
	m_pCommunication->CDT1028_WriteReg(0x030, 0x0026);
	DELAY_1ms(2);//Delay(Delay5ms);//cdt_delay_ms(5);
	m_pCommunication->CDT1028_WriteReg(0x030, 0x002e);

	//AGC_LNA_RESET
	m_pCommunication->CDT1028_WriteReg(0x110, 0x3424);
	//AGC_I2V_RESET
	m_pCommunication->CDT1028_WriteReg(0x117, 0x0804);	
	//DSP RESET
	m_pCommunication->CDT1028_WriteReg(0x101, 0x0000);
	m_pCommunication->CDT1028_WriteReg(0x110, 0x3024); 
	m_pCommunication->CDT1028_WriteReg(0x117, 0x0200);//m_pCommunication->CDT1028_WriteReg(0x117, 0x0800); ZhengJH edit for same of labview 120502 
	m_pCommunication->CDT1028_WriteReg(0x101, 0x0001); 		

}

UINT8 CCdtOrder1028::CDT1028_GetSystemMode(void)
{
	return m_SystemMode;
}

#if 1
void CCdtOrder1028::CDT1028_SetCvbsAm(UINT16 value)
{
	UINT16 reg;
	m_pCommunication->CDT1028_WriteReg(0x0183, 0x91C0); // add by ZhengJH 120320
	//m_pCommunication->CDT1028_WriteReg(0x0184, 0x1080); // add by ZhengJH 120406
	m_pCommunication->CDT1028_ReadReg(0x0181, &reg);
	reg &= (~0x03FF);
	m_pCommunication->CDT1028_WriteReg(0x0181, reg | (value & 0x03FF));
}

void CCdtOrder1028::CDT1028_SetAmDem27M(void)
{
	UINT16 reg;

	m_pCommunication->CDT1028_WriteReg(0x0141, 0x00a1); //  afc_disable=1
	m_pCommunication->CDT1028_WriteReg(0x0156, 0x0220); // sync_en=0); //
	m_pCommunication->CDT1028_WriteReg(0x0173, 0x0b0c); //
	m_pCommunication->CDT1028_WriteReg(0x0174, 0x0b48); //
	m_pCommunication->CDT1028_WriteReg(0x0175, 0x0b84); //
	m_pCommunication->CDT1028_WriteReg(0x0176, 0x0bca); //

	m_pCommunication->CDT1028_ReadReg(0x106, &reg); 
	m_pCommunication->CDT1028_WriteReg(0x106, (reg & (~0x0800))); //bit 11 system select 27m	
}

void CCdtOrder1028::CDT1028_SetAmDem31M(void)
{
	UINT16 reg;

	m_pCommunication->CDT1028_WriteReg(0x0141, 0x00a1); //  afc_disable=1
	m_pCommunication->CDT1028_WriteReg(0x0156, 0x0220); //  sync_en=0); //
	m_pCommunication->CDT1028_WriteReg(0x0173, 0x0b80); //
	m_pCommunication->CDT1028_WriteReg(0x0174, 0x0bc0); //
	m_pCommunication->CDT1028_WriteReg(0x0175, 0x0c10); //
	m_pCommunication->CDT1028_WriteReg(0x0176, 0x0c5b); //

	m_pCommunication->CDT1028_ReadReg(0x106, &reg); 
	m_pCommunication->CDT1028_WriteReg(0x106, (reg | 0x0800)); //system select 31m
}
#endif

void CCdtOrder1028::CDT1028_SetPllDem27M(void)
{
	//atv_pll_dem_27m
	UINT16 reg;

	m_pCommunication->CDT1028_WriteReg(0x0141, 0x00a0); // 
	m_pCommunication->CDT1028_WriteReg(0x0156, 0x0260); // sync_en=1); //
	m_pCommunication->CDT1028_WriteReg(0x0173, 0x0b0c); //
	m_pCommunication->CDT1028_WriteReg(0x0174, 0x0b48); //
	m_pCommunication->CDT1028_WriteReg(0x0175, 0x0b84); //
	m_pCommunication->CDT1028_WriteReg(0x0176, 0x0bca); //

	m_pCommunication->CDT1028_ReadReg(0x106, &reg); 
	m_pCommunication->CDT1028_WriteReg(0x106, (reg & (~0x0800))); //bit 11 system select 27m
}

void CCdtOrder1028::CDT1028_SetPllDem31M(void)
{
	//atv_pll_dem_31m
	UINT16 reg;
	m_pCommunication->CDT1028_WriteReg(0x0141, 0x00a0); // 
	m_pCommunication->CDT1028_WriteReg(0x0156, 0x0260); // sync_en=1); //
	m_pCommunication->CDT1028_WriteReg(0x0173, 0x0b80); //
	m_pCommunication->CDT1028_WriteReg(0x0174, 0x0bc0); //
	m_pCommunication->CDT1028_WriteReg(0x0175, 0x0c10); //
	m_pCommunication->CDT1028_WriteReg(0x0176, 0x0c5b); //

	m_pCommunication->CDT1028_ReadReg(0x106, &reg); 
	m_pCommunication->CDT1028_WriteReg(0x106, (reg | 0x0800)); //system select 31m
}

void CCdtOrder1028::CDT1028_SetSystemMode_31M(UINT8 SystemMode)
{
	m_SystemMode = SystemMode;
	switch(m_SystemMode)
	{
	case CDTMTV_VSTD_M:
		CDT1028_Mode_M_31M();
		break;

	case CDTMTV_VSTD_M_X:
		CDT1028_Mode_M_X_31M();
		break;

	case CDTMTV_VSTD_N:
		CDT1028_Mode_N_31M();
		break;

	case CDTMTV_VSTD_N_X:
		CDT1028_Mode_N_X_31M();
		break;

	case CDTMTV_VSTD_BG:
		CDT1028_Mode_BG_31M();
		break;

	case CDTMTV_VSTD_BG_X:
		CDT1028_Mode_BG_X_31M();
		break;

	case CDTMTV_VSTD_I:
		CDT1028_Mode_I_31M();
		break;

	case CDTMTV_VSTD_I_X:
		CDT1028_Mode_I_X_31M();
		break;

	case CDTMTV_VSTD_DK:
		CDT1028_Mode_DK_31M();
		break;

	case CDTMTV_VSTD_DK_X:
		CDT1028_Mode_DK_X_31M();
		break;

	case CDTMTV_VSTD_FM:
		//CDT1028_FM_Init();
		break;

	default:
		break;
	}

	CDT1028_SetFreqOffset(m_SystemMode);//��������Ƶƫ
}

void CCdtOrder1028::CDT1028_SetBBLvl(void)
{
	UINT16 reg;
	UINT8 i;

	m_pCommunication->CDT1028_ReadReg(0x11d, &reg);
	reg = (reg >> 6)&0x3F; //AGC VGA
	if(reg >= 0x06)     //0x1a edit by hu  121214
	{
		for(i = 0; i < 10; i++)
		{
			m_pCommunication->CDT1028_ReadReg(0x182, &reg);
			DELAY_1ms(10);//cdt_delay_ms(50);
			if(!(reg & 0x0800))
				break;
		}
		if(10 == i)
		{
			reg = 0x13ff;  //103f  for congcheng xuehuadian 
		}
		else
		{
			reg = reg | 0x1000;	
		}
		m_pCommunication->CDT1028_WriteReg(0x182, reg);
		//m_pCommunication->CDT1028_WriteReg(0x17f, 0xfc7f);
	}
}

void CCdtOrder1028::CDT1028_FreqSet(UINT32 dwVFreq)
{
	UINT16 reg;
	UINT16 rega, regb;
	UINT32 dwCFreq;
	UINT8 ucSysMode;
	UINT8 ucChangeFreq = 0;

	ucSysMode = CDT1028_GetSystemMode();

	if(dwVFreq < BAND_VH_START)
	{
		m_pCommunication->CDT1028_WriteReg(0x0112, LnaTH1);
		rega = (LnaTH1 >> 4)&0x0FFF;
		m_pCommunication->CDT1028_WriteReg(0x01a7, rega);
	}
	else if((dwVFreq >= BAND_VH_START)&&(dwVFreq < BAND_U_START))
	{
		m_pCommunication->CDT1028_WriteReg(0x0112, LnaTH2);
		rega = (LnaTH2 >> 4)&0x0FFF;
		m_pCommunication->CDT1028_WriteReg(0x01a7, rega);
	}
	else
	{
		m_pCommunication->CDT1028_WriteReg(0x0112, LnaTH3 + 0x10);
		rega = (LnaTH3 >> 4)&0x0FFF;
		m_pCommunication->CDT1028_WriteReg(0x01a7, rega);
	}

	/*transduction*/
	CDT1028_SetPllDem27M();//CDT1028_SetAmDem27M(); //CDT1028_SetAmDem27M(); //CDT1028_SetPllDem27M();
	CDT1028_SetSystemMode((UINT8)ucSysMode);
	m_pCommunication->CDT1028_WriteReg(0x0160, 0x61c2); // ��Ƶ��PLLƵ��
#if 1
	switch(dwVFreq)
	{
	case 48250:
	case 49750:
	case 55250:
	case 76250:
	case 83250: //for taiwang
	case 77250:
	case 133250:
	case 144250:
	case 105250:
	case 160250:
	case 161250:
	case 184250:
		//case 189250:
	case 216250:	
	case 360250:
	case 376250:
	case 415250:
	case 535250:
	case 671250:
	case 259250:
	case 266250:
	case 89250:
	case 319250:
	case 431250:
	case 647250:
	case 208250:
	case 320250:
	case 424250:
	case 551250:
	case 751250:
		CDT1028_SetPllDem31M();//CDT1028_SetAmDem31M(); //CDT1028_SetAmDem31M(); //
		CDT1028_SetSystemMode_31M((UINT8)ucSysMode);
		ucChangeFreq = 1;
		switch(ucSysMode)
		{
		case CDTMTV_VSTD_M:
		case CDTMTV_VSTD_BG_X:
		case CDTMTV_VSTD_DK_X:
		case CDTMTV_VSTD_I_X:
		case CDTMTV_VSTD_N:
			m_pCommunication->CDT1028_WriteReg(0x0160, 0x61ca);
		}

		break;
	default:
		break;
	}
#endif

	//notch filter
	CDT1028_NotchDisabel();
	switch(dwVFreq)
	{
	case 49750:
		CDT1028_NotchHarm(0);
		CDT1028_Notch(1, 4250);			
		break;

	case 77250:
	case 320250:	
		CDT1028_NotchHarm(0);
		CDT1028_Notch(0, 3750);				
		break;

	case 160250:
		CDT1028_NotchHarm(0);
		CDT1028_Notch(0, 1750);				
		break;

	case 76250:
	case 184250:
		CDT1028_NotchHarm(0);
		CDT1028_Notch(1, 4750);
		//m_pCommunication->CDT1028_WriteReg(0x0146, 0x0339);
		break;

	case 48250:
		CDT1028_NotchHarm(0);
		CDT1028_Notch(0, 5750);			
		break;

	case 105250:
		CDT1028_NotchHarm(0);
		CDT1028_Notch(1, 2750);				
		break;

	case 133250:
		CDT1028_NotchHarm(0);
		CDT1028_Notch(1, 1750);			
		break;

	case 161250:
		CDT1028_NotchHarm(0);
		CDT1028_Notch(1, 750);		
		break;

	default:
		break;
	}

	dwCFreq = dwVFreq + m_nFreqOffset; // convert to center carrier freq.
	dwCFreq = (dwCFreq << 10);	//set center freq
	regb = (UINT16)((dwCFreq >> 16)&0x0000ffff);//regb = (uint16)((dwCFreq&0xffff0000)>>16);
	rega = (UINT16)((dwCFreq&0x0000ffff));

	m_pCommunication->CDT1028_WriteReg(0x024, rega); //m_pCommunication->CDT1028_WriteReg(0x024, uwTData);
	DELAY_1ms(1);//Delay(Delay1ms);//cdt_delay_ms(1);
	m_pCommunication->CDT1028_WriteReg(0x023, regb);		

	m_pCommunication->CDT1028_WriteReg(0x0182, 0x0000); //m_pCommunication->CDT1028_WriteReg(0x010C, 0x0000); //m_pCommunication->CDT1028_WriteReg(0x002F, 0x0060); //open cvbs
	ATVSetRxOn_Tuning();

	DELAY_1ms(8);//Delay(Delay20ms);//cdt_delay_ms(20);
	CDT1028_SetBBLvl();
	if(dwVFreq==551250)
	{
		DELAY_1ms(8);//Delay(Delay20ms);//cdt_delay_ms(20);
		reg=0;
		m_pCommunication->CDT1028_ReadReg(0x182, &reg);
		if((reg&0x1000)==0)
		{
			reg = reg | 0x1000;
			m_pCommunication->CDT1028_WriteReg(0x182, reg);
		}
	}
}

void CCdtOrder1028::CDT1028_GetLnaTH(void)
{
	CDT1028_LDOInit();
	CDT1028_DSPInit();
	CDT1028_Tuning();
	CDT1028_SetSystemMode(CDTMTV_VSTD_DK);
	CDT1028_FreqSet(43250);

	LnaTH1 = LnaTH2 =LnaTH3 = 0;

	m_pCommunication->CDT1028_WriteReg(0x02F, 0x4060); //put down lna_Pd

	m_pCommunication->CDT1028_WriteReg(0x001, 0x18DE); // select lnath1
	DELAY_1ms(10);//Delay(Delay50ms);//cdt_delay_ms(35); //cdt_delay_ms(5);
	m_pCommunication->CDT1028_ReadReg(0x116, &LnaTH1);

	m_pCommunication->CDT1028_WriteReg(0x001, 0x18DD); // select lnath2
	DELAY_1ms(10);//Delay(Delay50ms);//cdt_delay_ms(35); //cdt_delay_ms(5);
	m_pCommunication->CDT1028_ReadReg(0x116, &LnaTH2);	

	m_pCommunication->CDT1028_WriteReg(0x001, 0x18DB); // select lnath3
	DELAY_1ms(10);//Delay(Delay50ms);//cdt_delay_ms(35); //cdt_delay_ms(5);
	m_pCommunication->CDT1028_ReadReg(0x116, &LnaTH3);
	LnaTH1 += STANDARD_LNATH - 0x10;
	LnaTH2 += STANDARD_LNATH - 0x10;
	LnaTH3 += STANDARD_LNATH;

	LOGI("CCdtOrder1028::CDT1028_GetLnaTH:lnath1:%x, lnath2:%d,lnath3:%d --\r\n", LnaTH1, LnaTH2, LnaTH3);

	m_pCommunication->CDT1028_WriteReg(0x02F, 0x0060); //put on lna_Pd
	m_pCommunication->CDT1028_WriteReg(0x001, 0x4A53); //m_pCommunication->CDT1028_WriteReg(0x001, 0x18D3);  edit by ZhengJH 2012.6.28 for sensitivity and block

	/*
	unsigned short int value = 0;
	m_pCommunication->CDT1028_ReadReg(0x02F, &value);
	LOGI("Get reg 0x02f : %x" , value);
	m_pCommunication->CDT1028_ReadReg(0x001, &value);
	LOGI("Get reg 0x001 : %x" , value);
	*/
}

UINT8 CCdtOrder1028::CDT1028_SetPDN(UINT8 On)
{
	UINT8 rt = CDTMTV_RT_SUCCESS;

	{
		m_pCommunication->CDT1028_WriteReg(0x0FF, 0x0000);// PDN
		m_pCommunication->m_cReqMap = CDT1028_PAGE_0;
	}

	if(On)
	{
		rt = m_pCommunication->CDT1028_WriteReg(0x030, 0x0022);// PDN  
		DELAY_1ms(4);//Delay(Delay10ms);//cdt_delay_ms(10);
		m_pCommunication->CDT1028_WriteReg(0x00E, 0x9189);  //pll cal 
		//pll switch on
		m_pCommunication->CDT1028_WriteReg(0x02E, 0x0042); 
		m_pCommunication->CDT1028_WriteReg(0x02E, 0x0040);

		//chip_cal
		m_pCommunication->CDT1028_WriteReg(0x030, 0x0026);
	}
	else
	{
		rt = m_pCommunication->CDT1028_WriteReg(0x030, 0x0000); 
	}
	return rt;
}

UINT8 CCdtOrder1028::CDT1028_Seek(void)
{
	UINT8 rt = CDTMTV_RT_SUCCESS;

	rt =
		//;============================================
		//item: initial_DIG_SEEK
		m_pCommunication->CDT1028_WriteReg(0x0106, 0x8470); // blind_seek_en=1); //
	m_pCommunication->CDT1028_WriteReg(0x010a, 0x001a); //
	m_pCommunication->CDT1028_WriteReg(0x010d, AGC_VGA_TH); //
	m_pCommunication->CDT1028_WriteReg(0x012c, 0x0a04); // 
	//m_pCommunication->CDT1028_WriteReg(0x0134, 0x0221); //
	m_pCommunication->CDT1028_WriteReg(0x013b, 0x0410); // 
	m_pCommunication->CDT1028_WriteReg(0x0140, 0x1000); //m_pCommunication->CDT1028_WriteReg(0x0140, 0x1080); // field_hold_mixer_en=0); // line_hold_dpll_en=0); // bit 7: afc_hold_rssi_en 0x1080
	m_pCommunication->CDT1028_WriteReg(0x0141, 0x08a0); //
	m_pCommunication->CDT1028_WriteReg(0x0142, 0xb802); //m_pCommunication->CDT1028_WriteReg(0x0142, 0x2802); // afc_range=000 //[4:2]: afc_range 0x2802 by ZhengJH 120320 for Y_afc
	m_pCommunication->CDT1028_WriteReg(0x0143, 0x4646); //m_pCommunication->CDT1028_WriteReg(0x0143, 0x4466); // afc_lpf_u_ct1=0110); // afc_acc_u_ct1=0110); //
	m_pCommunication->CDT1028_WriteReg(0x0144, 0xaf68); // angle_in_vsb_sk_reg, 2.5m
	m_pCommunication->CDT1028_WriteReg(0x0150, 0x0854); //
	m_pCommunication->CDT1028_WriteReg(0x0151, 0x03af); //
	m_pCommunication->CDT1028_WriteReg(0x0152, 0x01aa); //
	m_pCommunication->CDT1028_WriteReg(0x0154, 0x3d13); // lpfil_iir_afc_bw_ct=110); // lpfil_iir_dpll_bw_ct=110); //
	m_pCommunication->CDT1028_WriteReg(0x0159, 0x7f7f); // sk_tmr_afc); // sk_tmr_rssi
	m_pCommunication->CDT1028_WriteReg(0x016f, 0x20ff); //0x08ff edit by ZhengJH 120426 for lower Seek
	m_pCommunication->CDT1028_WriteReg(0x0183, 0x9140); // clip_target
	m_pCommunication->CDT1028_WriteReg(0x01c4, 0x4bda); // angle_in_fm_sk_reg, 4.0m
	m_pCommunication->CDT1028_WriteReg(0x01de, 0x4204); //
	m_pCommunication->CDT1028_WriteReg(0x01e6, 0x1105); //

	return rt;
}

void CCdtOrder1028::CDT1028_ScanFreqSet(UINT32 dwVFreq)
{
	UINT16 rega, regb;
	UINT32 dwCFreq;

	if(dwVFreq < BAND_VH_START)
	{
		m_pCommunication->CDT1028_WriteReg(0x0112, LnaTH1);
		rega = (LnaTH1 >> 4)&0x0FFF;
		m_pCommunication->CDT1028_WriteReg(0x01a7, rega);
	}
	else if((dwVFreq >= BAND_VH_START)&&(dwVFreq < BAND_U_START))
	{
		m_pCommunication->CDT1028_WriteReg(0x0112, LnaTH2);
		rega = (LnaTH2 >> 4)&0x0FFF;
		m_pCommunication->CDT1028_WriteReg(0x01a7, rega);
	}
	else
	{
		m_pCommunication->CDT1028_WriteReg(0x0112, LnaTH3 + 0x10);
		rega = (LnaTH3 >> 4)&0x0FFF;
		m_pCommunication->CDT1028_WriteReg(0x01a7, rega);
	}

	dwCFreq = dwVFreq + 2500; // convert to center carrier freq.
	//set center freq
	dwCFreq = (dwCFreq << 10);
	regb = (UINT16)((dwCFreq>>16)&0xffff);
	rega = (UINT16)((dwCFreq&0x0000ffff));
	//rega|=0x0004;
	m_pCommunication->CDT1028_WriteReg(0x024, rega);
	m_pCommunication->CDT1028_WriteReg(0x023, regb);

	ATVSetRxOn_Seek();	
	//return rt;
}

void CCdtOrder1028::CDT1028_ScanFreqSet2(UINT32 dwVFreq)
{
	UINT32 dwCFreq;
	UINT16 regb, rega;
	dwCFreq = dwVFreq + m_nFreqOffset; // convert to center carrier freq.
	dwCFreq = (dwCFreq << 10);	//set center freq
	regb = (UINT16)((dwCFreq >> 16)&0x0000ffff);//regb = (uint16)((dwCFreq&0xffff0000)>>16);
	rega = (UINT16)((dwCFreq&0x0000ffff));
	m_pCommunication->CDT1028_WriteReg(0x024, rega); //m_pCommunication->CDT1028_WriteReg(0x024, uwTData);
	DELAY_1ms(1);//Delay(Delay1ms);//cdt_delay_ms(1);
	m_pCommunication->CDT1028_WriteReg(0x023, regb);
	ATVSetRxOn_Seek();
}

UINT8 CCdtOrder1028::CDT1028_ScanChMode(void)
{
	UINT16 reg;
	m_pCommunication->CDT1028_WriteReg(0x102, 0x00);
	m_pCommunication->CDT1028_WriteReg(0x102, 0x01);
	DELAY_1ms(50);//Delay(Delay200ms);//cdt_delay_ms(250);
	DELAY_1ms(120);
	m_pCommunication->CDT1028_ReadReg(0x102, &reg);
	if(0x06 == (reg&0x06))
		goto H_NOISE_DONE;
	m_pCommunication->CDT1028_ReadReg(0x1d1, &reg);
	if(0x0100 == (reg&0x0100))
	{
		m_pCommunication->CDT1028_ReadReg(0x1cb, &reg);
		if(((reg&0xFE00) >> 9) >= 65) //70 ���ܵ���24��������ʽ�жϴ���
			goto H_NOISE_DONE;
	}

	return CDTMTV_RT_SCAN_FAIL;

H_NOISE_DONE:
	return CDTMTV_RT_SCAN_DONE;
}

UINT8 CCdtOrder1028::CDT1028_ScanChAllMode(UINT32 dwVFreq)
{
	UINT8 status = CDTMTV_RT_SCAN_FAIL;
	UINT8 i;
	UINT16 reg;
	UINT16 reg_yrssi;
	UINT32 dwFreqTmp = dwVFreq;

#if 0	
	CDT1028_ScanFreqSet(dwVFreq);	
	for(i = 0; i <CHECK_SEEK_READY_TIMES; i++)
	{
		Delay(Delay50ms);//cdt_delay_ms(35);
		m_pCommunication->CDT1028_ReadReg(0x100, &reg);
		if(reg & 0x10)
			break;
	}

	if(i == CHECK_SEEK_READY_TIMES)
		return CDTMTV_RT_SCAN_TIMEOUT;
#endif

	m_pCommunication->CDT1028_ReadReg(0x15B, &reg_yrssi);
#if defined(__DEBUG_Cdt1028)
	LOGI("Tv reg_yrssi:%d\n",(int)reg_yrssi);
#endif

	m_pCommunication->CDT1028_ReadReg(0x100, &reg);
	if(0xF000 != (reg&0xF000))
		return CDTMTV_RT_SCAN_EMPTY_CH;
	if(0xF700 != (reg&0xF700))
		return CDTMTV_RT_SCAN_FAIL;

	//VGA_GAIN
	m_pCommunication->CDT1028_ReadReg(0x15c, &reg);
	if((reg >= 0x4BA)&&(reg <= 0xFB39))
		return CDTMTV_RT_SCAN_FAIL;
	if(((reg > 0xA9)&&(reg < 0x4BA))||((reg < 0xFF56)&&(reg > 0xFB39)))
	{		
		if(reg > 0xF000)
		{
			reg = 0xFFFF - reg;
			if(reg >= 0x445)
				dwVFreq -= 250;
			else if((reg < 0x445)&&(reg >= 0x351))
				dwVFreq -= 200;
			else if((reg < 0x351)&&(reg >= 0x25E))
				dwVFreq -= 150;
			else if((reg < 0x25E)&&(reg >= 0x16C))
				dwVFreq -= 100;
			else //if((reg < 0x16C)&&(reg >= 0xA9))
				dwVFreq -= 50;
		}
		else
		{
			if(reg >= 0x445)
				dwVFreq += 250;
			else if((reg < 0x445)&&(reg >= 0x351))
				dwVFreq += 200;
			else if((reg < 0x351)&&(reg >= 0x25E))
				dwVFreq += 150;
			else if((reg < 0x25E)&&(reg >= 0x16C))
				dwVFreq += 100;
			else //if((reg < 0x16C)&&(reg >= 0xA9)) //0x79 = 25k 0xA9 = 35k
				dwVFreq += 50;
		}

		CDT1028_ScanFreqSet(dwVFreq);
		for(i = 0; i <CHECK_SEEK_READY_TIMES; i++)
		{
			DELAY_1ms(10);//Delay(Delay50ms);//cdt_delay_ms(35);
			m_pCommunication->CDT1028_ReadReg(0x100, &reg);
			if(reg & 0x10)
				break;
		}
	}

	m_pCommunication->CDT1028_ReadReg(0x100, &reg);
	if(0xF711 ==  (reg & 0xF71F)) //��Y��FM�ж�FAIL
	{
		m_pCommunication->CDT1028_WriteReg(0x01e3, 0x2346); //m_pCommunication->CDT1028_WriteReg(0x01e3, 0x2346);
		CDT1028_ScanFreqSet(dwVFreq);
		for(i = 0; i <CHECK_SEEK_READY_TIMES; i++)
		{
			DELAY_1ms(10);//Delay(Delay50ms);//cdt_delay_ms(35);
			m_pCommunication->CDT1028_ReadReg(0x100, &reg);
			if(0x10==(reg & 0x10))
				break;
		}

		if(i == CHECK_SEEK_READY_TIMES)
			return CDTMTV_RT_SCAN_TIMEOUT;		

		m_pCommunication->CDT1028_WriteReg(0x01e3, 0x2040);
	}

	if(reg & 0x0E)
	{
		switch(((reg & 0x800) >> 9) |((reg & 0x60) >> 5))
		{
		case 0x04:
			//LOGI("PAL-DK,");
			CDT1028_ScanFreqSet(dwFreqTmp + 2000);
			for(i = 0; i <CHECK_SEEK_READY_TIMES; i++)
			{
				DELAY_1ms(10);//Delay(Delay50ms);//cdt_delay_ms(35);
				m_pCommunication->CDT1028_ReadReg(0x100, &reg);
				if(0x10==(reg & 0x10))
					break;
			}
			if(0xFF00 == (reg&0xFF00))
			{
				DELAY_1ms(80);//Delay(Delay200ms);//cdt_delay_ms(180);
				m_pCommunication->CDT1028_ReadReg(0x15b, &reg);
				if(reg > reg_yrssi)
					status = CDTMTV_RT_SCAN_FAIL;
				else
				{
					m_SystemMode = CDTMTV_VSTD_DK;
					status = CDTMTV_RT_SCAN_DONE;
				}
			}
			else
			{
				m_SystemMode = CDTMTV_VSTD_DK;
				status = CDTMTV_RT_SCAN_DONE;
			}
			break;

		case 0x00:
			//LOGI("PAL-DK-X,");
			m_SystemMode = CDTMTV_VSTD_DK_X;
			status = CDTMTV_RT_SCAN_DONE;
			break;

		case 0x05:
			//LOGI("PAL-I,");
			CDT1028_ScanFreqSet(dwFreqTmp + 1500);
			for(i = 0; i <CHECK_SEEK_READY_TIMES; i++)
			{
				DELAY_1ms(10);//Delay(Delay50ms);//cdt_delay_ms(35);
				m_pCommunication->CDT1028_ReadReg(0x100, &reg);
				if(0x10==(reg & 0x10))
					break;
			}
			if(0xFF00 == (reg&0xFF00))
			{
				m_pCommunication->CDT1028_ReadReg(0x15b, &reg);
				if(reg > reg_yrssi)
					status = CDTMTV_RT_SCAN_FAIL;
				else
				{
					m_SystemMode = CDTMTV_VSTD_I;
					status = CDTMTV_RT_SCAN_DONE;
				}
			}
			else
			{
				m_SystemMode = CDTMTV_VSTD_I;
				status = CDTMTV_RT_SCAN_DONE;
			}
			break;

		case 0x01:
			//LOGI("PAL-I-X,");
			m_SystemMode = CDTMTV_VSTD_I_X;
			status = CDTMTV_RT_SCAN_DONE;
			break;

		case 0x06:
			//LOGI("PAL-BG,");
			CDT1028_ScanFreqSet(dwFreqTmp + 1000);
			for(i = 0; i <CHECK_SEEK_READY_TIMES; i++)
			{
				DELAY_1ms(10);//Delay(Delay50ms);//cdt_delay_ms(35);
				m_pCommunication->CDT1028_ReadReg(0x100, &reg);
				if(0x10==(reg & 0x10))
					break;
			}

			if(0xFF00 == (reg&0xFF00))
			{
				m_pCommunication->CDT1028_ReadReg(0x15b, &reg);
				if(reg > reg_yrssi)
					status = CDTMTV_RT_SCAN_FAIL;
				else
				{
					m_SystemMode = CDTMTV_VSTD_BG;
					status = CDTMTV_RT_SCAN_DONE;
				}
			}
			else
			{
				m_SystemMode = CDTMTV_VSTD_BG;
				status = CDTMTV_RT_SCAN_DONE;
			}
			break;

		case 0x02:
			//LOGI("PAL-BG-X,");
			m_SystemMode = CDTMTV_VSTD_BG_X;
			status = CDTMTV_RT_SCAN_DONE;
			break;

		case 0x07:
			//LOGI("PAL-N,");
			CDT1028_ScanFreqSet(dwFreqTmp + 4500);
			for(i = 0; i <CHECK_SEEK_READY_TIMES; i++)
			{
				DELAY_1ms(10);//Delay(Delay50ms);//cdt_delay_ms(35);
				m_pCommunication->CDT1028_ReadReg(0x100, &reg);
				if(0x10==(reg & 0x10))
					break;
			}
			if(0xF700 == (reg&0xF700))
			{
				m_pCommunication->CDT1028_ReadReg(0x15b, &reg);
				if(reg > reg_yrssi)
					status = CDTMTV_RT_SCAN_FAIL;
				else
				{
					m_SystemMode = CDTMTV_VSTD_N;
					status = CDTMTV_RT_SCAN_DONE;
				}
			}
			else
			{
				m_SystemMode = CDTMTV_VSTD_N;
				status = CDTMTV_RT_SCAN_DONE;
			}
			break;

		case 0x03:
			//LOGI("PAL-M,");
			CDT1028_ScanFreqSet(dwFreqTmp + 4500);
			for(i = 0; i <CHECK_SEEK_READY_TIMES; i++)
			{
				DELAY_1ms(10);//Delay(Delay50ms);//cdt_delay_ms(35);
				m_pCommunication->CDT1028_ReadReg(0x100, &reg);
				if(0x10==(reg & 0x10))
					break;
			}
			if(0xF700 == (reg&0xF700))
			{
				m_pCommunication->CDT1028_ReadReg(0x15b, &reg);
				if(reg > reg_yrssi)
					status = CDTMTV_RT_SCAN_FAIL;
				else
				{
					m_SystemMode = CDTMTV_VSTD_M;
					status = CDTMTV_RT_SCAN_DONE;
				}
			}
			else
			{
				m_SystemMode = CDTMTV_VSTD_M;
				status = CDTMTV_RT_SCAN_DONE;
			}
			break;

		default:
			break;
		}
	}
	CDT1028_ScanFreqSet(dwFreqTmp);
	if(CDTMTV_RT_SCAN_DONE == status)
		return status;

	m_pCommunication->CDT1028_ReadReg(0x100, &reg);
	switch(dwFreqTmp)
	{
	case 48250:
	case 55250:	
	case 49750:	
	case 61250:
	case 62250:	
	case 103250:
	case 115250:
	case 157250:
	case 175250:    
	case 101250:
	case 189250:    
	case 210250:
	case 211250:
	case 265250:
	case 319250:
	case 355250:	
	case 359250:
	case 373250:	
	case 399250:
	case 407250:
	case 575250:
		CDT1028_Tuning();
		if(reg&0x0800)
		{
			CDT1028_SetSystemMode(CDTMTV_VSTD_BG);
			CDT1028_ScanFreqSet2(dwVFreq);
			if(CDTMTV_RT_SCAN_DONE == CDT1028_ScanChMode())
			{
				m_SystemMode = CDTMTV_VSTD_BG;
				goto SEEK_OK;
			}

			CDT1028_SetSystemMode(CDTMTV_VSTD_I);
			CDT1028_ScanFreqSet2(dwVFreq);
			if(CDTMTV_RT_SCAN_DONE == CDT1028_ScanChMode())
			{
				m_SystemMode = CDTMTV_VSTD_I;
				goto SEEK_OK;
			}

			CDT1028_SetSystemMode(CDTMTV_VSTD_DK);
			CDT1028_ScanFreqSet2(dwVFreq);
			if(CDTMTV_RT_SCAN_DONE == CDT1028_ScanChMode())
			{
				m_SystemMode = CDTMTV_VSTD_DK;
				goto SEEK_OK;
			}

			CDT1028_SetSystemMode(CDTMTV_VSTD_N);
			CDT1028_ScanFreqSet2(dwVFreq);
			if(CDTMTV_RT_SCAN_DONE == CDT1028_ScanChMode())
			{
				m_SystemMode = CDTMTV_VSTD_N;
				goto SEEK_OK;
			}
		}
		else
		{
			CDT1028_SetSystemMode(CDTMTV_VSTD_M);
			CDT1028_ScanFreqSet2(dwVFreq);
			if(CDTMTV_RT_SCAN_DONE == CDT1028_ScanChMode())
			{
				m_SystemMode = CDTMTV_VSTD_M;
				goto SEEK_OK;
			}
		}

		CDT1028_Seek();
		CDT1028_ScanFreqSet(dwFreqTmp);
		return CDTMTV_RT_SCAN_FAIL;

SEEK_OK:
		CDT1028_Seek();
		CDT1028_ScanFreqSet(dwFreqTmp);
		return CDTMTV_RT_SCAN_DONE;
		break;
	default:
		break;
	}

	return CDTMTV_RT_SCAN_FAIL;
}

UINT8 CCdtOrder1028::CDT1028_Init(void)
{
	UINT8 rt = CDTMTV_RT_SUCCESS;

	//PND and chip_cal
	CDT1028_SetPDN(1);
	CDT1028_GetLnaTH();

	CDT1028_LDOInit();
	CDT1028_DSPInit();

	if(rt)
	{
		rt = CDTMTV_RT_IIC_ERROR;
	}	
	return rt;
}

void CCdtOrder1028::printReg(UINT16 regAddr){
	UINT16 reg;
	m_pCommunication->CDT1028_ReadReg(regAddr, &reg);
	LOGI("printReg regAddr:0x%04X\n",(int)reg);
}
UINT8 CCdtOrder1028::CDT1028_ENTER_TV(void)
{
	UINT16 reg;
	/*
	int i=50;
	while(i-->0){
		//m_pCommunication->CDT1028_WriteReg(0x182, 0x5a5a + i);
		m_pCommunication->CDT1028_ReadReg(0x00+i, &reg);
		LOGI("Tv test :0x%04X\n",(int)reg);
	}
	*/
	m_pCommunication->CDT1028_ReadReg(0x00, &reg);
	LOGI("Tv Device Id:0x%04X\n",(int)reg);
	if (reg == 0x00)
	{
		return CDTMTV_RT_IIC_ERROR;
	}
	UINT8 rt =  CDTMTV_RT_SUCCESS;
	rt = CDT1028_Init();
	return rt;
}

UINT CCdtOrder1028::CDT1028_ReadReg(UINT16 RegAddr, UINT16 *Data)
{
	return m_pCommunication->CDT1028_ReadReg(RegAddr, Data);
}

#endif
