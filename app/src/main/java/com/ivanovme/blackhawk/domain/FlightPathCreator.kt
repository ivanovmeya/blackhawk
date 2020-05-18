package com.ivanovme.blackhawk.domain

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.ivanovme.blackhawk.ui.map.MapData
import com.ivanovme.blackhawk.ui.map.PlanePosition
import com.ivanovme.blackhawk.ui.utils.toPx

class FlightPathCreator(private val bezierCurveCreator: BezierCurveCreator) {

    fun createPathFlight(departure: LatLng, destination: LatLng): PlanePathData {
        val (points, angles) =
            bezierCurveCreator.createBezierCurveWithRotationAngles(
                departure.projectTo2D(),
                destination.projectTo2D()
            )

        val latLngPoints = points.map { it.projectToCoordinates() }
        return PlanePathData(
            mapData = MapData(
                bounds = LatLngBounds.Builder().include(departure).include(destination).build(),
                cameraOffset = 30.toPx(),
                flightPath = latLngPoints
            ),
            planePositions = latLngPoints.mapIndexed { index, latLng ->
                PlanePosition(latLng, angles[index])
            }
        )
    }

}

data class PlanePathData(
    val mapData: MapData,
    val planePositions: List<PlanePosition>
)