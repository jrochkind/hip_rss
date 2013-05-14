/* Singleton class to handle encrypted HIP barcodes for rss usage.
* lifted, with some modifications (to make websafe tokens), from http://javaalmanac.com/egs/javax.crypto/PassKey.html

 by Casey Durfee <casey.durfee@spl.org>
version 0.5 (Sep. 19, 2005)


THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package org.spl.utils;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class WebsafeDESCrypto {
	String privateKey;
	Cipher encryptCipher;
	Cipher decryptCipher;
	KeySpec spec;
	SecretKey key;
	BASE64Encoder encoder;
	BASE64Decoder decoder;
        private static WebsafeDESCrypto uniqueInstance;
	
	byte[] salt = {
		(byte) 0xA6, (byte) 0x9B, (byte) 0xD3, (byte) 0x38, (byte) 0x56, (byte) 0x72, (byte) 0xE3, (byte) 0x03
	};
	int iterCount = 43;
	
        
        public static synchronized WebsafeDESCrypto getInstance( String inKey ){
            // NB because it is a singleton, you can't re-define the encryption key after it has been instantiated.
            if( uniqueInstance == null ) {
                uniqueInstance = new WebsafeDESCrypto( inKey );
            } 
            return uniqueInstance;            
        }
        
	
	private WebsafeDESCrypto(String inKey) {
		this.privateKey = inKey;
		encoder = new sun.misc.BASE64Encoder();
		decoder = new sun.misc.BASE64Decoder();
		try { 
			spec = new PBEKeySpec( privateKey.toCharArray(), salt, iterCount);
			key = SecretKeyFactory.getInstance("PBEWithMD5AndTripleDES").generateSecret( spec );	
			encryptCipher = Cipher.getInstance( key.getAlgorithm() );
			decryptCipher = Cipher.getInstance( key.getAlgorithm() );
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterCount);
    	            	encryptCipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			decryptCipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String encrypt( String str) {
		try {
			// Encode the string into bytes using utf-8
			byte[] utf8 = str.getBytes("UTF8");
			byte[] enc = encryptCipher.doFinal(utf8);
                	String ret = encoder.encode(enc).replaceAll("/", "!").replaceAll("=", "~").replaceAll("\\+", "-"); // to make web-friendly
			return ret;
            } catch ( Exception e ) {
		    e.printStackTrace();
	    }
	    return null;
	}

	public String decrypt(String str) {
            try {
		String toDecode = str.replaceAll("!", "/").replaceAll("~", "=").replaceAll("-", "\\+"); // to translate back
                byte[] dec = decoder.decodeBuffer(toDecode);
                
                byte[] utf8 = decryptCipher.doFinal(dec);
                
                return new String(utf8, "UTF8");
	    } catch (BadPaddingException bpe ) {
              // this appears to totally hose the decoder when this happens -- it will incorrectly decode the next request
              synchronized( uniqueInstance ) {
                this.uniqueInstance = new WebsafeDESCrypto( this.privateKey );
              }
            } catch (IllegalBlockSizeException ibse ) {
              synchronized( uniqueInstance ) {
                this.uniqueInstance = new WebsafeDESCrypto( this.privateKey );
              }
            } catch (Exception e) {
		    e.printStackTrace();
	    }
	    return "";
	}
        
	//test wrapper
	/*public static void main(String args[]){
		org.spl.utils.RSSIDHandler rssHandler = new RSSIDHandler(args[0]);	
		String encrypted = rssHandler.encrypt(args[1]);
		System.out.println("encrypted: " + encrypted);
		String decrypted = rssHandler.decrypt( encrypted);
		System.out.println("decrypted: " + decrypted);
	} */
	
}
	