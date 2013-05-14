/*

 * Generates feed of a patron's items out.
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

import org.spl.RSSConf;
import org.spl.utils.BasicAuthHandler;
import org.spl.utils.Utils;
import org.spl.utils.WebsafeDESCrypto;

public class HoldsFeed extends GenericRSSFeed  {
	String barcode;
	String PIN;
	String feedType = "holds";
	
	public HoldsFeed( HttpServletRequest req ) throws RSSException {
		super( req );
                String id = req.getParameter("id");
                if( id == null  ){
                        
                        String authType = req.getAuthType();
                        if( authType == null ){
                                
                            if (RSSConf.getAllowDirectBarcodePIN() ){
                                    barcode = req.getParameter("barcode");
                                    PIN = req.getParameter("pin");
                            }
                        } else if( authType.equals("BASIC") ) {
                            // following is to handle HTTP BASIC authentication
                            // we have to do this because the patron is not authenticating against OUR webapp, they are authenticating
                            // against the horizon database.  So we have to deduce the password & PIN that they supplied to get here.
                            String password = BasicAuthHandler.decode( req.getHeader("Authorization") );
                            if (password == null){
                                PIN = "";
                            } else {
                                PIN = password;
                            }
                            barcode = req.getRemoteUser();
                        } 
                        
                } else {
                    
                    WebsafeDESCrypto handler = WebsafeDESCrypto.getInstance( RSSConf.getEncryptionPhrase() );
                    String decrypted = handler.decrypt( id );
                    String[] saDecrypted = decrypted.split("~~");
                    // NB if there is a problem, decrypt() will return "~~", so there will be no NPE here.
                    try {
                        barcode = saDecrypted[0];
                        PIN = saDecrypted[1];    
                    } catch( ArrayIndexOutOfBoundsException aiee){
                        throw new RSSException("Barcode or PIN undefined");
                    }
                }
                if( barcode.length() == 0 || PIN.length() == 0 ) {
                    throw new RSSException("Barcode or PIN undefined");
                }
                
	}
	
	public String determineQuery() {
		if( barcode == null || PIN == null) {
			log.warn("Problem doing holds feed for barcode " + barcode + ", PIN " + PIN);
			//return RSSConf.getProblemFeed( "holds" );
		}
		String query = "type=holds&barcode=" + barcode + "&pin=" + PIN;
		return query;
	}
	
	public String getXML(String query) {
		String retVal = "";
		log.debug("Query " + query + " not in cache, fetching it"); 
		String holdsURL = RSSConf.getHoldsURL();
		String sessionID = Utils.getSessionID( Utils.getURL( holdsURL ) );
		String urlToGet = holdsURL + "&session=" + sessionID + "&sortby=dateexpires&ready_sortby=dateexpires&GetXML=true&sec1=" + barcode + "&sec2=" + PIN + "&agent=Co-Meta-RSS";
		String raw = "";
                try {
			raw = Utils.getURL(urlToGet);
			if (RSSConf.getDoTwice() ){
				/*there's a bug with Dynix's code that makes it so you need to actually get the URL twice
				for it to work.  the first time it just gives you it in HTML, not XML. */
				raw = Utils.getURL(urlToGet);
			}
			// check to see if problem... if <prompt> is in the XML, that means it wasn't successfully authenticated.
			if( raw.indexOf("<prompt>") > -1){
				log.warn("Invalid barcode or PIN for barcode " + barcode);
				retVal = RSSConf.getProblemFeed( "holds" );
			} else {
				retVal = raw;
			}
		} catch (Exception e) {
			e.printStackTrace();
			retVal = RSSConf.getProblemFeed( "holds" );
		}
		return retVal;
	}
        public String getFeedType() {
            return "holds";
        }
}

