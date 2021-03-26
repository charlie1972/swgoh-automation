package com.charlie.swgoh.automation.process;

public interface IProcess {

  default void setParameters(String... parameters) {}

  void process() throws Exception;

  void init();

}
