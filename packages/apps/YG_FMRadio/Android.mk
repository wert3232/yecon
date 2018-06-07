ifeq ($(BOARD_USES_YECON_NEWUI),true)
ifeq ($(BOARD_USES_YECON_FACTORY_TEST),false)

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_SRC_FILES += $(call find-subdir-files,src -name "*.aidl" -and -not -name ".*")
LOCAL_SRC_FILES += $(call all-java-files-under, ../TuoXianUI/src)
LOCAL_SRC_FILES += $(call all-java-files-under, ../RxJava/src)
LOCAL_JAVA_LIBRARIES := autochips yecon

LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/res 
LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/../TuoXianUI/res

LOCAL_AAPT_FLAGS := --auto-add-overlay --extra-packages com.tuoxianui.view

LOCAL_STATIC_JAVA_LIBRARIES := androidv4 rxjava2
LOCAL_PACKAGE_NAME := YG_FMRadio
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := androidv4:libs/android-support-v4.jar \
					rxjava2:libs/rxjava-2.1.0.jar 
endif
endif