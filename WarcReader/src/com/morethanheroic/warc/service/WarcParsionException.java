package com.morethanheroic.warc.service;

public class WarcParsionException extends RuntimeException {

  public WarcParsionException(final String message) {
    super(message);
  }

  public WarcParsionException(final String message, final Throwable e) {
    super(message, e);
  }
}
