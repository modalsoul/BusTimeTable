package jp.modal.soul.KeikyuTimeTable.model;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * �o�X��������N���X
 * @author M
 *
 */
public class BusStopDao extends Dao {

	/**
	 * �����f�[�^
	 */
	private String[][] initicalData = new String[][]{
			{"1","���c�w"},
			{"2","����ߋ�"},
			{"3","���c�꒚��"},
			{"4","���M��w"},
			{"5","��X���l����"},
			{"6","�x�m����(��c��)"},
			{"7","��X���񒚖�"},
			{"8","��c�ʂ�"},
			{"9","��X�k�l����"},
			{"10","��X�k�O����"},
			{"11","�����ʂ�"},
			{"12","�m�s�s��X�O"},
			{"13","��X�w"}
	};
	
	/** �e�[�u���� */
	public static final String TABLE_NAME = "bus_stop";
	
	// �J��������`
	/** �o�X��ID�@*/
	public static final String COLUMN_ID = "id";
	/** �o�X�▼ */
	public static final String COLUMN_BUS_STOP_NAME = "bus_stop_name";

	
	
	// �J�������z���`
	public static final String[] COLUMNS = {
											COLUMN_ID,
											COLUMN_BUS_STOP_NAME };
	
	// create table����`
	public static final String CREATE_TABLE;
	static {
		// @formatter:off
		String columnDefine = COLUMN_ID + " integer primary key, "
							+ COLUMN_BUS_STOP_NAME + " text not null, "
							;
		// @formatter:off
		CREATE_TABLE = createTable(TABLE_NAME, columnDefine);
	}
	
	/**
	 * �R���X�g���N�^
	 * @param context
	 */
	public BusStopDao(Context context) {
		super(context);	
	}
	
	public static BusStopItem getBusStopItem(Cursor cursor){
		BusStopItem busStopItem = new BusStopItem();
		busStopItem.id = cursor.getInt(0);
		busStopItem.busStopName = cursor.getString(1);
		
		return busStopItem;
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
	private ArrayList<BusStopItem> queryList(String[] columns, String selection, String[] selectionArgs,  String groupBy, 
			String having, String orderBy, String limit) {
		// �Q�ƌn�ł�Readable���[�h
		SQLiteDatabase db = getReadableDatabase();
		
		// �Q�ƌn�����̎��{
		Cursor cursor = null;
		cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		
		// ���s���ʎ擾
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
	 * �o�X��ID���Ɏ擾
	 * @return
	 */
	public ArrayList<BusStopItem> queryBusStopOrderById() {
		String orderBy = COLUMN_ID + " asc";
		return queryList(COLUMNS, null, null, null, null, orderBy, null);
	}
	
	/**
	 * �V�K�쐬
	 * SQLiteDatabase�I�u�W�F�N�g��open,close�͊O���ōs��
	 * @param db
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public long insertWithoutOpenDb(SQLiteDatabase db, BusStopItem item) throws Exception {
		ContentValues values = new  ContentValues();
		values.put(COLUMN_ID, item.id);
		values.put(COLUMN_BUS_STOP_NAME, item.busStopName);
		
		long result = db.insert(TABLE_NAME, null, values);
		if(result == Dao.RETURN_CODE_INSERT_FAIL) {
			throw new Exception("insert exception");
		}
		return result;
	}
	
	public void setup() {
		BusStopItem item;
		
		SQLiteDatabase db = getWritableDatabase();
		for( String[] data: initicalData) {
			// �����f�[�^�̃o�X��A�C�e���̐ݒ�
			item = new BusStopItem();
			item.id = Long.parseLong(data[0]);
			item.busStopName = data[1];			
			try {
				// DB�փC���T�[�g				
				insertWithoutOpenDb(db, item);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
