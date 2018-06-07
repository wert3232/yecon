package com.adnroid.file.imageutil;

import android.content.Context;
import android.graphics.Bitmap;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

public class MyPhotoView extends PhotoView{

	public MyPhotoView(Context context) {
		super(context);
	}
	/**
     * 初始化ImageView图片
     * @param bitmap
     */
    public void setImage(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        if (null != mAttacher) {
            mAttacher.update();
        }
    }

    
}
