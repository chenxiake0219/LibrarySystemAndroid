package com.jy.librarysystemandroid.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jy.librarysystemandroid.LibConfig
import com.jy.librarysystemandroid.R
import com.jy.librarysystemandroid.activity.BorrowListActivity
import com.jy.librarysystemandroid.activity.StuInfoEditActivity
import com.jy.librarysystemandroid.adapter.BorrowAdapter
import com.jy.librarysystemandroid.api.LibrarySystemApi
import com.jy.librarysystemandroid.base.BaseListFragment
import com.jy.librarysystemandroid.dialog.CommonDialog
import com.jy.librarysystemandroid.event.BorrowEvent
import com.jy.librarysystemandroid.model.StudentBean
import com.jy.librarysystemandroid.utils.SPUtils
import kotlinx.android.synthetic.main.fragment_borrow.*
import kotlinx.android.synthetic.main.title_view.*
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BorrowFragment : BaseListFragment() {

    private lateinit var mAdapter: BorrowAdapter
    private var mData: ArrayList<StudentBean?> = ArrayList()

    private var mLookUpAnnounceDialog: CommonDialog? = null

    override fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_borrow, container, false)
    }

    override fun initTitle() {
        toolbar_title.setText(R.string.student_borrow_manager)
        val utype = SPUtils.getInstance("").getInt(LibConfig.LOGIN_U_TYPE)
        if (utype == LibConfig.LOGIN_TYPE_ADMIN) {
            btn_trigger.visibility = View.VISIBLE
        }
        btn_trigger.setOnClickListener {
            //添加
            val intent = Intent(context, StuInfoEditActivity::class.java)
            startActivityForResult(intent, 1)
        }
        btn_lookup.visibility = View.VISIBLE
        btn_lookup.setOnClickListener {
            //查询
            mLookUpAnnounceDialog = CommonDialog.Builder(context!!)
                .setTitle("查询学生")
                .setContentView(
                    View.inflate(
                        context,
                        R.layout.dialog_studetn_look,
                        null
                    )
                )
                .setPositiveButton("查询",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        lookUpMessage()
                        dialogInterface.dismiss()
                    })
                .setNegativeButton("取消",
                    DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                .create()
            mLookUpAnnounceDialog!!.show()
        }
        setUpView()
        mIsCreateView = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == 2) {
            refresh()
        }
    }

    private fun lookUpMessage() {
        mData.clear()
        mAdapter.notifyDataSetChanged()
        addLoading()
        val name: EditText = mLookUpAnnounceDialog!!.findViewById(R.id.et_message_look_up)
        context?.let {
            LibrarySystemApi(it).lookStudent(name.text.toString())
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.i(
                            "BorrowFragment",
                            "getStudentBorrowList_onFailure:" + t.printStackTrace()
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
                                    val turnsType = object : TypeToken<List<StudentBean>>() {}.type
                                    val studentsData: List<StudentBean> =
                                        Gson().fromJson<List<StudentBean>>(
                                            data.toString(),
                                            turnsType
                                        )
                                    if (studentsData.isNotEmpty()) {
                                        mData.addAll(studentsData)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            Toast.makeText(context, "网络异常", Toast.LENGTH_LONG).show()
                        }
                            removeLoading()
                    }
                })
        }
    }

    private fun setUpView() {
        recycler.layoutManager = LinearLayoutManager(context)
        mAdapter = BorrowAdapter(mData, object : BorrowAdapter.OnItemClickListener {
            override fun onItemClick(view: View, studentBean: StudentBean?) {
                when (view.id) {
                    R.id.btn_borrow -> {
                        var event: BorrowEvent = BorrowEvent()
                        event.stuid = studentBean?.stuid
                        EventBus.getDefault().post(event)
                    }
                    R.id.btn_look -> {
                        val intent = Intent(context, BorrowListActivity::class.java)
                        intent.putExtra("stuid", studentBean?.stuid)
                        context?.startActivity(intent)
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
                        getStudentBorrowList()
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
        getStudentBorrowList()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        refresh()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
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
        mAdapter.notifyDataSetChanged()
    }

    private fun getStudentBorrowList() {
        addLoading()
        context?.let {
            LibrarySystemApi(it).getStudentBorrowList(mCurrentPage, mPageSize)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.i(
                            "BorrowFragment",
                            "getStudentBorrowList_onFailure:" + t.printStackTrace()
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
                                    val turnsType = object : TypeToken<List<StudentBean>>() {}.type
                                    val studentsData: List<StudentBean> =
                                        Gson().fromJson<List<StudentBean>>(
                                            data.toString(),
                                            turnsType
                                        )
                                    if (studentsData.isNotEmpty()) {
                                        mAdapter.notifyDataSetChanged()
                                        mCurrentPage++
                                        mData.addAll(studentsData.asReversed())
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
                        }, 500)
                    }
                })
        }
    }
}