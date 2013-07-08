package jp.modal.soul.KeikyuTimeTable.model;

import java.io.Serializable;

public class BusStopItem implements Comparable<BusStopItem>, Serializable{
	/** ログ出力用 タグ */
    public final String TAG = this.getClass().getSimpleName();
    
    // バス停オブジェクトのプロパティ群
    public long id;
    public long route_id;
    public String web_id;
    public String busStopName;
    public String location;
    
    /**
     * Serializableクラスに記述する定数
     */
    private static final long serialVersionUID = 1L;
    @Override
	public int compareTo(BusStopItem another) {
		return (int)(this.id - another.id);
	}
}