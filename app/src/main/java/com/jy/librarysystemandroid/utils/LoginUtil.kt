package com.jy.librarysystemandroid.utils

import com.google.gson.Gson
import com.jy.librarysystemandroid.model.StudentBean

object LoginUtil {

    fun convertLoginData(data :String) : StudentBean {
        return Gson().fromJson<StudentBean>(
            data,
            StudentBean::class.java
        )
    }
}