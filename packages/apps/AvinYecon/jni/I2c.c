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

/* This is the structure as used in the I2C_RDWR ioctl call */
struct i2c_rdwr_ioctl_data {
        struct i2c_msg __user *msgs;    /* pointers to i2c_msgs */
        __u32 nmsgs;                    /* number of i2c_msgs */
};

int i2cOpen(const char *devPath, int slaveAddr){
	int fd =  open(devPath, O_RDWR);
	/*
	int ret = ioctl(fd,I2C_SLAVE_FORCE, slaveAddr);
	LOGI("I2C_SLAVE_FORCE Ioctl result %d.\n", ret);

	ret = ioctl(fd,I2C_RETRIES, 1);
	LOGI("I2C_RETRIES Ioctl result %d.\n", ret);

	ret = ioctl(fd,I2C_TIMEOUT, 2);
	LOGI("I2C_TIMEOUT Ioctl result %d.\n", ret);
	*/
	return fd;
}
void i2cClose(int fd){
	if(fd>=0)
		close(fd);
}

int i2cRead(int fd, unsigned char *buf, int len, unsigned slave_address, unsigned reg_address, int reg_bytes)
{
    struct i2c_rdwr_ioctl_data work_queue;
    struct i2c_msg messages[2];
    unsigned char w_buf[4];
    //unsigned short w_val = reg_address;
    int ret;
    if (fd<0) {
    	LOGW("i2c is not opened\n");
		return 0;
	}

    switch(reg_bytes){
    case 1:
    	w_buf[0] = reg_address&0xff;
    	break;
    case 2:
		w_buf[0] = ((reg_address>>8)&0xff);
		w_buf[1] = (reg_address&0xff);
		break;
    default:
    	LOGW("Unsupport reg bytes.\n");
    	return 0;
    }

    work_queue.nmsgs = 2;
    work_queue.msgs = messages;

    (work_queue.msgs[0]).len = reg_bytes;
    (work_queue.msgs[0]).flags = 0;
    (work_queue.msgs[0]).addr = slave_address;
    (work_queue.msgs[0]).buf  = w_buf;

    (work_queue.msgs[1]).len = len;
    (work_queue.msgs[1]).flags = I2C_M_RD;
    (work_queue.msgs[1]).addr = slave_address;
    (work_queue.msgs[1]).buf = buf;

    ret = ioctl(fd, I2C_RDWR, (unsigned long) &work_queue);
    //write(fd, w_buf, reg_bytes);
    //ret = read(fd, buf, len);
    return ret;
}
int i2cReadEx(int fd, unsigned char *buf, int len, unsigned wslave_address, unsigned rslave_address, unsigned reg_address, int reg_bytes)
{
    struct i2c_rdwr_ioctl_data work_queue;
    struct i2c_msg messages[2];
    unsigned char w_buf[4];
    //unsigned short w_val = reg_address;
    int ret;
    if (fd<0) {
    	LOGW("i2c is not opened\n");
		return 0;
	}

    switch(reg_bytes){
    case 1:
    	w_buf[0] = reg_address&0xff;
    	break;
    case 2:
		w_buf[0] = ((reg_address>>8)&0xff);
		w_buf[1] = (reg_address&0xff);
		break;
    default:
    	LOGW("Unsupport reg bytes.\n");
    	return 0;
    }

    work_queue.nmsgs = 2;
    work_queue.msgs = messages;

    (work_queue.msgs[0]).len = reg_bytes;
    (work_queue.msgs[0]).flags = 0;
    (work_queue.msgs[0]).addr = wslave_address;
    (work_queue.msgs[0]).buf  = w_buf;

    (work_queue.msgs[1]).len = len;
    (work_queue.msgs[1]).flags = I2C_M_RD;
    (work_queue.msgs[1]).addr = rslave_address;
    (work_queue.msgs[1]).buf = buf;

    ret = ioctl(fd, I2C_RDWR, (unsigned long) &work_queue);
    //write(fd, w_buf, reg_bytes);
    //ret = read(fd, buf, len);
    return ret;
}
int i2cWrite(int fd, unsigned char *buf, int len, unsigned slave_address, unsigned reg_address, int reg_bytes)
{
	#define BUF_MAX 8
    struct i2c_rdwr_ioctl_data work_queue;
    struct i2c_msg messages[1];
    unsigned char w_buf[8];

    int ret;
    if (fd<0) {
    	LOGW("i2c is not opened\n");
		return 0;
	}
    if((len+reg_bytes)>BUF_MAX){
    	LOGW("input too much one time\n");
    	return 0;
    }
    switch(reg_bytes){
    case 1:
    	w_buf[0] = reg_address&0xff;
    	break;
    case 2:
		w_buf[0] = ((reg_address>>8)&0xff);
		w_buf[1] = (reg_address&0xff);
		break;
    default:
    	LOGW("Unsupport reg bytes.\n");
    	return 0;
    }

    work_queue.nmsgs = 1;
    work_queue.msgs = messages;

    (work_queue.msgs[0]).len = reg_bytes + len;
    (work_queue.msgs[0]).flags = 0;
    (work_queue.msgs[0]).addr = slave_address;
    (work_queue.msgs[0]).buf = w_buf;

    switch(len){
        case 1:
        	w_buf[reg_bytes] = buf[0];
        	break;
        case 2:
    		w_buf[reg_bytes] = buf[1];
    		w_buf[reg_bytes+1] = buf[0];
    		break;
        default:
        	LOGW("Unsupport buf len.\n");
        	return 0;
        }
    //LOGW("write[%d %d %d %d]\n",reg_bytes + len, w_buf[0], w_buf[1], w_buf[2]);
    ret = ioctl(fd, I2C_RDWR, (unsigned long) &work_queue);
    //ret = write(fd, w_buf, reg_bytes + len);
    //LOGI("i2cWrite %d.\n", ret);
    return ret;
}

