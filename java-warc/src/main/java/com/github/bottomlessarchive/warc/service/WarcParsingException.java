package com.github.bottomlessarchive.warc.service;

public class WarcParsingException extends RuntimeException {

    public WarcParsingException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
