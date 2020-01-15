package com.github.bottomlessarchive.warc.test;

import com.github.bottomlessarchive.warc.service.WarcParsingException;
import com.github.bottomlessarchive.warc.service.WarcReader;
import com.github.bottomlessarchive.warc.service.content.response.domain.ResponseContentBlock;
import com.github.bottomlessarchive.warc.service.record.domain.WarcRecord;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;

public class TestFileWarcReader {

    public static void main(final String... arg) throws Exception {
        final WarcReader warcReader = new WarcReader(new FileInputStream(
            new File("C:\\warc-test\\CC-MAIN-20180716232549-20180717012549-00001.warc.gz")));

        boolean hasNext = true;
        while (hasNext) {
            try {
                final Optional<WarcRecord> optionalWarcRecord = warcReader.readRecord();

                optionalWarcRecord
                    .filter(WarcRecord::isResponse)
                    .map(warcRecord -> ((ResponseContentBlock) warcRecord.getWarcContentBlock())
                        .getPayloadAsString())
                    .ifPresent(System.out::println);

                hasNext = optionalWarcRecord.isPresent();
            } catch (WarcParsingException e) {
                e.printStackTrace();
            }
        }
    }
}
