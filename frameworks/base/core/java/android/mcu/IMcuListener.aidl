
package android.mcu;

import android.mcu.McuRadioBandInfo;
import android.mcu.McuRadioPresetListInfo;
import android.mcu.McuBaseInfo;

/**
 * {@hide}
 */
oneway interface IMcuListener {
    void onMcuInfoChanged(in McuBaseInfo mcuBaseInfo, in int infoType);
}