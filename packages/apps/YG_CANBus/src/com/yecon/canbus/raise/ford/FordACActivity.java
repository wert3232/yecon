/**
 * @Title: FordACActivity.java
 * @Package com.yecon.canbus.raise.Ford
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 上午9:16:32
 * @version V1.0
 */
package com.yecon.canbus.raise.ford;

import com.yecon.canbus.R;
import com.yecon.canbus.ui.CanbusACActivity;

/**
 * @ClassName: FordACActivity
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月15日 上午9:16:32
 *
 */
public class FordACActivity extends CanbusACActivity {

	/**
	 * @return ActivityConstruct
	 * @see com.yecon.canbus.ui.CanbusACActivity#getCanbusUIConstruct()
	 */
	@Override
	public CanbusUIConstruct getCanbusUIConstruct() {
		// TODO Auto-generated method stub
		CanbusUIConstruct ac = new CanbusUIConstruct();
		ac.mLayoutID = R.layout.canbus_ac_activity;
		ac.mRelateCmd = new int[1];
		ac.mRelateCmd[0] = RaiseFordParse.RAISE_FORD_DATACMD_AIR;
		return ac;
	}
}
