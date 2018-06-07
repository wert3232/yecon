
package com.yecon.metazoneop;

import java.io.FileInputStream;

import android.os.Bundle;

import android.os.RemoteException;
import android.os.ServiceManager;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import android.os.IPowerManager;

import com.android.org.bouncycastle.util.Arrays;
import com.autochips.metazone.AtcMetazone;

import com.yecon.metazone.YeconMetazone;


public class MetazoneOperateMainActivity extends Activity {
	private static final String TAG = "YECON_META";
	
	private static final String VOL_TYPE_SYSTEM = "SystemVolume";
	private static final String VOL_TYPE_BT_PHONE = "BTVolume";
	
	private static final String KEY_SD_SWAP = "persist.sys.sdcard.swap";
	private static final String KEY_GPS_IN_EXT = "persist.sys.sdcard.map_in_extsd";
	private static final String KEY_MCU_DEBUG_ENABLE = "persist.sys.mcu.debug";
	private static final String KEY_CONFIG_CHANGED = "persist.sys.config.changed";

	private static final int YECON_META_START_ADDR = 36;
	
	IPowerManager power ;
	private CheckBox chkbox_sdcard_swap;
	private CheckBox chkbox_gps_in_ext;
	private CheckBox chkbox_mcu_debugEn;
	
	private static boolean m_bSdSwap;
	private static boolean m_bGpsInExt;
	private static boolean m_bMcuDebug;

	//private YeconMetazone mYeconMetazone = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metazone_operate_main_activity);
    	power = IPowerManager.Stub.asInterface(
				ServiceManager.getService("power"));
	
        chkbox_sdcard_swap = (CheckBox)this.findViewById(R.id.cb_sdcard_swap);
        chkbox_gps_in_ext = (CheckBox)this.findViewById(R.id.cb_gps_data_ext);
        chkbox_mcu_debugEn = (CheckBox)this.findViewById(R.id.cb_mcu_debug_switch);

		//mYeconMetazone = new YeconMetazone();
		//Log.e(TAG,"<----YeconMetazone Lib Version is:"+mYeconMetazone.GetLibVersion()+"----->");
        ShowCheckBoxes();      
        Button btn_reset_value = (Button) findViewById(R.id.btn_reset_value);
        Button btn_save_and_reboot = (Button) findViewById(R.id.btn_save_and_reboot);
        Button btn_write_metazone = (Button)findViewById(R.id.btn_write_metazone_file);
        Button btn_reboot = (Button)findViewById(R.id.btn_reboot_system);
        Button btn_set_volume = (Button)findViewById(R.id.btn_set_system_volume);
        Button btn_set_btvol = (Button)findViewById(R.id.btn_set_bt_volume);
         
        btn_reset_value.setOnClickListener(new View.OnClickListener() {

        	@Override
        	public void onClick(View arg0) {
        		// TODO Auto-generated method stub
        		//Toast.makeText(this, "µã»÷ÁË°´Å¥", Toast.LENGTH_LONG).show();
        		ShowCheckBoxes();
        	}
        });
        
        btn_save_and_reboot.setOnClickListener(new View.OnClickListener() {

        	@Override
        	public void onClick(View arg0) {
        		// TODO Auto-generated method stub
        		//Toast.makeText(this, "µã»÷ÁË°´Å¥", Toast.LENGTH_LONG).show();
        		if(UpdateCheckBoxes())
        		{
        			RebootSystem();
        		}
        		else
        		{
        			Toast.makeText(getApplicationContext(), 
        					getResources().getText(R.string.nothing_changed),Toast.LENGTH_SHORT).show();
        		}
        	}
        });
    

    btn_write_metazone.setOnClickListener(new View.OnClickListener() {

    	@Override
    	public void onClick(View arg0) {
    		// TODO Auto-generated method stub
    		//Toast.makeText(this, "µã»÷ÁË°´Å¥", Toast.LENGTH_LONG).show();
    		AlertDialog.Builder dialog=new AlertDialog.Builder(MetazoneOperateMainActivity.this);
            dialog.setTitle(getResources().getText(R.string.dlg_title_metazone)).setIcon(android.R.drawable.ic_dialog_alert).setMessage(getResources().getText(R.string.dlg_msg_metazone)).setPositiveButton(getResources().getText(R.string.str_ok), 
            		new DialogInterface.OnClickListener() {
              
             @Override
             public void onClick(DialogInterface dialog, int which) {
             
                 // TODO Auto-generated method stub
            	 WriteMetazoneFile("/mnt/ext_sdcard1/metazone.bin");
             }
         }).setNegativeButton(getResources().getText(R.string.str_cancel), new DialogInterface.OnClickListener() {
              
              
             public void onClick(DialogInterface dialog, int which) {
                 // TODO Auto-generated method stub
                 dialog.cancel();//È¡Ïûµ¯³ö¿ò
             }
         }).create().show();
    	}
    });
    
    btn_reboot.setOnClickListener(new View.OnClickListener() {

    	@Override
    	public void onClick(View arg0) {
    		// TODO Auto-generated method stub
    		//Toast.makeText(this, "µã»÷ÁË°´Å¥", Toast.LENGTH_LONG).show();
    		  RebootSystem();
    		
    	}
    });
    
