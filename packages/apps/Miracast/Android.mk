ifeq ($(BOARD_USES_YECON_NEWUI),true)
ifeq ($(BOARD_USES_YECON_FACTORY_TEST),false)

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_JAVA_LIBRARIES := autochips yecon
LOCAL_PACKAGE_NAME := Miracast
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

########################
include $(call all-makefiles-under,$(LOCAL_PATH))

endif
endif
