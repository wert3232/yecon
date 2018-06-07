package nfore.android.bt.servicemanager;

/** 
 * nForeTek A2DP/AVRCP callback interface for Android
 *
 * Copyright (C) 2011-2019  nForeTek Corporation
 *
 * First release on 20120907
 * 
 * @author Bohan	<bohanlu@nforetek.com>
 * @version 20130211
 */



/**
 * The callback interface for A2DP and AVRCP profile.
 * UI program should implement all methods of this interface 
 * for receiving the possible callback from nFore service.
 */
 

interface UiA2dpCallback {

	/* ======================================================================================================================================================== 
	 * Callback function of state changed event to A2DP, AVRCP v1.0_v1.3, and AVRCP v1.4
	 */
	
	/** 
	 * Callback to inform change in A2DP state of remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * The possible values of state in this profile are 
	 * 		<p><blockquote>STATE_NOT_INITIALIZED	(int) 100
	 * 		<br>STATE_READY				(int) 110
	 * 		<br>STATE_CONNECTING		(int) 120
	 *		<br>STATE_CONNECTED			(int) 140
	 *		<br>STATE_STREAMING			(int) 150</blockquote>
	 * The state STATE_STREAMING implies connected and playing.
	 * Parameter address is only valid in state STATE_CONNECTING, STATE_CONNECTED, and STATE_STREAMING.
	 * In state STATE_NOT_INITIALIZED and STATE_READY, parameter address might contain unavailable content or 
	 * DEFAULT_ADDRESS , which is 00:00:00:00:00:00.
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
	 * @param prevState : the previous state. 
	 * The value of this parameter would be the same as newState if the new state is STATE_NOT_INITIALIZED.
	 * @param newState : the new state.
	 */
	void onA2dpStateChanged(String address, int prevState, int newState);

	/** 
	 * Callback to inform change in AVRCP state of remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * The possible values of state in this callback are 
	 * 		<p><blockquote>STATE_NOT_INITIALIZED	(int) 100
	 * 		<br>STATE_READY		(int) 110
	 *		<br>STATE_CONNECTED		(int) 140
 	 *		<br>STATE_BROWSING		(int) 145</blockquote>
	 * The state STATE_BROWSING implies connected. 	 
	 * There might be no need for users to concern for this in general case because we treat A2DP and AVRCP as one service, and
	 * expose only connect/disconnect APIs of A2DP but not AVRCP.
	 * However, if applications want to get more detailed information, it could achieve that by this callback function.
	 * Besides, if applications only requires functionalities of AVRCP on version 1.3 or lower, 
	 * the STATE_CONNECTED is necessary and sufficient for requesting corresponding APIs.
	 * If the functionalities of AVRCP on version 1.4 are needed, the STATE_BROWSING is necessary, otherwise.
	 * Parameter address is only valid in state STATE_CONNECTED, and STATE_BROWSING.
	 * In state STATE_NOT_INITIALIZED and STATE_READY, parameter address might contain unavailable content or 
	 * DEFAULT_ADDRESS , which is 00:00:00:00:00:00.
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
	 * @param prevState : the previous state. 
	 * The value of this parameter would be the same as newState if the new state is STATE_NOT_INITIALIZED.	 
	 * @param newState : the new state.
	 */	
	void onAvrcpStateChanged(String address, int prevState, int newState);
	
