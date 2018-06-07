package com.autochips.weather;






import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class SettingsActivity extends Activity {

	CheckBox checkBoxcurcity;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        checkBoxcurcity = (CheckBox)this.findViewById(R.id.checkBoxcurcity);
        SharedPreferences sp =this.getSharedPreferences("chkCurCity", MODE_PRIVATE);
        boolean mbCheckCurCity = sp.getBoolean("chkCurCity",true);
        checkBoxcurcity.setChecked(mbCheckCurCity);
        this.findViewById(R.id.fmid).setVisibility(mbCheckCurCity?View.INVISIBLE:View.VISIBLE);
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sp = this.getSharedPreferences("chkCurCity", MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

		editor.putBoolean("chkCurCity", checkBoxcurcity.isChecked());
		editor.commit();
		if(!checkBoxcurcity.isChecked())
		{
			Utils.sendUpdateCityBroadcast(this, DBFactory.getInstance(this));
		}
        if (WeatherApp.IS_WIFI_CONNECTED) {
            startService(new Intent(this, WeatherService.class));
        }
    }
    public void checkCurcity(View view){    
    	switch (view.getId()) {
		case R.id.checkBoxcurcity: {
			if(checkBoxcurcity.isChecked())
			{
				this.findViewById(R.id.fmid).setVisibility(View.INVISIBLE);
				WeatherProvider.mbCheckCurCity = true;
			}
			else
			{
				this.findViewById(R.id.fmid).setVisibility(View.VISIBLE);
				WeatherProvider.mbCheckCurCity = false;
			}
		}
			break;
		default:
			break;
		}
    }   
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
