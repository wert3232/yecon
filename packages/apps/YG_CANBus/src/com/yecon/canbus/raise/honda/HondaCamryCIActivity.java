/**
 * @Title: HondaCIActivity.java
 * @Package com.yecon.canbus.raise.honda
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 下午4:39:54
 * @version V1.0
 */
package com.yecon.canbus.raise.honda;

import com.yecon.canbus.R;
import com.yecon.canbus.ui.CanbusCIActivity;

/**
 * @ClassName: HondaCIActivity
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月15日 下午4:39:54
 *
 */
public class HondaCamryCIActivity extends CanbusCIActivity {

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
		ac.mRelateCmd[0] = RaiseHondaCamryParse.RAISE_HONDA_CAMRY_DATACMD_CAR;
		ac.mRelateCmd[1] = RaiseHondaCamryParse.RAISE_HONDA_CAMRY_DATACMD_SPEED;
		return ac;
	}

}
