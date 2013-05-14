/*
Contains utilities that are dependent upon data from Horizon.
 
by Casey Durfee <casey.durfee@spl.org>
version 0.5 (Sep. 19, 2005)


THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package org.spl.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.spl.RSSConf;
import org.spl.RSSDBConnection;

public class RSSDBUtils {
    RSSDBConnection rssdb;
    private static LinkedHashMap indexNumCache;
    private int maxCacheSize;
    public final int BAD_INDEX_NUM = -1;
    
    public RSSDBUtils() {
        this.rssdb = RSSConf.getRSSDBConnection();
        maxCacheSize = RSSConf.getMaxIndexCacheSize();
        
        indexNumCache = new LinkedHashMap(){
			protected boolean removeEldestEntry(Map.Entry eldest){
				return size() > maxCacheSize;
			}
		};
    }
    
    public String getLastModifiedDate(String bibNum) {
		return getLastModifiedDate( bibNum, "RFC822");
    }

    public int bibNumToIndexNum(String bibNum ) {
        // gets the ID from a browse table corresponding with a particular bib number.
        // you have to do this to use hip 3.0's (undocumented) ability to create an arbitrary list of bibs --
        // it needs to use the id# of the row corresponding to the bib in some table.
        
        int indexNum = BAD_INDEX_NUM; 
        String indexTableToUse = RSSConf.getIndexTableToUse();
        Connection conn = null; 
        Statement stmt = null;
        ResultSet rs = null;
       
        if( indexNumCache.containsKey(bibNum) ){
            return Integer.parseInt( (String) indexNumCache.get( bibNum ));
        }
        
        String idQuery = "select id# from " + indexTableToUse + " where bib#=" + bibNum;
        String titleFilterClause = RSSConf.getTitleFilterClause();
        if( titleFilterClause.length() > 0 ) {
            idQuery += " " + titleFilterClause;
        }
        
        try {
            conn = rssdb.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery( idQuery );
            if( rs.next() ) {
                indexNum = rs.getInt(1);
            }
        } catch (Exception e ){
            e.printStackTrace();
        } finally {
             if (rs != null) {
                  try { rs.close(); } catch (SQLException e) { ; }
                  rs = null;
                }
                if (stmt != null) {
                  try { stmt.close(); } catch (SQLException e) { ; }
                  stmt = null;
                }
                if (conn != null) {
                  try { conn.close(); } catch (SQLException e) { ; }
                  conn = null;
                }
        }
	
        // in some circumstances (I.E. forbidden title) we would want to cache bad values.
	// so we cache here even if response may be bogus.
            String sIndexNum = "" + indexNum;
            indexNumCache.put( bibNum, sIndexNum );
        return indexNum;
    }
    
    public int bibNumToIndexNum( int bibNum ) {
        return bibNumToIndexNum( "" + bibNum );
    }
    
    public String getRecentlyCreatedBibs( String maxBibs ) {
        return getRecentlyCreatedBibs( maxBibs, true);
    }
    
    
    public String getRecentlyCreatedBibs( String maxBibs, boolean asIndexNums ) {
        // returns the last n recently created bibs as a comma delimited string.
        // If asIndexNums is true, actually return the idNumbers to create an arbitrary list rather than bib nums.
        if( maxBibs.length() == 0) {
            maxBibs = "1"; 
        }
        String titleFilterClause = RSSConf.getTitleFilterClause();
        String recentlyCreatedQuery = "";
        if( titleFilterClause.length() > 0 && RSSConf.getIndexTableToUse().length() > 0 ) {
            /* NB. "select distinct top ...." is unbelievably slow with Sybase over JDBC.  It appears to scan the whole
             * table (though it does not do that in SQL Advantage).  
             * for that reason, we check for non-distinctness further down after we've gotten the keys.  That may 
             * result in doing a top 20 search and only getting 18 results in your feed...
             */
            recentlyCreatedQuery = "select top " + maxBibs + " bc.bib# from bib_control bc, " + RSSConf.getIndexTableToUse() +
                                            " allti where bc.bib# = allti.bib# " + titleFilterClause + " order by bc.create_date desc";
            // this filters out stuff using the titleFilterClause comparing against the indexTable.  For instance 
            // we do not want this feed to include any titles that start with "ILLM" because they are ILL items.
	    
        } else {
            recentlyCreatedQuery = "select top " + maxBibs + " bib# from bib_control order by create_date desc";
        }
	RSSConf.getLogger().debug("getting recently created with query " + recentlyCreatedQuery );
        Connection conn = null; 
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList alRet = new ArrayList();
        int numFound = 0;
        try {
            conn = rssdb.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery( recentlyCreatedQuery );
            int bibNumOn = BAD_INDEX_NUM;
            while( rs.next() ) {
                numFound++;
                bibNumOn = rs.getInt(1);
                if( asIndexNums == true ){
                    bibNumOn = bibNumToIndexNum( bibNumOn );
                }  
                if( bibNumOn != BAD_INDEX_NUM ) { 
                    if( !alRet.contains( "" + bibNumOn) ) {
                        // this filters out duplicates -- see above note.
                        alRet.add( "" + bibNumOn );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
                if (rs != null) {
                  try { rs.close(); } catch (SQLException e) { ; }
                  rs = null;
                }
                if (stmt != null) {
                  try { stmt.close(); } catch (SQLException e) { ; }
                  stmt = null;
                }
                if (conn != null) {
                  try { conn.close(); } catch (SQLException e) { ; }
                  conn = null;
                }		
        }
        StringBuffer sbRet = new StringBuffer();
        for(int i = 0; i < alRet.size(); i++) {
            if(i > 0) {
                sbRet.append(",");
            }
            sbRet.append( alRet.get(i) );
        }
        return sbRet.toString();
    }
       
    
    public String getLastModifiedDate( String bibNum, String format ) {
        // gets the date a bib was last modified from the bib_control table and 
        // returns it in the format specified.
        
        int createDate = 0;
        int changeDate = 0;
        Connection conn = null; 
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = rssdb.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select create_date, change_date from bib_control where bib# = " + bibNum );
            if( rs.next() ){
                createDate = rs.getInt(1);
                changeDate = rs.getInt(2);
            } else {
                    // do nothing for now
            }
            stmt.close();
            stmt = null;
            conn.close();
            conn = null;   
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
                if (rs != null) {
                  try { rs.close(); } catch (SQLException e) { ; }
                  rs = null;
                }
                if (stmt != null) {
                  try { stmt.close(); } catch (SQLException e) { ; }
                  stmt = null;
                }
                if (conn != null) {
                  try { conn.close(); } catch (SQLException e) { ; }
                  conn = null;
                }		
        }
        if( changeDate > 0 && changeDate > createDate) {
                createDate = changeDate;
        }

        // now convert to desired format....
        long lCreateDate = (long) createDate;
        lCreateDate *= 0x5265c00L;
        java.util.Date theDate = new java.util.Date( lCreateDate );
        SimpleDateFormat sdf;
        if(format.equals("RFC822") ){	
                sdf = 	new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
        } else {
                sdf = 	new SimpleDateFormat("MM/dd/yyyy");
        }
        return sdf.format( theDate);        
    }
   
    public String getBarcodeAndPIN( int borrowerID ) {
        // returns the barcode and PIN for a borrower ID#.
   	Connection conn = null;
        Statement pstmt = null;
        ResultSet rs = null;
        String barcode = "";
        String pin = "";

        try{
                conn = rssdb.getConnection(); 
                pstmt = conn.createStatement();
                rs = pstmt.executeQuery("select bb.bbarcode, b.pin from borrower b, borrower_barcode bb where bb.borrower# = b.borrower# and lost_date is NULL and b.borrower# = " + borrowerID);
                if(rs.next()){
                        barcode = rs.getString(1);
                        pin = rs.getString(2);
                } 		
        } catch(Exception e){
                e.printStackTrace();
        } finally {
                    if (rs != null) {
                      try { rs.close(); } catch (SQLException e) { ; }
                      rs = null;
                    }
                    if (pstmt != null) {
                      try { pstmt.close(); } catch (SQLException e) { ; }
                      pstmt = null;
                    }
                    if (conn != null) {
                      try { conn.close(); } catch (SQLException e) { ; }
                      conn = null;
                    }		
        }
	return barcode + "~~" + pin;
    }    
    
    public String getBarcodeAndPIN( String sBorrowerID ) {
        return getBarcodeAndPIN( Integer.parseInt( sBorrowerID) );
    }
    
          
}