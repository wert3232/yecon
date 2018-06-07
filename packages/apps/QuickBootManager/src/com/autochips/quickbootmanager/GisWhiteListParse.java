
package com.autochips.quickbootmanager;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class GisWhiteListParse {
    public static final String GIS_WHITE_LIST = "/data4write/whitelist4gis_conf.xml";

    private static XmlPullParser mXmlParser = null;

    private static ArrayList<String> mGisWhiteList = new ArrayList<String>();

    public static void gisWhiteListParse() {
        File file = new File(GIS_WHITE_LIST);
        if (!file.exists()) {
            Utils.IF_LOG_OUT("readWhiteListFromXML LoadFile /data4write/whitelist4gis_conf.xml fail");
            return;
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);

            mXmlParser = Xml.newPullParser();
            mXmlParser.setInput(fis, "UTF-8");
            int eventType = -1;
            eventType = mXmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG: {
                        String tagName = mXmlParser.getName();
                        if (tagName != null && tagName.equals("GISName")) {
                            mGisWhiteList.add(mXmlParser.nextText());
                        }
                    }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    default:
                        break;
                }

                eventType = mXmlParser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mGisWhiteList != null) {
            for (int i = 0; i < mGisWhiteList.size(); i++) {
                Utils.IF_LOG_OUT("gisWhiteListParse - gis package name: " + mGisWhiteList.get(i));
            }
        }
    }

    public static ArrayList<String> getGisWhiteList() {
        return mGisWhiteList;
    }

}
