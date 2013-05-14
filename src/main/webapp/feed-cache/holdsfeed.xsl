<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:sdf="/java.text.SimpleDateFormat"
	xmlns:javaUtilDate="/java.util.Date"
	xmlns:Utils="/org.spl.utils.Utils"
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="javaUtilDate sdf Utils">

<xsl:output method="xsl" indent="yes"/>

<xsl:include href="variables.xsl" />

<!-- theDate is following the standard for RSS. sdf:format(sdf:new('EEE, dd MMM yyyy H:m:s z') -->
<xsl:variable name="theDate" select="sdf:format(sdf:new('EEE, dd MMM yyyy H:m:s z'), javaUtilDate:new() )" />

<xsl:template match="patronpersonalresponse">
<xsl:processing-instruction name="xml-stylesheet">
  <xsl:text>type="text/xsl" </xsl:text>
  <xsl:text>href="/rssres/xsl/rss.xsl"</xsl:text>
</xsl:processing-instruction>

<rss version = "2.0">
	<channel>
		<xsl:variable name="patronName">
			<xsl:value-of select="//security/name"/>
		</xsl:variable>
		<title><xsl:value-of select="Utils:fixName($patronName)" />'s holds from <xsl:value-of select="$yourLibrary" /></title>
		<link><xsl:value-of select="$yourURL" /></link>
		<description>A feed of holds you have from <xsl:value-of select="$yourLibrary" /></description>
		<language>en-us</language>
		<generator>SPL-RSS</generator>
		<lastBuildDate><xsl:value-of select="$theDate" /></lastBuildDate>
		<image>
			<title><xsl:value-of select="$yourLibrary" /> logo</title>
			<url><xsl:value-of select="$logoURL" /></url>
			<link><xsl:value-of select="$yourURL" /></link>
		</image>
		
		<xsl:for-each select="//ready/readyitem">
			<item>
				<xsl:variable name="dateexpires"><xsl:value-of select="dateexpires" /></xsl:variable>
				<title>Ready for pickup: <xsl:value-of select="TITLE/data/text" /></title>
				<link><xsl:value-of select="$yourURL" />&amp;view=items&amp;uri=<xsl:value-of select="TITLE/data/link/func" /></link>
				<description>
					<img>
						<xsl:attribute name="src">https://syndetics.com/index.aspx?type=xw12&amp;isbn=<xsl:value-of select="isbn" />/SC.GIF&amp;client=<xsl:value-of select="$syndeticsID" /></xsl:attribute>
					</img>
				<xsl:value-of select="TITLE/data/text" /> 
				
				<xsl:if test="not(string-length(AUTHOR/data/text) = 0)">
					by <xsl:value-of select="AUTHOR/data/text" /> 
				</xsl:if>
				<!-- <xsl:if test="not(string-length(cell[1]/data/text) = 0)" >
					(<xsl:value-of select="cell[1]/data/text"/>)
				</xsl:if> -->
				<xhtml:p/>
				Pickup Location: <xsl:value-of select="pickuploc" />
				<xhtml:p/>
				<xhtml:b>Request Expires: <xsl:value-of select="$dateexpires" /></xhtml:b>
				</description>
				<pubDate><xsl:value-of select="Utils:genRFC822Date($dateexpires, $time_on_hold_shelf)" /></pubDate>
			</item>
		</xsl:for-each>
		<xsl:for-each select="//waiting/waitingitem">
			<item>
				<title>Not yet ready: <xsl:value-of select="TITLE/data/text" /></title>
				<link><xsl:value-of select="$yourURL" />&amp;view=items&amp;uri=<xsl:value-of select="TITLE/data/link/func" /></link>
				<description>
					<img>
						<xsl:attribute name="src">https://syndetics.com/index.aspx?type=xw12&amp;isbn=<xsl:value-of select="isbn" />/SC.GIF&amp;client=<xsl:value-of select="$syndeticsID" /></xsl:attribute>
					</img>
					<xhtml:br/>
					<xsl:value-of select="TITLE/data/text" /> 
					<xsl:if test="not(string-length(AUTHOR/data/text) = 0)">
						by <xsl:value-of select="AUTHOR/data/text" /> 
					</xsl:if>
					<xsl:if test="not(string-length(cell[1]/data/text) = 0)" >
						(<xsl:value-of select="cell[1]/data/text"/>)
					</xsl:if>
					<xhtml:p/>
					Pickup Location: <xsl:value-of select="pickuploc" />
					<xhtml:p/>
					Request Expires: <xsl:value-of select="dateexpires" />
					<xhtml:p/>
					<!-- this won't work if the 2nd non-fixed element of yours is not number of requests. -->
					<!--Queue position: <xsl:value-of select="queuepos" /> of  <xsl:value-of select="cell[2]/data/text"/> -->

					<xhtml:p/>
					Status: <xsl:variable name="text"><xsl:value-of select="status"/></xsl:variable>
						<xsl:choose>
							<xsl:when test="boolean(normalize-space($text))">
							   <xsl:choose>
								   <xsl:when test="$text='H' or $text='h'">
								   <xsl:value-of select="$patron_options_active"/>
								   </xsl:when>
								   <xsl:when test="$text='I' or $text='i'">
								   <xsl:value-of select="$patron_options_suspended"/>
								   </xsl:when>
								   <xsl:when test="$text='T' or $text='t'">
								   <xsl:value-of select="$patron_options_transit"/>
								   </xsl:when>
								   <xsl:otherwise>
									<xsl:value-of select="$text"/>
								   </xsl:otherwise>
							   </xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text disable-output-escaping="yes">unknown</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
				</description>
				<xsl:variable name="dateplaced"><xsl:value-of select="dateplaced" /></xsl:variable>
				<pubDate><xsl:value-of select="Utils:genRFC822Date($dateplaced)" /></pubDate>
			</item>
		
		</xsl:for-each>
		
		
	</channel>
</rss>
</xsl:template>


</xsl:stylesheet>
