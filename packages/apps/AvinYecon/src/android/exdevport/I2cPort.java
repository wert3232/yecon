package android.exdevport;

public class I2cPort {
	private static final String TAG = "I2cPort";

	public native void test(); 
	public native int i2cOpen(String dev, int slaveAddr);
	public native void i2cClose(); 
	public native short i2cRead16(int slave_address, int reg_address, int reg_bytes );
	public native int i2cWrite16(short data, int slave_address, int reg_address, int reg_bytes);
	static { 
		System.loadLibrary("ExDevPort"); 
		}
}
