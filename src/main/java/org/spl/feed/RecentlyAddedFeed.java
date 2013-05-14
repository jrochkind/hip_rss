/*
by Casey Durfee <casey.durfee@spl.org>
version 0.5 (Sep. 19, 2005)
 
 * this feed provides the last n bibs cataloged in the system.  
 * Problem is that it includes bibs with no items attached such as 
 * fast-add records.  
 * there is no simple way to filter those out.  it's on the todo list.


THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


*/


package org.spl.feed;

import javax.servlet.http.HttpServletRequest;

import org.spl.RSSConf;
import org.spl.RSSServlet;
import org.spl.utils.RSSDBUtils;

public class RecentlyAddedFeed extends GenericRSSFeed {
	String feedType = "recentlyadded";
        RSSDBUtils dbUtils;
	
	public RecentlyAddedFeed( HttpServletRequest req ) {
		super( req );
                dbUtils = RSSServlet.getRSSDBUtils();
	}
	
	public String determineQuery(){
                //String ret = "";
		String numItems = req.getParameter("num");
                if( numItems == null ) {
                    numItems = "20";
                }
                int iNumItems = Integer.parseInt( numItems ); 
                if( iNumItems > RSSConf.getMaxItemsInFeed() ) {
                    iNumItems = RSSConf.getMaxItemsInFeed();
                }
                //System.err.println("iNumItems = " + iNumItems );
                
                // the keys need to be web escaped or you get an error message.
                String selectedkeys = dbUtils.getRecentlyCreatedBibs( "" + iNumItems ).replaceAll(",", "%2B"); 
                //System.err.println( "selectedkeys => " + selectedkeys ); //csdebug
                String uri = RSSConf.getBibNumbersURI();
                
                String ret = "selectedkeys=" + selectedkeys + "&uri=" + uri + "&npp=" + iNumItems; 
                return ret;
	}	
	public String getFeedType() {
            return "recentlyadded";
        }
}
