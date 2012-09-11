package jp.modal.soul.KeikyuTimeTable.activity;


import java.util.ArrayList;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.model.BusStopDao;
import jp.modal.soul.KeikyuTimeTable.model.BusStopItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class KeikyuTimeTableActivity extends Activity {
	/** �o�X��I���{�^�� */
	public Button selectBusStopButton;
	/** �o�X�⃊�X�g��\������_�C�A���O */
	public AlertDialog busStopListDialog;
	
	/** �I�����ꂽ�o�X��̔ԍ� */
	public int selectedBusStopNumber; 
	
	
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
		
		final CharSequence[] busStopItems = getBusStopList();
		
		// �o�X��I���̃��X�g��\������_�C�A���O���쐬
		AlertDialog.Builder builder = new AlertDialog.Builder(KeikyuTimeTableActivity.this);		
		builder.setTitle(R.string.bus_stop_list_dialog_title);
		builder.setSingleChoiceItems(busStopItems, -1, new DialogInterface.OnClickListener(){
			// �o�X�⃊�X�g��I�������Ƃ��̓����ݒ�
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// �I�����ꂽ�o�X��ԍ���ݒ�
				selectedBusStopNumber = which;
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