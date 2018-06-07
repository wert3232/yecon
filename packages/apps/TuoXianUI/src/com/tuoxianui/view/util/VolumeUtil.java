package com.tuoxianui.view.util;

import android.app.Instrumentation;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;

public class VolumeUtil {
	public static boolean muteToggle(Context context){
		sendKeyCode(KeyEvent.KEYCODE_VOLUME_MUTE);	
		
		/*AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int v = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        Log.e("STREAM_SYSTEM", v+"");
        v = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.e("STREAM_MUSIC", v+"");
        v = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        Log.e("STREAM_RING", v+"");
        v = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        Log.e("STREAM_VOICE_CALL", v+"");
        v = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        Log.e("STREAM_ALARM", v+"");
        v = mAudioManager.getStreamVolume(AudioManager.STREAM_DTMF);
        Log.e("STREAM_DTMF", v+"");
        v = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        Log.e("STREAM_NOTIFICATION", v+"");*/
		return true;
	}
	
	public void setMute(Context context,boolean mute){
		
	}
	
	public static boolean isMute(Context context){
		
		AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int v = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		return v == 0;
	}
	private static void sendKeyCode(final int keyCode) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    
                }
                
            }       
        }.start();
    }
}
