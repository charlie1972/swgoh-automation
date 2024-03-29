package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.connector.HtmlConnector;
import com.charlie.swgoh.connector.JsonConnector;
import com.charlie.swgoh.datamodel.ModSlot;
import com.charlie.swgoh.datamodel.json.GameUnit;
import com.charlie.swgoh.datamodel.json.Profile;
import com.charlie.swgoh.datamodel.json.Progress;
import com.charlie.swgoh.datamodel.xml.Mod;
import com.charlie.swgoh.exception.ProcessException;
import com.charlie.swgoh.util.FileUtil;
import com.charlie.swgoh.util.ModUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RevertMoveMods extends AbstractMoveMods {

  private final List<String> moveModsFileNames;
  private final String progressFileName;
  private final String allyCode;
  private final boolean isDryRun;

  public RevertMoveMods(List<String> moveModsFileNames, String progressFileName, String allyCode, boolean isDryRun, boolean startImmediately) {
    super(startImmediately);
    if (moveModsFileNames.isEmpty()) {
      throw new ProcessException("No mods move file");
    }
    this.moveModsFileNames = moveModsFileNames;
    this.progressFileName = progressFileName;
    this.allyCode = allyCode;
    this.isDryRun = isDryRun;
  }

  @Override
  protected String getFileNameSuffix() {
    return "revert";
  }

  @Override
  protected void doProcess() throws Exception {
    Progress progress = JsonConnector.readObjectFromFile(progressFileName, Progress.class);

    // Build the character and mod slots that must be restored
    Map<String, Set<ModSlot>> modsToRestore = new LinkedHashMap<>();
    for (String moveModsFileName : moveModsFileNames) {
      List<Mod> modList = HtmlConnector.getModsFromHTML(moveModsFileName, true);
      addSlots(modList, modsToRestore, Mod::getFromCharacter);
      addSlots(modList, modsToRestore, Mod::getCharacter);
    }

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
    modsToRestore.forEach((character, slots) -> {
      if (originalModsMap.containsKey(character)) {
        List<Mod> mods = originalModsMap.get(character).stream().filter(mod -> slots.contains(mod.getSlot())).collect(Collectors.toList());
        modMap.put(character, mods);
      }
    });

    FileUtil.FileComponents fileComponents = FileUtil.getFileComponents(moveModsFileNames.get(0));
    if (moveModsFileNames.size() > 1) {
      fileComponents = fileComponents.withFileName("__all__");
    }

    perform(fileComponents, modMap, isDryRun);
  }

  private void addSlots(List<Mod> modList, Map<String, Set<ModSlot>> modsToRestore, Function<Mod, String> characterGetter) {
    modList.forEach(mod -> {
      String character = characterGetter.apply(mod);
      if (character.isEmpty()) {
        return;
      }
      if (!modsToRestore.containsKey(character)) {
        modsToRestore.put(character, Stream.of(mod.getSlot()).collect(Collectors.toSet()));
      }
      else {
        modsToRestore.get(character).add(mod.getSlot());
      }
    });
  }

}
