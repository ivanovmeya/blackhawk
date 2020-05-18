package com.ivanovme.blackhawk.di

import com.google.android.gms.maps.model.LatLng
import com.ivanovme.blackhawk.domain.BezierCurveCreator
import com.ivanovme.blackhawk.domain.FlightPathCreator
import com.ivanovme.blackhawk.network.SearchApi
import com.ivanovme.blackhawk.ui.base.ActivityHolder
import com.ivanovme.blackhawk.ui.base.Navigator
import com.ivanovme.blackhawk.ui.base.StringProvider
import com.ivanovme.blackhawk.ui.main.MainFlowCoordinator
import com.ivanovme.blackhawk.ui.map.MapViewModel
import com.ivanovme.blackhawk.ui.route.RouteFlowCoordinator
import com.ivanovme.blackhawk.ui.route.RouteHolder
import com.ivanovme.blackhawk.ui.route.RouteViewModel
import com.ivanovme.blackhawk.ui.search.SearchViewModel
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val mainModule = module {

    //presentation
    viewModel { (isDeparture: Boolean) ->
        SearchViewModel(isDeparture, get(), get(), get(), Schedulers.computation(), get())
    }

    viewModel {
        RouteViewModel(get())
    }

    viewModel { (from: LatLng, to: LatLng) ->
        MapViewModel(from, to, get())
    }


    single { MainFlowCoordinator(get()) }
    single { RouteFlowCoordinator(get()) }

    single { ActivityHolder() }
    single { Navigator(get()) }
    single { RouteHolder() }
    single { StringProvider(androidContext()) }

    //domain
    single { BezierCurveCreator() }
    single { FlightPathCreator(get()) }

    //network
    single<SearchApi> {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://yasen.hotellook.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

        retrofit.create(SearchApi::class.java)
    }
}