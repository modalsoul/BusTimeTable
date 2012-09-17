package jp.modal.soul.KeikyuTimeTable.util;

import android.app.Activity;
import android.content.Intent;

public class Utils {
	
	public static void intentLauncher(Activity activity, Intent intent){
		activity.startActivity(intent);
		
	}
	public static String[] longItems2StringItems(long[] longList) {
		String[] stringItems = new String[longList.length];
		int i  = 0;
		for(long item: longList) {
			stringItems[i] = Long.toString(item);
			i++;
		}
		return stringItems;
	}

	public static String[] busStopIdString2StringItems(String busStopIds) {
		return null;
	}
}
