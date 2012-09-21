package jp.modal.soul.KeikyuTimeTable.activity;


import java.util.ArrayList;
import java.util.List;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class KeikyuTimeTableActivity extends Activity {

	/** �o�X�⃊�X�g��\������_�C�A���O */
	public AlertDialog busStopListDialog;
	
	/** �I�����ꂽ�o�X��̔ԍ� */
	public int selectedBusStopNumber; 
	/** �I�����ꂽ�H��ID */
	public long selectedRouteId;
	
	/** DAO */
	private RouteDao routeDao;
	private BusStopDao busStopDao;
	
	/** ItemList */
	List<RouteItem> routeList;
	
	/** Adapter */
	RouteListAdapter adapter;
	
	/** ListView */
	ListView listView;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // DAO�̃Z�b�g�A�b�v
        setupDao();
        // ListView�̃Z�b�g�A�b�v
        setupListView();

        
        
// ����N�����̃Z�b�g�A�b�v
//        setupInit();
//        // View�̃Z�b�g�A�b�v
//        setupView();
//        // ����̃Z�b�g�A�b�v
//        setupEventhandling();
//                
//        // �H�����X�g�̃Z�b�g�A�b�v
//        setupRouteList();
        

    }
    /**
     * ListView�̃Z�b�g�A�b�v
     */
    public void setupListView() {
    	// �H�����̎擾
    	routeList = routeDao.queryRouteOrderById();
    	// Adapter�̐���
    	adapter = new RouteListAdapter(this, R.layout.route_row, routeList);
    	// ListView�̎擾
    	listView = (ListView)findViewById(R.id.lineList);
    	// �A�_�v�^�[�̐ݒ�
    	listView.setAdapter(adapter);
    }
    /**
     * Dao�̃Z�b�g�A�b�v
     */
    private void setupDao() {
    	routeDao = new RouteDao(getApplicationContext());
    	busStopDao = new BusStopDao(getApplicationContext());
    }

	private void setupRouteList() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ArrayList<RouteItem> routeList = routeDao.queryRouteOrderById();
            
        // �A�C�e���̒ǉ�
        for(RouteItem item : routeList) {
        	adapter.add(item.routeName);
        }
        
        ListView listView = (ListView)findViewById(id.lineList);
        // �A�_�v�^�[�̐ݒ�
        listView.setAdapter(adapter);
        // ListView�̃A�C�e�����N���b�N���ꂽ�Ƃ��̃R�[���o�b�N���X�i�[��o�^
        listView.setOnItemClickListener(onRouteItemClick);
	}

	// �H�����X�g�̃A�C�e�����N���b�N���ꂽ�Ƃ��̃��X�i�[
	AdapterView.OnItemClickListener onRouteItemClick = new AdapterView.OnItemClickListener() {
    	@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
    			long id) {
    		ListView listView = (ListView)parent;
    		// �N���b�N���ꂽ�A�C�e���̎擾
    		String item = (String)listView.getItemAtPosition(position);
    		Toast.makeText(KeikyuTimeTableActivity.this, item, Toast.LENGTH_SHORT).show();
    		
    		// �H��ID���Z�b�g
    		selectedRouteId = position + 1;
    		showBusStopList();
    		
    	}
	};

    /**
     *  View�̃Z�b�g�A�b�v
     */
	private void setupView() {
				
	}
	/**
	 *  ����̃Z�b�g�A�b�v
	 */
	private void setupEventhandling() {
			
	}
		
	/**
	 * �o�X��I���̃_�C�A���O��\������
	 */
	private void showBusStopList() {	
		// �H�������擾
		RouteItem routeItem = routeDao.queryAllBusStopByRouteId(selectedRouteId);
		
		if(routeItem == null) {
			// �V�X�e���G���[
		}
		String[] busStops = Utils.busStopIdString2StringItems(routeItem.busStops);
		// �H���̃o�X����擾
		ArrayList<BusStopItem> busStopItems = busStopDao.queryBusStopById(busStops);
		
		// �o�X�▼��ݒ�
		final CharSequence[] busStopNames = new CharSequence[busStopItems.size()];
		int i = 0;
		for(BusStopItem item: busStopItems) {
			busStopNames[i] = item.busStopName;
			i++;
		}
		// �o�X��I���̃��X�g��\������_�C�A���O���쐬
		AlertDialog.Builder builder = new AlertDialog.Builder(KeikyuTimeTableActivity.this);		
		builder.setTitle(R.string.bus_stop_list_dialog_title);
		builder.setSingleChoiceItems(busStopNames, -1, new DialogInterface.OnClickListener(){
			// �o�X�⃊�X�g��I�������Ƃ��̓����ݒ�
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// �I�����ꂽ�o�X��ԍ���ݒ�
				selectedBusStopNumber = which +1;
				// �o�X��̍s����I����ʂ֑J��
				launchBusStop();
			}
		});
		// �o�X��I���̃��X�g��\������_�C�A���O���쐬
		builder.show();
	}
	
	/**
	 * �I�����ꂽ�o�X��̍s�����I�������ʂ֑J�ڂ���
	 */
	public void launchBusStop(){
		// BusStopActivity���N������intent�̍쐬
		Intent intent = new Intent(getApplicationContext(), BusStopActivity.class);
		// �I�����ꂽ�o�X��ԍ����w��
		intent.putExtra(BusStopActivity.BUSS_STOP_NUMBER, selectedBusStopNumber);
		// BusStopActivity�̋N��
		Utils.intentLauncher(this, intent);	
//		this.startActivity(intent);
	}
	
	/**
	 * �o�X�▼�̃��X�g���擾
	 * @return
	 */
	public CharSequence[] getBusStopList() {
		ArrayList<BusStopItem> items = busStopDao.queryBusStopOrderById();
		
		// �o�X�␔
		int busStopNum = items.size();
		// �o�X�▼�̃��X�g
		CharSequence[] busStopList = new CharSequence[busStopNum];
		int i = 0;
		for(BusStopItem item: items) {
			busStopList[i] = item.busStopName;
			i++;
		}
		return busStopList;
	}
	
	/**
	 * �A�v������N�����̏���������
	 */
	public void setupInit() {
		InitState initState = new InitState();
		// ����N���̔���
		if(initState.getStatus() == InitState.PREFERENCE_INIT) {
			// ����N���̏ꍇ�A�����f�[�^���Z�b�g
			busStopDao.setup();
			routeDao.setup();
			// �N����Ԃ�ύX
			initState.setStatus(InitState.PREFERENCE_BOOTED);
		}
	}
	/**
	 * �N���X�e�[�^�X�̋��L�v���t�@�����X�̃N���X
	 * TODO �ʃt�@�C���ւ̐؂�o��
	 * @author M
	 *
	 */
	public class InitState {
		/** ���L�v���t�@�����X�� */
		public static final String INIT_PREFERENCE_NAME = "InitState";
		// �N���X�e�[�^�X�̒萔
		/** ���N���@*/
		public static final int PREFERENCE_INIT = 0;
		/** �N�� */
		public static final int PREFERENCE_BOOTED = 1;
		
		/**
		 * �N���X�e�[�^�X�̕ۑ�
		 * @param status
		 */
		void setStatus(int state) {
			SharedPreferences sp = getSharedPreferences(INIT_PREFERENCE_NAME, MODE_PRIVATE);
			sp.edit().putInt(INIT_PREFERENCE_NAME, state).commit();
		}
		/**
		 * �N���X�e�[�^�X�̎擾
		 * @return
		 */
		int getStatus() {
			SharedPreferences sp = getSharedPreferences(INIT_PREFERENCE_NAME, MODE_PRIVATE);
			return sp.getInt(INIT_PREFERENCE_NAME, 0);
		}
		
		
	}
}