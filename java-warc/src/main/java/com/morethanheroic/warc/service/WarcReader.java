package com.morethanheroic.warc.service;

import com.morethanheroic.warc.service.http.HttpParser;
import com.morethanheroic.warc.service.record.WarcRecordFactory;
import com.morethanheroic.warc.service.record.domain.WarcRecord;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
    public static final Charset DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;

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
    public WarcReader(final URL datasourceLocation) {
        this(datasourceLocation, DEFAULT_CHARSET);
    }

    /**
     * Create a new {@link WarcReader} and set the file on the provided {@link URL} location as the
     * data source.
     *
     * @param datasourceLocation the location of the data source to back this reader
     * @param charset            character set for the parser
     */
    public WarcReader(final URL datasourceLocation, final Charset charset) {
        this(datasourceLocation, charset, true);
    }

    /**
     * Create a new {@link WarcReader} and set the file on the provided {@link URL} location as the
     * data source. The default timeout value for connecting to the {@link URL} is 120 seconds.
     *
     * @param datasourceLocation the location of the data source to back this reader
     * @param charset            character set for the parser
     * @param compressed         true if the input stream is compressed, false otherwise
     */
    public WarcReader(final URL datasourceLocation, final Charset charset, final boolean compressed) {
        this(buildConnection(datasourceLocation), charset, compressed);
    }

    /**
     * Create a new {@link WarcReader} and set the file on the provided {@link URLConnection} as the
     * data source.
     *
     * @param datasourceConnection the location of the data source to back this reader
     * @param charset              character set for the parser
     * @param compressed           true if the input stream is compressed, false otherwise
     */
    public WarcReader(final URLConnection datasourceConnection, final Charset charset, final boolean compressed) {
        this(compressed ? new AvailableInputStream(openConnection(datasourceConnection))
            : openConnection(datasourceConnection), charset, compressed);
    }

    /**
     * Create a new {@link WarcReader} and set the provided stream as the data source.
     *
     * @param datasource the data source to back this reader
     */
    public WarcReader(final InputStream datasource) {
        this(datasource, DEFAULT_CHARSET);
    }

    /**
     * Create a new {@link WarcReader} and set the provided stream as the data source.
     *
     * @param datasource the data source to back this reader
     * @param charset    character set for the parser
     */
    public WarcReader(final InputStream datasource, final Charset charset) {
        this(datasource, charset, true);
    }

    /**
     * Create a new {@link WarcReader} and set the provided stream as the data source.
     *
     * @param datasource the data source to back this reader
     * @param charset    character set for the parser
     * @param compressed true if the input stream is compressed, false otherwise
     */
    public WarcReader(final InputStream datasource, final Charset charset, boolean compressed) {
        if (compressed) {
            try {
                input = new GZIPInputStream(datasource);
            } catch (IOException e) {
                throw new WarcNetworkException("Unable to open WARC input stream!", e);
            }
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
                throw new WarcParsingException("Unable to parse the next WARC record!", e);
            }
        }

        return parse();
    }

    /**
     * This method based on the WARC format specification parses a WARC record and creates a {@link
     * WarcRecord} object.
     * <p>
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
            throw new WarcFormatException("Cannot parse warc headers");
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

    private static URLConnection buildConnection(final URL datasourceLocation) {
        try {
            final URLConnection datasourceConnection = datasourceLocation.openConnection();

            datasourceConnection.setConnectTimeout(120000);
            datasourceConnection.setReadTimeout(120000);

            return datasourceConnection;
        } catch (IOException e) {
            throw new WarcNetworkException("Unable to open WARC input stream!", e);
        }
    }

    private static InputStream openConnection(final URLConnection datasourceConnection) {
        try {
            return datasourceConnection.getInputStream();
        } catch (IOException e) {
            throw new WarcNetworkException("Unable to open WARC input stream!", e);
        }
    }
}
