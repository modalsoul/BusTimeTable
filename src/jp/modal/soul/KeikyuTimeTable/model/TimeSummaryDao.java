package jp.modal.soul.KeikyuTimeTable.model;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * バス停を扱うクラス
 * @author M
 *
 */
public class TimeSummaryDao extends Dao {

	/** テーブル名 */
	public static final String TABLE_NAME = "time_summary";

	// カラム名定義
	/** バス停ID　*/
	public static final String COLUMN_BUS_STOP_ID = "bus_stop_id";
	/** 路線ID　*/
	public static final String COLUMN_ROUTE_ID = "route_id";
	/** 曜日タイプ */
	public static final String COLUMN_TYPE = "type";
	/** 時間 */
	public static final String COLUMN_HOUR = "hour";
	/** 位置 */
	public static final String COLUMN_POSITION = "position";
	
	
    // 時刻表の曜日タイプ定義
	public static final int WEEKDAY = 0;
	public static final int SATURDAY = 1;
	public static final int HOLIDAY = 2;

	// カラム名配列定義
	public static final String[] COLUMNS = {
											COLUMN_BUS_STOP_ID,
											COLUMN_ROUTE_ID,
											COLUMN_TYPE,
											COLUMN_HOUR,
											COLUMN_POSITION};

	// create table文定義
	public static final String CREATE_TABLE;
	static {
		// @formatter:off
		String columnDefine = COLUMN_BUS_STOP_ID + " integer not null, "
							+ COLUMN_ROUTE_ID + " integer not null, "
							+ COLUMN_TYPE + " integer not null, "
							+ COLUMN_HOUR + " integer not null, "
							+ COLUMN_POSITION + " integer not null, "
							;
		// @formatter:off
		CREATE_TABLE = createTable(TABLE_NAME, columnDefine);
	}

	/**
	 * コンストラクタ
	 * @param context
	 */
	public TimeSummaryDao(Context context) {
		super(context);
	}

	/**
	 * CursorをTimeTableItemへ変換して返却
	 * @param cursor
	 * @return TimeTableItem
	 */
	public static TimeSummaryItem getTimeSummaryItem(Cursor cursor){
		TimeSummaryItem timeSummaryItem = new TimeSummaryItem();
		timeSummaryItem.busStopId = cursor.getInt(0);
		timeSummaryItem.routeId = cursor.getInt(1);
		timeSummaryItem.type = cursor.getInt(2);
		timeSummaryItem.hour = cursor.getInt(3);
		timeSummaryItem.position = cursor.getInt(4);
		return timeSummaryItem;
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
	private ArrayList<TimeSummaryItem> queryList(String[] columns, String selection, String[] selectionArgs,  String groupBy,
			String having, String orderBy, String limit) {
		// 参照系ではReadableモード
		SQLiteDatabase db = getReadableDatabase();

		// 参照系処理の実施
		Cursor cursor = null;
		cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

		// 実行結果取得
		ArrayList<TimeSummaryItem> itemList = new ArrayList<TimeSummaryItem>();
		while (cursor.moveToNext()) {
			TimeSummaryItem item = getTimeSummaryItem(cursor);
			itemList.add(item);
		}

		// DB close
		cursor.close();
		db.close();

		return itemList;
	}

	/**
	 * 時刻順に取得
	 * @return
	 */
	public ArrayList<TimeSummaryItem> querySummaryOrderByHour(String[] selectionArgs) {
		String selection = COLUMN_BUS_STOP_ID + " = ? AND " + COLUMN_ROUTE_ID + " = ? AND " + COLUMN_TYPE + " = ? ";
		String orderBy = COLUMN_HOUR + " asc";
		return queryList(COLUMNS, selection, selectionArgs, null, null, orderBy, null);
	}
}