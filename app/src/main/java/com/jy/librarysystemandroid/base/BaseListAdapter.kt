package com.jy.librarysystemandroid.base

import android.graphics.drawable.AnimationDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.jy.librarysystemandroid.R

abstract class BaseListAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected val VIEW_FOOTER = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        if (viewType == VIEW_FOOTER) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_footer, parent, false)
            return FooterViewHolder(view)
        }
        return getViewHolder(parent);
    }

    abstract fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var loadImage: ImageView = itemView.findViewById(R.id.loading_img)
    }

    fun loadingAnimation(holder: FooterViewHolder) {
        val loadAnimation = holder.loadImage.drawable as AnimationDrawable
        loadAnimation.start()
    }
}