    btn_set_volume.setOnClickListener(new View.OnClickListener() {

    	@Override
    	public void onClick(View arg0) {
    		// TODO Auto-generated method stub
       		Intent intent = new Intent(MetazoneOperateMainActivity.this, SysVolumeSetting.class);
       		intent.putExtra("Type", VOL_TYPE_SYSTEM);   
    		startActivity(intent);
    		
    	}
    });
    
    btn_set_btvol.setOnClickListener(new View.OnClickListener() {

    	@Override
    	public void onClick(View arg0) {
    		// TODO Auto-generated method stub
       		Intent intent = new Intent(MetazoneOperateMainActivity.this, SysVolumeSetting.class);
       		intent.putExtra("Type", VOL_TYPE_BT_PHONE);   
    		startActivity(intent);
    		
    	}
    });
}
    
    void ShowCheckBoxes(){
    	int iSdSwap = YeconMetazone.GetSdSwap();			//SystemProperties.getInt(KEY_SD_SWAP, 0);
    	int iGpsInExt = YeconMetazone.GetGpsMapInExtSD();	//SystemProperties.getInt(KEY_GPS_IN_EXT, 0);
    	int iMcuDebugEn = SystemProperties.getInt(KEY_MCU_DEBUG_ENABLE, 0);
    	m_bSdSwap = iSdSwap > 0 ? true:false;
    	m_bGpsInExt = iGpsInExt > 0 ? true:false;
    	m_bMcuDebug = iMcuDebugEn > 0 ? true:false;
    	chkbox_sdcard_swap.setChecked(m_bSdSwap);
    	chkbox_gps_in_ext.setChecked(m_bGpsInExt);
    	chkbox_mcu_debugEn.setChecked(m_bMcuDebug);
    	
    }
    
    boolean UpdateCheckBoxes()
    {
    	boolean bPropChged = false;
    	if(chkbox_sdcard_swap.isChecked()!=m_bSdSwap)
    	{
    		bPropChged = true;
    		if(chkbox_sdcard_swap.isChecked())
    			SystemProperties.set(KEY_SD_SWAP, "1");
    		else
    			SystemProperties.set(KEY_SD_SWAP, "0");
    	}
    	
    	if(chkbox_gps_in_ext.isChecked() != m_bGpsInExt)
    	{
    		bPropChged = true;
    		if(chkbox_gps_in_ext.isChecked())
    			SystemProperties.set(KEY_GPS_IN_EXT, "1");
    		else
    			SystemProperties.set(KEY_GPS_IN_EXT, "0");
    	}
    	
    	if(chkbox_mcu_debugEn.isChecked() != m_bMcuDebug)
    	{
    		bPropChged = true;
    		if(chkbox_mcu_debugEn.isChecked())
    			SystemProperties.set(KEY_MCU_DEBUG_ENABLE, "1");
    		else
    			SystemProperties.set(KEY_MCU_DEBUG_ENABLE, "0");
    	}
    	if(bPropChged)
    		SystemProperties.set(KEY_CONFIG_CHANGED, "1");
    		
    	return bPropChged;
    }
    
	public boolean RebootSystem()
	{
		try{
			power.reboot(true,"",false);		

		} catch(RemoteException e)
		{
			Log.e(TAG, "RemoteException when RebootSystem: ", e);
			return false;
		}
		return true;
	}
    
	public int WriteMetazoneFile(String fileName) {
		//String res="";   
		 // AtcMetazone Meta;
		  int ret = -1;
		  int iYeconMetazoneSize = 32700;//SystemProperties.getInt(KEY_CONFIG_YECONMETA_SIZE, 0);
		  try{   
		  	FileInputStream fin = new FileInputStream(fileName);     
	        int length = fin.available();   
	         byte [] buffer = new byte[length]; 
	         byte [] MetaBuff = new byte[iYeconMetazoneSize];
	         fin.read(buffer);  
	         Log.d(TAG,"length="+length);
	         MetaBuff = Arrays.copyOfRange(buffer, YECON_META_START_ADDR,iYeconMetazoneSize);
	         ret = AtcMetazone.writereserved(MetaBuff, iYeconMetazoneSize);
	         Log.d(TAG,"writereserved,ret="+ret);
	         ret = AtcMetazone.flush(true);
	         Log.d(TAG,"flush,ret="+ret);
	         //printBuff(buffer, length);
	         //res = EncodingUtils.getString(buffer, "UTF-8");   
	         fin.close();       
	        }   
	  
	        catch(Exception e){   
	         e.printStackTrace(); 
	         String strError = (String)getResources().getText(R.string.str_msg_nometafile);
	         Toast.makeText(getApplicationContext(),strError,Toast.LENGTH_LONG).show();
	        }   
	
		String strWrite = (String) getResources().getText(R.string.str_msg_write); 
		String strBytes = String.format("  (%d)  ", iYeconMetazoneSize);
		String strCnt = (String) getResources().getText(R.string.str_msg_bytes); 
		strWrite += strBytes;
		strWrite += strCnt;
		
		if(ret==0)
			Toast.makeText(getApplicationContext(), strWrite,Toast.LENGTH_LONG).show();
		return 0;
	}
	
	  public static void printBuff(byte[] log, int len) {
		  		int i;
  	    		int j;
	    	    StringBuffer sb = new StringBuffer();
	    		for (i = 0; i < len/16; i++) {
	            	for(j = 0; j < 16; j++){
	            		String str = String.format("0x%02X ", log[i*16+j]);
	            		sb.append(str);
	            		Log.e(TAG, sb.toString());
	            	}
	        	   }
	            
	        }
	    
}
