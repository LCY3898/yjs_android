/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include <stdlib.h>
#include <stdio.h>
#include <jni.h>
#include <assert.h>

#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>
#include <errno.h>

#include "android/log.h"

static const char *TAG = "serial_port";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)
#define LOGW(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)

const char* kClassName = "com/efit/sport/pad/serial/SerialPort"; //指定要注册的类

const int NO_VERIFY = 1;
const int EVENT_VERIFY = 2;
const int ODD_VERIFY = 3;

static speed_t getBaudrate(jint baudrate) {
    switch (baudrate) {
        case 0:
            return B0;
        case 50:
            return B50;
        case 75:
            return B75;
        case 110:
            return B110;
        case 134:
            return B134;
        case 150:
            return B150;
        case 200:
            return B200;
        case 300:
            return B300;
        case 600:
            return B600;
        case 1200:
            return B1200;
        case 1800:
            return B1800;
        case 2400:
            return B2400;
        case 4800:
            return B4800;
        case 9600:
            return B9600;
        case 19200:
            return B19200;
        case 38400:
            return B38400;
        case 57600:
            return B57600;
        case 115200:
            return B115200;
        case 230400:
            return B230400;
        case 460800:
            return B460800;
        case 500000:
            return B500000;
        case 576000:
            return B576000;
        case 921600:
            return B921600;
        case 1000000:
            return B1000000;
        case 1152000:
            return B1152000;
        case 1500000:
            return B1500000;
        case 2000000:
            return B2000000;
        case 2500000:
            return B2500000;
        case 3000000:
            return B3000000;
        case 3500000:
            return B3500000;
        case 4000000:
            return B4000000;
        default:
            return -1;
    }
}

/**
* 设置串口数据，校验位,速率，停止位
* @param nBits 类型 int数据位 取值 位7或8
* @param nEvent 类型 char 校验类型 取值N ,E, O,S
 * N清除校验位, E:even偶校验, O:odd 奇校验
* @param mSpeed 类型 int 速率 取值 2400,4800,9600,115200
* @param mStop 类型 int 停止位 取值1 或者 2
 *@param block 是否阻塞模式，1:阻塞,  0:非阻塞
*/
jobject open_serial(JNIEnv *env, jobject thiz, jstring path,jint baudrate, jint nBits,jint nStop,jint verifyBit,jint block) {
    int fd;
    speed_t speed;
    jobject mFileDescriptor;

    LOGD("init native Check arguments");
    /* Check arguments */
    {
        speed = getBaudrate(baudrate);
        if (speed == -1) {
            /* TODO: throw an exception */
            LOGE("Invalid baudrate");
            return NULL;
        }
    }

    LOGD("init native Opening device!");
    /* Opening device */
    {
        jboolean iscopy;
        const char *path_utf = env->GetStringUTFChars(path, &iscopy);
        LOGD("Opening serial port %s", path_utf);
//		fd = open(path_utf, O_RDWR | O_DIRECT | O_SYNC);
        if(block == 1) {
            fd = open(path_utf, O_RDWR | O_NOCTTY);
        } else {
            fd = open(path_utf, O_RDWR | O_NOCTTY | O_NONBLOCK | O_NDELAY);
        }

        LOGD("open() fd = %d", fd);
        env->ReleaseStringUTFChars(path, path_utf);
        if (fd == -1) {
            /* Throw an exception */
            LOGE("Cannot open port %d",baudrate);
            /* TODO: throw an exception */
            return NULL;
        }
    }

    LOGD("init native Configure device!");
    /* Configure device */
    {
        struct termios cfg;
        if (tcgetattr(fd, &cfg)) {
            LOGE("Configure device tcgetattr() failed 1");
            close(fd);
            return NULL;
        }

        cfmakeraw(&cfg);
        cfsetispeed(&cfg, speed);
        cfsetospeed(&cfg, speed);


        switch(nStop){//设置停止位
            case 1:
                cfg.c_cflag &= ~CSTOPB;
                break;
            case 2:
                cfg.c_cflag |= CSTOPB;
                break;
            default:
                LOGW("nStop:%d,invalid param",nStop);
                break;
        }

        switch(nBits){//设置数据位数
            case 7:
                cfg.c_cflag &=~CSIZE;
                cfg.c_cflag |=CS7;
                break;
            case 8:
                cfg.c_cflag &=~CSIZE;
                cfg.c_cflag |=CS8;
                break;
            default:
                LOGW("nBits:%d,invalid param",nBits);
                break;
        }

        char nevent = 'N';
        if(verifyBit == NO_VERIFY) {
            nevent = 'N';
        } else if(verifyBit == ODD_VERIFY) {
            nevent = 'O';
        } else if(verifyBit == EVENT_VERIFY) {
            nevent = 'E';
        }

        switch(nevent)
        {
            case 'O':
                cfg.c_cflag |= PARENB;
                cfg.c_cflag |= PARODD;
                cfg.c_iflag |= (INPCK | ISTRIP);
                break;
            case 'E':
                cfg.c_cflag |= PARENB;
                cfg.c_cflag &= ~PARODD;
                cfg.c_iflag |= (INPCK | ISTRIP);
                break;
            case 'N':
                cfg.c_cflag &= ~PARENB;
                break;
        }

        if (tcsetattr(fd, TCSANOW, &cfg)) {
            LOGE("Configure device tcsetattr() failed 2");
            close(fd);
            return NULL;
        }

        tcflush(fd, TCOFLUSH);
        tcflush(fd, TCIFLUSH);
    }

    /* Create a corresponding file descriptor */
    {
        jclass cFileDescriptor = env->FindClass("java/io/FileDescriptor");
        jmethodID iFileDescriptor = env->GetMethodID(cFileDescriptor,"<init>", "()V");
        jfieldID descriptorID = env->GetFieldID(cFileDescriptor,"descriptor", "I");
        mFileDescriptor = env->NewObject(cFileDescriptor,iFileDescriptor);
        env->SetIntField(mFileDescriptor, descriptorID, (jint) fd);
    }

    return mFileDescriptor;
}


