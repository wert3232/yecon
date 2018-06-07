package com.autochips.picturebrowser;
import java.util.ArrayList;
import java.util.Iterator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class MediaButtonIntentReceiver extends BroadcastReceiver {
	private String TAG = "MediaButtonIntentReceiver";
	
	public static interface Callback{
		void onMediaKey(int action, int keycode);
	}
	private static ArrayList<Callback> callbackList = new ArrayList<Callback>();
	public static void registerListener(Callback callback){
		if(!callbackList.contains(callback)){
			callbackList.add(callback);
		}
	}
	public static void unregisterListener(Callback callback){
		callbackList.remove(callback);
	}
    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        // DebugUtil.printLog("action: " + intentAction);

        if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
            KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (event == null) {
                return;
            }
            
            int keycode = event.getKeyCode();
            int action = event.getAction();
            long eventtime = event.getEventTime();
            
            Iterator<Callback> it = callbackList.iterator();
            Callback callback;
			while(it.hasNext()){
				callback = it.next();
				callback.onMediaKey(action, keycode);
			}            

        }
    }

}