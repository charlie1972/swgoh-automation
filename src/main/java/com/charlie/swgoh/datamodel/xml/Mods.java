package com.charlie.swgoh.datamodel.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "mods")
@XmlAccessorType(XmlAccessType.FIELD)
public class Mods {

  @XmlElement(name = "mod")
  private List<Mod> mods = new ArrayList<>();

  public List<Mod> getMods() {
    return mods;
  }

  public void setMods(List<Mod> mods) {
    this.mods = mods;
  }

  @Override
  public String toString() {
    return "Mods{" +
            "mods=" + mods +
            '}';
  }

}
