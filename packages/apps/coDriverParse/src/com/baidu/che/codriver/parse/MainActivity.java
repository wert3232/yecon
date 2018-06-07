package com.baidu.che.codriver.parse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.che.codriver.platform.CoDriverPlatformConstants;
import com.baidu.che.codriver.platform.CoDriverPlatformManager;

public class MainActivity extends Activity {

	public static final String KEY_CMD = "cmd";
	private TextView mTextView;
	Context mContext;

	@Override
	protected void onNewIntent(Intent intent) {
		System.out.println("MainActivity.onNewIntent()");
		setIntent(intent);
		super.onNewIntent(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("MainActivity.onCreate()");
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_main);
		mTextView = (TextView) findViewById(R.id.text);
		findViewById(R.id.button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CoDriverPlatformManager.getInstance().setValue(
						CoDriverPlatformConstants.KEY_CONTACT,
						CoDriverPlatformConstants.VALUE_CONTACT_NOT_READY);
			}
		});
		findViewById(R.id.btn_set_contact).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CoDriverPlatformManager.getInstance().setValue(
						CoDriverPlatformConstants.KEY_CONTACT,
						CoDriverPlatformConstants.VALUE_CONTACT_CONTENTPROVIDER);
			}
		});
		findViewById(R.id.btn_set_tts).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CoDriverPlatformManager.getInstance().setValue(
						CoDriverPlatformConstants.KEY_TTS_TEXT, "帮我说出这句话吧！求求你了！");
			}
		});
		findViewById(R.id.btn_set_bt_off).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 蓝牙已断开连接，通知CoDriver
				CoDriverPlatformManager.getInstance().setValue(
						CoDriverPlatformConstants.KEY_BT_STATE,
						CoDriverPlatformConstants.VALUE_BT_STATE_DISCONNECTED);
			}
		});
		findViewById(R.id.btn_set_bt_on).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 蓝牙已连接，通知CoDriver
				CoDriverPlatformManager.getInstance().setValue(
						CoDriverPlatformConstants.KEY_BT_STATE,
						CoDriverPlatformConstants.VALUE_BT_STATE_CONNECTED);
			}
		});
		findViewById(R.id.btn_set_wakeup_on).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CoDriverPlatformManager.getInstance().setValue(
						CoDriverPlatformConstants.KEY_WAKE_UP_STATE,
						CoDriverPlatformConstants.VALUE_WAKE_UP_ENABLE);
				// Function.onHomePressed(mContext);
			}
		});
		findViewById(R.id.btn_set_wakeup_off).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CoDriverPlatformManager.getInstance().setValue(
						CoDriverPlatformConstants.KEY_WAKE_UP_STATE,
						CoDriverPlatformConstants.VALUE_WAKE_UP_DISABLE);

				// Function.sendKeyCode(KeyEvent.KEYCODE_YECON_MUSIC);
			}
		});
	}

	@Override
	protected void onResume() {
		Intent intent = getIntent();
		if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey(KEY_CMD)) {
			mTextView.setText(intent.getExtras().getString(KEY_CMD, ""));
		}
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
