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

	/**
	 * 初期データ
	 */
//	private String[][] initicalData = new String[][]{
//			{"1","蒲田駅"},
//			{"2","あやめ橋"},
//			{"3","蒲田一丁目"},
//			{"4","東邦大学"},
//			{"5","大森西四丁目"},
//			{"6","富士見橋(大田区)"},
//			{"7","大森西二丁目"},
//			{"8","沢田通り"},
//			{"9","大森北四丁目"},
//			{"10","大森北三丁目"},
//			{"11","八幡通り"},
//			{"12","ＮＴＴ大森前"},
//			{"13","大森駅"}
//	};

	/** テーブル名 */
	public static final String TABLE_NAME = "bus_stop";

	// カラム名定義
	/** バス停ID　*/
	public static final String COLUMN_ID = "id";
	/** WebID */
	public static final String COLUMN_WEB_ID = "web_id";
	/** バス停名 */
	public static final String COLUMN_BUS_STOP_NAME = "bus_stop_name";
	/** ロケーション */
	public static final String COLUMN_LOCATION = "location";
	
	// カラム名配列定義
	public static final String[] COLUMNS = {
											COLUMN_ID,
											COLUMN_WEB_ID,
											COLUMN_BUS_STOP_NAME,
											COLUMN_LOCATION};

	// create table文定義
	public static final String CREATE_TABLE;
	static {
		// @formatter:off
		String columnDefine = COLUMN_ID + " integer primary key autoincrement, "
							+ COLUMN_WEB_ID + " text not null"
							+ COLUMN_BUS_STOP_NAME + " text not null, "
							+ COLUMN_LOCATION + " text"
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
		busStopItem.web_id = cursor.getString(1);
		busStopItem.busStopName = cursor.getString(2);
		busStopItem.location = cursor.getString(3);

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

	public ArrayList<BusStopItem> queryBusStop(String selectionArg) {
		String[] selectionArgs = new String[]{selectionArg};

		String selection = COLUMN_ID + " = ?";

		return queryList(COLUMNS, selection, selectionArgs, null, null, null, null);

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
		values.put(COLUMN_WEB_ID, item.web_id);
		values.put(COLUMN_BUS_STOP_NAME, item.busStopName);
		values.put(COLUMN_LOCATION, item.location);
		
//		long createDate = new Date().getTime();
//		values.put(COLUMN_CREATE_DATE, createDate);
//		values.put(COLUMN_UPDATE_DATE, createDate);


		long result = db.insert(TABLE_NAME, null, values);
		if(result == Dao.RETURN_CODE_INSERT_FAIL) {
			throw new Exception("insert exception");
		}
		return result;
	}

//	public void setup() {
//		BusStopItem item;
//
//		SQLiteDatabase db = getWritableDatabase();
//		
//		db.beginTransaction();
//		try {
//			for( String[] data: initicalData) {
//				// 初期データのバス停アイテムの設定
//				item = new BusStopItem();
//				item.id = Long.parseLong(data[0]);
//				item.busStopName = data[1];			
//					// DBへインサート				
//					insertWithoutOpenDb(db, item);
//			}
//			db.setTransactionSuccessful();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.endTransaction();
//		}
//		db.close();
//	}


}