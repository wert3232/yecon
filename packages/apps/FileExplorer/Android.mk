LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := androidV4 rxjava2
LOCAL_STATIC_JAVA_LIBRARIES += library
LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_SRC_FILES += $(call find-subdir-files,src -name "*.aidl" -and -not -name ".*")
LOCAL_SRC_FILES += $(call all-java-files-under, ../TuoXianUI/src)
LOCAL_SRC_FILES += $(call all-java-files-under, ../RxJava/src)

LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/res 
LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/../TuoXianUI/res
LOCAL_AAPT_FLAGS := --auto-add-overlay --extra-packages com.tuoxianui.view
LOCAL_JAVA_LIBRARIES += autochips yecon
LOCAL_PACKAGE_NAME := FileExplorer
LOCAL_CERTIFICATE := platform
#LOCAL_SDK_VERSION := current
#LOCAL_PROGUARD_ENABLED := disabled
LOCAL_PROGUARD_FLAG_FILES := proguard.cfg
include $(BUILD_PACKAGE)


# Link libmad.so
include $(CLEAR_VARS)  
LOCAL_PREBUILT_LIBS :=libmad:libs/armeabi/libmad.so
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES :=androidV4:libs/android-support-v4.jar \
					rxjava2:libs/rxjava-2.1.0.jar 
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES +=library:libs/library.jar
#LOCAL_MODULE_TAGS := eng 
include $(BUILD_MULTI_PREBUILT)