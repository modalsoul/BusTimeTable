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
	
	public Button selectLineButton; /** 行き先選択ボタン */
	
	
	public static String BUSS_STOP_NUMBER = "BUS_STOP_NUMBER";
	
	
	/** 行き先を選択するバス停の番号 */
	public int busStop;

	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_stop);
        
        // intentからの設定値の取得
        setupMember();
        // Viewのセットアップ
        setupView();
        // 動作のセットアップ
        setupEventhandling();
        
    }


	private void setupMember() {
		// 行き先を選択するバス停をセット
		busStop = getIntent().getExtras().getInt(BUSS_STOP_NUMBER);
		
	}


	private void setupView() {
		// 行き先を選択するバス停名を設定
		TextView busStopName = (TextView)findViewById(R.id.selected_bus_stop_name);
		busStopName.setText(Integer.toString(busStop));
		// 行き先選択ボタンのセット
		selectLineButton = (Button)findViewById(R.id.select_line_btn);
		
	}
	private void setupEventhandling() {
		
		
		
	}
	
}