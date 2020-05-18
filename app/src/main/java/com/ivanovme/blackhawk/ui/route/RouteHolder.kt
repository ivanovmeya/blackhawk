package com.ivanovme.blackhawk.ui.route

import com.ivanovme.blackhawk.ui.route.model.Route
import com.ivanovme.blackhawk.ui.route.model.RoutePoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class RouteHolder {

    private val routeStream = BehaviorSubject.create<Route>()

    fun updateDeparture(departure: RoutePoint) {
        routeStream.onNext(
            Route(
                departure = departure,
                destination = routeStream.value?.destination
            )
        )
    }

    fun updateDestination(destination: RoutePoint?) {
        routeStream.onNext(
            Route(
                departure = routeStream.value?.departure,
                destination = destination
            )
        )
    }

    fun getRouteUpdates(): Observable<Route> {
        return routeStream
            .distinctUntilChanged()
            .replay(1)
            .autoConnect()
    }
}