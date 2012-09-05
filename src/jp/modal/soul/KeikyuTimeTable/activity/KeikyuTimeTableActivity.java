package jp.modal.soul.KeikyuTimeTable.activity;


import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
        
        // View�̃Z�b�g�A�b�v
        setupView();
        // ����̃Z�b�g�A�b�v
        setupEventhandling();
    }


	private void setupView() {
		
		selectBusStopButton = (Button)findViewById(R.id.select_bus_stop_btn);
		
	}
	private void setupEventhandling() {
		
		selectBusStopButton.setOnClickListener(selectBusStopClickListener);
		
	}
	
	public View.OnClickListener selectBusStopClickListener = new OnClickListener() {
		// �o�X��I���{�^�����N���b�N�����Ƃ��̓����ݒ�
		@Override
		public void onClick(View v) {			
			showBusStopList();
		}
	};
	
	/**
	 * �o�X��I���̃_�C�A���O��\������
	 */
	private void showBusStopList() {
		final CharSequence[] busStopItems = {"��X���Q����","���c","��X"};
		
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
}