package com.can.ui.draw;

import com.can.activity.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class FuelSeekBar extends SeekBar{
	private boolean mCanMove = true;
	public FuelSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.FuelSeekBar); 
		mCanMove = typedArray.getBoolean(R.styleable.FuelSeekBar_can_move, true);
		typedArray.recycle();
	}
	
	public FuelSeekBar(Context context) {
		super(context);
	}

//    public final int roundCorners = 15;//就是改变这个值，就可以改变自定义progressbar左右两端的圆角大小了，使用于自定义图片的情况，  
    Shape getDrawableShape() {  
        final float[] roundedCorners = new float[] { 0, 0, 0, 0, 0, 0, 0, 0 };  
//        for(int i=0;i<roundedCorners.length;i++){  
//            roundedCorners[i] = dp2px(getContext(), roundCorners);  
//        }  
        return new RoundRectShape(roundedCorners, null, null);  
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mCanMove) {
			return super.onTouchEvent(event);
		} else {
			return false;
		}
	}
    
    
}
