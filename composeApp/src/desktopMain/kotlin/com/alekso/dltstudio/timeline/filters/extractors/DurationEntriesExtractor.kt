package com.alekso.dltstudio.timeline.filters.extractors

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.timeline.TimeLineDurationEntry
import com.alekso.dltstudio.timeline.filters.NO_KEY
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor.ExtractionType
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor.Param

class DurationEntriesExtractor : EntriesExtractor {

    override fun extractEntry(
        message: DLTMessage,
        regex: Regex,
        extractionType: ExtractionType,
    ): List<TimeLineDurationEntry> {
        val matches = regex.find(message.payloadText() ?: "")!!
        val list = mutableListOf<TimeLineDurationEntry>()

        when (extractionType) {
            ExtractionType.NamedGroupsOneEntry -> {
                val key: String = matches.groups[Param.KEY.value]?.value ?: NO_KEY
                val begin: String? = matches.groups[Param.BEGIN.value]?.value
                val end: String? = matches.groups[Param.END.value]?.value

                list.add(
                    TimeLineDurationEntry(message.timeStampNano, key, Pair(begin, end))
                )
            }

            ExtractionType.GroupsManyEntries -> throw UnsupportedOperationException()
            ExtractionType.NamedGroupsManyEntries -> throw UnsupportedOperationException()
        }

        return list
    }
}
