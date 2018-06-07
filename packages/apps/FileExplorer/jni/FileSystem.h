#include <stdlib.h>

typedef    signed long            T_S32;      /* signed 32 bit integer */


#define T_pFILE 		T_S32
#define     T_hFILE             T_S32






#define _CREATE 0//"wb+"//0
#define _RDONLY 1//"rb" // 1
#define _WRONLY 2//"wb"// 2
#define _RDWR   3//"rb+"// 3



#define _FMODE_READ     _RDONLY
#define _FMODE_WRITE    _WRONLY
#define _FMODE_CREATE   _CREATE
#define _FMODE_OVERLAY   _RDWR  


#define _FSEEK_CURRENT  1
#define _FSEEK_END      2
#define _FSEEK_SET      0
#define _FOPEN_FAIL     -1


#define SEEK_CURRENT  1
#define SEEK_CUR  1
#define SEEK_END      2
#define SEEK_SET      0

#define FS_SEEK_SET   0