ifeq ($(BOARD_USES_YECON_NEWUI),true)
ifeq ($(BOARD_USES_YECON_FACTORY_TEST),false)

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := \
        android-common \
        guava \
        android-support-v13 \
        android-support-v4 \

LOCAL_SRC_FILES := \
        $(call all-java-files-under, src) \
        src/com/android/browser/EventLogTags.logtags

#LOCAL_SRC_FILES := $(filter-out src/com/android/browser/search/DefaultSearchEngine.java, $(LOCAL_SRC_FILES))
#LOCAL_SRC_FILES := $(filter-out src/com/android/browser/search/OpenSearchSearchEngine.java, $(LOCAL_SRC_FILES))

#LOCAL_SRC_FILES += \
#	../../../autochips/packages/apps/Browser/src/com/android/browser/search/DefaultSearchEngine.java \
#	../../../autochips/packages/apps/Browser/src/com/android/browser/search/OpenSearchSearchEngine.java

LOCAL_PACKAGE_NAME := Browser

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_EMMA_COVERAGE_FILTER := *,-com.android.common.*

# We need the sound recorder for the Media Capture API.
LOCAL_REQUIRED_MODULES := SoundRecorder

include $(BUILD_PACKAGE)

# additionally, build tests in sub-folders in a separate .apk
include $(call all-makefiles-under,$(LOCAL_PATH))

endif
endif
