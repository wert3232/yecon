package com.android.yecon.filemanager;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.yecon.copyInstall.R;

public class IconifiedTextView extends LinearLayout {
	// һ���ļ������ļ����ͼ��
	// ����һ����ֱ���Բ���
	private TextView mText = null;
	private ImageView mIcon = null;

	public IconifiedTextView(Context context, IconifiedText aIconifiedText) {
		super(context);
		// ���ò��ַ�ʽ
		this.setOrientation(HORIZONTAL);
		// this.setBackgroundColor(getResources().getColor(R.color.selector_color_state));
		mIcon = new ImageView(context);
		// ����ImageViewΪ�ļ���ͼ��
		mIcon.setImageDrawable(aIconifiedText.getIcon());
		// ����ͼ���ڸò����е����λ��
		mIcon.setPadding(8, 12, 6, 12);

		// ��ImageView��ͼ����ӵ��ò�����
		addView(mIcon, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		// �����ļ�����䷽ʽ�������С
		mText = new TextView(context);
		mText.setText(aIconifiedText.getText());
		// mText.setPadding(8, 10, 12, 14);
		mText.setGravity(Gravity.CENTER_VERTICAL);
		mText.setTextSize(getResources().getDimension(R.dimen.textsize_l));

		mText.setTextColor(getColors());

		// �����
		mText.setHorizontallyScrolling(true);
		mText.setEllipsize(TruncateAt.MARQUEE);
		mText.setMarqueeRepeatLimit(-1);

		// ���ļ�����ӵ�������
		addView(mText, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
	}

	public ColorStateList getColors() {
		int[][] states = { View.PRESSED_ENABLED_STATE_SET, View.FOCUSED_STATE_SET,
				View.EMPTY_STATE_SET, };
		int[] color = { getResources().getColor(R.color.textpresscolor), Color.RED, Color.WHITE };
		return new ColorStateList(states, color);
	}

	// �����ļ���
	public void setText(String words) {
		mText.setText(words);
	}

	// ����ͼ��
	public void setIcon(Drawable bullet) {
		mIcon.setImageDrawable(bullet);
	}
}
