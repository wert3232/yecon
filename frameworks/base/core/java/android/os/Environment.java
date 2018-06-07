/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.os;

import android.os.storage.IMountService;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import android.os.SystemProperties;
import android.content.Context;
import android.net.wifi.WifiManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.SystemClock;
import com.android.internal.app.LocalePicker;
import java.util.Locale;
/**
 * Provides access to environment variables.
 */
public class Environment {
    private static final String TAG = "Environment";

    private static final String ENV_EXTERNAL_STORAGE = "EXTERNAL_STORAGE";
    private static final String ENV_EMULATED_STORAGE_SOURCE = "EMULATED_STORAGE_SOURCE";
    private static final String ENV_EMULATED_STORAGE_TARGET = "EMULATED_STORAGE_TARGET";
    private static final String ENV_MEDIA_STORAGE = "MEDIA_STORAGE";

    /** {@hide} */
    public static String DIRECTORY_ANDROID = "Android";

    private static final File ROOT_DIRECTORY
            = getDirectory("ANDROID_ROOT", "/system");

    // Begin : Added by lei.ye 2012:7:12
    private static final File EXTERNAL_STORAGE_DIRECTORY_SDDISK
            = getDirectory("EXTERNAL_SDDISK_STORAGE", "/mnt/ext_sdcard");

    private static final File EXTERNAL_STORAGE_DIRECTORY_UDISK
            = getDirectory("EXTERNAL_UDISK_STORAGE", "/mnt/udisk");
    // End : Added by lei.ye 2012:7:12
    
    private static final String SYSTEM_PROPERTY_EFS_ENABLED = "persist.security.efs.enabled";

    private static UserEnvironment sCurrentUser;

    private static final Object sLock = new Object();

    // @GuardedBy("sLock")
    private static volatile StorageVolume sPrimaryVolume;
    
    private static final String PERSYS_NAVI_PATH = "persist.sys.navi_path";
    
    private static final String SDCARD_VOLUME = "SDCARD";
    private static final String EXT_SDCARD1_VOLUME = "EXT_SDCARD1";
    private static final String EXT_SDCARD2_VOLUME = "EXT_SDCARD2";
    private static final String USB1_VOLUME = "USB1";
    private static final String USB2_VOLUME = "USB2";
    private static final String USB3_VOLUME = "USB3";
    private static final String USB4_VOLUME = "USB4";

    private static String SDCARD1_PATH = "/mnt/ext_sdcard1";
    private static String SDCARD2_PATH = "/mnt/ext_sdcard2";
    
    private static String mGpsMapPath = SDCARD_VOLUME;
    
    private static int mGpsMapInExt = 0;

    private static StorageVolume getPrimaryVolume() {
        if (sPrimaryVolume == null) {
            synchronized (sLock) {
                if (sPrimaryVolume == null) {
                    try {
                        IMountService mountService = IMountService.Stub.asInterface(ServiceManager
                                .getService("mount"));
                        final StorageVolume[] volumes = mountService.getVolumeList();
                        sPrimaryVolume = StorageManager.getPrimaryVolume(volumes);
                    } catch (Exception e) {
                        Log.e(TAG, "couldn't talk to MountService", e);
                    }
                }
            }
        }
        return sPrimaryVolume;
    }

    static {
        initForCurrentUser();
    }

    /** {@hide} */
    public static void initForCurrentUser() {
        final int userId = UserHandle.myUserId();
        sCurrentUser = new UserEnvironment(userId);

        synchronized (sLock) {
            sPrimaryVolume = null;
        }
    }

    /** {@hide} */
    public static class UserEnvironment {
        // TODO: generalize further to create package-specific environment

        private File mExternalStorage;
        private final File mExternalStorageAndroidData;
        private final File mExternalStorageAndroidMedia;
        private final File mExternalStorageAndroidObb;
        private final File mMediaStorage;

		private final File mInternalStorage;  // Jade add;2014-12-2
		
