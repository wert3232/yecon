package com.can.ui.draw;

import com.can.activity.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

/**
 * @ClassName: CANBusWindLevelView
 * @Description: 用于汽车风量的显示
 * @author 郭会专
 * @date 2016年3月21日 下午3:19:44
 * 
 */
public class Windlv extends View {
	private static final int MAX_WIND_LEVEL = 7;
	private static final int DEFAULT_SPACING = 5;

	private int mSpacing = DEFAULT_SPACING;
	private int mMaxLevel = MAX_WIND_LEVEL;
	private int mCurLevel = 4;
	private Bitmap mBmpSelect = null;
	private Bitmap mBmpNormal = null;
	private Matrix mMatrix = null;

	public Windlv(Context context) {
		this(context, null);
	}

	public Windlv(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public Windlv(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.WindLevel, defStyleAttr, 0);

		// 获取背景图和前景图及最大值
		try {
			mBmpNormal = BitmapFactory.decodeResource(getResources(),
					a.getResourceId(R.styleable.WindLevel_BmpNormal, 0));
			mBmpSelect = BitmapFactory.decodeResource(getResources(),
					a.getResourceId(R.styleable.WindLevel_BmpSelect, 0));
			mMaxLevel = a.getInteger(R.styleable.WindLevel_MaxLevel,
					MAX_WIND_LEVEL);

			mSpacing = a.getInteger(R.styleable.WindLevel_Spacing,
					DEFAULT_SPACING);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			a.recycle();
		}

		mMatrix = new Matrix();
	}

	/**
	 * 获取最大风量
	 * 
	 * @return mMaxLevel
	 */
	public int getMaxLevel() {
		return mMaxLevel;
	}

	/**
	 * 设置最大风量
	 * 
	 * @param mMaxLevel
	 */
	public void setMaxLevel(int mMaxLevel) {
		this.mMaxLevel = ((mMaxLevel > 0) ? mMaxLevel : MAX_WIND_LEVEL);
	}

	/**
	 * 设置当前风量
	 * 
	 * @return mCurLevel
	 */
	public int getCurLevel() {
		return mCurLevel;
	}

	/**
	 * 获取当前风量
	 * 
	 * @param mCurLevel
	 */
	public void setCurLevel(int mCurLevel) {
		this.mCurLevel = mCurLevel;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

		if (mBmpSelect != null && mBmpNormal != null && mMaxLevel > 0) {
			// 获取当前空间的绘图区域宽度 (目前是横向显示的,暂时只考虑宽度)
			float fWidth = getWidth() - (mSpacing * mMaxLevel);
			int iSrcW = mBmpNormal.getWidth();
			int iSrcH = mBmpNormal.getHeight();

			float fScale = 1;
			if (iSrcW * mMaxLevel > fWidth) {
				// 目前空间区域无法绘制最大级别 需要对 bmp 进行 缩放操作
				fScale = (fWidth / (float) (mMaxLevel)) / (float) iSrcW;
			}

			// y方向居中显示
			float fHeight = (getHeight() - iSrcH * fScale) / 2;

			// 绘制背景
			for (int i = 0; i < mMaxLevel; i++) {
				mMatrix.setScale(fScale, fScale);
				mMatrix.postTranslate((iSrcW * fScale + mSpacing) * i, fHeight);
				canvas.drawBitmap(mBmpNormal, mMatrix, null);
			}

			// 绘制前景
			for (int i = 0; i < mCurLevel; i++) {
				mMatrix.setScale(fScale, fScale);
				mMatrix.postTranslate((iSrcW * fScale + mSpacing) * i, fHeight);
				canvas.drawBitmap(mBmpSelect, mMatrix, null);
			}

		} else {
			super.onDraw(canvas);
		}

	}
}
