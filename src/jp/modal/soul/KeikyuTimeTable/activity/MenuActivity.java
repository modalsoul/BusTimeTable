package jp.modal.soul.KeikyuTimeTable.activity;


import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.migration.DatabaseState;
import jp.modal.soul.KeikyuTimeTable.migration.InitState;
import jp.modal.soul.KeikyuTimeTable.model.BusStopDao;
import jp.modal.soul.KeikyuTimeTable.model.BusStopItem;
import jp.modal.soul.KeikyuTimeTable.model.HistoryDao;
import jp.modal.soul.KeikyuTimeTable.model.HistoryItem;
import jp.modal.soul.KeikyuTimeTable.model.RouteDao;
import jp.modal.soul.KeikyuTimeTable.model.RouteItem;
import jp.modal.soul.KeikyuTimeTable.model.TimeTableDao;
import jp.modal.soul.KeikyuTimeTable.task.SearchBusStopTask;
import jp.modal.soul.KeikyuTimeTable.util.Const;
import jp.modal.soul.KeikyuTimeTable.util.Utils;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
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
	private Button searchButton;
	private Button historyButton;
	private ImageView bus;
	private ImageView kemuri;
	private ImageView busStop;
	private Button history1;
	private Button history2;
	
	/** ItemList */
	ArrayList<HistoryItem> historyItemList;
	ArrayList<BusStopItem> busStopItemList;
	
	/** GA */
	GoogleAnalytics analytics;
	Tracker tracker;
	Uri uri;
	
	/** Dialog */
	AlertDialog.Builder historyDialogBuilder;
	AlertDialog.Builder searchDialogBuilder;
	AlertDialog searchDialog;
	AlertDialog.Builder aboutAppBuilder;
	
	/** Search */
	EditText searchEditText;
	int searchResultNum;
	
	/** Menu */
	private final int ABOUT_APP = 0;
	private final int CONTACT = 1;
	

	/** Dialog */
	static final int ROUTE_LIST_LOADING = 1;
	ProgressDialog m_progressDialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the intent that started this Activity.
        Intent intent = this.getIntent();
        uri = intent.getData();
        
        setContentView(R.layout.activity_menu);
        
        // DAOのセットアップ
        setupDao();
        // 初回起動時のセットアップ
        setupInit();
        
        checkUpdate();
        
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
		setupTitleViews();
        
		setupHistoryViews();

		setupSearchButtons();
        
        setupAnimationViews();
	}

	private void setupSearchButtons() {
		routeButton = (Button)findViewById(R.id.route_menu_button);
        setFont(routeButton);
        
        searchButton = (Button)findViewById(R.id.search_menu_button);
        setFont(searchButton);
        
        routeButton.setOnClickListener(onRouteClickListener);
        searchButton.setOnClickListener(onSearchClickListener);
        
        if(isTabletMode()) {
        	routeButton.setTextSize(44);
        	searchButton.setTextSize(44);
        }
	}

	private void setupTitleViews() {
		title = (TextView)findViewById(R.id.menu_title);
        setFont(title);
        
        unofficial = (TextView)findViewById(R.id.unofficial);
        setFont(unofficial);
	}

	private void setupHistoryViews() {
		historyButton = (Button)findViewById(R.id.history_menu_button);
        setFont(historyButton);
        
        history1 = (Button)findViewById(R.id.history_1);
        setFont(history1);
        
        history2 = (Button)findViewById(R.id.history_2);
        setFont(history2);
        
        CharSequence[] hist = getHistoryDialogList();
        
        if(hist.length > 2) {
        	history1.setText(hist[0]);
        	history1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					HistoryItem item = historyItemList.get(0);
					launchBusStop((int)item.routeId, (int)item.busStopId);
				}
			});
        	history2.setText(hist[1]);
        	history2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					HistoryItem item = historyItemList.get(1);
					launchBusStop((int)item.routeId, (int)item.busStopId);
				}
			});
        } else if(hist.length == 1) {
        	history1.setText(hist[0]);
        	history1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					HistoryItem item = historyItemList.get(0);
					launchBusStop((int)item.routeId, (int)item.busStopId);
				}
			});
        } else {
        	historyButton.setVisibility(View.GONE);
        	history1.setVisibility(View.GONE);
        	history2.setVisibility(View.GONE);
        }
        
        historyButton.setOnClickListener(onHistoryClickListener);
        

        if(isTabletMode()) {
        	historyButton.setTextSize(44);
        	history1.setTextSize(22);
        	history2.setTextSize(22);
        }
	}
	
	private void setupAnimationViews() {
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
			showDialog(ROUTE_LIST_LOADING);
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
	
	View.OnClickListener onSearchClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			launchSearchBox();
		}
	};
	
	private void launchSearchBox() {
		searchEditText = new EditText(MenuActivity.this);
		tracker.sendEvent(Const.UI_CATEGORY, Const.BUTTON_PRESS, Const.SELECT_SEARCH, 0L);
		searchDialogBuilder = new AlertDialog.Builder(MenuActivity.this);
		searchDialogBuilder.setTitle("バス停を検索");
		searchDialogBuilder.setView(searchEditText);
		searchDialogBuilder.setPositiveButton("検索", onDialogSearchClickListener);
		searchDialogBuilder.setNegativeButton("キャンセル", null);
		searchDialog = searchDialogBuilder.create();
		searchDialog.show();
	}
	
	DialogInterface.OnClickListener onDialogSearchClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// 空白以外の入力がある場合は、遷移
			if(searchEditText.getText().toString().trim().length() > 0) {
				launchSearchResultList(searchEditText.getText().toString());
			}
		}
	};
	
    View.OnClickListener onHistoryClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			launchHistoryList();
		}

	};
	private void launchHistoryList() {
		tracker.sendEvent(Const.UI_CATEGORY, Const.BUTTON_PRESS, Const.SELECT_HISTORY, 0L);
		historyDialogBuilder = new AlertDialog.Builder(MenuActivity.this);
		historyDialogBuilder.setTitle("履歴から選択");			
		historyDialogBuilder.setSingleChoiceItems(getHistoryDialogList(), -1, historyOnClickListener);
		historyDialogBuilder.show();
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
		if(time%7 == 0) {
			RotateAnimation rotate = new RotateAnimation(0, 1080, bus.getWidth()/2, bus.getHeight()/2);
			rotate.setDuration(1000);
			bus.startAnimation(rotate);
		} else {
			TranslateAnimation translate = new TranslateAnimation(0, -800, 0, 0);
			translate.setDuration(4000);
			bus.startAnimation(translate);
			kemuri.startAnimation(translate);
		}
	}
	
	DialogInterface.OnClickListener historyOnClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			HistoryItem item = historyItemList.get(which);
			launchBusStop((int)item.routeId, (int)item.busStopId);
		}
	};
	
	public CharSequence[] getHistoryDialogList() {
		historyDao = new HistoryDao(this);
		historyItemList = historyDao.queryLatestHistory();
		CharSequence[] dialogItem = new CharSequence[historyItemList.size()];
		int i = 0;
		for(HistoryItem item: historyItemList) {
			RouteItem routeItem = routeDao.queryRouteByRouteId(Long.valueOf(item.routeId));
			
			ArrayList<BusStopItem> busstopItemList = busStopDao.queryBusStop(String.valueOf(item.busStopId));
			dialogItem[i] = makeBusStopSelectRow(routeItem.routeName, routeItem.terminal, busstopItemList.get(0).busStopName);
			i++;
		}
		return dialogItem;
	}

	private String makeBusStopSelectRow(String routeName, String terminal, String busstopName) {
		return routeName + "\n"  + busstopName +"バス停\n";
	}
	
	private void launchSearchResultList(String word) {
		CharSequence[] items = getSearchDialogList(word);
		tracker.sendEvent(Const.UI_CATEGORY, Const.BUTTON_PRESS, Const.SELECT_HISTORY, 0L);
		historyDialogBuilder = new AlertDialog.Builder(MenuActivity.this);
		historyDialogBuilder.setSingleChoiceItems(items, -1, searchResultOnClickListener);
		historyDialogBuilder.setTitle("検索結果:" + searchResultNum + "件");		
		historyDialogBuilder.show();
	}
	
	DialogInterface.OnClickListener searchResultOnClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			BusStopItem item = busStopItemList.get(which);
			launchBusStop((int)item.routeId, (int)item.id);
		}
	};
	
	public CharSequence[] getSearchDialogList(String word) {
		busStopDao = new BusStopDao(this);
		SearchBusStopTask task = new SearchBusStopTask(MenuActivity.this, word);
		task.execute(null);
		try {
			busStopItemList = task.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
		searchResultNum = busStopItemList.size();
		CharSequence[] dialogItem = new CharSequence[busStopItemList.size()];
		int i = 0;
		for(BusStopItem item: busStopItemList) {
			RouteItem routeItem = routeDao.queryRouteByRouteId(Long.valueOf(item.routeId));
			
			dialogItem[i] = makeBusStopSelectRow(routeItem.routeName, routeItem.terminal, item.busStopName);
			i++;
		}
		return dialogItem;
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
		InitState initState = new InitState(getApplicationContext());
		initState.checkState();
	}
	/**
	 * バージョンチェック処理
	 */
	public void checkUpdate() {
		// DBバージョンチェック
		DatabaseState dbState = new DatabaseState(getApplicationContext());
		dbState.checkState();
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
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = super.onCreateDialog(id);
		switch (id) {
		case ROUTE_LIST_LOADING:
			ProgressDialog pDialog = new ProgressDialog(this);
			pDialog.setMessage(getResources().getString(R.string.now_loading_route_list));
			pDialog.setCancelable(false);
			dialog = (Dialog) pDialog;
			m_progressDialog = pDialog; // あとで使うかも知れないので、メンバ変数に格納しときます。
			break;
		}
		return dialog;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(m_progressDialog!=null) m_progressDialog = null;
		//dismissDialog(ROUTE_LIST_LOADING); // ダイアログのグルグルを終了
		removeDialog(ROUTE_LIST_LOADING);
	}	

}