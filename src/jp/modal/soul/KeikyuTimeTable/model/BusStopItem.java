package jp.modal.soul.KeikyuTimeTable.model;

import java.io.Serializable;

public class BusStopItem implements Comparable<BusStopItem>, Serializable{
	/** ���O�o�͗p �^�O */
    public final String TAG = this.getClass().getSimpleName();
    
    // �o�X��I�u�W�F�N�g�̃v���p�e�B�Q
    public long id;
    public String busStopName;
    
    /**
     * Serializable�N���X�ɋL�q����萔
     */
    private static final long serialVersionUID = 1L;
    @Override
	public int compareTo(BusStopItem another) {
		return (int)(this.id - another.id);
	}
}
