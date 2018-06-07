ifeq ($(BOARD_USES_YECON_NEWUI),false)
ifeq ($(BOARD_USES_YECON_FACTORY_TEST),false)

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_PACKAGE_NAME := PictureBrowser
LOCAL_CERTIFICATE := platform

LOCAL_JAVA_LIBRARIES := \
	android-support-v4 \
	autochips \
	yecon

include $(BUILD_PACKAGE)

endif
endif
