package com.github.bottomlessarchive.warc.service;

import com.github.bottomlessarchive.warc.service.content.domain.WarcContentBlock;
import com.github.bottomlessarchive.warc.service.record.domain.WarcRecord;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;

@SuppressWarnings("unused")
public class WarcRecordIteratorFactory {

    private static final int ONE_MEGABYTE_IN_BYTE = 1048576;

    public static <T extends WarcContentBlock> Iterator<WarcRecord<T>> iteratorOf(final URL url) {
        try {
            return iteratorOf(new AvailableInputStream(new BufferedInputStream(url.openStream(), ONE_MEGABYTE_IN_BYTE)),
                    WarcReader.DEFAULT_CHARSET, true);
        } catch (IOException e) {
            throw new WarcNetworkException("Unable to open WARC location: " + url + "!", e);
        }
    }

    public static <T extends WarcContentBlock> Iterator<WarcRecord<T>> iteratorOf(final InputStream inputStream) {
        return iteratorOf(inputStream, WarcReader.DEFAULT_CHARSET);
    }

    public static <T extends WarcContentBlock> Iterator<WarcRecord<T>> iteratorOf(final InputStream inputStream, final Charset charset) {
        return iteratorOf(new BufferedInputStream(inputStream, ONE_MEGABYTE_IN_BYTE), charset, true);
    }

    public static <T extends WarcContentBlock> Iterator<WarcRecord<T>> iteratorOf(final InputStream inputStream, final Charset charset,
            final boolean compressed) {
        final WarcReader warcReader = new WarcReader(inputStream, charset, compressed);

        return (Iterator<WarcRecord<T>>) new WarcRecordIterator(warcReader);
    }
}