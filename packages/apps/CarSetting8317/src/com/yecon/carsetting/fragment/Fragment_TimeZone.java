package com.yecon.carsetting.fragment;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.DialogFragment;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yecon.carsetting.R;
import com.yecon.carsetting.unitl.tzutil;

public class Fragment_TimeZone extends DialogFragment implements OnItemClickListener, OnClickListener {
	private static Context mContext;
	private ListView mTimeZoneListView;

	static public tzutil.TimeZoneSet mTimeZoneSet;
	private SimpleAdapter mTimezoneSortedAdapter;
	private SimpleAdapter mAlphabeticalAdapter;

	private static int defaultIndex;

	public Fragment_TimeZone(int timezoneIndex) {
		defaultIndex = timezoneIndex;
	}

	public Fragment_TimeZone(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	private void initData() {
		mContext = getActivity();
		mTimeZoneSet = tzutil.TimeZoneSet.getInstance(mContext);
		mTimezoneSortedAdapter = constructTimezoneAdapter(mContext, false);
		mAlphabeticalAdapter = constructTimezoneAdapter(mContext, true);
	}

	private void initView(View mView, Context context) {
		mTimeZoneListView = (ListView) mView.findViewById(R.id.id_listView);
		setSorting(true);
		mTimeZoneListView.setOnItemClickListener(this);
		
		mView.findViewById(R.id.back).setOnClickListener(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		/*setStyle(DialogFragment.STYLE_NO_FRAME, 0);*/
		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Wallpaper_NoTitleBar_Fullscreen);
		initData();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Window window = getDialog().getWindow();
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// return super.onCreateView(inflater, container, savedInstanceState);
		getDialog().setTitle(getResources().getString(R.string.general_set_timezone));
		View rootView = inflater.inflate(R.layout.layout_listview, container);
		initView(rootView, mContext);
		return rootView;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		final Map<?, ?> map = (Map<?, ?>) mTimeZoneListView.getItemAtPosition(position);
		final String tzId = (String) map.get(mTimeZoneSet.KEY_ID);

		// // Update the system timezone value
		// final Activity activity = getActivity();
		// final AlarmManager alarm = (AlarmManager)
		// activity.getSystemService(Context.ALARM_SERVICE);
		// alarm.setTimeZone(tzId);

		mOnItemClickListener.onClickItem(tzId);
		this.dismiss();
	}

	private void setSorting(boolean sortByTimezone) {
		final SimpleAdapter adapter = sortByTimezone ? mTimezoneSortedAdapter
				: mAlphabeticalAdapter;
		mTimeZoneListView.setAdapter(adapter);
		// defaultIndex = getTimeZoneIndex(sortedList, TimeZone.getDefault());
		if (defaultIndex >= 0) {
			mTimeZoneListView.setSelection(defaultIndex);
		}
	}

	public static SimpleAdapter constructTimezoneAdapter(Context context, boolean sortedByName) {
		return constructTimezoneAdapter(context, sortedByName,
				R.layout.date_time_setup_custom_list_item_2);
	}

	/**
	 * Constructs an adapter with TimeZone list. Sorted by TimeZone in default.
	 * 
	 * @param sortedByName
	 *            use Name for sorting the list.
	 */

	public static SimpleAdapter constructTimezoneAdapter(Context context, boolean sortedByName,
			final int layoutId) {
		final String[] from = new String[] { mTimeZoneSet.KEY_DISPLAYNAME, mTimeZoneSet.KEY_GMT };
		final int[] to = new int[] { android.R.id.text1, android.R.id.text2 };

		final String sortKey = (sortedByName ? mTimeZoneSet.KEY_DISPLAYNAME
				: mTimeZoneSet.KEY_OFFSET);

		final tzutil.MyComparator comparator = new tzutil.MyComparator(sortKey);
		final List<HashMap<String, Object>> sortedList = mTimeZoneSet.getZones(context);
		Collections.sort(sortedList, comparator);

		final LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final SimpleAdapter adapter = new SimpleAdapter(context, sortedList, layoutId, from, to) {
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = inflater.inflate(layoutId, parent, false);
				TextView text1 = (TextView) view.findViewById(to[0]);
				TextView text2 = (TextView) view.findViewById(to[1]);

				final Map<?, ?> map = (Map<?, ?>) sortedList.get(position);
				final String displayname = (String) map.get(mTimeZoneSet.KEY_DISPLAYNAME);
				final String gmt = (String) map.get(mTimeZoneSet.KEY_GMT);
				text1.setText(displayname);
				text2.setText(gmt);

				int color;
				if (defaultIndex == position) {
					view.setBackgroundResource(R.drawable.listbk_down);
					color = mContext.getResources().getColor(R.color.textpresscolor);
					text1.setTextColor(color);
					color = mContext.getResources().getColor(R.color.darker_gray);
					text2.setTextColor(color);
				} else {
					view.setBackgroundResource(R.xml.selector_listview_state);
					ColorStateList csl = (ColorStateList) mContext.getResources()
							.getColorStateList(R.color.selector_color_state);
					text1.setTextColor(csl);
					color = mContext.getResources().getColor(R.color.white);
					text2.setTextColor(color);
				}
				return view;
			};
		};
		return adapter;
	}

	private OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(OnItemClickListener mListener) {
		mOnItemClickListener = mListener;
	}

	// 定义dialog的回调事件
	public interface OnItemClickListener {
		void onClickItem(String str);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
				this.dismiss();
				break;
			default:
				break;
		}
	}

}
