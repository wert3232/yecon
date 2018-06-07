package com.autochips.bluetooth;

public class BluetoothConstants {
	public static final String ACTION_BLUETOOTH_CALL_STATUS_CHANGE =
	        "com.autochips.bluetooth.PhoneCallActivity.action.BLUETOOTH_CALL_STATUS_CHANGE"; 

    public static final String ACTION_BLUETOOTH_CALL_STATUS =
        "com.autochips.bluetooth.PhoneCallActivity.action.BLUETOOTH_CALL_STATUS";
    
    public static final String ACTION_BLUETOOTH_NEW_CALL =
        "com.autochips.bluetooth.PhoneCallActivity.action.BLUETOOTH_NEW_CALL";
    
    public static final int START_FRAGMENT_PAIR = 0xF1;
    public static final int START_FRAGMENT_SETTING = 0xF2;
    public static final int START_FRAGMENT_BT_MUSIC = 0xF3;
    public static final int START_FRAGMENT_CALL_LOG = 0xF4;
    public static final int START_FRAGMENT_PHONEBOOK = 0xF5;
    
    public static final int CALL_TYPE_OUTGOING = 0x01;
    public static final int CALL_TYPE_INCOME = 0x02;
    public static final int CALL_TYPE_COMBINED = 0x03;
    public static final int CALL_TYPE_MISS = 0x04;
}
