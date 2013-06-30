package jp.modal.soul.KeikyuTimeTable.activity;

import java.util.ArrayList;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.model.BusStopDao;
import jp.modal.soul.KeikyuTimeTable.model.HistoryDao;
import jp.modal.soul.KeikyuTimeTable.model.HistoryItem;
import jp.modal.soul.KeikyuTimeTable.model.TimeSummaryDao;
import jp.modal.soul.KeikyuTimeTable.model.TimeSummaryItem;
import jp.modal.soul.KeikyuTimeTable.model.TimeTableDao;
import jp.modal.soul.KeikyuTimeTable.model.TimeTableItem;
import jp.modal.soul.KeikyuTimeTable.util.Const;
import jp.modal.soul.KeikyuTimeTable.util.Utils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
	public int busStopID;

	/** 対象の路線番号 */
	public int routeID;

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
	
	String busStopNameString;

	/** GA */
	GoogleAnalytics analytics;
	Tracker tracker;
	Uri uri;
	
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
		routeID = getIntent().getExtras().getInt(ROUTE_NUMBER);
		busStopID = getIntent().getExtras().getInt(BUSS_STOP_NUMBER);

	}

	private void setupDao() {
		busStopDao = new BusStopDao(this);
		timeTableDao = new TimeTableDao(this);
		historyDao = new HistoryDao(this);
	}


	private void setupView() {
		// バス停名Viewの取得
//		busStopName = (TextView)findViewById(R.id.selected_bus_stop_name);
		// バス停名の取得
//		ArrayList<BusStopItem> item = busStopDao.queryBusStop(Integer.toString(busStopID));

//		if(item != null) {
//			busStopNameString = item.get(0).busStopName;
//			// 行き先を選択するバス停名を設定
////			busStopName.setText(busStopNameString);
//		}
		setupTabSheet();


//		gotoMapButton = (Button)findViewById(R.id.go_to_map_button);
//		gotoMapButton.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
////				String mapurl = "geo:0,0?p=35.577412, 139.726999";  //←ここの書き方で動作が微妙に変わる！  
////				  
////				Intent intent = new Intent();  
////				intent.setAction(Intent.ACTION_VIEW);  intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");  intent.setData(Uri.parse(mapurl));  
////				startActivity(intent);  
//				
//			}
//		});
	}
	
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
	private Bundle getBundle(int type) {
		Bundle weekdayBundle = new Bundle();
		weekdayBundle.putInt("route", routeID);
		weekdayBundle.putInt("busStop", busStopID);
		weekdayBundle.putInt("week", type);
		return weekdayBundle;
	}

	private void setupTabSheet() {
		FragmentTabHost host = (FragmentTabHost)findViewById(android.R.id.tabhost);
		host.setup(this, getSupportFragmentManager(), R.id.content);
		
		TabSpec weekdayTabSpec = getTabSpec(host, "tab1", R.string.weekday_tab_name, Const.WEEKDAY_TAB_BUTTON_TEXT_COLOR, R.drawable.weekday_tab_icon);
		host.addTab(weekdayTabSpec, SampleFragment.class, getBundle(TimeTableDao.WEEKDAY));
        
        TabSpec saturdayTabSpec = getTabSpec(host, "tab2", R.string.saturday_tab_name, Const.SATURDAY_TAB_BUTTON_TEXT_COLOR, R.drawable.saturday_tab_icon);
        host.addTab(saturdayTabSpec, SampleFragment.class, getBundle(TimeTableDao.SATURDAY));
        
        TabSpec holidayTabSpec = getTabSpec(host, "tab3", R.string.holiday_tab_name, Const.HOLIDAY_TAB_BUTTON_TEXT_COLOR, R.drawable.holiday_tab_icon);
        host.addTab(holidayTabSpec, SampleFragment.class, getBundle(TimeTableDao.HOLIDAY));
	}


	private void setupEventhandling() {



	}
    public static class SampleFragment extends Fragment {
        TimeTableDao timeTableDao;
        TimeSummaryDao timeSummaryDao;
        
        int route;
        int busStop;
        int weekType;
        ListView listView;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
             
//            TextView textView = new TextView(getActivity());
//            textView.setGravity(Gravity.CENTER);
////            textView.setText(getArguments().getString("name"));
        	
        	route = getArguments().getInt("route");
        	busStop = getArguments().getInt("busStop");
        	weekType = getArguments().getInt("week");
            
            timeTableDao = new TimeTableDao(getActivity());
            
            //ListView 
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
        		if(item.hour > hour -3 && item.hour < hour + 3) {
        			if(!isSet) {
        				listView.setSelection(item.position -1);
        			} else if(item.hour == hour){
        				isSet = true;
        				listView.setSelection(item.position -1);
            		}
        		} 
        	}
        }
        
//        @Override
//        public void onActivityCreated(Bundle savedInstanceState) {
//        	super.onActivityCreated(savedInstanceState);
//
//        	listView.setSelection(listView.getCount() -1);
//        }
        
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
    
    public void registerHistory() {
    	SQLiteDatabase db = historyDao.getWritableDatabase();
    	HistoryItem item = new HistoryItem();
    	item.routeId = routeID;
    	item.busStopId = busStopID;
    	item.idString = String.valueOf(routeID) + "."+ String.valueOf(busStopID);
    	try {
    		historyDao.insertOrReplace(db, item);
		} catch (Exception e) {
			e.printStackTrace();
		}
		db.close();
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
}