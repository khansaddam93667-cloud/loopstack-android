package com.loopstack.presentation.herocard

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import app.cash.turbine.test
import com.loopstack.domain.model.LoopbackStatus
import com.loopstack.domain.usecase.ObserveLoopbackHealthUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.io.File
import java.util.UUID
import kotlinx.coroutines.test.advanceUntilIdle

@OptIn(ExperimentalCoroutinesApi::class)
class HeroCardViewModelTest {

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var observeLoopbackHealthUseCase: ObserveLoopbackHealthUseCase
    private lateinit var healthFlow: MutableSharedFlow<LoopbackStatus>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        observeLoopbackHealthUseCase = mock(ObserveLoopbackHealthUseCase::class.java)
        healthFlow = MutableSharedFlow()
        `when`(observeLoopbackHealthUseCase.invoke()).thenReturn(healthFlow)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test state transitions from Checking to Active to Degraded to Active`() = runTest {
        val testFile = File(tmpFolder.root, "test_${UUID.randomUUID()}.preferences_pb")
        val dataStore = PreferenceDataStoreFactory.create(
            scope = backgroundScope,
            produceFile = { testFile }
        )

        // Set initial model in data store
        dataStore.edit { prefs ->
            prefs[HeroCardViewModel.MODEL_SELECTION_KEY] = "Llama-3"
        }

        val viewModel = HeroCardViewModel(observeLoopbackHealthUseCase, dataStore)

        viewModel.uiState.test {
            // Initial state should be Checking
            val initialState = awaitItem()
            assertTrue(initialState is HeroCardUiState.Checking)

            // Emit Active
            healthFlow.emit(LoopbackStatus.Active("Local Model"))
            val activeState = awaitItem() as HeroCardUiState.Active
            assertEquals("Local Model", activeState.model.name)

            // Emit Degraded
            healthFlow.emit(LoopbackStatus.Degraded)
            val degradedState = awaitItem() as HeroCardUiState.Degraded
            assertEquals("Llama-3", degradedState.fallbackModel.name)

            // Emit Active again
            healthFlow.emit(LoopbackStatus.Active("Local Model"))
            val activeState2 = awaitItem() as HeroCardUiState.Active
            assertEquals("Local Model", activeState2.model.name)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
