package jp.modal.soul.KeikyuTimeTable.activity;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.fragment.MapTabFragment;
import jp.modal.soul.KeikyuTimeTable.fragment.TimeTableAdapter;
import jp.modal.soul.KeikyuTimeTable.fragment.TimetableTabFragment;
import jp.modal.soul.KeikyuTimeTable.fragment.TrafficTabFragment;
import jp.modal.soul.KeikyuTimeTable.model.BusStopDao;
import jp.modal.soul.KeikyuTimeTable.model.BusStopItem;
import jp.modal.soul.KeikyuTimeTable.model.HistoryDao;
import jp.modal.soul.KeikyuTimeTable.model.HistoryItem;
import jp.modal.soul.KeikyuTimeTable.model.TimeTableDao;
import jp.modal.soul.KeikyuTimeTable.util.Const;
import jp.modal.soul.KeikyuTimeTable.util.Utils;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
//import jp.modal.soul.KeikyuTimeTable.fragment.MapTabFragment;

public class TimeTableActivity extends FragmentActivity {

	public Button selectLineButton; /** 行き先選択ボタン */


	public static String BUSS_STOP_NUMBER = "BUS_STOP_NUMBER";
	public static String ROUTE_NUMBER = "ROUTE_NUMBER";


	/** 行き先を選択するバス停の番号 */
	public int busStopId;

	/** 対象の路線番号 */
	public int routeId;

	/** Dao */
	BusStopDao busStopDao;
	TimeTableDao timeTableDao;
	HistoryDao historyDao;

	/** View */
	TextView busStopName;
	TextView startTimeView;
	TextView gotoMapButton;
	
	/** ListView */
	ListView weekdayListView;
	ListView saturdayListView;
	ListView holidayListView;
	
	/** adaptor */
	TimeTableAdapter weekdayAdaptor;
	TimeTableAdapter saturdayAdaptor;
	TimeTableAdapter holidayAdaptor;
	
	/** Font */
	Typeface face;
	String font = Utils.getFont();

	/** GA */
	GoogleAnalytics analytics;
	Tracker tracker;
	Uri uri;
	
	/** history */
	private HandlerThread backgroundThread;
	private HistoryHandler historyHandler;
	
