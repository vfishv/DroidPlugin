LOCAL_PATH := $(call my-dir) 
include $(CLEAR_VARS)
LOCAL_MODULE    := realloc
LOCAL_SRC_FILES := \
	MSHook/hook.cpp \
	MSHook/ARM.cpp \
	MSHook/Thumb.cpp \
	MSHook/x86.cpp \
	MSHook/x86_64.cpp \
	MSHook/Debug.cpp \
	MSHook/Hooker.cpp \
	MSHook/PosixMemory.cpp \
	MSHook/util.cpp \
	Core/RedirectAgent.cpp \
	Core/NativeIORedirect.cpp \
	XJniHook.cpp \
	JniUtils.cpp
LOCAL_LDLIBS	:= -llog
include $(BUILD_SHARED_LIBRARY)