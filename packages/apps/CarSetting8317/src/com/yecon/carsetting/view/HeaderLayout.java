package com.yecon.carsetting.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.RemotableViewMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.yecon.carsetting.R;

/**
 * @ClassName: HeaderLayout
 * @Description: TODO
 * @author lzy
 * @date 2016.4.19
 **/
public class HeaderLayout extends LinearLayout {
	private static final String TextView = null;
	private LayoutInflater mInflater;
	private View mHeader;
	private View mView;
	private LinearLayout mLayoutRightContainer;
	private ImageView mIcon;
	private TextView mSubTitle, mHintTitle;
	private String item_subTitle, item_hintTitle, item_two_button_left, item_two_button_right,
			item_bt_check_btTitle;
	private Drawable item_Drawable;

	private boolean mEnable = true;

	private int style_id = 1;
	private LinearLayout mLayoutRightImageButtonLayout;

	// only right text
	private TextView mRightTextTitle;

	// two button left/right
	private Button mLeftButton, mRightButton;
	private TextView mMiddleText;
	private onTwoButtonListener mTwoButtonListener;

	// only check 2
	private CheckBox mOneCheckBox;
	private onOneCheckBoxListener mOneCheckListener;

	// only right button 3

	private onOneButtonListener mOneButtonListener;
	private Button mOnlyOneButton;
	private TextView mPrompt_title, mPrompt_title2;

	// two button 4
	private RadioGroup item_two_radioButton;
	private RadioButton mRadioButton1, mRadioButton2;
	private onTwoRadioButtonListener mTwoRadioListener;

	// progress
	private int item_seekbarMax = 100, item_seekbarPos = 50;
	private TextView mSeekbarTitle;
	private SeekBar mSeekBar;

	private CheckBox mBtCheck_ck;
	private Button mBtCheck_bt;
	private final int STYLE_ONLY_TITLE = 0;
	private final int STYLE_LEFT_RIGHT_BUTTON = 1;
	private final int STYLE_ONLY_CHECKBOX = 2;
	private final int STYLE_ONLY_ONE_BUTTON = 3;
	private final int STYLE_TWO_RADIOBUTTON = 4;
	private final int STYLE_BT_CHECK = 5;
	private final int STYLE_SEEKBAR = 6;

	HeaderLayout getInstance() {
		HeaderLayout mHeaderLayout = new HeaderLayout(mContext);
		return mHeaderLayout;
	}

	public HeaderLayout(Context context) {
		super(context);
		init(context);
	}

