
package com.yecon.sourcemanager;

interface ISourceManagerService
{
	int request(int srcNo);
	void release(int srcNo);
	void hotplug(int srcNo, String devPath, boolean insert, boolean appExit);
	void releaseMediaKey(int srcNo);
	void updatePlayingPath(int srcNo, String path);
}

