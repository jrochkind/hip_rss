<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:sdf="/java.text.SimpleDateFormat"
	xmlns:javaUtilDate="/java.util.Date"
	xmlns:Utils="/com.cometa.utils.Utils"
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="javaUtilDate sdf xhtml Utils"
>
	
<xsl:output method="xml" indent="yes"/>

<xsl:include href="variables.xsl" />

<!-- the following looks really ridiculous, but it was the only way I could get the literal T to appear between yy-MM-dd and HH:mm:ss -->
<xsl:variable name="theDate">
	<xsl:value-of select="sdf:format(sdf:new('yyyy-MM-dd'), javaUtilDate:new() )" />T<xsl:value-of select="sdf:format(sdf:new('HH:mm:ss'), javaUtilDate:new() )" />
</xsl:variable>


<xsl:variable name="theYear" select="sdf:format(sdf:new('yyyy'), javaUtilDate:new() )" />

<xsl:template match="searchresponse">
<rdf:RDF
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns="http://purl.org/rss/1.0/"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:sy="http://purl.org/rss/1.0/modules/syndication/"
>

<channel>
	<xsl:attribute name="rdf:about"><xsl:value-of select="$yourURL" /></xsl:attribute>
	<title><xsl:value-of select="$yourLibrary" /></title>
	<link><xsl:value-of select="$yourURL" /></link>	
	<description>a feed of items from <xsl:value-of select="$yourLibrary" />'s catalog.</description>
	<dc:language><xsl:value-of select="$yourLanguage" /></dc:language>
	<dc:rights>Copyright <xsl:value-of select="$theYear" />, <xsl:value-of select="$yourLibrary" /></dc:rights>
	<dc:date><xsl:value-of select="$theDate" /></dc:date>
	<dc:publisher><xsl:value-of select="$yourLibrary" /></dc:publisher>
	<dc:creator><xsl:value-of select="$yourLibrary" /></dc:creator>
	<xsl:if test="//search/shortcut = $subjectShortcut" >
		<!-- the standard says that the subject should be from a controlled vocabulary.  
		So thought it's tempting touse the search term as the dublin core subject, but
		that wouldn't be polite.-->
		<dc:subject><xsl:value-of select="//search/term" /></dc:subject>
	</xsl:if>
	<sy:updatePeriod>daily</sy:updatePeriod>
	<sy:updateFrequency>1</sy:updateFrequency>
	<sy:updateBase>1970-01-01T00:00+00:00</sy:updateBase>
	<items>
		<rdf:Seq>
			<xsl:for-each select="//searchresults/results/row">
				<rdf:li>
					<xsl:attribute name="rdf:resource">
						<xsl:value-of select="$yourURL" />&amp;uri=<xsl:value-of select="TITLE/data/link/func"/>
					</xsl:attribute>
				</rdf:li>
			</xsl:for-each>
		</rdf:Seq>
	</items>
</channel>
	<image>
		<xsl:attribute name="rdf:about"><xsl:value-of select="$logoURL" /></xsl:attribute>
		<title><xsl:value-of select="$yourLibrary" /> logo</title>
		<url><xsl:value-of select="$logoURL" /></url>
		<link><xsl:value-of select="$yourURL" /></link>
	</image>
	<xsl:for-each select="//searchresults/results/row">
		<item>
			<xsl:attribute name="rdf:about">
				<xsl:value-of select="$yourURL" />&amp;uri=<xsl:value-of select="TITLE/data/link/func" />
			</xsl:attribute>
			<title><xsl:value-of select="TITLE/data/text" /></title>
			<link>	
				<xsl:value-of select="$yourURL" />&amp;view=items&amp;uri=<xsl:value-of select="TITLE/data/link/func" />&amp;term=<xsl:value-of select="//yoursearch//term" />
			</link>
			<description>
					<xsl:if test="not(string-length(isbn) = 0)">
						<xhtml:img>
							<xsl:attribute name="src">http://syndetics.com/index.aspx?type=xw12&amp;isbn=<xsl:value-of select="isbn" />/SC.GIF&amp;client=<xsl:value-of select="$syndeticsID" /></xsl:attribute>
						</xhtml:img>
					</xsl:if>
				<xhtml:br/>
				<xsl:value-of select="TITLE/data/text" />
					<xsl:if test="not(string-length(AUTHOR/data/text) = 0)">
							by <xsl:value-of select="AUTHOR/data/text" />
					</xsl:if> 
				<!--<xsl:if test="not(string-length(cell[1]/data/text) = 0)">
					    <xhtml:br /><xsl:value-of select="cell[1]/data/text" />
				</xsl:if> -->
                        </description>
			<xsl:if test="not(string-length(AUTHOR/data/text) = 0)">
				<dc:creator><xsl:value-of select="AUTHOR/data/text" /></dc:creator>
			</xsl:if>
			<xsl:if test="not(string-length(PUBDATE/data/text) = 0)">
				<dc:date><xsl:value-of select="PUBDATE/data/text" /></dc:date>
			</xsl:if>
			<xsl:if test="//search/shortcut = $subjectShortcut" >
				<dc:subject><xsl:value-of select="//search/term" /></dc:subject>
			</xsl:if>
			<!-- TODO: have type go off of the gmd -->
			<dc:type>Text</dc:type>
			<xsl:if test="not(string-length(PUBLISHER/data/text) = 0)">
				<dc:publisher><xsl:value-of select="PUBLISHER/data/text" /></dc:publisher>
			</xsl:if>
		</item>
	</xsl:for-each>
		<textinput>
			<xsl:attribute name="rdf:about">
				<xsl:value-of select="$yourURL" />
			</xsl:attribute>
			<title>
				Search <xsl:value-of select="$yourLibrary" />
			</title>
			<description>
				Search the collection of <xsl:value-of select="$yourLibrary" />
			</description>
			<name>term</name>
			<link><xsl:value-of select="$yourURL" />&amp;index=<xsl:value-of select="$generalShortcut" /></link>
		</textinput>

</rdf:RDF>
</xsl:template>
</xsl:stylesheet>
