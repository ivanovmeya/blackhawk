package com.ivanovme.blackhawk.domain

import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import kotlin.math.*

class BezierCurveCreator {

    companion object {
        const val equatorLength = 40075004.0f

        //coefficient to control curvature of the line
        private const val curvatureFactor = 0.75
    }

    /**
    the whole algorithm of bezier curve and tangets
    1) normalize points if over pacific flight, save normalization shift
    2) find control points to build bezier curve
    3) build bezier curve points along with rotation angles
    4) normalize points back by saved shift in first step
     */

    fun createBezierCurveWithRotationAngles(start: PointF, end: PointF): BezierCurveWithRotation {

        val isOverPacific = overPacificFlight(start, end)

        val (normalizedStart, normalizedEnd, xShift) = normalizeIfOverPacific(
            start,
            end,
            isOverPacific
        )

        val (controlPoint1, controlPoint2) = computeCubicBezierControlPoints(
            normalizedStart,
            normalizedEnd
        )

        val (bezierCurve, rotationAngles) = computeBezierCurvePointsWithRotations(
            normalizedStart,
            controlPoint1,
            controlPoint2,
            normalizedEnd
        )

        val bezierCurvePoints = normalizeBack(bezierCurve, xShift, isOverPacific)

        return BezierCurveWithRotation(
            points = bezierCurvePoints,
            angles = rotationAngles
        )
    }

    private fun normalizeIfOverPacific(
        start: PointF,
        end: PointF,
        isOverPacific: Boolean
    ): NormalizedPoints {
        return if (isOverPacific) {
            NormalizedPoints(
                start = PointF(normalize(start.x - start.x), start.y),
                end = PointF(normalize(end.x - start.x), end.y),
                shift = start.x
            )
        } else {
            NormalizedPoints(
                start = start,
                end = end,
                shift = 0f
            )
        }
    }

    private fun computeBezierCurvePointsWithRotations(
        start: PointF,
        controlPoint1: PointF,
        controlPoint2: PointF,
        end: PointF
    ): BezierCurveWithRotation {
        val bezierPoints = mutableListOf<PointF>()
        val rotationAngles = mutableListOf<Float>()
        val p = Path()

        p.moveTo(start.x, start.y)
        p.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, end.x, end.y)

        val pathMeasure = PathMeasure(p, false)

        val outputPoints = FloatArray(2) { 0f }
        val outputTangents = FloatArray(2) { 0f }

        val pathStep = 100.0f
        var pathX = 0f
        while (pathX <= pathMeasure.length) {
            pathMeasure.getPosTan(pathX, outputPoints, outputTangents)
            bezierPoints.add(PointF(outputPoints[0], outputPoints[1]))

            val dx = outputTangents[0]
            val dy = outputTangents[1]

            val isTopToBottom = dy < 0
            var rotation = Math.toDegrees(atan(dx / dy.toDouble())).toFloat()

            rotation = if (isTopToBottom) {
                rotation + 90
            } else {
                rotation - 90
            }

            rotationAngles.add(rotation)

            pathX += pathStep
        }

        return BezierCurveWithRotation(bezierPoints, rotationAngles)
    }

    private fun normalizeBack(
        bezierCurvePoints: List<PointF>,
        shift: Float,
        isOverPacific: Boolean
    ): List<PointF> {
        return if (isOverPacific) {
            bezierCurvePoints.map { PointF(normalize(it.x + shift), it.y) }
        } else {
            bezierCurvePoints
        }
    }

    private fun computeCenterPoint(
        start: PointF,
        end: PointF
    ): PointF =
        PointF(
            (start.x + end.x) / 2f,
            (start.y + end.y) / 2f
        )

    private fun computeCubicBezierControlPoints(
        start: PointF,
        end: PointF
    ): Pair<PointF, PointF> {

        val centerPoint = computeCenterPoint(start, end)
        val startCenterPoint = computeCenterPoint(start, centerPoint)
        val endCenterPoint = computeCenterPoint(centerPoint, end)

        val halfDistance = start.distanceTo(centerPoint)

        //find fi angle
        //leg = cathetus
        val oppositeLeg = end.y - start.y
        val adjacentLeg = end.x - start.x

        val fi = if (adjacentLeg == 0.0f) {
            Math.PI / 2
        } else {
            atan(oppositeLeg / adjacentLeg.toDouble())
        }

        //as opposite angle in the right triangle
        val gamma = Math.PI / 2 - fi

        //projections on x and y of the half distance
        val xDiff = abs(curvatureFactor * halfDistance * cos(gamma))
        val yDiff = abs(curvatureFactor * halfDistance * sin(gamma))

        val xSign = if (endCenterPoint.x < startCenterPoint.x) -1 else 1
        val ySign = if (endCenterPoint.y < startCenterPoint.y) -1 else 1

        return Pair(
            PointF(
                startCenterPoint.x + xSign * xDiff.toFloat(),
                startCenterPoint.y - ySign * yDiff.toFloat()
            ),
            PointF(
                endCenterPoint.x - xSign * xDiff.toFloat(),
                endCenterPoint.y + ySign * yDiff.toFloat()
            )
        )
    }

    private fun normalize(xCoordinate: Float): Float {
        return when {
            xCoordinate < -equatorLength / 2f -> {
                xCoordinate + equatorLength
            }
            xCoordinate > equatorLength / 2f -> {
                xCoordinate - equatorLength
            }
            else -> {
                xCoordinate
            }
        }
    }

    private fun overPacificFlight(
        fromPoint: PointF,
        toPoint: PointF
    ) = abs(toPoint.x) + abs(fromPoint.x) > equatorLength / 2f && toPoint.x * fromPoint.x < 0

    private fun PointF.distanceTo(to: PointF): Float =
        sqrt((this.x - to.x) * (this.x - to.x) + (this.y - to.y) * (this.y - to.y))

}

data class NormalizedPoints(val start: PointF, val end: PointF, val shift: Float)

data class BezierCurveWithRotation(val points: List<PointF>, val angles: List<Float>)