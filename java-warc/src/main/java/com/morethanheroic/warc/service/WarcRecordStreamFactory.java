package com.morethanheroic.warc.service;

import com.morethanheroic.warc.service.record.domain.WarcRecord;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WarcRecordStreamFactory {

    public static Stream<WarcRecord> streamOf(final URL url) {
        try {
            return streamOf(new AvailableInputStream(new BufferedInputStream(url.openStream())),
                WarcReader.DEFAULT_CHARSET, true);
        } catch (IOException e) {
            throw new WarcNetworkException("Unable to open WARC location: " + url + "!", e);
        }
    }

    public static Stream<WarcRecord> streamOf(final InputStream warcFileLocation) {
        return streamOf(warcFileLocation, WarcReader.DEFAULT_CHARSET);
    }

    public static Stream<WarcRecord> streamOf(final InputStream warcFileLocation, final Charset charset) {
        return streamOf(new BufferedInputStream(warcFileLocation), charset, true);
    }

    public static Stream<WarcRecord> streamOf(final InputStream inputStream, final Charset charset,
        final boolean compressed) {
        final WarcReader warcReader = new WarcReader(inputStream, charset, compressed);

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
            new SafeWarcRecordIterator(warcReader), Spliterator.ORDERED | Spliterator.NONNULL), false);
    }
}
