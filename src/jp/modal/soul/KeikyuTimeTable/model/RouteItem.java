package jp.modal.soul.KeikyuTimeTable.model;

import java.io.Serializable;

import org.json.JSONObject;

public class RouteItem implements Comparable<RouteItem>, Serializable{
	/** ログ出力用 タグ */
    public final String TAG = this.getClass().getSimpleName();
    
    // 路線オブジェクトのプロパティ群
    public long id;
    public String routeName;
    public long terminal;
    public long starting;
    public String busStops;
    
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
}
