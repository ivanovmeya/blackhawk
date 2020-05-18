package com.ivanovme.blackhawk.network

import com.ivanovme.blackhawk.network.model.SearchResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("autocomplete")
    fun search(
        @Query("term") query: String,
        @Query("lang") language: String
    ): Single<SearchResponse>
}