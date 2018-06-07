
package com.yecon.music;

import static com.yecon.music.MusicConstant.*;

public class MediaScanStatus {
    private static int mMediaScanStatus = MEDIA_SCAN_STATUS_STARTED;

    public static int getMediaScanStatus() {
        return mMediaScanStatus;
    }

    public static void setMediaScanStatus(int mediaScanStatus) {
        mMediaScanStatus = mediaScanStatus;
    }

}
