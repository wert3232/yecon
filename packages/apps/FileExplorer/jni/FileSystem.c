#include <unistd.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <sys/types.h>
#include <stdlib.h>
#include <fcntl.h>
#include "FileSystem.h"
#include <android/log.h>   
#define TAG "NativeMP3Decoder"   

 int file_open(const char *filename, int flags)
{
	int access;
    T_pFILE fd = 0;
    if (flags ==  _CREATE) {
        access = O_CREAT | O_TRUNC | O_RDWR;
    } else if (flags == _WRONLY) {
        access = O_CREAT | O_TRUNC | O_WRONLY;
    } else if (flags == _RDONLY){
        access = O_RDONLY;
    } else if (flags == _RDWR){
     	access = O_RDWR;
    } else{
    	return -1;
    }
	
#ifdef O_BINARY
    access |= O_BINARY;
#endif
    fd = open(filename, access, 0666);
    if (fd == -1)
        return -1;
    return fd;
}

int file_read(T_pFILE fd, unsigned char *buf, int size)
{
    
    return read(fd, buf, size);
}

int file_write(T_pFILE fd, unsigned char *buf, int size)
{
    
    return write(fd, buf, size);
}


int64_t file_seek(T_pFILE fd, int64_t pos, int whence)
{
    
    if (whence == 0x10000) {
        struct stat st;
        int ret = fstat(fd, &st);
        return ret < 0 ? -1 : st.st_size;
    }
   //__android_log_print(ANDROID_LOG_ERROR, TAG, "qqq----%d-----",ftell(fd));  
    return lseek(fd, pos, whence);
}

int file_close(T_pFILE fd)
{
   
    return close(fd);
}