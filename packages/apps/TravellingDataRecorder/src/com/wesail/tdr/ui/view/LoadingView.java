package com.wesail.tdr.ui.view;

import com.wesail.tdr.R;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

public class LoadingView extends ImageView{
	private Animator animator;
	public LoadingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setImageDrawable(getResources().getDrawable(R.drawable.loading));
	}
	public LoadingView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}
	public LoadingView(Context context) {
		this(context,null);
	}
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		animator = AnimatorInflater.loadAnimator(getContext(), R.anim.loading);
		animator.setTarget(this);
		animator.start();
	}
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if(animator != null){	
			if (visibility == VISIBLE) {
				animator.cancel();
				animator.start();
			}else {
				animator.cancel();
			}
		}
	}

}
