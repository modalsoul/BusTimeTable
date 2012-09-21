package jp.modal.soul.KeikyuTimeTable.activity;


import java.util.List;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.model.RouteDao;
import jp.modal.soul.KeikyuTimeTable.model.RouteItem;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RouteListAdapter extends ArrayAdapter<RouteItem> {

	private Context context;
	private List<RouteItem> items;
	private LayoutInflater inflater;
	
	/** Dao */
	RouteDao routeDao;
	
	RouteListAdapter(Context context, int textViewResourceId, List<RouteItem> items){
		super(context, textViewResourceId, items);
		this.context = context;
		this.items = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// View�̎󂯎��
		View view = convertView;
		// �󂯎����View��null�Ȃ�V����View�𐶐�
		if(view == null) {
			view = inflater.inflate(R.layout.route_row, null);
		}
		// �\���f�[�^�̎擾
//		routeDao = new RouteDao(context);
//		ArrayList<RouteItem> routeList = routeDao.queryRouteOrderById();
		
		RouteItem item = items.get(position);
		// item��null�łȂ����View�ɃZ�b�g
		if(item != null) {
			Log.e("Not null, so...","adapter");
			TextView routeName = (TextView)view.findViewById(R.id.route_name);
			routeName.setText(item.routeName());
			TextView toFrom = (TextView)view.findViewById(R.id.to_from);
			toFrom.setText(String.format(context.getString(R.string.starting_to_terminal), 0, Long.toString(item.starting)));
			toFrom.setText(String.format(context.getString(R.string.starting_to_terminal), 1, Long.toString(item.terminal)));
		}
		
		
//		return super.getView(position, convertView, parent);
		return view;
	}
	

}
