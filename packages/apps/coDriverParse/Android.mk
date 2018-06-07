ifeq ($(BOARD_USES_CODRIVE),true)
ifeq ($(BOARD_USES_YECON_FACTORY_TEST),false)

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := android-common android-support-v13 CoDriverSDK NaviSDK
LOCAL_SRC_FILES := $(call all-subdir-java-files)


LOCAL_JAVA_LIBRARIES := autochips yecon


LOCAL_PACKAGE_NAME := coDriverParse

#LOCAL_SDK_VERSION := current

LOCAL_CERTIFICATE := platform

#LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)

include $(CLEAR_VARS) 
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += CoDriverSDK:libs/CoDriverSDK.jar \
	NaviSDK:libs/NaviSDK-2016-05-27_11-31-50_Release.jar
include $(BUILD_MULTI_PREBUILT) 

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))

endif
endif
