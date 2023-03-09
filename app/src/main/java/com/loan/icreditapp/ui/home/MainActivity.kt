package com.loan.icreditapp.ui.home

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.drojian.alpha.toolslib.log.LogSaver
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.UpdateResponseBean
import com.loan.icreditapp.collect.UpdateMgr
import com.loan.icreditapp.dialog.RequestPermissionDialog
import com.loan.icreditapp.global.ConfigMgr
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.global.FireBaseMgr
import com.loan.icreditapp.ui.home.fragment.*
import com.loan.icreditapp.ui.setting.PageType
import com.loan.icreditapp.ui.setting.SettingFragment
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.RateUsUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject
import java.io.File

class MainActivity : BaseActivity() {

    private val TAG = "MainActivity"

    private var drawerLayout: DrawerLayout? = null
    private var flContent: FrameLayout? = null
    private var flSetting: FrameLayout? = null
    private var ivMenu: ImageView? = null
    private var tvTitle: AppCompatTextView? = null

    private var handler: Handler = Handler(Looper.getMainLooper())

    @PageType
    private
    var mCurPageType: Int = PageType.MY_LOAN

    var mMyLoanFragment: MyLoanFragment? = null
    var mMyProfileFragment: MyProfileFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.theme_color))
        BarUtils.setStatusBarLightMode(this, false)
        setContentView(R.layout.activity_main)
        initializeView()
        OkGo.getInstance().addCommonHeaders(BuildRequestJsonUtils.buildHeaderToken())
        requestPermission()
        ConfigMgr.getAllConfig()

        SPUtils.getInstance().put(Constant.KEY_ACCOUNT_ID, Constant.mAccountId)
        SPUtils.getInstance().put(Constant.KEY_TOKEN, Constant.mToken)
        SPUtils.getInstance().put(Constant.KEY_MOBILE, Constant.mMobile)

        FireBaseMgr.sInstance.reportFcmToken()

        UpdateMgr.sInstance.setOnShowUpdateListener(object : UpdateMgr.OnShowUpdateListener {
            override fun onShowDialog(updateBean: UpdateResponseBean) {

            }
        })
        UpdateMgr.sInstance.checkUpdate()
    }

    private fun initializeView() {
        drawerLayout = findViewById(R.id.drawer_layout_container)
        flContent = findViewById(R.id.fl_main_content)
        flSetting = findViewById(R.id.fl_main_setting)
        ivMenu = findViewById(R.id.iv_main_menu)
        tvTitle = findViewById(R.id.iv_main_title)

        ivMenu?.setOnClickListener(View.OnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        })

        updatePageByTypeInternal()
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
//            PermissionConstants.LOCATION,
//            PermissionConstants.CAMERA,
            PermissionConstants.SMS,
            PermissionConstants.CONTACTS,
//            PermissionConstants.STORAGE,
        )
//        val hasPermissionCallLog = PermissionUtils.isGranted(Manifest.permission.READ_CALL_LOG)
        val hasPermissionReadPhoneState =
            PermissionUtils.isGranted(Manifest.permission.READ_PHONE_STATE)
//                if (false && hasPermission) {
        if (hasPermission && hasPermissionReadPhoneState) {
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
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_SMS,
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
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

    private fun executeNext() {
        OkGo.getInstance().addCommonHeaders(BuildRequestJsonUtils.buildHeaderImei())
//        LocationMgr.getInstance().getLocation()

//        handler.postDelayed(Runnable {
//            checkAndShowRateUs()
//        }, 500)
//                handler.postDelayed(Runnable {
//                    EventBus.getDefault().post(LogTimeOut())
//        }, 6000)
    }

//    private fun checkAndShowRateUs() {
//        var hasShowRate = SPUtils.getInstance().getBoolean(Constant.KEY_HAS_SHOW_RATE)
//        if (!hasShowRate) {
//            var count = SPUtils.getInstance().getInt(Constant.KEY_SHOW_RATE_COUNT, 0)
//            if (count >= 2) {
//                var rateUs = RateUsUtils()
//                rateUs.showRate(this)
//                SPUtils.getInstance().put(Constant.KEY_HAS_SHOW_RATE, true)
//            } else {
//                count++
//                SPUtils.getInstance().put(Constant.KEY_SHOW_RATE_COUNT, count)
//            }
//        }
//    }

    fun updatePageByType(@PageType type: Int) {
        if (type == mCurPageType) {
            return
        }
        mCurPageType = type
        updatePageByTypeInternal()
    }

    fun closeSlide() {
        drawerLayout?.closeDrawer(GravityCompat.START)
    }

    fun setTitle(title: String) {
        tvTitle?.text = title
    }

    override fun setTitle(@StringRes titleRes: Int) {
        tvTitle?.text = resources.getText(titleRes)
    }

    private fun updatePageByTypeInternal() {
        var curFragment: BaseFragment? = null
        when (mCurPageType) {
            PageType.MY_LOAN -> {
                setTitle(R.string.setting_my_loan)
                if (mMyLoanFragment == null) {
                    mMyLoanFragment = MyLoanFragment()
                }
                curFragment = mMyLoanFragment
            }
            PageType.MY_PROFILE -> {
                setTitle(R.string.setting_my_profile)
                if (mMyProfileFragment == null) {
                    mMyProfileFragment = MyProfileFragment()
                }
                curFragment = mMyProfileFragment
            }
            PageType.CARD -> {
                setTitle(R.string.setting_card)
                curFragment = BankCardFragment()
            }
            PageType.BANK_ACCOUNT -> {
                setTitle(R.string.setting_bank_account)
                curFragment = BankAccountFragment()
            }
            PageType.MESSAGE -> {
                setTitle(R.string.setting_message)
                curFragment = MessageFragment()
            }
            PageType.CONTACT_US -> {
                setTitle(R.string.setting_contact_us)
                curFragment = ContactUsFragment()
            }
            PageType.HELP -> {
                setTitle(R.string.setting_help)
                curFragment = HelpFragment()
            }
            PageType.ABOUT -> {
                setTitle(R.string.setting_about)
                curFragment = AboutFragment()
            }
        }
        if (curFragment != null) {
            replaceFragment(curFragment, R.id.fl_main_content)
        }

    }

    private fun requestUpdate() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        OkGo.post<String>(Api.UPDATE_DETAIL).tag("Test")
            .params("data", jsonObject.toString()) //                .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {

                }

                override fun onError(response: Response<String>) {
                    super.onError(response)

                }
            })
    }

    override fun onBackPressed() {
        onBackPressedInternal()
    }

    private fun onBackPressedInternal(): Boolean {
        if (drawerLayout?.isDrawerVisible(GravityCompat.START) == true) {
            drawerLayout?.closeDrawer(GravityCompat.START)
            return true
        }
        finish()
        return false
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun useLogout() : Boolean{
        return true
    }
}