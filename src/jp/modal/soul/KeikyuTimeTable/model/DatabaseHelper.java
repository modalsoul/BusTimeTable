package jp.modal.soul.KeikyuTimeTable.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

import jp.modal.soul.KeikyuTimeTable.R;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    /** ログ用タグ */
    public final String TAG = this.getClass().getSimpleName();

    /** DBのバージョン番号 */
    public static final int DB_VERSION = 3;
    
    private SQLiteDatabase mDataBase;  
    private final Context mContext; 
    private String DB_FULL_PATH;
    private String DB_ASSET;

    /**
     * コンストラクタ
     * @param context
     */
    public DatabaseHelper(Context context) {
    	super(context, context.getResources().getString(R.string.db_name), null, DB_VERSION);
    	this.mContext = context;
    	this.DB_FULL_PATH = mContext.getResources().getString(R.string.db_path) + mContext.getResources().getString(R.string.db_name);
    	this.DB_ASSET = mContext.getResources().getString(R.string.db_name_asset);
    }

    public void createEmptyDataBase() throws IOException{  
        boolean dbExist = checkDataBaseExists();  
  
        if(dbExist){  
            // すでにデータベースは作成されている  
        	
        }else{  
            // このメソッドを呼ぶことで、空のデータベースが  
            // アプリのデフォルトシステムパスに作られる  
            this.getReadableDatabase();  
   
            try {  
                // asset に格納したデータベースをコピーする  
                copyDataBaseFromAsset();   
            } catch (IOException e) {  
                throw new Error("Error copying database");  
            } finally {
            	close();
            }
        }  
    }
    
    private void copyDataBaseFromAsset() throws IOException{  
    	   
        // asset 内のデータベースファイルにアクセス  
        InputStream mInput = mContext.getAssets().open(DB_ASSET);  
   
        // デフォルトのデータベースパスに作成した空のDB  
        String outFileName = mContext.getResources().getString(R.string.db_path) + mContext.getResources().getString(R.string.db_name);  
   
        OutputStream mOutput = new FileOutputStream(outFileName);  
  
        // コピー  
        byte[] buffer = new byte[1024];  
        int size;  
        while ((size = mInput.read(buffer)) > 0){  
            mOutput.write(buffer, 0, size);  
        }  
   
        //Close the streams  
        mOutput.flush();  
        mOutput.close();  
        mInput.close();  
    }  
   
    public SQLiteDatabase openDataBaseReadable() throws SQLException{  
        //Open the database  
        mDataBase = SQLiteDatabase.openDatabase(DB_FULL_PATH, null, SQLiteDatabase.OPEN_READONLY);  
        return mDataBase;  
    }
    public SQLiteDatabase openDataBaseWritable() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_FULL_PATH, null, SQLiteDatabase.OPEN_READWRITE);  
        return mDataBase;
    }
      
    @Override  
    public void onCreate(SQLiteDatabase arg0) {  
    }  
  
    @Override  
    public synchronized void close() {  
        if(mDataBase != null)  
            mDataBase.close();  
      
        super.close();  
    }  
    
    private boolean checkDataBaseExists() {  
        SQLiteDatabase checkDb = null;  
   
        try{  
            checkDb = SQLiteDatabase.openDatabase(DB_FULL_PATH, null, SQLiteDatabase.OPEN_READONLY);  
        }catch(SQLiteException e){  
            // データベースはまだ存在していない  
        }  
   
        if(checkDb != null){  
            checkDb.close();  
        }  
        return checkDb != null ? true : false;  
    }
    
    public boolean updateDatabase() {
    	SQLiteDatabase db = getWritableDatabase();
    	HistoryDao dao = new HistoryDao(mContext);
    	// 履歴情報の待避
    	ArrayList<HistoryItem> items = dao.queryLatestHistory();
    	db.close();
    	try {
    		// データベースファイルの置き換え
			copyDataBaseFromAsset();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	// 更新が古い順に並べ直し
    	Collections.reverse(items);

    	SQLiteDatabase newDb = getWritableDatabase();
    	// 履歴情報を戻す
    	boolean flag = true;
    	for(HistoryItem item: items) {
    		try {
				dao.insertOrReplace(newDb, item);
			} catch (Exception e) {
				flag = false;
				e.printStackTrace();
			} 
    	}
    	newDb.close();
    	return flag;
    }

    /**
     * マイグレーション用メソッド
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}