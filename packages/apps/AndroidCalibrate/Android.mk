#ifeq ($(DRIVER_TP_CTOUCH),false)
ifneq ($(filter YECON_TP_AUTODETECT,$(DRIVER_TP_TYPE)),)
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_JAVA_LIBRARIES := 

LOCAL_PACKAGE_NAME := MTKCalibrate
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

########################
#include $(call all-makefiles-under,$(LOCAL_PATH))
endif
