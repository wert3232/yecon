LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_C_INCLUDES += $(JNI_H_INCLUDE) 
LOCAL_SRC_FILES := I2c.c SerialPort.c  I2cPort.c AtvPort.cpp atv/Cdt1028.cpp  atv/CdtCommunication1028.cpp atv/CdtOrder1028.cpp atv/WydMidSimulationTv.cpp
LOCAL_SHARED_LIBRARIES := liblog libcutils
LOCAL_PRELINK_MODULE := false
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE := libExDevPort
include $(BUILD_SHARED_LIBRARY)