package jp.modal.soul.KeikyuTimeTable.activity;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import android.util.Log;

public class HttpHandler implements ContentHandler {

    int mLevel = 0;
    
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
        Log.e("PARSE", "startElement:");
        for(int i = 0; i < mLevel; i++) Log.e("PARSE", "@");
        Log.e("PARSE", "<" + localName + "> ");
        for( int ii = 0; ii < atts.getLength(); ii++)
        {
            Log.e("PARSE", " ["+atts.getQName(ii)+"="+atts.getValue(ii)+"]");
        }
        System.out.println();
        mLevel ++;
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        mLevel --;

        Log.e("PARSE", "endElement  :");
        for(int i = 0; i < mLevel; i++) Log.e("PARSE", "@");
        System.out.println("<" + localName + ">");
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        Log.e("PARSE", "characters  :");
        for(int i = 0; i < mLevel; i++) Log.e("PARSE", "@");
        Log.e("PARSE", "[");
        for (int i = 0; i < length; i++) {
          Log.e("PARSE", ch[start + i] + "");
        }
        System.out.println("]");
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