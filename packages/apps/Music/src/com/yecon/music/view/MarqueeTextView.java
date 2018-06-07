package com.yecon.music.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView
{
	public MarqueeTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect)
	{
		// TODO Auto-generated method stub
		if(focused) super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus)
	{
		// TODO Auto-generated method stub
		if(hasWindowFocus) super.onWindowFocusChanged(hasWindowFocus);
	}
	
	@Override
	public boolean isFocused()
	{
		return true;
	}
}