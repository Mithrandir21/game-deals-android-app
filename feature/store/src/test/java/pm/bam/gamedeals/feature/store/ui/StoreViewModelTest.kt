package pm.bam.gamedeals.feature.store.ui

import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pm.bam.gamedeals.domain.models.Store
import pm.bam.gamedeals.domain.repositories.deals.DealsRepository
import pm.bam.gamedeals.domain.repositories.stores.StoresRepository
import pm.bam.gamedeals.testing.MainCoroutineRule
import pm.bam.gamedeals.testing.TestingLoggingListener
import pm.bam.gamedeals.testing.utils.observeEmissions
import pm.bam.gamedeals.testing.utils.second

@OptIn(ExperimentalCoroutinesApi::class)
class StoreViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val storesRepository: StoresRepository = mockk()

    private val dealsRepository: DealsRepository = mockk()

    private lateinit var viewModel: StoreViewModel

    @Before
    fun setup() {
        viewModel = StoreViewModel(TestingLoggingListener(), dealsRepository, storesRepository)
    }

    @Test
    fun `initially store details is null`() = runTest {
        val emissions = viewModel.storeDetails.observeEmissions(this.backgroundScope, mainCoroutineRule.testDispatcher)
        assertEquals(1, emissions.size)
        assertNull(emissions.first())
    }

    @Test
    fun `setting storeId loads StoreDetails`() = runTest {
        val storeId = 1
        val store: Store = mockk()

        coEvery { storesRepository.getStore(storeId) } returns store

        val emissions = viewModel.storeDetails.observeEmissions(this.backgroundScope, mainCoroutineRule.testDispatcher)


        assertEquals(1, emissions.size)
        assertNull(emissions.first())

        viewModel.setStoreId(storeId)

        assertEquals(2, emissions.size)
        assertNull(emissions.first())
        assertEquals(store, emissions.second())
    }

    @Test
    fun `setting storeId failure - throws caught error`() = runTest {
        val storeId = 1
        val exception: Exception = mockk {
            every { printStackTrace() } just runs
        }

        coEvery { storesRepository.getStore(222) } throws exception
        coEvery { storesRepository.getStore(storeId) } throws exception

        val emissions = viewModel.storeDetails.observeEmissions(this.backgroundScope, mainCoroutineRule.testDispatcher)


        assertEquals(1, emissions.size)
        assertNull(emissions.first())

        viewModel.setStoreId(storeId)

        assertEquals(1, emissions.size)
        assertNull(emissions.first())
    }
}