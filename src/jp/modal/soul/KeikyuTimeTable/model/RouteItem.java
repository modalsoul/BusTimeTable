package jp.modal.soul.KeikyuTimeTable.model;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;

public class RouteItem implements Comparable<RouteItem>, Serializable{
	/** ログ出力用 タグ */
    public final String TAG = this.getClass().getSimpleName();
    
    // 路線オブジェクトのプロパティ群
    public long id;
    public String routeName;
    public int terminal;
    public int starting;
    public String busStops;
    
    /** Dao */
    BusStopDao busStopDao;
    
    /** Item */
    ArrayList<BusStopItem> busStopItemList;
    
    /**
     * Serializableクラスに記述する定数
     */
    private static final long serialVersionUID = 2L;

    @Override
	public int compareTo(RouteItem another) {
		return (int)(this.id - another.id);
	}

	/**
	 * 路線名を取得する
	 * @return 路線名
	 */
	public String routeName() {
		return routeName;
	}
	/**
	 * バス停名を取得
	 * @param context
	 * @param busStopId
	 * @return
	 */
	private String getBusStopName(Context context, long busStopId) {
		busStopDao = new BusStopDao(context);

		busStopItemList = busStopDao.queryBusStopById(new String[]{Long.toString(busStopId)});

		return busStopItemList.get(0).busStopName;
	}
	/**
	 * 終点バス停名を取得
	 * @param context
	 * @return
	 */
	public String terminalName(Context context) {
		return getBusStopName(context, terminal);
	}
	/**
	 * 始発バス停名を取得
	 * @param context
	 * @return
	 */
	public String startingName(Context context) {
		return getBusStopName(context, starting);
	}
}