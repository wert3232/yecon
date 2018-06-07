
package com.yecon.yeconfactorytesting;

import android.os.SystemClock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.yecon.yeconfactorytesting.FactoryTestUtil.*;
import static com.yecon.yeconfactorytesting.Constants.*;

public class TestGPIO {
    public static final String GPIO_PATH = "/sys/gpio/ac8317_gpio";

    /**
     * GPIO55 => GPIO55 EINT2 => GPIO37 EINT1 => GPIO36 PR_SCL0 => GPIO112 PR_SDA0 => GPIO117 SP0_SI
     * => GPIO121 SP0_SO => GPIO122 SP0_CS => GPIO120 SP0_CLK => GPIO119
     */
    public static final int[] GPIO_PORT_NUM = {
            // 128, 113, 118, 127, 126, 123, 53, 54, 55, 56, 57, 37, 36, 112,
            // 117, 121, 122, 120, 119,

            // 119, 120, 122, 121, 53, 123, 126, 127, 36, 37, 57, 56, 54, 118,
            // 113, 128

            // 119, 120, 121, 53, 123, 126, 127, 57, 56, 118, 113, 128

            113, 118, 119, 120, 121, 122
    };

    public static boolean GPIO_SINGLE_TEST = false;

    public static int mGPIOIndex = 0;

    public static boolean mGPIOEnable = true;

    public static boolean mTestFirst = true;

    private static void setGPIO(int port) {
        File file = new File(GPIO_PATH);
        if (!file.exists()) {
            printLog(GPIO_PATH + "is not exists!");
            return;
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(GPIO_PATH));

            printLog("mGPIOEnable: " + mGPIOEnable + " - port: " + port);

            if (mGPIOEnable) {
                // mGPIOEnable = false;

                String data = port + " 0";
                writer.write(data);
                writer.flush();

                SystemClock.sleep(GPIO_TEST_DELAY_TIME);
            } else {
                // mGPIOEnable = true;

                String data = port + " 1";
                writer.write(data);
                writer.flush();

                SystemClock.sleep(GPIO_TEST_DELAY_TIME);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @SuppressWarnings("unused")
    private static void setGPIOExt(int port) {
        File file = new File(GPIO_PATH);
        if (!file.exists()) {
            printLog(GPIO_PATH + "is not exists!");
            return;
        }

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(GPIO_PATH));

            String data = port + " 0";
            writer.write(data);
            writer.flush();

            SystemClock.sleep(GPIO_TEST_DELAY_TIME);

            data = port + " 1";
            writer.write(data);
            writer.flush();

            SystemClock.sleep(GPIO_TEST_DELAY_TIME);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static boolean setGPIOStatus() {
        if (!GPIO_SINGLE_TEST) {
            mGPIOIndex = 0;
        } else {
            if (mGPIOIndex >= GPIO_PORT_NUM.length) {
                mGPIOIndex = 0;
            }
        }
        for (int i = mGPIOIndex; i < GPIO_PORT_NUM.length; i++) {
            if (mTestFirst) {
                mGPIOEnable = true;
            } else {
                mGPIOEnable = false;
            }
            setGPIO(GPIO_PORT_NUM[i]);
            // setGPIOExt(GPIO_PORT_NUM[i]);

            if (GPIO_SINGLE_TEST) {
                if (!mGPIOEnable) {
                    mGPIOIndex++;
                }
                break;
            }
        }

        if (mTestFirst) {
            mTestFirst = false;
        } else {
            mTestFirst = true;
        }

        return true;
    }

}
