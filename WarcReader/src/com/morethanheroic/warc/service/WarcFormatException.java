package com.morethanheroic.warc.service;


public class WarcFormatException extends RuntimeException {

  public WarcFormatException(final String message) {
    super(message);
  }

  public WarcFormatException(final String message, final Throwable e) {
    super(message, e);
  }
}
