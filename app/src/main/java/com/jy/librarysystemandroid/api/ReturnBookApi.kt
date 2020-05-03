package com.jy.librarysystemandroid.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ReturnBookApi {

    @POST("ReturnTableServlet?action=returnBook")
    fun returnBook(@Body body: RequestBody) : Call<ResponseBody>
}