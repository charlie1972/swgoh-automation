package com.charlie.swgoh.connector;

import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.datamodel.xml.Mods;
import com.charlie.swgoh.exception.ProcessException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HtmlConnector {

  private static final Pattern PATTERN_TO_REMOVE = Pattern.compile("<img.*?>|<input.*?>|<br.*?>|<script>.*?</script>|&.*?;");

  private static final String XSL_FILE = "/xml/transform.xsl";

  private HtmlConnector() {}

  public static Map<String, List<Mod>> getModsByCharacterFromHTML(String filename) {
    Mods mods = getModsFromHTML(filename);
    return mods.getMods().stream().collect(Collectors.groupingBy(Mod::getCharacter, LinkedHashMap::new, Collectors.toList()));
  }

  public static Map<String, List<Mod>> getModsByFromCharacterFromHTML(String filename) {
    Mods mods = getModsFromHTML(filename);
    return mods.getMods().stream()
            .filter(mod -> !mod.getFromCharacter().isEmpty())
            .collect(Collectors.groupingBy(Mod::getFromCharacter, LinkedHashMap::new, Collectors.toList()));
  }

  private static Mods getModsFromHTML(String fileName) {
    try {
      String html = Files.readString(new File(fileName).toPath());
      String xmlFromHtml = convertHTMLToXML(html);
      String transformedXML = transformXML(xmlFromHtml);
      return unmarshallXML(transformedXML);
    }
    catch (Exception e) {
      throw new ProcessException(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  private static String convertHTMLToXML(String html) {
    String temp = extractBody(html);
    Matcher matcher = PATTERN_TO_REMOVE.matcher(temp);
    return matcher.replaceAll("");
  }

  private static String extractBody(String html) {
    int start = html.indexOf("<body>");
    if (start < 0) {
      return html;
    }
    int end = html.indexOf("</body>");
    if (end < 0) {
      throw new ProcessException("HTML input file has a <body> tag but it is not closed");
    }
    return html.substring(start, end + 7);
  }

  private static String transformXML(String xml) throws Exception {
    TransformerFactory transformerFactory = new net.sf.saxon.TransformerFactoryImpl();
    Source xslSource = new StreamSource(HtmlConnector.class.getResourceAsStream(XSL_FILE));
    Transformer transformer = transformerFactory.newTransformer(xslSource);

    Source xmlSource = new StreamSource(new StringReader(xml));
    StringWriter stringWriter = new StringWriter();
    transformer.transform(xmlSource, new StreamResult(stringWriter));

    return stringWriter.toString();
  }

  private static Mods unmarshallXML(String xml) throws Exception {
    JAXBContext context = JAXBContext.newInstance(Mods.class);
    Unmarshaller unmarshaller = context.createUnmarshaller();
    return (Mods) unmarshaller.unmarshal(new StringReader(xml));
  }

}
