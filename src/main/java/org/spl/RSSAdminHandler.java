/*

Handles administrative (non-RSS feed) functions that this servlet provides ( encrypting/decrypting tokens, 
 clearing cache, etc. )  Also handles getting the last modified date of an item, which IS used by the RSS stylesheets
 to provide an accurate publication date for stories...

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
import org.spl.utils.RSSAdminUtils;
import org.spl.utils.RSSDBUtils;

public class RSSAdminHandler {
    RSSAdminUtils adminUtils;
    RSSDBUtils dbUtils;
    Logger log;
    
    public RSSAdminHandler() {
        this.log = RSSConf.getLogger();
        adminUtils = new RSSAdminUtils();
        dbUtils = new RSSDBUtils();
    }
    
    public int bibNumToIndexNum( String bibNum ) {
        return dbUtils.bibNumToIndexNum( bibNum );
    }
    
    
    public String handle( HttpServletRequest req ) {
        // handles HTTP requests for admin services
        String ret = "";
        String service = req.getParameter("service");
        
       // if( req.getLocalAddr().equals("localhost") || req.getLocalAddr().equals("127.0.0.1") ) {
       // JHU customization because 'localhost' is not what the server is seeing 
       if( req.getRemoteAddr().equals("127.0.0.1")) {
       //these services are only available from localhost; not meant to be used remotely.
            if(service.equals("decrypt-arbitrary-token") ) {
                // decrypt/encrypt service is only available from localhost TODO: make sure this is right
                String token = req.getParameter("token");
                if( token != null ){            
                   ret = adminUtils.decrypt( token );
                }
            } else if( service.equals("encrypt-arbitrary-token") || service.equals("encrypt") ) {
                // encrypts the token directly as passed in.
                String token = req.getParameter("token");
                if( token != null ){
                    ret = adminUtils.encrypt( token );
                }
            } else if( service.equals("encrypt-borrnum") ) {
                // the "encrypt-borrnum" service treats the token as a horizon patron # and gets the barcode and PIN and encrypts that.
                String token = req.getParameter("token");
                if( token != null ){
                    String toEncrypt = dbUtils.getBarcodeAndPIN( token );
                    ret = adminUtils.encrypt( toEncrypt );
                }   
            }   
       }   
        // the following are non-secured services
        if( service.equals("lastmodified") ) {
            String bib = req.getParameter("bib");
            String format = req.getParameter("format");
            if( format == null ){
                format = "RFC822";
            }
            ret = dbUtils.getLastModifiedDate( bib, format );   
        } 
       return ret; 
    }
    
    
}