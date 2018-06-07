package com.wesail.tdr.util;/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2>字节工具类，提供一些有关字节的便捷方法</h2>
 *
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;(01)、位移加密：static void byteJiaMi(byte[] bytes)
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;(02)、从bytes上截取一段：static byte[] cutOut(byte[] bytes, int off, int length)
 */
public class ByteUtils {
    /**
     * (01)、位移加密
     * @param bytes
     */
    public static void byteJiaMi(byte[] bytes){
        for (int w = 0; w < bytes.length; w++){
            int a = bytes[w];
            a = ~a;
            bytes[w] = (byte)a;
        }
    }

    /**
     * (02)、从bytes上截取一段
     * @param bytes 母体
     * @param off 起始
     * @param length 个数
     * @return byte[]
     */
    public static byte[] cutOut(byte[] bytes, int off, int length){
        byte[] bytess = new byte[length];
        System.arraycopy(bytes, off, bytess, 0, length);
        return bytess;
    }

    /**
     * 将字节转换为二进制字符串
     * @param bytes 字节数组
     * @return 二进制字符串
     */
    public static String byteToBit(byte... bytes){
        StringBuffer sb = new StringBuffer();
        int z, len;
        String str;
        for(int w = 0; w < bytes.length ; w++){
            z = bytes[w];
            z |= 256;
            str = Integer.toBinaryString(z);
            len = str.length();
            sb.append(str.substring(len-8, len));
        }
        return sb.toString();
    }


    /**
     * 字节数组转换成16进制字符串
     * @param raw
     * @return
     */
    public static String getHex(byte [] raw ) {
        String HEXES = "0123456789ABCDEF";
        if ( raw == null ) {
            return null;
        }
        final StringBuilder hex = new StringBuilder( 2 * raw.length );
        for ( final byte b : raw ) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    /**
     *
     * @param raw 要生成16进制字符串的小字节数组（数组大小最好不要超过 1万）
     * @param bet 16进制之间的间隔符
     * @return
     */
    public static String getHex2(byte [] raw,String bet) {
        String HEXES = "0123456789ABCDEF";
        if ( raw == null ) {
            return null;
        }
        final StringBuilder hex = new StringBuilder( 2 * raw.length );
        for ( final byte b : raw ) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
            if(bet != null) hex.append(bet);
        }
        return hex.toString();
    }

    /**
     * 将一个short转换成字节数组
     * @param sh short
     * @return 字节数组
     */
    public static byte[] valueOf(short sh){
        byte[] shortBuf = new byte[2];
        for(int i=0;i<2;i++) {
            int offset = (shortBuf.length - 1 -i)*8;
            shortBuf[i] = (byte)((sh>>>offset)&0xff);
        }
        return shortBuf;
    }

    /**
     * 将一个int转换成字节数组
     * @param in int
     * @return 字节数组
     */
    public static byte[] valueOf(int in){
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((in >>> offset) & 0xFF);
        }
        return b;
    }

    /**
     * 转换成byte
     * @param in 输入的数字
     * @param length byte位数，若输入数字大于 0xff的length次方就报错
     * @return
     * @throws Exception
     */
    public static byte[] valueOf2HasLength(int in,int length) throws Exception {
        if(in > Math.pow(0xff,length)) throw new Exception("number " + in + "is greater than 0xff power " + length);
        byte[] b = new byte[length];
        for (int i = 0; i < length; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((in >>> offset) & 0xFF);
        }
        return b;
    }
    public static byte[] valueOf2(int in){
        byte[] b = new byte[4];
        if(in <= 0xff){
            b = new byte[1];
        }else if(0xff < in && in <= 0xffff){
            b = new byte[2];
        }else if (0xffff < in && in  <= 0xffffff){
            b = new byte[3];
        }else if(0xffffff < in && in  <= 0xffffffff){
            b = new byte[4];
        }
        for (int i = 0; i < b.length; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((in >>> offset) & 0xFF);
        }
        return b;
    }

    /**
     * 转换成byte
     * @param in 输入的数字
     * @param size 匹配字节数 ，若不匹配则报错
     * @return
     * @throws Exception
     */
    public static byte[] valueOf2(int in,int size) throws Exception {
        byte[] b = new byte[4];
        if(in <= 0xff){
            b = new byte[1];
        }else if(0xff < in && in <= 0xffff){
            b = new byte[2];
        }else if (0xffff < in && in  <= 0xffffff){
            b = new byte[3];
        }else if(0xffffff < in && in  <= 0xffffffff){
            b = new byte[4];
        }
        for (int i = 0; i < 4; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((in >>> offset) & 0xFF);
        }
        if(b.length != size) throw new Exception("this number no match " + size +" .");
        return b;
    }

    /**
     * 将一个字节数组通过编码转换成字符串
     */
    public static String byteToString(byte[] data,String encodingType) {
        String str = null;
        try {
            str = new String(data, encodingType);
        } catch (UnsupportedEncodingException e) {
        }
        return str;
    }

    /**
     * 将一个字节数组通过UTF-8编码转换成字符串
     */
    public static String utf8ToString(byte[] data) {
        return byteToString(data,"UTF-8");
    }

    /**
     * 将字符串通过指定的字符集转换成一个字节数组
     */
    public static byte[] stringToBytes(String str,String encodingType){
        try {
            return str.getBytes(encodingType);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字符串通过UTF-8转换成一个字节数组
     */
    public static byte[] utf8StringToBytes(String str){
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 两个字节数组合并
     */
    public static byte[] combineByte(byte[] bs1,byte[] bs2){
        byte[] byte_3 = new byte[bs1.length + bs2.length];
        System.arraycopy(bs1, 0, byte_3, 0, bs1.length);
        System.arraycopy(bs2, 0, byte_3, bs1.length, bs2.length);
        return byte_3;
    }
    public static byte[] combineByte(byte[] bs1,byte b){
        byte[] byte_3 = new byte[bs1.length + 1];
        System.arraycopy(bs1, 0, byte_3, 0, bs1.length);
        byte_3[bs1.length] = b;
        return byte_3;
    }
    public static byte[] combineByte(byte b1,byte b2) {
        byte[] byte_3 = new byte[2];
        byte_3[0] = b1;
        byte_3[1] = b2;
        return byte_3;
    }
    
    public static int bytesToInt(byte[] src, int offset) {  
        int value;    
        value = (int) ((src[offset] & 0xFF)   
                | ((src[offset+1] & 0xFF)<<8)   
                | ((src[offset+2] & 0xFF)<<16)   
                | ((src[offset+3] & 0xFF)<<24));  
        return value;  
    }  
}
