/*

Handles decoding HTTP BASIC authentication
 
by Casey Durfee <casey.durfee@spl.org>
version 0.5 (Sep. 19, 2005)


THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/
package org.spl.utils;

import java.util.StringTokenizer;
import sun.misc.BASE64Decoder;
import org.spl.RSSConf;

public class BasicAuthHandler{
    private static BASE64Decoder decoder;
    
    public static String decode(String auth) {
        String password = "";
        if(auth != null) {
            StringTokenizer st = new StringTokenizer(auth);
            if(st.hasMoreTokens()) {
                String tokenOn = st.nextToken();
                if(st.hasMoreTokens() && tokenOn.equalsIgnoreCase("basic")) {
                    String decoded = decodeBase64( st.nextToken() );
                    String userPass[] = decoded.split(":");
                    if(userPass.length == 2) {
                        password = userPass[1];
                    }
                }
            }
        }
        return password.trim();
    }
    
    public static String decodeBase64(String toDecode ) {
        String ret = "";
        if( decoder == null ){
            decoder =  new sun.misc.BASE64Decoder();
        }
        try {
            byte[] dec = decoder.decodeBuffer(toDecode);
            ret = new String( dec );
        } catch( Exception ioe ) {
            RSSConf.getLogger().error("IO Exception while handling basic auth. header decryption!");
            ioe.printStackTrace();
        }
        return ret;
    }
    
}