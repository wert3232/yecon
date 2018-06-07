package com.yecon.sound.setting;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.autochips.settings.AtcSettings;
import com.yecon.sound.setting.unitl.L;
import com.yecon.sound.setting.unitl.SoundArray;
import com.yecon.sound.setting.unitl.Tag;
import com.yecon.sound.setting.unitl.mtksetting;
import com.yecon.sound.setting.view.VerticalSeekBar;
import com.yecon.sound.setting.view.VerticalSeekBar.onSeekBarChangeVertical;

public class EffectActivity extends Activity implements View.OnClickListener,
		onSeekBarChangeVertical {

	private final static int handler_set_effect_self = 400;
	private final static int handler_set_effect = 300;

	Integer ID_SeekBar[] = { R.id.seekbar_view_1, R.id.seekbar_view_2, R.id.seekbar_view_3,
			R.id.seekbar_view_4, R.id.seekbar_view_5, R.id.seekbar_view_6, R.id.seekbar_view_7,
			R.id.seekbar_view_8, R.id.seekbar_view_9, R.id.seekbar_view_10, R.id.seekbar_view_11,
			R.id.seekbar_view_12 };
	VerticalSeekBar mSeekBar[] = new VerticalSeekBar[12];

	Integer ID_RadioButton[] = { R.id.rb_rock, R.id.rb_popu, R.id.rb_live, R.id.rb_dance,
			R.id.rb_classical, R.id.rb_soft, R.id.rb_origin, R.id.rb_favourite };
	RadioButton mRadioButton[] = new RadioButton[8];

	Integer ID_TextView[] = { R.id.btn_favourite, R.id.btn_reset, R.id.btn_analogmode };
	TextView textView[] = new TextView[3];

	private List<VerticalSeekBar> seekbar_view_list = new ArrayList<VerticalSeekBar>();
	private List<RadioButton> bm_textview_list = new ArrayList<RadioButton>();

	boolean isSelfEQ;
	private int type_id;
	private int loundnessValue;

	Integer[] selfEQGain = new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	StringBuffer selfValues = new StringBuffer();
	mtksetting mmtksetting;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_effect);
		initData();
		initView();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void initData() {
		MyApplication.getInstance().addActivity(this);
		mmtksetting = new mtksetting();
		mmtksetting.setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this));
		isSelfEQ = mmtksetting.uiState.getBoolean(Tag.eq_type_self, false);
		type_id = mmtksetting.uiState.getInt(Tag.eq_type, 6);
		loundnessValue = mmtksetting.uiState.getInt(Tag.loudness, 0);
		if (mmtksetting.getSelfEQGain(Tag.eq_self_value) != null)
			selfEQGain = mmtksetting.getSelfEQGain(Tag.eq_self_value);
	}

	private void initView() {
		int i = 0;
		seekbar_view_list.clear();
		for (VerticalSeekBar seekBar : mSeekBar) {
			mSeekBar[i] = (VerticalSeekBar) findViewById(ID_SeekBar[i]);
			mSeekBar[i].setOnSeekBarChangeVertical(this);
			seekbar_view_list.add(mSeekBar[i]);
			i++;
		}

		i = 0;
		bm_textview_list.clear();
		for (RadioButton rb : mRadioButton) {
			mRadioButton[i] = (RadioButton) findViewById(ID_RadioButton[i]);
			mRadioButton[i].setOnClickListener(this);
			bm_textview_list.add(mRadioButton[i]);
			i++;
		}

		i = 0;
		for (TextView tv : textView) {
			textView[i] = (TextView) findViewById(ID_TextView[i]);
			textView[i].setOnClickListener(this);
			i++;
		}

		seekbar_view_list.get(11).setProgress(loundnessValue);

		if (isSelfEQ) {
			setSeekBarProgress_self();
			bm_textview_list.get(7).setChecked(true);
			textView[0].setEnabled(false);
		} else {
			setSeekBarProgress(type_id);
			bm_textview_list.get(type_id).setChecked(true);
		}

	}

	@Override
	public void onSeekBarChange(VerticalSeekBar mSeekbar, int progress) {
		L.v("lzy........................................" + progress);
		if (mSeekbar.getId() == R.id.seekbar_view_12) {
			setLoundNess(progress);
		} else {
			mRadioButton[7].setChecked(true);
			textView[0].setEnabled(false);
			for (int k = 0; k < selfEQGain.length; k++) {
				selfEQGain[k] = seekbar_view_list.get(k).getProgress() - 14;
			}
			SetEQValuesSelf(selfEQGain);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.rb_rock:
			setTitleStatus(0);
			break;
		case R.id.rb_popu:
			setTitleStatus(1);
			break;
		case R.id.rb_live:
			setTitleStatus(2);
			break;
		case R.id.rb_dance:
			setTitleStatus(3);
			break;
		case R.id.rb_classical:
			setTitleStatus(4);
			break;
		case R.id.rb_soft:
			setTitleStatus(5);
			break;
		case R.id.rb_origin:
			setTitleStatus(6);
			break;
		case R.id.rb_favourite:
			setSelfEffect();
			break;
		case R.id.btn_reset:
			setTitleStatus(6);
			bm_textview_list.get(6).setChecked(true);
			break;
		case R.id.btn_favourite:
			setSelfEffect();
			bm_textview_list.get(7).setChecked(true);
			textView[0].setEnabled(false);
			break;
		case R.id.btn_analogmode:
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(this, AudioSetting.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void SetEQValuesSelf(Integer[] Value) {
		mmtksetting.SetEQValuesSelf(Value);
		mmtksetting.saveSelfEQGain(Value, Tag.eq_self_value);
	}

	private void setTitleStatus(int value) {
		mmtksetting.SetEQValues(value);
		setSeekBarProgress(value);
		type_id = value;
		mmtksetting.editor.putInt(Tag.eq_type, value);
		mmtksetting.editor.putBoolean(Tag.eq_type_self, false);
		mmtksetting.editor.commit();
		textView[0].setEnabled(true);
	}

	private void setSelfEffect() {
		mmtksetting.editor.putBoolean(Tag.eq_type_self, true);
		mmtksetting.editor.commit();
		SetEQValuesSelf(selfEQGain);
		setSeekBarProgress_self();
	}

	public void setSeekBarProgress_self() {
		Message message = Message.obtain();
		message.what = handler_set_effect_self;
		myHandler.sendMessage(message);
	}

	public void setSeekBarProgress(int index) {
		Message message = Message.obtain();
		message.what = handler_set_effect;
		message.obj = index;
		myHandler.sendMessage(message);
	}

	private void setLoundNess(int progressInt) {
		int[] rLoudNessGain = SoundArray.LoudNess_gLoudNessGain[progressInt];
		AtcSettings.Audio.SetLoudness(AtcSettings.Audio.LoudnessMode.nativeToType(progressInt),
				rLoudNessGain);
		mmtksetting.editor.putInt(Tag.loudness, progressInt);
		mmtksetting.editor.commit();
	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			int mPre;
			if (msg.what == handler_set_effect) {
				int index = (Integer) msg.obj;
				for (int idx = 0; idx < 11; idx++) {
					mPre = SoundArray.gEQTypePos[index][idx];
					seekbar_view_list.get(idx).setProgress(mPre + 14);
				}
			} else if (msg.what == handler_set_effect_self) {
				for (int idx = 0; idx < 11; idx++) {
					mPre = selfEQGain[idx];
					seekbar_view_list.get(idx).setProgress(mPre + 14);
				}
			}
		}
	};
}
