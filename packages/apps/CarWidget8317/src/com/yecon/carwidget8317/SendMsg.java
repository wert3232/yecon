package com.yecon.carwidget8317;

import android.content.Context;
import android.content.Intent;

public class SendMsg {
	public static void send(Context context, String msg) {
		Intent intent = new Intent(msg);
		context.sendBroadcast(intent);
	}
}
