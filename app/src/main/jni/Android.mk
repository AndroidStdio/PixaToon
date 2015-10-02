LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

include C:/Users/atuljadhav/Documents/Personal/android/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE    := image_filters
LOCAL_SRC_FILES := jni_part.cpp filters.cpp
LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)
