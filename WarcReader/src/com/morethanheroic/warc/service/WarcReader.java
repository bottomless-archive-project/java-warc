package com.morethanheroic.warc.service;

import com.morethanheroic.warc.service.http.HttpParser;
import com.morethanheroic.warc.service.record.domain.WarcRecord;

import com.morethanheroic.warc.service.record.WarcRecordFactory;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.input.BoundedInputStream;
import org.apache.http.HttpException;
import org.apache.http.message.HeaderGroup;

/**
 * This class provides basic functions to read and parse a WARC file. Providing a compressed or an
 * uncompressed stream of WARC file, WarcReader reads WARC records and parses them to {@link
 * WarcRecord} objects.
 */
public class WarcReader implements Closeable {

  public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  private final WarcRecordFactory warcRecordFactory = new WarcRecordFactory();

  private final InputStream input;
  private final Charset charset;

  private BoundedInputStream lastRecordStream;

  public WarcReader(final URL url) throws IOException {
    this(url, DEFAULT_CHARSET);
  }

  public WarcReader(final URL url, final Charset charset) throws IOException {
    this(url, charset, true);
  }

  public WarcReader(final URL url, final Charset charset, final boolean compressed)
      throws IOException {
    this(compressed ? new AvailableInputStream(url.openStream()) : url.openStream(), charset,
        compressed);
  }

  /**
   * Create a WarcReader object for a Compressed stream of a WARC file
   *
   * @param compressedStream Compressed input stream
   */
  public WarcReader(final InputStream compressedStream) throws IOException {
    this(compressedStream, DEFAULT_CHARSET);
  }

  /**
   * Create a WarcReader object for a Compressed stream of a WARC file with a specific charset for
   * the parser
   *
   * @param compressedStream compressedStream Input compressed stream
   * @param charset character set for the parser
   */
  public WarcReader(final InputStream compressedStream, final Charset charset) throws IOException {
    this(compressedStream, charset, true);
  }

  /**
   * Create a WarcReader object for a stream.
   *
   * @param stream Input stream
   * @param charset charset character set for the parser
   * @param compressed whether the input stream is compressed
   */
  public WarcReader(InputStream stream, final Charset charset, boolean compressed)
      throws IOException {
    if (compressed) {
      input = new GZIPInputStream(stream);
    } else {
      input = stream;
    }

    this.charset = charset;
  }

  /**
   * Read a WARC record from A WARC file By a call to this function WARC reader will skip the
   * current record. This means that any stream from current WARC record will not be accessible
   * after a new 'readRecord' call.
   *
   * @return a WARC record object
   */
  public WarcRecord readRecord() throws IOException {
    if (lastRecordStream != null) {
      lastRecordStream.skip(Long.MAX_VALUE);
      HttpParser.readLine(input, charset);
      HttpParser.readLine(input, charset);
    }

    return parse();
  }

  /**
   * Base on WARC format specification 'parse' function parses a WARC record and create a WarcRecord
   * object This function throw WarcFomatException if the structure of an input file is invalid.
   * Explanation for parse error is provided in the WarcFomatException message
   *
   * @return Output WARC record
   */
  protected WarcRecord parse() throws WarcFormatException {
    WarcRecord record;
    String protocol;
    try {
      protocol = HttpParser.readLine(input, charset);
    } catch (IOException e1) {
      throw new WarcFormatException("Illegal warc format");
    }
    if (protocol == null) {
      return null;
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
    return record;
  }

  @Override
  public void close() throws IOException {
    input.close();
  }
}
