/*

by Casey Durfee <casey.durfee@spl.org>
version 0.5 (Sep. 19, 2005)

 * Feed which is built from a URL that you define in the feeds.properties file.  Basically all it is doing is
 * giving a friendly name to what might be a complicated search URL you want to hook people up with.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/



package org.spl.feed;

import javax.servlet.http.HttpServletRequest;

import org.spl.RSSConf;
import org.spl.utils.Utils;

public class StaticFeed extends GenericRSSFeed  {
	String feedType = "static";
	
	public StaticFeed( HttpServletRequest req) {
		super( req );	
	}
	
	public String getXML( String query ) {
		String retVal;
		String theURL = (String) RSSConf.getStaticFeedURL( query );
		String urlContents = Utils.getURL( theURL );
		if ( urlContents.length() > 0 ) {
			retVal = urlContents;
		} else {
			retVal = RSSConf.getProblemFeed( getFeedType() );
		}
		return retVal;
	}
        
        public String getFeedType() {
            return "static";
        }
}
