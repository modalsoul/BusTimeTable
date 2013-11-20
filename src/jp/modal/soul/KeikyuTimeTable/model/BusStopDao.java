package jp.modal.soul.KeikyuTimeTable.model;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * バス停を扱うクラス
 * @author M
 *
 */
public class BusStopDao extends Dao {

	/** テーブル名 */
	public static final String TABLE_NAME = "bus_stop";

	// カラム名定義
	/** バス停ID　*/
	public static final String COLUMN_ID = "id";
	/** 路線ID */
	public static final String COLUMN_ROUTE_ID = "route_id";
	/** バス停名 */
	public static final String COLUMN_BUS_STOP_NAME = "bus_stop_name";
	/** 運行情報取得用バス停ID */
	public static final String COLUMN_SEARCH = "search";
	
	// カラム名配列定義
	public static final String[] COLUMNS = {
											COLUMN_ID,
											COLUMN_ROUTE_ID,
											COLUMN_BUS_STOP_NAME,
											COLUMN_SEARCH};

	// create table文定義
	public static final String CREATE_TABLE;
	static {
		// @formatter:off
		String columnDefine = COLUMN_ID + " integer primary key autoincrement, "
							+ COLUMN_ROUTE_ID + " integer not null"
							+ COLUMN_BUS_STOP_NAME + " text not null, "
							+ COLUMN_SEARCH + " text"
							;
		// @formatter:off
		CREATE_TABLE = createTableNoDate(TABLE_NAME, columnDefine);
	}

	/**
	 * コンストラクタ
	 * @param context
	 */
	public BusStopDao(Context context) {
		super(context);	
	}

	public static BusStopItem getBusStopItem(Cursor cursor){
		BusStopItem busStopItem = new BusStopItem();
		busStopItem.id = cursor.getInt(0);
		busStopItem.routeId = cursor.getInt(1);
		busStopItem.busStopName = cursor.getString(2);
		busStopItem.search = cursor.getInt(3);

		return busStopItem;
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
	private ArrayList<BusStopItem> queryList(String[] columns, String selection, String[] selectionArgs,  String groupBy, 
			String having, String orderBy, String limit) {
		// 参照系ではReadableモード
		SQLiteDatabase db = getReadableDatabase();

		// 参照系処理の実施
		Cursor cursor = null;
		cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

		// 実行結果取得
		ArrayList<BusStopItem> itemList = new ArrayList<BusStopItem>();
		while (cursor.moveToNext()) {
			BusStopItem item = getBusStopItem(cursor);
			itemList.add(item);
		}
		// DB close
		cursor.close();
		db.close();

		return itemList;
	}

	/**
	 * バス停ID順に取得
	 * @return
	 */
	public ArrayList<BusStopItem> queryBusStopOrderById() {
		String orderBy = COLUMN_ID + " asc";
		return queryList(COLUMNS, null, null, null, null, orderBy, null);
	}

	/**
	 * 指定されたバス停IDの情報を取得
	 * @return
	 */
	public ArrayList<BusStopItem> queryBusStopById(String[] selectionArgs) {
		String selection = COLUMN_ID + " = ?";
		int i = 0;
		while(selectionArgs.length > i) {
			selection += "OR ?";
			i++;
		}
		return queryList(COLUMNS, selection, selectionArgs, null, null, null, null);
	}

	public ArrayList<BusStopItem> queryBusStop(String busStopId) {
		String[] selectionArgs = new String[]{busStopId};
		String selection = COLUMN_ID + " = ?";
		return queryList(COLUMNS, selection, selectionArgs, null, null, null, null);
	}
	
	public ArrayList<BusStopItem> queryBusStopByName(String name) {
		String[] wordList = name.replaceAll("　", " ").split("[\\s]+");
		String[] selectionArgs;
		String selection;
		if(wordList.length == 1) {
			selectionArgs = new String[]{"%" + name + "%"};
			selection = COLUMN_BUS_STOP_NAME + " like ?";
		} else {
			selectionArgs = new String[wordList.length];
			selectionArgs[0] = "%" + wordList[0] + "%";
			selection = COLUMN_BUS_STOP_NAME + " like ?";
			for(int i = 1; i < wordList.length; i++) {
				selectionArgs[i] = "%" + wordList[i] + "%";
				selection += " and " + COLUMN_BUS_STOP_NAME + " like ?";
			}
		}
		return queryList(COLUMNS, selection, selectionArgs, null, null, null, null);
	}
	
	public int queryTerminalBusSearchIDByRouteID(int routeId) {
		String[] selectionArgs = new String[]{Integer.toString(routeId)};
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select distinct search from bus_stop where bus_stop_name = (select terminal from route where id = ?)";
		Cursor cursor = null;
		cursor = db.rawQuery(sql, selectionArgs);
		int res = 0;
		while (cursor.moveToNext()) {
			res = cursor.getInt(0);
		}
		// DB close
		cursor.close();
		db.close();
		return res;
	}

	/**
	 * 新規作成
	 * SQLiteDatabaseオブジェクトのopen,closeは外部で行う
	 * @param db
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public long insertWithoutOpenDb(SQLiteDatabase db, BusStopItem item) throws Exception {
		ContentValues values = new  ContentValues();
		values.put(COLUMN_ID, item.id);
		values.put(COLUMN_BUS_STOP_NAME, item.busStopName);
		values.put(COLUMN_SEARCH, item.search);
		
		long result = db.insert(TABLE_NAME, null, values);
		if(result == Dao.RETURN_CODE_INSERT_FAIL) {
			throw new Exception("insert exception");
		}
		return result;
	}
}