package com.yecon.carsetting.fragment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.autochips.dvp.DvdLogicManager;
import com.yecon.carsetting.R;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.tzutil;
import com.yecon.metazone.YeconMetazone;

public class Fragment_About extends DialogFragment {
	// view
	private Context mContext;
	private TextView textView[] = new TextView[7];
	// info
	private static final String FILENAME_MSV = "/sys/board_properties/soc/msv";
	private static final String FILENAME_PROC_VERSION = "/proc/version";

	public Fragment_About() {

	}

	private void initData() {
		mContext = getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setStyle(DialogFragment.STYLE_NO_FRAME, 0);
		initData();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Window window = getDialog().getWindow();
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.general_about_main, container);
		initView(rootView, mContext);
		return rootView;
	}

	private void initView(View mView, Context context) {
		textView[0] = (TextView) mView.findViewById(R.id.about_model);
		textView[1] = (TextView) mView.findViewById(R.id.about_androidVersion);
		textView[2] = (TextView) mView.findViewById(R.id.about_mcuVersion);
		textView[3] = (TextView) mView.findViewById(R.id.about_mcuID);
		textView[4] = (TextView) mView.findViewById(R.id.about_dvdVersion);
		if (SystemProperties.getInt(Tag.PERSYS_FUN_DVD_ENABLE, 1) == 0)
			textView[4].setVisibility(View.GONE);
		textView[5] = (TextView) mView.findViewById(R.id.about_kernelVersion);
		textView[6] = (TextView) mView.findViewById(R.id.about_Version);

		textView[0].setText(mContext.getResources().getString(R.string.general_about_model,
				Function.getCpuType()));
		textView[1].setText(mContext.getResources().getString(
				R.string.general_about_androidVersion, Build.VERSION.RELEASE));
		textView[2].setText(mContext.getResources().getString(R.string.general_about_mcuVersion,
				tzutil.getmcuVersion()));
		textView[3].setText(mContext.getResources().getString(R.string.general_about_mcuID,
				tzutil.getmcuID()));
		DvdLogicManager mLogiccd = DvdLogicManager.getInstance();
		String dvdVersion = mLogiccd.getFirmwareVer() + "-" + mLogiccd.getServoVer();
		textView[4].setText(mContext.getResources().getString(R.string.general_about_dvdVersion,
				dvdVersion));
		textView[5].setText(mContext.getResources().getString(R.string.general_about_kernelVersion,
				getFormattedKernelVersion()));
		
		String cpuMode = Function.getCpuType();
		String version="";
		if (cpuMode.contains("8317")) {
			version = Build.DISPLAY.replaceAll("8317", "8317");
		}else if (cpuMode.contains("8327")) {
			version = Build.DISPLAY.replaceAll("8317", "8327");
		}else if (cpuMode.contains("8217")) {
			version = Build.DISPLAY.replaceAll("8317", "8217");
		}else if (cpuMode.contains("8227")) {
			version = Build.DISPLAY.replaceAll("8317", "8227");
		}
		
		textView[6].setText(mContext.getResources().getString(R.string.general_about_Version,
				version));
	}

	/**
	 * Returns " (ENGINEERING)" if the msv file has a zero value, else returns
	 * "".
	 * 
	 * @return a string to append to the model numbe r description.
	 */
	private String getMsvSuffix() {
		// Production devices should have a non-zero value. If we can't read it,
		// assume it's a
		// production device so that we don't accidentally show that it's an
		// ENGINEERING device.
		try {
			String msv = readLine(FILENAME_MSV);
			// Parse as a hex number. If it evaluates to a zero, then it's an
			// engineering build.
			if (Long.parseLong(msv, 16) == 0) {
				return " (ENGINEERING)";
			}
		} catch (IOException ioe) {
			// Fail quietly, as the file may not exist on some devices.
		} catch (NumberFormatException nfe) {
			// Fail quietly, returning empty string should be sufficient
		}
		return "";
	}

	/**
	 * Reads a line from the specified file.
	 * 
	 * @param filename
	 *            the file to read from
	 * @return the first line, if any.
	 * @throws IOException
	 *             if the file couldn't be read
	 */
	private static String readLine(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
		try {
			return reader.readLine();
		} finally {
			reader.close();
		}
	}

	// kernel version
	public static String getFormattedKernelVersion() {
		try {
			return formatKernelVersion(readLine(FILENAME_PROC_VERSION));

		} catch (IOException e) {
			// L.e("","IO Exception when getting kernel version for Device Info screen",
			// e);

			return "Unavailable";
		}
	}

	public static String formatKernelVersion(String rawKernelVersion) {
		// Example (see tests for more):
		// Linux version 3.0.31-g6fb96c9 (android-build@xxx.xxx.xxx.xxx.com) \
		// (gcc version 4.6.x-xxx 20120106 (prerelease) (GCC) ) #1 SMP PREEMPT \
		// Thu Jun 28 11:02:39 PDT 2012

		final String PROC_VERSION_REGEX = "Linux version (\\S+) " + /*
																	 * group 1:
																	 * "3.0.31-g6fb96c9"
																	 */
		"\\((\\S+?)\\) " + /* group 2: "x@y.com" (kernel builder) */
		"(?:\\(gcc.+? \\)) " + /* ignore: GCC version information */
		"(#\\d+) " + /* group 3: "#1" */
		"(?:.*?)?" + /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
		"((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /*
											 * group 4:
											 * "Thu Jun 28 11:02:39 PDT 2012"
											 */

		Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(rawKernelVersion);
		if (!m.matches()) {
			// Log.e(LOG_TAG, "Regex did not match on /proc/version: " +
			// rawKernelVersion);
			return "Unavailable";
		} else if (m.groupCount() < 4) {
			// Log.e(LOG_TAG, "Regex match on /proc/version only returned " +
			// m.groupCount()
			// + " groups");
			return "Unavailable";
		}
		return m.group(1) + "\n" + // 3.0.31-g6fb96c9
				m.group(2) + " " + m.group(3) + // x@y.com #1
				" " + m.group(4); // Thu Jun 28 11:02:39 PDT 2012
	}

}
