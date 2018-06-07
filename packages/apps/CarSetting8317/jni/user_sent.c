#include <sys/stat.h>     
#include <unistd.h>     
#include <stdio.h>     
#include <stdlib.h>     
#include <sys/socket.h>     
#include <sys/types.h>     
#include <string.h>     
#include <asm/types.h>     
#include <linux/netlink.h>     
#include <linux/socket.h>     
#include <stddef.h>  
#include <errno.h>     
#include <jni.h>
#include <android/log.h>
#include <termios.h>
#include <fcntl.h>

#define   UINT16   unsigned short
#define   UINT8    unsigned char
#define   TOUCH_FILE_PATH   "/usr2/touchKey.dat"
#define   TOUCH_CALIBRATION_PATH   "/usr2/touchCalibration.dat"
#define   TOUCH_KEY_SIZE 16

typedef struct
{
	UINT8  u8KeyType;//鎸夐敭鏂瑰紡鎴栧尯鍩熸柟寮�
	UINT16 u16XSPoint;
	UINT16 u16YSPoint;
	UINT16 u16XEPoint;
	UINT16 u16YEPoint;
	UINT16 u16ShortKeyFunID;
	UINT16 u16LongKeyFunID;
} CPSigalKey,*pCPSigalKey;

typedef struct
{
	UINT8 u8HeaderCode[4];
	UINT8 u8ZoneNumber;
	UINT8 u8KeyNum;
	UINT8 u8KeyXShift;
	UINT8 u8KeyYShift;
	CPSigalKey bKeyP[TOUCH_KEY_SIZE];
} CPTouchKey,*pCPTouchKey;

typedef struct _TouchScreenZone
{
	UINT8 u8HeaderCode[4];
	UINT8 u8ZoneNumber;
	UINT8 u8ChipType;					//
	UINT8 u8SendTpCfg;					//
	UINT8 u8CfgId;						//
	UINT8 u8SwapXY;						//
	UINT8 u8MirrorXY;					//
	UINT8 u8IntPinGpio;					//
	UINT8 u8RstPinGpio;					//Reset GPIO
	UINT8 u8PwrPinGpio;					// Power GPIO
	UINT8 u8ShowTouchLocation;			//
	UINT8 u8MirrorX;					// X
	UINT8 u8MirrorY;					// Y
	UINT8 u8ZoomX;						// X
	UINT8 u8ZoomY;						// Y
	UINT8 u8Reserved[3];				//
	char chBccChecksum;					//
} TouchScreenZone,*pTouchScreenZone;

CPTouchKey gTocuchKey;
int        gCurKeyIndex=0;

#define  LOG_TAG    "WriteDataLib"
#define  LOGI(...)    __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)    __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
  
/*
#define FREE_INIT(ptr) do{ \  
    free(ptr); \  
    ptr = NULL; \  
}while(0)  */

#define NETLINK_TEST 17  
enum{  
    NLMSG_TYPE_START = 0,	/*寮�鎺ユ敹鏁版嵁 */
    NLMSG_TYPE_MSG,			/*鎺ユ敹甯,y鐨勬秷鎭�*/
    NLMSG_TYPE_FINISH,		/* 缁撴潫鎺ユ敹鏁版嵁 */
};  
struct nlmsg{  
    int type;	/* 娑堟伅绫诲瀷 */
	int len;	/* 娑堟伅闀垮害*/
    int x;		/* 娑堟伅X杞村潗鏍�*/
    int y;		/* 娑堟伅Y杞村潗鏍�*/
};  

int g_socketID = -1;
jint xPos = 0;
jint yPos = 0;
TouchScreenZone g_touchScreenZone;


int netlink_open(void)  
{  
	  struct sockaddr_nl saddr;
	  int sockfd = -1, ret = 0;

	  printf("start socket.\n");
	  sockfd = socket(PF_NETLINK, SOCK_RAW, NETLINK_TEST);
	  if (sockfd < -1) {
		  printf("create socket failed!\n");
		LOGI("WriteDataLib:create socket failed %d \n", 1);
		return -1;
	  }

	  memset(&saddr, 0, sizeof(saddr));
	  saddr.nl_family = PF_NETLINK;
	  saddr.nl_pad = 0;
	  saddr.nl_pid = getpid(); // self pid
	  saddr.nl_groups = 0; // multi cast

	  printf("start bind.\n");
	  ret = bind(sockfd, (struct sockaddr*) &saddr, sizeof(saddr));
	  if (ret < 0) {
		  printf("bind failed!\n");
		  LOGI("WriteDataLib:bind failed %d \n", 1);
		  close(sockfd);
		  return -1;
	  }

	 int flags = fcntl(sockfd, F_GETFL, 0);
	   fcntl(sockfd, F_SETFL, flags|O_NONBLOCK);
	 // struct timeval timeout = {1,0};
	 // setsockopt(sockfd,SOL_SOCKET,SO_RCVTIMEO,(char*)&timeout,sizeof(timeout));
	  return sockfd;

}  

