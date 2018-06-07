#include <stdio.h>
#include <linux/types.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <assert.h>
#include <string.h>
#include <linux/i2c.h>

#define TAG "i2c-port"
#include "Common.h"

#include "atv/WydMidSimulationTv.h"

extern "C"{

static AtvAndArmProtocol *atvAndArmProtocol;

JNIEXPORT void Java_android_exdevport_AtvPort_test(JNIEnv *env, jobject thiz){

}
JNIEXPORT int Java_android_exdevport_AtvPort_init(JNIEnv *env, jobject thiz, int tvRegion, jintArray configData){
	if(NULL==atvAndArmProtocol){
		atvAndArmProtocol = new AtvAndArmProtocol();
		atvAndArmProtocol->InitI2C();
		if(configData!=NULL){
			jsize len  = env->GetArrayLength(configData);
			jint *jintarray = (jint *)malloc(len * sizeof(jint));
			env->GetIntArrayRegion(configData,0,len,jintarray);
			atvAndArmProtocol->AtvProtocalInit(tvRegion, (unsigned int *)jintarray, len);
			free(jintarray);
		}
		else{
			atvAndArmProtocol->AtvProtocalInit(tvRegion, NULL, 0);
		}
	}
	return 0;
}
JNIEXPORT void Java_android_exdevport_AtvPort_exit(JNIEnv *env, jobject thiz){
	if(NULL!=atvAndArmProtocol){
		atvAndArmProtocol->AtvProtocalDeinit();
		//atvAndArmProtocol->CloseI2C();
		delete atvAndArmProtocol;
		atvAndArmProtocol=NULL;
	}
}
JNIEXPORT jintArray Java_android_exdevport_AtvPort_getConfig(JNIEnv *env, jobject thiz){
	if(NULL!=atvAndArmProtocol){
		jintArray  jintarray = env->NewIntArray(sizeof(SHARE_MEMORY_FileInfo_T)/4);
		jint *config = env->GetIntArrayElements(jintarray,0);
		atvAndArmProtocol->SetATVStaionInfoToShareMemory();
		atvAndArmProtocol->config2Array((unsigned int *)config, sizeof(SHARE_MEMORY_FileInfo_T)/4);
		env->SetIntArrayRegion(jintarray, 0, sizeof(SHARE_MEMORY_FileInfo_T)/4, config);
		env->ReleaseIntArrayElements(jintarray, config, 0);
		return jintarray;
	}
	return NULL;
}
JNIEXPORT jint Java_android_exdevport_AtvPort_sendUserMsg(JNIEnv *env, jobject thiz, int cmd, int keyValue){
	if(NULL!=atvAndArmProtocol){
		return atvAndArmProtocol->AtvSendMidCmd(cmd, keyValue);
	}
	return -1;
}

JNIEXPORT jint Java_android_exdevport_AtvPort_timerSchedule(JNIEnv *env, jobject thiz, int par0, int par1){
	if(NULL!=atvAndArmProtocol){
		return SimSrch(atvAndArmProtocol);
	}
	return -1;
}

JNIEXPORT jintArray Java_android_exdevport_AtvPort_getStatus(JNIEnv *env, jobject thiz){
	if(NULL!=atvAndArmProtocol){
		int len = atvAndArmProtocol->AtvGetMidStatusLen();
		//unsigned int *status = (unsigned int *)malloc(len * sizeof(int));
		jintArray  jintarray = env->NewIntArray(len);
		jint *jstatus = env->GetIntArrayElements(jintarray,0);
		atvAndArmProtocol->AtvGetMidStatus((unsigned int *)jstatus);
		env->SetIntArrayRegion(jintarray, 0, len, jstatus);
		env->ReleaseIntArrayElements(jintarray, jstatus, 0);
		return jintarray;
	}
	return NULL;
}

static BOOL avinSignalReady = 0;
BOOL isAvinSignalReady(){
	return avinSignalReady;
}
JNIEXPORT void Java_android_exdevport_AtvPort_notifyAvinSignalStatus(JNIEnv *env, jobject thiz, jint ready){
	avinSignalReady = ready;
}
}
