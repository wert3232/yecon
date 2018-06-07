ifeq ($(BOARD_USES_YECON_NEWUI),true)
ifeq ($(BOARD_USES_YECON_FACTORY_TEST),false)

LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files,src)\
                   src/com/autochips/weather/IWeatherService.aidl

LOCAL_JAVA_LIBRARIES := autochips

LOCAL_PACKAGE_NAME := ATCWeather
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

endif
endif
