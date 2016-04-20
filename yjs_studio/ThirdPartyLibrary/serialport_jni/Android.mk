LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)


# All of the source files that we will compile.
LOCAL_SRC_FILES:= SerialPort.cpp

# All of the shared libraries we link against.
LOCAL_LDLIBS += -llog


# This is the target being built.
LOCAL_MODULE:= serialport

include $(BUILD_SHARED_LIBRARY)