package com.morethanheroic.warc.service.content.response;

import com.morethanheroic.warc.service.WarcFormatException;
import com.morethanheroic.warc.service.content.response.domain.ResponseContentBlock;
import com.morethanheroic.warc.service.header.HeaderParser;
import java.io.IOException;
import java.nio.charset.Charset;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.io.DefaultHttpResponseParser;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.IdentityInputStream;
import org.apache.http.impl.io.SessionInputBufferImpl;

/**
 * This class is responsible for creating new {@link ResponseContentBlock} instances.
 */
@RequiredArgsConstructor
public class ResponseContentBlockFactory {

  private static final int BUFFER_SIZE = 1024;

  private final HeaderParser headerParser;

  /**
   * Create a {@link ResponseContentBlock} from a content block stream of a response WARC entry.
   *
   * @param stream the response WARC entry's content block stream
   * @return the newly created content block
   */
  public ResponseContentBlock newResponseContentBlock(final BoundedInputStream stream)
      throws IOException {
    final SessionInputBufferImpl buffer = new SessionInputBufferImpl(new HttpTransportMetricsImpl(),
        BUFFER_SIZE, 0, null, null);

    buffer.bind(stream);
    final DefaultHttpResponseParser responseParser = new DefaultHttpResponseParser(buffer);
    final HttpResponse response;
    try {
      response = responseParser.parse();
    } catch (HttpException e) {
      throw new WarcFormatException("Can't parse the response", e);
    }
    final BasicHttpEntity entity = new BasicHttpEntity();
    entity.setContent(new IdentityInputStream(buffer));
    Header contentTypeHeader = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
    if (contentTypeHeader != null) {
      entity.setContentType(contentTypeHeader);
    }
    response.setEntity(entity);

    final String mimeType = ContentType.get(entity).getMimeType();
    final Charset charset = ContentType.get(entity).getCharset();

    return ResponseContentBlock.builder()
        .headers(headerParser.parseHeaders(response))
        .mimeType(mimeType)
        .charset(charset)
        .statusCode(response.getStatusLine().getStatusCode())
        .payload(response.getEntity().getContent())
        .build();
  }
}
