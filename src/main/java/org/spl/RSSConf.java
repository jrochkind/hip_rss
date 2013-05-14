/*

Main Registry of config settings for RSS servlet properties

This is slightly different from the usual registry/singleton pattern in that 
getInstance() does not lazily create the singleton object.  It is created by init(),
which must be called before you call any of the class methods.  Init() has to be there
to have a way to pass in the realPath, which only RSSServlet knows.

by Casey Durfee <casey.durfee@spl.org>
version 0.5 (Sep. 19, 2005)


THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package org.spl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import org.jboss.logging.Logger;

import com.opensymphony.oscache.general.GeneralCacheAdministrator;

public class RSSConf implements Serializable {
        private boolean allowDirectBarcodePIN;	
	private String baseURL;
        private String bibNumbersURI;
	private boolean bInitialized = false;
	private String cacheDir;
	private Properties cacheprops;
	private boolean canSpecifyStylesheet;
        private String datasourceName;
        private RSSDBConnection dbConnection;
	private String defaultFeedType;
	private String defaultIndex;
	private String defaultLimit;
	private String defaultNpp;
	private String defaultSort;
	private int defaultTimeToLive;
	private boolean doTwice;
	private String[] dynamicGroupName;
        private String encryptionPhrase;
	private Properties feedprops;
	private Properties fileprops;
	private String fileStylesheet;
	private String flushPasswd; 
	private String[] forbiddenURLParameters;
	private Properties generalprops;
	private String genericProblem = "<?xml version=\"1.0\" ?><!-- no feeds found by that name --><rss version=\"0.91\"></rss>";	
	private String holdsFeedStylesheet;
	private String holdsProblem = "<?xml version=\"1.0\" ?><!-- the barcode or PIN you specified was incorrect or the server is down. --><rss version=\"0.91\"></rss>";	
	private String holdsURL;
        private String indexTableToUse;
	private int itemsOutCacheTime;	
	private String itemsOutFeedStylesheet;
	private String[] itemsOutGroupName;
	private String itemsOutProblem = "<?xml version=\"1.0\" ?><!-- the barcode or PIN you specified was incorrect or the server is down. --><rss version=\"0.91\"></rss>";
	private String itemsOutURL;
        private Logger log;
        private int maxIndexCacheSize;
	private int maxItemsInFeed;
	private GeneralCacheAdministrator memCache;
	private String noFeedFound = "<?xml version=\"1.0\" ?><!-- no feeds found by that name --><rss version=\"0.91\"></rss>";
	private String openSearchStylesheet;
	private String openSearchDescriptionStylesheet;
        private String prefix;
        private String providerURL;
	private String realPath;
  	private String searchStylesheet;
	private String sec3Password;
	private Enumeration staticFeedNames; 
	private String[] staticGroupName;
	private String staticStylesheet;
	private Properties stylesheetprops;

        private String titleFilterClause;

	
	private static RSSConf uniqueInstance; 
	
	public static void init( String realPath ) {
		uniqueInstance = new RSSConf( realPath );	
	}
	
	public static RSSConf getInstance(){	
		return uniqueInstance;
	}
		
	private RSSConf(String realPath){	
		log = Logger.getLogger("RSSServlet");
		this.realPath = realPath;
		cacheDir = realPath + "/feed-cache/";	
		feedprops = new Properties();
                FileInputStream fis;
                
		try {
                        fis = new FileInputStream( realPath + "/feed-cache/feeds.properties");
			feedprops.load( fis  );
                        fis.close();
		} catch (IOException e) { 
			System.err.println("Unable to load feeds.properties from path " + realPath + "/feed-cache/");
		}
		
		generalprops = new Properties();
		try {
                        fis  = new FileInputStream(realPath + "/feed-cache/general.properties");
			generalprops.load( fis );
                        fis.close();
		} catch (IOException e) { 
			System.err.println("Unable to load general.properties from path " + realPath + "/feed-cache/");
		}
		
		fileprops = new Properties();
		try {
			fileprops.load( new FileInputStream(realPath + "/feed-cache/file.properties") );
		} catch (IOException e) {
			System.err.println("Unable to load file.properties from path " + realPath + "/feed-cache/");
		}
		
		stylesheetprops = new Properties();
		try {
                        fis = new FileInputStream(realPath + "/feed-cache/stylesheet.properties");
			stylesheetprops.load( fis );
                        fis.close();
		} catch (IOException e) {
			System.err.println("Unable to load stylesheet.properties from path " + realPath + "/feed-cache/");
		}
		
		cacheprops = new Properties();
		try {
                        fis = new FileInputStream(realPath + "/feed-cache/oscache.properties");
			cacheprops.load( fis );
                        fis.close();
		} catch (IOException e) {
			System.err.println("Unable to load oscache.properties from path " + realPath + "/feed-cache/");
		}		

		itemsOutURL = generalprops.getProperty("itemsout.URL", "http://localhost/ipac20/ipac.jsp?profile=&menu=account&submenu=itemsout");
		holdsURL = generalprops.getProperty("holds.URL", "http://localhost/ipac20/ipac.jsp?profile=&menu=account&submenu=holds");

		defaultTimeToLive = Integer.parseInt( generalprops.getProperty("TTL", "360") );
		defaultIndex = generalprops.getProperty("DefaultIndex", ".GW");
		defaultNpp = generalprops.getProperty("DefaultNpp", "10");
		defaultSort = generalprops.getProperty("DefaultSort", "");
		defaultLimit = generalprops.getProperty("DefaultLimit", "");
		baseURL = generalprops.getProperty("BaseURL", "http://localhost:80/ipac20/ipac.jsp");
		itemsOutCacheTime = Integer.parseInt(generalprops.getProperty("itemsout.CacheTime", "60"));
		
                
                /* the following variables have to do with lists of arbitrary bib keys (bibnumbers) and recently added feeds. */
                bibNumbersURI = generalprops.getProperty("BibNumbersURI", "link%3D3100007%7E%21123%7E%213100001%7E%213100002");
                indexTableToUse = generalprops.getProperty("IndexTableToUse" , "all_title");
                maxIndexCacheSize = Integer.parseInt(generalprops.getProperty("MaxIndexCacheSize", "10000"));
                // this is used to make sure that titles that start with a particular prefix do not get included in recently added, etc. lists
                // for instance if all your ILL titles start with "ILLM", this variable will make sure those get filtered out
                titleFilterClause = generalprops.getProperty("TitleFilterClause", " and processed not like 'ILLM%' ");
                
                
		// the password is secret, thus no default given.  Flushing/viewing cache won't work without one, though.
		flushPasswd = generalprops.getProperty("FlushPasswd", "");
		defaultFeedType = generalprops.getProperty("DefaultFeedType", "passthrough");
		maxItemsInFeed = Integer.parseInt(generalprops.getProperty("MaxItemsInFeed", "100"));
		doTwice = (new Boolean( generalprops.getProperty("DoPatronPersonalRequestTwice", "true") )).booleanValue();
		canSpecifyStylesheet = (new Boolean( generalprops.getProperty("CanSpecifyStylesheet", "false") )).booleanValue();
                allowDirectBarcodePIN = ( new Boolean( generalprops.getProperty("AllowDirectBarcodePIN", "true")).booleanValue() );
                
                
		// the password is secret, thus no default given.  not required for software to work.
		sec3Password = generalprops.getProperty("Sec3Password", "");
		
		// JNDI/datapool related variables
                prefix = generalprops.getProperty("JNDIPrefix", "java:/");
                providerURL = generalprops.getProperty("JNDIProviderURL", "localhost:1099");
                datasourceName = generalprops.getProperty("DatasourceName", "horizon");
                
                encryptionPhrase = generalprops.getProperty("EncryptionPhrase", "the quick brown fox jumps over the lazy dog!");
                
		// the following represents URL parameters you do not want to support/consider distinctive enough to cache.
		String forbiddenParams = generalprops.getProperty("ForbiddenURLParameters", "menu,ri,logout,startover,ts,aspect,x,y,session,go_sort_limit.x,go_sort_limit.y,sec3");
		forbiddenURLParameters = forbiddenParams.split(",");
		Arrays.sort( forbiddenURLParameters);
		

		memCache = new GeneralCacheAdministrator( cacheprops );
                dbConnection = new RSSDBConnection( datasourceName, providerURL, prefix );
                
                 
	}
	
	
        public static String getBibNumbersURI() {
		return RSSConf.getInstance().bibNumbersURI;
        }
        public static String getIndexTableToUse() {
		return RSSConf.getInstance().indexTableToUse;
        }
        public static int getMaxIndexCacheSize() {
            return RSSConf.getInstance().maxIndexCacheSize;
        }
        public static String getTitleFilterClause() {
            return RSSConf.getInstance().titleFilterClause;
        }
        public static boolean getAllowDirectBarcodePIN() {
            return RSSConf.getInstance().allowDirectBarcodePIN;
        }
        public static String getEncryptionPhrase() {
            return RSSConf.getInstance().encryptionPhrase;
        }
        public static RSSDBConnection getRSSDBConnection() {
            return RSSConf.getInstance().dbConnection;
        }
	
	public static boolean isForbidden(String param){
		return (Arrays.binarySearch( RSSConf.getInstance().forbiddenURLParameters, param) > -1);	
	}
	
	public static String getStaticFeedURL( String key){
                String property = (String) RSSConf.getInstance().feedprops.getProperty(key);
		if( property.startsWith("bibkeys") ){
                   // then it is a list of bibkeys; need to build link for this. 
                    // expecting it to be of the form bibkeys:123,456,789
                   StringBuffer URLToGet = new StringBuffer(RSSConf.getBaseURL() + "?selectedkeys=");                               
                   RSSAdminHandler adminHandler = RSSServlet.getRSSAdminHandler();
                   String[] saTemp = property.split(":");
                   String[] saKeys = saTemp[1].split(","); //TODO: better error handing here.
                   for(int i = 0; i < saKeys.length; i++ ) {
                       if( i > 0 )  {
                            URLToGet.append("%2B");
                       }
                       URLToGet.append( adminHandler.bibNumToIndexNum(saKeys[i]) );
                   }
                   URLToGet.append( "&uri=" + RSSConf.getBibNumbersURI() +"&GetXML=true");            
                    return URLToGet.toString();        
                } else {
                    return property;
                }
	}
	
	public static String getFileName( String key ) {
                RSSConf instance = RSSConf.getInstance();
                String path = "";
                path = instance.fileprops.getProperty(key, "");
                if( path.length() > 0) {
                    if( path.indexOf("/") == -1 && path.indexOf("\\") == -1) {
                    // then it is relative path, assume in the feed-files dir.
                    path = instance.realPath + "/feed-files/" + path;
                    }
                }
		return path;
	}
	
	public static Enumeration getStaticFeedNames(){
		return RSSConf.getInstance().feedprops.propertyNames();
	}
	public static int getNumStaticFeeds(){
		return RSSConf.getInstance().feedprops.size();	
	}
	public static int getMaxItemsInFeed() {
		return RSSConf.getInstance().maxItemsInFeed;
	}
	public static int getDefaultTimeToLive(){
		return RSSConf.getInstance().defaultTimeToLive;
	}
        
        public static int getTimeToLive( String feedType ) {
	    RSSConf conf = RSSConf.getInstance();
	    String sTTL = (String) conf.stylesheetprops.getProperty( feedType + ".TTL" , "");
	    if( sTTL.length() == 0 ){
		    return conf.getDefaultTimeToLive();
	    } else {
		    return Integer.parseInt( sTTL );
	    }
            
        }
        
	public static int getGeneralCacheTime() { 
		return RSSConf.getInstance().defaultTimeToLive;
	}
	
	public static String getDefaultIndex(){
		return RSSConf.getInstance().defaultIndex;
	}
	public static String getDefaultNpp(){
		return RSSConf.getInstance().defaultNpp;
	}
	public static String getDefaultSort(){
		return RSSConf.getInstance().defaultSort;
	}
	public static String getDefaultLimit(){
		return RSSConf.getInstance().defaultLimit;
	}
	public static String getBaseURL(){
		return RSSConf.getInstance().baseURL;
	}
	public static int getItemsOutCacheTime(){
		return RSSConf.getInstance().itemsOutCacheTime;
	}
	public static String getFlushPasswd(){
		return RSSConf.getInstance().flushPasswd;
	}
	public static String getAdminPasswd() {
		return RSSConf.getInstance().flushPasswd;
	}
        // JNDI-related stuff
        public static String getJNDIPrefix() {
            return RSSConf.getInstance().prefix;
        }
        public static String getJNDIProviderURL() {
            return RSSConf.getInstance().providerURL;
        }
        public static String getDatasourceName() {
            return RSSConf.getInstance().datasourceName;
        }
	
	// error-related stuff
	public static String getNoFeedFound(){
		return RSSConf.getInstance().noFeedFound;
	}
	public static String getItemsOutProblem(){
		return RSSConf.getInstance().itemsOutProblem;
	}
	public static String getHoldsProblem() {
		return RSSConf.getInstance().holdsProblem;
	}
	public static String getItemsOutURL(){
		return RSSConf.getInstance().itemsOutURL;
	}
	public static String getHoldsURL() {
		return RSSConf.getInstance().holdsURL;
	}
	/* // following are obsolete
	public String getItemsOutFeedStylesheetName(){
		return itemsOutFeedStylesheet;
	}
	public String getHoldsStylesheetName() {
		return holdsFeedStylesheet;
	}
	public String getSearchStylesheetName() {
		return searchStylesheet;
	}
	public String getOpenSearchStylesheetName() {
		return openSearchStylesheet;
	}
	public String getOpenSearchDescriptionStylesheetName() {
		return openSearchDescriptionStylesheet;
	}
	public String getStylesheetName(){ // legacy
		return searchStylesheet;
	}
	public String getStaticStylesheetName(){
		return staticStylesheet;
	}
	public String getFileStylesheetName() {
		return fileStylesheet;
	}
	*/
	
	public static String getDefaultFeedType() {
		return RSSConf.getInstance().defaultFeedType;
	}
	public static String getSec3Password() {
		return RSSConf.getInstance().sec3Password;
	}
	public static boolean getDoTwice() {
		return RSSConf.getInstance().doTwice;
	}
	public static boolean canSpecifyStylesheet() {
		return RSSConf.getInstance().canSpecifyStylesheet;
	}
	
	public static String getStylesheetName( String style) {
		// TODO: make (more) useful
                RSSConf instance = RSSConf.getInstance();
                String stylePath = (String) instance.stylesheetprops.getProperty(style, "");
                if( stylePath.length() == 0 ){
                    RSSConf.getLogger().error("no stylesheet defined for style " + style + ", using feed.xsl instead");
                    stylePath = "feed.xsl"; // if it's not in the properties file, use the feed.xsl instead.
                }
		return instance.realPath + "/feed-cache/" + stylePath;		
	}
	
	public static String getDefaultStyle( String feedType ) {
		// in the stylesheetprops, we are expecting there to be a stylesheet
		// matching the feed type name for each feed type.
		return RSSConf.getStylesheetName( feedType );	
	}
	
	public static String getProblemFeed( String feedType ) {
		RSSConf conf = RSSConf.getInstance();
		if( feedType.equals("itemsout") ){
			return conf.itemsOutProblem;
		} else if (feedType.equals("holds") ) {
			return conf.holdsProblem;
		} else {
			//TODO: expand here...
			return conf.genericProblem;
		}
	}
	
	public static Properties getCacheConfig() {
		return RSSConf.getInstance().cacheprops;
	}
	
	public static Logger getLogger() {
		return RSSConf.getInstance().log; 
	}
	
	public static GeneralCacheAdministrator getCache() {
		return RSSConf.getInstance().memCache;
	}
}
