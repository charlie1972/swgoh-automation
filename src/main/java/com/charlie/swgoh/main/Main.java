package com.charlie.swgoh.main;

import com.charlie.swgoh.automation.Configuration;
import com.charlie.swgoh.automation.process.AbstractProcess;
import com.charlie.swgoh.automation.process.TakeScreenshot;
import com.charlie.swgoh.automation.process.TestProcess;

public class Main {

  public static void main(String[] args) {
    Configuration.loadProperties();
//    AbstractProcess process = new TestProcess();
    AbstractProcess process = new TakeScreenshot();
    process.process();
    System.exit(0);
  }

}
