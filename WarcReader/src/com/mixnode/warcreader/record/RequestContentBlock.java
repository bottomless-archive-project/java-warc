package com.mixnode.warcreader.record;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Builder
public class RequestContentBlock implements WarcContentBlock {

  @Getter
  private final String method;

  @Getter
  private final String location;

  @Getter
  private final InputStream payload;

  @Getter
  private final String protocol;

  @Getter
  private final int majorProtocolVersion;

  @Getter
  private final int minorProtocolVersion;

  private final Map<String, String> headers;

  public String getHeader(final String headerName) {
    return headers.get(headerName);
  }

  public Map<String, String> getHeaders() {
    return Collections.unmodifiableMap(headers);
  }
}
