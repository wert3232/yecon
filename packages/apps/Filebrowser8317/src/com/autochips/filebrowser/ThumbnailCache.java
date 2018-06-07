package com.autochips.filebrowser;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images;

public class ThumbnailCache {
	static ConcurrentHashMap<String, SoftReference<Bitmap>> imageCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>();
	static ConcurrentHashMap<String, ThumbnailLoadTask> taskCache = new ConcurrentHashMap<String, ThumbnailLoadTask>();

	static Bitmap getThumbnail(String filepath, int filetype,
			final ThumbnailLoadCallback callback) {
		if (imageCache == null) {
			imageCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>();
		}
		if (taskCache == null) {
			taskCache = new ConcurrentHashMap<String, ThumbnailLoadTask>();
		}
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				callback.onLoad((Bitmap)msg.obj);
			}
		};

		if (callback == null) {
			return null;
		}
		SoftReference<Bitmap> ref = imageCache.get(filepath);
		if (ref != null) {
			Bitmap bitmap = ref.get();
			if (bitmap != null) {
				return bitmap;
			}
		}

		ThumbnailLoadTask task = taskCache.get(filepath);
		if (task != null) {
			return null;
		}
		task = new ThumbnailLoadTask() {
			@Override
			protected void onPostExecute(Bitmap result) {
				if (isCancelled())
					return;
				if (taskCache != null) {
					taskCache.remove(this.path);
				}

				if (result != null) {
					if (imageCache != null) {
						imageCache.put(this.path, new SoftReference<Bitmap>(
								result));
					}
					handler.sendMessage(handler.obtainMessage(0,result));
				} else {
					callback.onLoadFail();
				}
			}
		};
		taskCache.put(filepath, task);
		task.execute(filepath, String.valueOf(filetype));
		return null;

	}

	static void clearCache() {
		if (imageCache != null) {
			imageCache.clear();
		}
		if (taskCache != null) {
			taskCache.clear();
		}
	}

	static class ThumbnailLoadTask extends AsyncTask<String, Void, Bitmap> {
		private final static int IMAGE_PREVIEW_WIDTH = 96;
		private final static int IMAGE_PREVIEW_HEIGHT = 96;

		protected String path;

		@Override
		protected Bitmap doInBackground(String... params) {
			path = params[0];
			int filetype = Integer.valueOf(params[1]);
			return getBitmapFromPath(path, filetype);
		}

		private Bitmap getBitmapFromPath(String filepath, int filetype) {
			Bitmap bitmap = null;
			bitmap = getPreviewBmp(filepath, filetype);
			return bitmap;
		}

		private Bitmap getPreviewBmp(String path, int type) {
			if (type == 1)
				return getPhotoThumbnail(path);
			else if (type == 2)
				return getVideoThumbnail(path);

			return null;
		}

		private Bitmap getPhotoThumbnail(String path) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);

			int width = options.outWidth;
			int height = options.outHeight;

			if (width == -1 || height == -1 || width > 4096 || height > 4096) {
				return null;
			}

			float scale = getBitmapScale(width, height);

			options.inJustDecodeBounds = false;
			options.inSampleSize = (int) (1 / scale);
			options.inPurgeable = true;
			options.inInputShareable = true;
			Bitmap previewBmp = BitmapFactory.decodeFile(path, options);
			return previewBmp;
		}

		private Bitmap getVideoThumbnail(String path) {
			Bitmap previewBmp = null;
			previewBmp = ThumbnailUtils.createVideoThumbnail(path,
					Images.Thumbnails.MICRO_KIND);

			return previewBmp;
		}

		private float getBitmapScale(int BitmapWidth, int BitmapHeight) {
			float scale = (float) 1.0;
			if (IMAGE_PREVIEW_WIDTH != 0 && IMAGE_PREVIEW_HEIGHT != 0) {
				if (BitmapWidth > IMAGE_PREVIEW_WIDTH
						|| BitmapHeight > IMAGE_PREVIEW_HEIGHT) {
					float xScale = BitmapWidth / (float) IMAGE_PREVIEW_WIDTH;
					float yScale = BitmapHeight / (float) IMAGE_PREVIEW_HEIGHT;

					if (xScale > yScale) {
						scale = xScale;
					} else {
						scale = yScale;
					}
				}
			}
			return (float) (1 / scale);
		}

	}

	interface ThumbnailLoadCallback {
		void onLoad(Bitmap bitmap);

		void onLoadFail();
	}
}
