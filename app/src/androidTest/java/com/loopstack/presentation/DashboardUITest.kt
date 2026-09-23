package com.loopstack.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import com.loopstack.MainActivity
import com.loopstack.core.theme.LoopStackTheme
import com.loopstack.domain.model.ModelSelection
import com.loopstack.presentation.dashboard.ToolCardModel
import com.loopstack.presentation.herocard.HeroCard
import com.loopstack.presentation.herocard.HeroCardUiState
import com.loopstack.presentation.toolgrid.ToolGrid
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class DashboardUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun heroCard_displaysTermuxActive_whenStatusIsActive() {
        composeTestRule.setContent {
            LoopStackTheme {
                HeroCard(uiState = HeroCardUiState.Active(latency = 42L, model = ModelSelection("Test Model")))
            }
        }

        composeTestRule.onNodeWithText("Termux Active").assertIsDisplayed()
    }

    @Test
    fun heroCard_displaysDegraded_whenStatusIsDegraded() {
        composeTestRule.setContent {
            LoopStackTheme {
                HeroCard(uiState = HeroCardUiState.Degraded(fallbackModel = ModelSelection("Fallback Model")))
            }
        }

        composeTestRule.onNodeWithText("Degraded").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fallback Model").assertIsDisplayed()
    }

    @Test
    fun toolGrid_rendersExactly8Items() {
        val tools = List(8) { index ->
            ToolCardModel(
                id = UUID.randomUUID().toString(),
                title = "Tool $index",
                subtitle = "Subtitle $index"
            )
        }

        composeTestRule.setContent {
            LoopStackTheme {
                ToolGrid(tools = tools)
            }
        }

        for (i in 0 until 8) {
            composeTestRule.onNodeWithText("Tool $i").assertIsDisplayed()
        }
    }
}
