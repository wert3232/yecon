package android.exdevport;

public class AtvPort {
	private static final String TAG = "AtvPort";
	static { 
		System.loadLibrary("ExDevPort"); 
	}
	public native void test(); 
	public native int init(int tvRegion, int[] userData);
	public native void exit();
	public native int[] getConfig();
	public native int[] getStatus();
	public native int sendUserMsg(int cmd, int keyValue);
	public native int timerSchedule(int par0, int par1);
	public native void notifyAvinSignalStatus(int ready);
}
