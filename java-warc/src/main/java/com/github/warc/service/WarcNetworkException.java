package com.github.warc.service;

public class WarcNetworkException extends RuntimeException {

    public WarcNetworkException(final String message, final Throwable e) {
        super(message, e);
    }
}
