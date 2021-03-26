package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.automation.Configuration;

public abstract class AbstractProcess implements IProcess {

  @Override
  public void process() throws Exception{
    Configuration.configure();
    init();
    doProcess();
  }

  protected abstract void doProcess() throws Exception;

}
