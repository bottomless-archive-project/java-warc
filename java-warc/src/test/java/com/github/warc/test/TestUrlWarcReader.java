package com.github.warc.test;

import com.github.warc.service.WarcRecordStreamFactory;
import com.github.warc.service.content.response.domain.ResponseContentBlock;
import com.github.warc.service.record.domain.WarcRecord;
import java.net.URL;

public class TestUrlWarcReader {

  public static void main(final String... arg) throws Exception {
    final URL warcUrl = new URL(
        "https://commoncrawl.s3.amazonaws.com/crawl-data/CC-MAIN-2018-43/segments/1539583508988.18/warc/CC-MAIN-20181015080248-20181015101748-00000.warc.gz");

    WarcRecordStreamFactory.streamOf(warcUrl)
        .filter(WarcRecord::isResponse)
        .map(entry -> ((ResponseContentBlock) entry.getWarcContentBlock()).getPayloadAsString())
        .forEach(System.out::println);
  }
}
