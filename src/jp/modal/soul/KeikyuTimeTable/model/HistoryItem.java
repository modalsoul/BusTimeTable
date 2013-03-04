package jp.modal.soul.KeikyuTimeTable.model;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;

public class HistoryItem implements Comparable<HistoryItem>, Serializable{
	/** ログ出力用 タグ */
    public final String TAG = this.getClass().getSimpleName();
    
    // 履歴オブジェクトのプロパティ群
    public long id;
    public long routeId;
    public long busStopId;
    
    /** Dao */
    BusStopDao busStopDao;
    
    /** Item */
    ArrayList<BusStopItem> historyItemList;
    
    /**
     * Serializableクラスに記述する定数
     */
    private static final long serialVersionUID = 2L;

    @Override
	public int compareTo(HistoryItem another) {
		return (int)(this.id - another.id);
	}

}