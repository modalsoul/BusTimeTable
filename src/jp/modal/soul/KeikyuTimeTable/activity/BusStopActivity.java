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
	
	public Button selectLineButton; /** �s����I���{�^�� */
	
	
	public static String BUSS_STOP_NUMBER = "BUS_STOP_NUMBER";
	
	
	/** �s�����I������o�X��̔ԍ� */
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
        
        // intent����̐ݒ�l�̎擾
        setupMember();
        // Dao�̃Z�b�g�A�b�v
        setupDao();
        // View�̃Z�b�g�A�b�v
        setupView();
        // ����̃Z�b�g�A�b�v
        setupEventhandling();
        
        
     // TabHost�̃C���X�^���X���擾
        TabHost tabs = getTabHost(); 
    
        LayoutInflater.from(this).inflate(R.layout.bus_stop, tabs.getTabContentView(), true);
        
     // �^�u�V�[�g�̐ݒ�
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
     // �����\���̃^�u�ݒ�
        tabs.setCurrentTab(0);
        
        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
	        // �^�u���N���b�N���ꂽ���̃n���h��
	        @Override
	        public void onTabChanged(String tabId) {
	
	        	// �N���b�N���ꂽ���̏������L�q
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
		// �s�����I������o�X����Z�b�g
		busStop = getIntent().getExtras().getInt(BUSS_STOP_NUMBER);
		
	}

	private void setupDao() {
		busStopDao = new BusStopDao(this);
	}

	private void setupView() {
		// �o�X�▼View�̎擾
		busStopName = (TextView)findViewById(R.id.selected_bus_stop_name);
		// �o�X�▼�̎擾
		ArrayList<BusStopItem> item = busStopDao.queryBusStop(Integer.toString(busStop));
		
		if(item != null) {
			busStopNameString = item.get(0).busStopName;
			// �s�����I������o�X�▼��ݒ�
			busStopName.setText(busStopNameString);
		}		
	}
	private void setupEventhandling() {
		
		
		
	}
	
}