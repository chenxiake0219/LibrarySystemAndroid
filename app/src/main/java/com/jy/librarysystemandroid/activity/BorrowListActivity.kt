package com.jy.librarysystemandroid.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jy.librarysystemandroid.LibConfig
import com.jy.librarysystemandroid.R
import com.jy.librarysystemandroid.adapter.BorrowListAdapter
import com.jy.librarysystemandroid.api.LibrarySystemApi
import com.jy.librarysystemandroid.model.BorrowListBean
import kotlinx.android.synthetic.main.activity_list_borrow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BorrowListActivity : AppCompatActivity() {

    private var mCurrentPage = 1
    private var mPageSize = 15
    private var stuid = ""

    private var mLoading = false
    private lateinit var mAdapter: BorrowListAdapter
    private var mData: ArrayList<BorrowListBean?> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_borrow)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        stuid = intent.getStringExtra("stuid")
        setUpView()
        getBorrowList()
    }

    private fun setUpView() {

        recycler.layoutManager = LinearLayoutManager(this)
        mAdapter = BorrowListAdapter(mData, object : BorrowListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int, borrowListBean: BorrowListBean?) {
                when (view.id) {
                    R.id.btn_return -> {
                        Toast.makeText(this@BorrowListActivity, "还书", Toast.LENGTH_LONG).show()
                        var body: RequestBody = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("stuid", stuid)
                            .addFormDataPart("bookid", borrowListBean?.bookid.toString())
                            .build()
                        LibrarySystemApi(this@BorrowListActivity).returnBook(body)
                            .enqueue(object : Callback<ResponseBody> {
                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                    Log.i(
                                        "BorrowListActivity",
                                        "returnBook_onFailure:" + t.printStackTrace()
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
                                                Toast.makeText(
                                                    this@BorrowListActivity,
                                                    "还书成功" + position,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                //集合中的数据没有变，所以更新没有用
                                                mData.removeAt(position)
                                                mAdapter.notifyItemRemoved(position)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    } else {
                                        Toast.makeText(
                                            this@BorrowListActivity,
                                            "网络异常",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            })
                    }
                }
            }
        })
        recycler.adapter = mAdapter
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                //判断触底
                if (!mLoading && mData.size >= mPageSize && RecyclerView.SCROLL_STATE_IDLE == newState) {
                    val lastItemPosition = layoutManager.findLastVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount
                    if (lastItemPosition == totalItemCount - 1) {
                        getBorrowList()
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
        mAdapter.notifyDataSetChanged()
        getBorrowList()
    }

    private fun addLoading() {
        mLoading = true
        mData.add(mData.size, null)
        mAdapter.notifyItemInserted(mData.size - 1)
    }

    private fun removeLoading() {
        mLoading = false
        val index = mData.indexOf(null)
        mData.removeAt(index)
        mAdapter.notifyItemRemoved(index)
        //不加这一行，适配器的起始索引就是 1，否则就是0
        mAdapter.notifyDataSetChanged()
    }


    private fun getBorrowList() {
        if (TextUtils.isEmpty(stuid)) {
            return
        }
        addLoading()
        LibrarySystemApi(this).getBorrowList(stuid, mCurrentPage, mPageSize)
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.i(
                        "BorrowListActivity:",
                        "getBorrowList_onFailure:" + t.printStackTrace()
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
                                val turnsType = object : TypeToken<List<BorrowListBean>>() {}.type
                                val borrowList: List<BorrowListBean> =
                                    Gson().fromJson<List<BorrowListBean>>(
                                        data.toString(),
                                        turnsType
                                    )
                                if (borrowList.isNotEmpty()) {
                                    mAdapter.notifyDataSetChanged()
                                    mCurrentPage++
                                    mData.addAll(borrowList.asReversed())
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(this@BorrowListActivity, "网络异常", Toast.LENGTH_LONG).show()
                    }
                    recycler.postDelayed(Runnable {
                        removeLoading()
                    }, 100)
                }
            })
    }
}
