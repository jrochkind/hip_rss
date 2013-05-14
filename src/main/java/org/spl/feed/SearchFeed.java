/*

 * Produces an RSS feed from an arbitrary search query, checking to make sure all search params are OK.
 *
by Casey Durfee <casey.durfee@spl.org>
version 0.5 (Sep. 19, 2005)


THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/


package org.spl.feed;

import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.spl.RSSConf;

public class SearchFeed extends GenericRSSFeed {
	String feedType = "search";
	
	public SearchFeed( HttpServletRequest req) {
		super( req );
	}
	
	public String determineQuery(){
		/* sort to make sure that two identical URLS with the arguments in different orders 
		don't get cached twice.  
		NB: This may have the unfortunate side effect that multi-index searches
		do not work as expected.  eg. GW on dogs + name on cats -> general word on cats + name word on dogs.
		*/
		ArrayList attribs = Collections.list(req.getParameterNames());
		Collections.sort( attribs );
		StringBuffer URLToGet = new StringBuffer("");
		String elementOn;
		String paramOn;
		int numIndexes=0;
		int numSorts=0;
		for(int i = 0; i < attribs.size(); i++) {
			elementOn = (String) attribs.get(i);
			paramOn = req.getParameter(elementOn);
			if( !RSSConf.isForbidden( elementOn ) ){
				if( elementOn.equals("index")){
					numIndexes++;
					if (paramOn == null) {
						paramOn = RSSConf.getDefaultIndex();
					}	
				} else if( elementOn.equals("npp") ) {
					if(paramOn == null) {
						paramOn = RSSConf.getDefaultNpp();
					} else if ( Integer.parseInt(paramOn) > RSSConf.getMaxItemsInFeed() ) {
						paramOn = "" + RSSConf.getMaxItemsInFeed();
					}
				} else if( elementOn.equals("sort") ) {
					numSorts++;
					if( paramOn == null ) {
						paramOn = RSSConf.getDefaultSort();
					}
				} else if ( elementOn.equals("limit") && paramOn == null ) {
					paramOn = RSSConf.getDefaultLimit();
				} else if ( elementOn.equals("term") && paramOn.indexOf("=") > 1 ) {
					//hey, might as well fix the e=mc2 problem that HIP has
					paramOn = paramOn.replace('=', ' ');
				}
				URLToGet.append( "&" + elementOn + "=" + paramOn );
			} else {
				log.debug("parameter " + elementOn + " is forbidden, removing..." );
			}
		}
		if(numIndexes==0){ 
			URLToGet.append("&index=" + RSSConf.getDefaultIndex() ) ;
		}
		if(numSorts==0) { 
			URLToGet.append("&sort=" + RSSConf.getDefaultSort() );
		}
		return URLToGet.toString();
	}	
	public String getFeedType() {
            return "search";
        }
}
