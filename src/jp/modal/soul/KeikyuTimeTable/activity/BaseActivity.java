package jp.modal.soul.KeikyuTimeTable.activity;

import jp.modal.soul.KeikyuTimeTable.R;
import jp.modal.soul.KeikyuTimeTable.util.Utils;
import android.app.Activity;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.TextView;


public class BaseActivity extends Activity {
	Typeface face;
	String font = Utils.getFont();

	public void setFont(TextView text) {
		face = Typeface.createFromAsset(getAssets(), font);
		text.setTypeface(face);
	}
	
	public void setFont(Button button) {
		face = Typeface.createFromAsset(getAssets(), font);
		button.setTypeface(face);
	}
	
	boolean isTabletMode(){
	    return getResources().getBoolean(R.bool.is_tablet);
	}
}
