package com.ivanovme.blackhawk.ui.search

import com.google.android.gms.maps.model.LatLng
import com.ivanovme.blackhawk.R
import com.ivanovme.blackhawk.network.SearchApi
import com.ivanovme.blackhawk.network.model.CityResponse
import com.ivanovme.blackhawk.ui.base.BaseViewModel
import com.ivanovme.blackhawk.ui.base.StringProvider
import com.ivanovme.blackhawk.ui.route.RouteFlowCoordinator
import com.ivanovme.blackhawk.ui.route.RouteHolder
import com.ivanovme.blackhawk.ui.route.model.RoutePoint
import com.ivanovme.blackhawk.ui.search.data.CityData
import com.ivanovme.blackhawk.ui.search.data.SearchViewState
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.IOException
import java.io.InterruptedIOException
import java.util.*
import java.util.concurrent.TimeUnit

class SearchViewModel(
    private val isDeparture: Boolean,
    private val api: SearchApi,
    private val routeHolder: RouteHolder,
    private val routeFlowCoordinator: RouteFlowCoordinator,
    private val debounceScheduler: Scheduler,
    private val stringProvider : StringProvider
) : BaseViewModel() {

    companion object {
        const val DEBOUNCE_TIMEOUT = 300L
    }

    private val eventsStream: PublishSubject<SearchEvent> = PublishSubject.create()

    val state: Observable<SearchViewState>

    private val inputTransformer = ObservableTransformer<SearchEvent, LocalChange> { upstream ->
        Observable.mergeArray(
            upstream.ofType(QueryTextChanged::class.java)
                .debounce(DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS, debounceScheduler)
                .switchMap {
                    performSearch(it.newQuery).toObservable()
                        .startWithItem(SearchLoading(it.newQuery))
                },
            upstream.ofType(EraseQueryClicked::class.java).map { ClearSearchResults },
            upstream.ofType(BackArrowPressed::class.java).flatMapCompletable {
                Completable.fromAction { routeFlowCoordinator.showRoute() }
            }.toObservable(),
            upstream.ofType(CitySelected::class.java).flatMapCompletable {
                Completable.fromAction {
                    updateRouteData(it)
                    routeFlowCoordinator.showRoute()
                }
            }.toObservable()
        )
    }

    private val reducer =
        { prev: SearchViewState, change: LocalChange ->
            println("change = $change")
            when (change) {
                is SearchSuccess -> prev.copy(
                    searchQuery = change.query,
                    searchResults = change.searchResults,
                    isLoading = false,
                    error = ""
                )
                is SearchLoading -> prev.copy(
                    searchQuery = change.newQuery,
                    isLoading = true,
                    error = ""
                )
                is SearchFailed -> prev.copy(
                    isLoading = false,
                    error = change.error,
                    searchResults = emptyList()

                )
                is ClearSearchResults -> prev.copy(
                    isLoading = false,
                    searchQuery = "",
                    searchResults = emptyList(),
                    error = ""
                )
                is EmptyChange -> prev
            }
        }

    fun postEvent(event: SearchEvent) {
        eventsStream.onNext(event)
    }

    init {
        state = eventsStream.compose(inputTransformer)
            .scan(SearchViewState.empty(), reducer)
            .distinctUntilChanged()
            .doOnSubscribe { compositeDisposable.add(it) }
            .doOnNext { println("state update $it") }
            .replay(1)
            .autoConnect()
    }

    private fun performSearch(query: String): Single<LocalChange> {
        return api
            .search(query, Locale.getDefault().language)
            .map {
                SearchSuccess(
                    query = query,
                    searchResults = it.cities.mapToItemData()
                ) as LocalChange
            }
            .onErrorReturn {
                if (it is IOException) {
                    if (it is InterruptedIOException) {
                        EmptyChange
                    } else {
                        SearchFailed(stringProvider.get(R.string.error_no_internet))
                    }
                } else {
                    SearchFailed(stringProvider.get(R.string.error_unknown))
                }
            }
    }

    private fun List<CityResponse>.mapToItemData(): List<CityData> {
        return this.filter { it.iata.isNotEmpty() }.map {
            CityData(
                city = it.city,
                country = it.country,
                airportCode = it.iata.firstOrNull() ?: "WWW",
                location = LatLng(it.location.lat, it.location.lon)
            )
        }
    }

    private fun updateRouteData(event: CitySelected) {
        if (isDeparture) {
            routeHolder.updateDeparture(
                RoutePoint(
                    city = event.cityName,
                    airportCode = event.airportCode,
                    location = event.location
                )
            )
        } else {
            routeHolder.updateDestination(
                RoutePoint(
                    city = event.cityName,
                    airportCode = event.airportCode,
                    location = event.location
                )
            )
        }
    }
}

sealed class LocalChange
data class SearchSuccess(
    val query: String,
    val searchResults: List<CityData>
) : LocalChange()

data class SearchFailed(val error: String) : LocalChange()
data class SearchLoading(val newQuery: String) : LocalChange()
object ClearSearchResults : LocalChange()
object EmptyChange : LocalChange()

sealed class SearchEvent
data class QueryTextChanged(val newQuery: String) : SearchEvent()
object EraseQueryClicked : SearchEvent()
object BackArrowPressed : SearchEvent()
data class CitySelected(val cityName: String, val airportCode: String, val location: LatLng) :
    SearchEvent()