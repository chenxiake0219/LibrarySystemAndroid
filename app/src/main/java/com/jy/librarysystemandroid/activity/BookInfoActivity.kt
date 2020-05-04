package com.jy.librarysystemandroid.activity

import android.app.Activity
import android.os.Bundle
import com.jy.librarysystemandroid.R
import com.jy.librarysystemandroid.utils.UIUtils

class BookInfoActivity : Activity() {


    private var mBookid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookinfo)

         mBookid =  intent.getStringExtra(BOOKID)

         UIUtils.toast(mBookid!!);
    }

    companion object {
        const val BOOKID = "BOOKID"
    }
}