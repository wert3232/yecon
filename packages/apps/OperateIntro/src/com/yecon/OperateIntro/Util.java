package com.yecon.OperateIntro;

import android.content.Context;

public class Util {
	public static final String indexPage = "IndexPage";
	public static final int ID_MSG_SLIDE = 300;
	public static final long TIMER_DELAY = 5000;
	public static final long TIMER_PERIOD = 5000;
	public static int imgWidth = 1024;
	public static int imgHeight = 600;
	public static int index = 5;
	public static Integer[] mImageIds = { R.drawable.img1, R.drawable.img2, R.drawable.img3,
			R.drawable.img4, R.drawable.img5, R.drawable.img6, R.drawable.img7, R.drawable.img8,
			R.drawable.img9, R.drawable.img10, R.drawable.img11, R.drawable.img12,
			R.drawable.img13, R.drawable.img14, R.drawable.img15, R.drawable.img16,
			R.drawable.img17, R.drawable.img18, R.drawable.img19, R.drawable.img20,
			R.drawable.img21, R.drawable.img22, R.drawable.img23, R.drawable.img24,
			R.drawable.img25, R.drawable.img26, R.drawable.img27, R.drawable.img28,
			R.drawable.img29, R.drawable.img30, R.drawable.img31, R.drawable.img32,
			R.drawable.img33, R.drawable.img34, R.drawable.img35, R.drawable.img36,
			R.drawable.img37, R.drawable.img38, R.drawable.img39, R.drawable.img40,
			R.drawable.img41, R.drawable.img42, R.drawable.img43, R.drawable.img44, };

	public static void setTitle(Context mContext) {
		/*
		 * String[] Contents =
		 * mContext.getResources().getStringArray(R.array.Contents); String
		 * strTitle; if (Util.index == 0) { strTitle =
		 * mContext.getString(R.string.app_name) + ">" + Contents[0]; } else if
		 * (Util.index >= 1 && Util.index <= 2) { strTitle =
		 * mContext.getString(R.string.app_name) + ">" + Contents[1]; } else if
		 * (Util.index == 3) { strTitle = mContext.getString(R.string.app_name)
		 * + ">" + Contents[2]; } else if (Util.index >= 4 && Util.index <= 6) {
		 * strTitle = mContext.getString(R.string.app_name) + ">" + Contents[3];
		 * } else if (Util.index >= 7 && Util.index <= 9) { strTitle =
		 * mContext.getString(R.string.app_name) + ">" + Contents[4]; } else if
		 * (Util.index >= 10 && Util.index <= 15) { strTitle =
		 * mContext.getString(R.string.app_name) + ">" + Contents[5]; } else if
		 * (Util.index >= 16 && Util.index <= 19) { strTitle =
		 * mContext.getString(R.string.app_name) + ">" + Contents[6]; } else if
		 * (Util.index >= 20 && Util.index <= 33) { strTitle =
		 * mContext.getString(R.string.app_name) + ">" + Contents[7]; } else if
		 * (Util.index >= 34) { strTitle = mContext.getString(R.string.app_name)
		 * + ">" + Contents[8]; } else { return; } Intent intent = new
		 * Intent(ACTION_UPDATE_ACTIVITY_TITLE);
		 * intent.putExtra(INTENT_EXTRA_ACTIVITY_TITLE, strTitle);
		 * mContext.sendBroadcast(intent);
		 */
	}
}
