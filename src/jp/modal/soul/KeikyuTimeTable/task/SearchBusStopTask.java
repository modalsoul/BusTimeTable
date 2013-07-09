package jp.modal.soul.KeikyuTimeTable.task;

import java.util.ArrayList;

import jp.modal.soul.KeikyuTimeTable.model.BusStopDao;
import jp.modal.soul.KeikyuTimeTable.model.BusStopItem;
import android.content.Context;
import android.os.AsyncTask;

public class SearchBusStopTask extends AsyncTask<Void, Void, Void>{

	Context context;
	String word;
	
	public SearchBusStopTask(Context context, String word) {
		this.context = context;
		this.word = word;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		
		return null;
	}
	
	void searchBusstop() {
		BusStopDao dao = new BusStopDao(context);
		ArrayList<BusStopItem> list = dao.queryBusStopByName(word);
		
		
	}

}
