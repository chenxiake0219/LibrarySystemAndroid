package com.jy.librarysystemandroid.model

data class BorrowListBean(
    val bookid: String,
    val bookname: String,
    val borrowdate: String,
    val expect_return_date: String,
    val name: String,
    val stuid: String
)