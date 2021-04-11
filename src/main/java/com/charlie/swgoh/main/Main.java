package com.charlie.swgoh.main;

import com.charlie.swgoh.automation.process.AbstractProcess;
import com.charlie.swgoh.automation.process.TestProcess;

public class Main {

  public static void main(String[] args) {
    AbstractProcess process = new TestProcess();
    process.process();
    System.exit(0);
  }

}
