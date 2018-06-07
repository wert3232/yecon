package com.yecon.carwidget8317;

import android.content.Context;

public abstract class WidgetsBase {
	public static final String ACTION_APPWIDGET_UPDATE = "com.yecon.UPDATE_APP_WIDGET";
	public static final String ACTION_ENTER = "com.yecon.carwidgets.gotoactivity";
	public static final String ACTION_NEXT = "com.yecon.carwidgets.next";
	public static final String ACTION_PAUSE = "com.yecon.carwidgets.pause";
	public static final String ACTION_PLAY = "com.yecon.carwidgets.play";
	public static final String ACTION_PLAYPAUSE = "com.yecon.carwidgets.playpause";
	public static final String ACTION_PREVIOUS = "com.yecon.carwidgets.previous";
	public static final String ACTION_STOP = "com.yecon.carwidgets.stop";

	protected Context mContext;

	public WidgetsBase() {
		this.mContext = null;
	}

	public void destroy(final Context context) {
	}

	public void init(final Context context) {
	}

	public void update(final Context context) {
	}

	public void notify(final Context context, final String s) {
	}
}
