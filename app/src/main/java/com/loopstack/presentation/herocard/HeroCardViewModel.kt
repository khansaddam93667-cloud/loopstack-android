package com.loopstack.presentation.herocard

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loopstack.domain.model.LoopbackStatus
import com.loopstack.domain.model.ModelSelection
import com.loopstack.domain.usecase.ObserveLoopbackHealthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HeroCardUiState {
    data object Checking : HeroCardUiState
    data class Active(val latency: Long, val model: ModelSelection) : HeroCardUiState
    data class Degraded(val fallbackModel: ModelSelection) : HeroCardUiState
    data object Inactive : HeroCardUiState
}

sealed interface HeroCardEvent {
    data class ExpandDropdown(val expanded: Boolean) : HeroCardEvent
    data class SwitchModel(val modelName: String) : HeroCardEvent
}

@HiltViewModel
class HeroCardViewModel @Inject constructor(
    private val observeLoopbackHealthUseCase: ObserveLoopbackHealthUseCase,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    companion object {
        val MODEL_SELECTION_KEY = stringPreferencesKey("model_selection")
        const val DEFAULT_MODEL = "Default Model"
    }

    private val eventChannel = Channel<HeroCardEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    val uiState: StateFlow<HeroCardUiState> = combine(
        observeLoopbackHealthUseCase(),
        dataStore.data.map { preferences ->
            ModelSelection(preferences[MODEL_SELECTION_KEY] ?: DEFAULT_MODEL)
        }
    ) { status, modelSelection ->
        when (status) {
            is LoopbackStatus.Active -> HeroCardUiState.Active(latency = 42L, model = ModelSelection(status.providerName))
            LoopbackStatus.Degraded -> HeroCardUiState.Degraded(fallbackModel = modelSelection)
            LoopbackStatus.Inactive -> HeroCardUiState.Inactive
            else -> HeroCardUiState.Checking // Should not reach here typically for this enum but needed for completeness if we had a Checking state from use case
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HeroCardUiState.Checking
    )

    fun onEvent(event: HeroCardEvent) {
        viewModelScope.launch {
            when (event) {
                is HeroCardEvent.ExpandDropdown -> {
                    eventChannel.send(event)
                }
                is HeroCardEvent.SwitchModel -> {
                    dataStore.edit { preferences ->
                        preferences[MODEL_SELECTION_KEY] = event.modelName
                    }
                    eventChannel.send(event)
                }
            }
        }
    }
}
