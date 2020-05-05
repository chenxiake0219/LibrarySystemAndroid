package com.jy.librarysystemandroid.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.jy.librarysystemandroid.R
import com.jy.librarysystemandroid.api.LibrarySystemApi
import com.jy.librarysystemandroid.utils.FileUtil
import com.jy.librarysystemandroid.utils.UIUtils
import kotlinx.android.synthetic.main.activity_book_add.*
import kotlinx.android.synthetic.main.title_view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class BookAddActivity : Activity() {

    private var path: String? = null
    private val mBean: Any? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_add)

        toolbar_title.text = "添加书籍"
        submit_tv.setOnClickListener(View.OnClickListener {
            val bookName = et_book_name.text.toString()
            val bookPrice = et_price.text.toString()
            val author = et_author.text.toString()
            val press = et_press.text.toString()
            val operator = et_operator.text.toString()
            val bookNum = et_book_name.text.toString()
            val bookContent = et_content_info.text.toString()
            val bookCase = et_book_case.text.toString()
            val barCode = et_book_isbn.text.toString()
            if (TextUtils.isEmpty(bookName)) {
                UIUtils.toast("请输入书籍名称")
                return@OnClickListener
            } else if (TextUtils.isEmpty(bookPrice)) {
                UIUtils.toast("请输书籍价格")
                return@OnClickListener
            } else if (TextUtils.isEmpty(author)) {
                UIUtils.toast("请输入作者")
                return@OnClickListener
            } else if (TextUtils.isEmpty(press)) {
                UIUtils.toast("请输入出版社")
                return@OnClickListener
            } else if (TextUtils.isEmpty(operator)) {
                UIUtils.toast("请输入操作者")
                return@OnClickListener
            } else if (TextUtils.isEmpty(bookNum)) {
                UIUtils.toast("请输入书籍数量")
                return@OnClickListener
            } else if (TextUtils.isEmpty(bookContent)) {
                UIUtils.toast("请输入书籍介绍")
                return@OnClickListener
            } else if (TextUtils.isEmpty(bookCase)) {
                UIUtils.toast("请输入书籍类型")
                return@OnClickListener
            } else {
                if (mBean == null) {
                    addBooks(
                        bookName,
                        bookPrice,
                        author,
                        press,
                        operator,
                        bookNum,
                        bookContent,
                        bookCase,
                        barCode
                    )
                } else {
                    editAnnounce(
                        bookName,
                        bookPrice,
                        author,
                        press,
                        operator,
                        bookNum,
                        bookContent,
                        bookCase,
                        barCode
                    )
                }
            }
        })

        select_file_tv.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            //intent.setType(“image/*”);//选择图片
            //intent.setType(“audio/*”); //选择音频
            //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
            //intent.setType(“video/*;image/*”);//同时选择视频和图片
            intent.type = "image/*"  //选择图片
            //intent.setType(“audio/*”); //选择音频
            //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
            //intent.setType(“video/*;image/*”);//同时选择视频和图片
            //intent.type = "*/*" //无类型限制
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, 1)
        }

        scan_code_tv.setOnClickListener {
            activityJumpToScan()
        }
    }

    fun activityJumpToScan() {
        val intent = Intent(this, CaptureActivity::class.java)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent
    ) {
        if (resultCode == RESULT_OK) {
            val uri: Uri = data.data
            if ("file".equals(uri.getScheme(), ignoreCase = true)) { //使用第三方应用打开
                path = uri.getPath()
                et_book_cover.setText(path)
                Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show()
                return
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) { //4.4以后
                path = FileUtil().getPath(this, uri)
                et_book_cover.setText(path)
                Toast.makeText(this, path, Toast.LENGTH_SHORT).show()
            } else { //4.4以下下系统调用方法
                path = FileUtil().getRealPathFromURI(this, uri)
                et_book_cover.setText(path)
                Toast.makeText(this@BookAddActivity, path + "222222", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == 1) {
            if (resultCode == 1) {
                val bundle = data.extras
                bundle?.let {
                    et_book_isbn.setText(it.getString("result"))
                }
            }
        }
    }

    private fun editAnnounce(
        bookName: String,
        bookPrice: String,
        author: String,
        press: String,
        operator: String,
        bookNum: String,
        bookContent: String,
        bookCase: String,
        barCode: String
    ) {

    }

    private fun addBooks(
        bookName: String,
        bookPrice: String,
        author: String,
        press: String,
        operator: String,
        bookNum: String,
        bookContent: String,
        bookCase: String,
        barCode: String
    ) {
        val params: HashMap<String, RequestBody> = HashMap()
        params["author"] = toRequestBody(author)
        params["barcode"] = toRequestBody(barCode)
        params["bookname"] = toRequestBody(bookName)
        params["bookcase"] = toRequestBody(bookCase)
        params["booknum"] = toRequestBody(bookNum)
        params["operator"] = toRequestBody(operator)
        params["press"] = toRequestBody(press)
        params["price"] = toRequestBody(bookPrice)
        params["content"] = toRequestBody(bookContent)
        params["action"] = toRequestBody("addBook")
        params["type"] = toRequestBody("1")


        val fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), File(path))
        val filename = path?.let {
            it.substring(it.lastIndexOf("/"))
        }
        val multiBody = MultipartBody.Part.createFormData("imageurl", filename, fileBody)


        LibrarySystemApi(this).uploadImage(params, multiBody).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                finish()
                setResult(2)
            }

        })
    }

    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), value)
    }
}