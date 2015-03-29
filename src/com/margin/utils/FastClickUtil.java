package com.margin.utils;

import android.util.Log;


public class FastClickUtil {
	private static long lastClickTime = 0;
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		Log.i("Click", String.valueOf(timeD));
		if ( 0 < timeD && timeD < 1000) {
			return true;
		}
		lastClickTime = time;
		return false;
	}
}
