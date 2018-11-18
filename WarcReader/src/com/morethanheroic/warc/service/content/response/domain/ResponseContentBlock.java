package com.morethanheroic.warc.service.content.response.domain;

import com.morethanheroic.warc.service.WarcParsionException;
import com.morethanheroic.warc.service.WarcReader;
import com.morethanheroic.warc.service.content.domain.WarcContentBlock;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.io.IOUtils;

/**
 * An implementation of WarcContentBlock interface to handle contents block's of WARC responses.
 */
@Builder
public class ResponseContentBlock implements WarcContentBlock {

  /**
   * The HTTP status code of the response.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status">
   * https://developer.mozilla.org/en-US/docs/Web/HTTP/Status</a>
   */
  @Getter
  private final int statusCode;

  /**
   * The payload of the content block. Contains the data sent back as response body by the server.
   */
  @Getter
  private final InputStream payload;

  /**
   * The mime type of the response.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types">
   * https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types</a>
   */
  @Getter
  private final String mimeType;

  private final Charset charset;
  private final Map<String, String> headers;

  /**
   * Return a value of a header from the response.
   *
   * @param headerName the name of the header to get the value for
   * @return the value of the header
   */
  public Optional<String> getHeader(final String headerName) {
    return Optional.ofNullable(headers.get(headerName));
  }

  /**
   * Return all of the headers of a WARC response.
   *
   * @return the headers of the response
   */
  public Map<String, String> getHeaders() {
    return Collections.unmodifiableMap(headers);
  }

  /**
   * The charset of the response. If the mime type of the response is not text then this field is
   * null.
   */
  public Optional<Charset> getCharset() {
    return Optional.ofNullable(charset);
  }

  /**
   * Return the payload as a {@link String} instance. After this method is called consider the
   * payload of this content block fully read. Any further read on the payload will raise an
   * exception.
   *
   * @return the payload of the content block as string
   */
  public String getPayloadAsString() {
    final Charset charset = this.charset != null ? this.charset : Charset.forName(WarcReader.DEFAULT_CHARSET);

    try {
      return IOUtils.toString(payload, charset);
    } catch (IOException e) {
      throw new WarcParsionException("Unable to parse the payload of a WARC document!", e);
    }
  }
}
