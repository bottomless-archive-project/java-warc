package com.github.warc.service;

public class WarcParsingException extends RuntimeException {

    public WarcParsingException(final String message) {
        super(message);
    }

    public WarcParsingException(final String message, final Throwable e) {
        super(message, e);
    }
}
