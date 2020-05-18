package com.ivanovme.blackhawk.ui.route

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ivanovme.blackhawk.R
import com.ivanovme.blackhawk.ui.route.model.Route
import com.ivanovme.blackhawk.ui.utils.withLifecycle
import kotlinx.android.synthetic.main.fragment_route.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class RouteFragment : Fragment(R.layout.fragment_route) {

    companion object {
        fun newInstance() = RouteFragment()
        private const val KEY_DEBUG_IS_VISIBLE = "key_debug_is_visible"
    }

    private val viewModel: RouteViewModel by viewModel()

    private val routeFlowCoordinator: RouteFlowCoordinator by inject()

    private var route: Route? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
        subscribeToViewModel()
        setupClickListeners()

    }

    private fun setupViews() {
        testDestinationList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        testDestinationList.adapter = TestRouteAdapter(this::performFlightAnimation)
    }

    private fun subscribeToViewModel() {
        withLifecycle { compositeDisposable ->
            viewModel
                .getRouteUpdates()
                .doOnSubscribe { compositeDisposable.add(it) }
                .subscribe(
                    { route ->
                        this.route = route
                        destinationText.text = route.destination?.city ?: ""
                        destinationAirportText.text = route.destination?.airportCode ?: ""

                        departureText.text = route.departure?.city ?: ""
                        departureAirportText.text = route.departure?.airportCode ?: ""
                    },
                    {
                        throw it
                    }
                )
        }
    }

    private fun setupClickListeners() {
        developerModeView.setOnLongClickListener {
            testDestinationList.visibility = if (testDestinationList.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
            true
        }
        destinationText.setOnClickListener {
            routeFlowCoordinator.selectDestination()
        }

        departureText.setOnClickListener {
            routeFlowCoordinator.selectDeparture()
        }

        startAnimationView.setOnClickListener {
            performFlightAnimation(route)
        }
    }

    private fun performFlightAnimation(localRoute: Route?) {
        if (localRoute?.departure == null) {
            departureText.setHintTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )
        }

        if (localRoute?.destination == null) {
            destinationText.setHintTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )
        }

        if (localRoute?.destination == null || localRoute.departure == null) {
            return
        } else {
            val departure = localRoute.departure
            val destination = localRoute.destination

            if (departure == destination) {
                Toast.makeText(
                    requireContext(),
                    R.string.error_same_route_directions,
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            routeFlowCoordinator.showMap(
                departure.location,
                departure.airportCode,
                destination.location,
                destination.airportCode
            )
        }
    }
}