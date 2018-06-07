ifeq ($(BOARD_USES_YECON_NEWUI),true)
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_SRC_FILES += $(call find-subdir-files,src -name "*.aidl" -and -not -name ".*")
LOCAL_SRC_FILES += $(call all-java-files-under, ../MyUI/src)
LOCAL_SRC_FILES += $(call all-java-files-under, ../TuoXianUI/src)
LOCAL_SRC_FILES += $(call all-java-files-under, ../RxJava/src)

LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/res 
LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/../MyUI/res
LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/../TuoXianUI/res
LOCAL_AAPT_FLAGS := --auto-add-overlay --extra-packages com.myui.view --extra-packages com.tuoxianui.view

LOCAL_JAVA_LIBRARIES := autochips yecon
LOCAL_SHARED_LIBRARIES  := libExDevPort
LOCAL_STATIC_JAVA_LIBRARIES := rxjava2
LOCAL_PACKAGE_NAME := TDR
LOCAL_CERTIFICATE := platform
LOCAL_REQUIRED_MODULES := libExDevPort

include $(BUILD_PACKAGE)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := rxjava2:libs/rxjava-2.1.0.jar 
########################
include $(call all-makefiles-under,$(LOCAL_PATH))
endif
