package com.can.parser;

import java.util.ArrayList;

import com.can.assist.GdSharedData.MediaInfo;
import android.os.Message;

/**
 * ClassName:Protocol
 * 
 * @function:协议格式类
 * @author Kim
 * @Date:  2016-5-27 上午11:04:42
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public abstract class Protocol {
	/**
	 * 
	* <p>Title: connect</p>
	* <p>Description: 连接Can盒</p>
	* @return
	 */
	public abstract byte[] connect();
	/**
	 * 
	 * 	* <p>Title: disconnect</p>
	 * <p>Description: 断开连接Can盒</p>
	 * @return
	 */
	public abstract Message disconnect(boolean bconnect);
	/**
	 * 
	* <p>Title: getDataCursor</p>
	* <p>Description: 获取数据位的游标</p>
	* @return
	 */
    public abstract int getDataCursor();
    /**
     * 
    * <p>Title: getCmdIDCursor</p>
    * <p>Description: 获取CmdId的游标</p>
    * @return
     */
    public abstract int getCmdIDCursor();
    /**
     * 
    * <p>Title: getCmdSubIDCursor</p>
    * <p>Description: 获取SubId的游标</p>
    * @return
     */
    public abstract int getCmdSubIDCursor();
    /**
     * 
    * <p>Title: getCheckSum</p>
    * <p>Description: 获取校验</p>
    * @param buffer
    * @param cursor
    * @param packetLength
    * @return
     */
    public abstract int getCheckSum(byte[] buffer, int cursor, int packetLength);
    /**
     * 
    * <p>Title: isPacketHeader</p>
    * <p>Description: 是否是包头</p>
    * @param buffer
    * @param cursor
    * @return
     */
    public abstract boolean isPacketHeader(byte[] buffer, int cursor);
    /**
     * 
    * <p>Title: isWholePacket</p>
    * <p>Description: 是否是完整包</p>
    * @param buffer
    * @param cursor
    * @param length
    * @return
     */
    public abstract boolean isWholePacket(byte[] buffer, int cursor, int length);
    /**
     * 
    * <p>Title: parsePacketLength</p>
    * <p>Description: 解析包长</p>
    * @param buffer
    * @param cursor
    * @return
     */
    public abstract int parsePacketLength(byte[] buffer, int cursor);
    /**
     * 
    * <p>Title: parseDataLength</p>
    * <p>Description: 解析数据长</p>
    * @param buffer
    * @param cursor
    * @return
     */
    public abstract int parseDataLength(byte[] buffer, int cursor);
    /**
     * 
    * <p>Title: getMinPacketLength</p>
    * <p>Description: 获取最小包长</p>
    * @return
     */
    public abstract int getMinPacketLength();
    /**
     * 
    * <p>Title: ParseRegisterCmdId</p>
    * <p>Description: 分析注册的CmdId</p>
    * @param buffer
    * @param cursor
    * @return
     */
    public abstract int ParseRegisterCmdId(byte[] buffer, int cursor);
    /**
     * 
    * <p>Title: parseRegisterSubId</p>
    * <p>Description: 分析注册的SubId</p>
    * @param buffer
    * @param cursor
    * @return
     */
    public abstract int parseRegisterSubId(byte[] buffer, int cursor);
    /**
     * 
    * <p>Title: getPacket</p>
    * <p>Description: 打包</p>
    * @param cmdID
    * @param cmdSubID
    * @param datas
    * @return
     */
    public abstract byte[] getPacket(int cmdID, int cmdSubID, byte[] datas);
    /**
     * 
    * <p>Title: parsePack</p>
    * <p>Description: 解析数据包</p>
    * @param buffer
    * @param pCursor
    * @param pRemainingLength
    * @param pMinPacketLength
    * @return
     */
    public abstract boolean parsePack(byte[] buffer, int[] pCursor, int[] pRemainingLength,
            int[] pMinPacketLength);
    /**
     * 
    * <p>Title: caculateCheckSum</p>
    * <p>Description: 计算校验</p>
    * @param buffer
    * @param cursor
    * @param packetLength
    * @return
     */
    public abstract int caculateCheckSum(byte[] buffer, int cursor, int packetLength);
    /**
     * 
    * <p>Title: isValidPacket</p>
    * <p>Description: 是否是有效包</p>
    * @param buffer
    * @param cursor
    * @param packetLength
    * @return
     */
    public abstract boolean isValidPacket(byte[] buffer, int cursor, int packetLength);
    /**
     * 
    * <p>Title: IsAck</p>
    * <p>Description: 是否是Ack</p>
    * @param packet
    * @return
     */
    public abstract boolean IsAck(byte[] packet);
    /**
     * 
    * <p>Title: getPacketType</p>
    * <p>Description: 获取包的类型</p>
    * @param packet
    * @return
     */
    public byte getPacketType(byte[] packet) {
        return (byte) 0;
    };
	/**
	 * 
	* <p>Title: setPacketType</p>
	* <p>Description: 设置包的类型</p>
	* @param packet
	* @param byPacketType
	 */
    public void setPacketType(byte[] packet, byte byPacketType) {
        return;
    };
    /**
     * 
    * <p>Title: getRepeatTimes</p>
    * <p>Description: 获取重发时间</p>
    * @param packet
    * @return
     */
    public byte getRepeatTimes(byte[] packet) {
        return (byte) 0;
    };
    /**
     * 
    * <p>Title: setRepeatTimes</p>
    * <p>Description: 设置重发时间</p>
    * @param packet
    * @param repeatTimes
     */
    public void setRepeatTimes(byte[] packet, byte repeatTimes) {
        return;
    };
    /**
     * 
    * <p>Title: getPacketId</p>
    * <p>Description: 获取包的Id</p>
    * @param packet
    * @return
     */
    public int getPacketId(byte[] packet) {
        return (byte) 0;
    };
    /**
     * 
    * <p>Title: setPacketId</p>
    * <p>Description: 设置包的Id</p>
    * @param packet
    * @param id
     */
    public void setPacketId(byte[] packet, int id) {
        return;
    };
    /**
     * 
    * <p>Title: haveNoPackageAck</p>
    * <p>Description: Ack是否打包</p>
    * @return
     */
    public boolean haveNoPackageAck() {
        return false;
    }
    /**
     * 
    * <p>Title: noPackageAckLen</p>
    * <p>Description: 不打包Ack的长度</p>
    * @return
     */
    public int noPackageAckLen() {
        return 100000;
    }
    /**
     * 
    * <p>Title: findOutNoPackageAck</p>
    * <p>Description: 查找不用打包的Ack</p>
    * @param buffer
    * @return
     */
    public boolean findOutNoPackageAck(byte[] buffer) {
        return false;
    }
    /**
     * 
    * <p>Title: IsSuportMeInfo</p>
    * <p>Description: 是否支持媒体信息</p>
    * @return
     */
    public boolean IsSuportMeInfo(){
    	return false;
    }
    /**
     * 
    * <p>Title: sendMediaInfo</p>
    * <p>Description: 抽象媒体信息公用接口</p>
    * @param mediaInfo
    * @return
     */
    public ArrayList<Message> getMediaInfo(MediaInfo mediaInfo){
    	return null;
    }
    /**
     * 
    * <p>Title: sendCallInfo</p>
    * <p>Description: 抽象媒体信息公用接口</p>
    * @param iType:0连接状态， ：1通话状态
    * @return
     */
    public Message getBtInfo(int iType,int iValue, MediaInfo mediaInfo){
    	return null;
    }
    /**
    * <p>Title: getVolInfo</p>
    * <p>Description: 获取音量</p>
     * @param iVol
     * @return
     */
    public Message getVolInfo(int iVol){
    	return null;
    }
    /**
     * 
    * <p>Title: GetMuteRadarData</p>
    * <p>Description: 获取雷达静音数据</p>
    * @param isMute
    * @return
     */
    public Message getMuteRadarData(boolean isMute){
    	return null;
    }
    /**
     * 
    * <p>Title: getParkData</p>
    * <p>Description: </p>
    * @param bPark
    * @return
     */
    public Message getParkData(boolean bPark){
    	return null;
    }
    /**
     * 
    * <p>Title: getInquiryTrackData</p>
    * <p>Description: </p>
    * @return
     */
    public Message getInquiryTrackData(){
    	return null;
    }
    /**
     * 
    * <p>Title: getCanKeyAttr</p>
    * <p>Description: 回调按键给Can 相当于转述</p>
    * @param iKeyCode
    * @return
     */
    public Message getCanKeyAttr(String strKeyCode){
    	return null;
    }
    /**
     * 
     * @Title: sendcmd   
     * @Description: TODO 
     * @param bycmd
     * @return      
     * @return: Message
     */
    public Message sendcmd(byte bycmd){
    	return null;
    }
    /**
     * 
     * @Title: senddata   
     * @Description: TODO 
     * @param bycmd
     * @param byval
     * @return      
     * @return: Message
     */
    public Message senddata(byte bycmd, byte byval){
    	return null;
    }
}
