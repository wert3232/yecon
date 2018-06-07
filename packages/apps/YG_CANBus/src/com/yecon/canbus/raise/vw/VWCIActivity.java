/**
 * @Title: VWCIActivity.java
 * @Package com.yecon.canbus.raise.vw
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 下午5:03:56
 * @version V1.0
 */
package com.yecon.canbus.raise.vw;

import com.yecon.canbus.R;
import com.yecon.canbus.ui.CanbusCIActivity;

/**
 * @ClassName: VWCIActivity
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月15日 下午5:03:56
 *
 */
public class VWCIActivity extends CanbusCIActivity {

	/**
	 * <p>Title: getCanbusUIConstruct</p>
	 * <p>Description: </p>
	 * @return
	 * @see com.yecon.canbus.ui.CANBusUI#getCanbusUIConstruct()
	 */
	@Override
	public CanbusUIConstruct getCanbusUIConstruct() {
		// TODO Auto-generated method stub
		CanbusUIConstruct ci = new CanbusUIConstruct();
		ci.mLayoutID = R.layout.canbus_ci_activity;
		ci.mRelateCmd = new int[5];
		ci.mRelateCmd[0] = RaiseVWParse.RAISE_VW_DATACMD_CAR;
		ci.mRelateCmd[1] = RaiseVWParse.RAISE_VW_DATACMD_CARSTATUS;
		ci.mRelateCmd[2] = RaiseVWGolf7Parse.RAISE_VW_GOLF7_DATACMD_CARSTATUS;
		ci.mRelateCmd[3] = RaiseVWGolf7Parse.RAISE_VW_GOLF7_DATACMD_OUTSIDETEMP;
		ci.mRelateCmd[4] = RaiseVWGolf7Parse.RAISE_VW_GOLF7_DATACMD_SPEED;
		return ci;
	}

}
