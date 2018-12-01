package com.morethanheroic.warc.service;

import com.morethanheroic.warc.service.record.domain.WarcRecord;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;

public class WarcRecordIteratorFactory {

    public static Iterator<WarcRecord> iteratorOf(final URL url) throws IOException {
        return iteratorOf(new AvailableInputStream(url.openStream()), WarcReader.DEFAULT_CHARSET, true);
    }

    public static Iterator<WarcRecord> iteratorOf(final InputStream inputStream) throws IOException {
        return iteratorOf(inputStream, WarcReader.DEFAULT_CHARSET, true);
    }

    public static Iterator<WarcRecord> iteratorOf(final InputStream inputStream, final Charset charset)
            throws IOException {
        return iteratorOf(inputStream, charset, true);
    }

    public static Iterator<WarcRecord> iteratorOf(final InputStream inputStream, final Charset charset,
            final boolean compressed) throws IOException {
        final WarcReader warcReader = new WarcReader(inputStream, charset, compressed);

        return new WarcRecordIterator(warcReader);
    }
}