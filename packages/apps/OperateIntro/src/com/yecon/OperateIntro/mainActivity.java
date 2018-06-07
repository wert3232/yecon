package com.yecon.OperateIntro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.yecon.OperateIntro.HeaderLayout.onOneButtonListener;

public class mainActivity extends Activity implements OnClickListener, onOneButtonListener {

	final Integer IDs_btn[] = { R.id.id_Contents_0, R.id.id_Contents_1, R.id.id_Contents_2,
			R.id.id_Contents_3, R.id.id_Contents_4, R.id.id_Contents_5, R.id.id_Contents_6,
			R.id.id_Contents_7, R.id.id_Contents_8, R.id.id_Contents_9, R.id.id_Contents_10,
			R.id.id_Contents_11, R.id.id_Contents_12, R.id.id_Contents_13, };
	private HeaderLayout mHeaderLayout[] = new HeaderLayout[IDs_btn.length];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void initView() {
		setContentView(R.layout.main);
		String[] Contents = getResources().getStringArray(R.array.Contents);
		String[] directory = getResources().getStringArray(R.array.directory);
		int i = 0;
		for (HeaderLayout layout : mHeaderLayout) {
			layout = (HeaderLayout) findViewById(IDs_btn[i]);
			layout.setSubTitle(i + 1 + ". " + Contents[i]);
			layout.setHintTitle(directory[i]);
			layout.setOneButtonListener(layout, this);
			i++;
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_pre:
			SetSelection(--Util.index);
			break;
		case R.id.btn_next:
			SetSelection(++Util.index);
			break;

		default:
			break;
		}
	}

	void setSelection(int index) {

		Intent intent = new Intent();
		intent.setClass(this, GalleryActivity.class);
		intent.putExtra(Util.indexPage, index);
		startActivity(intent);
	}

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.id_Contents_0:
			setSelection(0);
			break;
		case R.id.id_Contents_1:
			setSelection(5);
			break;
		case R.id.id_Contents_2:
			setSelection(7);
			break;
		case R.id.id_Contents_3:
			setSelection(8);
			break;
		case R.id.id_Contents_4:
			setSelection(13);
			break;
		case R.id.id_Contents_5:
			setSelection(15);
			break;
		case R.id.id_Contents_6:
			setSelection(19);
			break;
		case R.id.id_Contents_7:
			setSelection(20);
			break;
		case R.id.id_Contents_8:
			setSelection(21);
			break;
		case R.id.id_Contents_9:
			setSelection(22);
			break;
		case R.id.id_Contents_10:
			setSelection(23);
			break;
		case R.id.id_Contents_11:
			setSelection(40);
			break;
		case R.id.id_Contents_12:
			setSelection(41);
			break;
		case R.id.id_Contents_13:
			setSelection(42);
			break;
		default:
			break;
		}
	}

	private void SetSelection(int value) {
		if (value < 0) {
			value = 0;
		} else if (value > Util.mImageIds.length - 1) {
			value = Util.mImageIds.length - 1;
		}
	}

	private void onHomePressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		startActivity(intent);
	}

}
