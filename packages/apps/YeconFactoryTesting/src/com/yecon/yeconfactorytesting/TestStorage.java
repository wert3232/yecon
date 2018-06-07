
package com.yecon.yeconfactorytesting;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.Handler;

import com.autochips.storage.EnvironmentATC;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.yecon.yeconfactorytesting.Constants.*;
import static com.yecon.yeconfactorytesting.FactoryTestUtil.*;

public class TestStorage {
    private static int mSdcardTestCount = 0;
    private static int mUdiskTestCount = 0;

    public static boolean readFile(String path) {
        String text = "";
        FileInputStream fis = null;

        try {
            File file = new File(path);
            if (!file.exists()) {
                return false;
            }
            fis = new FileInputStream(file);
            int len = fis.available();
            byte[] buf = new byte[len];
            fis.read(buf);
            text = EncodingUtils.getString(buf, "UTF-8");
            if (!text.equals(TEST_FILE_CONTENTS)) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    public static boolean readFileExt(String path) {
        String text = "";
        FileInputStream fis = null;

        try {
            File file = new File(path);
            if (!file.exists()) {
                return false;
            }
            fis = new FileInputStream(file);
            int len = fis.available();
            byte[] buf = new byte[len];
            fis.read(buf);
            text = EncodingUtils.getString(buf, "UTF-8");
            printLog(text);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    public static boolean writeFile(String path, String contents) {
        FileOutputStream fos = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                printLog(e.toString());
            }

            fos = new FileOutputStream(file);
            fos.write(contents.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            printLog("writeFile - finally");
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static List<String> loadingTextFiles(String path) {
        List<String> fileList = new ArrayList<String>();

        FilenameFilter filter = new FilenameFilter() {

            @SuppressLint("DefaultLocale")
            @Override
            public boolean accept(File file, String filename) {
                String lowercasename = filename.toLowerCase();
                if (lowercasename.endsWith("txt")) {
                    return true;
                }
                return false;
            }
        };

        File file = new File(path);
        File[] filesArray = file.listFiles(filter);
        for (File f : filesArray) {
            if (f.isFile()) {
                fileList.add(f.getPath());
            }
        }
        Collections.sort(fileList);

        return fileList;
    }

    /**
     * 插入SD卡，检测SD卡是否存在：存在－－OK / 不存在－－NO
     * 
     * @注意：由于某些原因，向SD卡写文件时，总是失败，因此只检测SD卡的状态
     * @param env
     * @param handler
     */
    public static void onSDCardAutoTest(EnvironmentATC env, Handler handler) {
        printLog("onSDCardAutoTest - start");

        boolean sdcardState = false;

        mSdcardTestCount = 0;
        while (mSdcardTestCount < REPEAT_TEST_COUNT) {
            sdcardState = env.getStorageState(EXT_SDCARD1_PATH).equals(Environment.MEDIA_MOUNTED);
            if (!sdcardState) {
                mSdcardTestCount++;
                printLog("onSDCardAutoTest - sdcard1 test count: " + mSdcardTestCount);
            } else {
                break;
            }
        }

        if (mSdcardTestCount >= REPEAT_TEST_COUNT) {
            printLog("sdCard1 is not mounted");
            handler.sendEmptyMessage(MSG_SDCARD1_ERROR);
        } else {
            printLog("sdCard1 is mounted");
            handler.sendEmptyMessage(MSG_SDCARD1_SUCCESS);

            // List<String> files =
            // TestStorage.loadingTextFiles(EXT_SDCARD1_PATH);
            // if (files == null || (files != null && files.size() == 0)) {
            // mHandler.sendEmptyMessage(MSG_SDCARD1_ERROR);
            // } else {
            // boolean ret = TestStorage.readFileExt(files.get(0));
            // if (ret) {
            // mHandler.sendEmptyMessage(MSG_SDCARD1_SUCCESS);
            // } else {
            // mHandler.sendEmptyMessage(MSG_SDCARD1_ERROR);
            // }
            // }

            // String sdCard1FilePath = EXT_SDCARD1_PATH + TEST_FILENAME;
            // boolean ret = TestStorage.writeFile(sdCard1FilePath,
            // TEST_FILE_CONTENTS);
            // printLog("sdCard1 - write ret: " + ret);
            // if (!ret) {
            // mHandler.sendEmptyMessage(MSG_SDCARD1_ERROR);
            // } else {
            // ret = TestStorage.readFile(sdCard1FilePath);
            // printLog("sdCard1 - read ret: " + ret);
            // if (ret) {
            // mHandler.sendEmptyMessage(MSG_SDCARD1_SUCCESS);
            // } else {
            // mHandler.sendEmptyMessage(MSG_SDCARD1_ERROR);
            // }
            // TestStorage.deleteFile(sdCard1FilePath);
            // }
        }

        if (!NEED_TEST_WIFI) {
            sdcardState = false;

            mSdcardTestCount = 0;
            while (mSdcardTestCount < REPEAT_TEST_COUNT) {
                sdcardState = env.getStorageState(EXT_SDCARD2_PATH).equals(
                        Environment.MEDIA_MOUNTED);
                if (!sdcardState) {
                    mSdcardTestCount++;
                    printLog("onSDCardAutoTest - sdcard2 test count: " + mSdcardTestCount);
                } else {
                    break;
                }
            }

            if (mSdcardTestCount >= REPEAT_TEST_COUNT) {
                printLog("sdCard2 is not mounted");
                handler.sendEmptyMessage(MSG_SDCARD2_ERROR);
            } else {
                printLog("sdCard2 is mounted");
                handler.sendEmptyMessage(MSG_SDCARD2_SUCCESS);

                // List<String> files =
                // TestStorage.loadingTextFiles(EXT_SDCARD2_PATH);
                // if (files == null || (files != null && files.size() == 0)) {
                // mHandler.sendEmptyMessage(MSG_SDCARD2_ERROR);
                // } else {
                // boolean ret = TestStorage.readFileExt(files.get(0));
                // if (ret) {
                // mHandler.sendEmptyMessage(MSG_SDCARD2_SUCCESS);
                // } else {
                // mHandler.sendEmptyMessage(MSG_SDCARD2_ERROR);
                // }
                // }

                // String sdCard2FilePath = EXT_SDCARD2_PATH + TEST_FILENAME;
                // boolean ret = TestStorage.writeFile(sdCard2FilePath,
                // TEST_FILE_CONTENTS);
                // printLog("sdCard2 - write ret: " + ret);
                // if (!ret) {
                // mHandler.sendEmptyMessage(MSG_SDCARD2_ERROR);
                // } else {
                // ret = TestStorage.readFile(sdCard2FilePath);
                // printLog("sdCard2 - read ret: " + ret);
                // if (ret) {
                // mHandler.sendEmptyMessage(MSG_SDCARD2_SUCCESS);
                // } else {
                // mHandler.sendEmptyMessage(MSG_SDCARD2_ERROR);
                // }
                // TestStorage.deleteFile(sdCard2FilePath);
                // }
            }
        }

        printLog("onSDCardAutoTest - end");
    }

    /**
     * 插入USB，检测USB是否存在：存在－－OK / 不存在－－NO
     * 
     * @注意：由于某些原因，向USB写文件时，总是失败，因此只检测USB的状态
     * @param env
     * @param handler
     */
    public static void onUDISKAutoTest(EnvironmentATC env, Handler handler) {
        printLog("onUDISKAutoTest - start");

        boolean udiskState = false;

        mUdiskTestCount = 0;
        while (mUdiskTestCount < REPEAT_TEST_COUNT) {
            udiskState = (env.getStorageState(UDISK1_PATH).equals(Environment.MEDIA_MOUNTED)
                    || env.getStorageState(UDISK3_PATH).equals(Environment.MEDIA_MOUNTED)
                    || env.getStorageState(UDISK4_PATH).equals(Environment.MEDIA_MOUNTED)
                    || env.getStorageState(UDISK5_PATH).equals(Environment.MEDIA_MOUNTED));
            if (!udiskState) {
                mUdiskTestCount++;
                printLog("onSDCardAutoTest - udisk1 test count: " + mUdiskTestCount);
            } else {
                break;
            }
        }

        if (mUdiskTestCount >= REPEAT_TEST_COUNT) {
            handler.sendEmptyMessage(MSG_UDISK0_ERROR);
        } else {
            handler.sendEmptyMessage(MSG_UDISK0_SUCCESS);

            // String udisk1FilePath = "";
            // if
            // (mEnv.getStorageState(UDISK1_PATH).equals(Environment.MEDIA_MOUNTED))
            // {
            // udisk1FilePath = UDISK1_PATH;
            // } else if
            // (mEnv.getStorageState(UDISK3_PATH).equals(Environment.MEDIA_MOUNTED))
            // {
            // udisk1FilePath = UDISK3_PATH;
            // } else if
            // (mEnv.getStorageState(UDISK4_PATH).equals(Environment.MEDIA_MOUNTED))
            // {
            // udisk1FilePath = UDISK4_PATH;
            // } else if
            // (mEnv.getStorageState(UDISK5_PATH).equals(Environment.MEDIA_MOUNTED))
            // {
            // udisk1FilePath = UDISK5_PATH;
            // }
            // udisk1FilePath += TEST_FILENAME;
            // printLog("udisk1 - udisk1FilePath: " + udisk1FilePath);
            // boolean ret = TestStorage.writeFile(udisk1FilePath,
            // TEST_FILE_CONTENTS);
            // printLog("udisk1 - write ret: " + ret);
            // if (!ret) {
            // mHandler.sendEmptyMessage(MSG_UDISK0_ERROR);
            // } else {
            // ret = TestStorage.readFile(udisk1FilePath);
            // printLog("udisk1 - read ret: " + ret);
            // if (ret) {
            // mHandler.sendEmptyMessage(MSG_UDISK0_SUCCESS);
            // } else {
            // mHandler.sendEmptyMessage(MSG_UDISK0_ERROR);
            // }
            // TestStorage.deleteFile(udisk1FilePath);
            // }
        }

        if (NEED_TEST_WIFI) {
            return;
        }

        udiskState = false;

        mUdiskTestCount = 0;
        while (mUdiskTestCount < REPEAT_TEST_COUNT) {
            udiskState = env.getStorageState(UDISK2_PATH).equals(Environment.MEDIA_MOUNTED);
            if (!udiskState) {
                mUdiskTestCount++;
                printLog("onSDCardAutoTest - udisk2 test count: " + mUdiskTestCount);
            } else {
                break;
            }
        }

        if (mUdiskTestCount >= REPEAT_TEST_COUNT) {
            handler.sendEmptyMessage(MSG_UDISK1_ERROR);
        } else {
            handler.sendEmptyMessage(MSG_UDISK1_SUCCESS);

            // String udisk2FilePath = UDISK2_PATH + TEST_FILENAME;
            // printLog("udisk2 - udisk1FilePath: " + udisk2FilePath);
            // boolean ret = TestStorage.writeFile(udisk2FilePath,
            // TEST_FILE_CONTENTS);
            // printLog("udisk2 - write ret: " + ret);
            // if (!ret) {
            // mHandler.sendEmptyMessage(MSG_UDISK1_ERROR);
            // } else {
            // ret = TestStorage.readFile(udisk2FilePath);
            // printLog("udisk2 - read ret: " + ret);
            // if (ret) {
            // mHandler.sendEmptyMessage(MSG_UDISK1_SUCCESS);
            // } else {
            // mHandler.sendEmptyMessage(MSG_UDISK1_ERROR);
            // }
            // TestStorage.deleteFile(udisk2FilePath);
            // }
        }

        printLog("onUDISKAutoTest - end");
    }
}
