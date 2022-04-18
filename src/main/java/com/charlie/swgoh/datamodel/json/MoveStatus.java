package com.charlie.swgoh.datamodel.json;

import java.util.List;

public class MoveStatus {

  private List<String> attention;
  private List<String> done;
  private List<String> toProcess;

  public List<String> getAttention() {
    return attention;
  }

  public void setAttention(List<String> attention) {
    this.attention = attention;
  }

  public List<String> getDone() {
    return done;
  }

  public void setDone(List<String> done) {
    this.done = done;
  }

  public List<String> getToProcess() {
    return toProcess;
  }

  public void setToProcess(List<String> toProcess) {
    this.toProcess = toProcess;
  }

}
