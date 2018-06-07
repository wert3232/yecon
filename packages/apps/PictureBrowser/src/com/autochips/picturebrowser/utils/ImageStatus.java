package com.autochips.picturebrowser.utils;

public class ImageStatus {

    private float mTop = 0f;
    private float mLeft = 0f;
    private float mRight = 0f;
    private float mBottom = 0f;

    public float getTop() {
        return mTop;
    }

    public void setTop(float mTop) {
        this.mTop = mTop;
    }

    public float getLeft() {
        return mLeft;
    }

    public void setLeft(float mLeft) {
        this.mLeft = mLeft;
    }

    public float getRight() {
        return mRight;
    }

    public void setRight(float mRight) {
        this.mRight = mRight;
    }

    public float getBottom() {
        return mBottom;
    }

    public void setBottom(float mBottom) {
        this.mBottom = mBottom;
    }

    public float getWidth() {
        return mRight - mLeft;
    }

    public float getHeight() {
        return mBottom - mTop;
    }

    @Override
    public String toString() {
        return "x: " + mLeft + " y: " + mTop + " w: " + (mRight - mLeft)
                + " h: " + (mBottom - mTop);
    }
}
