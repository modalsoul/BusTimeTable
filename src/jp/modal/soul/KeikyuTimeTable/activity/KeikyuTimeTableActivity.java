package jp.modal.soul.KeikyuTimeTable.activity;


import java.util.ArrayList;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.R.id;
import jp.modal.soul.KeikyuTimeTable.model.BusStopDao;
import jp.modal.soul.KeikyuTimeTable.model.BusStopItem;
import jp.modal.soul.KeikyuTimeTable.model.RouteDao;
import jp.modal.soul.KeikyuTimeTable.model.RouteItem;
import jp.modal.soul.KeikyuTimeTable.util.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class KeikyuTimeTableActivity extends Activity {
	/** バス停選択ボタン */
	public Button selectBusStopButton;
	/** バス停リストを表示するダイアログ */
	public AlertDialog busStopListDialog;
	
	/** 選択されたバス停の番号 */
	public int selectedBusStopNumber; 
	/** 選択された路線ID */
	public long selectedRouteId;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // 初回起動時のセットアップ
        setupInit();
        // Viewのセットアップ
        setupView();
        // 動作のセットアップ
        setupEventhandling();
        
        // TODO adaptorを外だし
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        
        // リストの取得
        RouteDao routeDao = new RouteDao(getApplicationContext());
        ArrayList<RouteItem> routeList = routeDao.queryRouteOrderById();
            
        // アイテムの追加
        for(RouteItem item : routeList) {
        	adapter.add(item.routeName);
        }
        
        ListView listView = (ListView)findViewById(id.lineList);
        // アダプターの設定
        listView.setAdapter(adapter);
        // ListViewのアイテムがクリックされたときのコールバックリスナーを登録
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position,
        			long id) {
        		ListView listView = (ListView)parent;
        		// クリックされたアイテムの取得
        		String item = (String)listView.getItemAtPosition(position);
        		Toast.makeText(KeikyuTimeTableActivity.this, item, Toast.LENGTH_SHORT).show();
        		
        		// 路線IDをセット
        		selectedRouteId = position + 1;
        		showBusStopList();
        		
        	}
		});
        

    }


    /**
     *  Viewのセットアップ
     */
	private void setupView() {
		
		selectBusStopButton = (Button)findViewById(R.id.select_bus_stop_btn);
		
	}
	/**
	 *  動作のセットアップ
	 */
	private void setupEventhandling() {
		
		selectBusStopButton.setOnClickListener(selectBusStopClickListener);
		
	}
	
	// バス停選択ボタンをクリックしたときの動作を設定
	public View.OnClickListener selectBusStopClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {			
			showBusStopList();
		}
	};
	
	/**
	 * バス停選択のダイアログを表示する
	 */
	private void showBusStopList() {	
		RouteDao routeDao = new RouteDao(getApplicationContext());
		// 路線情報を取得
		Log.e("GYAAAAAAAAAAA", Long.toString(selectedRouteId));
		RouteItem routeItem = routeDao.queryAllBusStopByRouteId(selectedRouteId);
		if(routeItem == null) {
			// システムエラー
		}
		String[] busStops = Utils.busStopIdString2StringItems(routeItem.busStops);
		// 路線のバス停を取得
		BusStopDao busStopDao = new BusStopDao(getApplicationContext());
		ArrayList<BusStopItem> busStopItems = busStopDao.queryBusStopById(busStops);
		
		// バス停名を設定
		final CharSequence[] busStopNames = new CharSequence[busStopItems.size()];
		int i = 0;
		for(BusStopItem item: busStopItems) {
			busStopNames[i] = item.busStopName;
			i++;
		}
		// バス停選択のリストを表示するダイアログを作成
		AlertDialog.Builder builder = new AlertDialog.Builder(KeikyuTimeTableActivity.this);		
		builder.setTitle(R.string.bus_stop_list_dialog_title);
		builder.setSingleChoiceItems(busStopNames, -1, new DialogInterface.OnClickListener(){
			// バス停リストを選択したときの動作を設定
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 選択されたバス停番号を設定
				selectedBusStopNumber = which +1;
				// バス停の行き先選択画面へ遷移
				launchBusStop();
			}
		});
		// バス停選択のリストを表示するダイアログを作成
		builder.show();
	}
	
	/**
	 * 選択されたバス停の行き先を選択する画面へ遷移する
	 */
	public void launchBusStop(){
		// BusStopActivityを起動するintentの作成
		Intent intent = new Intent(getApplicationContext(), BusStopActivity.class);
		// 選択されたバス停番号を指定
		intent.putExtra(BusStopActivity.BUSS_STOP_NUMBER, selectedBusStopNumber);
		// BusStopActivityの起動
//		Util.intentLauncher(this, intent);	
		this.startActivity(intent);
	}
	
	/**
	 * バス停名のリストを取得
	 * @return
	 */
	public CharSequence[] getBusStopList() {
		BusStopDao busStopDao = new BusStopDao(getApplicationContext());
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
			BusStopDao busStopDao = new BusStopDao(getApplicationContext());
			busStopDao.setup();
			RouteDao routeDao = new RouteDao(getApplicationContext());
			routeDao.setup();
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