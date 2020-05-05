package com.jy.librarysystemandroid.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jy.librarysystemandroid.LibConfig
import com.jy.librarysystemandroid.LibConfig.LOGIN_TYPE_NO_LOGIN
import com.jy.librarysystemandroid.R
import com.jy.librarysystemandroid.activity.LoginActivity
import com.jy.librarysystemandroid.api.LibrarySystemApi
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

        val data = SPUtils.instance.getString(LibConfig.LOGIN_U_DATA);
        val utype = SPUtils.instance.getInt(LibConfig.LOGIN_U_TYPE)
        val stuBean = convertLoginData(data)
        tv_name.text = stuBean.name
        if (utype == LibConfig.LOGIN_TYPE_ADMIN) {
            tv_test_server.text = "管理员"
        } else if (utype == LibConfig.LOGIN_TYPE_STUDENT) {
            tv_test_server.text = "学生"
        }

        tv_company_website_.setText(LibrarySystemApi.DOMAIN)

        btn_logout.setOnClickListener{
            SPUtils.getInstance("").put(LibConfig.LOGIN_U_DATA, "");
            SPUtils.getInstance("").put(LibConfig.LOGIN_U_TYPE, LOGIN_TYPE_NO_LOGIN)
            activity?.finish()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
    }
}