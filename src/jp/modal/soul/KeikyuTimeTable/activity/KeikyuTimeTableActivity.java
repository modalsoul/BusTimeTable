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
	/** バス停選択ボタン */
	public Button selectBusStopButton;
	/** バス停リストを表示するダイアログ */
	public AlertDialog busStopListDialog;
	
	/** 選択されたバス停の番号 */
	public int selectedBusStopNumber; 
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Viewのセットアップ
        setupView();
        // 動作のセットアップ
        setupEventhandling();
    }


	private void setupView() {
		
		selectBusStopButton = (Button)findViewById(R.id.select_bus_stop_btn);
		
	}
	private void setupEventhandling() {
		
		selectBusStopButton.setOnClickListener(selectBusStopClickListener);
		
	}
	
	public View.OnClickListener selectBusStopClickListener = new OnClickListener() {
		// バス停選択ボタンをクリックしたときの動作を設定
		@Override
		public void onClick(View v) {			
			showBusStopList();
		}
	};
	
	/**
	 * バス停選択のダイアログを表示する
	 */
	private void showBusStopList() {
		final CharSequence[] busStopItems = {"大森西２丁目","蒲田","大森"};
		
		// バス停選択のリストを表示するダイアログを作成
		AlertDialog.Builder builder = new AlertDialog.Builder(KeikyuTimeTableActivity.this);		
		builder.setTitle(R.string.bus_stop_list_dialog_title);
		builder.setSingleChoiceItems(busStopItems, -1, new DialogInterface.OnClickListener(){
			// バス停リストを選択したときの動作を設定
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 選択されたバス停番号を設定
				selectedBusStopNumber = which;
				// バス停の行き先選択画面へ遷移
				launchBusStop();
			}
		});
		// バス停選択のリストを表示するダイアログを作成
		builder.show();
	}
	
	/**
	 * 選択されたバス停の行き先を選択する画面へ遷移する
	 */
	public void launchBusStop(){
		// BusStopActivityを起動するintentの作成
		Intent intent = new Intent(getApplicationContext(), BusStopActivity.class);
		// 選択されたバス停番号を指定
		intent.putExtra(BusStopActivity.BUSS_STOP_NUMBER, selectedBusStopNumber);
		// BusStopActivityの起動
//		Util.intentLauncher(this, intent);	
		this.startActivity(intent);
	}
}