package com.morethanheroic.warc.service;

import java.io.IOException;

public class WarcFormatException extends IOException {

  public WarcFormatException(String message) {
    super(message);
  }

  public WarcFormatException(String message, Throwable e) {
    super(message, e);
  }
}
