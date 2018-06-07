package com.autochips.bluetooth.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.os.SystemProperties;
import android.util.Log;
import android.util.Xml;

public class XmlRW {

	public final static String TAG = "xml_RW";

	// private static String mXmlPath =
	// Environment.getExternalStorageDirectory().getPath()
	// + "/factoryconfig.xml";
	static FileInputStream inputStream = null;
	static XmlPullParser parser = null;
	static XmlParse XmlParse = new XmlParse();
	static XmlSerializer serializer = Xml.newSerializer();
	static int gain_index = 0;
	static int rgb_index = 0;

	public XmlRW() {
		try {
			XmlParse = new XmlParse();
			serializer = Xml.newSerializer();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "create xml reader failed");
		}
	}

	public static void readXMLFile(String filePath) {
		File f = new File(filePath);
		if (!f.exists()) {
			return;
		}

		try {
			inputStream = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			return;
		}

		parser = Xml.newPullParser();
		try {

			parser.setInput(inputStream, "UTF-8");
			int eventType = -1;
			eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					read_data();
					break;
				case XmlPullParser.END_TAG: {
				}
					break;
				case XmlPullParser.TEXT:
					break;
				default:
					break;
				}

				eventType = parser.next();
			}
			inputStream.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public static String writeXMLFile(String filePath) {
		File newxmlfile = new File(filePath);
		try {
			if (!newxmlfile.exists())
				newxmlfile.createNewFile();
		} catch (IOException e) {
			Log.e("IOException", "exception in createNewFile() method");
		}
		FileOutputStream fileos = null;
		try {
			fileos = new FileOutputStream(newxmlfile);
		} catch (FileNotFoundException e) {
			Log.e("FileNotFoundException", "can't create FileOutputStream");
		}

		// XmlSerializer serializer = Xml.newSerializer();
		try {
			// we set the FileOutputStream as output for the serializer, using
			// UTF-8 encoding
			serializer.setOutput(fileos, "UTF-8"); // Write
			// <?xml declaration with encoding (if encoding not null) and
			// standalone flag (if standalone not null)
			serializer.startDocument(null, Boolean.valueOf(true));
			// set indentation option
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			// 閿熸枻鎷蜂竴閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷蜂负閿熸枻鎷烽敓鏂ゆ嫹鍗犻敓锟介敓鏂ゆ嫹閿熺粸鐧告嫹閿熸枻鎷烽敓鏂ゆ嫹閿熺Ц纭锋嫹,閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹涓簄ull
			serializer.startTag("", "XML-parse");

			write_data();

			serializer.endTag("", "XML-parse");
			serializer.endDocument();
			// write xml data into the FileOutputStream
			serializer.flush();
			// finally we close the file stream
			fileos.close();
			return fileos.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void read_timezone() throws XmlPullParserException {
		String tagName = parser.getName();

		if (tagName != null && tagName.equals("timezone")) {
			String string = parser.getAttributeValue(null, "default");
			XmlParse.default_timezone = string;
		}
	}

	private static void write_timezone() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "timezone");
		serializer.attribute(null, "default", XmlParse.default_timezone);
		serializer.endTag("", "timezone");
	}

	private static void read_language() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("Language")) {
			String string = parser.getAttributeValue(null, "default");
			XmlParse.default_language = string;
			string = parser.getAttributeValue(null, "support");
			XmlParse.support_language = string;
		}
	}

	private static void write_language() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "Language");
		serializer.attribute(null, "default", XmlParse.default_language);
		serializer.attribute(null, "support", XmlParse.support_language);
		serializer.endTag("", "Language");
	}

	private static void read_fun() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("funs")) {
			String string = parser.getAttributeValue(null, "dvd");
			XmlParse.fun_dvd_enable = Integer.parseInt(string);

			string = parser.getAttributeValue(null, "fm");
			XmlParse.fun_fm_enable = Integer.parseInt(string);

			string = parser.getAttributeValue(null, "gps");
			XmlParse.fun_gps_enable = Integer.parseInt(string);

			string = parser.getAttributeValue(null, "avin");
			XmlParse.fun_avin_enable = Integer.parseInt(string);
			
			string = parser.getAttributeValue(null, "avin2");
			XmlParse.fun_avin2_enable = Integer.parseInt(string);

			string = parser.getAttributeValue(null, "tv");
			XmlParse.fun_tv_enable = Integer.parseInt(string);

			string = parser.getAttributeValue(null, "ipod");
			XmlParse.fun_ipod_enable = Integer.parseInt(string);

			string = parser.getAttributeValue(null, "music");
			XmlParse.fun_music_enable = Integer.parseInt(string);

			string = parser.getAttributeValue(null, "video");
			XmlParse.fun_video_enable = Integer.parseInt(string);

			string = parser.getAttributeValue(null, "bt");
			XmlParse.fun_bt_enable = Integer.parseInt(string);

			string = parser.getAttributeValue(null, "dvr");
			XmlParse.fun_dvr_enable = Integer.parseInt(string);
			
			string = parser.getAttributeValue(null, "dvr_uart");
			XmlParse.fun_dvr_uart_enable = Integer.parseInt(string);

			string = parser.getAttributeValue(null, "micracast");
			XmlParse.fun_micracast_enable = Integer.parseInt(string);
			
			string = parser.getAttributeValue(null, "tpms");
			XmlParse.fun_tpms_enable = Integer.parseInt(string);
			
			XmlParse.fun_carlife_enable = 1;
			XmlParse.fun_img_enable = 1;
		}
	}

	private static void write_fun() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "funs");
		serializer.attribute(null, "dvd", String.valueOf(XmlParse.fun_dvd_enable));
		serializer.attribute(null, "fm", String.valueOf(XmlParse.fun_fm_enable));
		serializer.attribute(null, "gps", String.valueOf(XmlParse.fun_gps_enable));
		serializer.attribute(null, "avin", String.valueOf(XmlParse.fun_avin_enable));
		serializer.attribute(null, "avin2", String.valueOf(XmlParse.fun_avin2_enable));
		serializer.attribute(null, "tv", String.valueOf(XmlParse.fun_tv_enable));
		serializer.attribute(null, "ipod", String.valueOf(XmlParse.fun_ipod_enable));
		serializer.attribute(null, "music", String.valueOf(XmlParse.fun_music_enable));
		serializer.attribute(null, "video", String.valueOf(XmlParse.fun_video_enable));
		serializer.attribute(null, "bt", String.valueOf(XmlParse.fun_bt_enable));
		serializer.attribute(null, "dvr", String.valueOf(XmlParse.fun_dvr_enable));
		serializer.attribute(null, "dvr_uart", String.valueOf(XmlParse.fun_dvr_uart_enable));
		serializer.attribute(null, "micracast", String.valueOf(XmlParse.fun_micracast_enable));
		serializer.attribute(null, "tpms", String.valueOf(XmlParse.fun_tpms_enable));
		serializer.attribute(null, "carlife", String.valueOf(XmlParse.fun_carlife_enable));
		serializer.attribute(null, "img", String.valueOf(XmlParse.fun_img_enable));
		serializer.endTag("", "funs");
	}

	private static void read_backlight() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("backlight")) {
			String string = parser.getAttributeValue(null, "default");
			XmlParse.backlight[0] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "low");
			XmlParse.backlight[1] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "middle");
			XmlParse.backlight[2] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "high");
			XmlParse.backlight[3] = Integer.parseInt(string);
		}
	}

	private static void write_backlight() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "backlight");
		serializer.attribute(null, "default", String.valueOf(XmlParse.backlight[0]));
		serializer.attribute(null, "low", String.valueOf(XmlParse.backlight[1]));
		serializer.attribute(null, "middle", String.valueOf(XmlParse.backlight[2]));
		serializer.attribute(null, "high", String.valueOf(XmlParse.backlight[3]));
		serializer.endTag("", "backlight");
	}

	private static void read_audio() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("audio")) {
			String string = parser.getAttributeValue(null, "treble");
			XmlParse.audio[0] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "alto");
			XmlParse.audio[1] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "bass");
			XmlParse.audio[2] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "subwoofer");
			XmlParse.audio[3] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "loundness");
			XmlParse.audio[4] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "balance");
			XmlParse.audio[5] = Integer.parseInt(XmlParse.getStringArray(string).get(0));
			XmlParse.audio[6] = Integer.parseInt(XmlParse.getStringArray(string).get(1));
		}
	}

	private static void write_audio() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "audio");
		serializer.attribute(null, "treble", String.valueOf(XmlParse.audio[0]));
		serializer.attribute(null, "alto", String.valueOf(XmlParse.audio[1]));
		serializer.attribute(null, "bass", String.valueOf(XmlParse.audio[2]));
		serializer.attribute(null, "subwoofer", String.valueOf(XmlParse.audio[3]));
		serializer.attribute(null, "loundness", String.valueOf(XmlParse.audio[4]));
		serializer.attribute(null, "balance", XmlParse.audio[5] + "," + XmlParse.audio[6]);
		serializer.endTag("", "audio");
	}

	static void read_rgb_video() throws XmlPullParserException, IOException {
		String tagName = parser.getName();

		if (tagName != null && tagName.equals("rgb_screen")) {
			rgb_index = Tag.VIDEOTYPE.SCREEN.ordinal();
		}

		if (tagName != null && tagName.equals("rgb_aux")) {
			rgb_index = Tag.VIDEOTYPE.AVIN_1.ordinal();
		}

		if (tagName != null && tagName.equals("rgb_backcar")) {
			rgb_index = Tag.VIDEOTYPE.BACKCAR.ordinal();
		}

		if (tagName != null && tagName.equals("rgb_usb")) {
			rgb_index = Tag.VIDEOTYPE.USB.ordinal();
		}

		if (tagName != null && tagName.equals("rgb_dvd")) {
			rgb_index = Tag.VIDEOTYPE.DVD.ordinal();
		}

		if (tagName != null) {
			String string;
			if (tagName.equals("bright")) {
				string = parser.nextText();
				XmlParse.setValue(XmlParse.rgb_video[rgb_index], 0, Integer.parseInt(string));
			}

			if (tagName.equals("contrast")) {
				string = parser.nextText();
				XmlParse.setValue(XmlParse.rgb_video[rgb_index], 1, Integer.parseInt(string));
			}

			if (tagName.equals("chroma")) {
				string = parser.nextText();
				XmlParse.setValue(XmlParse.rgb_video[rgb_index], 2, Integer.parseInt(string));
			}

			if (tagName.equals("saturation")) {
				string = parser.nextText();
				XmlParse.setValue(XmlParse.rgb_video[rgb_index], 3, Integer.parseInt(string));
			}
		}
	}

	private static void write_rgb_video(String tag, int[] value) throws IllegalArgumentException,
			IllegalStateException, IOException {
		serializer.startTag("", tag);
		serializer.startTag("", "bright");
		serializer.text(String.valueOf(value[0]));
		serializer.endTag("", "bright");
		serializer.startTag("", "contrast");
		serializer.text(String.valueOf(value[1]));
		serializer.endTag("", "contrast");
		serializer.startTag("", "chroma");
		serializer.text(String.valueOf(value[2]));
		serializer.endTag("", "chroma");
		serializer.startTag("", "saturation");
		serializer.text(String.valueOf(value[3]));
		serializer.endTag("", "saturation");
		serializer.endTag("", tag);
	}

	static void read_YUVGain() throws XmlPullParserException, IOException {
		String tagName = parser.getName();

		if (tagName != null && tagName.equals("yuv_aux")) {
			gain_index = Tag.VIDEOTYPE.AVIN_1.ordinal();
		}

		if (tagName != null && tagName.equals("yuv_backcar")) {
			gain_index = Tag.VIDEOTYPE.BACKCAR.ordinal();
		}

		if (tagName != null && tagName.equals("yuv_video")) {
			gain_index = Tag.VIDEOTYPE.USB.ordinal();
		}

		if (tagName != null && tagName.equals("yuv_dvd")) {
			gain_index = Tag.VIDEOTYPE.DVD.ordinal();
		}

		if (tagName != null) {
			String string;
			if (tagName.equals("y_gain")) {
				string = parser.nextText();
				XmlParse.setValue(XmlParse.yuv_gain[gain_index], 0, Integer.parseInt(string));
			}

			if (tagName.equals("u_gain")) {
				string = parser.nextText();
				XmlParse.setValue(XmlParse.yuv_gain[gain_index], 1, Integer.parseInt(string));
			}

			if (tagName.equals("v_gain")) {
				string = parser.nextText();
				XmlParse.setValue(XmlParse.yuv_gain[gain_index], 2, Integer.parseInt(string));
			}
		}
	}

	private static void write_YUVGain(String tag, int[] value) throws IllegalArgumentException,
			IllegalStateException, IOException {
		serializer.startTag("", tag);
		serializer.startTag("", "y_gain");
		serializer.text(String.valueOf(value[0]));
		serializer.endTag("", "y_gain");
		serializer.startTag("", "u_gain");
		serializer.text(String.valueOf(value[1]));
		serializer.endTag("", "u_gain");
		serializer.startTag("", "v_gain");
		serializer.text(String.valueOf(value[2]));
		serializer.endTag("", "v_gain");
		serializer.endTag("", tag);
	}

	private static void read_radio_type() throws XmlPullParserException {

		String tagName = parser.getName();
		if (tagName != null && tagName.equals("radio_type")) {
			String string = parser.getAttributeValue(null, "default");
			XmlParse.default_radio_type = string;
			string = parser.getAttributeValue(null, "support");
			XmlParse.support_radio_type = string;
		}
	}

	private static void write_radio_type() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "radio_type");
		serializer.attribute(null, "default", XmlParse.default_radio_type);
		serializer.attribute(null, "support", XmlParse.support_radio_type);
		serializer.endTag("", "radio_type");
	}

	private static void read_radio_area() throws XmlPullParserException {

		String tagName = parser.getName();
		if (tagName != null && tagName.equals("radio_area")) {
			String string = parser.getAttributeValue(null, "default");
			XmlParse.default_radio_area = string;
			string = parser.getAttributeValue(null, "support");
			XmlParse.support_radio_area = string;
		}
	}

	private static void write_radio_area() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "radio_area");
		serializer.attribute(null, "default", XmlParse.default_radio_area);
		serializer.attribute(null, "support", XmlParse.support_radio_area);
		serializer.endTag("", "radio_area");
	}

	private static void read_radio_area_enable() throws XmlPullParserException {

		String tagName = parser.getName();
		if (tagName != null && tagName.equals("radio_area_enable")) {

			String string;
			for (int i = 0; i < XmlParse.list_radio_area_all.size(); i++) {
				string = parser.getAttributeValue(null, XmlParse.list_radio_area_all.get(i));
				XmlParse.radio_area_enable[i] = Integer.parseInt(string);
			}
		}
	}

	private static void write_radio_area_enable() throws IllegalArgumentException,
			IllegalStateException, IOException {

		serializer.startTag("", "radio_area_enable");
		for (int i = 0; i < XmlParse.list_radio_area_all.size(); i++) {
			serializer.attribute(null, XmlParse.list_radio_area_all.get(i),
					String.valueOf(XmlParse.radio_area_enable[i]));
		}
		serializer.endTag("", "radio_area_enable");
	}

	private static void read_navi_path() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("navi_path")) {
			String string = parser.getAttributeValue(null, "default");
			XmlParse.default_navi_path = string;
			string = parser.getAttributeValue(null, "support");
			XmlParse.support_navi_path = string;
		}
	}

	private static void write_navi_path() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "navi_path");
		serializer.attribute(null, "default", XmlParse.default_navi_path);
		serializer.attribute(null, "support", XmlParse.support_navi_path);
		serializer.endTag("", "navi_path");
	}

	private static void read_video_out_format() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("video_out_format")) {
			String string = parser.getAttributeValue(null, "default");
			XmlParse.default_video_out_format = string;
			string = parser.getAttributeValue(null, "support");
			XmlParse.support_video_out_format = string;
		}
	}

	private static void write_video_out_format() throws IllegalArgumentException,
			IllegalStateException, IOException {
		serializer.startTag("", "video_out_format");
		serializer.attribute(null, "default", XmlParse.default_video_out_format);
		serializer.attribute(null, "support", XmlParse.support_video_out_format);
		serializer.endTag("", "video_out_format");
	}

	private static void read_volume() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("volume")) {
			String string = parser.getAttributeValue(null, "front");
			XmlParse.volume[0] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "rear");
			XmlParse.volume[1] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "gis");
			XmlParse.volume[2] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "backcar");
			XmlParse.volume[3] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "bluephone");
			XmlParse.volume[4] = Integer.parseInt(string);
		}
	}

	private static void write_volume() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "volume");
		serializer.attribute(null, "front", String.valueOf(XmlParse.volume[0]));
		serializer.attribute(null, "rear", String.valueOf(XmlParse.volume[1]));
		serializer.attribute(null, "gis", String.valueOf(XmlParse.volume[2]));
		serializer.attribute(null, "backcar", String.valueOf(XmlParse.volume[3]));
		serializer.attribute(null, "bluephone", String.valueOf(XmlParse.volume[4]));
		serializer.endTag("", "volume");
	}

	private static void read_tv_type() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("tv_type")) {
			String string = parser.getAttributeValue(null, "default");
			XmlParse.default_tv_type = string;
			string = parser.getAttributeValue(null, "support");
			XmlParse.support_tv_type = string;
		}
	}

	private static void write_tv_type() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "tv_type");
		serializer.attribute(null, "default", XmlParse.default_tv_type);
		serializer.attribute(null, "support", XmlParse.support_tv_type);
		serializer.endTag("", "tv_type");
	}

	private static void read_tv_area() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("tv_area")) {
			String string = parser.getAttributeValue(null, "default");
			XmlParse.default_tv_area = string;
			string = parser.getAttributeValue(null, "support");
			XmlParse.support_tv_area = string;
		}
	}

	private static void write_tv_area() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "tv_area");
		serializer.attribute(null, "default", XmlParse.default_tv_area);
		serializer.attribute(null, "support", XmlParse.support_tv_area);
		serializer.endTag("", "tv_area");
	}

	private static void read_dvr_type() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("dvr_type")) {
			String string = parser.getAttributeValue(null, "default");
			XmlParse.default_dvr_type = string;
			string = parser.getAttributeValue(null, "support");
			XmlParse.support_dvr_type = string;
		}
	}

	private static void write_dvr_type() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "dvr_type");
		serializer.attribute(null, "default", XmlParse.default_dvr_type);
		serializer.attribute(null, "support", XmlParse.support_dvr_type);
		serializer.endTag("", "dvr_type");
	}

	private static void read_backcar() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("backcar")) {
			String string = parser.getAttributeValue(null, "enable");
			XmlParse.backcar_enable = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "mirror");
			XmlParse.backcar_mirror = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "guidelines");
			XmlParse.backcar_guidelines = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "radar");
			XmlParse.backcar_radar = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "mute");
			XmlParse.backcar_mute = Integer.parseInt(string);
		}
	}

	private static void write_backcar() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "backcar");
		serializer.attribute(null, "enable", String.valueOf(XmlParse.backcar_enable));
		serializer.attribute(null, "mirror", String.valueOf(XmlParse.backcar_mirror));
		serializer.attribute(null, "guidelines", String.valueOf(XmlParse.backcar_guidelines));
		serializer.attribute(null, "radar", String.valueOf(XmlParse.backcar_radar));
		serializer.attribute(null, "mute", String.valueOf(XmlParse.backcar_mute));
		serializer.endTag("", "backcar");
	}

	private static void read_bt() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("bt")) {
			String string = parser.getAttributeValue(null, "noice_enable");
			XmlParse.bt_noice_enable = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "device");
			XmlParse.bt_device = string;
			string = parser.getAttributeValue(null, "pair");
			XmlParse.bt_pair = string;
			string = parser.getAttributeValue(null, "auto_connect");
			XmlParse.bt_auto_connect = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "auto_answer");
			XmlParse.bt_auto_answer = Integer.parseInt(string);
			
			string = parser.getAttributeValue(null, "device_auto_set");
			XmlParse.bt_device_auto_set = Integer.parseInt(string);
			
			string = parser.getAttributeValue(null, "device_name_oem");
			XmlParse.bt_device_name_oem = string;
		}
	}

	private static void write_bt() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "bt");
		serializer.attribute(null, "noice_enable", String.valueOf(XmlParse.bt_noice_enable));
		serializer.attribute(null, "device", XmlParse.bt_device);
		serializer.attribute(null, "pair", XmlParse.bt_pair);
		serializer.attribute(null, "auto_connect", String.valueOf(XmlParse.bt_auto_connect));
		serializer.attribute(null, "auto_answer", String.valueOf(XmlParse.bt_auto_answer));
		serializer.attribute(null, "device_auto_set", String.valueOf(XmlParse.bt_device_auto_set));
		serializer.attribute(null, "device_name_oem", String.valueOf(XmlParse.bt_device_name_oem));
		serializer.endTag("", "bt");
	}

	private static void read_gis() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("gis")) {
			String string = parser.getAttributeValue(null, "remix_enable");
			XmlParse.remix_enable = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "listen_enable");
			XmlParse.listen_enable = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "remix_ratio");
			XmlParse.remix_ratio = Integer.parseInt(string);
		}
	}

	private static void write_gis() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "gis");
		serializer.attribute(null, "remix_enable", String.valueOf(XmlParse.remix_enable));
		serializer.attribute(null, "listen_enable", String.valueOf(XmlParse.listen_enable));
		serializer.attribute(null, "remix_ratio", String.valueOf(XmlParse.remix_ratio));
		serializer.endTag("", "gis");
	}

	private static void read_gain() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("gain")) {
			String string = parser.getAttributeValue(null, "btphone");
			XmlParse.gain[0] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "gis");
			XmlParse.gain[1] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "other");
			XmlParse.gain[2] = Integer.parseInt(string);
		}
	}

	private static void write_gain() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "gain");
		serializer.attribute(null, "btphone", String.valueOf(XmlParse.gain[0]));
		serializer.attribute(null, "gis", String.valueOf(XmlParse.gain[1]));
		serializer.attribute(null, "other", String.valueOf(XmlParse.gain[2]));
		serializer.endTag("", "gain");
	}

	private static void read_db() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("db")) {
			String string = parser.getAttributeValue(null, "aux");
			XmlParse.db[0] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "a2dp");
			XmlParse.db[1] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "fm");
			XmlParse.db[2] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "am");
			XmlParse.db[3] = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "tv");
			XmlParse.db[4] = Integer.parseInt(string);
		}
	}

	private static void write_db() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "db");
		serializer.attribute(null, "aux", String.valueOf(XmlParse.db[0]));
		serializer.attribute(null, "a2dp", String.valueOf(XmlParse.db[1]));
		serializer.attribute(null, "fm", String.valueOf(XmlParse.db[2]));
		serializer.attribute(null, "am", String.valueOf(XmlParse.db[3]));
		serializer.attribute(null, "tv", String.valueOf(XmlParse.db[4]));
		serializer.endTag("", "db");
	}

	private static void read_others() throws XmlPullParserException {
		String tagName = parser.getName();

		if (tagName != null && tagName.equals("headlight_enable")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.headlight_enable = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("aux_port")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.aux_port = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("brake_enable")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.brake_enable = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("audioport_crv_audio_input")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.audioport_crv_audio_input = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("mic_volume")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.mic_volume = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("dvd_area_code")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.dvd_area_code = Integer.parseInt(string);
		}
		
		if (tagName != null && tagName.equals("tyre_com")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.tyre_com = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("outside_temp_test")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.outside_temp_test = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("lcd_enable")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.lcd_enable = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("antenna_ctrl_enable")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.antenna_ctrl_enable = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("FM_DX")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.DX_LOC[0] = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("FM_LOC")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.DX_LOC[1] = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("AM_DX")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.DX_LOC[2] = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("AM_LOC")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.DX_LOC[3] = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("OIRT_DX")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.DX_LOC[4] = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("OIRT_LOC")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.DX_LOC[5] = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("videoport_drive_record")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.videoport_drive_record = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("videoport_front_camera")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.videoport_front_camera = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("power_amplifier_ctrl_enable")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.power_amplifier_ctrl_enable = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("air_temp_ctrl")) {
			String string = parser.getAttributeValue(null, "default");
			XmlParse.default_air_temp_ctrl = string;
			string = parser.getAttributeValue(null, "support");
			XmlParse.support_air_temp_ctrl = string;
		}

		if (tagName != null && tagName.equals("chramatic_lamp")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.chramatic_lamp = Integer.parseInt(string);
		}

		if (tagName != null && tagName.equals("voice_touch_enable")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.voice_touch_enable = Integer.parseInt(string);
		}
		
		if (tagName != null && tagName.equals("voice_wakeup_enable")) {
			String string = parser.getAttributeValue(null, "value");
			XmlParse.voice_wakeup_enable = Integer.parseInt(string);
		}
		
		if (tagName != null && tagName.equals("factory_password")) {
			String string = parser.getAttributeValue(null, "value");
			if (string != null) {
				XmlParse.factory_password = string;
			}else {
				XmlParse.factory_password = "8317";
			}
		}
	}

	private static void write_others() throws IllegalArgumentException, IllegalStateException,
			IOException {

		serializer.startTag("", "headlight_enable");
		serializer.attribute(null, "value", String.valueOf(XmlParse.headlight_enable));
		serializer.endTag("", "headlight_enable");

		serializer.startTag("", "aux_port");
		serializer.attribute(null, "value", String.valueOf(XmlParse.aux_port));
		serializer.endTag("", "aux_port");

		serializer.startTag("", "brake_enable");
		serializer.attribute(null, "value", String.valueOf(XmlParse.brake_enable));
		serializer.endTag("", "brake_enable");

		serializer.startTag("", "audioport_crv_audio_input");
		serializer.attribute(null, "value", String.valueOf(XmlParse.audioport_crv_audio_input));
		serializer.endTag("", "audioport_crv_audio_input");

		serializer.startTag("", "mic_volume");
		serializer.attribute(null, "value", String.valueOf(XmlParse.mic_volume));
		serializer.endTag("", "mic_volume");

		serializer.startTag("", "dvd_area_code");
		serializer.attribute(null, "value", String.valueOf(XmlParse.dvd_area_code));
		serializer.endTag("", "dvd_area_code");
		
		serializer.startTag("", "tyre_com");
		serializer.attribute(null, "value", String.valueOf(XmlParse.tyre_com));
		serializer.endTag("", "tyre_com");

		serializer.startTag("", "outside_temp_test");
		serializer.attribute(null, "value", String.valueOf(XmlParse.outside_temp_test));
		serializer.endTag("", "outside_temp_test");

		serializer.startTag("", "lcd_enable");
		serializer.attribute(null, "value", String.valueOf(XmlParse.lcd_enable));
		serializer.endTag("", "lcd_enable");

		serializer.startTag("", "antenna_ctrl_enable");
		serializer.attribute(null, "value", String.valueOf(XmlParse.antenna_ctrl_enable));
		serializer.endTag("", "antenna_ctrl_enable");

		serializer.startTag("", "FM_DX");
		serializer.attribute(null, "value", String.valueOf(XmlParse.DX_LOC[0]));
		serializer.endTag("", "FM_DX");

		serializer.startTag("", "FM_LOC");
		serializer.attribute(null, "value", String.valueOf(XmlParse.DX_LOC[1]));
		serializer.endTag("", "FM_LOC");

		serializer.startTag("", "AM_DX");
		serializer.attribute(null, "value", String.valueOf(XmlParse.DX_LOC[2]));
		serializer.endTag("", "AM_DX");

		serializer.startTag("", "AM_LOC");
		serializer.attribute(null, "value", String.valueOf(XmlParse.DX_LOC[3]));
		serializer.endTag("", "AM_LOC");

		serializer.startTag("", "OIRT_DX");
		serializer.attribute(null, "value", String.valueOf(XmlParse.DX_LOC[4]));
		serializer.endTag("", "OIRT_DX");

		serializer.startTag("", "OIRT_LOC");
		serializer.attribute(null, "value", String.valueOf(XmlParse.DX_LOC[5]));
		serializer.endTag("", "OIRT_LOC");

		serializer.startTag("", "videoport_drive_record");
		serializer.attribute(null, "value", String.valueOf(XmlParse.videoport_drive_record));
		serializer.endTag("", "videoport_drive_record");

		serializer.startTag("", "videoport_front_camera");
		serializer.attribute(null, "value", String.valueOf(XmlParse.videoport_front_camera));
		serializer.endTag("", "videoport_front_camera");

		serializer.startTag("", "power_amplifier_ctrl_enable");
		serializer.attribute(null, "value", String.valueOf(XmlParse.power_amplifier_ctrl_enable));
		serializer.endTag("", "power_amplifier_ctrl_enable");

		serializer.startTag("", "air_temp_ctrl");
		serializer.attribute(null, "default", XmlParse.default_air_temp_ctrl);
		serializer.attribute(null, "support", XmlParse.support_air_temp_ctrl);
		serializer.endTag("", "air_temp_ctrl");

		serializer.startTag("", "chramatic_lamp");
		serializer.attribute(null, "value", String.valueOf(XmlParse.chramatic_lamp));
		serializer.endTag("", "chramatic_lamp");

		serializer.startTag("", "voice_touch_enable");
		serializer.attribute(null, "value", String.valueOf(XmlParse.voice_touch_enable));
		serializer.endTag("", "voice_touch_enable");
		
		serializer.startTag("", "voice_wakeup_enable");
		serializer.attribute(null, "value", String.valueOf(XmlParse.voice_wakeup_enable));
		serializer.endTag("", "voice_wakeup_enable");
		
		serializer.startTag("", "factory_password");
		serializer.attribute(null, "value", XmlParse.factory_password);
		serializer.endTag("", "factory_password");
	}

	private static void read_qicaideng() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("qicaideng")) {
			String string = parser.getAttributeValue(null, "mode");
			XmlParse.light_mode = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "red");
			XmlParse.light_r = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "green");
			XmlParse.light_g = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "blue");
			XmlParse.light_b = Integer.parseInt(string);
		}
	}

	private static void write_qicaideng() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "qicaideng");
		serializer.attribute(null, "mode", String.valueOf(XmlParse.light_mode));
		serializer.attribute(null, "red", String.valueOf(XmlParse.light_r));
		serializer.attribute(null, "green", String.valueOf(XmlParse.light_g));
		serializer.attribute(null, "blue", String.valueOf(XmlParse.light_b));
		serializer.endTag("", "qicaideng");
	}

	private static void read_touchKeyLearning() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("touchkeylearn")) {
			XmlParse.touch_key_btn = parser.getAttributeValue(null, "keyVals");
			String strPosThreshold = parser.getAttributeValue(null, "touchSize");
			Util.pos_threshold = Integer.valueOf(strPosThreshold);
		}
	}

	private static void write_touchKeyLearning() throws IllegalArgumentException,
			IllegalStateException, IOException {
		serializer.startTag("", "touchkeylearn");
		XmlParse.touch_key_btn = "";
		for (int i = 0; i < XmlParse.touch_key_array.size(); i++) {
			XmlParse.touch_key_btn += XmlParse.touch_key_array.get(i) + ",";
		}
		serializer.attribute(null, "keyVals", XmlParse.touch_key_btn);
		serializer.attribute(null, "touchSize", String.valueOf(Util.pos_threshold));
		serializer.endTag("", "touchkeylearn");
	}

	final static String strAssistivTouch[] = { "enable", "home", "return", "power", "screen",
			"mode", "navi", "band", "play", "preview", "next", "mute", "voladd", "volminus",
			"answer", "hungup" };

	private static void read_assistive_touch() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("assistive_touch")) {

			int i = 0;
			String string;
			for (String str : strAssistivTouch) {
				string = parser.getAttributeValue(null, strAssistivTouch[i]);
				XmlParse.assistive_touch_enable[i] = Integer.parseInt(string);
				i++;
			}
		}
	}

	private static void write_assistive_touch() throws IllegalArgumentException,
			IllegalStateException, IOException {

		serializer.startTag("", "assistive_touch");
		int i = 0;
		for (String str : strAssistivTouch) {
			serializer.attribute(null, strAssistivTouch[i],
					String.valueOf(XmlParse.assistive_touch_enable[i]));
			i++;
		}
		serializer.endTag("", "assistive_touch");
	}

	final static String strFuns_other[] = { "headlight", "assistive_touch", "volume_touch", "swc",
			"wallpaper", "wifi", "alto", "subwoofer", "clock" };

	private static void read_funs_other() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("funs_other")) {

			int i = 0;
			String string;
			for (String str : strFuns_other) {
				string = parser.getAttributeValue(null, strFuns_other[i]);
				XmlParse.funs_other[i] = Integer.parseInt(string);
				i++;
			}
		}
	}

	private static void write_funs_other() throws IllegalArgumentException, IllegalStateException,
			IOException {

		serializer.startTag("", "funs_other");
		int i = 0;
		for (String str : strFuns_other) {
			serializer.attribute(null, strFuns_other[i], String.valueOf(XmlParse.funs_other[i]));
			i++;
		}
		serializer.endTag("", "funs_other");
	}

	private static void read_sleep_about() throws XmlPullParserException {
		String tagName = parser.getName();
		if (tagName != null && tagName.equals("sleep")) {
			String string = parser.getAttributeValue(null, "boot_logo_enable");
			XmlParse.boot_logo_enable = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "power_enable");
			XmlParse.sleep_power_enable = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "ready_time");
			XmlParse.sleep_ready_time = Integer.parseInt(string);
			string = parser.getAttributeValue(null, "time");
			XmlParse.sleep_time = Integer.parseInt(string);
		}
	}

	private static void write_sleep_about() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "sleep");
		serializer.attribute(null, "boot_logo_enable", String.valueOf(XmlParse.boot_logo_enable));
		serializer.attribute(null, "power_enable", String.valueOf(XmlParse.sleep_power_enable));
		serializer.attribute(null, "ready_time", String.valueOf(XmlParse.sleep_ready_time));
		serializer.attribute(null, "time", String.valueOf(XmlParse.sleep_time));
		serializer.endTag("", "sleep");
	}
	
	//lcd config
	private static void read_lcd_type() throws XmlPullParserException {
		String tagName = parser.getName();

		if (tagName != null && tagName.equals("lcdtype")) {
			String string = parser.getAttributeValue(null, "default");
			XmlParse.default_lcd_type = string;
		}
	}

	private static void write_lcd_type() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "lcdtype");
		serializer.attribute(null, "default", XmlParse.default_lcd_type);
		serializer.endTag("", "lcdtype");
	}
	
	private static void read_lvds_bit() throws XmlPullParserException {
		String tagName = parser.getName();

		if (tagName != null && tagName.equals("lvds_bit")) {
			String string = parser.getAttributeValue(null, "default");
			XmlParse.default_lvds_bit = string;
		}
	}

	private static void write_lvds_bit() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "lvds_bit");
		serializer.attribute(null, "default", XmlParse.default_lvds_bit);
		serializer.endTag("", "lvds_bit");
	}
	
	private static void read_lcd_resolution() throws XmlPullParserException {
		String tagName = parser.getName();

		if (tagName != null && tagName.equals("lcd_resolution")) {
			String string = parser.getAttributeValue(null, "default");
			XmlParse.default_lcd_resolution = string;
		}
	}

	private static void write_lcd_resolution() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "lcd_resolution");
		serializer.attribute(null, "default", XmlParse.default_lcd_resolution);
		serializer.endTag("", "lcd_resolution");
	}
	
	private static void read_ttf_drive_current() throws XmlPullParserException {
		String tagName = parser.getName();

		if (tagName != null && tagName.equals("ttf_drive_current")) {
			String string = parser.getAttributeValue(null, "default");
			XmlParse.default_tft_drive_current = string;
		}
	}

	private static void write_ttf_drive_current() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "ttf_drive_current");
		serializer.attribute(null, "default", XmlParse.default_tft_drive_current);
		serializer.endTag("", "ttf_drive_current");
	}
	
	private static void read_lvds_drive_current() throws XmlPullParserException {
		String tagName = parser.getName();

		if (tagName != null && tagName.equals("lvds_drive_current")) {
			String string = parser.getAttributeValue(null, "default");
			XmlParse.default_lvds_drive_current = string;
		}
	}

	private static void write_lvds_drive_current() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "lvds_drive_current");
		serializer.attribute(null, "default", XmlParse.default_lvds_drive_current);
		serializer.endTag("", "lvds_drive_current");
	}
	
	private static void read_dithering_output() throws XmlPullParserException {
		String tagName = parser.getName();

		if (tagName != null && tagName.equals("dithering_output")) {
			String string = parser.getAttributeValue(null, "default");
			XmlParse.default_dithering_output = string;
		}
	}

	private static void write_dithering_output() throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag("", "dithering_output");
		serializer.attribute(null, "default", XmlParse.default_dithering_output);
		serializer.endTag("", "dithering_output");
	}

	private static void read_data() throws XmlPullParserException, IOException {
		read_rgb_video();
		// read_YUVGain();
		read_language();
		read_timezone();
		read_backlight();
		read_audio();
		read_radio_type();
		read_radio_area();
		read_radio_area_enable();
		read_navi_path();
		read_gis();
		read_video_out_format();
		read_volume();
		read_tv_type();
		read_tv_area();
		read_dvr_type();
		read_backcar();
		read_bt();
		read_others();
		read_gain();
		read_db();
		read_fun();
		read_qicaideng();
//		read_touchKeyLearning();
//		read_assistive_touch();
		read_funs_other();
		read_sleep_about();
		
		read_lcd_type();
		read_lvds_bit();
		read_lcd_resolution();
		read_ttf_drive_current();
		read_lvds_drive_current();
		read_dithering_output();
	}

	private static void write_data() throws IllegalArgumentException, IllegalStateException,
			IOException {

		write_rgb_video("rgb_screen", XmlParse.rgb_video[Tag.VIDEOTYPE.SCREEN.ordinal()]);
		write_rgb_video("rgb_aux", XmlParse.rgb_video[Tag.VIDEOTYPE.AVIN_1.ordinal()]);
		write_rgb_video("rgb_backcar", XmlParse.rgb_video[Tag.VIDEOTYPE.BACKCAR.ordinal()]);
		write_rgb_video("rgb_usb", XmlParse.rgb_video[Tag.VIDEOTYPE.USB.ordinal()]);
		write_rgb_video("rgb_dvd", XmlParse.rgb_video[Tag.VIDEOTYPE.DVD.ordinal()]);

		// write_YUVGain("yuv_aux",
		// XmlParse.yuv_gain[Tag.VIDEOTYPE.AVIN_1.ordinal()]);
		// write_YUVGain("yuv_backcar",
		// XmlParse.yuv_gain[Tag.VIDEOTYPE.BACKCAR.ordinal()]);
		// write_YUVGain("yuv_usb",
		// XmlParse.yuv_gain[Tag.VIDEOTYPE.USB.ordinal()]);
		// write_YUVGain("yuv_dvd",
		// XmlParse.yuv_gain[Tag.VIDEOTYPE.DVD.ordinal()]);

		write_language();
		write_timezone();
		write_backlight();
		write_audio();
		write_radio_type();
		write_radio_area();
		write_radio_area_enable();
		write_navi_path();
		write_gis();
		write_video_out_format();
		write_volume();
		write_tv_type();
		write_tv_area();
		write_dvr_type();
		write_backcar();
		write_bt();
		write_others();
		write_gain();
		write_db();
		write_fun();
		write_qicaideng();
//		write_touchKeyLearning();
//		write_assistive_touch();
		write_funs_other();
		write_sleep_about();
		
		write_lcd_type();
		write_lvds_bit();
		write_lcd_resolution();
		write_ttf_drive_current();
		write_lvds_drive_current();
		write_dithering_output();
	}
	
	public static void setSystemProperties(){
		setSystemProperties(null,null);
	}
	public static void setSystemProperties(String language,String country) {
		// SystemProperties.set(Tag.PERSYS_REBOOT_FLAG,
		// XmlParse.system_reboot_flag + "");
		if(language != null && country != null){
			L.e(language + "  " + country);
			SystemProperties.set(Tag.PERSYS_LANGUAGE, language);
			SystemProperties.set(Tag.PERSYS_COUNTYR, country);
			SystemProperties.set(Tag.PERSYS_LOCALEVAR, country);
			SystemProperties.set(Tag.PERSYS_LANGUAGE_USER, language);
			SystemProperties.set(Tag.PERSYS_COUNTYR_USER, country);
			SystemProperties.set("persist.sys.hzh.language",language);
			SystemProperties.set("persist.sys.hzh.country",country);
			SystemProperties.set("persist.sys.hzh.locale.change","true");
			//return;
		}else{	
			SystemProperties.set(Tag.PERSYS_LANGUAGE, XmlParse.default_language.substring(0, 2));
			SystemProperties.set(Tag.PERSYS_COUNTYR, XmlParse.default_language.substring(3, 5));
			SystemProperties.set(Tag.PERSYS_LOCALEVAR, XmlParse.default_language.substring(3, 5));
			SystemProperties.set(Tag.PERSYS_LANGUAGE_USER, XmlParse.default_language.substring(0, 2));
			SystemProperties.set(Tag.PERSYS_COUNTYR_USER, XmlParse.default_language.substring(3, 5));
		}
		
		final Locale l = new Locale(XmlParse.default_language.substring(0, 2),
				XmlParse.default_language.substring(3, 5));
		Locale.setDefault(l);

		SystemProperties.set(Tag.TIMEZONE_PROPERTY, XmlParse.default_timezone);

		for (int i = 0; i < Tag.PERSYS_BKLIGHT.length; i++) {
			SystemProperties.set(Tag.PERSYS_BKLIGHT[i], XmlParse.backlight[i] * 255 / 100 + "");
		}

		for (int i = 0; i < Tag.PERSYS_VOLUME.length; i++) {
			SystemProperties.set(Tag.PERSYS_VOLUME[i], XmlParse.volume[i] + "");
		}

		int index[] = { Tag.VIDEOTYPE.AVIN_2.ordinal(), Tag.VIDEOTYPE.AVIN_3.ordinal(),
				Tag.VIDEOTYPE.AVIN_4.ordinal(), Tag.VIDEOTYPE.AVIN_5.ordinal(),
				Tag.VIDEOTYPE.DGI.ordinal(), Tag.VIDEOTYPE.HDMI.ordinal(),
				Tag.VIDEOTYPE.VGA.ordinal(), Tag.VIDEOTYPE.YPBPR.ordinal() };

		for (int i = 0; i < index.length; i++) {
			for (int j = 0; j < Tag.PERSYS_RGB_VIDEO[index[i]].length; j++)
				XmlParse.rgb_video[index[i]][j] = XmlParse.rgb_video[Tag.VIDEOTYPE.AVIN_1.ordinal()][j];
		}

		for (int i = 0; i < Tag.PERSYS_RGB_VIDEO.length; i++) {
			for (int j = 0; j < Tag.PERSYS_RGB_VIDEO[i].length; j++)
				SystemProperties.set(Tag.PERSYS_RGB_VIDEO[i][j], XmlParse.rgb_video[i][j] + "");
		}

		// for (int i = 0; i < index.length; i++) {
		// for (int j = 0; j < Tag.PERSYS_YUV_GAIN[index[i]].length; j++)
		// XmlParse.yuv_gain[index[i]][j] =
		// XmlParse.yuv_gain[Tag.VIDEOTYPE.AVIN_1.ordinal()][j];
		// }

		// for (int i = 0; i < Tag.PERSYS_YUV_GAIN.length; i++) {
		// for (int j = 0; j < Tag.PERSYS_YUV_GAIN[i].length; j++)
		// SystemProperties.set(Tag.PERSYS_YUV_GAIN[i][j],
		// XmlParse.yuv_gain[i][j] + "");
		// }

		for (int i = 0; i < Tag.PERSYS_AUDIO.length; i++) {
			SystemProperties.set(Tag.PERSYS_AUDIO[i], XmlParse.audio[i] + "");
		}

		SystemProperties.set(Tag.PERSYS_HEADLIGHT_ENABLE, XmlParse.headlight_enable == 1 ? "true"
				: "false");
		SystemProperties.set(Tag.PERSYS_BACKCAR_ENABLE, XmlParse.backcar_enable == 1 ? "true"
				: "false");
		SystemProperties
				.set(Tag.PERSYS_BACKCAR_MUTE, XmlParse.backcar_mute == 1 ? "true" : "false");
		SystemProperties.set(Tag.PERSYS_BACKCAR_TRACE, XmlParse.backcar_guidelines == 1 ? "true"
				: "false");
		SystemProperties.set(Tag.PERSYS_BACKCAR_RADAR, XmlParse.backcar_radar == 1 ? "true"
				: "false");
		SystemProperties.set(Tag.PERSYS_BACKCAR_MIRROR, XmlParse.backcar_mirror + "");

		// BT
		SystemProperties.set(Tag.PERSYS_BT_NOICE, XmlParse.bt_noice_enable == 1 ? "true" : "false");
		SystemProperties.set(Tag.PERSYS_BT_DEVICE, XmlParse.bt_device);
		SystemProperties.set(Tag.PERSYS_BT_PAIR, XmlParse.bt_pair);
		SystemProperties.set(Tag.PERSYS_BT_AUTO_CONNECT, XmlParse.bt_auto_connect == 1 ? "true"
				: "false");
		SystemProperties.set(Tag.PERSYS_BT_AUTO_ANSWER, XmlParse.bt_auto_answer == 1 ? "true"
				: "false");
		SystemProperties.set(Tag.PERSYS_BT_DISCOVER, XmlParse.bt_discover == 1 ? "true" : "false");
		
		SystemProperties.set(Tag.PERSYS_BT_DEVICE_AUTO_SET, XmlParse.bt_device_auto_set == 1 ? "true" : "false");
		SystemProperties.set(Tag.PERSYS_BT_DEVICE_NAME_OEM, XmlParse.bt_device_name_oem);

		SystemProperties.set(Tag.PERSYS_NAVI_PATH, XmlParse.default_navi_path);
		SystemProperties.set(Tag.PERSYS_NAVI_REMIX, XmlParse.remix_enable == 1 ? "true" : "false");
		SystemProperties
				.set(Tag.PERSYS_NAVI_LISTEN, XmlParse.listen_enable == 1 ? "true" : "false");
		SystemProperties.set(Tag.PERSYS_REMIX_RATIO, XmlParse.remix_ratio + "");

		SystemProperties.set(Tag.PERSYS_RADIO_TYPE, XmlParse.default_radio_type);
		SystemProperties.set(Tag.PERSYS_RADIO_AREA, XmlParse.default_radio_area);
		for (int i = 0; i < Tag.PERSYS_RADIO_AREA_ENABLE.length; i++) {
			SystemProperties.set(Tag.PERSYS_RADIO_AREA_ENABLE[i],
					XmlParse.radio_area_enable[i] == 1 ? "true" : "false");
		}

		SystemProperties.set(Tag.PERSYS_VIDEO_OUT_FORMAT, XmlParse.default_video_out_format);
		SystemProperties.set(Tag.PERSYS_TV_TYPE, XmlParse.default_tv_type);
		SystemProperties.set(Tag.PERSYS_TV_AREA, XmlParse.default_tv_area);
		SystemProperties.set(Tag.PERSYS_DVR_TYPE, XmlParse.default_dvr_type);
		// brake
		SystemProperties
				.set(Tag.PERSYS_BRAKE_ENABLE, XmlParse.brake_enable == 1 ? "true" : "false");

		SystemProperties.set(Tag.PERSYS_AUDIO_PORT_CRV, XmlParse.audioport_crv_audio_input + "");
		SystemProperties.set(Tag.PERSYS_MIC_VOLUME, XmlParse.mic_volume + "");
		SystemProperties.set(Tag.PERSYS_DVD_AREA_CODE, XmlParse.dvd_area_code + "");
		SystemProperties.set(Tag.PERSYS_TYRE_COM, XmlParse.tyre_com + "");
		SystemProperties.set(Tag.PERSYS_OUTSIDE_TEMP_TEST, XmlParse.outside_temp_test == 1 ? "true"
				: "false");
		SystemProperties.set(Tag.PERSYS_LCD_ENABLE, XmlParse.lcd_enable == 1 ? "true" : "false");
		SystemProperties.set(Tag.PERSYS_ANTENNA_CTRL, XmlParse.antenna_ctrl_enable == 1 ? "true"
				: "false");

		SystemProperties.set(Tag.PERSYS_FM_DX, XmlParse.DX_LOC[0] + "");
		SystemProperties.set(Tag.PERSYS_FM_LOC, XmlParse.DX_LOC[1] + "");
		SystemProperties.set(Tag.PERSYS_AM_DX, XmlParse.DX_LOC[2] + "");
		SystemProperties.set(Tag.PERSYS_AM_LOC, XmlParse.DX_LOC[3] + "");
		SystemProperties.set(Tag.PERSYS_OIRT_DX, XmlParse.DX_LOC[4] + "");
		SystemProperties.set(Tag.PERSYS_OIRT_LOC, XmlParse.DX_LOC[5] + "");

		SystemProperties.set(Tag.PERSYS_VIDEO_PORT_DRIVE_RECORD, XmlParse.videoport_drive_record
				+ "");
		SystemProperties.set(Tag.PERSYS_VIDEO_PORT_FRONT_CAMERA, XmlParse.videoport_front_camera
				+ "");

		SystemProperties.set(Tag.PERSYS_GAIN_BT_PHONE, XmlParse.gain[0] + "");
		SystemProperties.set(Tag.PERSYS_GAIN_GIS, XmlParse.gain[1] + "");
		SystemProperties.set(Tag.PERSYS_GAIN_OTHER, XmlParse.gain[2] + "");

		SystemProperties.set(Tag.PERSYS_POWER_AMPLIFIER_CTRL,
				XmlParse.power_amplifier_ctrl_enable == 1 ? "true" : "false");
		SystemProperties.set(Tag.PERSYS_AIR_TEMP_CTRL, XmlParse.default_air_temp_ctrl);
		SystemProperties.set(Tag.PERSYS_CHRAMATIC_LAMP, XmlParse.chramatic_lamp + "");

		SystemProperties.set(Tag.PERSYS_DB_AUX, XmlParse.db[0] + "");
		SystemProperties.set(Tag.PERSYS_DB_A2DP, XmlParse.db[1] + "");
		SystemProperties.set(Tag.PERSYS_DB_FM, XmlParse.db[2] + "");
		SystemProperties.set(Tag.PERSYS_DB_AM, XmlParse.db[3] + "");
		SystemProperties.set(Tag.PERSYS_DB_TV, XmlParse.db[4] + "");

		SystemProperties.set(Tag.PERSYS_AUX_PORT, XmlParse.aux_port + "");

		SystemProperties.set(Tag.PERSYS_LCD_TYPE,  XmlParse.default_lcd_type + "");		//lcd鎺ュ彛绫诲瀷 ttl or lvds
		SystemProperties.set(Tag.PERSYS_LVDS_BIT, XmlParse.default_lcd_type + "");		//lvds鏁版嵁浣嶅
		SystemProperties.set(Tag.PERSYS_LCD_RESOLUTION, XmlParse.default_lcd_resolution);	//lcd鍒嗚鲸鐜�
		SystemProperties.set(Tag.PERSYS_TFT_DRIVE_CURRENT, XmlParse.default_tft_drive_current);	//TFT椹卞姩鐢垫祦
		SystemProperties.set(Tag.PERSYS_LVDS_DRIVE_CURRENT, XmlParse.default_lvds_drive_current);	//lvds椹卞姩鐢垫祦
		SystemProperties.set(Tag.PERSYS_DITHERING_OUTPUT , XmlParse.default_dithering_output);	//dithering杈撳嚭鎺у埗

		// functions config
		SystemProperties.set(Tag.PERSYS_FUN_DVD_ENABLE, XmlParse.fun_dvd_enable + "");
		SystemProperties.set(Tag.PERSYS_FUN_GPS_ENABLE, XmlParse.fun_gps_enable + "");
		SystemProperties.set(Tag.PERSYS_FUN_FM_ENABLE, XmlParse.fun_fm_enable + "");
		SystemProperties.set(Tag.PERSYS_FUN_AVIN_ENABLE, XmlParse.fun_avin_enable + "");
		SystemProperties.set(Tag.PERSYS_FUN_AVIN2_ENABLE, XmlParse.fun_avin2_enable + "");
		SystemProperties.set(Tag.PERSYS_FUN_TV_ENABLE, XmlParse.fun_tv_enable + "");

		SystemProperties.set(Tag.PERSYS_FUN_IPOD_ENABLE, XmlParse.fun_ipod_enable + "");
		SystemProperties.set(Tag.PERSYS_FUN_MUSIC_ENABLE, XmlParse.fun_music_enable + "");
		SystemProperties.set(Tag.PERSYS_FUN_VIDEO_ENABLE, XmlParse.fun_video_enable + "");
		//SystemProperties.set(Tag.PERSYS_FUN_BT_ENABLE, XmlParse.fun_bt_enable + "");
		SystemProperties.set(Tag.PERSYS_FUN_DVR_ENABLE, XmlParse.fun_dvr_enable + "");
		SystemProperties.set(Tag.PERSYS_FUN_DVR_UART_ENABLE, XmlParse.fun_dvr_uart_enable + "");
		SystemProperties.set(Tag.PERSYS_FUN_MICRACAST_ENABLE, XmlParse.fun_micracast_enable + "");
		SystemProperties.set(Tag.PERSYS_FUN_TPMS_ENABLE, XmlParse.fun_tpms_enable + "");
		
		SystemProperties.set(Tag.PERSYS_FUN_CARLIFE_ENABLE, XmlParse.fun_carlife_enable + "");
		SystemProperties.set(Tag.PERSYS_FUN_IMG_ENABLE, XmlParse.fun_img_enable + "");
		
		// qicaideng
		SystemProperties.set(Tag.PERSYS_QICAIDENG_LIGHT_MODE, XmlParse.light_mode + "");
		SystemProperties.set(Tag.PERSYS_QICAIDENG_LIGHT_R, XmlParse.light_r + "");
		SystemProperties.set(Tag.PERSYS_QICAIDENG_LIGHT_G, XmlParse.light_g + "");
		SystemProperties.set(Tag.PERSYS_QICAIDENG_LIGHT_B, XmlParse.light_b + "");
//		SystemProperties.set(Tag.PERSYS_TOUCHKEYLEAN_KBTN, XmlParse.touch_key_btn);
//		SystemProperties.set(Tag.PERSYS_TOUCHKEYLEAN_KTHRESOLD, Util.pos_threshold + "");

		SystemProperties.set(Tag.PERSYS_VOICE_TOUCH, XmlParse.voice_touch_enable == 1 ? "true"
				: "false");
		
		SystemProperties.set(Tag.PERSYS_VOICE_WAKEUP, XmlParse.voice_wakeup_enable == 1 ? "true"
				: "false");

		// assistive touch
		for (int i = 0; i < Tag.PERSYS_ASSISTIVE_TOUCH.length; i++) {
			SystemProperties.set(Tag.PERSYS_ASSISTIVE_TOUCH[i],
					XmlParse.assistive_touch_enable[i] == 1 ? "true" : "false");
		}

		// funs other
		for (int i = 0; i < Tag.PERSYS_FUNS_OTHER.length; i++) {
			SystemProperties.set(Tag.PERSYS_FUNS_OTHER[i], XmlParse.funs_other[i] == 1 ? "true"
					: "false");
		}

		SystemProperties.set(Tag.PERSYS_BOOT_LOGO_ENABLE, XmlParse.boot_logo_enable == 1 ? "true"
				: "false");
		SystemProperties.set(Tag.PERSYS_SLEEP_READY_TIME, XmlParse.sleep_ready_time + "");
		SystemProperties.set(Tag.PERSYS_SLEEP_TIME, XmlParse.sleep_time + "");
		SystemProperties.set(Tag.PERSYS_SLEEP_POWER_ENABLE, XmlParse.sleep_power_enable == 1 ? "true"
				: "false");
	}

}
