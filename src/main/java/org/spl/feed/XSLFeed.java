/*

Feed generated from the contents of an XSL file.  It basically does a dummy transformation on the file so you
 *can easily incorporate contents from other XML files or call external functions in the XSL way.
 * A FileFeed does not allow this because it never gets XSL transformed.
 * If this seems like a crazy thing to do, look at opensearchdescriptionfeed.xsl to get the idea
 
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

public class XSLFeed extends GenericRSSFeed {
	String feedType = "xsl";
	
	public XSLFeed( HttpServletRequest req) {
		super( req );
                
	}
	
	public String getXML( String query ) {
		return "<dummy></dummy>";
	}
        
        
        public String getFeedType() {
            return feedType;
        }
        
        public String determineQuery() {
            return "dummy";
        }
        
}
