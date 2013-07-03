package jp.modal.soul.KeikyuTimeTable.migration;

import jp.modal.soul.KeikyuTimeTable.model.DatabaseHelper;
import android.content.Context;

public class DatabaseState extends BaseState {
	static final String DATABASE_PREFERENCE = "DB_STATE";
	static final String KEY_NAME = "DB_VERSION";
	
	public DatabaseState(Context context) {
		super(context);
		preferenceName = DATABASE_PREFERENCE;
	}
	
	public void checkState() {
		if(getStatus(KEY_NAME) != DatabaseHelper.DB_VERSION) {
			if(dbHelper.updateDatabase()) setStatus(KEY_NAME, DatabaseHelper.DB_VERSION);
		}
	}
}
