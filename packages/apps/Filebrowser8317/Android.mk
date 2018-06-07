ifeq ($(BOARD_USES_YECON_NEWUI),false)

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_JAVA_LIBRARIES := yecon

LOCAL_PACKAGE_NAME := FileBrowser8317
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

########################
#include $(call all-makefiles-under,$(LOCAL_PATH))

endif