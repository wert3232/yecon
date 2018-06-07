 package com.yecon.mediaservice;
 
 public final class MediaPlayerContants
 {
   public static final String LAST_MEMORY_MEDIA_INFO = "LAST_MEMORY_MEDIA_INFO";
   public static final String LAST_MEMORY_MEDIA_TAG_SD = "LAST_MEMORY_MEDIA_TAG_SD";
   public static final String LAST_MEMORY_MEDIA_TAG_USB = "LAST_MEMORY_MEDIA_TAG_USB";
   public static final String LAST_MEMORY_DEVICE = "LAST_MEMORY_DEVICE";
   public static final String PATH = "path";
   public static final int ID_INVALID = -1;
   public static final int ID_ALL = -1;
   public static final String[] FILE_COLUMNS = { 
     "_id", 
     "data", 
     "file_name", 
     "title", 
     "album", 
     "artist", 
     "album_id", 
     "artist_id", 
     "parent_id", 
     "apic_id", 
     "favorite" };
   public static final int FILE_COLUMNS_INDEX_ID = 0;
   public static final int FILE_COLUMNS_INDEX_DATA = 1;
   public static final int FILE_COLUMNS_INDEX_NAME = 2;
   public static final int FILE_COLUMNS_INDEX_TITLE = 3;
   public static final int FILE_COLUMNS_INDEX_ALBUM = 4;
   public static final int FILE_COLUMNS_INDEX_ARTIST = 5;
   public static final int FILE_COLUMNS_INDEX_ALBUM_ID = 6;
   public static final int FILE_COLUMNS_INDEX_ARTIST_ID = 7;
   public static final int FILE_COLUMNS_INDEX_PARENT_ID = 8;
   public static final int FILE_COLUMNS_INDEX_APIC_ID = 9;
   public static final int FILE_COLUMNS_INDEX_FAVOR = 10;
   public static final String[] DIR_COLUMNS = { 
     "_id", 
     "data", 
     "dir_name", 
     "amount_audio", 
     "amount_image", 
     "amount_video" };
   public static final int DIR_COLUMNS_INDEX_ID = 0;
   public static final int DIR_COLUMNS_INDEX_DATA = 1;
   public static final int DIR_COLUMNS_INDEX_NAME = 2;
   public static final int DIR_COLUMNS_INDEX_AMOUNT_AUDIO = 3;
   public static final int DIR_COLUMNS_INDEX_AMOUNT_IMAGE = 4;
   public static final int DIR_COLUMNS_INDEX_AMOUNT_VIDEO = 5;
   public static final String[] ALBUM_COLUMNS = { 
     "_id", 
     "album_name", 
     "amount" };
   public static final int ALBUM_COLUMNS_INDEX_ID = 0;
   public static final int ALBUM_COLUMNS_INDEX_NAME = 1;
   public static final int ALBUM_COLUMNS_INDEX_AMOUNT = 2;
   public static final String[] ARTIST_COLUMNS = { 
     "_id", 
     "artist_name", 
     "amount" };
   public static final int ARTIST_COLUMNS_INDEX_ID = 0;
   public static final int ARTIST_COLUMNS_INDEX_NAME = 1;
   public static final int ARTIST_COLUMNS_INDEX_AMOUNT = 2;
 
   public static final class MediaListContants
   {
     public static boolean isFileList(int iListType)
     {
       return (iListType & 0x1) == 1;
     }
 
     public static int getListOrigin(int iListType)
     {
       return iListType >> 4;
     }
 
     public static final class Attr
     {
       public static final int FILE_LIST = 1;
       public static final int OTHER_LIST = 0;
     }
 
     public static final class Origin
     {
       public static final int FILE = 1;
       public static final int DIR = 2;
       public static final int ALBUM = 3;
       public static final int ARTIST = 4;
       public static final int FAVORITE = 5;
     }
 
     public static final class Type
     {
       public static final int PLAY_FILE = 1;
       public static final int ALL_FILE = 17;
       public static final int ALL_DIR = 32;
       public static final int ALL_ALBUM = 48;
       public static final int ALL_ARTIST = 64;
       public static final int DIR_FILE = 33;
       public static final int ALBUM_FILE = 49;
       public static final int ARTIST_FILE = 65;
       public static final int FAVORITE_FILE = 81;
     }
   }
 
   public static final class MediaPlayerMessage
   {
     public static final int UPDATE_SERVICE_STATE = 0;
     public static final int UPDATE_LIST_DATA = 1;
     public static final int UPDATE_PLAY_STATE = 2;
     public static final int UPDATE_RANDOM_STATE = 3;
     public static final int UPDATE_REPEAT_STATE = 4;
     public static final int UPDATE_PLAY_PROGRESS = 5;
     public static final int UPDATE_MEDIA_INTO = 6;
   }
 
   public static final class MediaType
   {
     public static final int MEDIA_AUDIO = 1;
     public static final int MEDIA_VIDEO = 2;
     public static final int MEDIA_IMAGE = 3;
   }
 
   public static final class PlayStatus
   {
     public static final int IDLE = 0;
     public static final int DECODING = 1;
     public static final int DECODED = 2;
     public static final int SEEKING = 3;
     public static final int STARTED = 4;
     public static final int PAUSED = 5;
     public static final int STOPED = 6;
     public static final int ERROR = 7;
     public static final int FINISH = 8;
   }
 
   public static final class RandomMode
   {
     public static final int OFF = 0;
     public static final int ON = 1;
   }
 
   public static final class RepeatMode
   {
     public static final int OFF = 0;
     public static final int SINGLE = 1;
     public static final int ALL = 2;
   }
 
   public static final class ServiceStatus
   {
     public static final int SCANING = 1;
     public static final int SCANED = 2;
     public static final int SCAN_TIMEOUT = 3;
     public static final int PLAYED = 4;
     public static final int EMPTY_STORAGE = 5;
     public static final int LOST_STORAGE = 6;
     public static final int NO_STORAGE = 7;
     public static final int LOST_AUDIO_FOCUS = 8;
     public static final int QB_POWER = 9;
   }
 }

