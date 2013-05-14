/*
Database connection wrapper
 *
by Casey Durfee <casey.durfee@spl.org>
version 0.5 (Sep. 19, 2005)


THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/


package org.spl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;

public class RSSDBConnection {
    private static String dsn;
    private static Context ctx;
    private static String prefix;
    private static DataSource ds;
    
    public RSSDBConnection( String datasourceName, String providerURL, String JNDIPrefix) {
        try {
            dsn = datasourceName;
            ctx = getInitialContext( providerURL );
            prefix = JNDIPrefix;
            ds = (DataSource)PortableRemoteObject.narrow(ctx.lookup(prefix + dsn), javax.sql.DataSource.class);
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
    
    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
    
    private static Context getInitialContext(String providerURL)
            throws NamingException{
                    Properties p = new Properties();
                    p.put( Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory" );
                    p.put( Context.PROVIDER_URL, providerURL );
                    return new InitialContext( p );
        }
    
    
}
    