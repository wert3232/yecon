package com.yecon.carwidget8317;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.yecon.atv.AtvLogic;
import com.yecon.avin.AvinLogic;
import com.yecon.bt.BtLogic;
import com.yecon.dtv.DtvLogic;
import com.yecon.dvd.DvdLogic;
import com.yecon.ipod.IpodLogic;
import com.yecon.music.MusicLogic;
import com.yecon.radio.RadioLogic;
import com.yecon.video.VideoLogic;

import android.util.Log;

public class manager
{
    private static final String TAG = "manager";
    private static Map<String, WidgetsBase> mMapModuleObjects;
    private static Map<eSourceID, String> mmaplogic;
    
    static {
        manager.mmaplogic = null;
        manager.mMapModuleObjects = null;
        manager.mmaplogic = new HashMap<eSourceID, String>();
        
        Log.i(TAG, "widget--->manager init");
        regSourceEvent(eSourceID.SOURCE_RADIO, RadioLogic.class.getName());
        regSourceEvent(eSourceID.SOURCE_AUDIO, MusicLogic.class.getName());
        regSourceEvent(eSourceID.SOURCE_BT, BtLogic.class.getName());
        regSourceEvent(eSourceID.SOURCE_IPOD, IpodLogic.class.getName());
        regSourceEvent(eSourceID.SOURCE_DISC, DvdLogic.class.getName());
        regSourceEvent(eSourceID.SOURCE_AVIN, AvinLogic.class.getName());
        regSourceEvent(eSourceID.SOURCE_ATV, AtvLogic.class.getName());
        regSourceEvent(eSourceID.SOURCE_DTV, DtvLogic.class.getName());
        regSourceEvent(eSourceID.SOURCE_VIDEO, VideoLogic.class.getName());
    }
    
    private static Object create(final String s) {
        Object instance = null;
        if (s == null) {
            return instance;
        }
        final boolean empty = s.isEmpty();
        instance = null;
        if (empty) {
            return instance;
        }
        try {
            instance = Class.forName(s).newInstance();
            return instance;
        }
        catch (InstantiationException ex) {
            ex.printStackTrace();
            return null;
        }
        catch (IllegalAccessException ex2) {
            ex2.printStackTrace();
            return null;
        }
        catch (ClassNotFoundException ex3) {
            ex3.printStackTrace();
            return null;
        }
    }
    
    public static WidgetsBase getLogicFromString(String name) {
    	WidgetsBase logicBase = null;
    	
        if (manager.mMapModuleObjects == null) {
            manager.mMapModuleObjects = new ConcurrentHashMap<String, WidgetsBase>();
        }
        
        for (Entry<String, WidgetsBase> entry: mMapModuleObjects.entrySet()) {
        	if (name.equals(entry.getKey())) {
        		logicBase = entry.getValue();
        		break;
        	}
        }
        
        if (logicBase == null) {
        	logicBase = (WidgetsBase)create(name);
        	if (logicBase != null) {
        		if (mMapModuleObjects != null) {
        			mMapModuleObjects.put(name, logicBase);
        		}
        	}
        }
        
        return logicBase;
    }
    
    public static String getLogicNameFromSourceID(final int n) {
        String name = "";
        if (mmaplogic != null) {
            for (Entry<eSourceID, String> entry : mmaplogic.entrySet()) {
                if (n == entry.getKey().ordinal()) {
                    name = entry.getValue();
                    break;
                }
            }
        }else {
        	Log.i(TAG, "widget--->mmaplogic=null");
        }
        
        if (name == "") {
        	name = RadioLogic.class.getName();
        }
        return name;
    }
    
    private static void regSourceEvent(final eSourceID eSourceID, final String s) {
        if (manager.mmaplogic != null && s != null && !s.isEmpty()) {
        	
        	Log.i(TAG, "widget--->reg source event, source="+eSourceID.ordinal()+", name="+ s);
            manager.mmaplogic.put(eSourceID, s);
        }
    }
    
    public enum eSourceID
    {
    	SOURCE_RADIO("SOURCE_RADIO", 0), 
    	SOURCE_AUDIO("SOURCE_AUDIO", 1), 
    	SOURCE_VIDEO("SOURCE_VIDEO", 2),
    	SOURCE_BT("SOURCE_BT", 3),
    	SOURCE_DISC("SOURCE_DISC", 4), 
        SOURCE_DVR("SOURCE_DVR", 5),
        SOURCE_DTV("SOURCE_DTV", 6),
        SOURCE_ATV("SOURCE_ATV", 7),
        SOURCE_AVIN("SOURCE_AVIN", 8), 
    	SOURCE_IPOD("SOURCE_IPOD", 9),
    	SOURCE_MAX("SOURCE_MAX", 15);
        private eSourceID(final String s, final int n) {
        }
    }
}
