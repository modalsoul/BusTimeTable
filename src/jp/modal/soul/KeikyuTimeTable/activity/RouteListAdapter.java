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
	
	/** View */
	View routeRow;
	TextView routeName;
	TextView toFrom;
	
	RouteListAdapter(Context context, int textViewResourceId, List<RouteItem> items){
		super(context, textViewResourceId, items);
		this.context = context;
		this.items = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// View�̎󂯎��
		routeRow = convertView;
		// �󂯎����View��null�Ȃ�V����View�𐶐�
		if(routeRow == null) {
			routeRow = inflater.inflate(R.layout.route_row, null);
		}
		// �\���f�[�^�̃Z�b�g	
		RouteItem item = items.get(position);
		// item��null�łȂ����View�ɃZ�b�g
		if(item != null) {

			setupRowView(item);
		}

		return routeRow;
	}

	private void setupRowView(RouteItem item) {
		routeName = (TextView)routeRow.findViewById(R.id.route_name);
		routeName.setText(item.routeName());
		toFrom = (TextView)routeRow.findViewById(R.id.to_from);
		// �H�����̃Z�b�g
		toFrom.setText(String.format(context.getString(R.string.starting_to_terminal), item.startingName(context), item.terminalName(context)));
		// �n���A�I�_�̃Z�b�g
//		toFrom.setText(String.format(context.getString(R.string.starting_to_terminal), 99, item.terminalName(context)));
	}
	

}
