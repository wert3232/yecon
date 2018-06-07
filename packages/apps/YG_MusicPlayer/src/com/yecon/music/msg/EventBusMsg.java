package com.yecon.music.msg;

import java.util.HashMap;
import java.util.Map;

public class EventBusMsg {
	public String type;
	public String msgStr;
	public Map<String, Object> map = new HashMap<String, Object>();
	public boolean is;
	public int i;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMsgStr() {
		return msgStr;
	}
	public void setMsgStr(String msgStr) {
		this.msgStr = msgStr;
	}
	public Map<String, Object> getMap() {
		return map;
	}
	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
	public boolean isIs() {
		return is;
	}
	public void setIs(boolean is) {
		this.is = is;
	}
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	
}
