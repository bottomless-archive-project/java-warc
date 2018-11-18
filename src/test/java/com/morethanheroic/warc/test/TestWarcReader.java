package com.morethanheroic.warc.test;

import com.morethanheroic.warc.service.WarcRecordStreamFactory;
import com.morethanheroic.warc.service.content.response.domain.ResponseContentBlock;
import java.net.URL;

public class TestWarcReader {

  public static void main(final String[] arg) throws Exception {
    final URL warcInputStream = new URL(
        "https://commoncrawl.s3.amazonaws.com/crawl-data/CC-MAIN-2018-43/segments/1539583508988.18/warc/CC-MAIN-20181015080248-20181015101748-00000.warc.gz");

    WarcRecordStreamFactory.streamOf(warcInputStream)
        .filter(entry -> entry.getWarcContentBlock() instanceof ResponseContentBlock)
        .map(entry -> ((ResponseContentBlock) entry.getWarcContentBlock()).getPayloadAsString())
        .forEach(System.out::println);
  }
}
