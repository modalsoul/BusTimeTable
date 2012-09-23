package jp.modal.soul.KeikyuTimeTable.activity;

import java.util.ArrayList;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.model.BusStopDao;
import jp.modal.soul.KeikyuTimeTable.model.BusStopItem;
import android.app.TabActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class BusStopActivity extends TabActivity {
	
	public Button selectLineButton; /** 行き先選択ボタン */
	
	
	public static String BUSS_STOP_NUMBER = "BUS_STOP_NUMBER";
	
	
	/** 行き先を選択するバス停の番号 */
	public int busStop;

	/** Dao */
	BusStopDao busStopDao;
	
	/** View */
	TextView busStopName;
	
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
        
        
     // TabHostのインスタンスを取得
        TabHost tabs = getTabHost(); 
    
        LayoutInflater.from(this).inflate(R.layout.bus_stop, tabs.getTabContentView(), true);
        
     // タブシートの設定
        TabSpec tab01 = tabs.newTabSpec("TabSheet1");
        tab01.setIndicator("TabSheet1");
        tab01.setContent(R.id.weekday_content);
        tabs.addTab(tab01);
        TabSpec tab02 = tabs.newTabSpec("TabSheet2");
        tab02.setIndicator("TabSheet2");
        tab02.setContent(R.id.saturday_content);
        tabs.addTab(tab02);
        TabSpec tab03 = tabs.newTabSpec("TabSheet3");
        tab03.setIndicator("TabSheet3");
        tab03.setContent(R.id.sunday_content);
        tabs.addTab(tab03);
     // 初期表示のタブ設定
        tabs.setCurrentTab(0);
        
        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
	        // タブがクリックされた時のハンドラ
	        @Override
	        public void onTabChanged(String tabId) {
	
	        	// クリックされた時の処理を記述
	        	TextView text;
	        	if(tabId == "TabSheet1") {
	        		text = (TextView)findViewById(R.id.weekday_tab_name);
	        		text.setTextColor(Color.BLUE);
	        	}
	        	else if(tabId == "TabSheet2") {
	        		text = (TextView)findViewById(R.id.saturday_tab_name);
	        		text.setTextColor(Color.RED);
	        	}
	        	else if(tabId == "TabSheet3") {
	        		text = (TextView)findViewById(R.id.sunday_tab_name);
	        		text.setTextColor(Color.GREEN);
	        	}
	        }
        });
    }


	private void setupMember() {
		// 行き先を選択するバス停をセット
		busStop = getIntent().getExtras().getInt(BUSS_STOP_NUMBER);
		
	}

	private void setupDao() {
		busStopDao = new BusStopDao(this);
	}

	private void setupView() {
		// バス停名Viewの取得
		busStopName = (TextView)findViewById(R.id.selected_bus_stop_name);
		// バス停名の取得
		ArrayList<BusStopItem> item = busStopDao.queryBusStop(Integer.toString(busStop));
		
		if(item != null) {
			busStopNameString = item.get(0).busStopName;
			// 行き先を選択するバス停名を設定
			busStopName.setText(busStopNameString);
		}		
	}
	private void setupEventhandling() {
		
		
		
	}
	
}