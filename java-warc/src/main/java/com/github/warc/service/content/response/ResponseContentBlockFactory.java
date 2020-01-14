package com.github.warc.service.content.response;

import com.github.warc.service.WarcFormatException;
import com.github.warc.service.content.response.domain.ResponseContentBlock;
import com.github.warc.service.header.HeaderParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.io.DefaultHttpResponseParser;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.IdentityInputStream;
import org.apache.http.impl.io.SessionInputBufferImpl;
import org.apache.http.io.SessionInputBuffer;

/**
 * This class is responsible for creating new {@link ResponseContentBlock} instances.
 */
@RequiredArgsConstructor
public class ResponseContentBlockFactory {

    private static final int BUFFER_SIZE = 1024;

    private final HeaderParser headerParser;

    /**
     * Create a {@link ResponseContentBlock} from a content block {@link InputStream} of a response
     * WARC entry.
     *
     * @param inputStream the response WARC entry's content block stream
     * @return the newly created content block
     * @throws IOException when an error happens while reading the input stream
     */
    public ResponseContentBlock newResponseContentBlock(final InputStream inputStream)
        throws IOException {
        final SessionInputBuffer buffer = buildInputBuffer(inputStream);
        final HttpResponse response = buildResponse(buffer);

        try {
            final ContentType contentType = ContentType.getLenientOrDefault(response.getEntity());

            final String mimeType = contentType.getMimeType();
            final Charset charset = contentType.getCharset();

            return ResponseContentBlock.builder()
                .headers(headerParser.parseHeaders(response))
                .mimeType(mimeType)
                .charset(charset)
                .statusCode(response.getStatusLine().getStatusCode())
                .payload(response.getEntity().getContent())
                .build();
        } catch (UnsupportedCharsetException e) {
            throw new WarcFormatException("Unable to parse WARC record! Unsupported charset found: "
                + e.getCharsetName() + "!", e);
        } catch (IllegalCharsetNameException e) {
            throw new WarcFormatException("Unable to parse WARC record! Unsupported charset found: "
                + e.getCharsetName() + "!", e);
        }
    }

    private SessionInputBuffer buildInputBuffer(final InputStream stream) {
        final SessionInputBufferImpl buffer = new SessionInputBufferImpl(new HttpTransportMetricsImpl(),
            BUFFER_SIZE, 0, null, null);

        buffer.bind(stream);

        return buffer;
    }

    private HttpResponse buildResponse(final SessionInputBuffer buffer) throws IOException {
        final DefaultHttpResponseParser responseParser = new DefaultHttpResponseParser(buffer);

        final HttpResponse response;
        try {
            response = responseParser.parse();
        } catch (HttpException e) {
            throw new WarcFormatException("Can't parse the WARC response!", e);
        }

        response.setEntity(buildEntity(buffer, response));

        return response;
    }

    private HttpEntity buildEntity(SessionInputBuffer buffer, HttpResponse response) {
        final InputStreamEntity entity = new InputStreamEntity(new IdentityInputStream(buffer));
        final Header contentTypeHeader = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
        if (contentTypeHeader != null) {
            entity.setContentType(contentTypeHeader);
        }
        return entity;
    }
}
