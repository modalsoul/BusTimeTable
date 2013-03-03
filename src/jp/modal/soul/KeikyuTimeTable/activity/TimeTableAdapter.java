package jp.modal.soul.KeikyuTimeTable.activity;


import java.util.List;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.model.RouteItem;
import jp.modal.soul.KeikyuTimeTable.model.TimeTableDao;
import jp.modal.soul.KeikyuTimeTable.model.TimeTableItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TimeTableAdapter extends ArrayAdapter<TimeTableItem> {

	private Context context;
	private List<TimeTableItem> items;
	private LayoutInflater inflater;
	
	/** Dao */
	TimeTableDao timetableDao;
	
	/** View */
	View timetableRow;
	TextView startingTime;
	
	/**
	 * コンストラクタ
	 * @param context
	 * @param textViewResourceId
	 * @param items
	 */
	TimeTableAdapter(Context context, int textViewResourceId, List<TimeTableItem> items){
		super(context, textViewResourceId, items);
		this.context = context;
		this.items = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Viewの受け取り
		timetableRow = convertView;
		// 受け取ったViewがnullなら新しくViewを生成
		if(timetableRow == null) {
			timetableRow = inflater.inflate(R.layout.time_table_row, null);
		}
		// 表示データのセット
		TimeTableItem item = items.get(position);
		// itemがnullでなければViewにセット
		if(item != null) {
			setupRowView(item);
		}

		return timetableRow;
	}

	/**
	 * 発車時刻のセット
	 * @param item
	 */
	private void setupRowView(TimeTableItem item) {
		startingTime = (TextView)timetableRow.findViewById(R.id.starting_time);
		startingTime.setText(item.startingTime);

	}
	

}
