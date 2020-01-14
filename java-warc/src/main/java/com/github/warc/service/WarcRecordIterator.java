package com.github.warc.service;

import com.github.warc.service.record.domain.WarcRecord;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WarcRecordIterator implements Iterator<WarcRecord> {

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

    final Optional<WarcRecord> warcRecord = warcReader.readRecord();
    hasNextValue = warcRecord.isPresent();
    nextValue = warcRecord.orElse(null);
  }
}
