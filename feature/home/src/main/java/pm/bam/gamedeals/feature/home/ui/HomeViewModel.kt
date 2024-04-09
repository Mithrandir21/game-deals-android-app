package pm.bam.gamedeals.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pm.bam.gamedeals.common.mapDelayAtLeast
import pm.bam.gamedeals.common.onError
import pm.bam.gamedeals.domain.models.Deal
import pm.bam.gamedeals.domain.models.Store
import pm.bam.gamedeals.domain.repositories.deals.DealsRepository
import pm.bam.gamedeals.domain.repositories.stores.StoresRepository
import pm.bam.gamedeals.feature.deal.ui.DealBottomSheetData
import pm.bam.gamedeals.logging.Logger
import pm.bam.gamedeals.logging.fatal
import javax.inject.Inject

internal const val LIMIT_DEALS = 10
internal val topStores = listOf(1, 11, 3, 23, 15, 27, 7, 21, 2)

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val storesRepository: StoresRepository,
    private val dealsRepository: DealsRepository,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenData())
    val uiState: StateFlow<HomeScreenData> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeScreenData()
    )

    init {
        viewModelScope.launch {
            loadTopStoreDataFlow()
                .collect { _uiState.emit(it) }
        }
    }

    fun loadTopStoresDeals() =
        viewModelScope.launch {
            loadTopStoreDataFlow()
                .onStart { emit(_uiState.value.copy(state = HomeScreenStatus.LOADING)) }
                .collect { _uiState.emit(it) }
        }

    private fun loadTopStoreDataFlow() =
        flow { emitAll(storesRepository.observeStores()) }
            .map { listOfStores -> listOfStores.filter { topStores.contains(it.storeID) } }
            .map { listOfStores -> listOfStores.map { it to dealsRepository.getStoreDeals(it.storeID, LIMIT_DEALS) } }
            .map {
                val data = mutableListOf<HomeScreenListData>()

                it.forEach { (store, deals) ->
                    data.add(HomeScreenListData.StoreData(store))
                    data.addAll(deals.map { deal -> HomeScreenListData.DealData(deal) })
                    data.add(HomeScreenListData.ViewAllData(store))
                }

                return@map data
            }
            .map { HomeScreenData(state = HomeScreenStatus.SUCCESS, items = it) }
            .onError { fatal(logger, it) }
            .catch { emit(HomeScreenData(state = HomeScreenStatus.ERROR)) }

    fun loadDealDetails(deal: Deal) =
        viewModelScope.launch {
            flowOf(_uiState.value)
                .mapDelayAtLeast(750) { data ->
                    val dealDetails = dealsRepository.getDeal(deal.dealID)

                    data.copy(
                        dealDetailsData = DealBottomSheetData.DealDetailsData(
                            store = storesRepository.getStore(deal.storeID),
                            gameName = deal.title,
                            dealId = deal.dealID,
                            gameSalesPriceDenominated = deal.salePriceDenominated,
                            gameInfo = dealDetails.gameInfo,
                            cheapestPrice = dealDetails.cheapestPrice,
                            cheaperStores = dealDetails.cheaperStores.map {
                                storesRepository.getStore(it.storeID) to it
                            }
                        )
                    )
                }
                .onStart {
                    _uiState.value.copy(
                        dealDetailsData = DealBottomSheetData.DealDetailsLoading(
                            store = storesRepository.getStore(deal.storeID),
                            gameName = deal.title,
                            dealId = deal.dealID,
                            gameSalesPriceDenominated = deal.salePriceDenominated
                        )
                    ).let { emit(it) }
                }
                .onError { fatal(logger, it) }
                .catch {
                    dismissDealDetails()
                    emit(HomeScreenData(state = HomeScreenStatus.ERROR))
                }
                .collect { _uiState.emit(it) }
        }

    fun dismissDealDetails() =
        viewModelScope.launch {
            _uiState.value
                .copy(dealDetailsData = null)
                .let { _uiState.emit(it) }
        }


    internal data class HomeScreenData(
        val state: HomeScreenStatus = HomeScreenStatus.LOADING,
        val items: List<HomeScreenListData> = emptyList(),
        val dealDetailsData: DealBottomSheetData? = null
    )

    internal enum class HomeScreenStatus {
        LOADING, ERROR, SUCCESS
    }

    internal sealed class HomeScreenListData {
        data class StoreData(val store: Store) : HomeScreenListData()
        data class DealData(val deal: Deal) : HomeScreenListData()
        data class ViewAllData(val store: Store) : HomeScreenListData()
    }
}