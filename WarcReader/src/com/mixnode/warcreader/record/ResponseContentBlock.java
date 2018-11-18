package com.mixnode.warcreader.record;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

/**
 * An implementation of WarcContentBlock interface to handle contents block's of WARC responses.
 */
@Builder
public class ResponseContentBlock implements WarcContentBlock {

  @Getter
  private final int statusCode;

  @Getter
  private final InputStream payload;

  private final Map<String, String> headers;

  public String getHeader(final String headerName) {
    return headers.get(headerName);
  }

  public Map<String, String> getHeaders() {
    return Collections.unmodifiableMap(headers);
  }
}
