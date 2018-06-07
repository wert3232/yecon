ifeq ($(BOARD_USES_YECON_NEWUI),false)
ifeq ($(BOARD_USES_MTK_MUSIC),true)
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4

LOCAL_SRC_FILES := $(call all-java-files-under, src) \
	src/com/yecon/music/IMediaPlaybackService.aidl

LOCAL_JAVA_LIBRARIES := autochips yecon

LOCAL_PACKAGE_NAME := Music

#LOCAL_SDK_VERSION := current

LOCAL_CERTIFICATE := platform

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))

endif
endif
