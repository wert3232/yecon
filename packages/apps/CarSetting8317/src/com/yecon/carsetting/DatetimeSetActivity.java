package com.yecon.carsetting;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.mcu.McuManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.tuoxianui.tools.AtTimerHelpr;
import com.yecon.carsetting.view.MyDatePicker;
import com.yecon.carsetting.view.MyDatePicker.OnDateChangedListener;

import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.yecon.carsetting.fragment.Fragment_TimeZone;
import com.yecon.carsetting.fragment.Fragment_TimeZone.OnItemClickListener;
import com.yecon.carsetting.tuoxian.DeviceAboutAcitivity;
import com.yecon.carsetting.tuoxian.LightSettingsAcitivity;
import com.yecon.carsetting.tuoxian.TFactroySettingsActivity;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.L;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.tzutil;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;
import com.yecon.carsetting.view.HeaderLayout.onTwoButtonListener;

public class DatetimeSetActivity extends Activity implements onOneButtonListener,
		onOneCheckBoxListener, onTwoButtonListener, OnTimeChangedListener, OnDateChangedListener,OnClickListener {

	private Context mContext;
	FragmentManager mFragmentManager;
	private SharedPreferences.Editor mEditor ;
	private SharedPreferences mPref;
	private Switch showtime_Switch;
	private static final String HOURS_12 = "12";
	private static final String HOURS_24 = "24";
	final static Integer[] mHandlerIDs = { 100, 200 };

	private List<HashMap<String, Object>> sortedList;

	static tzutil.TimeZoneSet mTimeZoneSet;
	private int mTimezoneIndex;
	String[] list_clock;
	int mIndex;

	Calendar mCalendar = null;
	int ID_Button[] = { R.id.set_datetime_zone };
	int ID_CheckBox[] = { R.id.set_datetime_24hours, R.id.set_datetime_gps_syc };
	int ID_TwoButton[] = { R.id.set_datetime_clock };

	RelativeLayout mRelativeLayout;
	private com.yecon.carsetting.view.MyDatePicker mDatePicker;
	private TimePicker mTimePicker;
	HeaderLayout mLayout_Button[] = new HeaderLayout[ID_Button.length];
	HeaderLayout mLayout_CheckBox[] = new HeaderLayout[ID_CheckBox.length];
	HeaderLayout mLayout_TwoButton[] = new HeaderLayout[ID_TwoButton.length];
	private AtTimerHelpr mAtTimerHelpr;
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
		}
	};

	private void initData() {
		mContext = this;
		mFragmentManager = getFragmentManager();
		tzutil.initSharePF(mContext);
		mCalendar = Calendar.getInstance();
		IntentFilter filter = new IntentFilter(Tag.ACTION_BRIGHTNESS_CHANGED);
		mContext.registerReceiver(mBroadcastReceiver, filter);

		mTimeZoneSet = tzutil.TimeZoneSet.getInstance(mContext);

		tzutil.MyComparator comparator = new tzutil.MyComparator(mTimeZoneSet.KEY_OFFSET);
		sortedList = mTimeZoneSet.getZones(mContext);
		Collections.sort(sortedList, comparator);

		String defaultId = TimeZone.getDefault().getID();
		mTimezoneIndex = mTimeZoneSet.getTimeZoneIndex(sortedList, defaultId);

		list_clock = getResources().getStringArray(R.array.clock_type);
		mIndex = 0;
		
		mPref = this.getSharedPreferences("datetime_settings", 3);
		mEditor = mPref.edit();

	}

	private void setStyle_DatePickerAndTimePicker() {
		mTimePicker.setIs24HourView(true);
		tzutil.setcolorfortimepickerdivider(mTimePicker, Color.TRANSPARENT); //设置里面的横分隔线
		tzutil.setcolorfordatepickerdivider(mDatePicker, Color.TRANSPARENT);
		float size = getResources().getDimensionPixelOffset(R.dimen.textsize_general);
		tzutil.settextsizefortimepicker(mTimePicker, size); //设置里面的文字大小
		tzutil.settextsizefordatepicker(mDatePicker, size);
		/*int monthId= getResources().getIdentifier("month", "id", "com.android.internal");
		mDatePicker.findViewById(monthId);
		Log.e(this.getClass().getSimpleName(), "month:" + monthId);*/
		// Change DatePicker layout
		// 0 : LinearLayout; 1 : CalendarView
		LinearLayout dpContainer = (LinearLayout) mDatePicker.getChildAt(0);
		LinearLayout dpSpinner = (LinearLayout) dpContainer.getChildAt(0);
		for (int i = 0; i < dpSpinner.getChildCount(); i++) {
			NumberPicker numPicker = (NumberPicker) dpSpinner.getChildAt(i);
			// 0-2 : NumberPicker
			int width = getResources().getDimensionPixelOffset(
					R.dimen.DatePicker_NumberPicker_width);
			LayoutParams params1 = new LayoutParams(width, LayoutParams.WRAP_CONTENT);
			params1.leftMargin = 0;
			params1.rightMargin = 0;
			numPicker.setLayoutParams(params1);

			// EditText cusET = (EditText)numPicker.getChildAt(0); //
			// CustomEditText
			// cusET.setTextSize(14);
			// cusET.setWidth(70);
		}

		// Change TimePicker layout
		// 0 : LinearLayout; 1 : CalendarView
		LinearLayout tpContainer = (LinearLayout) mTimePicker.getChildAt(0);
		LinearLayout tpSpinner = (LinearLayout) tpContainer.getChildAt(0);
		for (int i = 0; i < tpSpinner.getChildCount(); i++) {
			// child(1) is a TextView ( : )
			if (i == 1) {
				// TextView tv = (TextView) tpSpinner.getChildAt(i);
				// tv.setWidth(120);
				continue;
			}
			// 0 : NumberPicker; 1 : TextView; 2 : NumberPicker
			NumberPicker numPicker = (NumberPicker) tpSpinner.getChildAt(i);
			int width = getResources().getDimensionPixelOffset(
					R.dimen.TimePicker_NumberPicker_width);
			LayoutParams params3 = new LayoutParams(width, LayoutParams.WRAP_CONTENT);
			params3.leftMargin = 0;
			params3.rightMargin = 0;
			numPicker.setLayoutParams(params3);

			// EditText cusET = (EditText)numPicker.getChildAt(0); //
			// cusET.setTextSize(14);
			// cusET.setWidth(120);
		}
	}

	private void initView() {

		mRelativeLayout = (RelativeLayout) findViewById(R.id.set_datetime);
		mDatePicker = (com.yecon.carsetting.view.MyDatePicker) findViewById(R.id.datepicker);
		mTimePicker = (TimePicker) findViewById(R.id.timepicker);
		setStyle_DatePickerAndTimePicker();

		mDatePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
				mCalendar.get(Calendar.DAY_OF_MONTH), this);
		mTimePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
		mTimePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
		mTimePicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
		mTimePicker.setOnTimeChangedListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		int i = 0;
		for (HeaderLayout layout : mLayout_Button) {
			mLayout_Button[i] = (HeaderLayout) findViewById(ID_Button[i]);
			mLayout_Button[i].setOneButtonListener(this);
			i++;
		}

		setPromptTitle(mTimezoneIndex);

		i = 0;
		for (HeaderLayout layout : mLayout_CheckBox) {
			mLayout_CheckBox[i] = (HeaderLayout) findViewById(ID_CheckBox[i]);
			mLayout_CheckBox[i].setOneCheckBoxListener(this);
			i++;
		}

		i = 0;
		for (HeaderLayout layout : mLayout_TwoButton) {
			mLayout_TwoButton[i] = (HeaderLayout) findViewById(ID_TwoButton[i]);
			mLayout_TwoButton[i].setTwoButtonListener(this);
			i++;
		}

		mLayout_CheckBox[0].getOneCheckBox().setChecked(is24Hour());

		boolean enable = tzutil.getAutoState(mContext, Settings.Global.AUTO_TIME);
		mLayout_CheckBox[1].getOneCheckBox().setChecked(enable);
		mRelativeLayout.setVisibility(enable ? View.GONE : View.VISIBLE);

		enable = SystemProperties.getBoolean(Tag.PERSYS_FUNS_OTHER[8], false);
		mLayout_TwoButton[0].setVisibility(enable ? View.VISIBLE : View.GONE);

		mIndex = SystemProperties.getInt(Tag.PERSYS_CLOCK_TYPE, 0);
		mLayout_TwoButton[0].setMiddleTitle(list_clock[mIndex]);
		
		if(mPref.getInt("AutoTime", 0) == 0){
			mPref.getInt("AutoTime", 0);
			mLayout_CheckBox[1].getOneCheckBox().setChecked(false);
			setAutoTime(false);
			mEditor.putInt("AutoTime", 0).commit();
		}
		showtime_Switch = (Switch)findViewById(R.id.showtime_Switch);
		showtime_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	SystemProperties.set("persist.sys.showtime",isChecked?"1":"0");
				sendBroadcast(new Intent("com.wesail.showtime"));
				//一汽要求,不自动流转 mAtTimerHelpr.stop();
				handler.removeCallbacks(runnable);
				handler.postDelayed(runnable, 5000);
            }
        });
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.fragment_datetime);
		initData();
		initView();
		
		//一汽要求,不自动流转 mAtTimerHelpr = new AtTimerHelpr();
		AtTimerHelpr.AtTimerHelprDoItCallBack atTimerHelprDoItCallBack = new AtTimerHelpr.AtTimerHelprDoItCallBack() {
			@Override
			public void doIt() {
				backHandler.sendEmptyMessage(0);
			}
		};
		//一汽要求,不自动流转 mAtTimerHelpr.setCallBack(atTimerHelprDoItCallBack);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub
		if (view.getId() == ID_Button[0]) {
			Fragment_TimeZone dlg = Function.onSet_timezone(mFragmentManager, mTimezoneIndex);
			dlg.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onClickItem(String str) {
					// TODO Auto-generated method stub
					// Update the system timezone value
					String defaultId = str;
					mTimezoneIndex = mTimeZoneSet.getTimeZoneIndex(sortedList, defaultId);
					setPromptTitle(mTimezoneIndex);
					final AlarmManager alarm = (AlarmManager) mContext
							.getSystemService(Context.ALARM_SERVICE);
					alarm.setTimeZone(defaultId);
				}
			});
		}
	}

	@Override
	public void onCheckout(View view, boolean value) {
		// TODO Auto-generated method stub
		Log.e("id","viewID:" + view.getId() + " ?=" + ID_CheckBox[1]);
		if (view.getId() == ID_CheckBox[0]) {
			set24Hour(value);
			timeUpdated();
		} else if (view.getId() == ID_CheckBox[1]) {
			setAutoTime(value);
		}
	}
	private void setAutoTime(Boolean isAutoTime){
		if (mLListener != null)
			mLListener.removeGpsListener(isAutoTime);
		Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME, isAutoTime ? 1 : 0);
		mRelativeLayout.setVisibility(isAutoTime ? View.GONE : View.VISIBLE);
	}

	private void setPromptTitle(int index) {
		final Map<?, ?> map = (Map<?, ?>) sortedList.get(index);
		final String displayname = (String) map.get(mTimeZoneSet.KEY_DISPLAYNAME);
		final String gmt = (String) map.get(mTimeZoneSet.KEY_GMT);

		mLayout_Button[0].setPromptTitle(displayname);
		mLayout_Button[0].setPromptTitle2(gmt);
	}

	private void timeUpdated() {
		Intent timeChanged = new Intent(Intent.ACTION_TIME_CHANGED);
		mContext.sendBroadcast(timeChanged);
	}

	private void set24Hour(boolean is24Hour) {
		Settings.System.putString(mContext.getContentResolver(), Settings.System.TIME_12_24,
				is24Hour ? HOURS_24 : HOURS_12);
	}

	private boolean is24Hour() {
		return DateFormat.is24HourFormat(mContext);
	}

	public interface onGpsListener {
		public void removeGpsListener(boolean isRemove);
	}

	public static onGpsListener mLListener;

	public static void setLocationListener(onGpsListener mL) {
		mLListener = mL;
	}

	@Override
	public void onDateChanged(MyDatePicker arg0, int year, int month, int day) {
		// TODO Auto-generated method stub
		myHandler.removeMessages(mHandlerIDs[0]);
		sendMessage(mHandlerIDs[0], year, month, day);
	}

	@Override
	public void onTimeChanged(TimePicker arg0, int hour, int minute) {
		// TODO Auto-generated method stub
		myHandler.removeMessages(mHandlerIDs[1]);
		sendMessage(mHandlerIDs[1], hour, minute, 0);
	}

	static void setTime(Context context, int hourOfDay, int minute) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0);
		long when = c.getTimeInMillis();
		Date date = c.getTime();
		int mYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
		int mMonth = Integer.parseInt(new SimpleDateFormat("MM").format(date));
		int mDay = Integer.parseInt(new SimpleDateFormat("dd").format(date));
		int mHour = Integer.parseInt(new SimpleDateFormat("HH").format(date));
		int mMinute = Integer.parseInt(new SimpleDateFormat("mm").format(date));
		int mSecond = Integer.parseInt(new SimpleDateFormat("ss").format(date));
		if (when / 1000 < Integer.MAX_VALUE) {
			((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
			McuManager mMcuManager = (McuManager) context.getSystemService(Context.MCU_SERVICE);
			mMcuManager.RPC_SetTime(mYear, mMonth, mDay, mHour, mMinute, mSecond);
		}
	}

	void setDate(Context context, int year, int month, int day) {
		mCalendar.set(Calendar.YEAR, year);
		mCalendar.set(Calendar.MONTH, month);
		mCalendar.set(Calendar.DAY_OF_MONTH, day);
		
		long when = mCalendar.getTimeInMillis();
		Date date = mCalendar.getTime();
		int mYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
		int mMonth = Integer.parseInt(new SimpleDateFormat("MM").format(date));
		int mDay = Integer.parseInt(new SimpleDateFormat("dd").format(date));
		int mHour = Integer.parseInt(new SimpleDateFormat("HH").format(date));
		int mMinute = Integer.parseInt(new SimpleDateFormat("mm").format(date));
		int mSecond = Integer.parseInt(new SimpleDateFormat("ss").format(date));
		if (when / 1000 < Integer.MAX_VALUE) {
			((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
			McuManager mMcuManager = (McuManager) context.getSystemService(Context.MCU_SERVICE);
			mMcuManager.RPC_SetTime(mYear, mMonth, mDay, mHour, mMinute, mSecond);
		}
		
	}

	public void sendMessage(int what, int obj, int arg1, int arg2) {
		Message message = Message.obtain();
		message.what = what;
		message.obj = obj;
		message.arg1 = arg1;
		message.arg2 = arg2;
		myHandler.sendMessageDelayed(message, 1000);
	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mHandlerIDs[0] == msg.what) {
				setDate(mContext, (Integer) msg.obj, msg.arg1, msg.arg2);
			} else if (mHandlerIDs[1] == msg.what) {
				setTime(mContext, (Integer) msg.obj, msg.arg1);
			}
		}
	};

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.set_datetime_clock:
			mIndex -= 1;
			if (mIndex < 0)
				mIndex = 0;
			mLayout_TwoButton[0].setMiddleTitle(list_clock[mIndex]);
			SystemProperties.set(Tag.PERSYS_CLOCK_TYPE, mIndex + "");
			Intent intent = new Intent(Tag.ACTION_CLOCK_TYPE);
			sendBroadcast(intent);
			break;

		default:
			break;
		}

	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.set_datetime_clock:
			mIndex += 1;
			if (mIndex > 1)
				mIndex = 1;
			mLayout_TwoButton[0].setMiddleTitle(list_clock[mIndex]);
			SystemProperties.set(Tag.PERSYS_CLOCK_TYPE, mIndex + "");
			Intent intent = new Intent(Tag.ACTION_CLOCK_TYPE);
			sendBroadcast(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
				onBackPressed();
				break;
			default:
				break;
		}
	}
	
	
	Handler backHandler = new Handler() {
		public void handleMessage(Message msg) {
			onBackPressed();
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		//一汽要求,不自动流转 mAtTimerHelpr.start(10);
		showtime_Switch.setChecked(SystemProperties.get("persist.sys.showtime","1").equals("1"));
	}
	@Override
	protected void onPause() {
		super.onPause();
		//一汽要求,不自动流转 mAtTimerHelpr.stop();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//一汽要求,不自动流转 mAtTimerHelpr.stop();
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//一汽要求,不自动流转 mAtTimerHelpr.stop();
		handler.removeCallbacks(runnable);
		handler.postDelayed(runnable, 5000);
		return super.dispatchTouchEvent(ev);
	}
	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			//一汽要求,不自动流转 mAtTimerHelpr.start(5);
		}
	};
}
