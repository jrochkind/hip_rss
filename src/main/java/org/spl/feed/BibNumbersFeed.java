/*
Servlet to provide RSS feeds from Horizon Information Portal OPACs.
by Casey Durfee <casey.durfee@spl.org>
version 0.5 (Sep. 19, 2005)

The bib numbers feed takes a list of bib numbers and returns a feed containing those bibs alone.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package org.spl.feed;

import javax.servlet.http.HttpServletRequest;

import org.spl.RSSAdminHandler;
import org.spl.RSSConf;
import org.spl.RSSServlet;

public class BibNumbersFeed extends GenericRSSFeed {
	String feedType = "bibNumbers";
	
	public BibNumbersFeed( HttpServletRequest req )  {
		super( req );
	}
	
	public String determineQuery(){
		String keys = req.getParameter("keys");
                // need to convert these bib keys to all_title (or whatever browse table we're using)
		// keys to be able to use the selectedkeys= link trick
		StringBuffer URLToGet = new StringBuffer("selectedkeys=");              
                if( keys != null ) {
                    RSSAdminHandler adminHandler = RSSServlet.getRSSAdminHandler();
                   String[] saKeys = keys.split(",");
                   for(int i = 0; i < saKeys.length; i++ ) {
                       if( i > 0 )  {
                            URLToGet.append("%2B");
                       }
                       URLToGet.append( adminHandler.bibNumToIndexNum(saKeys[i]) );
                   }
                   URLToGet.append( "&uri=" + RSSConf.getBibNumbersURI() );            
                }              
		return URLToGet.toString();
	}	
	public String getFeedType() {
            return "bibnumbers";
        }
}
