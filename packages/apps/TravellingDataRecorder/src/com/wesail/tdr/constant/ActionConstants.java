package com.wesail.tdr.constant;

public class ActionConstants {
	public static final String SEND_CMD = "com.wesail.tdr.send.cmd";
	public static final String INFO_VEHICLE_FILE = "com.wesail.tdr.info.vehicle_file";
	public static final String INFO_RECORDER = "com.wesail.tdr.info.recorder";
	public static final String INFO_DRIVER = "com.wesail.tdr.info.driver";
	public static final String INFO_MILEAGE = "com.wesail.tdr.info.mileage";
	public static final String LIST_DATA_DRAVEL_SPEED = "com.wesail.tdr.list_data.dravel_speed";
	public static final String LIST_DATA_POWER = "com.wesail.tdr.list_data.power";
	public static final String LIST_DATA_OVER_SPEED = "com.wesail.tdr.list_data.over_speed";
	public static final String LIST_DATA_OVER_TIME = "com.wesail.tdr.list_data.over_time";
	public static final String LIST_DATA_ACCIDENT_DOUBT = "com.wesail.tdr.list_data.accident_doubt";
	public static final String LIST_DATA_ACCIDENT_DOUBT_DETIL = "com.wesail.tdr.list_data.accident_doubt.detil";
	public static final String TDR_CONNECT = "com.wesail.tdr.connect.success";
	public static final String TDR_DISCONNECT = "com.wesail.tdr.connect.fail";
	
	public static enum KeyNo{
		Poweron(0x8A),Mute(0x8C),Num0(0x90),Num9(0x99),
	    ChUp(0x80),ChDown(0x81),VolDownLeft(0x83),VolUpRight(0x82),
	    Menu(0x85),Exit(0x87),Info(0x89),Epg(0x9B),
	    Subtitle(0xC4),FastScan(0x9A),OK(0x9F);
		private final int value; 
        public int getValue() { 
            return value; 
        }
        KeyNo(int value) { 
            this.value = value; 
        } 
	};
}
