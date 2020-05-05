package com.jy.librarysystemandroid.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jy.librarysystemandroid.LibConfig
import com.jy.librarysystemandroid.R
import com.jy.librarysystemandroid.adapter.BorrowListAdapter
import com.jy.librarysystemandroid.api.LibrarySystemApi
import com.jy.librarysystemandroid.base.BaseListFragment
import com.jy.librarysystemandroid.event.RefreshEvent
import com.jy.librarysystemandroid.model.BorrowListBean
import com.jy.librarysystemandroid.utils.LoginUtil
import com.jy.librarysystemandroid.utils.SPUtils
import kotlinx.android.synthetic.main.activity_list_borrow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BorrowStuFragment : BaseListFragment(){

    private var stuid = ""
    private var mAdapter: BorrowListAdapter? = null
    private var mData: ArrayList<BorrowListBean?> = ArrayList()

    override fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        return inflater.inflate(R.layout.activity_list_borrow, container, false)
    }

    override fun initTitle() {
        super.initTitle()
        var data = LoginUtil.convertLoginData(SPUtils.instance.getString(LibConfig.LOGIN_U_DATA))
        stuid = data.stuid
        setUpView()
    }

    private fun setUpView() {

        recycler.layoutManager = LinearLayoutManager(context)
        mAdapter = BorrowListAdapter(mData, object : BorrowListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int, borrowListBean: BorrowListBean?) {
                when (view.id) {
                    R.id.btn_return -> {
                        Toast.makeText(context, "还书", Toast.LENGTH_LONG).show()
                        var body: RequestBody = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("stuid", stuid)
                            .addFormDataPart("bookid", borrowListBean?.bookid.toString())
                            .build()
                        context?.let {
                            LibrarySystemApi(it).returnBook(body)
                                .enqueue(object : Callback<ResponseBody> {
                                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                        Log.i(
                                            "BorrowStuFragment",
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
                                                        context,
                                                        "还书成功",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    //集合中的数据没有变，所以更新没有用
                                                    mData.removeAt(position)
                                                    mAdapter?.notifyItemRemoved(position)
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "网络异常",
                                                Toast.LENGTH_LONG
                                            ).show()
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
        mAdapter?.notifyDataSetChanged()
        getBorrowList()
    }

    private fun addLoading() {
        mLoading = true
        Log.i("=====前====", mData.size.toString())
        mData.add(mData.size, null)
        mAdapter?.notifyItemInserted(mData.size - 1)
        Log.i("=====后====", mData.size.toString())
    }

    private fun removeLoading() {
        mLoading = false
        val index = mData.indexOf(null)
        Log.i("=====index====", index.toString())
        mData.removeAt(index)
        mAdapter?.notifyItemRemoved(index)
        //不加这一行，适配器的起始索引就是 1，否则就是0
        mAdapter?.notifyDataSetChanged()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getBorrowList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event : RefreshEvent) {
        Log.i("222222----", stuid)
        refresh()
        recycler.scrollToPosition(mData.size - 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    private fun getBorrowList() {
        if (TextUtils.isEmpty(stuid)) {
            return
        }
        addLoading()
        context?.let {
            LibrarySystemApi(it).getBorrowList(stuid, mCurrentPage, mPageSize)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.i(
                            "BorrowStuFragment:",
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
                                        mAdapter?.notifyDataSetChanged()
                                        mCurrentPage++
                                        mData.addAll(borrowList.asReversed())
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
