package jp.modal.soul.KeikyuTimeTable.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.model.TrafficInfoItem;
import jp.modal.soul.KeikyuTimeTable.util.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class TrafficTabFragment extends Fragment {
	final int MAX_BUS_STOP_NUM = 3;
	String name;
	int search;
	int terminal;
	TrafficInfoItem trafficInfoItem;
	
	/** View */
	LinearLayout tabLinearLayout;
	TextView busStopNameView;
	TextView threeBefore;
	TextView twoBefore;
	TextView oneBefore;
	TextView rideBusStop;
	
	TextView labelComment;
	TextView label3B;
	TextView label2B;
	TextView label1B;
	
	Button reloadButton;

	/** Font */
	Typeface face;
	String font = Utils.getFont();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		name = getArguments().getString("name");
		search = getArguments().getInt("search");
		terminal = getArguments().getInt("terminal");
		tabLinearLayout = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.traffic_info_tab, null);
		
		trafficInfoItem = new TrafficInfoItem(name);
		
		setView();
		
		setEventHandling();
		
		setupTrafficInfo();
		
		setFontAll();
		
		return tabLinearLayout;
	}
	void setFontAll() {
		setFont(threeBefore);
		setFont(twoBefore);
		setFont(oneBefore);
		setFont(rideBusStop);
		setFont(label1B);
		setFont(label2B);
		setFont(label3B);
		setFont(labelComment);
		setFont(reloadButton);
	}
	void setView() {
		
		threeBefore = (TextView)tabLinearLayout.findViewById(R.id.threeBefore);
		twoBefore = (TextView)tabLinearLayout.findViewById(R.id.twoBefore);
		oneBefore = (TextView)tabLinearLayout.findViewById(R.id.oneBefore);
		rideBusStop = (TextView)tabLinearLayout.findViewById(R.id.rideBusStop);
		rideBusStop.setText(name + "停留所");
		
		label1B = (TextView)tabLinearLayout.findViewById(R.id.one_before_label);
		label2B = (TextView)tabLinearLayout.findViewById(R.id.two_before_label);
		label3B = (TextView)tabLinearLayout.findViewById(R.id.three_before_label);
		labelComment = (TextView)tabLinearLayout.findViewById(R.id.traffic_comment);
		
		reloadButton = (Button)tabLinearLayout.findViewById(R.id.reload_traffic_info);
	}
	
	void setEventHandling() {
		reloadButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ProgressDialog dialog = new ProgressDialog(getActivity());
				dialog.setMessage(getResources().getString(R.string.now_loading_route_list));
				dialog.setCancelable(false);
				dialog.show();
				setupTrafficInfo();
				dialog.dismiss();
			}
		});
	}
	void setupTrafficInfo() {
		trafficInfoItem.clear();
		String url = "http://keikyu-bus-loca.jp/BusLocWeb/getInpApchInfo.do?usn=" + search + "&dsn=" + terminal;
		try {
			Document doc = Jsoup.connect(url).get();
			Elements dd = doc.getElementsByTag("dd");
			
			int busStopIndex = 3;
			for(Elements ele : getBusStopElementList(dd)) {
				String[] time = ele.html().replaceAll("<.+?>", "/").split("/");
				if(time.length > 2) {
					trafficInfoItem.arriveTime(busStopIndex,time[1]);
					trafficInfoItem.terminalTime(busStopIndex,time[2]);
				}
				busStopIndex--;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		threeBefore.setText(makeTrafficText(trafficInfoItem, TrafficInfoItem.THREE_BEFORE));
		twoBefore.setText(makeTrafficText(trafficInfoItem, TrafficInfoItem.TWO_BEFORE));
		oneBefore.setText(makeTrafficText(trafficInfoItem, TrafficInfoItem.ONE_BEFORE));
	}
	String makeTrafficText(TrafficInfoItem item, int type) {
		return getRideString(item.busStopName(), item.arriveTime(type).trim()) + "\n" + getTerminalString(item.terminalTime(type).trim());
		
	}
	String getRideString(String busStopName, String str) {
		if(str.length() > 6) return busStopName + str.substring(5);
		else return "";
	}
	String getTerminalString(String str) {
		if(str.length() > 6) return "終点" + str.substring(5);
		else return "";
	}
	/**
	 * 運行情報HTMLの<dd>タグ以下のエレメントを受け取って、
	 * 
	 * @param dd
	 * @return
	 */
	ArrayList<Elements> getBusStopElementList(Elements dd) {
		ArrayList<Elements> busStopElementList = new ArrayList<Elements>();
		int busStopNum = MAX_BUS_STOP_NUM;
		for(Iterator<Element>i = dd.iterator(); i.hasNext() && busStopNum>=0;) {
			Elements bus =  ((Element)i.next()).getElementsByClass("bus");
			busStopElementList.add(bus);
			busStopNum--;
		}
		return busStopElementList;
	}
	private void setFont(TextView text) {
		face = Typeface.createFromAsset(getActivity().getAssets(), font);
		text.setTypeface(face);
	}
}