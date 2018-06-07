#include "CdtCommunication1028.h"
#define TAG "CdtCommunication1028"
#include "../Common.h"

#ifdef ATVTYPE_ADT1028
CCdtCommunication1028::CCdtCommunication1028(void)
{
	m_cReqMap = CDT1028_PAGE_0;
}

CCdtCommunication1028::~CCdtCommunication1028(void)
{
}

int CCdtCommunication1028::Read_Control_RegMap(UINT8 Reg,UINT16 *Value)
{
	UINT8 uArry[2] = {0};
	//UINT8 uRet = ATV_Read_DataWithAddressMultByte(CDT1028_CONTROL_WRITE_ADDR, uArry, 2, CDT1028_CONTROL_READ_ADDR, (SIF_BIT_T)SIF_0_BIT, Reg);
	//int uRet = i2cReadEx(fd, uArry, 2, CDT1028_CONTROL_WRITE_ADDR, CDT1028_CONTROL_READ_ADDR, Reg, 1);
	int ret = i2cRead(fd, uArry, 2, CDT1028_DEVICE_ADDR, Reg, 1);
	*Value = (((UINT16)(uArry[0])) << 8) & 0xff00;
	*Value = (*Value)|uArry[1];
	//LOGI("Read  reg %02x: %04x  ret:%d", Reg, *Value, ret);
	return ret;
}

int CCdtCommunication1028::Write_Control_RegMap(UINT8 Reg,UINT16 Value)
{
	UINT8 u8Temp[2] = {0};
	u8Temp[1] = (Value>>8)&0xff;
	u8Temp[0] = Value&0xFF;
	//return ATV_Write_DataWithAddressAndSubAddress(NULL, u8Temp, 2, CDT1028_CONTROL_WRITE_ADDR, (SIF_BIT_T)SIF_0_BIT, Reg);
	int ret =  i2cWrite(fd, u8Temp, 2, CDT1028_DEVICE_ADDR, Reg, 1);
	//LOGI("Write reg %02x: %04x  ret:%d", Reg, Value, ret);
	return ret;
}

int CCdtCommunication1028::Get_Cdt1028_ControlMap(UINT8 Reg,UINT16 *Value)
{
	UINT8 i;
	for(i = 0; i < _CDT1028_REG_MAP_ERROR_ ; i ++)
	{
		if(Read_Control_RegMap(Reg,Value) > 0)return TRUE;
		//Sleep(10);
		usleep(1000 * 10);
	}
	return 0;
}

int CCdtCommunication1028::Set_Cdt1028_ControlMap(UINT8 Reg,UINT16 Value)
{
	UINT8 i;
	for(i = 0; i < _CDT1028_REG_MAP_ERROR_ ; i ++)
	{
		if(Write_Control_RegMap(Reg,Value) > 0)return TRUE;
		//Sleep(10);
		usleep(1000 * 10);
	}
	return 0;
}

//--------------------------------------------------------------------------------------------//
//--------------------------------------------------------------------------------------------//
//--------------------------------------------------------------------------------------------//

int CCdtCommunication1028::CDT1028_WriteReg(UINT16 RegAddr, UINT16 Data)
{
	if(RegAddr < 0x100)
	{
		if(CDT1028_PAGE_0 == m_cReqMap)
			Set_Cdt1028_ControlMap((UINT8)RegAddr, Data);
		else
		{
			m_cReqMap = CDT1028_PAGE_0;
			Set_Cdt1028_ControlMap(0xff, CDT1028_PAGE_0);
			Set_Cdt1028_ControlMap((UINT8)RegAddr, Data);
		}
	}
	else
	{
		if(CDT1028_PAGE_1 == m_cReqMap)
			Set_Cdt1028_ControlMap((UINT8)(RegAddr&0xff), Data);
		else
		{
			m_cReqMap = CDT1028_PAGE_1;
			Set_Cdt1028_ControlMap(0xff, CDT1028_PAGE_1);
			Set_Cdt1028_ControlMap((UINT8)(RegAddr&0xff), Data);
		}
	}
	return CDTMTV_RT_SUCCESS;
}

int CCdtCommunication1028::CDT1028_ReadReg(UINT16 RegAddr, UINT16 *Data)
{
	if(RegAddr < 0x100)
	{
		Set_Cdt1028_ControlMap(0xff, CDT1028_PAGE_0); //page 0
		m_cReqMap = CDT1028_PAGE_0;
	}
	else
	{
		Set_Cdt1028_ControlMap(0xff, CDT1028_PAGE_1); //page 1
		m_cReqMap = CDT1028_PAGE_1;

		RegAddr = RegAddr&0xff;
	}
	Get_Cdt1028_ControlMap((UINT8)RegAddr, Data);

	return CDTMTV_RT_SUCCESS;
}

//--------------------------------------------------------------------------------------------//
#endif
