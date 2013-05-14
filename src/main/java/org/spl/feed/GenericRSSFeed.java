/*

 * This class provides some sensible implementations that will be almost
 * what you want most of the time when creating a new feed type, but is 
 * itself abstract.

by Casey Durfee <casey.durfee@spl.org>
version 0.5 (Sep. 19, 2005)


THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/


package org.spl.feed;

import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.spl.RSSConf;
import org.spl.utils.Utils;

import com.opensymphony.oscache.base.NeedsRefreshException;

public abstract class GenericRSSFeed implements RSSFeed {
	
	
	String xml;
	String stylesheetName;
	String feedType = "generic";
	HttpServletRequest req;
	Logger log;
        String query;
	String style;
        
	public GenericRSSFeed( HttpServletRequest request)  {
                this.req = request;
		this.log = RSSConf.getLogger();
        }
        
        public void loadData() {
            /* template method to outline general algorithm for getting the feed data and then
             * caching it.  These can't be in the constructor because we need to make sure they
             * get called with the subclassed versions,not the superclass.
             */
		// determine query and stylesheet.
		query = determineQuery();
                if( query != null) {
                    style = req.getParameter("style");
                    if ( style != null && canUseStyle( style ) ) {
                            stylesheetName = RSSConf.getStylesheetName(style);
                    } else {
                            String type = req.getParameter("type");
                            if( type == null) {
                                type = getFeedType();
                            }
                            stylesheetName = RSSConf.getDefaultStyle( type );
                            log.debug("using stylesheet name: " + stylesheetName ); 
                    }

                    // now try and get XML from cache, if it's allowed
                    if ( canCache() ) {
                            try {
                                    xml = (String) RSSConf.getCache().getFromCache( query, getTimeToLive() );
                            } catch (NeedsRefreshException nre) {
				    log.debug(" Query " + query + " expired, fetching..." ) ;
                                    xml = getXML(query);
                                    RSSConf.getCache().putInCache( query, xml, new String[] { getFeedType() } );
                            }
                    } else {
                            xml = getXML(query);
                    }
                } else {
                    xml = RSSConf.getProblemFeed( getFeedType() );
                    log.debug(" Query was null for :" + req.getRequestURL().toString() + "?" + req.getQueryString() );
                }
	}
	
	public String transform() {
		return transform( this.stylesheetName );
	}
	
	public String transform( String stylesheet ) {
		String transformed = "";
		try {
            log.debug("\n\nXML: " + xml + "\n\n\n\n");
            StringBuffer xmlLogMsg = new StringBuffer();
            if ( xml != null ) {
            	if ( xml.length() >= 40 ) {
            		xmlLogMsg.append( "First 20 characters of 'xml': [" + xml.substring(0, 20) + "], " );
            		xmlLogMsg.append( "Last 20 characters of 'xml': [" + xml.substring(xml.length() - 20, xml.length()) + "] " );
            		xmlLogMsg.append( "(prior to transformation)" );
            	} else if ( xml.length() > 0 ) {
            		xmlLogMsg.append( "'xml' content: [" + xml + "]" );
            	} else if ( xml.length() == 0 ) {
            		xmlLogMsg.append( "No XML to transform: 'xml' is the empty string" );
            	}
            } else {
            	xmlLogMsg.append( "No XML to transform: 'xml' is null" );
            }
            log.info( xmlLogMsg.toString() );
            log.info( "Stylesheet located at " + stylesheet );
			transformed = Utils.simpleTransform( xml, stylesheet );
		} catch (Exception e) {
                        log.fatal("Problem transforming with stylesheet " + stylesheet + ":");
			e.printStackTrace();
			transformed = RSSConf.getProblemFeed( getFeedType() );
		}
		return transformed;
	}
	
	public boolean canCache() {
		/* should be overridden if this type of feed should not
		 *   be cached.
		 */
		return true;
	}
	
	public boolean canUseStyle(String stylesheetName) {
		/* should be overridden if there are requirements about
		 what stylesheets can be used. 	*/
		return true;
	}
	
	public int getTimeToLive() {
		return RSSConf.getTimeToLive( getFeedType() );
	}
	
	public String getFeedType() {
		return "generic";
	}
	
	public void setFeedType(String ft) {
		this.feedType = ft;
	}
	
	public String determineQuery() {
		// you will probably want to override this.
		return req.getParameter("term");	
	}
	
	public String getXML(String query) {
		// you will probably want to overrride this.
		log.debug("Query " + query + " not in cache, fetching it"); 
		String ret = "";
                // need to append the base URL for this query!!!
                query = RSSConf.getBaseURL() + "?" + query;
                if ( query.indexOf("GetXML") == -1 ) {
                    query = query.trim() + "&GetXML=true";
                }
		try {
			ret = Utils.getURL( query );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	public String getXML() {
		if (xml == null ) {
			return "";
		} else {
			return xml;
		}
	}	
}
