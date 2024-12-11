package com.alekso.dltstudio.tests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.alekso.dltstudio.ui.TabsPanel
import dltstudio.composeapp.generated.resources.Res
import dltstudio.composeapp.generated.resources.tab_logs
import dltstudio.composeapp.generated.resources.tab_timeline
import kotlinx.coroutines.test.runTest
import org.jetbrains.compose.resources.getString
import org.junit.Rule
import org.junit.Test

class UIJVMTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `test tabs presence`() = runTest {
        val tabLogs = getString(Res.string.tab_logs)
        val tabTimeline = getString(Res.string.tab_timeline)
        rule.setContent {
            TabsPanel(
                0,
                listOf(tabLogs, tabTimeline)
            ) { }
        }

        rule.onNodeWithText(tabLogs).assertExists("Logs tab doesn't exist")
        rule.onNodeWithText(tabTimeline).assertExists("Timeline tab doesn't exist")
    }
}