package com.charlie.swgoh.datamodel;

import java.util.function.Function;

public enum InputType {

  XML(ModStatUnit::getOptimizerXmlText),
  GAME(ModStatUnit::getGameText);

  private final Function<ModStatUnit, String> modStatUnitText;

  InputType(Function<ModStatUnit, String> modStatUnitText) {
    this.modStatUnitText = modStatUnitText;
  }

  public Function<ModStatUnit, String> getModStatUnitText() {
    return modStatUnitText;
  }

}
