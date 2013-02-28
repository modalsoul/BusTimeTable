package jp.modal.soul.KeikyuTimeTable.timetable;

import java.util.ArrayList;
import java.util.List;

public class Times {
	public int hour = 0;
	public List<List> minute = null;
	
	public Times(int hour) {
		this.hour = hour;
		minute = new ArrayList<List>();
	}
	
	public void addMinute(List minutes) {
		minute.add(minutes);
	}

}