        public UserEnvironment(int userId) {
            // See storage config details at http://source.android.com/tech/storage/
            // String rawExternalStorage = System.getenv(ENV_EXTERNAL_STORAGE);
            String rawExternalStorage = initExternalStorage();
			
            String rawEmulatedStorageTarget = System.getenv(ENV_EMULATED_STORAGE_TARGET);
            String rawMediaStorage = System.getenv(ENV_MEDIA_STORAGE);
            if (TextUtils.isEmpty(rawMediaStorage)) {
                rawMediaStorage = "/data/media";
            }

            if (!TextUtils.isEmpty(rawEmulatedStorageTarget)) {
                // Device has emulated storage; external storage paths should have
                // userId burned into them.
                final String rawUserId = Integer.toString(userId);
                final File emulatedBase = new File(rawEmulatedStorageTarget);
                final File mediaBase = new File(rawMediaStorage);

                // /storage/emulated/0
                mExternalStorage = buildPath(emulatedBase, rawUserId);
                // /data/media/0
                mMediaStorage = buildPath(mediaBase, rawUserId);

            } else {
                // Device has physical external storage; use plain paths.
                if (TextUtils.isEmpty(rawExternalStorage)) {
                    Log.w(TAG, "EXTERNAL_STORAGE undefined; falling back to default");
                    rawExternalStorage = "/storage/sdcard0";
                }

                // /storage/sdcard0
                mExternalStorage = new File(rawExternalStorage);
                // /data/media
                mMediaStorage = new File(rawMediaStorage);
            }

			mInternalStorage = new File("/mnt/sdcard");
			mExternalStorageAndroidObb = buildPath(mInternalStorage, DIRECTORY_ANDROID, "obb");
            mExternalStorageAndroidData = buildPath(mInternalStorage, DIRECTORY_ANDROID, "data");
            mExternalStorageAndroidMedia = buildPath(mInternalStorage, DIRECTORY_ANDROID, "media");
        }

    	private String getNaviPath() {
    	    mGpsMapPath = SystemProperties.get(PERSYS_NAVI_PATH, SDCARD_VOLUME);
            Log.d(TAG, "getNaviPath - mGpsMapPath: " + mGpsMapPath);
            if (mGpsMapPath.equalsIgnoreCase(SDCARD_VOLUME)) {
                SDCARD1_PATH = "/mnt/sdcard";
                SDCARD2_PATH = "/mnt/sdcard";
                
                mGpsMapInExt = 0x01;
            } else if (mGpsMapPath.equalsIgnoreCase(EXT_SDCARD1_VOLUME)) {
                SDCARD1_PATH = "/mnt/ext_sdcard1";
                SDCARD2_PATH = "/mnt/ext_sdcard1";
                
                mGpsMapInExt = 0x02;
            } else if (mGpsMapPath.equalsIgnoreCase(EXT_SDCARD2_VOLUME)) {
                SDCARD1_PATH = "/mnt/ext_sdcard2";
                SDCARD2_PATH = "/mnt/ext_sdcard2";
                
                mGpsMapInExt = 0x03;
            } else if (mGpsMapPath.equalsIgnoreCase(USB1_VOLUME)) {
                SDCARD1_PATH = "/mnt/udisk1";
                SDCARD2_PATH = "/mnt/udisk1";
                
                mGpsMapInExt = 0x04;
            } else if (mGpsMapPath.equalsIgnoreCase(USB2_VOLUME)) {
                SDCARD1_PATH = "/mnt/udisk2";
                SDCARD2_PATH = "/mnt/udisk2";
                
                mGpsMapInExt = 0x05;
            } else if (mGpsMapPath.equalsIgnoreCase(USB3_VOLUME)) {
                SDCARD1_PATH = "/mnt/udisk3";
                SDCARD2_PATH = "/mnt/udisk3";
                
                mGpsMapInExt = 0x06;
            } else if (mGpsMapPath.equalsIgnoreCase(USB4_VOLUME)) {
                SDCARD1_PATH = "/mnt/udisk4";
                SDCARD2_PATH = "/mnt/udisk4";
                
                mGpsMapInExt = 0x07;
            }
            
            StringBuffer log = new StringBuffer();
            log.append("getNaviPath - SDCARD1_PATH: ");
            log.append(SDCARD1_PATH);
            log.append(" - SDCARD2_PATH: ");
            log.append(SDCARD2_PATH);
            Log.d(TAG, log.toString());
            
            return SDCARD1_PATH;
    	}
    	
    	private String initExternalStorage() {
    	    String rawExternalStorage = System.getenv(ENV_EXTERNAL_STORAGE);
    	    
    	    rawExternalStorage = getNaviPath();
            
            mExternalStorage = new File(rawExternalStorage);
            
            return rawExternalStorage;
    	}
  
        public File getExternalStorageDirectory() {
            return mExternalStorage;
        }

        public File getExternalStorageObbDirectory() {
            return mExternalStorageAndroidObb;
        }

        public File getExternalStoragePublicDirectory(String type) {
            return new File(mExternalStorage, type);
        }

