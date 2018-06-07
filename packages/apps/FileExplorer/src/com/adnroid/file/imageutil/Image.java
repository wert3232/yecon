package com.adnroid.file.imageutil;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class Image {
	private String path;
	private Bitmap bitmap;
	private Drawable drawable;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}
}
