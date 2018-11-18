package com.morethanheroic.warc.service;

import com.morethanheroic.warc.service.record.domain.WarcRecord;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WarcRecordStreamFactory {

  public Stream<WarcRecord> stream(final InputStream warcFileLocation)
      throws IOException {
    return stream(warcFileLocation, WarcReader.DEFAULT_CHARSET, true);
  }

  public Stream<WarcRecord> stream(final InputStream warcFileLocation, final String charset)
      throws IOException {
    return stream(warcFileLocation, charset, true);
  }

  public Stream<WarcRecord> stream(final InputStream warcFileLocation, final String charset,
      final boolean compressed) throws IOException {
    final WarcReader warcReader = new WarcReader(warcFileLocation, charset, compressed);

    final Iterator<WarcRecord> iterator = new Iterator<>() {

      private boolean hasNext = true;

      @Override
      public boolean hasNext() {
        return hasNext;
      }

      @Override
      public WarcRecord next() {
        final WarcRecord warcRecord = readRecord(warcReader);

        if (warcRecord == null) {
          hasNext = false;

          try {
            warcReader.close();
          } catch (IOException e) {
            throw new RuntimeException("Unable to close WARC file!", e);
          }
        }

        return warcRecord;
      }
    };

    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
        iterator, Spliterator.ORDERED | Spliterator.NONNULL), false);
  }

  private WarcRecord readRecord(final WarcReader warcReader) {
    try {
      return warcReader.readRecord();
    } catch (IOException e) {
      throw new RuntimeException("Unable to read WARC record!", e);
    }
  }
}
