package com.morethanheroic.warc.service.header;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.http.HttpMessage;
import org.apache.http.NameValuePair;

public class HeaderParser {

  public Map<String, String> parseHeaders(final HttpMessage message) {
    return Arrays.stream(message.getAllHeaders())
        .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
  }
}
