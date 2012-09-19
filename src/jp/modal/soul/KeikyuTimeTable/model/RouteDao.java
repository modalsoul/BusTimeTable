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
 * �H���������N���X
 * @author M
 *
 */
public class RouteDao extends Dao {

	/**
	 * �����f�[�^
	 */
	private String[][] initicalData = new String[][]{
			{"1","�X50 ���M��w ��X�w �䂫", "13", "1", "1,2,3,4,5,6,7,8,9,10,11,12,13"},
			{"2","�X50 ���M��w ���c�w �䂫", "1", "13", "13,12,11,10,9,8,7,6,5,4,3,2,1"}
	};
	
	/** �e�[�u���� */
	public static final String TABLE_NAME = "route";
	
	// �J��������`
	/** �H��ID�@*/
	public static final String COLUMN_ID = "id";
	/** �H���� */
	public static final String COLUMN_ROUTE_NAME = "route_name";
	/** �I���o�X��ID */
	public static final String COLUMN_TERMINAL = "terminal";
	/** �n���o�X��ID */
	public static final String COLUMN_STARTING = "starting";
	/** �o�X�� */
	public static final String COLUMN_BUS_STOPS = "bus_stops";
	
	
	// �J�������z���`
	public static final String[] COLUMNS = {
											COLUMN_ID,
											COLUMN_ROUTE_NAME,
											COLUMN_TERMINAL,
											COLUMN_STARTING,
											COLUMN_BUS_STOPS};
	
	// create table����`
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
	 * �R���X�g���N�^
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
	 * ���X�g�擾
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
		// �Q�ƌn�ł�Readable���[�h
		SQLiteDatabase db = getReadableDatabase();
		
		// �Q�ƌn�����̎��{
		Cursor cursor = null;
		cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		
		// ���s���ʎ擾
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
	 * �H��ID���Ɏ擾
	 * @return
	 */
	public ArrayList<RouteItem> queryRouteOrderById() {
		String orderBy = COLUMN_ID + " asc";
		return queryList(COLUMNS, null, null, null, null, orderBy, null);
	}
	
	/**
	 * �w�肳�ꂽ�H��ID�̏����擾
	 * @param routeId �擾�Ώۂ̘H��ID
	 * @return
	 */
	public RouteItem queryAllBusStopByRouteId(long routeId) {
		String selection = COLUMN_ID + " = ?";

		String[] selectionArgs = new String[1];
		selectionArgs[0] = Long.toString(routeId);
		ArrayList<RouteItem> routeItems =  queryList(COLUMNS, selection, selectionArgs, null, null, null, null);
		
		if(routeItems.size() != 1){
			// �V�X�e���G���[
			return null;
		}
		return routeItems.get(0);
	}
	
	/**
	 * �V�K�쐬
	 * SQLiteDatabase�I�u�W�F�N�g��open,close�͊O���ōs��
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
			// �����f�[�^�̃o�X��A�C�e���̐ݒ�
			item = new RouteItem();
			item.id = Long.parseLong(data[0]);
			item.routeName = data[1];
			item.terminal = Long.parseLong(data[2]);
			item.starting = Long.parseLong(data[3]);
			item.busStops = data[4];

			try {
				// DB�փC���T�[�g				
				Log.e("**********************",Long.toString(insertWithoutOpenDb(db, item)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		db.close();
	}
	
	
}
