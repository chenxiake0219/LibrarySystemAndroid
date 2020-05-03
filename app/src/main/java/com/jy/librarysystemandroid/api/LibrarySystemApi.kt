package com.jy.librarysystemandroid.api

import android.content.Context
import com.jy.librarysystemandroid.api.intercepter.UrlInterceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LibrarySystemApi {

    private val DOMAIN = "http://192.168.0.107:8080/"
    private var mRetrofit: Retrofit
    private var mOkHttpClient: OkHttpClient

    private val mLoginApi: LoginApi
    private var mStudentBorrowApi: StudentBorrowApi
    private var mBookLibraryApi: BookLibraryApi
    private var mReturnBookApi: ReturnBookApi

    constructor(context: Context) {
        // HttpLoggingInterceptor会造成上传文件时ProgressRequestBody回调两次writeTo
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        mOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(UrlInterceptor(context))
            .build()
        mRetrofit = Retrofit.Builder()
            .client(mOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(DOMAIN)
            .build()

        mLoginApi = mRetrofit.create(LoginApi::class.java)
        mStudentBorrowApi = mRetrofit.create(StudentBorrowApi::class.java)
        mBookLibraryApi = mRetrofit.create(BookLibraryApi::class.java)
        mReturnBookApi = mRetrofit.create(ReturnBookApi::class.java)
    }

    fun checkLogin(requestBody: RequestBody) : Call<ResponseBody> {
        return mLoginApi.checkLogin(requestBody)
    }

    fun getStudentBorrowList(page: Int, pageSize: Int) : Call<ResponseBody> {
        return mStudentBorrowApi.getStudentBorrowList(page, pageSize)
    }

    fun getListBooks(page: Int, pageSize: Int) :Call<ResponseBody> {
        return mBookLibraryApi.getListBooks(page, pageSize)
    }

    fun borrowBook(body: RequestBody) :Call<ResponseBody>{
        return mBookLibraryApi.borrowBook(body)
    }

    fun getBorrowList(stuid :String, currentPage: Int, pageSize: Int): Call<ResponseBody> {
        return mStudentBorrowApi.getBorrowList(stuid, currentPage, pageSize)
    }

    fun returnBook(body:RequestBody) : Call<ResponseBody> {
        return mReturnBookApi.returnBook(body)
    }
}