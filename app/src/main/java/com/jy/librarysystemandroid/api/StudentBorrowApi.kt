package com.jy.librarysystemandroid.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StudentBorrowApi {

    @GET("StudentServlet?action=getListStudent&type=1")
    fun getStudentBorrowList(@Query("page") page:Int, @Query("pageSize") pageSize:Int): Call<ResponseBody>

    @GET("StuBorrowServlet?action=getStuBorrowRecord&type=1")
    fun getBorrowList(@Query("stuid") stuid :String, @Query("page") currentPage: Int, @Query("pageSize") pageSize: Int): Call<ResponseBody>

    @GET("StudentServlet?action=lookStudent&type=1")
    fun lookStudent(@Query("content") name: String): Call<ResponseBody>

    @GET("StudentServlet?action=addStudent&type=1")
    fun addStudent(@Query("pwd") password: String, @Query("sex") gender: String, @Query("name") name: String,  @Query("email") email: String, @Query("operator") operator: String): Call<ResponseBody>
}