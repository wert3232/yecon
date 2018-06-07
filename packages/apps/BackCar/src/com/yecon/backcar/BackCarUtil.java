
package com.yecon.backcar;

import android.os.SystemProperties;

import com.autochips.settings.AtcSettings;

public class BackCarUtil {
    public final static String PERSYS_RGB_VIDEO[][] = {
            { "persist.sys.screen_bright", "persist.sys.screen_contrast", "persist.sys.screen_hue",
                    "persist.sys.screen_saturation" },
            { "persist.sys.backcar_bright", "persist.sys.backcar_contrast",
                    "persist.sys.backcar_hue", "persist.sys.backcar_saturation" }, };
    // default values
    private static final int[][] DEFAULT_VALUES = { { 40, 25, 50, 50 }, { 45, 45, 50, 96 }, };

    private static int video_values[] = { 40, 25, 50, 50 };

    public static void SetAuxVideoPara() {
        int i = 0;
        for (int value : video_values) {
            video_values[i] = Integer.parseInt(SystemProperties.get(PERSYS_RGB_VIDEO[1][i],
                    DEFAULT_VALUES[1][i] + ""));
            i++;
        }

        AtcSettings.Display.SetBrightnessLevel(video_values[0]);
        AtcSettings.Display.SetContrastLevel(video_values[1]);
        AtcSettings.Display.SetHueLevel(video_values[2]);
        AtcSettings.Display.SetSaturationLevel(video_values[3]);
    }

    public static void SetSysVideoPara() {
        int i = 0;
        for (int value : video_values) {
            video_values[i] = Integer.parseInt(SystemProperties.get(PERSYS_RGB_VIDEO[0][i],
                    DEFAULT_VALUES[0][i] + ""));
            i++;
        }

        /*
         * Log.e(TAG, "Sys brightness_values: " + video_values[0] + " - contrast_values: " +
         * video_values[1] + " - hue_values: " + video_values[2] + " - saturation_values: " +
         * video_values[3]);
         */

        AtcSettings.Display.SetBrightnessLevel(video_values[0]);
        AtcSettings.Display.SetContrastLevel(video_values[1]);
        AtcSettings.Display.SetHueLevel(video_values[2]);
        AtcSettings.Display.SetSaturationLevel(video_values[3]);
    }
}
