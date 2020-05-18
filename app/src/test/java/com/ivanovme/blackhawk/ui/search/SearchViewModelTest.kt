package com.ivanovme.blackhawk.ui.search

import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import com.ivanovme.blackhawk.network.SearchApi
import com.ivanovme.blackhawk.network.model.CityResponse
import com.ivanovme.blackhawk.network.model.Location
import com.ivanovme.blackhawk.network.model.SearchResponse
import com.ivanovme.blackhawk.ui.base.StringProvider
import com.ivanovme.blackhawk.ui.route.RouteFlowCoordinator
import com.ivanovme.blackhawk.ui.route.RouteHolder
import com.ivanovme.blackhawk.ui.route.model.RoutePoint
import com.ivanovme.blackhawk.ui.search.data.CityData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.Test
import java.util.concurrent.TimeUnit

class SearchViewModelTest {

    private val testScheduler = TestScheduler()

    private lateinit var viewModel: SearchViewModel

    private val api = mock<SearchApi> {
        on { search(any(), any()) }.then { Single.just(SearchResponse(emptyList())) }
    }

    private val routeFlowCoordinator = mock<RouteFlowCoordinator> {
        on { showRoute() }.then { }
    }

    private val routeHolder = mock<RouteHolder> {
        on { updateDeparture(any()) }.then { }
        on { updateDestination(any()) }.then { }
    }

    private val stringProvider = mock<StringProvider> {
        on { get(any()) }.thenReturn("any")
    }

    @Test
    fun `start search with progress`() {
        whenever(api.search(any(), any())).thenReturn(Single.never())
        buildViewModel()
        viewModel.postEvent(QueryTextChanged("query"))
        testScheduler.advanceTimeBy(SearchViewModel.DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)
        assertThat(getState().isLoading).isTrue()
    }

    @Test
    fun `hide progress after success`() {
        whenever(api.search(any(), any())).thenReturn(Single.just(SearchResponse(emptyList())))
        buildViewModel()
        viewModel.postEvent(QueryTextChanged("query"))
        testScheduler.advanceTimeBy(SearchViewModel.DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)
        assertThat(getState().isLoading).isFalse()
    }

    @Test
    fun `hide progress after fail`() {
        whenever(api.search(any(), any())).thenReturn(Single.error(Throwable("error")))
        buildViewModel()
        viewModel.postEvent(QueryTextChanged("query"))
        testScheduler.advanceTimeBy(SearchViewModel.DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)
        assertThat(getState().isLoading).isFalse()
    }

    @Test
    fun `show error on search fail`() {
        whenever(api.search(any(), any())).thenReturn(Single.error(Throwable("error")))
        buildViewModel()
        viewModel.postEvent(QueryTextChanged("query"))
        testScheduler.advanceTimeBy(SearchViewModel.DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)
        assertThat(getState().error).isNotEmpty()
    }

    @Test
    fun `show search result`() {

        val expectedCityData = setupSearchDefaultResult()
        buildViewModel()
        viewModel.postEvent(QueryTextChanged("query"))

        testScheduler.advanceTimeBy(SearchViewModel.DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)

        assertThat(getState().searchResults).isEqualTo(listOf(expectedCityData))
    }

    @Test
    fun `update query on user typing`() {
        setupSearchDefaultResult()
        buildViewModel(isDeparture = true)

        val expectedQuery = "18_gh"
        viewModel.postEvent(QueryTextChanged(expectedQuery))

        testScheduler.advanceTimeBy(SearchViewModel.DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)

        assertThat(getState().searchQuery).isEqualTo(expectedQuery)
    }

    @Test
    fun `update route departure on city selected`() {
        buildViewModel(isDeparture = true)
        val expectedDeparture = RoutePoint(
            city = "tsk",
            airportCode = "TOF",
            location = LatLng(1.0, 1.0)
        )
        viewModel.postEvent(
            CitySelected(
                expectedDeparture.city,
                expectedDeparture.airportCode,
                expectedDeparture.location
            )
        )

        verify(routeHolder).updateDeparture(expectedDeparture)
    }

    @Test
    fun `update route destination on city selected`() {
        buildViewModel(isDeparture = false)
        val expectedDeparture = RoutePoint(
            city = "tsk",
            airportCode = "TOF",
            location = LatLng(1.0, 1.0)
        )
        viewModel.postEvent(
            CitySelected(
                expectedDeparture.city,
                expectedDeparture.airportCode,
                expectedDeparture.location
            )
        )

        verify(routeHolder).updateDestination(expectedDeparture)
    }

    @Test
    fun `close screen after destination selected`() {
        buildViewModel(isDeparture = false)
        viewModel.postEvent(CitySelected("", "", LatLng(1.0, 1.0)))
        verify(routeFlowCoordinator).showRoute()
    }

    @Test
    fun `close screen after departure selected`() {
        buildViewModel(isDeparture = true)
        viewModel.postEvent(CitySelected("", "", LatLng(1.0, 1.0)))
        verify(routeFlowCoordinator).showRoute()
    }

    @Test
    fun `close screen on back arrow pressed`() {
        buildViewModel(isDeparture = true)
        viewModel.postEvent(BackArrowPressed)
        verify(routeFlowCoordinator).showRoute()
    }

    @Test
    fun `clear search query on erase btn pressed`() {
        setupSearchDefaultResult()
        buildViewModel(isDeparture = true)

        val expectedQuery = "18_gh"
        viewModel.postEvent(QueryTextChanged(expectedQuery))

        testScheduler.advanceTimeBy(SearchViewModel.DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)

        assertThat(getState().searchQuery).isEqualTo(expectedQuery)

        viewModel.postEvent(EraseQueryClicked)

        assertThat(getState().searchQuery).isEmpty()

    }

    @Test
    fun `clear search result on erase btn pressed`() {
        setupSearchDefaultResult()
        buildViewModel(isDeparture = true)

        val expectedQuery = "18_gh"
        viewModel.postEvent(QueryTextChanged(expectedQuery))

        testScheduler.advanceTimeBy(SearchViewModel.DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)

        assertThat(getState().searchQuery).isEqualTo(expectedQuery)

        viewModel.postEvent(EraseQueryClicked)

        assertThat(getState().searchResults).isEmpty()
    }

    private fun setupSearchDefaultResult(): CityData {
        val expectedCountry = "RUS"
        val expectedAirport = "LED"
        val expectedCity = "SPB"
        val latitude = 1.0
        val longitude = 2.0
        val expectedLocation = LatLng(latitude, longitude)
        val cityResponseList = listOf(
            CityResponse(
                expectedCountry,
                Location(latitude, longitude),
                listOf(expectedAirport, expectedAirport),
                expectedCity
            )
        )
        whenever(api.search(any(), any())).thenReturn(Single.just(SearchResponse(cityResponseList)))

        return CityData(
            city = expectedCity,
            country = expectedCountry,
            airportCode = expectedAirport,
            location = expectedLocation
        )
    }

    private fun getState() = viewModel.state.test().values().last()

    private fun buildViewModel(
        isDeparture: Boolean = false
    ) {

        viewModel = SearchViewModel(
            isDeparture = isDeparture,
            api = api,
            routeHolder = routeHolder,
            routeFlowCoordinator = routeFlowCoordinator,
            debounceScheduler = testScheduler,
            stringProvider = stringProvider
        )

        viewModel.state.test()
    }
}