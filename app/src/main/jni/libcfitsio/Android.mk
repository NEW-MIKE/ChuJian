LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)  
LOCAL_MODULE := cfitsio
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libcfitsio.so

include $(PREBUILT_SHARED_LIBRARY)