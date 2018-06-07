package com.can.ui.toyota;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.TPMSInfo;
import com.can.ui.CanFrament;
import com.can.ui.draw.ResDef;

/**
 * ClassName:CanToyotaTpms
 * 
 * @function:胎压信息
 * @author Kim
 * @Date: 2016-6-23 下午2:31:13
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanToyotaTpms extends CanFrament {

	private View mObjView = null;
	private RelativeLayout mObjNotelayout = null;
	private RelativeLayout mObjCarTpmslayout = null;
	private LinearLayout mObjLineTpmslayout = null;

	private TextView[] mObjTextcarTpms = null;
	private Button[] mObjTextlineTpms = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.Init(mObjhHandler, this.getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (mObjView == null) {
			mObjView = inflater.inflate(R.layout.toyota_tpms, container, false);
			init();
		}
		return mObjView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.DeInit();
		super.onDestroy();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		super.getData(DDef.TPMS_CMD_ID);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	private void init() {
		if (mObjView != null) {

			mObjNotelayout = (RelativeLayout) mObjView
					.findViewById(R.id.layout_tpms_dev);
			mObjCarTpmslayout = (RelativeLayout) mObjView
					.findViewById(R.id.layout_tpms_car);
			mObjLineTpmslayout = (LinearLayout) mObjView
					.findViewById(R.id.layout_tpms_line);

			int iIndex = 0;
			mObjTextcarTpms = new TextView[ResDef.mToyotaTpmsVal1.length];
			for (int iter : ResDef.mToyotaTpmsVal1) {
				mObjTextcarTpms[iIndex] = (TextView) mObjView
						.findViewById(iter);
				iIndex++;
			}

			iIndex = 0;
			mObjTextlineTpms = new Button[ResDef.mToyotaTpmsVal2.length];
			for (int iter : ResDef.mToyotaTpmsVal2) {
				mObjTextlineTpms[iIndex] = (Button) mObjView.findViewById(iter);
				iIndex++;
			}
		}
	}

	private void setTpmsInfo(TPMSInfo objTpmsInfo) {
		if (this.isVisible()) {
			if (objTpmsInfo.mIsExistDevice == 0) {
				if (mObjNotelayout != null && mObjCarTpmslayout != null
						&& mObjLineTpmslayout != null) {
					mObjNotelayout.setVisibility(View.VISIBLE);
					mObjCarTpmslayout.setVisibility(View.INVISIBLE);
					mObjLineTpmslayout.setVisibility(View.INVISIBLE);
				}
			} else {
				if (mObjNotelayout != null && mObjCarTpmslayout != null
						&& mObjLineTpmslayout != null) {
					mObjNotelayout.setVisibility(View.INVISIBLE);

					int iIndex = 0;
					StringBuffer strTpmsInfo = new StringBuffer();

					String strUnit = getResources().getString(
							R.string.str_tx_tpms_unit_bar);
					if (objTpmsInfo.mTpmsUnit == 1) {
						strUnit = getResources().getString(
								R.string.str_tx_tpms_unit_psi);
					} else if (objTpmsInfo.mTpmsUnit == 2) {
						strUnit = getResources().getString(
								R.string.str_tx_tpms_unit_kpa);
					}

					if (objTpmsInfo.mTireShowMode == 0) {
						mObjCarTpmslayout.setVisibility(View.INVISIBLE);
						mObjLineTpmslayout.setVisibility(View.VISIBLE);

						for (@SuppressWarnings("unused")
						int iter : ResDef.mToyotaTpmsVal2) {

							strTpmsInfo.delete(0, strTpmsInfo.length());

							switch (iIndex) {
							case 0:
								if (objTpmsInfo.mFLTirePressure == 0xFF) {
									strTpmsInfo.append("-");
								} else {
									strTpmsInfo
											.append(objTpmsInfo.mFLTirePressure);
								}
								break;
							case 1:
								if (objTpmsInfo.mFRTirePressure == 0xFF) {
									strTpmsInfo.append("-");
								} else {
									strTpmsInfo
											.append(objTpmsInfo.mFRTirePressure);
								}
								break;
							case 2:
								if (objTpmsInfo.mBLTirePressure == 0xFF) {
									strTpmsInfo.append("-");
								} else {
									strTpmsInfo
											.append(objTpmsInfo.mBLTirePressure);
								}
								break;
							case 3:
								if (objTpmsInfo.mBRTirePressure == 0xFF) {
									strTpmsInfo.append("-");
								} else {
									strTpmsInfo
											.append(objTpmsInfo.mBRTirePressure);
								}
								break;
							case 4:
								if (objTpmsInfo.mSpareTirePressure == 0xFF) {
									strTpmsInfo.append("-");
								} else {
									strTpmsInfo
											.append(objTpmsInfo.mSpareTirePressure);
								}
								break;
							default:
								break;
							}

							if (mObjTextlineTpms[iIndex] != null) {

								if (iIndex == 4) {
									mObjTextlineTpms[iIndex]
											.setVisibility((objTpmsInfo.mShowSpareTire == 1) ? View.VISIBLE
													: View.INVISIBLE);
								}

								strTpmsInfo.append(strUnit);
								mObjTextlineTpms[iIndex].setText(strTpmsInfo
										.toString());
							}

							iIndex++;
						}
					} else {
						mObjCarTpmslayout.setVisibility(View.VISIBLE);
						mObjLineTpmslayout.setVisibility(View.INVISIBLE);

						for (@SuppressWarnings("unused")
						int iter : ResDef.mToyotaTpmsVal1) {

							strTpmsInfo.delete(0, strTpmsInfo.length());

							switch (iIndex) {
							case 0:
								if (objTpmsInfo.mFLTirePressure == 0xFF) {
									strTpmsInfo.append("-");
								} else {
									strTpmsInfo
											.append(objTpmsInfo.mFLTirePressure);
								}
								break;
							case 1:
								if (objTpmsInfo.mFRTirePressure == 0xFF) {
									strTpmsInfo.append("-");
								} else {
									strTpmsInfo
											.append(objTpmsInfo.mFRTirePressure);
								}
								break;
							case 2:
								if (objTpmsInfo.mBLTirePressure == 0xFF) {
									strTpmsInfo.append("-");
								} else {
									strTpmsInfo
											.append(objTpmsInfo.mBLTirePressure);
								}
								break;
							case 3:
								if (objTpmsInfo.mBRTirePressure == 0xFF) {
									strTpmsInfo.append("-");
								} else {
									strTpmsInfo
											.append(objTpmsInfo.mBRTirePressure);
								}
								break;
							case 4:
								if (objTpmsInfo.mSpareTirePressure == 0xFF) {
									strTpmsInfo.append("-");
								} else {
									strTpmsInfo
											.append(objTpmsInfo.mSpareTirePressure);
								}
								break;
							default:
								break;
							}

							if (mObjTextcarTpms[iIndex] != null) {

								if (iIndex == 4) {
									mObjTextcarTpms[iIndex]
											.setVisibility((objTpmsInfo.mShowSpareTire == 1) ? View.VISIBLE
													: View.INVISIBLE);
								}

								strTpmsInfo.append(strUnit);
								mObjTextcarTpms[iIndex].setText(strTpmsInfo
										.toString());
							}

							iIndex++;
						}
					}
				}
			}
		}
	}

	private Handler mObjhHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case DDef.FINISH_BIND:
				break;
			case DDef.TPMS_CMD_ID:
				setTpmsInfo((TPMSInfo) msg.obj);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
}
