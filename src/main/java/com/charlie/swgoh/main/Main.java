package com.charlie.swgoh.main;

import com.charlie.swgoh.automation.process.IProcess;
import com.charlie.swgoh.automation.process.MainProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    try {
      IProcess mainProcess = new MainProcess();
      mainProcess.setParameters(args);
      mainProcess.process();
    }
    catch (Exception e) {
      LOG.error("Exception", e);
    }
    finally {
      System.exit(0);
    }
  }

}
