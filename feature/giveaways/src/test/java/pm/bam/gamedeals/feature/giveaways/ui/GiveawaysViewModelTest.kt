package pm.bam.gamedeals.feature.giveaways.ui

import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pm.bam.gamedeals.domain.models.Giveaway
import pm.bam.gamedeals.domain.models.GiveawayPlatform
import pm.bam.gamedeals.domain.models.GiveawaySearchParameters
import pm.bam.gamedeals.domain.models.GiveawaySortBy
import pm.bam.gamedeals.domain.models.GiveawayType
import pm.bam.gamedeals.domain.repositories.giveaway.GiveawaysRepository
import pm.bam.gamedeals.testing.MainCoroutineRule
import pm.bam.gamedeals.testing.TestingLoggingListener
import pm.bam.gamedeals.testing.utils.observeEmissions
import pm.bam.gamedeals.testing.utils.second

@OptIn(ExperimentalCoroutinesApi::class)
class GiveawaysViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val giveawaysRepository: GiveawaysRepository = mockk()


    @Before
    fun setup() {
    }

    @Test
    fun `initially loading`() = runTest {
        coEvery { giveawaysRepository.observeGiveaways() } returns flowOf(emptyList())
        val viewModel = GiveawaysViewModel(TestingLoggingListener(), giveawaysRepository)

        val emissions = observeStates(viewModel)
        Assert.assertEquals(2, emissions.size)
        Assert.assertEquals(GiveawaysViewModel.GiveawaysScreenStatus.LOADING, emissions.first().status)
        Assert.assertEquals(GiveawaysViewModel.GiveawaysScreenStatus.SUCCESS, emissions.second().status)
    }

    @Test
    fun `initially error`() = runTest {
        coEvery { giveawaysRepository.observeGiveaways() } throws Exception()
        val viewModel = GiveawaysViewModel(TestingLoggingListener(), giveawaysRepository)

        val emissions = observeStates(viewModel)
        Assert.assertEquals(2, emissions.size)
        Assert.assertEquals(GiveawaysViewModel.GiveawaysScreenStatus.LOADING, emissions.first().status)
    }

    @Test
    fun `reload Giveaways`() = runTest {
        coEvery { giveawaysRepository.observeGiveaways() } returns flowOf(emptyList())
        coEvery { giveawaysRepository.refreshGiveaways() } just runs
        val viewModel = GiveawaysViewModel(TestingLoggingListener(), giveawaysRepository)
        viewModel.reloadGiveaways()

        val emissions = observeStates(viewModel)
        Assert.assertEquals(1, emissions.size)

        // Loading is emitted twice, but observed only once because StateFlow emits only distinct values
        Assert.assertEquals(GiveawaysViewModel.GiveawaysScreenStatus.LOADING, emissions.first().status)
    }

    @Test
    fun `load Giveaways with search parameters`() = runTest {
        val resultOne = mockk<Giveaway>()
        val resultTwo = mockk<Giveaway>()
        val resultThree = mockk<Giveaway>()


        val para = GiveawaySearchParameters(
            types = listOf(GiveawayType.GAME to true, GiveawayType.BETA to true),
            platforms = listOf(GiveawayPlatform.PC to true, GiveawayPlatform.NINTENDO_SWITCH to true),
            sortBy = GiveawaySortBy.DATE
        )

        coEvery { giveawaysRepository.observeGiveaways() } returns flowOf(listOf(resultOne, resultTwo, resultThree))
        coEvery { giveawaysRepository.observeGiveaways(any()) } returns flowOf(listOf(resultThree, resultOne))
        val viewModel = GiveawaysViewModel(TestingLoggingListener(), giveawaysRepository)
        viewModel.loadGiveaway(para)

        val emissions = observeStates(viewModel)
        Assert.assertEquals(2, emissions.size)
        Assert.assertEquals(GiveawaysViewModel.GiveawaysScreenStatus.LOADING, emissions.first().status)
        Assert.assertEquals(GiveawaysViewModel.GiveawaysScreenStatus.SUCCESS, emissions.second().status)
        Assert.assertEquals(2, emissions.second().giveaways.size)
        Assert.assertEquals(resultThree, emissions.second().giveaways.first())
        Assert.assertEquals(resultOne, emissions.second().giveaways.second())
    }


    private fun TestScope.observeStates(viewModel: GiveawaysViewModel) =
        viewModel.uiState.observeEmissions(this.backgroundScope, mainCoroutineRule.testDispatcher)
}