	/** 
	 * Callback to inform change in AVRCP v1.4 browsing channel state of remote connected device with given Bluetooth hardware address.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * The possible values of state in this callback are 
	 * 		<p><blockquote>STATE_NOT_INITIALIZED	(int) 100
	 * 		<br>STATE_READY		(int) 110
	 * 		<br>STATE_CONNECTING	(int) 120
	 *		<br>STATE_CONNECTED		(int) 140</blockquote>
	 * There might be no need for users to concern for this in general case because browsing functionality is the extension of AVRCP, and
	 * STATE_BROWSING of {@link IA2dpCallback#onAvrcpStateChanged onAvrcpStateChanged} implies the STATE_CONNECTED of this command. 
	 * However, if applications want to get more detailed information, it could achieve that by this callback function. 
	 * Parameter address is only valid in state STATE_CONNECTING and STATE_CONNECTED.
	 * In state STATE_NOT_INITIALIZED and STATE_READY, parameter address might contain unavailable content or 
	 * DEFAULT_ADDRESS , which is 00:00:00:00:00:00.	 
	 *
	 * @param address : Bluetooth MAC address of remote device which involves state changed.
	 * @param prevState : the previous state. 
	 * The value of this parameter would be the same as newState if the new state is STATE_NOT_INITIALIZED.	 
	 * @param newState : the new state.
	 */		
	void onAvrcpBrowsingStateChanged(String address, int prevState, int newState);
	
	
	/* ======================================================================================================================================================== 
	 * Callback function of AVRCP v1.3 related operations
	 */	
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpGetCapabilitiesSupportEvent reqAvrcpGetCapabilitiesSupportEvent}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 * @param eventIds : the events supported on remote device. Possible values are
	 *		<p><blockquote>AVRCP_EVENT_ID_PLAYBACK_STATUS_CHANGED			(byte) 0x01
	 *		<br>AVRCP_EVENT_ID_TRACK_CHANGED				(byte) 0x02
	 *		<br>AVRCP_EVENT_ID_TRACK_REACHED_END			(byte) 0x03
	 *		<br>AVRCP_EVENT_ID_TRACK_REACHED_START			(byte) 0x04
	 *		<br>AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED			(byte) 0x05
	 *		<br>AVRCP_EVENT_ID_BATT_STATUS_CHANGED			(byte) 0x06
	 *		<br>AVRCP_EVENT_ID_SYSTEM_STATUS_CHANGED			(byte) 0x07
	 *		<br>AVRCP_EVENT_ID_PLAYER_APPLICATION_SETTING_CHANGED	(byte) 0x08
	 *		<br>AVRCP_EVENT_ID_NOW_PLAYING_CONTENT_CHANGED		(byte) 0x09
	 *		<br>AVRCP_EVENT_ID_AVAILABLE_PLAYERS_CHANGED		(byte) 0x0a
	 *		<br>AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED			(byte) 0x0b
	 *		<br>AVRCP_EVENT_ID_UIDS_CHANGED				(byte) 0x0c
	 *		<br>AVRCP_EVENT_ID_VOLUME_CHANGED				(byte) 0x0d</blockquote>
	 */		
	void retAvrcpCapabilitiesSupportEvent(String address, in byte[] eventIds);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpGetPlayerSettingAttributesList reqAvrcpGetPlayerSettingAttributesList}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 * @param attributeIds : the attributes supported on remote device. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE	(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN		(byte) 0x04</blockquote>
	 */		
	void retAvrcpPlayerSettingAttributesList(String address, in byte[] attributeIds);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpGetPlayerSettingValuesList reqAvrcpGetPlayerSettingValuesList}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 * @param attributeId : the attribute to which the valueIds belong. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE	(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN		(byte) 0x04</blockquote>
	 * @param valueIds : the values supported by attributeId on remote device. Possible values are
	 * 		<p><blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_EQUALIZER_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_EQUALIZER_ON	(byte) 0x02</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_REPEAT_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_SINGLE	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_ALL	(byte) 0x03
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_GROUP	(byte) 0x04</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SHUFFLE_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_ALL	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_GROUP	(byte) 0x03</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SCAN</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SCAN_OFF		(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_ALL		(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_GROUP	(byte) 0x03</blockquote></blockquote>
	 */		
	void retAvrcpPlayerSettingValuesList(String address, byte attributeId, in byte[] valueIds);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpGetPlayerSettingCurrentValues reqAvrcpGetPlayerSettingCurrentValues}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * an attribute attributeIds's n-th value is placed at valueIds's n-th element.
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 * @param attributeIds : the attributes to which each valueId of valueIds belongs. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE	(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN		(byte) 0x04</blockquote>
	 * @param valueIds : the current values of attributeId on remote device. Possible values are
	 * 		<p><blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_EQUALIZER_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_EQUALIZER_ON	(byte) 0x02</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_REPEAT_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_SINGLE	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_ALL	(byte) 0x03
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_GROUP	(byte) 0x04</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SHUFFLE_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_ALL	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_GROUP	(byte) 0x03</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SCAN</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SCAN_OFF		(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_ALL		(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_GROUP	(byte) 0x03</blockquote></blockquote>
	 */	
	void retAvrcpPlayerSettingCurrentValues(String address, in byte[] attributeIds, in byte[] valueIds);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpSetPlayerSettingValue reqAvrcpSetPlayerSettingValue}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * This means that the previously requested command {@link INforeService#reqAvrcpSetPlayerSettingValue reqAvrcpSetPlayerSettingValue}
	 * is accepted successfully by remote device.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 */		
	void retAvrcpPlayerSettingValueSet(String address);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpGetElementAttributesPlaying reqAvrcpGetElementAttributesPlaying}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
 	 * @param metadataAtrributeIds : list of media attributes IDs. These IDs are used to uniquely identify media information. Possible values are
 	 *		<p><blockquote>AVRCP_META_ATTRIBUTE_ID_TITLE		(int) 0x01 : title of the media
     *		<br>AVRCP_META_ATTRIBUTE_ID_ARTIST 					(int) 0x02 : name of the artist
     *		<br>AVRCP_META_ATTRIBUTE_ID_ALBUM 					(int) 0x03 : name of the album
     *		<br>AVRCP_META_ATTRIBUTE_ID_NUMBER_OF_MEDIA 		(int) 0x04 : number of the media, i.e. track number of the CD
     *		<br>AVRCP_META_ATTRIBUTE_ID_TOTAL_NUMBER_OF_MEDIA 	(int) 0x05 : total number of the media, i.e. Total track number of the CD
     *		<br>AVRCP_META_ATTRIBUTE_ID_GENRE 					(int) 0x06 : genre
     *		<br>AVRCP_META_ATTRIBUTE_ID_SONG_LENGTH 			(int) 0x07 : playing time in milliseconds</blockquote>
  	 * @param texts : the result of string type to corresponding metadata attribute ID.
	 */			
	void retAvrcpElementAttributesPlaying(String address, in int[] metadataAtrributeIds, in String[] texts);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpGetPlayStatus reqAvrcpGetPlayStatus}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
 	 * @param songLen : the total length of the playing song in milliseconds. Values allowed are from 0 to (2^32 - 1).
  	 * @param songPos : the current position of the playing in milliseconds elapsed. Values allowed are from 0 to (2^32 - 1).
  	 * @param statusId : the current status of playing. Possible values are
  	 *		<p><blockquote>AVRCP_PLAYING_STATUS_ID_STOPPED 	(byte) 0x00 : stopped
	 *		<br>AVRCP_PLAYING_STATUS_ID_PLAYING 	(byte) 0x01 : playing
	 *		<br>AVRCP_PLAYING_STATUS_ID_PAUSED 		(byte) 0x02 : paused
	 *		<br>AVRCP_PLAYING_STATUS_ID_FWD_SEEK 	(byte) 0x03 : fwd seek
	 *		<br>AVRCP_PLAYING_STATUS_ID_REW_SEEK 	(byte) 0x04 : rev seek
	 *		<br>AVRCP_PLAYING_STATUS_ID_ERROR 		(byte) 0xFF : error</blockquote> 	 
	 */		
	void retAvrcpPlayStatus(String address, long songLen, long songPos, byte statusId);
	
	/** 
	 * Callback to inform change in playback status of remote connected device with given Bluetooth hardware address.
	 * This callback would be invoked only if event AVRCP_EVENT_ID_PLAYBACK_STATUS_CHANGED was registered before by using
	 * {@link INforeService#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
  	 * @param statusId : the current status of playback. Possible values are
  	 *		<p><blockquote>AVRCP_PLAYING_STATUS_ID_STOPPED 	(byte) 0x00 : stopped
	 *		<br>AVRCP_PLAYING_STATUS_ID_PLAYING 	(byte) 0x01 : playing
	 *		<br>AVRCP_PLAYING_STATUS_ID_PAUSED 		(byte) 0x02 : paused
	 *		<br>AVRCP_PLAYING_STATUS_ID_FWD_SEEK 	(byte) 0x03 : fwd seek
	 *		<br>AVRCP_PLAYING_STATUS_ID_REW_SEEK 	(byte) 0x04 : rev seek
	 *		<br>AVRCP_PLAYING_STATUS_ID_ERROR 		(byte) 0xFF : error</blockquote>  	 
	 */		
	void onAvrcpEventPlaybackStatusChanged(String address, byte statusId);

	/** 
	 * Callback to inform change in track of remote connected device with given Bluetooth hardware address.
	 * This callback would be invoked only if event AVRCP_EVENT_ID_TRACK_CHANGED was registered before by using
	 * {@link INforeService#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
  	 * @param statusId : the index of current track. 	 
	 */		
	void onAvrcpEventTrackChanged(String address, long elementId);

	/** 
	 * Callback to inform end of the current track of remote connected device with given Bluetooth hardware address.
	 * This callback would be invoked only if event AVRCP_EVENT_ID_TRACK_REACHED_END was registered before by using
	 * {@link INforeService#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.	 
	 */		
	void onAvrcpEventTrackReachedEnd(String address);
	
	/** 
	 * Callback to inform start of the current track of remote connected device with given Bluetooth hardware address.
	 * This callback would be invoked only if event AVRCP_EVENT_ID_TRACK_REACHED_START was registered before by using
	 * {@link INforeService#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.	 
	 */		
	void onAvrcpEventTrackReachedStart(String address);

	/** 
	 * Callback to inform change in playback position of remote connected device with given Bluetooth hardware address.
	 * This callback would be invoked only if event AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED was registered before by using
	 * {@link INforeService#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 * Besides, this callback shall be notified in the following conditions:
	 * 		<p><blockquote>remote device has reached the registered playback interval time
	 * 		<br>change in play status.
	 * 		<br>change in current track.
	 *		<br>reached end or beginning of track.	 
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".</blockquote>
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
 	 * @param songPos : current playback position in millisecond.	 
	 */			
	void onAvrcpEventPlaybackPosChanged(String address, long songPos);

	/** 
	 * Callback to inform change in battery status of remote connected device with given Bluetooth hardware address.
	 * This callback would be invoked only if event AVRCP_EVENT_ID_BATT_STATUS_CHANGED was registered before by using
	 * {@link INforeService#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
 	 * @param statusId : battery status. possible values are
	 * 		<p><blockquote>AVRCP_BATTERY_STATUS_ID_NORMAL		(byte) 0x00 : normal 	 
 	 *		<br>AVRCP_BATTERY_STATUS_ID_WARNING 	(byte) 0x01 : warning
	 *      <br>AVRCP_BATTERY_STATUS_ID_CRITICAL 	(byte) 0x02 : critical
	 *      <br>AVRCP_BATTERY_STATUS_ID_EXTERNAL 	(byte) 0x03 : external
	 *      <br>AVRCP_BATTERY_STATUS_ID_FULL 		(byte) 0x04 : full charged</blockquote> 	 
	 */		
	void onAvrcpEventBatteryStatusChanged(String address, byte statusId);

	/** 
	 * Callback to inform change in system status of remote connected device with given Bluetooth hardware address.
	 * This callback would be invoked only if event AVRCP_EVENT_ID_SYSTEM_STATUS_CHANGED was registered before by using
	 * {@link INforeService#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
 	 * @param statusId : system status. possible values are
	 *		<p><blockquote>AVRCP_SYSTEM_STATUS_ID_POWER_ON 		(byte) 0x00 : power on
	 *      <br>AVRCP_SYSTEM_STATUS_ID_POWER_OFF 		(byte) 0x01 : power off
	 *      <br>AVRCP_SYSTEM_STATUS_ID_POWER_UNPLUGGED 	(byte) 0x02 : unplugged</blockquote>	 
	 */		
	void onAvrcpEventSystemStatusChanged(String address, byte statusId);

	/** 
	 * Callback to inform change in player application settings of remote connected device with given Bluetooth hardware address.
	 * This callback would be invoked only if event AVRCP_EVENT_ID_PLAYER_APPLICATION_SETTING_CHANGED was registered before by using
	 * {@link INforeService#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 * @param attributeIds : the attributes to which each valueId of valueIds belongs. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE		(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN			(byte) 0x04</blockquote>
	 * @param valueIds : the current values of attributeId on remote device. Possible values are
	 * 		<p><blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_EQUALIZER_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_EQUALIZER_ON	(byte) 0x02</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_REPEAT_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_SINGLE	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_ALL	(byte) 0x03
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_GROUP	(byte) 0x04</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SHUFFLE_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_ALL	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_GROUP	(byte) 0x03</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SCAN</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SCAN_OFF		(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_ALL		(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_GROUP	(byte) 0x03</blockquote></blockquote>
	 */		
	void onAvrcpEventPlayerSettingChanged(String address, in byte[] attributeIds, in byte[] valueIds);	

	/** 
	 * Callback to inform that continuing response has been aborted on remote connected device with given Bluetooth hardware address.
	 * This callback would be invoked only if we have called {@link INforeService#reqAvrcpAbortMetadataReceiving reqAvrcpAbortMetadataReceiving}.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 * @param opId : the target PDU_ID for abort continue command. Possible values are
	 * 		<p><blockquote>REQ_AVRCP_CT_GET_CAPABILITIES						(short) 0x10
	 *		<br>REQ_AVRCP_CT_LIST_PLAYER_APP_SETTING_ATTRIBUTES		(short) 0x11
	 *		<br>REQ_AVRCP_CT_LIST_PLAYER_APP_SETTING_VALUES			(short) 0x12
	 *		<br>REQ_AVRCP_CT_GET_CURRENT_PLAYER_APP_SETTING_VALUE	(short) 0x13 
	 *		<br>REQ_AVRCP_CT_SET_PLAYER_APP_SETTING_VALUE			(short) 0x14
	 *		<br>REQ_AVRCP_CT_GET_ELEMENT_ATTRIBUTES_PLAYING			(short) 0x20	 	 
	 *		<br>REQ_AVRCP_CT_GET_PLAY_STATUS						(short) 0x30	 		
	 *<br>
	 *		<br>REQ_AVRCP_CT_GET_PLAYER_APP_SETTING_ATTRIBUTE_TEXT	(short) 0x15
	 *		<br>REQ_AVRCP_CT_GET_PLAYER_APP_SETTING_VALUE_TEXT		(short) 0x16
	 *		<br>REQ_AVRCP_CT_INFORM_DISPLAYABLE_CHARSET				(short) 0x17
	 *		<br>REQ_AVRCP_CT_INFORM_BATTERY_STATUS_OF_CT			(short) 0x18
	 *<br>
	 *		<br>REQ_AVRCP_CT_SET_ADDRESSED_PLAYER					(short) 0x60
	 *		<br>REQ_AVRCP_CT_PLAY_ITEM								(short) 0x74 
	 *		<br>REQ_AVRCP_CT_ADD_TO_NOW_PLAYING						(short) 0x90
	 *<br>	 
	 *		<br>REQ_AVRCP_CT_SET_ABSOLUTE_VOLUME					(short) 0x50</blockquote>	 
	 */		
	void retAvrcpMetadataReceivingAborted(String address, int opId);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpGetPlayerSettingAttributeText reqAvrcpGetPlayerSettingAttributeText}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * In this version, we only support UTF-8 character set.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 * @param cause : callback cause. Possible values are
	 * 		<p><blockquote>AVRCP_CAUSE_NO_ENTRY		(int) 0	: no entries got (so path, attr_id, charset_id, text are dummy).
 	 * 		<br>AVRCP_CAUSE_NEXT_ENTRY		(int) 1 : it have next entry, will be called continuously.
 	 *		<br>AVRCP_CAUSE_LAST_ENTRY		(int) 2 : this is last entry (have no next entries).</blockquote>
	 * @param attributeId : the attributes id on request. Possible values are
	 * 		<p><blockquote>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER	(byte) 0x01
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE	(byte) 0x02
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE		(byte) 0x03
	 *		<br>AVRCP_SETTING_ATTRIBUTE_ID_SCAN			(byte) 0x04</blockquote>	 
	 * @param text : the player application setting attribute string in character set UTF-8. It is not terminated by NUL.
	 */	
	void retAvrcpPlayerSettingAttributeText(String address, int cause, byte attributeId, in byte[] text);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpGetPlayerSettingValueText reqAvrcpGetPlayerSettingValueText}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * In this version, we only support UTF-8 character set.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 * @param cause : callback cause. Possible values are
	 * 		<p><blockquote>AVRCP_CAUSE_NO_ENTRY		(int) 0	: no entries got (so path, attr_id, charset_id, text are dummy).
 	 * 		<br>AVRCP_CAUSE_NEXT_ENTRY		(int) 1 : it have next entry, will be called continuously.
 	 *		<br>AVRCP_CAUSE_LAST_ENTRY		(int) 2 : this is last entry (have no next entries).</blockquote>
	 * @param valueId : the value id on request. Possible values are
	 * 		<p><blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_EQUALIZER_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_EQUALIZER_ON	(byte) 0x02</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_REPEAT_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_SINGLE	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_ALL	(byte) 0x03
	 *			<br>AVRCP_SETTING_VALUE_ID_REPEAT_GROUP	(byte) 0x04</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SHUFFLE_OFF	(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_ALL	(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SHUFFLE_GROUP	(byte) 0x03</blockquote></blockquote>
	 *		<blockquote>For AVRCP_SETTING_ATTRIBUTE_ID_SCAN</blockquote>
	 *			<blockquote><blockquote>AVRCP_SETTING_VALUE_ID_SCAN_OFF		(byte) 0x01
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_ALL		(byte) 0x02
	 *			<br>AVRCP_SETTING_VALUE_ID_SCAN_GROUP	(byte) 0x03</blockquote></blockquote>
	 * @param text : the player application setting attribute string in character set UTF-8. It is not terminated by NUL.
	 */		
	void retAvrcpPlayerSettingValueText(String address, int cause, byte valueId, in byte[] text);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpInformDisplayableCharset reqAvrcpInformDisplayableCharset}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * In this version, we only support UTF-8 character set.
	 * This means that the previously requested command {@link INforeService#reqAvrcpInformDisplayableCharset reqAvrcpInformDisplayableCharset}
	 * is accepted successfully by remote device.	 
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 */		
	void retAvrcpDisplayableCharsetInformed(String address);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpInformBatteryStatusOfCt reqAvrcpInformBatteryStatusOfCt}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * This means that the previously requested command {@link INforeService#reqAvrcpInformBatteryStatusOfCt reqAvrcpInformBatteryStatusOfCt}
	 * is accepted successfully by remote device.	 
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 */		
	void retAvrcpBatteryStatusOfCtInformed(String address);
	
	
	/* ======================================================================================================================================================== 
	 * Callback function of AVRCP v1.4 related operations
	 */	
	 
	/** 
	 * Callback to inform change in the content of the NowPlaying folder for the AddressedPlayer 
	 * on remote connected device with given Bluetooth hardware address.
	 * The notification should not be completed if only the track has changed or the order of the tracks on the now playing list has changed.
	 * This callback would be invoked only if event AVRCP_EVENT_ID_NOW_PLAYING_CONTENT_CHANGED was registered before by using
	 * {@link INforeService#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * This notification is only available on v1.4.
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 */		
	void onAvrcpEventNowPlayingContentChanged(String address);
	
	/** 
	 * Callback to inform change in the available player on remote connected device with given Bluetooth hardware address.
	 * That is if a new player becomes available to be addressed (for instance started, or installed) or if a player ceases to be available.
	 * This callback would be invoked only if event AVRCP_EVENT_ID_AVAILABLE_PLAYERS_CHANGED was registered before by using
	 * {@link INforeService#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * This notification is only available on v1.4.
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 */			
	void onAvrcpEventAvailablePlayerChanged(String address);

	/** 
	 * Callback to inform change in the AddressedPlayer on remote connected device with given Bluetooth hardware address.
	 * The interim response to registration of AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED shall contain the PlayerId of the current AddressedPlayer. 
	 * If the registration of AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED was done before sending a 
	 * {@link INforeService#reqAvrcpSetAddressedPlayer reqAvrcpSetAddressedPlayer} command,
	 * the interim response contains the PlayerId of the default player on the remote device.
	 * This callback would be invoked only if event AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED was registered before by using
	 * {@link INforeService#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * This notification is only available on v1.4.
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 * @param playerId : the PlayerId of the AddressedPlayer.
 	 * @param uidCounter : UID counter.
	 */	
	void onAvrcpEventAddressedPlayerChanged(String address, int playerId, int uidCounter);
	
	/** 
	 * Callback to inform change in UIDs on remote connected device with given Bluetooth hardware address.
	 * A database unaware player may accept and complete UIDs changed notifications as it may be able to detect some changes to the available media. 
	 * However it should be noted that the UID counter value shall always be 0 in this case.
	 * Note that to refresh UID information after having received this callback, the Media Player Virtual Filesystem may be browsed.
	 * This callback would be invoked only if event AVRCP_EVENT_ID_UIDS_CHANGED was registered before by using
	 * {@link INforeService#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * This notification is only available on v1.4.
	 *<br>
	 * Indtroduction to UID.
	 * <br>Media elements are identified within the virtual filesystem by an 8 octet identifier, the UID. 
	 * The UID shall be unique within a scope with the exception of the Virtual Media Player Filesystem on database unaware media players.
	 * Within the Virtual Media Player Filesystem a UID uniquely identifies a Media Element Item. 
	 * If the same Media Element Item is present at more than one location in the Virtual Media Player Filesystem, 
	 * then it MAY have the same UID in each location. Within the NowPlaying list a UID uniquely identifies a Media Element Item. 
	 * If the same Media Element Item is present at more than one location in the NowPlaying list each instance shall have a different UID.
	 * The value of UID=0x0 is a special value used only to request the metadata for the currently playing media using the 
	 * {@link INforeService#reqAvrcpGetElementAttributesPlaying reqAvrcpGetElementAttributesPlaying} command and shall not be used for any item in a folder.
	 * The UID shall be used whenever a media element is required to be identified. For example it may be used in conjunction with the 
	 * {@link INforeService#reqAvrcpGetItemAttributes reqAvrcpGetItemAttributes} command to obtain metadata information about a specific media element, or 
	 * in combination with ChangePath to change to a specific folder. 
	 * The UID scope is limited to one player, even if that player may have unique UIDs across its virtual filesystem. 
	 * For example a media element with the UID 1 in the folder hierarchy for player 1 may be a different media element to that 
	 * with the UID 1 in the folder hierarchy for player 2. 
	 * There are two different ways a Media Player on remote device may handle the UID, depending on the nature of the Media Player: 
	 * Database Aware (with UID change detection) and Database Unaware (without UID change detection). 
	 * UID handling is specific to a player, and different players on the same remote device may behave in different ways.
	 * Database aware players shall maintain a UID counter that is incremented whenever the database changes.
	 * <br>
	 * Database Unaware Players (Without UID change detection) :
	 * <br>Some media players may not have the ability to detect when UIDs cease to be valid. For these the scope in the media browsing tree is limited 
	 * to one folder at a time, that is when the current path is changed all current UIDs cease to be valid. Therefore, user shall always update its UID information 
	 * after each change of path and not rely on any UIDs stored from previous visits to a folder. Database Unaware Players shall always return UIDcounter=0, and
	 * may use the this callback to indicate changes to the Media Database.
	 *<br>
	 * Database Aware Players (With UID change detection) :
	 * <br>Database Aware players are aware of any change to their Media database. They shall ensure the UID is unique across the entire media browsing tree. 
	 * Any change to the Media Database shall result in an increase of the UIDcounter and this callback.
	 * The unique identifier may persist between AVRCP Browse Reconnects, but is not required to do so. However the remote device would try to persist the UIDs 
	 * for as long as possible and should only change them when there is a change in available media content. 
	 * An AVRCP Browse Reconnect occurs whenever the browsed player is switched with the {@link INforeService#reqAvrcpSetBrowsedPlayer reqAvrcpSetBrowsedPlayer} command. 
	 * This may be the result of the user application changing the browsed player, or may be as a result of the underlying AVCTP browsing channel 
	 * having been released and established again. 
	 * The UID counter shall be incremented every time the remote device makes an update which invalidates existing UIDs, skipping the value 0. 
	 * The remote device would ensure that the amount by which the counter is incremented is small, to avoid the counter wrapping frequently. 
	 * The initial value of the UID counter, including the situation when the UID counter is not persisted between AVRCP Browse Reconnects, 
	 * would be a random value between 1 and 65535. 
	 * If remote device has a UID counter value not equal to the UID counter value on the user application 
	 * then any UIDs cached on the user application are invalid. Any UID dependant information cached on the user application is therefore invalid.
	 * Remote device would keep UIDs as persistent as possible, to avoid situations such as the user application retrieving a folder listing, then the UIDs becoming invalid 
	 * before the user application performs an operation. Only circumstances such as a change of available media (eg insertion of memory card) or 
	 * local error conditions (eg out of memory) should cause remote device to regenerate UIDs. 
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
 	 * @param uidCounter : UID Counter.
	 */		
	void onAvrcpEventUidsChanged(String address, int uidCounter);	 
	
	/** 
	 * Callback to inform change in the volume on remote connected device with given Bluetooth hardware address, or 
	 * what the actual volume level is following use of relative volume commands.
	 * This callback would be invoked only if event AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED was registered before by using
	 * {@link INforeService#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 * This notification is only available on v1.4.
	 *
	 * @attention : This callback shall not be completed when the user application changes the volume remotely with the {@link INforeService#reqAvrcpSetAbsoluteVolume reqAvrcpSetAbsoluteVolume} command. 
	 * It is expected for this command that the audio sink fulfils the TG role.
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 * @param playerId : the PlayerId of the AddressedPlayer.
 	 * @param uidCounter : UID Counter.
	 */		 	
	void onAvrcpEventVolumeChanged(String address, byte volume);	 	
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpSetAddressedPlayer reqAvrcpSetAddressedPlayer}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * This means that the previously requested command {@link INforeService#reqAvrcpSetAddressedPlayer reqAvrcpSetAddressedPlayer}
	 * is accepted successfully by remote device.	 
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 */			 	
	void retAvrcpAddressedPlayerSet(String address);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpSetBrowsedPlayer reqAvrcpSetBrowsedPlayer}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * This means that the previously requested command {@link INforeService#reqAvrcpSetBrowsedPlayer reqAvrcpSetBrowsedPlayer}
	 * is accepted successfully by remote device.	 
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 * @param uidCounter : UID counter.
 	 * @param itemCount : the number of items in the current folder.
	 */		
	void retAvrcpBrowsedPlayerSet(String address, int uidCounter, long itemCount);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpGetFolderItems reqAvrcpGetFolderItems}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * This means that the previously requested command {@link INforeService#reqAvrcpGetFolderItems reqAvrcpGetFolderItems}
	 * is accepted successfully by remote device.
	 * Besides, system would first invocate this callback with parameter itemCount which is equal to 0
	 * if previous {@link INforeService#reqAvrcpGetFolderItems reqAvrcpGetFolderItems} was accepted successfully, and 
	 * system would invocate this again with parameter itemCount which is the real number of items after 
	 * attributes of all items in this folder have been received. 
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 * @param uidCounter : UID counter.
 	 * @param itemCount : the number of items in the current folder.
	 */		
	void retAvrcpFolderItems(String address, int uidCounter, long itemCount);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpChangePath reqAvrcpChangePath}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * This means that the previously requested command {@link INforeService#reqAvrcpChangePath reqAvrcpChangePath}
	 * is accepted successfully by remote device.	 
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
 	 * @param itemCount : the number of items in the current folder.
	 */		
	void retAvrcpPathChanged(String address, long itemCount);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpGetItemAttributes reqAvrcpGetItemAttributes}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
 	 * @param metadataAtrributeIds : list of media attributes IDs. These IDs are used to uniquely identify media information. Possible values are
 	 *		<p><blockquote>AVRCP_META_ATTRIBUTE_ID_TITLE 					(int) 0x01 : title of the media
     *		<br>AVRCP_META_ATTRIBUTE_ID_ARTIST 					(int) 0x02 : name of the artist
     *		<br>AVRCP_META_ATTRIBUTE_ID_ALBUM 					(int) 0x03 : name of the album
     *		<br>AVRCP_META_ATTRIBUTE_ID_NUMBER_OF_MEDIA 		(int) 0x04 : number of the media, i.e. track number of the CD
     *		<br>AVRCP_META_ATTRIBUTE_ID_TOTAL_NUMBER_OF_MEDIA 	(int) 0x05 : total number of the media, i.e. Total track number of the CD
     *		<br>AVRCP_META_ATTRIBUTE_ID_GENRE 					(int) 0x06 : genre
     *		<br>AVRCP_META_ATTRIBUTE_ID_SONG_LENGTH 			(int) 0x07 : playing time in milliseconds</blockquote>
  	 * @param texts : the result of string type to corresponding metadata attribute ID.
	 */	
	void retAvrcpItemAttributes(String address, in int[] metadataAtrributeIds, in String[] texts);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpPlaySelectedItem reqAvrcpPlaySelectedItem}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * This means that the previously requested command {@link INforeService#reqAvrcpPlaySelectedItem reqAvrcpPlaySelectedItem}
	 * is accepted successfully by remote device.	 
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 */			
	void retAvrcpSelectedItemPlayed(String address);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpSearch reqAvrcpSearch}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * This means that the previously requested command {@link INforeService#reqAvrcpSearch reqAvrcpSearch}
	 * is accepted successfully by remote device.	 
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 * @param uidCounter : UID counter.
 	 * @param itemCount : the number of items in the current folder.
	 */		
	void retAvrcpSearchResult(String address, int uidCounter, long itemCount);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpAddToNowPlaying reqAvrcpAddToNowPlaying}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * This means that the previously requested command {@link INforeService#reqAvrcpAddToNowPlaying reqAvrcpAddToNowPlaying}
	 * is accepted successfully by remote device.	 
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 */		
	void retAvrcpNowPlayingAdded(String address);
	
	/** 
	 * Callback to inform response to {@link INforeService#reqAvrcpSetAbsoluteVolume reqAvrcpSetAbsoluteVolume}
	 * from remote connected device with given Bluetooth hardware address has received.
	 * This means that the previously requested command {@link INforeService#reqAvrcpSetAbsoluteVolume reqAvrcpSetAbsoluteVolume}
	 * is accepted successfully by remote device.	 
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 * @param volume : volume which has actually been set.	 
	 */		
	void retAvrcpAbsoluteVolumeSet(String address, byte volume);
	
	/** 
	 * Callback to inform the error in previously requested command specified by parameter opId with given Bluetooth hardware address.
	 * This means there is an error in the previously requested command, and the possible reason is specified by parameter reason.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @attention : the parameter reason is RESERVED and contains default AVRCP_ERROR_CODE_INVALID_COMMAND or AVRCP_ERROR_CODE_BUSY value 
	 * in this version. Besides, not all operation errors 
	 * would send this error response. It depends on what operation we requested. Only the operations listed below would have error response.
	 * @param address : Bluetooth MAC address of remote device which involves callback.
	 * @param opId : the operation which causes the error response callback. Possible values are
	 * 		<p><blockquote>REQ_AVRCP_CT_GET_CAPABILITIES						(short) 0x10
	 *		<br>REQ_AVRCP_CT_LIST_PLAYER_APP_SETTING_ATTRIBUTES		(short) 0x11
	 *		<br>REQ_AVRCP_CT_LIST_PLAYER_APP_SETTING_VALUES			(short) 0x12
	 *		<br>REQ_AVRCP_CT_GET_CURRENT_PLAYER_APP_SETTING_VALUE	(short) 0x13 
	 *		<br>REQ_AVRCP_CT_SET_PLAYER_APP_SETTING_VALUE			(short) 0x14
	 *		<br>REQ_AVRCP_CT_GET_ELEMENT_ATTRIBUTES_PLAYING			(short) 0x20	 	 
	 *		<br>REQ_AVRCP_CT_GET_PLAY_STATUS						(short) 0x30
 	 *		<br>REQ_AVRCP_CT_REGISTER_EVENT_WATCHER					(short) 0x31
 	 *		<br>REQ_AVRCP_CT_UNREGISTER_EVENT_WATCHER				(short) 0x100	The value of PDU ID used in AVRCP v1.4 is from 0x00 to 0xff of octet. 	 		
	 *		<br>REQ_AVRCP_CT_ABORT_METADATA_RECEIVING				(short) 0x41
	 *<br>
	 *		<br>REQ_AVRCP_CT_GET_PLAYER_APP_SETTING_ATTRIBUTE_TEXT	(short) 0x15
	 *		<br>REQ_AVRCP_CT_GET_PLAYER_APP_SETTING_VALUE_TEXT		(short) 0x16
	 *		<br>REQ_AVRCP_CT_INFORM_DISPLAYABLE_CHARSET				(short) 0x17
	 *		<br>REQ_AVRCP_CT_INFORM_BATTERY_STATUS_OF_CT			(short) 0x18
	 *<br>
	 *		<br>REQ_AVRCP_CT_SET_ADDRESSED_PLAYER					(short) 0x60
	 *		<br>REQ_AVRCP_CT_SET_BROWSED_PLAYER						(short) 0x70
	 *		<br>REQ_AVRCP_CT_GET_FOLDER_ITEMS						(short) 0x71	 	 
	 *		<br>REQ_AVRCP_CT_CHANGE_PATH							(short) 0x72
	 *		<br>REQ_AVRCP_CT_GET_ITEM_ATTRIBUTES					(short) 0x73
	 *		<br>REQ_AVRCP_CT_PLAY_ITEM								(short) 0x74
	 *		<br>REQ_AVRCP_CT_SEARCH									(short) 0x80	 
	 *		<br>REQ_AVRCP_CT_ADD_TO_NOW_PLAYING						(short) 0x90
	 *<br>	 
	 *		<br>REQ_AVRCP_CT_SET_ABSOLUTE_VOLUME					(short) 0x50</blockquote>
	 *
	 * @param reason : the possible reason of error. We only support the following two reansons in this version.
	 * 		<p><blockquote>AVRCP_ERROR_CODE_INVALID_COMMAND					(short) 0x00
	 *		<br>AVRCP_ERROR_CODE_BUSY								(short) 0x100	The value of error code used in AVRCP v1.4 is from 0x00 to 0xff of octet.</blockquote>
	 * @param eventId : the event ID of {@link INforeService#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher} which failed to register. 
	 * This parameter is ONLY valid when the opId is REQ_AVRCP_CT_REGISTER_EVENT_WATCHER. In other cases, it would have value of 0xFF. 
	 * Possible values are
	 *		<p><blockquote>AVRCP_EVENT_ID_PLAYBACK_STATUS_CHANGED				(byte) 0x01
	 *		<br>AVRCP_EVENT_ID_TRACK_CHANGED						(byte) 0x02
	 *		<br>AVRCP_EVENT_ID_TRACK_REACHED_END					(byte) 0x03
	 *		<br>AVRCP_EVENT_ID_TRACK_REACHED_START					(byte) 0x04
	 *		<br>AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED					(byte) 0x05
	 *		<br>AVRCP_EVENT_ID_BATT_STATUS_CHANGED					(byte) 0x06
	 *		<br>AVRCP_EVENT_ID_SYSTEM_STATUS_CHANGED				(byte) 0x07
	 *		<br>AVRCP_EVENT_ID_PLAYER_APPLICATION_SETTING_CHANGED	(byte) 0x08
	 *		<br>AVRCP_EVENT_ID_NOW_PLAYING_CONTENT_CHANGED			(byte) 0x09
	 *		<br>AVRCP_EVENT_ID_AVAILABLE_PLAYERS_CHANGED			(byte) 0x0a
	 *		<br>AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED				(byte) 0x0b
	 *		<br>AVRCP_EVENT_ID_UIDS_CHANGED							(byte) 0x0c
	 *		<br>AVRCP_EVENT_ID_VOLUME_CHANGED						(byte) 0x0d	</blockquote>
	 */	
	void onAvrcpErrorResponse(String address, int opId, int reason, byte eventId);


	/**
	 * return song status 
	 * include artist , album , title
	 * data maybe empty string
	 */

	 void retAvrcpUpdateSongStatus(String artist , String album , String title);

}
