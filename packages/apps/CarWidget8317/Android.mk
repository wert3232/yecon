LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files,src)

LOCAL_JAVA_LIBRARIES := autochips yecon

LOCAL_PACKAGE_NAME := CarWidget
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)
