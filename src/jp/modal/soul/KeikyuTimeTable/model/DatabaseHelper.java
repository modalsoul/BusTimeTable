package jp.modal.soul.KeikyuTimeTable.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    /** ���O�p�^�O */
    public final String TAG = this.getClass().getSimpleName();
    
    /** DB�� */
    private static final String DB_NAME = "kqbtt";
    
    /**
     * DB�̃o�[�W�����ԍ�
     */
    private static final int DB_VERSION = 1;
    
    /**
     * �R���X�g���N�^
     * @param context
     */
    public DatabaseHelper(Context context) {
    	super(context, DB_NAME, null, DB_VERSION);
    }
    
    /**
     * �e�[�u����`�p���\�b�h
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.beginTransaction();
    	try {
    		db.execSQL(BusStopDao.CREATE_TABLE);
    		db.execSQL(RouteDao.CREATE_TABLE);
    		db.setTransactionSuccessful();
    	} finally {
    		db.endTransaction();
    	}
    	
    }
    /**
     * �}�C�O���[�V�����p���\�b�h
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
