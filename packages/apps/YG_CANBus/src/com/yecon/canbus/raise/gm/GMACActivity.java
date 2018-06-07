/**
 * @Title: GMACActivity.java
 * @Package com.yecon.canbus.raise.gm
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 下午4:53:15
 * @version V1.0
 */
package com.yecon.canbus.raise.gm;

import com.yecon.canbus.R;
import com.yecon.canbus.info.CANBusConstants;
import com.yecon.canbus.ui.CanbusACActivity;

/**
 * @ClassName: GMACActivity
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月15日 下午4:53:15
 *
 */
public class GMACActivity extends CanbusACActivity {

	/**
	 * <p>Title: getCanbusUIConstruct</p>
	 * <p>Description: </p>
	 * @return
	 * @see com.yecon.canbus.ui.CANBusUI#getCanbusUIConstruct()
	 */
	@Override
	public CanbusUIConstruct getCanbusUIConstruct() {
		// TODO Auto-generated method stub
		CanbusUIConstruct ac = new CanbusUIConstruct();
		ac.mLayoutID = R.layout.canbus_ac_activity;
		ac.mRelateCmd = new int[1];
		ac.mRelateCmd[0] = RaiseGMParse.RAISE_GM_DATACMD_AIR;
		return ac;
	}

	@Override
	protected void updateTempUI() {
		// TODO Auto-generated method stub
		int tempUnit = getAir().getTempUnit();
		if (tempUnit == CANBusConstants.TEMP_UNIT_C) {
			String tempUnitStr = getString(R.string.ac_temp_unit_c);

			int leftTemp = getAir().getLeftTemp();
			if (leftTemp == 0x00) {
				mTVTempLeft.setText(R.string.ac_temp_lo);
			} else if (leftTemp == 0x1E) {
				mTVTempLeft.setText(R.string.ac_temp_hi);
			} else if (leftTemp == 0x1D) {
				mTVTempLeft.setText("16" + tempUnitStr);
			} else if (leftTemp == 0x1F) {
				mTVTempLeft.setText("16.5" + tempUnitStr);
			} else if (leftTemp == 0x20) {
				mTVTempLeft.setText("15" + tempUnitStr);
			} else if (leftTemp == 0x21) {
				mTVTempLeft.setText("15.5" + tempUnitStr);
			} else if (leftTemp == 0x22) {
				mTVTempLeft.setText("31" + tempUnitStr);
			} else if (leftTemp >= 0x01 && leftTemp <= 0x1c){
				double temp = 0.0;
				temp = leftTemp * 0.5 + 16.5;
				mTVTempLeft.setText(temp + tempUnitStr);
			}

			int rightTemp = getAir().getRightTemp();
			if (rightTemp == 0x00) {
				mTVTempRight.setText(R.string.ac_temp_lo);
			} else if (rightTemp == 0x1E) {
				mTVTempRight.setText(R.string.ac_temp_hi);
			} else if (rightTemp == 0x1D) {
				mTVTempRight.setText("16" + tempUnitStr);
			} else if (rightTemp == 0x1F) {
				mTVTempRight.setText("16.5" + tempUnitStr);
			} else if (rightTemp == 0x20) {
				mTVTempRight.setText("15" + tempUnitStr);
			} else if (rightTemp == 0x21) {
				mTVTempRight.setText("15.5" + tempUnitStr);
			} else if (rightTemp == 0x22) {
				mTVTempRight.setText("31" + tempUnitStr);
			} else if (rightTemp >= 0x01 && rightTemp <= 0x1c){
				double temp = 0.0;
				temp = rightTemp * 0.5 + 16.5;
				mTVTempRight.setText(temp + tempUnitStr);
			}
		}
	}
	
	
}
