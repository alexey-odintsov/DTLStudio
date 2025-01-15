package com.alekso.dltstudio.logs.insights

import com.alekso.dltstudio.model.LogMessage
import kotlinx.coroutines.flow.MutableStateFlow

internal data class CompiledRule(
    val regex: Regex,
    val groupNames: List<String>,
)

class InsightsRepository {
    private val insightRules = MutableStateFlow<List<LogInsightRule>>(emptyList())
    private val compiledRules = mutableMapOf<String, CompiledRule>()
    private val extractGroupRegex = "\\?\\<(.*?)\\>".toRegex()

    init {
        loadInsights()
    }

    private fun loadInsights() {
        insightRules.value = listOf()
        compiledRules.clear()

        // todo: load insights from persistent storage
        insightRules.value = listOf(
            LogInsightRule(
                "TimeStamp",
                "Possible TimeStamp found: {ts}",
                """.*(?<ts>[0-9]{10}).*"""
            ),
            LogInsightRule(
                "Choreographer: Skipped frames",
                "Process {pid} was doing heavy work on the main thread. This led to skipped {frames} frames.\rRecommendation is to check whether the app is doing some heavy operations on the main thread and offload them",
                "Choreographer\\[(?<pid>\\d+)\\].*kipped (?<frames>\\d+) frames.*main thread"
            ),
        )

        // Compile rules
        insightRules.value.forEach {
            val groupsNames =
                extractGroupRegex.findAll(it.pattern).map { it.groups[1]?.value }.filterNotNull()
                    .toList()
            compiledRules[it.name] = CompiledRule(it.pattern.toRegex(), groupsNames)
        }
    }

    fun findInsight(logMessage: LogMessage): List<LogInsight> {
        val insights = mutableListOf<LogInsight>()
        insightRules.value.forEach { rule ->
            val compiledRule = compiledRules[rule.name]
            val text = logMessage.dltMessage.payloadText()
            if (compiledRule != null && text != null && text.contains(compiledRule.regex)) {
                val matches = compiledRule.regex.find(text)!!
                insights.add(LogInsight(name = rule.name, text = fillInsightText(rule, matches)))
            }
        }
        return insights
    }

    private fun fillInsightText(rule: LogInsightRule, matches: MatchResult): String {
        var result = rule.template
        compiledRules[rule.name]?.groupNames?.forEach { groupName ->
            result = result.replace("{$groupName}", matches.groups[groupName]?.value ?: "")
        }
        return result
    }

}
