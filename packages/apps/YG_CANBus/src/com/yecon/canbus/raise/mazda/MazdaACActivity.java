/**
 * @Title: MazdaACActivity.java
 * @Package com.yecon.canbus.raise.mazda
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 下午4:14:25
 * @version V1.0
 */
package com.yecon.canbus.raise.mazda;

import com.yecon.canbus.R;
import com.yecon.canbus.ui.CanbusACActivity;

/**
 * @ClassName: MazdaACActivity
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月15日 下午4:14:25
 *
 */
public class MazdaACActivity extends CanbusACActivity {

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
		ac.mRelateCmd = new int[2];
		ac.mRelateCmd[0] = RaiseMazdaParse.RAISE_MAZDA_DATACMD_AIR;
		ac.mRelateCmd[1] = RaiseGC7Parse.RAISE_MAZDA_DATACMD_GC7AIR;
		return ac;
	}

}
