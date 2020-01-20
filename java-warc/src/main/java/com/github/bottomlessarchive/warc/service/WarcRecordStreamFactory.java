package com.github.bottomlessarchive.warc.service;

import com.github.bottomlessarchive.warc.service.content.domain.WarcContentBlock;
import com.github.bottomlessarchive.warc.service.record.domain.WarcRecord;
import com.github.bottomlessarchive.warc.service.record.domain.WarcRecordType;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("unused")
public class WarcRecordStreamFactory {

    private static final List<WarcRecordType> EVERY_WARC_RECORD_TYPE = Arrays.asList(WarcRecordType.values());

    public static <T extends WarcContentBlock> Stream<WarcRecord<T>> streamOf(@NotNull @NonNull final URL url) {
        return WarcRecordStreamFactory.streamOf(url, EVERY_WARC_RECORD_TYPE);
    }

    public static <T extends WarcContentBlock> Stream<WarcRecord<T>> streamOf(@NotNull @NonNull final URL url,
            @NotNull @NonNull final List<WarcRecordType> requiredRecordTypes) {
        try {
            return streamOf(new AvailableInputStream(new BufferedInputStream(url.openStream())),
                    WarcReader.DEFAULT_CHARSET, true, requiredRecordTypes);
        } catch (IOException e) {
            throw new WarcNetworkException("Unable to open WARC location: " + url + "!", e);
        }
    }

    public static <T extends WarcContentBlock> Stream<WarcRecord<T>> streamOf(@NotNull @NonNull final InputStream warcFileLocation) {
        return streamOf(warcFileLocation, EVERY_WARC_RECORD_TYPE);
    }

    public static <T extends WarcContentBlock> Stream<WarcRecord<T>> streamOf(@NotNull @NonNull final InputStream warcFileLocation,
            @NotNull @NonNull final List<WarcRecordType> requiredRecordTypes) {
        return streamOf(warcFileLocation, WarcReader.DEFAULT_CHARSET, requiredRecordTypes);
    }

    public static <T extends WarcContentBlock> Stream<WarcRecord<T>> streamOf(@NotNull @NonNull final InputStream warcFileLocation,
            @NotNull @NonNull final Charset charset) {
        return streamOf(new BufferedInputStream(warcFileLocation), charset, true, EVERY_WARC_RECORD_TYPE);
    }

    public static <T extends WarcContentBlock> Stream<WarcRecord<T>> streamOf(@NotNull @NonNull final InputStream warcFileLocation,
            @NotNull @NonNull final Charset charset, @NotNull @NonNull final List<WarcRecordType> requiredRecordTypes) {
        return streamOf(new BufferedInputStream(warcFileLocation), charset, true, requiredRecordTypes);
    }

    public static <T extends WarcContentBlock> Stream<WarcRecord<T>> streamOf(@NotNull @NonNull final InputStream inputStream,
            @NotNull @NonNull final Charset charset, final boolean compressed) {
        return streamOf(inputStream, charset, compressed, EVERY_WARC_RECORD_TYPE);
    }

    public static <T extends WarcContentBlock> Stream<WarcRecord<T>> streamOf(
            @NotNull @NonNull final InputStream inputStream,
            @NotNull @NonNull final Charset charset, final boolean compressed,
            @NotNull @NonNull final List<WarcRecordType> requiredRecordTypes) {
        final WarcReader warcReader = new WarcReader(inputStream, charset, compressed);

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                new SafeWarcRecordIterator(warcReader), Spliterator.ORDERED | Spliterator.NONNULL), false)
                .filter(warcRecord -> requiredRecordTypes.contains(warcRecord.getType()))
                .map(warcRecord -> ((WarcRecord<T>) warcRecord));
    }
}
