package com.charlie.swgoh.connector;

import com.charlie.swgoh.datamodel.ModStat;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ModStatXmlAdapter extends XmlAdapter<String, ModStat> {

  @Override
  public ModStat unmarshal(String string) throws Exception {
    return new ModStat(string);
  }

  @Override
  public String marshal(ModStat modStat) throws Exception {
    return modStat.toString();
  }

}
