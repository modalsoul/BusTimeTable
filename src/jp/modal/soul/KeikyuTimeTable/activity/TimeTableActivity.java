package jp.modal.soul.KeikyuTimeTable.activity;

import java.util.ArrayList;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.model.BusStopDao;
import jp.modal.soul.KeikyuTimeTable.model.BusStopItem;
import jp.modal.soul.KeikyuTimeTable.model.TimeTableDao;
import jp.modal.soul.KeikyuTimeTable.model.TimeTableItem;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

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
        setContentView(R.layout.tab);

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


	private void setupView() {
		// バス停名Viewの取得
//		busStopName = (TextView)findViewById(R.id.selected_bus_stop_name);
		// バス停名の取得
		ArrayList<BusStopItem> item = busStopDao.queryBusStop(Integer.toString(busStopID));

		if(item != null) {
			busStopNameString = item.get(0).busStopName;
			// 行き先を選択するバス停名を設定
//			busStopName.setText(busStopNameString);
		}
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


	private void setupTabSheet() {
		FragmentTabHost host = (FragmentTabHost)findViewById(android.R.id.tabhost);
		host.setup(this, getSupportFragmentManager(), R.id.content);

		TabSpec weekdayTabSpec = host.newTabSpec("tab1");
        Button weekdayTabButton = new Button(this);
        weekdayTabButton.setText(R.string.weekday_tab_name);
        weekdayTabButton.setTextSize(24);
        weekdayTabButton.setTextColor(getResources().getColor(R.color.black));
        weekdayTabButton.setBackgroundResource(R.drawable.weekday_tab_icon);
        weekdayTabSpec.setIndicator(weekdayTabButton);
        
        Bundle weekdayBundle = new Bundle();
        weekdayBundle.putInt("route", routeID);
        weekdayBundle.putInt("busStop", busStopID);
        weekdayBundle.putInt("week", TimeTableDao.WEEKDAY);
        host.addTab(weekdayTabSpec, SampleFragment.class, weekdayBundle);
         
        
        TabSpec saturdayTabSpec = host.newTabSpec("tab2");
        Button saturdayTabButton = new Button(this);
        saturdayTabButton.setText(R.string.saturday_tab_name);
        saturdayTabButton.setTextSize(24);
        saturdayTabButton.setTextColor(getResources().getColor(R.color.blue));
        saturdayTabButton.setBackgroundResource(R.drawable.saturday_tab_icon);
        saturdayTabSpec.setIndicator(saturdayTabButton);
        
        Bundle saturdayBundle = new Bundle();
        saturdayBundle.putInt("route", routeID);
        saturdayBundle.putInt("busStop", busStopID);
        saturdayBundle.putInt("week", TimeTableDao.SATURDAY);
        host.addTab(saturdayTabSpec, SampleFragment.class, saturdayBundle);
         
        
        TabSpec holidayTabSpec = host.newTabSpec("tab3");
        Button holidayTabButton = new Button(this);
        holidayTabButton.setText(R.string.holiday_tab_name);
        holidayTabButton.setTextSize(24);
        holidayTabButton.setTextColor(getResources().getColor(R.color.red));
        holidayTabButton.setBackgroundResource(R.drawable.holiday_tab_icon);
        holidayTabSpec.setIndicator(holidayTabButton);
        Bundle holidaBundle = new Bundle();
        
        holidaBundle.putInt("route", routeID);
        holidaBundle.putInt("busStop", busStopID);
        holidaBundle.putInt("week", TimeTableDao.HOLIDAY);
        host.addTab(holidayTabSpec, SampleFragment.class, holidaBundle);
	}


	private void setupEventhandling() {



	}
    public static class SampleFragment extends Fragment {
        TimeTableDao timeTableDao;
        int route;
        int busStop;
        int weekType;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
             
//            TextView textView = new TextView(getActivity());
//            textView.setGravity(Gravity.CENTER);
////            textView.setText(getArguments().getString("name"));
        	
        	route = getArguments().getInt("route");
        	busStop = getArguments().getInt("busStop");
        	weekType = getArguments().getInt("week");
            
            timeTableDao = new TimeTableDao(getActivity());
            
            ListView listView = new ListView(getActivity());
            ArrayList<TimeTableItem> items = getTimeList(weekType);
            
//            Time time = new Time("Asia/Tokyo");
//            String timeString = time.hour + ":";
//            
//            listView.setSelection(position);
            TimeTableAdapter adapter = new TimeTableAdapter(getActivity(), R.layout.time_table_row, items);
            listView.setAdapter(adapter);
            
             
            return listView;
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
}