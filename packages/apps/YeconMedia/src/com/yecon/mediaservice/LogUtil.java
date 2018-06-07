 package com.yecon.mediaservice;
 
 import android.annotation.SuppressLint;
 import android.util.Log;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 
 public class LogUtil
 {
   public static String getFileLineMethod()
   {
     StackTraceElement traceElement = new java.lang.Exception().getStackTrace()[1];
     StringBuffer toStringBuffer = new StringBuffer("[")
       .append(traceElement.getFileName()).append(" | ")
       .append(traceElement.getLineNumber()).append(" | ")
       .append(traceElement.getMethodName()).append("]");
     return toStringBuffer.toString();
   }
 
   public static String _FILE_()
   {
     StackTraceElement traceElement = new java.lang.Exception().getStackTrace()[1];
     return traceElement.getFileName();
   }
 
   public static String _FUNC_()
   {
     StackTraceElement traceElement = new java.lang.Exception().getStackTrace()[1];
     return traceElement.getMethodName();
   }
 
   public static int _LINE_()
   {
     StackTraceElement traceElement = new java.lang.Exception().getStackTrace()[1];
     return traceElement.getLineNumber();
   }
 
   @SuppressLint({"SimpleDateFormat"})
   public static String _TIME_()
   {
     Date now = new Date();
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
     return sdf.format(now);
   }
 
   public static void printError(String TAG, String FUNC, String content) {
     Log.e(TAG, "[" + FUNC + "] " + content);
   }
 
   public static void printInfo(String TAG, String FUNC, String content) {
     Log.i(TAG, "[" + FUNC + "] " + content);
   }
 
   public static void printWarn(String TAG, String FUNC, String content) {
     Log.w(TAG, "[" + FUNC + "] " + content);
   }
 }