        public File getExternalStorageAndroidDataDir() {
            return mExternalStorageAndroidData;
        }

        public File getExternalStorageAppDataDirectory(String packageName) {
            return new File(mExternalStorageAndroidData, packageName);
        }

        public File getExternalStorageAppMediaDirectory(String packageName) {
            return new File(mExternalStorageAndroidMedia, packageName);
        }

        public File getExternalStorageAppObbDirectory(String packageName) {
            return new File(mExternalStorageAndroidObb, packageName);
        }

        public File getExternalStorageAppFilesDirectory(String packageName) {
            return new File(new File(mExternalStorageAndroidData, packageName), "files");
        }

        public File getExternalStorageAppCacheDirectory(String packageName) {
            return new File(new File(mExternalStorageAndroidData, packageName), "cache");
        }

        public File getMediaStorageDirectory() {
            return mMediaStorage;
        }
    }

    /**
     * Gets the Android root directory.
     */
    public static File getRootDirectory() {
        return ROOT_DIRECTORY;
    }

    /**
     * Gets the system directory available for secure storage.
     * If Encrypted File system is enabled, it returns an encrypted directory (/data/secure/system).
     * Otherwise, it returns the unencrypted /data/system directory.
     * @return File object representing the secure storage system directory.
     * @hide
     */
    public static File getSystemSecureDirectory() {
        if (isEncryptedFilesystemEnabled()) {
            return new File(SECURE_DATA_DIRECTORY, "system");
        } else {
            return new File(DATA_DIRECTORY, "system");
        }
    }

    public static File getData4writeSystemSecureDirectory() {
        if (isEncryptedFilesystemEnabled()) {
            return new File(SECURE_DATA4WRITE_DIRECTORY, "system");
        } else {
            return new File(DATA4WRITE_DIRECTORY, "system");
        }
    }
    /**
     * Gets the data directory for secure storage.
     * If Encrypted File system is enabled, it returns an encrypted directory (/data/secure).
     * Otherwise, it returns the unencrypted /data directory.
     * @return File object representing the data directory for secure storage.
     * @hide
     */
    public static File getSecureDataDirectory() {
        if (isEncryptedFilesystemEnabled()) {
            return SECURE_DATA_DIRECTORY;
        } else {
            return DATA_DIRECTORY;
        }
    }

    public static File getSecureData4writeDirectory() {
        if (isEncryptedFilesystemEnabled()) {
            return SECURE_DATA4WRITE_DIRECTORY;
        } else {
            return DATA4WRITE_DIRECTORY;
        }
    }
    /**
     * Return directory used for internal media storage, which is protected by
     * {@link android.Manifest.permission#WRITE_MEDIA_STORAGE}.
     *
     * @hide
     */
    public static File getMediaStorageDirectory() {
        throwIfSystem();
        return sCurrentUser.getMediaStorageDirectory();
    }

    /**
     * Return the system directory for a user. This is for use by system services to store
     * files relating to the user. This directory will be automatically deleted when the user
     * is removed.
     *
     * @hide
     */
    public static File getUserSystemDirectory(int userId) {
        return new File(new File(getSystemSecureDirectory(), "users"), Integer.toString(userId));
    }

    public static File getData4writeUserSystemDirectory(int userId) {
        return new File(new File(getData4writeSystemSecureDirectory(), "users"), Integer.toString(userId));
    }
    /**
     * Returns whether the Encrypted File System feature is enabled on the device or not.
     * @return <code>true</code> if Encrypted File System feature is enabled, <code>false</code>
     * if disabled.
     * @hide
     */
    public static boolean isEncryptedFilesystemEnabled() {
        return SystemProperties.getBoolean(SYSTEM_PROPERTY_EFS_ENABLED, false);
    }

    private static final File DATA_DIRECTORY
            = getDirectory("ANDROID_DATA", "/data");

    private static final File DATA4WRITE_DIRECTORY
            = getDirectory("ANDROID_DATA4WRITE", "/data4write");

    /**
     * @hide
     */
    private static final File SECURE_DATA_DIRECTORY
            = getDirectory("ANDROID_SECURE_DATA", "/data/secure");

    private static final File SECURE_DATA4WRITE_DIRECTORY
            = getDirectory("ANDROID_SECURE_DATA", "/data4write/secure");
	
    private static final File DOWNLOAD_CACHE_DIRECTORY = getDirectory("DOWNLOAD_CACHE", "/cache");

