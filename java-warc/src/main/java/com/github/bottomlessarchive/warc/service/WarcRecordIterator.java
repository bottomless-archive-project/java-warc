package com.github.bottomlessarchive.warc.service;

import com.github.bottomlessarchive.warc.service.content.domain.WarcContentBlock;
import com.github.bottomlessarchive.warc.service.record.domain.WarcRecord;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WarcRecordIterator<T extends WarcContentBlock> implements Iterator<WarcRecord<T>> {

    private final WarcReader warcReader;

    private boolean preloadDone;
    private boolean hasNextValue;
    private WarcRecord<T> nextValue;

    @Override
    public boolean hasNext() {
        if (!preloadDone) {
            preloadNextRecord();
        }

        return hasNextValue;
    }

    @Override
    public WarcRecord<T> next() {
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

        final Optional<WarcRecord<T>> warcRecord = warcReader.readRecord()
                .map(warcRecord1 -> (WarcRecord<T>) warcRecord1);
        hasNextValue = warcRecord.isPresent();
        nextValue = warcRecord.orElse(null);
    }
}
