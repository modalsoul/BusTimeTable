package jp.modal.soul.KeikyuTimeTable.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    /** ログ用タグ */
    public final String TAG = this.getClass().getSimpleName();

    /** DB名 */
    private static final String DB_NAME = "kqbtt";

    /**
     * DBのバージョン番号
     */
    private static final int DB_VERSION = 1;

    /**
     * コンストラクタ
     * @param context
     */
    public DatabaseHelper(Context context) {
    	super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * テーブル定義用メソッド
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.beginTransaction();
    	try {
    		db.execSQL(BusStopDao.CREATE_TABLE);
    		db.execSQL(RouteDao.CREATE_TABLE);
    		db.execSQL(TimeTableDao.CREATE_TABLE);
    		db.execSQL(HistoryDao.CREATE_TABLE);
    		db.setTransactionSuccessful();
    	} finally {
    		db.endTransaction();
    	}

    }
    /**
     * マイグレーション用メソッド
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}