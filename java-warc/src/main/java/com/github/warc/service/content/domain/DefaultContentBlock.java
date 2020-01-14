package com.github.warc.service.content.domain;

import java.io.InputStream;

/**
 * A simple implementation of a WarcContentBlock for Most of WARC-Types.
 *
 * @author Hadi Jooybar
 */
public class DefaultContentBlock implements WarcContentBlock {

  protected InputStream payload;

  /**
   * DefaultContentBlock constructor
   *
   * @param input Input stream for content block.
   */
  public DefaultContentBlock(final InputStream input) {
    payload = input;
  }

  /**
   * Return content block stream as payload
   *
   * @return payload stream
   */
  public InputStream getPayload() {
    return payload;
  }

  @Override
  public String toString() {
    return payload.toString();
  }
}
