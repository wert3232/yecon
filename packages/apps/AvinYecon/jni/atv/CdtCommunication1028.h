
#ifndef __CCdtCommunication1028_H__
#define __CCdtCommunication1028_H__
#include "AtvDef.h"
#include "../I2c.h"

#ifdef ATVTYPE_ADT1028
//#include "i2c.h"

//extern UINT8 ATV_Read_DataWithAddressOneByte(HANDLE hI2cDevice,unsigned char* pdata,UINT8 len, UINT8 address, SIF_BIT_T mode,  UINT8 u1SubAddress);
//extern UINT8 ATV_Write_DataWithAddressAndSubAddress(HANDLE hI2cDevice,unsigned char* pdata,UINT8 len, UINT8 address, SIF_BIT_T mode, UINT8 u1SubAddress);
//extern UINT8 ATV_Read_DataWithAddressMultByte(UINT8 wAddress,unsigned char* pdata,UINT8 len, UINT8 address, SIF_BIT_T mode,  UINT8 u1SubAddress);
extern int i2cRead(unsigned char *buf, int len, unsigned slave_address, unsigned reg_address, int reg_bytes);
extern int i2cWrite(unsigned char *buf, int len, unsigned slave_address, unsigned reg_address, int reg_bytes);
class CCdtCommunication1028
{
public:
	CCdtCommunication1028(void);
	virtual ~CCdtCommunication1028(void);

	int init(){
		fd = i2cOpen(DEVICE_NAME, CDT1028_DEVICE_ADDR);
		return fd;
	}
	void deinit(){
		i2cClose(fd);
		fd = -1;
	}
	int CDT1028_ReadReg(UINT16 RegAddr, UINT16 *Data);
	int CDT1028_WriteReg(UINT16 RegAddr, UINT16 Data);

	int Read_Control_RegMap(UINT8 Reg,UINT16 *Value);
	int Write_Control_RegMap(UINT8 Reg,UINT16 Value);

	int Get_Cdt1028_ControlMap(UINT8 Reg,UINT16 *Value);
	int Set_Cdt1028_ControlMap(UINT8 Reg,UINT16 Value);

public:
	UINT8 m_cReqMap;
	int fd;
};

#endif
#endif
