package com.github.bottomlessarchive.warc.service.record.domain;

/**
 * Describes the various types of WARC records.
 *
 * @see <a href="https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#warc-type-mandatory">
 * https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#warc-type-mandatory</a>
 */
public enum WarcRecordType {

    /**
     * A 'warcinfo' record describes the records that follow it, up through end of file, end of input,
     * or until next 'warcinfo' record. Typically, this appears once and at the beginning of a WARC
     * file. For a web archive, it often contains information about the web crawl which generated the
     * following records.
     *
     * @see <a href="https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#warcinfo">
     * https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#warcinfo</a>
     */
    WARCINFO,

    /**
     * A 'request' record holds the details of a complete scheme-specific request, including network
     * protocol information, where possible.
     *
     * @see <a href="https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#request">
     * https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#request</a>
     */
    REQUEST,

    /**
     * A 'response' record should contain a complete scheme-specific response, including network
     * protocol information, where possible.
     *
     * @see <a href="https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#response">
     * https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#response</a>
     */
    RESPONSE,

    /**
     * A 'resource' record contains a resource, without full protocol response information. For
     * example: a file directly retrieved from a locally accessible repository or the result of a
     * networked retrieval where the protocol information has been discarded.
     *
     * @see <a href="https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#resource">
     * https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#resource</a>
     */
    RESOURCE,

    /**
     * A 'metadata' record contains content created in order to further describe, explain, or
     * accompany a harvested resource, in ways not covered by other record types. A 'metadata' record
     * will almost always refer to another record of another type, with that other record holding
     * original harvested or transformed content. (However, it is allowable for a 'metadata' record to
     * refer to any record type, including other 'metadata' records.) Any number of metadata records
     * may reference another specific record.
     *
     * @see <a href="https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#metadata">
     * https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#metadata</a>
     */
    METADATA,

    /**
     * A 'revisit' record describes the revisitation of content already archived, and might include
     * only an abbreviated content body which has to be interpreted relative to a previous record.
     * Most typically, a 'revisit' record is used instead of a 'response' or 'resource' record to
     * indicate that the content visited was either a complete or substantial duplicate of material
     * previously archived.
     *
     * @see <a href="https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#revisit">
     * https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#revisit</a>
     */
    REVISIT,

    /**
     * A 'conversion' record shall contain an alternative version of another record’s content that was
     * created as the result of an archival process. Typically, this is used to hold content
     * transformations that maintain viability of content after widely available rendering tools for
     * the originally stored format disappear. As needed, the original content may be migrated
     * (transformed) to a more viable format in order to keep the information usable with current
     * tools while minimizing loss of information (intellectual content, look and feel, etc.). Any
     * number of 'conversion' records may be created that reference a specific source record, which
     * may itself contain transformed content. Each transformation should result in a freestanding,
     * complete record, with no dependency on survival of the original record.
     *
     * @see <a href="https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#conversion">
     * https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#conversion</a>
     */
    CONVERSION,

    /**
     * Record blocks from 'continuation' records must be appended to corresponding prior record
     * block(s) (e.g. from other WARC files) to create the logically complete full-sized original
     * record. That is, 'continuation' records are used when a record that would otherwise cause a
     * WARC file size to exceed a desired limit is broken into segments.
     *
     * @see <a href="https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#continuation">
     * https://iipc.github.io/warc-specifications/specifications/warc-format/warc-1.1/#continuation</a>
     */
    CONTINUATION
}
