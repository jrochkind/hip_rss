<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:sdf="/java.text.SimpleDateFormat"
	xmlns:javaUtilDate="/java.util.Date"
	xmlns:Utils="/org.spl.utils.Utils"
	xmlns:openSearch="http://a9.com/-/spec/opensearchrss/1.0/"
	exclude-result-prefixes="javaUtilDate sdf Utils">
<xsl:output method="xml" indent="yes"/>

<xsl:include href="variables.xsl" />

<!-- theDate is following the standard for RSS. sdf:format(sdf:new('EEE, dd MMM yyyy H:m:s z') -->
<xsl:variable name="theDate" select="sdf:format(sdf:new('EEE, dd MMM yyyy H:m:s z'), javaUtilDate:new() )" />
<xsl:variable name="theYear" select="sdf:format(sdf:new('yyyy'), javaUtilDate:new() )" />

<xsl:template match="searchresponse">
<rss version = "2.0">
	<channel>
		<title>Search on <xsl:value-of select="//yoursearch//term" /> from <xsl:value-of select="$yourLibrary" /></title>
		<link><xsl:value-of select="$yourURL" /></link>
		<description>Search results for <xsl:value-of select="//yoursearch//term" /> from <xsl:value-of select="$yourLibrary" />'s catalog .</description>
		<language>en-us</language>
		<copyright>&amp;copy; <xsl:value-of select="$theYear" />,  <xsl:value-of select="$yourLibrary" />.</copyright>
		<lastBuildDate><xsl:value-of select="$theDate" /></lastBuildDate>
		<pubDate><xsl:value-of select="$theDate" /></pubDate>
		<docs>http://blogs.law.harvard.edu/tech/rss</docs>
		<generator>SPL-RSS</generator>
		<openSearch:totalResults xmlns:openSearch="http://a9.com/-/spec/opensearchrss/1.0/"><xsl:value-of select="//hits" /></openSearch:totalResults>
		<openSearch:startIndex xmlns:openSearch="http://a9.com/-/spec/opensearchrss/1.0/"><xsl:value-of select="//view/start" /></openSearch:startIndex>
		<openSearch:itemsPerPage xmlns:openSearch="http://a9.com/-/spec/opensearchrss/1.0/"><xsl:value-of select="//view/npp" /></openSearch:itemsPerPage>
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
					<xsl:variable name="theURL">http://localhost/lastmodified?bibNum=<xsl:value-of select="key" /></xsl:variable>
					<pubDate><xsl:value-of select="Utils:getURL($theURL)" /></pubDate>

					<title><xsl:value-of select="TITLE/data/text" /></title>
					<!-- TODO: include category if it is a summary from a browse.  <category></category>-->
					<link><xsl:value-of select="$yourURL" />&amp;view=items&amp;uri=<xsl:value-of select="TITLE/data/link/func" /></link>
					<guid isPermaLink="true"><xsl:value-of select="$yourURL" />&amp;uri=<xsl:value-of select="TITLE/data/link/func" /></guid>
					<description>&lt;img src="https://syndetics.com/index.aspx?type=xw12&amp;isbn=<xsl:value-of select="isbn" />/SC.GIF&amp;client=<xsl:value-of select="$syndeticsID" />"&gt;&lt;br/&gt;
					<xsl:value-of select="TITLE/data/text" /> by <xsl:value-of select="AUTHOR/data/text" /> 
					<!-- <xsl:if test="string-length(cell[1]/data/text) &gt; 0" >
						(<xsl:value-of select="cell[1]/data/text" />)
					
					</xsl:if> -->
					</description>
				</item>
			</xsl:for-each>
		</xsl:when>
		<xsl:otherwise>
			<!-- give back some kind of default error message when there is a bad search.  -->
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
