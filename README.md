# Java WARC

This library makes reading WARC files in Java extremely easy. It is open source and compatible with the latest WARC standard (<a href="https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.0/">  WARC-1.1 </a>).

## Usage examples

These are basic usage examples for the library. When the library was created the main goal was to make something easy to use and lightweight. Because of this the examples are rather short and self-describing.

### Stream a WARC file from an URL

Stream a WARC file from an URL and print the payload (response body) to the console.

 ```
final URL warcUrl = new URL(
    "https://commoncrawl.s3.amazonaws.com/crawl-data/CC-MAIN-2018-43/segments/1539583508988.18/warc/CC-MAIN-20181015080248-20181015101748-00000.warc.gz");

WarcRecordStreamFactory.streamOf(warcUrl)
    .filter(entry -> entry.getType() == WarcRecordType.RESPONSE)
    .map(entry -> ((ResponseContentBlock) entry.getWarcContentBlock()).getPayloadAsString())
    .forEach(System.out::println);
```

### Read WARC records one by one

Read WARC records from a file one by one.

 ```
 asd
 ```