    /**
     * Gets the Android data directory.
     */
    public static File getDataDirectory() {
        return DATA_DIRECTORY;
    }

    public static File getData4writeDirectory() {
        return DATA4WRITE_DIRECTORY;
    }
    /**
     * Gets the Android external storage directory.  This directory may not
     * currently be accessible if it has been mounted by the user on their
     * computer, has been removed from the device, or some other problem has
     * happened.  You can determine its current state with
     * {@link #getExternalStorageState()}.
     * 
     * <p><em>Note: don't be confused by the word "external" here.  This
     * directory can better be thought as media/shared storage.  It is a
     * filesystem that can hold a relatively large amount of data and that
     * is shared across all applications (does not enforce permissions).
     * Traditionally this is an SD card, but it may also be implemented as
     * built-in storage in a device that is distinct from the protected
     * internal storage and can be mounted as a filesystem on a computer.</em></p>
     *
     * <p>On devices with multiple users (as described by {@link UserManager}),
     * each user has their own isolated external storage. Applications only
     * have access to the external storage for the user they're running as.</p>
     *
     * <p>In devices with multiple "external" storage directories (such as
     * both secure app storage and mountable shared storage), this directory
     * represents the "primary" external storage that the user will interact
     * with.</p>
     *
     * <p>Applications should not directly use this top-level directory, in
     * order to avoid polluting the user's root namespace.  Any files that are
     * private to the application should be placed in a directory returned
     * by {@link android.content.Context#getExternalFilesDir
     * Context.getExternalFilesDir}, which the system will take care of deleting
     * if the application is uninstalled.  Other shared files should be placed
     * in one of the directories returned by
     * {@link #getExternalStoragePublicDirectory}.</p>
     *
     * <p>Writing to this path requires the
     * {@link android.Manifest.permission#WRITE_EXTERNAL_STORAGE} permission. In
     * a future platform release, access to this path will require the
     * {@link android.Manifest.permission#READ_EXTERNAL_STORAGE} permission,
     * which is automatically granted if you hold the write permission.</p>
     *
     * <p>This path may change between platform versions, so applications
     * should only persist relative paths.</p>
     * 
     * <p>Here is an example of typical code to monitor the state of
     * external storage:</p>
     * 
     * {@sample development/samples/ApiDemos/src/com/example/android/apis/content/ExternalStorage.java
     * monitor_storage}
     *
     * @see #getExternalStorageState()
     * @see #isExternalStorageRemovable()
     */
    public static File getExternalStorageDirectory() {
        throwIfSystem();
        return sCurrentUser.getExternalStorageDirectory();
    }

    /** {@hide} */
    public static File getLegacyExternalStorageDirectory() {
        return new File(System.getenv(ENV_EXTERNAL_STORAGE));
    }

    /** {@hide} */
    public static File getLegacyExternalStorageObbDirectory() {
        return buildPath(getLegacyExternalStorageDirectory(), DIRECTORY_ANDROID, "obb");
    }

    /** {@hide} */
    public static File getEmulatedStorageSource(int userId) {
        // /mnt/shell/emulated/0
        return new File(System.getenv(ENV_EMULATED_STORAGE_SOURCE), String.valueOf(userId));
    }

    /** {@hide} */
    public static File getEmulatedStorageObbSource() {
        // /mnt/shell/emulated/obb
        return new File(System.getenv(ENV_EMULATED_STORAGE_SOURCE), "obb");
    }

    // Begin : Added by lei.ye 2012:7:12
    public static File getExternalSddiskStorageDirectory() {
        return EXTERNAL_STORAGE_DIRECTORY_SDDISK;
    }

    public static File getExternalUdiskStorageDirectory() {
        return EXTERNAL_STORAGE_DIRECTORY_UDISK;
    }
    // End : by lei.ye 2012:7:12
    
    /**
     * Standard directory in which to place any audio files that should be
     * in the regular list of music for the user.
     * This may be combined with
     * {@link #DIRECTORY_PODCASTS}, {@link #DIRECTORY_NOTIFICATIONS},
     * {@link #DIRECTORY_ALARMS}, and {@link #DIRECTORY_RINGTONES} as a series
     * of directories to categories a particular audio file as more than one
     * type.
     */
    public static String DIRECTORY_MUSIC = "Music";
    
