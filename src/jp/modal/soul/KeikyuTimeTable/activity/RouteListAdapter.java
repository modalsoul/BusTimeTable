package jp.modal.soul.KeikyuTimeTable.activity;


import java.util.List;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.model.RouteDao;
import jp.modal.soul.KeikyuTimeTable.model.RouteItem;
import jp.modal.soul.KeikyuTimeTable.util.Utils;
import android.content.Context;
import android.graphics.Typeface;
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
	
	/** Font */
	Typeface face;
	String font = Utils.getFont();
	
	/**
	 * コンストラクタ
	 * @param context
	 * @param textViewResourceId
	 * @param items
	 */
	RouteListAdapter(Context context, int textViewResourceId, List<RouteItem> items){
		super(context, textViewResourceId, items);
		this.context = context;
		this.items = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		// Viewの受け取り
		routeRow = convertView;
		// 受け取ったViewがnullなら新しくViewを生成
		if(routeRow == null) {
			routeRow = inflater.inflate(R.layout.route_row, null);
			holder = new ViewHolder();
			holder.routeName = (TextView)routeRow.findViewById(R.id.route_name);
			holder.toFrom = (TextView)routeRow.findViewById(R.id.to_from);
			setFont(holder.routeName);
			setFont(holder.toFrom);
			routeRow.setTag(holder);
		} else {
			holder = (ViewHolder)routeRow.getTag();
		}
		// 表示データのセット
		RouteItem item = items.get(position);
		// itemがnullでなければViewにセット
		if(item != null) {
			setupRowView(holder, item);
		}

		return routeRow;
	}

	/**
	 * 路線情報のセット
	 * @param item
	 */
	private void setupRowView(ViewHolder holder, RouteItem item) {
		// 路線名のセット
//		routeName = (TextView)routeRow.findViewById(R.id.route_name);
		holder.routeName.setText(item.routeName());
//		setFont(routeName);

		// 始発、終点のセット
//		toFrom = (TextView)routeRow.findViewById(R.id.to_from);
		holder.toFrom.setText(String.format(context.getString(R.string.starting_to_terminal), item.starting, item.terminal));

	}
	
	private void setFont(TextView text) {
		face = Typeface.createFromAsset(context.getAssets(), font);
		text.setTypeface(face);
	}
	
    static class ViewHolder {
        TextView routeName;
        TextView toFrom;
    }

}
