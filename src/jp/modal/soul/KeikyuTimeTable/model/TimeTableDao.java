package jp.modal.soul.KeikyuTimeTable.model;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * バス停を扱うクラス
 * @author M
 *
 */
public class TimeTableDao extends Dao {

	/**
	 * 初期データ
	 */
	private String[][] initicalData = new String[][]{

	};
		
	/** テーブル名 */
	public static final String TABLE_NAME = "time_table";
	
	// カラム名定義
	/** 時刻ID　*/
	public static final String COLUMN_ID = "id";
	/** バス停ID　*/
	public static final String COLUMN_BUS_STOP_ID = "bus_stop_id";
	/** 路線ID　*/
	public static final String COLUMN_ROUTE_ID = "route_id";
	/** 曜日タイプ */
	public static final String COLUMN_TYPE = "type";
	/** 時刻 */
	public static final String COLUMN_STARTING_TIME = "starting_time";

	
	
	// カラム名配列定義
	public static final String[] COLUMNS = {
											COLUMN_ID,
											COLUMN_BUS_STOP_ID,
											COLUMN_ROUTE_ID,
											COLUMN_TYPE,
											COLUMN_STARTING_TIME};
	
	// create table文定義
	public static final String CREATE_TABLE;
	static {
		// @formatter:off
		String columnDefine = COLUMN_ID + " integer primary key, "
							+ COLUMN_BUS_STOP_ID + " integer not null, "
							+ COLUMN_BUS_STOP_ID + " integer not null, "
							+ COLUMN_TYPE + " integer not null, "
							+ COLUMN_STARTING_TIME + " text not null, "
							;
		// @formatter:off
		CREATE_TABLE = createTable(TABLE_NAME, columnDefine);
	}
	
	/**
	 * コンストラクタ
	 * @param context
	 */
	public TimeTableDao(Context context) {
		super(context);	
	}
	
	public static TimeTableItem getTimeTableItem(Cursor cursor){
		TimeTableItem timeTableItem = new TimeTableItem();
		timeTableItem.id = cursor.getInt(0);
		timeTableItem.busStopId = Long.valueOf(cursor.getString(1));
		timeTableItem.routeId = Long.valueOf(cursor.getString(2));
		timeTableItem.startingTime = cursor.getString(3);
		
		return timeTableItem;
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
	private ArrayList<TimeTableItem> queryList(String[] columns, String selection, String[] selectionArgs,  String groupBy, 
			String having, String orderBy, String limit) {
		// 参照系ではReadableモード
		SQLiteDatabase db = getReadableDatabase();
		
		// 参照系処理の実施
		Cursor cursor = null;
		cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		
		// 実行結果取得
		ArrayList<TimeTableItem> itemList = new ArrayList<TimeTableItem>();
		while (cursor.moveToNext()) {
			TimeTableItem item = getTimeTableItem(cursor);
			itemList.add(item);
		}
		
		// DB close
		cursor.close();
		db.close();
		
		return itemList;
	}
	
	/**
	 * 時刻ID順に取得
	 * @return
	 */
	public ArrayList<TimeTableItem> queryBusStopOrderById(String[] selectionArgs) {
		String selection = COLUMN_BUS_STOP_ID + " = ? AND " + COLUMN_ROUTE_ID + " = ? " + COLUMN_TYPE + " = ? ";
		String orderBy = COLUMN_ID + " asc";
		return queryList(COLUMNS, selection, selectionArgs, null, null, orderBy, null);
	}
	
	/**
	 * 新規作成
	 * SQLiteDatabaseオブジェクトのopen,closeは外部で行う
	 * @param db
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public long insertWithoutOpenDb(SQLiteDatabase db, TimeTableItem item) throws Exception {
		ContentValues values = new  ContentValues();
		values.put(COLUMN_ID, item.id);
		values.put(COLUMN_BUS_STOP_ID, item.busStopId);
		values.put(COLUMN_ROUTE_ID, item.routeId);
		values.put(COLUMN_TYPE, item.type);
		values.put(COLUMN_STARTING_TIME, item.startingTime);
		
		long result = db.insert(TABLE_NAME, null, values);
		if(result == Dao.RETURN_CODE_INSERT_FAIL) {
			throw new Exception("insert exception");
		}
		return result;
	}
	
	public void setup() {
		TimeTableItem item;
		
		SQLiteDatabase db = getWritableDatabase();
		for( String[] data: initicalData) {
			// 初期データのバス停アイテムの設定
			item = new TimeTableItem();
			item.id = Long.parseLong(data[0]);
			item.busStopId = Long.valueOf(data[1]);
			item.routeId = Long.valueOf(data[2]);
			item.type = Integer.valueOf(data[3]);
			item.startingTime = data[4];
			
			try {
				// DBへインサート				
				insertWithoutOpenDb(db, item);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		db.close();
	}
	
	
}
