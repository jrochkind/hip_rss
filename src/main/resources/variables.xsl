<?xml version="1.0" ?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- IMPORTANT STUFF!!! YOU MUST CHANGE THESE! -->

<!-- 
  Note: Weird things happen here with maven when this property is
  set to ${catalog.url} - it is interpolated as the Maven Project
  URL, which is http://javaprojects.mse.jhu.edu.  So I changed
  the name of the property to ${catalog.opac.url}.
-->
<xsl:variable name="yourURL">${catalog.opac.url}</xsl:variable>
<xsl:variable name="logoURL">${catalog.logo.url}</xsl:variable> 
<xsl:variable name="yourLibrary">${library.name}</xsl:variable> 
<xsl:variable name="syndeticsID">${catalog.sendeticsid}</xsl:variable>
<!-- time_on_hold shelf is used to calculate the publication date in the holds feed. -->
<xsl:variable name="time_on_hold_shelf">${catalog.timeonholdshelf}</xsl:variable> <!-- # of days things stay on your hold shelf * -1  --> 

<!-- END IMPORTANT STUFF -->

<xsl:variable name="patron_options_active">active</xsl:variable>
<xsl:variable name="patron_options_suspended">suspended</xsl:variable>
<xsl:variable name="patron_options_transit">in transit</xsl:variable>

<!-- for opensearch -->
<xsl:variable name="shortName">${library.shortname}</xsl:variable> <!-- must be less than 16 chars. -->
<xsl:variable name="baseRSSURL">${rss.feed.baseurl2}</xsl:variable>
<xsl:variable name="libraryContactEmail">${library.contactemail}</xsl:variable>
<!-- end for opensearch -->

<!-- for RDF -->
<xsl:variable name="yourLanguage">en-us</xsl:variable> 
<xsl:variable name="subjectShortcut">.SW</xsl:variable>
<xsl:variable name="generalShortcut">.GW</xsl:variable>
<!-- end for RDF -->

</xsl:stylesheet>
