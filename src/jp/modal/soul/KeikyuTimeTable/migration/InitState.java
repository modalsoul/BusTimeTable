package jp.modal.soul.KeikyuTimeTable.migration;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 起動ステータスの共有プリファレンスのクラス
 * TODO 別ファイルへの切り出し
 * @author M
 *
 */
public class InitState {
	/** 共有プリファレンス名 */
	public static final String INIT_PREFERENCE_NAME = "InitState";
	// 起動ステータスの定数
	/** 未起動　*/
	public static final int PREFERENCE_INIT = 0;
	/** 起動 */
	public static final int PREFERENCE_BOOTED = 1;

	Context context;
	
	public InitState(Context context) {
		this.context = context;
	}
	/**
	 * 起動ステータスの保存
	 * @param status
	 */
	public void setStatus(int state) {
		SharedPreferences sp = context.getSharedPreferences(INIT_PREFERENCE_NAME, Context.MODE_PRIVATE);
		sp.edit().putInt(INIT_PREFERENCE_NAME, state).commit();
	}
	/**
	 * 起動ステータスの取得
	 * @return
	 */
	public int getStatus() {
		SharedPreferences sp = context.getSharedPreferences(INIT_PREFERENCE_NAME, Context.MODE_PRIVATE);
		return sp.getInt(INIT_PREFERENCE_NAME, 0);
	}
}
