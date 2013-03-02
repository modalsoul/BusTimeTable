package jp.modal.soul.KeikyuTimeTable.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * バス停を扱うクラス
 * @author M
 *
 */
public class TimeTableDao extends Dao {

	/**
	 * 初期データ
	 */
	private int[][] ROUTE_LIST = {{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}, {13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2}};

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

    // 時刻表の曜日タイプ定義
	public static final int WEEKDAY = 0;
	public static final int SATURDAY = 1;
	public static final int HOLIDAY = 2;

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
							+ COLUMN_ROUTE_ID + " integer not null, "
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

	/**
	 * CursorをTimeTableItemへ変換して返却
	 * @param cursor
	 * @return TimeTableItem
	 */
	public static TimeTableItem getTimeTableItem(Cursor cursor){
		TimeTableItem timeTableItem = new TimeTableItem();
		timeTableItem.id = cursor.getInt(0);
		timeTableItem.busStopId = cursor.getInt(1);
		timeTableItem.routeId = cursor.getInt(2);
		timeTableItem.type = cursor.getInt(3);
		timeTableItem.startingTime = cursor.getString(4);
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
		String selection = COLUMN_BUS_STOP_ID + " = ? AND " + COLUMN_ROUTE_ID + " = ? AND " + COLUMN_TYPE + " = ? ";
		String orderBy = COLUMN_ID + " asc";
		return queryList(COLUMNS, selection, selectionArgs, null, null, orderBy, null);
	}

	public ArrayList<TimeTableItem> queryALL() {
		String orderBy = COLUMN_ID + " asc";
		return queryList(COLUMNS, null, null, null, null, orderBy, null);
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

	/**
	 * TimeTableテーブルの初期化処理
	 */
	public void setup() {
		TimeTableItem item;

		ArrayList<String> initialData = getInitialData();

		SQLiteDatabase db = getWritableDatabase();
		Long id = 0L;
		for( String rawData: initialData) {
			// 初期データの時刻アイテムの設定
			String[] data = rawData.split(",");
			item = new TimeTableItem();
			item.id = id;
			item.routeId = Integer.valueOf(data[0]);
			item.busStopId = Integer.valueOf(data[1]);
			item.type = Integer.valueOf(data[2]);
			item.startingTime = data[3];

			try {
				insertWithoutOpenDb(db, item);
			} catch (Exception e) {
				e.printStackTrace();
			}
			id++;
		}
		db.close();
	}

	/**
	 * assetsの時刻データの取得
	 * @return 路線ID,バス停ID,曜日タイプ,時刻
	 */
	public ArrayList<String> getInitialData() {
		ArrayList<String> initialDataList = new ArrayList<String>();

		for(int route = 0; route < ROUTE_LIST.length; route++ ) {
			ArrayList<String> timeList = new ArrayList<String>();
			for(int busStop: ROUTE_LIST[route]) {
				timeList = getTimeList(route+1, busStop);
				for(String time: timeList) {
					initialDataList.add(route+1 + "," + busStop + "," + time);
				}
			}
		}
		return initialDataList;
	}

	private ArrayList<String> getTimeList(int route, int busStop) {
		AssetManager as = null;
		ArrayList<String> timeList = new ArrayList<String>();
		try {
			as = context.getResources().getAssets();
			InputStream is = as.open(route + "/" + busStop);

			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String str;
			while((str = reader.readLine()) != null) {
				timeList.add(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return timeList;
	}




}