package jp.modal.soul.KeikyuTimeTable.migration;

import jp.modal.soul.KeikyuTimeTable.model.DatabaseHelper;
import android.content.Context;
import android.content.SharedPreferences;

public class BaseState {
	Context context;
	static String preferenceName;

	DatabaseHelper dbHelper;
	
	public BaseState(Context context) {
		this.context = context;
		dbHelper = new DatabaseHelper(context);
	}

	/**
	 * ステータスの保存
	 * @param status
	 */
	void setStatus(String key, int state) {
		SharedPreferences sp = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		sp.edit().putInt(key, state).commit();
	}
	/**
	 * ステータスの取得
	 * @return
	 */
	int getStatus(String key) {
		SharedPreferences sp = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		return sp.getInt(key, 0);
	}
}
