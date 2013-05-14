/*
Servlet to provide RSS feeds from Horizon Information Portal OPACs.
by Casey Durfee <casey.durfee@spl.org>
version 0.5 (Sep. 19, 2005)


THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/
package org.spl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.spl.feed.RSSFeed;
import org.spl.utils.RSSAdminUtils;
import org.spl.utils.RSSDBUtils;

import com.opensymphony.oscache.general.GeneralCacheAdministrator;


public class RSSServlet extends HttpServlet {
	private static Logger log;
	private static GeneralCacheAdministrator memCache;
	private static RSSFeedFactory feedFactory;
        private static RSSDBUtils dbUtils;
        private static RSSAdminUtils adminUtils;
	private static RSSAdminHandler adminHandler;
        
        public static RSSAdminHandler getRSSAdminHandler() {
            return adminHandler;
        }
        
        public static RSSDBUtils getRSSDBUtils() {
            return dbUtils;
        }
        
        
	public void init(ServletConfig config) throws ServletException {
		String realPath = config.getServletContext().getRealPath(".");
		//rssp = RSSServletProperties.getInstance(realPath);
		RSSConf.init( realPath );
		log = RSSConf.getLogger();
                dbUtils = new RSSDBUtils();
                adminHandler = new RSSAdminHandler();
		feedFactory = new RSSFeedFactory();
		
		// TODO: put in bit here which will initialize static feeds
		// after we are sure that HIP is up.  Right now static feeds are lazily loaded,
                // which is not how I originally envisioned them working.
		log.warn("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		log.warn("  ~~~ SPL-RSS Servlet v0.5 initialized.~~~");
		log.warn("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}
	
	public void doGet( HttpServletRequest req, HttpServletResponse resp) 
	throws ServletException, IOException {
            String serviceType = req.getParameter("service");
            if(serviceType == null ) {
                serviceType = "feed";
            }
            
            if( serviceType.equals("feed") ) {
		RSSFeed feedOn = feedFactory.getFeed( req );
                log.debug( "Feed type is: " + feedOn.getFeedType() );
                feedOn.loadData();
		String output = feedOn.transform();
		
		/* getWriter ensurse that it gets outputted as Unicode data.
		   One should not use getOutputStream().
		*/
		// resp.setContentType("application/rss+xml");
		//     resp.setContentType("text/xml"); // this works OK in web browsers but not in some feed readers!
                resp.setContentType("application/xml; charset=UTF-8");


		PrintWriter pw = resp.getWriter();
		pw.write( output );
		pw.flush();
		pw.close();
            } else {
                // admin/internal functions that do not generate an RSS feed go here
                String output = adminHandler.handle( req );
                PrintWriter pw = resp.getWriter();
		pw.write( output );
		pw.flush();
		pw.close();
                
            }
	
	}
}
