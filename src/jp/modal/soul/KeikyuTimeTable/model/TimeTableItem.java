package jp.modal.soul.KeikyuTimeTable.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;

public class TimeTableItem implements Comparable<TimeTableItem>, Serializable{
	/** ���O�o�͗p �^�O */
    public final String TAG = this.getClass().getSimpleName();
    
    // �o�X��I�u�W�F�N�g�̃v���p�e�B�Q
    public long id;
    public long busStopId;
    public long routeId;
    public int type;
    public String startingTime;
    
    /**
     * Serializable�N���X�ɋL�q����萔
     */
    private static final long serialVersionUID = 1L;
    @Override
	public int compareTo(TimeTableItem another) {
		return (int)(this.id - another.id);
	}
}
