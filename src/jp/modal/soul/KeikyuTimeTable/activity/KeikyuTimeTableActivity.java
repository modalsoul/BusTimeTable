package jp.modal.soul.KeikyuTimeTable.activity;


import java.util.ArrayList;

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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class KeikyuTimeTableActivity extends Activity {
	/** �o�X��I���{�^�� */
	public Button selectBusStopButton;
	/** �o�X�⃊�X�g��\������_�C�A���O */
	public AlertDialog busStopListDialog;
	
	/** �I�����ꂽ�o�X��̔ԍ� */
	public int selectedBusStopNumber; 
	/** �I�����ꂽ�H��ID */
	public long selectedRouteId;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // ����N�����̃Z�b�g�A�b�v
        setupInit();
        // View�̃Z�b�g�A�b�v
        setupView();
        // ����̃Z�b�g�A�b�v
        setupEventhandling();
        
        // TODO adaptor���O����
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        
        // ���X�g�̎擾
        RouteDao routeDao = new RouteDao(getApplicationContext());
        ArrayList<RouteItem> routeList = routeDao.queryRouteOrderById();
            
        // �A�C�e���̒ǉ�
        for(RouteItem item : routeList) {
        	adapter.add(item.routeName);
        }
        
        ListView listView = (ListView)findViewById(id.lineList);
        // �A�_�v�^�[�̐ݒ�
        listView.setAdapter(adapter);
        // ListView�̃A�C�e�����N���b�N���ꂽ�Ƃ��̃R�[���o�b�N���X�i�[��o�^
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
		});
        

    }


    /**
     *  View�̃Z�b�g�A�b�v
     */
	private void setupView() {
		
		selectBusStopButton = (Button)findViewById(R.id.select_bus_stop_btn);
		
	}
	/**
	 *  ����̃Z�b�g�A�b�v
	 */
	private void setupEventhandling() {
		
		selectBusStopButton.setOnClickListener(selectBusStopClickListener);
		
	}
	
	// �o�X��I���{�^�����N���b�N�����Ƃ��̓����ݒ�
	public View.OnClickListener selectBusStopClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {			
			showBusStopList();
		}
	};
	
	/**
	 * �o�X��I���̃_�C�A���O��\������
	 */
	private void showBusStopList() {	
		RouteDao routeDao = new RouteDao(getApplicationContext());
		// �H�������擾
		Log.e("GYAAAAAAAAAAA", Long.toString(selectedRouteId));
		RouteItem routeItem = routeDao.queryAllBusStopByRouteId(selectedRouteId);
		if(routeItem == null) {
			// �V�X�e���G���[
		}
		String[] busStops = Utils.busStopIdString2StringItems(routeItem.busStops);
		// �H���̃o�X����擾
		BusStopDao busStopDao = new BusStopDao(getApplicationContext());
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
//		Util.intentLauncher(this, intent);	
		this.startActivity(intent);
	}
	
	/**
	 * �o�X�▼�̃��X�g���擾
	 * @return
	 */
	public CharSequence[] getBusStopList() {
		BusStopDao busStopDao = new BusStopDao(getApplicationContext());
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
			BusStopDao busStopDao = new BusStopDao(getApplicationContext());
			busStopDao.setup();
			RouteDao routeDao = new RouteDao(getApplicationContext());
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