/**
 * @Title: PeugeotACActivity.java
 * @Package com.yecon.canbus.raise.peugeot
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 下午3:57:34
 * @version V1.0
 */
package com.yecon.canbus.raise.peugeot;

import com.yecon.canbus.R;
import com.yecon.canbus.ui.CanbusACActivity;

/**
 * @ClassName: PeugeotACActivity
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月15日 下午3:57:34
 *
 */
public class PeugeotACActivity extends CanbusACActivity {

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
		ac.mRelateCmd[0] = RaisePeugeotParse.RAISE_PEUGEOT_DATACMD_AIR;
		return ac;
	}

}
