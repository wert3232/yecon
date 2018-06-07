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

import com.yecon.canbus.R;
import com.yecon.canbus.ui.CanbusACActivity;

/**
 * @ClassName: HondaACActivity
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月15日 下午4:39:24
 *
 */
public class HondaACActivity extends CanbusACActivity {

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
		ac.mRelateCmd[0] = RaiseHondaParse.RAISE_HONDA_DATACMD_AIR;
		return ac;
	}

}
