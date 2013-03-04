package jp.modal.soul.KeikyuTimeTable.model;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 路線を扱うクラス
 * @author M
 *
 */
public class HistoryDao extends Dao {

	/** テーブル名 */
	public static final String TABLE_NAME = "history";

	// カラム名定義
	/** 履歴ID　*/
	public static final String COLUMN_ID = "id";
	/** 路線ID */
	public static final String COLUMN_ROUTE_ID = "route_id";
	/** バス停ID */
	public static final String COLUMN_BUS_STOP_ID = "bus_stop_id";
	
	/** 履歴取得上限 */
	private static final String HISTORY_LIMIT = "5";

	// カラム名配列定義
	public static final String[] COLUMNS = {
											COLUMN_ID,
											COLUMN_ROUTE_ID,
											COLUMN_BUS_STOP_ID};

	// create table文定義
	public static final String CREATE_TABLE;
	static {
		// @formatter:off
		String columnDefine = COLUMN_ID + " integer primary key, "
							+ COLUMN_ROUTE_ID + " integer not null, "
							+ COLUMN_BUS_STOP_ID + " integer not null, "
							;
		// @formatter:off
		CREATE_TABLE = createTable(TABLE_NAME, columnDefine);
	}

	/**
	 * コンストラクタ
	 * @param context
	 */
	public HistoryDao(Context context) {
		super(context);	
	}

	public static HistoryItem getHistoryItem(Cursor cursor){
		HistoryItem historyItem = new HistoryItem();
		historyItem.id = cursor.getInt(0);
		historyItem.routeId = cursor.getInt(1);
		historyItem.busStopId = cursor.getInt(2);

		return historyItem;
	}
	/**
	 * リスト取得
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	private ArrayList<HistoryItem> queryList(String[] columns, String selection, String[] selectionArgs,  String groupBy, 
			String having, String orderBy, String limit) {
		// 参照系ではReadableモード
		SQLiteDatabase db = getReadableDatabase();

		// 参照系処理の実施
		Cursor cursor = null;
		cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

		// 実行結果取得
		ArrayList<HistoryItem> itemList = new ArrayList<HistoryItem>();
		while (cursor.moveToNext()) {
			HistoryItem item = getHistoryItem(cursor);
			itemList.add(item);
		}

		// DB close
		cursor.close();
		db.close();

		return itemList;
	}

	/**
	 * 更新順に最大件数分取得
	 * @return
	 */
	public ArrayList<HistoryItem> queryLatestHistory() {
		String orderBy = COLUMN_UPDATE_DATE + " desc ";
		String limit = null;
		return queryList(COLUMNS, null, null, null, null, orderBy, limit);
	}

	/**
	 * 新規作成
	 * SQLiteDatabaseオブジェクトのopen,closeは外部で行う
	 * @param db
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public long insertWithoutOpenDb(SQLiteDatabase db, HistoryItem item) throws Exception {
		ContentValues values = new  ContentValues();
//		values.put(COLUMN_ID, item.id);
		values.put(COLUMN_ROUTE_ID, item.routeId);
		values.put(COLUMN_BUS_STOP_ID, item.busStopId);

		long result = db.insert(TABLE_NAME, null, values);
		if(result == Dao.RETURN_CODE_INSERT_FAIL) {
			throw new Exception("insert exception");
		}
		return result;
	}

}