package jp.modal.soul.KeikyuTimeTable.migration;

import java.io.IOException;

import jp.modal.soul.KeikyuTimeTable.model.DatabaseHelper;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 起動ステータスの共有プリファレンスのクラス
 * @author M
 *
 */
public class InitState extends BaseState {
	/** 共有プリファレンス名 */
	public static final String INIT_PREFERENCE_NAME = "INIT_STATE";
	public static final String KEY_NAME = "BOOT_STATE";
	// 起動ステータスの定数
	/** 未起動　*/
	public static final int PREFERENCE_INIT = 0;
	/** 起動 */
	public static final int PREFERENCE_BOOTED = 1;
	
	public InitState(Context context) {
		super(context);
		preferenceName = INIT_PREFERENCE_NAME;
	}
	
	public void checkState() {
		// 初回起動の判定
		if(getStatus(KEY_NAME) == InitState.PREFERENCE_INIT) {
			// 初回起動の場合、初期データをセット
			try {
				dbHelper.createEmptyDataBase();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 起動状態を変更
			setStatus(KEY_NAME, PREFERENCE_BOOTED);
		}	
	}
}
