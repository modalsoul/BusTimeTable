package jp.modal.soul.KeikyuTimeTable.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;

public class TimeTableItem implements Comparable<TimeTableItem>, Serializable{
	/** ログ出力用 タグ */
    public final String TAG = this.getClass().getSimpleName();
    
    // バス停オブジェクトのプロパティ群
    public long id;
    public long busStopId;
    public long routeId;
    public int type;
    public String startingTime;
    
    /**
     * Serializableクラスに記述する定数
     */
    private static final long serialVersionUID = 1L;
    @Override
	public int compareTo(TimeTableItem another) {
		return (int)(this.id - another.id);
	}
}
