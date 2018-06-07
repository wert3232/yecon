package com.autochips.picturebrowser.utils;

import java.util.Random;

public class RandomIndex {
    private int[] mIndexs = null;
    private int mCurIndex = -1;

    public RandomIndex() {
    }

    private void generateRandomIndex(int[] indexs, int range) {

        if (indexs.length != range) {
            return;
        }

        for (int i = 0; i < range; i++) {
            indexs[i] = i;
        }

        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < range; i++) {
            int switchIndex = rand.nextInt(range);
            indexs[i] += indexs[switchIndex];
            indexs[switchIndex] = indexs[i] - indexs[switchIndex];
            indexs[i] = indexs[i] - indexs[switchIndex];
        }
    }

    public void setRange(int range) {
        if (range <= 0) {
            return;
        }
        mCurIndex = 0;
        if (mIndexs != null && range == mIndexs.length) {
            return;
        }
        mIndexs = new int[range];
        generateRandomIndex(mIndexs, range);
    }

    public int next() {
        if (mIndexs == null) {
            return -1;
        }
        int size = mIndexs.length;
        int cur = mCurIndex;
        mCurIndex = (mCurIndex + 1) % size;
        return mIndexs[cur];
    }

    public int prev() {
        if (mIndexs == null) {
            return -1;
        }
        int size = mIndexs.length;
        mCurIndex = (mCurIndex + size - 1) % size;
        return mIndexs[mCurIndex];
    }
}
