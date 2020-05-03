package com.jy.librarysystemandroid.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jy.librarysystemandroid.R
import com.jy.librarysystemandroid.base.BaseListAdapter
import com.jy.librarysystemandroid.model.StudentBean
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_books.page_index
import kotlinx.android.synthetic.main.item_borrows.*

class BorrowAdapter(
    var data: List<StudentBean?>,
    var onClickListener: OnItemClickListener? = null
) : BaseListAdapter() {

    interface OnItemClickListener {
        fun onItemClick(view: View, studentBean: StudentBean?)
    }

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_borrows, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (data.isNotEmpty() && holder is ItemViewHolder) {
            holder.bind(data[position], position)
        } else if (holder is FooterViewHolder) {
            loadingAnimation(holder)
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
        fun bind(item: StudentBean?, index: Int) {
            page_index.text = (index).toString()
            tv_index.text = item?.stuid
            tv_sex.text = item?.sex
            tv_stu_name.text = item?.name
            tv_email.text = item?.email
            tv_phone_number.text = item?.tel
            tv_borrow_date.text = item?.createDate
            tv_operator.text = item?.operator

            btn_borrow.setOnClickListener {
                onClickListener?.onItemClick(it, item)
            }

            btn_look.setOnClickListener {
                onClickListener?.onItemClick(it, item)
            }
        }
    }
}