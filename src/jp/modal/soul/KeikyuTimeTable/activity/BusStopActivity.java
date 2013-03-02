package jp.modal.soul.KeikyuTimeTable.activity;

import java.util.ArrayList;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.model.BusStopDao;
import jp.modal.soul.KeikyuTimeTable.model.BusStopItem;
import jp.modal.soul.KeikyuTimeTable.model.TimeTableDao;
import jp.modal.soul.KeikyuTimeTable.model.TimeTableItem;
import android.app.TabActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class BusStopActivity extends TabActivity {

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
	
	String busStopNameString;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_stop);

        // intentからの設定値の取得
        setupMember();
        // Daoのセットアップ
        setupDao();
        // Viewのセットアップ
        setupView();
        // 動作のセットアップ
        setupEventhandling();


        
    }


	private void setupMember() {
		// 行き先を選択するバス停をセット
		routeID = getIntent().getExtras().getInt(ROUTE_NUMBER);
		busStopID = getIntent().getExtras().getInt(BUSS_STOP_NUMBER);

	}

	private void setupDao() {
		busStopDao = new BusStopDao(this);
		timeTableDao = new TimeTableDao(this);
	}
	/**
	 * 指定した曜日の発車時刻のリストを取得
	 * @param weekType
	 * @return　
	 */
	public ArrayList<TimeTableItem> getTimeList(int weekType) {
		String[] selectionArgs = { Integer.toString(this.busStopID), Integer.toString(this.routeID), String.valueOf(weekType)};
		ArrayList<TimeTableItem> list = new ArrayList<TimeTableItem>();
		list = timeTableDao.queryBusStopOrderById(selectionArgs);
		return list;
	}

	private void setupView() {
		// バス停名Viewの取得
		busStopName = (TextView)findViewById(R.id.selected_bus_stop_name);
		// バス停名の取得
		ArrayList<BusStopItem> item = busStopDao.queryBusStop(Integer.toString(busStopID));

		if(item != null) {
			busStopNameString = item.get(0).busStopName;
			// 行き先を選択するバス停名を設定
			busStopName.setText(busStopNameString);
		}
		setupTabSheet();
		setupListView();
		
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


	private void setupTabSheet() {
		// TabHostのインスタンスを取得
        TabHost tabs = getTabHost();

        LayoutInflater.from(this).inflate(R.layout.bus_stop, tabs.getTabContentView(), true);

        // タブシートの設定
        TabSpec tab01 = tabs.newTabSpec("Weekday");
        tab01.setIndicator(getString(R.string.weekday_tab_name));
        tab01.setContent(R.id.weekday_content);
        tabs.addTab(tab01);
        TabSpec tab02 = tabs.newTabSpec("Saturday");
        tab02.setIndicator(getString(R.string.saturday_tab_name));
        tab02.setContent(R.id.saturday_content);
        tabs.addTab(tab02);
        TabSpec tab03 = tabs.newTabSpec("Holiday");
        tab03.setIndicator(getString(R.string.holiday_tab_name));
        tab03.setContent(R.id.holiday_content);
        tabs.addTab(tab03);
        // 初期表示のタブ設定
        tabs.setCurrentTab(0);

        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
	        // タブがクリックされた時のハンドラ
	        @Override
	        public void onTabChanged(String tabId) {

//	        	// クリックされた時の処理を記述
//	        	TextView text;
//	        	if(tabId == "TabSheet1") {
//	        		text = (TextView)findViewById(R.id.weekday_tab_name);
//	        		text.setTextColor(Color.BLUE);
//	        	}
//	        	else if(tabId == "TabSheet2") {
//	        		text = (TextView)findViewById(R.id.saturday_tab_name);
//	        		text.setTextColor(Color.RED);
//	        	}
//	        	else if(tabId == "TabSheet3") {
//	        		text = (TextView)findViewById(R.id.holiday_tab_name);
//	        		text.setTextColor(Color.GREEN);
//	        	}
	        }
        });
	}


	private void setupListView() {
		// 発車時刻の取得
		ArrayList<TimeTableItem> weekdayItems = new ArrayList<TimeTableItem>();
		ArrayList<TimeTableItem> saturdayItems = new ArrayList<TimeTableItem>();
		ArrayList<TimeTableItem> holidayItems = new ArrayList<TimeTableItem>();

		weekdayItems = getTimeList(TimeTableDao.WEEKDAY);
		saturdayItems = getTimeList(TimeTableDao.SATURDAY);
		holidayItems = getTimeList(TimeTableDao.HOLIDAY);
		
		weekdayAdaptor = new TimeTableAdapter(this, R.layout.time_table_row, weekdayItems);
		saturdayAdaptor = new TimeTableAdapter(this, R.layout.time_table_row, saturdayItems);
		holidayAdaptor = new TimeTableAdapter(this, R.layout.time_table_row, holidayItems);
		
		weekdayListView = (ListView)findViewById(R.id.weekday_list);
		saturdayListView = (ListView)findViewById(R.id.saturday_list);
		holidayListView = (ListView)findViewById(R.id.holiay_list);
		
		weekdayListView.setAdapter(weekdayAdaptor);
		saturdayListView.setAdapter(saturdayAdaptor);
		holidayListView.setAdapter(holidayAdaptor);
	}
	private void setupEventhandling() {



	}

}