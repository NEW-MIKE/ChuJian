JNI_PATH  :=$(call my-dir)
include $(call all-subdir-makefiles)
include $(CLEAR_VARS)
LOCAL_MODULE    := libjnilib
LOCAL_CFLAGS    := -Werror -O2 -fPIC -fvisibility=hidden -Wno-writable-strings
LOCAL_SRC_FILES :=  $(JNI_PATH)/cache.cpp\
    $(JNI_PATH)/camera.cpp\
    $(JNI_PATH)/toup.cpp\
    $(JNI_PATH)/cfits.cpp\
    $(JNI_PATH)/key_man.cpp

LOCAL_C_INCLUDES := $(JNI_PATH)/libastrovideo\
    $(JNI_PATH)/libcfitsio
LOCAL_LDLIBS    := -llog -landroid
LOCAL_SHARED_LIBRARIES := astrovideo\
    cfitsio
include $(BUILD_SHARED_LIBRARY)