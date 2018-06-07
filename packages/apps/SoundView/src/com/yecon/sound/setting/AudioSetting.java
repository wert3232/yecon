package com.yecon.sound.setting;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yecon.sound.setting.unitl.SoundArray;
import com.yecon.sound.setting.unitl.Tag;
import com.yecon.sound.setting.unitl.mtksetting;
import com.yecon.sound.setting.view.PanView;
import com.yecon.sound.setting.view.PanView.onRotateListener;

public class AudioSetting extends Activity implements OnClickListener, onRotateListener {
	private final static int handler_rotate_angle = 100;

	// public static String ReverbCoef;
	
	TextView tv_eq_set, tv_balance_set, tv_scene_up, tv_scene, tv_scene_down, tv_reset,
			tv_loundness, tv_scene_set;
	String[] ReverbCoefs;
	int index_ReverbCoef;
	boolean bLoundness;
	mtksetting mmtksetting;
	int uLoudNessType;
	PanView panView[] = new PanView[4];

	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if (Tag.MCU_ACTION_LOUDNESS_KEY.equals(arg1.getAction())) {
				bLoundness = !bLoundness;
				freshLoundnessUI();
			} else if (Tag.ACTION_RESET_FACTORY.equals(arg1.getAction())) {
				FreshUI();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_audiosetting);
		MyApplication.getInstance().addActivity(this);
		initData();
		initView();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	private void initData() {
		mmtksetting = new mtksetting();
		mmtksetting.setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this));
		ReverbCoefs = getResources().getStringArray(R.array.ReverbCoef);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Tag.MCU_ACTION_LOUDNESS_KEY);
		filter.addAction(Tag.ACTION_RESET_FACTORY);
		registerReceiver(mBroadcastReceiver, filter);
	}

	private void initView() {

		LinearLayout layout_Subwoofer = (LinearLayout) findViewById(R.id.layout_Subwoofer);
		boolean enable = SystemProperties.getBoolean(Tag.PERSYS_SUBWOOFER_ENABLE, true);
		if (!enable)
			layout_Subwoofer.setVisibility(View.GONE);
		
		LinearLayout layout_Alto = (LinearLayout) findViewById(R.id.layout_Alto);
		enable = SystemProperties.getBoolean(Tag.PERSYS_ALTO_ENABLE, true);
		if (!enable)
			layout_Alto.setVisibility(View.GONE);

		tv_eq_set = (TextView) findViewById(R.id.eq_set_id);
		tv_eq_set.setOnClickListener(this);
		tv_balance_set = (TextView) findViewById(R.id.set_balance);
		tv_balance_set.setOnClickListener(this);
		tv_scene_up = (TextView) findViewById(R.id.scene_direct_up);
		tv_scene_up.setOnClickListener(this);
		tv_scene_down = (TextView) findViewById(R.id.scene_direct_down);
		tv_scene_down.setOnClickListener(this);
		tv_reset = (TextView) findViewById(R.id.reset_all);
		tv_reset.setOnClickListener(this);
		tv_loundness = (TextView) findViewById(R.id.set_loundness);
		tv_loundness.setOnClickListener(this);
		tv_scene = (TextView) findViewById(R.id.scene_set);

		panView[0] = (PanView) findViewById(R.id.panview_treble);
		panView[1] = (PanView) findViewById(R.id.panview_alto);
		panView[2] = (PanView) findViewById(R.id.panview_bass);
		panView[3] = (PanView) findViewById(R.id.panview_sub_woofer);

		for (PanView view : panView) {
			view.setRotateListener(view, this);
		}

		FreshUI();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (arg0.getId()) {
		case R.id.eq_set_id:
			intent = new Intent();
			intent.setClass(this, EffectActivity.class);
			startActivity(intent);
			break;
		case R.id.set_balance:
			intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(this, BalanceActivity.class);
			startActivity(intent);
			break;
		case R.id.scene_direct_up:
			index_ReverbCoef += 1;
			index_ReverbCoef = SetScene(index_ReverbCoef);
			break;
		case R.id.scene_direct_down:
			index_ReverbCoef -= 1;
			index_ReverbCoef = SetScene(index_ReverbCoef);
			break;
		case R.id.reset_all:
			ReSetFactory();
			FreshUI();
			break;
		case R.id.set_loundness:
			bLoundness = !bLoundness;

			freshLoundnessUI();

			uLoudNessType = bLoundness ? SystemProperties.getInt(Tag.PERSYS_AUDIO[4], Tag.audio[4])
					: 0;
			mmtksetting
					.SetLoudNess(uLoudNessType, SoundArray.LoudNess_gLoudNessGain[uLoudNessType]);
			mmtksetting.editor.putBoolean(Tag.loudNess_enable, bLoundness);
			mmtksetting.editor.commit();

			break;
		}
	}

	private int SetScene(int value) {

		if (value > ReverbCoefs.length - 1) {
			value = 0;
		} else if (value < 0) {
			value = ReverbCoefs.length - 1;
		}
		tv_scene.setText(ReverbCoefs[value]);
		mmtksetting.setReverbCoef(value);
		mmtksetting.editor.putInt(Tag.ReverbCoef, value);
		mmtksetting.editor.commit();
		return value;
	}

	private void FreshUI() {

		float angle = mmtksetting.uiState.getFloat(Tag.treble_angle, 0);
		panView[0].onChangeAngleValue(angle);
		angle = mmtksetting.uiState.getFloat(Tag.alto_angle, 0);
		panView[1].onChangeAngleValue(angle);
		angle = mmtksetting.uiState.getFloat(Tag.bass_angle, 0);
		panView[2].onChangeAngleValue(angle);
		angle = mmtksetting.uiState.getFloat(Tag.subwoofer_angle, -Tag.RANGLE);
		panView[3].onChangeAngleValue(angle);

		index_ReverbCoef = mmtksetting.uiState.getInt(Tag.ReverbCoef, 0);
		tv_scene.setText(ReverbCoefs[index_ReverbCoef]);

		bLoundness = mmtksetting.uiState.getBoolean(Tag.loudNess_enable, false);
		freshLoundnessUI();
	}

	private void freshLoundnessUI() {
		tv_loundness.setText(bLoundness ? R.string.Loundness_on : R.string.Loundness_off);
		Drawable loundnessDrawable = getResources().getDrawable(R.xml.selector_button_loundness);
		loundnessDrawable.setBounds(0, 0, loundnessDrawable.getMinimumWidth(),
				loundnessDrawable.getMinimumHeight());
		Drawable loundnessDrawable_off = getResources().getDrawable(
				R.xml.selector_button_loundness_off);
		loundnessDrawable_off.setBounds(0, 0, loundnessDrawable_off.getMinimumWidth(),
				loundnessDrawable_off.getMinimumHeight());

		tv_loundness.setCompoundDrawables(null, bLoundness ? loundnessDrawable
				: loundnessDrawable_off, null, null);
	}

	private void ReSetFactory() {

		for (int i = 0; i < Tag.PERSYS_AUDIO.length; i++) {
			Tag.audio[i] = SystemProperties.getInt(Tag.PERSYS_AUDIO[i], Tag.audio[i]);
		}

		mmtksetting.editor.putInt(Tag.treble, Tag.audio[0]);
		mmtksetting.editor.putInt(Tag.alto, Tag.audio[1]);
		mmtksetting.editor.putInt(Tag.bass, Tag.audio[2]);

		mmtksetting.editor.putFloat(Tag.treble_angle, Tag.audio[0] * Tag.RANGLE / 14);
		mmtksetting.editor.putFloat(Tag.alto_angle, Tag.audio[1] * Tag.RANGLE / 14);
		mmtksetting.editor.putFloat(Tag.bass_angle, Tag.audio[2] * Tag.RANGLE / 14);
		mmtksetting.editor.putFloat(Tag.subwoofer_angle, Tag.audio[3] * Tag.RANGLE / 20
				- Tag.RANGLE);
		mmtksetting.editor.commit();

		mmtksetting.EnableSubwoofer(true);
		mmtksetting.SetSubwoofer(Tag.audio[3]);

		// eq
		int nEQ_type = 6;
		mmtksetting.SetEQValues(nEQ_type);
		mmtksetting.editor.putInt(Tag.eq_type, nEQ_type);
		mmtksetting.editor.putBoolean(Tag.eq_type_self, false);
		mmtksetting.editor.commit();

		// scene
		index_ReverbCoef = 0;
		mmtksetting.editor.putInt(Tag.ReverbCoef, index_ReverbCoef);
		mmtksetting.setReverbCoef(index_ReverbCoef);

		// loundness
		bLoundness = false;
		uLoudNessType = 0;
		mmtksetting.SetLoudNess(uLoudNessType, SoundArray.LoudNess_gLoudNessGain[uLoudNessType]);
		mmtksetting.editor.putBoolean(Tag.loudNess_enable, bLoundness);

		// balance
		mmtksetting.setBalance(Tag.audio[6], Tag.audio[5]);
		mmtksetting.editor.putInt(Tag.valueX_tag, Tag.audio[6]);
		mmtksetting.editor.putInt(Tag.valueY_tag, Tag.audio[5]);
		mmtksetting.editor.commit();
	}

	@Override
	public void onChangeRotateAngle(View view, float value) {
		// TODO Auto-generated method stub
		Message message = Message.obtain();
		message.what = handler_rotate_angle;
		message.arg1 = view.getId();
		message.obj = value;
		myHandler.sendMessage(message);
	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == handler_rotate_angle) {
				float value = (Float) msg.obj;
				switch (msg.arg1) {
				case R.id.panview_treble:
					mmtksetting.editor.putFloat(Tag.treble_angle, value);
					if (Math.abs((int) value) > Tag.RANGLE)
						value = 0;
					value = 14 * value / Tag.RANGLE;
					mmtksetting.SetTrebleValue((int) value);
					mmtksetting.editor.putInt(Tag.treble, (int) value);
					mmtksetting.editor.commit();
					break;
				case R.id.panview_alto:
					mmtksetting.editor.putFloat(Tag.alto_angle, value);
					if (Math.abs((int) value) > Tag.RANGLE)
						value = 0;
					value = 14 * value / Tag.RANGLE;
					mmtksetting.SetAltoValue((int) value);
					mmtksetting.editor.putInt(Tag.alto, (int) value);
					mmtksetting.editor.commit();
					break;
				case R.id.panview_bass:
					mmtksetting.editor.putFloat(Tag.bass_angle, value);
					if (Math.abs((int) value) > Tag.RANGLE)
						value = 0;
					value = 14 * value / Tag.RANGLE;
					mmtksetting.SetBassValue((int) value);
					mmtksetting.editor.putInt(Tag.bass, (int) value);
					mmtksetting.editor.commit();
					break;
				case R.id.panview_sub_woofer:
					mmtksetting.editor.putFloat(Tag.subwoofer_angle, value);

					if (Math.abs((int) value) > Tag.RANGLE)
						value = -Tag.RANGLE;
					value = 20 * value / Tag.RANGLE;
					value += 20;
					mmtksetting.SetSubwoofer((int) value);
					mmtksetting.editor.putInt(Tag.subwoofer, (int) value);
					mmtksetting.editor.commit();
					break;
				}
				// Log.i("lzy", "........................RotateAngle =" +
				// value);
			}
		}
	};
}
