package com.morethanheroic.warc.service;

import com.morethanheroic.warc.service.record.domain.WarcRecord;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
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

  public static Stream<WarcRecord> streamOf(final InputStream inputStream,
      final Charset charset, final boolean compressed) throws IOException {
    final WarcReader warcReader = new WarcReader(inputStream, charset, compressed);

    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
        new WarcRecordIterator(warcReader), Spliterator.ORDERED | Spliterator.NONNULL), false);
  }
}
