/**
 * 20141212 Jade add for BKL control
 */

package android.backlight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.os.SystemProperties;
import android.util.Log;

public class BacklightControl {
    public static final String TAG = "BKL_CTL";
    public static final String BKL_PATH = "/sys/bkl/bkl_enable";

    public static final String Style_light_backlight_tag = "persist.sys.yeconBacklight";

    public static final String PERSYS_BKLIGHT[] = { "persist.sys.yeconBacklight",
            "persist.sys.Backlight_low", "persist.sys.Backlight_mid", "persist.sys.Backlight_high" };

    public static final int DEFAULT_BACKLIGHT = 204;

    public static final int LIGHTLEVEL[] = {
            51, 102, 201
    };

    public static boolean SetBklEnable(int enable, int touch_wakeup_en, int power_key_pressed) {
        enable = (enable > 0) ? 1 : 0;
        touch_wakeup_en = (touch_wakeup_en > 0) ? 1 : 0;
        power_key_pressed = (power_key_pressed > 0) ? 1 : 0;
        File bkl_file = new File(BKL_PATH);
        if (!bkl_file.exists()) {
            Log.e(TAG, "BKL driver file not found:" + BKL_PATH);
            return false;
        }

        String strCmd = "" + enable + " " + touch_wakeup_en + " " + power_key_pressed;
        Log.d(TAG, "strCmd=" + strCmd);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(BKL_PATH), 32);
            try {
                writer.write(strCmd + "\r\n");
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "IO Exception when write: " + BKL_PATH, e);
            return false;
        }
        return true;
    }

    public static boolean GetBklEnable() {
        File bkl_file = new File(BKL_PATH);
        if (!bkl_file.exists()) {
            Log.e(TAG, "BKL driver file not found:" + BKL_PATH);
            return true;
        }

        String strEnalbe = "1";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(BKL_PATH), 32);
            try {
                strEnalbe = reader.readLine();
            } finally {
                reader.close();
            }

            if (strEnalbe.equals("1")) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, "IO Exception when read: " + BKL_PATH, e);
            return true;
        }
    }

    public static int getBrightness() {
        int backlight = SystemProperties.getInt(PERSYS_BKLIGHT[0], DEFAULT_BACKLIGHT);
        return backlight;
    }

    public static int getBacklightLevel(int brightness) {
        int brightnessLow = SystemProperties.getInt(PERSYS_BKLIGHT[1], LIGHTLEVEL[0]);
        int brightnessMid = SystemProperties.getInt(PERSYS_BKLIGHT[2], LIGHTLEVEL[1]);
        int brightnessHigh = SystemProperties.getInt(PERSYS_BKLIGHT[3], LIGHTLEVEL[2]);
        Log.e(TAG, "getBacklightLevel - brightnessLow: " + brightnessLow + " - brightnessMid: "
                + brightnessMid + " - brightnessHigh: " + brightnessHigh);

        int backlightLevel = 0;

        if (brightness >= LIGHTLEVEL[0] && brightness < LIGHTLEVEL[1]) {
            backlightLevel = 1;
        } else if (brightness >= LIGHTLEVEL[1]
                && brightness < LIGHTLEVEL[2]) {
            backlightLevel = 2;
        } else if (brightness >= LIGHTLEVEL[2]) {
            backlightLevel = 3;
        }
        return backlightLevel;
    }

}
