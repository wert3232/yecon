@echo off
rem ##########################################################################
rem
rem Module:  memtest
rem
rem Function:
rem	This is a script used to install memtest app and runtime elf files.
rem	Please make sure that adb tools are installed in your computer.
rem
rem Version:
rem	V1.0
rem
rem ##########################################################################

echo wait-for-device
adb wait-for-device
adb devices
adb remount
echo push memtest /system/bin/
adb push memtest /system/bin/
echo push YG_MemoryFlashTest.apk /system/app/
adb push YG_MemoryFlashTest.apk /system/app/
echo chmod 6755 /system/bin/memtest
adb shell "chmod 6755 /system/bin/memtest"
echo install Done.
pause
