package com.example.drivermanager

import android.content.Context
import android.widget.Toast
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DriverServiceImpl(var context : Context) {

    private val baseURL = "http://10.100.204.82:8080"
    private var service: DriverService
    private var service2: DriverService

    init {
        service = getDriverService()
        service2 = getDriverService2()
    }

    private fun getDriverService() : DriverService {
        val retrofit = Retrofit.Builder().baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(DriverService::class.java)
    }
    private fun getDriverService2() : DriverService {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder().baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(DriverService::class.java)
    }

    // 로그인
    fun getToken(did : String, password : String, operation: (String?) -> Unit) {
        service2.getToken(
            Driver(did, password)
        ).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
//                token = response.body()!!
//                Toast.makeText(context, token, Toast.LENGTH_SHORT).show()
                operation(response.body())
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                println("getToken 3: " + t.message)
//                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    // 배달원 현황 정보
    fun getDriverSummary(token: String, operation: (ArrayList<DriverSummary>?) -> Unit) {
        service.getDriverSummary(token).enqueue(object : Callback<ArrayList<DriverSummary>> {
            override fun onResponse(
                call: Call<ArrayList<DriverSummary>>,
                response: Response<ArrayList<DriverSummary>>
            ) {
//                datas = response.body()!!
                operation(response.body())
            }

            override fun onFailure(call: Call<ArrayList<DriverSummary>>, t: Throwable) {
                Toast.makeText(context, "등록된 배달원이 없습니다.", Toast.LENGTH_LONG).show()
            }
        })
    }

    // 배달원 정보
    fun getDriver(token: String, did: String, operation: (Driver?) -> Unit) {
        service.getDriver(token, did).enqueue(object : Callback<Driver> {
            override fun onResponse(call: Call<Driver>, response: Response<Driver>) {
//                driver = response.body()
                operation(response.body())
            }

            override fun onFailure(call: Call<Driver>, t: Throwable) {
                Toast.makeText(context, "선택한 배달원이 없습니다.", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun approve(token: String, driver: Driver, operation: () -> Unit) {
        service2.approveRegist(
            token, driver
        ).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                operation()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "선택한 배달원이 없습니다. 새로 불러오기 해주세요.", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun reject(token: String, driver: Driver, operation: () -> Unit) {
        service2.rejectRegist(
            token, driver
        ).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                print("DEBUG:reject: ")
                println(response)
                operation()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "선택한 배달원이 없습니다. 새로 불러오기 해주세요.", Toast.LENGTH_LONG).show()
            }
        })
    }

}