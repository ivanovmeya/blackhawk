package com.ivanovme.blackhawk.ui.map

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.ivanovme.blackhawk.domain.FlightPathCreator
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class MapViewModel(
    departure: LatLng,
    destination: LatLng,
    flightPathCreator: FlightPathCreator
) : ViewModel() {

    companion object {
        private const val planeAnimationDuration = 20000L
        private const val planeAnimationDelay = 1000L
    }

    private var animationWasStarted: Boolean = false
    private var planePositionAnimator: ValueAnimator? = null

    private val planePositionStream = PublishSubject.create<PlanePosition>()
    private val mapDataStream = PublishSubject.create<MapData>()

    private var flightPathData: MapData? = null

    fun getPlanePosition(): Observable<PlanePosition> = planePositionStream
        .distinctUntilChanged()
        .replay(1)
        .autoConnect()

    fun getMapData(): Observable<MapData> = mapDataStream
        .distinctUntilChanged()
        .replay(1)
        .autoConnect()

    init {

        flightPathCreator.createPathFlight(departure, destination)
            .let {
                createPlaneAnimator(it.planePositions)
                flightPathData = it.mapData
            }
    }

    fun onMapReady() {
        mapDataStream.onNext(flightPathData)
    }

    fun startAnimation() {
        if (!animationWasStarted) {
            animationWasStarted = true
            planePositionAnimator?.start()
        }
    }

    fun pauseAnimation() {
        planePositionAnimator?.pause()
    }

    fun resumeAnimation() {
        planePositionAnimator?.resume()
    }

    private fun createPlaneAnimator(
        planeAnimationData: List<PlanePosition>
    ) {
        if (planePositionAnimator == null) {
            planePositionAnimator = ValueAnimator.ofInt(0, planeAnimationData.size - 1).apply {
                addUpdateListener {
                    planePositionStream.onNext(planeAnimationData[it.animatedValue as Int])
                }
                duration = planeAnimationDuration
                interpolator = LinearInterpolator()
                startDelay = planeAnimationDelay
            }
        }
    }
}

data class MapData(
    val bounds: LatLngBounds,
    val cameraOffset: Int,
    val flightPath: List<LatLng>,
    val debugInfoPoints: List<LatLng> = emptyList()
)

data class PlanePosition(val coordinate: LatLng, val rotationAngle: Float)