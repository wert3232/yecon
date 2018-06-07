/**
 * @Title: GMCIActivity.java
 * @Package com.yecon.canbus.raise.gm
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 下午4:53:47
 * @version V1.0
 */
package com.yecon.canbus.raise.gm;

import com.yecon.canbus.R;
import com.yecon.canbus.ui.CanbusCIActivity;

/**
 * @ClassName: GMCIActivity
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月15日 下午4:53:47
 *
 */
public class GMCIActivity extends CanbusCIActivity {

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
		ac.mLayoutID = R.layout.canbus_ci_activity;
		ac.mRelateCmd = new int[2];
		ac.mRelateCmd[0] = RaiseGMParse.RAISE_GM_DATACMD_CAR;
		ac.mRelateCmd[1] = RaiseGMParse.RAISE_GM_DATACMD_SPEED;
		return ac;
	}

}
