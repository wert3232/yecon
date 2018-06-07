/**
 * @Title: HyundaiCIActivity.java
 * @Package com.yecon.canbus.raise.hyundai
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 下午4:24:28
 * @version V1.0
 */
package com.yecon.canbus.raise.hyundai;

import com.yecon.canbus.R;
import com.yecon.canbus.ui.CanbusCIActivity;

/**
 * @ClassName: HyundaiCIActivity
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月15日 下午4:24:28
 *
 */
public class HyundaiCIActivity extends CanbusCIActivity {

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
		ac.mRelateCmd = new int[1];
		ac.mRelateCmd[0] = RaiseHyundaiParse.RAISE_HYUNDAI_DATACMD_TEMP;
		return ac;
	}

}
