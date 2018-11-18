package com.morethanheroic.warc.service;

import com.morethanheroic.warc.service.http.HttpParser;
import com.morethanheroic.warc.service.record.WarcRecordFactory;
import com.morethanheroic.warc.service.record.domain.WarcRecord;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.http.HttpException;
import org.apache.http.message.HeaderGroup;

/**
 * This class provides basic functions to read and parse a WARC file. Providing a compressed or an
 * uncompressed stream of WARC file, WarcReader reads WARC records and parses them to {@link
 * WarcRecord} objects.
 */
public class WarcReader {

  /**
   * The default {@link Charset} used by the parser when no other {@link Charset} is provided.
   */
  public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  private final WarcRecordFactory warcRecordFactory = new WarcRecordFactory();

  private final InputStream input;
  private final Charset charset;

  private BoundedInputStream lastRecordStream;

  /**
   * Create a new {@link WarcReader} and set the file on the provided {@link URL} location as the
   * data source.
   *
   * @param datasourceLocation the location of the data source to back this reader
   */
  public WarcReader(final URL datasourceLocation) throws IOException {
    this(datasourceLocation, DEFAULT_CHARSET);
  }

  /**
   * Create a new {@link WarcReader} and set the file on the provided {@link URL} location as the
   * data source.
   *
   * @param datasourceLocation the location of the data source to back this reader
   * @param charset character set for the parser
   */
  public WarcReader(final URL datasourceLocation, final Charset charset) throws IOException {
    this(datasourceLocation, charset, true);
  }

  /**
   * Create a new {@link WarcReader} and set the file on the provided {@link URL} location as the
   * data source.
   *
   * @param datasourceLocation the location of the data source to back this reader
   * @param charset character set for the parser
   * @param compressed true if the input stream is compressed, false otherwise
   */
  public WarcReader(final URL datasourceLocation, final Charset charset, final boolean compressed)
      throws IOException {
    this(compressed ? new AvailableInputStream(datasourceLocation.openStream())
        : datasourceLocation.openStream(), charset, compressed);
  }

  /**
   * Create a new {@link WarcReader} and set the provided stream as the data source.
   *
   * @param datasource the data source to back this reader
   */
  public WarcReader(final InputStream datasource) throws IOException {
    this(datasource, DEFAULT_CHARSET);
  }

  /**
   * Create a new {@link WarcReader} and set the provided stream as the data source.
   *
   * @param datasource the data source to back this reader
   * @param charset character set for the parser
   */
  public WarcReader(final InputStream datasource, final Charset charset) throws IOException {
    this(datasource, charset, true);
  }

  /**
   * Create a new {@link WarcReader} and set the provided stream as the data source.
   *
   * @param datasource the data source to back this reader
   * @param charset character set for the parser
   * @param compressed true if the input stream is compressed, false otherwise
   */
  public WarcReader(final InputStream datasource, final Charset charset, boolean compressed)
      throws IOException {
    if (compressed) {
      input = new GZIPInputStream(datasource);
    } else {
      input = datasource;
    }

    this.charset = charset;
  }

  /**
   * Read a WARC record from the provided data source. If the returned Optional is empty then the
   * reader reached the end of the data source.
   *
   * @return the freshly read WARC record
   */
  public Optional<WarcRecord> readRecord() {
    if (lastRecordStream != null) {
      try {
        lastRecordStream.skip(Long.MAX_VALUE);

        HttpParser.readLine(input, charset);
        HttpParser.readLine(input, charset);
      } catch (IOException e) {
        throw new WarcParsionException("Unable to parse the next WARC record!", e);
      }
    }

    return parse();
  }

  /**
   * This method based on the WARC format specification parses a WARC record and creates a {@link
   * WarcRecord} object.
   *
   * This function throws a {@link WarcFormatException} if the structure of an input file is
   * invalid. Explanation for parsing error is provided in the message of the exception.
   *
   * @return the parsed WARC record
   * @throws WarcFormatException when unable to parse the next record
   */
  protected Optional<WarcRecord> parse() {
    WarcRecord record;
    String protocol;
    try {
      protocol = HttpParser.readLine(input, charset);
    } catch (IOException e1) {
      throw new WarcFormatException("Illegal warc format");
    }
    if (protocol == null) {
      return Optional.empty();
    }
    if (!protocol.toLowerCase().startsWith("warc/")) {
      throw new WarcFormatException("Warc version is missing");
    }
    HeaderGroup headers = new HeaderGroup();
    try {
      headers.setHeaders(HttpParser.parseHeaders(input, charset));
    } catch (IOException | HttpException e) {
      throw new WarcFormatException("cannot parse warc headers");
    }
    try {
      long payloadSize = Long.parseLong(headers.getFirstHeader("Content-Length").getValue());
      lastRecordStream = new BoundedInputStream(input, payloadSize);

      record = warcRecordFactory.createWarcRecord(headers, lastRecordStream);
    } catch (NumberFormatException e) {
      throw new WarcFormatException("Cannot parse warc Content-Length");
    }
    return Optional.of(record);
  }
}
