package com.can.parser.raise;

import android.os.Message;
import android.util.Log;

import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CarInfo;
import com.can.parser.DDef.CarSetting;
import com.can.parser.DDef.CompassInfo;
import com.can.parser.DDef.FuelMilInfo;
import com.can.parser.DDef.ParkAssistInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.UsbIpodInfo;
import com.can.parser.DDef.VerInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.Protocol;
import com.can.services.CanTxRxStub;

/**
 * ClassName:ReProtocol
 * 
 * @function:TODO
 * @author 睿志城协议
 * @Date:  2016-5-28 下午12:29:54
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public abstract class ReProtocol extends Protocol{

	private final String TAG = this.getClass().getName();

    final byte mHeadFlag      = (byte) 0x2e;
    final byte mHeadCursor    = (byte) 0;
    final byte mDataLenCursor = (byte) 2;
    final byte mCmdIDCursor   = (byte) 1;
    final byte mDataCursor    = (byte) 3;
    
    /**
     * 连接命令
     */
	@Override
	public byte[] connect() {
		// TODO Auto-generated method stub
		byte[] byData   = {(byte)0x01};
		byte[] byConCmd = getPacket(0x81, 0x00, byData);
		return byConCmd;
	}
	
	@Override
	public Message disconnect(boolean bconnect) {
		// TODO Auto-generated method stub
		byte[] byData = new byte[1];
		byData[0] = (byte) (bconnect ? 0x01 : 0x00);
		
		return CanTxRxStub.getTxMessage(0x81, 0x81, byData, this);
	}

	/**
     * 获取协议的最小包长
     */
	@Override
	public int getMinPacketLength() {
		// TODO Auto-generated method stub
        // 协议头长度3个字节（开始符1，长度1, 类型1）
        return mDataCursor;
	}

	@Override
	public boolean IsAck(byte[] packet) {
		// TODO Auto-generated method stub
		boolean bAck = false;
		
		if (packet[0] == (byte)0xFF || packet[0] == (byte)0xF0 || packet[0] == (byte)0xF3) {
			bAck = true;
		}
		
		return bAck;
	}
	/**
	 * 获取协议的CmId
	 */
	@Override
	public int ParseRegisterCmdId(byte[] buffer, int cursor) {
		// TODO Auto-generated method stub
		return (int) (buffer[cursor + mCmdIDCursor] & 0xFF);
	}
	/**
	 * 获取协议的SubId
	 */
	@Override
	public int parseRegisterSubId(byte[] buffer, int cursor) {
		// TODO Auto-generated method stub
		return (int) (buffer[cursor + mCmdIDCursor] & 0xFF);
	}
	/**
	 * 打包    注意：认为datas是 不 带着 CmdID 和 SubCmdID，这 simple 协议里只有一个
     * datatype，认为 subCmdID 和 cmdID 指向都是 datatype
	 */
	@Override
	public byte[] getPacket(int cmdID, int cmdSubID, byte[] datas) {
		// TODO Auto-generated method stub
		// 3 是 head + datatype + len, 1 是 checksum
        byte[] packet = new byte[3 + 1 + datas.length];

        // 填充包头
        packet[0] = mHeadFlag;

        // 填充datatype
        packet[1] = (byte) (cmdID & 0xFF);

        // 填充length
        packet[2] = (byte) (datas.length & 0xFF);

        // 填充 data section
        System.arraycopy(datas, 0, packet, mDataCursor, datas.length);

        // 计算 checksum，填充 checksum section
        packet[packet.length - 1] = (byte) (caculateCheckSum(packet, 0, packet.length) & 0xFF);

        return packet;
	}
	/**
	 * 分析数据包 是否是有效包
	 */
	@Override
	public boolean parsePack(byte[] buffer, int[] pCursor,
			int[] pRemainingLength, int[] pMinPacketLength) {
		// TODO Auto-generated method stub
		boolean bOnePacket = false;
        final int iEnd = pCursor[0] + pRemainingLength[0];

        // 有效包开始
        while (pCursor[0] < iEnd) {
            if (isPacketHeader(buffer, pCursor[0])) {
                pMinPacketLength[0] = parsePacketLength(buffer, pCursor[0]);
                if (pRemainingLength[0] >= pMinPacketLength[0]) {
                    // 有一包数据
                    if (isValidPacket(buffer, pCursor[0], pMinPacketLength[0])) {
                        // 有效包
                        pCursor[0] += pMinPacketLength[0];
                        pRemainingLength[0] -= pMinPacketLength[0];
                        bOnePacket = true;
                        break;
                    } else {
                        // 无效包，忽略无效包，继续分析
                        pCursor[0] += pMinPacketLength[0];
                        pRemainingLength[0] -= pMinPacketLength[0];
                        Log.e(TAG, "parsePack: invalid packet, error: " + pCursor[0]);
                    }
                } else {
                    // 等收到完整包后再做分析
                    // minPacketLength = packetLen;
                    break;
                }
            } else {
                // 这里有不完整数据
                pCursor[0]++;
                pRemainingLength[0]--;
                Log.e(TAG, "parsePack: " + pCursor[0] + " miss data, error!!!");
            }
        }

        return bOnePacket;
	}
	/**
	 * 获取数据游标
	 */
	@Override
	public int getDataCursor() {
		// TODO Auto-generated method stub
        // 协议里定好就是 3
        return mDataCursor;
	}
	/**
	 * 获取CmdId游标
	 */
	@Override
	public int getCmdIDCursor() {
		// TODO Auto-generated method stub
        // 协议里定好就是 1
        return mCmdIDCursor;
	}
	/**
	 * 获取SubId游标
	 */
	@Override
	public int getCmdSubIDCursor() {
		// TODO Auto-generated method stub
        // 协议CmdID 与 CmdSub是一样
        return mCmdIDCursor;
	}
	/**
	 * 获取校验
	 */
	@Override
	public int getCheckSum(byte[] buffer, int cursor, int packetLength) {
		// TODO Auto-generated method stub
        // 包的最后个字节
        return (int) (buffer[cursor + packetLength - 1] & 0xFF);
	}
	/**
	 * 是否是包头 验证数据包的开始
	 */
	@Override
	public boolean isPacketHeader(byte[] buffer, int cursor) {
		// TODO Auto-generated method stub
		return (buffer[mHeadCursor + cursor] == mHeadFlag);
	}
	/**
	 * 是否是完整包 长度是否是协议定义的长度
	 */
	@Override
	public boolean isWholePacket(byte[] buffer, int cursor, int length) {
		// TODO Auto-generated method stub
		return length >= parsePacketLength(buffer, cursor);
	}
	/**
	 * 计算包长
	 */
	@Override
	public int parsePacketLength(byte[] buffer, int cursor) {
		// TODO Auto-generated method stub
        int packetLength = parseDataLength(buffer, cursor);

        // 4 是 Head code + Data type + Lenght + CheckSum
        packetLength += 4;

        return packetLength;
	}
	/**
	 * 计算数据长度
	 */
	@Override
	public int parseDataLength(byte[] buffer, int cursor) {
		// TODO Auto-generated method stub
        int dataLength = (int) (buffer[mDataLenCursor + cursor] & 0xFF);
        return dataLength;
	}
	/**
	 * 计算校验
	 */
	@Override
	public int caculateCheckSum(byte[] buffer, int cursor, int packetLength) {
		// TODO Auto-generated method stub
        // 一个有效包的开始 Header(占一字节)+ DataType + byLength + Data1 + Data2 + …
        // + DataN + Checksum
        // 计算当前包数据长度 byLength 是从 Data1 到 DataN 的所有数据的字节数之和
        // 计算checksum 从DataType（包含）开始到DataN（包含）
        // CheckSum = SUM(DataType + length + Data0 + ... + DataN)
        int iCheckSum = 0;
        int iDataEnd = packetLength - 2;
        int i = 0;

        iDataEnd += cursor;
        
        for (i = mCmdIDCursor + cursor; i <= iDataEnd; i++) {
            iCheckSum += buffer[i];
        }

        iCheckSum ^= 0xFF;

        return (int) (iCheckSum & 0xFF);
	}
	/**
	 * 是否为有效包
	 */
	@Override
	public boolean isValidPacket(byte[] buffer, int cursor, int packetLength) {
		// TODO Auto-generated method stub
        int rcvCheckSum = getCheckSum(buffer, cursor, packetLength);
        int calCheckSum = caculateCheckSum(buffer, cursor, packetLength);

        return rcvCheckSum == calCheckSum;
	}
	
	/**
     * 
    * @Title:GetxxxxCmdId parsexxxxInfo
    * @Description: 
    * @Condition: 获取CMD ID 功能协议解析
    *
    */
    public int GetAirInfoCmdId(){ 
    	return 0; 
    }
    
    public int GetCarInfoCmdId(){ 
    	return 0;
    }
    
    public int GetCarInfoRequestCmdId(){ 
    	return 0; 
    }
    
    public int GetBackRadarInfoCmdId(){ 
    	return 0; 
    }
    public int GetFrontRadarInfoCmdId(){ 
    	return 0; 
    }
    
    public int GetBaseInfoCmdId(){ 
    	return 0; 
    }
    
    public int GetParkAssistCmdId(){ 
    	return 0; 
    }
    
    public int GetVersionInfoCmdId(){ 
    	return 0; 
    }
    
    public int GetWheelInfoCmdId(){ 
    	return 0; 
    }
    
    public int GetWheelKeyInfoCmdId(){ 
    	return 0; 
    }
    
    public int GetBackLightInfoCmdId(){ 
    	return 0; 
    }
    
    public int GetMediaSourceCmdId(){ 
    	return 0; 
    }
    
    public int GetPhoneActionCmdId(){ 
    	return 0; 
    }
    
    public int TranslateKey(byte byKeyCode){ 
    	return 0; 
    }
    
    public byte TranslateSource(int iSource){ 
    	return 0; 
    }
    
    //车辆信息
  	public CarInfo parseCarInfo(byte[] packet, CarInfo carInfo){ 
  		return null; 
  	}
  	
  	//空调信息
  	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo){ 
  		return null; 
  	}
  	
  	//雷达信息
  	public RadarInfo parseRadarInfo(byte[] packet,RadarInfo radarInfo){ 
  		return null; 
  	}
  	
  	//基本信息
  	public BaseInfo parseBaseInfo(byte[] packet, BaseInfo baseInfo){ 
  		return null; 
  	}
  	
  	//泊车辅助
  	public ParkAssistInfo parseParkAssistantInfo(byte[] packet){ 
  		return null; 
  	}
  	
  	//版本信息
  	public VerInfo parseVersionInfo(byte[] packet){ 
  		return null; 
  	}
  	
  	//转角信息
  	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo){ 
  		return null; 
  	}
  	
  	//方向盘按键信息
  	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo){ 
  		return null; 
  	}
  	
  	//背光信息
  	public BackLightInfo parseBackLightInfo(byte[] packet, BackLightInfo backLightInfo){ 
  		return null; 
  	}
	
  	//油耗里程信息
  	public FuelMilInfo parseFuelMilInfo(byte[] packet, FuelMilInfo fuelMilInfo){
		return null;
	}
  	
  	//指南针信息
  	public CompassInfo parseCompassInfo(byte[] packet, CompassInfo compassInfo){
		return null;
	}
  	
  	//原车设置
  	public CarSetting parseCarSettingInfo(byte[] packet, CarSetting carSetting){
		return null;
	}
  	
  	//UsbIpod
  	public UsbIpodInfo parseUsbIpodInfo(byte[] packet, UsbIpodInfo usbIpodInfo) {
  		return null;
  	}
}
