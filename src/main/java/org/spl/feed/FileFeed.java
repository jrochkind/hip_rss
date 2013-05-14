/*

Feed generated from the contents of a file (basically just takes the file on your HIP server and spits it back)
 *if you submit rss?type=file&term=foo.xml, it goes and looks in rss.war\feed-files\foo.xml for contents.
 *Currently does not cache file contents.
 
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
import org.spl.utils.Utils;

public class FileFeed extends GenericRSSFeed {
	String feedType = "file";
	
	public FileFeed( HttpServletRequest req ) {
		super( req );	
	}
	
	public String getXML( String query ) {
		String retVal;
		String filePath = RSSConf.getFileName( query );
                String fileContents = "";
                try {
        		fileContents = Utils.getFile( filePath ).trim();
                } catch( Exception e ) {
                    log.error("Problem while fetching fileFeed for file " + filePath );
                    e.printStackTrace();
                }
		if ( fileContents.length() > 0 ) {
			retVal = fileContents;
		} else {
			retVal = RSSConf.getNoFeedFound();
		}
		return retVal;
	}
        
        public String transform() {
            // we don't do any transformation on files.
            return xml;
        }
        
        public String getFeedType() {
            return feedType;
        }	
}
