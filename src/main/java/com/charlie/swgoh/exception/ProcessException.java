package com.charlie.swgoh.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessException extends RuntimeException {

  private static final Logger LOG = LoggerFactory.getLogger(ProcessException.class);

  public ProcessException(String message) {
    super(message);
    LOG.error(message);
  }

}
