package com.jy.librarysystemandroid.api.intercepter

import android.content.Context
import android.util.Log

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Response

class UrlInterceptor(private val mContext: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.i("lyf", "path:" + request.url())
        return chain.proceed(request)
    }
}
