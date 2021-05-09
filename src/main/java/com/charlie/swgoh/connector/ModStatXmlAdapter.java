package com.charlie.swgoh.connector;

import com.charlie.swgoh.datamodel.InputType;
import com.charlie.swgoh.datamodel.ModStat;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class ModStatXmlAdapter extends XmlAdapter<String, ModStat> {

  @Override
  public ModStat unmarshal(String string) throws Exception {
    return new ModStat(string, InputType.XML);
  }

  @Override
  public String marshal(ModStat modStat) throws Exception {
    return modStat.toString();
  }

}
