package com.jy.librarysystemandroid.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jy.librarysystemandroid.LibConfig
import com.jy.librarysystemandroid.R
import com.jy.librarysystemandroid.utils.LoginUtil
import com.jy.librarysystemandroid.utils.LoginUtil.convertLoginData
import com.jy.librarysystemandroid.utils.SPUtils
import kotlinx.android.synthetic.main.fragment_manager.*

class ManagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = SPUtils.getInstance().getString(LibConfig.LOGIN_U_DATA);
        val utype = SPUtils.getInstance().getInt(LibConfig.LOGIN_U_TYPE)
        val stuBean = convertLoginData(data)
        tv_name.text = stuBean.name
        if (utype == LibConfig.LOGIN_TYPE_ADMIN) {
            tv_test_server.text = "管理员"
        } else if (utype == LibConfig.LOGIN_TYPE_STUDENT) {
            tv_test_server.text = "学生"
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
    }
}