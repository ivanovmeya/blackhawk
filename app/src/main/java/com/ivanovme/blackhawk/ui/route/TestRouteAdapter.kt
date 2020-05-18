package com.ivanovme.blackhawk.ui.route

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.ivanovme.blackhawk.R
import com.ivanovme.blackhawk.ui.route.model.Route
import com.ivanovme.blackhawk.ui.route.model.RoutePoint
import kotlinx.android.synthetic.main.item_route_test.view.*

class TestRouteAdapter(private val onRouteSelected: (route: Route) -> Unit) :
    RecyclerView.Adapter<TestRouteAdapter.TestRouteViewHolder>() {

    private val routes = testRoutesData()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestRouteViewHolder =
        TestRouteViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_route_test, parent, false)
        )

    override fun getItemCount(): Int = routes.size

    override fun onBindViewHolder(holder: TestRouteViewHolder, position: Int) {
        holder.bind(routes[position], onRouteSelected)
    }

    class TestRouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val destination: TextView = itemView.destinationName
        private val departure: TextView = itemView.departureName
        fun bind(route: Route, onRouteSelected: (route: Route) -> Unit) {

            destination.text = route.destination?.airportCode
            departure.text = route.departure?.airportCode
            itemView.setOnClickListener {
                onRouteSelected(route)
            }
        }
    }

    private fun testRoutesData(): List<Route> {
        return listOf(
            Route(
                RoutePoint("NYC", "NYC", LatLng(40.730610, -73.935242)),
                RoutePoint("SPB", "LED", LatLng(59.93863, 30.31413))
            ),
            Route(
                RoutePoint("Lapperanta", "LPA", LatLng(61.05871, 28.18871)),
                RoutePoint("South Africa", "SFR", LatLng(-26.195246, 28.034088))
            ),
            Route(
                RoutePoint("South Africa", "SFR", LatLng(-26.195246, 28.034088)),
                RoutePoint("Lapperanta", "LPA", LatLng(61.05871, 28.18871))
            ),
            Route(
                RoutePoint("Paris", "PAR", LatLng(48.85341, 2.3488)),
                RoutePoint("South Africa", "SFR", LatLng(-26.195246, 28.034088))
            ),
            Route(
                RoutePoint("London", "LON", LatLng(51.509865, -0.118092)),
                RoutePoint("Istanbul", "IST", LatLng(41.015137, 28.979530))
            ),
            Route(
                RoutePoint("Istanbul", "IST", LatLng(41.015137, 28.979530)),
                RoutePoint("Amster", "AMS", LatLng(52.377956, 4.897070))
            ),
            Route(

                RoutePoint("Amster", "AMS", LatLng(52.377956, 4.897070)),
                RoutePoint("Minsk", "MIN", LatLng(53.893009, 27.567444))
            ),
            Route(
                RoutePoint("Amster", "AMS", LatLng(52.377956, 4.897070)),
                RoutePoint("Kiev", "MIN", LatLng(50.4547, 30.5238))
            ),
            Route(
                RoutePoint("Amster", "AMS", LatLng(52.377956, 4.897070)),
                RoutePoint("Moscow", "MSK", LatLng(55.751244, 37.618423))
            ),
            Route(
                RoutePoint("San Francisco", "SFO", LatLng(37.77493, -122.419416)),
                RoutePoint("Brazil", "BRZ", LatLng(-23.53, -46.62))
            ),
            Route(
                RoutePoint("Brazil", "BRZ", LatLng(-23.53, -46.62)),
                RoutePoint("San Francisco", "SFO", LatLng(37.77493, -122.419416))
            ),
            Route(
                RoutePoint("San Francisco", "SFO", LatLng(37.77493, -122.419416)),
                RoutePoint("Moscow", "MSK", LatLng(55.751244, 37.618423))
            ),
            Route(
                RoutePoint("Moscow", "MSK", LatLng(55.751244, 37.618423)),
                RoutePoint("San Francisco", "SFO", LatLng(37.77493, -122.419416))
            ),
            Route(
                RoutePoint("Brazil", "BRZ", LatLng(-23.53, -46.62)),
                RoutePoint("Buenos Aires", "BUE", LatLng(-34.603, -58.381))
            ),
            Route(
                RoutePoint("Buenos Aires", "BUE", LatLng(-34.603, -58.381)),
                RoutePoint("Brazil", "BRZ", LatLng(-23.53, -46.62))
            ),
            Route(
                RoutePoint("Brazil", "BRZ", LatLng(-23.53, -46.62)),
                RoutePoint("Moscow", "MSK", LatLng(55.751244, 37.618423))
            ),
            Route(
                RoutePoint("Moscow", "MSK", LatLng(55.751244, 37.618423)),
                RoutePoint("Brazil", "BRZ", LatLng(-23.53, -46.62))
            ),
            Route(
                RoutePoint("Moscow", "MSK", LatLng(55.751244, 37.618423)),
                RoutePoint("Tokyo", "TOK", LatLng(35.652832, 139.839478))
            ),
            Route(
                RoutePoint("Tokyo", "TOK", LatLng(35.652832, 139.839478)),
                RoutePoint("Moscow", "MSK", LatLng(55.751244, 37.618423))
            ),
            Route(
                RoutePoint("New Deli", "NDL", LatLng(28.644800, 77.216721)),
                RoutePoint("TOMSK", "TSK", LatLng(56.49771, 84.97437))
            ),
            Route(
                RoutePoint("TOMSK", "TSK", LatLng(56.49771, 84.97437)),
                RoutePoint("New Deli", "NDL", LatLng(28.644800, 77.216721))
            ),
            Route(
                RoutePoint("TOMSK", "TSK", LatLng(56.49771, 84.97437)),
                RoutePoint("Mystery", "MYS", LatLng(61.04083317, 28.14249943))
            ),
            Route(
                RoutePoint("Moscow", "MSK", LatLng(55.751244, 37.618423)),
                RoutePoint("TOMSK", "TSK", LatLng(56.49771, 84.97437))
            ),
            Route(
                RoutePoint("Moscow", "MSK", LatLng(55.751244, 37.618423)),
                RoutePoint("Mystery", "MYS", LatLng(61.04083317, 28.14249943))
            ),
            Route(
                RoutePoint("Brazil", "BRZ", LatLng(-23.53, -46.62)),
                RoutePoint("Sydney", "NSW", LatLng(-33.865, 151.209))
            ),
            Route(
                RoutePoint("Sydney", "NSW", LatLng(-33.865, 151.209)),
                RoutePoint("Brazil", "BRZ", LatLng(-23.53, -46.62))
            ),
            Route(
                RoutePoint("Petropavlovsk-Kamchatsk", "PKC", LatLng(53.044, 130.00)),
                RoutePoint("Vancouver", "YVR", LatLng(49.246, -140.00))

            ),
            Route(
                RoutePoint("Vancouver", "YVR", LatLng(49.246, -140.00)),
                RoutePoint("Petropavlovsk-Kamchatsk", "PKC", LatLng(53.044, 170.00))
            ),
            Route(
                RoutePoint("Petropavlovsk-Kamchatsk", "PKC", LatLng(53.044, 170.00)),
                RoutePoint("Vancouver", "YVR", LatLng(60.246, -140.00))
            ),
            Route(
                RoutePoint("Vancouver", "YVR", LatLng(60.246, -140.00)),
                RoutePoint("Petropavlovsk-Kamchatsk", "PKC", LatLng(53.044, 170.00))
            ),
            Route(
                RoutePoint("Vancouver", "YVR", LatLng(49.246292, -123.116226)),
                RoutePoint("Tokyo", "TOK", LatLng(35.652832, 139.839478))
            ),
            Route(
                RoutePoint("Tokyo", "TOK", LatLng(35.652832, 139.839478)),
                RoutePoint("Vancouver", "NSW", LatLng(49.246292, -123.116226))
            ),
            Route(
                RoutePoint("Sydney", "NSW", LatLng(49.246292, -123.116226)),
                RoutePoint("Tokyo", "TOK", LatLng(35.652832, 139.839478))
            )
        )
    }
}