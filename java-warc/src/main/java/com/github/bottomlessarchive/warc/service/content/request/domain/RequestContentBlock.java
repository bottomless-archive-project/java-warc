package com.github.bottomlessarchive.warc.service.content.request.domain;

import com.github.bottomlessarchive.warc.service.content.domain.WarcContentBlock;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Builder
public class RequestContentBlock implements WarcContentBlock {

    /**
     * the http method used in the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods">
     * https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods</a>
     */
    @Getter
    private final String method;

    /**
     * The target location of the request.
     */
    @Getter
    private final String location;

    /**
     * The payload of the request.
     */
    @Getter
    private final InputStream payload;

    /**
     * The protocol used for the request.
     */
    @Getter
    private final String protocol;

    /**
     * The major protocol version of the request.
     */
    @Getter
    private final int majorProtocolVersion;

    /**
     * The minor protocol version of the request.
     */
    @Getter
    private final int minorProtocolVersion;

    private final Map<String, String> headers;

    /**
     * Return a value of a header from the request.
     *
     * @param headerName the name of the header to get the value for
     * @return the value of the header
     */
    public Optional<String> getHeader(final String headerName) {
        return Optional.ofNullable(headers.get(headerName));
    }

    /**
     * Return all of the headers of a WARC request.
     *
     * @return the headers of the response
     */
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}
