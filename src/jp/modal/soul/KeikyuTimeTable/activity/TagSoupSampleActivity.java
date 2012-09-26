package jp.modal.soul.KeikyuTimeTable.activity;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.InputSource;

import android.content.Context;
import android.content.res.AssetManager;

public class TagSoupSampleActivity  {

	Context mContext;
	
	public TagSoupSampleActivity(Context mContext) {
		this.mContext = mContext;
	}
	
	public void tagSoupTest() {
		try {
			AssetManager as = mContext.getResources().getAssets();   
			InputStream is = as.open("sample.txt");
			
			Parser parser = new Parser();
			
			HttpHandler handler = new HttpHandler();
			
			parser.setContentHandler(handler);
			
			parser.parse(new InputSource(new InputStreamReader(is, "utf-8")));
			
			is.close();
			
			
			
		} catch (Exception e) {
	
		}
	}
	
	
}