int netlink_send(struct nlmsg *pmsg)
{    
    struct msghdr msg;
    struct iovec iov;
	struct sockaddr_nl daddr;
    struct nlmsghdr *nlh = NULL;
      
    int msglen = pmsg->len;  
    int totlen = NLMSG_SPACE(pmsg->len);  
    int ret = 0;

	printf("msglen=%d.\n",msglen);
	printf("totlen=%d.\n",totlen);
	memset(&daddr,0,sizeof(daddr));
	daddr.nl_family = AF_NETLINK;
	daddr.nl_pad = 0;
	daddr.nl_pid = 0;
	daddr.nl_groups = 0;

    nlh = malloc(totlen);  
    if(!nlh)  
    {  
        printf("malloc failed!\n");
        return -1;  
    }
    nlh->nlmsg_len = totlen;
    nlh->nlmsg_flags = NLM_F_REQUEST;
    nlh->nlmsg_pid = getpid();  

    iov.iov_base = (void *)nlh;
    iov.iov_len = nlh->nlmsg_len;

    memset(&msg, 0, sizeof(msg));
	msg.msg_name = (void *)&daddr;
	msg.msg_namelen = sizeof(daddr);
    msg.msg_iov = &iov;
    msg.msg_iovlen = 1;

    memcpy(NLMSG_DATA(nlh), pmsg, msglen);

	printf("sendmsg.\n");
    ret = sendmsg(g_socketID, &msg, 0);
    free(nlh);
    if(ret < 0)  
    {
        printf("sendmsg failed!\n");  
       // FREE_INIT(nlh);
        nlh = 0;
        return -1;  
    }
	printf("sendmsg done.\n");
    return 0;  
}  

int netlink_recv(struct nlmsg *pmsg)
{  
    struct msghdr msg;  
    struct iovec iov;  
    struct nlmsghdr *nlh = NULL;  
      
    int msglen = sizeof(*pmsg);  
    int totlen = NLMSG_SPACE(sizeof(*pmsg));  
    int ret = 0;  
      
    nlh = malloc(totlen);  
    if(!nlh)  
    {  
        printf("malloc failed!\n");  
        nlh = 0;
        return -1;  
    }  
      
    iov.iov_base = (void *)nlh;    
    iov.iov_len = totlen;    
    
    memset(&msg, 0, sizeof(msg));    
    msg.msg_iov = &iov;    
    msg.msg_iovlen = 1;   

	printf("recvmsg.\n");
    memcpy(NLMSG_DATA(nlh), pmsg, msglen);  
    ret = recvmsg(g_socketID, &msg, 0);
    if(ret < 0)  
    {
        printf("recvmsg failed!\n");  
        free(nlh);
        nlh = 0;
        return -1;  
    }

    memcpy(pmsg, NLMSG_DATA(nlh), msglen);  
    free(nlh);
    nlh = 0 ;

    return 0;  
}  

void netlink_close()
{  
    if(g_socketID > 0)
        close(g_socketID);

    LOGI("WriteDataLib: netlink closed %d\n",g_socketID);
}  

JNIEXPORT int JNICALL Java_com_yecon_carsetting_KeyLearnMain_getXPosNetLink(JNIEnv *env,jobject obj){

	return xPos;

}

JNIEXPORT int JNICALL Java_com_yecon_carsetting_KeyLearnMain_getYPosNetLink(JNIEnv *env,jobject obj){

	return yPos;
}

JNIEXPORT void JNICALL Java_com_yecon_carsetting_KeyLearnMain_closeNetLink(JNIEnv *env,jobject obj){

	netlink_close();

}

