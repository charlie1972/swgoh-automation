<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:fn="http://www.w3.org/2005/xpath-functions"
>
<xsl:template match="/">
  <mods>
    <xsl:for-each select="//div/div[@class='mod-set-view']/div">
      <mod>
        <fromCharacter>
          <xsl:value-of select=".//div[@class='assigned-character']/span"/>
        </fromCharacter>
        <character>
          <xsl:value-of select="../../../div/h3"/>
        </character>
        <xsl:variable name="detail" select="fn:tokenize(fn:normalize-space(./div[1]/@class), '\s')"/>
        <dots><xsl:value-of select="fn:tokenize($detail[2], '-')[2]"/></dots>
        <level>
          <xsl:value-of select="./div[1]/div[3]"/>
        </level>
        <slot><xsl:value-of select="$detail[3]"/></slot>
        <tier><xsl:value-of select="$detail[5]"/></tier>
        <set><xsl:value-of select="$detail[4]"/></set>
        <primary-stat>
          <xsl:value-of select="fn:normalize-space(.//div[@class='mod-stats']/ul[1]/li)"/>
        </primary-stat>
        <xsl:for-each select=".//div[@class='mod-stats']/ul[2]/li">
          <secondary-stat>
            <xsl:value-of select="./span[@class='rolls']" />
            <xsl:text> </xsl:text>
            <xsl:for-each select="text()">
              <xsl:value-of select="normalize-space(.)"/>
            </xsl:for-each>
          </secondary-stat>
        </xsl:for-each>
      </mod>
    </xsl:for-each>
  </mods>
</xsl:template>
</xsl:stylesheet>
