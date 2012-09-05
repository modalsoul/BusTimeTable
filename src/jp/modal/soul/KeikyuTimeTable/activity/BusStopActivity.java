package jp.modal.soul.KeikyuTimeTable.activity;

import jp.modal.soul.KeikyuTimeTable.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BusStopActivity extends Activity {
	
	public Button selectLineButton; /** �s����I���{�^�� */
	
	
	public static String BUSS_STOP_NUMBER = "BUS_STOP_NUMBER";
	
	
	/** �s�����I������o�X��̔ԍ� */
	public int busStop;

	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_stop);
        
        // intent����̐ݒ�l�̎擾
        setupMember();
        // View�̃Z�b�g�A�b�v
        setupView();
        // ����̃Z�b�g�A�b�v
        setupEventhandling();
        
    }


	private void setupMember() {
		// �s�����I������o�X����Z�b�g
		busStop = getIntent().getExtras().getInt(BUSS_STOP_NUMBER);
		
	}


	private void setupView() {
		// �s�����I������o�X�▼��ݒ�
		TextView busStopName = (TextView)findViewById(R.id.selected_bus_stop_name);
		busStopName.setText(Integer.toString(busStop));
		// �s����I���{�^���̃Z�b�g
		selectLineButton = (Button)findViewById(R.id.select_line_btn);
		
	}
	private void setupEventhandling() {
		
		
		
	}
	
}