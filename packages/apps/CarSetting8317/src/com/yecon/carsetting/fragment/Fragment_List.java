package com.yecon.carsetting.fragment;

import java.util.Locale;

import android.app.DialogFragment;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.internal.app.LocalePicker.LocaleInfo;
import com.yecon.carsetting.R;

public class Fragment_List extends DialogFragment implements OnItemClickListener {
	private static Context mContext;
	private ListView mListView;
	private static final String SAVE_TARGET_LOCALE = "locale";

	ArrayAdapter<String> adapter;
	private Locale mTargetLocale;

	private int mID;
	private static String[] stringTypeArray;
	private static int selectItem = -1;
	private boolean mExitFlag = true;
	private int main_layout = 0;
	private int item_layout = 0;
	private boolean isfullScreen = false;
	public Fragment_List() {
		super();
	}
	public Fragment_List(boolean isfullScreen) {
		super();
		this.isfullScreen = isfullScreen;
	}
	public Fragment_List(int main_layout,int item_layout) {
		super();
		this.main_layout = main_layout;
		this.item_layout = item_layout;
	}

	public Fragment_List(Context context, int width, int height) {

	}

	public void setData(int id, String[] stringArray, int select) {
		mID = id;
		stringTypeArray = stringArray;
		setSelectItem(select);
	}

	public void setData(int id, String[] stringArray, boolean exitFlag) {
		mID = id;
		stringTypeArray = stringArray;
		setSelectItem(-1);
		mExitFlag = exitFlag;
	}

	public void setData(int id, String[] stringArray) {
		mID = id;
		stringTypeArray = stringArray;
	}

	public void setSelectItem(int select) {
		selectItem = select;
	}

	private void initData() {
		mContext = getActivity();
	}

	private void initView(View mView, Context context) {
		mListView = (ListView) mView.findViewById(R.id.id_listView);
		if(item_layout != 0){
			adapter = constructAdapter(getActivity(),item_layout,R.id.language_item_title);
		}else{
			adapter = constructAdapter(getActivity());
		}
		mListView.setAdapter(adapter);
		// mListView.setCacheColorHint(Color.TRANSPARENT);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		// mListView.setSelected(true);
		mListView.setSelection(selectItem);
		// mListView.setItemChecked(selectItem, true);
		adapter.notifyDataSetInvalidated();
		mListView.setOnItemClickListener(this);
		mView.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});
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
		View rootView; 
		if(main_layout != 0){
			rootView = inflater.inflate(main_layout, container);
		}else{
			rootView = inflater.inflate(R.layout.layout_listview, container);
		}
		initView(rootView, mContext);
		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(isfullScreen){	
			setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		}else{
			setStyle(DialogFragment.STYLE_NO_FRAME, 0);
		}
		initData();
		if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_TARGET_LOCALE)) {
			mTargetLocale = new Locale(savedInstanceState.getString(SAVE_TARGET_LOCALE));
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mTargetLocale != null) {
			outState.putString(SAVE_TARGET_LOCALE, mTargetLocale.toString());
		}
	}

	/**
	 * Constructs an Adapter object containing Locale information. Content is
	 * sorted by {@link LocaleInfo#label}.
	 */
	public static ArrayAdapter<String> constructAdapter(Context context) {
		return constructAdapter(context, R.layout.general_language_item, R.id.language_item_title);
	}

	public static ArrayAdapter<String> constructAdapter(Context context, final int layoutId,
			final int fieldId) {

		final LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		return new ArrayAdapter<String>(context, layoutId, fieldId, stringTypeArray) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view;
				TextView text;
				if (convertView == null) {
					view = inflater.inflate(layoutId, parent, false);
					text = (TextView) view.findViewById(fieldId);
					view.setTag(text);
				} else {
					view = convertView;
					text = (TextView) view.getTag();
				}

				if (selectItem == position) {
					text.setBackgroundResource(R.drawable.listbk_down);
					int color = mContext.getResources().getColor(R.color.textpresscolor);
					text.setTextColor(color);
				} else {
					text.setBackgroundResource(R.xml.selector_listview_state);
					ColorStateList csl = (ColorStateList) mContext.getResources()
							.getColorStateList(R.color.selector_color_state);
					text.setTextColor(csl);
				}
				text.setText(stringTypeArray[position]);

				return view;
			}
		};
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		mListView.setSelectionFromTop(position, 0);
		setSelectItem(position);
		adapter.notifyDataSetInvalidated();
		if (mListener != null)
			mListener.onListDlgSetValue(mID, position);
		if (mExitFlag)
			dismiss();
	}

	OnListDlgListener mListener;

	public void setOnItemClickListener(OnListDlgListener Listener) {
		// TODO Auto-generated method stub
		mListener = Listener;
	}

	public interface OnListDlgListener {
		void onListDlgSetValue(int id, int value);
	}

}