JNIEXPORT jint JNICALL Java_com_yecon_carsetting_KeyLearnMain_recvNetLink(JNIEnv *env,jobject obj){

	struct nlmsg msg;
	memset(&msg, 0, sizeof(msg));
	msg.type = NLMSG_TYPE_START;
	msg.len = sizeof(struct nlmsg);
	msg.x = 0;
	msg.y = 0;

   int ret = netlink_recv(&msg);
   if(ret < 0){
	   LOGI("WriteDataLib: netlink recv failed %d\n",g_socketID);
       return -1;
   }

   LOGI("WriteDataLib:netlink_recv %d,%d,\n", msg.x,msg.y);
   xPos = msg.x; yPos = msg.y;

	return 0;

}

JNIEXPORT jint JNICALL Java_com_yecon_carsetting_KeyLearnMain_sendStsNetLink(JNIEnv *env,jobject obj,jboolean sts){

    struct nlmsg msg;
    memset(&msg, 0, sizeof(msg));

    if(g_socketID < 0)
    	return -1;

    if(sts){
    	msg.type = NLMSG_TYPE_START;
    }else{
    	msg.type = NLMSG_TYPE_FINISH;
    }

    msg.len = sizeof(struct nlmsg);
  	msg.x = 0;
  	msg.y = 0;

    int ret = netlink_send(&msg);
    if(ret < 0)
    {
        printf("netlink_send failed!\n");
        LOGI("WriteDataLib:set netlink sts failed %d\n",g_socketID);
        return -1;
    }



    return 0;

}
  
//int main(int argc, char* argv[])
JNIEXPORT jint JNICALL Java_com_yecon_carsetting_KeyLearnMain_createNetLink(JNIEnv *env,jobject obj)
{    
    g_socketID = netlink_open();
    if(g_socketID < 0)
    {  
    	LOGI("WriteDataLib:create netlink socket failed %d\n",g_socketID);
        return -1;  
    }

    return 0;

}


JNIEXPORT void JNICALL Java_com_yecon_carsetting_KeyLearnMain_fillKeyElement(JNIEnv *env,jobject thiz,jint xSPoint,
		jint ySPoint,jint xEPoint,jint yEPoint,jint shortKeyCode,jint longKeyCode,jint keyType){

	if(gCurKeyIndex >= 16 || gCurKeyIndex >=gTocuchKey.u8KeyNum)
	{
		return;
	}

	gTocuchKey.bKeyP[gCurKeyIndex].u8KeyType = keyType;
	gTocuchKey.bKeyP[gCurKeyIndex].u16XSPoint = xSPoint;
	gTocuchKey.bKeyP[gCurKeyIndex].u16YSPoint = ySPoint;
	gTocuchKey.bKeyP[gCurKeyIndex].u16XEPoint = xEPoint;
	gTocuchKey.bKeyP[gCurKeyIndex].u16YEPoint = yEPoint;
	gTocuchKey.bKeyP[gCurKeyIndex].u16ShortKeyFunID = shortKeyCode;
	gTocuchKey.bKeyP[gCurKeyIndex].u16LongKeyFunID = longKeyCode;

	LOGI("WriteDataLib:keyType=%d,xSPoint=%d,ySPoint=%d,xEPoint=%d,yEPoint=%d,shortKeyCode=%d,longKeyCode=%d\n",
			keyType,xSPoint,ySPoint,xEPoint,yEPoint,shortKeyCode,longKeyCode);

	gCurKeyIndex++;

}

JNIEXPORT void JNICALL Java_com_yecon_carsetting_KeyLearnMain_initData(JNIEnv *env,jobject thiz,
		jint keyCount,jint xShift,jint yShift){

	int j = 0;
	for(j = 0; j< TOUCH_KEY_SIZE;j++){

		gTocuchKey.bKeyP[j].u8KeyType = 0;
		gTocuchKey.bKeyP[j].u16XSPoint = 0;
		gTocuchKey.bKeyP[j].u16YSPoint = 0;
		gTocuchKey.bKeyP[j].u16XEPoint = 0;
		gTocuchKey.bKeyP[j].u16YEPoint = 0;
		gTocuchKey.bKeyP[j].u16ShortKeyFunID = 0;
		gTocuchKey.bKeyP[j].u16LongKeyFunID = 0;

	}

	gCurKeyIndex = 0;

	gTocuchKey.u8KeyNum = keyCount;
	gTocuchKey.u8KeyXShift = xShift;
	gTocuchKey.u8KeyYShift = yShift;

	LOGI("WriteDataLib:keyCount=%d,xShift=%d,yShift=%d\n",keyCount,xShift,yShift);

}

