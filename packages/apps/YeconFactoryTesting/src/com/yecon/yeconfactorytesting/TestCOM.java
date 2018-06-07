
package com.yecon.yeconfactorytesting;

import static com.yecon.yeconfactorytesting.Constants.*;
import static com.yecon.yeconfactorytesting.FactoryTestUtil.*;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Handler;
import android.os.SystemClock;
import android.os.SystemProperties;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TestCOM {
    private static final Object lock = new Object();

    private boolean mStopComThread = false;

    private LocalSocket mSocket;
    private InputStream mIn;
    private OutputStream mOut;

    private void comDisconnect() {
        try {
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }

            if (mIn != null) {
                mIn.close();
                mIn = null;
            }

            if (mOut != null) {
                mOut.close();
                mOut = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void comConnect(Handler handler) {
        int retryCount = 0;

        try {
            while (true) {
                LocalSocket socket = null;
                LocalSocketAddress address = null;
                try {
                    socket = new LocalSocket();
                    address = new LocalSocketAddress(UART_SOCKET_NAME,
                            LocalSocketAddress.Namespace.RESERVED);
                    socket.connect(address);
                } catch (IOException ex) {
                    try {
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (IOException er) {
                    }

                    retryCount++;

                    printLog("Counldn't find " + UART_SOCKET_NAME + " socket after "
                            + retryCount + " times, continuing to retry silently");

                    if (retryCount == 50) {
                        handler.sendEmptyMessage(MSG_COM_ALL_ERROR);

                        return;
                    }

                    synchronized (lock) {
                        if (mStopComThread) {
                            return;
                        }
                    }

                    SystemClock.sleep(30);

                    continue;
                }

                retryCount = 0;

                mSocket = socket;
                mIn = mSocket.getInputStream();
                mOut = mSocket.getOutputStream();

                break;
            }

            if (mSocket.isConnected()) {
                printLog("Sender connected to " + UART_SOCKET_NAME + " socket");
            }
        } catch (Throwable e) {
            printLog("Uncaught exception: " + e.getMessage());

            e.printStackTrace();
        }
    }

    private void sendComRequest(byte[] request, int offset, int count) {
        try {
            if (mOut != null) {
                printLog("sendComRequest - start");
                printLog(request, count);

                mOut.write(request, offset, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean readComMessage(InputStream is, int comType) {
        boolean ret = false;

        int countRead = 0;
        byte buf[] = new byte[COM_COMMAND_BYTES];

        printLog("readComMessage - start - comType: " + comType);

        try {
            mSocket.setSoTimeout(READ_SOCKET_DELAY_TIME);

            countRead = is.read(buf, 0, COM_COMMAND_BYTES);
        } catch (IOException e) {
            printLog("readComMessage - error: " + e.toString());
            e.printStackTrace();
            return ret;
        }

        printLog("readComMessage - countRead: " + countRead);

        if (countRead != COM_COMMAND_BYTES) {
            return ret;
        }

        printLog(buf, COM_COMMAND_BYTES);

        // 0X0E, 0X11，0X22，0X03, 0X00，0XE0 -- success
        // 0X0E, 0X11，0X22，0X03, 0XFF，0XE0 -- error
        if (((buf[0] & 0xFF) == 0x0E) && ((buf[1] & 0xFF) == 0x11) && ((buf[2] & 0x22) == 0x03)
                && ((buf[3] & 0xFF) == comType) && ((buf[5] & 0xFF) == 0xE0)) {
            ret = true;
        }

        if (((buf[4] & 0xFF) == 0x00)) {
            ret = true;
        } else {
            ret = false;
        }

        return ret;
    }

    public void setStopComThread(boolean stopComThread) {
        this.mStopComThread = stopComThread;
    }

    public void singleComTest(Handler handler, int comType) {
        try {
            int count = 0;
            boolean comTestSuccess = false;
            while (count++ < COM_TEST_COUNT) {
                byte[] comCmdData = {
                        (byte) 0x0F, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0x00,
                        (byte) 0xF0
                };
                comCmdData[4] = (byte) comType;
                sendComRequest(comCmdData, 0, comCmdData.length);

                boolean ret = readComMessage(mIn, comType);
                if (ret) {
                    comTestSuccess = true;

                    count = COM_TEST_COUNT;
                } else {
                    comTestSuccess = false;
                }
                printLog("readComMessage - ret: " + ret + " - count: " + count);
            }

            int what = MSG_COM3_ERROR;
            switch (comType) {
                case COM3_TEST:
                    if (!comTestSuccess) {
                        what = MSG_COM3_ERROR;
                    } else if (comTestSuccess) {
                        what = MSG_COM3_SUCCESS;
                    }
                    break;

                case COM4_TEST:
                    if (!comTestSuccess) {
                        what = MSG_COM4_ERROR;
                    } else if (comTestSuccess) {
                        what = MSG_COM4_SUCCESS;
                    }
                    break;

                case COM6_TEST:
                    if (!comTestSuccess) {
                        what = MSG_COM6_ERROR;
                    } else if (comTestSuccess) {
                        what = MSG_COM6_SUCCESS;
                    }
                    break;
            }
            handler.sendEmptyMessage(what);
        } catch (Throwable e) {
            printLog("Uncaught exception: " + e.getMessage());

            e.printStackTrace();
        }
    }

    public boolean startUartService(boolean enable) {
        if (enable) {
            SystemProperties.set("ctl.start", "uart_service");

            int count = 0;
            String svcState = "";
            while (true) {
                SystemClock.sleep(10);
                svcState = SystemProperties.get("init.svc.uart_service");
                printLog("onComAutoTest - svcState: " + svcState + " - count: " + count);
                if (svcState.equals("running")) {
                    break;
                }
                count++;
                if (count == 50) {
                    return false;
                }
            }
        } else {
            sendComRequest(COM_CMD_QUIT, 0, COM_CMD_QUIT.length);

            SystemClock.sleep(100);

            SystemProperties.set("ctl.stop", "uart_service");
        }
        return true;
    }

    public void onComAutoTest(Handler handler) {
        printLog("onComAutoTest - start");

        if (!startUartService(true)) {
            handler.sendEmptyMessage(MSG_COM_ALL_ERROR);
            return;
        }

        comDisconnect();

        comConnect(handler);

        handler.sendEmptyMessage(MSG_COM3_TEST);

        printLog("onComAutoTest - end");
    }

}
