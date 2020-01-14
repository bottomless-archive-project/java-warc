package com.github.warc.service.record.domain;

import com.github.warc.service.content.domain.WarcContentBlock;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;

/**
 * Basic constituent of a WARC file, consisting of a sequence of WARC records. Class WarcRecord
 * contains all information of a WARC record. WarcRecord consists of
 * <ul>
 * <li> Protocol of the WARC record
 * <li> WARC record headers
 * <ul>
 * <li> WARC-Type
 * <li> WARC-Record-ID
 * <li> WARC-Date
 * <li> Content-Length
 * </ul>
 * <li> WARC record content block
 * </ul>
 */
@Builder
public class WarcRecord {

  private final WarcRecordType type;
  private final Map<String, String> headers;
  private final WarcContentBlock warcContentBlock;

  /**
   * Returns the WARC record's {@link WarcContentBlock}. The returned content block may refer to
   * different classes based on the type of the WARC record.
   *
   * @return the WarcContentBlock object of a WARC record
   */
  public WarcContentBlock getWarcContentBlock() {
    return warcContentBlock;
  }


  /**
   * Returns WARC-Type of a WARC record
   *
   * @return WARC-Type
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

  public boolean isWarcinfo() {
    return type == WarcRecordType.WARCINFO;
  }
}
