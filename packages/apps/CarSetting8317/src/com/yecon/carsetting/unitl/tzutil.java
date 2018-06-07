package com.yecon.carsetting.unitl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.xmlpull.v1.XmlPullParserException;

import android.app.AlarmManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import com.yecon.carsetting.view.MyDatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.internal.app.LocalePicker;
import com.yecon.carsetting.R;

public class tzutil {

	private static final String TAG = "CarSetting";
	public static StringBuilder mcuVersion = new StringBuilder();
	public static StringBuilder mcuID = new StringBuilder();
	public static SharedPreferences.Editor editor;
	public static SharedPreferences uiState;
	private static final int timeout = 3000;
	public static int USB_10=1;
	public static int USB_20=2;

	public void setSharedPreferences(SharedPreferences mS) {
		uiState = mS;
		editor = uiState.edit();
	}

	public static void initSharePF(Context context) {
		uiState = PreferenceManager.getDefaultSharedPreferences(context);
		editor = uiState.edit();
	}

	public static void putBoolean(String str, boolean mValue) {
		editor.putBoolean(str, mValue);
		editor.commit();
	}

	public static boolean getCheckValues(String str) {
		return uiState.getBoolean(str, false);
	}

	public static boolean getCheckValuesTrue(String str) {
		return uiState.getBoolean(str, true);
	}

	public static void saveIntValue(String str, int value) {
		editor.putInt(str, value);
		editor.commit();
	}

	public static int getIntValue(String str, int value) {
		return uiState.getInt(str, value);
	}

	public static String getStringValue(String str, String defaulValue) {
		return uiState.getString(str, defaulValue);
	}

	public static void saveStringValue(String str, String strValue) {
		editor.putString(str, strValue);
		editor.commit();
	}

	public static String[] getStringArray(int value) {
		String[] s1 = new String[value + 1];
		for (int i = 0; i <= value; i++) {
			s1[i] = i + "";
		}
		return s1;
	}

	public static String[] sort(String[] string) {
		Arrays.sort(string);
		final String[] temp = new String[string.length];
		int size = string.length - 1;
		for (int i = 0; i <= size; i++) {
			temp[i] = string[size - i];
		}
		return temp;
	}

	public static String getmcuVersion() {
		return mcuVersion.toString();
	}

	public static String getmcuID() {
		return mcuID.toString();
	}

