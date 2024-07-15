package pm.bam.gamedeals.feature.freetogame.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pm.bam.gamedeals.common.onError
import pm.bam.gamedeals.domain.models.FreeGame
import pm.bam.gamedeals.domain.models.FreeGameSearchParameters
import pm.bam.gamedeals.domain.repositories.free.FreeGamesRepository
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.logging.fatal
import javax.inject.Inject


@HiltViewModel
internal class FreeGameViewModel @Inject constructor(
    private val logger: Logger,
    private val freeGamesRepository: FreeGamesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FreeGameScreenData())
    val uiState: StateFlow<FreeGameScreenData> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FreeGameScreenData()
    )


    init {
        viewModelScope.launch {
            flow { emitAll(freeGamesRepository.observeFreeGames()) }
                .map { FreeGameScreenData(status = FreeGameScreenStatus.SUCCESS, freeGames = it) }
                .onError { fatal(logger, it) }
                .catch { emit(_uiState.value.copy(status = FreeGameScreenStatus.ERROR)) }
                .collect { _uiState.emit(it) }
        }
    }

    fun reloadFreeGames() {
        viewModelScope.launch {
            flow { emit(_uiState.value.copy(status = FreeGameScreenStatus.LOADING)) }
                .onStart { freeGamesRepository.refreshFreeGames() }
                .onError { fatal(logger, it) }
                .catch { emit(_uiState.value.copy(status = FreeGameScreenStatus.ERROR)) }
                .collect { _uiState.emit(it) }
        }
    }

    fun loadFreeGames(parameters: FreeGameSearchParameters) {
        viewModelScope.launch {
            flow { emitAll(freeGamesRepository.observeFreeGames(parameters)) }
                .map { FreeGameScreenData(status = FreeGameScreenStatus.SUCCESS, freeGames = it) }
                .onError { fatal(logger, it) }
                .catch { emit(_uiState.value.copy(status = FreeGameScreenStatus.ERROR)) }
                .collect { _uiState.emit(it) }
        }
    }


    data class FreeGameScreenData(
        val status: FreeGameScreenStatus = FreeGameScreenStatus.LOADING,
        val freeGames: List<FreeGame> = emptyList()
    )


    internal enum class FreeGameScreenStatus {
        LOADING, ERROR, SUCCESS
    }
}