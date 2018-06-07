package com.android.file;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.PaintDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.adnroid.file.MyActivity;
import com.adnroid.file.Util;
import com.adnroid.file.MyActivity.searchEntity;
import com.adnroid.file.imageutil.Image;
import com.adnroid.file.imageutil.MyPhotoView;
import com.adnroid.file.imageutil.MyViewPager;

public class PhotoActivity extends Activity  {
	private MyViewPager pager;
	LinearLayout linea;
	private List<Image> images;
	private ImageAdapter adapter;
	String[] numzs;
	int position = 0;
	int l = 0 ;
	private PopupWindow MyPopWindowtop;
	ImageView back;
	TextView current_path1;
	String dirname;
	String fpath;
	//public static ProgressDialog m_Dialogts = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		
		Log.e("进来PhotoActivity", "进来PhotoActivity");
		Intent intent = getIntent();
		// 拿到上个Activity传过来的参数
		Bundle bundle = intent.getExtras();
		numzs = bundle.getStringArray("photopath");
		dirname = bundle.getString("dirpath");
		//position = bundle.getInt("num");
		position = 0;
		fpath=bundle.getString("fpath");
		pager = (MyViewPager) findViewById(R.id.my_pager);
		linea = (LinearLayout) findViewById(R.id.linea);
//		m_Dialogts = ProgressDialog.show(PhotoActivity.this,getResources().getString(R.string.photo_load),
//				getResources().getString(R.string.photo_load_msg) , true , false);
		images = new ArrayList<Image>();	
		init();	
		
		Log.e("imageslength", images.size()+"");
		pager.setAdapter(new ImageAdapter(images, position));
		pager.setCurrentItem(position);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		initpopuwindow();
		super.onWindowFocusChanged(hasFocus);
	}

	private void init() {
		
//		if(position>6){
//			Log.e("position>6", "position>6");
//			for (int i = 0; i < numzs.length; i++) {
				try {
					//	Bitmap pic = BitmapFactory.decodeFile(numzs[i]);
						Bitmap pic = Util.compressImageFromFile(fpath);
						Image image = new Image();
						image.setBitmap(pic);
						images.add(image);
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
//		}else{
//			Log.e("position<6", "position<6");
//			for (int i = 0; i < position+3; i++) {
//				try {
//					//	Bitmap pic = BitmapFactory.decodeFile(numzs[i]);
//						Bitmap pic = Util.compressImageFromFile(numzs[i]);
//						Image image = new Image();
//						image.setBitmap(pic);
//						images.add(image);
//					} catch (Throwable t) {
//						t.printStackTrace();
//					}
//					
//				}
//		}
//	}

	// 初始化popuwindow
	public void initpopuwindow() {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.popuwindowlayout, null);
		MyPopWindowtop = new PopupWindow(layout, LayoutParams.FILL_PARENT, 100);
		MyPopWindowtop.setFocusable(false);
		back = (ImageView) layout.findViewById(R.id.pop_back);
		current_path1 = (TextView) layout.findViewById(R.id.current_path1);
		MyPopWindowtop.showAtLocation(linea, Gravity.TOP
				| Gravity.CENTER_HORIZONTAL, 0, 0);
		current_path1.setText(dirname);
		MyPopWindowtop.setFocusable(true);
	
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
				MyPopWindowtop.dismiss();
				MyPopWindowtop = null;
			}
		});
	}
	
	public class ImageAdapter extends PagerAdapter {
		private List<Image> list;
		int a;
		public ImageAdapter(List<Image> list, int a) {
			this.list = list;
			this.a = a;
		}
		@Override
		public View instantiateItem(ViewGroup container, int position) {
			if (l == 0) {
				position = a;
				l++;
			}
			Image image = list.get(position);
			MyPhotoView photoView = null;
			if (null != image) {
				photoView = new MyPhotoView(container.getContext());
				photoView.setImage(image.getBitmap());	

				//解决单击与双击的冲突
				photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
					@Override
					public void onPhotoTap(View arg0, float arg1, float arg2) {
						if (MyPopWindowtop.isShowing()){
							MyPopWindowtop.dismiss();
						} else {
							initpopuwindow();
						}
					}
				});					
			}
			container.addView(photoView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			
			return photoView;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
		@Override
		public int getCount() {
			return images.size();
		}
	}
	
	
}