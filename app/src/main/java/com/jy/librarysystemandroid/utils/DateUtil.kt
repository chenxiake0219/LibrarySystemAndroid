package com.jy.librarysystemandroid.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    //时间戳转换日期格式字符串
    fun timeStamp2Date(time: Long, format: String?): String {
        var format = format
        if (format == null || format.isEmpty()) {
            //HH:mm:ss
            format = "yyyy-MM-dd"
        }
        if (time <= 0) {
            return ""
        }
        val sdf = SimpleDateFormat(format)
        return sdf.format(Date(time))
    }
}