JNIEXPORT void JNICALL Java_com_yecon_carsetting_KeyLearnMain_writeTouchKeyData(JNIEnv *env,jobject thiz){

	int   fd = 0;
	fd = open(TOUCH_FILE_PATH,O_TRUNC | O_CREAT | O_RDWR,00700);
	if(fd == -1){
		LOGI("WriteDataLib:create file fail");
		return;
	}

	gCurKeyIndex = 0;
	write(fd,&gTocuchKey,sizeof(CPTouchKey));
	close(fd);

}

JNIEXPORT void JNICALL Java_com_yecon_carsetting_KeyLearnMain_readTocuhKeyData(JNIEnv *env,jobject thiz){
	int   fd = 0;
	fd = open(TOUCH_FILE_PATH,O_RDWR,00700);
	if(fd == -1){
		LOGI("WriteDataLib:create file fail");
		return;
	}

	gCurKeyIndex = 0;
	read(fd,&gTocuchKey,sizeof(CPTouchKey));

	LOGI("read:keyType=%d,xSPoint=%d,ySPoint=%d,xEPoint=%d,yEPoint=%d,shortKeyCode=%d,longKeyCode=%d\n",
			gTocuchKey.bKeyP[0].u8KeyType,gTocuchKey.bKeyP[0].u16XSPoint,gTocuchKey.bKeyP[0].u16YSPoint,gTocuchKey.bKeyP[0].u16XEPoint,
			gTocuchKey.bKeyP[0].u16YEPoint,gTocuchKey.bKeyP[0].u16ShortKeyFunID,gTocuchKey.bKeyP[0].u16LongKeyFunID);

	LOGI("read:keyCount=%d,xShift=%d,yShift=%d\n",gTocuchKey.u8KeyNum,gTocuchKey.u8KeyXShift,gTocuchKey.u8KeyYShift);

	close(fd);

}


JNIEXPORT jint JNICALL Java_com_yecon_carsetting_KeyLearnMain_readCalibrationData(JNIEnv *env,jobject thiz){

	int   fd = 0;
	fd = open(TOUCH_CALIBRATION_PATH,O_RDWR,00700);
	if(fd == -1){
		LOGI("WriteDataLib:create file fail");
		return - 1;
	}


	memset(&g_touchScreenZone,sizeof(TouchScreenZone),0);
	int ret = read(fd,&g_touchScreenZone,sizeof(TouchScreenZone));
	if(ret <= 0)
		return -1;

	LOGI("read:SwapXY=%d,MirrorXY=%d,MirrorX=%d,MirrorY=%d\n",g_touchScreenZone.u8SwapXY,g_touchScreenZone.u8MirrorXY,
			g_touchScreenZone.u8MirrorX,g_touchScreenZone.u8MirrorY);

	close(fd);

	return 0;

}

JNIEXPORT jint JNICALL Java_com_yecon_carsetting_KeyLearnMain_getCalibrationSwapXY(JNIEnv *env,jobject thiz){

	return g_touchScreenZone.u8SwapXY;
}

JNIEXPORT jint JNICALL Java_com_yecon_carsetting_KeyLearnMain_getCalibrationMirrorX(JNIEnv *env,jobject thiz){

	return g_touchScreenZone.u8MirrorX;
}

JNIEXPORT jint JNICALL Java_com_yecon_carsetting_KeyLearnMain_getCalibrationMirrorY(JNIEnv *env,jobject thiz){

	return g_touchScreenZone.u8MirrorY;
}


