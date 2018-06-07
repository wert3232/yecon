package com.yecon.sound.setting.unitl;

public class Tag {
    
    public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    public static final String MCU_ACTION_LOUDNESS_KEY = "com.yecon.action.LOUDNESS_KEY";
    public final static String ACTION_RESET_FACTORY = "com.yecon.action.FACTORY_RESET";
    public static final String ACTION_SETTING_AUDIO_EXIT = "com.yecon.setting.audio.exit";
    // effect
    public static final String eq_type = "eq_key";
    public static final String loudNess_enable = "loudNess_enable";
    public static final String loudness = "loudNess_values";
    public static final String bass = "bass_values";
    public static final String bass_angle = "bass_angle";
    public static final String treble = "treble_values";
    public static final String treble_angle = "treble_angle";
    public static final String alto = "alto_values";
    public static final String alto_angle = "alto_angle";
    public static final String subwoofer_enable = "subwoofer_enable";
    public static final String subwoofer = "subwoofer_values";
    public static final String subwoofer_angle = "subwoofer_angle";
    public static final String ReverbCoef = "ReverbCoef_values";

    public static final String eq_type_self = "eq_type_self";
    public static final String eq_self_value = "eq_self_value";

    public static final String back_front_value = "back_front_value";
    public static final String left_right_value = "left_right_value";

	public static final String pointX_tag = "pointXx";
	public static final String pointY_tag = "pointYx";
	public static final String valueX_tag = "valueX";
	public static final String valueY_tag = "valueY";

	public static final float RANGLE = 128;
	public final static String PERSYS_AUDIO[] = { "persist.sys.audio_treble",
			"persist.sys.audio_alto", "persist.sys.audio_bass", "persist.sys.audio_subwoofer",
			"persist.sys.audio_loundness", "persist.sys.balance_f_r", "persist.sys.balance_l_r" };
	public static int audio[] = { 0, 0, 0, 0, 15, 0, 0 };

	public final static String PERSYS_SUBWOOFER_ENABLE = "persist.sys.fun.audio.subwoofer";
	public final static String PERSYS_ALTO_ENABLE = "persist.sys.fun.audio.alto";
}
