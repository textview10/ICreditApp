package com.loan.icreditapp.ui.home

import android.Manifest
import android.os.Bundle
import android.widget.FrameLayout
import androidx.drawerlayout.widget.DrawerLayout
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.dialog.RequestPermissionDialog

class MainActivity : BaseActivity() {

    private var drawerLayout: DrawerLayout? = null
    private var flContent: FrameLayout? = null
    private var flSetting: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(android.R.color.transparent))
        BarUtils.setNavBarLightMode(this,true)
        setContentView(R.layout.activity_main)
        initializeView()
        requestPermission()
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

    private fun requestPermission() {
        val hasPermission = PermissionUtils.isGranted(
            PermissionConstants.LOCATION,
            PermissionConstants.CAMERA,
            PermissionConstants.SMS,
            PermissionConstants.CONTACTS,
            PermissionConstants.PHONE,
            PermissionConstants.STORAGE
        )
        //        if (false && hasPermission) {
        if (hasPermission) {
            executeNext()
        } else {
            requestPermissionInternal()
        }
    }

    private fun requestPermissionInternal() {
        val dialog = RequestPermissionDialog(this)
        dialog.setOnItemClickListener(object : RequestPermissionDialog.OnItemClickListener() {
            override fun onClickAgree() {
                PermissionUtils.permission(
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ).callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        executeNext()
                    }

                    override fun onDenied() {
                        ToastUtils.showShort("please allow permission.")
                    }
                }).request()
            }
        })
        dialog.show()
    }

    private fun executeNext() {}
}