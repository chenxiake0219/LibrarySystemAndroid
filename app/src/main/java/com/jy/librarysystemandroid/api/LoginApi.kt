package com.jy.librarysystemandroid.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {

    @POST("LoginServlet")
    fun checkLogin(@Body body: RequestBody) : Call<ResponseBody>
}