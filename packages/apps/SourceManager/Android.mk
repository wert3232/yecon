ifeq ($(BOARD_USES_YECON_NEWUI),true)
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src) \
	src/com/yecon/sourcemanager/ISourceManagerService.aidl

LOCAL_JAVA_LIBRARIES := autochips yecon

LOCAL_PACKAGE_NAME := SourceManager
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)
endif