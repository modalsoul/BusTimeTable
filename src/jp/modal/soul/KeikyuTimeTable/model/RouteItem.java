package jp.modal.soul.KeikyuTimeTable.model;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;

public class RouteItem implements Comparable<RouteItem>, Serializable{
	/** ���O�o�͗p �^�O */
    public final String TAG = this.getClass().getSimpleName();
    
    // �H���I�u�W�F�N�g�̃v���p�e�B�Q
    public long id;
    public String routeName;
    public long terminal;
    public long starting;
    public String busStops;
    
    /** Dao */
    BusStopDao busStopDao;
    
    /** Item */
    ArrayList<BusStopItem> busStopItemList;
    
    /**
     * Serializable�N���X�ɋL�q����萔
     */
    private static final long serialVersionUID = 2L;

    @Override
	public int compareTo(RouteItem another) {
		return (int)(this.id - another.id);
	}
	
	/**
	 * �H�������擾����
	 * @return �H����
	 */
	public String routeName() {
		return routeName;
	}
	/**
	 * �o�X�▼���擾
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
	 * �I�_�o�X�▼���擾
	 * @param context
	 * @return
	 */
	public String terminalName(Context context) {
		return getBusStopName(context, terminal);
	}
	/**
	 * �n���o�X�▼���擾
	 * @param context
	 * @return
	 */
	public String startingName(Context context) {
		return getBusStopName(context, starting);
	}
}
