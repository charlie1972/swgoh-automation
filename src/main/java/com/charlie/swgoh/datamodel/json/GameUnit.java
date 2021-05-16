package com.charlie.swgoh.datamodel.json;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GameUnit {

  private String baseID;
  private String name;

  @JsonGetter
  public String getBaseID() {
    return baseID;
  }

  @JsonSetter
  public void setBaseID(String baseID) {
    this.baseID = baseID;
  }

  @JsonGetter
  public String getName() {
    return name;
  }

  @JsonSetter
  public void setName(String name) {
    this.name = name;
  }

  // Other attributes here
  private final Map<String, Object> properties = new HashMap<>();

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
    GameUnit gameUnit = (GameUnit) o;
    return Objects.equals(getBaseID(), gameUnit.getBaseID()) && Objects.equals(getName(), gameUnit.getName()) && Objects.equals(getProperties(), gameUnit.getProperties());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getBaseID(), getName(), getProperties());
  }

}
