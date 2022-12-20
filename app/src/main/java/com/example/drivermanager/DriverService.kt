package com.example.drivermanager

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query


interface DriverService {

    @POST("driver/manager/login2")
    fun getToken(
        @Body driver: Driver
    ) : Call<String>

    @GET("driver/manager/driverList2")
    fun getDriverSummary(
        @Query("token") token: String
    ) : Call<ArrayList<DriverSummary>>

    @GET("driver/manager/driverData2")
    fun getDriver(
        @Query("token") token: String,
        @Query("did") did: String
    ) : Call<Driver>

    @PUT("driver/manager/join/approve2")
    fun approveRegist(
        @Query("token") token: String,
        @Body driver: Driver
    ) : Call<Void>

    @PUT("driver/manager/join/reject2")
    fun rejectRegist(
        @Query("token") token: String,
        @Body driver: Driver
    ) : Call<Void>

}