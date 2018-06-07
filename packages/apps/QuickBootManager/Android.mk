ifeq ($(BOARD_USES_YECON_NEWUI), true)
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
USE_STATIC_BROADCAST_RECV := false
TEST_RESUME_APP := false

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    src/com/autochips/quickbootmanager/StartAppInfo.java \
    src/com/autochips/quickbootmanager/QuickBootService.java \
    src/com/autochips/quickbootmanager/QuickBootReceiver.java \
    src/com/autochips/quickbootmanager/Utils.java \
    src/com/autochips/quickbootmanager/GisWhiteListParse.java \
    src/com/autochips/quickbootmanager/filters/Filter.java \
    src/com/autochips/quickbootmanager/filters/FilterHub.java \
    src/com/autochips/quickbootmanager/filters/InputMethodFilter.java \
    src/com/autochips/quickbootmanager/filters/WallpaperSetterFilter.java

ifeq ($(USE_STATIC_BROADCAST_RECV), true)
LOCAL_MANIFEST_FILE := staticrecv/AndroidManifest.xml
LOCAL_SRC_FILES += \
    src/com/autochips/quickbootmanager/staticrecv/Config.java
else
LOCAL_MANIFEST_FILE := dynamicrecv/AndroidManifest.xml
LOCAL_SRC_FILES += \
    src/com/autochips/quickbootmanager/dynamicrecv/Config.java
endif

ifeq ($(TEST_RESUME_APP), true)

LOCAL_SRC_FILES += \
    src/com/autochips/quickbootmanager/test/TestResumeApp.java

LOCAL_MANIFEST_FILE := test/AndroidManifest.xml
endif

LOCAL_JAVA_LIBRARIES := autochips
LOCAL_PACKAGE_NAME := QuickBootManager
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)
endif