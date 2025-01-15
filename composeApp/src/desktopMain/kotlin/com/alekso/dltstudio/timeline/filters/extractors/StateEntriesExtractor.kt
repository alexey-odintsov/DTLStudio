package com.alekso.dltstudio.timeline.filters.extractors

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.timeline.TimeLineStateEntry
import com.alekso.dltstudio.timeline.filters.NO_KEY
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor.*

class StateEntriesExtractor : EntriesExtractor {

    override fun extractEntry(
        message: DLTMessage,
        regex: Regex,
        extractionType: ExtractionType,
    ): List<TimeLineStateEntry> {
        val matches = regex.find(message.payloadText() ?: "")!!
        val list = mutableListOf<TimeLineStateEntry>()

        when (extractionType) {
            ExtractionType.NamedGroupsOneEntry -> {
                val key: String = matches.groups[Param.KEY.value]?.value ?: NO_KEY
                val value: String? = matches.groups[Param.VALUE.value]?.value
                val oldValue: String? = matches.groups[Param.OLD_VALUE.value]?.value

                if (value != null && oldValue != null) {
                    list.add(
                        TimeLineStateEntry(
                            message.timeStampNano,
                            key,
                            Pair(value, oldValue)
                        )
                    )
                }
            }

            ExtractionType.NamedGroupsManyEntries -> throw UnsupportedOperationException()

            ExtractionType.GroupsManyEntries -> {
                if (matches.groups.size > 2) {
                    val key = matches.groups[INDEX_KEY + 1]?.value
                    val value = matches.groups[INDEX_VALUE + 1]?.value
                    val oldValue = matches.groups[INDEX_OLD_VALUE + 1]?.value
                    if (key != null && value != null && oldValue != null) {
                        list.add(
                            TimeLineStateEntry(message.timeStampNano, key, Pair(value, oldValue))
                        )
                    }
                }
            }
        }

        return list
    }

    companion object {
        const val INDEX_KEY = 0
        const val INDEX_VALUE = 1
        const val INDEX_OLD_VALUE = 2
    }
}
