package com.github.bottomlessarchive.warc.service.content.request;

import com.github.bottomlessarchive.warc.service.WarcFormatException;
import com.github.bottomlessarchive.warc.service.content.request.domain.RequestContentBlock;
import com.github.bottomlessarchive.warc.service.header.HeaderParser;
import java.io.IOException;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.impl.io.DefaultHttpRequestParser;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.IdentityInputStream;
import org.apache.http.impl.io.SessionInputBufferImpl;

public class RequestContentBlockFactory {

    private static final int BUFFER_SIZE = 1024;

    private final HeaderParser headerParser = new HeaderParser();

    public RequestContentBlock createWarcRecord(final BoundedInputStream stream)
        throws IOException {
        SessionInputBufferImpl buffer = new SessionInputBufferImpl(new HttpTransportMetricsImpl(),
            BUFFER_SIZE, 0, null, null);
        buffer.bind(stream);
        final DefaultHttpRequestParser requestParser = new DefaultHttpRequestParser(buffer);
        final HttpRequest request;
        try {
            request = requestParser.parse();
        } catch (HttpException e) {
            throw new WarcFormatException("Can't parse the request", e);
        }

        final RequestLine requestLine = request.getRequestLine();
        final ProtocolVersion protocolVersion = requestLine.getProtocolVersion();

        return RequestContentBlock.builder()
            .method(requestLine.getMethod())
            .location(requestLine.getUri())
            .protocol(protocolVersion.getProtocol())
            .majorProtocolVersion(protocolVersion.getMajor())
            .minorProtocolVersion(protocolVersion.getMinor())
            .payload(new IdentityInputStream(buffer))
            .headers(headerParser.parseHeaders(request))
            .build();
    }
}
