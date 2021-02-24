package com.charlie.swgoh.datamodel.json;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Profile {

  private String allyCode;
  private String playerName;
  private List<Mod> mods;

  @JsonGetter
  public String getAllyCode() {
    return allyCode;
  }

  @JsonSetter
  public void setAllyCode(String allyCode) {
    this.allyCode = allyCode;
  }

  @JsonGetter
  public String getPlayerName() {
    return playerName;
  }

  @JsonSetter
  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  @JsonGetter
  public List<Mod> getMods() {
    return mods;
  }

  @JsonSetter
  public void setMods(List<Mod> mods) {
    this.mods = mods;
  }

  // Other attributes here
  private Map<String, Object> properties = new HashMap<>();

  @JsonAnySetter
  public void add(String key, Object value) {
    properties.put(key, value);
  }

  @JsonAnyGetter
  public Map<String, Object> getProperties() {
    return properties;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Profile profile = (Profile) o;
    return Objects.equals(getAllyCode(), profile.getAllyCode()) && Objects.equals(getPlayerName(), profile.getPlayerName()) && Objects.equals(getMods(), profile.getMods()) && Objects.equals(getProperties(), profile.getProperties());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getAllyCode(), getPlayerName(), getMods(), getProperties());
  }

}
