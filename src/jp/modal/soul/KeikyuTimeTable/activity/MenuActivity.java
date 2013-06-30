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
import jp.modal.soul.KeikyuTimeTable.util.Const;
import jp.modal.soul.KeikyuTimeTable.util.Utils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
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
	private ImageView bus;
	private ImageView kemuri;
	private ImageView busStop;
	
	/** ItemList */
	ArrayList<HistoryItem> historyItemList;
	
	/** GA */
	GoogleAnalytics analytics;
	Tracker tracker;
	Uri uri;
	
	/** Dialog */
	AlertDialog.Builder builder;
	AlertDialog.Builder aboutAppBuilder;
	
	/** Menu */
	private final int ABOUT_APP = 0;
	private final int CONTACT = 1;
	
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(Menu.NONE, ABOUT_APP, Menu.NONE, Const.MENU_ABOUT_APP);
    	menu.add(Menu.NONE, CONTACT, Menu.NONE, Const.MENU_CONTACT);
    	return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected( MenuItem item ){
    	if(item.getItemId() == ABOUT_APP) {
    		String versionName = "";
    	    PackageManager packageManager = this.getPackageManager();
    	    try {
    	    	PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
    	        versionName = packageInfo.versionName;
    	    } catch (NameNotFoundException e) {
    	    	e.printStackTrace();
    	    }
    		if(!versionName.equals("")) {
    			versionName = "バージョン番号：" + versionName + "\n";
    		}
    		aboutAppBuilder = new AlertDialog.Builder(MenuActivity.this);
    		aboutAppBuilder.setTitle(Const.MENU_ABOUT_APP);		
    		aboutAppBuilder.setPositiveButton("OK", null);
    		aboutAppBuilder.setMessage(versionName + Const.ABOUT_APP_MESSAGE);
    		
    		aboutAppBuilder.show();
    	} else if(item.getItemId() == CONTACT) {
    		Uri uri = Uri.parse(Const.CONTACT_URL);
    			Intent i = new Intent(Intent.ACTION_VIEW,uri);
    			startActivity(i);
    	}
    	return false;
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
        
        routeButton.setOnClickListener(onRouteClickListener);
        
        historyButton.setOnClickListener(onHistoryClickListener);
        
        bus = (ImageView)findViewById(R.id.bus);
        
        kemuri = (ImageView)findViewById(R.id.kemuri);
        
        bus.setOnClickListener(busOnClickListener);
        
        busStop = (ImageView)findViewById(R.id.busstop);
        busStop.setOnClickListener(onBusstopClickListener);
        
	}
	View.OnClickListener onBusstopClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			tracker.sendEvent(Const.UI_CATEGORY, Const.IMAGE_PRESS, Const.BUSSTOP_IMAGE, 0L);
			launchReviewDialog();
		}

	};
	private void launchReviewDialog() {
		AlertDialog.Builder reviewDialog = new AlertDialog.Builder(MenuActivity.this);
		reviewDialog.setMessage(Const.REVIEW_MESSAGE);
		reviewDialog.setPositiveButton(Const.GO_TO_PLAY, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Uri uri = Uri.parse("market://details?id=jp.modal.soul.KeikyuTimeTable");
				
				Intent intent = new Intent(Intent.ACTION_VIEW,uri);
				startActivity(intent);	
			}
		});
		reviewDialog.setNegativeButton(Const.REVIEW_CANCEL, null);
		reviewDialog.show();
	}
	
	View.OnClickListener onRouteClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			launchRouteList();
		}
	};
	public void launchRouteList() {
		tracker.sendEvent(Const.UI_CATEGORY, Const.BUTTON_PRESS, Const.SELECT_ROUTE, 0L);
		// BusStopActivityを起動するintentの作成
		Intent intent = new Intent(getApplicationContext(), RouteListActivity.class);
		// BusStopActivityの起動
		Utils.intentLauncher(this, intent);
	}
	
    View.OnClickListener onHistoryClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			launchHistoryList();
		}

	};
	private void launchHistoryList() {
		tracker.sendEvent(Const.UI_CATEGORY, Const.BUTTON_PRESS, Const.SELECT_HISTORY, 0L);
		builder = new AlertDialog.Builder(MenuActivity.this);
		builder.setTitle("履歴から選択");			
		builder.setSingleChoiceItems(getDialogList(), -1, historyOnClickListener);
		builder.show();
	}
	
	private View.OnClickListener busOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			tracker.sendEvent(Const.UI_CATEGORY, Const.IMAGE_PRESS, Const.EGG, 0L);
			setUpBusAnimation();
		}
	};
	
	void setUpBusAnimation() {
		Long time = System.currentTimeMillis();
		if(time%3 == 0) {
			RotateAnimation rotate = new RotateAnimation(0, 1080, bus.getWidth()/2, bus.getHeight()/2);
			rotate.setDuration(1000);
			bus.startAnimation(rotate);
		} else {
			TranslateAnimation translate = new TranslateAnimation(0, -500, 0, 0);
			translate.setDuration(3000);
			bus.startAnimation(translate);
			kemuri.startAnimation(translate);
		}
	}
	
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