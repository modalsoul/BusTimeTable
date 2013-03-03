package jp.modal.soul.KeikyuTimeTable.activity;


import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.model.BusStopDao;
import jp.modal.soul.KeikyuTimeTable.model.RouteDao;
import jp.modal.soul.KeikyuTimeTable.model.TimeTableDao;
import jp.modal.soul.KeikyuTimeTable.util.Utils;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends Activity {

	/** DAO */
	private RouteDao routeDao;
	private BusStopDao busStopDao;
	private TimeTableDao timeTableDao;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        
        // DAOのセットアップ
        setupDao();
        // 初回起動時のセットアップ
        setupInit();
        
        Button routeButton = (Button)findViewById(R.id.route_menu_button);
        
        Button historyButton = (Button)findViewById(R.id.history_menu_button);
        
        routeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchRouteList();
			}
		});
    }
    public void launchRouteList() {
    	// BusStopActivityを起動するintentの作成
    	Intent intent = new Intent(getApplicationContext(), RouteListActivity.class);
    	// BusStopActivityの起動
    	Utils.intentLauncher(this, intent);
    }
    /**
     * Daoのセットアップ
     */
    private void setupDao() {
    	routeDao = new RouteDao(getApplicationContext());
    	busStopDao = new BusStopDao(getApplicationContext());
    	timeTableDao = new TimeTableDao(getApplicationContext());
    }
	/**
	 * アプリ初回起動時の初期化処理
	 */
	public void setupInit() {
		InitState initState = new InitState();
		// 初回起動の判定
		if(initState.getStatus() == InitState.PREFERENCE_INIT) {
			// 初回起動の場合、初期データをセット
			busStopDao.setup();
			routeDao.setup();
			timeTableDao.setup();
			// 起動状態を変更
			initState.setStatus(InitState.PREFERENCE_BOOTED);
		}
	}
	/**
	 * 起動ステータスの共有プリファレンスのクラス
	 * TODO 別ファイルへの切り出し
	 * @author M
	 *
	 */
	public class InitState {
		/** 共有プリファレンス名 */
		public static final String INIT_PREFERENCE_NAME = "InitState";
		// 起動ステータスの定数
		/** 未起動　*/
		public static final int PREFERENCE_INIT = 0;
		/** 起動 */
		public static final int PREFERENCE_BOOTED = 1;

		/**
		 * 起動ステータスの保存
		 * @param status
		 */
		void setStatus(int state) {
			SharedPreferences sp = getSharedPreferences(INIT_PREFERENCE_NAME, MODE_PRIVATE);
			sp.edit().putInt(INIT_PREFERENCE_NAME, state).commit();
		}
		/**
		 * 起動ステータスの取得
		 * @return
		 */
		int getStatus() {
			SharedPreferences sp = getSharedPreferences(INIT_PREFERENCE_NAME, MODE_PRIVATE);
			return sp.getInt(INIT_PREFERENCE_NAME, 0);
		}
	}

}