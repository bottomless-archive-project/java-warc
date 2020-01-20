package com.github.bottomlessarchive.warc.service.record.domain;

import com.github.bottomlessarchive.warc.service.content.domain.WarcContentBlock;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import lombok.Builder;

/**
 * Basic constituent of a WARC file. Contains all information of a WARC record.
 *
 * @see <a href="https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#file-and-record-model">
 * https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#file-and-record-model</a>
 */
@Builder
@SuppressWarnings("unused")
public class WarcRecord<T extends WarcContentBlock> {

    private final WarcRecordType type;
    private final Map<String, String> headers;
    private final T warcContentBlock;

    /**
     * Returns the WARC record's {@link WarcContentBlock}. The returned content block may refer to different classes
     * based on the type of the WARC record.
     *
     * @return the content block of a WARC record
     */
    public T getContentBlock() {
        return warcContentBlock;
    }

    /**
     * Returns the WARC record's {@link WarcContentBlock}. The returned content block may refer to different classes
     * based on the type of the WARC record.
     *
     * @return the content block of a WARC record
     */
    @Deprecated(since = "1.1.0", forRemoval = true)
    public T getWarcContentBlock() {
        return warcContentBlock;
    }

    /**
     * Returns the type of a WARC record.
     *
     * @return the type of the record
     * @see <a href="https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#warc-type-mandatory">
     * https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#warc-type-mandatory</a>
     */
    public WarcRecordType getType() {
        return type;
    }

    /**
     * Returns WARC-Record-ID of a WARC record. WARC-Record-ID is An identifier assigned to the
     * current record that is globally unique for intended amount of time WARC-Record-ID is a
     * mandatory field of record WARC header
     *
     * @return WARC-Record-ID string if possible. Returns null when WARC headers does not contain
     * WARC-Record-ID field
     */
    public Optional<String> getRecordId() {
        return Optional.ofNullable(headers.get("WARC-Record-ID"));
    }

    public String getHeader(final String headerName) {
        return headers.get(headerName);
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public boolean isRequest() {
        return type == WarcRecordType.REQUEST;
    }

    public boolean isResponse() {
        return type == WarcRecordType.RESPONSE;
    }

    public boolean isWarcInfo() {
        return type == WarcRecordType.WARCINFO;
    }

    public boolean isResource() {
        return type == WarcRecordType.RESOURCE;
    }

    public boolean isMetadata() {
        return type == WarcRecordType.METADATA;
    }

    public boolean isRevisit() {
        return type == WarcRecordType.REVISIT;
    }

    public boolean isConversion() {
        return type == WarcRecordType.CONVERSION;
    }

    public boolean isContinuation() {
        return type == WarcRecordType.CONTINUATION;
    }

    @Deprecated(since = "1.1.0", forRemoval = true)
    public boolean isWarcinfo() {
        return type == WarcRecordType.WARCINFO;
    }
}
