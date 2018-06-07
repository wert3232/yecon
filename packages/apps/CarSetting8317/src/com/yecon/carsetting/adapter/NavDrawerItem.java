package com.yecon.carsetting.adapter;

public class NavDrawerItem {

	private String mSubTitle, mHintTitle;
	private int mUpIconid, mDownIconid;

	// boolean to set visiblity of the counter
	private boolean isCounterVisible = false;

	public NavDrawerItem() {

	}

	public NavDrawerItem(String mSubTitle, String mHintTitle, int mIconUp, int mIconDown) {
		this.mSubTitle = mSubTitle;
		this.mHintTitle = mHintTitle;
		this.mDownIconid = mIconDown;
		this.mUpIconid = mIconUp;

	}

	public String getmSubTitle() {
		return mSubTitle;
	}

	public void setmSubTitle(String mSubTitle) {
		this.mSubTitle = mSubTitle;
	}

	public String getmHintTitle() {
		return mHintTitle;
	}

	public void setmHintTitle(String mHintTitle) {
		this.mHintTitle = mHintTitle;
	}

	public int getmUpIconid() {
		return mUpIconid;
	}

	public void setmUpIconid(int mUpIconid) {
		this.mUpIconid = mUpIconid;
	}

	public int getmDownIconid() {
		return mDownIconid;
	}

	public void setmDownIconid(int mDownIconid) {
		this.mDownIconid = mDownIconid;
	}

	public boolean isCounterVisible() {
		return isCounterVisible;
	}

	public void setCounterVisible(boolean isCounterVisible) {
		this.isCounterVisible = isCounterVisible;
	}

	// public void setCounterVisibility(boolean isCounterVisible){
	// this.isCounterVisible = isCounterVisible;
	// }
}
