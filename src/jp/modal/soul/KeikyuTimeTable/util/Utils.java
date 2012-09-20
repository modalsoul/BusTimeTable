package jp.modal.soul.KeikyuTimeTable.util;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;

public class Utils {
	
	public static void intentLauncher(Activity activity, Intent intent){
		activity.startActivity(intent);
		
	}
	/**
	 * long型のリストをString型のリストに変換
	 * @param longList
	 * @return
	 */
	public static String[] longItems2StringItems(long[] longList) {
		String[] stringItems = new String[longList.length];
		int i  = 0;
		for(long item: longList) {
			stringItems[i] = Long.toString(item);
			i++;
		}
		return stringItems;
	}

	/**
	 * カンマ区切りのバス停IDをパースする
	 * @param busStopIds
	 * @return
	 */
	public static String[] busStopIdString2StringItems(String busStopIds) {
		return busStopIds.split(",");
	}
	
}
