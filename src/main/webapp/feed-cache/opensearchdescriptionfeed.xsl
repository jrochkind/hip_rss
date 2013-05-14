<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:sdf="/java.text.SimpleDateFormat"
	xmlns:javaUtilDate="/java.util.Date"
	xmlns:Utils="/org.spl.utils.Utils"
	exclude-result-prefixes="javaUtilDate sdf Utils">
<xsl:output method="xml" indent="yes"/>

<xsl:include href="variables.xsl" />
<xsl:variable name="theDate" select="sdf:format(sdf:new('EEE, dd MMM yyyy H:m:s z'), javaUtilDate:new() )" />
<xsl:variable name="theYear" select="sdf:format(sdf:new('yyyy'), javaUtilDate:new() )" />

<xsl:template match="/">
	<OpenSearchDescription xmlns="http://a9.com/-/spec/opensearchdescription/1.0/">
		<Url><xsl:value-of select="$baseRSSURL" />term=(searchTerms)&amp;type=opensearch&amp;npp=(count)&amp;page=(startPage)</Url>
		<Format>http://a9.com/-/spec/opensearchrss/1.0/</Format>
		<ShortName><xsl:value-of select="$shortName" /></ShortName>
		<LongName><xsl:value-of select="$yourLibrary" /></LongName>
		<Description>Search for materials at <xsl:value-of select="$yourLibrary" /></Description>
		<Tags><xsl:value-of select="$yourLibrary" /> catalog opac SPL-RSS</Tags>
		<Image><xsl:value-of select="$logoURL" /></Image>
		<SampleSearch>cats</SampleSearch>
		<Developer>SPL-RSS</Developer>
		<Contact><xsl:value-of select="$libraryContactEmail" /></Contact>
		<Attribution>Data copyright <xsl:value-of select="$theYear" />, <xsl:value-of select="$yourLibrary" />.  All rights reserved.</Attribution>
		<SyndicationRight>open</SyndicationRight>
		<AdultContent>false</AdultContent>
	</OpenSearchDescription>
</xsl:template>
</xsl:stylesheet>
