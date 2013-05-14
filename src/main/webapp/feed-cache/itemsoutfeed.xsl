<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:sdf="java.text.SimpleDateFormat"
	xmlns:javaUtilDate="java.util.Date"
	xmlns:Utils="org.spl.utils.Utils"
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="javaUtilDate sdf xhtml Utils">
	
<xsl:output method="xml" indent="yes"/>

<xsl:include href="variables.xsl" />


<!-- theDate is following the standard for RSS. sdf:format(sdf:new('EEE, dd MMM yyyy HH:m:ss Z') -->
<xsl:variable name="theDate" select="sdf:format(sdf:new('EEE, dd MMM yyyy HH:mm:ss Z'), javaUtilDate:new() )" />

<xsl:template match="patronpersonalresponse">
<!-- <xsl:processing-instruction name="xml-stylesheet">
  <xsl:text>type="text/xsl" </xsl:text>
  <xsl:text>href="/rssres/xsl/rss.xsl"</xsl:text>
</xsl:processing-instruction> -->

<rss version = "2.0">
	<channel>
		<xsl:variable name="patronName">
			<xsl:value-of select="//security/name"/>
		</xsl:variable>
		<title><xsl:value-of select="Utils:fixName($patronName)" />'s items out from <xsl:value-of select="$yourLibrary" /></title>
		<link><xsl:value-of select="$yourURL" /></link>
		<description>A feed of items you have out from <xsl:value-of select="$yourLibrary" /></description>
		<language>en-us</language>
		<generator>SPL-RSS</generator>
		<lastBuildDate><xsl:value-of select="$theDate" /></lastBuildDate>
		<image>
			<title><xsl:value-of select="$yourLibrary" /> logo</title>
			<url><xsl:value-of select="$logoURL" /></url>
			<link><xsl:value-of select="$yourURL" /></link>
		</image>
		
		<xsl:for-each select="//itemsoutdata/itemout">
			<item>
				<xsl:variable name="duedate"><xsl:value-of select="duedate" /></xsl:variable>
				<xsl:variable name="dueIn"><xsl:value-of select="Utils:dueIn($duedate)" /></xsl:variable>
				<title>
				<xsl:if test="not(string-length($dueIn) = 0)" >
					<xsl:text>  </xsl:text>
					<xhtml:span>
						<xsl:attribute name="style">color: #c00;</xsl:attribute>
						<xsl:value-of select="$dueIn" />
					</xhtml:span>
				</xsl:if>
				<xsl:value-of select="TITLE/data/text" /></title>
				<link><xsl:value-of select="$yourURL" />&amp;view=items&amp;uri=<xsl:value-of select="TITLE/data/link/func" />&amp;session=<xsl:value-of select="/patronpersonalresponse/session" /></link>
				<description>
					&lt;img src=&quot;https://syndetics.com/index.aspx?type=xw12&amp;isbn=<xsl:value-of select="isbn"/>/SC.GIF&amp;client=<xsl:value-of select="$syndeticsID" />&quot; /&gt;
					&lt;p&gt;
						<xsl:value-of select="TITLE/data/text" />
						<xsl:if test="not(string-length(AUTHOR/data/text) = 0)">
							by <xsl:value-of select="AUTHOR/data/text" /> 
							</xsl:if>
					<!--for format -> this won't work if the 1st non-fixed element of yours is not format. 
						<xsl:if test="not(string-length(cell[1]/data/text) = 0)" >
							(<xsl:value-of select="cell[1]/data/text" />)
							</xsl:if> -->
					&lt;/p&gt;
					&lt;p&gt;
						&lt;strong&gt;Date Due: <xsl:value-of select="duedate" />&lt;/strong&gt;
					&lt;/p&gt;
					&lt;p&gt;
						Checkout Date: <xsl:value-of select="ckodate" />
					&lt;/p&gt;
				</description>
				<xsl:variable name="ckodate"><xsl:value-of select="ckodate" /></xsl:variable>
				
				<!-- pubDateForItemsOut makes the pubDate be cko date if it is due in more than 5 days.  If < 5 days till due it is today's date. -->
				<pubDate><xsl:value-of select="Utils:pubDateForItemsOut($duedate, $ckodate)" /></pubDate>
			</item>
		</xsl:for-each>
	</channel>
</rss>
</xsl:template>


</xsl:stylesheet>
