
package com.yecon.gpstest;

import java.text.NumberFormat;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

public class Clock {

    private static final int STOP = 0;
    // private static final int COUNTDOWN = 1;

    public int v;

    public boolean isStarted = false;
    clockThread threadS;
    countDownThread threadC;
    GPSTestActivity parent;
    dsechandler dsech;
    sechandler sech;
    minhandler minh;

    int dsec = 0;
    int sec = 0;
    int min = 0;

    public NumberFormat nf;

    public Clock(GPSTestActivity parent) {
        this.parent = parent;
        nf = NumberFormat.getInstance();

        nf.setMinimumIntegerDigits(2); // The minimum Digits required is 2
        nf.setMaximumIntegerDigits(2); // The maximum Digits required is 2

        dsech = new dsechandler();
        sech = new sechandler();
        minh = new minhandler();

    }

    public void startcount() {
        if (!isStarted) {

            dsec = Integer.valueOf(parent.dsecondsView.getText().toString());
            sec = Integer.valueOf(parent.secondsView.getText().toString());
            min = Integer.valueOf(parent.minView.getText().toString());

            isStarted = true;
            if (v == STOP) {
                threadS = new clockThread();
                threadS.start();
            } else {
                threadC = new countDownThread();
                threadC.start();
            }
        }
    }

    public void stopcount() {
        if (isStarted) {
            dsec = Integer.valueOf(parent.dsecondsView.getText().toString());
            sec = Integer.valueOf(parent.secondsView.getText().toString());
            min = Integer.valueOf(parent.minView.getText().toString());

            if (v == STOP) {
                if (threadS.isAlive())
                    threadS.interrupt();
            } else {
                if (threadC.isAlive())
                    threadC.interrupt();
            }
            isStarted = false;
        }
    }

    private class clockThread extends Thread {
        @Override
        public void run() {

            while (isStarted) {
                dsec++;

                if (dsec == 10) {
                    sec++;
                    dsec = 0;
                    sech.sendEmptyMessage(MAX_PRIORITY);

                    if (sec == 60) {
                        min++;
                        sec = 0;
                        minh.sendEmptyMessage(MAX_PRIORITY);
                    }
                }

                dsech.sendEmptyMessage(MAX_PRIORITY);

                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    private class countDownThread extends Thread {
        @Override
        public void run() {
            if (min == 0 && sec == 0 && dsec == 0) {
                isStarted = false;
                // parent.modeMenuItem.setEnabled(false);
                return;
            }

            while (true) {
                if (dsec == 0) {
                    if (sec == 0) {
                        if (min != 0) {
                            min--;
                            sec = 60;
                            minh.sendEmptyMessage(MAX_PRIORITY);
                        }
                    }

                    if (sec != 0) {
                        sec--;
                        dsec = 10;
                        sech.sendEmptyMessage(MAX_PRIORITY);
                    }
                }
                dsec--;

                dsech.sendEmptyMessage(MAX_PRIORITY);

                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private class dsechandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            parent.dsecondsView.setText("" + nf.format(dsec));
        }
    }

    @SuppressLint("HandlerLeak")
    private class sechandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            parent.secondsView.setText("" + nf.format(sec));
        }
    }

    @SuppressLint("HandlerLeak")
    private class minhandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            parent.minView.setText("" + nf.format(min));
        }
    }

}
