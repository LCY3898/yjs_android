
#include <android/log.h>

#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#include <asm/termbits.h>
#include <termios.h>
#include <assert.h>
#include <jni.h>


#define DEV_ZIGBEE   "/dev/ttyUSB0"
#define DEV_BAUT    115200
//defined for host 
const char* kClassName = "com/efit/sport/pad/serial/SerialPort"; //指定要注册的类

#define LOG_TAG "com.efit.sport.pad.SerialDriver"
static  int fd = 0;

#define  ALOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)



const int NO_VERIFY = 1;
const int EVENT_VERIFY = 2;
const int ODD_VERIFY = 3;

/* 计算异或和 */
unsigned char fcs_lrc(unsigned char * buf, int buflen)
{
	unsigned char lrc = 0;
	if (buf != NULL) {
		while(buflen --) {
			lrc ^= *buf ++;
		}
	}
	return lrc;
}


/*
* 丢弃串口的收发缓冲区内的所有未完成发送和接收的数据。即清空串口收发缓冲区。
* fd: 串口设备句柄。
* return：
	<0: 发生错误。
	=0: 清空串口收发缓冲区成功
*/
static int nd_rs232clear(int fd)
{
	int iret = tcflush(fd, TCIOFLUSH);
	if (-1 == iret) return -errno;
	return iret;
}

int	nd_rs232init(int fd,int nspeed,int nbits,char nevent,int nstop)
{
	struct termios newtio,oldtio;

	if(tcgetattr(fd,&oldtio) != 0)
	{
		return -errno;
	}

	memset(&newtio,0,sizeof(newtio));
	
	newtio.c_cflag |= CLOCAL | CREAD;
	newtio.c_cflag &= ~CSIZE;

	switch(nbits)
	{
		case 7:
			newtio.c_cflag |= CS7;break;
		case 8:
			newtio.c_cflag |= CS8;break;
		default:
			return -1;
	}

	switch(nevent)
	{
		case 'O':
			newtio.c_cflag |= PARENB;
			newtio.c_cflag |= PARODD;
			newtio.c_iflag |= (INPCK | ISTRIP);
			break;
		case 'E':
			newtio.c_cflag |= PARENB;
			newtio.c_cflag &= ~PARODD;
			newtio.c_iflag |= (INPCK | ISTRIP);
			break;
		case 'N':
			newtio.c_cflag &= ~PARENB;
			break;
		default:
			return -1;
	}

	switch(nspeed)
	{
		case 9600:
               		cfsetispeed(&newtio,B9600);
               		cfsetospeed(&newtio,B9600);
               		break;

		case 38400:
					cfsetispeed(&newtio,B38400);
					cfsetospeed(&newtio,B38400);
					break;

					
		case 57600:
               		cfsetispeed(&newtio,B57600);
               		cfsetospeed(&newtio,B57600);
               		break;
		case 115200:
               		cfsetispeed(&newtio,B115200);
               		cfsetospeed(&newtio,B115200);
               		break;
		case 460800:
               		cfsetispeed(&newtio,B460800);
               		cfsetospeed(&newtio,B460800);
               		break;
		
		default:
				return -1;
               	break;
	}
	
	if(nstop == 1)
			newtio.c_cflag &= ~CSTOPB;
	else if(nstop == 2)
			newtio.c_cflag |= CSTOPB;
	else 
		return -1;
	
	newtio.c_cc[VTIME] = 0;
	newtio.c_cc[VMIN] = 1; //block mode until one byte received at least

	if(tcsetattr(fd,TCSANOW,&newtio) != 0)
	{
		return -errno;
	}

	//config uart in block mode.
	fcntl(fd, F_SETFL, 0);
	
	tcflush(fd, TCOFLUSH);
	tcflush(fd, TCIFLUSH);
	
	return 0;
}

//=============================================================================
static unsigned char frame[ 32 ];

static int send_frame(void *frame, int len)
{
	if (fd <= 0) return 0;
	return write(fd, frame, len);
}

