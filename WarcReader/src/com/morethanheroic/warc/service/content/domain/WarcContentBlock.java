package com.morethanheroic.warc.service.content.domain;

import java.io.InputStream;

/**
 * WarcContentBlock interface represents content block of a WARC record Here is a list of known
 * implementations of this interface
 * <ul>
 * <li> RequestContentBlock
 * <li> ResponseContentBlock
 * <li> DefaultContentBlock
 * </ul>
 *
 * @author Hadi Jooybar
 */
public interface WarcContentBlock {

  /**
   * Return an InputStream of WARC payload Payload referred to, or contained by a WARC record as a
   * meaningful subset of the content block
   *
   * @return payload InputStream
   */
  InputStream getPayload();
}
