#ifndef __I2C_H__
#define __I2C_H__
#ifdef __cplusplus
extern "C"{
#endif
int i2cOpen(const char *devPath, int slaveAddr);
void i2cClose(int fd);
int i2cRead(int fd, unsigned char *buf, int len, unsigned slave_address, unsigned reg_address, int reg_bytes);
int i2cReadEx(int fd, unsigned char *buf, int len, unsigned wslave_address, unsigned rslave_address, unsigned reg_address, int reg_bytes);
int i2cWrite(int fd, unsigned char *buf, int len, unsigned slave_address, unsigned reg_address, int reg_bytes);
#ifdef __cplusplus
}
#endif
#endif