    /**
     * Standard directory in which to place any audio files that should be
     * in the list of podcasts that the user can select (not as regular
     * music).
     * This may be combined with {@link #DIRECTORY_MUSIC},
     * {@link #DIRECTORY_NOTIFICATIONS},
     * {@link #DIRECTORY_ALARMS}, and {@link #DIRECTORY_RINGTONES} as a series
     * of directories to categories a particular audio file as more than one
     * type.
     */
    public static String DIRECTORY_PODCASTS = "Podcasts";
    
    /**
     * Standard directory in which to place any audio files that should be
     * in the list of ringtones that the user can select (not as regular
     * music).
     * This may be combined with {@link #DIRECTORY_MUSIC},
     * {@link #DIRECTORY_PODCASTS}, {@link #DIRECTORY_NOTIFICATIONS}, and
     * {@link #DIRECTORY_ALARMS} as a series
     * of directories to categories a particular audio file as more than one
     * type.
     */
    public static String DIRECTORY_RINGTONES = "Ringtones";
    
    /**
     * Standard directory in which to place any audio files that should be
     * in the list of alarms that the user can select (not as regular
     * music).
     * This may be combined with {@link #DIRECTORY_MUSIC},
     * {@link #DIRECTORY_PODCASTS}, {@link #DIRECTORY_NOTIFICATIONS},
     * and {@link #DIRECTORY_RINGTONES} as a series
     * of directories to categories a particular audio file as more than one
     * type.
     */
    public static String DIRECTORY_ALARMS = "Alarms";
    
    /**
     * Standard directory in which to place any audio files that should be
     * in the list of notifications that the user can select (not as regular
     * music).
     * This may be combined with {@link #DIRECTORY_MUSIC},
     * {@link #DIRECTORY_PODCASTS},
     * {@link #DIRECTORY_ALARMS}, and {@link #DIRECTORY_RINGTONES} as a series
     * of directories to categories a particular audio file as more than one
     * type.
     */
    public static String DIRECTORY_NOTIFICATIONS = "Notifications";
    
    /**
     * Standard directory in which to place pictures that are available to
     * the user.  Note that this is primarily a convention for the top-level
     * public directory, as the media scanner will find and collect pictures
     * in any directory.
     */
    public static String DIRECTORY_PICTURES = "Pictures";
    
    /**
     * Standard directory in which to place movies that are available to
     * the user.  Note that this is primarily a convention for the top-level
     * public directory, as the media scanner will find and collect movies
     * in any directory.
     */
    public static String DIRECTORY_MOVIES = "Movies";
    
    /**
     * Standard directory in which to place files that have been downloaded by
     * the user.  Note that this is primarily a convention for the top-level
     * public directory, you are free to download files anywhere in your own
     * private directories.  Also note that though the constant here is
     * named DIRECTORY_DOWNLOADS (plural), the actual file name is non-plural for
     * backwards compatibility reasons.
     */
    public static String DIRECTORY_DOWNLOADS = "Download";
    
    /**
     * The traditional location for pictures and videos when mounting the
     * device as a camera.  Note that this is primarily a convention for the
     * top-level public directory, as this convention makes no sense elsewhere.
     */
    public static String DIRECTORY_DCIM = "DCIM";
    
    /**
     * Get a top-level public external storage directory for placing files of
     * a particular type.  This is where the user will typically place and
     * manage their own files, so you should be careful about what you put here
     * to ensure you don't erase their files or get in the way of their own
     * organization.
     * 
     * <p>On devices with multiple users (as described by {@link UserManager}),
     * each user has their own isolated external storage. Applications only
     * have access to the external storage for the user they're running as.</p>
     *
     * <p>Here is an example of typical code to manipulate a picture on
     * the public external storage:</p>
     * 
     * {@sample development/samples/ApiDemos/src/com/example/android/apis/content/ExternalStorage.java
     * public_picture}
     * 
     * @param type The type of storage directory to return.  Should be one of
     * {@link #DIRECTORY_MUSIC}, {@link #DIRECTORY_PODCASTS},
     * {@link #DIRECTORY_RINGTONES}, {@link #DIRECTORY_ALARMS},
     * {@link #DIRECTORY_NOTIFICATIONS}, {@link #DIRECTORY_PICTURES},
     * {@link #DIRECTORY_MOVIES}, {@link #DIRECTORY_DOWNLOADS}, or
     * {@link #DIRECTORY_DCIM}.  May not be null.
     * 
     * @return Returns the File path for the directory.  Note that this
     * directory may not yet exist, so you must make sure it exists before
     * using it such as with {@link File#mkdirs File.mkdirs()}.
     */
    public static File getExternalStoragePublicDirectory(String type) {
        throwIfSystem();
        return sCurrentUser.getExternalStoragePublicDirectory(type);
    }

