
package com.yecon.yeconfactorytesting;

import android.content.Context;
import android.os.StatFs;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yecon.yeconfactorytesting.Constants.*;

public class FactoryTestUtil {
    private static final String TAG = "FactoryTesting";
    private static final boolean DEBUG = true;

    private static FactoryConfigInfo mConfigInfo = new FactoryConfigInfo();

    public static void printLog(byte[] log, int len) {
        if (log == null) {
            return;
        }

        if (DEBUG) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < len; i++) {
                String str = String.format("0x%02X ", log[i]);
                sb.append(str);
            }
            Log.e(TAG, "len: " + len + " - " + sb.toString());
        }
    }

    public static void printLog(byte[] log, int len, boolean debug) {
        if (log == null) {
            return;
        }

        if (debug) {
            printLog(log, len);
        }
    }

    public static void printLog(int[] log, int len) {
        if (log == null) {
            return;
        }

        if (DEBUG) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < len; i++) {
                String str = String.format("0x%02X ", log[i]);
                sb.append(str);
            }
            Log.e(TAG, "len: " + len + " - " + sb.toString());
        }
    }

    public static void printLog(int[] log, int len, boolean debug) {
        if (log == null) {
            return;
        }

        if (debug) {
            printLog(log, len);
        }
    }

    public static void printLog(long[] log, int len) {
        if (log == null) {
            return;
        }

        if (DEBUG) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < len; i++) {
                String str = String.format("0x%02X ", log[i]);
                sb.append(str);
            }
            Log.e(TAG, "len: " + len + " - " + sb.toString());
        }
    }

    public static void printLog(long[] log, int len, boolean debug) {
        if (log == null) {
            return;
        }

        if (debug) {
            printLog(log, len);
        }
    }

    public static void printLog(String log) {
        if (log == null) {
            return;
        }

        if (DEBUG) {
            Log.e(TAG, log);
        }
    }

    public static void printLog(String log, boolean debug) {
        if (log == null) {
            return;
        }

        if (debug) {
            printLog(log);
        }
    }

    /**
     * 获取系统总内存 －－ 单位是byte
     */
    public static long getTotalMemorySize(Context context) {
        String dir = "/proc/meminfo";
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(dir);
            br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            return Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) * 1024l;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 获取手机内部总的存储空间 －－ 单位是byte
     */
    @SuppressWarnings("deprecation")
    public static long getTotalInternalMemorySize() {
        File path = new File("/mnt/sdcard");
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public static String formatSize(long size) {
        float totalMemorySize = size;
        totalMemorySize /= (1024 * 1024);
        String totalMemoryStr = "";
        if (totalMemorySize <= 0) {
            totalMemoryStr = "0GB";
        } else if (totalMemorySize > 0 && totalMemorySize <= 128) {
            totalMemoryStr = "128MB";
        } else if (totalMemorySize > 128 && totalMemorySize <= 256) {
            totalMemoryStr = "256MB";
        } else if (totalMemorySize > 256 && totalMemorySize <= 512) {
            totalMemoryStr = "512MB";
        } else if (totalMemorySize > 512 && totalMemorySize <= 1024) {
            totalMemoryStr = "1GB";
        } else if (totalMemorySize > 1024 && totalMemorySize <= 1024 * 2) {
            totalMemoryStr = "2GB";
        } else if (totalMemorySize > 1024 * 2 && totalMemorySize <= 1024 * 4) {
            totalMemoryStr = "4GB";
        } else if (totalMemorySize > 1024 * 4 && totalMemorySize <= 1024 * 8) {
            totalMemoryStr = "8GB";
        } else if (totalMemorySize > 1024 * 8 && totalMemorySize <= 1024 * 16) {
            totalMemoryStr = "16GB";
        } else if (totalMemorySize > 1024 * 16 && totalMemorySize <= 1024 * 32) {
            totalMemoryStr = "32GB";
        } else if (totalMemorySize > 1024 * 32 && totalMemorySize <= 1024 * 64) {
            totalMemoryStr = "64GB";
        } else if (totalMemorySize > 1024 * 64 && totalMemorySize <= 1024 * 128) {
            totalMemoryStr = "128GB";
        } else if (totalMemorySize > 1024 * 128 && totalMemorySize <= 1024 * 256) {
            totalMemoryStr = "256GB";
        }
        return totalMemoryStr;
    }

    public static String getFormattedKernelVersion() {
        String procVersionStr;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(
                    "/proc/version"), 256);
            try {
                procVersionStr = reader.readLine();
            } finally {
                reader.close();
            }

            final String PROC_VERSION_REGEX =
                    "Linux version (\\S+) " + // group 1: "3.0.31-g6fb96c9"
                            "\\((\\S+?)\\) " + // group 2: "x@y.com" (kernel
                                               // builder)
                            "(?:\\(gcc.+? \\)) " + // ignore: GCC version
                                                   // information
                            "(#\\d+) " + // group 3: "#1"
                            "(?:.*?)?" + // ignore: optional SMP, PREEMPT, and
                                         // any CONFIG_FLAGS
                            "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)";

            Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(procVersionStr);
            if (!m.matches()) {
                return "Unavailable";
            } else if (m.groupCount() < 4) {
                return "Unavailable";
            }
            return m.group(1) + "\n" + // 3.0.31-g6fb96c9
                    m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
                    m.group(4);
        } catch (IOException e) {
            return "Unavailable";
        }
    }

    public static FactoryConfigInfo getConfigInfo() {
        return mConfigInfo;
    }

    public static void parseFactoryConfig() {
        File file = new File(FACTORY_CONFIG_FILENAME);
        if (!file.exists()) {
            return;
        }

        FileInputStream fis = null;
        XmlPullParser parser = null;
        try {
            fis = new FileInputStream(file);

            parser = Xml.newPullParser();
            parser.setInput(fis, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.END_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG: {
                        String tagName = parser.getName();
                        if (tagName.equals("Volume")) {
                            int volume = Integer.parseInt(parser.getAttributeValue(0));
                            printLog("parseFactoryConfig - volume: " + volume);
                            mConfigInfo.setDefaultVolume(volume);
                        } else if (tagName.equals("GpsStarValueRange")) {
                            int gpsStartValue = Integer.parseInt(parser.getAttributeValue(0));
                            int gpsEndValue = Integer.parseInt(parser.getAttributeValue(1));
                            printLog("parseFactoryConfig - gpsStartValue: " + gpsStartValue
                                    + " - gpsEndValue: " + gpsEndValue);
                            mConfigInfo.setGpsStartValue(gpsStartValue);
                            mConfigInfo.setGpsEndValue(gpsEndValue);
                        } else if (tagName.equals("FlashAndMemory")) {
                            int flashType = Integer.parseInt(parser.getAttributeValue(0));
                            printLog("parseFactoryConfig - flashType: " + flashType);
                            mConfigInfo.setFlashType(flashType);
                        } else if (tagName.equals("Wifi")) {
                            String ssid = parser.getAttributeValue(0);
                            String password = parser.getAttributeValue(1);
                            printLog("parseFactoryConfig - ssid: " + ssid + " - password: "
                                    + password);
                            mConfigInfo.setWifiConnectSSID(ssid);
                            mConfigInfo.setWifiConnectPassword(password);
                        } else if (tagName.equals("TestType")) {
                            int testType = Integer.parseInt(parser.getAttributeValue(0));
                            printLog("parseFactoryConfig - testType: " + testType);
                            mConfigInfo.setTestType(testType);
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

                eventType = parser.next();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
    }
}
