package com.jy.librarysystemandroid.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jy.librarysystemandroid.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_borrow.*
import kotlinx.android.synthetic.main.title_view.*

abstract class BaseListFragment : Fragment() {

    protected var mCurrentPage = 1
    protected var mPageSize = 15
    protected var mIsCreateView: Boolean = false
    protected var mLoading = false
    protected var mFirstLoading = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mIsCreateView = true
        return getLayoutView(inflater, container, savedInstanceState)
    }

    abstract fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTitle()
    }

    open fun initTitle(){
        toolbar.title = ""
        (context as MainActivity).setSupportActionBar(toolbar)
    }
}