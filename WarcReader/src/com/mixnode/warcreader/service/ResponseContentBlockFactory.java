package com.mixnode.warcreader.service;

import com.mixnode.warcreader.WarcFormatException;
import com.mixnode.warcreader.record.ResponseContentBlock;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.io.DefaultHttpResponseParser;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.IdentityInputStream;
import org.apache.http.impl.io.SessionInputBufferImpl;

public class ResponseContentBlockFactory {

  private static final int BUFFER_SIZE = 1024;

  /**
   * Create a ResponseContentBlock from a content block stream of a response WARC.
   *
   * @param stream Response WARC's content block stream
   * @return Output ContentBlock
   */
  public ResponseContentBlock createWarcRecord(final BoundedInputStream stream)
      throws IOException {
    SessionInputBufferImpl buffer = new SessionInputBufferImpl(new HttpTransportMetricsImpl(),
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

    return ResponseContentBlock.builder()
        .headers(parseHeaders(response))
        .statusCode(response.getStatusLine().getStatusCode())
        .payload(response.getEntity().getContent())
        .build();
  }

  private Map<String, String> parseHeaders(final HttpResponse response) {
    return Arrays.stream(response.getAllHeaders())
        .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
  }
}
