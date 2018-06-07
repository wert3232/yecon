
#ifndef YECON_PORT_COMMON_H
#define YECON_PORT_COMMON_H
#include <jni.h>
#include <stdio.h>
#include "android/log.h"

#ifndef TAG
#define TAG "yecon_port"
#endif

#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO, TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGW(fmt, args...) __android_log_print(ANDROID_LOG_WARN, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

#endif
