package com.mixnode.warcreader.record;

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

  private final WarcType type;
  private final Map<String, String> headers;
  protected WarcContentBlock warcContentBlock;

  /**
   * WarcType specifies the type of a WARC record. 'WARC-Type' field is mandatory for all WARC
   * records WARC records unrecognized type will cause an exception
   */
  public enum WarcType {
    /**
     * 'warcinfo record contains some information about following WARC records
     */
    warcinfo,
    /**
     * Request WARC record contains a complete scheme specific (HTTP, HTTPS, etc.) request
     */
    request,
    /**
     * Response WARC record contains a scheme-specific response. The most common use if "response"
     * is for HTTP/HTTPS response
     */
    response,
    /**
     * Resource WARC record contains a resource without HTTP/HTTPS wrapping
     */
    resource,
    /**
     * Metadata WARC record usually describes feature of another WARC-Record specified by
     * 'WARC-Concurrent-To header' or 'WARC-Refers-To' WARC headers
     */
    metadata,
    revisit,
    conversion,
    segmentation;
  }

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
   * @see type
   */
  public WarcType getType() {
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
}
