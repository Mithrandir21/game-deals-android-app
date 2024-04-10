package pm.bam.gamedeals.feature.store.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

@HiltViewModel
internal class StoreViewModel @Inject constructor(
    private val logger: Logger,
    private val dealsRepository: DealsRepository,
    private val storesRepository: StoresRepository
) : ViewModel() {

    // We store and react to the StoreId changes so that only a single 'deals' flow can exists
    private val storeIdFlow = MutableStateFlow<Int?>(null)

    private val _storeDetails = MutableStateFlow<Store?>(null)
    val storeDetails: StateFlow<Store?> = _storeDetails.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _dealDetails = MutableStateFlow<DealBottomSheetData?>(null)
    val dealDealDetails: StateFlow<DealBottomSheetData?> = _dealDetails.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    init {
        viewModelScope.launch {
            storeIdFlow
                .filterNotNull() // Skip our initial null value
                .distinctUntilChanged() // Skip fetching if storeId is the same, like on orientation change
                .map { storesRepository.getStore(it) }
                .catch { logger.fatalThrowable(it) }
                .collect { _storeDetails.emit(it) }
        }
    }

    fun setStoreId(storeId: Int) = storeIdFlow.update { storeId }

    @OptIn(ExperimentalCoroutinesApi::class)
    val deals = storeIdFlow
        .filterNotNull() // Skip our initial null value
        .distinctUntilChanged() // Skip fetching if storeId is the same, like on orientation change
        .flatMapLatest { dealsRepository.getPagingStoreDeals(it) }
        // cachedIn() shares the paging state across multiple consumers of posts,
        // e.g. different generations of UI across rotation config change
        .cachedIn(viewModelScope)
        .catch { logger.fatalThrowable(it) }

    fun loadDealDetails(deal: Deal) {
        viewModelScope.launch {
            flowOf(true)
                .mapDelayAtLeast(750) {
                    val dealDetails = dealsRepository.getDeal(deal.dealID)
                    val store = storesRepository.getStore(deal.storeID)

                    DealBottomSheetData.DealDetailsData(
                        store = store,
                        gameName = deal.title,
                        dealId = deal.dealID,
                        gameSalesPriceDenominated = deal.salePriceDenominated,
                        gameInfo = dealDetails.gameInfo,
                        cheapestPrice = dealDetails.cheapestPrice,
                        cheaperStores = dealDetails.cheaperStores.map { store to it }
                    )
                }
                .onStart<DealBottomSheetData> {
                    DealBottomSheetData.DealDetailsLoading(
                        store = storesRepository.getStore(deal.storeID),
                        gameName = deal.title,
                        dealId = deal.dealID,
                        gameSalesPriceDenominated = deal.salePriceDenominated
                    ).let { emit(it) }
                }
                .onError { fatal(logger, it) }
                .catch { dismissDealDetails() }
                .collect { _dealDetails.emit(it) }
        }
    }


    fun dismissDealDetails() {
        viewModelScope.launch {
            _dealDetails.emit(null)
        }
    }

}