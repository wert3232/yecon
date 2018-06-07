ifeq ($(BOARD_USES_YECON_NEWUI),true)
ifeq ($(BOARD_USES_YECON_FACTORY_TEST),false)
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_STATIC_JAVA_LIBRARIES := android-common android-support-v13 android-support-v4
LOCAL_JAVA_LIBRARIES := autochips yecon

LOCAL_PACKAGE_NAME := IPodPlayer

include $(BUILD_PACKAGE)

endif
endif