	public static boolean SwitchUSBModeRunning(String strValue) {
		try {
			SystemProperties.set("ctl.start", strValue);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void setUsbType(int type) {
		String protocol;
		String config;
		
		if (type == USB_10) {
			protocol = SystemProperties.get("persist.current.usb.protocol");
			config = SystemProperties.get("persist.current.usb.mode");
			if(protocol.equals("usb2_0")){
				SystemProperties.set("persist.current.usb.protocol","usb1_1");
				if(config.equals("host2_0")){
					SystemProperties.set("persist.current.usb.mode","host1_1");
				}
				SystemClock.sleep(1000);
			}
		}else if (type == USB_20) {
			protocol = SystemProperties.get("persist.current.usb.protocol");
			config = SystemProperties.get("persist.current.usb.mode");
			if(protocol.equals("usb1_1")){
				SystemProperties.set("persist.current.usb.protocol","usb2_0");
				if(config.equals("host1_1")){
					SystemProperties.set("persist.current.usb.mode","host2_0");
				}
				SystemClock.sleep(1000);			
			}
		}else {
			
		}
	}
	
	public static void setSynchronization(boolean value) {
		String usbConfig = SystemProperties.get("persist.current.usb.mode");
		if (value) {
			if ((usbConfig.equals("host2_0")) || (usbConfig.equals("host1_1"))) {
				SwitchUSBModeRunning("switch_usb_mode");
				while (true) {
					long startTime = System.currentTimeMillis();
					SystemClock.sleep(200);
					String strState = SystemProperties.get("init.svc.switch_usb_mode");
					if (strState.equals("stopped"))
						break;
					if ((System.currentTimeMillis() - startTime) > timeout) {
						break;
					}
				}
			}
		} else {
			if (usbConfig.equals("device")) {
				SwitchUSBModeRunning("switch_usb_mode");
				while (true) {
					long startTime = System.currentTimeMillis();
					SystemClock.sleep(200);
					String strState = SystemProperties.get("init.svc.switch_usb_mode");
					if (strState.equals("stopped"))
						break;
					if ((System.currentTimeMillis() - startTime) > timeout) {
						break;
					}
				}
			}
		}
	}

	public static void ResetFactory(Context mContext) {
		initSharePF(mContext);
		mtksetting mmtksetting = mtksetting.getInstance();
		String language = XmlParse.default_language.substring(0, 2);
		String country = XmlParse.default_language.substring(3, 5);
		final Locale l = new Locale(language, country);
		LocalePicker.updateLocale(l);

		// Update the system timezone value
		final AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		final String tzId = XmlParse.default_timezone;
		alarm.setTimeZone(tzId);

		final AudioManager mAudioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, XmlParse.volume[0], 0);
		mmtksetting.SetRearVolume(mtksetting.getVolume(XmlParse.volume[1]));
		mAudioManager.setStreamVolume(AudioManager.STREAM_GIS, XmlParse.volume[2], 0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_BACKCAR, XmlParse.volume[3], 0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_BLUETOOTH_SCO, XmlParse.volume[4], 0);
	}

	public static void copyFolder(String oldPath, String newPath) {
		try {
			File createFile = new File(newPath);
			createFile.mkdirs(); // ����ļ��в������������ļ���
			File a = new File(oldPath);
			String[] file = a.list();// �г���Ŀ¼�е�����
			File temp = null;
			for (int i = 0; i < file.length; i++) {

				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}
				if (temp.isFile()) {// ������ļ�
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/"
							+ (temp.getName()).toString());
					byte[] b = new byte[1024];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// �����Ŀ¼�������ļ���
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);// ���±���
				}
			}
		} catch (Exception e) {
			// System.out.println("�����ļ������ݲ�������");
			e.printStackTrace();
		}
	}

	// ************************ date time************************//
	
	public static boolean getAutoState(Context context,String name) {
		try {
			return Settings.Global.getInt(context.getContentResolver(), name) > 0;
		} catch (SettingNotFoundException snfe) {
			return false;
		}
	}

	public static String getCurrentDateTime(Context context) {
		StringBuffer strDatetime = new StringBuffer();
		strDatetime.append(tzutil.getDate());
		strDatetime.append(" ");
		strDatetime.append(getHourMinute(context)[0]);
		strDatetime.append(":");
		strDatetime.append(getHourMinute(context)[1]);
		return strDatetime.toString();
	}

	public static String getCurrentDate(Context context) {
		StringBuffer strDatetime = new StringBuffer();
		strDatetime.append(getDate());
		return strDatetime.toString();
	}

	public static String getCurrentTime(Context context) {
		StringBuffer strDatetime = new StringBuffer();
		strDatetime.append(getWeek(context));
		strDatetime.append("  ");
		strDatetime.append(getHourMinute(context)[0]);
		strDatetime.append(":");
		strDatetime.append(getHourMinute(context)[1]);
		return strDatetime.toString();
	}
	
	public static String getWeek(Context context) {
        final Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int week1 = calendar.get(Calendar.DAY_OF_WEEK);
        String[] weekdays = context.getResources().getStringArray(R.array.weekday);
        String week = weekdays[week1 - 1];
        return week;
	}
	
	public void set24Hour(Context context, boolean is24Hour) {
		Settings.System.putString(context.getContentResolver(), Settings.System.TIME_12_24,
				is24Hour ? "24" : "12");
	}

	public static boolean is24HourFormat(Context context) {
		ContentResolver cv = context.getContentResolver();
		String strTimeFormat = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.TIME_12_24);
		if (strTimeFormat != null && strTimeFormat.equals("24"))
			return true;
		else
			return false;
	}

	public static String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}

	public static String[] getHourMinute(Context context) {
		String[] res = new String[2];
		String time = "";
		Calendar c = Calendar.getInstance();
		int hour = 0;
		hour = c.get(Calendar.HOUR_OF_DAY);
		if (is24HourFormat(context)) {
			time += hour < 10 ? "0" + hour : hour;
		} else {
			int tens = hour > 12 ? (hour - 12) / 10 : hour / 10;
			int single = hour > 12 ? (hour - 12) % 10 : hour % 10;
			time = String.valueOf(tens) + String.valueOf(single);
		}
		res[0] = time;
		int minute = c.get(Calendar.MINUTE);
		if (minute < 10) {
			time = "0" + minute;
		} else
			time = "" + minute;
		res[1] = time;
		return res;
	}

	public static void setTime(Context context, int hourOfDay, int minute) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		long when = c.getTimeInMillis();
		if (when / 1000 < Integer.MAX_VALUE) {
			((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
		}
	}

	public static void setDate(Context context, int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		long when = c.getTimeInMillis();
		if (when / 1000 < Integer.MAX_VALUE) {
			((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
		}
	}

	public static int getDayOfMonth() {
		Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
		int day = aCalendar.getActualMaximum(Calendar.DATE);
		return day;
	}

	public static boolean checkLeapYear(int year) {
		boolean flag = false;
		if ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0))) {
			flag = true;
		}
		return flag;
	}

	public static int getDayOfMonth(int year, int month) {
		int days = 31;
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			days = 31;
			System.out.print(days + " days");
			break;
		case 2:
			days = checkLeapYear(year) ? 29 : 28;
			System.out.print(days + " days");
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			days = 30;
			System.out.print(days + " days");
			break;
		default:
			break;
		}
		return days;
	}

	public static class TimeZoneSet {

		public static final String KEY_ID = "id"; // value: String
		public static final String KEY_DISPLAYNAME = "name"; // value: String
		public static final String KEY_GMT = "gmt"; // value: String
		public static final String KEY_OFFSET = "offset"; // value: int
															// (Integer)
		public static final String XMLTAG_TIMEZONE = "timezone";
		public static final int HOURS_1 = 60 * 60000;

		public static TimeZoneSet mInstance;

		public TimeZoneSet(Context context) {

		}

		public static TimeZoneSet getInstance(Context context) {
			if (null == mInstance) {
				mInstance = new TimeZoneSet(context);
			}
			return mInstance;
		}

		public List<HashMap<String, Object>> getZones(Context context) {
			final List<HashMap<String, Object>> myData = new ArrayList<HashMap<String, Object>>();
			final long date = Calendar.getInstance().getTimeInMillis();
			try {
				XmlResourceParser xrp = context.getResources().getXml(R.xml.timezones);
				while (xrp.next() != XmlResourceParser.START_TAG)
					continue;
				xrp.next();
				while (xrp.getEventType() != XmlResourceParser.END_TAG) {
					while (xrp.getEventType() != XmlResourceParser.START_TAG) {
						if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
							return myData;
						}
						xrp.next();
					}
					if (xrp.getName().equals(XMLTAG_TIMEZONE)) {
						String id = xrp.getAttributeValue(0);
						String displayName = xrp.nextText();
						addItem(myData, id, displayName, date);
					}
					while (xrp.getEventType() != XmlResourceParser.END_TAG) {
						xrp.next();
					}
					xrp.next();
				}
				xrp.close();
			} catch (XmlPullParserException xppe) {
				Log.e(TAG, "Ill-formatted timezones.xml file");
			} catch (java.io.IOException ioe) {
				Log.e(TAG, "Unable to read timezones.xml file");
			}

			return myData;
		}

		private void addItem(List<HashMap<String, Object>> myData, String id, String displayName,
				long date) {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(KEY_ID, id);
			map.put(KEY_DISPLAYNAME, displayName);
			final TimeZone tz = TimeZone.getTimeZone(id);
			final int offset = tz.getOffset(date);
			final int p = Math.abs(offset);
			final StringBuilder name = new StringBuilder();
			name.append("GMT");

			if (offset < 0) {
				name.append('-');
			} else {
				name.append('+');
			}

			name.append(p / (HOURS_1));
			name.append(':');

			int min = p / 60000;
			min %= 60;

			if (min < 10) {
				name.append('0');
			}
			name.append(min);

			map.put(KEY_GMT, name.toString());
			map.put(KEY_OFFSET, offset);

			myData.add(map);
		}

		/**
		 * Searches {@link TimeZone} from the given {@link SimpleAdapter}
		 * object, and returns the index for the TimeZone.
		 * 
		 * @param adapter
		 *            SimpleAdapter constructed by
		 *            {@link #constructTimezoneAdapter(Context, boolean)}.
		 * @param tz
		 *            TimeZone to be searched.
		 * @return Index for the given TimeZone. -1 when there's no
		 *         corresponding list item. returned.
		 */
		public int getTimeZoneIndex(List<HashMap<String, Object>> list, String defaultId) {
			// final String defaultId = tz.getID();
			final int listSize = list.size();
			for (int i = 0; i < listSize; i++) {
				// Using HashMap<String, Object> induces unnecessary warning.
				final HashMap<?, ?> map = (HashMap<?, ?>) list.get(i);
				final String id = (String) map.get(KEY_ID);
				if (defaultId.equals(id)) {
					// If current timezone is in this list, move focus to it
					return i;
				}
			}
			return -1;
		}
	}

	public static class MyComparator implements Comparator<HashMap<?, ?>> {
		private String mSortingKey;

		public MyComparator(String sortingKey) {
			mSortingKey = sortingKey;
		}

		public void setSortingKey(String sortingKey) {
			mSortingKey = sortingKey;
		}

		public int compare(HashMap<?, ?> map1, HashMap<?, ?> map2) {
			Object value1 = map1.get(mSortingKey);
			Object value2 = map2.get(mSortingKey);

			/*
			 * This should never happen, but just in-case, put non-comparable
			 * items at the end.
			 */
			if (!isComparable(value1)) {
				return isComparable(value2) ? 1 : 0;
			} else if (!isComparable(value2)) {
				return -1;
			}

			return ((Comparable) value1).compareTo(value2);
		}

		private boolean isComparable(Object value) {
			return (value != null) && (value instanceof Comparable);
		}
	}

	/***************************** DatePicker&TimePicker ************************************/
	public static void setcolorfortimepickerdivider(TimePicker tp, int color) {
		Field[] myfs = null;
		try {
			myfs = tp.getClass().getDeclaredFields();
			for (Field field : myfs) {
				field.setAccessible(true);
				String name = field.getName();
				Log.e("datetimedialog", "timepicker:" + name + "=" + "\n");
				if (name.equalsIgnoreCase("mAmPmSpinner") || name.equalsIgnoreCase("mHourSpinner")
						|| name.equalsIgnoreCase("mMinuteSpinner")) {
					NumberPicker pk = (NumberPicker) field.get(tp);
					L.e("CarSettings","pk:" + (pk == null) + "  tp:" + (tp ==null));
					Field dvfd = pk.getClass().getDeclaredField("mSelectionDivider");
					dvfd.setAccessible(true);
					dvfd.set(pk, new ColorDrawable(color));
				} else if (name.equalsIgnoreCase("mDivider")) {
					TextView tv = (TextView) field.get(tp);
					tv.setText("");
				}
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setcolorfordatepickerdivider(MyDatePicker dp, int color) {
		Field[] myfs = null;
		try {
			myfs = dp.getClass().getDeclaredFields();
			for (Field field : myfs) {
				field.setAccessible(true);
				String name = field.getName();
				Log.e("datetimedialog", "datepicker:" + name + "=" + "\n");
				if (name.equalsIgnoreCase("mYearSpinner") || name.equalsIgnoreCase("mMonthSpinner")
						|| name.equalsIgnoreCase("mDaySpinner")) {
					NumberPicker pk = (NumberPicker) field.get(dp);
					Field dvfd = pk.getClass().getDeclaredField("mSelectionDivider");
					dvfd.setAccessible(true);
					dvfd.set(pk, new ColorDrawable(color));
				}
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void settextsizefortimepicker(TimePicker tp, float size) {
		Field[] myfs = null;
		try {
			myfs = tp.getClass().getDeclaredFields();
			for (Field field : myfs) {
				field.setAccessible(true);
				String name = field.getName();
				Log.e("datetimedialog", "timepicker:" + name + "=" + "\n");

				Object ob = field.get(tp);
				if (ob != null) {
					if (ob instanceof ViewGroup) {
						settextsize_viewgroup((ViewGroup) ob, size);
					} else if (ob instanceof View) {
						settextsize_view((View) ob, size);
					}
				}
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void settextsizefordatepicker(MyDatePicker dp, float size) {
		Field[] myfs = null;
		try {
			myfs = dp.getClass().getDeclaredFields();
			for (Field field : myfs) {
				field.setAccessible(true);
				String name = field.getName();
				Log.e("datetimedialog", "timepicker:" + name + "=" + "\n");

				Object ob = field.get(dp);
				if (ob != null) {
					if (ob instanceof ViewGroup) {
						((ViewGroup) ob).setMinimumWidth(150);
						settextsize_viewgroup((ViewGroup) ob, size);
					} else if (ob instanceof View) {
						settextsize_view((View) ob, size);
					}
				}
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void settextsize_viewgroup(ViewGroup vg, float size) {
		for (int i = 0;; i++) {
			Object ob = vg.getChildAt(i);
			if (ob == null) {
				break;
			} else {
				if (ob instanceof ViewGroup) {
					settextsize_viewgroup((ViewGroup) ob, size);
				} else if (ob instanceof View) {
					settextsize_view((View) ob, size);
				}
			}
		}
		Field[] fs = vg.getClass().getDeclaredFields();
		for (Field field : fs) {
			field.setAccessible(true);
			String name = field.getName();
			if (name.contains("Paint")) {
				try {
					Paint pt = (Paint) field.get(vg);
					if (pt != null) {
						pt.setTextSize(size);
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static void settextsize_view(View v, float size) {
		if (v instanceof EditText) {
			EditText et = (EditText) v;
			et.setTextSize(size);
		} else if (v instanceof TextView) {
			TextView et = (TextView) v;
			et.setTextSize(size);
		} else if (v instanceof Button) {
			Button et = (Button) v;
			et.setTextSize(size);
		} else if (v instanceof CheckBox) {
			CheckBox et = (CheckBox) v;
			et.setTextSize(size);
		} else {

			Field[] fs = v.getClass().getDeclaredFields();
			for (Field field : fs) {
				field.setAccessible(true);
				String name = field.getName();
				if (name.contains("Paint")) {
					try {
						Paint pt = (Paint) field.get(v);
						pt.setTextSize(size);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

}
