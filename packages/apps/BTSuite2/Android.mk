ifeq ($(BOARD_USES_YECON_NEWUI),true)
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

# Only compile source java files in this apk.
LOCAL_SRC_FILES := $(call all-java-files-under, src) 

LOCAL_SRC_FILES += $(call find-subdir-files,src -name "*.aidl" -and -not -name ".*")
LOCAL_SRC_FILES += $(call all-java-files-under, ../TuoXianUI/src)
LOCAL_SRC_FILES += $(call all-java-files-under, ../RxJava/src)
#LOCAL_SRC_FILES += $(call find-subdir-files,src/nfore/android/bt/servicemanager -name "*.aidl" -and -not -name ".*")

LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/res
LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/../TuoXianUI/res
LOCAL_AAPT_FLAGS := --auto-add-overlay --extra-packages com.tuoxianui.view

LOCAL_JAVA_LIBRARIES := autochips yecon

LOCAL_PACKAGE_NAME := BTSuite2
LOCAL_CERTIFICATE := platform

##add
LOCAL_STATIC_JAVA_LIBRARIES := nForceAPI rxjava2

include $(BUILD_PACKAGE)

##### add
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES :=nForceAPI:libs/nForeAPI.jar \
rxjava2:libs/rxjava-2.1.0.jar 
include $(BUILD_MULTI_PREBUILT)
########################
#include $(call all-makefiles-under,$(LOCAL_PATH))

endif
