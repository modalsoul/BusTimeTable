package jp.modal.soul.KeikyuTimeTable.model;

import java.util.ArrayList;

import jp.modal.soul.KeikyuTimeTable.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 路線を扱うクラス
 * @author M
 *
 */
public class RouteDao extends Dao {

	/**
	 * 初期データ
	 */
	private String[][] initicalData = new String[][]{
			{"1","森50 東邦大学 大森駅 ゆき", "13", "1", "1,2,3,4,5,6,7,8,9,10,11,12,13"},
			{"2","森50 東邦大学 蒲田駅 ゆき", "1", "13", "13,12,11,10,9,8,7,6,5,4,3,2,1"}
	};
	
	/** テーブル名 */
	public static final String TABLE_NAME = "route";
	
	// カラム名定義
	/** 路線ID　*/
	public static final String COLUMN_ID = "id";
	/** 路線名 */
	public static final String COLUMN_ROUTE_NAME = "route_name";
	/** 終着バス停ID */
	public static final String COLUMN_TERMINAL = "terminal";
	/** 始発バス停ID */
	public static final String COLUMN_STARTING = "starting";
	/** バス停 */
	public static final String COLUMN_BUS_STOPS = "bus_stops";
	
	
	// カラム名配列定義
	public static final String[] COLUMNS = {
											COLUMN_ID,
											COLUMN_ROUTE_NAME,
											COLUMN_TERMINAL,
											COLUMN_STARTING,
											COLUMN_BUS_STOPS};
	
	// create table文定義
	public static final String CREATE_TABLE;
	static {
		// @formatter:off
		String columnDefine = COLUMN_ID + " integer primary key, "
							+ COLUMN_ROUTE_NAME + " text not null, "
							+ COLUMN_TERMINAL + " integer not null, "
							+ COLUMN_STARTING + " integer not null, "
							+ COLUMN_BUS_STOPS + " text not null, "
							;
		// @formatter:off
		CREATE_TABLE = createTable(TABLE_NAME, columnDefine);
	}
	
	/**
	 * コンストラクタ
	 * @param context
	 */
	public RouteDao(Context context) {
		super(context);	
	}
	
	public static RouteItem getRouteItem(Cursor cursor){
		RouteItem routeItem = new RouteItem();
		routeItem.id = cursor.getInt(0);
		routeItem.routeName = cursor.getString(1);
		routeItem.terminal = cursor.getInt(2);
		routeItem.starting = cursor.getInt(3);
		routeItem.busStops = cursor.getString(4);
		
		return routeItem;
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
	private ArrayList<RouteItem> queryList(String[] columns, String selection, String[] selectionArgs,  String groupBy, 
			String having, String orderBy, String limit) {
		// 参照系ではReadableモード
		SQLiteDatabase db = getReadableDatabase();
		
		// 参照系処理の実施
		Cursor cursor = null;
		cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		
		// 実行結果取得
		ArrayList<RouteItem> itemList = new ArrayList<RouteItem>();
		while (cursor.moveToNext()) {
			RouteItem item = getRouteItem(cursor);
			itemList.add(item);
		}
		
		// DB close
		cursor.close();
		db.close();
		
		return itemList;
	}
	
	/**
	 * 路線ID順に取得
	 * @return
	 */
	public ArrayList<RouteItem> queryRouteOrderById() {
		String orderBy = COLUMN_ID + " asc";
		return queryList(COLUMNS, null, null, null, null, orderBy, null);
	}
	
	/**
	 * 指定された路線IDの情報を取得
	 * @param routeId 取得対象の路線ID
	 * @return
	 */
	public RouteItem queryAllBusStopByRouteId(long routeId) {
		String selection = COLUMN_ID + " = ?";

		String[] selectionArgs = new String[1];
		selectionArgs[0] = Long.toString(routeId);
		ArrayList<RouteItem> routeItems =  queryList(COLUMNS, selection, selectionArgs, null, null, null, null);
		
		if(routeItems.size() != 1){
			// システムエラー
			return null;
		}
		return routeItems.get(0);
	}
	
	/**
	 * 新規作成
	 * SQLiteDatabaseオブジェクトのopen,closeは外部で行う
	 * @param db
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public long insertWithoutOpenDb(SQLiteDatabase db, RouteItem item) throws Exception {
		ContentValues values = new  ContentValues();
		values.put(COLUMN_ID, item.id);
		values.put(COLUMN_ROUTE_NAME, item.routeName);
		values.put(COLUMN_TERMINAL, item.terminal);
		values.put(COLUMN_STARTING, item.starting);
		values.put(COLUMN_BUS_STOPS, item.busStops);
		
		long result = db.insert(TABLE_NAME, null, values);
		if(result == Dao.RETURN_CODE_INSERT_FAIL) {
			throw new Exception("insert exception");
		}
		return result;
	}
	
	public void setup() {
		RouteItem item;
		
		SQLiteDatabase db = getWritableDatabase();
		for( String[] data: initicalData) {
			// 初期データのバス停アイテムの設定
			item = new RouteItem();
			item.id = Long.parseLong(data[0]);
			item.routeName = data[1];
			item.terminal = Long.parseLong(data[2]);
			item.starting = Long.parseLong(data[3]);
			item.busStops = data[4];

			try {
				// DBへインサート				
				Log.e("**********************",Long.toString(insertWithoutOpenDb(db, item)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		db.close();
	}
	
	
}
