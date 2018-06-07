package com.yecon.imagebrowser;

import android.app.Application;
import android.graphics.Bitmap.Config;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class ImageBrowserApplication extends Application {

	private static final int MAX_WIDTH = 1024;	// 图片高度
	private static final int MAX_HEIGHT = 600;	// 图片宽度
	private static final int MAX_COUNT = 12;	// 最大缓存: 每一行4张,每页两行,缓存一行
	private static final int PERPIX_SIZE = 4;	// 每个像素大小
	private static final int ELEM_SIZE = MAX_WIDTH * MAX_HEIGHT * PERPIX_SIZE; // 单个图片最大大小
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		DisplayImageOptions defaultOptions = new DisplayImageOptions
				.Builder()
				.showImageForEmptyUri(R.drawable.empty_photo) 
				.showImageOnFail(R.drawable.empty_photo) 
				.cacheInMemory(false)
				.cacheOnDisc(true)
				.bitmapConfig(Config.RGB_565)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration
				.Builder(getApplicationContext())
				.memoryCacheExtraOptions(MAX_WIDTH, MAX_HEIGHT) // max width, max height
				.defaultDisplayImageOptions(defaultOptions)
				.discCacheSize(MAX_COUNT * ELEM_SIZE)
				.discCacheFileCount(MAX_COUNT)
				.build();
		ImageLoader.getInstance().init(config);
	}
}
