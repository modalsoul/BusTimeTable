package jp.modal.soul.KeikyuTimeTable.task;

import java.util.ArrayList;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.model.BusStopDao;
import jp.modal.soul.KeikyuTimeTable.model.BusStopItem;
import jp.modal.soul.KeikyuTimeTable.model.RouteDao;
import jp.modal.soul.KeikyuTimeTable.model.RouteItem;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class ListAllRouteTask extends AsyncTask<Void, Void, ArrayList<RouteItem>>{

	Context context;
	ProgressDialog progressDialog;
	boolean isShowingDialog;
	RouteDao dao;
	
	public ListAllRouteTask(Context context, RouteDao dao) {
		this.context = context;
		this.dao = dao;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		isShowingDialog = true;
		showDialog();
	}
	
	@Override
	protected ArrayList<RouteItem> doInBackground(Void... arg0) {
		return getAllRoute();
	}
	
	@Override
	protected void onPostExecute(ArrayList<RouteItem> result) {
		super.onPostExecute(result);
		isShowingDialog = false;
		dismissDialog();
	}
	
	ArrayList<RouteItem> getAllRoute() {
		return dao.queryRouteOrderById();
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
