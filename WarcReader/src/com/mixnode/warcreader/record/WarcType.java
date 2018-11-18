package com.mixnode.warcreader.record;

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
