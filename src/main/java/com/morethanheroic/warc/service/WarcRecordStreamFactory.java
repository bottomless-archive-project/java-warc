package com.morethanheroic.warc.service;

import com.morethanheroic.warc.service.record.domain.WarcRecord;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WarcRecordStreamFactory {

  public static Stream<WarcRecord> streamOf(final URL url)
      throws IOException {
    return streamOf(new AvailableInputStream(url.openStream()), WarcReader.DEFAULT_CHARSET, true);
  }

  public static Stream<WarcRecord> streamOf(final InputStream warcFileLocation)
      throws IOException {
    return streamOf(warcFileLocation, WarcReader.DEFAULT_CHARSET, true);
  }

  public static Stream<WarcRecord> streamOf(final InputStream warcFileLocation,
      final Charset charset) throws IOException {
    return streamOf(warcFileLocation, charset, true);
  }

  public static Stream<WarcRecord> streamOf(final InputStream warcFileLocation,
      final Charset charset, final boolean compressed) throws IOException {
    final WarcReader warcReader = new WarcReader(warcFileLocation, charset, compressed);

    final Iterator<WarcRecord> iterator = new Iterator<>() {

      private boolean preloadingDone = false;
      private WarcRecord nextRecord;

      @Override
      public boolean hasNext() {
        if (!preloadingDone) {
          preloadNextRecord();
        }

        return nextRecord != null;
      }

      @Override
      public WarcRecord next() {
        if (!preloadingDone) {
          preloadNextRecord();
        }

        preloadingDone = false;

        if (nextRecord == null) {
          throw new NoSuchElementException();
        }

        return nextRecord;
      }

      private void preloadNextRecord() {
        preloadingDone = true;

        nextRecord = readRecord(warcReader);
      }
    };

    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
        iterator, Spliterator.ORDERED | Spliterator.NONNULL), false);
  }

  private static WarcRecord readRecord(final WarcReader warcReader) {
    try {
      return warcReader.readRecord();
    } catch (IOException e) {
      throw new RuntimeException("Unable to read WARC record!", e);
    }
  }
}