    /**
     * Returns the path for android-specific data on the SD card.
     * @hide
     */
    public static File getExternalStorageAndroidDataDir() {
        throwIfSystem();
        return sCurrentUser.getExternalStorageAndroidDataDir();
    }
    
    /**
     * Generates the raw path to an application's data
     * @hide
     */
    public static File getExternalStorageAppDataDirectory(String packageName) {
        throwIfSystem();
        return sCurrentUser.getExternalStorageAppDataDirectory(packageName);
    }
    
    /**
     * Generates the raw path to an application's media
     * @hide
     */
    public static File getExternalStorageAppMediaDirectory(String packageName) {
        throwIfSystem();
        return sCurrentUser.getExternalStorageAppMediaDirectory(packageName);
    }
    
    /**
     * Generates the raw path to an application's OBB files
     * @hide
     */
    public static File getExternalStorageAppObbDirectory(String packageName) {
        throwIfSystem();
        return sCurrentUser.getExternalStorageAppObbDirectory(packageName);
    }
    
    /**
     * Generates the path to an application's files.
     * @hide
     */
    public static File getExternalStorageAppFilesDirectory(String packageName) {
        throwIfSystem();
        return sCurrentUser.getExternalStorageAppFilesDirectory(packageName);
    }

    /**
     * Generates the path to an application's cache.
     * @hide
     */
    public static File getExternalStorageAppCacheDirectory(String packageName) {
        throwIfSystem();
        return sCurrentUser.getExternalStorageAppCacheDirectory(packageName);
    }
    
    // Begin : Added by lei.ye 2012:7:12
    public static File getExternalSddiskStoragePublicDirectory(String type) {
        return new File(getExternalSddiskStorageDirectory(), type);
    }

    public static File getExternalUdiskStoragePublicDirectory(String type) {
        return new File(getExternalUdiskStorageDirectory(), type);
    }
    // End : by lei.ye 2012:7:12
    
    /**
     * Gets the Android download/cache content directory.
     */
    public static File getDownloadCacheDirectory() {
        return DOWNLOAD_CACHE_DIRECTORY;
    }

    /**
     * {@link #getExternalStorageState()} returns MEDIA_REMOVED if the media is not present.
     */
    public static final String MEDIA_REMOVED = "removed";
     
    /**
     * {@link #getExternalStorageState()} returns MEDIA_UNMOUNTED if the media is present
     * but not mounted. 
     */
    public static final String MEDIA_UNMOUNTED = "unmounted";

    /**
     * {@link #getExternalStorageState()} returns MEDIA_CHECKING if the media is present
     * and being disk-checked
     */
    public static final String MEDIA_CHECKING = "checking";

    /**
     * {@link #getExternalStorageState()} returns MEDIA_NOFS if the media is present
     * but is blank or is using an unsupported filesystem
     */
    public static final String MEDIA_NOFS = "nofs";

    /**
     * {@link #getExternalStorageState()} returns MEDIA_MOUNTED if the media is present
     * and mounted at its mount point with read/write access. 
     */
    public static final String MEDIA_MOUNTED = "mounted";

    /**
     * {@link #getExternalStorageState()} returns MEDIA_MOUNTED_READ_ONLY if the media is present
     * and mounted at its mount point with read only access. 
     */
    public static final String MEDIA_MOUNTED_READ_ONLY = "mounted_ro";

    /**
     * {@link #getExternalStorageState()} returns MEDIA_SHARED if the media is present
     * not mounted, and shared via USB mass storage. 
     */
    public static final String MEDIA_SHARED = "shared";

    /**
     * {@link #getExternalStorageState()} returns MEDIA_BAD_REMOVAL if the media was
     * removed before it was unmounted. 
     */
    public static final String MEDIA_BAD_REMOVAL = "bad_removal";

    /**
     * {@link #getExternalStorageState()} returns MEDIA_UNMOUNTABLE if the media is present
     * but cannot be mounted.  Typically this happens if the file system on the
     * media is corrupted. 
     */
    public static final String MEDIA_UNMOUNTABLE = "unmountable";

