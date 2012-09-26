package jp.modal.soul.KeikyuTimeTable.model;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * �o�X��������N���X
 * @author M
 *
 */
public class TimeTableDao extends Dao {

	/**
	 * �����f�[�^
	 */
	private String[][] initicalData = new String[][]{

	};
		
	/** �e�[�u���� */
	public static final String TABLE_NAME = "time_table";
	
	// �J��������`
	/** ����ID�@*/
	public static final String COLUMN_ID = "id";
	/** �o�X��ID�@*/
	public static final String COLUMN_BUS_STOP_ID = "bus_stop_id";
	/** �H��ID�@*/
	public static final String COLUMN_ROUTE_ID = "route_id";
	/** �j���^�C�v */
	public static final String COLUMN_TYPE = "type";
	/** ���� */
	public static final String COLUMN_STARTING_TIME = "starting_time";

	
	
	// �J�������z���`
	public static final String[] COLUMNS = {
											COLUMN_ID,
											COLUMN_BUS_STOP_ID,
											COLUMN_ROUTE_ID,
											COLUMN_TYPE,
											COLUMN_STARTING_TIME};
	
	// create table����`
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
	 * �R���X�g���N�^
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
	private ArrayList<TimeTableItem> queryList(String[] columns, String selection, String[] selectionArgs,  String groupBy, 
			String having, String orderBy, String limit) {
		// �Q�ƌn�ł�Readable���[�h
		SQLiteDatabase db = getReadableDatabase();
		
		// �Q�ƌn�����̎��{
		Cursor cursor = null;
		cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		
		// ���s���ʎ擾
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
	 * ����ID���Ɏ擾
	 * @return
	 */
	public ArrayList<TimeTableItem> queryBusStopOrderById(String[] selectionArgs) {
		String selection = COLUMN_BUS_STOP_ID + " = ? AND " + COLUMN_ROUTE_ID + " = ? " + COLUMN_TYPE + " = ? ";
		String orderBy = COLUMN_ID + " asc";
		return queryList(COLUMNS, selection, selectionArgs, null, null, orderBy, null);
	}
	
	/**
	 * �V�K�쐬
	 * SQLiteDatabase�I�u�W�F�N�g��open,close�͊O���ōs��
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
			// �����f�[�^�̃o�X��A�C�e���̐ݒ�
			item = new TimeTableItem();
			item.id = Long.parseLong(data[0]);
			item.busStopId = Long.valueOf(data[1]);
			item.routeId = Long.valueOf(data[2]);
			item.type = Integer.valueOf(data[3]);
			item.startingTime = data[4];
			
			try {
				// DB�փC���T�[�g				
				insertWithoutOpenDb(db, item);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		db.close();
	}
	
	
}
