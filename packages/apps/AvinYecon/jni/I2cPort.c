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

#include "I2c.h"

#define TAG "i2c-port"
#include "Common.h"

/* This is the structure as used in the I2C_RDWR ioctl call */
struct i2c_rdwr_ioctl_data {
        struct i2c_msg __user *msgs;    /* pointers to i2c_msgs */
        __u32 nmsgs;                    /* number of i2c_msgs */
};

static int fd=-1;
JNIEXPORT jint JNICALL Java_android_exdevport_I2cPort_i2cOpen(JNIEnv *env, jobject thiz, jstring dev){
	jboolean iscopy;
	const char *path_utf = (*env)->GetStringUTFChars(env, dev, &iscopy);
	fd = open(path_utf, O_RDWR);
	ioctl(fd,I2C_TIMEOUT,2);//超时时间
	ioctl(fd,I2C_RETRIES,1);//重复次数
	return fd;
}

JNIEXPORT void  JNICALL Java_android_exdevport_I2cPort_i2cClose(JNIEnv *env, jobject thiz){
	if(fd>=0){
		close(fd);
		fd = -1;
	}
}
JNIEXPORT void JNICALL Java_android_exdevport_I2cPort_test(JNIEnv *env, jobject thiz){

}
JNIEXPORT jshort JNICALL Java_android_exdevport_I2cPort_i2cRead16(JNIEnv *env, jobject thiz, jint slave_address, jint reg_address, jint reg_bytes)
{
	int ret = -1;
	unsigned char r_buf[2];
	LOGI("i2cRead [%x %x %x]", slave_address, reg_address, reg_bytes);
	ret = i2cRead(fd, (unsigned char *)r_buf , 2, slave_address, reg_address, reg_bytes);
	LOGI("i2cRead ret: %d", ret);
	short value = (r_buf[0]<<8)&0xff00;
	value |= r_buf[1];
	return value;
}

JNIEXPORT int JNICALL Java_android_exdevport_I2cPort_i2cWrite16(JNIEnv *env, jobject thiz,  jshort data, jint slave_address, jint reg_address, jint reg_bytes)
{
	int ret = -1;
	ret = i2cWrite(fd, (unsigned char*)&data, sizeof(data) , slave_address, reg_address, reg_bytes);
	return ret;
}

/*
int main(int argc, char **argv)
{
    unsigned int fd;
    unsigned int slave_address, reg_address;
    unsigned r_w;
    unsigned w_val;
    unsigned char rw_val;

    if (argc < 5) {
        printf("Usage:\n%s /dev/i2c-x start_addr reg_addr rw[0|1] [write_val]\n", argv[0]);
        return 0;
    }

    fd = open(argv[1], O_RDWR);

    if (!fd) {
        printf("Error on opening the device file %s\n", argv[1]);
        return 0;
    }

    sscanf(argv[2], "%x", &slave_address);
    sscanf(argv[3], "%x", ®_address);
    sscanf(argv[4], "%d", &r_w);

    if (r_w == 0) {
        i2c_read_reg(argv[1], &rw_val, slave_address, reg_address, 1);
        printf("Read %s-%x reg %x, read value:%x\n", argv[1], slave_address, reg_address, rw_val);
    } else {
        if (argc < 6) {
            printf("Usage:\n%s /dev/i2c-x start_addr reg_addr r|w[0|1] [write_val]\n", argv[0]);
            return 0;
        }
        sscanf(argv[5], "%d", &w_val);
        if ((w_val & ~0xff) != 0)
            printf("Error on written value %s\n", argv[5]);

        rw_val = (unsigned char)w_val;
        i2c_write_reg(argv[1], &rw_val, slave_address, reg_address, 1);
    }

    return 0;
}  */
