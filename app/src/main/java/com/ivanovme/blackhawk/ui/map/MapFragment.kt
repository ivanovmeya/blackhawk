package com.ivanovme.blackhawk.ui.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.ivanovme.blackhawk.R
import com.ivanovme.blackhawk.ui.utils.withLifecycle
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.min

class MapFragment : Fragment() {

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var map: GoogleMap

    private lateinit var from: LatLng
    private lateinit var to: LatLng

    private lateinit var fromTitle: String
    private lateinit var toTitle: String

    private val viewModel: MapViewModel by viewModel {
        parametersOf(
            arguments?.get(MAP_FROM) as LatLng,
            arguments?.get(MAP_TO) as LatLng
        )
    }

    private var planeMarker: Marker? = null
    private var mapContainerId = View.generateViewId()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        from = arguments?.get(MAP_FROM) as LatLng
        to = arguments?.get(MAP_TO) as LatLng

        fromTitle = arguments?.getString(MAP_FROM_AIRPORT) ?: ""
        toTitle = arguments?.getString(MAP_TO_AIRPORT) ?: ""

        return createView()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupMap()

        mapFragment.getMapAsync { googleMap ->
            map = googleMap
            drawAirportMarker(from, prepareMarkerBitmap(fromTitle))
            drawAirportMarker(to, prepareMarkerBitmap(toTitle))

            withLifecycle { compositeDisposable ->
                viewModel.getMapData()
                    .doOnSubscribe { compositeDisposable.add(it) }
                    .subscribe(
                        {
                            renderMap(it)
                        },
                        {
                            throw it
                        }
                    )
                viewModel.getPlanePosition()
                    .doOnSubscribe { compositeDisposable.add(it) }
                    .subscribe(
                        {
                            renderPlanePosition(it)
                        },
                        {
                            throw it
                        }
                    )
            }
            viewModel.onMapReady()
        }

        childFragmentManager.beginTransaction().replace(mapContainerId, mapFragment).commit()

    }


    override fun onStart() {
        super.onStart()
        viewModel.resumeAnimation()
    }

    override fun onStop() {
        super.onStop()
        viewModel.pauseAnimation()
    }

    private fun renderMap(mapData: MapData) {
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(mapData.bounds, mapData.cameraOffset))
        drawPath(mapData.flightPath)
        viewModel.startAnimation()
    }

    private fun renderPlanePosition(planePosition: PlanePosition) {
        if (planeMarker == null) {
            planeMarker = drawPlaneWithMarker(
                planePosition.coordinate,
                BitmapDescriptorFactory.fromResource(R.drawable.ic_plane),
                planePosition.rotationAngle
            )
        }

        planeMarker?.position = planePosition.coordinate
        planeMarker?.rotation = planePosition.rotationAngle
    }

    private fun drawPath(path: List<LatLng>) {
        map.addPolyline(
            PolylineOptions()
                .add(*path.toTypedArray())
                .width(10f)
                .geodesic(false)
                .jointType(JointType.BEVEL)
                .pattern(listOf(Dot(), Gap(20f)))
                .color(Color.BLUE)
        )
    }

    private fun drawAirportMarker(position: LatLng, bmp: Bitmap) {
        map.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                .anchor(0.5f, 0.5f)
                .position(position)
                .zIndex(5f)
        )
    }

    private fun drawPlaneWithMarker(
        startPosition: LatLng,
        planeImage: BitmapDescriptor,
        startRotation: Float
    ): Marker {
        return map.addMarker(
            MarkerOptions()
                .icon(planeImage)
                .anchor(0.5f, 0.5f)
                .position(startPosition)
                .rotation(startRotation)
                .zIndex(10f)
        )
    }

    private fun prepareMarkerBitmap(title: String): Bitmap {
        val width = context!!.resources.getDimension(R.dimen.bg_label_width)
        val height = context!!.resources.getDimension(R.dimen.bg_label_height)
        val textView = TextView(context).apply {
            background = context.getDrawable(R.drawable.bg_airport)
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTypeface(this.typeface, Typeface.BOLD)
            text = title
            gravity = Gravity.CENTER
            layoutParams = ViewGroup.LayoutParams(width.toInt(), height.toInt())
        }

        textView.measure(
            View.MeasureSpec.makeMeasureSpec(width.toInt(), View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height.toInt(), View.MeasureSpec.EXACTLY)
        )

        textView.layout(0, 0, textView.measuredWidth, textView.measuredHeight)

        val bmp = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        textView.draw(canvas)
        canvas.save()
        return bmp
    }

    private fun setupMap() {
        mapFragment = SupportMapFragment.newInstance(GoogleMapOptions().apply {
            minZoomPreference(1f)
            maxZoomPreference(16f)
            scrollGesturesEnabled(true)
            zoomGesturesEnabled(true)
            rotateGesturesEnabled(false)
        })
    }

    private fun createView(): LinearLayout {
        val alignmentContainer = LinearLayout(requireContext()).apply {
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        alignmentContainer.addView(LinearLayout(requireContext()).apply {
            id = mapContainerId
            val screenWidth = resources.displayMetrics.widthPixels
            val screenHeight = resources.displayMetrics.heightPixels

            setVerticalGravity(Gravity.CENTER_VERTICAL)
            setHorizontalGravity(Gravity.CENTER_HORIZONTAL)

            val min = min(screenWidth, screenHeight)
            layoutParams = ViewGroup.LayoutParams(min, min)
        })

        return alignmentContainer
    }

    companion object {
        const val MAP_FROM = "map_from"
        const val MAP_TO = "map_to"
        const val MAP_FROM_AIRPORT = "map_from_airport"
        const val MAP_TO_AIRPORT = "map_to_airport"

        fun newInstance(
            from: LatLng,
            fromAirport: String,
            to: LatLng,
            toAirport: String
        ): Fragment =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(MAP_FROM, from)
                    putParcelable(MAP_TO, to)
                    putString(MAP_FROM_AIRPORT, fromAirport)
                    putString(MAP_TO_AIRPORT, toAirport)
                }
            }
    }

}