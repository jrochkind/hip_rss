/*

Factory Method that produces the appropriate RSSFeed() object based upon request parameters.

by Casey Durfee <casey.durfee@spl.org>
version 0.5 (Sep. 19, 2005)


THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package org.spl;

import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.spl.feed.BibNumbersFeed;
import org.spl.feed.ErrorFeed;
import org.spl.feed.FileFeed;
import org.spl.feed.HoldsFeed;
import org.spl.feed.ItemsOutFeed;
import org.spl.feed.RSSFeed;
import org.spl.feed.RecentlyAddedFeed;
import org.spl.feed.SearchFeed;
import org.spl.feed.StaticFeed;
import org.spl.feed.XSLFeed;


public class RSSFeedFactory {
	Logger log;
	
	public RSSFeedFactory() {
		this.log = RSSConf.getLogger();
	}
	
	public RSSFeed getFeed(HttpServletRequest req) {
		RSSFeed ret;
		String type = req.getParameter("type");
		String term = req.getParameter("term");
		 
		String reqURL = req.getRequestURL().toString() + "?" + req.getQueryString();
		String passwdTried = req.getParameter("passwd");
		if (type == null) {
			type = RSSConf.getDefaultFeedType();
		}
		log.debug("doing " + type + "feed for " + reqURL);
		// now decide which type of feed to return.
                try {
                    if (type.equals("search") || type.equals("passthrough") || type.equals("opensearch") ){
                            ret = new SearchFeed( req);
                    } else if( type.equals("itemsout") ){
                            ret = new ItemsOutFeed( req );
                    } else if ( type.equals("holds" ) ) {
                            ret = new HoldsFeed( req );
                    } else if( type.equals("static") || type.equals("predef") ) { // "static" is the legacy name for predefined feeds
                            ret = new StaticFeed( req );
                    } else if( type.equals("file") ) {
                            ret = new FileFeed( req );
                    }  else if( type.equals("bibnumbers") ) {
                        ret = new BibNumbersFeed( req );
                    } else if( type.equals("recentlyadded") ) {
                        ret = new RecentlyAddedFeed( req );
                    } else if ( type.equals("opensearchdescription") || type.equals("xsl") ) {
                        ret = new XSLFeed( req );
                    }
                    // default case: return error
                    else {
                            ret = new ErrorFeed( req );
                    }
                } catch( Exception rsse) {
                    rsse.printStackTrace();
                    ret = new ErrorFeed( req );
                }
		return ret;
	}
}
