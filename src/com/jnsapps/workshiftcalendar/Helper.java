package com.jnsapps.workshiftcalendar;


/**
 * @author Joaquín Navarro Salmerón
 *
 */
public class Helper {
	
	public static String formatInterval(float hours){
		long hr;
		if (hours >= 0) {
			hr = Math.round(android.util.FloatMath.floor(hours));
		} else {
			hr = Math.round(android.util.FloatMath.ceil(hours));
		}
        long min = Math.round((hours-hr)*60);
        return String.format("%02dh %02dm", hr, min);
    }
	
	public static String formatInterval(long minutes){
		long hr = minutes / 60;
        long min = minutes - hr * 60;
        return String.format("%02dh %02dm", hr, min);
    }
	
	public static long getCompleteHours(float hours){
		if (hours >= 0) {
			return Math.round(android.util.FloatMath.floor(hours));
		} else {
			return Math.round(android.util.FloatMath.ceil(hours));
		}
    }
	
	public static long getMinutesLeft(float hours){
		long hr;
		if (hours >= 0) {
			hr = Math.round(android.util.FloatMath.floor(hours));
		} else {
			hr = Math.round(android.util.FloatMath.ceil(hours));
		}
        return Math.round((hours-hr)*60);
	}

}
