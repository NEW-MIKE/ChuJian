#ifndef __misc_h__
#define __misc_h__
#include <jni.h>
#include <android/log.h>

#ifndef ORG_NAME
#define ORG_NAME    "ToupTek Photonics Co., Ltd"
#endif

#ifndef NDEBUG
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, ORG_NAME, __VA_ARGS__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,  ORG_NAME,__VA_ARGS__)
#else
#define LOGE(...)
#define LOGI(...)
#endif

#ifndef TDIBWIDTHBYTES
#define TDIBWIDTHBYTES(bits)    ((unsigned)(((bits) + 31) & (~31)) / 8)
#endif

#endif
