package com.charlie.swgoh.datamodel;

import com.charlie.swgoh.util.FileUtil;

public class MoveFile {

  public enum Status {
    NEW("New"),
    MOVING("Moving"),
    MOVED("Moved"),
    REVERTING("Reverting"),
    REVERTED("Reverted"),
    REVERTING_ALL("Reverting - All"),
    REVERTED_ALL("Reverted - All"),
    UNKNOWN("Unknown");

    private final String label;

    Status(String label) {
      this.label = label;
    }

    @Override
    public String toString() {
      return this.label;
    }
  }

  private final FileUtil.FileComponents fileComponents;
  private final Status status;

  public MoveFile(FileUtil.FileComponents fileComponents, Status status) {
    this.fileComponents = fileComponents;
    this.status = status;
  }

  public String getName() {
    return fileComponents.getFileName();
  }

  public FileUtil.FileComponents getFileComponents() {
    return fileComponents;
  }

  public Status getStatus() {
    return status;
  }

}
