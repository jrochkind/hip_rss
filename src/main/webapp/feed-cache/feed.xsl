<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:sdf="/java.text.SimpleDateFormat"
	xmlns:javaUtilDate="/java.util.Date"
	xmlns:Utils="/org.spl.utils.Utils"
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="javaUtilDate sdf xhtml Utils">
	
<xsl:output method="xml" indent="yes"/>

<xsl:include href="variables.xsl" />


<!-- theDate is following the standard for RSS. sdf:format(sdf:new('EEE, dd MMM yyyy H:m:s z') -->
<xsl:variable name="theDate" select="sdf:format(sdf:new('EEE, dd MMM yyyy HH:mm:ss z'), javaUtilDate:new() )" />

<xsl:template match="searchresponse">
<xsl:processing-instruction name="xml-stylesheet">
  <xsl:text>type="text/xsl" </xsl:text>
  <xsl:text>href="/rssres/xsl/rss.xsl"</xsl:text>
</xsl:processing-instruction>
<rss version = "2.0">
	<channel>
		<title><xsl:value-of select="//yoursearch//term" /> feed from <xsl:value-of select="$yourLibrary" /></title>
		<link><xsl:value-of select="$yourURL" /></link>
		<description>a feed of items from <xsl:value-of select="$yourLibrary" />'s catalog.</description>
		<language>en-us</language>
		<lastBuildDate><xsl:value-of select="$theDate" /></lastBuildDate>
		<pubDate><xsl:value-of select="$theDate" /></pubDate>
		<docs>http://blogs.law.harvard.edu/tech/rss</docs>
		<generator>SPL-RSS</generator>
		<ttl>360</ttl>
		<image>
			<title><xsl:value-of select="$yourLibrary" /> logo</title>
			<url><xsl:value-of select="$logoURL" /></url>
			<link><xsl:value-of select="$yourURL" /></link>
		</image>
	<xsl:variable name="hits"><xsl:value-of select="//hits" /></xsl:variable>
	<xsl:choose>
	<xsl:when test="$hits &gt; 0 ">
		<xsl:for-each select="//searchresults/results/row">
			<item>
				<!-- normally pubDate indicates the date that this item was added to the feed, which is a 
					bit irrelevant in our case, so I am just using the last time the whole feed was manipulated 
					(the lastBuildDate)
				-->
				<!--<pubDate><xsl:value-of select="$theDate" /></pubDate> -->
				<!-- this web service gets the last modified date from the bib control table to use as pubDate -->
				<xsl:variable name="theURL">http://localhost/rss?service=lastmodified&amp;bib=<xsl:value-of select="key" /></xsl:variable>
				<pubDate><xsl:value-of select="Utils:getURL($theURL)" /></pubDate>

				<title><xsl:value-of select="TITLE/data/text" /></title>
				<!-- TODO: include category if it is a summary from a browse.  eg <category domain="http://www.loc.gov/cds/lcsh.html"></category>-->
				<link><xsl:value-of select="$yourURL" />&amp;view=items&amp;uri=<xsl:value-of select="TITLE/data/link/func" />&amp;term=<xsl:value-of select="//yoursearch//term" /></link>
				<guid isPermaLink="true"><xsl:value-of select="$yourURL" />&amp;<xsl:value-of select="TITLE/data/link/func" /></guid>
				<description>
					<img>
						<xsl:attribute name="src">http://syndetics.com/index.aspx?type=xw12&amp;isbn=<xsl:value-of select="isbn" />/SC.GIF&amp;client=<xsl:value-of select="$syndeticsID" /></xsl:attribute>
					</img>
				<xhtml:br/>
					<xsl:value-of select="TITLE/data/text" />
					<p>
					<xsl:if test="not(string-length(AUTHOR/data/text) = 0)">
							by <xsl:value-of select="AUTHOR/data/text" />
					</xsl:if>
					</p>
				 
                          </description>
			</item>
		</xsl:for-each>
	</xsl:when>
	<xsl:otherwise>
			<item>
				<pubDate><xsl:value-of select="$theDate" /></pubDate>
				<title>No search results found for <xsl:value-of select="//yoursearch//term" /></title>
				<link><xsl:value-of select="$yourURL" /></link>
				<guid isPermaLink="false">error!</guid>
				<description>No search results were found for your search.  Please make sure you are using the proper syntax.  You can check the 
				library catalog directly at <xsl:value-of select="$yourURL" />.</description>
			</item>
	</xsl:otherwise>
	</xsl:choose>
	</channel>
</rss>
</xsl:template>


</xsl:stylesheet>
