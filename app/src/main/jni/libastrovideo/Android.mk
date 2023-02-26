LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)  
LOCAL_MODULE := astrovideo
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libastrovideo.so
include $(PREBUILT_SHARED_LIBRARY)