jint close_serial(JNIEnv * env, jobject thiz)
{
    jclass SerialPortClass = env->GetObjectClass(thiz);
    jclass FileDescriptorClass = env->FindClass("java/io/FileDescriptor");

    jfieldID mFdID = env->GetFieldID(SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
    jfieldID descriptorID = env->GetFieldID(FileDescriptorClass, "descriptor", "I");

    jobject mFd = env->GetObjectField(thiz, mFdID);
    jint descriptor = env->GetIntField(mFd, descriptorID);

    LOGD("close(fd = %d)", descriptor);
    close(descriptor);
    return 1;
}


/*
* 丢弃串口的收发缓冲区内的所有未完成发送和接收的数据。即清空串口收发缓冲区。
* fd: 串口设备句柄。
* return：
	<0: 发生错误。
	=0: 清空串口收发缓冲区成功
*/
int clear_buf(JNIEnv * env, jobject thiz)
{

    jclass SerialPortClass = env->GetObjectClass(thiz);
    jclass FileDescriptorClass = env->FindClass("java/io/FileDescriptor");

    jfieldID mFdID = env->GetFieldID(SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
    jfieldID descriptorID = env->GetFieldID(FileDescriptorClass, "descriptor", "I");

    jobject mFd = env->GetObjectField(thiz, mFdID);
    jint descriptor = env->GetIntField(mFd, descriptorID);
    int iret = tcflush(descriptor, TCIOFLUSH);
    if (-1 == iret) return -errno;
    return iret;
}

static JNINativeMethod gMethods[] = {
        { "open", "(Ljava/lang/String;IIIII)Ljava/io/FileDescriptor;",(void*) open_serial},
        { "close", "()I",(void*) close_serial },
        { "clearBuf", "()I",(void*) clear_buf },
};

/*
 * 为某一个类注册本地方法
 */
static int registerNativeMethods(JNIEnv* env, const char* className,
                                 JNINativeMethod* gMethods, int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

/*
 * 为所有类注册本地方法
 */
static int registerNatives(JNIEnv* env) {

    return registerNativeMethods(env, kClassName, gMethods,
                                 sizeof(gMethods) / sizeof(gMethods[0]));
}

/*
 * System.loadLibrary("lib")时调用
 * 如果成功返回JNI版本, 失败返回-1
 */
 jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;
    jint result = -1;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);

    if (!registerNatives(env)) { //注册
        return -1;
    }
    //成功
    result = JNI_VERSION_1_4;

    return result;
}

