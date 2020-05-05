package com.jy.librarysystemandroid.activity

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.NonNull
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.jy.librarysystemandroid.R
import com.jy.librarysystemandroid.api.LibrarySystemApi
import com.jy.librarysystemandroid.model.Books
import com.jy.librarysystemandroid.utils.ImageFilter
import kotlinx.android.synthetic.main.activity_bookinfo.*
import java.security.MessageDigest


class BookInfoActivity : Activity() {

    private val mUrl = LibrarySystemApi.DOMAIN + "image/"

    private var mBook: Books? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookinfo)

        var flags = window.decorView.systemUiVisibility
        flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.decorView.systemUiVisibility = flags
        window.statusBarColor = Color.TRANSPARENT

        mBook =  intent.getParcelableExtra(BOOKID)

        mBook?.let {
            book_name.text = it.bookname
            author_name.text = it.author
            book_type.text = "类型: ${it.bookcase}"
            tv_content.text = "\u3000\u3000${it.content}"

            Glide
                .with(this)
                .load(mUrl + it.imageurl)
                .apply(RequestOptions.bitmapTransform(GlideBlurTransformation(this)))
                .into(h_back)

            Glide
                .with(this)
                .load(mUrl + it.imageurl)
                .centerCrop()
                .placeholder(R.drawable.haha)
                .into(book_icon);
        }
    }

    class GlideBlurTransformation(private val context: Context) : CenterCrop() {
        override fun transform(
            @NonNull pool: BitmapPool, @NonNull toTransform: Bitmap, outWidth: Int,
            outHeight: Int
        ): Bitmap {
            val bitmap = super.transform(pool, toTransform, outWidth, outHeight)
            return ImageFilter.blurBitmap(context, bitmap, 25f)
        }

        override fun updateDiskCacheKey(@NonNull messageDigest: MessageDigest) {}

    }
    companion object {
        const val BOOKID = "BOOKID"
    }
}