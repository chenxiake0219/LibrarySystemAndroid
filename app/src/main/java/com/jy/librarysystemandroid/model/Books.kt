package com.jy.librarysystemandroid.model

import android.os.Parcel
import android.os.Parcelable

data class Books(
    val author: String?,
    val barCode: String?,
    val bookname: String?,
    val bookcase: String?,
    val bookid: String?,
    val booknum: Int,
    val inTime: String?,
    val operator: String?,
    val press: String?,
    val price: String?,
    val content: String?,
    val imageurl: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(author)
        parcel.writeString(barCode)
        parcel.writeString(bookname)
        parcel.writeString(bookcase)
        parcel.writeString(bookid)
        parcel.writeInt(booknum)
        parcel.writeString(inTime)
        parcel.writeString(operator)
        parcel.writeString(press)
        parcel.writeString(price)
        parcel.writeString(content)
        parcel.writeString(imageurl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Books> {
        override fun createFromParcel(parcel: Parcel): Books {
            return Books(parcel)
        }

        override fun newArray(size: Int): Array<Books?> {
            return arrayOfNulls(size)
        }
    }
}