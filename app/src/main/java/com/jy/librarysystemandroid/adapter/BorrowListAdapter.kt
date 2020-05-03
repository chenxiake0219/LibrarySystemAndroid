package com.jy.librarysystemandroid.adapter

import android.graphics.drawable.AnimationDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.jy.librarysystemandroid.R
import com.jy.librarysystemandroid.model.BorrowListBean
import com.jy.librarysystemandroid.model.StudentBean
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_books.page_index
import kotlinx.android.synthetic.main.item_borrows.*
import kotlinx.android.synthetic.main.item_borrows_list.*

class BorrowListAdapter(
    var data: List<BorrowListBean?>,
    var onClickListener: OnItemClickListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_FOOTER = 2

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int, borrowListBean: BorrowListBean?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View
        if (viewType == VIEW_FOOTER) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_footer, parent, false)
            return FooterViewHolder(view)
        }
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_borrows_list, parent, false)
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

    inner class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(
        containerView
    ), LayoutContainer {
        fun bind(item: BorrowListBean?, index: Int) {
            page_index.text = (index + 1).toString()
            tv_stu_id.text = item?.stuid
            tv_book_id.text = item?.bookid
            tv_book_name.text = item?.bookname
            tv_borrow_list_date.text = item?.borrowdate
            tv_exp_return_date.text = item?.expect_return_date

            btn_return.setOnClickListener {
                onClickListener?.onItemClick(it, index, item)
            }
        }
    }

    inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var loadImage: ImageView = itemView.findViewById(R.id.loading_img)
    }
}