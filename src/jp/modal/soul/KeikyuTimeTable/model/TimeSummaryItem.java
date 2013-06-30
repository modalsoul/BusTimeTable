package jp.modal.soul.KeikyuTimeTable.model;


//public class TimeSummaryItem implements Comparable<TimeSummaryItem>, Serializable{

public class TimeSummaryItem {
	/** ログ出力用 タグ */
    public final String TAG = this.getClass().getSimpleName();
    
    // タイムサマリーのプロパティ群
    public int busStopId;
    public int routeId;
    public int type;
    public int hour;
    public int position;
    
//    /**
//     * Serializableクラスに記述する定数
//     */
//    private static final long serialVersionUID = 1L;
//    @Override
//	public int compareTo(TimeSummaryItem another) {
//		return (int)(this.id - another.id);
//	}
}