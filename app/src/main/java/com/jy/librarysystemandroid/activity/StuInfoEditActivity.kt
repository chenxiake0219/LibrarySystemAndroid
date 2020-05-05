package com.jy.librarysystemandroid.activity

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.jy.librarysystemandroid.LibConfig
import com.jy.librarysystemandroid.R
import com.jy.librarysystemandroid.api.LibrarySystemApi
import com.jy.librarysystemandroid.event.StuBorrowEvent
import com.jy.librarysystemandroid.utils.UIUtils
import kotlinx.android.synthetic.main.activity_list_borrow.*
import kotlinx.android.synthetic.main.activity_stu_info_edit.*
import okhttp3.Callback
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class StuInfoEditActivity : Activity() {


    private val mBean: Any? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stu_info_edit)

        toolbar_title.text = "添加学生"
        submit_tv.setOnClickListener(View.OnClickListener {
            if (mBean != null) {
                et_password.setText("123")
            }
            val gender: String = et_gender.getText().toString()
            val name: String = et_name.getText().toString()
            val password: String = et_password.getText().toString()
            val email: String = et_email.getText().toString()
            val operator: String = et_operator.getText().toString()
            if (TextUtils.isEmpty(password)) {
                UIUtils.toast("请输入密码")
                return@OnClickListener
            } else if (TextUtils.isEmpty(gender)) {
                UIUtils.toast("请输入性别")
                return@OnClickListener
            } else if (TextUtils.isEmpty(name)) {
                UIUtils.toast("请输入学生姓名")
                return@OnClickListener
            } else if (TextUtils.isEmpty(email)) {
                UIUtils.toast("请输入邮箱")
                return@OnClickListener
            } else if (TextUtils.isEmpty(operator)) {
                UIUtils.toast("请输入操作者")
                return@OnClickListener
            } else {
                if (mBean == null) {
                    addStudent(
                        password,
                        gender,
                        name,
                        email,
                        operator
                    )
                } else {
                    editAnnounce(
                        password,
                        gender,
                        name,
                        email,
                        operator
                    )
                }
            }
        })
    }

    private fun editAnnounce(password: String, gender: String, name: String, email: String, operator: String) {

    }

    private fun addStudent(password: String, gender: String, name: String, email: String, operator: String) {
        LibrarySystemApi(this).addStudent(password, gender, name, email, operator).enqueue(object: retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (null != response.body()) {
                    try {
                        var jsonObject: JSONObject =
                            JSONObject(response.body()!!.string())
                        var code = jsonObject.optInt("code")
                        if (code == LibConfig.SUCCESS_CODE) {
                            Toast.makeText(this@StuInfoEditActivity, "添加成功", Toast.LENGTH_LONG)
                                .show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this@StuInfoEditActivity, "网络异常", Toast.LENGTH_LONG)
                        .show()
                }
            }

        })
    }
}