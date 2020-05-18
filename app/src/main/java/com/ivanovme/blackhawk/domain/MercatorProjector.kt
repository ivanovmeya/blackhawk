package com.ivanovme.blackhawk.domain

import android.graphics.PointF
import com.google.android.gms.maps.model.LatLng
import com.ivanovme.blackhawk.domain.MercatorProjectorConstants.RADIUS
import kotlin.math.atan
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.tan

object MercatorProjectorConstants {
    const val RADIUS = 6378137.0f
}

fun LatLng.projectTo2D() =
    PointF(
        Math.toRadians(this.longitude).toFloat() * RADIUS,
        ln(tan(Math.PI / 4 + Math.toRadians(this.latitude) / 2f)).toFloat() * RADIUS
    )

fun PointF.projectToCoordinates() =
    LatLng(
        Math.toDegrees(2 * atan(exp(this.y / RADIUS)) - Math.PI / 2f),
        Math.toDegrees((this.x / RADIUS).toDouble())
    )