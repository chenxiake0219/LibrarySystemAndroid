package com.jy.librarysystemandroid.adapter

import android.graphics.drawable.AnimationDrawable
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.jy.librarysystemandroid.LibConfig
import com.jy.librarysystemandroid.R
import com.jy.librarysystemandroid.model.Books
import com.jy.librarysystemandroid.utils.SPUtils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_books.*
import org.greenrobot.eventbus.EventBus

class LibraryAdapter(
    var data: List<Books?>,
    var onItemClickListener: OnItemClickListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isBorrow: Boolean = false
    private val VIEW_FOOTER = 2

    interface OnItemClickListener {
        fun onItemClick(vi00ew: View, books: Books)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View
        if (viewType == VIEW_FOOTER) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_footer, parent, false)
            return FooterViewHolder(view)
        }
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_books, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (data.isNotEmpty() && holder is ItemViewHolder) {
            holder.bind(data[position], position)
        } else if (holder is FooterViewHolder) {
            val loadAnimation = holder.loadImage.drawable as AnimationDrawable
            loadAnimation.start()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun getItemViewType(position: Int): Int {

        return if (data.isNotEmpty() && null == data[position]) {
            VIEW_FOOTER
        } else super.getItemViewType(position)
    }

    inner class ItemViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(item: Books?, index: Int) {
            page_index.text = (index + 1).toString()
            tv_id.text = item?.bookid
            tv_book_name.text = item?.bookname
            tv_author.text = item?.author
            tv_enter_date.text = item?.inTime
            tv_price.text = item?.price
            tv_press.text = item?.press

            val utype = SPUtils.getInstance().getInt(LibConfig.LOGIN_U_TYPE)
            if (utype == LibConfig.LOGIN_TYPE_STUDENT) {
                btn_borrow.visibility = View.VISIBLE
                btn_edie.visibility = View.INVISIBLE
            } else if (utype == LibConfig.LOGIN_TYPE_ADMIN) {
                if (isBorrow) {
                    btn_borrow.visibility = View.VISIBLE
                } else {
                    btn_borrow.visibility = View.GONE
                }
            }

            btn_borrow.setOnClickListener {
                item?.let { it1 -> onItemClickListener?.onItemClick(it, it1) }
            }

            btn_edie.setOnClickListener {
                item?.let { it1 -> onItemClickListener?.onItemClick(it, it1) }
            }

            btn_look.setOnClickListener{
                item?.let { it1 -> onItemClickListener?.onItemClick(it, it1) }
            }
        }
    }

    inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var loadImage: ImageView = itemView.findViewById(R.id.loading_img)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (!EventBus.getDefault().isRegistered(recyclerView.context)) {
            EventBus.getDefault().register(recyclerView.context)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        if (EventBus.getDefault().isRegistered(recyclerView.context)) {
            EventBus.getDefault().unregister(recyclerView.context)
        }
    }
}



