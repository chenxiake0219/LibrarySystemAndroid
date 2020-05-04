package com.jy.librarysystemandroid.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jy.librarysystemandroid.LibConfig
import com.jy.librarysystemandroid.activity.MainActivity
import com.jy.librarysystemandroid.R
import com.jy.librarysystemandroid.activity.BookInfoActivity
import com.jy.librarysystemandroid.adapter.LibraryAdapter
import com.jy.librarysystemandroid.api.LibrarySystemApi
import com.jy.librarysystemandroid.event.BorrowEvent
import com.jy.librarysystemandroid.event.StuBorrowEvent
import com.jy.librarysystemandroid.model.Books
import com.jy.librarysystemandroid.model.StudentBorrowBean
import com.jy.librarysystemandroid.utils.DateUtil
import com.jy.librarysystemandroid.utils.LoginUtil
import com.jy.librarysystemandroid.utils.SPUtils
import com.jy.librarysystemandroid.utils.UIUtils
import kotlinx.android.synthetic.main.fragment_borrow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LibraryFragment : Fragment() {

    private var mCurrentPage = 1
    private var mPageSize = 15
    private var mIsCreateView: Boolean = false
    private var mLoading = false
    private var mFirstLoading = true
    private var mAdapter: LibraryAdapter? = null
    private var mData: ArrayList<Books?> = ArrayList()

    private var mStuid: String =  ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = ""
        (context as MainActivity).setSupportActionBar(toolbar)
        setUpView()
        mIsCreateView = true
    }

    private fun setUpView() {

        recycler.layoutManager = LinearLayoutManager(context)
        mAdapter = LibraryAdapter(mData, object : LibraryAdapter.OnItemClickListener {
            override fun onItemClick(view: View, books: Books) {
                when (view.id) {
                    R.id.btn_edie -> {
                        Toast.makeText(context, "编辑", Toast.LENGTH_LONG).show()
                    }
                    R.id.btn_look -> {
                        //查看 图书详情
                        val intent = Intent(context, BookInfoActivity::class.java)
                        intent.putExtra(BookInfoActivity.BOOKID, books.bookid)
                        startActivity(intent)
                    }
                    R.id.btn_borrow -> {
                        if (SPUtils.getInstance().getInt(LibConfig.LOGIN_U_TYPE) == LibConfig.LOGIN_TYPE_STUDENT) {
                            val bean = LoginUtil.convertLoginData(SPUtils.getInstance().getString(LibConfig.LOGIN_U_DATA))
                            mStuid = bean.stuid
                        }
                        context?.let {
                            var body: RequestBody = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("stuid", mStuid)
                            .addFormDataPart("bookid" , books.bookid)
                            .addFormDataPart("expectreturndate", DateUtil.timeStamp2Date(System.currentTimeMillis(), ""))
                            .addFormDataPart("operator", books.operator)
                            .build()
                            LibrarySystemApi(it).borrowBook(body)
                                .enqueue(object : Callback<ResponseBody> {
                                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                        Log.i(
                                            "LibraryFragment",
                                            "borrowBook_onFailure:" + t.printStackTrace()
                                        )
                                    }

                                    override fun onResponse(
                                        call: Call<ResponseBody>,
                                        response: Response<ResponseBody>
                                    ) {
                                        if (null != response.body()) {
                                            try {
                                                var jsonObject: JSONObject =
                                                    JSONObject(response.body()!!.string())
                                                var code = jsonObject.optInt("code")
                                                if (code == LibConfig.SUCCESS_CODE) {
                                                    var event: StuBorrowEvent = StuBorrowEvent()
//                                                    event.stuid = mStuid
//                                                    EventBus.getDefault().post(event)
                                                    Toast.makeText(context, "借阅成功", Toast.LENGTH_LONG)
                                                        .show()
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        } else {
                                            Toast.makeText(context, "网络异常", Toast.LENGTH_LONG)
                                                .show()
                                        }
                                    }
                                })
                        }
                    }
                }
            }
        })
        recycler.adapter = mAdapter
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (!mLoading && mData.size >= mPageSize && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val lastItemPosition = linearLayoutManager.findLastVisibleItemPosition()
                    val itemCount = linearLayoutManager.itemCount
                    if (lastItemPosition == itemCount - 1) {
                        getListBooks()
                    }
                }
            }
        })
        swiprefresh.setOnRefreshListener {
            swiprefresh.isRefreshing = false
            if (!mLoading) {
                swiprefresh.isRefreshing = true
                refresh()
                swiprefresh.isRefreshing = false
            }
        }
    }

    private fun refresh() {
        mCurrentPage = 1
        mData.clear()
        mAdapter?.notifyDataSetChanged()
        getListBooks()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (mIsCreateView && isVisibleToUser && mFirstLoading) {
            refresh()
            mFirstLoading = false
        }

        mAdapter?.let {
            mStuid = arguments?.getString("stuid").toString()
            it.isBorrow = arguments?.getString("stuid") != null
            it.notifyDataSetChanged()
        }
    }

    private fun addLoading() {
        mLoading = true
        mData.add(mData.size, null)
        mAdapter?.notifyItemInserted(mData.size - 1)
    }

    private fun removeLoading() {
        mLoading = false
        val index = mData.indexOf(null)
        mData.removeAt(index)
        mAdapter?.notifyItemRemoved(index)
        mAdapter?.notifyDataSetChanged()
    }

    private fun getListBooks() {
        addLoading()
        context?.let {
            LibrarySystemApi(it).getListBooks(mCurrentPage, mPageSize)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.i(
                            "LibraryFragment",
                            "getBooksList_onFailure:" + t.printStackTrace()
                        )
                        removeLoading()
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (null != response.body()) {
                            try {
                                var jsonObject: JSONObject = JSONObject(response.body()!!.string())
                                var code = jsonObject.optInt("code")
                                if (code == LibConfig.SUCCESS_CODE) {
                                    val data = jsonObject.optJSONArray("data")
                                    val turnsType = object : TypeToken<List<Books>>() {}.type
                                    val books: List<Books> =
                                        Gson().fromJson<List<Books>>(
                                            data.toString(),
                                            turnsType
                                        )
                                    if (books.isNotEmpty()) {
                                        mAdapter?.notifyDataSetChanged()
                                        mCurrentPage++
                                        mData.addAll(books.asReversed())
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            Toast.makeText(context, "网络异常", Toast.LENGTH_LONG).show()
                        }
                        recycler.postDelayed(Runnable {
                            removeLoading()
                        }, 0)
                    }
                })
        }
    }
}