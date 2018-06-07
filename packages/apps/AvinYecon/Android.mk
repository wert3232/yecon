ifeq ($(BOARD_USES_YECON_NEWUI),true)
LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_JAVA_LIBRARIES := autochips yecon
LOCAL_SHARED_LIBRARIES  := libExDevPort
LOCAL_PACKAGE_NAME := AVINYecon
LOCAL_CERTIFICATE := platform
LOCAL_REQUIRED_MODULES := libExDevPort

include $(BUILD_PACKAGE)

########################
include $(call all-makefiles-under,$(LOCAL_PATH))
endif
