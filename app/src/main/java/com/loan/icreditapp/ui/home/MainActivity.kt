package com.loan.icreditapp.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.drawerlayout.widget.DrawerLayout
import com.blankj.utilcode.util.BarUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.base.BaseFragment

class MainActivity : BaseActivity() {

    private var drawerLayout: DrawerLayout? = null
    private var flContent: FrameLayout? = null
    private var flSetting: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(android.R.color.transparent))
        setContentView(R.layout.activity_main)
        initializeView()
    }

    private fun initializeView() {
        drawerLayout = findViewById(R.id.drawer_layout_container)
        flContent = findViewById(R.id.fl_main_content)
        flSetting = findViewById(R.id.fl_main_setting)

        var homeFragment = HomeFragment()
        replaceFragment(homeFragment, R.id.fl_main_content)
        var settingFragment = SettingFragment()
        replaceFragment(settingFragment, R.id.fl_main_setting)
    }

    private fun replaceFragment(fragment: BaseFragment, containRes: Int) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction() // 开启一个事务
        transaction.replace(containRes, fragment)
        transaction.commitAllowingStateLoss()
    }

}