LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_C_INCLUDES += $(JNI_H_INCLUDE) 
LOCAL_SRC_FILES := user_sent.c
LOCAL_SHARED_LIBRARIES := liblog libcutils
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE := libWriteDataLib
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)