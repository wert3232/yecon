/**
 * @Title: HondaACActivity.java
 * @Package com.yecon.canbus.raise.honda
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 下午4:39:24
 * @version V1.0
 */
package com.yecon.canbus.raise.honda;

import static com.yecon.canbus.info.CANBusConstants.TEMP_UNIT_C;

import com.yecon.canbus.R;
import com.yecon.canbus.ui.CanbusACActivity;

/**
 * @ClassName: HondaACActivity
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月15日 下午4:39:24
 *
 */
public class HondaCamryACActivity extends CanbusACActivity {

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
		ac.mRelateCmd[0] = RaiseHondaCamryParse.RAISE_HONDA_CAMRY_DATACMD_AIR;
		return ac;
	}

	@Override
	protected void updateTempUI() {
		// TODO Auto-generated method stub
		int tempUnit = getAir().getTempUnit();
		String tempUnitStr = getString((tempUnit == TEMP_UNIT_C) ? R.string.ac_temp_unit_c
				: R.string.ac_temp_unit_f);

		
		if (tempUnit == TEMP_UNIT_C) {
			float dbStep = 0.5f;
			int leftTemp = getAir().getLeftTemp();
			if (leftTemp == 0x00) {
				mTVTempLeft.setText(R.string.ac_temp_lo);
			} else if (leftTemp == 0x1F) {
				mTVTempLeft.setText(R.string.ac_temp_hi);
			} else if (leftTemp >= 0x01 && leftTemp <= 0x1D){
				float temp = 0.0f;
				temp = leftTemp * dbStep + 17.5f;
				mTVTempLeft.setText(temp + tempUnitStr);
			} else if (leftTemp >= 0x20 && leftTemp <= 0x23) {
				mTVTempLeft.setText(leftTemp * dbStep + tempUnitStr);
			}

			int rightTemp = getAir().getRightTemp();
			if (rightTemp == 0x00) {
				mTVTempRight.setText(R.string.ac_temp_lo);
			} else if (rightTemp == 0x1F) {
				mTVTempRight.setText(R.string.ac_temp_hi);
			} else if (rightTemp >= 0x01 && rightTemp <= 0x1D){
				float temp = 0.0f;
				temp = rightTemp * dbStep + 17.5f;
				mTVTempRight.setText(temp + tempUnitStr);
			} else if (rightTemp >= 0x20 && rightTemp <= 0x23) {
				mTVTempRight.setText(rightTemp * dbStep + tempUnitStr);
			}
		}
		else {
			float dbStep = 1f;
			int iTempLine = 0;
			int leftTemp = getAir().getLeftTemp();
			if (leftTemp == 0x00) {
				mTVTempLeft.setText(R.string.ac_temp_lo);
			} else if (leftTemp == 0xFF) {
				mTVTempLeft.setText(R.string.ac_temp_hi);
			} else {
				float temp = 0.0f;
				temp = leftTemp * dbStep + iTempLine;
				mTVTempLeft.setText(temp + tempUnitStr);
			}

			int rightTemp = getAir().getRightTemp();
			if (rightTemp == 0x00) {
				mTVTempRight.setText(R.string.ac_temp_lo);
			} else if (rightTemp == 0xFF) {
				mTVTempRight.setText(R.string.ac_temp_hi);
			} else {
				float temp = 0.0f;
				temp = rightTemp * dbStep + iTempLine;
				mTVTempRight.setText(temp + tempUnitStr);
			}
		}
		
	}
	
}
