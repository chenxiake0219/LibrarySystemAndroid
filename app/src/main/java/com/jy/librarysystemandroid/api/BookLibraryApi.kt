package com.jy.librarysystemandroid.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BookLibraryApi {

    @GET("BooksLibrary?action=getListBooks&type=1")
    fun getListBooks(@Query("page") page: Int, @Query("pageSize") pageSize: Int): Call<ResponseBody>

    @POST("BorrowServlet?action=borrowBook")
    fun borrowBook(@Body body: RequestBody) : Call<ResponseBody>
}