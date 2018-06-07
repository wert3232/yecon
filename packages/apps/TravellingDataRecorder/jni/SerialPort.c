#include <termios.h> 
#include <unistd.h> 
#include <sys/types.h> 
#include <sys/stat.h> 
#include <fcntl.h> 
#include <string.h> 
#include <jni.h>
#include <stdio.h>
#include "android/log.h"

#define TAG "serial-port"
#include "Common.h"

static int fd = 0;

static speed_t getBaudrate(jint baudrate) 
{ 
	switch(baudrate) {
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


/* * Class: cedric_serial_SerialPort * 
Method: open * Signature: (Ljava/lang/String;)V */
JNIEXPORT jobject JNICALL Java_android_exdevport_SerialPort_open (JNIEnv *env, jobject thiz, jstring path, jint baudrate)
{
  speed_t speed;
  jobject mFileDescriptor; /* Check arguments */ 
  {
  	speed = getBaudrate(baudrate); 
	if (speed == -1) 
		{ /* TODO: throw an exception */
			// LOGE("Invalid baudrate");
	      return NULL; 
		} 
 }

 /* Opening device */ 
 { 
 	jboolean iscopy; 
	const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy); 
	LOGD("Opening serial port %s", path_utf);
	fd = open(path_utf, O_RDWR );//| O_SYNC);
	 LOGD("open() fd = %d", fd);
	printf("fd = %d\r\n",fd);
	(*env)->ReleaseStringUTFChars(env, path, path_utf); 
	if (fd == -1) 
	{ /* Throw an exception */ 
	  //LOGE("Cannot open port");
	  /* TODO: throw an exception */ 
	  return NULL;
	 }
 }

 /* Configure device */ 
 { 
 	struct termios cfg;
 	 LOGD("Configuring serial port");
	if (tcgetattr(fd, &cfg))
		{ 
			//LOGE("tcgetattr() failed");

			close(fd); /* TODO: throw an exception */ 
			return NULL; 
		}
	
	cfmakeraw(&cfg);

	//bzero(&cfg,byte);

	cfsetispeed(&cfg, speed); 
	cfsetospeed(&cfg, speed); 


	//c_cflag锟斤拷志锟斤拷锟皆讹拷锟斤拷CLOCAL锟斤拷CREAD锟斤拷锟解将确锟斤拷锟矫筹拷锟津不憋拷锟斤拷锟斤拷丝诳锟斤拷坪锟斤拷藕鸥锟斤拷牛锟酵憋拷锟斤拷锟斤拷锟斤拷取锟斤拷锟斤拷锟斤拷锟捷★拷CLOCAL锟斤拷CREAD通锟斤拷锟斤拷锟角憋拷锟斤拷锟杰碉拷
	cfg.c_cflag |=CLOCAL|CREAD;
	//锟斤拷锟轿�
	cfg.c_cflag &=~CSIZE;
	cfg.c_cflag |=CS8;
	//锟斤拷锟斤拷偶位校锟斤拷
	//cfg.c_cflag &=~PARENB;
	cfg.c_cflag |= PARENB;
	cfg.c_cflag &= ~PARODD;
	cfg.c_iflag |= (INPCK);// | ISTRIP);
	//cfg.c_cflag |= PARENB;
	//cfg.c_cflag |= PARODD;
	//cfg.c_iflag |= (INPCK | ISTRIP);
	//停止位1位
	cfg.c_cflag &= ~CSTOPB;
	//停止位2位
	//cfg.c_cflag |= CSTOPB;

	//newtio.c_cflag|=PARENB;//
	//newtio.c_cflag&=~PARODD;//偶校锟斤拷位
	//newtio.c_iflag|=(INPCK|ISTRIP);

	//newtio.c_cflag |=PARENB;//enable parity checking
	//newtio.c_cflag |=PARODD;//锟斤拷校锟斤拷位
	//newtio.c_iflag |=(INPCK|ISTRIP);


	if (tcsetattr(fd, TCSANOW, &cfg)) 
		{ 
		LOGE("tcsetattr() failed");
		close(fd); /* TODO: throw an exception */
		return NULL; 
		}

 	}

 /* Create a corresponding file descriptor */ 
  {
  	jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
	jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor, "<init>", "()V");
	jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor, "descriptor", "I");
	mFileDescriptor = (*env)->NewObject(env, cFileDescriptor, iFileDescriptor); 
	(*env)->SetIntField(env, mFileDescriptor, descriptorID, (jint)fd); 
  } 
 
 return mFileDescriptor;
 
 }


 /* * Class: cedric_serial_SerialPort
      * Method: close 
      * Signature: ()V */

 JNIEXPORT void JNICALL Java_android_exdevport_SerialPort_close (JNIEnv *env, jobject thiz)
 {
 	jclass SerialPortClass = (*env)->GetObjectClass(env, thiz); 
	jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");
	jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
	jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");
	jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
	jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);
	//LOGD("close(fd = %d)", descriptor);
	close(descriptor); 
}

 JNIEXPORT void JNICALL Java_android_exdevport_SerialPort_writeTouchDown (JNIEnv *env, jobject thiz, jint screenWidth,jint screenHeight,jint dvrWidth,jint dvrHeight,jint xPos,jint yPos)
 {
	 if(fd == 0)
		 return;


	 unsigned char data[7]={0xfe,0x04,0xa1,0,0,0,0};
	 unsigned char checkSum = 0;
	 /*
#if(1)
	 	jint* pba = (*env)->GetIntArrayElements(env,dataArray,NULL);
	 	if(pba == NULL)
	 	{
	 		LOGD("pba is null\r\n");
	 		return;
	 	}


#endif

#if(1)
	 	int len = (*env)->GetArrayLength(env,pba);
	 	if(len > 4)
	 		return;
#endif*/

	 /*	int k = 0;
	 	for(k = 0; k < 4 ;k++)
	 	{
	 		checkSum += data[k];
	 	}

	 	checkSum = ~checkSum + 1;
	 	data[4]=checkSum;

	 	LOGD("checkSum %d",checkSum);
	 	write(fd,data,sizeof(data) / sizeof(unsigned char));*/

	 	//(*env)->ReleaseIntArrayElements(env,dataArray,pba,0);


		unsigned short posValueX = xPos * dvrWidth / screenWidth;
		unsigned short posValueY = yPos * dvrHeight / screenHeight;

		LOGD("touchdown posX = %d,posY = %d",posValueX,posValueY);

		data[3] = posValueX & 0xFF;
		data[4] = (((posValueX>>8) & 0x0F)<<4) | ((posValueY>>8) & 0x0F);
		data[5] = posValueY & 0xFF;
		int j =0;
		for (j = 0; j < 6;j++)
		{
			checkSum += data[j];
		}
		checkSum = ~checkSum + 1;
		data[6] = checkSum;

		LOGD("checkSum %d",checkSum);
		write(fd,data,sizeof(data) / sizeof(unsigned char));

 }


 JNIEXPORT void JNICALL Java_android_exdevport_SerialPort_writeTouchUp (JNIEnv *env, jobject thiz)
 {
	 if(fd == 0)
		 return;
	 struct termios cfg;
	 if (tcgetattr(fd, &cfg))
	 		{
	 			//LOGE("tcgetattr() failed");

	 			close(fd); /* TODO: throw an exception */
	 			return NULL;
	 		}
	 LOGD("ispeed = %d, ospeed = %d",cfgetispeed(&cfg),cfgetospeed(&cfg));
	 LOGD("c_cflag = 0x%X",cfg.c_cflag);
	 LOGD("c_iflag = 0x%X",cfg.c_iflag);

/*
	 unsigned char data[4]={0xfe,0x01,0xa2,0};
	 unsigned char checkSum = 0;

	 int j =0;
	 for (j = 0; j < 3;j++)
	{
		checkSum += data[j];
	}

	 checkSum = ~checkSum + 1;
	 data[3] = checkSum;
	 LOGD("checkSum touch up = %d",checkSum);
	 write(fd,data,sizeof(data) / sizeof(unsigned char));*/
 }

 JNIEXPORT void JNICALL Java_android_exdevport_SerialPort_writeTime (JNIEnv *env, jobject thiz,jint hour,jint minute,jint timeFormat)
 {
	 if(fd == 0)
		 return;

	 unsigned char data[7]={0xfe,0x04,0xa4,hour,minute,timeFormat,0};
	 unsigned char checkSum = 0;

	 int j =0;
	 for (j = 0; j < 6;j++)
	{
		checkSum += data[j];
	}

	 checkSum = ~checkSum + 1;
	 data[6] = checkSum;
	 LOGD("checkSum time = %d , %d,%d",checkSum,hour,minute);
	 write(fd,data,sizeof(data) / sizeof(unsigned char));
 }

 JNIEXPORT void JNICALL Java_android_exdevport_SerialPort_writeYear (JNIEnv *env, jobject thiz,jint year,jint month,jint day,jint weekDay)
 {
	 if(fd == 0)
		 return;

	 year -= 2000;
	 unsigned char data[8]={0xfe,0x05,0xa5,year,month,day,weekDay,0};
	 unsigned char checkSum = 0;

	 int j =0;
	 for (j = 0; j < 7;j++)
	{
		checkSum += data[j];
	}

	 checkSum = ~checkSum + 1;
	 data[7] = checkSum;
	 LOGD("checkSum year = %d ,%d,%d,%d,%d",checkSum,year,month,day,weekDay);
	 write(fd,data,sizeof(data) / sizeof(unsigned char));
 }
