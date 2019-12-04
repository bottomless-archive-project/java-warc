package com.morethanheroic.warc.service.record;

import com.morethanheroic.warc.service.content.domain.DefaultContentBlock;
import com.morethanheroic.warc.service.content.domain.WarcContentBlock;
import com.morethanheroic.warc.service.content.request.RequestContentBlockFactory;
import com.morethanheroic.warc.service.content.response.ResponseContentBlockFactory;
import com.morethanheroic.warc.service.header.HeaderParser;
import com.morethanheroic.warc.service.record.domain.WarcRecord;
import com.morethanheroic.warc.service.record.domain.WarcRecordType;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.http.NameValuePair;
import org.apache.http.message.HeaderGroup;

@Slf4j
public class WarcRecordFactory {

  private final ResponseContentBlockFactory responseContentBlockFactory =
      new ResponseContentBlockFactory(new HeaderParser());

  private final RequestContentBlockFactory requestContentBlockFactory =
      new RequestContentBlockFactory();

  /**
   * Creates a WARC record with specified WARC Headers.
   *
   * @param warcHeaders WARC Headers of the WARC record
   * @param contentBlockStream Content block stream
   */
  public WarcRecord createWarcRecord(final HeaderGroup warcHeaders,
      final BoundedInputStream contentBlockStream) {
    WarcRecordType type = null;
    WarcContentBlock warcContentBlock = null;
    if (warcHeaders != null) {
      type = WarcRecordType.valueOf(
          warcHeaders.getFirstHeader("WARC-Type").getValue().toUpperCase());
    }
    try {
      if (type == WarcRecordType.RESPONSE) {
        warcContentBlock = responseContentBlockFactory.newResponseContentBlock(contentBlockStream);
      } else if (type == WarcRecordType.REQUEST) {
        warcContentBlock = requestContentBlockFactory.createWarcRecord(contentBlockStream);
      } else {
        warcContentBlock = new DefaultContentBlock(contentBlockStream);
      }
    } catch (IOException e) {
      log.debug("WARNING: cannot parse content block of WARC record " +
          warcHeaders.getFirstHeader("WARC-Record-ID").getValue());
    }

    return WarcRecord.builder()
        .type(type)
        .headers(parseHeaders(warcHeaders))
        .warcContentBlock(warcContentBlock)
        .build();
  }

  private Map<String, String> parseHeaders(final HeaderGroup warcHeaders) {
    if (warcHeaders == null) {
      return Collections.emptyMap();
    }

    return Arrays.stream(warcHeaders.getAllHeaders())
        .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
  }
}
