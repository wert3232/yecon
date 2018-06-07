ifeq ($(BOARD_USES_YECON_NEWUI),true)
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_MODULE_TAGS := optional

LOCAL_PACKAGE_NAME := YG_GpsTest

include $(BUILD_PACKAGE)
endif