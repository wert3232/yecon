
package com.yecon.yeconfactorytesting;

import static com.yecon.yeconfactorytesting.Constants.*;
import static com.yecon.yeconfactorytesting.FactoryTestUtil.*;

import android.os.Handler;

import com.autochips.inputsource.AVIN;

public class TestAux {
    public static void onAUXAudioTest(AVIN avinA, Handler handler, int currAudioPort,
            int auxTestCount) {
        printLog("onAUXAudioTest - auxTestCount: " + auxTestCount);

        int what = 0;

        AVIN.Volume volumeValue = avinA.createVolume();
        avinA.getVolume(volumeValue);
        int volFrontL = volumeValue.u4ChVolFrontL;
        int volFrontR = volumeValue.u4ChVolFrontR;
        printLog("onAUXAudioTest - u4ChVolFrontL: " + volFrontL + " - u4ChVolFrontR: " + volFrontR
                + " - mCurrAudioPort: " + currAudioPort);

        if (auxTestCount < REPEAT_TEST_COUNT
                && (volFrontL < VOLUME_DEFAULT_VALUE || volFrontR < VOLUME_DEFAULT_VALUE)) {
            handler.removeMessages(MSG_AUX_AUDIO_REPEAT_TEST);
            handler.sendEmptyMessageDelayed(MSG_AUX_AUDIO_REPEAT_TEST, AUX_TEST_DELAY_TIME);
            return;
        }

        switch (currAudioPort) {
            case AVIN.PORT1:
                what = volFrontL > VOLUME_DEFAULT_VALUE ? MSG_AUX1_LEFT_SUCCESS
                        : MSG_AUX1_LEFT_ERROR;
                handler.sendEmptyMessage(what);

                what = volFrontR > VOLUME_DEFAULT_VALUE ? MSG_AUX1_RIGHT_SUCCESS
                        : MSG_AUX1_RIGHT_ERROR;
                handler.sendEmptyMessage(what);

                handler.sendEmptyMessageDelayed(MSG_AUX1_DONE, AUX_TEST_DELAY_TIME);
                break;

            case AVIN.PORT2:
                what = volFrontL > VOLUME_DEFAULT_VALUE ? MSG_AUX2_LEFT_SUCCESS
                        : MSG_AUX2_LEFT_ERROR;
                handler.sendEmptyMessage(what);

                what = volFrontR > VOLUME_DEFAULT_VALUE ? MSG_AUX2_RIGHT_SUCCESS
                        : MSG_AUX2_RIGHT_ERROR;
                handler.sendEmptyMessage(what);

                handler.sendEmptyMessageDelayed(MSG_AUX2_DONE, AUX_TEST_DELAY_TIME);
                break;

            case AVIN.PORT3:
                what = volFrontL > VOLUME_DEFAULT_VALUE ? MSG_AUX3_LEFT_SUCCESS
                        : MSG_AUX3_LEFT_ERROR;
                handler.sendEmptyMessage(what);

                what = volFrontR > VOLUME_DEFAULT_VALUE ? MSG_AUX3_RIGHT_SUCCESS
                        : MSG_AUX3_RIGHT_ERROR;
                handler.sendEmptyMessage(what);

                handler.sendEmptyMessageDelayed(MSG_AUX3_DONE, AUX_TEST_DELAY_TIME);
                break;

            case AVIN.PORT4:
                what = volFrontL > VOLUME_DEFAULT_VALUE ? MSG_AUX4_LEFT_SUCCESS
                        : MSG_AUX4_LEFT_ERROR;
                handler.sendEmptyMessage(what);

                what = volFrontR > VOLUME_DEFAULT_VALUE ? MSG_AUX4_RIGHT_SUCCESS
                        : MSG_AUX4_RIGHT_ERROR;
                handler.sendEmptyMessage(what);

                handler.sendEmptyMessageDelayed(MSG_AUX4_DONE, AUX_TEST_DELAY_TIME);
                break;

            case AVIN.PORT5:
                what = volFrontL > VOLUME_DEFAULT_VALUE ? MSG_AUX5_LEFT_SUCCESS
                        : MSG_AUX5_LEFT_ERROR;
                handler.sendEmptyMessage(what);

                what = volFrontR > VOLUME_DEFAULT_VALUE ? MSG_AUX5_RIGHT_SUCCESS
                        : MSG_AUX5_RIGHT_ERROR;
                handler.sendEmptyMessage(what);

                handler.sendEmptyMessageDelayed(MSG_AUX5_DONE, AUX_TEST_DELAY_TIME);
                break;
        }
    }

    public static void onAUXVideoTest(int msg, int currVideoPort, Handler handler) {
        printLog("onAUXVideoTest - start");

        int what = MSG_AUX1_VIDEO_ERROR;

        switch (msg) {
            case AVIN.SIGNAL_READY:
                switch (currVideoPort) {
                    case AVIN.PORT1:
                        what = MSG_AUX1_VIDEO_SUCCESS;
                        break;
                    case AVIN.PORT2:
                        what = MSG_AUX2_VIDEO_SUCCESS;
                        break;
                    case AVIN.PORT3:
                        what = MSG_AUX3_VIDEO_SUCCESS;
                        break;
                    case AVIN.PORT4:
                        what = MSG_AUX4_VIDEO_SUCCESS;
                        break;
                    case AVIN.PORT5:
                        what = MSG_AUX5_VIDEO_SUCCESS;
                        break;
                }
                break;

            case AVIN.SIGNAL_LOST:
                switch (currVideoPort) {
                    case AVIN.PORT1:
                        what = MSG_AUX1_VIDEO_ERROR;
                        break;
                    case AVIN.PORT2:
                        what = MSG_AUX2_VIDEO_ERROR;
                        break;
                    case AVIN.PORT3:
                        what = MSG_AUX3_VIDEO_ERROR;
                        break;
                    case AVIN.PORT4:
                        what = MSG_AUX4_VIDEO_ERROR;
                        break;
                    case AVIN.PORT5:
                        what = MSG_AUX5_VIDEO_ERROR;
                        break;
                }
                break;
        }
        handler.sendEmptyMessage(what);

        printLog("onAUXVideoTest - end");
    }
}
