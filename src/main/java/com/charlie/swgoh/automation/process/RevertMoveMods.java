package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.connector.HtmlConnector;
import com.charlie.swgoh.connector.JsonConnector;
import com.charlie.swgoh.datamodel.json.GameUnit;
import com.charlie.swgoh.datamodel.json.Profile;
import com.charlie.swgoh.datamodel.json.Progress;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.util.FileUtil;
import com.charlie.swgoh.util.ModUtil;

import java.util.*;
import java.util.stream.Collectors;

public class RevertMoveMods extends AbstractMoveMods {

  private String moveModsFileName;
  private String progressFileName;
  private String allyCode;
  private boolean isDryRun;

  @Override
  public void setParameters(String... parameters) {
    moveModsFileName = parameters[0];
    progressFileName = parameters[1];
    allyCode  = parameters[2];
    isDryRun = Boolean.parseBoolean(parameters[3]);
  }

  @Override
  protected void doProcess() throws Exception {
    List<Mod> modList = HtmlConnector.getModsFromHTML(moveModsFileName);
    Progress progress = JsonConnector.readProgressFromFile(progressFileName);

    // Build the character list for which the mods must be restored
    Set<String> characters = new LinkedHashSet<>();
    modList.stream()
            .map(Mod::getCharacter)
            .forEach(characters::add);
    modList.stream()
            .map(Mod::getFromCharacter)
            .filter(s -> !s.isEmpty())
            .forEach(characters::add);

    // Build the mapping between unit ID and unit name for mod conversion
    Map<String, String> unitIdMap = progress.getGameUnits().stream().collect(Collectors.toMap(GameUnit::getBaseID, GameUnit::getName));

    // Get the original mods for all the characters
    Profile profile = progress.getProfiles().stream()
            .filter(p -> allyCode.equals(p.getAllyCode()))
            .findFirst()
            .orElseThrow(() -> new ProcessException("Ally code " + allyCode + " not found in Progress file"));
    Map<String, List<Mod>> originalModsMap = profile.getMods().stream()
            .filter(jsonMod -> jsonMod.getCharacterID() != null)
            .map(jsonMod -> ModUtil.convertToXmlMod(jsonMod, unitIdMap))
            .collect(Collectors.groupingBy(Mod::getCharacter, LinkedHashMap::new, Collectors.toList()));

    // Now build the move mod map
    Map<String, List<Mod>> modMap = new LinkedHashMap<>();
    characters.forEach(character -> modMap.put(character, originalModsMap.get(character)));

    FileUtil.FileComponents fileComponents = FileUtil.getFileComponents(moveModsFileName);

    perform(fileComponents, modMap, isDryRun);
  }

}
