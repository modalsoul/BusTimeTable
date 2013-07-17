package jp.modal.soul.KeikyuTimeTable.task;

import java.util.List;

import jp.modal.soul.KeikyuTimeTable.model.BusStopDao;
import jp.modal.soul.KeikyuTimeTable.model.BusStopItem;
import android.content.Context;
import android.os.AsyncTask;

public class SearchBusStopTask extends AsyncTask<Void, Void, List<BusStopItem>>{

	Context context;
	String word;
	
	public SearchBusStopTask(Context context, String word) {
		this.context = context;
		this.word = word;
	}
	
	@Override
	protected List<BusStopItem> doInBackground(Void... arg0) {
		
		return searchBusstop();
	}
	
	List<BusStopItem> searchBusstop() {
		BusStopDao dao = new BusStopDao(context);
		List<BusStopItem> list = dao.queryBusStopByName(word);
		return list;
	}

}
