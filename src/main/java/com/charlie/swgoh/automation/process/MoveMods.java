package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.connector.HtmlConnector;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.datamodel.xml.Mods;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.screen.CharacterModsScreen;
import com.charlie.swgoh.screen.ModScreen;
import com.charlie.swgoh.screen.ModScreenFilter;
import com.charlie.swgoh.util.AutomationUtil;
import com.charlie.swgoh.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoveMods extends AbstractMoveMods {

  private String fileName;
  private boolean isDryRun;

  @Override
  public void setParameters(String... parameters) {
    fileName = parameters[0];
    isDryRun = Boolean.parseBoolean(parameters[1]);
  }

  @Override
  protected String getFileNamePrefix() {
    return "move-mods";
  }

  @Override
  protected void doProcess() throws Exception {
    List<Mod> modList = HtmlConnector.getModsFromHTML(fileName);
    Map<String, List<Mod>> modMap = modList.stream().collect(Collectors.groupingBy(Mod::getCharacter, LinkedHashMap::new, Collectors.toList()));

    FileUtil.FileComponents fileComponents = FileUtil.getFileComponents(fileName);

    perform(fileComponents, modMap, isDryRun);
  }

}