    /**
     * Gets the current state of the primary "external" storage device.
     * 
     * @see #getExternalStorageDirectory()
     */
    public static String getExternalStorageState() {
        try {
            IMountService mountService = IMountService.Stub.asInterface(ServiceManager
                    .getService("mount"));
            final StorageVolume primary = getPrimaryVolume();
            String primaryPath = primary.getPath();
            if ((mGpsMapInExt != 0) && mountService.getVolumeState(SDCARD1_PATH).equals(MEDIA_MOUNTED)) {
                primaryPath = SDCARD1_PATH;
            }
            return mountService.getVolumeState(primaryPath);
        } catch (RemoteException rex) {
            Log.w(TAG, "Failed to read external storage state; assuming REMOVED: " + rex);
            return Environment.MEDIA_REMOVED;
        }
    }
	//-----------Jade add function for music player calling----------------------//
	 public static String getStorageState(String strPath) {
        try {
            IMountService mountService = IMountService.Stub.asInterface(ServiceManager
                    .getService("mount"));
            //final StorageVolume primary = getPrimaryVolume();
            return mountService.getVolumeState(strPath);
        } catch (RemoteException rex) {
            Log.w(TAG, "Failed to read external storage state; assuming REMOVED: " + rex);
            return Environment.MEDIA_REMOVED;
        }
    }

    // Begin : Added by lei.ye 2012:7:12
    public static String getExternalSddiskStorageState() {
        try {
            IMountService mountService = IMountService.Stub.asInterface(ServiceManager
                    .getService("mount"));
            return mountService.getVolumeState(getExternalSddiskStorageDirectory()
                    .toString());
        } catch (Exception rex) {
            return Environment.MEDIA_REMOVED;
        }
    }

    public static String getExternalUdiskStorageState() {
        try {
            IMountService mountService = IMountService.Stub.asInterface(ServiceManager
                    .getService("mount"));
            return mountService.getVolumeState(getExternalUdiskStorageDirectory()
                    .toString());
        } catch (Exception rex) {
            return Environment.MEDIA_REMOVED;
        }
    }
    // End : Added by lei.ye 2012:7:12

    /**
     * Returns whether the primary "external" storage device is removable.
     * If true is returned, this device is for example an SD card that the
     * user can remove.  If false is returned, the storage is built into
     * the device and can not be physically removed.
     *
     * <p>See {@link #getExternalStorageDirectory()} for more information.
     */
    public static boolean isExternalStorageRemovable() {
        final StorageVolume primary = getPrimaryVolume();
        return (primary != null && primary.isRemovable());
    }

    /**
     * Returns whether the device has an external storage device which is
     * emulated. If true, the device does not have real external storage, and the directory
     * returned by {@link #getExternalStorageDirectory()} will be allocated using a portion of
     * the internal storage system.
     *
     * <p>Certain system services, such as the package manager, use this
     * to determine where to install an application.
     *
     * <p>Emulated external storage may also be encrypted - see
     * {@link android.app.admin.DevicePolicyManager#setStorageEncryption(
     * android.content.ComponentName, boolean)} for additional details.
     */
    public static boolean isExternalStorageEmulated() {
        final StorageVolume primary = getPrimaryVolume();
        return (primary != null && primary.isEmulated());
    }

    static File getDirectory(String variableName, String defaultPath) {
        String path = System.getenv(variableName);
        return path == null ? new File(defaultPath) : new File(path);
    }

    private static void throwIfSystem() {
        if (Process.myUid() == Process.SYSTEM_UID) {
            Log.wtf(TAG, "Static storage paths aren't available from AID_SYSTEM", new Throwable());
        }
    }

    private static File buildPath(File base, String... segments) {
        File cur = base;
        for (String segment : segments) {
            if (cur == null) {
                cur = new File(segment);
            } else {
                cur = new File(cur, segment);
            }
        }
        return cur;
    }
	
