package com.charlie.swgoh.datamodel;

import java.util.function.Function;

public enum InputType {

  XML(ModSlot::getName, ModStatUnit::getOptimizerXmlText),
  GAME(ModSlot::getName, ModStatUnit::getGameText),
  JSON(ModSlot::getShape, ModStatUnit::getJsonString);

  private final Function<ModSlot, String> modSlotText;
  private final Function<ModStatUnit, String> modStatUnitText;

  InputType(Function<ModSlot, String> modSlotText, Function<ModStatUnit, String> modStatUnitText) {
    this.modSlotText = modSlotText;
    this.modStatUnitText = modStatUnitText;
  }

  public Function<ModSlot, String> getModSlotText() {
    return modSlotText;
  }

  public Function<ModStatUnit, String> getModStatUnitText() {
    return modStatUnitText;
  }

}
