package jp.modal.soul.KeikyuTimeTable.model;

import android.R.bool;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Dao {
	/** ���O�o�͗p�@�^�O */
	public final String TAG = this.getClass().getSimpleName();
	/** �X�e�[�^�X�R�[�h�萔 */
	public static final int RETURN_CODE_INSERT_FAIL = -1;
	public static final int RETURN_CODE_UPDATE_FAIL = 0;
	
	public Context context;
	private DatabaseHelper dbHelper;
	
	// �e�[�u���̋��ʃJ������
	/** ���R�[�h�쐬���� */
	public static final String COLUMN_CREATE_DATE = "create_date"; 
	/** ���R�[�h�X�V���� */
	public static final String COLUMN_UPDATE_DATE = "update_date";
	
	// �e�[�u����create��
	public static String createTable(String tableName, String columnDefine) {
		// @formatter:off
		return "create table " + tableName + " ( "
				+ columnDefine 
				+ COLUMN_CREATE_DATE + " text, "
				+ COLUMN_UPDATE_DATE + " text "
				+ ")";
		// @formatter:on
	}
	
	/**
	 * �R���X�g���N�^
	 * @param context
	 */
	public Dao(Context context) {
		this.context = context.getApplicationContext();
		dbHelper = new DatabaseHelper(context);
	}
	/**
	 * SQLiteDatabase�̎擾
	 * db�����b�N���̏ꍇ�A���������܂ő҂�
	 * @param isWritable
	 * @return
	 */
	private SQLiteDatabase getDatabase(boolean isWritable) {
		SQLiteDatabase db = null;
		while (db == null) {
			try {
				db = isWritable ? dbHelper.getWritableDatabase() : dbHelper.getReadableDatabase();
			} catch (Exception e) {
				sleep();
			} catch (Error e) {
				sleep();
			}
		}
		return db;
	}
	/**
	 * �X���[�v����
	 */
	private void sleep() {
		try {
			Thread.sleep(100);
		} catch(InterruptedException e) {
			// to crush exception
		}
	}
	/**
	 * DB��Readable���[�h�ŊJ��
	 * �f�B�X�N�e�ʕs�����̂݁Aread-only��DB���I�[�v������
	 * @return
	 */
	public final SQLiteDatabase getReadableDatabase() {
		return getDatabase(false);
	}

	/**
	 * DB��Writable���[�h�ŊJ��
	 * �������݉\�ȏ�Ԃ�DB���I�[�v������B
	 * �f�B�X�N�e�ʕs�����ASQLException����������B�Q�Ƃ݂̂̏ꍇ�ł��ASQLException�ł������B
	 * �f�B�X�N�e�ʕs�������Q�Ə����𑱍s����ꍇ�AgetReadableDatabase���g�p
	 * @return
	 */
	public final SQLiteDatabase getWritableDatabase() {
		return getDatabase(true);
	}
}
