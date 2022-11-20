package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.connector.HtmlConnector;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.util.FileUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MoveMods extends AbstractMoveMods {

  private final String fileName;
  private final boolean useAllSlots;
  private final boolean isDryRun;

  public MoveMods(String fileName, boolean useAllSlots, boolean isDryRun, boolean startImmediately) {
    super(startImmediately);
    if (fileName == null) {
      throw new ProcessException("No mods move file selected");
    }
    this.fileName = fileName;
    this.useAllSlots = useAllSlots;
    this.isDryRun = isDryRun;
  }

  @Override
  protected String getFileNameSuffix() {
    return "move";
  }

  @Override
  protected void doProcess() throws Exception {
    List<Mod> modList = HtmlConnector.getModsFromHTML(fileName, useAllSlots);
    Map<String, List<Mod>> modMap = modList.stream().collect(Collectors.groupingBy(Mod::getCharacter, LinkedHashMap::new, Collectors.toList()));

    FileUtil.FileComponents fileComponents = FileUtil.getFileComponents(fileName);

    perform(fileComponents, modMap, isDryRun);
  }

}
