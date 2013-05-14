/*

Various generic Horizon-related utils.

by Casey Durfee <casey.durfee@spl.org>
version 0.5 (Sep. 19th, 2005)


THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package org.spl.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


public class Utils{
	public static String getSessionID( String sTmp){
		// parses out the session ID from some HTML data
		int startSessionID = sTmp.indexOf("session=") + "session=".length();
		int endSessionID = sTmp.indexOf("&", startSessionID);
		if(startSessionID == -1 || endSessionID == -1){
			return "";
		} else {
			return sTmp.substring(startSessionID, endSessionID);
		}
	}
	
	public static String simpleTransform( String xmlData, String xslFileName){
		StringWriter ret = new StringWriter();
		try{
			File xslFile = new File( xslFileName);
			if ( ! xslFile.exists() ) {
				throw new RuntimeException( "File not found: " + xslFile.getAbsolutePath() );
			}
			TransformerFactory f = TransformerFactory.newInstance();
			Transformer t = f.newTransformer(new StreamSource(xslFile) );
			Source s = new StreamSource( new StringReader(xmlData) );
			Result r = new StreamResult( ret );
			t.transform( s,r);
                        
		} catch (Exception e){
			e.printStackTrace();
		}
		return ret.toString();
	}
	
	public static String getURL(String URLName){
	       StringBuffer response = new StringBuffer();
	       BufferedReader br = null;
		try{
		   URL u = new java.net.URL(URLName);
		   
		   HttpURLConnection hu = (HttpURLConnection) u.openConnection();
		   hu.setFollowRedirects(true);
		   br = new BufferedReader(new InputStreamReader( hu.getInputStream()));
		   String lineOn = "";
		   while((lineOn = br.readLine()) != null){                    
		       response.append(lineOn);
		   }
	       }
	       catch(Exception e){
		   e.printStackTrace();
		   return "";
	       }
	       finally{
		   try{	
			   if(br != null) br.close();
		   }
		   catch(Exception e){
			   e.printStackTrace();
		   }
	       }
	       return response.toString();
	}
	
	public static String getFile(String fileName) {
		String out = "";
		try {
			FileInputStream fis = new FileInputStream(fileName);
			DataInputStream dis = new DataInputStream(fis);
			byte[] b = new byte[ dis.available() ];
			dis.readFully( b);
			dis.close();
			out = new String( b, 0, b.length, "Cp850");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return out;
	}
		
	
	
	public static String dueIn(String d, String threshold){
		// d is a date in the MM/DD/YYYY format.
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date dNow = new Date();
		Date dDue;
		try{
			dDue = sdf.parse(d);
		} catch (java.text.ParseException pe){
			System.err.println("problem parsing.");
			dDue = (Date) dNow.clone() ;
		}
		int dueIn = (new Long( (dDue.getTime() - dNow.getTime()  ) / ( 1000 * 60 * 60 * 24)) ).intValue();
		String ret = "";
		if( dueIn < 0 ){
			if( dueIn == -1){
				ret = "Due yesterday:";
			} else {
				ret = "Overdue by " + (-1 * dueIn) + " days:"; // TODO load these from properties file
			}
		} else if (dueIn == 0){
			ret = "Due today:";
		} else if ( dueIn == 1   ){
			ret = "Due tomorrow:";
		} else {
			if(dueIn < Integer.parseInt(threshold))	// only want to change a title (hence forcing a new story in the reader if less than threshold)
				ret = "Due in " + dueIn + " days:";
		}
		return ret;
	}
	public static String pubDateForItemsOut(String dueDate, String ckoDate, String threshold) {
		// if it is > 5 days before due, pub. date should be checkout date.  Otherwise, it should be today's date.
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date dNow = new Date();
		Date dDue;
		try{
			dDue = sdf.parse(dueDate);
		} catch (java.text.ParseException pe){
			System.err.println("problem parsing.");
			dDue = (Date) dNow.clone() ;
		}
		int dueIn = (new Long( (dDue.getTime() - dNow.getTime()  ) / ( 1000 * 60 * 60 * 24)) ).intValue();
		int iThreshold = Integer.parseInt( threshold);
		if( dueIn > iThreshold ) { 			// want to use checkout date -- don't pester till almost due.
			return genRFC822Date( ckoDate );
		} else { 					// want to use today's date to force daily nagging updates when about to be due or overdue!
			SimpleDateFormat sdfOut = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
			return sdfOut.format( dNow );	
		}	
	}
	
	public static String pubDateForItemsOut(String dueDate, String ckoDate) {
		return pubDateForItemsOut(dueDate, ckoDate, "5");
	}
		
	public static String genRFC822Date( String date, String delay ) {
		/* delay = 7 means, give me the RFC822Date for 7 days after date
		   delay = -7 means, give me the date for 7 days before
		*/
		SimpleDateFormat sdfOut = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
		SimpleDateFormat sdfIn = new SimpleDateFormat("MM/dd/yyyy");
		Date inDate = new Date();
		Date outDate;
		try {
			outDate = sdfIn.parse( date );
		} catch (java.text.ParseException pe) {
			System.err.println("problem parsing.");
			outDate = (Date) inDate.clone() ;
		}
		int iDate = Integer.parseInt( delay);
		if( iDate == 0) {
			return sdfOut.format( outDate) ;
		} else {	
			Calendar gc = new GregorianCalendar(); 
			gc.setTime( outDate );
			if(iDate > 0) {
				for(int i = 0; i < iDate; i++) {
					gc.roll( Calendar.DATE, true);
				}
			} else {
				for(int i = 0; i < (iDate * -1); i++) {
					gc.roll( Calendar.DATE, false);
				}
			}
			return sdfOut.format( gc.getTime() );
		}
	}
		
	public static String genRFC822Date(String date ) {
		return genRFC822Date( date, "0");
	}
        
        
        
	public static String dueIn(String d){
		return dueIn(d, "3"); // only remind for up to 3 days before due.
	}
	
	public static String fixName(String s){
		/* for use in xsl stylesheet, since doing this in pure xsl would be a pain. 
			transforms DURFEE, CASEY to Casey Durfee
			transforms KING, MARTIN L to Martin L. King
		*/
		StringBuffer ret = new StringBuffer("");
		String[] names = s.trim().split("[ ,]");
		ArrayList al = new ArrayList();
		// there's probably a more clever regular expression that would allow you to avoid this annoyance
		for(int i = 0; i < names.length; i++){
			if(names[i].length() > 0){
				al.add( names[i]);
			}
		}
		names = (String[]) al.toArray(new String[ al.size() ] );
		if(names.length == 2){
			String lName = names[0].trim();
			String fName = names[1].trim();
			if(fName.length() != 0){
				ret.append( fName.substring(0,1).toUpperCase() );
				if(fName.length() > 1){
					ret.append(fName.substring(1).toLowerCase() );
				}
			}
			if(lName.length() != 0){
				ret.append( " " + lName.substring(0,1).toUpperCase() );
				if(lName.length() > 1){
					ret.append( lName.substring(1).toLowerCase() );
				}
			}
		} else if(names.length == 3){
			String lName = names[0].trim();
			String fName = names[1].trim();
			String mName = names[2].trim();
			if(fName.length() != 0){
				ret.append( fName.substring(0,1).toUpperCase() );
				if(fName.length() > 1){
					ret.append(fName.substring(1).toLowerCase() );
				}
			}
			if(mName.length() != 0){
				ret.append( " " + mName.substring(0,1).toUpperCase() );
				if(mName.length() > 1){
					ret.append(mName.substring(1).toLowerCase() );
				} else if(mName.indexOf(".") == -1){ // only want to do if 1 letter
					ret.append(".");
				}
			}
			if(lName.length() != 0){
				ret.append( " " + lName.substring(0,1).toUpperCase() );
				if(lName.length() > 1){
					ret.append( lName.substring(1).toLowerCase() );
				}
			}
		} else {
			ret.append(s);
		}
		return ret.toString().trim();
	}
}
