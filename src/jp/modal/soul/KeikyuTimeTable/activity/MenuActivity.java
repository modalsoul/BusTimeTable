package jp.modal.soul.KeikyuTimeTable.activity;


import java.io.IOException;
import java.util.ArrayList;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.model.BusStopDao;
import jp.modal.soul.KeikyuTimeTable.model.BusStopItem;
import jp.modal.soul.KeikyuTimeTable.model.DatabaseHelper;
import jp.modal.soul.KeikyuTimeTable.model.HistoryDao;
import jp.modal.soul.KeikyuTimeTable.model.HistoryItem;
import jp.modal.soul.KeikyuTimeTable.model.RouteDao;
import jp.modal.soul.KeikyuTimeTable.model.RouteItem;
import jp.modal.soul.KeikyuTimeTable.model.TimeTableDao;
import jp.modal.soul.KeikyuTimeTable.util.Utils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

public class MenuActivity extends BaseActivity {
	
	/** DAO */
	private RouteDao routeDao;
	private BusStopDao busStopDao;
	private HistoryDao historyDao;
	
	/** View */
	private TextView title;
	private TextView unofficial;
	private Button routeButton;
	private Button historyButton;
	
	/** ItemList */
	ArrayList<HistoryItem> historyItemList;
	
	GoogleAnalytics analytics;
	Tracker tracker;
	
	Uri uri;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the intent that started this Activity.
        Intent intent = this.getIntent();
        uri = intent.getData();
        
        setContentView(R.layout.menu);
        
        // DAOのセットアップ
        setupDao();
        // 初回起動時のセットアップ
        setupInit();
        
        setupView();
        
        setupGA();

    }


	private void setupGA() {
		analytics = GoogleAnalytics.getInstance(this);
        tracker = analytics.getTracker(getResources().getString(R.string.ga_trackingId));
        EasyTracker.getInstance().setContext(this);
        if (uri != null) {
            if(uri.getQueryParameter("utm_source") != null) {    // Use campaign parameters if avaialble.
              EasyTracker.getTracker().setCampaign(uri.getPath()); 
            } else if (uri.getQueryParameter("referrer") != null) {    // Otherwise, try to find a referrer parameter.
              EasyTracker.getTracker().setReferrer(uri.getQueryParameter("referrer"));
            }
          }
	}


	private void setupView() {
		title = (TextView)findViewById(R.id.menu_title);
        setFont(title);
        
        unofficial = (TextView)findViewById(R.id.unofficial);
        setFont(unofficial);
        
        routeButton = (Button)findViewById(R.id.route_menu_button);
        setFont(routeButton);
        
        historyButton = (Button)findViewById(R.id.history_menu_button);
        setFont(historyButton);
        
        routeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchRouteList();
			}
		});
        
        historyButton.setOnClickListener(onClickListener);
	}
    View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
			builder.setTitle("履歴から選択");
			
			builder.setSingleChoiceItems(getDialogList(), -1, historyOnClickListener);
			
			builder.show();
		}
	};
	
	DialogInterface.OnClickListener historyOnClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			HistoryItem item = historyItemList.get(which);
			Log.e("hoge", item.routeId + ":" + item.busStopId);
			launchBusStop((int)item.routeId, (int)item.busStopId);
		}
		
	};
	
	public CharSequence[] getDialogList() {
		historyDao = new HistoryDao(this);
		historyItemList = historyDao.queryLatestHistory();
		CharSequence[] dialogItem = new CharSequence[historyItemList.size()];
		int i = 0;
		for(HistoryItem item: historyItemList) {
			RouteItem routeItem = routeDao.queryRouteByRouteId(Long.valueOf(item.routeId));

			ArrayList<BusStopItem> busstopItemList = busStopDao.queryBusStop(String.valueOf(item.busStopId));
			dialogItem[i] = makeHistoryRow(routeItem.terminal, busstopItemList.get(0).busStopName);
			i++;
		}
		return dialogItem;
	}
	
	private String makeHistoryRow(String routeName, String busstopName) {
		return routeName + "ゆき\n" + busstopName +"バス停";
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
    	new TimeTableDao(getApplicationContext());
    }
	/**
	 * アプリ初回起動時の初期化処理
	 */
	public void setupInit() {
		InitState initState = new InitState();
		// 初回起動の判定
		if(initState.getStatus() == InitState.PREFERENCE_INIT) {
			// 初回起動の場合、初期データをセット
			DatabaseHelper dbHelper = new DatabaseHelper(this);
			try {
				dbHelper.createEmptyDataBase();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			busStopDao.setup();
//			routeDao.setup();
//			timeTableDao.setup();
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
	/**
	 * 選択されたバス停の行き先を選択する画面へ遷移する
	 */
	public void launchBusStop(int route, int busStop){
		// BusStopActivityを起動するintentの作成
		Intent intent = new Intent(getApplicationContext(), TimeTableActivity.class);
		// 選択されたバス停番号を指定
		intent.putExtra(TimeTableActivity.BUSS_STOP_NUMBER, busStop);
		// 選択された路線番号を指定
		intent.putExtra(TimeTableActivity.ROUTE_NUMBER, route);
		// BusStopActivityの起動
		Utils.intentLauncher(this, intent);
	}
	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
		tracker.trackView("/menu");
	}
	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
}