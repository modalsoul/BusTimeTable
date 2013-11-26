package jp.modal.soul.KeikyuTimeTable.task;

import java.util.ArrayList;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.model.BusStopDao;
import jp.modal.soul.KeikyuTimeTable.model.BusStopItem;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class SearchBusStopTask extends AsyncTask<Void, Void, ArrayList<BusStopItem>>{

	Context context;
	String word;
	ProgressDialog progressDialog;
	boolean isShowingDialog;
	
	public SearchBusStopTask(Context context, String word) {
		this.context = context;
		this.word = word;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		isShowingDialog = true;
		showDialog();
	}
	
	@Override
	protected ArrayList<BusStopItem> doInBackground(Void... arg0) {
		return searchBusstop();
	}
	
	@Override
	protected void onPostExecute(ArrayList<BusStopItem> result) {
		super.onPostExecute(result);
		isShowingDialog = false;
		dismissDialog();
	}
	
	ArrayList<BusStopItem> searchBusstop() {
		BusStopDao dao = new BusStopDao(context);
		ArrayList<BusStopItem> list = dao.queryBusStopByName(word);
		return list;
	}

	private void showDialog() {
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(context.getText(R.string.now_preparing));
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
	private void dismissDialog() {
		progressDialog.dismiss();
		progressDialog = null;
	}
}
