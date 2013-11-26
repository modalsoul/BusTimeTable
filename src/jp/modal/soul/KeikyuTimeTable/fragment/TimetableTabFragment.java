package jp.modal.soul.KeikyuTimeTable.fragment;

import java.util.ArrayList;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.model.TimeSummaryDao;
import jp.modal.soul.KeikyuTimeTable.model.TimeSummaryItem;
import jp.modal.soul.KeikyuTimeTable.model.TimeTableDao;
import jp.modal.soul.KeikyuTimeTable.model.TimeTableItem;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TimetableTabFragment extends Fragment {
    TimeTableDao timeTableDao;
    TimeSummaryDao timeSummaryDao;
    
    int route;
    int busStop;
    int weekType;
    ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	route = getArguments().getInt("route");
    	busStop = getArguments().getInt("busStop");
    	weekType = getArguments().getInt("week");
        
        timeTableDao = new TimeTableDao(getActivity());

        listView = new ListView(getActivity());
        listView.setCacheColorHint(R.color.transparent);
        listView.setSelection(listView.getCount());
        
        ArrayList<TimeTableItem> items = getTimeList(weekType);
        
        TimeTableAdapter adapter = new TimeTableAdapter(getActivity(), R.layout.time_table_row, items);
        listView.setAdapter(adapter);
        setSelection();
         
        return listView;
    }
    
    private void setSelection() {
    	Time time = new Time("Asia/Tokyo");
    	time.setToNow();
    	int hour = time.hour;
    	
    	timeSummaryDao = new TimeSummaryDao(getActivity());
    	String[] selectionArgs = {Integer.toString(busStop), Integer.toString(route), Integer.toString(weekType)};
    	ArrayList<TimeSummaryItem> list = timeSummaryDao.querySummaryOrderByHour(selectionArgs);
    	
    	boolean isSet = false;
    	for(TimeSummaryItem item:list) {
    		if(item.hour >= hour -2 && item.hour < hour) {
    			listView.setSelection(item.position);
    		} else if(item.hour == hour) {
    			isSet = true;
    			listView.setSelection(item.position);
    		} else if(item.hour > hour && item.hour <= hour + 2 && !isSet) {        			
    			isSet = true;
    			listView.setSelection(item.position);
        	} 
    	}
    }
    
	/**
	 * 指定した曜日の発車時刻のリストを取得
	 * @param weekType
	 * @return　
	 */
	public ArrayList<TimeTableItem> getTimeList(int weekType) {
		String[] selectionArgs = { String.valueOf(busStop), String.valueOf(route), String.valueOf(weekType)};
		ArrayList<TimeTableItem> list = new ArrayList<TimeTableItem>();
		list = timeTableDao.queryBusStopOrderById(selectionArgs);
		return list;
	}
}
