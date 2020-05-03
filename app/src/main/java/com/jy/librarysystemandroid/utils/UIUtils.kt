package com.jy.librarysystemandroid.utils

import android.app.Application
import android.widget.Toast
import com.jy.librarysystemandroid.MyApplication

object UIUtils {

    fun toast(str:String) {
        Toast.makeText(MyApplication.instance().applicationContext, str, Toast.LENGTH_LONG).show()
    }
}