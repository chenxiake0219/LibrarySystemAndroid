package com.jy.librarysystemandroid

import android.app.Application
import android.content.Context
import kotlin.properties.Delegates

class MyApplication : Application() {


    companion object {
        private var instance: MyApplication by Delegates.notNull()
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this;
    }
}
