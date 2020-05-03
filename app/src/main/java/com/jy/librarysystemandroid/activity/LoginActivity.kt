package com.jy.librarysystemandroid.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.gson.Gson
import com.jy.librarysystemandroid.LibConfig
import com.jy.librarysystemandroid.R
import com.jy.librarysystemandroid.api.LibrarySystemApi
import com.jy.librarysystemandroid.model.StudentBorrowBean
import com.jy.librarysystemandroid.utils.SPUtils
import com.jy.librarysystemandroid.utils.UIUtils
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : Activity() {

    private var mSelectType: Int = LibConfig.LOGIN_TYPE_STUDENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        changeSpinner()
       tv_login.setOnClickListener {
           val userName = et_mobile_number.text.toString()
           val passWord = et_password.text.toString()
           when {
               userName.isNullOrBlank() -> {
                   UIUtils.toast("请输入用户名")
               }
               passWord.isNullOrBlank() -> {
                   UIUtils.toast("请输入密码")
               }
               else -> {
                  login(userName, passWord)
               }
           }
       }
    }

    //?utype=3&username=简言&password=123
    private fun login(userName: String, passWord: String) {
        val body: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("username", userName)
            .addFormDataPart("password", passWord)
            .addFormDataPart("utype", mSelectType.toString())
            .addFormDataPart("type", LibConfig.TYPE_JSON.toString())
            .build()
        LibrarySystemApi(this).checkLogin(body).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                if (null != response.body()) {
                    try {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val code = jsonObject.optInt("code")
                        if (code == LibConfig.SUCCESS_CODE) {
                            UIUtils.toast("登录成功")
                            //将 utype  存起来， 后面各个界面  通过他去控制  退出时要清除
                            val data = jsonObject.opt("data")
                            SPUtils.getInstance().put(LibConfig.LOGIN_U_TYPE, mSelectType)
                            SPUtils.getInstance().put(LibConfig.LOGIN_U_DATA, data.toString())
                            finish()
                            val intent =
                                Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.i("LoginActivity-----", "onResponse")
                    }
                } else {
                    UIUtils.toast("没有任何数据")
                }
            }

            override fun onFailure(
                call: Call<ResponseBody?>,
                t: Throwable
            ) {
                Log.i("LoginActivity-----", "onFailure：$t")
            }
        })
    }

    fun changeSpinner() {
        spinner.dropDownWidth = 600 //下拉宽度
        spinner.dropDownHorizontalOffset = 100 //下拉的横向偏移
        //mSpinner.setDropDownVerticalOffset(100); //下拉的纵向偏移
        val spinnerItems = arrayOf<String?>("学生", "管理员")
        val spinnerAdapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            this,
            R.layout.item_select, spinnerItems
        )
        //自定义下拉的字体样式
        spinnerAdapter.setDropDownViewResource(R.layout.item_drop)
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                i: Int,
                l: Long
            ) {
                val type = (view as TextView).text.toString()
                mSelectType = if (type == "学生"){
                    LibConfig.LOGIN_TYPE_STUDENT
                } else {
                    LibConfig.LOGIN_TYPE_ADMIN
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }
}