	public HeaderLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Style
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HeaderLayout);
		// Sets the shadow drawable
		style_id = ta.getInt(R.styleable.HeaderLayout_item_style, 0);
		item_Drawable = ta.getDrawable(R.styleable.HeaderLayout_item_icon);
		item_subTitle = ta.getString(R.styleable.HeaderLayout_item_subTitle);
		item_hintTitle = ta.getString(R.styleable.HeaderLayout_item_hintTitle);

		if (style_id == STYLE_TWO_RADIOBUTTON) {
			item_two_button_left = ta.getString(R.styleable.HeaderLayout_item_two_button_left);
			item_two_button_right = ta.getString(R.styleable.HeaderLayout_item_two_button_right);
		} else if (style_id == STYLE_BT_CHECK) {
			item_bt_check_btTitle = ta.getString(R.styleable.HeaderLayout_item_bt_check_btTitle);
		} else if (style_id == STYLE_SEEKBAR) {
			item_seekbarMax = ta.getInt(R.styleable.HeaderLayout_item_seekbarMax, 100);
			item_seekbarPos = ta.getInt(R.styleable.HeaderLayout_item_seekbarPos, 50);
		}

		ta.recycle();
		init(context);
	}

	@Override
	@RemotableViewMethod
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		mEnable = enabled;
		mHeader.setEnabled(mEnable);
		mSubTitle.setEnabled(mEnable);
		mHintTitle.setEnabled(mEnable);
		if (mOnlyOneButton != null)
			mOnlyOneButton.setEnabled(mEnable);
		super.setEnabled(enabled);
	}

	public void init(Context context) {
		mInflater = LayoutInflater.from(context);
		// if (style_id == STYLE_ONLY_TITLE) {
		// mHeader = mInflater.inflate(R.layout.item_parent_onlytitle, null);
		// } else {
		mHeader = mInflater.inflate(R.layout.item_parent, null);
		// }
		addView(mHeader);
		mView = (View) mHeader.getParent();
		initViews();
		init(style_id);
	}

	public void initViews() {
		mLayoutRightContainer = (LinearLayout) findViewByHeaderId(R.id.item_parent_right);
		mIcon = (ImageView) findViewByHeaderId(R.id.item_icon);
		mSubTitle = (TextView) findViewByHeaderId(R.id.item_sub_title);
		mHintTitle = (TextView) findViewByHeaderId(R.id.item_hint_title);

		setIcon(item_Drawable);
		setSubTitle(item_subTitle);
		setHintTitle(item_hintTitle);

		mHeader.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mOneButtonListener != null) {
					mOneButtonListener.onOneButtonClick(mView);
				}
			}
		});

	}

	public View findViewByHeaderId(int id) {
		return mHeader.findViewById(id);
	}
	
	public void setSelected()
	{
		mHeader.setBackgroundResource(R.drawable.listbk_down);
	}

	public void init(int hStyle) {
		switch (hStyle) {
		case STYLE_ONLY_TITLE:
			defaultTitle();
			initOnlyRightText();
			break;

		case STYLE_LEFT_RIGHT_BUTTON:
			defaultTitle();
			initLeftRightButton();
			break;
		case STYLE_ONLY_CHECKBOX:
			defaultTitle();
			initOnlyCheckBox();
			break;
		case STYLE_ONLY_ONE_BUTTON:
			defaultTitle();
			initOnlyOneButton();
			break;
		case STYLE_TWO_RADIOBUTTON:
			defaultTitle();
			titleTwoRadioButton();
			break;

		case STYLE_BT_CHECK:
			defaultTitle();
			initBtCheckLayout();
			break;
		case STYLE_SEEKBAR:
			defaultTitle();
			initSeekBarLayout();
			break;
		}
	}

	private void defaultTitle() {
		mLayoutRightContainer.removeAllViews();
	}

	public void setIcon(Drawable icon) {
		if (icon != null) {
			mIcon.setVisibility(View.VISIBLE);
			mIcon.setBackground(icon);
		} else {
			mIcon.setVisibility(View.GONE);
		}
	}

	public void setRightTitle(CharSequence title) {
		if (mRightTextTitle == null)
			return;
		if (title != null) {
			mRightTextTitle.setText(title);
		}
	}

	public void setSubTitle(CharSequence title) {
		if (title != null) {
			mSubTitle.setVisibility(View.VISIBLE);
			mSubTitle.setText(title);
		} else {
			mSubTitle.setVisibility(View.GONE);
		}
	}

	public void setHintTitle(CharSequence title) {
		if (title != null && style_id != STYLE_ONLY_CHECKBOX) {
			mHintTitle.setVisibility(View.VISIBLE);
			mHintTitle.setText(title);
		} else {
			mHintTitle.setVisibility(View.GONE);
		}
	}

	public TextView getHintTitle() {
		return mHintTitle;
	}

	public void initOnlyRightText() {
		View view = mInflater.inflate(R.layout.item_only_right_text, null);
		mLayoutRightContainer.addView(view);
		mRightTextTitle = (TextView) view.findViewById(R.id.one_right_text);
	}

	public void initLeftRightButton() {
		View view = mInflater.inflate(R.layout.item_left_right_button, null);
		mLayoutRightContainer.addView(view);
		mLeftButton = (Button) view.findViewById(R.id.item_icon_left);
		mRightButton = (Button) view.findViewById(R.id.item_icon_right);
		mMiddleText = (TextView) view.findViewById(R.id.item_text_middle);
		mLeftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mTwoButtonListener != null) {
					mTwoButtonListener.onLeftButtonClick(mView);
				}
			}
		});
		mRightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mTwoButtonListener != null) {
					mTwoButtonListener.onRightButtonClick(mView);
				}
			}
		});
	}

	public TextView getMiddleTitle() {
		return mMiddleText;
	}

	public void setMiddleTitle(CharSequence title) {
		if (title != null) {
			mMiddleText.setVisibility(View.VISIBLE);
			mMiddleText.setText(title);
		} else {
			mMiddleText.setVisibility(View.INVISIBLE);
		}
	}

	public void setTwoButtonListener(onTwoButtonListener listener) {
		mTwoButtonListener = listener;
	}

	public interface onTwoButtonListener {
		void onLeftButtonClick(View view);

		void onRightButtonClick(View view);
	}

	public void initBtCheckLayout() {
		View view = mInflater.inflate(R.layout.item_button_checkbox, null);

		mLayoutRightContainer.addView(view);
		mBtCheck_ck = (CheckBox) view.findViewById(R.id.item_bt_check_ck);
		mBtCheck_bt = (Button) view.findViewById(R.id.item_bt_check_bt);

		if (item_bt_check_btTitle != null) {
			mBtCheck_bt.setText(item_bt_check_btTitle);
		}
		mBtCheck_ck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			}
		});
		mBtCheck_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			}
		});
	}

	public void initOnlyCheckBox() {
		View view = mInflater.inflate(R.layout.item_one_checkbox, null);
		mLayoutRightContainer.addView(view);
		mOneCheckBox = (CheckBox) view.findViewById(R.id.one_checkbox);
		mOneCheckBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mOneCheckListener != null) {
					mOneCheckListener.onCheckout(mView, ((CheckBox) arg0).isChecked());
				}
			}
		});
	}

	public void setOneCheckBoxListener(final onOneCheckBoxListener listener) {
		if (mOneCheckBox != null) {
			setOneCheckListener(listener);
		}
	}

	public void setOneCheckListener(onOneCheckBoxListener listener) {
		mOneCheckListener = listener;
	}

	public interface onOneCheckBoxListener {
		void onCheckout(View view, boolean value);
	}

	public CheckBox getOneCheckBox() {
		return mOneCheckBox;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void setPromptTitle(CharSequence title) {
		if (mPrompt_title == null)
			return;
		if (title != null) {
			mPrompt_title.setVisibility(View.VISIBLE);
			mPrompt_title.setText(title);
		} else {
			mPrompt_title.setVisibility(View.GONE);
		}
	}

	public void setPromptTitle2(CharSequence title) {
		if (mPrompt_title2 == null)
			return;
		if (title != null) {
			mPrompt_title2.setVisibility(View.VISIBLE);
			mPrompt_title2.setText(title);
		} else {
			mPrompt_title2.setVisibility(View.GONE);
		}

	}

	public void initOnlyOneButton() {
		View view = mInflater.inflate(R.layout.item_only_right_button, null);
		mLayoutRightContainer.addView(view);
		mOnlyOneButton = (Button) view.findViewById(R.id.one_right_button);
		mPrompt_title = (TextView) view.findViewById(R.id.item_prompt_title);
		mPrompt_title2 = (TextView) view.findViewById(R.id.item_prompt_title2);
		mOnlyOneButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mOneButtonListener != null) {
					mOneButtonListener.onOneButtonClick(mView);
				}
			}
		});
	}

	public void setOneButtonListener(onOneButtonListener listener) {
		mOneButtonListener = listener;
	}

	public interface onOneButtonListener {
		void onOneButtonClick(View view);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void titleTwoRadioButton() {

		View view = mInflater.inflate(R.layout.item_two_button, null);
		mLayoutRightContainer.addView(view);
		item_two_radioButton = (RadioGroup) view.findViewById(R.id.item_two_radioButton);
		mRadioButton1 = (RadioButton) view.findViewById(R.id.item_button_1);
		mRadioButton2 = (RadioButton) view.findViewById(R.id.item_button_2);

		if (item_two_button_left != null) {
			mRadioButton1.setText(item_two_button_left);
		}
		if (item_two_button_right != null) {
			mRadioButton2.setText(item_two_button_right);
		}

		item_two_radioButton
				.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						item_two_radioButton
								.playSoundEffect(android.view.SoundEffectConstants.CLICK);
						if (checkedId == R.id.item_button_1) {
							if (mTwoRadioListener != null) {
								mTwoRadioListener.onLeftRadioButtonClick(mView);
							}
						} else {
							if (mTwoRadioListener != null) {
								mTwoRadioListener.onRightRadioButtonClick(mView);
							}
						}
					}
				});
	}

	public void setRadioButtonTitle(int StrId1, int StrId2) {
		mRadioButton1.setText(StrId1);
		mRadioButton2.setText(StrId2);
	}

	public void setRadioCheck(int index) {
		if (index == 1) {
			item_two_radioButton.check(mRadioButton1.getId());
		} else {
			item_two_radioButton.check(mRadioButton2.getId());
		}
	}

	public void setTwoRadioButtonListener(HeaderLayout headlayout,
			final onTwoRadioButtonListener listener) {
		if (item_two_radioButton != null) {
			setTwoRadioButtonListener(listener);
		}
	}

	public void setTwoRadioButtonListener(onTwoRadioButtonListener listener) {
		mTwoRadioListener = listener;
	}

	public interface onTwoRadioButtonListener {
		void onLeftRadioButtonClick(View view);

		void onRightRadioButtonClick(View view);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void initSeekBarLayout() {
		View view = mInflater.inflate(R.layout.item_seekbar, null);
		mLayoutRightContainer.addView(view);
		mSeekbarTitle = (TextView) view.findViewById(R.id.item_seekbar_title);
		mSeekBar = (SeekBar) view.findViewById(R.id.item_seekbar);
		mSeekBar.setMax(item_seekbarMax);
		setSeekbarPos(item_seekbarPos);
		setSeekbarText(item_seekbarPos);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekbar, int progress, boolean arg2) {
				// TODO Auto-generated method stub
				// mSeekbarTitle.setText(progress + "");
				if (mOnProgressChange != null)
					mOnProgressChange.onProgressChanged(mView, seekbar, progress);
			}
		});
	}

	public void setSeekbarMax(int max) {
		if (mSeekBar != null) {
			item_seekbarMax = max;
			mSeekBar.setMax(item_seekbarMax);
		}
	}

	public int getSeekbarMax() {
		if (mSeekBar != null)
			return mSeekBar.getMax();
		return item_seekbarMax;
	}

	public void setSeekbarText(int pos) {
		if (mSeekbarTitle != null)
			mSeekbarTitle.setText(pos + "");
	}

	public void setSeekbarPos(int pos) {
		if (mSeekBar != null)
			mSeekBar.setProgress(pos);
	}

	private onProgressChanged mOnProgressChange;

	public interface onProgressChanged {
		public void onProgressChanged(View view, SeekBar mSeekbar, int progress);
	}

	public void setOnProgressChanged(onProgressChanged mP) {
		mOnProgressChange = mP;
	}

}
