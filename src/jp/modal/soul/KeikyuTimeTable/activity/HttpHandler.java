package jp.modal.soul.KeikyuTimeTable.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.modal.soul.KeikyuTimeTable.util.Utils;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import android.text.TextUtils;
import android.util.Log;

public class HttpHandler implements ContentHandler {

    int mLevel = 0;

    // timetableのtableフラグ
    boolean isTimeTable = false;
    // 時間ヘッダのフラグ
    boolean isRowHour = false;
    // 分のフラグ
    boolean isMinute = false;
    
    // 曜日分け
    int dayGrouping = NOT_DAY_GROUPING;
    
    // 曜日分けの定数
    public static final int WEEK_DAY = 1;
    public static final int SATARDAY = 2;
    public static final int SUNDAY = 3;
    public static final int NOT_DAY_GROUPING = 0;

    // エレメント名の保存先
    ArrayList<Integer> minutes;
    
    // 出発時刻のハッシュマップ
    public HashMap<Integer, String> startTimeHashMap;
    
    public ArrayList<String> weekDayStartTime;
    public ArrayList<String> satardayStartTime;
    public ArrayList<String> sundayStartTime;


    // 現在の行の時間
    int hour = 0;
    
    public HttpHandler() {
    	// メンバーの初期化
		minutes = new ArrayList<Integer>();
	}
    
    @Override
    public void startDocument() throws SAXException {
        System.out.println("startDocument");
    }
    
    @Override
    public void endDocument() throws SAXException {
        System.out.println("endDocument");
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
//        Log.e("PARSE", "startElement:");
//        for(int i = 0; i < mLevel; i++) Log.e("PARSE", "�@");
//        Log.e("PARSE", "<" + localName + "> ");
//        for( int ii = 0; ii < atts.getLength(); ii++) {
//            Log.e("PARSE", " ["+atts.getQName(ii)+"="+atts.getValue(ii)+"]");
//        }
//        System.out.println();
//        mLevel ++;

    	
    	// エレメントがTableの場合
    	if(localName.equals("table")) {
    		// 属性をチェック
    		for(int i = 0; i < atts.getLength(); i++) {
	    		// class属性がtimetableの場合
	    		if(atts.getQName(i).equals("class") && atts.getValue(i).endsWith("timetable")) {
	    			// timetableフラグをオンにセット
	    			this.isTimeTable = true;
	    		}
	    	}
    	}
    	
    	// timetable内の場合
    	if(isTimeTable) {
    		// エレメントがthの場合
    		if(localName.equals("th")) {
    			//属性をチェック
    			for(int i = 0; i < atts.getLength(); i++) {
    				// class属性がhourの場合
    	    		if(atts.getQName(i).equals("class") && atts.getValue(i).endsWith("hour")) {
    	    			// 時間ヘッダフラグをセット
    	    			this.isRowHour = true;
    	    		}	
    			}
    		}
    		// エレメントがtdの場合
    		if(localName.equals("td")) {
    			// 曜日分けをインクリメント
    			this.dayGrouping++;
    			this.isMinute = true;
    		}
    	}
    				
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
    	// timetable内の場合
    	if(isTimeTable) {
	    	if(localName.equals("table")) {
	    		// timetableが終了したので、フラグをオフにセット
	    		this.isTimeTable = false;
	    	}
	    	// tdの場合
	    	if(localName.equals("td")) {
	    		// 曜日分けをデクリメント
	    		this.dayGrouping--;
	    		this.isMinute = false;
	    	}
	    	// thの場合
	    	if(localName.equals("th")) {
	    		this.isRowHour = false;
	    	}
    	}
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
//
//        Log.e("PARSE", "characters  :");
//        for(int i = 0; i < mLevel; i++) Log.e("PARSE", "�@");
//        Log.e("PARSE", "[");
//        for (int i = 0; i < length; i++) {
//          Log.e("PARSE", ch[start + i] + "");
//        }
//        System.out.println("]");

//    	if(this.localName.equals("span")) {
//    		String value = new String(ch, start, length);
//    		if(!TextUtils.isEmpty(value)) {
//    			minutes.add(Integer.valueOf(value));
//    		}
//    	}
    	Log.e("HUGA", "GOGOGOGOGOGOGOG");
    	// 時間ヘッダの場合、現在の行の時間をセット
    	if(isRowHour) {
    		// 対象の文字配列が数値だった場合
    		if(Utils.isNum(ch)) {
    			// 現在の行の時間をセット
    			this.hour = Integer.valueOf(String.valueOf(ch));
    		}
    	}
    	
    	// 分の場合
    	if(isMinute) {
    		// 対象の文字配列が数値だった場合
    		if(Utils.isNum(ch)) {
    			// 時分を生成
    			String time = String.valueOf(hour) + ":" + String.valueOf(ch);
    			// バスの出発時間をセット
    			switch (dayGrouping) {
				case WEEK_DAY:
					weekDayStartTime.add(time);
					break;
				case SATARDAY:
					satardayStartTime.add(time);
					break;
				case SUNDAY:
					sundayStartTime.add(time);
					break;
				default:
					break;
				}
    			
    		}
    	}
    	
    	// 時の場合
    	if(isRowHour) {
    		// 対象の文字配列が数値だった場合
    		if(Utils.isNum(ch)) {
    			// 現在の行時間をセット
    			this.hour = Integer.valueOf(String.valueOf(ch));
    		}
    	}
    	for(int i = 0; i < weekDayStartTime.size(); i++) {
    		Log.e("HOGE", "start");
    		Log.e("WEEK", weekDayStartTime.get(i));
    	}
    }

	@Override
	public void endPrefixMapping(String arg0) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDocumentLocator(Locator arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void skippedEntity(String arg0) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}
}