	private BusStopItem busStop;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab);

        // intentからの設定値の取得
        setupMember();
        
        // Daoのセットアップ
        setupDao();
        
        // Viewのセットアップ
        setupView();
        
        setupGA();
        
        // 履歴の保存
		registerHistory();

    }
	private void setupTitlebar() {
		busStop = (busStopDao.queryBusStopById(new String[]{Integer.toString(busStopId)})).get(0);
        setTitle(busStop.busStopName);
        setTitleColor(getResources().getColor(R.color.white));
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

	private void setupMember() {
		// 行き先を選択するバス停をセット
		routeId = getIntent().getExtras().getInt(ROUTE_NUMBER);
		busStopId = getIntent().getExtras().getInt(BUSS_STOP_NUMBER);

	}

	private void setupDao() {
		busStopDao = new BusStopDao(this);
		timeTableDao = new TimeTableDao(this);
		historyDao = new HistoryDao(this);
	}

	private void setupView() {
		setupTitlebar();
		setupTabSheet();
	}
	
	/**
	 * 時刻表のタブを生成する
	 * @param host
	 * @param tabSpecName
	 * @param nameId
	 * @param color
	 * @param icon
	 * @return
	 */
	private TabSpec getTabSpec(FragmentTabHost host, String tabSpecName, int nameId, int color, int icon) {
		TabSpec tabSpec = host.newTabSpec(tabSpecName);
        Button tabButton = new Button(this);
        tabButton.setText(nameId);
        tabButton.setTextSize(Const.TAB_BUTTON_TEXT_SIZE);
        tabButton.setTextColor(getResources().getColor(color));
        tabButton.setBackgroundResource(icon);
        setFont(tabButton);
        tabSpec.setIndicator(tabButton);
        
        return tabSpec;
	}
	/**
	 * 地図タブを生成する
	 * @param host
	 * @param tabSpecName
	 * @return
	 */
	private TabSpec getMapTabSpec(FragmentTabHost host, String tabSpecName) {
		TabSpec tabSpec = host.newTabSpec(tabSpecName);
		Button tabButton = new Button(this);
		tabButton.setText("地図");
		tabButton.setTextSize(Const.TAB_BUTTON_TEXT_SIZE);
		tabButton.setBackgroundResource(R.drawable.map_tab_icon);
		setFont(tabButton);
		tabSpec.setIndicator(tabButton);
		return tabSpec;
	}
	
	private TabSpec getTrafficTabSpec(FragmentTabHost host, String tabSpecName) {
		TabSpec tabSpec = host.newTabSpec(tabSpecName);
		Button tabButton = new Button(this);
		tabButton.setText("運行情報");
		tabButton.setTextSize(Const.TAB_BUTTON_TEXT_SIZE);
		tabButton.setBackgroundResource(R.drawable.traffic_tab_icon);
		setFont(tabButton);
		tabSpec.setIndicator(tabButton);
		return tabSpec;
	}
	
	private Bundle getBundle(int type) {
		Bundle weekdayBundle = new Bundle();
		weekdayBundle.putInt("route", routeId);
		weekdayBundle.putInt("busStop", busStopId);
		weekdayBundle.putInt("week", type);
		return weekdayBundle;
	}
	
	private Bundle getMapBundle() {
		Bundle mapBundle = new Bundle();
		mapBundle.putString("busStop", busStop.busStopName);
		return mapBundle;
	}
	
	private Bundle getTrafficBundle() {
		Bundle trafficBundle = new Bundle();
		trafficBundle.putString("name", busStop.busStopName);
		trafficBundle.putInt("search", busStop.search);
		trafficBundle.putInt("terminal", busStopDao.queryTerminalBusSearchIDByRouteID(routeId));
		return trafficBundle;
	}

	private void setupTabSheet() {
		FragmentTabHost host = (FragmentTabHost)findViewById(android.R.id.tabhost);
		host.setup(this, getSupportFragmentManager(), R.id.content);
		
		TabSpec weekdayTabSpec = getTabSpec(host, "tab1", R.string.weekday_tab_name, Const.WEEKDAY_TAB_BUTTON_TEXT_COLOR, R.drawable.weekday_tab_icon);
		host.addTab(weekdayTabSpec, TimetableTabFragment.class, getBundle(TimeTableDao.WEEKDAY));
        
        TabSpec saturdayTabSpec = getTabSpec(host, "tab2", R.string.saturday_tab_name, Const.SATURDAY_TAB_BUTTON_TEXT_COLOR, R.drawable.saturday_tab_icon);
        host.addTab(saturdayTabSpec, TimetableTabFragment.class, getBundle(TimeTableDao.SATURDAY));
        
        TabSpec holidayTabSpec = getTabSpec(host, "tab3", R.string.holiday_tab_name, Const.HOLIDAY_TAB_BUTTON_TEXT_COLOR, R.drawable.holiday_tab_icon);
        host.addTab(holidayTabSpec, TimetableTabFragment.class, getBundle(TimeTableDao.HOLIDAY));
        
        TabSpec trafficTabSpec = getTrafficTabSpec(host, "tab0");
        host.addTab(trafficTabSpec, TrafficTabFragment.class, getTrafficBundle());
        
        TabSpec mapTabSpec = getMapTabSpec(host, "tab4");
        host.addTab(mapTabSpec, MapTabFragment.class, getMapBundle());
	}
    
    public void registerHistory() {
    	backgroundThread = new HandlerThread("BackgroundThread", android.os.Process.THREAD_PRIORITY_DEFAULT);
    	backgroundThread.start();
    	historyHandler = new HistoryHandler(backgroundThread.getLooper(), getApplicationContext(), routeId, busStopId);
    	Message.obtain(historyHandler, HistoryHandler.INSERT_HISTORY).sendToTarget();
    }
    
    private void setFont(Button button) {
    	face = Typeface.createFromAsset(getAssets(), font);
    	button.setTypeface(face);
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
    	backgroundThread.quit();
    }
    
    private static class HistoryHandler extends Handler {
    	public static final int INSERT_HISTORY = 0;
    	int routeId;
    	int busStopId;
    	HistoryDao dao;
    	
    	public HistoryHandler(android.os.Looper looper, Context context, int routeId, int busStopId) {
    		super(looper);
    		this.routeId = routeId;
    		this.busStopId = busStopId;
    		dao = new HistoryDao(context);
    	}
    	
    	@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case INSERT_HISTORY:
				registerHistory();
				break;
			default:
				break;
			}
    	}
    	
    	private void registerHistory() {
        	SQLiteDatabase db = dao.getWritableDatabase();
        	HistoryItem item = new HistoryItem();
        	item.routeId = routeId;
        	item.busStopId = busStopId;
        	item.idString = String.valueOf(routeId) + "."+ String.valueOf(busStopId);
        	try {
        		dao.insertOrReplace(db, item);
    		} catch (Exception e) {
    			e.printStackTrace();
    		} finally {
    			db.close();
    		}
    	}
    }
}