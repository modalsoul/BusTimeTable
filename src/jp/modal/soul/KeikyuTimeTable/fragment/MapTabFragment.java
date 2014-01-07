package jp.modal.soul.KeikyuTimeTable.fragment;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.util.Utils;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MapTabFragment extends Fragment {
	String target;
	LocationManager locationManager;
	/** Font */
	Typeface face;
	String font = Utils.getFont();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		
		target = getArguments().getString("busStop");
		LinearLayout tabLinearLayout = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.map_tab, null).findViewById(R.id.map_linear_layout);
		
		TextView busStopName = (TextView)tabLinearLayout.findViewById(R.id.map_bus_stop_name);
		busStopName.setText(target + " バス停");
		setFont(busStopName);
		
		Button openMapButton = (Button)tabLinearLayout.findViewById(R.id.open_map_button);
		setFont(openMapButton);
		
		TextView mapComment = (TextView)tabLinearLayout.findViewById(R.id.map_comment);
		setFont(mapComment);
		
		openMapButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoMap();
			};
		});
		Button naviButton = (Button)tabLinearLayout.findViewById(R.id.navi_button);
		setFont(naviButton);
		naviButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openNavi();
			};
		});
		
		TextView naviComment = (TextView)tabLinearLayout.findViewById(R.id.navi_comment);
		setFont(naviComment);
		
		if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Button gpsOnButton = (Button)tabLinearLayout.findViewById(R.id.gps_button);
			gpsOnButton.setOnClickListener(gpsOnButtonOnClickListener);
			gpsOnButton.setVisibility(View.VISIBLE);
			setFont(gpsOnButton);
		}
		
		return tabLinearLayout;
	}

	View.OnClickListener gpsOnButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(callGPSSettingIntent);
		}
	};
	
	void gotoMap() {
		Intent mi = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=\"" + target + "（バス）\""));
		startActivity(mi);
	}
	private void setFont(TextView text) {
		face = Typeface.createFromAsset(getActivity().getAssets(), font);
		text.setTypeface(face);
	}
	void openNavi() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
		intent.setData(Uri.parse("http://maps.google.com/maps?daddr=" + target +"（バス）&dirflg=w"));
		startActivity(intent);
	}
}
