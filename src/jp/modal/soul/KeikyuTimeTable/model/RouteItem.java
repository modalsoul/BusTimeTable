package jp.modal.soul.KeikyuTimeTable.model;

import java.io.Serializable;

import org.json.JSONObject;

public class RouteItem implements Comparable<RouteItem>, Serializable{
	/** ���O�o�͗p �^�O */
    public final String TAG = this.getClass().getSimpleName();
    
    // �H���I�u�W�F�N�g�̃v���p�e�B�Q
    public long id;
    public String routeName;
    public long terminal;
    public long starting;
    public JSONObject busStops;
    
    /**
     * Serializable�N���X�ɋL�q����萔
     */
    private static final long serialVersionUID = 1L;

	@Override
	public int compareTo(RouteItem another) {
		return (int)(this.id - another.id);
	}
}