static int write_cmd(unsigned char cmd)
{
	if (fd <= 0) return 0;

	memset(frame, 0, sizeof(frame));
	frame[0] = 0xFE;
	frame[1] = 0x01; 
	frame[2] = cmd; //cmd
	frame[3] = fcs_lrc(&frame[1], frame[1]+1); //fcs, from seq -> val

	ALOGD("-->command is [%02x]\n", cmd);
	return send_frame(frame, 4);	
}

int open_uart(void)
{
	fd = open(DEV_ZIGBEE, O_RDWR);
	if (fd <= 0) return fd;

	int iret = nd_rs232init(fd, DEV_BAUT, 8, 'N', 1);
	nd_rs232clear(fd);

	return fd;
}
//=============================================================================
static jint write_cmd_to_robot(JNIEnv *env, jobject thiz, jbyte cmd)
{	
	jint ret = write_cmd(cmd);
	return ret;
}

static jint open_channel(JNIEnv *env, jobject thiz)
{
    fd = open_uart();
    return fd;
}

static jint close_robot_channel(JNIEnv *env, jobject thiz)
{
    if (fd > 0) {
        close(fd);
	}
    fd = 0;

    return 0;
}


/**
* 设置串口数据，校验位,速率，停止位
* @param nBits 类型 int数据位 取值 位7或8
* @param nEvent 类型 char 校验类型 取值N ,E, O,S
 * N清除校验位, E:even偶校验, O:odd 奇校验
* @param mSpeed 类型 int 速率 取值 2400,4800,9600,115200
* @param mStop 类型 int 停止位 取值1 或者 2
*/
jobject open_serial(JNIEnv *env, jobject thiz, jstring path,jint baudrate, jint nBits,jint nStop,jint verifyBit) {
	int fd;
	speed_t speed;
	jobject mFileDescriptor;
	jboolean iscopy;
	const char *path_utf = env->GetStringUTFChars(path, &iscopy);
	fd = open(path_utf, O_RDWR);
	if (fd <= 0) return NULL;

	char nevent = 'N';
	if(verifyBit == NO_VERIFY) {
		nevent = 'N';
	} else if(verifyBit == ODD_VERIFY) {
		nevent = 'O';
	} else if(verifyBit == EVENT_VERIFY) {
		nevent = 'E';
	}
	int iret = nd_rs232init(fd, baudrate, nBits, nevent, nStop);
	nd_rs232clear(fd);
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
/* the first step： interface define and load it to JNI */
/*
 * Array of methods.
 *
 * Each entry has three fields: the name of the method, the method
 * signature, and a pointer to the native implementation.
 */

static const JNINativeMethod gMethods[] = {
		{ "open", "(Ljava/lang/String;IIII)Ljava/io/FileDescriptor;",(void*) open_serial},
		{ "close", "()I",(void*) close_serial },
};

static int registerMethods(JNIEnv* env) {
    jclass clazz;

    /* look up the class */
    clazz = env->FindClass(kClassName);
    if (clazz == NULL) {
        ALOGD("Can't find class %s\n", kClassName);
        return -1;
    }

    /* register all the methods */
    if (env->RegisterNatives(clazz, gMethods,
            sizeof(gMethods) / sizeof(gMethods[0])) != JNI_OK)
    {
        ALOGD("Failed registering methods for %s\n", kClassName);
        return -1;
    }
	ALOGD("register success %s\n", kClassName);
    /* fill out the rest of the ID cache */
    return 0;
}

/*
 * This is called by the VM when the shared library is first loaded.
 */
jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;
    jint result = -1;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        ALOGD("ERROR: GetEnv failed\n");
        goto fail;
    }
    assert(env != NULL);

    if (registerMethods(env) != 0) {
        ALOGD("ERROR: PlatformLibrary native registration failed\n");
        goto fail;
    }

    /* success -- return valid version number */
    result = JNI_VERSION_1_4;

fail:
    return result;
}
