package com.github.warc.service;

import com.github.warc.service.record.domain.WarcRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class SafeWarcRecordIterator implements Iterator<WarcRecord> {

    private final WarcReader warcReader;

    private boolean preloadDone;
    private boolean hasNextValue;
    private WarcRecord nextValue;

    @Override
    public boolean hasNext() {
        if (!preloadDone) {
            preloadNextRecord();
        }

        return hasNextValue;
    }

    @Override
    public WarcRecord next() {
        if (!preloadDone) {
            preloadNextRecord();
        }

        preloadDone = false;

        if (!hasNextValue) {
            throw new NoSuchElementException();
        }

        return nextValue;
    }

    private void preloadNextRecord() {
        preloadDone = true;

        try {
            final Optional<WarcRecord> warcRecord = warcReader.readRecord();

            hasNextValue = warcRecord.isPresent();
            nextValue = warcRecord.orElse(null);
        } catch (WarcParsingException | WarcFormatException e) {
            log.debug("Failed to parse the next record! Skipping it!", e);

            preloadNextRecord();
        }
    }
}
