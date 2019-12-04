package com.morethanheroic.warc.service;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * This class is a hack to bypass a bug in the {@link GZIPInputStream}. More info is available at
 * <a href="https://stackoverflow.com/questions/41400810/gzipinputstream-closes-prematurely-when-decompressing-httpinputstream">here</a>.
 */
@RequiredArgsConstructor
public class AvailableInputStream extends InputStream {

  private final InputStream inputStream;

  public int read() throws IOException {
    return inputStream.read();
  }

  public int read(byte[] b) throws IOException {
    return inputStream.read(b);
  }

  public int read(byte[] b, int off, int len) throws IOException {
    return inputStream.read(b, off, len);
  }

  public void close() throws IOException {
    inputStream.close();
  }

  public int available() throws IOException {
    // Always say that we have 1 more byte in the
    // buffer, even when we don't
    int a = inputStream.available();
    if (a == 0) {
      return (1);
    } else {
      return (a);
    }
  }
}
