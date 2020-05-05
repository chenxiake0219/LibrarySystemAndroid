package com.jy.librarysystemandroid.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.jy.librarysystemandroid.LibConfig
import com.jy.librarysystemandroid.R
import com.jy.librarysystemandroid.adapter.TabFragmentAdapter
import com.jy.librarysystemandroid.event.BorrowEvent
import com.jy.librarysystemandroid.event.RefreshEvent
import com.jy.librarysystemandroid.event.StuBorrowEvent
import com.jy.librarysystemandroid.fragments.BorrowFragment
import com.jy.librarysystemandroid.fragments.BorrowStuFragment
import com.jy.librarysystemandroid.fragments.LibraryFragment
import com.jy.librarysystemandroid.fragments.ManagerFragment
import com.jy.librarysystemandroid.utils.SPUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mFragmentAdapter: TabFragmentAdapter
    private var mFragmentList: ArrayList<Fragment> = ArrayList()
    //我的
    private lateinit var mManagerFragment: ManagerFragment
    //学生和管理员共有
    private lateinit var mLibraryFragment: LibraryFragment
    //管理员特有的
    private lateinit var mBorrowFragment: BorrowFragment
    //学生有的 借阅书记列表
    private lateinit var mBorrowStuFragment: BorrowStuFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        var flags = window.decorView.systemUiVisibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.decorView.systemUiVisibility = flags
        window.statusBarColor = Color.TRANSPARENT
        setUpTabLayout()
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onPause() {
        super.onPause()
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: BorrowEvent) {
        val bundle: Bundle = Bundle()
        bundle.putString("stuid", event.stuid)
        mLibraryFragment.arguments = bundle
        tablayout_main.getTabAt(1)?.select()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshEvent(event : StuBorrowEvent) {
        tablayout_main.getTabAt(0)?.select()
        EventBus.getDefault().post(RefreshEvent())
    }

    override fun onResume() {
        super.onResume()
        //恢复当前选中的页面
        //mFragmentAdapter.getItem(viewpager_main.currentItem).userVisibleHint = true
    }


    private fun setUpTabLayout() {
        val iconResID = intArrayOf(
            R.drawable.icon_tab_homepage,
            R.drawable.icon_tab_works,
            R.drawable.icon_tab_personal
        )
        val iconText = arrayOf(
            getString(R.string.borrow),
            getString(R.string.library),
            getString(R.string.manager)
        )

        mLibraryFragment = LibraryFragment()
        mManagerFragment = ManagerFragment()
        val utype = SPUtils.getInstance("").getInt(LibConfig.LOGIN_U_TYPE)
        if (utype == LibConfig.LOGIN_TYPE_ADMIN) {
            mBorrowFragment = BorrowFragment()
            mFragmentList.add(mBorrowFragment)
        } else if (utype == LibConfig.LOGIN_TYPE_STUDENT) {
            mBorrowStuFragment = BorrowStuFragment()
            mFragmentList.add(mBorrowStuFragment)
        }

        mFragmentList.add(mLibraryFragment)
        mFragmentList.add(mManagerFragment)

        mFragmentAdapter = TabFragmentAdapter(supportFragmentManager, mFragmentList)
        viewpager_main.adapter = mFragmentAdapter
        viewpager_main.offscreenPageLimit = 0
        tablayout_main.tabRippleColor = null
        tablayout_main.setupWithViewPager(viewpager_main, true)
        for (i in iconResID.indices) {
            tablayout_main.getTabAt(i)!!.setCustomView(R.layout.item_home_tab)
            tablayout_main.getTabAt(i)!!.setIcon(iconResID[i])
            tablayout_main.getTabAt(i)!!.text = iconText[i]
        }
//        tablayout_main.getTabAt(0)?.select()
//        viewpager_main.currentItem = 0

        viewpager_main.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {

            }

            override fun onPageSelected(i: Int) {
                mFragmentAdapter.getItem(i).userVisibleHint = true
                if (i != 1) {
                    mLibraryFragment.arguments?.clear()
                }
                Log.i("borrow", "mainA")
            }

            override fun onPageScrollStateChanged(i: Int) {

            }
        })
    }

}
