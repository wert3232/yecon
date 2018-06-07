/* //device/samples/SampleCode/src/com/android/samples/app/RemoteServiceInterface.java
**
** Copyright 2007, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License"); 
** you may not use this file except in compliance with the License. 
** You may obtain a copy of the License at 
**
**     http://www.apache.org/licenses/LICENSE-2.0 
**
** Unless required by applicable law or agreed to in writing, software 
** distributed under the License is distributed on an "AS IS" BASIS, 
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and 
** limitations under the License.
*/

package com.yecon.music;

import android.graphics.Bitmap;
import com.yecon.music.MusicFileInfo;

interface IMediaPlaybackService
{
    void openFile(String path);
    void open(in long [] list, int position, int listMode, long fieldId);
    int getQueuePosition();
    boolean isPlaying();
    void stop();
    void pause();
    void play();
    void prev();
    void next();
    long duration();
    long position();
    long seek(long pos);
    String getTrackName();
    String getAlbumName();
    long getAlbumId();
    String getArtistName();
    long getArtistId();
    void enqueue(in long [] list, int action);
    long [] getQueue();
    void moveQueueItem(int from, int to);
    void setQueuePosition(int index);
    String getPath();
    long getAudioId();
    void setShuffleMode(int shufflemode);
    int getShuffleMode();
    int removeTracks(int first, int last);
    int removeTrack(long id);
    void setRepeatMode(int repeatmode);
    int getRepeatMode();
    int getMediaMountedCount();
    int getAudioSessionId();
    MusicFileInfo[] getPlayList();
    int getPlayListLen();
    String getYear();
    void rePlay();
    void rePlayFromSpecifyPosition(long position);
    int getPlaySpeed();
    int setPlaySpeed(int speed);
    int [] getSupportedMaxRate();
    int   getCapability();
    void openRearAudio();
    void closeRearAudio();
    int getCurrentRealState();
    String getTrackPath();
    void reloadPlayList();
    boolean isScanning();
    void userAction(int action);
}

