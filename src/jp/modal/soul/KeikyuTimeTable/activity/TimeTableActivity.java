package jp.modal.soul.KeikyuTimeTable.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.model.BusStopDao;
import jp.modal.soul.KeikyuTimeTable.model.BusStopItem;
import jp.modal.soul.KeikyuTimeTable.model.HistoryDao;
import jp.modal.soul.KeikyuTimeTable.model.HistoryItem;
import jp.modal.soul.KeikyuTimeTable.model.TimeSummaryDao;
import jp.modal.soul.KeikyuTimeTable.model.TimeSummaryItem;
import jp.modal.soul.KeikyuTimeTable.model.TimeTableDao;
import jp.modal.soul.KeikyuTimeTable.model.TimeTableItem;
import jp.modal.soul.KeikyuTimeTable.model.TrafficInfoItem;
import jp.modal.soul.KeikyuTimeTable.util.Const;
import jp.modal.soul.KeikyuTimeTable.util.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

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
        // Itemのセットアップ　
        busStop = (busStopDao.queryBusStopById(new String[]{Integer.toString(busStopId)})).get(0);
        setTitle(busStop.busStopName);
        setTitleColor(getResources().getColor(R.color.white));
        // Viewのセットアップ
        setupView();
        
        setupGA();
        // 動作のセットアップ
        setupEventhandling();
        // 履歴の保存
		registerHistory();

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
//		ArrayList<BusStopItem> items = busStopDao.queryBusStopById(new String[]{Integer.toString(busStopId)});
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
		host.addTab(weekdayTabSpec, TabFragment.class, getBundle(TimeTableDao.WEEKDAY));
        
        TabSpec saturdayTabSpec = getTabSpec(host, "tab2", R.string.saturday_tab_name, Const.SATURDAY_TAB_BUTTON_TEXT_COLOR, R.drawable.saturday_tab_icon);
        host.addTab(saturdayTabSpec, TabFragment.class, getBundle(TimeTableDao.SATURDAY));
        
        TabSpec holidayTabSpec = getTabSpec(host, "tab3", R.string.holiday_tab_name, Const.HOLIDAY_TAB_BUTTON_TEXT_COLOR, R.drawable.holiday_tab_icon);
        host.addTab(holidayTabSpec, TabFragment.class, getBundle(TimeTableDao.HOLIDAY));
        
        TabSpec trafficTabSpec = getTrafficTabSpec(host, "tab0");
        host.addTab(trafficTabSpec, TrafficTabFragment.class, getTrafficBundle());
        
        TabSpec mapTabSpec = getMapTabSpec(host, "tab4");
        host.addTab(mapTabSpec, MapTabFragment.class, getMapBundle());
	}


	private void setupEventhandling() {
	}
	
    public static class TabFragment extends Fragment {
        TimeTableDao timeTableDao;
        TimeSummaryDao timeSummaryDao;
        
        int route;
        int busStop;
        int weekType;
        ListView listView;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        	route = getArguments().getInt("route");
        	busStop = getArguments().getInt("busStop");
        	weekType = getArguments().getInt("week");
            
            timeTableDao = new TimeTableDao(getActivity());

            listView = new ListView(getActivity());
            listView.setCacheColorHint(R.color.transparent);
            listView.setSelection(listView.getCount());
            
            ArrayList<TimeTableItem> items = getTimeList(weekType);
            
            TimeTableAdapter adapter = new TimeTableAdapter(getActivity(), R.layout.time_table_row, items);
            listView.setAdapter(adapter);
            setSelection();
             
            return listView;
        }
        
        private void setSelection() {
        	Time time = new Time("Asia/Tokyo");
        	time.setToNow();
        	int hour = time.hour;
        	
        	timeSummaryDao = new TimeSummaryDao(getActivity());
        	String[] selectionArgs = {Integer.toString(busStop), Integer.toString(route), Integer.toString(weekType)};
        	ArrayList<TimeSummaryItem> list = timeSummaryDao.querySummaryOrderByHour(selectionArgs);
        	
        	boolean isSet = false;
        	for(TimeSummaryItem item:list) {
        		if(item.hour >= hour -2 && item.hour < hour) {
        			listView.setSelection(item.position);
        		} else if(item.hour == hour) {
        			isSet = true;
        			listView.setSelection(item.position);
        		} else if(item.hour > hour && item.hour <= hour + 2 && !isSet) {        			
        			isSet = true;
        			listView.setSelection(item.position);
            	} 
        	}
        }
        
    	/**
    	 * 指定した曜日の発車時刻のリストを取得
    	 * @param weekType
    	 * @return　
    	 */
    	public ArrayList<TimeTableItem> getTimeList(int weekType) {
    		String[] selectionArgs = { String.valueOf(busStop), String.valueOf(route), String.valueOf(weekType)};
    		ArrayList<TimeTableItem> list = new ArrayList<TimeTableItem>();
    		list = timeTableDao.queryBusStopOrderById(selectionArgs);
    		return list;
    	}
    }
    
    public static class TrafficTabFragment extends Fragment {
    	final int MAX_BUS_STOP_NUM = 3;
    	String name;
    	int search;
    	int terminal;
    	TrafficInfoItem item;
    	LinearLayout tabLinearLayout;
    	TextView busStopNameView;
    	TextView threeBefore;
    	TextView twoBefore;
    	TextView oneBefore;
    	TextView rideBusStop;
    	
    	TextView labelComment;
    	TextView label3B;
    	TextView label2B;
    	TextView label1B;

    	/** Font */
    	Typeface face;
    	String font = Utils.getFont();
    	
    	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    		name = getArguments().getString("name");
    		search = getArguments().getInt("search");
    		terminal = getArguments().getInt("terminal");
    		tabLinearLayout = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.traffic_info, null);
    		
    		item = new TrafficInfoItem(name);
    		
    		setView();
    		
    		setupTrafficInfo();
    		
    		setFontAll();
    		
    		return tabLinearLayout;
    	}
    	void setFontAll() {
    		setFont(threeBefore);
    		setFont(twoBefore);
    		setFont(oneBefore);
    		setFont(rideBusStop);
    		setFont(label1B);
    		setFont(label2B);
    		setFont(label3B);
    		setFont(labelComment);
    	}
    	void setView() {
    		
    		threeBefore = (TextView)tabLinearLayout.findViewById(R.id.threeBefore);
    		twoBefore = (TextView)tabLinearLayout.findViewById(R.id.twoBefore);
    		oneBefore = (TextView)tabLinearLayout.findViewById(R.id.oneBefore);
    		rideBusStop = (TextView)tabLinearLayout.findViewById(R.id.rideBusStop);
    		rideBusStop.setText(name + "停留所");
    		
    		label1B = (TextView)tabLinearLayout.findViewById(R.id.one_before_label);
    		label2B = (TextView)tabLinearLayout.findViewById(R.id.two_before_label);
    		label3B = (TextView)tabLinearLayout.findViewById(R.id.three_before_label);
    		labelComment = (TextView)tabLinearLayout.findViewById(R.id.traffic_comment);
    	}
    	void setupTrafficInfo() {
    		String url = "http://keikyu-bus-loca.jp/BusLocWeb/getInpApchInfo.do?usn=" + search + "&dsn=" + terminal;
    		try {
    			Document doc = Jsoup.connect(url).get();
    			Elements dd = doc.getElementsByTag("dd");
    			
    			int busStopIndex = 3;
    			for(Elements ele : getBusStopElementList(dd)) {
    				String[] time = ele.html().replaceAll("<.+?>", "/").split("/");
    				if(time.length > 2) {
    					item.arriveTime(busStopIndex,time[1]);
    					item.terminalTime(busStopIndex,time[2]);
    				}
    				busStopIndex--;
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		threeBefore.setText(makeTrafficText(item, TrafficInfoItem.THREE_BEFORE));
    		twoBefore.setText(makeTrafficText(item, TrafficInfoItem.TWO_BEFORE));
    		oneBefore.setText(makeTrafficText(item, TrafficInfoItem.ONE_BEFORE));
    	}
    	String makeTrafficText(TrafficInfoItem item, int type) {
    		return getRideString(item.busStopName(), item.arriveTime(type).trim()) + "\n" + getTerminalString(item.terminalTime(type).trim());
    		
    	}
    	String getRideString(String busStopName, String str) {
    		if(str.length() > 6) return busStopName + str.substring(5);
    		else return "";
    	}
    	String getTerminalString(String str) {
    		if(str.length() > 6) return "終点" + str.substring(5);
    		else return "";
    	}
    	/**
    	 * 運行情報HTMLの<dd>タグ以下のエレメントを受け取って、
    	 * 
    	 * @param dd
    	 * @return
    	 */
    	ArrayList<Elements> getBusStopElementList(Elements dd) {
    		ArrayList<Elements> busStopElementList = new ArrayList<Elements>();
    		int busStopNum = MAX_BUS_STOP_NUM;
    		for(Iterator<Element>i = dd.iterator(); i.hasNext() && busStopNum>=0;) {
    			Elements bus =  ((Element)i.next()).getElementsByClass("bus");
    			busStopElementList.add(bus);
    			busStopNum--;
    		}
    		return busStopElementList;
    	}
    	private void setFont(TextView text) {
    		face = Typeface.createFromAsset(getActivity().getAssets(), font);
    		text.setTypeface(face);
    	}
    }
    
    public static class MapTabFragment extends Fragment {
    	String target;
    	LocationManager locationManager;
    	/** Font */
    	Typeface face;
    	String font = Utils.getFont();
    	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    		locationManager = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);
    		
    		target = getArguments().getString("busStop");
    		LinearLayout tabLinearLayout = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.map_tab, null);
    		
    		TextView busStopName = (TextView)tabLinearLayout.getChildAt(0);
    		busStopName.setText(target + " バス停");
    		setFont(busStopName);
    		
    		Button openMapButton = (Button)tabLinearLayout.getChildAt(1);
    		setFont(openMapButton);
    		
    		TextView mapComment = (TextView)tabLinearLayout.getChildAt(2);
    		setFont(mapComment);
    		
    		openMapButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					gotoMap();
				};
			});
    		Button naviButton = (Button)tabLinearLayout.getChildAt(3);
    		setFont(naviButton);
    		naviButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					openNavi();
				};
			});
    		
    		TextView naviComment = (TextView)tabLinearLayout.getChildAt(4);
    		setFont(naviComment);
    		
    		if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
    			Button gpsOnButton = (Button)tabLinearLayout.getChildAt(5);
    			gpsOnButton.setOnClickListener(gpsOnButtonOnClickListener);
    			gpsOnButton.setVisibility(View.VISIBLE);
    			setFont(gpsOnButton);
    		}
    		
    		return tabLinearLayout;
    	}

    	View.OnClickListener gpsOnButtonOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(callGPSSettingIntent);
			}
		};
    	
    	void gotoMap() {
			Intent mi = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=\"" + target + "（バス）\""));
			startActivity(mi);
    	}
    	private void setFont(TextView text) {
    		face = Typeface.createFromAsset(getActivity().getAssets(), font);
    		text.setTypeface(face);
    	}
    	void openNavi() {
    		Intent intent = new Intent();
    		intent.setAction(Intent.ACTION_VIEW);
    		intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
    		intent.setData(Uri.parse("http://maps.google.com/maps?daddr=" + target +"（バス）&dirflg=w"));
    		startActivity(intent);
    	}
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