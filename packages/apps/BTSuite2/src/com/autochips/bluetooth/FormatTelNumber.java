package com.autochips.bluetooth;

import java.util.Locale;

public class FormatTelNumber {

	private static int  _get_country_code_len(String lpszNumber)
	{
		String country_code[] = {"60", "62", "63", "65", "66", "673",
			"81", "82", "84", "850", "852", "853", "855", "856", "86","886", "880",
		"90","91","92","93","94","95","960","961","962","963","964","965",
		"7","30","31","32","33","34","351","49","39","41","44",
		"20","212","213","216","218",
		"1","1808","51","52","53","55","61","64"};
		if (lpszNumber != null && !lpszNumber.isEmpty() && lpszNumber.charAt(0) == '+')
		{
			for (int i = 0; i < country_code.length; i++)
			{
				String str = lpszNumber.substring(1, lpszNumber.length());
				int nPos = str.indexOf(country_code[i]);
				if (nPos == 0)
				{
					return country_code[i].length() + 1;
				}
			}
		}
	
		return 0;
	}

	static public String ui_format_tel_number(String lpszNumber)
	{
		if (lpszNumber == null || lpszNumber.isEmpty()) {
			return "";
		}
	    String languagecode = Locale.getDefault().toString();
	    if(!languagecode.equalsIgnoreCase("zh_CN") && !languagecode.equalsIgnoreCase("zh_TW")){
	    	return lpszNumber;
	    }
	
		String strOriginal = lpszNumber;
		if (lpszNumber.charAt(0) == '0' && lpszNumber.length() >= 3)	// 固话,区号0开头
		{
			int zero_code_len = 4;	// 大多数为四位
			if ((lpszNumber.charAt(1) == '1' && lpszNumber.charAt(2) == '0')		// 010
				|| lpszNumber.charAt(1) == '2')								// 02X
			{
				// 区号为3位
				zero_code_len = 3;
			}
			if (lpszNumber.length() > zero_code_len 
				&& (lpszNumber.length() - zero_code_len <= 8))
			{
				strOriginal = String.format("%s-%s", lpszNumber.substring(0, zero_code_len), 
						lpszNumber.substring(zero_code_len, lpszNumber.length()));
			}
		}
		// 手机号码, 9到11位时才格式化
		else if (lpszNumber.length() >= 9	&& lpszNumber.length() <= 11 &&
			(lpszNumber.indexOf("13") == 0 || lpszNumber.indexOf("15") == 0 || lpszNumber.indexOf("18") == 0  ))
		{
			strOriginal = String.format("%s-%s-%s", lpszNumber.substring(0, 3), 
					lpszNumber.substring(3, 7), lpszNumber.substring(7, lpszNumber.length()));
		}
		else if (lpszNumber.charAt(0) == '+')		// 国家区号
		{
			int country_code_len = _get_country_code_len(lpszNumber);
			if (country_code_len > 0 && country_code_len < 8)
			{
				String strafterformat = ui_format_tel_number(lpszNumber.substring(country_code_len, lpszNumber.length()));
				if (!strafterformat.isEmpty()) {
					strOriginal = String.format("%s %s", lpszNumber.substring(0, country_code_len), strafterformat);
				}
			}
		}
		else
		{
			// 其它不格式化
		}
		return strOriginal;
	}

}