JNIEXPORT void JNICALL Java_com_yecon_carsetting_TouchCalibrationMainActivity_writeCalibrationData(JNIEnv *env,jobject thiz,jint u8SwapXY,jint u8MirrorXY,
		jint u8MirrorX,jint u8MirrorY,jint u8XZoom,jint u8YZoom){

	int   fd = 0;
	fd = open(TOUCH_CALIBRATION_PATH,O_TRUNC | O_CREAT | O_RDWR,00700);
	if(fd == -1){
		LOGI("WriteDataLib:create file fail");
		return;
	}

	TouchScreenZone  touchScreenZone;
	memset(&touchScreenZone,sizeof(touchScreenZone),0);
	touchScreenZone.u8SwapXY = u8SwapXY;
	touchScreenZone.u8MirrorXY = u8MirrorXY;
	touchScreenZone.u8MirrorX = u8MirrorX;
	touchScreenZone.u8MirrorY = u8MirrorY;
	touchScreenZone.u8ZoomX = u8XZoom;
	touchScreenZone.u8ZoomY = u8YZoom;
	write(fd,&touchScreenZone,sizeof(touchScreenZone));
	close(fd);

}


JNIEXPORT jint JNICALL Java_com_yecon_carsetting_TouchCalibrationMainActivity_sendStsNetLinkCalibration(JNIEnv *env,jobject obj,jboolean sts){

    struct nlmsg msg;
    struct nlmsg *pmsg;
    memset(&msg, 0, sizeof(msg));

    if(g_socketID < 0)
    	return -1;

    if(sts){
    	msg.type = NLMSG_TYPE_MSG;
    }else{
    	msg.type = NLMSG_TYPE_FINISH;
    }


    msg.len = sizeof(struct nlmsg);
  	msg.x = 0;
  	msg.y = 0;

    struct msghdr msgs;
    struct iovec iov;
	struct sockaddr_nl daddr;
    struct nlmsghdr *nlh = NULL;

    pmsg = &msg;

    int msglen = pmsg->len;
    int totlen = NLMSG_SPACE(pmsg->len);
    int ret = 0;

	printf("msglen=%d.\n",msglen);
	printf("totlen=%d.\n",totlen);
	memset(&daddr,0,sizeof(daddr));
	daddr.nl_family = AF_NETLINK;
	daddr.nl_pad = 0;
	daddr.nl_pid = 0;
	daddr.nl_groups = 0;

    nlh = malloc(totlen);
    if(!nlh)
    {
        printf("malloc failed!\n");
        return -1;
    }
    nlh->nlmsg_len = totlen;
    nlh->nlmsg_flags = NLM_F_REQUEST;
    nlh->nlmsg_pid = getpid();

    iov.iov_base = (void *)nlh;
    iov.iov_len = nlh->nlmsg_len;

    memset(&msgs, 0, sizeof(msgs));
    msgs.msg_name = (void *)&daddr;
    msgs.msg_namelen = sizeof(daddr);
    msgs.msg_iov = &iov;
    msgs.msg_iovlen = 1;

    memcpy(NLMSG_DATA(nlh), pmsg, msglen);

	printf("sendmsg.\n");
    ret = sendmsg(g_socketID, &msgs, 0);
    free(nlh);
    if(ret < 0)
    {
        printf("sendmsg failed!\n");
       // FREE_INIT(nlh);
        nlh = 0;
        return -1;
    }
	printf("sendmsg done.\n");

	/*
    int ret = netlink_send(&msg);
    if(ret < 0)
    {
        printf("netlink_send failed!\n");
        LOGI("WriteDataLib:TouchCalibrationMainActivity set netlink sts failed %d\n",g_socketID);
        return -1;
    }*/

    return 0;

}


JNIEXPORT jint JNICALL Java_com_yecon_carsetting_TouchCalibrationMainActivity_createNetLinkCalibration(JNIEnv *env,jobject obj)
{
	  struct sockaddr_nl saddr;
	  int sockfd = -1, ret = 0;

	  printf("start socket.\n");
	  g_socketID = socket(PF_NETLINK, SOCK_RAW, NETLINK_TEST);
	  if (g_socketID < -1) {
		  printf("create socket failed!\n");
		  LOGI("WriteDataLib:create socket failed %d \n", 1);
		  return -1;
	  }

	  memset(&saddr, 0, sizeof(saddr));
	  saddr.nl_family = PF_NETLINK;
	  saddr.nl_pad = 0;
	  saddr.nl_pid = getpid(); // self pid
	  saddr.nl_groups = 0; // multi cast

	  printf("start bind.\n");
	  ret = bind(g_socketID, (struct sockaddr*) &saddr, sizeof(saddr));
	  if (ret < 0) {
		  printf("bind failed!\n");
		  LOGI("WriteDataLib:bind failed %d \n", 1);
		  close(g_socketID);
		  return -1;
	  }


    return 0;

}

