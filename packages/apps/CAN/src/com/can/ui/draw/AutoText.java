package com.can.ui.draw;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

public class AutoText extends TextView{

	public AutoText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		// TODO Auto-generated method stub
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasWindowFocus);
	}

	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		// TODO Auto-generated method stub
		return true;
	}
}
