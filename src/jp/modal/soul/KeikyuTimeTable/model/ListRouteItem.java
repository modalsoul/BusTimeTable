package jp.modal.soul.KeikyuTimeTable.model;

import java.util.List;

public class ListRouteItem {

	List<RouteItem> routeList;
	
	public List<RouteItem> routeItem() {
		return routeList;
	}
	public void routeItem(List<RouteItem> list) {
		this.routeList = list;
	}

}
