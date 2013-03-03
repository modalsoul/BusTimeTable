package jp.modal.soul.KeikyuTimeTable.activity;


import java.util.ArrayList;
import java.util.List;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.model.BusStopDao;
import jp.modal.soul.KeikyuTimeTable.model.BusStopItem;
import jp.modal.soul.KeikyuTimeTable.model.RouteDao;
import jp.modal.soul.KeikyuTimeTable.model.RouteItem;
import jp.modal.soul.KeikyuTimeTable.model.TimeTableDao;
import jp.modal.soul.KeikyuTimeTable.util.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class RouteListActivity extends Activity {

	/** バス停リストを表示するダイアログ */
	public AlertDialog busStopListDialog;

	/** 選択されたバス停の番号 */
	public int selectedBusStopNumber;
	/** 選択された路線ID */
	public int selectedRouteId;

	/** DAO */
	private RouteDao routeDao;
	private BusStopDao busStopDao;
	private TimeTableDao timeTableDao;

	/** ItemList */
	List<RouteItem> routeList;

	/** Adapter */
	RouteListAdapter adapter;

	/** ListView */
	ListView listView;

	/** list */
	String[] busStops;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // DAOのセットアップ
        setupDao();
        // 初回起動時のセットアップ
        setupInit();
        // ListViewのセットアップ
        setupListView();

    }
    /**
     * ListViewのセットアップ
     */
    public void setupListView() {
    	// 路線情報の取得
    	routeList = routeDao.queryRouteOrderById();
    	// Adapterの生成
    	adapter = new RouteListAdapter(this, R.layout.route_row, routeList);
    	// ListViewの取得
    	listView = (ListView)findViewById(R.id.line_list);
    	// アダプターの設定
    	listView.setAdapter(adapter);
    	// ListViewのアイテムがクリックされたときのコールバックリスナーを登録
        listView.setOnItemClickListener(onRouteItemClick);
    }
    /**
     * Daoのセットアップ
     */
    private void setupDao() {
    	routeDao = new RouteDao(getApplicationContext());
    	busStopDao = new BusStopDao(getApplicationContext());
    	timeTableDao = new TimeTableDao(getApplicationContext());
    }


	// 路線リストのアイテムがクリックされたときのリスナー
	AdapterView.OnItemClickListener onRouteItemClick = new AdapterView.OnItemClickListener() {
    	@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
    			long id) {
    		ListView listView = (ListView)parent;
    		// クリックされたアイテムの取得
    		RouteItem item = (RouteItem) listView.getItemAtPosition(position);

    		// 路線IDをセット
    		selectedRouteId = (int)item.id;

    		showBusStopList();

    	}
	};

	/**
	 *  動作のセットアップ
	 */
	private void setupEventhandling() {

	}

	/**
	 * バス停選択のダイアログを表示する
	 */
	private void showBusStopList() {
		// 路線情報を取得
		RouteItem routeItem = routeDao.queryAllBusStopByRouteId(selectedRouteId);

		if(routeItem == null) {
			// システムエラー
		}
		// バス停を取得
		busStops = Utils.busStopIdString2StringItems(routeItem.busStops);
		// バス停名を設定
		CharSequence[] busStopNames = new CharSequence[busStops.length];

		ArrayList<BusStopItem> busStopItems;
		int i = 0;
		for(String busStop: busStops) {

			busStopItems = busStopDao.queryBusStop(busStop);

			busStopNames[i] = busStopItems.get(0).busStopName;

			i++;
		}


		// バス停選択のリストを表示するダイアログを作成
		AlertDialog.Builder builder = new AlertDialog.Builder(RouteListActivity.this);
		builder.setTitle(R.string.bus_stop_list_dialog_title);
		builder.setSingleChoiceItems(busStopNames, -1, busStopOnClickListener);
		builder.show();
	}
	
	/**
	 * バス停ダイアログのバス停選択時のonClickListener
	 */
	DialogInterface.OnClickListener busStopOnClickListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// 選択されたバス停番号を設定
			selectedBusStopNumber = Integer.valueOf(busStops[which]);
			// バス停の行き先選択画面へ遷移
			launchBusStop();	
		}
	};

	/**
	 * 選択されたバス停の行き先を選択する画面へ遷移する
	 */
	public void launchBusStop(){
		// BusStopActivityを起動するintentの作成
		Intent intent = new Intent(getApplicationContext(), TimeTableActivity.class);
		// 選択されたバス停番号を指定
		intent.putExtra(TimeTableActivity.BUSS_STOP_NUMBER, selectedBusStopNumber);
		// 選択された路線番号を指定
		intent.putExtra(TimeTableActivity.ROUTE_NUMBER, selectedRouteId);
		// BusStopActivityの起動
		Utils.intentLauncher(this, intent);
//		this.startActivity(intent);
	}

	/**
	 * バス停名のリストを取得
	 * @return
	 */
	public CharSequence[] getBusStopList() {
		ArrayList<BusStopItem> items = busStopDao.queryBusStopOrderById();

		// バス停数
		int busStopNum = items.size();
		// バス停名のリスト
		CharSequence[] busStopList = new CharSequence[busStopNum];
		int i = 0;
		for(BusStopItem item: items) {
			busStopList[i] = item.busStopName;
			i++;
		}
		return busStopList;
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