package com.can.ui.draw;

import java.util.ArrayList;
import com.can.activity.R;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

public class PopDialog extends DialogFragment implements OnClickListener,
		OnItemClickListener, SeekBar.OnSeekBarChangeListener {

	public static enum E_POPDIALOG_TYPE {
		ePopDialog_choose, ePopDialog_carset_list, ePopDialog_carset_seekbar
	};

	private String mStrText = "";
	private E_POPDIALOG_TYPE mePopDialogType;
	private TextView mObjTextViewTips = null;
	private TextView mObjTextViewSure = null;
	private TextView mObjTextViewCancel = null;
	private ListView mObjListInfo = null;
	private listAdapter mObjlistAdapter = null;

	private SeekBar mObjSeekBar = null;
	private TextView mObjSeekBarText = null;

	private int misel = 0;
	private int miSeekbarStep = 0;
	private int miSeekOffset = 0;
	private int miSeekbarMax = 0;
	private int miSeekbarPos = 0;
	private String mStrseekbar = "";
	private ArrayList<String> mArrayList = new ArrayList<String>();

	public PopDialog(String string, E_POPDIALOG_TYPE eType) {
		// TODO Auto-generated constructor stub
		mStrText = string;
		mePopDialogType = eType;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setStyle(DialogFragment.STYLE_NO_FRAME, R.style.popdialogtheme);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		Bitmap dialog = BitmapFactory.decodeResource(getResources(),
				R.drawable.dialog_prompt_bk);
		Window window = getDialog().getWindow();
		window.setLayout(dialog.getWidth(), dialog.getHeight());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View objView = null;

		if (mePopDialogType == E_POPDIALOG_TYPE.ePopDialog_choose) {
			objView = inflater.inflate(R.layout.pop_dialog, container, false);
			mObjTextViewTips = (TextView) objView
					.findViewById(R.id.textview_title);
			mObjTextViewSure = (TextView) objView
					.findViewById(R.id.textview_yes);
			mObjTextViewSure.setOnClickListener(this);
			mObjTextViewCancel = (TextView) objView
					.findViewById(R.id.textview_no);
			mObjTextViewCancel.setOnClickListener(this);
			mObjTextViewTips.setText(mStrText);
		} else if (mePopDialogType == E_POPDIALOG_TYPE.ePopDialog_carset_list) {
			objView = inflater
					.inflate(R.layout.pop_dailog_v1, container, false);
			mObjTextViewTips = (TextView) objView
					.findViewById(R.id.pop_dailog_title);
			mObjTextViewTips.setText(mStrText);

			mObjlistAdapter = new listAdapter(this.getActivity());
			mObjListInfo = (ListView) objView
					.findViewById(R.id.pop_dailog_list_info);
			mObjListInfo.setAdapter(mObjlistAdapter);
			mObjListInfo.setOnItemClickListener(this);
		
		} else if (mePopDialogType == E_POPDIALOG_TYPE.ePopDialog_carset_seekbar) {
			objView = inflater
					.inflate(R.layout.pop_dailog_v2, container, false);
			mObjTextViewTips = (TextView) objView
					.findViewById(R.id.pop_dailog_v2_title);
			mObjTextViewTips.setText(mStrText);

			mObjSeekBar = (SeekBar) objView
					.findViewById(R.id.pop_dailog_seekbar);
			mObjSeekBarText = (TextView) objView
					.findViewById(R.id.pop_dailog_seekbar_val);
			mObjSeekBar.setOnSeekBarChangeListener(this);

			if (mObjSeekBar != null && mObjSeekBarText != null) {
				mObjSeekBar.setMax(miSeekbarMax);
				mObjSeekBar.setProgress(miSeekbarPos/miSeekbarStep);

				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append(miSeekbarPos);
				stringBuffer.append(mStrseekbar);

				mObjSeekBarText.setText(stringBuffer.toString());
			}
		}

		return objView;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	public void putdata(ArrayList<String> arrayList, int isel) {
		this.misel = isel;
		this.mArrayList = arrayList;
	}

	public void setSeekbarVal(int iMax, int iPos, int iStep, int ioffset, String string) {
		this.miSeekbarMax = iMax;
		this.miSeekbarPos = iPos;
		this.miSeekOffset = ioffset;
		this.mStrseekbar  = string;
		this.miSeekbarStep = iStep;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.textview_yes:
			if (mConfirmListener != null) {
				mConfirmListener.onConfirm();
				dismiss();
			}
			break;
		case R.id.textview_no:
			dismiss();
			break;
		default:
			break;
		}
	}

	public class listAdapter extends BaseAdapter {

		private Context mObjContext;
		private LayoutInflater mObjInflater;

		public listAdapter(Context context) {
			// TODO Auto-generated constructor stub
			mObjContext = context;
			mObjInflater = (LayoutInflater) mObjContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mArrayList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mArrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView objTextView;

			if (convertView == null) {
				convertView = mObjInflater.inflate(R.layout.pop_dailog_list,
						null);
				objTextView = (TextView) convertView
						.findViewById(R.id.pop_dailog_list_tx);

				convertView.setTag(objTextView);
			} else {
				objTextView = (TextView) convertView.getTag();
			}

			if (objTextView != null) {
				objTextView.setText(mArrayList.get(position));
				
				if (misel == position) {
					objTextView.setBackgroundResource(R.drawable.list_info_s);
				}else {
					objTextView.setBackgroundResource(R.drawable.list_info_n);
				}
			}
			
			return convertView;
		}
	}

	private OnConfirmListener mConfirmListener;

	public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
		mConfirmListener = onConfirmListener;
	}

	// 定义dialog的回调事件
	public interface OnConfirmListener {
		void onConfirm();

		void onSelPos(int iPos);

		void onSeekVal(int iVal);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (mConfirmListener != null) {
			misel = position;
			mObjlistAdapter.notifyDataSetChanged();
			mConfirmListener.onSelPos(position);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		if (fromUser) {
			if (mObjSeekBarText != null) {
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append(progress*miSeekbarStep+miSeekOffset);
				stringBuffer.append(mStrseekbar);
				mObjSeekBarText.setText(stringBuffer.toString());
			}

			if (mConfirmListener != null) {
				mConfirmListener.onSeekVal(progress*miSeekbarStep+miSeekOffset);
			}
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}
}
