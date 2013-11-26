package jp.modal.soul.KeikyuTimeTable.model;

import java.util.ArrayList;

public class TrafficInfoItem {
	String busStopName;
	ArrayList<String> arriveTime;
	ArrayList<String> terminalTime;
	public static final int BUS_STOP_NUM = 4;
	public static final int THREE_BEFORE = 3;
	public static final int TWO_BEFORE = 2;
	public static final int ONE_BEFORE = 1;
	public static final int JUST_NOW = 0;
	
	public TrafficInfoItem(String busStopName) {
		this.busStopName = busStopName;
		arriveTime = new ArrayList<String>();
		terminalTime = new ArrayList<String>();
		init();
	}
	void init() {
		for(int i = 0; i < BUS_STOP_NUM; i++) {
			arriveTime.add("");
			terminalTime.add("");
		}
	}
	
	public String busStopName() {
		return busStopName;
	}
	
	public void arriveTime(int i, String timeStr) {
		arriveTime.set(i, timeStr);
	}
	
	public void terminalTime(int i, String timeStr) {
		terminalTime.set(i, timeStr);
	}
	
	public String arriveTime(int i) {
		return arriveTime.get(i);
	}

	public String terminalTime(int i) {
		return terminalTime.get(i);
	}
	
	public void clear() {
		arriveTime.clear();
		terminalTime.clear();
		init();
	}
}
