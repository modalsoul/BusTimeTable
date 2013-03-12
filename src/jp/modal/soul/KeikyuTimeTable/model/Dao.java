package jp.modal.soul.KeikyuTimeTable.model;

import android.R.bool;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Dao {
	/** ログ出力用　タグ */
	public final String TAG = this.getClass().getSimpleName();
	/** ステータスコード定数 */
	public static final int RETURN_CODE_INSERT_FAIL = -1;
	public static final int RETURN_CODE_UPDATE_FAIL = 0;

	public Context context;
	private DatabaseHelper dbHelper;

	// テーブルの共通カラム名
	/** レコード作成日時 */
	public static final String COLUMN_CREATE_DATE = "create_date"; 
	/** レコード更新日時 */
	public static final String COLUMN_UPDATE_DATE = "update_date";

	// テーブルのcreate文
	public static String createTable(String tableName, String columnDefine) {
		// @formatter:off
		return "create table " + tableName + " ( "
				+ columnDefine 
				+ COLUMN_CREATE_DATE + " text not null, "
				+ COLUMN_UPDATE_DATE + " text not null"
				+ ")";
		// @formatter:on
	}
	public static String createTableNoDate(String tableName, String columnDefine) {
		// @formatter:off
		return "create table " + tableName + " ( "
				+ columnDefine
				+ ")";
		// @formatter:on
	}

	/**
	 * コンストラクタ
	 * @param context
	 */
	public Dao(Context context) {
		this.context = context.getApplicationContext();
		dbHelper = new DatabaseHelper(context);
	}
	/**
	 * SQLiteDatabaseの取得
	 * dbがロック中の場合、解除されるまで待つ
	 * @param isWritable
	 * @return
	 */
	private SQLiteDatabase getDatabase(boolean isWritable) {
		SQLiteDatabase db = null;
		while (db == null) {
			try {
				db = isWritable ? dbHelper.getWritableDatabase() : dbHelper.getReadableDatabase();
			} catch (Exception e) {
				sleep();
			} catch (Error e) {
				sleep();
			}
		}
		return db;
	}
	/**
	 * スリープする
	 */
	private void sleep() {
		try {
			Thread.sleep(100);
		} catch(InterruptedException e) {
			// to crush exception
		}
	}
	/**
	 * DBをReadableモードで開く
	 * ディスク容量不足時のみ、read-onlyでDBをオープンする
	 * @return
	 */
	public final SQLiteDatabase getReadableDatabase() {
		return getDatabase(false);
	}

	/**
	 * DBをWritableモードで開く
	 * 書き込み可能な状態でDBをオープンする。
	 * ディスク容量不足時、SQLExceptionが投げられる。参照のみの場合でも、SQLExceptionでも同じ。
	 * ディスク容量不足時も参照処理を続行する場合、getReadableDatabaseを使用
	 * @return
	 */
	public final SQLiteDatabase getWritableDatabase() {
		return getDatabase(true);
	}
}