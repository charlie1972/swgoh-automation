package com.charlie.swgoh.datamodel.json;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Progress {

  private List<Profile> profiles;

  @JsonGetter
  public List<Profile> getProfiles() {
    return profiles;
  }

  @JsonSetter
  public void setProfiles(List<Profile> profiles) {
    this.profiles = profiles;
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
    Progress progress = (Progress) o;
    return Objects.equals(getProfiles(), progress.getProfiles()) && Objects.equals(getProperties(), progress.getProperties());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getProfiles(), getProperties());
  }

}
