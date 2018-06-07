package com.autochips.picturebrowser;

import com.autochips.picturebrowser.ShowImgActivity.PicturePresentation;
import com.autochips.picturebrowser.ThumbnailsActivity.ThumbPresentation;
import com.autochips.picturebrowser.data.LocalData;
import com.autochips.picturebrowser.data.LocalFiles;
import com.autochips.picturebrowser.data.MediaData;
import com.autochips.picturebrowser.status.StatusHub;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;

public class PictureApplication extends Application {
    private ThumbPresentation mThumbPresentation = null;
    private PicturePresentation mPicturePresentation = null;
    private boolean mIsRearShow = false;

    @Override
    public void onCreate() {
        super.onCreate(); 
        // init Android-Universal-Image-Loader
        // https://github/nostra13/Android-Universal-Image-Loader
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);
        ImageLoader.getInstance().init(configuration);
    }
   
    public ThumbPresentation getThumbPresentation() {
        return mThumbPresentation;
    }

    public void setThumbPresentation(ThumbPresentation mThumbPresentation) {
        this.mThumbPresentation = mThumbPresentation;
    }

    public PicturePresentation getPicturePresentation() {
        return mPicturePresentation;
    }

    public void setPicturePresentation(PicturePresentation mPicturePresentation) {
        this.mPicturePresentation = mPicturePresentation;
    }

    public void DismissRearShow() {
        if (null != mThumbPresentation) {
            mThumbPresentation.dismiss();
            mThumbPresentation = null;
        }

        if (null != mPicturePresentation) {
            mPicturePresentation.dismiss();
            mPicturePresentation = null;
        }
    }

    public boolean isRearShow() {
        return mIsRearShow;
    }

    public void setIsRearShow(boolean mIsRearShow) {
        this.mIsRearShow = mIsRearShow;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
