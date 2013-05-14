/*
Feed which is generated when there is some error handling the request.

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

public class ErrorFeed extends GenericRSSFeed   {
	String feedType = "file";
	String errorMessage;
        
	public ErrorFeed( HttpServletRequest req ) {
		super( req );	
		errorMessage = "unspecified error";
	}
	
        public ErrorFeed( HttpServletRequest req, RSSException rsse ) {
            super( req );
            errorMessage = rsse.getMessage();
            
        }
        
	public String getXML() {
                if( errorMessage == null ) {
                    log.warn(" Unable to determine feed corresponding to type " + feedType +  ", query " + query);
                    return RSSConf.getNoFeedFound();
                } else {
                    log.error(" Problem with feed: " + errorMessage) ;
                    // TODO: make this more sophisticated/not just get the generic "no feed found" error.
                    return RSSConf.getNoFeedFound();
                }
	}
	
        public String getFeedType() {
            return "error";
        }
	
}
