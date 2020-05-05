package com.jy.librarysystemandroid.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface BookLibraryApi {

    @GET("BooksLibrary?action=getListBooks&type=1")
    fun getListBooks(@Query("page") page: Int, @Query("pageSize") pageSize: Int): Call<ResponseBody>

    @POST("BorrowServlet?action=borrowBook")
    fun borrowBook(@Body body: RequestBody) : Call<ResponseBody>

    @GET("BooksLibrary?action=lookBook&type=1")
    fun lookBook(@Query("content") content: String): Call<ResponseBody>

    @PUT("UploadServlet")
    fun addBooks(): Call<ResponseBody>

    @Multipart
    @POST("UploadServlet")
    fun uploadImage(@PartMap map: HashMap<String, RequestBody>, @Part file: MultipartBody.Part) : Call<ResponseBody>
}