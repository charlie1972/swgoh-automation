package com.charlie.swgoh.main;

import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.automation.process.AbstractProcess;
import com.charlie.swgoh.automation.process.TestProcess;

public class MainTestProcess {

  public static void main(String[] args) {
    Configuration.loadProperties();
    AbstractProcess process = new TestProcess();
    process.process();
    System.exit(0);
  }

}