	public static void ClearOSData() throws Exception{
		String[] AppPackageNames = {"com.android.launcher",
			"com.autochips.bluetooth",
			"com.yecon.video",
			"com.yecon.music",
			"com.yecon.carsetting",
			"com.yecon.fmradio",
			"com.yecon.sound.setting",
			"com.yecon.imagebrowser",
			"com.wesail.tdr",
			"com.yecon.sourcemanager" 
		};
        for(String packName : AppPackageNames){
			File directory = new File("/data/data/"
					+ packName + "/shared_prefs");
			if (directory != null && directory.exists() && directory.isDirectory()) {
				if(directory.listFiles() != null){
					for (File item : directory.listFiles()) {
						Log.e(TAG,item.getPath() + "prefs delete");
						item.delete();
					}
				}
			}else{
				Log.e(TAG,packName + " delete prefs is error");
			}
		}
		SystemProperties.set("persist.sys.bluetooth.clear", "true"); 
		SystemProperties.set("persist.sys.current_volume", Context.DEFUALT_VOLUME + "");
		SystemProperties.set("persist.sys.volume_balance_lr", "0");
		SystemProperties.set("persist.sys.volume_balance_fr", "0");
		
		try {	
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = dateFormat.parse("2017-01-01 00:00:00");
			SystemClock.setCurrentTimeMillis(date.getTime());
		} catch (Exception e) {
			Log.e(TAG,e.toString());
		}
    }
	public static void ClearSDMediaData() throws Exception{
		String[] AppPackageNames = {
			"com.yecon.video",
			"com.yecon.music"
		};
		for(String packName : AppPackageNames){
			File directory = new File("/data/data/"
					+ packName + "/shared_prefs");
			if (directory != null && directory.exists() && directory.isDirectory()) {
				if(directory.listFiles() != null){
					for (File item : directory.listFiles()) {
						try{
							if(item.getName().matches("sdcard\\d_MediaType_\\d.xml")){
								item.delete();
								Log.e(TAG,item.getName() + " delete");
							}
						}catch(Exception e){
							Log.e(TAG,e.toString());
						}
					}
				}
			}else{
				Log.e(TAG,packName + " delete SDMedia prefs is error");
			}
		}
	}
	public static void ClearUSBMediaData() throws Exception{
		String[] AppPackageNames = {
			"com.yecon.video",
			"com.yecon.music"
		};
		for(String packName : AppPackageNames){
			File directory = new File("/data/data/"
					+ packName + "/shared_prefs");
			if (directory != null && directory.exists() && directory.isDirectory()) {
				if(directory.listFiles() != null){
					for (File item : directory.listFiles()) {
						try{
							if(item.getName().matches("udisk\\d_MediaType_\\d.xml")){
								item.delete();
								Log.e(TAG,item.getName() + " delete");
							}
						}catch(Exception e){
							Log.e(TAG,e.toString());
						}
					}
				}
			}else{
				Log.e(TAG,packName + " delete SDMedia prefs is error");
			}
		}
	}
	
	public static void ClearContextData(Context context) throws Exception{
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mWifiManager.setWifiEnabled(false);
		File wifiFile = new File("/data4write/wifi/wpa_supplicant.conf");
		if(wifiFile != null && wifiFile.exists()){
			wifiFile.delete();
		}
		final Locale l = new Locale("zh","CN");
		LocalePicker.updateLocale(l);
		SystemProperties.set("persist.sys.hzh.language","zh");
		SystemProperties.set("persist.sys.hzh.country","CN");
		SystemProperties.set("persist.sys.hzh.locale.change","true");
	}
	public static byte[] getInitVolumeData(){
		int volume = Integer.parseInt(SystemProperties.get("persist.sys.current_volume", Context.DEFUALT_VOLUME + ""));
		int lr = SystemProperties.getInt("persist.sys.volume_balance_lr",0);
		int fr = SystemProperties.getInt("persist.sys.volume_balance_fr",0);
		byte[] data = new byte[8];
		data[0] = (byte)volume;		
		data[2] = (byte)lr;
		data[3] = (byte)fr;
		
		if(volume == 0){
			data[4] = 1; //静音
		}else{
			data[4] = 0;//有声
		}
		return data;
	}
	
	public static byte[] getCurrentDateTimeBCD(){
		Date d = new Date();
		int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(d));
		int month = Integer.parseInt(new SimpleDateFormat("MM").format(d));
		int day = Integer.parseInt(new SimpleDateFormat("dd").format(d));
		int hour = Integer.parseInt(new SimpleDateFormat("HH").format(d));
		int minute = Integer.parseInt(new SimpleDateFormat("mm").format(d));
		int second = Integer.parseInt(new SimpleDateFormat("ss").format(d));
		byte[] data = new byte[9];
		data[0] = 0x02;
		data[1] = (byte)(((int)year / 1000) * 16 + ((int)year % 1000 / 100));
		data[2] = (byte)(((int)year  % 100 / 10) * 16 + ((int)(year % 100) % 10));
		data[3] = (byte)(((int)month / 10) * 16 + month % 10);
		data[4] = (byte)(((int)day / 10) * 16 + day % 10);
		data[5] = (byte)(((int)hour / 10) * 16 + hour % 10);
		data[6] = (byte)(((int)minute / 10) * 16 + minute % 10);
		data[7] = (byte)(((int)second / 10) * 16 + second % 10);
		data[8] = 0x00;
		return data;
	}
}
