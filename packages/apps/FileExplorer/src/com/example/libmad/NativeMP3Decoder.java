package com.example.libmad;


public class NativeMP3Decoder {

	private int ret;

	public NativeMP3Decoder() {

	}

	public native int init(String file,int StartAddr);

	public native int getAudioBuf(short[] audioBuffer, int numSamples);

	public native void closeAduioFile();

	public native int getAudioSamplerate();



}