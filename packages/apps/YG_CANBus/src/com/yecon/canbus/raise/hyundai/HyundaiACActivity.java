/**
 * @Title: HyundaiACActivity.java
 * @Package com.yecon.canbus.raise.hyundai
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 下午4:23:54
 * @version V1.0
 */
package com.yecon.canbus.raise.hyundai;

import static com.yecon.canbus.info.CANBusConstants.TEMP_UNIT_C;

import com.yecon.canbus.R;
import com.yecon.canbus.ui.CanbusACActivity;

/**
 * @ClassName: HyundaiACActivity
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月15日 下午4:23:54
 *
 */
public class HyundaiACActivity extends CanbusACActivity {

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
		ac.mRelateCmd[0] = RaiseHyundaiParse.RAISE_HYUNDAI_DATACMD_AIR;
		return ac;
	}

	@Override
	protected void updateTempUI() {
		// TODO Auto-generated method stub
		int tempUnit = getAir().getTempUnit();
		String tempUnitStr = getString((tempUnit == TEMP_UNIT_C) ? R.string.ac_temp_unit_c
				: R.string.ac_temp_unit_f);
		float dbStep = (tempUnit == TEMP_UNIT_C) ? 0.5f : 0.5f * 1.8f;
		int iTempLine = (tempUnit == TEMP_UNIT_C) ? 17 : 49;

		int leftTemp = getAir().getLeftTemp();
		if (leftTemp == 0x00) {
			mTVTempLeft.setText(R.string.ac_temp_lo);
		} else if (leftTemp >= 0x1E) {
			mTVTempLeft.setText(R.string.ac_temp_hi);
		} else {
			float temp = 0.0f;
			temp = leftTemp * dbStep + iTempLine;
			mTVTempLeft.setText(temp + tempUnitStr);
		}

		int rightTemp = getAir().getRightTemp();
		if (rightTemp == 0x00) {
			mTVTempRight.setText(R.string.ac_temp_lo);
		} else if (rightTemp >= 0x1E) {
			mTVTempRight.setText(R.string.ac_temp_hi);
		} else {
			float temp = 0.0f;
			temp = rightTemp * dbStep + iTempLine;
			mTVTempRight.setText(temp + tempUnitStr);
		}
	}

	
}
