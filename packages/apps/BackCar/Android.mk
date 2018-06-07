ifeq ($(BOARD_USES_YECON_NEWUI),true)
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := BackCar
LOCAL_CERTIFICATE := platform
LOCAL_JAVA_LIBRARIES := autochips yecon


include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))
endif
