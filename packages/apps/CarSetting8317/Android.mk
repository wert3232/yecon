#
# Copyright (C) 2008 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_STATIC_JAVA_LIBRARIES := android-common android-support-v13  rxjava2
#LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_SRC_FILES += $(call find-subdir-files,src -name "*.aidl" -and -not -name ".*")
LOCAL_SRC_FILES += $(call all-java-files-under, ../TuoXianUI/src)
LOCAL_SRC_FILES += $(call all-java-files-under, ../RxJava/src)

LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/res
LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/../TuoXianUI/res
LOCAL_AAPT_FLAGS := --auto-add-overlay --extra-packages com.tuoxianui.view
#LOCAL_SDK_VERSION := current
LOCAL_JAVA_LIBRARIES := autochips yecon
LOCAL_SHARED_LIBRARIES  := libWriteDataLib
LOCAL_PACKAGE_NAME := CarSetting8317
LOCAL_CERTIFICATE := platform
LOCAL_REQUIRED_MODULES := libWriteDataLib
LOCAL_JNI_SHARED_LIBRARIES := libWriteDataLib
include $(BUILD_PACKAGE)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := rxjava2:libs/rxjava-2.1.0.jar 
include $(call all-makefiles-under,$(LOCAL_PATH))
